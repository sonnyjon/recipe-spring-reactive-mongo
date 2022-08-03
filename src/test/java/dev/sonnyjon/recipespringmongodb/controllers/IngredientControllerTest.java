package dev.sonnyjon.recipespringmongodb.controllers;

import dev.sonnyjon.recipespringmongodb.dto.IngredientDto;
import dev.sonnyjon.recipespringmongodb.dto.RecipeDto;
import dev.sonnyjon.recipespringmongodb.dto.UnitOfMeasureDto;
import dev.sonnyjon.recipespringmongodb.exceptions.NotFoundException;
import dev.sonnyjon.recipespringmongodb.model.Recipe;
import dev.sonnyjon.recipespringmongodb.services.IngredientService;
import dev.sonnyjon.recipespringmongodb.services.RecipeService;
import dev.sonnyjon.recipespringmongodb.services.UnitOfMeasureService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Sonny on 7/15/2022.
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class IngredientControllerTest
{
    @Mock
    UnitOfMeasureService unitOfMeasureService;
    @Mock
    RecipeService recipeService;
    @Mock
    IngredientService ingredientService;
    IngredientController controller;

    MockMvc mockMvc;
    AutoCloseable mocks;

    @BeforeEach
    void setUp()
    {
        mocks = MockitoAnnotations.openMocks(this);
        controller = new IngredientController(ingredientService, recipeService, unitOfMeasureService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @AfterEach
    void tearDown() throws Exception
    {
        mocks.close();
    }

    @Test
    void givenRecipeId_whenFindById_thenIngredientListView() throws Exception
    {
        final String RECIPE_ID = "RECIPE-1";
        final String TEST_URI = String.format("/recipe/%s/ingredients", RECIPE_ID);
        final String EXPECTED_RETURN = "recipe/ingredient/list";

        // given
        final RecipeDto recipe = new RecipeDto();
        recipe.setId( RECIPE_ID );

        // when, then
        when(recipeService.findDtoById( RECIPE_ID )).thenReturn( recipe );

        mockMvc.perform(get( TEST_URI ))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("recipe"))
                .andExpect(forwardedUrl( EXPECTED_RETURN ));

        verify(recipeService, times(1)).findDtoById(anyString());
    }

    @Test
    void givenBadRecipeId_whenFindById_thenThrowException() throws Exception
    {
        final String RECIPE_ID = "RECIPE-1";
        final String TEST_URI = String.format("/recipe/%s/ingredients", RECIPE_ID);

        // given
        final RecipeDto recipe = new RecipeDto();
        recipe.setId( RECIPE_ID );

        // when
        when(recipeService.findDtoById( RECIPE_ID )).thenThrow( NotFoundException.class );

        mockMvc.perform(get( TEST_URI ))
                .andExpect( status().isNotFound() )
                .andExpect(
                    result -> assertTrue(result.getResolvedException() instanceof NotFoundException)
                );

        // then
        verify(recipeService, times(1)).findDtoById(anyString());
    }

    @Test
    void givenBothIds_whenFindInRecipe_thenShowIngredientView() throws Exception
    {
        final String RECIPE_ID = "RECIPE-1";
        final String INGRED_ID = "INGRED-1";
        final String TEST_URI = String.format("/recipe/%1$s/ingredient/%2$s/show", RECIPE_ID, INGRED_ID);
        final String EXPECTED_RETURN = "recipe/ingredient/show";

        // given
        Recipe recipe = new Recipe();
        recipe.setId( RECIPE_ID );

        IngredientDto ingredient = new IngredientDto();
        ingredient.setId( INGRED_ID );

        // when, then
        when(ingredientService.findInRecipe( RECIPE_ID, INGRED_ID )).thenReturn(Mono.just( ingredient ));

        mockMvc.perform(get( TEST_URI ))
                .andExpect( status().isOk() )
                .andExpect( model().attributeExists("ingredient") )
                .andExpect( forwardedUrl( EXPECTED_RETURN ) );

        verify(ingredientService, times(1)).findInRecipe(anyString(), anyString());
    }

    @Test
    void givenBadIds_whenFindInRecipe_thenThrowException() throws Exception
    {
        final String RECIPE_ID = "RECIPE-1";
        final String INGRED_ID = "INGRED-1";
        final String TEST_URI = String.format("/recipe/%1$s/ingredient/%2$s/show", RECIPE_ID, INGRED_ID);

        // given
        when(ingredientService.findInRecipe( RECIPE_ID, INGRED_ID )).thenThrow( NotFoundException.class );

        // when, then
        mockMvc.perform(get( TEST_URI ))
                .andExpect( status().isNotFound() )
                .andExpect(
                    result -> assertTrue(result.getResolvedException() instanceof NotFoundException)
                );

        verify(ingredientService, times(1)).findInRecipe(anyString(), anyString());
    }

    @Test
    void givenRecipeId_whenNewIngredient_thenIngredientFormView() throws Exception
    {
        final String RECIPE_ID = "RECIPE-1";
        final String TEST_URI = String.format("/recipe/%s/ingredient/new", RECIPE_ID);
        final String EXPECTED_RETURN = "recipe/ingredient/ingredientform";

        // given
        final RecipeDto testRecipe = new RecipeDto();
        testRecipe.setId( RECIPE_ID );
        Flux<UnitOfMeasureDto> uoms = Flux.just( new UnitOfMeasureDto() );

        // when, then
        when(recipeService.findDtoById( RECIPE_ID )).thenReturn( testRecipe );
        when(unitOfMeasureService.listAllUoms()).thenReturn( uoms );

        mockMvc.perform(get( TEST_URI ))
                .andExpect( status().isOk() )
                .andExpect(model().attributeExists("recipeId"))
                .andExpect(model().attributeExists("ingredient"))
                .andExpect(model().attributeExists("uomList"))
                .andExpect(forwardedUrl( EXPECTED_RETURN ));

        verify(recipeService, times(1)).findDtoById(anyString());
        verify(unitOfMeasureService, times(1)).listAllUoms();
    }

    @Test
    void givenBadRecipe_whenNewIngredient_thenThrowException() throws Exception
    {
        final String RECIPE_ID = "RECIPE-1";
        final String TEST_URI = String.format("/recipe/%s/ingredient/new", RECIPE_ID);

        // given
        final RecipeDto testRecipe = new RecipeDto();
        testRecipe.setId( RECIPE_ID );

        // when, then
        when(recipeService.findDtoById( RECIPE_ID )).thenThrow( NotFoundException.class );

        mockMvc.perform(get( TEST_URI ))
                .andExpect( status().isNotFound() )
                .andExpect(
                    result -> assertTrue(result.getResolvedException() instanceof NotFoundException)
                );

        verify(recipeService, times(1)).findDtoById(anyString());
    }

    @Test
    void givenBothIds_whenUpdateRecipeIngredient_thenIngredientFormView() throws Exception
    {
        final String RECIPE_ID = "RECIPE-1";
        final String INGRED_ID = "INGRED-1";
        final String TEST_URI = String.format("/recipe/%1$s/ingredient/%2$s/update", RECIPE_ID, INGRED_ID);
        final String EXPECTED_RETURN = "recipe/ingredient/ingredientform";

        // given
        IngredientDto ingredient = new IngredientDto();
        ingredient.setId( INGRED_ID );
        Flux<UnitOfMeasureDto> uoms = Flux.just( new UnitOfMeasureDto() );

        // when, then
        when(ingredientService.findInRecipe( RECIPE_ID, INGRED_ID )).thenReturn(Mono.just( ingredient ));
        when(unitOfMeasureService.listAllUoms()).thenReturn( uoms );

        mockMvc.perform(get( TEST_URI ))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("ingredient"))
                .andExpect(model().attributeExists("uomList"))
                .andExpect(forwardedUrl( EXPECTED_RETURN ));

        verify(ingredientService, times(1)).findInRecipe(anyString(), anyString());
        verify(unitOfMeasureService, times(1)).listAllUoms();
    }

    @Test
    void givenBadIds_whenUpdateRecipeIngredient_thenThrowException() throws Exception
    {
        final String RECIPE_ID = "RECIPE-1";
        final String INGRED_ID = "INGRED-1";
        final String TEST_URI = String.format("/recipe/%1$s/ingredient/%2$s/update", RECIPE_ID, INGRED_ID);

        // given
        when(ingredientService.findInRecipe( RECIPE_ID, INGRED_ID )).thenThrow( NotFoundException.class );

        // when, then
        mockMvc.perform(get( TEST_URI ))
                .andExpect( status().isNotFound() )
                .andExpect(
                    result -> assertTrue(result.getResolvedException() instanceof NotFoundException)
                );

        verify(ingredientService, times(1)).findInRecipe(anyString(), anyString());
    }

    // TODO Not working. Debug this once resources are in place
    @Test
    void givenRecipeIdAndIngredient_whenSaveOrUpdate_thenSave_andShowIngredientView() throws Exception
    {
        final String RECIPE_ID = "RECIPE-1";
        final String INGRED_ID = "INGRED-1";
        final String TEST_URI = String.format("/recipe/%s/ingredient", RECIPE_ID);
        final String EXPECTED_RETURN = String.format("/recipe/%1$s/ingredient/%2$s/show", RECIPE_ID, INGRED_ID);

        // given
        IngredientDto expected = new IngredientDto();
        expected.setId( INGRED_ID );

        when(ingredientService.saveIngredient(anyString(), any())).thenReturn(Mono.just( expected ));

        // when, then
        mockMvc.perform(post( TEST_URI )
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "")
                        .param("description", "My new ingredient")
                        .param("amount", "3")
                )
                .andExpect( status().is3xxRedirection() )
                .andExpect(redirectedUrl( EXPECTED_RETURN ));

        verify(ingredientService, times(1)).saveIngredient(anyString(), any());
    }

    @Test
    void givenBadRecipe_whenSaveOrUpdate_thenThrowException() throws Exception
    {
        final String RECIPE_ID = "RECIPE-1";
        final String TEST_URI = String.format("/recipe/%s/ingredient", RECIPE_ID);

        // given
        when(ingredientService.saveIngredient(anyString(), any())).thenThrow( NotFoundException.class );

        // when, then
        mockMvc.perform(post( TEST_URI )
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "")
                        .param("description", "My new ingredient")
                        .param("amount", "3")
                )
                .andExpect( status().isNotFound() )
                .andExpect(
                    result -> assertTrue(result.getResolvedException() instanceof NotFoundException)
                );

        verify(ingredientService, times(1)).saveIngredient(anyString(), any());
    }

    @Test
    void givenBothIds_whenDeleteIngredient_thenDeleteAndRedirectView() throws Exception
    {
        final String RECIPE_ID = "RECIPE-1";
        final String INGRED_ID = "INGRED-1";
        final String TEST_URI = String.format("/recipe/%1$s/ingredient/%2$s/delete", RECIPE_ID, INGRED_ID);
        final String EXPECTED_RETURN = String.format("/recipe/%s/ingredients", RECIPE_ID);

        // given
        when(ingredientService.removeIngredient(anyString(), anyString())).thenReturn( Mono.empty() );

        // when, then
        mockMvc.perform(get( TEST_URI ))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl( EXPECTED_RETURN ));

        verify(ingredientService, times(1)).removeIngredient(anyString(), anyString());
    }

    @Test
    void givenBadIds_whenDeleteIngredient_thenThrowException() throws Exception
    {
        final String RECIPE_ID = "RECIPE-1";
        final String INGRED_ID = "INGRED-1";
        final String TEST_URI = String.format("/recipe/%1$s/ingredient/%2$s/delete", RECIPE_ID, INGRED_ID);

        // given
        when(ingredientService.removeIngredient(anyString(), anyString())).thenThrow( NotFoundException.class );

        // when, then
        mockMvc.perform(get( TEST_URI ))
                .andExpect( status().isNotFound() )
                .andExpect(
                    result -> assertTrue(result.getResolvedException() instanceof NotFoundException)
                );

        verify(ingredientService, times(1)).removeIngredient(anyString(), anyString());
    }
}