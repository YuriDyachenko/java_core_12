package kurs;

import java.util.LinkedList;
import java.util.List;

public class Duration {
    private long millis = 0;
    private long subMillis = 0;
    private final List<String> interior = new LinkedList<>();

    public void fix(String msg) {
        interior.clear();
        System.out.println("----- " + msg + " -----");
        System.out.println("старт...");
        millis = System.currentTimeMillis();
        subMillis = millis;
    }

    public void subFix(String msg) {
        long currentMillis = System.currentTimeMillis();
        interior.add("  " + msg + " = " + (currentMillis - subMillis));
        subMillis = currentMillis;
    }

    public void out() {
        long passedMillis = System.currentTimeMillis() - millis;
        System.out.println("стоп через " + passedMillis);
    }

    public void out(String subMsg) {
        long currentMillis = System.currentTimeMillis();
        System.out.println("стоп через " + (currentMillis - millis));
        if (interior.size() == 0) return;
        interior.add("  " + subMsg + " = " + (currentMillis - subMillis));
        System.out.println("в т.ч.:");
        for (String item: interior) {
            System.out.println(item);
        }
    }
}
