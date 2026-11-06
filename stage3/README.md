# Stage 3: New Role, Menu Policy & Dynamic Pricing

## 1. Overview

Stage 3 completes the final functional tier of the Restaurant Rush turn-based management game by introducing the **Host** staff role, **ComboMeal** composition, and the **Happy Hour** dynamic pricing policy, fulfilling the requirements of **Section 4.3** of the assignment brief while upholding strict OOP and BCE design principles:

- **Host (`Host.java` & `SeatingStaff.java`)**: Seats at most one queued customer per turn at an available table in addition to the Waiter's action. Both `Host` and `Waiter` inherit shared seating behavior from `abstract class SeatingStaff` (eliminating duplicate seating logic while upholding LSP).
- **ComboMeal (`ComboMeal.java`)**: A composite menu item composed of two individual base menu items (Composite Pattern / Composition). Polymorphically rejects nested combo meals via `MenuItem.canBeComboComponent()`. Preparation units equal the sum of both components, and price is 90% of the combined component price rounded to the nearest integer cent.
  - Choice 4: **Salad + Pasta Combo** — $24.30 (900 + 1800 = 2700 * 0.90), 3 preparation units (Section 4.3 example).
  - Choice 5: **Burger + Pasta Combo** — $29.70 (1500 + 1800 = 3300 * 0.90), 3 preparation units.
