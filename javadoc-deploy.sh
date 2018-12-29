#!/usr/bin/env bash
set -o errexit

# config
git config --global user.email "damian@stachuofficial.tv"
git config --global user.name "Damian Staszewski"

./gradlew javadoc

JAVADOC_PATH=./build/javadocs
JAVADOC_REPO=https://${GITHUB_TOKEN}@github.com/stachu540/Commandor.git
JAVADOC_DESTINATION=${JAVADOC_PATH}/${TRAVIS_TAG}

git clone -b gh-pages --single-branch ${JAVADOC_REPO} ${JAVADOC_PATH}

mkdir ${JAVADOC_DESTINATION}

for PROJECT in "api" "glitch" "twitch4j" "jda" "discord4j" "javacord" "docs"; do
    DIRECTORY=${PROJECT}/build/javadoc

    if [[ PROJECT -eq "docs" ]]; then
        PROJECT="all"
    fi

    cp -ar ${DIRECTORY} ${JAVADOC_DESTINATION}/${PROJECT}
done

cd ${JAVADOC_PATH}

#cp -L ./latest/index.html ./${TRAVIS_TAG}/index.html
ln -sfn ./${TRAVIS_TAG} ./latest

git add .
git commit -m "Build release: ${TRAVIS_TAG}"
git push origin gh-pages
