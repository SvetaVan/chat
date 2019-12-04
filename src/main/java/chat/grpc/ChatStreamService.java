package chat.grpc;

import com.example.chat.ChatMessage;
import com.example.chat.ChatMessageFromServer;

public interface ChatStreamService {
    ChatMessageFromServer makeChatMessageFromServer(ChatMessage chatMessage);
}
