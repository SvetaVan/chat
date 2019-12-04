package chat;

import com.example.chat.ChatMessage;
import com.example.chat.ChatMessageFromServer;
import com.example.chat.ChatStreamServiceGrpc;
import com.example.chat.MessageType;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ChatClient {
    private static final Logger LOGGER = Logger.getLogger(ChatClient.class.getName());

    public static void main(String[] args) {

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 4000)
                .usePlaintext()
                .build();

        ChatStreamServiceGrpc.ChatStreamServiceStub chatStreamServiceStub =
                ChatStreamServiceGrpc.newStub(managedChannel);


        final Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter your name: ");
        String name = scanner.nextLine();

        StreamObserver<ChatMessage> chat = chatStreamServiceStub.chat(new StreamObserver<ChatMessageFromServer>() {
            @Override
            public void onNext(ChatMessageFromServer value) {
                System.out.println("Message " + value.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                LOGGER.log(Level.SEVERE, "Disconnected", t);
            }

            @Override
            public void onCompleted() {

                System.out.println("Disconnected");
            }
        });

        chat.onNext(ChatMessage.newBuilder().setUsername(name).setType(MessageType.JOIN).build());

        while (true) {

            System.out.println("Your message: ");
            String message = scanner.nextLine();

            chat.onNext(ChatMessage.newBuilder()
                    .setUsername(name)
                    .setMessage(message)
                    .setType(MessageType.TEXT)
                    .build());
            System.out.println();

        }


    }
}
