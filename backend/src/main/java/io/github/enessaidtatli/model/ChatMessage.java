package io.github.enessaidtatli.model;

/**
 * @author etatli on 9.05.2025 15:24
 */
import io.github.enessaidtatli.config.audit.Audit;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity(name = "chat_message")
@Table(name = "t_chat_message", indexes = {
        @Index(name = "idx_chat_sender_id", columnList = "sender_id"),
        @Index(name = "idx_chat_receiver_id", columnList = "receiver_id")
})
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ChatMessage extends Audit {

    @OneToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @OneToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    // Is this necessary to hold room_id in the t_chat_messages ?
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false, referencedColumnName = "id") // try this -> referencedColumnName = "id"
    private Room room;

    @Column(nullable = false)
    @Size(min = 1, max = 256)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    private LocalDateTime time = LocalDateTime.now();

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        PRIVATE
    }

}