package com.auth0.gateway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.auth0.gateway.IntegrationTest;
import com.auth0.gateway.domain.Rota;
import com.auth0.gateway.repository.EntityManager;
import com.auth0.gateway.repository.RotaRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link RotaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RotaResourceIT {

    private static final String DEFAULT_CLIENT = "AAAAAAAAAA";
    private static final String UPDATED_CLIENT = "BBBBBBBBBB";

    private static final String DEFAULT_CARER = "AAAAAAAAAA";
    private static final String UPDATED_CARER = "BBBBBBBBBB";

    private static final Instant DEFAULT_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Duration DEFAULT_DURATION = Duration.ofHours(6);
    private static final Duration UPDATED_DURATION = Duration.ofHours(12);

    private static final String ENTITY_API_URL = "/api/rotas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RotaRepository rotaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Rota rota;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rota createEntity(EntityManager em) {
        Rota rota = new Rota().client(DEFAULT_CLIENT).carer(DEFAULT_CARER).time(DEFAULT_TIME).duration(DEFAULT_DURATION);
        return rota;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rota createUpdatedEntity(EntityManager em) {
        Rota rota = new Rota().client(UPDATED_CLIENT).carer(UPDATED_CARER).time(UPDATED_TIME).duration(UPDATED_DURATION);
        return rota;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Rota.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        rota = createEntity(em);
    }

    @Test
    void createRota() throws Exception {
        int databaseSizeBeforeCreate = rotaRepository.findAll().collectList().block().size();
        // Create the Rota
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rota))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll().collectList().block();
        assertThat(rotaList).hasSize(databaseSizeBeforeCreate + 1);
        Rota testRota = rotaList.get(rotaList.size() - 1);
        assertThat(testRota.getClient()).isEqualTo(DEFAULT_CLIENT);
        assertThat(testRota.getCarer()).isEqualTo(DEFAULT_CARER);
        assertThat(testRota.getTime()).isEqualTo(DEFAULT_TIME);
        assertThat(testRota.getDuration()).isEqualTo(DEFAULT_DURATION);
    }

    @Test
    void createRotaWithExistingId() throws Exception {
        // Create the Rota with an existing ID
        rota.setId(1L);

        int databaseSizeBeforeCreate = rotaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rota))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll().collectList().block();
        assertThat(rotaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkClientIsRequired() throws Exception {
        int databaseSizeBeforeTest = rotaRepository.findAll().collectList().block().size();
        // set the field null
        rota.setClient(null);

        // Create the Rota, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rota))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Rota> rotaList = rotaRepository.findAll().collectList().block();
        assertThat(rotaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCarerIsRequired() throws Exception {
        int databaseSizeBeforeTest = rotaRepository.findAll().collectList().block().size();
        // set the field null
        rota.setCarer(null);

        // Create the Rota, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rota))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Rota> rotaList = rotaRepository.findAll().collectList().block();
        assertThat(rotaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = rotaRepository.findAll().collectList().block().size();
        // set the field null
        rota.setTime(null);

        // Create the Rota, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rota))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Rota> rotaList = rotaRepository.findAll().collectList().block();
        assertThat(rotaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDurationIsRequired() throws Exception {
        int databaseSizeBeforeTest = rotaRepository.findAll().collectList().block().size();
        // set the field null
        rota.setDuration(null);

        // Create the Rota, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rota))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Rota> rotaList = rotaRepository.findAll().collectList().block();
        assertThat(rotaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllRotasAsStream() {
        // Initialize the database
        rotaRepository.save(rota).block();

        List<Rota> rotaList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Rota.class)
            .getResponseBody()
            .filter(rota::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(rotaList).isNotNull();
        assertThat(rotaList).hasSize(1);
        Rota testRota = rotaList.get(0);
        assertThat(testRota.getClient()).isEqualTo(DEFAULT_CLIENT);
        assertThat(testRota.getCarer()).isEqualTo(DEFAULT_CARER);
        assertThat(testRota.getTime()).isEqualTo(DEFAULT_TIME);
        assertThat(testRota.getDuration()).isEqualTo(DEFAULT_DURATION);
    }

    @Test
    void getAllRotas() {
        // Initialize the database
        rotaRepository.save(rota).block();

        // Get all the rotaList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(rota.getId().intValue()))
            .jsonPath("$.[*].client")
            .value(hasItem(DEFAULT_CLIENT))
            .jsonPath("$.[*].carer")
            .value(hasItem(DEFAULT_CARER))
            .jsonPath("$.[*].time")
            .value(hasItem(DEFAULT_TIME.toString()))
            .jsonPath("$.[*].duration")
            .value(hasItem(DEFAULT_DURATION.toString()));
    }

    @Test
    void getRota() {
        // Initialize the database
        rotaRepository.save(rota).block();

        // Get the rota
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rota.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rota.getId().intValue()))
            .jsonPath("$.client")
            .value(is(DEFAULT_CLIENT))
            .jsonPath("$.carer")
            .value(is(DEFAULT_CARER))
            .jsonPath("$.time")
            .value(is(DEFAULT_TIME.toString()))
            .jsonPath("$.duration")
            .value(is(DEFAULT_DURATION.toString()));
    }

    @Test
    void getNonExistingRota() {
        // Get the rota
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRota() throws Exception {
        // Initialize the database
        rotaRepository.save(rota).block();

        int databaseSizeBeforeUpdate = rotaRepository.findAll().collectList().block().size();

        // Update the rota
        Rota updatedRota = rotaRepository.findById(rota.getId()).block();
        updatedRota.client(UPDATED_CLIENT).carer(UPDATED_CARER).time(UPDATED_TIME).duration(UPDATED_DURATION);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRota.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedRota))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll().collectList().block();
        assertThat(rotaList).hasSize(databaseSizeBeforeUpdate);
        Rota testRota = rotaList.get(rotaList.size() - 1);
        assertThat(testRota.getClient()).isEqualTo(UPDATED_CLIENT);
        assertThat(testRota.getCarer()).isEqualTo(UPDATED_CARER);
        assertThat(testRota.getTime()).isEqualTo(UPDATED_TIME);
        assertThat(testRota.getDuration()).isEqualTo(UPDATED_DURATION);
    }

    @Test
    void putNonExistingRota() throws Exception {
        int databaseSizeBeforeUpdate = rotaRepository.findAll().collectList().block().size();
        rota.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rota.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rota))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll().collectList().block();
        assertThat(rotaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRota() throws Exception {
        int databaseSizeBeforeUpdate = rotaRepository.findAll().collectList().block().size();
        rota.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rota))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll().collectList().block();
        assertThat(rotaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRota() throws Exception {
        int databaseSizeBeforeUpdate = rotaRepository.findAll().collectList().block().size();
        rota.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rota))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll().collectList().block();
        assertThat(rotaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRotaWithPatch() throws Exception {
        // Initialize the database
        rotaRepository.save(rota).block();

        int databaseSizeBeforeUpdate = rotaRepository.findAll().collectList().block().size();

        // Update the rota using partial update
        Rota partialUpdatedRota = new Rota();
        partialUpdatedRota.setId(rota.getId());

        partialUpdatedRota.time(UPDATED_TIME).duration(UPDATED_DURATION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRota.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRota))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll().collectList().block();
        assertThat(rotaList).hasSize(databaseSizeBeforeUpdate);
        Rota testRota = rotaList.get(rotaList.size() - 1);
        assertThat(testRota.getClient()).isEqualTo(DEFAULT_CLIENT);
        assertThat(testRota.getCarer()).isEqualTo(DEFAULT_CARER);
        assertThat(testRota.getTime()).isEqualTo(UPDATED_TIME);
        assertThat(testRota.getDuration()).isEqualTo(UPDATED_DURATION);
    }

    @Test
    void fullUpdateRotaWithPatch() throws Exception {
        // Initialize the database
        rotaRepository.save(rota).block();

        int databaseSizeBeforeUpdate = rotaRepository.findAll().collectList().block().size();

        // Update the rota using partial update
        Rota partialUpdatedRota = new Rota();
        partialUpdatedRota.setId(rota.getId());

        partialUpdatedRota.client(UPDATED_CLIENT).carer(UPDATED_CARER).time(UPDATED_TIME).duration(UPDATED_DURATION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRota.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRota))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll().collectList().block();
        assertThat(rotaList).hasSize(databaseSizeBeforeUpdate);
        Rota testRota = rotaList.get(rotaList.size() - 1);
        assertThat(testRota.getClient()).isEqualTo(UPDATED_CLIENT);
        assertThat(testRota.getCarer()).isEqualTo(UPDATED_CARER);
        assertThat(testRota.getTime()).isEqualTo(UPDATED_TIME);
        assertThat(testRota.getDuration()).isEqualTo(UPDATED_DURATION);
    }

    @Test
    void patchNonExistingRota() throws Exception {
        int databaseSizeBeforeUpdate = rotaRepository.findAll().collectList().block().size();
        rota.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rota.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rota))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll().collectList().block();
        assertThat(rotaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRota() throws Exception {
        int databaseSizeBeforeUpdate = rotaRepository.findAll().collectList().block().size();
        rota.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rota))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll().collectList().block();
        assertThat(rotaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRota() throws Exception {
        int databaseSizeBeforeUpdate = rotaRepository.findAll().collectList().block().size();
        rota.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rota))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll().collectList().block();
        assertThat(rotaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRota() {
        // Initialize the database
        rotaRepository.save(rota).block();

        int databaseSizeBeforeDelete = rotaRepository.findAll().collectList().block().size();

        // Delete the rota
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rota.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Rota> rotaList = rotaRepository.findAll().collectList().block();
        assertThat(rotaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
