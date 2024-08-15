import json

# Leitura do arquivo SARIF
with open("results/java.sarif/java.sarif", "r") as sarif_file:
    sarif_data = json.load(sarif_file)

# Geração do HTML
html_content = """
<html>
<head>
    <title>Relatório de Vulnerabilidades - Java</title>
</head>
<body>
    <h1>Relatório de Vulnerabilidades - Java</h1>
    <ul>
"""

for run in sarif_data.get("runs", []):
    for result in run.get("results", []):
        rule_id = result.get("ruleId", "N/A")
        message = result.get("message", {}).get("text", "N/A")
        html_content += f"<li><strong>{rule_id}</strong>: {message}</li>"

html_content += """
    </ul>
</body>
</html>
"""

# Escreve o conteúdo HTML em um arquivo
with open("relatorio_vulnerabilidades.html", "w") as html_file:
    html_file.write(html_content)
