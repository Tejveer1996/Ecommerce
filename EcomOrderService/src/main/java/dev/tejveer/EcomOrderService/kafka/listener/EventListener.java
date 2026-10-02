package dev.tejveer.EcomOrderService.kafka.listener;

import dev.tejveer.EcomOrderService.Exception.UpdateOrderException;
import dev.tejveer.EcomOrderService.Impl.service.OrderService;
import dev.tejveer.EcomOrderService.kafka.listener.dto.OrderStatusEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventListener {
    private final OrderService orderService;

    @KafkaListener(
            topics = "${app.kafka.topic.order-status-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    private void orderStatusListener(OrderStatusEventDto orderStatusEventDto) throws UpdateOrderException {
        orderService.updateOrderPaymentStatus(orderStatusEventDto);
    }
}
