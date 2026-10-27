import java.util.ArrayList;
import java.util.List;

public class CustomerRulesTest {
    static int passed = 0, failed = 0;

    public static void main(String[] args) {
        example33();
        seatedAbandonment();
        queueAbandonment();
        ledger();
        System.out.println("\n" + passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }

    static void example33() {
        System.out.println("-- Section 3.3 example");
        CustomerRules rules = new CustomerRules();
        Customer c = new Customer("Cust_1");
        Table t = new Table(1);
        check("T1 in queue", rules.statusOf(c, false) == CustomerStatus.IN_QUEUE);

        t.seat(c);
        rules.applyWaitingTurn(t);
        check("T1 sat 92", c.getSatisfaction() == 92);
        check("T1 seated", rules.statusOf(c, true) == CustomerStatus.SEATED);

        Order o = new Waiter().takeOrder(c, Menu.Choice(3));
        o.advancePreparation();
        rules.applyWaitingTurn(t);
        check("T2 sat 84", c.getSatisfaction() == 84);
        check("T2 cooking", rules.statusOf(c, true) == CustomerStatus.COOKING);

        o.advancePreparation();
        rules.applyWaitingTurn(t);
        check("T3 sat 76", c.getSatisfaction() == 76);
        check("T3 ready", rules.statusOf(c, true) == CustomerStatus.READY);

        o.serve(4);
        rules.applyWaitingTurn(t);
        check("T4 sat 76", c.getSatisfaction() == 76);
        check("T4 eating", rules.statusOf(c, true) == CustomerStatus.EATING);

        o.setReadyToPay(true); o.setTurnReadyToPay(5);
        rules.applyWaitingTurn(t);
        check("T5 sat 76", c.getSatisfaction() == 76);
        check("T5 ready to pay", rules.statusOf(c, true) == CustomerStatus.READY_TO_PAY);
        check("served customers lose 0", rules.lossFor(c) == 0);
    }

    static void seatedAbandonment() {
        System.out.println("-- Seated abandonment");
        CustomerRules rules = new CustomerRules();
        Customer c = new Customer("Cust_2");
        Table t = new Table(2);
        t.seat(c);
        for (int i = 1; i <= 12; i++) {
            check("turn " + i + " stays", rules.applyWaitingTurn(t) == null);
        }
        check("sat 4 after 12 turns", c.getSatisfaction() == 4);
        check("13th turn leaves", rules.applyWaitingTurn(t) == c);
        check("floors at 0", c.getSatisfaction() == 0);
        check("table freed", !t.isOccupied());
    }

    static void queueAbandonment() {
        System.out.println("-- Queue abandonment");
        CustomerRules rules = new CustomerRules();
        List<Customer> q = new ArrayList<>();
        Customer a = new Customer("A"), b = new Customer("B");
        b.setSatisfaction(8);
        q.add(a); q.add(b);
        List<Customer> gone = rules.applyWaitingTurn(q);
        check("B removed", gone.size() == 1 && gone.get(0) == b);
        check("A stays at 92", q.size() == 1 && a.getSatisfaction() == 92);
    }

    static void ledger() {
        System.out.println("-- PaymentLedger");
        PaymentLedger l = new PaymentLedger();
        check("empty avg is N/A", l.formatAverageSatisfaction().equals("N/A"));
        check("empty is not victory", !l.isVictory());
        int[] prices = {1500, 1500, 1500, 1800};
        for (int i = 0; i < prices.length; i++) {
            Customer c = new Customer("C" + i);
            c.setSatisfaction(84);
            l.recordPayment(c, new Order("x", prices[i] / 100.0, 1));
        }
        check("4 paid", l.getPaidCount() == 4);
        check("revenue 6300 cents", l.getRevenueCents() == 6300);
        check("format $63.00", l.formatRevenue().equals("$63.00"));
        check("avg 84.0", l.formatAverageSatisfaction().equals("84.0"));
        check("victory", l.isVictory());

        PaymentLedger low = new PaymentLedger();
        for (int i = 0; i < 4; i++) {
            Customer c = new Customer("D" + i);
            c.setSatisfaction(50);
            low.recordPayment(c, new Order("x", 15.0, 1));
        }
        check("low satisfaction -> defeat", !low.isVictory() && low.paidTargetMet() && low.revenueTargetMet());

        PaymentLedger edge = new PaymentLedger();
        for (int i = 0; i < 4; i++) {
            Customer c = new Customer("E" + i);
            c.setSatisfaction(60);
            edge.recordPayment(c, new Order("x", 11.25, 1));
        }
        check("exactly $45 and avg 60 -> victory (>=)", edge.isVictory());
    }

    static void check(String name, boolean ok) {
        if (ok) { passed++; System.out.println("  PASS  " + name); }
        else    { failed++; System.out.println("  FAIL  " + name); }
    }
}
