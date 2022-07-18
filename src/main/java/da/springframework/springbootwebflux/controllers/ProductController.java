package da.springframework.springbootwebflux.controllers;

import da.springframework.springbootwebflux.model.documents.Product;
import da.springframework.springbootwebflux.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.Duration;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@SessionAttributes("product") //save on session for create and save
@Controller
public class ProductController {

    private final ProductService productService;

    @GetMapping({"/list", "/"})
    public Mono<String> list (Model model) {

        Flux<Product> productFlux = productService.findAllByNameUpperCase();

        productFlux.subscribe(product -> log.info(product.getName()));

        model.addAttribute("products", productFlux);
        model.addAttribute("title", "Listado de Productos");

        return Mono.just("list");
    }

    @GetMapping("/form")
    public Mono<String> create(Model model) {

        model.addAttribute("product", new Product());
        model.addAttribute("title", "Formulario de producto");
        model.addAttribute("button", "Crear");

        return Mono.just("form");
    }

    @GetMapping("/form/{id}")
    public Mono<String> edit(@PathVariable String id, Model model) {

        Mono<Product> productMono = productService.findById(id)
                .doOnNext(product -> log.info("Producto: " + product.getName()))
                .defaultIfEmpty(new Product());

        model.addAttribute("title", "Editar Producto");
        model.addAttribute("product", productMono);
        model.addAttribute("button", "Editar");

        return Mono.just("form");
    }

    @GetMapping("/form-v2/{id}")
    public Mono<String> editV2(@PathVariable String id, Model model) {

        return productService.findById(id)
                .doOnNext(product -> {
                    log.info("Producto: " + product.getName());
                    model.addAttribute("title", "Editar Producto");
                    model.addAttribute("product", product);
                    model.addAttribute("button", "Editar");
                })
                .defaultIfEmpty(new Product())
                .flatMap(product -> {
                    if (product.getId() == null){
                        return Mono.error(new InterruptedException());
                    }

                    return Mono.just(product);
                })
                .then(Mono.just("form"))
                .onErrorResume(throwable -> Mono.just("redirect:/list?error=no+existe+el+producto"));
    }

    @PostMapping("/form")
    public Mono<String> save(@Valid Product product, BindingResult result, Model model, SessionStatus sessionStatus){

        if (result.hasErrors()) {
            model.addAttribute("title", "Errores en el formulario Producto");
            model.addAttribute("button", "Guardar");

            return Mono.just("form");
        }

        sessionStatus.setComplete(); //To clean the SessionAttribute

        if (product.getCreationDate() == null){
            product.setCreationDate(new Date());
        }

        return productService.save(product).doOnNext(product1 -> {
            log.info("Producto guardado: " + product.getName() + " Id: " + product.getId());
        }).thenReturn("redirect:/list?success=producto+guardado+con+exito"); // too : }).then(Mono.just("redirect:/list"));
    }

    @GetMapping("/datadriver-list")
    public String dataDriverList (Model model) {

        Flux<Product> productFlux = productService.findAllByNameUpperCase().delayElements(Duration.ofSeconds(1));

        productFlux.subscribe(product -> log.info(product.getName()));

        model.addAttribute("products", new ReactiveDataDriverContextVariable(productFlux, 2));
        model.addAttribute("title", "Listado de Productos");

        return "list";
    }

    @GetMapping("/full-list")
    public String fullList (Model model) {

        Flux<Product> productFlux = productService.findAllByNameUpperCaseRepeat();

        model.addAttribute("products", productFlux);
        model.addAttribute("title", "Listado de Productos");

        return "list";
    }

    @GetMapping("/chunked-list")
    public String chunkedList (Model model) {

        Flux<Product> productFlux = productService.findAllByNameUpperCaseRepeat();

        model.addAttribute("products", productFlux);
        model.addAttribute("title", "Listado de Productos");

        return "chunked-list";
    }
}
