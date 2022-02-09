#!/usr/bin/env groovy

package stages

import stages.impl.ci.Stage as ciStage
import stages.impl.cd.Stage as cdStage
import groovy.io.FileType
import java.lang.annotation.Annotation
import hudson.FilePath


class StageFactory {

    Script script
    private def stages = [:]

    @NonCPS
    def loadStages() {
        def classesList = []
        def res = Thread.currentThread().getContextClassLoader().getResources("stages/impl")
        def dir = new File(res.nextElement().getFile())
        dir.eachDirRecurse() { directory ->
            directory.eachFile(FileType.FILES) { file ->
                println "stages.impl.${directory.path.replace("${dir.path}/", "").replaceAll('/', '.')}."
                classesList.push(Class.forName("stages.impl.${directory.path.replace("${dir.path}/", "").replaceAll('/', '.')}."
                        + file.name.substring(0, file.name.length() - 7)))
            }
        }
        return classesList
    }

    void add(clazz) {
        Annotation ciAnnotation = clazz.getAnnotation(ciStage)
        if (ciAnnotation) {
            for (tool in ciAnnotation.buildTool()) {
                for (app in ciAnnotation.type()) {
                    stages.put(ciKey(ciAnnotation.name(), tool, app.getValue()), clazz)
                }
            }
        }

        Annotation cdAnnotation = clazz.getAnnotation(cdStage)
        if (cdAnnotation)
            stages.put(cdKey(cdAnnotation.name()), clazz)
    }

    def getStage(name, buildTool = null, type = null) {
        def stageClass
        if (buildTool && type)
            stageClass = stages.get(ciKey(name, buildTool, type)) ?: stages.get(ciKey(name, "any", type))
        else
            stageClass = stages.find { it.key == cdKey(name) }?.value

        if (!stageClass) {
            script.error("[StageFactory] There is no implementation for stage: ${name}, buildtool: ${buildTool}, projecttype: ${type}")
        }
        stageClass.newInstance(script: script)
    }

    private def ciKey(name, buildTool, type) {
        name + buildTool + type
    }

    private def cdKey(name) {
        name
    }
}
