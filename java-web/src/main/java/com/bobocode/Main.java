package com.bobocode;

import com.bobocode.context.ApplicationContext;
import com.bobocode.context.impl.AnnotationConfigApplicationContext;
import com.bobocode.context.service.MessageService;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.bobocode.context");
        MessageService messageService = context.getBean(MessageService.class);
        messageService.print();
    }
}