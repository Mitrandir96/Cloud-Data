package ru.netology.entities;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "files")
public class File {
    @Id
    private UUID id;
    private String name;
    private String hash;
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
