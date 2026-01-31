# Centralized-Task-Execution-Retry-Engine

> **An enterprise-grade platform for reliable asynchronous task execution, retries, and operational recovery**

---

## 🚀 Overview

**Centralized-Task-Execution-Retry-Engine** is an internal-platform–style service designed to solve a problem that **every distributed system faces**:

> *How do we execute background tasks reliably when failures, retries, duplicates, and partial executions are inevitable?*

Instead of every microservice implementing its own retry logic, schedulers, idempotency checks, and failure handling, this platform **centralizes** those concerns into a **single, reusable reliability layer**.

---

## 🧠 Why This Project Exists

In real enterprise systems:

- Network failures are common  
- Downstream services are unreliable  
- Duplicate requests happen  
- Retries are often implemented inconsistently  
- Operational recovery is mostly manual  

Most teams solve these problems **independently**, leading to:
- Duplicated code
- Inconsistent retry behavior
- Retry storms
- Difficult debugging
- Poor observability

**Centralized-Task-Execution-Retry-Engine** addresses this by providing:
- A standard execution model
- Controlled retries with backoff
- Idempotency guarantees
- Dead-letter handling
- Admin-level recovery APIs
- Metrics and observability

---

## ✨ Key Features

### ✅ Reliable Async Execution
- Tasks are persisted before execution
- Execution happens asynchronously using a bounded thread pool
- HTTP request threads are never blocked

### 🔁 Automatic Retries with Exponential Backoff
- Configurable retry limits
- Backoff strategy to prevent retry storms
- Retry scheduling based on eligibility time

### 🧾 Idempotency (Exactly-Once Illusion)
- Redis-based idempotency keys
- Prevents duplicate execution across:
  - Client retries
  - Scheduler retries
  - Admin replays

### ☠️ Dead-Letter Handling
- Tasks exceeding retry limits are moved to `DEAD_LETTER`
- Requires explicit human intervention
- No infinite retries

### 🧑‍💻 Admin & Operational APIs
- Inspect failed and dead-letter tasks
- Manually retry tasks
- Replay tasks after fixes
- Debug production incidents safely

### 📊 Observability & Metrics
- Task creation, success, failure, retry counters
- Health endpoints via Spring Boot Actuator
- Ready for Prometheus / Grafana integration

---

## 🏗️ High-Level Architecture

![Architecture Diagram](docs/images/architecture.png)

**Core components:**
- REST API layer
- Persistent task store (RDBMS)
- Async execution engine (thread pool)
- Retry scheduler
- Redis for idempotency
- Metrics & health monitoring

---

   ↓
COMPLETED
