package ru.netology.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;
    private String login;
    private String passwordHash;
    private String authToken;

    @OneToMany(mappedBy = "user")
    private Set<File> files;

}
