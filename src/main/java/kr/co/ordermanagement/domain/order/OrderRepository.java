package kr.co.ordermanagement.domain.order;

public interface OrderRepository {
    Order add(Order order);
    Order findById(Long id);
}
