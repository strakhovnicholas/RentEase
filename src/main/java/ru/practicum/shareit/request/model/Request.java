package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.entity.User;

import javax.print.attribute.standard.MediaSize;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
public class Request {
    @Id
    private Long id;
    @Column(name = "description", nullable = false, length = 100)
    private String description;
    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;
    @Column(name = "created")
    private LocalDateTime createdAt;
}
