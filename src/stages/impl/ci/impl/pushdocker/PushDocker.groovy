#!/usr/bin/env groovy

package stages.impl.ci.impl.pushdocker

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType


@Stage(name = "pushdocker", buildTool = ["any"], type = [ProjectType.APPLICATION])
class PushDocker {
    Script script

    void run(context) {
        script.echoer.info("Push docker image to docker repository.")
        script.echoer.info("Docker image full path: ${context.config.project.docker_repo_full_url}:${context.config.job.releaseTag}")

        script.sh("docker push ${context.config.project.docker_repo_full_url}:${context.config.job.releaseTag}")
    }
}