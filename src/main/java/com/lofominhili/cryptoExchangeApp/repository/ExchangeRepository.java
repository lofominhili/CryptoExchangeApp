package com.lofominhili.cryptoExchangeApp.repository;

import com.lofominhili.cryptoExchangeApp.entity.ExchangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRepository extends JpaRepository<ExchangeEntity, Long> {
    Optional<ExchangeEntity> getExchangeByCurrencyName(String name);

}
