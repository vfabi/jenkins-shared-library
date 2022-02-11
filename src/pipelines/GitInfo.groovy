#!/usr/bin/env groovy

package pipelines


class GitInfo {

    Script script

    def gitBranch
    def gitBranch2
    def gitAuthor
    def gitAuthorEmail
    def gitMessage
    def gitCommitHash
    def gitCommitHashShort

    GitInfo(script) {
        this.script = script
    }

    def getParameterValue(parameter, defaultValue = null) {
        def parameterValue = script.env["${parameter}"] ? script.env["${parameter}"] : defaultValue
        return parameterValue
    }

    def init() {
        this.gitBranch = getParameterValue("BRANCH_NAME").trim().replace("/", "-")
        this.gitBranch2 = script.sh(returnStdout: true, script: 'git name-rev --name-only HEAD').trim().replace("/", "-")
        this.gitCommitHash = script.sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
        this.gitCommitHashShort = script.sh(returnStdout: true, script: 'git log -1 --pretty=%h').trim()
        this.gitAuthor = script.sh(returnStdout: true, script: "git --no-pager show -s --format='%an' ${this.gitCommitHash}").trim().replace("(", "").replace(")", "")
        this.gitAuthorEmail = script.sh(returnStdout: true, script: "git log -1 --pretty=%ae").trim()
        this.gitMessage = script.sh(returnStdout: true, script: 'git log -1 --pretty=%B').trim().replace("(", "").replace(")", "")
    }
}