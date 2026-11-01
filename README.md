## Stage 1: User Inputs and Expected Results

### 1. Waiter Actions

| 1st Input | 2nd Input | Action | Prompt | Expected Result |
|---|---|---|---|---|
| 1 | 1 | Seat customer | Which table to seat at? (1 or 2, 0 to cancel): | Seats the first waiting customer at Table 1, if available. |
| 1 | 2 | Seat customer | Which table to seat at? (1 or 2, 0 to cancel): | Seats the first waiting customer at Table 2, if available. |
| 2 | 1 | Take Order | Which Table to take order for? (1 or 2, 0 to cancel): | Takes the order of the customer at Table 1. The customer automatically selects a dish. |
| 2 | 2 | Take Order | Which Table to take order for? (1 or 2, 0 to cancel): | Takes the order of the customer at Table 2. The customer automatically selects a dish. |
| 3 | 1 | Serve Food | Which Table to serve READY food to? (1 or 2, 0 to cancel): | Serves the READY order at Table 1. Customer satisfaction stops decreasing. |
| 3 | 2 | Serve Food | Which Table to serve READY food to? (1 or 2, 0 to cancel): | Serves the READY order at Table 2. Customer satisfaction stops decreasing. |
| 4 | - | Wait | - | Waiter performs no task for the turn. |
| 1, 2 or 3 | 0 | Cancel Selection | Waiter choice prompt | Cancels the selection and allows the player to choose another action. |
| Invalid number | - | Invalid input | Waiter choice prompt | Displays an error message and prompts the player to enter a valid option. |

### 2. Chef Actions

#### Case 1: One Pending Order

| Input | Action | Expected Result |
|---|---|---|
| 1 | Cook order | Prepares one unit of the pending order. |
| 2 | Wait | Chef performs no task for the turn. |
| Invalid number | - | Displays an error message and allows the player to choose again. |

#### Case 2: Two Pending Orders

| Input | Action | Expected Result |
|---|---|---|
| 1 | Cook order for Table 1 | Prepares one unit of the pending order from Table 1. |
| 2 | Cook order for Table 2 | Prepares one unit of the pending order from Table 2. |
| 3 | Wait | Chef performs no task for the turn. |
| Invalid number | - | Displays an error message and allows the player to choose again. |
