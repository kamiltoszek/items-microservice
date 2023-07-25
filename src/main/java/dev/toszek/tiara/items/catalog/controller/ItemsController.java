package dev.toszek.tiara.items.catalog.controller;

import dev.toszek.tiara.items.catalog.ItemCatalogApi;
import dev.toszek.tiara.items.catalog.command.CreateItemsCommand;
import dev.toszek.tiara.items.catalog.command.ItemFetchFilter;
import dev.toszek.tiara.items.catalog.command.SaveItemCommand;
import dev.toszek.tiara.items.catalog.dto.ItemDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/items")
@RequiredArgsConstructor
@SecurityScheme(
        name = "X-Api-Key",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "X-Api-Key"
)
class ItemsController {
    private final ItemCatalogApi itemCatalogApi;

    @PostMapping
    @CacheEvict(value = "items", allEntries = true) // Clear cache on delete or update
    @Operation(
            summary = "Creates item in item database",
            security = @SecurityRequirement(name = "X-Api-Key")
    )
    public ResponseEntity<ItemDto> createItem(@RequestBody @Valid SaveItemCommand saveItemCommand) {
        ItemDto createdItem = itemCatalogApi.createItem(saveItemCommand);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    @CacheEvict(value = "items", allEntries = true) // Clear cache on delete or update
    @Transactional // wrapping bulk item creation in one transaction
    @Operation(
            summary = "Creates items in item database, max items allowed at once = 50",
            security = @SecurityRequirement(name = "X-Api-Key")
    )
    public ResponseEntity<List<ItemDto>> createItems(@RequestBody @Valid CreateItemsCommand createItemsCommand) {
        List<ItemDto> savedItems = new ArrayList<>();
        createItemsCommand.items().forEach(createItemCommand -> savedItems.add(itemCatalogApi.createItem(createItemCommand)));
        return new ResponseEntity<>(savedItems, HttpStatus.CREATED);
    }

    @Cacheable("items") // Cache the result of getItemById
    @GetMapping("/{itemUuid}")
    @Operation(
            summary = "Return item by uuid",
            security = @SecurityRequirement(name = "X-Api-Key")
    )
    public ResponseEntity<ItemDto> getItemById(@PathVariable("itemUuid") UUID itemUuid) {
        return itemCatalogApi.findById(itemUuid)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Cacheable("items") // Cache the result of getAllItems
    @GetMapping
    @Operation(
            summary = "Return items based on search criteria",
            security = @SecurityRequirement(name = "X-Api-Key")
    )
    public ResponseEntity<Page<ItemDto>> getAllItems(@ParameterObject Pageable pageable,
                                                     @RequestParam(required = false) String name,
                                                     @RequestParam(required = false) String description,
                                                     @RequestParam(required = false) BigDecimal price,
                                                     @RequestParam(required = false) BigDecimal lessThenPrice,
                                                     @RequestParam(required = false) BigDecimal greaterThenPrice) {
        final Page<ItemDto> itemsPage = itemCatalogApi.findAllPageable(pageable, new ItemFetchFilter(name, description, price, lessThenPrice, greaterThenPrice));
        return new ResponseEntity<>(itemsPage, HttpStatus.OK);
    }

    @CacheEvict(value = "items", allEntries = true) // Clear cache on delete or update
    @DeleteMapping("/{itemUuid}")
    @Operation(
            summary = "Removes item from database if exists",
            security = @SecurityRequirement(name = "X-Api-Key")
    )
    public ResponseEntity<Void> deleteItem(@PathVariable("itemUuid") UUID itemUuid) {
        itemCatalogApi.deleteItem(itemUuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @CacheEvict(value = "items", allEntries = true) // Clear cache on delete or update
    @PutMapping("/{itemUuid}")
    @Operation(
            summary = "Updates item in database, by replacing item values with provided ones.",
            security = @SecurityRequirement(name = "X-Api-Key")
    )
    public ResponseEntity<ItemDto> updateItem(@PathVariable("itemUuid") UUID itemUuid, @RequestBody @Valid SaveItemCommand updatedItem) {
        return new ResponseEntity<>(itemCatalogApi.updateItem(itemUuid, updatedItem), HttpStatus.OK);
    }
}
