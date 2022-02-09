#!/usr/bin/env groovy

import stages.StageFactory
import pipelines.Config
import pipelines.Job
import pipelines.JobType
import pipelines.GitInfo
import pipelines.Parameters


def call(Map userConfigVariables=[:]) {
    def context = [:]

    node('master') {
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
    }

    node('!master') {
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
            context.job.runStage('Notification', context, null)
            context.job.runStage('Cleanup', context, null)
        }
    }
}
