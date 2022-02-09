#!/usr/bin/env groovy

/*
    Dependencies:
        - 'ansicolor' plugin
    Notes:
        “\u001B[31m” = RED
        “\u001B[30m” = BLACK
        “\u001B[32m” = GREEN
        “\u001B[33m” = YELLOW
        “\u001B[34m” = BLUE
        “\u001B[35m” = PURPLE
        “\u001B[36m” = CYAN
        “\u001B[37m” = WHITE
*/


def warning(message) {
    echo "\u001B[31mWARNING: ${message} \u001B[0m"
}

def info(message) {
    echo "\u001B[32mINFO: ${message} \u001B[0m"
}

def notice(message) {
    echo "\u001B[35mNOTICE: ${message} \u001B[0m"
}

def debug(message) {
    echo "\u001B[33mDEBUG:\n${message}\u001B[0m"
}

def handler(message) {
    echo "\u001B[34m\u276F handler: ${message} \u001B[0m"
}

def step(message) {
    echo "\u001B[34m\u276F\u276F STEP: ${message} \u001B[0m"
}

def stage(message) {
    echo "\n\u001B[34m\u001B[1m\u276F\u276F\u276F STAGE: ${message} \u001B[0m"
}