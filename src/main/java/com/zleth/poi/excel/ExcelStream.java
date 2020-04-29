package com.zleth.poi.excel;



import com.zleth.poi.excel.annotation.ExcelAlias;
import com.zleth.poi.excel.annotation.ExcelIgnore;
import com.zleth.poi.excel.exception.NotFoundGetterException;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExcelStream<T>{

    private Class<T> clazz;

    public ExcelStream(Class<T> tClass){
        this.clazz = tClass;
    }

    public List<String> headerList(){
        List<String> headerList = new ArrayList<>();
        for(Field field : this.clazz.getDeclaredFields()){
                if(field.isAnnotationPresent(ExcelIgnore.class)){
                    continue;
                }

            if(field.isAnnotationPresent(ExcelAlias.class)){
                ExcelAlias alias = field.getAnnotation(ExcelAlias.class);
                headerList.add(alias.value());
            }else{
                headerList.add(field.getName());
            }
        }
        return headerList;
    }

    public ExcelTable getTable(Collection<T> list) throws NotFoundGetterException {
        ExcelTable table = new ExcelTable();
        if(this.clazz.isAnnotationPresent(ExcelAlias.class)){
            ExcelAlias alias = this.clazz.getAnnotation(ExcelAlias.class);
            table.setTableName(alias.value());
        }else{
            table.setTableName(this.clazz.getSimpleName());
        }
        table.setHeaderList(this.headerList());
        for(T t : list){
            List<ExcelElement> elementList = new ArrayList<>();
            for(Field field : this.clazz.getDeclaredFields()){
                if(field.isAnnotationPresent(ExcelIgnore.class)){
                    continue;
                }
                try {
                    Method method = this.getMethod(field);
                    ExcelElement excelElement = new ExcelElement();
                    if(field.isAnnotationPresent(ExcelAlias.class)) {
                        ExcelAlias excelAlias = field.getAnnotation(ExcelAlias.class);
                        excelElement.setHeader(excelAlias.value());
                        excelElement.setAlignment(excelAlias.alignment());
                    }else {
                        excelElement.setHeader(field.getName());
                        excelElement.setAlignment(HorizontalAlignment.LEFT);
                    }
                    excelElement.setValue(method.invoke(t).toString());
                    elementList.add(excelElement);
                }catch (Exception e){
                    throw new NotFoundGetterException("未找到字段 '"+field.getName()+"' 对应的getter方法!");
                }
            }
            table.getRows().add(ExcelRow.builder().elements(elementList).build());
        }

        return table;
    }

    private Method getMethod(Field field) throws NoSuchMethodException {
        Method method = this.clazz.getMethod("get"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1));
        return method;
    }

    public byte[] toExcel(ExcelTable table) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(table.getTableName());

        int rowNum = 0;

        HSSFRow titleRow = sheet.createRow(rowNum++);
        titleRow.createCell(0).setCellValue(table.getTableName());


        HSSFFont titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short)18);

        HSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setFont(titleFont);
        titleRow.setHeightInPoints(28);
        titleRow.getCell(0).setCellStyle(titleStyle);

        CellRangeAddress range = new CellRangeAddress(0,0,0,table.getHeaderList().size()-1);
        sheet.addMergedRegion(range);


        HSSFRow headerRow = sheet.createRow(rowNum++);
        headerRow.setHeightInPoints(18.75f);

        for(int i=0;i<table.getHeaderList().size();i++){
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellStyle(this.getHeaderStyle(workbook));
            HSSFRichTextString text = new HSSFRichTextString(table.getHeaderList().get(i));
            cell.setCellValue(text);
        }

        for (ExcelRow excelRow : table.getRows()){
            HSSFRow hssfRow = sheet.createRow(rowNum++);
            hssfRow.setHeightInPoints(18.75f);
            int cellIndex = 0;
            for(ExcelElement excelElement : excelRow.getElements()){
                HSSFCell dataCell = hssfRow.createCell(cellIndex++);
                dataCell.setCellStyle(this.getDataCellStyle(workbook,excelElement.getAlignment()));
                dataCell.setCellValue(excelElement.getValue());
            }
        }
        for (int i = 0; i < table.getHeaderList().size(); i++) {
            sheet.autoSizeColumn(i);
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        workbook.write(stream);
        return stream.toByteArray();
    }

    private HSSFCellStyle getDataCellStyle(HSSFWorkbook workbook,HorizontalAlignment alignment) {
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(alignment);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        HSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short)12);
        cellStyle.setFont(headerFont);
        return cellStyle;
    }

    private HSSFCellStyle getHeaderStyle(HSSFWorkbook workbook) {
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        HSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short)14);
        cellStyle.setFont(headerFont);
        return cellStyle;
    }
}
