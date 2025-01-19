# Executar api localmente

## Iniciar localstack com serviços da AWS usando docker-compose

```
docker-compose up -d
```

## criar bucket s3

```
aws --endpoint-url=http://localhost:4566 s3 mb s3://images-extractions
```

## verificar se o bucket foi criado com sucesso

```
aws --endpoint-url=http://localhost:4566 s3 mb s3://images-extractions
```