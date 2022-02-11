#!/usr/bin/env groovy

package stages.impl.ci.impl.preparation

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType


@Stage(name = "preparation", buildTool = ["any"], type = [ProjectType.APPLICATION, ProjectType.LIBRARY])
class Preparation {
    Script script

    void run(context) {
        script.echoer.info("Stage implementation Preparation")

        // Integration. CICDdashboard. -->
        try {
            script.sh("mkdir artifacts && touch artifacts/cicd-dashboard-data.txt")
            script.sh("echo git_branch=${context.git.gitBranch} >> artifacts/cicd-dashboard-data.txt")
            script.sh("echo git_commit=${context.git.gitCommitHashShort} >> artifacts/cicd-dashboard-data.txt")
            script.sh("echo git_author=${context.git.gitAuthor} >> artifacts/cicd-dashboard-data.txt")
            script.sh("echo git_author_email=${context.git.gitAuthorEmail} >> artifacts/cicd-dashboard-data.txt")
            script.stash includes: 'artifacts/cicd-dashboard-data.txt', name: 'cicd-dashboard-data'
        } catch(Exception e) {}
        // Integration. CICDdashboard. <--

        // script.container('python') {
        //     script.sh("python -V")
        // }
    }
}