package dev.sonnyjon.recipespringmongodb.repositories.reactifve;

import dev.sonnyjon.recipespringmongodb.model.Category;
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
 * Created by Sonny on 7/31/2022.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class CategoryReactiveRepositoryIT
{
    @Autowired
    CategoryReactiveRepository categoryReactiveRepository;

    @BeforeEach
    void setUp()
    {
        categoryReactiveRepository.deleteAll().block();
    }

    /*= CREATE =*/

    @Test
    void givenCategory_whenSave_thenSaveCategory()
    {
        final String description = "Foo";

        Category newCategory = new Category();
        newCategory.setDescription( description );
        Mono<Category> categoryMono = categoryReactiveRepository.save( newCategory );

        StepVerifier
            .create( categoryMono )
            .assertNext(category -> assertNotNull( category.getId() ))
            .expectComplete()
            .verify();
    }

    /*= READ =*/

    @Test
    void givenDescription_whenFindByDescription_thenFindCategory()
    {
        final String description = "Foo";

        Category category = new Category();
        category.setDescription( description );
        categoryReactiveRepository.save( category ).then().block();

        Mono<Category> fetchedCat = categoryReactiveRepository.findByDescription( description );

        StepVerifier
            .create( fetchedCat )
            .assertNext(cat -> {
                assertNotNull( cat.getDescription() );
                assertEquals( description, cat.getDescription() );
            })
            .expectComplete()
            .verify();
    }

    @Test
    void givenBadDescription_whenFindByDescription_thenReturnNull()
    {
        final String description = "Foo";
        final String badDescription = "Foobar";

        Category category = new Category();
        category.setDescription( description );
        categoryReactiveRepository.save( category ).then().block();

        Mono<Category> fetchedCat = categoryReactiveRepository.findByDescription( badDescription );

        StepVerifier
                .create( fetchedCat )
                .verifyComplete();
    }

    /*= UPDATE =*/

    @Test
    void givenCategory_whenUpdate_thenSaveCategory()
    {
        final String description = "Foo";
        final String newDescription = "Foobar";

        Category test = new Category();
        test.setDescription( description );
        categoryReactiveRepository.save( test ).block();

        Category toUpdate = categoryReactiveRepository.findByDescription( description ).block();
        if (toUpdate != null) toUpdate.setDescription( newDescription );

        Mono<Category> saved = categoryReactiveRepository.save( toUpdate );

        StepVerifier
                .create( saved )
                .assertNext(category -> {
                    assertEquals( newDescription, category.getDescription() );
                })
                .expectComplete()
                .verify();
    }

    /*= DELETE =*/

    @Test
    void givenCategory_whenDelete_thenDeleteCategory()
    {
        final String description = "Foo";

        Category category = new Category();
        category.setDescription( description );
        Category toDelete = categoryReactiveRepository.save( category ).block();

        assertEquals(Long.valueOf(1L), categoryReactiveRepository.count().block());

        Mono<Void> deleted = categoryReactiveRepository.delete( toDelete );

        StepVerifier
                .create( deleted )
                .verifyComplete();

        assertEquals(0, categoryReactiveRepository.count().block());
    }
}