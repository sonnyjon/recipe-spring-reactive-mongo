package dev.sonnyjon.recipespringmongodb.services;

import dev.sonnyjon.recipespringmongodb.converters.RecipeConverter;
import dev.sonnyjon.recipespringmongodb.dto.RecipeDto;
import dev.sonnyjon.recipespringmongodb.exceptions.NotFoundException;
import dev.sonnyjon.recipespringmongodb.model.Recipe;
import dev.sonnyjon.recipespringmongodb.repositories.reactifve.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by Sonny on 7/12/2022.
 */
@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService
{
    private final RecipeReactiveRepository recipeReactiveRepository;
    private final RecipeConverter converter = new RecipeConverter();

    public RecipeServiceImpl(RecipeReactiveRepository recipeReactiveRepository)
    {
        this.recipeReactiveRepository = recipeReactiveRepository;
    }

    @Override
    public Flux<Recipe> getRecipes()
    {
        log.debug("I'm in the service");
        return recipeReactiveRepository.findAll();
    }

    @Override
    public Mono<Recipe> findById(String id)
    {
        return recipeReactiveRepository.findById( id );
    }

    @Override
    public Mono<RecipeDto> findDtoById(String id)
    {
        return findById( id )
                .map(converter::convertEntity)
                .doOnError(err -> {
                   throw new NotFoundException("Recipe not found: " + id);
                });
    }

    @Override
    public Mono<RecipeDto> saveRecipe(RecipeDto dto)
    {
        Recipe detachedRecipe = converter.convertDto( dto );

        return recipeReactiveRepository
                    .save( detachedRecipe )
                    .map(converter::convertEntity);
    }

    @Override
    public Mono<Void> deleteById(String idToDelete)
    {
        return recipeReactiveRepository
                    .deleteById( idToDelete )
                    .doOnError(err -> {
                        throw new NotFoundException("Recipe not found: " + idToDelete);
                    });
    }
}