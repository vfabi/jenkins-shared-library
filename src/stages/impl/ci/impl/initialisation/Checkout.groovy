#!/usr/bin/env groovy

/*
    Dependencies:
        - 'ws-cleanup' plugin
*/

package stages.impl.ci.impl.initialisation

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType


@Stage(name = "initialisation", buildTool = ["any"], type = [ProjectType.APPLICATION, ProjectType.LIBRARY, ProjectType.AUTOTESTS])
class Checkout {
    Script script

    void run(context) {
        script.echoer.info("Stage implementation Checkout")

        script.echoer.info("Workspace cleanup.")
        script.cleanWs()
        script.echoer.info("Checkout SCM.")
        script.checkout(script.scm)
    }
}