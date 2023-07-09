package dev.toszek.tiara.items.catalog.controller;

import dev.toszek.tiara.items.catalog.ItemCatalogApi;
import dev.toszek.tiara.items.catalog.command.CreateItemCommand;
import dev.toszek.tiara.items.catalog.command.CreateItemsCommand;
import dev.toszek.tiara.items.catalog.dto.ItemDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/items")
@RequiredArgsConstructor
class ItemsController {
    private final ItemCatalogApi itemCatalogApi;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestBody @Valid CreateItemCommand createItemCommand) {
        ItemDto createdItem = itemCatalogApi.createItem(createItemCommand);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    @Transactional // wrapping bulk item creation in one transaction
    public ResponseEntity<List<ItemDto>> createItems(@RequestBody @Valid CreateItemsCommand createItemsCommand) {
        List<ItemDto> savedItems = new ArrayList<>();
        createItemsCommand.items().forEach(createItemCommand -> savedItems.add(itemCatalogApi.createItem(createItemCommand)));
        return new ResponseEntity<>(savedItems, HttpStatus.CREATED);
    }

    @Cacheable("items") // Cache the result of getItemById
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable("itemId") UUID itemUuid) {
        return itemCatalogApi.findById(itemUuid)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Cacheable("items") // Cache the result of getAllItems
    @GetMapping
    public ResponseEntity<Page<ItemDto>> getAllItems(@ParameterObject Pageable pageable) {
        final Page<ItemDto> itemsPage = itemCatalogApi.findAllPageable(pageable);
        return new ResponseEntity<>(itemsPage, HttpStatus.OK);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<List<String>> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<String> errors = new ArrayList<>();
        bindingResult.getFieldErrors().forEach(error -> {
            String errorMessage = error.getField() + ": " + error.getDefaultMessage();
            errors.add(errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
