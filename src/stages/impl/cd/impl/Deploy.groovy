#!/usr/bin/env groovy

package stages.impl.cd.impl

import stages.impl.cd.Stage


@Stage(name = "deploy")
class Deploy {
    Script script

    void run(context) {
        script.echoer.info("Stage implementation Deploy")

        script.timeout(time: 15, unit: "MINUTES") {
            script.input(
                id: 'inputDeploy',
                message: "Do you want to deploy to ${context.config.job.deployKubernetesCluster} Kubernetes cluster?",
                ok: 'Yes',
                submitter: 'vfabiianskyi,max', 
                submitterParameter: 'deployApprover'
            )
        }

        // Integration  -->
        // Update artifacts for CDCDdashboard stage.
        script.unstash 'cicd-dashboard-data'
        script.sh("echo deploy_success=true >> artifacts/cicd-dashboard-data.txt")
        script.sh("echo deploy_environment=${context.config.job.deployKubernetesCluster} >> artifacts/cicd-dashboard-data.txt")
        script.sh("echo deploy_namespace=${context.config.project.namespace} >> artifacts/cicd-dashboard-data.txt")
        script.sh("echo deploy_apprelease=${context.config.job.releaseTag} >> artifacts/cicd-dashboard-data.txt")
        script.stash includes: 'artifacts/cicd-dashboard-data.txt', name: 'cicd-dashboard-data'
        // Integration <--
    }
}