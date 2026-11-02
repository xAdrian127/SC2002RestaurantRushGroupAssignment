# Stage 2: Customer Behaviour Extension

## 1. Overview

Stage 2 extends the restaurant management game by introducing polymorphic customer types alongside regular customers, fulfilling the requirements of **Section 4.2** of the assignment brief:

- **RegularCustomer**: Loses 8 satisfaction points per unserved turn, pays full price. Food preference: Alice chooses Burger ($15.00), David chooses Salad ($9.00).
- **VIPCustomer**: Loses 5 satisfaction points per unserved turn, receives a 10% discount on their total bill (stored in integer cents). Food preference: Burger ($13.50 discounted).
- **CriticCustomer**: Loses 12 satisfaction points per eligible unserved turn, becoming unforgiving below 50 satisfaction (losing 17). Distinct order preference: Always orders Pasta (2 preparation units, $18.00) to rigorously test the kitchen's culinary craftsmanship. Pays full price.

Arrival schedule cycles across turns 1, 4, 7, 10, 13, and 16:

- Turn 1: Alice (`RegularCustomer`)
- Turn 4: Bob (`VIPCustomer`)
- Turn 7: Charlie (`CriticCustomer`)
- Turn 10: David (`RegularCustomer`)
- Turn 13: Edward (`VIPCustomer`)
- Turn 16: Faith (`CriticCustomer`)

---

## 2. Key Architectural Improvements over Stage 1 (Design Reflection)

### A. Polymorphic Billing (Open-Closed Principle - OCP)

- **Stage 1 Limitation**: In early implementations, specialized billing was either omitted or checked procedurally in the turn controller using `instanceof` checks (`if (c instanceof VIPCustomer)`).
- **Stage 2 Refactoring**: Dispatched bill calculations polymorphically through `Customer.calculateBill()`. `VIPCustomer` overrides this method to apply its 10% discount (`super.calculateBill() * 0.90`). `RestaurantController` requires **zero type checks**, maintaining full adherence to OCP.

### B. Polymorphic Satisfaction Decay (Liskov Substitution Principle - LSP)

- **Stage 1 Limitation**: The turn controller hardcoded and passed a decay argument of `8` (`decreaseSatisfaction(8)`), which subclasses had to ignore or override inconsistently.
- **Stage 2 Refactoring**: Each customer subclass encapsulates its own decay rate via `getSatisfactionLossPerTurn()`. The controller simply invokes `c.decaySatisfaction()`. Subclasses safely substitute for the base class without violating behavioral contracts.

### C. Encapsulation of Domain Rules (Boundary vs. Entity Separation)

- **Stage 1 Limitation**: Food selection was randomly rolled in `Main.java` (the UI Boundary layer) and passed into `controller.takeOrder(tableIdx, choice)`, violating the rule: _"Keep rules out of the UI. The UI only collects the manager's decisions and displays the state."_
- **Stage 2 Refactoring**: Food selection is encapsulated inside `Customer.chooseMenuChoice()`. When the Waiter takes an order at a table, the seated customer decides what to order based on their preferences. `Main.java` only issues the command `controller.takeOrder(tableIdx)`.

### D. Start-of-Turn Cashier Payment & Multi-Table Oldest-First Tracking

- **Stage 1 Limitation**: Payment was processed at turn end, meaning a customer who finished eating had to hold the table for an entire additional turn after becoming `READY_TO_PAY`. Additionally, a single integer flag (`tableBecameReadyThisTurn`) was vulnerable to being overwritten if multiple tables finished eating on the same turn.
- **Stage 2 Refactoring**: Cashier payment is executed at the **start of the turn** for any customer who reached `READY_TO_PAY` on an earlier turn (`turnBecameReadyToPay < currentTurn`). This immediately frees the table so the manager can seat waiting customers during that same turn. Multi-table prioritization uses `turnBecameReadyToPay` to strictly honor the **oldest-first** cashier rule.

### E. Queue Data Structure Integration (WaitingQueue & PaymentQueue)

- **`WaitingQueue`**: Encapsulates FIFO queue manipulation for arriving customers waiting to be seated.
- **`PaymentQueue`**: Encapsulates FIFO payment processing for customers ready to pay, naturally enforcing the assignment rule that the Cashier processes payments in **oldest-first** order.

