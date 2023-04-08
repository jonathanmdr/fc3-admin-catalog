package org.fullcycle.admin.catalog.e2e;

import org.fullcycle.admin.catalog.infrastructure.configuration.json.Json;
import org.hamcrest.core.StringContains;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public interface MockMvcDsl {

    MockMvc mvc();

    default ResultActions get(
        final String url,
        final int page,
        final int perPage,
        final String search,
        final String sort,
        final String direction
    ) throws Exception {
        final var request = MockMvcRequestBuilders.get(url)
            .queryParam("page", String.valueOf(page))
            .queryParam("perPage", String.valueOf(perPage))
            .queryParam("search", search)
            .queryParam("sort", sort)
            .queryParam("direction", direction)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        return this.mvc().perform(request)
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }

    default String get(final String url, final Object id) throws Exception {
        final var request = MockMvcRequestBuilders.get("%s/{id}".formatted(url), id)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        return this.mvc().perform(request)
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andReturn()
            .getResponse()
            .getContentAsString();
    }

    default void get(final String url, final Object id, final String errorMessage) throws Exception {
        final var request = MockMvcRequestBuilders.get("%s/{id}".formatted(url), id)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        this.mvc().perform(request)
            .andExpect(status().isNotFound())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.message", equalTo(errorMessage)));
    }

    default String post(final String url, final Object body) throws Exception {
        final var request = MockMvcRequestBuilders.post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(Json.writeValueAsString(body));

        return this.mvc().perform(request)
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, StringContains.containsString("%s/".formatted(url))))
            .andReturn()
            .getResponse()
            .getContentAsString();
    }

    default String put(final String url, final Object id, final Object body) throws Exception {
        final var request = MockMvcRequestBuilders.put("%s/{id}".formatted(url), id)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(Json.writeValueAsString(body));

        return this.mvc().perform(request)
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andReturn()
            .getResponse()
            .getContentAsString();
    }

    default void delete(final String url, final Object id) throws Exception {
        final var request = MockMvcRequestBuilders.delete("%s/{id}".formatted(url), id)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        this.mvc().perform(request)
            .andExpect(status().isNoContent());
    }

}
