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
    private byte[] content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
