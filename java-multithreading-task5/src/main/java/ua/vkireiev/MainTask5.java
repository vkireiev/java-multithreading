package ua.vkireiev;

public class MainTask5 {
    public static void main(String[] args) {
        System.out.println("Thread '" + Thread.currentThread().getName() + "': start");

        OrderSupplier orderSupplier = new OrderSupplier();
        OrderProducer orderProducer1 = new OrderProducer(orderSupplier);
        OrderProducer orderProducer2 = new OrderProducer(orderSupplier);
        Thread producer1 = new Thread(orderProducer1, "Producer1");
        Thread producer2 = new Thread(orderProducer2, "Producer2");
        OrderConsumer orderConsumer1 = new OrderConsumer(orderSupplier);
        OrderConsumer orderConsumer2 = new OrderConsumer(orderSupplier);
        Thread consumer1 = new Thread(orderConsumer1, "Consumer1");
        Thread consumer2 = new Thread(orderConsumer2, "Consumer2");

        try {
            consumer1.start();
            consumer2.start();
            producer1.start();
            producer2.start();

            Thread.sleep(10000);
            orderProducer1.finish();

            Thread.sleep(5000);
            orderProducer2.finish();

            producer1.join();
            producer2.join();
            consumer1.join();
            consumer2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread '" + Thread.currentThread().getName() + "' interrupted");
        }

        System.out.println("Thread '" + Thread.currentThread().getName() + "': finish");
    }
}
