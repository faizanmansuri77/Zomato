
# DevSecOps CI/CD Pipeline Documentation – Zomato Clone

## Project Overview

This project demonstrates a complete **DevSecOps CI/CD pipeline** implementation for the Zomato Clone application using:

* AWS EC2
* Jenkins
* Docker
* SonarQube
* OWASP Dependency Check
* Trivy
* GitHub
* DockerHub

The pipeline automatically:

* Pulls code from GitHub
* Performs code quality analysis
* Runs security scans
* Builds Docker images
* Pushes images to DockerHub
* Deploys the application container

---

# Architecture Flow

```text
GitHub Repository
        ↓
Jenkins Pipeline
        ↓
SonarQube Analysis
        ↓
Quality Gate Validation
        ↓
Dependency Check
        ↓
Docker Build
        ↓
DockerHub Push
        ↓
Trivy Image Scan
        ↓
Container Deployment
```

---

# Step 1: Launch AWS EC2 Instance

Launch an EC2 instance with:

| Configuration    |          Value |
| ---------------- | -------------: |
| Instance Type    | m7i-flex.large |
| Storage          |          30 GB |
| Operating System |         Ubuntu |

Connect using SSH:

```bash
ssh -i key.pem ubuntu@<public-ip>
```

Update packages:

```bash
sudo apt update
```

---

# Step 2: Install AWS CLI

Download AWS CLI:

```bash
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" \
-o "awscliv2.zip"
```

Install unzip utility:

```bash
sudo apt install unzip -y
```

Extract files:

```bash
unzip awscliv2.zip
```

Install AWS CLI:

```bash
sudo ./aws/install
```

Verify installation:

```bash
aws --version
```

---

# Step 3: Install Git

Install Git:

```bash
sudo apt-get update

sudo apt-get install git -y
```

Verify installation:

```bash
git --version
```

---

# Step 4: Install Docker

Install Docker:

```bash
sudo apt update

sudo apt install docker.io -y
```

Add user to Docker group:

```bash
sudo usermod -aG docker ubuntu

newgrp docker
```

Grant permissions:

```bash
sudo chmod 777 /var/run/docker.sock
```

Verify Docker:

```bash
docker --version
```

---

# Step 5: Install Jenkins

Update packages:

```bash
sudo apt update
```

Install Java:

```bash
sudo apt install fontconfig openjdk-21-jre -y
```

Verify Java:

```bash
java -version
```

Add Jenkins repository:

```bash
sudo wget -O /etc/apt/keyrings/jenkins-keyring.asc \
https://pkg.jenkins.io/debian-stable/jenkins.io-2026.key
```

```bash
echo "deb [signed-by=/etc/apt/keyrings/jenkins-keyring.asc]" \
https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
/etc/apt/sources.list.d/jenkins.list > /dev/null
```

Install Jenkins:

```bash
sudo apt update

sudo apt install jenkins -y
```

Add Jenkins user to Docker group:

```bash
sudo usermod -aG docker jenkins
```

Restart services:

```bash
sudo systemctl restart docker

sudo systemctl restart jenkins
```

Check service status:

```bash
sudo systemctl status jenkins
```

Access Jenkins:

```text
http://<EC2-PUBLIC-IP>:8080
```

Retrieve Jenkins password:

```bash
cat /var/lib/jenkins/secrets/initialAdminPassword
```

Install:

```text
Suggested Plugins
```

---

# Step 6: Install Required Jenkins Plugins

Navigate:

```text
Dashboard → Manage Jenkins → Plugins
```

Install:

* JDK Plugin
* NodeJS Plugin
* SonarQube Scanner Plugin
* OWASP Dependency Check Plugin
* Docker Plugin
* Docker Commons
* Docker Pipeline
* Docker API
* docker-build-step

---

# Step 7: Install SonarQube

Run SonarQube container:

```bash
docker run -d \
--name sonar \
-p 9000:9000 \
sonarqube:lts-community
```

Open SonarQube:

```text
http://<EC2-PUBLIC-IP>:9000
```

Generate a token from:

```text
Administration → Security → Users → Tokens
```

Copy the generated token.

---

# Step 8: Install Trivy

Install Trivy:

```bash
curl -sfL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/install.sh \
| sudo sh -s -- -b /usr/local/bin v0.70.0
```

Verify:

```bash
trivy --version
```

---

# Step 9: Configure Jenkins Tools

Navigate:

```text
Dashboard → Manage Jenkins → Tools
```

Configure:

### JDK

```text
Name: jdk17
```

### NodeJS

```text
Name: node16
```

### Sonar Scanner

```text
Name: sonar-scanner
```

### OWASP Dependency Check

```text
Name: OWASP-DC
Version: dependency-check 12.2.2
```

Enable:

```text
Install automatically
```

---

# Step 10: Configure Jenkins Credentials

Navigate:

```text
Dashboard → Manage Jenkins → Credentials
```

Create:

### Sonar Token

```text
Kind: Secret Text
ID: sonar-token
```

Paste generated token.

Add DockerHub credentials:

```text
Username: your-dockerhub-username
Password: your-dockerhub-password
ID: docker
```

---

# Step 11: Configure SonarQube Server in Jenkins

Navigate:

```text
Dashboard → Manage Jenkins → System
```

Add SonarQube Server:

```text
Name: sonar-server

Server URL:
http://<SONAR-IP>:9000

Credentials:
sonar-token
```

---

# Step 12: Configure SonarQube Webhook

In SonarQube:

```text
Administration → Configuration → Webhooks
```

Add:

```text
http://<JENKINS-PUBLIC-IP>:8080/sonarqube-webhook/
```

---

# Step 13: Configure Docker in Jenkins

Navigate:

```text
Dashboard → Manage Jenkins → Tools
```

Add Docker:

```text
Name: docker
```

Enable:

```text
Install automatically
```

---

# Step 14: Create Jenkins Pipeline Job

Create a new Jenkins Pipeline:

```text
Dashboard → New Item → Pipeline
```

Name:

```text
Zomato-pipeline
```

Paste the following Jenkinsfile:

```groovy
[paste your pipeline code here]
```

---

# Pipeline Stages Explanation

| Stage                | Purpose                       |
| -------------------- | ----------------------------- |
| Clean Workspace      | Cleans previous build files   |
| Checkout from Git    | Pull source code              |
| SonarQube Analysis   | Analyze code quality          |
| Quality Gate         | Validate quality standards    |
| Install Dependencies | Install Node packages         |
| OWASP FS Scan        | Dependency vulnerability scan |
| Docker Build & Push  | Build and push Docker image   |
| Trivy Image Scan     | Container vulnerability scan  |
| Deploy to Container  | Deploy application            |

---

# Build Pipeline

Click:

```text
Build Now
```

Monitor:

```text
Dashboard → Zomato-pipeline → Console Output
```

---

# Verify Deployment

Check running containers:

```bash
docker ps
```

Open application:

```text
http://<EC2-PUBLIC-IP>:3000
```

---

# Expected Output

After successful execution:

✅ Source code cloned from GitHub
✅ SonarQube quality analysis completed
✅ Dependency vulnerabilities scanned
✅ Docker image built successfully
✅ Image pushed to DockerHub
✅ Trivy image scan completed
✅ Container deployed automatically

---

