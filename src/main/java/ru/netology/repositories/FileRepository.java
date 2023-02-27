package ru.netology.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.entities.File;
import ru.netology.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
    Optional<File> findFileByNameAndUser(String filename, User user);

    List<File> findAllByUser(User user, Pageable pageable);

}
