package com.example.productcatalog.search;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.Date;

@Data
@Document(indexName = "products")
public class ProductSearchDocument {

    @Id
    private Long id;

    // Full-text searchable
    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    // Filterable
    @Field(type = FieldType.Keyword)
    private String category;

    // Sortable
    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Date)
    private Date createdAt;
}
