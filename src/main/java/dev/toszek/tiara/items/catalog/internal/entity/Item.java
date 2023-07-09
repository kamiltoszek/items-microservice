package dev.toszek.tiara.items.catalog.internal.entity;

import dev.toszek.tiara.shared.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Item extends BaseEntity {
    private UUID uuid;
    private String name;
    private String description;
    @Version
    private int version;
}
