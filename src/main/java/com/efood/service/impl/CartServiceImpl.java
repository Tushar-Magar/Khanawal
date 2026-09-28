package com.efood.service.impl;

import com.efood.dto.CartSummary;
import com.efood.entity.Cart;
import com.efood.entity.CartItem;
import com.efood.entity.Food;
import com.efood.entity.User;
import com.efood.exception.ResourceNotFoundException;
import com.efood.repository.CartItemRepository;
import com.efood.repository.CartRepository;
import com.efood.repository.FoodRepository;
import com.efood.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.Optional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final FoodRepository foodRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           FoodRepository foodRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.foodRepository = foodRepository;
    }

    @Override
    public Cart getOrCreateCartForUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart(user);
                    return cartRepository.save(newCart);
                });
    }

    @Override
    public Cart addToCart(User user, Long foodId, Integer quantity) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with ID: " + foodId));

        if (!Boolean.TRUE.equals(food.getAvailable())) {
            throw new IllegalStateException("Food item '" + food.getName() + "' is currently unavailable.");
        }

        int addQty = (quantity != null && quantity > 0) ? quantity : 1;
        Cart cart = getOrCreateCartForUser(user);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getFood().getId().equals(foodId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + addQty);
            item.setPrice(food.getPrice()); // Always update to current DB price
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem(cart, food, addQty, food.getPrice());
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart updateCartItemQuantity(User user, Long cartItemId, Integer quantity) {
        Cart cart = getOrCreateCartForUser(user);

        Iterator<CartItem> iterator = cart.getItems().iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getId().equals(cartItemId)) {
                if (quantity == null || quantity <= 0) {
                    iterator.remove();
                    cartItemRepository.delete(item);
                } else {
                    item.setQuantity(quantity);
                    // refresh current food price
                    if (item.getFood() != null) {
                        item.setPrice(item.getFood().getPrice());
                    }
                    cartItemRepository.save(item);
                }
                break;
            }
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart removeFromCart(User user, Long cartItemId) {
        Cart cart = getOrCreateCartForUser(user);
        cart.getItems().removeIf(item -> {
            if (item.getId().equals(cartItemId)) {
                cartItemRepository.delete(item);
                return true;
            }
            return false;
        });
        return cartRepository.save(cart);
    }

    @Override
    public void clearCart(User user) {
        Cart cart = getOrCreateCartForUser(user);
        cartItemRepository.deleteByCart(cart);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartSummary getCartSummary(Cart cart) {
        if (cart == null || cart.getItems().isEmpty()) {
            return new CartSummary(0.0, 0.0, 0.0, 0.0, 0);
        }

        double subtotal = 0.0;
        int count = 0;

        for (CartItem item : cart.getItems()) {
            if (item.getFood() != null && item.getQuantity() != null) {
                // Ensure live price from DB
                double itemPrice = item.getFood().getPrice();
                subtotal += itemPrice * item.getQuantity();
                count += item.getQuantity();
            }
        }

        subtotal = Math.round(subtotal * 100.0) / 100.0;
        double deliveryFee = subtotal > 0 ? 40.0 : 0.0;
        double tax = Math.round(subtotal * 0.05 * 100.0) / 100.0; // 5% tax
        double total = Math.round((subtotal + deliveryFee + tax) * 100.0) / 100.0;

        return new CartSummary(subtotal, deliveryFee, tax, total, count);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartItemCount(User user) {
        if (user == null) return 0;
        Optional<Cart> cartOpt = cartRepository.findByUser(user);
        return cartOpt.map(Cart::getTotalItemCount).orElse(0);
    }
}
