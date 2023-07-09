package dev.toszek.tiara.items.catalog.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SaveItemCommand(@NotBlank String name,
                              @NotNull String description,
                              @NotNull BigDecimal price) {
}
