package dev.sonnyjon.recipespringmongodb.repositories.reactifve;

import dev.sonnyjon.recipespringmongodb.model.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Created by Sonny on 7/29/2022.
 */
public interface CategoryReactiveRepository extends ReactiveMongoRepository<Category, String>
{
    Mono<Category> findByDescription(String description);
}
