#!/usr/bin/env groovy

import stages.StageFactory
import pipelines.Config
import pipelines.Job
import pipelines.JobType
import pipelines.GitInfo
import pipelines.Parameters
import kubernetes.JenkisAgentPodTemplates


def call(Map userConfigVariables=[:]) {
    def context = [:]

    podTemplate = new AgentPodTemplates()


    // podTemplate(
    //     nodeSelector: 'tier: cicd',
    //     containers: [
    //         containerTemplate(
    //             name: 'jnlp', 
    //             image: 'jenkins/inbound-agent:latest'
    //             ),
    //         containerTemplate(
    //             name: 'python', 
    //             image: 'python:latest', 
    //             command: 'sleep', 
    //             args: '30d'
    //         ),
    //         containerTemplate(
    //             name: 'gcloud', 
    //             image: 'gcr.io/google.com/cloudsdktool/cloud-sdk:latest', 
    //             command: 'cat',
    //             ttyEnabled: true,
    //             envVars: [
    //                 envVar(key: 'GOOGLE_APPLICATION_CREDENTIALS', value: '/secret/av-cms-293206-90534c67e5c6.json')
    //             ]
    //         )
    //     ],
    //     volumes: [
    //         secretVolume(secretName: 'kaniko-secret', mountPath: '/secret'),
    //         persistentVolumeClaim(claimName: 'cd-jenkins-agent-kaniko-cache', mountPath: '/cache')
    //     ]
    // )

    // podTemplate(yaml: """
    //     apiVersion: v1
    //     kind: Pod
    //     metadata:
    //         name: build-docker-image
    //     spec:
    //         containers:
    //           - name: gcloud
    //             image: gcr.io/google.com/cloudsdktool/cloud-sdk:latest
    //             command:
    //             - cat
    //             tty: true
    //             volumeMounts:
    //               - name: kaniko-secret
    //                 mountPath: /secret
    //             env:
    //               - name: GOOGLE_APPLICATION_CREDENTIALS
    //                 value: /secret/av-cms-293206-90534c67e5c6.json

    //           - name: kaniko-gcp
    //             image: gcr.io/kaniko-project/executor:v1.6.0-debug
    //             imagePullPolicy: Always
    //             command:
    //             - /busybox/cat
    //             tty: true
    //             volumeMounts:
    //               - name: kaniko-secret
    //                 mountPath: /secret
    //               - name: kaniko-cache
    //                 mountPath: /cache
    //                 readOnly: true
    //             env:
    //               - name: GOOGLE_APPLICATION_CREDENTIALS
    //                 value: /secret/av-cms-293206-90534c67e5c6.json

    //           - name: kaniko-aws
    //             image: gcr.io/kaniko-project/executor:v1.6.0-debug
    //             imagePullPolicy: Always
    //             command:
    //             - cat
    //             tty: true
    //             volumeMounts:
    //               - name: docker-config
    //                 mountPath: /kaniko/.docker
    //               - name: aws-secret
    //                 mountPath: /root/.aws/

    //           - name: python
    //             image: python:latest
    //             imagePullPolicy: Always
    //             command:
    //             - cat
    //             tty: true

    //         volumes:
    //           - name: docker-config
    //             configMap:
    //               name: docker-config
    //           - name: aws-secret
    //             secret:
    //               secretName: aws-secret
    //           - name: kaniko-secret
    //             secret:
    //               secretName: kaniko-secret
    //           - name: kaniko-cache
    //             readOnly: true
    //             persistentVolumeClaim:
    //               claimName: cd-jenkins-agent-kaniko-cache

    //         tolerations:
    //         - effect: NoSchedule
    //           key: tier
    //           operator: Equal
    //           value: cicd
    //         nodeSelector:
    //           tier: cicd
    // """)
 
    podTemplate.mainTemplate {
        node(POD_LABEL) {
            stage("INIT") {
                echoer.stage('INIT')

                echoer.handler("Config")
                context.config = new Config(userConfigVariables, this)
                context.config.getJobinitVariables()
                context.config.setJobRuntimeVariables()
                context.config.getJobVariables()
                context.config.getGlobalVariables()
                context.config.getProjectVariables()
                context.config.getEnvVariables()

                echoer.handler("Parameters")
                context.parameters = new Parameters(userConfigVariables, this)
                context.parameters.genJobParameters(context.config)

                echoer.handler("Job")
                context.job = new Job(JobType.BUILD.value, this)
                context.job.init(context.config)

                echoer.handler("StageFactory")
                context.factory = new StageFactory(script: this)
                context.factory.loadStages().each() { context.factory.add(it) }
            }

            try {
                context.job.runStage('Initialisation', context, null)

                echoer.handler("GitInfo")
                context.git = new GitInfo(this)
                context.git.init()

                context.config.getReleaseTagVariable(context)

                context.job.runStage('Info', context, null)

                context.config.job.stages.each() { stage ->
                    context.job.runStage(stage, context, 'main')
                }
            } catch (Exception ex) {
                echoer.warning("Build failed.")
                echoer.warning("Build fail reason: ${ex}")
                echoer.warning("Build trace: ${ex.getStackTrace().collect { it.toString() }.join('\n')}")
                currentBuild.setResult('FAILED')
            } finally {
                context.job.buildResult = currentBuild.currentResult
                context.job.buildDuration = currentBuild.durationString.replace(' and counting', '')
                if (currentBuild.currentResult == 'SUCCESS') {
                    echoer.info("Build successfull.")
                }
                // context.job.runStage('Notification', context, null)
                context.job.runStage('CICDdashboard', context, null)
                context.job.runStage('Cleanup', context, null)
            }

        }
    }
}
