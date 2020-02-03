package com.sanjeev.rest.concurrencyservice.service;

import com.sanjeev.rest.concurrencyservice.domain.User;
import com.sanjeev.rest.concurrencyservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Async
    public CompletableFuture<List<User>> saveUsers(MultipartFile file) {
        Instant start = Instant.now();
        List<User> usersToSave = parseCsvFile(file);
        log.info("saving list of users of size() {} with thread: {}", usersToSave.size(),
                Thread.currentThread().getName());
        List<User> users = userRepository.saveAll(usersToSave);
        Instant end = Instant.now();
        long timeElapsed = Duration.between(start, end).toMillis();
        log.info("timeElapsed : {}", timeElapsed);
        return CompletableFuture.completedFuture(users);
    }

    @Async
    @Override
    public CompletableFuture<List<User>> getUsers() {
        log.info("Find list of users with thread {}", Thread.currentThread().getName());
        List<User> users = userRepository.findAll();
        return CompletableFuture.completedFuture(users);
    }

    private List<User> parseCsvFile(MultipartFile file) {
        List<User> users = new ArrayList<>();
        try {
            try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] data = line.split(",");
                    User user = User.builder()
                            .name(data[0])
                            .email(data[1])
                            .gender(data[2])
                            .build();
                    users.add(user);
                }
            }
        } catch (IOException e) {
            log.error("Problem while reading the file");
        }
        return users;
    }
}
