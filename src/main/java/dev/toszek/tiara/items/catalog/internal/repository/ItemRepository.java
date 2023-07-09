package dev.toszek.tiara.items.catalog.internal.repository;

import dev.toszek.tiara.items.catalog.internal.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findItemByUuid(UUID uuid);
}
