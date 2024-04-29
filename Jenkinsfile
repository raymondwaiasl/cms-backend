pipeline {
    agent any

    environment {
        DOCKER_IMAGE_NAME = 'cmab-backend'
        DOCKER_IMAGE_TAG = 'latest'
    }

    stages {
        stage('list directory') {
            steps {
                // Build steps (e.g., compile code, build artifacts)
                sh 'ls'
            }
        }

        stage('Check podman version') {
            steps {
                sh 'oc version'
                sh 'oc login -u cmadashb-c01 -p Cm@bP@ssw0rd --server=https://api.t11.caas.gcisdctr.hksarg:6443'
                sh 'oc get po'
            }
        }

        stage('Deploy') {
            steps {
                // Deployment steps (e.g., deploy to test environment)
                echo 'Deploying to test environment...'
            }
        }
    }

}