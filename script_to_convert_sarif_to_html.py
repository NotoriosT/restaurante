import json
from sarif_om import SarifLog

def get_code_snippet(file_path, line_number, context=2):
    try:
        with open(file_path, 'r') as file:
            lines = file.readlines()
            start_line = max(line_number - context - 1, 0)
            end_line = min(line_number + context, len(lines))
            return ''.join(lines[start_line:end_line])
    except Exception as e:
        return f"Could not retrieve code snippet: {str(e)}"

def sarif_to_html(sarif_file, output_html):
    with open(sarif_file, "r") as sarif_file:
        sarif_data = json.load(sarif_file)
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

sarif_to_html("results/java.sarif", "relatorio_vulnerabilidades.html")
