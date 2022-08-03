package com.auth0.gateway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.auth0.gateway.IntegrationTest;
import com.auth0.gateway.domain.Carer;
import com.auth0.gateway.domain.enumeration.Days;
import com.auth0.gateway.repository.CarerRepository;
import com.auth0.gateway.repository.EntityManager;
import java.time.Duration;
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
 * Integration tests for the {@link CarerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CarerResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_PHONE = 1L;
    private static final Long UPDATED_PHONE = 2L;

    private static final Days DEFAULT_DAYSAVAILABLE = Days.MONDAY;
    private static final Days UPDATED_DAYSAVAILABLE = Days.TUESDAY;

    private static final String ENTITY_API_URL = "/api/carers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CarerRepository carerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Carer carer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Carer createEntity(EntityManager em) {
        Carer carer = new Carer().name(DEFAULT_NAME).phone(DEFAULT_PHONE).daysavailable(DEFAULT_DAYSAVAILABLE);
        return carer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Carer createUpdatedEntity(EntityManager em) {
        Carer carer = new Carer().name(UPDATED_NAME).phone(UPDATED_PHONE).daysavailable(UPDATED_DAYSAVAILABLE);
        return carer;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Carer.class).block();
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
        carer = createEntity(em);
    }

    @Test
    void createCarer() throws Exception {
        int databaseSizeBeforeCreate = carerRepository.findAll().collectList().block().size();
        // Create the Carer
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(carer))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll().collectList().block();
        assertThat(carerList).hasSize(databaseSizeBeforeCreate + 1);
        Carer testCarer = carerList.get(carerList.size() - 1);
        assertThat(testCarer.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCarer.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testCarer.getDaysavailable()).isEqualTo(DEFAULT_DAYSAVAILABLE);
    }

    @Test
    void createCarerWithExistingId() throws Exception {
        // Create the Carer with an existing ID
        carer.setId(1L);

        int databaseSizeBeforeCreate = carerRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(carer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll().collectList().block();
        assertThat(carerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = carerRepository.findAll().collectList().block().size();
        // set the field null
        carer.setName(null);

        // Create the Carer, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(carer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Carer> carerList = carerRepository.findAll().collectList().block();
        assertThat(carerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDaysavailableIsRequired() throws Exception {
        int databaseSizeBeforeTest = carerRepository.findAll().collectList().block().size();
        // set the field null
        carer.setDaysavailable(null);

        // Create the Carer, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(carer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Carer> carerList = carerRepository.findAll().collectList().block();
        assertThat(carerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCarersAsStream() {
        // Initialize the database
        carerRepository.save(carer).block();

        List<Carer> carerList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Carer.class)
            .getResponseBody()
            .filter(carer::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(carerList).isNotNull();
        assertThat(carerList).hasSize(1);
        Carer testCarer = carerList.get(0);
        assertThat(testCarer.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCarer.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testCarer.getDaysavailable()).isEqualTo(DEFAULT_DAYSAVAILABLE);
    }

    @Test
    void getAllCarers() {
        // Initialize the database
        carerRepository.save(carer).block();

        // Get all the carerList
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
            .value(hasItem(carer.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE.intValue()))
            .jsonPath("$.[*].daysavailable")
            .value(hasItem(DEFAULT_DAYSAVAILABLE.toString()));
    }

    @Test
    void getCarer() {
        // Initialize the database
        carerRepository.save(carer).block();

        // Get the carer
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, carer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(carer.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.phone")
            .value(is(DEFAULT_PHONE.intValue()))
            .jsonPath("$.daysavailable")
            .value(is(DEFAULT_DAYSAVAILABLE.toString()));
    }

    @Test
    void getNonExistingCarer() {
        // Get the carer
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCarer() throws Exception {
        // Initialize the database
        carerRepository.save(carer).block();

        int databaseSizeBeforeUpdate = carerRepository.findAll().collectList().block().size();

        // Update the carer
        Carer updatedCarer = carerRepository.findById(carer.getId()).block();
        updatedCarer.name(UPDATED_NAME).phone(UPDATED_PHONE).daysavailable(UPDATED_DAYSAVAILABLE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCarer.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCarer))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll().collectList().block();
        assertThat(carerList).hasSize(databaseSizeBeforeUpdate);
        Carer testCarer = carerList.get(carerList.size() - 1);
        assertThat(testCarer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCarer.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testCarer.getDaysavailable()).isEqualTo(UPDATED_DAYSAVAILABLE);
    }

    @Test
    void putNonExistingCarer() throws Exception {
        int databaseSizeBeforeUpdate = carerRepository.findAll().collectList().block().size();
        carer.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, carer.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(carer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll().collectList().block();
        assertThat(carerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCarer() throws Exception {
        int databaseSizeBeforeUpdate = carerRepository.findAll().collectList().block().size();
        carer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(carer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll().collectList().block();
        assertThat(carerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCarer() throws Exception {
        int databaseSizeBeforeUpdate = carerRepository.findAll().collectList().block().size();
        carer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(carer))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll().collectList().block();
        assertThat(carerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCarerWithPatch() throws Exception {
        // Initialize the database
        carerRepository.save(carer).block();

        int databaseSizeBeforeUpdate = carerRepository.findAll().collectList().block().size();

        // Update the carer using partial update
        Carer partialUpdatedCarer = new Carer();
        partialUpdatedCarer.setId(carer.getId());

        partialUpdatedCarer.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCarer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCarer))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll().collectList().block();
        assertThat(carerList).hasSize(databaseSizeBeforeUpdate);
        Carer testCarer = carerList.get(carerList.size() - 1);
        assertThat(testCarer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCarer.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testCarer.getDaysavailable()).isEqualTo(DEFAULT_DAYSAVAILABLE);
    }

    @Test
    void fullUpdateCarerWithPatch() throws Exception {
        // Initialize the database
        carerRepository.save(carer).block();

        int databaseSizeBeforeUpdate = carerRepository.findAll().collectList().block().size();

        // Update the carer using partial update
        Carer partialUpdatedCarer = new Carer();
        partialUpdatedCarer.setId(carer.getId());

        partialUpdatedCarer.name(UPDATED_NAME).phone(UPDATED_PHONE).daysavailable(UPDATED_DAYSAVAILABLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCarer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCarer))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll().collectList().block();
        assertThat(carerList).hasSize(databaseSizeBeforeUpdate);
        Carer testCarer = carerList.get(carerList.size() - 1);
        assertThat(testCarer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCarer.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testCarer.getDaysavailable()).isEqualTo(UPDATED_DAYSAVAILABLE);
    }

    @Test
    void patchNonExistingCarer() throws Exception {
        int databaseSizeBeforeUpdate = carerRepository.findAll().collectList().block().size();
        carer.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, carer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(carer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll().collectList().block();
        assertThat(carerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCarer() throws Exception {
        int databaseSizeBeforeUpdate = carerRepository.findAll().collectList().block().size();
        carer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(carer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll().collectList().block();
        assertThat(carerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCarer() throws Exception {
        int databaseSizeBeforeUpdate = carerRepository.findAll().collectList().block().size();
        carer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(carer))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll().collectList().block();
        assertThat(carerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCarer() {
        // Initialize the database
        carerRepository.save(carer).block();

        int databaseSizeBeforeDelete = carerRepository.findAll().collectList().block().size();

        // Delete the carer
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, carer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Carer> carerList = carerRepository.findAll().collectList().block();
        assertThat(carerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
