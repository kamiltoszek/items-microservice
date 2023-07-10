package dev.toszek.tiara.items.catalog.internal;

import dev.toszek.tiara.items.catalog.ItemCatalogApi;
import dev.toszek.tiara.items.catalog.command.ItemFetchFilter;
import dev.toszek.tiara.items.catalog.command.SaveItemCommand;
import dev.toszek.tiara.items.catalog.dto.ItemDto;
import dev.toszek.tiara.items.catalog.exception.ItemNotFoundException;
import dev.toszek.tiara.items.catalog.internal.entity.Item;
import dev.toszek.tiara.items.catalog.internal.mapper.ItemMapper;
import dev.toszek.tiara.items.catalog.internal.mapper.ItemMapperImpl;
import dev.toszek.tiara.items.catalog.internal.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemCatalogApiTest {

    private ItemCatalogApi itemCatalogApi;
    private ItemRepository itemRepository;
    private ItemMapper itemMapper = new ItemMapperImpl();

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        itemCatalogApi = new ItemCatalogApiImpl(itemRepository, itemMapper);
    }

    @Test
    void testFindByIdWithExistingItem() {
        // Mocking behavior
        UUID itemUuid = UUID.randomUUID();
        Item item = new Item(itemUuid, "Test Item", "Description", BigDecimal.TEN);
        when(itemRepository.findItemByUuid(itemUuid)).thenReturn(Optional.of(item));

        // Test the method
        Optional<ItemDto> result = itemCatalogApi.findById(itemUuid);

        // Verify the result
        assertThat(result)
                .hasValue(new ItemDto(itemUuid, item.getName(), item.getDescription(), item.getPrice(), null, null));

        // Verify the interaction with the mock
        verify(itemRepository, times(1)).findItemByUuid(itemUuid);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void testFindByIdWithNotExistingItem() {
        // Mocking behavior
        UUID itemUuid = UUID.randomUUID();
        when(itemRepository.findItemByUuid(itemUuid)).thenReturn(Optional.empty());

        // Test the method
        Optional<ItemDto> result = itemCatalogApi.findById(itemUuid);

        // Verify the result
        assertThat(result)
                .isEmpty();

        // Verify the interaction with the mock
        verify(itemRepository, times(1)).findItemByUuid(itemUuid);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void testFindAllPageable() {
        // Mocking behavior
        Item item1 = new Item(UUID.randomUUID(), "Test Item1", "Description", BigDecimal.TEN);
        Item item2 = new Item(UUID.randomUUID(), "Test Item2", "Description", BigDecimal.TEN);

        Pageable pageable = PageRequest.of(0, 10);
        ItemFetchFilter filter = new ItemFetchFilter(null, null, null, null, null);
        Page<Item> pageResult = new PageImpl<>(List.of(item1, item2));
        when(itemRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageResult);

        // Test the method
        Page<ItemDto> result = itemCatalogApi.findAllPageable(pageable, filter);

        // Verify the result
        assertThat(result)
                .isNotNull()
                .containsExactlyInAnyOrder(itemMapper.toDto(item1), itemMapper.toDto(item2));

        // Verify the interaction with the mock
        verify(itemRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void testCreateItem() {
        final UUID uuid = UUID.randomUUID();
        // Mocking behavior
        SaveItemCommand saveItemCommand = new SaveItemCommand("Item", "Description", BigDecimal.TEN);
        when(itemRepository.save(any(Item.class))).thenAnswer(invocationOnMock -> {
            final Item item = invocationOnMock.getArgument(0);
            item.setUuid(uuid);
            item.setCreationDate(Instant.now());
            item.setLastModifiedDate(Instant.now());
            return item;
        });

        // Test the method
        ItemDto result = itemCatalogApi.createItem(saveItemCommand);

        // Verify the result
        assertThat(result)
                .isNotNull()
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("uuid", uuid);

        // Verify the interaction with the mock
        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void testDeleteItem() {
        // Mocking behavior
        UUID itemUuid = UUID.randomUUID();

        // Test the method
        itemCatalogApi.deleteItem(itemUuid);

        // Verify the interaction with the mock
        verify(itemRepository, times(1)).deleteItemByUuid(itemUuid);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void testUpdateNotExistingItem() {
        // Mocking behavior
        UUID itemUuid = UUID.randomUUID();
        SaveItemCommand updatedItem = new SaveItemCommand("Updated Item", "description", BigDecimal.ONE);
        when(itemRepository.findItemByUuid(itemUuid)).thenReturn(Optional.empty());

        // Test the method and verify result
        assertThatExceptionOfType(ItemNotFoundException.class)
                .isThrownBy(() -> itemCatalogApi.updateItem(itemUuid, updatedItem));

        // Verify the interaction with the mock
        verify(itemRepository, times(1)).findItemByUuid(itemUuid);
        verify(itemRepository, times(0)).save(any());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void testUpdateExistingItem() {
        // Mocking behavior
        UUID itemUuid = UUID.randomUUID();
        Item item1 = new Item(itemUuid, "Test Item1", "Description", BigDecimal.TEN);
        SaveItemCommand updatedItem = new SaveItemCommand("Updated Item", "description", BigDecimal.ONE);
        when(itemRepository.findItemByUuid(itemUuid)).thenReturn(Optional.of(item1));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        // Test the method and verify result
        assertThatNoException()
                .isThrownBy(() -> {
                    final ItemDto result = itemCatalogApi.updateItem(itemUuid, updatedItem);
                    assertThat(result)
                            .isNotNull()
                            .hasFieldOrPropertyWithValue("name", "Updated Item");
                });

        // Verify the interaction with the mock
        verify(itemRepository, times(1)).findItemByUuid(itemUuid);
        verify(itemRepository, times(1)).save(any());
        verifyNoMoreInteractions(itemRepository);
    }
}
