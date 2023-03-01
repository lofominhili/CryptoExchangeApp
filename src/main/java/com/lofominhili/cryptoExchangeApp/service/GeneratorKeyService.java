package com.lofominhili.cryptoExchangeApp.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class GeneratorKeyService {

    public String generate() {
        StringBuilder key = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i <= 34; i++)
            key.append((char) ('a' + random.nextInt(0, 25)));
        return key.toString();
    }
}
