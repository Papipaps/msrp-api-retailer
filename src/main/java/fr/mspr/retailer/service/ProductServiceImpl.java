package fr.mspr.retailer.service;

import fr.mspr.retailer.data.dto.OrderDTO;
import fr.mspr.retailer.data.dto.ProductDTO;
import fr.mspr.retailer.data.model.Order;
import fr.mspr.retailer.data.model.Product;
import fr.mspr.retailer.data.model.Profile;
import fr.mspr.retailer.repository.OrderRepository;
import fr.mspr.retailer.repository.ProductRepository;
import fr.mspr.retailer.repository.ProfileRepository;
import fr.mspr.retailer.utils.ListToPage;
import fr.mspr.retailer.utils.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO) {
         Product product = new Product();
        product.setCreatedAt(LocalDateTime.now());
        Product save = productRepository.save(productMapper.updateProductFromDTO(productDTO, product));
        return productMapper.toDTO(save);
    }

    @Override
    public ProductDTO getProductById(long id) {
        Product product = productRepository.findById(id).orElse(null);
        return productMapper.toDTO(product);
    }

    @Override
    public Page<ProductDTO> getProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        List<ProductDTO> dtos = productMapper.toDTOs(products.getContent());
        return (Page<ProductDTO>) ListToPage.toPage(dtos, pageable, products.getTotalElements());
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO) {
        Product product = checkProduct(productDTO.getId());
        Product save = productRepository.save(productMapper.updateProductFromDTO(productDTO, product));
        return productMapper.toDTO(save);
    }

    @Override
    public boolean deleteProduct(long id) {
        productRepository.deleteById(id);
        return productRepository.findById(id).isEmpty();
    }

    private Product checkProduct(long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct.orElse(null);
    }
}
