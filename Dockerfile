FROM eclipse-temurin:24-jdk AS build
WORKDIR /app

# Copiar archivos del proyecto
COPY . .

# 🔧 Agregar permisos de ejecución al wrapper
RUN chmod +x mvnw

# Construir el proyecto con Maven Wrapper
RUN ./mvnw clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:24-jdk
WORKDIR /app

# Copiar el .jar construido desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Puerto que usará el contenedor
EXPOSE 8080

# Comando para correr el JAR
ENTRYPOINT ["java", "-jar", "app.jar"]

