# Use uma imagem base do OpenJDK
FROM openjdk:17-jdk-slim

# Adicione um volume apontando para /tmp
VOLUME /tmp

# Copie o arquivo JAR do projeto para o container
COPY target/*.jar app.jar

# Expõe a porta em que a aplicação Spring Boot estará rodando
EXPOSE 8080

# Define o comando padrão a ser executado quando o container inicia
ENTRYPOINT ["java", "-jar", "/app.jar"]
