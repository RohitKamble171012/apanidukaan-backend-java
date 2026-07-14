package com.apanidukaan.backend.controller;

import com.apanidukaan.backend.model.Product;
import com.apanidukaan.backend.security.FirebaseUserPrincipal;
import com.apanidukaan.backend.service.ExcelService;
import com.apanidukaan.backend.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ExcelService excelService;

    @GetMapping("/shop/{shopId}")
    public List<Product> getProductsByShop(@PathVariable String shopId) {
        return productService.getProductsForShop(shopId);
    }

    @PostMapping
    public Product createProduct(@Valid @RequestBody Product product) {
        return productService.createProduct(product, getCurrentUid());
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable String id, @Valid @RequestBody Product product) {
        return productService.updateProduct(id, product, getCurrentUid());
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id, getCurrentUid());
    }

    @PostMapping("/import/{shopId}")
    public List<Product> importProducts(@PathVariable String shopId,
                                        @RequestParam("file") MultipartFile file) throws IOException {
        return productService.importProducts(file, shopId, getCurrentUid());
    }

    @GetMapping("/export/{shopId}")
    public ResponseEntity<byte[]> exportProducts(@PathVariable String shopId) throws IOException {
        byte[] excelBytes = productService.exportProducts(shopId, getCurrentUid());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    @GetMapping("/import-template")
    public ResponseEntity<byte[]> downloadTemplate() throws IOException {
        byte[] templateBytes = excelService.generateProductTemplate();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=product-template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(templateBytes);
    }

    private String getCurrentUid() {
        FirebaseUserPrincipal principal = (FirebaseUserPrincipal)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUid();
    }
}