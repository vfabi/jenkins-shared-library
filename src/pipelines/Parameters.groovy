#!/usr/bin/env groovy

package pipelines


class Parameters {

    def userConfigVariables = [:]
    Script script

    Parameters(userConfigVariables, script) {
        this.userConfigVariables = userConfigVariables
        this.script = script
    }

    def genJobParameters(config) {
        /*
        Generate pipeline's parameters block.
        */

        // def environments = config.global.environments
        def kubernetes_clusters = config.global.kubernetes_clusters
        // environments.add(0, userConfigVariables.defaultDeployEnvironment)  // TODO: add check for user defined ${defaultDeployEnvironment} variable exists in ${config.global.environments}.
        kubernetes_clusters.add(0, userConfigVariables.defaultDeployKubernetesCluster)  // TODO: add check for user defined ${defaultDeployKubernetesCluster} variable exists in ${config.global.kubernetes_clusters}.

        script.properties([
            script.parameters([
                // script.booleanParam(name: 'UPDATE_PARAMETERS', defaultValue: false, description: 'Force update parameters block on changes.'),
                script.string(name: 'BUILD_APP_GIT_BRANCH', defaultValue: '', description: 'Application git branch to build from.', required: true),
                script.choice(name: 'DEPLOY_KUBERNETES_CLUSTER', choices: kubernetes_clusters, description: 'Kubernetes cluster to deploy application.')
            ])
        ])

        // TODO: check if it works.
        if (script.params.UPDATE_PARAMETERS) {
            script.currentBuild.result = 'SUCCESS'
            return
        }
    }
}