package com.jvnyor.demorestclient.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvnyor.demorestclient.dtos.CatRequestDTO;
import com.jvnyor.demorestclient.dtos.CatResponseDTO;
import com.jvnyor.demorestclient.services.exceptions.CatNotFoundException;
import com.jvnyor.demorestclient.services.exceptions.CatUnknownErrorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(CrudService.class)
class CrudServiceTest {

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private CrudService crudService;

    @Autowired
    private ObjectMapper objectMapper;

    private CatRequestDTO catRequestDTO;

    private CatResponseDTO catResponseDTO;

    @BeforeEach
    void setUp() {
        this.catRequestDTO = new CatRequestDTO("Smith", "Black", 6.0);
        this.catResponseDTO = new CatResponseDTO("1", "Smith", "Black", 6.0);
    }

    @Test
    void givenCatRequestDTO_whenCreateCat_thenReturnCatResponseDTO() throws JsonProcessingException {
        server.expect(requestTo("/cats"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(objectMapper.writeValueAsString(catResponseDTO), MediaType.APPLICATION_JSON));

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
        server.expect(requestTo("/cats"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        assertThrows(CatUnknownErrorException.class, () -> crudService.createCat(catRequestDTO));
    }

    @Test
    void givenExistingID_whenGetCat_thenReturnCatResponseDTO() throws JsonProcessingException {
        server.expect(requestTo("/cats/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(catResponseDTO), MediaType.APPLICATION_JSON));

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
        server.expect(requestTo("/cats/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(CatNotFoundException.class, () -> crudService.getCat("1"));
    }

    @Test
    void givenExistingID_whenGetCat_butRequestFail_thenThrowException() {
        server.expect(requestTo("/cats/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        assertThrows(CatUnknownErrorException.class, () -> crudService.getCat("1"));
    }

    @Test
    void givenExistingIDAndCatRequestDTO_whenUpdateCat_thenExceptionIsNotThrown() {
        server.expect(requestTo("/cats/1"))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withNoContent());

        var id = "1";
        var catRequestDTOUpdated = catRequestDTO.withWeight(7.0);

        assertDoesNotThrow(() -> crudService.updateCat(id, catRequestDTOUpdated));
    }

    @Test
    void givenNonExistentIDAndCatRequestDTO_whenUpdateCat_thenThrowException() {
        server.expect(requestTo("/cats/1"))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        var id = "1";
        var catRequestDTOUpdated = catRequestDTO.withWeight(7.0);

        assertThrows(CatNotFoundException.class, () -> crudService.updateCat(id, catRequestDTOUpdated));
    }

    @Test
    void givenExistingIDAndCatRequestDTO_whenUpdateCat_butRequestFail_thenThrowException() {
        server.expect(requestTo("/cats/1"))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withServerError());

        var id = "1";
        var catRequestDTOUpdated = catRequestDTO.withWeight(7.0);

        assertThrows(CatUnknownErrorException.class, () -> crudService.updateCat(id, catRequestDTOUpdated));
    }

    @Test
    void givenExistingID_whenDeleteCat_thenExceptionIsNotThrown() {
        server.expect(requestTo("/cats/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withNoContent());

        var id = "1";

        assertDoesNotThrow(() -> crudService.deleteCat(id));
    }

    @Test
    void givenNonExistentID_whenDeleteCat_thenThrowException() {
        server.expect(requestTo("/cats/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        var id = "1";

        assertThrows(CatNotFoundException.class, () -> crudService.deleteCat(id));
    }

    @Test
    void givenExistingID_whenDeleteCat_butRequestFail_thenThrowException() {
        server.expect(requestTo("/cats/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withServerError());

        var id = "1";

        assertThrows(CatUnknownErrorException.class, () -> crudService.deleteCat(id));
    }

    @Test
    void givenRequest_whenListCats_thenReturnListContainingCatResponseDTO() throws JsonProcessingException {
        server.expect(requestTo("/cats"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(new CatResponseDTO[]{catResponseDTO}), MediaType.APPLICATION_JSON));

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
        server.expect(requestTo("/cats"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        assertThrows(CatUnknownErrorException.class, () -> crudService.listCats());
    }
}