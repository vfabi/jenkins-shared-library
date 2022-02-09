#!/usr/bin/env groovy

package pipelines


enum JobType {
    BUILD("build"), RELEASE("release")

    private String value

    JobType(String value) {
        this.value = value
    }

    String getValue() {
        return value
    }
}