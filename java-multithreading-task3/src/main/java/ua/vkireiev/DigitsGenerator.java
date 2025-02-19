package ua.vkireiev;

public class DigitsGenerator implements Runnable {
    final SymbolPrinter printer;

    public DigitsGenerator(SymbolPrinter printer) {
        this.printer = printer;
    }

    @Override
    public void run() {
        for (int i = 1; i < 6; i++) {
            printer.printDigit(i);
        }
    }
}
