#!/usr/bin/env groovy

import stages.StageFactory
import pipelines.Config
import pipelines.Job
import pipelines.JobType
import pipelines.GitInfo
import pipelines.Parameters


def call(Map userConfigVariables=[:]) {
    def context = [:]

    podTemplate(containers: [
        containerTemplate(
            name: 'jnlp', 
            image: 'jenkins/inbound-agent:latest'
            )
    ]) {

        node(POD_LABEL) {
            stage('Get a Maven project') {
                container('jnlp') {
                    stage('Shell Execution') {
                        sh '''
                        echo "Hello! I am executing shell"
                        '''
                    }
                }
            }

        }
    }
}
