pipeline {

    agent any
    environment {
        APP_NAME = 'devpulse'
        APP_VERSION = '1.0.0'
    }
    parameters {
    choice(
        name: 'ENVIRONMENT',
        choices: ['DEV', 'QA', 'PROD']
    )
}
    stages {

        stage('Build') {
            steps {
                 bat 'echo Building %APP_NAME%'
                bat 'echo Version %APP_VERSION%'
                bat 'echo Building %ENVIRONMENT%'
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
