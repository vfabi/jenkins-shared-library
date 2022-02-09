#!/usr/bin/env groovy

package stages.impl.ci.impl.compile

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType


@Stage(name = "compile", buildTool = ["gradle"], type = [ProjectType.APPLICATION, ProjectType.LIBRARY])
class CompileGradleApplication {
    Script script

    void run(context) {
        script.echoer.info("Stage implementation CompileGradleApplication")

        script.sh("./gradlew classes")
    }
}