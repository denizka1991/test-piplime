folder('Deploy')

deliveryPipelineView('Deploy/Pipeline') {
    pipelineInstances(5)
    allowPipelineStart()
    enableManualTriggers()
    showChangeLog()
    pipelines {
        component('Component', 'Deploy/Build')
    }
}

job('Deploy/Build') {
    deliveryPipelineConfiguration("Build", "Build")
    scm {
        git {
            remote {
                url('https://github.com/denizka1991/test-piplime')
            }
        }
    }
    wrappers {
        deliveryPipelineVersion('1.0.0.\$BUILD_NUMBER', true)
    }
    publishers {
        downstreamParameterized {
            trigger('Deploy/Sonar') {
                parameters {
                    currentBuild()
                }
            }
            trigger('Deploy/DeployCI') {
                condition('SUCCESS')
                parameters {
                    currentBuild()
                }
            }
        }
    }
}

job('Deploy/Sonar') {
    deliveryPipelineConfiguration("Build", "Static code analysis")
    scm {
        git {
            remote {
                url('https://github.com/denizka1991/test-piplime')
            }
        }
    }

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell 'sleep 10'
    }
}

job('Deploy/DeployCI') {
    deliveryPipelineConfiguration("CI", "Deploy")

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell 'sleep 5'
    }

    publishers {
        downstreamParameterized {
            trigger('Deploy/TestCI') {
                condition('SUCCESS')
                parameters {
                    currentBuild()
                }
            }
        }
    }
}

job('Deploy/TestCI') {
    deliveryPipelineConfiguration("CI", "Test")

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell 'sleep 10'
    }


    publishers {
        buildPipelineTrigger('Deploy/DeployQA') {
        }
    }
}

job('Deploy/DeployQA') {
    deliveryPipelineConfiguration("QA", "Deploy")

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell 'sleep 5'
    }

    publishers {
        downstreamParameterized {
            trigger('Deploy/TestQA') {
                condition('SUCCESS')
                parameters {
                    currentBuild()
                }
            }
        }
    }
}

job('Deploy/TestQA') {
    deliveryPipelineConfiguration("QA", "Test")

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell 'sleep 10'
    }

    publishers {
        buildPipelineTrigger('Deploy/DeployProd') {
        }
    }
}

job('Deploy/DeployProd') {
    deliveryPipelineConfiguration("Prod", "Deploy")

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell 'sleep 5'
    }

    publishers {
        downstreamParameterized {
            trigger('Deploy/TestProd') {
                condition('SUCCESS')
                parameters {
                    currentBuild()
                }
            }
        }
    }
}

job('Deploy/TestProd') {
    deliveryPipelineConfiguration("Prod", "Test")

    wrappers {
        buildName('\$PIPELINE_VERSION')
    }

    steps {
        shell 'sleep 5'
    }
}

