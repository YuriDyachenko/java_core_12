package kurs;

public class CalcThread extends Thread {
    private final float[] a;
    private final int startIndex;

    public CalcThread(float[] a, int startIndex) {
        this.a = a;
        this.startIndex = startIndex;
    }

    @Override
    public void run() {
        int size = a.length;
        int j = startIndex;
        for (int i = 0; i < size; i++, j++) {
            a[i] = Main.calcElement(a[i], j);
        }
    }
}
