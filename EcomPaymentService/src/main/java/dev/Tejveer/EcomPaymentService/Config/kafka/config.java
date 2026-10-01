package dev.Tejveer.EcomPaymentService.Config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Declares the Kafka topics this service depends on.
 *
 * Spring Boot auto-configures a KafkaAdmin bean once spring-kafka is on the
 * classpath and spring.kafka.bootstrap-servers is set. That KafkaAdmin scans
 * the application context for any bean of type NewTopic and, on startup,
 * asks the broker to create it if it doesn't already exist. This class exists
 * purely to register those NewTopic beans — nothing here talks to Kafka
 * directly.
 *
 * The broker has auto.create.topics.enable=false (see docker-compose.yml),
 * so a topic that isn't explicitly created — either here or manually via the
 * CLI — simply won't exist, and produce/consume calls against it will fail.
 * This is intentional: partition count and replication factor should be a
 * deliberate choice per topic, not an accidental default.
 *
 * Payment Service owns this topic because it owns the event: it's the
 * service that decides what "a payment was confirmed" means and when it
 * happened. Order Service only consumes from it and doesn't need its own
 * copy of this bean, though declaring it there too would be harmless
 * (topic creation is idempotent — "already exists" is not an error).
 */
@Configuration
public class config {

    @Value("${app.kafka.topic.order-status-events}")
    private String ORDER_EVENT_TOPIC;

    @Bean
    public NewTopic orderStatusEventsTopic() {
        return TopicBuilder.name(ORDER_EVENT_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
