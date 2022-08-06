package dev.sonnyjon.recipespringmongodb.services;

import dev.sonnyjon.recipespringmongodb.dto.RecipeDto;
import dev.sonnyjon.recipespringmongodb.model.Recipe;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by Sonny on 7/9/2022.
 */
public interface RecipeService
{
    Flux<Recipe> getRecipes();
    Mono<Recipe> findById(String id);
    Mono<RecipeDto> findDtoById(String id);
    Mono<RecipeDto> saveRecipe(RecipeDto dto);
    Mono<Void> deleteById(String idToDelete);
}
