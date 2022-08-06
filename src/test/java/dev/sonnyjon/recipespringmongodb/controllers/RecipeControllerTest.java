package dev.sonnyjon.recipespringmongodb.controllers;

import dev.sonnyjon.recipespringmongodb.dto.RecipeDto;
import dev.sonnyjon.recipespringmongodb.exceptions.NotFoundException;
import dev.sonnyjon.recipespringmongodb.services.RecipeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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
public class RecipeControllerTest
{
    @Mock
    RecipeService recipeService;

    RecipeController controller;
    MockMvc mockMvc;
    AutoCloseable mocks;

    @BeforeEach
    public void setUp() throws Exception
    {
        mocks = MockitoAnnotations.openMocks(this);
        controller = new RecipeController( recipeService );
        mockMvc = MockMvcBuilders.standaloneSetup( controller )
                                .setControllerAdvice(new ControllerExceptionHandler())
                                .build();
    }

    @AfterEach
    public void tearDown() throws Exception
    {
        mocks.close();
    }

    @Test
    void givenRecipeId_whenShowById_thenShowRecipeView() throws Exception
    {
        final String RECIPE_ID = "RECIPE-1";
        final String TEST_URI = String.format("/recipe/%s/show", RECIPE_ID);
        final String EXPECTED_RETURN = "recipe/show";

        // given
        RecipeDto recipe = new RecipeDto();
        recipe.setId( RECIPE_ID );

        // when, then
        when(recipeService.findDtoById(anyString())).thenReturn(Mono.just( recipe ));

        mockMvc.perform(get( TEST_URI ))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("recipe"))
                .andExpect(forwardedUrl( EXPECTED_RETURN ));

        verify(recipeService, times(1)).findDtoById(anyString());
    }

    @Test
    void givenBadRecipeId_whenShowById_thenThrowException() throws Exception
    {
        final String RECIPE_ID = "RECIPE-1";
        final String TEST_URI = String.format("/recipe/%s/show", RECIPE_ID);

        // given
        when(recipeService.findDtoById(anyString())).thenThrow( NotFoundException.class );

        // when
        mockMvc.perform(get( TEST_URI ))
                .andExpect( status().isNotFound() )
                .andExpect(
                    result -> assertTrue(result.getResolvedException() instanceof NotFoundException)
                )
                .andExpect(forwardedUrl( "404error" ));

        // then
        verify(recipeService, times(1)).findDtoById(anyString());
    }

    @Test
    void whenNewRecipe_thenRecipeFormView() throws Exception
    {
        final String TEST_URI = "/recipe/new";
        final String EXPECTED_RETURN = "recipe/recipeform";

        // when, then
        mockMvc.perform(get( TEST_URI ))
                .andExpect( status().isOk() )
                .andExpect(model().attributeExists("recipe"))
                .andExpect(forwardedUrl( EXPECTED_RETURN ));
    }

    @Test
    void givenRecipeId_whenUpdateRecipe_thenRecipeFormView() throws Exception
    {
        final String RECIPE_ID = "RECIPE-1";
        final String TEST_URI = String.format("/recipe/%s/update", RECIPE_ID);
        final String EXPECTED_RETURN = "recipe/recipeform";

        // given
        RecipeDto recipe = new RecipeDto();
        recipe.setId( RECIPE_ID );

        // when, then
        when(recipeService.findDtoById(anyString())).thenReturn(Mono.just( recipe ));

        mockMvc.perform(get( TEST_URI ))
                .andExpect( status().isOk() )
                .andExpect(model().attributeExists("recipe"))
                .andExpect(forwardedUrl( EXPECTED_RETURN ));

        verify(recipeService, times(1)).findDtoById(anyString());
    }

    @Test
    void givenBadRecipeId_whenUpdateRecipe_thenThrowException() throws Exception
    {
        final String RECIPE_ID = "RECIPE-1";
        final String TEST_URI = String.format("/recipe/%s/update", RECIPE_ID);

        // given
        when(recipeService.findDtoById(anyString())).thenThrow(NotFoundException.class);

        // when, then
        mockMvc.perform(get( TEST_URI ))
                .andExpect( status().isNotFound() )
                .andExpect(
                    result -> assertTrue(result.getResolvedException() instanceof NotFoundException)
                )
                .andExpect(forwardedUrl( "404error" ));

        verify(recipeService, times(1)).findDtoById(anyString());
    }

    @Test
    void givenRecipeDto_andValidInputs_whenSaveOrUpdate_thenSave_andShowRecipeView() throws Exception
    {
        final String RECIPE_ID = "RECIPE-1";
        final String TEST_URI = "/recipe";
        final String EXPECTED_RETURN = String.format("/recipe/%s/show", RECIPE_ID);

        // given
        RecipeDto expectedRecipe = new RecipeDto();
        expectedRecipe.setId( RECIPE_ID );

        // when, then
        when(recipeService.saveRecipe(any())).thenReturn(Mono.just( expectedRecipe ));

        mockMvc.perform(post( TEST_URI )
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "")
                        .param("description", "My new recipe")
                        .param("descriptions", "This is how you do it")
                )
                .andExpect( status().is3xxRedirection() )
                .andExpect(redirectedUrl( EXPECTED_RETURN ));
    }

    @Test
    void givenRecipeId_whenDeleteById_thenDelete_andRedirectToRoot() throws Exception
    {
        final String RECIPE_ID = "RECIPE-1";
        final String TEST_URI = String.format("/recipe/%s/delete", RECIPE_ID);
        final String EXPECTED_RETURN = "/";

        // given
        when(recipeService.deleteById(anyString())).thenReturn( Mono.empty() );

        // when, then
        mockMvc.perform(get( TEST_URI ))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl( EXPECTED_RETURN ));

        verify(recipeService, times(1)).deleteById(anyString());
    }
}