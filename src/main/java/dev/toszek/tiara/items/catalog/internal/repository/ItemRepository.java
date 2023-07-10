package dev.toszek.tiara.items.catalog.internal.repository;

import dev.toszek.tiara.items.catalog.internal.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {
    Optional<Item> findItemByUuid(UUID uuid);

    <T> Optional<T> findItemByUuid(UUID uuid, Class<T> type);

    void deleteItemByUuid(UUID uuid);
}
