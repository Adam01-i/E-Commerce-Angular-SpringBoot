# ==========================================
# ÉTAPE 1 : Build de l'application avec Maven
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Définition du dossier de travail pour le build
WORKDIR /app

# Copie du fichier pom.xml pour télécharger les dépendances (mise en cache optimale)
COPY pom.xml .

# Téléchargement des dépendances en amont pour gagner du temps lors des builds suivants
RUN mvn dependency:go-offline -B

# Copie du code source du microservice
COPY src ./src

# Compilation et packaging de l'application en ignorant les tests pour le build Docker
RUN mvn clean package -DskipTests

# ==========================================
# ÉTAPE 2 : Image finale d'exécution (légère)
# ==========================================
FROM eclipse-temurin:21-jre-jammy

# Définition du dossier de travail dans le conteneur final
WORKDIR /app

# Copie du JAR généré à l'étape précédente depuis l'image de build
COPY --from=build /app/target/catalog-service-0.0.1-SNAPSHOT.jar app.jar

# Déclaration du port d'écoute officiel du Catalog Service (8082)
EXPOSE 8082

# Bonnes pratiques : Variables d'environnement par défaut pour optimiser la JVM
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Commande d'exécution sécurisée et optimisée
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]