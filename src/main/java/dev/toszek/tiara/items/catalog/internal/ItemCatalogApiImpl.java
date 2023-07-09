package dev.toszek.tiara.items.catalog.internal;

import dev.toszek.tiara.items.catalog.ItemCatalogApi;
import dev.toszek.tiara.items.catalog.command.ItemFetchFilter;
import dev.toszek.tiara.items.catalog.command.SaveItemCommand;
import dev.toszek.tiara.items.catalog.dto.ItemDto;
import dev.toszek.tiara.items.catalog.exception.ItemNotFoundException;
import dev.toszek.tiara.items.catalog.internal.entity.Item;
import dev.toszek.tiara.items.catalog.internal.entity.Item_;
import dev.toszek.tiara.items.catalog.internal.mapper.ItemMapper;
import dev.toszek.tiara.items.catalog.internal.repository.ItemRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
class ItemCatalogApiImpl implements ItemCatalogApi {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public Optional<ItemDto> findById(final UUID itemUuid) {
        return itemRepository.findItemByUuid(itemUuid)
                .map(itemMapper::toDto);
    }

    @Override
    public Page<ItemDto> findAllPageable(final Pageable pageable, final ItemFetchFilter filter) {
        return itemRepository.findAll((root, query, criteriaBuilder) -> {
                    List<Predicate> filters = new ArrayList<>();
                    if (filter.name() != null) {
                        filters.add(criteriaBuilder.like(root.get(Item_.name), filter.name()));
                    }
                    if (filter.description() != null) {
                        filters.add(criteriaBuilder.like(root.get(Item_.description), filter.description()));
                    }
                    if (filter.price() != null) {
                        filters.add(criteriaBuilder.equal(root.get(Item_.price), filter.price()));
                    }
                    if (filter.lessThenPrice() != null) {
                        filters.add(criteriaBuilder.lessThan(root.get(Item_.price), filter.lessThenPrice()));
                    }
                    if (filter.greaterThanPrice() != null) {
                        filters.add(criteriaBuilder.greaterThan(root.get(Item_.price), filter.greaterThanPrice()));
                    }
                    return criteriaBuilder.and(filters.toArray(new Predicate[0]));
                }, pageable)
                .map(itemMapper::toDto);
    }

    @Override
    public ItemDto createItem(@Valid final SaveItemCommand saveItemCommand) {
        // saveItemCommand.selfValidate();
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
    public ItemDto updateItem(@Valid @NotNull final UUID itemUuid, @Valid final SaveItemCommand updatedItem) {
        Item item = itemRepository.findItemByUuid(itemUuid).orElseThrow(ItemNotFoundException::new);
        itemMapper.updateItem(updatedItem, item);
        item = itemRepository.save(item);
        return itemMapper.toDto(item);
    }
}
