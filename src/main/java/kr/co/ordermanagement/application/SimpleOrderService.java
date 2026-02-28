package kr.co.ordermanagement.application;

import kr.co.ordermanagement.domain.order.Order;
import kr.co.ordermanagement.domain.order.OrderRepository;
import kr.co.ordermanagement.domain.product.Product;
import kr.co.ordermanagement.domain.product.ProductRepository;
import kr.co.ordermanagement.presentation.dto.ChangeStateReqeustDto;
import kr.co.ordermanagement.presentation.dto.OrderProductRequestDto;
import kr.co.ordermanagement.presentation.dto.OrderResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.plaf.BorderUIResource;
import java.util.List;

@Service
public class SimpleOrderService {

    private ProductRepository productRepository;
    private OrderRepository orderRepository;

    @Autowired
    public SimpleOrderService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public OrderResponseDto createOrder(List<OrderProductRequestDto> orderProductRequestDtos) {
        List<Product> orderedProducts = makeOrderedProducts(orderProductRequestDtos);
        decreaseProductsAmount(orderedProducts);

        Order order = new Order(orderedProducts);
        orderRepository.add(order);

        OrderResponseDto orderResponseDto = OrderResponseDto.toDto(order);
        return orderResponseDto;
    }

    private List<Product> makeOrderedProducts(List<OrderProductRequestDto> orderProductRequestDtos) {
        return orderProductRequestDtos
                .stream()
                .map(orderProductRequestDto -> {
                    // Product를 조회
                    Long productId = orderProductRequestDto.getId();
                    Product product = productRepository.findById(productId);

                    // 조회된 Product의 상품 재고(amount)가 충분한지 확인
                    Integer orderedAmount = orderProductRequestDto.getAmount();
                    product.checkEnoughAmount(orderedAmount);

                    // 조회 후에는 Product 생성
                    return new Product(productId, product.getName(), product.getPrice(), orderProductRequestDto.getAmount());
                }).toList();
    }

    private void decreaseProductsAmount(List<Product> orderedProducts) {
        orderedProducts
                .stream()
                .forEach(orderedProduct -> {
                    Long productId = orderedProduct.getId();
                    Product product = productRepository.findById(productId);

                    Integer orderedAmount = orderedProduct.getAmount();
                    product.decreaseAmount(orderedAmount);

                    // productRepository.update(product);
                });
    }

    public OrderResponseDto findById(Long orderId) {
        Order order = orderRepository.findById(orderId);

        OrderResponseDto orderResponseDto = OrderResponseDto.toDto(order);
        return orderResponseDto;
    }

    public OrderResponseDto changeState(Long orderId, ChangeStateReqeustDto changeStateRequestDto) {
        Order order = orderRepository.findById(orderId);
        String state = changeStateRequestDto.getState();

        order.changeStateForce(state);
        // orderRepository.update(order);

        OrderResponseDto orderResponseDto = OrderResponseDto.toDto(order);
        return orderResponseDto;
    }

    public List<OrderResponseDto> findByState(String state) {
        List<Order> orders = orderRepository.findByState(state);

        List<OrderResponseDto> orderResponseDtos = orders
                .stream()
                .map(order -> OrderResponseDto.toDto(order))
                .toList();

        return orderResponseDtos;
    }
}
