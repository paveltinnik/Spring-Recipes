package com.paveltinnik.recipes.accessingdatajpa;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.paveltinnik.recipes.models.MyUser;

import java.util.Optional;

@Service
public class MyUserServiceImpl {
    MyUserRepository myUserRepository;
    PasswordEncoder passwordEncoder;

    @Autowired
    public MyUserServiceImpl(MyUserRepository myUserRepository, PasswordEncoder passwordEncoder) {
        this.myUserRepository = myUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void addUser(MyUser myUser) {
        myUser.setPassword(passwordEncoder.encode(myUser.getPassword()));
        myUserRepository.save(myUser);
    }

    public MyUser getUser(String email) {
        Optional<MyUser> user = myUserRepository.findMyUserByEmail(email);
        return user.orElse(null);
    }
}