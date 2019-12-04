package chat.kafkastream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class KafkaProducerImpl implements KafkaProducer {
    private static final Logger LOGGER = Logger.getLogger(KafkaProducerImpl.class.getName());


    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void produce(String message) {
        LOGGER.info("Sent :: " + message);
        kafkaTemplate.send(new GenericMessage<>(message));
    }

}
