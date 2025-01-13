package com.mindhub.todolist.config;

import com.mindhub.todolist.models.EntityUser;
import com.mindhub.todolist.repositories.EntityUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private EntityUserRepository entityUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EntityUser userEntity = entityUserRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new User(userEntity.getEmail(), userEntity.getPassword(), AuthorityUtils.createAuthorityList
                (userEntity.getRole().toString()));
    }
}
