package ua.vkireiev;

public class LiteralsGenerator implements Runnable {
    final SymbolPrinter printer;

    public LiteralsGenerator(SymbolPrinter printer) {
        this.printer = printer;
    }

    @Override
    public void run() {
        for (char i = 'A'; i < 'F'; i++) {
            printer.printLiteral(i);
        }
    }
}
