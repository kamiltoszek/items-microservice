package dev.toszek.tiara.items.catalog.command;

import java.math.BigDecimal;

public record ItemFetchFilter(String name,
                              String description,
                              BigDecimal price,
                              BigDecimal lessThenPrice,
                              BigDecimal greaterThanPrice) {
}
