package ru.netology.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.entities.File;
import ru.netology.repositories.FileRepository;
import ru.netology.repositories.UserRepository;

import javax.security.auth.login.LoginException;
import javax.security.auth.message.AuthException;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
@Service
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public FileService(FileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    public void uploadFile(String authToken, String hash, MultipartFile file, String filename) throws IOException, AuthException {
        var currentFile = new File();
        var optionalUser = userRepository.findUserByAuthToken(authToken);
        if (!optionalUser.isPresent()) {
            throw new AuthException("user with provided auth token not found");
        }
        var user = optionalUser.get();
        currentFile.setUser(user);
        currentFile.setId(UUID.randomUUID());
        currentFile.setHash(hash);
        currentFile.setName(filename);
        currentFile.setContent(file.getBytes());
        fileRepository.save(currentFile);
    }
}
