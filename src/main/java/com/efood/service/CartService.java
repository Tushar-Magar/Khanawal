package com.efood.service;

import com.efood.dto.CartSummary;
import com.efood.entity.Cart;
import com.efood.entity.User;

public interface CartService {
    Cart getOrCreateCartForUser(User user);
    Cart addToCart(User user, Long foodId, Integer quantity);
    Cart updateCartItemQuantity(User user, Long cartItemId, Integer quantity);
    Cart removeFromCart(User user, Long cartItemId);
    void clearCart(User user);
    CartSummary getCartSummary(Cart cart);
    int getCartItemCount(User user);
}
