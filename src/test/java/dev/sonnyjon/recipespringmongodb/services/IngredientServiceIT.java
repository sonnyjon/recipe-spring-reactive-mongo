package dev.sonnyjon.recipespringmongodb.services;

import dev.sonnyjon.recipespringmongodb.exceptions.NotFoundException;
import dev.sonnyjon.recipespringmongodb.model.Ingredient;
import dev.sonnyjon.recipespringmongodb.model.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Created by Sonny on 7/24/2022.
 */
@Disabled
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class IngredientServiceIT
{
    @Autowired
    IngredientService ingredientService;
    @Autowired
    RecipeService recipeService;
    Recipe testRecipe;
    Ingredient testIngredient;

    @BeforeEach
    void setUp()
    {
        testRecipe = recipeService.getRecipes()
                                    .stream()
                                    .findFirst()
                                    .orElseThrow(NotFoundException::new);

        testIngredient = testRecipe.getIngredients()
                                    .stream()
                                    .findFirst()
                                    .orElseThrow(NotFoundException::new);
    }

//    @Test
//    void findByRecipe_shouldReturnDto_whenFound()
//    {
//        // when
//        IngredientDto ingredient = ingredientService.findInRecipe( testRecipe.getId(), testIngredient.getId() );
//
//        // then
//        assertNotNull( testRecipe );
//        assertNotNull( testRecipe.getIngredients() );
//        assertNotNull( ingredient );
//    }
}