#!/usr/bin/env groovy

package stages.impl.ci.impl.tests

import stages.impl.ci.Stage
import stages.impl.ci.ProjectType


@Stage(name = "tests", buildTool = ["gradle"], type = ProjectType.APPLICATION)
class TestsGradleApplication {
    Script script

    void run(context) {
        script.echoer.info("Stage implementation TestsGradleApplication")

        script.sh("./gradlew check -Pipm.test.failure.ignore=true")

        def testsResult = script.junit('build/test-results/*/TEST-*.xml')
        if (testsResult != null) {
            def total = testsResult.totalCount
            def failed = testsResult.failCount
            def skipped = testsResult.skipCount
            def passed = total - failed - skipped
            context.job.stages['tests_tests_result'] = "total ${total}, passed ${passed}, failed ${failed}, skipped ${skipped}"
            if (failed == 0) {
                context.job.stages['tests_threshold_passed'] = true
            } else {
                context.job.stages['tests_threshold_passed'] = false
            }
        } else {
            context.job.stages['tests_threshold_passed'] = false
            context.job.stages['tests_tests_result'] = null
        }
    }
}