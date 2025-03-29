package com.example.htttdl.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.htttdl.FirebaseCrud.Notification;

@RestController
public class te {
    public Object f() {
        Notification.pushData();
        return null;
    }
}
