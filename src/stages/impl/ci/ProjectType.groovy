#!/usr/bin/env groovy

package stages.impl.ci


enum ProjectType {
    APPLICATION("application"), LIBRARY("library"), AUTOTESTS("autotests")

    private String value

    ProjectType(String value) {
        this.value = value
    }

    String getValue() {
        return value
    }
}