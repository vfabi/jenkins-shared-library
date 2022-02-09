#!/usr/bin/env groovy

package stages.impl.ci.impl.notification

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.net.URL


@Stage(name = "notification", buildTool = ["any"], type = [ProjectType.APPLICATION, ProjectType.LIBRARY, ProjectType.AUTOTESTS])
class NotificationTeams {
    Script script

    void run(context) {
        script.echoer.info("Stage implementation NotificationTeams")

        try {
            def post = new URL("${context.config.global.ci_stages.notification_teams_webhook_url}").openConnection()
            def sonar_qualitygate_status = context.job.stages['sonar_qualitygate_status'] ?: "N/A"
            def tests_tests_result = context.job.stages['tests_tests_result'] ?: "N/A"
            def git_commit_url = "${context.config.global.ci_stages.notification_git_server_url_prefix}/${context.config.job.projectName.split('/')[1]}/-/commit/${context.git.gitCommitHash}"
            def themeColor = null
            switch(context.job.buildResult) {
                case "SUCCESS":
                    themeColor = '228B22'
                    break
                case "FAILURE":
                    themeColor = 'FF0000'
                    break
                default:
                    themeColor = '848484'
                    break
            }

            def json_payload = JsonOutput.toJson([
                title: "${context.config.job.projectName}",
                text: "Build result: ${context.job.buildResult}</br>\
                Build id: *[${context.job.buildID}](${context.job.buildURL})*</br>\
                Build duration: ${context.job.buildDuration}</br>\
                Artifact release: ${context.config.job.releaseTag}</br>\
                Tests summary: ${tests_tests_result}</br>\
                Sonar quality gate status: ${sonar_qualitygate_status}</br>\
                Git branch: ${context.git.gitBranch}</br>\
                Git hash: *[${context.git.gitCommitHashShort}](${git_commit_url})*</br>\
                Git author: ${context.git.gitAuthor}</br>\
                Git message: *${context.git.gitMessage}*",
                themeColor: themeColor,
            ])

            post.setRequestMethod("POST")
            post.setDoOutput(true)
            post.setRequestProperty("Content-Type", "application/json")
            post.getOutputStream().write(json_payload.getBytes("UTF-8"))
            
            def postRC = post.getResponseCode();
            if (postRC.equals(200)) {
                def resp = post.getInputStream().getText()
                def jsonSlurper = new JsonSlurper()
                def object = jsonSlurper.parseText(resp)
            }

        } catch(Exception e) {
            script.echoer.warning("Stage implementation NotificationTeams error: " + e.toString())
        }
    }
}