### F. Single Responsibility Principle (SRP) Controller Decomposition

To eliminate the "Fat / God Controller" anti-pattern from Stage 1, the controller layer was decomposed into 4 highly cohesive, focused classes:
- **`RestaurantController` (Facade Orchestrator)**: Coordinates high-level turn loop progression and delegates tasks to specialized sub-controllers without managing low-level staff mechanics or scoring formulas.
- **`StaffController`**: Manages operational staff coordination (`Waiter`, `Chef`, `Cashier`), enforces per-turn action quotas, and executes seating, ordering, cooking, and billing tasks. (Extensible for the Stage 3 `Host` role).
- **`CustomerController`**: Manages customer domain lifecycles: scheduled arrivals (Turns 1, 4, 7, 10, 13, 16), eating state transitions (`SERVED` $\rightarrow$ `READY_TO_PAY`), and polymorphic turn-end satisfaction decay/abandonment.
- **`GameScoreManager`**: Manages financial accounting in integer cents (Section 2.2), customer counts, satisfaction averages, and victory/defeat target evaluation.

### G. Package Architecture

- **Root Entrypoint**: User interface and CLI presentation driver (`Main.java`). Handles input/output and rendering; decoupled from domain game rules.
- **`control/`**: Business logic, turn coordination, staff dispatching, customer lifecycles, and scoring (`RestaurantController`, `StaffController`, `CustomerController`, `GameScoreManager`).
- **`entity/`**: Core restaurant domain models (`Table`, `Order`, `MenuItem`, `Menu`).
  - **`entity/customer/`**: Specialized customer domain package encapsulating customer hierarchy and behaviors (`Customer`, `CustomerState`, `RegularCustomer`, `VIPCustomer`, `CriticCustomer`).
  - **`entity/staff/`**: Staff roles performing turn actions (`Waiter`, `Chef`, `Cashier`).
  - **`entity/queue/`**: FIFO queue data structures for restaurant operations (`WaitingQueue`, `PaymentQueue`).

---

## 3. User Inputs and Expected Results

### 1. Waiter Actions

| 1st Input | 2nd Input | Action           | Prompt                                                     | Expected Result                                                        |
| --------- | --------- | ---------------- | ---------------------------------------------------------- | ---------------------------------------------------------------------- |
| 1         | 1         | Seat customer    | Which table to seat at? (1 or 2, 0 to cancel):             | Seats the first waiting customer at Table 1.                           |
| 1         | 2         | Seat customer    | Which table to seat at? (1 or 2, 0 to cancel):             | Seats the first waiting customer at Table 2.                           |
| 2         | 1         | Take Order       | Which Table to take order for? (1 or 2, 0 to cancel):      | Takes order at Table 1. Seated customer selects dish polymorphically.  |
| 2         | 2         | Take Order       | Which Table to take order for? (1 or 2, 0 to cancel):      | Takes order at Table 2. Seated customer selects dish polymorphically.  |
| 3         | 1         | Serve Food       | Which Table to serve READY food to? (1 or 2, 0 to cancel): | Serves READY order at Table 1. Customer satisfaction stops decreasing. |
| 3         | 2         | Serve Food       | Which Table to serve READY food to? (1 or 2, 0 to cancel): | Serves READY order at Table 2. Customer satisfaction stops decreasing. |
| 4         | -         | Wait             | -                                                          | Waiter performs no task for the turn.                                  |
| 1, 2 or 3 | 0         | Cancel Selection | Waiter choice prompt                                       | Cancels selection and returns to Waiter menu without consuming action. |

### 2. Chef Actions

- **Option 1/2**: Prepare one unit for a pending table order.
- **Wait**: Chef remains idle for the turn.
- Invalid table choices or commands are denied without consuming the Chef's turn.

---

## 4. Build & Execution

To compile and run Stage 2 from the `stage2` directory:

```bash
# Compile all source files and packages
javac Main.java control/*.java entity/*.java entity/customer/*.java entity/staff/*.java entity/queue/*.java

# Run the game
java Main
```

Or execute the automated batch file:

```powershell
.\run.bat
```

---

## 5. Reproducible Test Runs (Section 4.1 & 7 Grading Evidence)

