package com.lofominhili.cryptoExchangeApp.repository;

import com.lofominhili.cryptoExchangeApp.entity.CurrencyPriceEntity;
import com.lofominhili.cryptoExchangeApp.entity.ExchangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyPriceRepository extends JpaRepository<CurrencyPriceEntity, Long> {
    Optional<CurrencyPriceEntity> getCurrencyPriceEntityByExchangeAndName(ExchangeEntity exchange, String name);
}
