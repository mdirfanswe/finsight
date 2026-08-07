package com.cashlens.expensetracker.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cashlens.expensetracker.models.UserModel;
import com.cashlens.expensetracker.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping()
    public ResponseEntity<UserModel> getUserDetails() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel user = userService.getByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isDeleted = userService.delete(username);
        if (isDeleted) {
            return new ResponseEntity<>("Account deleted successfully!", HttpStatus.OK);
        }
        return new ResponseEntity<>("No account found!", HttpStatus.BAD_REQUEST);
    }

}
