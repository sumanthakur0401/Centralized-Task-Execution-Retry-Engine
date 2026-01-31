## 🏗️ High-Level Architecture

![Architecture Diagram](docs/images/architecture.png)

At a high level, the platform works like a **reliability layer** that sits between producers of work and the actual execution logic.

**Core components:**
- **REST API layer** – accepts task submissions and admin actions
- **Persistent task store (RDBMS)** – source of truth for task state
- **Async execution engine** – executes tasks using a bounded thread pool
- **Retry scheduler** – retries failed tasks based on backoff rules
- **Redis** – provides idempotency guarantees
- **Metrics & health monitoring** – ensures operational visibility

The system is intentionally **stateless at runtime** and **stateful in storage**, which makes it resilient to crashes and restarts.

---

## 🔄 How a Task Flows Through the System

![Execution Flow](docs/images/flow.png)

1. A client submits a task via the API  
2. The task is persisted in the database (`RECEIVED`)  
3. Execution is dispatched asynchronously to a worker thread  
4. On success → task is marked `SUCCESS`  
5. On failure → task is scheduled for retry with backoff  
6. After max retries → task moves to `DEAD_LETTER`  
7. Admins can inspect, retry, or replay tasks safely  

This flow ensures **no task is lost**, even during crashes or restarts.

---

## ⚙️ What Makes This Interesting (And Useful)

This project is not about inventing a new idea —  
it’s about **doing a common thing correctly and consistently**.

What makes it valuable:

- **Failure is treated as a first-class concept**
- **Retries are deliberate, not accidental**
- **Duplicate execution is actively prevented**
- **Operational recovery is built-in**
- **Observability is part of the design, not an afterthought**

---

## 🧪 Example Use Cases

- Retry failed payment processing without double-charging
- Ensure emails or notifications are sent exactly once
- Reprocess failed background jobs after a bug fix
- Safely handle client retries and duplicate requests
- Provide ops teams visibility and control during incidents

---

## 🛠️ Tech Stack

| Layer | Technology |
|------|-----------|
| Language | Java 21 |
| Framework | Spring Boot |
| Persistence | Spring Data JPA |
| Database | H2 (local), pluggable for production |
| Cache | Redis |
| Async Execution | ThreadPoolTaskExecutor |
| Scheduling | Spring Scheduler |
| Observability | Spring Boot Actuator + Micrometer |
| API Docs | OpenAPI / Swagger |

---

## ▶️ Quick Start (Local)

```bash
git clone https://github.com/your-username/Centralized-Task-Execution-Retry-Engine.git
cd Centralized-Task-Execution-Retry-Engine
mvn spring-boot:run
