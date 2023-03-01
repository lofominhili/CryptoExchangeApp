package com.lofominhili.cryptoExchangeApp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "currency_price")
public class CurrencyPriceEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = false)
    private String name;

    @Column(nullable = false, unique = false)
    private Double price;

    @ManyToOne
    private ExchangeEntity exchange;


    public CurrencyPriceEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ExchangeEntity getExchange() {
        return exchange;
    }

    public void setExchange(ExchangeEntity exchange) {
        this.exchange = exchange;
    }
}
