package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(idSequence.incrementAndGet());
        }
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public Collection<Item> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Collection<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description) {
        String nameLower = name != null ? name.toLowerCase() : "";
        String descLower = description != null ? description.toLowerCase() : "";

        return storage.values().stream()
                .filter(item -> {
                    String itemName = item.getName() != null ? item.getName().toLowerCase() : "";
                    String itemDesc = item.getDescription() != null ? item.getDescription().toLowerCase() : "";
                    return itemName.contains(nameLower) || itemDesc.contains(descLower);
                })
                .collect(Collectors.toList());
    }
}

