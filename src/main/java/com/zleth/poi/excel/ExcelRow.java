package com.zleth.poi.excel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelRow {
    @Builder.Default
    private List<ExcelElement> elements = new ArrayList<>();
}
