name: CODEQL

on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]
  schedule:
    - cron: '0 2 * * 1'

jobs:
  analyze:
    name: Analyze Java
    runs-on: ubuntu-latest

    permissions:
      contents: write
      security-events: write

    steps:
    - name: Checkout repository
      uses: actions/checkout@v2

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        distribution: 'temurin'
        java-version: '17'

    - name: Initialize CodeQL
      uses: github/codeql-action/init@v2
      with:
        languages: java

    - name: Build with Maven
      run: mvn clean install -DskipTests

    - name: Perform CodeQL Analysis
      uses: github/codeql-action/analyze@v2
      with:
        output: ${{ github.workspace }}/results/java.sarif

    - name: Install Python Dependencies
      run: pip install sarif-om

    - name: Convert SARIF to Detailed HTML Report
      run: |
        python script_to_convert_sarif_to_html.py

    - name: Send Email with Report
      run: |
        python send_email.py
      env:
        SMTP_USERNAME: ${{ secrets.SMTP_USERNAME }}
        SMTP_PASSWORD: ${{ secrets.SMTP_PASSWORD }}
        SMTP_SERVER: "live.smtp.mailtrap.io"
        SMTP_PORT: 587
        EMAIL_FROM: "mailtrap@demomailtrap.com"
        EMAIL_TO: "tonon2002@gmail.com"
        GITHUB_REPOSITORY: ${{ github.repository }}
        GITHUB_REF: ${{ github.ref }}
