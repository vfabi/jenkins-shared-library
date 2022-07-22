# jenkins-shared-library

Jenkins shared library implementation with some CI/CD stages examples.  


# Technology stack

- Groovy


# Configuration

1. Configure this library git repo URL in global Jenkins configuration ("Manage Jenkins -> Configure System -> Global Pipeline Libraries").
2. Create a Jenkins job: select Jenkinsfile as a pipeline code source and select your sourcecode git repository URL.
3. Create a Jenkinsfile with some configuration based on this Jenkins shared library. 
4. For bare-metal Jenkins setup use `master` branch, for Kubernetes Jenkins setup use `k8s` branch.


# Usage

Just configure your Jenkinsfile with some configuration based on this Jenkins shared library.  
Example:
```groovy

@Library('jsl@k8s')_  // The name of this jenkins shared library configured in Jenkins configuration and its branch.

def config = [
    projectType: 'application',
    buildTool: 'nodejs',
    releaseBranches: ['release-1', 'newbranch2'],
    stages: ['BuildDocker','Deploy']
]
Pipeline(config)
```


# Contributing
Please refer to each project's style and contribution guidelines for submitting patches and additions. In general, we follow the "fork-and-pull" Git workflow.

 1. **Fork** the repo on GitHub
 2. **Clone** the project to your own machine
 3. **Commit** changes to your own branch
 4. **Push** your work back up to your fork
 5. Submit a **Pull request** so that we can review your changes

NOTE: Be sure to merge the latest from "upstream" before making a pull request!


# License
Apache 2.0