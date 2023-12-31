package dev.toszek.tiara.items.catalog;

import dev.toszek.tiara.items.catalog.command.ItemFetchFilter;
import dev.toszek.tiara.items.catalog.command.SaveItemCommand;
import dev.toszek.tiara.items.catalog.dto.ItemDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ItemCatalogApi {
    Optional<ItemDto> findById(UUID itemUuid);

    Page<ItemDto> findAllPageable(Pageable pageable, ItemFetchFilter filter);

    ItemDto createItem(@Valid SaveItemCommand saveItemCommand);

    void deleteItem(UUID itemUuid);

    ItemDto updateItem(@Valid @NotNull UUID itemUuid, @Valid SaveItemCommand updatedItem);
}
