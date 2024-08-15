import json  # Adicione esta linha para importar o módulo json
from sarif_om import SarifLog

def sarif_to_html(sarif_file, output_html):
    with open(sarif_file, "r") as file:
        sarif_data = json.load(file)  # Agora o json.load() funcionará corretamente
        sarif_log = SarifLog.from_dict(sarif_data)
        
    html_content = "<html><body><h1>Vulnerability Report</h1>"

    for run in sarif_log.runs:
        for result in run.results:
            rule_id = result.rule_id
            message = result.message.text
            location = result.locations[0].physical_location
            file_path = location.artifact_location.uri
            line_number = location.region.start_line

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

# Altere o caminho para apontar diretamente para o arquivo SARIF
sarif_to_html("results/java.sarif/java.sarif", "relatorio_vulnerabilidades.html")
