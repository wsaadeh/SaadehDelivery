### Microservices Project:

<ul>
<li>Eureka: 8671</li>
<li>Kafka: 8084</li>
<li>delivery-tracking service: 8080</li>
<li>courier-management: 8082</li>
<li>gateway: 9999</li>
</ul>

<ul>Steps to implement Kafka:
  <li>Considere to extends your entity using AbstractAggregateRoot&lt;Entity&gt;</li>
  <li>include dependencies on pom.xml: 
	<pre><code class="language-xml">
    		&lt;dependency&gt;
			&lt;groupId&gt;org.springframework.kafka&lt;/groupId&gt;
			&lt;artifactId&gt;spring-kafka&lt;/artifactId&gt;
		&lt;/dependency&gt;
		&lt;dependency&gt;
			&lt;groupId&gt;org.springframework.kafka&lt;/groupId&gt;
			&lt;artifactId&gt;spring-kafka-test&lt;/artifactId&gt;
			&lt;scope&gt;test&lt;/scope&gt;
		&lt;/dependency&gt;
	</code></pre>
  </li>
  <li> include settings on application.yml or properties: 
        <li>Microservice who will be the Producer[Delivery-tracking]:
         <pre><code class="language-yaml">
          kafka:
            producer:
              bootstrap-servers: localhost:9092
              key-serializer: org.apache.kafka.common.serialization.StringSerializer
              value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
              properties:
                spring.json.add.type.headers: true
                spring.json.type.mapping:
                  delivery-placed-event:com.saadeh.delivery.delivery.tracking.domain.event.DeliveryPlacedEvent,
                  delivery-pickup-event:com.saadeh.delivery.delivery.tracking.domain.event.DeliveryPickUpEvent,
                  delivery-fulfilled-event:com.saadeh.delivery.delivery.tracking.domain.event.DeliveryFulFilledEven       
          </code></pre> 
        </li>
        <li>Microservice who will be the Consumer[Courier-management]:
           <pre><code class="language-yaml">
            kafka:
              consumer:
                bootstrap-servers: localhost:9092
                key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
                value-deserializer: org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
                group-id: courier-management
                properties:
                  spring.deserializer.value.delegate.class: org.springframework.kafka.support.serializer.JsonDeserializer
                  spring.json.trusted.packages: '*'
                  spring.json.type.mapping:
                    delivery-placed-event:com.saadeh.delivery.courier.management.infrastructure.event.DeliveryPlacedIntegrationEvent,
                    delivery-pickup-event:com.fasterxml.jackson.databind.JsonNode,
                    delivery-fulfilled-event:com.saadeh.delivery.courier.management.infrastructure.event.DeliveryFulfilledIntegrationEvent          
          </code></pre>
        </li>
        <li>Docker-compose.yml who will instantiate the following services: Postgresql, Pgadmin, Kafka and KafkaUI
          <pre><code class="language-yaml">
            services:
              postgres:
                image: postgres:17.5
                environment:
                  POSTGRES_USER: postgres
                  POSTGRES_PASSWORD: postgres
                volumes:
                  - postgres-data:/var/lib/postgresql/data
                ports:
                  - 5434:5432
                networks:
                  - local-network
              pgadmin:
                image: dpage/pgadmin4:9.5
                depends_on:
                  - postgres
                environment:
                  PGADMIN_DEFAULT_EMAIL: wilson.saadeh@gmail.com
                  PGADMIN_DEFAULT_PASSWORD: saadehdelivery
                ports:
                  - 8083:80
                volumes:
                  - pgadmin-data:/var/lib/pgadmin
                networks:
                  - local-network
              kafka:
                image: bitnami/kafka:4.0
                ports:
                  - 9092:9092
                environment:
                  KAFKA_CFG_NODE_ID: 0
                  KAFKA_CFG_PROCESS_ROLES: controller,broker
                  KAFKA_CFG_LISTENERS: PLAINTEXT://:9090,CONTROLLER://:9091,EXTERNAL://:9092
                  KAFKA_CFG_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9090,EXTERNAL://localhost:9092
                  KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP: CONTROLLER:PLAINTEXT,EXTERNAL:PLAINTEXT,PLAINTEXT:PLAINTEXT
                  KAFKA_CFG_CONTROLLER_QUORUM_VOTERS: 0@kafka:9091
                  KAFKA_CFG_CONTROLLER_LISTENER_NAMES: CONTROLLER
                volumes:
                  - kafka-data:/bitnami/kafka
                networks:
                  - local-network
              kafka-ui:
                image: provectuslabs/kafka-ui:v0.7.2
                ports:
                  - 8084:8080
                environment:
                  KAFKA_CLUSTERS_0_NAME: saadehdelivery-kafka-cluster
                  KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:9090
                networks:
                  - local-network
            volumes:
              kafka-data:
              postgres-data:
              pgadmin-data:
            networks:
              local-network:
                driver: bridge
          </code></pre>
        </li>
  </li>
  <li>Create classes to represent events on the producer: 
  <pre><code class="language-java">
  package com.saadeh.delivery.delivery.tracking.domain.event;

    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.ToString;
    
    import java.time.OffsetDateTime;
    import java.util.UUID;
    
    @Getter
    @AllArgsConstructor
    @ToString
    public class DeliveryFulFilledEvent {
        private final OffsetDateTime occurredAt;
        private final UUID deliveryId;
    }
  </code></pre>
  </li>
  <li>Create classes to represent events on the consumer: 
  <pre><code class="language-java">
