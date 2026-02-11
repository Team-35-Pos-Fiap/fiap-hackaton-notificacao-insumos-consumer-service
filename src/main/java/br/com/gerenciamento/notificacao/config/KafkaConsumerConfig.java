package br.com.gerenciamento.notificacao.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import br.com.gerenciamento.notificacao.dto.NotificaoDto;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

        @Value(value = "${spring.kafka.bootstrap-servers}")
        private String bootstrapAddress;

        @Bean
        public ConsumerFactory<String, NotificaoDto> notificacaoConsumerFactory() {
                Map<String, Object> props = new HashMap<>();
        
                props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
                props.put(ConsumerConfig.GROUP_ID_CONFIG,"insumos");
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
                props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, JsonDeserializer.class);
                props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
                props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
                props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "br.com.gerenciamento.notificacao.dto.NotificaoDto");
        
                return new DefaultKafkaConsumerFactory<>(props);
        }

        @SuppressWarnings("null")
        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, NotificaoDto> emailKafkaListenerContainerFactory() {
                ConcurrentKafkaListenerContainerFactory<String, NotificaoDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
                
                factory.setConsumerFactory(notificacaoConsumerFactory());
                
                return factory;
        }
}
