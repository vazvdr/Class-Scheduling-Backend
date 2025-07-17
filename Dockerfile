# Etapa de build (usa imagem com Maven e Java 21)
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

# Copia todos os arquivos do projeto
COPY . .

# Compila o projeto e gera o .jar (sem rodar testes)
RUN mvn clean package -DskipTests

# Etapa final: só com Java, mais leve
FROM eclipse-temurin:21
WORKDIR /app

# Copia o jar da etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
