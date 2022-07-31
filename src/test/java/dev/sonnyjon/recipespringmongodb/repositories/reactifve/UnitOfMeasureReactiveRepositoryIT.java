package dev.sonnyjon.recipespringmongodb.repositories.reactifve;

import dev.sonnyjon.recipespringmongodb.model.UnitOfMeasure;
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
class UnitOfMeasureReactiveRepositoryIT
{
    @Autowired
    UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

    @BeforeEach
    void setUp()
    {
        unitOfMeasureReactiveRepository.deleteAll().block();
    }

    /*= CREATE =*/

    @Test
    void givenUom_whenSave_thenSaveUom()
    {
        final String description = "Each";

        UnitOfMeasure test = new UnitOfMeasure();
        test.setDescription( description );
        Mono<UnitOfMeasure> uomMono = unitOfMeasureReactiveRepository.save( test );

        StepVerifier
                .create( uomMono )
                .assertNext(uom -> assertNotNull( uom.getId() ))
                .expectComplete()
                .verify();
    }

    /*= READ =*/

    @Test
    void givenDescription_whenFindByDescription_thenFindUom() throws Exception
    {
        final String description = "Foo";

        UnitOfMeasure test = new UnitOfMeasure();
        test.setDescription( description );
        unitOfMeasureReactiveRepository.save( test ).then().block();

        Mono<UnitOfMeasure> uomMono = unitOfMeasureReactiveRepository.findByDescription( description );

        StepVerifier
                .create( uomMono )
                .assertNext(uom -> {
                    assertNotNull( uom.getDescription() );
                    assertEquals( description, uom.getDescription() );
                })
                .expectComplete()
                .verify();
    }

    /*= UPDATE =*/

    @Test
    void givenUom_whenUpdate_thenSaveUom()
    {
        final String description = "Foo";
        final String newDescription = "Bar";

        UnitOfMeasure test = new UnitOfMeasure();
        test.setDescription( description );
        unitOfMeasureReactiveRepository.save( test ).block();

        UnitOfMeasure toUpdate = unitOfMeasureReactiveRepository.findByDescription( description ).block();
        if (toUpdate != null) toUpdate.setDescription( newDescription );

        Mono<UnitOfMeasure> saved = unitOfMeasureReactiveRepository.save( toUpdate );

        StepVerifier
                .create( saved )
                .assertNext(uom -> {
                    assertEquals( newDescription, uom.getDescription() );
                })
                .expectComplete()
                .verify();
    }

    /*= DELETE =*/

    @Test
    void givenUom_whenDelete_thenDeleteUom()
    {
        final String description = "Foo";

        UnitOfMeasure test = new UnitOfMeasure();
        test.setDescription( description );
        UnitOfMeasure toDelete = unitOfMeasureReactiveRepository.save( test ).block();

        assertEquals(Long.valueOf(1L), unitOfMeasureReactiveRepository.count().block());

        Mono<Void> deleted = unitOfMeasureReactiveRepository.delete( toDelete );

        StepVerifier
                .create( deleted )
                .verifyComplete();

        assertEquals(0, unitOfMeasureReactiveRepository.count().block());
    }

}