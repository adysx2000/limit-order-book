package ro.satsoft.test.bento.service;

import ro.satsoft.test.bento.model.Order;
import ro.satsoft.test.bento.helper.OrderList;

import java.util.List;
import java.util.stream.Collectors;

public class OrderBook {
    public static final char ORDER_BID = 'B';
    public static final char ORDER_OFFER = 'O';

    //Better to use Set and implement equals and hashcode in Order
    //But because we're not allowed to modify the Order class, we'll use a List
    private OrderList orderList = new OrderList();

    public boolean addOrder(Order order) throws Exception {
        if (order == null){
            throw new Exception("Cannot add null Orders!");
        }

        if (orderList.contains(order)){
            return false;
        }

        return orderList.add(order, true);
    }

    public boolean removeOrder(long orderId) {
        return orderList.removeIf(o -> o.getId() == orderId);
    }

    public boolean updateOrder(long orderId, long size) {
        Order order = findById(orderId);
        if (order == null){
            return false;
        }

        if ( removeOrder(orderId) ) {
            return orderList.add(new Order(order.getId(), order.getPrice(), order.getSide(), size), false);
        }

        return false;
    }

    public double getPriceBySideAndLevel(char side, long level) throws Exception {
        if (side != ORDER_BID && side != ORDER_OFFER){
            throw new Exception("Invalid side!");
        }

        if (level < 1){
            throw new Exception("Invalid level!");
        }

        return orderList.stream()
                .filter(o -> o.getSide() == side)
                .map(Order::getPrice)
                .distinct()
                .sorted((o1, o2) -> {
                    if (side == ORDER_BID){
                        return Double.compare(o2, o1);
                    }
                    else {
                        return Double.compare(o1, o2);
                    }
                })
                .skip(level - 1)
                .findFirst()
                .orElse(0.0);
    }

    public long getTotalSizeBySideAndLevel(char side, long level) throws Exception {
        if (side != ORDER_BID && side != ORDER_OFFER){
            throw new Exception("Invalid side!");
        }

        if (level < 1){
            throw new Exception("Invalid level!");
        }

        double levelPrice = getPriceBySideAndLevel(side, level);
        return orderList.stream()
                .filter(o -> o.getSide() == side && levelPrice == o.getPrice())
                .map(Order::getSize)
                .reduce(0L, Long::sum);
    }

    public List<Order> getOrdersForSide(char side) throws Exception{
        if (side != ORDER_BID && side != ORDER_OFFER){
            throw new Exception("Invalid side!");
        }

        orderList.sortByTimeMapping();

        return orderList.stream()
                .filter(o -> o.getSide() == side)
                .collect(Collectors.toList());
    }

    public Order findById(long orderId) {
        return orderList.stream()
                .filter(o -> o.getId() == orderId)
                .findFirst()
                .orElse(null);
    }

    public void clearOrders() {
        orderList.clear();
    }

    public boolean contains(Order order) {
        return orderList.contains(order);
    }
}
