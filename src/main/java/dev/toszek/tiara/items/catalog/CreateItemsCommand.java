package dev.toszek.tiara.items.catalog;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateItemsCommand(@NotNull @Size(max = 50) @Valid List<CreateItemCommand> items) {
}
