#!/bin/bash
set -euxo pipefail

echo "TRAVIS_BUILD_DIR=${TRAVIS_BUILD_DIR}"

echo "Add ssh key to access remote server..."
openssl aes-256-cbc -K $encrypted_d1b137041ec5_key -iv $encrypted_d1b137041ec5_iv -in deploy@morty.enc -out ~/.ssh/deploy@morty -d
chmod 600 ~/.ssh/deploy@morty
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/deploy@morty

echo "Sync with remote folder..."
rsync -r --verbose "${TRAVIS_BUILD_DIR}/komodo-docs/build/dist/" deploy@heapy.io:~/files/heapy.io/komodo/docs/
source "${TRAVIS_BUILD_DIR}/komodo-docs/build/dist/version.properties"
ssh deploy@heapy.io "rm  ~/files/heapy.io/komodo/docs/latest; ln -s ~/files/heapy.io/komodo/docs/${version} ~/files/heapy.io/komodo/docs/latest"
