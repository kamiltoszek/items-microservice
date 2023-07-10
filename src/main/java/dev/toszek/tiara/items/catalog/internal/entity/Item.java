package dev.toszek.tiara.items.catalog.internal.entity;

import dev.toszek.tiara.shared.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Item extends BaseEntity {
    private UUID uuid;
    private String name;
    private String description;
    private BigDecimal price;
}
