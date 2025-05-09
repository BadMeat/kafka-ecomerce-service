package com.zarta.inventory.repository;

import com.zarta.inventory.model.MasterItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MasterItemRepository extends JpaRepository<MasterItem, Long> {
    Optional<MasterItem> findByKode(String kode);
}
