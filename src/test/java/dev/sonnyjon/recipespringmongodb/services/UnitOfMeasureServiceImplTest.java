package dev.sonnyjon.recipespringmongodb.services;

import dev.sonnyjon.recipespringmongodb.converters.UnitOfMeasureConverter;
import dev.sonnyjon.recipespringmongodb.dto.UnitOfMeasureDto;
import dev.sonnyjon.recipespringmongodb.model.UnitOfMeasure;
import dev.sonnyjon.recipespringmongodb.repositories.reactifve.UnitOfMeasureReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by Sonny on 7/12/2022.
 */
@ExtendWith(SpringExtension.class)
class UnitOfMeasureServiceImplTest
{
    @Mock
    UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

    UnitOfMeasureService service;
    UnitOfMeasureConverter converter;
    AutoCloseable mocks;

    @BeforeEach
    void setUp()
    {
        mocks = MockitoAnnotations.openMocks(this);
        service = new UnitOfMeasureServiceImpl( unitOfMeasureReactiveRepository );
        converter = new UnitOfMeasureConverter();
    }

    @AfterEach
    void tearDown() throws Exception
    {
        mocks.close();
    }

    @Test
    void givenReactiveRepo_whenListAllUoms_thenFindAllUoms()
    {
        final String id1 = "UOM-1";
        final String id2 = "UOM-2";

        // given
        List<UnitOfMeasure> uoms = new ArrayList<>();
        UnitOfMeasure uom1 = new UnitOfMeasure();
        uom1.setId( id1 );

        UnitOfMeasure uom2 = new UnitOfMeasure();
        uom2.setId( id2 );

        uoms.add(uom1);
        uoms.add(uom2);

        when(unitOfMeasureReactiveRepository.findAll()).thenReturn(Flux.just( uom1, uom2 ));

        // when
        List<UnitOfMeasureDto> actual = service.listAllUoms().collectList().block();

        // then
        assertEquals(2, actual.size());
        verify(unitOfMeasureReactiveRepository, times(1)).findAll();
    }
}