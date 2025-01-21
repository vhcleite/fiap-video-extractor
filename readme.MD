# Executar api localmente

## Iniciar localstack com serviços da AWS usando docker-compose

```
docker-compose up -d
```

## criar bucket s3

```
aws --endpoint-url=http://localhost:4566 s3 mb s3://images-extractions
```

## Criar tabela no dynamodb

```
aws --endpoint-url=http://127.0.0.1:4566 dynamodb create-table \
    --table-name extraction_info \
    --attribute-definitions AttributeName=user_id,AttributeType=S AttributeName=id,AttributeType=S \
    --key-schema AttributeName=user_id,KeyType=HASH AttributeName=id,KeyType=RANGE \
    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1
```

## Criar fila de comandos

```
aws --endpoint-url=http://127.0.0.1:4566 sqs create-queue --queue-name extraction-info-queue

```