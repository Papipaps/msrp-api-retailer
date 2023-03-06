package fr.mspr.retailer.utils.mapper;

import fr.mspr.retailer.data.dto.OrderDTO;
import fr.mspr.retailer.data.model.Order;

public class OrderCustomMapper {
    public static OrderDTO toDTO(Order order){
        return OrderDTO.builder()
                .id(order.getId())
                .customerId(order.getProfile().getId())
                .productId(order.getProduct().getId())
                .createdAt(order.getCreatedAt())
                .quantity(order.getQuantity())
                .build();
    }
}
