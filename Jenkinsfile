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

        stage('Build Docker Image') {
            steps {
                script {
                    def dockerImage
                    
                    // Build the Docker image from the source code's Dockerfile
                    dockerImage = docker.build("${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}", ".")
                    
                    // Optional: Push the Docker image to a registry
                    // docker.withRegistry('https://registry.example.com', 'docker-registry-credentials') {
                    //     dockerImage.push()
                    // }
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

}