package com.zleth.poi.excel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelElement {
    private String header;
    private String value;
    private HorizontalAlignment alignment;
}
