package com.foodapp.config;

import com.foodapp.model.DiningTable;
import com.foodapp.model.FoodItem;
import com.foodapp.repository.DiningTableRepository;
import com.foodapp.repository.FoodItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Runs once at startup and inserts a few sample menu items and the
 * restaurant's dining tables, so the app has something to display
 * the first time you run it. Safe to delete once you add your own
 * menu / tables through the API.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final FoodItemRepository foodItemRepository;
    private final DiningTableRepository diningTableRepository;

    public DataSeeder(FoodItemRepository foodItemRepository, DiningTableRepository diningTableRepository) {
        this.foodItemRepository = foodItemRepository;
        this.diningTableRepository = diningTableRepository;
    }

    @Override
    public void run(String... args) {
        seedFoodItems();
        seedTables();
    }

    private void seedFoodItems() {
        if (foodItemRepository.count() > 0) return; // already seeded

        foodItemRepository.save(item("Paneer Tikka", "Char-grilled cottage cheese with mint chutney", 189.0, "Starters",
                "/images/paneer-tikka.jpg", false));
        foodItemRepository.save(item("Chicken 65", "Spicy, deep-fried Chennai-style chicken bites", 219.0, "Starters",
                "/images/chicken-65.jpg", true));
        foodItemRepository.save(item("Butter Chicken", "Creamy tomato curry with tandoori chicken", 289.0, "Main Course",
                "/images/butter-chicken.jpg", true));
        foodItemRepository.save(item("Veg Biryani", "Basmati rice layered with spiced vegetables", 219.0, "Main Course",
                "https://placehold.co/400x300/5F7A47/FFFFFF?text=Veg+Biryani", true));
        foodItemRepository.save(item("Masala Dosa", "Crisp rice crepe filled with spiced potato", 129.0, "Main Course",
                "/images/masala-dosa.jpg", false));
        foodItemRepository.save(item("Gulab Jamun", "Warm milk dumplings in rose-cardamom syrup", 89.0, "Desserts",
                "/images/gulab-jamun.jpg", false));
        foodItemRepository.save(item("Filter Coffee", "South Indian decoction coffee with frothed milk", 49.0, "Beverages",
                "/images/filter-coffee.jpg", false));
        foodItemRepository.save(item("Masala Chaas", "Spiced buttermilk with curry leaf tempering", 45.0, "Beverages",
                "https://placehold.co/400x300/5F7A47/FFFFFF?text=Masala+Chaas", false));
        foodItemRepository.save(item("Tandoori Chicken", "Half chicken marinated overnight, char-grilled in the tandoor", 279.0, "Main Course",
                "https://placehold.co/400x300/B23A2E/FFFFFF?text=Tandoori+Chicken", true));
        foodItemRepository.save(item("Paneer Butter Masala", "Soft paneer cubes simmered in a rich buttery tomato gravy", 239.0, "Main Course",
                "https://placehold.co/400x300/E8A33D/241B14?text=Paneer+Butter+Masala", true));
    }

    private FoodItem item(String name, String description, double price, String category, String imageUrl,
                           boolean todaySpecial) {
        FoodItem item = new FoodItem();
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setCategory(category);
        item.setImageUrl(imageUrl);
        item.setAvailable(true);
        item.setTodaySpecial(todaySpecial);
        return item;
    }

    private void seedTables() {
        if (diningTableRepository.count() > 0) return; // already seeded

        // 3 four-seater tables
        diningTableRepository.save(table("F1", DiningTable.TableType.FOUR_SEATER, 4));
        diningTableRepository.save(table("F2", DiningTable.TableType.FOUR_SEATER, 4));
        diningTableRepository.save(table("F3", DiningTable.TableType.FOUR_SEATER, 4));

        // 2 family-seater tables
        diningTableRepository.save(table("FM1", DiningTable.TableType.FAMILY, 6));
        diningTableRepository.save(table("FM2", DiningTable.TableType.FAMILY, 6));

        // 2 couple-seater tables
        diningTableRepository.save(table("C1", DiningTable.TableType.COUPLE, 2));
        diningTableRepository.save(table("C2", DiningTable.TableType.COUPLE, 2));
    }

    private DiningTable table(String tableNumber, DiningTable.TableType type, int capacity) {
        DiningTable t = new DiningTable();
        t.setTableNumber(tableNumber);
        t.setType(type);
        t.setCapacity(capacity);
        t.setStatus(DiningTable.TableStatus.AVAILABLE);
        return t;
    }
}
