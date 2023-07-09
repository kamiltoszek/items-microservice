package dev.toszek.tiara.items.catalog.internal.mapper;

import dev.toszek.tiara.items.catalog.CreateItemCommand;
import dev.toszek.tiara.items.catalog.ItemDto;
import dev.toszek.tiara.items.catalog.internal.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ItemMapper {
    ItemDto toDto(Item item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    Item fromCreateCommand(CreateItemCommand createItemCommand, UUID uuid);
}
