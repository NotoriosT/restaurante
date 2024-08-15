import json

def sarif_to_html(sarif_file, output_html):
    with open(sarif_file, "r") as file:
        sarif_data = json.load(file)
        
    html_content = "<html><body><h1>Vulnerability Report</h1>"

    for run in sarif_data.get('runs', []):
        for result in run.get('results', []):
            rule_id = result.get('ruleId', 'N/A')
            message = result.get('message', {}).get('text', 'No message provided')
            location = result.get('locations', [])[0].get('physicalLocation', {})
            file_path = location.get('artifactLocation', {}).get('uri', 'Unknown file')
            line_number = location.get('region', {}).get('startLine', 'Unknown line')

            # Aqui, você pode implementar uma função que obtém o snippet de código, se desejar
            code_snippet = get_code_snippet(file_path, line_number)
            
            html_content += f"<h2>Vulnerability: {rule_id}</h2>"
            html_content += f"<p><strong>Message:</strong> {message}</p>"
            html_content += f"<p><strong>File:</strong> {file_path}</p>"
            html_content += f"<p><strong>Line:</strong> {line_number}</p>"
            html_content += f"<pre>{code_snippet}</pre>"
            html_content += "<hr>"

    html_content += "</body></html>"

    with open(output_html, "w") as html_file:
        html_file.write(html_content)

def get_code_snippet(file_path, line_number):
    # Implementar a lógica para ler o arquivo e pegar o snippet de código na linha especificada
    # Por enquanto, retornamos uma string vazia como exemplo
    return "Código de exemplo na linha " + str(line_number)

# Altere o caminho para apontar diretamente para o arquivo SARIF
sarif_to_html("results/java.sarif/java.sarif", "relatorio_vulnerabilidades.html")
