pipeline {

    agent any

    stages {

        stage('Build') {
            steps {
                bat 'echo Building application'
                bat 'java -version'
                bat 'git --version'
            }
        }

        stage('Test') {
            steps {
                echo 'Running Tests'
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying Application'
            }
        }

    }
}
