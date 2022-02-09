#!/usr/bin/env groovy

package stages.impl.ci.impl.build

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType


@Stage(name = "build", buildTool = ["gradle"], type = ProjectType.APPLICATION)
class BuildGradleApplication {
    Script script

    void run(context) {
        script.echoer.info("Stage implementation BuildGradleApplication")

        script.sh("./gradlew assemble")
    }
}