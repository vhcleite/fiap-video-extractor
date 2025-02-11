#!/bin/sh
echo "Starting aws services initialization..."

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
        ReadCapacityUnits=10,WriteCapacityUnits=1

# Create SQS queue
awslocal sqs create-queue --queue-name extraction-info-queue
awslocal sqs create-queue --queue-name queue-notification-extraction

echo "Initialization complete!"