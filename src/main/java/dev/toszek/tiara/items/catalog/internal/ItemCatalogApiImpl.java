package dev.toszek.tiara.items.catalog.internal;

import dev.toszek.tiara.items.catalog.ItemCatalogApi;
import dev.toszek.tiara.items.catalog.command.SaveItemCommand;
import dev.toszek.tiara.items.catalog.dto.ItemDto;
import dev.toszek.tiara.items.catalog.exception.ItemNotFoundException;
import dev.toszek.tiara.items.catalog.internal.entity.Item;
import dev.toszek.tiara.items.catalog.internal.mapper.ItemMapper;
import dev.toszek.tiara.items.catalog.internal.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ItemDto createItem(final SaveItemCommand saveItemCommand) {
        Item newItem = itemMapper.fromCreateCommand(saveItemCommand, UUID.randomUUID());
        newItem = itemRepository.save(newItem);
        return itemMapper.toDto(newItem);
    }

    @Override
    @Transactional
    public void deleteItem(final UUID itemUuid) {
        itemRepository.deleteItemByUuid(itemUuid);
    }

    @Override
    public ItemDto updateItem(final UUID itemUuid, final SaveItemCommand updatedItem) {
        Item item = itemRepository.findItemByUuid(itemUuid).orElseThrow(ItemNotFoundException::new);
        itemMapper.updateItem(updatedItem, item);
        item = itemRepository.save(item);
        return itemMapper.toDto(item);
    }
}
