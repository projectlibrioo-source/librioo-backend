package org.example.projectlibrioo.DTO;

import lombok.Data;

@Data
public class PopularItemDTO {
    private String title;
    private String author;
    private long checkouts;
}