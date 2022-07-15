package da.springframework.springbootwebflux.bootstrap;

import da.springframework.springbootwebflux.model.documents.Product;
import da.springframework.springbootwebflux.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
@Component
public class SpringBootWebFluxBootstrap implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public void run(String... args) throws Exception {

        reactiveMongoTemplate.dropCollection("products").subscribe();

        Flux.just(new Product("TV Panasonic Pantalla LCD", 456.89),
                        new Product("Sony Camara HD Digital", 177.99),
                        new Product("Apple iPad", 46.89),
                        new Product("Sony Notebook", 846.99),
                        new Product("Hewlett Packard Multifuncional", 200.89),
                        new Product("Bianchi Bicicleta", 70.89),
                        new Product("HP Notebook Omen 17", 2500.89),
                        new Product("Mica Cómoda 5 Cajones", 150.99),
                        new Product("TV Sony Bravia OLED 4K Ultra HD", 2255.99)
                ).flatMap(product -> productRepository.save(product))
                .subscribe(product -> log.info("Inserted: " + product.getId() + " " + product.getName()));
    }
}
