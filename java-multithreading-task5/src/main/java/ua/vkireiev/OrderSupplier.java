package ua.vkireiev;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class OrderSupplier {
    final static List<OrderProcessing> orders = new ArrayList<>();
    volatile static long ordersCount = 0;

    final private Object ordersLockForProcessOrder = new Object();

    OrderProcessing addOrder(String name, int processingTime) {
        synchronized (orders) {
            System.out.println("Thread '" + Thread.currentThread().getName() + "': trying to place '" + name
                    + "' with processingTime=" + processingTime);
            Order order = new Order(ordersCount++, name);
            OrderProcessing orderPlacement = new OrderProcessing(order, processingTime);
            orders.add(orderPlacement);

            orders.notifyAll();
            return orderPlacement;
        }
    }

    Optional<OrderProcessing> processOrder() {
        Optional<OrderProcessing> orderProcessingOptional = Optional.empty();
        synchronized (ordersLockForProcessOrder) {
            orderProcessingOptional = orders.stream()
                    .filter(Predicate.not(OrderProcessing::isInProcessing))
                    .findAny();
        }
        if (orderProcessingOptional.isPresent()) {
            OrderProcessing orderProcessing = orderProcessingOptional.get();
            synchronized (orderProcessing) {
                try {
                    orderProcessing.setInProcessing(true);
                    System.out.println("Thread '" + Thread.currentThread().getName()
                            + "': trying to process '" + orderProcessing.getOrder().getName()
                            + "' with processingTime=" + orderProcessing.getProcessingTime());
                    long startTime = System.currentTimeMillis();
                    int processingTime = 0;
                    while (processingTime < orderProcessing.processingTime) {
                        int timeSleep = ThreadLocalRandom.current().nextInt(200, 2000);
                        Thread.sleep(timeSleep);
                        long loopTime = System.currentTimeMillis();
                        processingTime = (int) (loopTime - startTime);
                    }

                    orders.remove(orderProcessing);
                    System.out.println("Thread '" + Thread.currentThread().getName()
                            + "': spend " + processingTime
                            + " to process '" + orderProcessing.getOrder().getName()
                            + "' with processingTime=" + orderProcessing.getProcessingTime());
                    orderProcessing.notifyAll();
                } catch (InterruptedException e) {
                    System.out.println("Thread '" + Thread.currentThread().getName() + "' interrupted");
                    orderProcessing.setInProcessing(false);
                }
            }
        }

        return orderProcessingOptional;
    }
}
