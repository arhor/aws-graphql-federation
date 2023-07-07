#!/bin/bash

awslocal cloudformation deploy \
    --stack-name "graphql-playground-dev" \
    --template-file "/cloudformation/graphql-playground.yml"
