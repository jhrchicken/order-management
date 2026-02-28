package kr.co.ordermanagement.domain.order;

import kr.co.ordermanagement.domain.exception.CanNotCancellableStateException;
import kr.co.ordermanagement.domain.product.Product;

import java.util.List;

public class Order {
    private Long id;
    private List<Product> orderedProducts;
    private Integer totalPrice;
    private State state;

    public Order(List<Product> orderedProducts) {
        this.orderedProducts = orderedProducts;
        this.totalPrice = calculateTotalPrice(orderedProducts);
        this.state = State.CREATED;
    }

    public Long getId() {
        return id;
    }

    public List<Product> getOrderedProducts() {
        return orderedProducts;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public State getState() {
        return state;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Integer calculateTotalPrice(List<Product> orderedProducts) {
        return orderedProducts
                .stream()
                .mapToInt(orderedProduct -> orderedProduct.getPrice() * orderedProduct.getAmount())
                .sum();
    }

    public boolean sameId(Long id) {
        return this.id.equals(id);
    }

    public void changeStateForce(State state) {
        this.state = state;
    }

    public boolean sameState(State state) {
        return this.state.equals(state);
    }

    public void cancel() {
        this.state.checkCancellable();
        this.state = State.CANCELED;
    }
}
