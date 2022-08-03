package dev.sonnyjon.recipespringmongodb.services;

import dev.sonnyjon.recipespringmongodb.converters.IngredientConverter;
import dev.sonnyjon.recipespringmongodb.dto.IngredientDto;
import dev.sonnyjon.recipespringmongodb.exceptions.NotFoundException;
import dev.sonnyjon.recipespringmongodb.model.Ingredient;
import dev.sonnyjon.recipespringmongodb.model.Recipe;
import dev.sonnyjon.recipespringmongodb.repositories.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Created by Sonny on 7/19/2022.
 */
@Slf4j
@Service("ingredientService")
public class IngredientServiceMongoImpl implements IngredientService
{
    private final RecipeRepository recipeRepository;
    private final IngredientConverter converter = new IngredientConverter();

    public IngredientServiceMongoImpl(RecipeRepository recipeRepository)
    {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public Mono<IngredientDto> findInRecipe(String recipeId, String ingredientId)
    {
        Recipe recipe = findRecipe( recipeId );
        IngredientDto ingredientDto = converter.convertEntity(findIngredientInRecipe( ingredientId, recipe ));
        return Mono.just( ingredientDto );
    }

    @Override
    @Transactional
    public Mono<IngredientDto> saveIngredient(String recipeId, IngredientDto dto)
    {
        Recipe recipe = findRecipe( recipeId );
        Ingredient toBeSaved = converter.convertDto( dto );

        try {
            // Existing ingredient
            Ingredient ingredient = findIngredientInRecipe( dto.getId(), recipe );
            recipe.getIngredients().remove( ingredient );
            recipe.addIngredient( toBeSaved );
        }
        catch (NotFoundException e)
        {
            // New ingredient
            recipe.addIngredient( toBeSaved );
        }

        Recipe savedRecipe = recipeRepository.save( recipe );
        IngredientDto ingredientDto = converter.convertEntity( findIngredientInRecipe(dto.getId(), savedRecipe) );
        return Mono.just( ingredientDto );
    }

    @Override
    @Transactional
    public Mono<Void> removeIngredient(String recipeId, String ingredientId)
    {
        Recipe recipe = findRecipe( recipeId );
        Ingredient ingredient = findIngredientInRecipe( ingredientId, recipe );
        recipe.getIngredients().remove( ingredient );

        recipeRepository.save( recipe );

        return Mono.just("removeIngredient").then();
    }

    //==================================================================================================================
    private Recipe findRecipe(String recipeId)
    {
        return recipeRepository.findById( recipeId )
                                .orElseThrow(
                                    () -> new NotFoundException("Recipe not found. ID: " + recipeId)
                                );
    }

    private Ingredient findIngredientInRecipe(String ingredientId, Recipe recipe)
    {
        return recipe.getIngredients()
                        .stream()
                        .filter(
                            ingredient -> ingredient.getId().equals( ingredientId )
                        )
                        .findFirst()
                        .orElseThrow(
                            ()-> new NotFoundException("Ingredient not found. ID: " + ingredientId)
                        );
    }
}
