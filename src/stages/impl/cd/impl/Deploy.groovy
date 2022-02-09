#!/usr/bin/env groovy

package stages.impl.cd.impl

import stages.impl.cd.Stage


@Stage(name = "deploy")
class Deploy {
    Script script

    void run(context) {
        script.echoer.info("Stage implementation Deploy")
    }
}