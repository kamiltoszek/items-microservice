package dev.toszek.tiara.items.catalog;

import dev.toszek.tiara.items.catalog.command.CreateItemCommand;
import dev.toszek.tiara.items.catalog.dto.ItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ItemCatalogApi {
    Optional<ItemDto> findById(UUID itemUuid);

    Page<ItemDto> findAllPageable(Pageable pageable);

    ItemDto createItem(CreateItemCommand createItemCommand);
}