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
            ),
        containerTemplate(
            name: 'python', 
            image: 'python:latest', 
            command: 'sleep', 
            args: '30d'
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
            stage('Stage-2') {
                container('python') {
                    stage('Run python') {
                        sh '''
                        echo "python -V"
                        '''
                    }
                }
            }

        }
    }
}
