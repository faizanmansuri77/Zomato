# Zomato Clone — Interview Cheat Sheet

\---

## What is this project?

A **React-based Zomato clone** with a complete **DevSecOps CI/CD pipeline**.
The app is just the frontend. The real focus is the automated pipeline — code is pushed to GitHub and the pipeline handles everything: quality checks, security scans, Docker build, and deployment — without manual steps.

\---

## Tech Stack

|Layer|Tool|
|-|-|
|Frontend|React 18, SCSS, Material UI|
|CI/CD|Jenkins (Declarative Pipeline)|
|Code Quality|SonarQube|
|Dependency Security|OWASP Dependency Check|
|Container Security|Trivy|
|Containerization|Docker|
|Registry|DockerHub|
|Cloud|AWS EC2 (Ubuntu, m7i-flex.large)|

\---

## Pipeline Stages (in order)

1. **Clean Workspace** — removes old build files, fresh start every time
2. **Git Checkout** — pulls latest code from GitHub (main branch)
3. **SonarQube Analysis** — static code analysis (bugs, smells, vulnerabilities)
4. **Quality Gate** — waits up to 5 min for SonarQube pass/fail verdict
5. **npm install** — installs all Node.js dependencies
6. **OWASP FS Scan** — scans npm packages for known CVEs (NVD database)
7. **Docker Build \& Push** — builds image, pushes to DockerHub (orionpax77/zomato:latest)
8. **Trivy Image Scan** — scans Docker image for OS-level vulnerabilities → saved to trivy.txt
9. **Deploy Container** — stops old container, runs new one on port 3000

\---

## Security Tools — Key Differences

* **SonarQube** → scans *source code* for bugs and code quality issues
* **OWASP Dependency Check** → scans *npm packages* against CVE database (app-level)
* **Trivy** → scans *Docker image* layers — OS + runtime (container-level)

Together = full coverage from code → dependencies → container.

\---

## React App Components

* **Header** — navbar, hamburger menu, hero search bar with city dropdown
* **Card** — 3 service tiles: Order Online, Dining, Nightlife
* **Collection** — curated restaurant collections grid
* **Cities** — browse by city section
* **CTA** — app download section (iPhone image + App Store / Play Store)
* **AccContainer / Accordion** — FAQ section (data from data.js)
* **Footer** — links and info

State: only `useState` for hamburger toggle. Data: static array in `src/data.js`.

\---

## Dockerfile Notes

* Base: `node:16-slim` (slim = smaller image)
* Copies `package\*.json` first → then `npm install` → then rest of source
* Why? **Layer caching** — npm install only re-runs if dependencies change
* Runs `npm run build`, exposes port 3000, starts with `npm start`

\---

## AWS / Infrastructure

* Single EC2 instance: Ubuntu, m7i-flex.large, 30 GB
* Jenkins on port **8080**
* SonarQube (Docker container) on port **9000**
* App container on port **3000**
* Credentials stored securely in Jenkins (DockerHub ID: `docker`, SonarQube ID: `sonar-token`)
* SonarQube webhook → `http://JENKINS-IP:8080/sonarqube-webhook/`

\---

## Common Interview Questions

**Q: What is DevSecOps?**
Security is integrated into every stage of the pipeline automatically — not as a final gate. Also called "Shift Left Security."

**Q: OWASP vs Trivy — what's the difference?**
OWASP = app dependencies (npm packages). Trivy = container image (OS + packages inside Docker). They cover different layers.

**Q: What if Quality Gate fails?**
I used `abortPipeline: false` so the build continues. In production, you'd use `true` to block deployment of low-quality code.

**Q: How would you improve this?**

* Multi-stage Docker builds (smaller final image)
* Kubernetes/EKS instead of single container
* Slack/email notifications on failure
* Prometheus + Grafana for monitoring
* Staging environment before production

\---

## One-line project pitch

> "A Zomato frontend clone used as a base to demonstrate a complete DevSecOps pipeline — automated code quality, security scanning at both the application and container level, Dockerized deployment on AWS, all orchestrated through Jenkins."

