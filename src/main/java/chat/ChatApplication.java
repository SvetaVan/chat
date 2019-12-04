package chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class ChatApplication {

  public static void main(String[] args) {
    try {
      // Нет времени сделать нормальный health check для kafkа, возвращающий успех, только после создания output-topic
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    SpringApplication.run(ChatApplication.class, args);
  }
}
