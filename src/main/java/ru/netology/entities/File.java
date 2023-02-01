package ru.netology.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class File {
    @GeneratedValue
    @Id
    private UUID id;
    private String name;
    private String hash;
    private String content;

}
