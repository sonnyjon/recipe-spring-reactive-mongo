package dev.sonnyjon.recipespringmongodb.services;

import dev.sonnyjon.recipespringmongodb.dto.IngredientDto;
import reactor.core.publisher.Mono;

/**
 * Created by Sonny on 7/9/2022.
 */
public interface IngredientService
{
    Mono<IngredientDto> findInRecipe(String recipeId, String ingredientId);
    Mono<IngredientDto> saveIngredient(String recipeId, IngredientDto dto);
    Mono<Void> removeIngredient(String recipeId, String ingredientId);
}
