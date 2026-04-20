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
        booleanParam(
            name: 'RUN_LIVE_TESTS',
            defaultValue: false,
            description: 'Run live contract smoke tests'
        )
    }

    environment {
        AUTH_USERNAME = credentials('auth-username')
        AUTH_PASSWORD = credentials('auth-password')
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

        stage('Live Contract Tests') {
            when {
                expression { params.RUN_LIVE_TESTS == true }
            }
            steps {
                sh 'mvn test -Dmode=live -Denv=live -Dgroups=live -DexcludedGroups='
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/**/*.xml'
            allure([
                includeProperties: false,
                reportBuildPolicy: 'ALWAYS',
                results: [[path: 'target/allure-results']]
            ])
        }
        failure {
            echo 'Tests failed — check Allure report for details'
        }
        success {
            echo 'All tests passed'
        }
    }
}