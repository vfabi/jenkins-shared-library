#!/usr/bin/env groovy

package stages.impl.ci.impl.builddocker

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType


@Stage(name = "builddocker", buildTool = ["any"], type = [ProjectType.APPLICATION])
class BuildDocker {
    Script script

    void run(context) {
        script.echoer.info("sStage implementation BuildDocker")

        def dockerRegistryCloud = context.config.project.dockerRegistryCloud
        def appName = context.config.project.appName
        def appReleaseTag = context.config.job.releaseTag
        def gcpImageTag = "${context.config.global.gcpDockerRegistry}/${appName}:${appReleaseTag}"
        def awsImageTag = "${context.config.global.awsDockerRegistry}/${appName}:${appReleaseTag}"

        if dockerRegistryCloud == "gcp" {
            script.container('kaniko-gcp') {
                script.sh("/kaniko/executor --dockerfile `pwd`/Dockerfile --context `pwd` --cache=true --cache-dir=/cache --single-snapshot --destination ${context.config.global.gcpDockerRegistry}/${appName}:latest --destination ${gcpImageTag} --build-arg APP_VERSION_ARG=${appName}:${appReleaseTag}")
            }
        }

        if dockerRegistryCloud == "aws" {
            script.container('kaniko-aws') {
                script.sh("/kaniko/executor --dockerfile `pwd`/Dockerfile --context `pwd` --cache=true --cache-dir=/cache --single-snapshot --destination=${context.config.global.awsDockerRegistry}/${appName}:latest --destination ${awsImageTag} --build-arg APP_VERSION_ARG=${appName}:${appReleaseTag}")
            }
        }

    }
}