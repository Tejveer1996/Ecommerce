package dev.tejveer.EcomOrderService.kafka;

import dev.tejveer.EcomOrderService.kafka.dto.OrderConfirmEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventProducer {

    @Value("${app.kafka.topic.order-confirmed-events}")
    private String ORDER_CONFIRM_TOPIC;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderConfirmedEvent(OrderConfirmEventDto orderConfirmEventDto){
        kafkaTemplate.send(ORDER_CONFIRM_TOPIC, orderConfirmEventDto.getOrderId(), orderConfirmEventDto)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish order-confirmed event for orderId {}",
                                orderConfirmEventDto.getOrderId(), ex);
                    }
                });
    }
}
