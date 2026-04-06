pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK17'
    }

    parameters {
        choice(
            name: 'ENV',
            choices: ['local', 'staging', 'prod'],
            description: 'Target environment'
        )
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn clean test -Denv=${ENV}'
            }
        }

        stage('Publish Allure Report') {
            steps {
                allure([
                    includeProperties: false,
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'target/allure-results']]
                ])
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/**/*.xml'
        }
        failure {
            echo 'Tests failed — check Allure report for details'
        }
        success {
            echo 'All tests passed'
        }
    }
}