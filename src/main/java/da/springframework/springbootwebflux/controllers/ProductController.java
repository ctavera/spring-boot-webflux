package da.springframework.springbootwebflux.controllers;

import da.springframework.springbootwebflux.model.documents.Product;
import da.springframework.springbootwebflux.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Controller
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping({"/list", "/"})
    public String list (Model model) {

        Flux<Product> productFlux = productRepository.findAll();
        model.addAttribute("products", productFlux);
        model.addAttribute("title", "Listado de Productos");

        return "list";
    }
}
