package dev.toszek.tiara.items.catalog.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateItemCommand(@NotBlank String name,
                                @NotNull String description) {
}
