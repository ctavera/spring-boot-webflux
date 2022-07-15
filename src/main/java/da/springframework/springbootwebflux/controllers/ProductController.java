package da.springframework.springbootwebflux.controllers;

import da.springframework.springbootwebflux.model.documents.Product;
import da.springframework.springbootwebflux.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping({"/list", "/"})
    public String list (Model model) {

        Flux<Product> productFlux = productRepository.findAll()
                .map(product -> {
                    product.setName(product.getName().toUpperCase());

                    return product;
                });

        productFlux.subscribe(product -> log.info(product.getName()));

        model.addAttribute("products", productFlux);
        model.addAttribute("title", "Listado de Productos");

        return "list";
    }

    @GetMapping("/list-datadriver")
    public String listDataDriver (Model model) {

        Flux<Product> productFlux = productRepository.findAll()
                .map(product -> {
                    product.setName(product.getName().toUpperCase());

                    return product;
                }).delayElements(Duration.ofSeconds(1));

        productFlux.subscribe(product -> log.info(product.getName()));

        model.addAttribute("products", new ReactiveDataDriverContextVariable(productFlux, 2));
        model.addAttribute("title", "Listado de Productos");

        return "list";
    }

    @GetMapping("/list-full")
    public String listFull (Model model) {

        Flux<Product> productFlux = productRepository.findAll()
                .map(product -> {
                    product.setName(product.getName().toUpperCase());

                    return product;
                }).repeat(5000);

        model.addAttribute("products", productFlux);
        model.addAttribute("title", "Listado de Productos");

        return "list";
    }
}