package com.saadeh.delivery.courier.management.infrastructure.event;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class DeliveryFulfilledIntegrationEvent {
    private OffsetDateTime occurredAt;
    private UUID deliveryId;
}
  </code></pre>
  </li>  
  <li>Create event handler to deal with each event:
    <pre><code class="language-java">
      package com.saadeh.delivery.delivery.tracking.infrastructure.event;
      
      import com.saadeh.delivery.delivery.tracking.domain.event.DeliveryFulFilledEvent;
      import com.saadeh.delivery.delivery.tracking.domain.event.DeliveryPickUpEvent;
      import com.saadeh.delivery.delivery.tracking.domain.event.DeliveryPlacedEvent;
      import lombok.RequiredArgsConstructor;
      import lombok.extern.slf4j.Slf4j;
      import org.springframework.context.event.EventListener;
      import org.springframework.stereotype.Component;
      
      import static com.saadeh.delivery.delivery.tracking.infrastructure.kafka.kafkaTopicConfig.deliveryEventsTopicName;
      
      @Component
      @Slf4j
      @RequiredArgsConstructor
      public class DeliveryDomainEventHandler {
      
          private final IntegrationEventPublisher integrationEventPublisher;
      
          @EventListener
          public void handle(DeliveryPlacedEvent event){
              log.info(event.toString());
              integrationEventPublisher.publish(event, event.getDeliveryId().toString(),deliveryEventsTopicName);
          }
      
          @EventListener
          public void handle(DeliveryPickUpEvent event){
              log.info(event.toString());
              integrationEventPublisher.publish(event, event.getDeliveryId().toString(),deliveryEventsTopicName);
          }
      
          @EventListener
          public void handle(DeliveryFulFilledEvent event){
              log.info(event.toString());
              integrationEventPublisher.publish(event, event.getDeliveryId().toString(),deliveryEventsTopicName);
          }
      
      }
      
  </code></pre>  
  </li>
  <li>Configure a Topic on Kafka
    <pre><code class=language-java>
      package com.saadeh.delivery.delivery.tracking.infrastructure.kafka;
      
      import org.apache.kafka.clients.admin.NewTopic;
      import org.springframework.context.annotation.Bean;
      import org.springframework.context.annotation.Configuration;
      import org.springframework.kafka.config.TopicBuilder;
      
      @Configuration
      public class kafkaTopicConfig {
      
          public static final String deliveryEventsTopicName = "deliveries.v1.events";
      
          @Bean
          public NewTopic deliveryEventsTopic(){
              return TopicBuilder.name(deliveryEventsTopicName)
                      .partitions(3)
                      .replicas(1)
                      .build();
          }
      }
  </code></pre>    
  </li>
  <li>Create a Interface to represent the publisher</li>
  <li>Implement this interface to publishs the message using Kafka: 
<pre><code class=language-java>
      package com.saadeh.delivery.delivery.tracking.infrastructure.kafka;
    
    import com.saadeh.delivery.delivery.tracking.infrastructure.event.IntegrationEventPublisher;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.apache.kafka.clients.producer.RecordMetadata;
    import org.springframework.kafka.core.KafkaTemplate;
    import org.springframework.kafka.support.KafkaHeaders;
    import org.springframework.kafka.support.SendResult;
    import org.springframework.messaging.Message;
    import org.springframework.messaging.support.MessageBuilder;
    import org.springframework.stereotype.Component;
    
    @Component
    @RequiredArgsConstructor
    @Slf4j
    public class IntegrationEventPublisherKafkaImpl implements IntegrationEventPublisher {
    
    
        private final KafkaTemplate<String, Object> kafkaTemplate;
    
        @Override
        public void publish(Object event, String key, String topic) {
            // Dynamically generate __TypeId__ from event class name (mapped in consumer)
            String typeId = event.getClass().getSimpleName(); // e.g., DeliveryPlacedEvent
    
            Message<Object> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader(KafkaHeaders.KEY, key)
                    .setHeader("__TypeId__", typeId)
                    .build();
    
            SendResult<String, Object> result = kafkaTemplate.send(message).join();
    
            //SendResult<String, Object> result = kafkaTemplate.send(topic, key, event).join();
            RecordMetadata recordMetadata = result.getRecordMetadata();
            log.info("Message publish: \n\t Topic: {} \n\t Offset:{}", recordMetadata.topic(), recordMetadata.offset());
        }
    }
 </code></pre>
  </li>
  <li>Create a class to deal with messages from kafka.
<pre><code class=language-java>
package com.saadeh.delivery.courier.management.infrastructure.kafka;

import com.saadeh.delivery.courier.management.domain.services.CourierDeliveryService;
import com.saadeh.delivery.courier.management.infrastructure.event.DeliveryFulfilledIntegrationEvent;
import com.saadeh.delivery.courier.management.infrastructure.event.DeliveryPlacedIntegrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {
        "deliveries.v1.events"
}, groupId = "courier-management")
@Slf4j
@RequiredArgsConstructor
public class KafkaDeliveriesMessageHandler {

    private final CourierDeliveryService courierDeliveryService;

    @KafkaHandler(isDefault = true)
    public void defaultHandler(@Payload Object object,
                               @Headers MessageHeaders headers) {
        log.info("Default Handler: {}", object.getClass());
        log.info("Payload content: {}", object);
        log.info("Headers: {}", headers);
    }

    @KafkaHandler
    public void handle(@Payload DeliveryPlacedIntegrationEvent event) {
        log.info("Received: {} ", event);
        courierDeliveryService.assign(event.getDeliveryId());
    }

    @KafkaHandler
    public void handle(@Payload DeliveryFulfilledIntegrationEvent event) {
        log.info("Received: {} ", event);
        courierDeliveryService.fullfill(event.getDeliveryId());
    }

}
  
</code></pre>
    
  </li>
</ul>
 

        
  
  
  


