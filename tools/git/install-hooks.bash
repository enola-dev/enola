#!/usr/bin/env bash
set -euxo pipefail

DIR="$(realpath $(dirname $0))"

cp $DIR/hooks/* $DIR/../../.git/hooks/
