package dev.Tejveer.EcomPaymentService.Config.kafka;

import dev.Tejveer.EcomPaymentService.Config.kafka.dto.OrderStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventProducer {

    private final KafkaTemplate<String, OrderStatusEvent> kafkaTemplate;

    @Value("${app.kafka.topic.order-status-events}")
    private String ORDER_EVENT_TOPIC;

    public void publishOrderStatusEvent(OrderStatusEvent orderStatusEvent){
        kafkaTemplate.send(ORDER_EVENT_TOPIC, orderStatusEvent.getOrderId(), orderStatusEvent);
    }

}
