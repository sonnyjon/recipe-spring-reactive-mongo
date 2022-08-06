package dev.sonnyjon.recipespringmongodb.services;

import dev.sonnyjon.recipespringmongodb.converters.IngredientConverter;
import dev.sonnyjon.recipespringmongodb.dto.IngredientDto;
import dev.sonnyjon.recipespringmongodb.exceptions.NotFoundException;
import dev.sonnyjon.recipespringmongodb.model.Ingredient;
import dev.sonnyjon.recipespringmongodb.model.Recipe;
import dev.sonnyjon.recipespringmongodb.model.UnitOfMeasure;
import dev.sonnyjon.recipespringmongodb.repositories.reactifve.RecipeReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Created by Sonny on 7/20/2022.
 */
@ExtendWith(SpringExtension.class)
class IngredientServiceMongoImplTest
{
    public static final String RECIPE_ID = "RECIPE-1";
    public static final String INGRED1_ID = "INGRED1-ID";
    public static final String INGRED2_ID = "INGRED2-ID";
    public static final String INGRED1_DESC = "INGRED1-DESC";
    public static final String INGRED2_DESC = "INGRED2-DESC";
    public static final BigDecimal AMOUNT = new BigDecimal(2);
    public static final String UOM_ID = "UOM-1";

    @Mock
    RecipeReactiveRepository recipeReactiveRepository;

    IngredientService ingredientService;
    IngredientConverter converter;
    AutoCloseable mocks;

    @BeforeEach
    void setUp()
    {
        mocks = MockitoAnnotations.openMocks(this);
        ingredientService = new IngredientServiceMongoImpl( recipeReactiveRepository );
        converter = new IngredientConverter();
    }

    @AfterEach
    void tearDown() throws Exception
    {
        mocks.close();
    }

    @Test
    void givenBothIds_whenFindInRecipe_thenFindIngredient()
    {
        // given
        Recipe testRecipe = getTestRecipeWithTwoIngredients();
        Mono<Recipe> recipeMono = Mono.just( testRecipe );
        Ingredient testIngredient = getTestIngredient( INGRED1_ID, INGRED1_DESC );

        // when
        when(recipeReactiveRepository.findById(anyString())).thenReturn( recipeMono );

        IngredientDto expected = converter.convertEntity( testIngredient );
        IngredientDto actual = ingredientService.findInRecipe( testRecipe.getId(), expected.getId() ).block();

        // then
        assertEquals( expected.getDescription(), actual.getDescription() );
        assertEquals( expected.getAmount(), actual.getAmount() );
        assertEquals( expected.getUom().getId(), actual.getUom().getId() );

        verify(recipeReactiveRepository, times(1)).findById(anyString());
    }

    @Test
    void givenBadIds_whenFindInRecipe_thenThrowException()
    {
        // given
        Recipe testRecipe = getTestRecipeWithTwoIngredients();
        Ingredient testIngredient = getTestIngredient( INGRED1_ID, INGRED1_DESC );
        IngredientDto expected = converter.convertEntity( testIngredient );

        // when
        when(recipeReactiveRepository.findById(anyString())).thenThrow( NotFoundException.class );
        Executable executable = () -> ingredientService.findInRecipe( testRecipe.getId(), expected.getId() ).block();

        // then
        assertThrows( NotFoundException.class, executable );
    }

    @Test
    void givenNewIngredient_whenSaveIngredient_thenAddIngredient()
    {
        // given
        Recipe testRecipe = getTestRecipeWithNoIngredient();
        Recipe expectedRecipe = getTestRecipeWithOneIngredient();

        Ingredient expectedIngredient = getTestIngredient( INGRED1_ID, INGRED1_DESC );
        IngredientDto testIngredient = converter.convertEntity( expectedIngredient );

        // when
        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just( testRecipe ));
        when(recipeReactiveRepository.save(any())).thenReturn(Mono.just( expectedRecipe ));

        IngredientDto actualDto = ingredientService.saveIngredient( testRecipe.getId(), testIngredient ).block();

