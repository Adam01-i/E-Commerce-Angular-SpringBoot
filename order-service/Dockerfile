# Image officielle Java 21
FROM eclipse-temurin:21-jdk

# Dossier de travail
WORKDIR /app

# Le JAR sera copié ici lors du build
COPY target/*.jar app.jar

# Exposition du port (à adapter selon le service)
EXPOSE 8083

# Démarrage de l'application
ENTRYPOINT ["java","-jar","app.jar"]