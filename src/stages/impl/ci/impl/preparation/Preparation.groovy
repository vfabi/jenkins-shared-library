#!/usr/bin/env groovy

package stages.impl.ci.impl.preparation

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType


@Stage(name = "preparation", buildTool = ["any"], type = [ProjectType.APPLICATION, ProjectType.LIBRARY])
class Preparation {
    Script script

    void run(context) {
        script.echoer.info("stages.impl.ci.impl.preparation.Preparation")

        script.container('python') {
            sh """
            python -V
            """
        }
    }
}