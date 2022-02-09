#!/usr/bin/env groovy

package stages.impl.ci.impl.compile

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType


@Stage(name = "compile", buildTool = ["any"], type = [ProjectType.APPLICATION, ProjectType.LIBRARY])
class Compile {
    Script script

    void run(context) {
        script.echoer.info("stages.impl.ci.impl.compile.Compile")
    }
}