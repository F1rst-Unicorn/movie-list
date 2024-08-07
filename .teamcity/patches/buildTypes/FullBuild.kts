package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'FullBuild'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("FullBuild")) {
    features {
        add {
            commitStatusPublisher {
                vcsRootExtId = "${DslContext.settingsRoot.id}"
                publisher = github {
                    githubUrl = "https://j.njsm.de/git/api/v1"
                    authType = personalToken {
                        token = "credentialsJSON:557337ec-b35f-4879-a148-11d578a847a4"
                    }
                }
            }
        }
    }
}
