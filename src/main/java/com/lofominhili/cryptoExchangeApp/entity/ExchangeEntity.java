package com.lofominhili.cryptoExchangeApp.entity;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "_exchange")
public class ExchangeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = true)
    private String currencyName;

    @OneToMany(mappedBy = "exchange", cascade = CascadeType.REMOVE)
    private List<CurrencyPriceEntity> currencyPriceEntities;

    public ExchangeEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public List<CurrencyPriceEntity> getCurrencyPriceEntities() {
        return currencyPriceEntities;
    }

    public void setCurrencyPriceEntities(List<CurrencyPriceEntity> currencyPriceEntities) {
        this.currencyPriceEntities = currencyPriceEntities;
    }
}