package com.erickrodrigues.musicflux.integration;

import com.erickrodrigues.musicflux.catalogue.CatalogueResultDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = ITConfig.class)
@Sql({"/data-test.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CatalogueByGenreTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void findArtistsAlbumsAndSongsByGenre() {
        final ResponseEntity<CatalogueResultDto> response = restTemplate.getForEntity(
                getUrl("Synth-pop"),
                CatalogueResultDto.class
        );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getArtists().size());
        assertEquals(2, response.getBody().getAlbums().size());
        assertEquals(21, response.getBody().getSongs().size());
        assertEquals(0, response.getBody().getPlaylists().size());
    }

    @Test
    public void findArtistsAlbumsAndSongsByGenreWhenItDoesNotExist() {
        final ResponseEntity<CatalogueResultDto> response = restTemplate.getForEntity(
                getUrl("something unknown that only cult people know and like"),
                CatalogueResultDto.class
        );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getArtists().size());
        assertEquals(0, response.getBody().getAlbums().size());
        assertEquals(0, response.getBody().getSongs().size());
        assertEquals(0, response.getBody().getPlaylists().size());
    }

    private String getUrl(String genre) {
        return "http://localhost:" + port + "/catalogue/genres/" + genre;
    }
}
