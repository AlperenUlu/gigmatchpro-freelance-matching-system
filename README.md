# 💼 GigMatch Pro

GigMatch Pro is a Java-based freelance marketplace simulation focused on
**efficient ranking**, **custom data structures**, and **scalable user management**.

The system matches customers with freelancers based on composite scores derived from
skills, ratings, reliability, and dynamic system states such as burnout and loyalty tiers.

---

## ✨ Features

- Customer and freelancer registration
- Priority-based freelancer ranking
- Dynamic skill evolution and rating updates
- Burnout and recovery simulation
- Customer loyalty tiers and platform subsidies
- Blacklisting at both user and platform level
- Month-based system simulation

---

## 🏗️ Architecture Overview

The system is designed around **explicit performance constraints** and avoids
unnecessary abstraction layers.  
All users are indexed using custom data structures to ensure predictable runtime
behavior under large input sizes.

---

## 🧩 Data Structures

- **Custom Hash Table**
  - User lookup by ID
  - Average-case access: **O(1)**
- **Custom Maximum Heap**
  - Used for ranking freelancers by composite score
  - Insert / extract-max: **O(log N)**
- **User Tables**
  - Separate indexing for customers and freelancers
- **Primitive-first design**
  - Composite scores are integers to avoid floating-point comparison overhead

Built-in maps and priority queues are intentionally avoided.

---

## 🧠 Ranking & Matching Logic

Freelancers are ranked using an integer composite score derived from:
- Skill–service compatibility
- Average rating
- Reliability (completion vs cancellation ratio)
- Burnout penalty

Tie-breaking is handled deterministically using freelancer IDs.

---

## ⏱️ Time Complexity Analysis

### Core Operations
| Operation | Complexity |
|---------|------------|
| Register user | **O(1)** average |
| User lookup | **O(1)** average |
| Blacklist / unblacklist | **O(1)** |

---

### Freelancer Ranking
| Operation | Complexity |
|----------|------------|
| Heap insertion | **O(log N)** |
| Best freelancer selection | **O(log N)** |
| Re-ranking after updates | **O(log N)** |

---

### Monthly Simulation
| Operation | Complexity |
|----------|------------|
| Burnout & recovery checks | **O(F)** |
| Service change application | **O(F)** |
| Loyalty tier updates | **O(C)** |

Where:
- **F** = number of freelancers  
- **C** = number of customers  

---
## 🔧 Core System Behavior

The system operates entirely through command-driven input files.
Each command is processed sequentially and updates the internal
state of the platform deterministically.

User registrations, job requests, cancellations, and simulations
directly affect freelancer availability, rankings, and customer
status without deferred side effects.

---

## 📊 Composite Score Overview

Freelancer selection is driven by an integer-based composite score
that balances multiple factors:

- Skill compatibility with the requested service
- Average user rating
- Reliability based on completion and cancellation history
- Temporary penalties caused by burnout

The scoring model is designed to remain stable under large-scale
inputs while preserving deterministic ordering.

---

## 🔄 Dynamic State Management

Several system properties evolve over time:

- Freelancer skills change based on job outcomes
- Burnout status is updated during monthly simulations
- Customer loyalty tiers adjust according to cumulative spending
- Queued service changes are applied at month boundaries

All state transitions are explicitly controlled to prevent
inconsistent intermediate states.

---

## 📁 Repository Structure

```bash
.
├── src/
│   ├── Main.java
│   ├── AppManager.java
│   ├── User.java
│   ├── Customer.java
│   ├── Freelancer.java
│   ├── HashTable.java
│   ├── MaximumHeap.java
│   └── Entry.java
├── testcases/
│   ├── Type1/
│   │   ├── inputs/
│   │   └── outputs/
│   ├── Type2/
│   │   ├── inputs/
│   │   └── outputs/
│   └── Type3/
│       ├── inputs/
│       └── outputs/
├── test_runner.py
├── README.md
└── .gitignore
```

---

## ▶️ Usage

### Compilation

```bash
javac *.java
```
### Execution

```bash
java Main <input_file> <output_file>
```
### Automated Testing

Basic automated testing is supported via a lightweight test runner.

```bash
python3 test_runner.py
```
The runner executes predefined input files and compares the produced
outputs against expected results to ensure correctness and format compliance.

---

## ⚠️ Large Test Output Files Notice

Some **large test output files were intentionally not uploaded to GitHub** due to
GitHub’s recommended maximum file size limit (50 MB).

The following files exceed this limit and therefore **do not exist in the remote repository**:

- `testcases/Type2/outputs/t2_large_2c_output.txt` (~51 MB)
- `testcases/Type3/outputs/t3_large_3a_output.txt` (~51 MB)
- `testcases/Type3/outputs/t3_large_3b_output.txt` (~60 MB)
- `testcases/Type3/outputs/t3_large_3c_output.txt` (~55 MB)

These files are **generated locally during testing** and are excluded via `.gitignore`
to keep the repository lightweight and GitHub-compliant.

---

## 📌 Originality & Usage Notice

This repository contains an **original implementation** written from scratch.
No external solutions or third-party algorithm implementations were copied.

The project is shared **for educational and demonstrational purposes only**.

---

## 🧾 Final Remarks

GigMatch Pro demonstrates how **carefully chosen data structures** and
**asymptotically efficient algorithms** enable scalable ranking and matching
systems under dynamic, real-world constraints.
