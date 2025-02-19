package ua.vkireiev;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class OrderConsumer implements Runnable {
    volatile boolean consuming = true;
    final OrderSupplier orderSupplier;

    public OrderConsumer(OrderSupplier orderSupplier) {
        this.orderSupplier = orderSupplier;
    }

    @Override
    public void run() {
        System.out.println("Thread '" + Thread.currentThread().getName() + "': start");
        int count = 0;
        int idleCount = 0;
        while (consuming && idleCount < 5) {
            try {
                Optional<OrderProcessing> processedOrder = orderSupplier.processOrder();
                if (processedOrder.isPresent()) {
                    System.out.println("Thread '" + Thread.currentThread().getName()
                            + "': processed '" + processedOrder.get().getOrder().getName()
                            + "' with ID=" + processedOrder.get().getOrder().getId());
                    count++;
                    idleCount = 0;
                } else {
                    System.out.println("Thread '" + Thread.currentThread().getName() + "': nothing to process...");
                    idleCount++;
                }
                int sleep = ThreadLocalRandom.current().nextInt(1000, 2000);
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.println("Thread '" + Thread.currentThread().getName() + "' interrupted");
            }
        }
        System.out.println("Thread '" + Thread.currentThread().getName() + "': processed " + count + " orders");
        System.out.println("Thread '" + Thread.currentThread().getName() + "': finish");
    }
}
