#!/usr/bin/env groovy

/*
    Description:
        This stage implements CICD Dashboard integration.
*/

package stages.impl.ci.impl.integration

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType


@Stage(name = "cicddashboard", buildTool = ["any"], type = [ProjectType.APPLICATION, ProjectType.LIBRARY, ProjectType.AUTOTESTS])
class CICDdashboard {
    Script script

    void run(context) {
        script.echoer.info("Stage integration CICDdashboard")

        // script.echoer.info("Workspace cleanup.")
        // script.cleanWs()
        // script.echoer.info("Checkout SCM.")
        // script.checkout(script.scm)

        script.sh("mkdir artifacts && touch artifacts/cicd-dashboard-data.txt")
        script.sh("echo git_branch=${context.git.gitBranch} >> artifacts/cicd-dashboard-data.txt")
        script.sh("echo git_commit=${context.git.gitCommitHashShort} >> artifacts/cicd-dashboard-data.txt")
        script.sh("echo git_author=${context.git.gitAuthor} >> artifacts/cicd-dashboard-data.txt")
        script.sh("echo git_author_email=${context.git.gitAuthorEmail} >> artifacts/cicd-dashboard-data.txt")
        script.sh("echo deploy_success=true >> artifacts/cicd-dashboard-data.txt") //TODO:
        script.sh("echo deploy_environment=${context.config.job.deployKubernetesCluster} >> artifacts/cicd-dashboard-data.txt")
        script.sh("echo deploy_namespace=${context.config.project.namespace} >> artifacts/cicd-dashboard-data.txt")
        script.sh("echo deploy_apprelease=${context.config.job.releaseTag} >> artifacts/cicd-dashboard-data.txt")
        script.stash includes: 'artifacts/cicd-dashboard-data.txt', name: 'cicd-dashboard-data'
    }
}