package kurs;

import java.util.Arrays;

public class Main {
    private static final int SIZE = 10_000_000;
    private static final int HALF = SIZE / 2;
    private static final Duration duration = new Duration();

    public static void main(String[] args) {
        float[] floats1 = getArrayMainThread();
        float[] floats2 = getArrayTwoThreads();
        float[] floats3 = getArrayMainAndAdditionalThread();
        float[] floats4 = getArrayTwoThreadsOneArray();
        System.out.println("----- сравниваем массивы (Arrays.equals) -----");
        System.out.println("один поток == два отдельных потока: " + Arrays.equals(floats1, floats2));
        System.out.println("один поток == главный + дополнительный поток: " + Arrays.equals(floats1, floats3));
        System.out.println("один поток == два отдельных потока, один массив: " + Arrays.equals(floats1, floats4));
    }

    private static float[] getArrayMainThread() {
        float[] a = createFilledArray();

        duration.fix("один поток");

        for (int i = 0; i < SIZE; i++) {
            a[i] = calcElement(a[i], i);
        }
        duration.out();
        return a;
    }

    private static float[] getArrayTwoThreads() {
        float[] a = createFilledArray();

        duration.fix("два отдельных потока");

        float[] a1 = createHalfFromArray(a, false);
        float[] a2 = createHalfFromArray(a, true);
        duration.subFix("разделение");

        CalcThread thread1 = new CalcThread(a1, 0);
        CalcThread thread2 = new CalcThread(a2, HALF);
        thread1.start();
        thread2.start();
        while (thread1.getState() != Thread.State.TERMINATED || thread2.getState() != Thread.State.TERMINATED);
        duration.subFix("расчет");

        uniteArrays(a, a1, a2);
        duration.out("склеивание");
        return a;
    }

    private static float[] getArrayMainAndAdditionalThread() {
        float[] a = createFilledArray();

        duration.fix("главный + дополнительный поток");

        float[] a1 = createHalfFromArray(a, false);
        float[] a2 = createHalfFromArray(a, true);
        duration.subFix("разделение");

        //только вторую часть массива считаем в отдельном потоке
        CalcThread thread = new CalcThread(a2, HALF);
        thread.start();
        //а первую - в основном
        for (int i = 0; i < HALF; i++) {
            a1[i] = calcElement(a1[i], i);
        }
        //ждем завершения потока
        while (thread.getState() != Thread.State.TERMINATED);
        duration.subFix("расчет");

        uniteArrays(a, a1, a2);
        duration.out("склеивание");
        return a;
    }

    private static float[] getArrayTwoThreadsOneArray() {
        float[] a = createFilledArray();

        duration.fix("два отдельных потока, один массив");

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                //именно первая половина считается
                for (int i = 0; i < HALF; i++) {
                    a[i] = calcElement(a[i], i);
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                //именно вторая половина считается
                for (int i = HALF; i < SIZE; i++) {
                    a[i] = calcElement(a[i], i);
                }
            }
        });
        thread1.start();
        thread2.start();
        while (thread1.getState() != Thread.State.TERMINATED || thread2.getState() != Thread.State.TERMINATED);

        duration.out();
        return a;
    }

    public static float calcElement(float a, int i) {
        return (float) (a * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
    }

    private static float[] createFilledArray() {
        float[] a = new float[SIZE];
        Arrays.fill(a, 1.0f);
        return a;
    }

    private static float[] createHalfFromArray(float[] a, boolean secondHalf) {
        float[] aHalf = new float[HALF];
        if (secondHalf) {
            System.arraycopy(a, HALF, aHalf, 0, HALF);
        } else {
            System.arraycopy(a, 0, aHalf, 0, HALF);
        }
        return aHalf;
    }

    private static void uniteArrays(float[] a, float[] a1, float[] a2) {
        System.arraycopy(a1, 0, a, 0, HALF);
        System.arraycopy(a2, 0, a, HALF, HALF);
    }
}
