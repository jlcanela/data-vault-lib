#!/bin/bash

rm -rf docs/jgiven
cp -R target/jgiven-reports/html docs/jgiven

rm -rf docs/coverage
cp -R target/scala-2.12/scoverage-report docs/coverage