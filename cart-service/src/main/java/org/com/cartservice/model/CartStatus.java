package org.com.cartservice.model;

public enum CartStatus {
    ACTIVE,              // Активная корзина (текущая)
    CONVERTED_TO_ORDER,  // Преобразована в заказ
    ABANDONED,           // Брошена (неактивна)
    ARCHIVED,            // Архивирована (после заказа)
    CLEARED              // Очищена (после заказа)
}
