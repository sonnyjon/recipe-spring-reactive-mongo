package dev.sonnyjon.recipespringmongodb.services;

import dev.sonnyjon.recipespringmongodb.converters.RecipeConverter;
import dev.sonnyjon.recipespringmongodb.dto.RecipeDto;
import dev.sonnyjon.recipespringmongodb.exceptions.NotFoundException;
import dev.sonnyjon.recipespringmongodb.model.Recipe;
import dev.sonnyjon.recipespringmongodb.repositories.reactifve.RecipeReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by Sonny on 7/12/2022.
 */
@ExtendWith(SpringExtension.class)
class RecipeServiceImplTest
{
    @Mock
    RecipeReactiveRepository recipeReactiveRepository;

    RecipeConverter converter;
    RecipeService recipeService;
    AutoCloseable mocks;

    @BeforeEach
    void setUp()
    {
        mocks = MockitoAnnotations.openMocks(this);
        recipeService = new RecipeServiceImpl( recipeReactiveRepository );
        converter = new RecipeConverter();
    }

    @AfterEach
    void tearDown() throws Exception
    {
        mocks.close();
    }

    @Test
    void whenGetRecipes_thenFindAll()
    {
        // given
        List<Recipe> expectedRecipes = new ArrayList<>();
        Recipe recipe1 = new Recipe();
        recipe1.setId("RECIPE-1");
        expectedRecipes.add( recipe1 );

        Recipe recipe2 = new Recipe();
        recipe2.setId("RECIPE-2");
        expectedRecipes.add( recipe2 );

        Recipe recipe3 = new Recipe();
        recipe3.setId("RECIPE-3");
        expectedRecipes.add( recipe3 );

        Flux<Recipe> recipeFlux = Flux.just( recipe1, recipe2, recipe3 );

        // when
        when(recipeReactiveRepository.findAll()).thenReturn( recipeFlux );
        List<Recipe> actualRecipes = recipeService.getRecipes().collectList().block();

        // then
        assertEquals( expectedRecipes, actualRecipes );
    }

    @Test
    void givenRecipeId_whenFindById_thenFindRecipe()
    {
        // given
        final String ID = "RECIPE-1";

        Recipe expectedRecipe = new Recipe();
        expectedRecipe.setId( ID );
        Mono<Recipe> recipeMono = Mono.just( expectedRecipe );

        // when
        when(recipeReactiveRepository.findById(anyString())).thenReturn( recipeMono );
        Recipe actualRecipe = recipeService.findById( ID ).block();

        // then
        assertEquals(expectedRecipe, actualRecipe);
    }

    @Test
    void givenBadId_whenFindById_thenThrowException()
    {
        // given
        final String ID = "RECIPE-1";

        // when
        when(recipeReactiveRepository.findById(anyString())).thenThrow( NotFoundException.class );
        Executable executable = () -> recipeService.findById( ID ).block();

        // then
        assertThrows( NotFoundException.class, executable );
    }

    @Test
    void givenRecipeId_whenFindDtoById_thenFindRecipe_andReturnDto()
    {
        // given
        final String ID = "RECIPE-1";

        Recipe recipe = new Recipe();
        recipe.setId( ID );
        Mono<Recipe> recipeMono = Mono.just( recipe );
        RecipeDto expectedDto = converter.convertEntity( recipe );

        // when
        when(recipeReactiveRepository.findById(anyString())).thenReturn( recipeMono );
        RecipeDto actualDto = recipeService.findDtoById( ID ).block();

        // then
        assertEquals(expectedDto.getId(), actualDto.getId());
    }

    @Test
    void givenBadRecipeId_whenFindDtoById_thenThrowException()
    {
        // given
        final String ID = "RECIPE-1";

        // when
        when(recipeReactiveRepository.findById(anyString())).thenThrow( NotFoundException.class );
        Executable executable = () -> recipeService.findDtoById( ID ).block();

        // then
        assertThrows(NotFoundException.class, executable);
    }

    @Test
    void givenRecipeDto_whenSaveRecipe_thenSave()
    {
        // given
        final String ID = "RECIPE-1";
        final String DESC = "RECIPE-DESC";

        Recipe recipe = new Recipe();
        recipe.setId( ID );
        recipe.setDescription( DESC );

        Mono<Recipe> recipeMono = Mono.just( recipe );
        RecipeDto expectedDto = converter.convertEntity( recipe );

        // when
        when(recipeReactiveRepository.save(any())).thenReturn( recipeMono );
        RecipeDto actualDto = recipeService.saveRecipe( expectedDto ).block();

        // then
        assertEquals(expectedDto.getId(), actualDto.getId());
        assertEquals(expectedDto.getDescription(), actualDto.getDescription());
    }

    @Test
    void givenRecipeId_whenDeleteById_thenDeleteRecipe()
    {
        // given
        final String ID = "RECIPE-1";

        // when
        when(recipeReactiveRepository.deleteById(anyString())).thenReturn( Mono.empty() );
        recipeService.deleteById( ID ).block();

        // then
        verify(recipeReactiveRepository, times(1)).deleteById(anyString());
    }
}