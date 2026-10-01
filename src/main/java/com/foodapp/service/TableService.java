package com.foodapp.service;

import com.foodapp.exception.ResourceNotFoundException;
import com.foodapp.model.DiningTable;
import com.foodapp.repository.DiningTableRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableService {

    private final DiningTableRepository diningTableRepository;

    public TableService(DiningTableRepository diningTableRepository) {
        this.diningTableRepository = diningTableRepository;
    }

    public List<DiningTable> getAll() {
        return diningTableRepository.findAllByOrderByTableNumberAsc();
    }

    public DiningTable getById(Long id) {
        return diningTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id " + id));
    }

    public DiningTable book(Long id, String name, String phone) {
        DiningTable table = getById(id);
        if (table.getStatus() == DiningTable.TableStatus.BOOKED) {
            throw new IllegalStateException("Table " + table.getTableNumber() + " is already booked");
        }
        table.setStatus(DiningTable.TableStatus.BOOKED);
        table.setBookedByName(name);
        table.setBookedByPhone(phone);
        return diningTableRepository.save(table);
    }

    public DiningTable release(Long id) {
        DiningTable table = getById(id);
        table.setStatus(DiningTable.TableStatus.AVAILABLE);
        table.setBookedByName(null);
        table.setBookedByPhone(null);
        return diningTableRepository.save(table);
    }
}
