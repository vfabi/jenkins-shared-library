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
        script.echoer.info("Stage implementation CICDdashboard")

        script.sh("mkdir artifacts && touch artifacts/cicd-dashboard-data.txt")
        script.sh("echo git_branch=${context.git.gitBranch} >> artifacts/cicd-dashboard-data.txt")
        script.sh("echo git_commit=${context.git.gitCommitHashShort} >> artifacts/cicd-dashboard-data.txt")
        script.sh("echo git_author=${context.git.gitAuthor} >> artifacts/cicd-dashboard-data.txt")
        script.sh("echo git_author_email=${context.git.gitAuthorEmail} >> artifacts/cicd-dashboard-data.txt")
        script.stash includes: 'artifacts/cicd-dashboard-data.txt', name: 'cicd-dashboard-data'
    }
}