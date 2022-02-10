#!/usr/bin/env groovy

package stages.impl.ci.impl.info

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType


@Stage(name = "info", buildTool = ["any"], type = [ProjectType.APPLICATION, ProjectType.LIBRARY, ProjectType.AUTOTESTS])
class Metadata {
    Script script

    void run(context) {
        script.echoer.info("\nGit commit branch: ${context.git.gitBranch}\n"
        + "Git commit hash: ${context.git.gitCommitHash}\n"
        + "Git commit hash short: ${context.git.gitCommitHashShort}\n"
        + "Git commit author: ${context.git.gitAuthor}\n"
        + "Git commit message: ${context.git.gitMessage}\n"
        + "Job config buildTool: ${context.config.job.buildTool}\n"
        + "Job config projectName: ${context.config.job.projectName}\n"
        + "Job config projectMetaName: ${context.config.job.projectMetaName}\n"
        + "Job config projectType: ${context.config.job.projectType}\n"
        + "Job config stages: ${context.config.job.stages}\n"
        + "Job config releaseBranches: ${context.config.job.releaseBranches}\n"
        + "Job config isRelease: ${context.config.job.isRelease}\n"
        + "Job config releaseTag: ${context.config.job.releaseTag}\n"
        + "Job config deployAppRelease: ${context.config.job.deployAppRelease}\n"
        + "Job config deployKubernetesCluster: ${context.config.job.deployKubernetesCluster}\n"
        + "Job config triggerCause: ${context.config.jobinit.triggerCause}")
    }
}