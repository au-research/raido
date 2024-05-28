#!/usr/bin/env bash

tag=$1

echo $tag > CURRENT_TAG
git commit -m "New tag for release: $tag"
git tag -a $tag -m "$tag"
git push --tags origin main
