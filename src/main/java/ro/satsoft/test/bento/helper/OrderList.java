package ro.satsoft.test.bento.helper;

import ro.satsoft.test.bento.model.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderList extends ArrayList<Order> {
    //Could be done better with a TreeMap or by defining another class for the mapping, etc
    private Map<Long, Long> orderTimeMapping = new HashMap<>();

    public boolean add(Order order, boolean updateTime) {
        boolean ok = true;

        if (updateTime){
            ok = orderTimeMapping.put(order.getId(), System.currentTimeMillis()) == null;
        }

        if (ok) {
            ok = super.add(order);

            //revert the changes in the map if something went wrong
            if (!ok && updateTime) {
                orderTimeMapping.remove(order.getId());
            }
        }

        return ok;
    }

    @Override
    public void clear() {
        orderTimeMapping.clear();
        super.clear();
    }

    @Override
    public boolean contains(Object order) {
        if (order == null || !(order instanceof Order)){
            return false;
        }

        return this.stream()
                .filter(o -> o.getId() == ((Order)order).getId())
                .count() > 0;
    }

    public void sortByTimeMapping(){
        //sort by time
        this.sort((o1, o2) -> {
            long time1 = orderTimeMapping.get(o1.getId());
            long time2 = orderTimeMapping.get(o2.getId());

            return Long.compare(time1, time2);
        });
    }
}
