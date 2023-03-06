package fr.mspr.retailer;

import fr.mspr.retailer.data.dto.ProductDTO;
import fr.mspr.retailer.data.model.ProductDetails;
import fr.mspr.retailer.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductReadingTest {

    @Autowired
    private ProductService productService;

    @Test
    public void testGetProductById() {
        LocalDateTime now = LocalDateTime.now();
        ProductDTO product = productService.addProduct(ProductDTO.builder()
                .createdAt(now)
                .details(ProductDetails.builder()
                        .color("red")
                        .description("description")
                        .price(9.99F)
                        .build())
                .stock(100)
                .name("product")
                .build());
        ProductDTO result = productService.getProductById(product.getId());
        assertNotNull(result);
        assertEquals(product.getName(), result.getName());
        assertEquals(product.getStock(), result.getStock());
        assertEquals(product.getDetails().getColor(), result.getDetails().getColor());
        assertEquals(product.getDetails().getPrice(), result.getDetails().getPrice());
        assertEquals(product.getDetails().getDescription(), result.getDetails().getDescription());
        productService.deleteProduct(result.getId());
        assertNull(productService.getProductById(result.getId()));
    }

    @Test
    public void testGetProducts() {
        LocalDateTime now = LocalDateTime.now();

        Sort.TypedSort<ProductDTO> typedSort = Sort.sort(ProductDTO.class);

        Pageable pageable = PageRequest.of(0, 10, typedSort.by(ProductDTO::getCreatedAt).descending());
        Page<ProductDTO> products = productService.getProducts(pageable);
        assertNotNull(products);

        long currentElements = products.getTotalElements();

        ProductDTO product1 = productService.addProduct(ProductDTO.builder()
                .createdAt(now)
                .details(ProductDetails.builder()
                        .color("red")
                        .description("description")
                        .price(9.99F)
                        .build())
                .stock(100)
                .name("product")
                .build());

        ProductDTO product2 = productService.addProduct(ProductDTO.builder()
                .createdAt(now)
                .details(ProductDetails.builder()
                        .color("blue")
                        .description("description 2")
                        .price(4.98F)
                        .build())
                .stock(9)
                .name("product 2")
                .build());
        products = productService.getProducts(PageRequest.of(0, 2, typedSort.by(ProductDTO::getCreatedAt).descending()));
        assertNotNull(products);

        List<ProductDTO> productList = products.getContent();

        assertFalse(productList.isEmpty());
        assertEquals(currentElements + 2, products.getTotalElements());

        assertTrue(productList.contains(product1));
        assertTrue(productList.contains(product2));

        productService.deleteProduct(product1.getId());
        productService.deleteProduct(product2.getId());
        assertNull(productService.getProductById(product1.getId()));
        assertNull(productService.getProductById(product2.getId()));

    }
}

