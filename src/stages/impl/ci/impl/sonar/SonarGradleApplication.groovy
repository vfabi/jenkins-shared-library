#!/usr/bin/env groovy

package stages.impl.ci.impl.sonar

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType


@Stage(name = "sonar", buildTool = ["gradle"], type = [ProjectType.APPLICATION, ProjectType.LIBRARY])
class SonarGradleApplication {
    Script script

    void run(context) {
        script.echoer.info("Stage implementation SonarGradleApplication")

        // Workaround for Sonar CE version. Fix Sonar proprities file to send reports with different basenames (with branches and PR/MR postfixes) to Sonar server.
        def sonarPropritiesFile = "gradle.properties"
        def projectName = context.config.job.projectName.split('/')[1]
        script.sh("sed -i 's/systemProp.sonar.projectKey=${projectName}/systemProp.sonar.projectKey=${projectName}--${context.git.gitBranch}/g' ${sonarPropritiesFile}")
        script.sh("sed -i 's/systemProp.sonar.projectName=${projectName}/systemProp.sonar.projectName=${projectName}--${context.git.gitBranch}/g' ${sonarPropritiesFile}")

        script.withSonarQubeEnv("${context.config.global.ci_stages.sonar_server_installation_name}") {
            script.echoer.info("Run Sonar scanner to check application.")
            def scannerHome = script.tool("${context.config.global.ci_stages.sonar_scanner_installation_name}")
            script.sh("./gradlew sonarqube")
        }

        def sonarQualityGate = script.waitForQualityGate()
        context.job.stages.put('sonar_qualitygate_status', sonarQualityGate.status)
        if (sonarQualityGate.status != 'OK') {
            script.error "Build aborted at Sonar stage. Due to Sonar quality gate failure: ${sonarQualityGate.status}"
        }

        if (!context.job.stages['tests_threshold_passed']) {
            script.error "Build aborted at Sonar stage. Due to Test stage's pass threshold low value. Please check Tests stage."
        }
    }
}