- **Documented Deterministic Customer Food Preferences (`abstract class Customer`)**:
  - `RegularCustomer`: Odd arrival sequence (#1 Alice) orders Burger ($15.00, 1 prep); even arrival sequence (#4 David) orders **ComboMeal** (Salad + Pasta Combo, $24.30, 3 prep).
  - `VIPCustomer`: Early-game VIP (#2 Bob, arrival <= 3) orders Burger ($13.50 VIP, or $10.80 during Happy Hour); late-game VIP (#5 Edward, arrival > 3) orders **ComboMeal** (Burger + Pasta Combo, $26.73 VIP, 3 prep).
  - `CriticCustomer`: Always orders Pasta ($18.00, 2 prep).
- **Happy Hour Policy (`HappyHour.java`)**: A dynamic pricing policy the manager can switch on once per game at the start of a turn.
  - Duration: Active for the activation turn and the next turn (2 turns total). Remaining duration is displayed in the turn header.
  - **Price Locking**: Orders taken while active receive a 20% discount on their bill, locked into `Order.lockedPriceCents` at order placement time. For VIP customers, the 10% VIP discount is applied first using integer cent arithmetic, followed by the 20% Happy Hour discount ($15.00 * 0.90 * 0.80 = $10.80, Section 4.3 example). Orders placed before Happy Hour remain unaffected.
  - **Satisfaction Recovery**: During each active turn, customers still waiting for a table (`QUEUED`) or waiting for food (`SEATED` or `ORDERED`) recover 15 satisfaction at turn start via `Customer.recoverSatisfaction(int)` (capped at 100). Customers already eating or ready to pay do not recover satisfaction.

---

## 2. Key Architectural Improvements over Stage 2 (Design Reflection)

### A. Generalization & Polymorphic Hierarchies (`Staff` / `SeatingStaff` & `Customer`)
- **Staff Hierarchy**: `abstract class Staff` encapsulates `private boolean actedThisTurn` and turn quota operations; `abstract class SeatingStaff extends Staff` centralizes seating validation and state transitions shared by `Host` and `Waiter`. `StaffController` resets all staff polymorphically via `List<Staff>`.
- **Customer Hierarchy**: `Customer` is an `abstract` base class declaring `getSatisfactionLossPerTurn()`, `getCustomerType()`, and `chooseMenuChoice()`, eliminating runtime reflection (`getClass().getSimpleName()`) and string-literal name checks.

### B. Composite Menu Design (Open-Closed Principle - OCP & LSP)
- `ComboMeal` extends `MenuItem` while holding two component `MenuItem` objects (**Composite Pattern**). Because `ComboMeal` *is-a* `MenuItem`, `Order`, `Waiter`, and `Chef` handle combo meals polymorphically with zero `instanceof` checks, while `canBeComboComponent()` prevents invalid nested combos.

### C. Information Expert & Encapsulation
- `Customer.computeOrderBillCents(item, isHappyHourActive)` computes and locks bills in integer cents at order creation time, and `Customer.recoverSatisfaction(points)` encapsulates Happy Hour eligibility and saturation rules.
- `GameScoreManager` evaluates all win/loss targets using integer cents (`totalRevenueCents >= 4500`).
- `Menu.getItems()` and `RestaurantController.getTables()` expose unmodifiable `List` views rather than raw mutable arrays, and `Order` / `Table` / `Customer` protect their internal invariants without raw state-corrupting setters.

### D. Strict Boundary-Control-Entity (BCE) & SRP Boundary Decomposition
- **Entity (`entity/`)** and **Control (`control/`)** contain **zero** `System.out` or `Scanner` calls. Controllers return structured immutable `ActionResult` objects and event lists.
- **Boundary (`boundary/`)** is decomposed by the **Single Responsibility Principle** to mirror the `control/` layer:
  - `InputReader`: Encapsulates `Scanner` lifecycle, EOF detection, and numeric token validation.
  - `GameStatusView`: Handles read-only CLI rendering (banners, turn headers, event logs, table/queue state, and scoreboards), querying victory status from `RestaurantController`.
  - `StaffActionMenu`: Handles interactive manager command menus (Happy Hour, Host, Waiter, Chef), dynamically scaling Chef menus via `getCookableTables()` and sharing a single table-selection helper (DRY).
  - `RestaurantUI`: Lightweight Boundary Facade coordinating `InputReader`, `GameStatusView`, and `StaffActionMenu`.
- **Entrypoint (`Main.java`)** cleanly wires `RestaurantController` and `RestaurantUI`.

---

## 3. User Inputs and Expected Results

### 1. Happy Hour Prompt (Turn Start)
- Shown once per game until activated:
  `Activate Happy Hour for Turn X? (1 = Yes, 2 = No): `
- Entering `1` activates Happy Hour for 2 turns and immediately grants +15 satisfaction recovery to eligible waiting/unserved customers.

### 2. Host Actions (Step 2A)
| 1st Input | 2nd Input | Action | Expected Result |
| :---: | :---: | :--- | :--- |
| 1 | 1 | Seat customer at Table 1 | Dequeues the first waiting customer and seats them at Table 1. |
| 1 | 2 | Seat customer at Table 2 | Dequeues the first waiting customer and seats them at Table 2. |
| 2 | - | Wait / Do Nothing | Host performs no action this turn. |
| 1 | 0 | Cancel Selection | Cancels seating and returns to Host menu without consuming action. |

### 3. Waiter Actions (Step 2B)
- **Option 1**: Seat customer at Table 1 or 2 (can seat another customer if queue has people and another table is free).
- **Option 2**: Take order at Table 1 or 2 (seated customer polymorphically selects food and locks bill).
- **Option 3**: Serve READY food at Table 1 or 2.
- **Option 4**: Wait / Do Nothing.

### 4. Chef Actions (Step 2C)
- Advance preparation by 1 unit on pending table orders, or wait.

---

## 4. Build & Execution

To compile and run Stage 3 from the `stage3/` directory:

```bash
# Compile all packages and source files
javac Main.java boundary/*.java control/*.java entity/*.java entity/customer/*.java entity/staff/*.java entity/queue/*.java

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

**Objective Targets Met at Turn 18**:
- Paid Customers: **4** (Target $\ge$ 4) $\rightarrow$ **PASSED**
- Total Revenue: **$68.10** (Target $\ge$ $45.00) $\rightarrow$ **PASSED**
  - Alice (Turn 4 Cashier): **$15.00** (Regular Burger)
  - Bob (Turn 7 Cashier): **$10.80** (VIP Burger ordered during Happy Hour: $15.00 * 0.90 * 0.80 = $10.80 locked)
  - Charlie (Turn 11 Cashier): **$18.00** (Critic Pasta)
  - David (Turn 15 Cashier): **$24.30** (Regular Salad + Pasta Combo)
- Average Paid Satisfaction: **86.0** (Target $\ge$ 60.0) $\rightarrow$ **PASSED**
- Unhappy Departures: **0**
- Overall Result: **VICTORY!**

**Turn-by-turn Manager Strategy**:
1. **Turn 1**: Alice arrives. Host seats Alice at Table 1. Waiter immediately takes Alice's Burger order on the same turn! Chef cooks unit 1 $\rightarrow$ Burger READY!
2. **Turn 2**: Waiter serves Burger to Alice. Alice begins eating.
3. **Turn 3**: Alice finishes eating $\rightarrow$ READY_TO_PAY.
4. **Turn 4**: Cashier pays Alice ($15.00); T1 freed. Bob (VIP) arrives. **Manager activates Happy Hour!** Host seats Bob at T1. Waiter takes Bob's order: Burger locked at $10.80 (VIP + Happy Hour). Chef cooks $\rightarrow$ READY!
5. **Turn 5**: Waiter serves Burger to Bob. Bob begins eating. Happy Hour turn 2 concludes.
6. **Turn 6**: Bob finishes eating $\rightarrow$ READY_TO_PAY.
7. **Turn 7**: Cashier pays Bob ($10.80); T1 freed. Charlie (Critic) arrives. Host seats Charlie at T1. Waiter takes Charlie's Pasta order. Chef cooks unit 1.
8. **Turn 8**: Chef cooks unit 2 $\rightarrow$ Pasta READY!
9. **Turn 9**: Waiter serves Pasta to Charlie. Charlie begins eating.
10. **Turn 10**: Charlie finishes eating (READY_TO_PAY). David (Regular) arrives. Host seats David at Table 2. Waiter takes David's order: **ComboMeal** (Salad + Pasta Combo, $24.30, 3 prep units). Chef cooks unit 1.
11. **Turn 11**: Cashier pays Charlie ($18.00); T1 freed. Chef cooks unit 2 for David.
12. **Turn 12**: Chef cooks unit 3 for David $\rightarrow$ ComboMeal READY!
13. **Turn 13**: Edward (VIP) arrives. Host seats Edward at T1. Waiter serves David at T2!
14. **Turn 14**: David finishes eating. Waiter takes Edward's order: **ComboMeal** (Burger + Pasta Combo, $26.73 VIP). Chef cooks unit 1 for Edward.
15. **Turn 15**: Cashier pays David ($24.30 ComboMeal); T2 freed. Edward unit 2 cooked.
16. **Turn 16**: Faith (Critic) arrives. Edward unit 3 cooked $\rightarrow$ READY!
17. **Turn 17**: Waiter serves Edward.
18. **Turn 18**: Turn 18 completes. Final victory targets evaluated.

**One-line Input Stream for TA Replication**:
```text
2 1 1 2 1 1 2 2 3 1 1 2 2 4 1 1 1 1 2 1 1 2 3 1 1 2 4 1 1 1 2 1 1 2 4 1 2 3 1 1 1 2 2 2 1 2 4 1 2 4 1 1 1 3 2 1 2 2 1 1 2 4 1 2 4 1 2 3 1 1 2 4 1
```

---

### B. Reproducible Defeat Run (Manager Inaction)

**Objective Targets Evaluated at Turn 18**:
- Paid Customers: **0** (Target $\ge$ 4) $\rightarrow$ **FAILED**
- Total Revenue: **$0.00** (Target $\ge$ $45.00) $\rightarrow$ **FAILED**
- Average Paid Satisfaction: **N/A** (Target $\ge$ 60.0) $\rightarrow$ **FAILED**
- Unhappy Departures: **2** (Alice and Charlie reach 0 satisfaction and abandon the queue)
- Result: **DEFEAT!**

**One-line Input Stream for TA Replication**:
Manager enters `2` (No Happy Hour), `2` (Host wait), `4` (Waiter wait), `1` (Chef wait) across all 18 turns:
```text
2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1 2 2 4 1
```
