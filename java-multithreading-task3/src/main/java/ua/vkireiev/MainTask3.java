package ua.vkireiev;

public class MainTask3 {
    public static void main(String[] args) {
        SymbolPrinter printer = new SymbolPrinter();
        Thread digitsPrinter = new Thread(new DigitsGenerator(printer));
        Thread literalsPrinter = new Thread(new LiteralsGenerator(printer));

        try {
            digitsPrinter.start();
            literalsPrinter.start();
            //
            digitsPrinter.join();
            literalsPrinter.join();
        } catch (InterruptedException e) {
            System.out.println("Thread '" + Thread.currentThread().getName() + "' interrupted.");
        }
    }
}
