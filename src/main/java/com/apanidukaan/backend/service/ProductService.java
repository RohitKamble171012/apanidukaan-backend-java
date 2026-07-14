package com.apanidukaan.backend.service;

import com.apanidukaan.backend.exception.ForbiddenException;
import com.apanidukaan.backend.exception.ResourceNotFoundException;
import com.apanidukaan.backend.model.Product;
import com.apanidukaan.backend.model.Shop;
import com.apanidukaan.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;



import java.util.List;



@Service
public class ProductService {
    @Autowired
    private ExcelService excelService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopService shopService;

    public List<Product> getProductsForShop(String shopId) {
        return productRepository.findByShopId(shopId);
    }

    public Product createProduct(Product product, String currentUid) {
        Shop shop = shopService.getShopById(product.getShopId());
        assertOwnership(shop, currentUid);
        return productRepository.save(product);
    }

    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    public Product updateProduct(String id, Product updatedData, String currentUid) {
        Product existing = getProductById(id);
        Shop shop = shopService.getShopById(existing.getShopId());
        assertOwnership(shop, currentUid);

        existing.setProductName(updatedData.getProductName());
        existing.setCategory(updatedData.getCategory());
        existing.setPrice(updatedData.getPrice());
        existing.setQuantity(updatedData.getQuantity());
        existing.setUnit(updatedData.getUnit());
        existing.setAvailability(updatedData.isAvailability());

        return productRepository.save(existing);
    }

    public void deleteProduct(String id, String currentUid) {
        Product existing = getProductById(id);
        Shop shop = shopService.getShopById(existing.getShopId());
        assertOwnership(shop, currentUid);
        productRepository.deleteById(id);
    }
    public List<Product> importProducts(MultipartFile file, String shopId, String currentUid) throws IOException {
        Shop shop = shopService.getShopById(shopId);
        assertOwnership(shop, currentUid);

        List<Product> parsed = excelService.parseProducts(file, shopId);
        return productRepository.saveAll(parsed);
    }

    public byte[] exportProducts(String shopId, String currentUid) throws IOException {
        Shop shop = shopService.getShopById(shopId);
        assertOwnership(shop, currentUid);

        List<Product> products = productRepository.findByShopId(shopId);
        return excelService.exportProducts(products);
    }

    private void assertOwnership(Shop shop, String currentUid) {
        if (!shop.getOwnerUid().equals(currentUid)) {
            throw new ForbiddenException("You do not own this shop — cannot modify its products.");
        }
    }
}