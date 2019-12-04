package chat.kafkastream;

import chat.grpc.ChatStreamService;
import com.example.chat.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class KafkaConsumer {
    private static final Logger LOGGER = Logger.getLogger(KafkaConsumer.class.getName());

    @Autowired
    private ChatStreamService grpcService;

    @KafkaListener(topics = "${kafka.topic.output}")
    public void consume(String message) {
        LOGGER.info(String.format("Consumed :: %s", message));
        grpcService.makeChatMessageFromServer(ChatMessage.newBuilder()
                .setMessage(message)
                .build());
    }

}
