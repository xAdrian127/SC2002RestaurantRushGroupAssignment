# SC2002 Restaurant Rush — Group Assignment

**Course**: SC2002 Object-Oriented Design & Programming  
**Repository URL**: https://github.com/xAdrian127/SC2002RestaurantRushGroupAssignment  
**Lab & Assignment Group**: `MACS2` — Assignment Group `4` (`MACS2_Grp4`)

A command-line, turn-based restaurant management game in Java developed across **three runnable, Git-tagged stages** (`stage-1`, `stage-2`, `stage-3`), demonstrating iterative Object-Oriented Design, **Boundary-Control-Entity (BCE)** architecture, **SOLID** principles, and design patterns.

---

## Table of Contents
1. [Team Members & Responsibilities](#1-team-members--responsibilities)
2. [Repository Structure & Stage Links](#2-repository-structure--stage-links)
3. [Build & Run Instructions](#3-build--run-instructions)
4. [Game Setup & Deterministic Ordering Rules](#4-game-setup--deterministic-ordering-rules)
5. [Architecture & Design Principles Overview](#5-architecture--design-principles-overview)
6. [GitHub Evidence: Stage-by-Stage Transition Comparisons](#6-github-evidence-stage-by-stage-transition-comparisons)
   - [Transition 1: Stage 1 → Stage 2](#61-transition-1-stage-1--stage-2)
   - [Transition 2: Stage 2 → Stage 3](#62-transition-2-stage-2--stage-3)
   - [Intra-Stage 3 Structural Evolution](#63-intra-stage-3-structural-evolution)
7. [Reproducible Victory & Defeat Runs](#7-reproducible-victory--defeat-runs)
8. [Recorded Video & Segment Timestamps](#8-recorded-video--segment-timestamps)

---

## 1. Team Members & Responsibilities

| Member | Full Name | Role | Primary Contributions |
| :---: | :--- | :--- | :--- |
| **Member 1** | **Ephraim Yee Cheng En** | Tech Lead | `RestaurantController` turn engine, 18-turn game loop, BCE refactoring, Git repository admin & release tagging (`stage-1`, `stage-2`, `stage-3`), `run.bat` & documentation |
| **Member 2** | **John Biju Varghese** | Customer Domain | `Customer` abstract base class, `CustomerState` enum, `RegularCustomer`, `VIPCustomer` & `CriticCustomer` subclasses, polymorphic satisfaction decay & billing |
| **Member 3** | **Foo Loke Zen** | Staff & Menu Lead | `Table`, `WaitingQueue`, `PaymentQueue`, `Order`, `MenuItem`, `ComboMeal`, `Staff` / `SeatingStaff` roles (`Waiter`, `Chef`, `Host`, `Cashier`), `HappyHour` policy |
| **Member 4** | **Tricia Lin Zhixuan** | UML Diagram Lead | Code review for UML alignment, UML Class Diagrams (Stages 1, 2, and 3) & UML Sequence Diagram |
| **Member 5** | **Li Yunyi** | Evidence & Report Lead | System testing, CLI simulation execution, edge-case verification, functional evidence logs, and final PDF report collation |

---

## 2. Repository Structure & Stage Links

All three stages are self-contained and independently runnable from their respective directories or Git tags:

```text
SC2002RestaurantRushGroupAssignment/
├── README.md                  # Main project documentation & stage transition evidence
├── stage1/                    # Stage 1: Basic restaurant operation (Tag: stage-1)
│   ├── README.md              # Stage 1 detailed guide & v1 -> v2 reflection
│   ├── run.bat                # One-click compile & run script for Stage 1
│   └── *.java                 # Stage 1 Java source files
├── stage2/                    # Stage 2: Customer behaviour extension (Tag: stage-2)
│   ├── README.md              # Stage 2 detailed guide & reproducible test runs
│   ├── run.bat                # One-click compile & run script for Stage 2
│   ├── Main.java              # Stage 2 CLI entrypoint
│   ├── control/               # SRP Controllers (Restaurant, Staff, Customer, Score)
│   └── entity/                # Domain entities (customer/, staff/, queue/)
└── stage3/                    # Stage 3: Host, ComboMeal & Happy Hour (Tag: stage-3)
    ├── README.md              # Stage 3 detailed guide, BCE refactoring & test runs
    ├── run.bat                # One-click compile & run script for Stage 3
    ├── Main.java              # Stage 3 wiring entrypoint
    ├── boundary/              # SRP CLI views & input readers (RestaurantUI, etc.)
    ├── control/               # Pure business logic controllers & ActionResult
    └── entity/                # Rich domain models (ComboMeal, HappyHour, customer/, staff/, queue/)
```

### Stage Documentation & Git Tags

| Stage | Scope Summary | Stage README | Git Tag | Tag Commit |
| :---: | :--- | :---: | :---: | :---: |
| **Stage 1** | Base 18-turn game loop, `Regular` customers, 3 base menu items, `Waiter`, `Chef`, automated `Cashier`, satisfaction decay & abandonment | [stage1/README.md](./stage1/README.md) | [`stage-1`](https://github.com/xAdrian127/SC2002RestaurantRushGroupAssignment/tree/stage-1) | [`500cfbd`](https://github.com/xAdrian127/SC2002RestaurantRushGroupAssignment/commit/500cfbd) |
| **Stage 2** | Polymorphic `VIPCustomer` & `CriticCustomer`, oldest-first `PaymentQueue`, start-of-turn `Cashier`, SRP controller decomposition | [stage2/README.md](./stage2/README.md) | [`stage-2`](https://github.com/xAdrian127/SC2002RestaurantRushGroupAssignment/tree/stage-2) | [`e3a2499`](https://github.com/xAdrian127/SC2002RestaurantRushGroupAssignment/commit/e3a2499) |
| **Stage 3** | `Host` role (`SeatingStaff`), composite `ComboMeal`, `HappyHour` policy, strict **Boundary-Control-Entity (BCE)** separation | [stage3/README.md](./stage3/README.md) | [`stage-3`](https://github.com/xAdrian127/SC2002RestaurantRushGroupAssignment/tree/stage-3) | [`2b846f2`](https://github.com/xAdrian127/SC2002RestaurantRushGroupAssignment/commit/2b846f2) |

---

## 3. Build & Run Instructions

**Prerequisites**: Java Development Kit (JDK 11 or higher), offline-ready (no external dependencies required).

### Option A: Compile & Run by Stage Directory

#### Stage 1
```powershell
cd stage1
javac *.java
java Main
# Or on Windows: .\run.bat
```

#### Stage 2
```powershell
cd stage2
javac Main.java control/*.java entity/*.java entity/customer/*.java entity/staff/*.java entity/queue/*.java
java Main
# Or on Windows: .\run.bat
```

#### Stage 3
```powershell
cd stage3
javac Main.java boundary/*.java control/*.java entity/*.java entity/customer/*.java entity/staff/*.java entity/queue/*.java
java Main
# Or on Windows: .\run.bat
```

### Option B: Checkout by Official Git Tag
```powershell
git checkout stage-1   # or stage-2 / stage-3
```

---

## 4. Game Setup & Deterministic Ordering Rules

### 4.1 Base Setting & Victory Criteria (Section 2.1)
- **Maximum Turns**: 18 turns (always runs all 18 turns; no early termination).
- **Tables**: 2 dining tables (occupied until the seated customer pays or abandons).
- **Staff Quotas**:
  - `Host` *(Stage 3)*: Seats at most **1** queued customer per turn.
  - `Waiter`: Performs at most **1** task per turn (Seat customer, Take order, or Serve `READY` food).
  - `Chef`: Advances at most **1** preparation unit per turn (may cook an order taken by the Waiter on the same turn).
  - `Cashier`: Automatically processes at most **1** `READY_TO_PAY` customer per turn (oldest first).
- **Victory Targets (Evaluated after Turn 18)**:
  1. **Paid Customers**: $\ge 4$
  2. **Total Revenue**: $\ge \$45.00$ (`4500` integer cents)
  3. **Average Paid Satisfaction**: $\ge 60.0$

### 4.2 Arrival Schedule & Customer Behaviours (Stages 2 & 3)
Customers arrive at the start of turns **1, 4, 7, 10, 13, and 16** with initial satisfaction `100`:

| Arrival # | Turn | Customer Name | Type | Unserved Decay / Turn | Billing Rule | Deterministic Dish Choice (Stage 2) | Deterministic Dish Choice (Stage 3) |
| :---: | :---: | :--- | :--- | :---: | :--- | :--- | :--- |
| **#1** | Turn 1 | **Alice** | `RegularCustomer` | `-8` | Full price | Burger (\$15.00, 1 unit) | Burger (\$15.00, 1 unit) |
| **#2** | Turn 4 | **Bob** | `VIPCustomer` | `-5` | **10% off** | Burger (\$13.50 VIP, 1 unit) | Burger (\$13.50 VIP / \$10.80 w/ Happy Hour, 1 unit) |
| **#3** | Turn 7 | **Charlie** | `CriticCustomer` | `-12` (`-17` if Sat $< 50$) | Full price | Pasta (\$18.00, 2 units) | Pasta (\$18.00, 2 units) |
| **#4** | Turn 10 | **David** | `RegularCustomer` | `-8` | Full price | Salad (\$9.00, 1 unit) | **Salad + Pasta Combo** (\$24.30, 3 units) |
| **#5** | Turn 13 | **Edward** | `VIPCustomer` | `-5` | **10% off** | Burger (\$13.50 VIP, 1 unit) | **Burger + Pasta Combo** (\$26.73 VIP, 3 units) |
| **#6** | Turn 16 | **Faith** | `CriticCustomer` | `-12` (`-17` if Sat $< 50$) | Full price | Pasta (\$18.00, 2 units) | Pasta (\$18.00, 2 units) |

---

## 5. Architecture & Design Principles Overview

By Stage 3, the codebase enforces a strict **Boundary-Control-Entity (BCE)** architecture:

- **`boundary/` (Presentation Layer)**: `RestaurantUI`, `InputReader`, `GameStatusView`, and `StaffActionMenu`. Handles all `Scanner` input validation and `System.out` rendering. Contains **zero** business calculations or pricing formulas.
- **`control/` (Application / Turn Engine Layer)**: `RestaurantController` (Facade), `StaffController`, `CustomerController`, `GameScoreManager`, and immutable `ActionResult`. Contains **zero** console I/O calls.
- **`entity/` (Rich Domain Layer)**: Encapsulates state invariants and domain rules across `customer/`, `staff/`, `queue/`, `MenuItem`/`ComboMeal`, `HappyHour`, `Order`, and `Table`.
- **Key OOP & SOLID Highlights**:
  - **Single Responsibility Principle (SRP)**: Decomposed both the Control layer (`RestaurantController`, `StaffController`, `CustomerController`, `GameScoreManager`) and Boundary layer (`RestaurantUI`, `InputReader`, `GameStatusView`, `StaffActionMenu`).
  - **Open-Closed & Liskov Substitution Principles (OCP & LSP)**:
    - `Customer` hierarchy dispatches `getSatisfactionLossPerTurn()`, `chooseMenuChoice()`, and `computeOrderBillCents()` polymorphically without `instanceof` checks in controllers.
    - `SeatingStaff` generalizes shared seating validation for `Host` and `Waiter`.
    - `ComboMeal extends MenuItem` (**Composite Pattern**) allows `Order`, `Waiter`, and `Chef` to process single items and combos identically while `canBeComboComponent()` prevents nested combos polymorphically.
  - **Information Expert & Encapsulation**: All monetary values are stored and calculated in **integer cents** (`lockedPriceCents`, `totalRevenueCents`); collections are exposed via `Collections.unmodifiableList(...)`.

---

## 6. GitHub Evidence: Stage-by-Stage Transition Comparisons

In fulfillment of **Section 5 (GitHub Evidence)**, the tables below document every new file, modified/moved file, deleted file, design rationale, and GitHub comparison link across transitions **1 → 2** and **2 → 3**.

### 6.1 Transition 1: Stage 1 → Stage 2
- **GitHub Compare Link**: [`stage-1...stage-2`](https://github.com/xAdrian127/SC2002RestaurantRushGroupAssignment/compare/stage-1...stage-2)
- **Key Commits**: [`214ad2f`](https://github.com/xAdrian127/SC2002RestaurantRushGroupAssignment/commit/214ad2f) (Package modularization), [`8b39a45`](https://github.com/xAdrian127/SC2002RestaurantRushGroupAssignment/commit/8b39a45) (Stage 2 customer extensions, `PaymentQueue` & SRP controllers), [`e3a2499`](https://github.com/xAdrian127/SC2002RestaurantRushGroupAssignment/commit/e3a2499) (`stage-2` tag)

#### A. New Files Added in Stage 2 (8 Files)

| File Path (`stage2/`) | Category | Reason for Addition |
| :--- | :---: | :--- |
| `entity/customer/RegularCustomer.java` | Entity | Concrete customer subclass encapsulating standard `-8`/turn decay, 100% bill calculation, and deterministic food preferences (Burger/Salad). |
| `entity/customer/VIPCustomer.java` | Entity | Implements Section 4.2 VIP rules: overrides satisfaction decay (`-5`/turn) and `calculateBill()` for a 10% discount in integer cents. |
| `entity/customer/CriticCustomer.java` | Entity | Implements Section 4.2 Critic rules: overrides decay (`-12`/turn, or `-17`/turn below 50 sat) and always orders `Pasta` (2 prep units). |
| `entity/staff/Cashier.java` | Entity | Dedicated staff role handling automated payment collection for `READY_TO_PAY` customers (at most 1 per turn). |
| `entity/queue/PaymentQueue.java` | Entity | FIFO queue tracking customers as they finish eating, strictly enforcing the **oldest-first** Cashier payment rule across multiple tables. |
| `control/StaffController.java` | Control | Extracted from `RestaurantController` (**SRP**) to manage `Waiter`, `Chef`, and `Cashier` actions and turn quotas. |
| `control/CustomerController.java` | Control | Extracted from `RestaurantController` (**SRP**) to manage scheduled arrivals (Regular, VIP, Critic), eating transitions, and turn-end decay. |
| `control/GameScoreManager.java` | Control | Extracted from `RestaurantController` (**SRP**) to manage integer-cent revenue accounting, customer counters, and victory/defeat evaluation. |

#### B. Existing Stage 1 Files Changed / Moved in Stage 2 (11 Files)

| Stage 1 File (`stage1/`) | Stage 2 File (`stage2/`) | Change Type | Reason for Change |
| :--- | :--- | :---: | :--- |
| `Customer.java` | `entity/customer/Customer.java` | Moved & Modified | Refactored into a polymorphic base class with overridable `decaySatisfaction()`, `calculateBill()`, and `chooseMenuChoice()`, moving dish selection out of `Main.java` into the domain entity. |
| `CustomerState.java` | `entity/customer/CustomerState.java` | Moved | Organized into the `entity.customer` package. |
| `Waiter.java` | `entity/staff/Waiter.java` | Moved & Modified | Moved to `entity.staff`; updated `takeOrder(Table)` to ask the seated `Customer` for their polymorphic dish choice instead of accepting a UI-rolled random integer. |
| `Chef.java` | `entity/staff/Chef.java` | Moved | Organized into the `entity.staff` package. |
| `WaitingQueue.java` | `entity/queue/WaitingQueue.java` | Moved | Organized into `entity.queue` alongside `PaymentQueue`. |
| `Menu.java`, `MenuItem.java`, `Order.java`, `Table.java` | `entity/*.java` | Moved & Modified | Organized into `entity/`; added integer-cent price representation (`getPriceInCents()`) and `turnBecameReadyToPay` tracking on `Table` for oldest-first Cashier tie-breaking. |
| `RestaurantController.java` | `control/RestaurantController.java` | Moved & Modified | Refactored from a monolithic God controller into a **Facade Orchestrator** delegating to `StaffController`, `CustomerController`, and `GameScoreManager`; moved Cashier payment to the start of the turn so freed tables can be reseated immediately. |
| `Main.java` | `Main.java` | Modified | Removed `java.util.Random` food selection from the UI layer to comply with Section 2.2 (*"Keep rules out of the UI"*). |

#### C. Files Deleted in Stage 1 → Stage 2
- **None** during the Stage 1 $\rightarrow$ Stage 2 transition (obsolete v1 prototype files `CustomerRules.java`, `CustomerRulesTest.java`, `CustomerStatus.java`, and `PaymentLedger.java` were removed earlier when finalizing Stage 1 in commits `1eb5cf1`–`6d154d3`).

---

### 6.2 Transition 2: Stage 2 → Stage 3
- **GitHub Compare Link**: [`stage-2...stage-3`](https://github.com/xAdrian127/SC2002RestaurantRushGroupAssignment/compare/stage-2...stage-3)
- **Key Commits**: [`3b91312`](https://github.com/xAdrian127/SC2002RestaurantRushGroupAssignment/commit/3b91312) (Stage 3 foundation), [`97fa500`](https://github.com/xAdrian127/SC2002RestaurantRushGroupAssignment/commit/97fa500) (Full Stage 3 mechanics), [`005ea3e`](https://github.com/xAdrian127/SC2002RestaurantRushGroupAssignment/commit/005ea3e)–[`0374d0a`](https://github.com/xAdrian127/SC2002RestaurantRushGroupAssignment/commit/0374d0a) (Strict BCE & SOLID refactoring), [`2b846f2`](https://github.com/xAdrian127/SC2002RestaurantRushGroupAssignment/commit/2b846f2) (`stage-3` tag)

#### A. New Files Added in Stage 3 (10 Files)

| File Path (`stage3/`) | Category | Reason for Addition |
| :--- | :---: | :--- |
| `entity/staff/Staff.java` | Entity | Abstract base class encapsulating per-turn action state (`private boolean actedThisTurn`, `resetTurn()`, `waitTurn()`, `markActed()`) so `StaffController` manages all staff polymorphically via `List<Staff>`. |
| `entity/staff/SeatingStaff.java` | Entity | Abstract subclass of `Staff` centralizing table-seating validation and state transitions shared by `Host` and `Waiter` (**DRY & LSP**). |
| `entity/staff/Host.java` | Entity | Implements Section 4.3 `Host` role extending `SeatingStaff`; seats at most one queued customer per turn in addition to the `Waiter`. |
| `entity/ComboMeal.java` | Entity | Implements Section 4.3 `ComboMeal` via the **Composite Pattern** (`extends MenuItem`, holds two component `MenuItem`s). Sums preparation units, computes price at 90% rounded to nearest cent, and overrides `canBeComboComponent()` to `false`. |
| `entity/HappyHour.java` | Entity | Implements Section 4.3 `HappyHour` policy: tracks once-per-game activation, 2-turn duration countdown, 20% discount rate, and +15 turn-start satisfaction recovery. |
| `control/ActionResult.java` | Control | Immutable result object (`ok()`, `fail()`, `message`) enabling controllers and entities to report outcomes to the UI without calling `System.out.println`. |
| `boundary/InputReader.java` | Boundary | Encapsulates `Scanner` lifecycle, EOF handling, and safe integer parsing (**SRP**). |
| `boundary/GameStatusView.java` | Boundary | Renders read-only CLI banners, turn headers (with Happy Hour remaining turns), event logs, table/queue state, and final victory/defeat scoreboards (**SRP**). |
| `boundary/StaffActionMenu.java` | Boundary | Handles interactive manager menus for Happy Hour, Host, Waiter, and dynamic $N$-table Chef cooking prompts (**SRP & DRY**). |
| `boundary/RestaurantUI.java` | Boundary | Lightweight Boundary Facade coordinating `InputReader`, `GameStatusView`, and `StaffActionMenu`. |

#### B. Existing Stage 2 Files Changed in Stage 3 (17 Files)

| Stage 2 File (`stage2/`) | Stage 3 File (`stage3/`) | Reason for Change |
| :--- | :--- | :--- |
| `entity/customer/Customer.java` | `entity/customer/Customer.java` | Made `abstract`; added `computeOrderBillCents(MenuItem, boolean)` to lock order bills at placement time (VIP 10% first, then Happy Hour 20%) and `recoverSatisfaction(int)` (+15 for `QUEUED`/`SEATED`/`ORDERED`, capped at 100); declared abstract `getCustomerType()`, `getSatisfactionLossPerTurn()`, and `chooseMenuChoice()`. |
| `entity/customer/RegularCustomer.java`, `VIPCustomer.java`, `CriticCustomer.java` | `entity/customer/*.java` | Replaced hardcoded customer-name checks with arrival-order deterministic food rules so #4 David (`Regular`) orders Combo #4 (Salad + Pasta) and #5 Edward (`VIP`) orders Combo #5 (Burger + Pasta); implemented `getCustomerType()`. |
| `entity/MenuItem.java`, `entity/Menu.java` | `entity/MenuItem.java`, `entity/Menu.java` | Added polymorphic `canBeComboComponent()` to `MenuItem`; registered `Salad + Pasta Combo` (\$24.30, 3 units) and `Burger + Pasta Combo` (\$29.70, 3 units) in `Menu`; changed `Menu.getItems()` to return an unmodifiable `List<MenuItem>`. |
| `entity/Order.java`, `entity/Table.java` | `entity/Order.java`, `entity/Table.java` | Added immutable `lockedPriceCents` to `Order` (locking price at order creation); added Law-of-Demeter helpers `Table.hasCookableOrder()` and `Table.getActiveOrder()`; removed raw state-corrupting setters. |
| `entity/staff/Waiter.java`, `Chef.java`, `Cashier.java` | `entity/staff/*.java` | `Waiter` now extends `SeatingStaff` and passes `isHappyHourActive` when locking a new `Order`'s price; `Chef` extends `Staff`; removed all direct `System.out.println` calls from staff entities. |
| `control/RestaurantController.java`, `StaffController.java`, `CustomerController.java`, `GameScoreManager.java` | `control/*.java` | Integrated `Host` seating, once-per-game `HappyHour` activation/countdown/+15 recovery, `ActionResult` responses, structured turn event lists, dynamic `getCookableTables()`, and integer-cent victory evaluation (`totalRevenueCents >= 4500`). |
| `Main.java` | `Main.java` | Extracted all CLI presentation and input loops into `boundary/`; `Main.java` is now a clean 14-line wiring entrypoint. |

#### C. Files Deleted in Stage 2 → Stage 3
- **None**.

### 6.3 Intra-Stage 3 Structural Evolution
Within Stage 3, we deliberately separated **feature integration** (`97fa500`: adding `Host`, `ComboMeal`, and `HappyHour` on top of Stage 2's layout to verify game balance first) from **architectural refactoring** (`005ea3e` $\rightarrow$ `3d644ac` $\rightarrow$ `269056a` $\rightarrow$ `0374d0a`: extracting `boundary/`, introducing `SeatingStaff` and `ActionResult`, and enforcing strict BCE and SOLID compliance while keeping CLI behavior 100% identical). Full details are documented in [stage3/README.md Section 3](./stage3/README.md#3-structural-evolution-within-stage-3-why-the-architecture-changed-after-initial-integration).

---

## 7. Reproducible Victory & Defeat Runs

Paste these single-line input streams directly into the terminal after launching `java Main` (or `.\run.bat`) to reproduce Victory and Defeat outcomes.

### Stage 3 Reproducible Runs (Base Setting + Host + ComboMeal + Happy Hour)

- **Stage 3 Victory Run**  
  *Outcome*: **VICTORY!** — Paid Customers: **4** ($\ge 4$), Revenue: **\$68.10** ($\ge \$45.00$), Avg Paid Satisfaction: **86.0** ($\ge 60.0$), Unhappy Departures: **0**  
  *(Features demonstrated: Host seating, Happy Hour activation on Turn 4 locking VIP Bob's Burger at \$10.80 and recovering +15 satisfaction, Critic Charlie's Pasta, and Regular David's \$24.30 Salad + Pasta ComboMeal)*
  ```text
  2 1 1 2 1 1 2 2 3 1 1 2 2 4 1 1 1 1 2 1 1 2 3 1 1 2 4 1 1 1 2 1 1 2 4 1 2 3 1 1 1 2 2 2 1 2 4 1 2 4 1 1 1 3 2 1 2 2 1 1 2 4 1 2 4 1 2 3 1 1 2 4 1
  ```

- **Stage 3 Defeat Run**  
  *Outcome*: **DEFEAT!** — Paid Customers: **0**, Revenue: **\$0.00**, Avg Paid Satisfaction: **N/A**, Unhappy Departures: **2** (Alice and Charlie reach 0 satisfaction and abandon)
  ```text
  2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1
  ```

### Stage 2 Reproducible Runs (Base Setting + VIP & Critic Customers)

- **Stage 2 Victory Run**  
  *Outcome*: **VICTORY!** — Paid Customers: **4** ($\ge 4$), Revenue: **\$55.50** ($\ge \$45.00$), Avg Paid Satisfaction: **78.5** ($\ge 60.0$), Unhappy Departures: **0**
  ```text
  1 1 1 2 1 1 3 1 1 1 2 1 2 2 1 3 2 1 1 1 1 2 1 1 4 1 3 1 1 1 2 1 2 2 1 3 2 1 4 1 4 1 4 1 4 1 4 1
  ```

- **Stage 2 Defeat Run**  
  *Outcome*: **DEFEAT!** — Paid Customers: **0**, Revenue: **\$0.00**, Avg Paid Satisfaction: **N/A**, Unhappy Departures: **2**
  ```text
  4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1
  ```

### Stage 1 Reproducible Runs (Base Setting Only)

- **Stage 1 Defeat Run**  
  *Outcome*: **DEFEAT!** — Manager chooses Wait (`4`) for the Waiter every turn; when no orders are pending, the Chef automatically waits. Customers decay at `-8`/turn and abandon at `0` satisfaction.
  ```text
  4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4
  ```
- **Stage 1 Victory Strategy**  
  *(Note: In Stage 1, dish selection uses `java.util.Random` across Salad [\$9, 1 unit], Burger [\$15, 1 unit], and Pasta [\$18, 2 units]; deterministic customer food choices were introduced in Stage 2.)*  
  To achieve Victory on any Stage 1 run:
  1. Seat arriving customers immediately (`1 1` or `1 2`), take their order on the next turn (`2 1` or `2 2`), and assign the Chef (`1`) to cook their dish until `READY` (1 turn for Salad/Burger, 2 turns for Pasta).
  2. Serve `READY` dishes immediately (`3 1` or `3 2`) so the customer eats for 1 turn, becomes `READY_TO_PAY`, and is automatically billed by the Cashier. Serving the first 4–5 customers yields $\ge 4$ paid customers, $\ge \$45.00$ revenue, and $\ge 76.0$ average satisfaction.

---

## 8. Recorded Video & Segment Timestamps

**Submitted Video File**: `sc2002_15min video.mov` *(Included in the submission archive; Total Duration: `15:02`)*

Per **Section 6.2** of the assignment brief, the recorded group presentation covers all required segments at the following timestamps:

| # | Required Segment (Section 6.2) | Timestamp | What is Demonstrated in the Video |
| :---: | :--- | :---: | :--- |
| **1** | The game launching from the submitted project | `00:20 – 00:39`<br>`02:05 – 02:20` | Launching from the submitted GitHub project via `run.bat` (`00:20`) and compiling/running `javac` & `java Main` in the terminal (`02:05`). |
| **2** | Meaningful manager choices, including a decision where staff are constrained | `02:20 – 03:10`<br>`04:25 – 04:54` | Turn 1 staff task allocation (`Host` seating, `Waiter` order taking, `Chef` cooking) and Turns 10–13 multi-table prioritization where `Host` seats `VIPCustomer` Edward at Table 1 while `Waiter` serves `RegularCustomer` David at Table 2 under 1-action-per-staff constraints. |
| **3** | A dish going `READY` → `SERVED` → payment | `02:50 – 03:35`<br>`06:12 – 06:38` | Live CLI trace of Alice's Burger becoming `READY` (Turn 1, `02:50`), `Waiter` serving it to `SERVED` (Turn 2, `03:20`), eating to `READY_TO_PAY` (Turn 3, `03:25`), and `Cashier` collecting `$15.00` payment (Turn 4, `03:30`), plus Slide 2 lifecycle proof (`06:12`). |
| **4** | A VIP or Critic behaviour | `03:35 – 04:30`<br>`06:38 – 07:18` | Live CLI of `VIPCustomer` Bob (10% discount, `-5` decay) and `CriticCustomer` Charlie arriving on Turn 7 (`04:10`) and deterministically ordering `Pasta` (`$18.00`, 2 prep units), plus Slide 3 polymorphic decay & billing proof (`06:38`). |
| **5** | A Stage 3 feature (`Host`, `ComboMeal`, `HappyHour`) | `03:35 – 04:05`<br>`04:25 – 04:54`<br>`07:18 – 08:08` | Activating `HappyHour` on Turn 4 (`03:35`, locking VIP Bob's Burger at `$10.80` and recovering `+15` satisfaction on Turn 5), `Host` seating & David ordering `Salad + Pasta Combo` (`$24.30`, 3 prep units at `04:25`), plus Slides 4–5 (`07:18`). |
| **6** | One victory and one defeat (reproducible runs) | `04:55 – 05:20` *(Victory)*<br>`05:20 – 05:58` *(Defeat)* | Turn 18 **Victory** result (`4` paid customers, `$68.10` revenue, `86.0` avg satisfaction at `04:55`) followed by the reproducible **Defeat** run (`0` paid, `$0.00` revenue, customer abandonment at `05:20`). |
| **7** | The stage tag comparisons and the diagrams | `05:59 – 06:12`<br>`08:08 – 13:05` | Stage tag comparisons (`stage-1 @ 500cfbd`, `stage-2 @ e3a2499`, `stage-3 @ 2b846f2` at `05:59` & `08:08`), Stage 1 $\rightarrow$ 2 $\rightarrow$ 3 UML Class Diagrams & OOP Pillars (`08:08 – 09:59`), and UML Sequence Diagram walkthrough (`10:00 – 13:05`), followed by Individual Member & Team Reflections (`13:05 – 15:02`). |

### Full Video Timeline Breakdown
- **`00:00 – 02:04`**: Project Introduction, GitHub Repository & 3-Stage Evolution Overview
- **`02:05 – 05:58`**: Live CLI Gameplay Walkthrough (`READY` $\rightarrow$ `SERVED` $\rightarrow$ Payment, `Host`, `HappyHour`, `VIPCustomer`, `CriticCustomer`, `ComboMeal`, Reproducible Victory & Defeat Runs)
- **`05:59 – 09:59`**: Functional Evidence Verification & Stage 1 $\rightarrow$ Stage 2 $\rightarrow$ Stage 3 UML Class Diagrams
- **`10:00 – 13:05`**: UML Sequence Diagram Walkthrough
- **`13:05 – 15:02`**: Individual Member Reflections (John, Ephraim, Loke Zen, Tricia, Yunyi) & Team Learnings
