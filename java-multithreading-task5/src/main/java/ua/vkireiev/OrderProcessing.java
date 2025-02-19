package ua.vkireiev;

import java.util.Objects;

public class OrderProcessing {
    final Order order;
    final int processingTime;
    volatile boolean inProcessing = false;

    public OrderProcessing(Order order, int processingTime) {
        this.order = order;
        this.processingTime = processingTime;
    }

    public Order getOrder() {
        return order;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public boolean isInProcessing() {
        return inProcessing;
    }

    public void setInProcessing(boolean inProcessing) {
        this.inProcessing = inProcessing;
    }

    @Override
    public String toString() {
        return "OrderPlacement{" +
                "order=" + order +
                ", processingTime=" + processingTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderProcessing that = (OrderProcessing) o;
        return Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(order);
    }
}
