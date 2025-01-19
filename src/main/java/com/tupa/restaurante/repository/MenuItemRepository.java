package com.tupa.restaurante.repository;

import com.tupa.restaurante.entities.menu.MenuItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MenuItemRepository extends MongoRepository<MenuItem, String> {
    List<MenuItem> findByRolesIn(List<String> roles);
}
