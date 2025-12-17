package com.example.auctionist.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class LogNotifier implements Notifier {
    @Override public void notify(String message) {
        System.out.println("[DEV NOTIFY] " + message);
    }
}
