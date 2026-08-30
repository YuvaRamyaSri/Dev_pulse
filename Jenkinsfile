pipeline {

    agent any

    stages {

        stage('Build') {
            steps {
                bat 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Package') {
            steps {
                bat 'mvn package'
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.war'
            }
        }

    }
     post {

        success {
            echo '✅ Pipeline completed successfully'
        }

        failure {
            echo '❌ Pipeline failed'
        }

        always {
            echo 'Pipeline execution completed'
        }
    }
}
