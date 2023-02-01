package ru.netology.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @GeneratedValue
    @Id
    private int id;
    private String login;
    private String passwordHash;
    private String authToken;

}
