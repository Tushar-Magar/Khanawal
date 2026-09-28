package com.efood.config;

import com.efood.entity.*;
import com.efood.repository.CategoryRepository;
import com.efood.repository.FoodRepository;
import com.efood.repository.OrderItemRepository;
import com.efood.repository.OrderRepository;
import com.efood.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final FoodRepository foodRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           FoodRepository foodRepository,
                           OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.foodRepository = foodRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Initialize Users
        User admin = userRepository.findByEmail("admin@efood.com").orElse(null);
        if (admin == null) {
            admin = new User(
                    "E-Food Administrator",
                    "admin@efood.com",
                    "9876543210",
                    passwordEncoder.encode("admin123"),
                    "E-Food Corporate Tower, MG Road",
                    "Bengaluru",
                    "Karnataka",
                    "560001",
                    Role.ROLE_ADMIN
            );
            userRepository.save(admin);
        }

        User customer = userRepository.findByEmail("user@efood.com").orElse(null);
        if (customer == null) {
            customer = new User(
                    "Rahul Sharma",
                    "user@efood.com",
                    "9876543211",
                    passwordEncoder.encode("user123"),
                    "Flat 402, Green Valley Apartments, Indiranagar",
                    "Bengaluru",
                    "Karnataka",
                    "560038",
                    Role.ROLE_USER
            );
            customer = userRepository.save(customer);
        }

        // 2. Initialize Categories
        if (categoryRepository.count() == 0) {
            Category pizza = categoryRepository.save(new Category(
                    "Pizza",
                    "Stone-baked hand-stretched pizzas with gooey mozzarella and fresh gourmet toppings",
                    "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=800&auto=format&fit=crop&q=80",
                    true
            ));

            Category burger = categoryRepository.save(new Category(
                    "Burger",
                    "Juicy grilled and crispy burgers layered with signature melted sauces and crisp veggies",
                    "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800&auto=format&fit=crop&q=80",
                    true
            ));

            Category indian = categoryRepository.save(new Category(
                    "Indian",
                    "Authentic aromatic biryanis, rich buttery gravies, paneer specialties, and tandoori breads",
                    "https://images.unsplash.com/photo-1589302168068-964664d93dc0?w=800&auto=format&fit=crop&q=80",
                    true
            ));

            Category chinese = categoryRepository.save(new Category(
                    "Chinese",
                    "Wok-tossed noodles, fried rice, crispy spring rolls, dumplings, and spicy manchurian",
                    "https://images.unsplash.com/photo-1541696432-82c6da8ce7bf?w=800&auto=format&fit=crop&q=80",
                    true
            ));

            Category southIndian = categoryRepository.save(new Category(
                    "South Indian",
                    "Crispy golden dosas, steamed idlis, medu vadas, piping hot sambar, and coconut chutneys",
                    "https://images.unsplash.com/photo-1630383249896-424e482df921?w=800&auto=format&fit=crop&q=80",
                    true
            ));

            Category snacks = categoryRepository.save(new Category(
                    "Snacks",
                    "Crunchy loaded fries, samosas, spicy wraps, nachos, and mouthwatering quick finger foods",
                    "https://images.unsplash.com/photo-1576107232684-1279f3908594?w=800&auto=format&fit=crop&q=80",
                    true
            ));

            Category desserts = categoryRepository.save(new Category(
                    "Desserts",
                    "Decadent warm brownies, artisanal ice creams, classic gulab jamuns, and creamy cheesecakes",
                    "https://images.unsplash.com/photo-1551024709-8f23befc6f87?w=800&auto=format&fit=crop&q=80",
                    true
            ));

            Category beverages = categoryRepository.save(new Category(
                    "Beverages",
                    "Freshly brewed coffees, chilled mojitos, fruit smoothies, thick shakes, and iced teas",
                    "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=800&auto=format&fit=crop&q=80",
                    true
            ));

            Category healthyFood = categoryRepository.save(new Category(
                    "Healthy Food",
                    "Nutrient-dense protein salad bowls, low-calorie wraps, avocado toasts, and keto platters",
                    "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800&auto=format&fit=crop&q=80",
                    true
            ));

            // 3. Initialize Foods
            List<Food> sampleFoods = new ArrayList<>();

            // Pizza
            sampleFoods.add(new Food("Margherita Pizza", "Classic Napoli crust topped with San Marzano tomato sauce, fresh basil, and creamy mozzarella.", 299.0, "https://images.unsplash.com/photo-1604382355076-af4b0eb60143?w=800&auto=format&fit=crop&q=80", pizza, 4.8, true));
            sampleFoods.add(new Food("Farmhouse Veggie Pizza", "Loaded with crisp bell peppers, red onions, black olives, sweet corn, and mushrooms.", 349.0, "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800&auto=format&fit=crop&q=80", pizza, 4.7, true));
            sampleFoods.add(new Food("Spicy Paneer Tikka Pizza", "Tandoori marinated paneer cubes with spicy paprika, green capsicum, and melted mozzarella.", 379.0, "https://images.unsplash.com/photo-1534308983496-4fabb1a015ee?w=800&auto=format&fit=crop&q=80", pizza, 4.9, true));

            // Burger
            sampleFoods.add(new Food("Classic Cheese Burger", "Juicy patty grilled to perfection with cheddar slice, pickles, caramelized onions, and secret mayo.", 179.0, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800&auto=format&fit=crop&q=80", burger, 4.6, true));
            sampleFoods.add(new Food("Crispy Paneer Supreme Burger", "Golden fried spicy paneer patty layered with fresh lettuce, tomato, and tandoori chipotle dressing.", 199.0, "https://images.unsplash.com/photo-1550547660-d9450f859349?w=800&auto=format&fit=crop&q=80", burger, 4.8, true));
            sampleFoods.add(new Food("Double Decker Smoky Burger", "Twin patties with smoked barbecue glaze, double cheddar, and crispy onion rings in a brioche bun.", 249.0, "https://images.unsplash.com/photo-1586190848861-99aa4a171e90?w=800&auto=format&fit=crop&q=80", burger, 4.7, true));

            // Indian
            sampleFoods.add(new Food("Royal Hyderabadi Dum Biryani", "Slow-cooked fragrant basmati rice infused with saffron, aromatic spices, and served with mirchi ka salan.", 329.0, "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800&auto=format&fit=crop&q=80", indian, 4.9, true));
            sampleFoods.add(new Food("Paneer Butter Masala", "Cottage cheese simmered in a velvety tomato-cashew gravy finished with fresh cream and kasuri methi.", 279.0, "https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=800&auto=format&fit=crop&q=80", indian, 4.8, true));
            sampleFoods.add(new Food("Dal Makhani & Garlic Naan Combo", "Black lentils slow-cooked overnight with churned white butter, paired with two crisp tandoori garlic naans.", 259.0, "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=800&auto=format&fit=crop&q=80", indian, 4.7, true));

            // Chinese
            sampleFoods.add(new Food("Hakka Veg Noodles", "Thin noodles tossed with crisp shredded cabbage, carrots, bell peppers, soy sauce, and scallions.", 189.0, "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=800&auto=format&fit=crop&q=80", chinese, 4.5, true));
            sampleFoods.add(new Food("Veg Manchurian Gravy", "Crispy vegetable dumplings smothered in a rich, tangy dark soy, ginger, and garlic wok sauce.", 219.0, "https://images.unsplash.com/photo-1541696432-82c6da8ce7bf?w=800&auto=format&fit=crop&q=80", chinese, 4.6, true));
            sampleFoods.add(new Food("Steamed Dim Sums (6 Pcs)", "Delicate translucent parcels filled with finely minced garden vegetables, served with spicy red chili dip.", 199.0, "https://images.unsplash.com/photo-1496116218417-1a781b1c416c?w=800&auto=format&fit=crop&q=80", chinese, 4.7, true));

            // South Indian
            sampleFoods.add(new Food("Mysore Masala Dosa", "Crispy fermented rice-lentil crepe smeared with spicy red chutney and stuffed with seasoned potato masala.", 149.0, "https://images.unsplash.com/photo-1630383249896-424e482df921?w=800&auto=format&fit=crop&q=80", southIndian, 4.8, true));
            sampleFoods.add(new Food("Idli & Vada Sambar Combo", "Two melt-in-mouth steamed idlis and one crunchy medu vada, served with traditional drumstick sambar.", 129.0, "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=800&auto=format&fit=crop&q=80", southIndian, 4.7, true));

            // Snacks
            sampleFoods.add(new Food("Peri-Peri Loaded French Fries", "Golden crinkle-cut potato fries tossed in fiery African peri-peri spices, topped with melted cheddar.", 139.0, "https://images.unsplash.com/photo-1576107232684-1279f3908594?w=800&auto=format&fit=crop&q=80", snacks, 4.6, true));
            sampleFoods.add(new Food("Crispy Samosa Platter (4 Pcs)", "Flaky golden pastry cones packed with spicy crushed potatoes and green peas, with tamarind and mint chutney.", 99.0, "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=800&auto=format&fit=crop&q=80", snacks, 4.7, true));

            // Desserts
            sampleFoods.add(new Food("Sizzling Chocolate Brownie", "Fudgy Belgian chocolate walnut brownie served warm with a scoop of Madagascar vanilla ice cream.", 169.0, "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=800&auto=format&fit=crop&q=80", desserts, 4.9, true));
            sampleFoods.add(new Food("Gulab Jamun with Rabdi", "Warm fried dumplings soaked in cardamom saffron sugar syrup, topped with chilled thickened rabdi.", 149.0, "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=800&auto=format&fit=crop&q=80", desserts, 4.8, true));

            // Beverages
            sampleFoods.add(new Food("Mint Virgin Mojito", "Muddled fresh garden mint leaves, lime wedges, crushed ice, and sparkling soda.", 119.0, "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=800&auto=format&fit=crop&q=80", beverages, 4.6, true));
            sampleFoods.add(new Food("Cold Brew Hazelnut Frappe", "Chilled arabica coffee blended with roasted hazelnut syrup, whole milk, and whipped cream topping.", 149.0, "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=800&auto=format&fit=crop&q=80", beverages, 4.7, true));

            // Healthy Food
            sampleFoods.add(new Food("Avocado & Quinoa Protein Bowl", "Fresh Hass avocado slices, cooked white quinoa, cherry tomatoes, edamame, and lemon-tahini dressing.", 289.0, "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800&auto=format&fit=crop&q=80", healthyFood, 4.8, true));

            for (Food food : sampleFoods) {
                foodRepository.save(food);
            }

            // 4. Create an initial sample order for customer so dashboard charts have data out of the box
            if (orderRepository.count() == 0 && customer != null) {
                Food firstFood = sampleFoods.get(0); // Margherita Pizza
                Food secondFood = sampleFoods.get(3); // Classic Cheese Burger

                Order demoOrder = new Order();
                demoOrder.setOrderNumber("EF202609250001");
                demoOrder.setUser(customer);
                demoOrder.setOrderDate(LocalDateTime.now().minusHours(4));
                demoOrder.setSubtotal(478.0);
                demoOrder.setDeliveryFee(40.0);
                demoOrder.setTax(23.90);
                demoOrder.setTotalAmount(541.90);
                demoOrder.setDeliveryName(customer.getFullName());
                demoOrder.setDeliveryMobile(customer.getMobile());
                demoOrder.setDeliveryAddress(customer.getAddress());
                demoOrder.setCity(customer.getCity());
                demoOrder.setState(customer.getState());
                demoOrder.setPincode(customer.getPincode());
                demoOrder.setPaymentMethod("COD");
                demoOrder.setPaymentStatus("PAID");
                demoOrder.setOrderStatus("DELIVERED");

                Order savedDemoOrder = orderRepository.save(demoOrder);

                OrderItem item1 = new OrderItem(savedDemoOrder, firstFood, firstFood.getName(), firstFood.getPrice(), 1, firstFood.getPrice());
                OrderItem item2 = new OrderItem(savedDemoOrder, secondFood, secondFood.getName(), secondFood.getPrice(), 1, secondFood.getPrice());

                orderItemRepository.save(item1);
                orderItemRepository.save(item2);
            }
        }
    }
}
