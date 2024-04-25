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
                // sh 'docker build -t registry.t11.caas.gcisdctr.hksarg:30128/cmab-backend .'
                // script {
                //     def dockerImage
                    
                //     // Build the Docker image from the source code's Dockerfile
                //     dockerImage = docker.build("${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}", ".")
                    
                //     // Optional: Push the Docker image to a registry
                //     // docker.withRegistry('https://registry.example.com', 'docker-registry-credentials') {
                //     //     dockerImage.push()
                //     // }
                // }
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