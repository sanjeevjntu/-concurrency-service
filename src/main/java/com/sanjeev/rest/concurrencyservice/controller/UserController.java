package com.sanjeev.rest.concurrencyservice.controller;

import com.sanjeev.rest.concurrencyservice.domain.User;
import com.sanjeev.rest.concurrencyservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveUser(@RequestParam MultipartFile[] files) {
        for (MultipartFile file : files) {
            userService.saveUsers(file);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * It took 20 seconds for 144k records in h2 db
     */
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<List<User>>> getUsers() {
        return userService.getUsers()
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping(value = "/usersWithThreads", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> getUsersWithThreads() throws ExecutionException, InterruptedException {
        CompletableFuture<List<User>> task1 = userService.getUsers();
        CompletableFuture<List<User>> task2 = userService.getUsers();
        CompletableFuture<List<User>> task3 = userService.getUsers();
        CompletableFuture<List<User>> task4 = userService.getUsers();

        List<User> userList = Stream.of(task1, task2, task3, task4)
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        log.info("Total number of records to client: {}", userList.size());

        return ResponseEntity.ok(userList);
    }

}
