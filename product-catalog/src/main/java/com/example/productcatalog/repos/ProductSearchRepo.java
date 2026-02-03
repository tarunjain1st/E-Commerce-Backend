package com.example.productcatalog.repos;

import com.example.productcatalog.search.ProductSearchDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSearchRepo extends ElasticsearchRepository<ProductSearchDocument, Long> {

    // Keyword / full-text search (name OR description)
    Page<ProductSearchDocument> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name,
            String description,
            Pageable pageable
    );

    // Filter by category
    Page<ProductSearchDocument> findByCategory(String category, Pageable pageable);

    // Filter by category + price range
    Page<ProductSearchDocument> findByCategoryAndPriceBetween(
            String category,
            Double minPrice,
            Double maxPrice,
            Pageable pageable
    );

    // Filter by keyword + category + price
    Page<ProductSearchDocument> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndCategoryAndPriceBetween(
            String name, String description, String category, Double minPrice, Double maxPrice, Pageable pageable
    );

    Page<ProductSearchDocument> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndPriceBetween(
            String name, String description, Double minPrice, Double maxPrice, Pageable pageable
    );

    Page<ProductSearchDocument> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndCategory(
            String name, String description, String category, Pageable pageable
    );

}