### A. Reproducible Victory Run (Passed All 3 Criteria)
**Objective targets met at Turn 18**:
- Paid Customers: **4** (Target $\ge 4$) $\rightarrow$ **PASSED**
- Total Revenue: **$55.50** (Target $\ge \$45.00$) $\rightarrow$ **PASSED** (Alice $15.00 + Bob $13.50 + Charlie $18.00 + David $9.00)
- Average Paid Satisfaction: **78.5** (Target $\ge 60.0$) $\rightarrow$ **PASSED**
- Result: **VICTORY!**

**Turn-by-turn Manager Input Sequence**:
| Turn | Customer Event | Waiter Action | Chef Action | End-of-Turn Notes |
|:---:|:---|:---|:---|:---|
| 1 | Alice (`Regular`) arrives | `1 1` (Seat Table 1) | `1` (Wait) | Alice seated at Table 1 (Sat: 92) |
| 2 | - | `2 1` (Take order T1: Burger) | `1` (Cook T1) | Burger is READY (Sat: 84) |
| 3 | - | `3 1` (Serve Table 1) | `1` (Wait) | Alice SERVED; eating begins (Sat: 84) |
| 4 | Bob (`VIP`) arrives; Alice finishes eating | `1 2` (Seat Table 2) | `1` (Wait) | Bob seated at Table 2; Alice READY_TO_PAY |
| 5 | Cashier pays Alice ($15.00); T1 freed | `2 2` (Take order T2: Burger) | `1` (Cook T2) | Bob's Burger READY (Sat: 90) |
| 6 | - | `3 2` (Serve Table 2) | `1` (Wait) | Bob SERVED; eating begins (Sat: 90) |
| 7 | Charlie (`Critic`) arrives; Bob finishes eating | `1 1` (Seat Table 1) | `1` (Wait) | Charlie seated at T1; Bob READY_TO_PAY |
| 8 | Cashier pays Bob ($13.50 VIP); T2 freed | `2 1` (Take order T1: Pasta) | `1` (Cook T1) | Charlie Pasta prep unit 1 done |
| 9 | - | `4` (Wait) | `1` (Cook T1) | Charlie Pasta prep unit 2 done; READY |
| 10 | David (`Regular`) arrives | `3 1` (Serve Table 1) | `1` (Wait) | Charlie SERVED; eating begins (Sat: 64) |
| 11 | Charlie finishes eating | `1 2` (Seat Table 2) | `1` (Wait) | David seated at T2; Charlie READY_TO_PAY |
| 12 | Cashier pays Charlie ($18.00); T1 freed | `2 2` (Take order T2: Salad) | `1` (Cook T2) | David's Salad is READY |
| 13 | Edward (`VIP`) arrives | `3 2` (Serve Table 2) | `1` (Wait) | David SERVED; eating begins (Sat: 76) |
| 14 | David finishes eating | `4` (Wait) | `1` (Wait) | David READY_TO_PAY |
| 15 | Cashier pays David ($9.00); T2 freed | `4` (Wait) | `1` (Wait) | Paid: 4, Revenue: $55.50, Avg Sat: 78.5 |
| 16 | Faith (`Critic`) arrives | `4` (Wait) | `1` (Wait) | Idle turns to fulfill 18-turn game loop |
| 17 | - | `4` (Wait) | `1` (Wait) | Idle turns to fulfill 18-turn game loop |
| 18 | Final turn evaluation | `4` (Wait) | `1` (Wait) | **VICTORY ACHIEVED!** |

**One-line input stream for TA replication**:
```text
1 1 1 2 1 1 3 1 1 1 2 1 2 2 1 3 2 1 1 1 1 2 1 1 4 1 3 1 1 1 2 1 2 2 1 3 2 1 4 1 4 1 4 1 4 1 4 1
```

---

### B. Reproducible Defeat Run (Customer Abandonment)
**Objective targets evaluated at Turn 18**:
- Paid Customers: **0** (Target $\ge 4$) $\rightarrow$ **FAILED**
- Total Revenue: **$0.00** (Target $\ge \$45.00$) $\rightarrow$ **FAILED**
- Average Paid Satisfaction: **N/A** $\rightarrow$ **FAILED**
- Result: **DEFEAT!**

**Execution**: The manager enters `4` (Waiter wait) and `1` (Chef wait) across all 18 turns. Waiting customers undergo satisfaction decay until reaching 0 and abandon the queue without paying.
```text
4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1 4 1
```
