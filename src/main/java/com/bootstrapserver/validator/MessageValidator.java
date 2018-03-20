/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bootstrapserver.validator;

import message.Message;

import java.util.ArrayList;

/**
 * @author Mevan
 */
public class MessageValidator {

    private static MessageValidator messageValidator;

    private MessageValidator() {
    }

    public static MessageValidator getMessageValidator() {
        if (messageValidator == null) {
            synchronized (MessageValidator.class) {
                messageValidator = new MessageValidator();
            }
        }
        return messageValidator;
    }

    public boolean validate(Message message) {
        ArrayList<String> msgTitles = new ArrayList<String>() {{
            add("Login");
            add("Register");
            add("PWChange");
            add("Logout");
            add("HeartBeatMessage");
        }};

        boolean valid = true;
        if (msgTitles.contains(message.getTitle())) {
            if (portNumberValidator(message.getSenderPort()) && portNumberValidator(message.getReceiverPort())
                    && ipAddressValidator(message.getReceiverAddress()) && ipAddressValidator(message.getSenderAddress())) {
                if (!(String.valueOf(message.getTimestamp()).matches("[0-9]+"))) {
                    valid = false;
                }
            } else {
                valid = false;
            }
        } else {
            valid = false;
        }
        System.out.println(valid);
        return valid;
    }

    public boolean portNumberValidator(int port) {
        boolean valid = false;
        if ((port > 1024) && (port < 65536)) {
            valid = true;
        }
        System.out.println("port" + valid);
        return valid;
    }

    public boolean ipAddressValidator(String ipAdd) {
        boolean valid = true;
        if (!(ipAdd.matches("[0-9.]+") && (ipAdd.length() > 0 && ipAdd.length() < 16))) {
            valid = false;
        }
        System.out.println("ip" + valid);
        return valid;
    }
}
