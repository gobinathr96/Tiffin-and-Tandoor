package com.foodapp.controller;

import com.foodapp.dto.TableBookingRequest;
import com.foodapp.model.DiningTable;
import com.foodapp.service.TableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public List<DiningTable> getAll() {
        return tableService.getAll();
    }

    @PostMapping("/{id}/book")
    public ResponseEntity<DiningTable> book(@PathVariable Long id, @RequestBody TableBookingRequest request) {
        return ResponseEntity.ok(tableService.book(id, request.getName(), request.getPhone()));
    }

    @PostMapping("/{id}/release")
    public ResponseEntity<DiningTable> release(@PathVariable Long id) {
        return ResponseEntity.ok(tableService.release(id));
    }
}
