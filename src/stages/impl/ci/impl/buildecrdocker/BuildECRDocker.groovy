#!/usr/bin/env groovy

package stages.impl.ci.impl.buildecrdocker

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType


@Stage(name = "buildecrdocker", buildTool = ["any"], type = [ProjectType.APPLICATION])
class BuildECRDocker {
    Script script

    void run(context) {
        script.echoer.info("Build docker image for ECR repo.")
        script.echoer.info("Docker image full path: ${context.config.project.docker_repo_full_url}:${context.config.job.releaseTag}.")

        script.sh("aws"
        + " ecr get-login-password --region ${context.config.project.docker_repo_aws_region}"
        + " | docker login --username AWS --password-stdin"
        + " ${context.config.project.docker_repo_host}")
        script.sh("docker build -t ${context.config.project.docker_repo_full_url}:${context.config.job.releaseTag} .")
        script.sh("docker images --filter reference=${context.config.project.docker_repo_full_url}:${context.config.job.releaseTag} .")
    }
}