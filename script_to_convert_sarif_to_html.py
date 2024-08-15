import json
import os

def sarif_to_html(sarif_file, output_html):
    with open(sarif_file, "r") as file:
        sarif_data = json.load(file)
        
    html_content = """
    <html>
    <head>
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 20px;
                background-color: #f9f9f9;
                color: #333;
            }
            h1 {
                color: #333;
                text-align: center;
            }
            h2 {
                color: #555;
                border-bottom: 1px solid #ccc;
                padding-bottom: 5px;
            }
            p {
                color: #444;
            }
            pre {
                background-color: #f4f4f4;
                border-left: 5px solid #cc0000;
                padding: 10px;
                overflow-x: auto;
                color: #111;
            }
            .vulnerability {
                border: 1px solid #ddd;
                padding: 15px;
                margin-bottom: 20px;
                border-radius: 5px;
                background-color: #fff;
            }
            .critical {
                border-left: 5px solid #d9534f;
                background-color: #f2dede;
            }
            .high {
                border-left: 5px solid #f0ad4e;
                background-color: #fcf8e3;
            }
            .medium {
                border-left: 5px solid #5bc0de;
                background-color: #d9edf7;
            }
            .low {
                border-left: 5px solid #5cb85c;
                background-color: #dff0d8;
            }
            .filters {
                text-align: center;
                margin-bottom: 20px;
            }
            .filters button {
                margin: 0 5px;
                padding: 10px 20px;
                font-size: 16px;
                border: none;
                border-radius: 5px;
                cursor: pointer;
            }
            .filters .critical-btn {
                background-color: #d9534f;
                color: white;
            }
            .filters .high-btn {
                background-color: #f0ad4e;
                color: white;
            }
            .filters .medium-btn {
                background-color: #5bc0de;
                color: white;
            }
            .filters .low-btn {
                background-color: #5cb85c;
                color: white;
            }
            .filters .show-all {
                background-color: #777;
                color: white;
            }
        </style>
        <script>
            function filterVulnerabilities(level) {
                var vulnerabilities = document.getElementsByClassName('vulnerability');
                for (var i = 0; i < vulnerabilities.length; i++) {
                    vulnerabilities[i].style.display = 'none';
                    if (vulnerabilities[i].classList.contains(level) || level === 'all') {
                        vulnerabilities[i].style.display = 'block';
                    }
                }
            }
        </script>
    </head>
    <body>
        <h1>Vulnerability Report</h1>
        <div class="filters">
            <button class="critical-btn" onclick="filterVulnerabilities('critical')">Critical</button>
            <button class="high-btn" onclick="filterVulnerabilities('high')">High</button>
            <button class="medium-btn" onclick="filterVulnerabilities('medium')">Medium</button>
            <button class="low-btn" onclick="filterVulnerabilities('low')">Low</button>
            <button class="show-all" onclick="filterVulnerabilities('all')">Show All</button>
        </div>
    """

    for run in sarif_data.get('runs', []):
        for result in run.get('results', []):
            rule_id = result.get('ruleId', 'N/A')
            message = result.get('message', {}).get('text', 'No message provided')
            location = result.get('locations', [])[0].get('physicalLocation', {})
            file_path = location.get('artifactLocation', {}).get('uri', 'Unknown file')
            line_number = location.get('region', {}).get('startLine', 'Unknown line')
            
            # Ajuste manual para mapear severidade corretamente
            if 'properties' in result and 'securitySeverityLevel' in result['properties']:
                severity = result['properties']['securitySeverityLevel'].lower()
            else:
                severity = 'low'  # Default se não encontrado
                
            # Captura descrição da regra e recomendações
            rules = run.get('tool', {}).get('driver', {}).get('rules', [])
            matching_rule = next((rule for rule in rules if rule.get('id') == rule_id), None)
            if matching_rule:
                rule_desc = matching_rule.get('fullDescription', {}).get('text', '')
                recommendation = matching_rule.get('properties', {}).get('securityStandards', [])
                references = matching_rule.get('properties', {}).get('references', [])
                cwe_id = matching_rule.get('properties', {}).get('cwe', 'N/A')
            else:
                rule_desc = 'No description available'
                recommendation = []
                references = []
                cwe_id = 'N/A'

            # Gera o HTML para a vulnerabilidade
            code_snippet = get_code_snippet(file_path, line_number)
            html_content += f"""
            <div class="vulnerability {severity}">
                <h2>Vulnerability: {rule_id}</h2>
                <p><strong>Severity:</strong> {severity.capitalize()}</p>
                <p><strong>Message:</strong> {message}</p>
                <p><strong>Description:</strong> {rule_desc}</p>
                <p><strong>File:</strong> {file_path}</p>
                <p><strong>Line:</strong> {line_number}</p>
                <p><strong>CWE ID:</strong> {cwe_id}</p>
                <pre>{code_snippet}</pre>
                <p><strong>Recommendation:</strong> {', '.join(recommendation) if recommendation else 'No recommendation available'}</p>
                <p><strong>References:</strong> {', '.join(references) if references else 'No references available'}</p>
            </div>
            """

    html_content += "</body></html>"

    with open(output_html, "w") as html_file:
        html_file.write(html_content)

    return html_content

def get_code_snippet(file_path, line_number, context_lines=2):
    snippet = []
    try:
        if os.path.exists(file_path):
            with open(file_path, "r") as file:
                lines = file.readlines()
                start_line = max(0, line_number - context_lines - 1)
                end_line = min(len(lines), line_number + context_lines)
                snippet = lines[start_line:end_line]
        else:
            snippet = ["Código não encontrado: o arquivo não existe."]
    except Exception as e:
        snippet = [f"Erro ao ler o arquivo: {e}"]
    
    return "".join(snippet)

if __name__ == "__main__":
    sarif_to_html("results/java.sarif/java.sarif", "relatorio_vulnerabilidades.html")
