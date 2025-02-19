package ua.vkireiev;

public class SymbolPrinter {
    final private Object lock = new Object();
    private boolean isDigitPrinting = true;

    void printDigit(int i) {
        synchronized (this.lock) {
            while (!isDigitPrinting) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    System.out.println("Thread '" + Thread.currentThread().getName() + "' interrupted.");
                }
            }

            System.out.print(i + " ");
            isDigitPrinting = false;
            sleepAfterPrint(400);
            this.lock.notifyAll();
        }
    }

    void printLiteral(char c) {
        synchronized (this.lock) {
            while (isDigitPrinting) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    System.out.println("Thread '" + Thread.currentThread().getName() + "' interrupted.");
                }
            }

            System.out.println(c);
            isDigitPrinting = true;
            sleepAfterPrint(1000);
            this.lock.notifyAll();
        }
    }

    private void sleepAfterPrint(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            System.out.println("Thread '" + Thread.currentThread().getName() + "' interrupted.");
        }
    }
}
