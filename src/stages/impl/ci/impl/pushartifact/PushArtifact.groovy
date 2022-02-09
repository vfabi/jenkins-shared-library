#!/usr/bin/env groovy

package stages.impl.ci.impl.pushartifact

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType


@Stage(name = "pushartifact", buildTool = ["any"], type = [ProjectType.APPLICATION, ProjectType.LIBRARY])
class PushArtifact {
    Script script

    void run(context) {
        script.echoer.info("stages.impl.ci.impl.pushartifact.PushArtifact")
    }
}