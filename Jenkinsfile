pipeline {
    agent any

    stages {
        stage('list directory') {
            steps {
                // Build steps (e.g., compile code, build artifacts)
                sh 'ls'
            }
        }

        stage('Test VS') {
            steps {
                // Testing steps (e.g., run unit tests, integration tests)
                sh 'podman image list'
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