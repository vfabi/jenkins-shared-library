package kubernetes


public void mainTemplate(body) {
    podTemplate(yaml: """
        apiVersion: v1
        kind: Pod
        metadata:
            name: build-docker-image
        spec:
            containers:
              - name: gcloud
                image: gcr.io/google.com/cloudsdktool/cloud-sdk:latest
                command:
                - cat
                tty: true
                volumeMounts:
                  - name: kaniko-secret
                    mountPath: /secret
                env:
                  - name: GOOGLE_APPLICATION_CREDENTIALS
                    value: /secret/av-cms-293206-90534c67e5c6.json

              - name: kaniko-gcp
                image: gcr.io/kaniko-project/executor:v1.6.0-debug
                imagePullPolicy: Always
                command:
                - /busybox/cat
                tty: true
                volumeMounts:
                  - name: kaniko-secret
                    mountPath: /secret
                  - name: kaniko-cache
                    mountPath: /cache
                    readOnly: true
                env:
                  - name: GOOGLE_APPLICATION_CREDENTIALS
                    value: /secret/av-cms-293206-90534c67e5c6.json

              - name: kaniko-aws
                image: gcr.io/kaniko-project/executor:v1.6.0-debug
                imagePullPolicy: Always
                command:
                - cat
                tty: true
                volumeMounts:
                  - name: docker-config
                    mountPath: /kaniko/.docker
                  - name: aws-secret
                    mountPath: /root/.aws/

              - name: python
                image: python:latest
                imagePullPolicy: Always
                command:
                - cat
                tty: true

            volumes:
              - name: docker-config
                configMap:
                  name: docker-config
              - name: aws-secret
                secret:
                  secretName: aws-secret
              - name: kaniko-secret
                secret:
                  secretName: kaniko-secret
              - name: kaniko-cache
                readOnly: true
                persistentVolumeClaim:
                  claimName: cd-jenkins-agent-kaniko-cache

            tolerations:
            - effect: NoSchedule
              key: tier
              operator: Equal
              value: cicd
            nodeSelector:
              tier: cicd
    """)
        {
            body.call()
        }
}


public void dockerTemplate(body) {
    podTemplate(
        containers: [containerTemplate(name: 'docker', image: 'docker', command: 'sleep', args: '99d')],
        volumes: [hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')]
    ) 
    {
        body.call()
    }
}


public void mavenTemplate(body) {
    podTemplate(
        containers: [containerTemplate(name: 'maven', image: 'maven', command: 'sleep', args: '99d')],
        volumes: [
            secretVolume(secretName: 'maven-settings', mountPath: '/root/.m2'),
            persistentVolumeClaim(claimName: 'maven-local-repo', mountPath: '/root/.m2repo')]
    )
    {
        body.call()
    }
}


return this