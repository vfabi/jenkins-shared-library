#!/usr/bin/env groovy

package stages.impl.cd.impl

import stages.impl.cd.Stage


@Stage(name = "deploy")
class Deploy {
    Script script

    void run(context) {
        script.echoer.info("Stage implementation Deploy")

        def appSupportedKubernetesClusters = context.config.project.kubernetesClusters
        def deployKubernetesCluster = context.config.job.deployKubernetesCluster

        // debug
        script.echoer.info("appSupportedKubernetesClusters: ${appSupportedKubernetesClusters}")
        script.echoer.info("deployKubernetesCluster: ${deployKubernetesCluster}")
        // debug


        // Checks.
        if (!appSupportedKubernetesClusters.contains(deployKubernetesCluster)) {
            script.error("Application unsupported Kubernetes cluster selected (${deployKubernetesCluster}). Supported Kubernetes clusters are: ${appSupportedKubernetesClusters}.")
        }


        // Deploy approval.
        def deployApprovers = context.config.global.cd_stages.deploy_approvers["${deployKubernetesCluster}"].toString().replace("[", "").replace("]", "").trim()
        if (deployApprovers != "") {
            script.echoer.input("Please approve deploy to ${deployKubernetesCluster} Kubernetes cluster. Can be approved by ${deployApprovers} or any from Jenkins admins.")
            script.timeout(time: 15, unit: "MINUTES") {
                script.input(
                    id: 'inputDeploy',
                    message: "Do you want to deploy to ${deployKubernetesCluster} Kubernetes cluster?",
                    ok: 'Yes',
                    submitter: deployApprovers, 
                    submitterParameter: 'deployApprover'
                )
            }
        }


        // Deploy procedure
        def kubernetesCloud = context.config.project.kubernetesCloud
        def kubernetesNamespace = context.config.project.kubernetesNamespace
        def appName = context.config.project.appName
        def appReleaseTag = context.config.job.releaseTag

        if (kubernetesCloud == "gcp") {
            def appDockerImageURL = "${context.config.global.gcpDockerRegistry}/${appName}:${appReleaseTag}"

            script.container('gcloud') {
                script.sh("gcloud auth activate-service-account --key-file=/secret/av-cms-293206-90534c67e5c6.json")
                script.sh("gcloud container clusters get-credentials ${deployKubernetesCluster} --zone us-central1-c --project av-cms-293206")
                script.sh("kubectl --namespace ${kubernetesNamespace} get pods")
                // script.sh("kubectl --namespace ${kubernetesNamespace} --cluster gke_av-cms-293206_us-central1-c_${deployKubernetesCluster} set image deployment ${appName} ${appName}=${appDockerImageURL}")
            }
        }


        // Integration. CICDdashboard. -->
        script.unstash 'cicd-dashboard-data'
        script.sh("echo deploy_success=true >> artifacts/cicd-dashboard-data.txt")
        script.sh("echo deploy_environment=${deployKubernetesCluster} >> artifacts/cicd-dashboard-data.txt")
        script.sh("echo deploy_namespace=${context.config.project.namespace} >> artifacts/cicd-dashboard-data.txt")
        script.sh("echo deploy_apprelease=${context.config.job.releaseTag} >> artifacts/cicd-dashboard-data.txt")
        script.stash includes: 'artifacts/cicd-dashboard-data.txt', name: 'cicd-dashboard-data'
        // Integration. CICDdashboard. <--
    }
}