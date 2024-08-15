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
                background-color: #f4f4f4;
            }
            h1 {
                color: #333;
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
                background-color: #eee;
                border-left: 3px solid #cc0000;
                padding: 10px;
                color: #111;
            }
            .vulnerability {
                background-color: #fff;
                border: 1px solid #ddd;
                padding: 15px;
                margin-bottom: 20px;
                border-radius: 5px;
            }
        </style>
    </head>
    <body>
        <h1>Vulnerability Report</h1>
    """

    for run in sarif_data.get('runs', []):
        for result in run.get('results', []):
            rule_id = result.get('ruleId', 'N/A')
            message = result.get('message', {}).get('text', 'No message provided')
            location = result.get('locations', [])[0].get('physicalLocation', {})
            file_path = location.get('artifactLocation', {}).get('uri', 'Unknown file')
            line_number = location.get('region', {}).get('startLine', 'Unknown line')

            code_snippet = get_code_snippet(file_path, line_number)
            
            html_content += f"""
            <div class="vulnerability">
                <h2>Vulnerability: {rule_id}</h2>
                <p><strong>Message:</strong> {message}</p>
                <p><strong>File:</strong> {file_path}</p>
                <p><strong>Line:</strong> {line_number}</p>
                <pre>{code_snippet}</pre>
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
