[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/9TN0gSSC)
# TCP-20252-final

# 🏆 MatchUp - Gerenciador de Torneios

Este projeto implementa um sistema de gerenciamento de torneios utilizando Java e JavaFX, seguindo um design de arquitetura limpa (MVC/Service) para garantir a manutenibilidade e a testabilidade do código.

## 🚀 Arquitetura e Estrutura do Projeto

Adotamos a arquitetura **Model-View-Controller (MVC)** estendida com uma **Camada de Serviço (Service Layer)** para isolar a lógica de negócio e a persistência da interface gráfica (UI).

### 1. Separação de Responsabilidades (SRP)

| Pacote | Responsabilidade | Tecnologia |
| :--- |:-----| :--- |
| `models` | Contém os **objetos de domínio** (`Tournament`, `Competitor`, etc.) e o estado da aplicação. **Não deve ter lógica de UI.** | Java POJOs (Imutáveis) |
| `services` | Contém a **Lógica de Negócio** (regras de cálculo, *pairing* de rodadas) e a **Persistência**. | Interfaces e Implementações (ex: `TournamentServiceIM`) |
| `controller` | Atua como *Presenter*. Gerencia o fluxo de dados entre o **View** e o **Service**. Contém apenas lógica de UI. | Java (Classes `@FXML`) |
| `view` | A interface com o usuário. Contém a classe de *Application Launcher* e os recursos de interface. | JavaFX Application Class |
| `resources` | Contém todos os arquivos de *design* e estilo. | FXML e CSS |

### 2. Princípio da Inversão de Dependência (DIP)

A comunicação é feita através de **Interfaces**.

* **`Controller`** depende da interface **`TournamentService`**, e não da implementação concreta (`TournamentServiceIM`).
* **Vantagem:** Permite trocar a fonte de dados (de *In-Memory* para **JDBC/SQLite**) sem alterar o código dos Controllers, garantindo o **Princípio Aberto/Fechado (OCP)**.

## 💻 Configuração e Execução

O projeto utiliza o **Maven** para gerenciar dependências e execução do JavaFX.

### Requisitos

* Java JDK 17+ (Preferencialmente JDK 23)
* Maven
