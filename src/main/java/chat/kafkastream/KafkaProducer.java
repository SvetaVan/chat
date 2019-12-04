package chat.kafkastream;

public interface KafkaProducer {
    void produce(String message);
}
