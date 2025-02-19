package ua.vkireiev;

import java.util.concurrent.ThreadLocalRandom;

public class OrderProducer implements Runnable {
    volatile boolean producing = true;
    final OrderSupplier orderSupplier;

    public OrderProducer(OrderSupplier orderSupplier) {
        this.orderSupplier = orderSupplier;
    }

    @Override
    public void run() {
        System.out.println("Thread '" + Thread.currentThread().getName() + "': start");
        int count = 0;
        while (producing && (count < 10)) {
            try {
                int sleep = ThreadLocalRandom.current().nextInt(1000, 2000);
                Thread.sleep(sleep);
                int orderProcessingTime = ThreadLocalRandom.current().nextInt(200, 2000);
                String orderName = "Order #" + String.valueOf(ThreadLocalRandom.current().nextInt(1000000, 9999999));
                OrderProcessing orderProcessing = orderSupplier.addOrder(orderName, orderProcessingTime);
                System.out.println("Thread '" + Thread.currentThread().getName() + "': placed '" + orderName
                        + "' with ID=" + orderProcessing.getOrder().getId());
            } catch (InterruptedException e) {
                System.out.println("Thread '" + Thread.currentThread().getName() + "' interrupted");
            } finally {
                count++;
            }
        }
        System.out.println("Thread '" + Thread.currentThread().getName() + "': placed " + count + " orders");
        System.out.println("Thread '" + Thread.currentThread().getName() + "': finish");
    }

    void finish() {
        this.producing = false;
    }
}
