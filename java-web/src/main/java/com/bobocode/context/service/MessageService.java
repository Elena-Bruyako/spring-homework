package com.bobocode.context.service;

import com.bobocode.context.annotation.Bean;

@Bean
public class MessageService {

    public void print() {
        System.out.println("Print message!");
    }
}
