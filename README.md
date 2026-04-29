# 🚀 Gestor de Infraestrutura de Redes (Java + MySQL)

[![Status](https://img.shields.io/badge/Status-Finalizado-brightgreen)](https://github.com/)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=java&logoColor=white)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-005C84?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![PowerShell](https://img.shields.io/badge/PowerShell-5.1%20%2B-blue?logo=powershell&logoColor=white)](https://microsoft.com/powershell)
[![Cloudflare](https://img.shields.io/badge/Cloudflare-F38020?logo=cloudflare&logoColor=white)](https://www.cloudflare.com/)

Uma aplicação desktop robusta para a gestão hierárquica de ativos de rede, permitindo o mapeamento completo de topologias desde o cliente até ao dispositivo final.

---

## 📋 Índice
* [Funcionalidades](#-funcionalidades)
* [Tecnologias](#-tecnologias)
* [Arquitetura da Base de Dados](#-arquitetura-da-base-de-dados)
* [Instalação e Configuração](#-instalação-e-configuração)
* [Estrutura do Projeto](#-estrutura-do-projeto)
* [Extras e Túneis](#-extras-e-túneis)

---

## ✨ Funcionalidades

### 🔐 Segurança e Acesso (RBAC)
* **Login Multi-perfil:** Quatro níveis de permissão (Administrador, Colaborador, Cliente, Convidado).
* **Interface Dinâmica:** O sistema utiliza um controlo de visibilidade (`setVisible`) que adapta os menus e botões em tempo real de acordo com o nível de acesso do utilizador autenticado.

### 🌳 Gestão Hierárquica (Topologia)
* **Estrutura de Nodos:** Implementação de lógica recursiva para relações "Pai-Filho" (ex: Router > Switch > AP).
* **Visualização em Árvore:** Uso do componente `JTree` para navegar visualmente pela infraestrutura física de cada cliente.

### 💻 Gestão de Ativos
* Registo de equipamentos com validação de máscara para endereços MAC.
* **Filtragem em Cascata:** A seleção de nodos é automaticamente limitada ao cliente selecionado, prevenindo erros de inventário cruzado.
* Pesquisa avançada por Nome do Dispositivo ou MAC Address.

---

## 🛠 Tecnologias

* **Linguagem Principal:** Java 21 (Swing para interface gráfica).
* **Base de Dados:** MariaDB / MySQL.
* **Scripts de Automação:** PowerShell (utilizado para tarefas de suporte e configuração de ambiente).
* **Conectividade Remota:** Cloudflare Tunnel (`cloudflared`) para acessibilidade Zero Trust.

---

## 🗄 Arquitetura da Base de Dados

O projeto utiliza um esquema relacional normalizado para garantir a integridade dos dados e evitar redundâncias.

| Tabela | Função |
| :--- | :--- |
| `clientes` | Armazena a entidade raiz de cada infraestrutura. |
| `nodos` | Ativos de rede principais (Routers, Switches) com auto-relação (`id_pai`). |
| `equipamentos_ligados` | Dispositivos finais (PCs, Impressoras, etc) e a sua localização. |
| `utilizadores` | Credenciais e associação a perfis de acesso. |
| `niveis_acesso` | Dicionário de permissões do sistema. |

---

## ⚙️ Instalação e Configuração

### 1. Requisitos Prévios
* **JDK 21** ou superior.
* Servidor **MySQL/MariaDB**.
* Terminal **PowerShell 5.1+** para scripts de apoio.

### 2. Configuração da Base de Dados
Cria a base de dados e executa o script SQL incluído:
```sql
CREATE DATABASE gestao_redes;
USE gestao_redes;
```
-- Importar ficheiro sql/estrutura.sql

3. Ligação ao Servidor
Configura as tuas credenciais no ficheiro jbd.java:

```java
private String url = "jdbc:mysql://[TEU_IP]:3306/gestao_redes";
private String user = "teu_utilizador";
private String pass = "tua_password";
```

## 📂 Estrutura do Projeto
src/gestao: Código fonte da lógica de negócio e janelas Swing.

src/IMG: Recursos visuais e ícones (carregados via getClass().getResource).

lib: Drivers JDBC e dependências.

## 🌟 Extras e Túneis
### Cloudflare Tunnel & startTunnel()
A aplicação inclui a funcionalidade startTunnel(), que permite estabelecer uma ligação segura entre o cliente e a base de dados através da infraestrutura Cloudflare. Isto elimina a necessidade de abrir portas no router local (Port Forwarding), aumentando a segurança contra ataques externos.

### Relatórios CSV
Módulo integrado para exportação de inventários em formato .csv, utilizando o separador ; para total compatibilidade com Microsoft Excel em configurações regionais europeias.

## 👤 Autor
Jorge - Desenvolvimento e Arquitetura de Sistemas
