package org.example.projectlibrioo.DTO;

import lombok.Data;
import java.util.List;

@Data
public class LibraryReportResponseDTO {
    private List<LibraryReportRowDTO> rows;
    private List<PopularItemDTO> popularItems;
}