        // then
        assertNotNull( actualDto.getId() );
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, times(1)).save(any());
    }

    @Test
    void givenExistingIngredient_whenSaveIngredient_thenUpdateIngredient()
    {
        // given
        final String DESC = "CHANGED";

        Ingredient expectedIngredient = getTestIngredient( INGRED1_ID, DESC );
        Recipe testRecipe = getTestRecipeWithOneIngredient();

        Recipe expectedRecipe = getTestRecipeWithOneIngredient();
        expectedRecipe.getIngredients().clear();
        expectedRecipe.addIngredient( expectedIngredient );

        IngredientDto testIngredient = converter.convertEntity( expectedIngredient );

        // when
        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just( testRecipe ));
        when(recipeReactiveRepository.save(any())).thenReturn(Mono.just( expectedRecipe ));

        IngredientDto actualDto = ingredientService.saveIngredient( testRecipe.getId(), testIngredient ).block();

        // then
        assertEquals( expectedIngredient.getId(), actualDto.getId() );
        assertEquals( DESC, actualDto.getDescription() );
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, times(1)).save(any());
    }

    @Test
    void givenBadRecipe_whenSaveIngredient_thenThrowException()
    {
        // given
        Recipe testRecipe = getTestRecipeWithTwoIngredients();
        Ingredient expected = getTestIngredient( INGRED1_ID, INGRED1_DESC );
        IngredientDto testIngredient = converter.convertEntity( expected );

        when(recipeReactiveRepository.findById(anyString())).thenThrow( NotFoundException.class );

        // when
        Executable executable = () -> ingredientService.saveIngredient( testRecipe.getId(), testIngredient ).block();

        // then
        assertThrows( NotFoundException.class, executable );
        verify(recipeReactiveRepository, times(1)).findById(anyString());
    }

    @Test
    void givenBothIds_whenRemoveIngredient_thenRemoveAndDelete()
    {
        // given
        Recipe testRecipe = getTestRecipeWithTwoIngredients();
        Recipe expectedRecipe = getTestRecipeWithOneIngredient();

        Ingredient testIngredient = getTestIngredient( INGRED2_ID, INGRED2_DESC );

        // when
        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just( testRecipe ));
        when(recipeReactiveRepository.save(any())).thenReturn(Mono.just( expectedRecipe ));

        ingredientService.removeIngredient( testRecipe.getId(), testIngredient.getId() ).block();

        // then
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, times(1)).save(any());
    }

    @Test
    void givenBadRecipe_whenRemoveIngredient_thenThrowException()
    {
        // given
        Recipe testRecipe = getTestRecipeWithTwoIngredients();
        Ingredient testIngredient = getTestIngredient( INGRED1_ID, INGRED1_DESC );

        // when
        when(recipeReactiveRepository.findById(anyString())).thenThrow( NotFoundException.class );
        Executable executable = () -> ingredientService.removeIngredient( testRecipe.getId(), testIngredient.getId() )
                                                        .block();

        // then
        assertThrows( NotFoundException.class, executable );
    }

    //==================================================================================================================
    private Recipe getTestRecipeWithOneIngredient()
    {
        Recipe recipe = new Recipe();
        recipe.setId( RECIPE_ID );
        recipe.addIngredient(getTestIngredient( INGRED1_ID, INGRED1_DESC ));

        return recipe;
    }

    private Recipe getTestRecipeWithTwoIngredients()
    {
        Recipe recipe = getTestRecipeWithOneIngredient();
        recipe.addIngredient(getTestIngredient( INGRED2_ID, INGRED2_DESC ));

        return recipe;
    }

    private Recipe getTestRecipeWithNoIngredient()
    {
        Recipe recipe = new Recipe();
        recipe.setId( RECIPE_ID );

        return recipe;
    }

    private Ingredient getTestIngredient(String ingredientId, String description)
    {
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setId( UOM_ID );

        Ingredient ingredient = new Ingredient();
        ingredient.setId( ingredientId );
        ingredient.setDescription( description );
        ingredient.setAmount( AMOUNT );
        ingredient.setUom( uom );

        return ingredient;
    }
}