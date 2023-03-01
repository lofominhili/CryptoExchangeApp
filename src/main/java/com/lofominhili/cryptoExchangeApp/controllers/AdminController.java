package com.lofominhili.cryptoExchangeApp.controllers;

import com.lofominhili.cryptoExchangeApp.dto.ExceptionDto;
import com.lofominhili.cryptoExchangeApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
public class AdminController {
    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/change_exchange")
    public ResponseEntity<Map<String, String>> changeExchange(@RequestBody Map<String, String> value) {
        return ResponseEntity.ok(userService.changeExchange(value));
    }

    @GetMapping(value = "/all_value")
    public ResponseEntity<Map<String, String>> calcAllCurrencyValue(@RequestBody Map<String, String> value) {
        return ResponseEntity.ok(userService.calcAllCurrencyValue(value));
    }

    @GetMapping(value = "/amount_operations")
    public ResponseEntity<Map<String, String>> getOperationsAmount(@RequestBody Map<String, String> value) {
        return ResponseEntity.ok(userService.getOperationsAmount(value));
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ExceptionDto handleException(RuntimeException runtimeException) {
        ExceptionDto exceptionDto = new ExceptionDto();
        exceptionDto.setMessage(runtimeException.getMessage());
        exceptionDto.setTimestamp(System.currentTimeMillis());
        return exceptionDto;
    }
}
