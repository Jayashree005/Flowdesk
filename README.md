# FlowDesk — Intelligent Service Workflows, Built for Accountability

> **Problem Statement SD-05**: Enterprise Workflow-Based Service Ticket System  
> **Differentiator**: FlowDesk does not merely store tickets. It controls how tickets are allowed to move through an organization's service workflow.

---

## 🏛️ System Architecture

```
                          ┌──────────────────────────┐
                          │       React (Vite)       │
                          │   Enterprise SaaS UI     │
                          └────────────┬─────────────┘
                                       │ REST API (JSON)
                                       ▼
                          ┌──────────────────────────┐
                          │   Spring Boot Backend    │
                          │     (Java 17/20/25)      │
                          ├──────────────────────────┤
                          │ • Controller Layer       │
                          │ • Service Layer          │
                          │ • Workflow Rule Engine   │
                          │ • SLA Calculation Engine │
                          │ • Immutable Audit Trail  │
                          └────────────┬─────────────┘
                                       │ JPA / Hibernate
                                       ▼
                          ┌──────────────────────────┐
                          │     Database Layer       │
                          │ (H2 Embedded / Postgres) │
                          └──────────────────────────┘
```

---

## ⚡ Core Business Logic

### 1. Controlled State Machine
Tickets cannot transition arbitrarily. Every request is verified by the backend `WorkflowEngine`:
```
                ┌──────────────┐
                │     OPEN     │
                └──────┬───────┘
                       │ assign
                       ▼
                ┌──────────────┐
                │   ASSIGNED   │
                └──────┬───────┘
                       │ start
                       ▼
                ┌──────────────┐
                │ IN_PROGRESS  │
                └──────┬───────┘
                       │ resolve
                       ▼
                ┌──────────────┐
                │   RESOLVED   │
                └──────┬───────┘
                       │ close
                       ▼
                ┌──────────────┐
                │    CLOSED    │ (Terminal)
                └──────────────┘
```
- **Direct Closure Rejection**: Attempting `OPEN -> CLOSED` or `IN_PROGRESS -> CLOSED` returns `HTTP 400 Bad Request` with:
  > *"Invalid workflow transition: IN_PROGRESS -> CLOSED. Enterprise policy requires tickets to be properly RESOLVED and verified before CLOSURE."*
- **Reopen Flow**: A `RESOLVED` ticket can be verified and moved to `CLOSED`, or reopened to `IN_PROGRESS` if issues persist.
- **SLA Breach & Escalation**: When `currentTime > slaDeadline`, the ticket transitions to `ESCALATED`, recorded with `escalatedBy = "SYSTEM"` and an immutable audit entry.

### 2. Configurable Workflow Rules
Rules govern SLA duration and escalation based on **Category + Severity + Department**:
- **IT / CRITICAL**: 2h SLA ➔ Escalates to `IT Team Lead`
- **IT / HIGH**: 8h SLA ➔ Escalates to `Senior DevOps Lead`
- **HR / HIGH**: 12h SLA ➔ Escalates to `HR Operations Lead`
- **HR / LOW**: 48h SLA ➔ Escalates to `HR Coordinator`
- **FINANCE / HIGH**: 8h SLA ➔ Escalates to `Finance Director`

---

## 🚀 Running the Project

### Prerequisites
- Java 17+
- Node.js 18+ and npm

### 1. Spring Boot Backend
```powershell
cd backend
mvn spring-boot:run
```
- Server URL: `http://localhost:8080`
- H2 Web Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:flowdeskdb`, user: `sa`, no password)

### 2. React SaaS Frontend
```powershell
cd frontend
npm install
npm run dev
```
- Frontend UI: `http://localhost:5173`

---

## 🎯 Hackathon Judge Demo Script

1. **Service Operations Dashboard (`http://localhost:5173`)**:
   - Show the 4 KPI cards (`Open Tickets`, `SLA Risk`, `Escalated`, `Resolved`).
   - Observe the **Priority Workload distribution** and **SLA Risk Queue** with live countdown badges (`FD-1042`: 18m remaining, `FD-1025`: Breached by 1h 30m).

2. **State Machine Enforcement Demo**:
   - Open ticket `FD-1042` (Production API failure, `IN_PROGRESS`).
   - Click the **"⚡ Test Illegal Close"** button.
   - The UI displays the red workflow violation alert from the backend:
     `[WorkflowEngine Rejection] Invalid workflow transition: IN_PROGRESS -> CLOSED.`
   - Click **"✓ Mark as RESOLVED"** ➔ Status advances to `RESOLVED`.
   - Now click **"🔒 Verify & Close Ticket"** ➔ Transition succeeds to `CLOSED`.

3. **Immutable Audit Trail**:
   - Scroll down to the **Audit Trail** within `FD-1042` or switch to the **Global Audit Log** tab.
   - Every single creation, assignment, transition, and note is recorded with timestamp and actor.

4. **Automated SLA Escalation Demo**:
   - Click the **"Evaluate SLA Engine"** button in the top navigation bar.
   - Any ticket exceeding its deadline is automatically escalated to `ESCALATED` by `SYSTEM`.
