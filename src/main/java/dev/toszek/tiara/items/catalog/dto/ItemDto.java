package dev.toszek.tiara.items.catalog.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ItemDto(UUID uuid,
                      String name,
                      String description,
                      BigDecimal price,
                      Instant creationDate,
                      Instant lastModifiedDate) {
}
