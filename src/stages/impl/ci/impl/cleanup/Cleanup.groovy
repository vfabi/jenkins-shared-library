#!/usr/bin/env groovy

package stages.impl.ci.impl.cleanup

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType


@Stage(name = "cleanup", buildTool = ["any"], type = [ProjectType.APPLICATION, ProjectType.LIBRARY, ProjectType.AUTOTESTS])
class Cleanup {
    Script script

    void run(context) {
        script.echoer.info("Stage implementation Cleanup")

        script.echoer.info("Workspace cleanup.")
        script.cleanWs()
        //TODO: docker images cleanup
    }
}