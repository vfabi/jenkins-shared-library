#!/usr/bin/env groovy

package stages.impl.ci.impl.sonar

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType


@Stage(name = "sonar", buildTool = ["any"], type = [ProjectType.APPLICATION, ProjectType.LIBRARY])
class Sonar {
    Script script

    void run(context) {
        script.echoer.info("Stage implementation Sonar")
    }
}