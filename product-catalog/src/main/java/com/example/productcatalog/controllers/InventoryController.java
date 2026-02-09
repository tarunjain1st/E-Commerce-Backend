package com.example.productcatalog.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
//    POST /inventory/{productId}/stock  -- add quantity to totalQuantity
//    POST /inventory/{productId}/reserve -- add to reservedQuantity (when order created)
//    POST /inventory/{productId}/release -- remove from reservedQuantity (when order canceled or returned)
//    POST /inventory/{productId}/commit -- remove from total and reservedQuantity (when order is completed)
}
