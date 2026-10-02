package dev.tejveer.EcomOrderService.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class Config {

    @Value("${app.kafka.topic.order-confirmed-events}")
    private String ORDER_CONFIRM_EVENT;

    public NewTopic OrderConfirmEventTopic() {
        return TopicBuilder.name(ORDER_CONFIRM_EVENT)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
