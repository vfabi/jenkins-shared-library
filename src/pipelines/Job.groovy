#!/usr/bin/env groovy

package pipelines


class Job {

    def type
    Script script

    def jobName
    def jobBaseName
    def jobURL
    def buildURL
    def buildID
    def buildResult
    def buildDuration
    def stages

    Job(type, script) {
        this.type = type
        this.script = script
    }

    def getParameterValue(parameter, defaultValue = null) {
        def parameterValue = script.env["${parameter}"] ? script.env["${parameter}"] : defaultValue
        return parameterValue
    }

    def init(config) {
        this.jobName = getParameterValue("JOB_NAME")
        this.jobBaseName = getParameterValue("JOB_BASE_NAME")
        this.jobURL = getParameterValue("JOB_URL")
        this.buildURL = getParameterValue("BUILD_URL")
        this.buildID = getParameterValue("BUILD_ID")
        this.buildResult = null
        this.buildDuration = null
        this.stages = [:]
    }

    def runStage(stageName, context, rulesSet) {
        // Ruleset 'main'
        if (rulesSet == 'main') {
            // For release branch
            if (context.config.job.releaseBranches.contains(context.git.gitBranch)) {
                if (stageName.toLowerCase().startsWith('deploy')) {
                    // Run CDStage only if deployKubernetesCluster is set
                    if (context.config.job.deployKubernetesCluster) {
                        runCDStage(stageName, context)
                    }
                }
                else {
                    // Run CIStage only if deployAppRelease is not set
                    if (!context.config.job.deployAppRelease) {
                        runCIStage(stageName, context)
                    }
                }
            }
            // For non-release branch
            else {
                // Check if stage is for non-release branches
                if (!context.config.global.release_stages_only.contains(stageName.toLowerCase())) {
                    if (!stageName.toLowerCase().startsWith('deploy')) {
                        runCIStage(stageName, context)
                    }
                }
            }
        }
        // No ruleset set
        else {
            if (stageName.toLowerCase().startsWith('deploy')) {
                runCDStage(stageName, context)
            }
            else {
                runCIStage(stageName, context)
            }
        }
    }

    def runCIStage(stageName, context) {
        script.stage(stageName) {
            script.echoer.stage(stageName.toUpperCase())
            context.factory.getStage(
                stageName.toLowerCase(),
                context.config.job.buildTool.toLowerCase(),
                context.config.job.projectType.toLowerCase()
            ).run(context)
        }
    }

    def runCDStage(stageName, context) {
        script.stage(stageName) {
            script.echoer.stage(stageName.toUpperCase())
            context.factory.getStage(stageName.toLowerCase()).run(context)
        }
    }
}