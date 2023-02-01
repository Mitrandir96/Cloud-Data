package ru.netology.entities;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class File {
    @GeneratedValue
    @Id
    private UUID id;
    private String name;
    private String hash;
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
