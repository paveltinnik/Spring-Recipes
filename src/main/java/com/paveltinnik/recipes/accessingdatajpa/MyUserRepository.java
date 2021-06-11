package com.paveltinnik.recipes.accessingdatajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import com.paveltinnik.recipes.models.MyUser;

import java.util.Optional;

public interface MyUserRepository extends JpaRepository<MyUser, Long> {
    Optional<MyUser> findMyUserByEmail(String email);
}
