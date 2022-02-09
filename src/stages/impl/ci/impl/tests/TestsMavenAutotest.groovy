#!/usr/bin/env groovy

package stages.impl.ci.impl.tests

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType


@Stage(name = "tests", buildTool = ["maven"], type = ProjectType.AUTOTESTS)
class TestsMavenAutotest {
    Script script

    void run(context) {
        script.echoer.info("Stage implementation TestsMavenAutotest")

        def environmentName = script.env["DEPLOY_ENVIRONMENT"]
        def credentialsID = "app-qa-auto-configfile-${environmentName}"  //TODO: move this prefix to project variables file?
        script.currentBuild.displayName = "${script.currentBuild.displayName}-${environmentName}"

        script.withCredentials([script.string(credentialsId: credentialsID, variable: 'TEXT')]) {
            script.sh('''set +x && echo -en "$TEXT" > src/main/resources/secrets/secret.properties''')
        }
        script.sh("mvn clean test -Dtarget.environment=${environmentName}")
        script.allure([
            includeProperties: false,
            reportBuildPolicy: 'ALWAYS',
            results : [[path: 'target/allure-results']]
        ])
    }
}