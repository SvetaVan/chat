package chat.grpc;

import chat.kafkastream.KafkaProducer;
import com.example.chat.ChatMessage;
import com.example.chat.ChatMessageFromServer;
import com.example.chat.ChatStreamServiceGrpc;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@GRpcService
public class ChatStreamServiceImpl extends ChatStreamServiceGrpc.ChatStreamServiceImplBase implements ChatStreamService {

    private static final Object MOCK = new Object();

    @Autowired
    private KafkaProducer kafkaProducer;

    private static final Logger LOGGER = Logger.getLogger(ChatStreamServiceImpl.class.getName());
    private Map<StreamObserver<ChatMessageFromServer>, Object> roomObservers = new ConcurrentHashMap<>();


    private void removeObserverFromRoom(StreamObserver<ChatMessageFromServer> responseObserver) {
        roomObservers.remove(responseObserver);
        }

    @Override
    public StreamObserver<ChatMessage> chat(StreamObserver<ChatMessageFromServer> responseObserver) {


        return new StreamObserver<ChatMessage>() {
            @Override
            public void onNext(ChatMessage chatMessage) {

                switch (chatMessage.getType()) {
                    case JOIN:
                        roomObservers.put(responseObserver, MOCK);
                        break;
                    case LEAVE:
                        roomObservers.remove(responseObserver, MOCK);
                        break;
                    case TEXT:
                        if (!roomObservers.containsKey(responseObserver)) {
                            responseObserver.onError(
                                    Status.PERMISSION_DENIED.withDescription("You are not in the room ")
                                            .asRuntimeException());
                            return;
                        }

                        kafkaProducer.produce(chatMessage.getMessage());
                        break;
                }
            }

            @Override
            public void onError(Throwable throwable) {
                LOGGER.log(Level.SEVERE, "gRPC error", throwable);
                removeObserverFromRoom(responseObserver);
            }

            @Override
            public void onCompleted() {
                removeObserverFromRoom(responseObserver);
            }
        };
    }

    @Override
    public ChatMessageFromServer makeChatMessageFromServer(ChatMessage chatMessage){

        Timestamp now = Timestamp.newBuilder()
                .setSeconds(new Date().getTime()).build();
        ChatMessageFromServer messageFromServer =
                ChatMessageFromServer.newBuilder()
                        .setType(chatMessage.getType())
                        .setTimestamp(now)
                        .setMessage(chatMessage.getUsername() +": "+chatMessage.getMessage())
                        .build();
        roomObservers.keySet().forEach(o -> o.onNext(messageFromServer));
        return messageFromServer;
    }


}
