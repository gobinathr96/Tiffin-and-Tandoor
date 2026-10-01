package com.foodapp.repository;

import com.foodapp.model.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {
    List<DiningTable> findAllByOrderByTableNumberAsc();
}
