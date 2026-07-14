package com.apanidukaan.backend.service;

import com.apanidukaan.backend.model.Product;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {

    private static final String[] HEADERS = {
            "productName", "category", "price", "quantity", "unit"
    };

    public List<Product> parseProducts(MultipartFile file, String shopId) throws IOException {
        List<Product> products = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                String productName = getStringValue(row.getCell(0));
                String category = getStringValue(row.getCell(1));
                BigDecimal price = getNumericValue(row.getCell(2));
                Integer quantity = getIntValue(row.getCell(3));
                String unit = getStringValue(row.getCell(4));

                if (productName == null || productName.isBlank()) {
                    continue; // skip empty rows
                }

                Product product = new Product();
                product.setShopId(shopId);
                product.setProductName(productName);
                product.setCategory(category);
                product.setPrice(price != null ? price : BigDecimal.ZERO);
                product.setQuantity(quantity != null ? quantity : 0);
                product.setUnit(unit != null ? unit : "");

                products.add(product);
            }
        }

        return products;
    }

    public byte[] generateProductTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Products");
            Row headerRow = sheet.createRow(0);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4000);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportProducts(List<Product> products) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Products");
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < HEADERS.length; i++) {
                headerRow.createCell(i).setCellValue(HEADERS[i]);
            }

            int rowIndex = 1;
            for (Product product : products) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(product.getProductName());
                row.createCell(1).setCellValue(product.getCategory());
                row.createCell(2).setCellValue(product.getPrice().doubleValue());
                row.createCell(3).setCellValue(product.getQuantity());
                row.createCell(4).setCellValue(product.getUnit());
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private String getStringValue(Cell cell) {
        if (cell == null) return null;
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    private BigDecimal getNumericValue(Cell cell) {
        if (cell == null) return null;
        try {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getIntValue(Cell cell) {
        BigDecimal value = getNumericValue(cell);
        return value != null ? value.intValue() : null;
    }
}