#!/bin/sh
echo "Starting services initialization..."

# Create S3 bucket
awslocal s3 mb s3://images-extractions

# Create DynamoDB table
awslocal dynamodb create-table \
    --table-name extraction_info \
    --attribute-definitions \
        AttributeName=user_id,AttributeType=S \
        AttributeName=id,AttributeType=S \
    --key-schema \
        AttributeName=user_id,KeyType=HASH \
        AttributeName=id,KeyType=RANGE \
    --provisioned-throughput \
        ReadCapacityUnits=1,WriteCapacityUnits=1

# Create SQS queue
awslocal sqs create-queue --queue-name extraction-info-queue

echo "Initialization complete!"