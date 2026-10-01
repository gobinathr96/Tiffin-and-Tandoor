# Tiffin & Tandoor — Food Ordering System

A full-stack food ordering website built with **Java, Spring Boot, MySQL, HTML/CSS/JavaScript and Bootstrap-style CSS** — designed to match the skills on Gobinath R's resume (Core Java, Spring Boot, REST API, MySQL, HTML/CSS/JS, Git/GitHub).

## What it does

- Customers browse a menu (grouped by category), add items to a cart, and place an order.
- A REST API (Spring Boot) handles users, menu items, cart, and orders.
- MySQL stores everything through Spring Data JPA.
- A simple HTML/CSS/JS frontend (served straight from Spring Boot) talks to the API.

## Project structure

```
food-ordering-system/
├── pom.xml
├── src/main/java/com/foodapp/
│   ├── FoodOrderingApplication.java   ← main() entry point
│   ├── model/        ← JPA entities (User, FoodItem, Cart, CartItem, Order, OrderItem)
│   ├── repository/   ← Spring Data JPA repositories
│   ├── service/       ← business logic
│   ├── controller/    ← REST endpoints (@RestController)
│   ├── dto/           ← request objects
│   ├── exception/     ← custom exceptions + global error handler
│   └── config/        ← CORS config + sample data seeder
└── src/main/resources/
    ├── application.properties   ← MySQL connection settings
    └── static/                  ← the website (index.html, css/, js/)
```

## New: mobile-number ordering, loyalty points & auto-billing

- Customers identify themselves by **mobile number** instead of email/password (`POST /api/users/phone-login`). First order on a number creates their account; every order after that finds the same account and its points.
- **Loyalty points:** each order earns 1–10 points (1 point per ₹100 paid, capped at 10). Once a customer has 100+ points on file, their *next* order gets 10% off; 200+ points gets 20% off (the redeemed points are then deducted). Thresholds and percentages are constants at the top of `OrderService.java` — change them freely.
- **Auto-generated bill:** `POST /api/orders` returns the saved order *and* a ready-to-show bill with a bill number (`BILL000123`), the order number, the customer's mobile number, itemized lines, any discount applied, and the points earned/redeemed.
- **Live kitchen notification:** the moment an order is placed, a message is pushed over WebSocket to `/topic/kitchen-orders` — any connected staff dashboard sees it instantly (no polling, no refresh).
- **Menu images:** each food item now has an `imageUrl`. The seeded demo menu uses placeholder images from placehold.co so the site looks right immediately — swap in real photos any time by editing `imageUrl` (upload photos to `src/main/resources/static/images/` and point to `/images/yourphoto.jpg`, or use free stock photos from Unsplash/Pexels).

### One-time reset needed after this update

Because the database schema changed (new columns, a unique constraint on phone number), **drop and recreate the database once** so Hibernate builds it cleanly:

```sql
DROP DATABASE food_ordering_db;
CREATE DATABASE food_ordering_db;
```

Then run the app as usual — the sample menu (with images) reseeds automatically.

## New: real food photos

Six of your menu items now use real photos (stored in `src/main/resources/static/images/`, served automatically by Spring Boot): Paneer Tikka, Chicken 65, Butter Chicken, Masala Dosa, Gulab Jamun, and Filter Coffee. Veg Biryani and Masala Chaas still use the colored placeholders — drop a photo in `static/images/` and update `DataSeeder.java` (or run a SQL `UPDATE`, see below) to add those two.

**If you already have data in your database** (from testing earlier), the seeder won't touch it — it only runs on an empty table. Update the existing rows directly instead:

```sql
UPDATE food_items SET image_url = '/images/paneer-tikka.jpg'  WHERE name = 'Paneer Tikka';
UPDATE food_items SET image_url = '/images/chicken-65.jpg'    WHERE name = 'Chicken 65';
UPDATE food_items SET image_url = '/images/butter-chicken.jpg' WHERE name = 'Butter Chicken';
UPDATE food_items SET image_url = '/images/masala-dosa.jpg'   WHERE name = 'Masala Dosa';
UPDATE food_items SET image_url = '/images/gulab-jamun.jpg'   WHERE name = 'Gulab Jamun';
UPDATE food_items SET image_url = '/images/filter-coffee.jpg' WHERE name = 'Filter Coffee';
```

## How to run it locally

1. **Install prerequisites** (if you don't have them already):
   - Java 17+ (`java -version`)
   - Maven (`mvn -version`) — or use the included wrapper once you add one
   - MySQL Server, running locally

2. **Create the database:**
   ```sql
   CREATE DATABASE food_ordering_db;
   ```

3. **Set your MySQL password** in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.password=your_mysql_password
   ```

4. **Run the app:**
   ```bash
   cd food-ordering-system
   mvn spring-boot:run
   ```

5. **Open the website:** go to `http://localhost:8080` in your browser. A demo menu is auto-loaded the first time you run it (see `DataSeeder.java`).

## Key REST endpoints (test these in Postman too — good practice!)

| Method | Endpoint                          | What it does                  |
|--------|------------------------------------|--------------------------------|
| POST   | `/api/auth/signup`                 | Create a user                  |
| POST   | `/api/auth/login`                  | Log in                         |
| GET    | `/api/food-items`                  | List all menu items            |
| GET    | `/api/food-items?category=Starters`| Filter by category             |
| POST   | `/api/food-items`                  | Add a menu item (admin)        |
| GET    | `/api/cart/{userId}`               | View a user's cart              |
| POST   | `/api/cart/{userId}/items`         | Add an item to the cart         |
| POST   | `/api/orders`                      | Place an order from the cart    |
| GET    | `/api/orders/user/{userId}`        | Order history for a user        |
| PATCH  | `/api/orders/{id}/status`          | Update order status (admin)     |

## Pushing this to GitHub

```bash
cd food-ordering-system
git init
git add .
git commit -m "Initial commit: food ordering system (Spring Boot + MySQL)"
git branch -M main
git remote add origin https://github.com/<your-username>/food-ordering-system.git
git push -u origin main
```

Then add a short description and topics (`java`, `spring-boot`, `mysql`, `rest-api`) on the GitHub repo page so it reads well to recruiters.

## Where to go next (good ways to extend this project)

- Add real authentication with **Spring Security + JWT** instead of the plain-text demo login.
- Hash passwords with `BCryptPasswordEncoder`.
- Add pagination to `/api/food-items` once the menu grows.
- Add an **admin dashboard** page (separate HTML page) to manage the menu and order statuses.
- Write unit tests for the service layer with **JUnit + Mockito**.
- Containerize it with **Docker** (a `Dockerfile` + `docker-compose.yml` for the app and MySQL together) — ties directly into the Docker basics on your resume.
- Deploy it for free on **Render** or **Railway** so you have a live demo link for your resume.
