#!/bin/bash

case "${1}" in
tag)
  version="${TRAVIS_TAG}"
  echo "Uploading artifacts with version ${version}"
  ./gradlew bintrayUpload -Pversion="${version}"
  ;;
dev)
  version="0.0.1-dev-b${TRAVIS_BUILD_NUMBER}"
  echo "Uploading artifacts with version ${version}"
  ./gradlew bintrayUpload -Pversion="${version}"
  ;;
*)
  echo "Call script with tag or dev argument"
  ;;
esac

# TODO: Add signing and hash sums
#
#root="$(pwd)"
#
#set -e
#
#function upload() {
#  cd "${root}"
#  ./gradlew bintrayUpload
#}
#
#function build() {
#  cd "${root}"
#  ./gradlew clean jarSources jarDokka jar generatePomFileForUndertowHttpClientPublication
#}
#
#function sign() {
#  cd "${root}/build/libs/"
#  for file in *; do
#    gpg --output "${file}.asc" --sign "${file}"
#  done
#}
#
#function checksums() {
#  cd "${root}/build/libs/"
#  for file in *; do
#    md5sum "${file}" > "${file}.md5"
#    sha1sum "${file}" > "${file}.sha1"
#  done
#}
#
#build
#sign
#checksums
#upload
