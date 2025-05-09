package io.github.enessaidtatli.model;

/**
 * @author etatli on 9.05.2025 15:24
 */
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private MessageType type;
    private String content;
    private String sender;
    private String time;
    private String roomId;
    private String receiver;  // birebir sohbet için alıcı

    public enum MessageType {
        CHAT, JOIN, LEAVE, PRIVATE
    }
}