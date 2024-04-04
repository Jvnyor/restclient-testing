package com.jvnyor.demorestclient.services;

import com.jvnyor.demorestclient.dtos.CatRequestDTO;
import com.jvnyor.demorestclient.dtos.CatResponseDTO;
import com.jvnyor.demorestclient.services.exceptions.CatNotFoundException;
import com.jvnyor.demorestclient.services.exceptions.CatUnknownErrorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrudServiceTest {

    @Mock
    private RestClient restClient;

    @InjectMocks
    private CrudService crudService;

    private CatRequestDTO catRequestDTO;

    private CatResponseDTO catResponseDTO;

    @BeforeEach
    void setUp() {
        this.catRequestDTO = new CatRequestDTO("Smith", "Black", 6.0);
        this.catResponseDTO = new CatResponseDTO("1", "Smith", "Black", 6.0);
    }

    @Test
    void givenCatRequestDTO_whenCreateCat_thenReturnCatResponseDTO() {
        RestClient.RequestBodyUriSpec requestSpecMock = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec responseSpecMock = mock(RestClient.ResponseSpec.class);

        when(restClient.post()).thenReturn(requestSpecMock);
        when(requestSpecMock.uri(anyString())).thenReturn(requestSpecMock);
        when(requestSpecMock.body(any(CatRequestDTO.class))).thenReturn(requestSpecMock);
        when(requestSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.body(CatResponseDTO.class)).thenReturn(catResponseDTO);

        var catResponseDTOCreated = crudService.createCat(catRequestDTO);
        assertAll(
                () -> Assertions.assertEquals("1", catResponseDTOCreated._id()),
                () -> Assertions.assertEquals("Smith", catResponseDTOCreated.name()),
                () -> Assertions.assertEquals("Black", catResponseDTOCreated.color()),
                () -> Assertions.assertEquals(6.0, catResponseDTOCreated.weight())
        );
    }

    @Test
    void givenCatRequestDTO_whenCreateCat_butRequestFail_thenThrowException() {
        RestClient.RequestBodyUriSpec requestSpecMock = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec responseSpecMock = mock(RestClient.ResponseSpec.class);

        when(restClient.post()).thenReturn(requestSpecMock);
        when(requestSpecMock.uri(anyString())).thenReturn(requestSpecMock);
        when(requestSpecMock.body(any(CatRequestDTO.class))).thenReturn(requestSpecMock);
        when(requestSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenAnswer(invocation -> {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if (status.isError()) {
                throw new CatUnknownErrorException();
            }
            return responseSpecMock;
        });

        assertThrows(CatUnknownErrorException.class, () -> crudService.createCat(catRequestDTO));
    }

    @Test
    void givenExistingID_whenGetCat_thenReturnCatResponseDTO() {
        var requestSpecMock = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpecMock = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(requestSpecMock);
        when(requestSpecMock.uri(anyString(), anyString())).thenReturn(requestSpecMock);
        when(requestSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.body(CatResponseDTO.class)).thenReturn(catResponseDTO);

        var id = "1";
        var catResponseDTOFromGet = crudService.getCat(id);

        assertAll(
                () -> Assertions.assertEquals(id, catResponseDTOFromGet._id()),
                () -> Assertions.assertEquals("Smith", catResponseDTOFromGet.name()),
                () -> Assertions.assertEquals("Black", catResponseDTOFromGet.color()),
                () -> Assertions.assertEquals(6.0, catResponseDTOFromGet.weight())
        );
    }

    @Test
    void givenNonExistentID_whenGetCat_thenThrowException() {
        var requestSpecMock = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpecMock = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(requestSpecMock);
        when(requestSpecMock.uri(anyString(), anyString())).thenReturn(requestSpecMock);
        when(requestSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenAnswer(invocation -> {
            HttpStatus status = HttpStatus.NOT_FOUND;
            if (status.isError()) {
                throw new CatNotFoundException();
            }
            return responseSpecMock;
        });

        assertThrows(CatNotFoundException.class, () -> crudService.getCat("1"));
    }

    @Test
    void givenExistingID_whenGetCat_butRequestFail_thenThrowException() {
        var requestSpecMock = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpecMock = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(requestSpecMock);
        when(requestSpecMock.uri(anyString(), anyString())).thenReturn(requestSpecMock);
        when(requestSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenAnswer(invocation -> {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if (status.isError()) {
                throw new CatUnknownErrorException();
            }
            return responseSpecMock;
        });

        assertThrows(CatUnknownErrorException.class, () -> crudService.getCat("1"));
    }

    @Test
    void givenExistingIDAndCatRequestDTO_whenUpdateCat_thenExceptionIsNotThrown() {
        RestClient.RequestBodyUriSpec requestSpecMock = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec responseSpecMock = mock(RestClient.ResponseSpec.class);

        when(restClient.put()).thenReturn(requestSpecMock);
        when(requestSpecMock.uri(anyString(), anyString())).thenReturn(requestSpecMock);
        when(requestSpecMock.body(any(CatRequestDTO.class))).thenReturn(requestSpecMock);
        when(requestSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.toBodilessEntity()).thenReturn(ResponseEntity.noContent().build());

        var id = "1";
        var catRequestDTOUpdated = catRequestDTO.withWeight(7.0);

        assertDoesNotThrow(() -> crudService.updateCat(id, catRequestDTOUpdated));
    }

    @Test
    void givenNonExistentIDAndCatRequestDTO_whenUpdateCat_thenThrowException() {
        RestClient.RequestBodyUriSpec requestSpecMock = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec responseSpecMock = mock(RestClient.ResponseSpec.class);

        when(restClient.put()).thenReturn(requestSpecMock);
        when(requestSpecMock.uri(anyString(), anyString())).thenReturn(requestSpecMock);
        when(requestSpecMock.body(any(CatRequestDTO.class))).thenReturn(requestSpecMock);
        when(requestSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenAnswer(invocation -> {
            HttpStatus status = HttpStatus.NOT_FOUND;
            if (status.isError()) {
                throw new CatNotFoundException();
            }
            return responseSpecMock;
        });

        var id = "1";
        var catRequestDTOUpdated = catRequestDTO.withWeight(7.0);

        assertThrows(CatNotFoundException.class, () -> crudService.updateCat(id, catRequestDTOUpdated));
    }

    @Test
    void givenExistingIDAndCatRequestDTO_whenUpdateCat_butRequestFail_thenThrowException() {
        RestClient.RequestBodyUriSpec requestSpecMock = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec responseSpecMock = mock(RestClient.ResponseSpec.class);

        when(restClient.put()).thenReturn(requestSpecMock);
        when(requestSpecMock.uri(anyString(), anyString())).thenReturn(requestSpecMock);
        when(requestSpecMock.body(any(CatRequestDTO.class))).thenReturn(requestSpecMock);
        when(requestSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenAnswer(invocation -> {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if (status.isError()) {
                throw new CatUnknownErrorException();
            }
            return responseSpecMock;
        });

        var id = "1";
        var catRequestDTOUpdated = catRequestDTO.withWeight(7.0);

        assertThrows(CatUnknownErrorException.class, () -> crudService.updateCat(id, catRequestDTOUpdated));
    }

    @Test
    void givenExistingID_whenDeleteCat_thenExceptionIsNotThrown() {
        var requestSpecMock = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpecMock = mock(RestClient.ResponseSpec.class);

        when(restClient.delete()).thenReturn(requestSpecMock);
        when(requestSpecMock.uri(anyString(), anyString())).thenReturn(requestSpecMock);
        when(requestSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.toBodilessEntity()).thenReturn(ResponseEntity.noContent().build());

        var id = "1";

        assertDoesNotThrow(() -> crudService.deleteCat(id));
    }

    @Test
    void givenNonExistentID_whenDeleteCat_thenThrowException() {
        var requestSpecMock = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpecMock = mock(RestClient.ResponseSpec.class);

        when(restClient.delete()).thenReturn(requestSpecMock);
        when(requestSpecMock.uri(anyString(), anyString())).thenReturn(requestSpecMock);
        when(requestSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenAnswer(invocation -> {
            HttpStatus status = HttpStatus.NOT_FOUND;
            if (status.isError()) {
                throw new CatNotFoundException();
            }
            return responseSpecMock;
        });

        var id = "1";

        assertThrows(CatNotFoundException.class, () -> crudService.deleteCat(id));
    }

    @Test
    void givenExistingID_whenDeleteCat_butRequestFail_thenThrowException() {
        var requestSpecMock = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpecMock = mock(RestClient.ResponseSpec.class);

        when(restClient.delete()).thenReturn(requestSpecMock);
        when(requestSpecMock.uri(anyString(), anyString())).thenReturn(requestSpecMock);
        when(requestSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenAnswer(invocation -> {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if (status.isError()) {
                throw new CatUnknownErrorException();
            }
            return responseSpecMock;
        });

        var id = "1";

        assertThrows(CatUnknownErrorException.class, () -> crudService.deleteCat(id));
    }

    @Test
    void givenRequest_whenListCats_thenReturnListContainingCatResponseDTO() {
        var requestSpecMock = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpecMock = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(requestSpecMock);
        when(requestSpecMock.uri(anyString())).thenReturn(requestSpecMock);
        when(requestSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.body(any(ParameterizedTypeReference.class))).thenReturn(List.of(catResponseDTO));

        var catResponseDTOList = crudService.listCats();

        var catResponseDTOListFirst = catResponseDTOList.getFirst();
        assertAll(
                () -> Assertions.assertEquals(1, catResponseDTOList.size()),
                () -> Assertions.assertEquals("1", catResponseDTOListFirst._id()),
                () -> Assertions.assertEquals("Smith", catResponseDTOListFirst.name()),
                () -> Assertions.assertEquals("Black", catResponseDTOListFirst.color()),
                () -> Assertions.assertEquals(6.0, catResponseDTOListFirst.weight())
        );
    }

    @Test
    void givenRequest_whenListCats_butRequestFail_thenReturnListContainingCatResponseDTO() {
        var requestSpecMock = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpecMock = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(requestSpecMock);
        when(requestSpecMock.uri(anyString())).thenReturn(requestSpecMock);
        when(requestSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenAnswer(invocation -> {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if (status.isError()) {
                throw new CatUnknownErrorException();
            }
            return responseSpecMock;
        });

        assertThrows(CatUnknownErrorException.class, () -> crudService.listCats());
    }
}