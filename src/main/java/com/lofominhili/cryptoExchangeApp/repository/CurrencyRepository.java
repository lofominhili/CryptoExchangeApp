package com.lofominhili.cryptoExchangeApp.repository;

import com.lofominhili.cryptoExchangeApp.entity.CurrencyEntity;
import com.lofominhili.cryptoExchangeApp.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, Long> {
    Optional<CurrencyEntity> getCurrencyEntityByUserAndName(UserEntity userEntity, String name);
}
