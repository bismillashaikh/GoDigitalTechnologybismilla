pipeline {
    agent any

    environment {
        AWS_ACCESS_KEY_ID = credentials('aws-access-key-id')
        AWS_SECRET_ACCESS_KEY = credentials('aws-secret-access-key')
        ECR_REPO = "my-repo"
    }

    stages {
        stage('Pull') {
            steps {
                git 'https://github.com/bismillashaikh/GoDigitalTechnologybismilla.git'
            }
        }
        stage('Terraform') {
            steps {
                dir('terraform') {
                    sh 'terraform init'
                }
            }
        }
        stage('Terraformapply') {
            steps {
                dir('terraform') {
                    sh 'terraform apply --auto-approve'
                }
            }
        }
        stage('docker') {
            steps {
                script {
                    dockerImage = docker.build("${env.ECR_REPO}:${env.BUILD_ID}")
                }
            }
        }
        stage('Pushimage') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'bismillashaikh') {
                        dockerImage.push()
                    }
                }
            }
        }
        stage('Lambda') {
            steps {
                script {
                    sh '''
                    aws lambda create-function \
                        --function-name interactWithRDS \
                        --package-type Image \
                        --code ImageUri=${ECR_REPO}:${env.BUILD_ID} \
                        --role arn:aws:iam::your-aws-account-id:role/lambda_execution_role
                    '''
                }
            }
        }
    }
}
