package com.lofominhili.cryptoExchangeApp.service;

import com.lofominhili.cryptoExchangeApp.dto.User;
import com.lofominhili.cryptoExchangeApp.entity.CurrencyEntity;
import com.lofominhili.cryptoExchangeApp.entity.CurrencyPriceEntity;
import com.lofominhili.cryptoExchangeApp.entity.ExchangeEntity;
import com.lofominhili.cryptoExchangeApp.entity.UserEntity;
import com.lofominhili.cryptoExchangeApp.repository.CurrencyPriceRepository;
import com.lofominhili.cryptoExchangeApp.repository.CurrencyRepository;
import com.lofominhili.cryptoExchangeApp.repository.ExchangeRepository;
import com.lofominhili.cryptoExchangeApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final GeneratorKeyService generatorKeyService;
    private final CurrencyRepository currencyRepository;
    private final ExchangeRepository exchangeRepository;
    private final CurrencyPriceRepository currencyPriceRepository;

    @Autowired
    public UserService(UserRepository userRepository, GeneratorKeyService generatorKeyService, CurrencyRepository currencyRepository, ExchangeRepository exchangeRepository, CurrencyPriceRepository currencyPriceRepository) {
        this.userRepository = userRepository;
        this.generatorKeyService = generatorKeyService;
        this.currencyRepository = currencyRepository;
        this.exchangeRepository = exchangeRepository;
        this.currencyPriceRepository = currencyPriceRepository;
    }

    public String registrate(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(user.getUsername());
        userEntity.setEmail(user.getEmail());
        String key = generatorKeyService.generate();
        userEntity.setSecretKey(key);
        userRepository.save(userEntity);
        return key;
    }

    public Map<String, String> getBalance(Map<String, String> value) {
        String key = value.get("secret_key");
        UserEntity user = userRepository.getBySecretKey(key);
        Map<String, String> result = new HashMap<>();
        user.getCurrencies().forEach(c -> result.put(c.getName(), String.valueOf(c.getAmount())));
        return result;
    }

    public Map<String, String> makeDeposite(Map<String, String> value) {
        UserEntity user = userRepository.getBySecretKey(value.get("secret_key"));
        Map<String, String> result = new HashMap<>();
        value.forEach((k, v) -> {
            if (!k.equals("secret_key")) {
                Optional<CurrencyEntity> optional = currencyRepository.getCurrencyEntityByUserAndName(user, k);
                CurrencyEntity currency;
                if (optional.isPresent()) {
                    currency = optional.get();
                    currency.setAmount(currency.getAmount() + Double.parseDouble(v));
                } else {
                    currency = new CurrencyEntity();
                    currency.setName(k);
                    currency.setAmount(Double.parseDouble(v));
                    currency.setUser(user);
                }
                currencyRepository.save(currency);
                result.put(currency.getName(), String.valueOf(currency.getAmount()));
            }
        });
        makeOperation(user);
        return result;
    }

    public Map<String, String> makeWithDraw(Map<String, String> value) {
        UserEntity user = userRepository.getBySecretKey(value.get("secret_key"));
        String currency = value.get("currency");
        double count = Double.parseDouble(value.get("count"));
        Optional<CurrencyEntity> optional = currencyRepository.getCurrencyEntityByUserAndName(user, currency + "_wallet");
        if (optional.isPresent()) {
            if (optional.get().getAmount() >= count) {
                CurrencyEntity currencyEntity = optional.get();
                currencyEntity.setAmount(currencyEntity.getAmount() - count);
            } else
                throw new RuntimeException("Balance is too low");
        } else
            throw new RuntimeException("There is no such an currency");
        currencyRepository.save(optional.get());

        makeOperation(user);

        Map<String, String> result = new HashMap<>();
        result.put(optional.get().getName(), String.valueOf(optional.get().getAmount()));
        return result;
    }

    public Map<String, String> getAllExchanges(Map<String, String> value) {
        String key = value.get("secret_key");
        String currency = value.get("currency");
        Optional<ExchangeEntity> exchange = exchangeRepository.getExchangeByCurrencyName(currency);
        if (exchange.isEmpty()) throw new RuntimeException("There is no such an exchange");
        Map<String, String> result = new HashMap<>();
        exchange.get().getCurrencyPriceEntities().forEach(cp -> result.put(cp.getName(), String.valueOf(cp.getPrice())));
        return result;
    }

    public Map<String, String> makeExchange(Map<String, String> value) {
        UserEntity user = userRepository.getBySecretKey(value.get("secret_key"));
        String to = value.get("currency_to");
        String from = value.get("currency_from");
        String amount = value.get("amount");
        Optional<ExchangeEntity> exchange = exchangeRepository.getExchangeByCurrencyName(from);
        if (exchange.isEmpty()) throw new RuntimeException("There is no such an exchange");
        Optional<CurrencyPriceEntity> currencyPrice = currencyPriceRepository.getCurrencyPriceEntityByExchangeAndName(exchange.get(), to);
        if (currencyPrice.isEmpty()) throw new RuntimeException("The price for this currency is not set");
        makeWithDraw(Map.of("secret_key", user.getSecretKey(), "currency", from, "count", amount));
        Double amountTo = currencyPrice.get().getPrice() * Double.parseDouble(amount);
        makeDeposite(Map.of("secret_key", user.getSecretKey(), to + "_wallet", String.valueOf(amountTo)));

        makeOperation(user);

        return Map.of("currency_from", from, "currency_to", to, "amount_from", amount, "amount_to", String.valueOf(amountTo));
    }

    public Map<String, String> changeExchange(Map<String, String> value) {
        String key = value.get("secret_key");
        String baseCurrency = value.get("base_currency");
        ExchangeEntity exchange = exchangeRepository.getExchangeByCurrencyName(baseCurrency).orElseGet(() -> {
            ExchangeEntity exchangeEntity = new ExchangeEntity();
            exchangeEntity.setCurrencyName(baseCurrency);
            exchangeEntity.setCurrencyPriceEntities(new ArrayList<>());
            exchangeRepository.save(exchangeEntity);
            return exchangeEntity;
        });
        value.forEach((k, v) -> {
            if (!k.equals("secret_key") && !k.equals("base_currency")) {
                Optional<CurrencyPriceEntity> optional = currencyPriceRepository.getCurrencyPriceEntityByExchangeAndName(exchange, k);
                CurrencyPriceEntity priceEntity;
                if (optional.isPresent()) {
                    priceEntity = optional.get();
                    priceEntity.setPrice(Double.parseDouble(v));
                } else {
                    priceEntity = new CurrencyPriceEntity();
                    priceEntity.setName(k);
                    priceEntity.setPrice(Double.parseDouble(v));
                    priceEntity.setExchange(exchange);
                    exchange.getCurrencyPriceEntities().add(priceEntity);
                }
                currencyPriceRepository.save(priceEntity);
            }
        });

        Map<String, String> result = new HashMap<>();
        exchange.getCurrencyPriceEntities().forEach(cp -> result.put(cp.getName(), String.valueOf(cp.getPrice())));
        return result;
    }

    public Map<String, String> calcAllCurrencyValue(Map<String, String> value) {
        String key = value.get("secret_key");
        String currency = value.get("currency");
        Double sum = currencyRepository
                .findAll()
                .stream()
                .filter(c -> c.getName().equals(currency + "_wallet"))
                .mapToDouble(CurrencyEntity::getAmount).sum();
        return Map.of(currency, String.valueOf(sum));
    }

    public Map<String, String> getOperationsAmount(Map<String, String> value) {
        String key = value.get("secret_key");
        long dateFrom = parseDate(value.get("date_from")).getTime();
        long dateTo = parseDate(value.get("date_to")).getTime();
        long amount = userRepository
                .findAll()
                .stream()
                .filter(us -> us.getLastOperationDate() != null)
                .mapToLong(u -> u.getLastOperationDate().getTime())
                .filter(time -> dateFrom <= time && dateTo >= time).count();
        return Map.of("transaction_count", String.valueOf(amount));
    }

    private Date parseDate(String string) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date;
        try {
            date = dateFormat.parse(string);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date;
    }

    private void makeOperation(UserEntity user) {
        user.setLastOperationDate(new Date(System.currentTimeMillis()));
        userRepository.save(user);
    }
}
