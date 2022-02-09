#!/usr/bin/env groovy

/*
    TODO:
        - confgure options block
            options {
                    ansiColor('xterm')
                }
*/

package pipelines

import org.yaml.snakeyaml.Yaml


class Config {

    def userConfigVariables = [:]
    Script script

    def job = [:]
    def jobinit = [:]
    def global = [:]
    def project = [:]
    def environment = [:]

    Config(userConfigVariables, script) {
        this.userConfigVariables = userConfigVariables
        this.script = script
    }

    def getParameterValue(parameter, defaultValue = null) {
        def parameterValue = script.env["${parameter}"] ? script.env["${parameter}"] : defaultValue
        return parameterValue
    }

    def parseVarTriggerCause() {
        def triggerCause = 'unknown'
        def scriptObj = script.currentBuild.rawBuild.getCauses().join(", ")
        if (scriptObj.contains('Cause$UserIdCause')) {
            triggerCause = 'manual'
        }
        else {
            triggerCause = 'auto'
        }
        return triggerCause
    }

    def getJobVariables() {
        // TODO: wrap around try/except block
        def projectNameParcedList = getParameterValue("JOB_NAME").tokenize("/")
        projectNameParcedList.removeAt(projectNameParcedList.size() - 1)
        this.job['projectName'] = projectNameParcedList.join("/")  // Example: 'apps/application-1'.
        this.job['buildTool'] = userConfigVariables.buildTool  // TODO: if not use default value.
        this.job['stages'] = userConfigVariables.stages  // TODO: if not use default value.
        this.job['projectMetaName'] = this.job['projectName'].toLowerCase().trim().replace("-", "").replace("_", "").replace("/", "")
        this.job['projectType'] = userConfigVariables.projectType  // TODO: if not use default value.
        this.job['releaseBranches'] = userConfigVariables.releaseBranches // TODO: if not use default value.
        this.job['deployAppRelease'] = getParameterValue("DEPLOY_APP_RELEASE")
        this.job['deployEnvironment'] = getParameterValue("DEPLOY_ENVIRONMENT")
        this.job['isRelease'] = userConfigVariables.releaseBranches.contains(getParameterValue("BRANCH_NAME"))
    }

    def getJobinitVariables() {
        this.jobinit['triggerCause'] = parseVarTriggerCause()
    }

    def getGlobalVariables() {
        def yamlFilePath = "variables/global.yaml"
        try {
            def yamlFile = script.libraryResource(yamlFilePath)
            this.global = new Yaml().load(yamlFile)
        } catch (Exception ex) {
            def errorMessage = "[Config] File /resources/${yamlFilePath} read error occurred."
            script.echoer.warning(errorMessage)
            script.error(errorMessage)
        }
    }

    def getProjectVariables() {
        def projectName = this.job['projectName']
        def yamlFilePath = "variables/projects/${projectName}.yaml"
        try {
            def yamlFile = script.libraryResource(yamlFilePath)
            this.project = new Yaml().load(yamlFile)
        } catch (Exception ex) {
            script.echoer.notice("[Config] File /resources/${yamlFilePath} read error occurred.")
        }
    }

    def getEnvVariables() {
        def envName = this.job['deployEnvironment']
        def yamlFilePath = "variables/environments/${envName}.yaml"
        try {
            def yamlFile = script.libraryResource(yamlFilePath)
            this.environment = new Yaml().load(yamlFile)
        } catch (Exception ex) {
            script.echoer.notice("[Config] File /resources/${yamlFilePath} read error occurred.")
        }
    }

    def getReleaseTagVariable(context) {
        def tagPrefix = "v0.1.0"
        if (this.job['isRelease']) {
            this.job['releaseTag'] = "${tagPrefix}-${context.git.gitCommitHashShort}"
        }
        else {
            this.job['releaseTag'] = null
        }
    }

    def setJobRuntimeVariables() {
        def triggerCause = this.jobinit['triggerCause']
        script.currentBuild.displayName = "${script.BUILD_ID}-${triggerCause}"
    }
}