# RELATÓRIO DE IMPACTO À PROTEÇÃO DE DADOS (RIPD)

**Projeto:** Video extractor  
**Organização:** Pós graduação FIAP  
**Responsável pelo Relatório:**  Victor Hugo da Costa Leite  
**Data da Elaboração:** 09/02/2025

---

## 1. INTRODUÇÃO

Este Relatório de Impacto à Proteção de Dados (RIPD) tem como objetivo documentar o tratamento de dados pessoais
realizado no âmbito do projeto **Video extractor**, bem como avaliar os riscos associados e as medidas adotadas para
mitigar impactos à privacidade dos titulares, em conformidade com a Lei Geral de Proteção de Dados (LGPD).

---

## 2. DESCRIÇÃO DO PROCESSAMENTO DE DADOS

**2.1. Finalidade do Tratamento**

- O sistema em questão recebe um arquivo video do usuário e extrai imagens desse vídeo. Os dados coletados serão usados
  exclusivamente para permitir autenticação do usuário, processamento do vídeo, geração de arquivo de saída e também
  comunicações ao usuário sobre o estado do processamento do arquivo pelo sistema.

**2.2. Categoria de Titulares**

- usuários da plataforma

**2.3. Tipo de Dados Coletados**  
| Dados pessoais comuns | E-mail | Identificação e autenticação do usuário |  
| Dados de uso | Logs de requisição, status de processamento | Monitoramento e melhorias do sistema |  
| Dados enviados pelo usuário | Arquivo de vídeo | Processamento e extração de imagens |

**2.4. Fluxo de Dados e Compartilhamento**

- **Origem:** Os dados serão coletados do usuário no momento de cadastro do cliente na plataforma e também por meio das
  requisições feitas à plataforma.
- **Armazenamento:** Os dados serão armazenados em banco de dados da Nuvem AWS.

**2.5. Base Legal para o Tratamento**

- **Consentimento do usuário** (Art. 7º, I da LGPD) – obtido no momento do cadastro e uso da plataforma.

---

## 3. AVALIAÇÃO DOS RISCOS

| Risco Identificado    | Impacto Potencial                 | Probabilidade | Medidas Mitigadoras                     |
|-----------------------|-----------------------------------|---------------|-----------------------------------------|
| Vazamento de dados    | Exposição de informações pessoais | Média         | Criptografia, controle de acesso        |
| Uso indevido de dados | Tratamento não autorizado         | Alta          | Políticas de segurança e consentimento  |
| Ataques cibernéticos  | Acesso não autorizado             | Alta          | Firewalls, autenticação de dois fatores |

---

## 4. MEDIDAS DE SEGURANÇA ADOTADAS

**4.1. Medidas Técnicas**

- Criptografia de dados em repouso e em trânsito.
- Controle de acesso baseado em funções (RBAC).
- Monitoramento contínuo e auditoria de logs.

**4.2. Medidas Organizacionais**

- Treinamento de equipe sobre privacidade e proteção de dados.
- Revisão periódica de acessos e permissões.
- Política de Privacidade e Termos de Uso claros para os titulares.

---

## 5. CONCLUSÃO

Este relatório documenta as práticas de proteção de dados implementadas no projeto **Video Extractor**, garantindo
conformidade com a LGPD. Todas as medidas foram desenhadas para reduzir riscos e proteger os direitos dos titulares.

---

## 6. APROVAÇÃO

| Nome                       | Cargo                            | Assinatura  | Data       |
|----------------------------|----------------------------------|-------------|------------|
| Victor Hugo da Costa Leite | Encarregado de Proteção de Dados | ___________ | 09/02/2025 |

---
