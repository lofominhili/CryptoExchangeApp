package com.lofominhili.cryptoExchangeApp.controllers;

import com.lofominhili.cryptoExchangeApp.dto.ExceptionDto;
import com.lofominhili.cryptoExchangeApp.dto.User;
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
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/new")
    public ResponseEntity<Map<String, String>> addNewUser(@RequestBody User user) {
        String key = userService.registrate(user);
        return ResponseEntity.ok(Map.of("secret_key", key));
    }

    @GetMapping(value = "/balance")
    public ResponseEntity<Map<String, String>> getBalance(@RequestBody Map<String, String> value) {
        return ResponseEntity.ok(userService.getBalance(value));
    }

    @PostMapping(value = "/deposit")
    public ResponseEntity<Map<String, String>> makeDeposit(@RequestBody Map<String, String> value) {
        return ResponseEntity.ok(userService.makeDeposite(value));
    }

    @PostMapping(value = "/withdraw")
    public ResponseEntity<Map<String, String>> makeWithDraw(@RequestBody Map<String, String> value) {
        return ResponseEntity.ok(userService.makeWithDraw(value));
    }

    @GetMapping(value = "/current_exchange")
    public ResponseEntity<Map<String, String>> getAllExchanges(@RequestBody Map<String, String> value) {
        return ResponseEntity.ok(userService.getAllExchanges(value));
    }

    @PostMapping(value = "/make_exchange")
    public ResponseEntity<Map<String, String>> makeExchange(@RequestBody Map<String, String> value) {
        return ResponseEntity.ok(userService.makeExchange(value));
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ExceptionDto> handleException(RuntimeException runtimeException) {
        ExceptionDto exceptionDto = new ExceptionDto();
        exceptionDto.setMessage(runtimeException.getMessage());
        exceptionDto.setTimestamp(System.currentTimeMillis());
        return ResponseEntity.badRequest().body(exceptionDto);
    }
}
