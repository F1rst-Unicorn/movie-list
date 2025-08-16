
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.exec
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2019.2"

project {
    vcsRoot(Gitea)
    buildType(FullBuild)
}

object FullBuild : BuildType({
    name = "Full Build"

    artifactRules = """
        pkg/Archlinux/movie-list-*-any.pkg.tar.xz
        pkg/Archlinux/movie-list-*-any.pkg.tar.zst
    """.trimIndent()

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            name = "Build and Unit Test"
            goals = "clean package"
            runnerArgs = "-P teamcity"
            pomLocation = "server/pom.xml"
            coverageEngine = idea {
                includeClasses = "de.njsm.movielist.*"
                excludeClasses = "de.njsm.movielist.*.*Test\n" +
                        "de.njsm.movielist.server.db.jooq.*"
            }
        }
        exec {
            name = "Package"
            path = "makepkg"
            arguments = "-cf"
            workingDir = "pkg/Archlinux"
        }
        exec {
            name = "Clean server"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            path = "system-test/scripts/clean-up.sh"
        }
        exec {
            name = "Install server"
            path = "system-test/scripts/vm-deployment-test.sh"
        }
        maven {
            name = "System Test"
            goals = "test"
            pomLocation = "system-test/pom.xml"
            mavenVersion = auto()
        }
    }

    params {
        param("env.CI_SERVER", "1")
    }

    failureConditions {
        executionTimeoutMin = 45
    }

    cleanup {
        artifacts(builds = 100)
    }

    triggers {
        vcs {
            id = "vcsTrigger"
            branchFilter = "+:*"
        }
    }

    requirements {
        exists("env.DEPLOYMENT_VM")
        exists("env.POSTGRESQL_DB")
    }

    features {
        commitStatusPublisher {
            vcsRootExtId = "${DslContext.settingsRoot.id}"
            publisher = github {
                githubUrl = "https://veenj.de/git/api/v1"
                authType = personalToken {
                    token = "credentialsJSON:557337ec-b35f-4879-a148-11d578a847a4"
                }
            }
        }
    }
})

object Gitea : GitVcsRoot({
    id("Projects_MovieList_Gitlab")
    name = "Gitea 2"
    url = "ssh://gitea@veenj.de:2222/veenj/movie-list.git"
    branch = "refs/heads/master"
    branchSpec = "+:refs/heads/*"
    checkoutPolicy = GitVcsRoot.AgentCheckoutPolicy.USE_MIRRORS
    authMethod = uploadedKey {
        uploadedKey = "id_rsa"
    }
})

