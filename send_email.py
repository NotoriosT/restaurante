import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

# Configurações SMTP para o Mailtrap
smtp_server = 'live.smtp.mailtrap.io'
smtp_port = 587
username = 'api'
password = 'd0d46ad32d2962a69081a4784a3f36c6'  # Atualize com a senha correta

# Configurações do e-mail
sender_email = 'mailtrap@demomailtrap.com'
receiver_email = 'tonon2002@gmail.com'
subject = 'Relatório de Vulnerabilidades'

# Corpo do e-mail em texto simples e HTML
text = """\
Segue o relatório de vulnerabilidades em anexo.
"""
html = """\
<!doctype html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  </head>
  <body style="font-family: sans-serif;">
    <div style="display: block; margin: auto; max-width: 600px;" class="main">
      <h1 style="font-size: 18px; font-weight: bold; margin-top: 20px">Relatório de Vulnerabilidades</h1>
      <p>Segue o relatório de vulnerabilidades em anexo.</p>
    </div>
  </body>
</html>
"""

# Criação da mensagem MIME
message = MIMEMultipart("alternative")
message["Subject"] = subject
message["From"] = sender_email
message["To"] = receiver_email

# Anexando partes de texto e HTML
part1 = MIMEText(text, "plain")
part2 = MIMEText(html, "html")
message.attach(part1)
message.attach(part2)

# Enviando o e-mail
try:
    with smtplib.SMTP(smtp_server, smtp_port) as server:
        server.starttls()  # Inicia a conexão TLS
        server.login(username, password)
        server.sendmail(sender_email, receiver_email, message.as_string())
        print("E-mail enviado com sucesso!")
except Exception as e:
    print(f"Erro ao enviar e-mail: {e}")
