#!/usr/bin/env groovy

package stages.impl.ci.impl.builddocker

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType


@Stage(name = "builddocker", buildTool = ["any"], type = [ProjectType.APPLICATION])
class BuildDocker {
    Script script

    void run(context) {
        script.echoer.info("stages.impl.ci.impl.builddocker.BuildDocker")
    }
}