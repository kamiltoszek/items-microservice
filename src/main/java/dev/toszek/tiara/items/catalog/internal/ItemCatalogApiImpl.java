package dev.toszek.tiara.items.catalog.internal;

import dev.toszek.tiara.items.catalog.ItemCatalogApi;
import dev.toszek.tiara.items.catalog.command.CreateItemCommand;
import dev.toszek.tiara.items.catalog.dto.ItemDto;
import dev.toszek.tiara.items.catalog.internal.entity.Item;
import dev.toszek.tiara.items.catalog.internal.mapper.ItemMapper;
import dev.toszek.tiara.items.catalog.internal.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class ItemCatalogApiImpl implements ItemCatalogApi {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public Optional<ItemDto> findById(final UUID itemUuid) {
        return itemRepository.findItemByUuid(itemUuid)
                .map(itemMapper::toDto);
    }

    @Override
    public Page<ItemDto> findAllPageable(final Pageable pageable) {
        return itemRepository.findAll(pageable)
                .map(itemMapper::toDto);
    }

    @Override
    public ItemDto createItem(final CreateItemCommand createItemCommand) {
        Item newItem = itemMapper.fromCreateCommand(createItemCommand, UUID.randomUUID());
        newItem = itemRepository.save(newItem);
        return itemMapper.toDto(newItem);
    }
}
