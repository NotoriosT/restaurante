import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.mime.base import MIMEBase
from email import encoders
import os

def send_email(subject, body, filename):
    smtp_server = os.getenv('SMTP_SERVER')
    smtp_port = os.getenv('SMTP_PORT')
    smtp_username = os.getenv('SMTP_USERNAME')
    smtp_password = os.getenv('SMTP_PASSWORD')
    email_from = os.getenv('EMAIL_FROM')
    email_to = os.getenv('EMAIL_TO')

    # Criação da mensagem
    msg = MIMEMultipart()
    msg['From'] = email_from
    msg['To'] = email_to
    msg['Subject'] = subject

    # Corpo do email
    msg.attach(MIMEText(body, 'plain'))

    # Anexo do arquivo
    attachment = open(filename, "rb")

    part = MIMEBase('application', 'octet-stream')
    part.set_payload((attachment).read())
    encoders.encode_base64(part)
    part.add_header('Content-Disposition', f"attachment; filename= {filename}")

    msg.attach(part)

    # Conexão com o servidor SMTP e envio do email
    try:
        server = smtplib.SMTP(smtp_server, smtp_port)
        server.starttls()
        server.login(smtp_username, smtp_password)
        text = msg.as_string()
        server.sendmail(email_from, email_to, text)
        server.quit()
        print("E-mail enviado com sucesso!")
    except Exception as e:
        print(f"Falha ao enviar email: {e}")

if __name__ == "__main__":
    project_name = os.getenv('GITHUB_REPOSITORY').split('/')[1]
    branch_name = os.getenv('GITHUB_REF').split('/')[-1]
    
    email_body = f"""
    Olá,

    Segue em anexo o relatório detalhado de vulnerabilidades para o projeto {project_name} na branch {branch_name}.

    Atenciosamente,
    Equipe de Segurança
    """

    send_email(
        subject=f"Vulnerability Report for {project_name} on branch {branch_name}",
        body=email_body,
        filename="relatorio_vulnerabilidades.html"
    )
