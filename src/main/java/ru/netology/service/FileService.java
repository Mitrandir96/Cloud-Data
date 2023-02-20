package ru.netology.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.entities.File;
import ru.netology.repositories.FileRepository;
import ru.netology.repositories.UserRepository;

import javax.security.auth.message.AuthException;
import java.io.IOException;
import java.util.NoSuchElementException;
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
        if (hash.isEmpty() || hash.isBlank()) {
            throw new IllegalArgumentException("hash can't be empty");
        }
        if (hash == null) {
            throw new IllegalArgumentException("hash can't be null");
        }
        currentFile.setHash(hash);
        if (filename.isEmpty() || filename.isBlank()) {
            throw new IllegalArgumentException("filename is empty");
        }
        if (filename == null) {
            throw new IllegalArgumentException("filename can't be null");
        }
        var optionalFile = fileRepository.findFileByNameAndUser(filename, user);
        if (optionalFile.isPresent()) {
            throw new IllegalArgumentException("file with provided filename already exists");
        }
        currentFile.setName(filename);
        if (file == null) {
            throw new IllegalArgumentException("file can't be null");
        }
        if (file.isEmpty()) {
            throw new IllegalArgumentException("file can't be empty");
        }
        try {
            currentFile.setContent(file.getBytes());
        } catch (IOException e) {
            throw new IOException("can't get file bytes");
        }
        fileRepository.save(currentFile);
    }

    public void deleteFile(String authToken, String filename) throws AuthException {
        var optionalUser = userRepository.findUserByAuthToken(authToken);
        if (optionalUser.isEmpty()) {
            throw new AuthException("user with provided auth token not found");
        }
        var user = optionalUser.get();
        if (filename.isEmpty() || filename.isBlank()) {
            throw new IllegalArgumentException("filename is empty");
        }
        if (filename == null) {
            throw new IllegalArgumentException("filename can't be null");
        }
        var optionalFile = fileRepository.findFileByNameAndUser(filename, user);
        if (optionalFile.isEmpty()) {
            throw new NoSuchElementException("file with provided filename not found");
        }
        var file = optionalFile.get();
        fileRepository.delete(file);
    }

    public ResponseEntity<MultiValueMap<String, Object>> getFile(String authToken, String filename) throws AuthException {
        var optionalUser = userRepository.findUserByAuthToken(authToken);
        if (optionalUser.isEmpty()) {
            throw new AuthException("user with provided auth token not found");
        }
        var user = optionalUser.get();
        if (filename.isEmpty() || filename.isBlank()) {
            throw new IllegalArgumentException("filename is empty");
        }
        if (filename == null) {
            throw new IllegalArgumentException("filename can't be null");
        }
        var optionalFile = fileRepository.findFileByNameAndUser(filename, user);
        if (optionalFile.isEmpty()) {
            throw new NoSuchElementException("file with provided filename not found");
        }
        var file = optionalFile.get();
        var formData = new LinkedMultiValueMap<String, Object>();
        formData.add("hash", file.getHash());
        formData.add("file", file.getContent());
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.MULTIPART_FORM_DATA).body(formData);
    }
}
