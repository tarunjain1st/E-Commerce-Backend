package com.example.productcatalog.services;

import com.example.productcatalog.exceptions.*;
import com.example.productcatalog.repos.ProductSearchRepo;
import com.example.productcatalog.search.ProductSearchDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductSearchService {

    @Autowired
    private ProductSearchRepo repository;

    // -----------------------------
    // 1️ Keyword search
    // -----------------------------
    public Page<ProductSearchDocument> searchByKeyword(
            String q,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        if (q == null || q.isEmpty()) {
            throw new IllegalArgumentException("Keyword cannot be null or empty");
        }

        Pageable pageable = buildPageable(page, size, sortBy, direction);
        Page<ProductSearchDocument> result = repository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q, q, pageable);

        if (result.isEmpty()) {
            throw new ProductSearchEmptyException("No products found for keyword: " + q);
        }

        return result;
    }

    // -----------------------------
    // 2️ Full text search
    // -----------------------------
    public Page<ProductSearchDocument> fullTextSearch(String q, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (q == null || q.isEmpty()) {
            return repository.findAll(pageable);
        }

        // split the query into words
        String[] words = q.trim().split("\\s+");

        // start with the first word
        Page<ProductSearchDocument> result = repository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                words[0], words[0], pageable
        );

        // for multiple words, filter results in memory (OR condition)
        for (int i = 1; i < words.length; i++) {
            String word = words[i];
            Page<ProductSearchDocument> partial = repository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    word, word, pageable
            );

            result = mergePages(result, partial, pageable);
        }

        if (result.isEmpty()) {
            throw new ProductSearchEmptyException("No products found for query: " + q);
        }

        return result;
    }

    // helper to merge two pages
    private Page<ProductSearchDocument> mergePages(Page<ProductSearchDocument> p1,
                                                   Page<ProductSearchDocument> p2,
                                                   Pageable pageable) {
        List<ProductSearchDocument> combined = new ArrayList<>();
        combined.addAll(p1.getContent());
        for (ProductSearchDocument doc : p2.getContent()) {
            if (!combined.contains(doc)) {
                combined.add(doc);
            }
        }
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), combined.size());
        return new PageImpl<>(combined.subList(start, end), pageable, combined.size());
    }

    // -----------------------------
    // 3️ Filter + sort + paging
    // -----------------------------
    public Page<ProductSearchDocument> filterAndSort(
            String q,
            String category,
            Double minPrice,
            Double maxPrice,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        Pageable pageable = buildPageable(page, size, sortBy, direction);

        Page<ProductSearchDocument> result;

        if (q != null && !q.isEmpty() && category != null && !category.isEmpty()
                && minPrice != null && maxPrice != null) {
            result = repository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndCategoryAndPriceBetween(
                    q, q, category, minPrice, maxPrice, pageable
            );
        } else if (q != null && !q.isEmpty() && minPrice != null && maxPrice != null) {
            result = repository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndPriceBetween(
                    q, q, minPrice, maxPrice, pageable
            );
        } else if (q != null && !q.isEmpty() && category != null && !category.isEmpty()) {
            result = repository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndCategory(
                    q, q, category, pageable
            );
        } else if (category != null && !category.isEmpty() && minPrice != null && maxPrice != null) {
            result = repository.findByCategoryAndPriceBetween(category, minPrice, maxPrice, pageable);
        } else if (q != null && !q.isEmpty()) {
            result = repository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q, q, pageable);
        } else if (category != null && !category.isEmpty()) {
            result = repository.findByCategory(category, pageable);
        } else {
            result = repository.findAll(pageable);
        }

        if (result.isEmpty()) {
            throw new ProductSearchEmptyException("No products found for given filters");
        }

        return result;
    }

    // -----------------------------
    // Helper: build pageable
    // -----------------------------
    private Pageable buildPageable(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy != null ? sortBy : "createdAt"
        );
        return PageRequest.of(page, size, sort);
    }
}
