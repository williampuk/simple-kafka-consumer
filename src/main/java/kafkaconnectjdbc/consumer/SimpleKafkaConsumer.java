package kafkaconnectjdbc.consumer;

import kafkaconnectjdbc.record.Transaction;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class SimpleKafkaConsumer {

    public static void main(String[] args) {
        final var props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092,localhost:29093");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group-1");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        props.put("schema.registry.url", "http://localhost:28085");
        props.put("specific.avro.reader", "true");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        final var topic = "postgres-transaction";

        try (final var consumer = new KafkaConsumer<String, Transaction>(props)) {
            consumer.subscribe(List.of(topic));
            while (true) {
                final ConsumerRecords<String, Transaction> records = consumer.poll(Duration.ofSeconds(1));
                for (final ConsumerRecord<String, Transaction> record : records) {
                    System.out.printf("offset = %d, key = %s, value = %s, amount = %s\n",
                            record.offset(), record.key(), record.value(), record.value().getAmount());
                }
            }
        }
    }
}
