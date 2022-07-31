package dev.sonnyjon.recipespringmongodb.repositories.reactifve;

import dev.sonnyjon.recipespringmongodb.model.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by Sonny on 7/29/2022.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class RecipeReactiveRepositoryIT
{
    @Autowired
    RecipeReactiveRepository recipeReactiveRepository;

    @BeforeEach
    void setUp()
    {
        recipeReactiveRepository.deleteAll().block();
    }

    /*= CREATE =*/

    @Test
    void givenRecipe_whenSave_thenSaveRecipe()
    {
        final String description = "Noodles";

        Recipe newRecipe = new Recipe();
        newRecipe.setDescription( description );
        Mono<Recipe> recipeMono = recipeReactiveRepository.save( newRecipe );

        StepVerifier
                .create( recipeMono )
                .assertNext(recipe -> assertNotNull( recipe.getId() ))
                .expectComplete()
                .verify();
    }

    /*= READ =*/

    @Test
    void givenId_whenFindById_thenFindRecipe()
    {
        final String description = "Noodles";

        Recipe newRecipe = new Recipe();
        newRecipe.setDescription( description );
        Recipe saved = recipeReactiveRepository.save( newRecipe ).block();

        Mono<Recipe> recipeMono = recipeReactiveRepository.findById( saved.getId() );

        StepVerifier
                .create( recipeMono )
                .assertNext(recipe -> {
                    assertNotNull( recipe );
                    assertEquals( description, recipe.getDescription() );
                })
                .expectComplete()
                .verify();
    }

    @Test
    void givenBadId_whenFindById_thenReturnNull()
    {
        final String badId = "Foobar";

        Recipe newRecipe = new Recipe();
        recipeReactiveRepository.save( newRecipe ).block();

        Mono<Recipe> recipeMono = recipeReactiveRepository.findById( badId );

        StepVerifier
                .create( recipeMono )
                .verifyComplete();
    }

    /*= UPDATE =*/

    @Test
    void givenRecipe_whenUpdate_thenSaveRecipe()
    {
        final String description = "Foo";
        final String newDescription = "Foobar";

        Recipe test = new Recipe();
        test.setDescription( description );
        Recipe toUpdate = recipeReactiveRepository.save( test ).block();

        toUpdate.setDescription( newDescription );
        Mono<Recipe> saved = recipeReactiveRepository.save( toUpdate );

        StepVerifier
                .create( saved )
                .assertNext(recipe -> {
                    assertEquals( newDescription, recipe.getDescription() );
                })
                .expectComplete()
                .verify();

    }

    /*= DELETE =*/

    @Test
    void givenRecipe_whenDelete_thenDelete()
    {
        final String description = "Foo";

        Recipe recipe = new Recipe();
        recipe.setDescription( description );
        Recipe toDelete = recipeReactiveRepository.save( recipe ).block();

        assertEquals(Long.valueOf(1L), recipeReactiveRepository.count().block());

        Mono<Void> deleted = recipeReactiveRepository.delete( toDelete );

        StepVerifier
                .create( deleted )
                .verifyComplete();

        assertEquals(0, recipeReactiveRepository.count().block());
    }

}