package com.example.productcatalog.controllers;

import com.example.productcatalog.search.ProductSearchDocument;
import com.example.productcatalog.services.ProductSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/search")
public class ProductSearchController {

    @Autowired
    private ProductSearchService searchService;

    // -------------------- KEYWORD SEARCH --------------------
    @GetMapping
    public Page<ProductSearchDocument> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return searchService.searchByKeyword(q, page, size, sortBy, direction);
    }

    // -------------------- FULL TEXT SEARCH --------------------
    @GetMapping("/fulltext")
    public Page<ProductSearchDocument> fullTextSearch(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return searchService.fullTextSearch(q, page, size);
    }

    // -------------------- FILTER + SORT --------------------
    @GetMapping("/filter")
    public Page<ProductSearchDocument> filter(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return searchService.filterAndSort(
                q, category, minPrice, maxPrice,
                page, size, sortBy, direction
        );
    }
}
