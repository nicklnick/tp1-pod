#!/usr/bin/bash

if [ -f .env ]; then
    export $(cat .env | xargs)
fi

export MAVEN_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED"
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "Compilación exitosa. Iniciando SFTP..."

    sftp $SFTP_USER
else
    echo "La compilación falló, no se iniciará SFTP."
fi
