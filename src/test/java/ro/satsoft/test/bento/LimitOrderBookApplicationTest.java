package ro.satsoft.test.bento;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ro.satsoft.test.bento.model.Order;
import ro.satsoft.test.bento.service.OrderBook;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LimitOrderBookApplicationTest {

    private OrderBook underTest;

    @BeforeAll
    public void setup() {
        underTest = new OrderBook();
    }

    @BeforeEach
    public void init() {
        underTest.clearOrders();
    }

    @Test
    public void addOrderThrowExceptionWhenNullTest() {
        assertThatThrownBy(() -> underTest.addOrder(null))
                .hasMessage("Cannot add null Orders!");
    }

    @Test
    public void addOrderReturnFalseWhenIdExistsInOrdersTest() throws Exception {
        createOrders();
        Order order = new Order(1, 12, OrderBook.ORDER_BID, 13);

        // when
        boolean returnValue = underTest.addOrder(order);

        // then
        assertThat(returnValue).isFalse();
    }

    @Test
    public void addOrderReturnTrueWhenNotNullTest() throws Exception {
        Order order = new Order(1, 2, OrderBook.ORDER_BID, 3);

        // when
        boolean returnValue = underTest.addOrder(order);

        // then
        assertThat(returnValue).isTrue();
        assertThat(underTest.contains(order)).isTrue();
    }

    @Test
    public void removeOrderReturnFalseWhenNotThereTest() throws Exception {
        createOrders();
        Order order = new Order(100, 12, OrderBook.ORDER_BID, 13);

        // when
        boolean returnValue = underTest.removeOrder(order.getId());

        // then
        assertThat(returnValue).isFalse();
    }

    @Test
    public void removeOrderReturnTrueWhenThereTest() throws Exception {
        createOrders();
        Order order = new Order(1, 12, OrderBook.ORDER_BID, 13);

        assertThat(underTest.contains(order)).isTrue();

        // when
        boolean returnValue = underTest.removeOrder(order.getId());

        // then
        assertThat(returnValue).isTrue();
        assertThat(underTest.contains(order)).isFalse();
    }

    @Test
    public void updateOrderReturnFalseWhenOrderIdNotExistTest() throws Exception {
        createOrders();
        Order order = new Order(100, 12, OrderBook.ORDER_BID, 13);

        assertThat(underTest.contains(order)).isFalse();

        // when
        boolean returnValue = underTest.updateOrder(order.getId(), order.getSize());

        // then
        assertThat(returnValue).isFalse();
    }

    @Test
    public void updateOrderReturnTrueWhenOrderIdExistTest() throws Exception {
        createOrders();
        Order order = new Order(1, 12, OrderBook.ORDER_BID, 13);

        assertThat(underTest.contains(order)).isTrue();

        // when
        boolean returnValue = underTest.updateOrder(order.getId(), order.getSize());

        // then
        assertThat(returnValue).isTrue();
        assertThat(underTest.findById(order.getId()).getSize()).isEqualTo(order.getSize());
    }

    @Test
    public void getPriceBySideAndLevelThrowExceptionWhenSideNotDefinedTest() {
        assertThatThrownBy(() -> underTest.getPriceBySideAndLevel('X', 2))
                .hasMessage("Invalid side!");
    }

    @Test
    public void getPriceBySideAndLevelThrowExceptionWhenLevelNotDefinedTest() {
        assertThatThrownBy(() -> underTest.getPriceBySideAndLevel(OrderBook.ORDER_BID, 0))
                .hasMessage("Invalid level!");
    }

    @Test
    public void getPriceBySideAndLevelReturnZeroWhenOrderListEmptyTest() throws Exception {
        // when
        double returnValue = underTest.getPriceBySideAndLevel(OrderBook.ORDER_BID, 1);

        // then
        assertThat(returnValue).isEqualTo(0);
    }

    @Test
    public void getPriceBySideAndLevelReturnCorrectWhenBidSelectedTest() throws Exception {
        createOrders();

        // when
        double returnValue1 = underTest.getPriceBySideAndLevel(OrderBook.ORDER_BID, 1);
        double returnValue2 = underTest.getPriceBySideAndLevel(OrderBook.ORDER_BID, 2);

        // then
        assertThat(returnValue1).isEqualTo(4);
        assertThat(returnValue2).isEqualTo(3);
    }

    @Test
    public void getPriceBySideAndLevelReturnCorrectWhenOfferSelectedTest() throws Exception {
        createOrders();

        // when
        double returnValue1 = underTest.getPriceBySideAndLevel(OrderBook.ORDER_OFFER, 1);
        double returnValue2 = underTest.getPriceBySideAndLevel(OrderBook.ORDER_OFFER, 2);

        // then
        assertThat(returnValue1).isEqualTo(5);
        assertThat(returnValue2).isEqualTo(6);
    }

    @Test
    public void getTotalSizeBySideAndLevelThrowExceptionWhenSideNotDefinedTest() {
        assertThatThrownBy(() -> underTest.getTotalSizeBySideAndLevel('X', 2))
                .hasMessage("Invalid side!");
    }

    @Test
    public void getTotalSizeBySideAndLevelThrowExceptionWhenLevelNotDefinedTest() {
        assertThatThrownBy(() -> underTest.getTotalSizeBySideAndLevel(OrderBook.ORDER_BID, 0))
                .hasMessage("Invalid level!");
    }

    @Test
    public void getTotalSizeBySideAndLevelReturnZeroWhenOrderListEmptyTest() throws Exception {
        // when
        double returnValue = underTest.getTotalSizeBySideAndLevel(OrderBook.ORDER_BID, 1);

        // then
        assertThat(returnValue).isEqualTo(0);
    }

    @Test
    public void getTotalSizeBySideAndLevelReturnCorrectWhenBidSelectedTest() throws Exception {
        createOrders();

        // when
        double returnValue1 = underTest.getTotalSizeBySideAndLevel(OrderBook.ORDER_BID, 1);
        double returnValue2 = underTest.getTotalSizeBySideAndLevel(OrderBook.ORDER_BID, 2);

        // then
        assertThat(returnValue1).isEqualTo(12);
        assertThat(returnValue2).isEqualTo(3);
    }

    @Test
    public void getTotalSizeBySideAndLevelReturnCorrectWhenOfferSelectedTest() throws Exception {
        createOrders();

        // when
        double returnValue1 = underTest.getTotalSizeBySideAndLevel(OrderBook.ORDER_OFFER, 1);
        double returnValue2 = underTest.getTotalSizeBySideAndLevel(OrderBook.ORDER_OFFER, 2);

        // then
        assertThat(returnValue1).isEqualTo(14);
        assertThat(returnValue2).isEqualTo(3);
    }

    @Test
    public void getOrdersForSideThrowExceptionWhenSideNotDefinedTest() {
        assertThatThrownBy(() -> underTest.getOrdersForSide('X'))
                .hasMessage("Invalid side!");
    }

    @Test
    public void getOrdersForSideReturnCorrectWhenOfferSelectedTest() throws Exception {
        createOrders();

        // when
        List<Order> returnValue = underTest.getOrdersForSide(OrderBook.ORDER_OFFER);

        // then
        assertThat(returnValue.size()).isEqualTo(4);

        assertThat(returnValue.get(0)).isNotNull();
        assertThat(returnValue.get(0).getId()).isEqualTo(4);

        assertThat(returnValue.get(returnValue.size() - 1)).isNotNull();
        assertThat(returnValue.get(returnValue.size() - 1).getId()).isEqualTo(6);
    }

    private void createOrders() throws Exception {
        underTest.addOrder(new Order(1, 2, OrderBook.ORDER_BID, 1));
        underTest.addOrder(new Order(2, 4, OrderBook.ORDER_BID, 2));
        underTest.addOrder(new Order(3, 3, OrderBook.ORDER_BID, 3));
        underTest.addOrder(new Order(7, 4, OrderBook.ORDER_BID, 10));

        underTest.addOrder(new Order(4, 7, OrderBook.ORDER_OFFER, 1));
        underTest.addOrder(new Order(5, 5, OrderBook.ORDER_OFFER, 2));
        underTest.addOrder(new Order(8, 5, OrderBook.ORDER_OFFER, 12));
        underTest.addOrder(new Order(6, 6, OrderBook.ORDER_OFFER, 3));
    }

}
