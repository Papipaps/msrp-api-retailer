package fr.mspr.retailer.service;

import fr.mspr.retailer.data.dto.OrderDTO;
import fr.mspr.retailer.data.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {

    ProductDTO addProduct(ProductDTO productDTO);
    ProductDTO getProductById(long id);
    Page<ProductDTO> getProducts(Pageable pageable);

    ProductDTO updateProduct(ProductDTO productDTO);

    boolean deleteProduct(long id);


//    boolean placeOrder(OrderDTO orderDTO, Long id);
}
