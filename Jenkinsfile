pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                // Build steps (e.g., compile code, build artifacts)
                echo 'mvn clean package'
            }
        }

        stage('Test') {
            steps {
                // Testing steps (e.g., run unit tests, integration tests)
                echo 'mvn test'
            }
            post {
                always {
                    // Publish test reports
                    echo 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Deploy') {
            steps {
                // Deployment steps (e.g., deploy to test environment)
                echo 'Deploying to test environment...'
            }
        }
    }

    post {
        always {
            // Clean up after the build
            cleanWs()
        }
    }
}