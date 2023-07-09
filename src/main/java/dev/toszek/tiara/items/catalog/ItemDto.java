package dev.toszek.tiara.items.catalog;

import java.util.UUID;

public record ItemDto(UUID uuid,
                      String name,
                      String description) {
}
