package com.acme.coffeshop;

public final class Product {
    private final int id;
    private final String name;
    private final String description;
    private final int priceCents;

    public Product(int id, String name, String description, int priceCents) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priceCents = priceCents;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPriceCents() {
        return priceCents;
    }
}
