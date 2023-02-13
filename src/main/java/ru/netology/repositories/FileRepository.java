package ru.netology.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.entities.File;

import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {

}
