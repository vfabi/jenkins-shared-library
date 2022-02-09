#!/usr/bin/env groovy

package stages.impl.ci.impl.sonar

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType


@Stage(name = "sonar", buildTool = ["nodejs"], type = [ProjectType.APPLICATION, ProjectType.LIBRARY])
class SonarNodejsApplication {
    Script script

    void run(context) {
        script.echoer.info("Stage implementation SonarNodejsApplication")

        // Workaround for Sonar CE version. Fix Sonar proprities file to send reports with different basenames (with branches and PR/MR postfixes) to Sonar server.
        def sonarPropritiesFile = "sonar-project.properties"
        def projectName = context.config.job.projectName.split('/')[1]
        script.sh("sed -i 's/sonar.projectName=${projectName}/sonar.projectName=${projectName}--${context.git.gitBranch}/g' ${sonarPropritiesFile}")
        script.sh("sed -i 's/sonar.projectKey=${projectName}/sonar.projectKey=${projectName}--${context.git.gitBranch}/g' ${sonarPropritiesFile}")

        script.withSonarQubeEnv("${context.config.global.ci_stages.sonar_server_installation_name}") {
            script.echoer.info("Run Sonar scanner to check application.")
            def scannerHome = script.tool("${context.config.global.ci_stages.sonar_scanner_installation_name}")
            script.sh("${scannerHome}/bin/sonar-scanner")
        }

        def sonarQualityGate = script.waitForQualityGate()
        context.job.stages.put('sonar_qualitygate_status', sonarQualityGate.status)
        //NOTE: due to Tests stage issue
        // if (sonarQualityGate.status != 'OK') {
        //     script.error "Build aborted at Sonar stage. Due to Sonar quality gate failure: ${sonarQualityGate.status}"
        // }

        //NOTE: due to Tests stage issue
        // if (!context.job.stages['tests_threshold_passed']) {
        //     script.error "Build aborted at Sonar stage. Due to Test stage's pass threshold low value. Please check Tests stage."
        // }
    }
}