package dev.sonnyjon.recipespringmongodb.repositories.reactifve;

import dev.sonnyjon.recipespringmongodb.model.UnitOfMeasure;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Created by Sonny on 7/28/2022.
 */
public interface UnitOfMeasureReactiveRepository extends ReactiveMongoRepository<UnitOfMeasure, String>
{
    Mono<UnitOfMeasure> findByDescription(String description);
}
