package com.sanjeev.rest.concurrencyservice.repository;

import com.sanjeev.rest.concurrencyservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
