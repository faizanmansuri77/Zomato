pipeline {
    agent any

    tools {
        jdk 'jdk17'
        nodejs 'node16'
    }

    environment {
        SCANNER_HOME = tool('sonar-scanner')
    }

    stages {

        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Checkout from Git') {
            steps {
                git(
                    branch: 'main',
                    url: 'https://github.com/faizanmansuri77/Zomato.git'
                )
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar-server') {
                    sh '''
                        $SCANNER_HOME/bin/sonar-scanner \
                        -Dsonar.projectName=zomato \
                        -Dsonar.projectKey=zomato
                    '''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: false
                }
            }
        }

        stage('Install Dependencies') {
            steps {
                sh 'npm install'
            }
        }

        stage('OWASP FS Scan') {
            steps {
                dependencyCheck(
                    additionalArguments: '--scan ./',
                    odcInstallation: 'OWASP-DC'
                )

                dependencyCheckPublisher(
                    pattern: 'dependency-check-report.xml'
                )
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    withDockerRegistry(
                        credentialsId: 'docker',
                        url: 'https://index.docker.io/v1/'
                    ) {

                        sh '''
                            docker build -t orionpax77/zomato:latest .
                            docker push orionpax77/zomato:latest
                        '''
                    }
                }
            }
        }

        stage('TRIVY Image Scan') {
            steps {
                sh '''
                    trivy image \
                    orionpax77/zomato:latest > trivy.txt
                '''
            }
        }

        stage('Deploy Container') {
            steps {
                sh '''
                    docker stop zomato || true
                    docker rm zomato || true

                    docker run -d \
                    --name zomato \
                    -p 3000:3000 \
                    orionpax77/zomato:latest
                '''
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'trivy.txt', allowEmptyArchive: true
        }

        success {
            echo 'Pipeline executed successfully!'
        }

        failure {
            echo 'Pipeline failed!'
        }
    }
}
