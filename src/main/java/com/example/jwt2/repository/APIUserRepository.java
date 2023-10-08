package com.example.jwt2.repository;

import com.example.jwt2.domain.APIUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface APIUserRepository extends JpaRepository<APIUser, String> {
    Optional<APIUser> findByMid(String mid);

    List<APIUser> findAllByRole(String role);
}
