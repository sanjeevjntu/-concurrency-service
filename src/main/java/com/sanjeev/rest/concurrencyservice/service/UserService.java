package com.sanjeev.rest.concurrencyservice.service;

import com.sanjeev.rest.concurrencyservice.domain.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserService {

    CompletableFuture<List<User>> saveUsers(MultipartFile file);

    CompletableFuture<List<User>> getUsers();
}
