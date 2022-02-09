#!/usr/bin/env groovy

package stages.impl.ci.impl.build

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType


@Stage(name = "build", buildTool = ["nodejs"], type = ProjectType.APPLICATION)
class BuildNodejsApplication {
    Script script

    void run(context) {
        script.echoer.info("Stage implementation BuildNodejsApplication")

        script.docker.image("node:${context.config.project.nodejs_version}").inside("-e \'HOME=${script.env.WORKSPACE}\'") {
            script.sh("node --version")
            script.sh("npm ci")
            script.sh("npm run build")
        }
    }
}
