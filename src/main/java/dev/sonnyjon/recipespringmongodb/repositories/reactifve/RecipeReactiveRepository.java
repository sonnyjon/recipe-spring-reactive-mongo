package dev.sonnyjon.recipespringmongodb.repositories.reactifve;

import dev.sonnyjon.recipespringmongodb.model.Recipe;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * Created by Sonny on 7/29/2022.
 */
public interface RecipeReactiveRepository extends ReactiveMongoRepository<Recipe, String>
{
}
