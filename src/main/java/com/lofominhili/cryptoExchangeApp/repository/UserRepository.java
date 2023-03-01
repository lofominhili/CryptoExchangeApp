package com.lofominhili.cryptoExchangeApp.repository;

import com.lofominhili.cryptoExchangeApp.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity getBySecretKey(String key);
}
