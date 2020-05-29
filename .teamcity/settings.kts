
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.exec
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.youtrack
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

    buildType(FullBuild)

    features {
        youtrack {
            id = "PROJECT_EXT_4"
            displayName = "YouTrack"
            host = "https://j.njsm.de/youtrack"
            userName = ""
            password = ""
            projectExtIds = "ML"
            accessToken = "credentialsJSON:94939717-d1c5-4b83-9d0f-52d919a3de6a"
            param("authType", "accesstoken")
        }
    }
}

object FullBuild : BuildType({
    name = "Full Build"

    artifactRules = """
        pkg/Archlinux/movie-list-*-any.pkg.tar.xz
    """.trimIndent()

    vcs {
        root(DslContext.settingsRoot)
    }

    triggers {
        vcs {
            branchFilter = "+:*"
        }
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
            path = "system-test/bin/clean-up.sh"
        }
        exec {
            name = "Install server"
            path = "system-test/bin/vm-deployment-test.sh"
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
})
