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

        def environments = config.global.environments
        environments.add(0, userConfigVariables.defaultDeployEnvironment)  // TODO: add check for user defined ${defaultDeployEnvironment} variable exists in ${config.global.environments}.

        script.properties([
            script.parameters([
                script.booleanParam(name: 'UPDATE_PARAMETERS', defaultValue: false, description: 'Force update parameters block on changes.'),
                script.string(name: 'DEPLOY_APP_RELEASE', defaultValue: '', description: 'Application release to deploy.'),
                script.choice(name: 'DEPLOY_ENVIRONMENT', choices: environments, description: 'Environment name to deploy application.')
            ])
        ])

        // TODO: check if it works.
        if (script.params.UPDATE_PARAMETERS) {
            script.currentBuild.result = 'SUCCESS'
            return
        }
    }
}