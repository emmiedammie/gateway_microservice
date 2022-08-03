package com.auth0.gateway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.auth0.gateway.IntegrationTest;
import com.auth0.gateway.domain.Visit;
import com.auth0.gateway.domain.enumeration.Status;
import com.auth0.gateway.repository.EntityManager;
import com.auth0.gateway.repository.VisitRepository;
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
 * Integration tests for the {@link VisitResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class VisitResourceIT {

    private static final String DEFAULT_CLIENT = "AAAAAAAAAA";
    private static final String UPDATED_CLIENT = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_CARER = "AAAAAAAAAA";
    private static final String UPDATED_CARER = "BBBBBBBBBB";

    private static final Integer DEFAULT_ACCESSCODE = 1;
    private static final Integer UPDATED_ACCESSCODE = 2;

    private static final Instant DEFAULT_TIMEIN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIMEIN = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Status DEFAULT_STATUS = Status.PENDING;
    private static final Status UPDATED_STATUS = Status.COMPLETED;

    private static final Duration DEFAULT_TIMESPENT = Duration.ofHours(6);
    private static final Duration UPDATED_TIMESPENT = Duration.ofHours(12);

    private static final String ENTITY_API_URL = "/api/visits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Visit visit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Visit createEntity(EntityManager em) {
        Visit visit = new Visit()
            .client(DEFAULT_CLIENT)
            .address(DEFAULT_ADDRESS)
            .carer(DEFAULT_CARER)
            .accesscode(DEFAULT_ACCESSCODE)
            .timein(DEFAULT_TIMEIN)
            .status(DEFAULT_STATUS)
            .timespent(DEFAULT_TIMESPENT);
        return visit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Visit createUpdatedEntity(EntityManager em) {
        Visit visit = new Visit()
            .client(UPDATED_CLIENT)
            .address(UPDATED_ADDRESS)
            .carer(UPDATED_CARER)
            .accesscode(UPDATED_ACCESSCODE)
            .timein(UPDATED_TIMEIN)
            .status(UPDATED_STATUS)
            .timespent(UPDATED_TIMESPENT);
        return visit;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Visit.class).block();
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
        visit = createEntity(em);
    }

    @Test
    void createVisit() throws Exception {
        int databaseSizeBeforeCreate = visitRepository.findAll().collectList().block().size();
        // Create the Visit
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(visit))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll().collectList().block();
        assertThat(visitList).hasSize(databaseSizeBeforeCreate + 1);
        Visit testVisit = visitList.get(visitList.size() - 1);
        assertThat(testVisit.getClient()).isEqualTo(DEFAULT_CLIENT);
        assertThat(testVisit.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testVisit.getCarer()).isEqualTo(DEFAULT_CARER);
        assertThat(testVisit.getAccesscode()).isEqualTo(DEFAULT_ACCESSCODE);
        assertThat(testVisit.getTimein()).isEqualTo(DEFAULT_TIMEIN);
        assertThat(testVisit.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testVisit.getTimespent()).isEqualTo(DEFAULT_TIMESPENT);
    }

    @Test
    void createVisitWithExistingId() throws Exception {
        // Create the Visit with an existing ID
        visit.setId(1L);

        int databaseSizeBeforeCreate = visitRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(visit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll().collectList().block();
        assertThat(visitList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkClientIsRequired() throws Exception {
        int databaseSizeBeforeTest = visitRepository.findAll().collectList().block().size();
        // set the field null
        visit.setClient(null);

        // Create the Visit, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(visit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Visit> visitList = visitRepository.findAll().collectList().block();
        assertThat(visitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAddressIsRequired() throws Exception {
        int databaseSizeBeforeTest = visitRepository.findAll().collectList().block().size();
        // set the field null
        visit.setAddress(null);

        // Create the Visit, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(visit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Visit> visitList = visitRepository.findAll().collectList().block();
        assertThat(visitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCarerIsRequired() throws Exception {
        int databaseSizeBeforeTest = visitRepository.findAll().collectList().block().size();
        // set the field null
        visit.setCarer(null);

        // Create the Visit, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(visit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Visit> visitList = visitRepository.findAll().collectList().block();
        assertThat(visitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAccesscodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = visitRepository.findAll().collectList().block().size();
        // set the field null
        visit.setAccesscode(null);

        // Create the Visit, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(visit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Visit> visitList = visitRepository.findAll().collectList().block();
        assertThat(visitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkTimeinIsRequired() throws Exception {
        int databaseSizeBeforeTest = visitRepository.findAll().collectList().block().size();
        // set the field null
        visit.setTimein(null);

        // Create the Visit, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(visit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Visit> visitList = visitRepository.findAll().collectList().block();
        assertThat(visitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = visitRepository.findAll().collectList().block().size();
        // set the field null
        visit.setStatus(null);

        // Create the Visit, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(visit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Visit> visitList = visitRepository.findAll().collectList().block();
        assertThat(visitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkTimespentIsRequired() throws Exception {
        int databaseSizeBeforeTest = visitRepository.findAll().collectList().block().size();
        // set the field null
        visit.setTimespent(null);

        // Create the Visit, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(visit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Visit> visitList = visitRepository.findAll().collectList().block();
        assertThat(visitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllVisitsAsStream() {
        // Initialize the database
        visitRepository.save(visit).block();

        List<Visit> visitList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Visit.class)
            .getResponseBody()
            .filter(visit::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(visitList).isNotNull();
        assertThat(visitList).hasSize(1);
        Visit testVisit = visitList.get(0);
        assertThat(testVisit.getClient()).isEqualTo(DEFAULT_CLIENT);
        assertThat(testVisit.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testVisit.getCarer()).isEqualTo(DEFAULT_CARER);
        assertThat(testVisit.getAccesscode()).isEqualTo(DEFAULT_ACCESSCODE);
        assertThat(testVisit.getTimein()).isEqualTo(DEFAULT_TIMEIN);
        assertThat(testVisit.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testVisit.getTimespent()).isEqualTo(DEFAULT_TIMESPENT);
    }

    @Test
    void getAllVisits() {
        // Initialize the database
        visitRepository.save(visit).block();

        // Get all the visitList
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
            .value(hasItem(visit.getId().intValue()))
            .jsonPath("$.[*].client")
            .value(hasItem(DEFAULT_CLIENT))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].carer")
            .value(hasItem(DEFAULT_CARER))
            .jsonPath("$.[*].accesscode")
            .value(hasItem(DEFAULT_ACCESSCODE))
            .jsonPath("$.[*].timein")
            .value(hasItem(DEFAULT_TIMEIN.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].timespent")
            .value(hasItem(DEFAULT_TIMESPENT.toString()));
    }

    @Test
    void getVisit() {
        // Initialize the database
        visitRepository.save(visit).block();

        // Get the visit
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, visit.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(visit.getId().intValue()))
            .jsonPath("$.client")
            .value(is(DEFAULT_CLIENT))
            .jsonPath("$.address")
            .value(is(DEFAULT_ADDRESS))
            .jsonPath("$.carer")
            .value(is(DEFAULT_CARER))
            .jsonPath("$.accesscode")
            .value(is(DEFAULT_ACCESSCODE))
            .jsonPath("$.timein")
            .value(is(DEFAULT_TIMEIN.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.timespent")
            .value(is(DEFAULT_TIMESPENT.toString()));
    }

    @Test
    void getNonExistingVisit() {
        // Get the visit
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewVisit() throws Exception {
        // Initialize the database
        visitRepository.save(visit).block();

        int databaseSizeBeforeUpdate = visitRepository.findAll().collectList().block().size();

        // Update the visit
        Visit updatedVisit = visitRepository.findById(visit.getId()).block();
        updatedVisit
            .client(UPDATED_CLIENT)
            .address(UPDATED_ADDRESS)
            .carer(UPDATED_CARER)
            .accesscode(UPDATED_ACCESSCODE)
            .timein(UPDATED_TIMEIN)
            .status(UPDATED_STATUS)
            .timespent(UPDATED_TIMESPENT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedVisit.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedVisit))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll().collectList().block();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
        Visit testVisit = visitList.get(visitList.size() - 1);
        assertThat(testVisit.getClient()).isEqualTo(UPDATED_CLIENT);
        assertThat(testVisit.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testVisit.getCarer()).isEqualTo(UPDATED_CARER);
        assertThat(testVisit.getAccesscode()).isEqualTo(UPDATED_ACCESSCODE);
        assertThat(testVisit.getTimein()).isEqualTo(UPDATED_TIMEIN);
        assertThat(testVisit.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testVisit.getTimespent()).isEqualTo(UPDATED_TIMESPENT);
    }

    @Test
    void putNonExistingVisit() throws Exception {
        int databaseSizeBeforeUpdate = visitRepository.findAll().collectList().block().size();
        visit.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, visit.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(visit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll().collectList().block();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchVisit() throws Exception {
        int databaseSizeBeforeUpdate = visitRepository.findAll().collectList().block().size();
        visit.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(visit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll().collectList().block();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamVisit() throws Exception {
        int databaseSizeBeforeUpdate = visitRepository.findAll().collectList().block().size();
        visit.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(visit))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll().collectList().block();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateVisitWithPatch() throws Exception {
        // Initialize the database
        visitRepository.save(visit).block();

        int databaseSizeBeforeUpdate = visitRepository.findAll().collectList().block().size();

        // Update the visit using partial update
        Visit partialUpdatedVisit = new Visit();
        partialUpdatedVisit.setId(visit.getId());

        partialUpdatedVisit.address(UPDATED_ADDRESS).carer(UPDATED_CARER).status(UPDATED_STATUS).timespent(UPDATED_TIMESPENT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedVisit.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedVisit))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll().collectList().block();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
        Visit testVisit = visitList.get(visitList.size() - 1);
        assertThat(testVisit.getClient()).isEqualTo(DEFAULT_CLIENT);
        assertThat(testVisit.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testVisit.getCarer()).isEqualTo(UPDATED_CARER);
        assertThat(testVisit.getAccesscode()).isEqualTo(DEFAULT_ACCESSCODE);
        assertThat(testVisit.getTimein()).isEqualTo(DEFAULT_TIMEIN);
        assertThat(testVisit.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testVisit.getTimespent()).isEqualTo(UPDATED_TIMESPENT);
    }

    @Test
    void fullUpdateVisitWithPatch() throws Exception {
        // Initialize the database
        visitRepository.save(visit).block();

        int databaseSizeBeforeUpdate = visitRepository.findAll().collectList().block().size();

        // Update the visit using partial update
        Visit partialUpdatedVisit = new Visit();
        partialUpdatedVisit.setId(visit.getId());

        partialUpdatedVisit
            .client(UPDATED_CLIENT)
            .address(UPDATED_ADDRESS)
            .carer(UPDATED_CARER)
            .accesscode(UPDATED_ACCESSCODE)
            .timein(UPDATED_TIMEIN)
            .status(UPDATED_STATUS)
            .timespent(UPDATED_TIMESPENT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedVisit.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedVisit))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll().collectList().block();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
        Visit testVisit = visitList.get(visitList.size() - 1);
        assertThat(testVisit.getClient()).isEqualTo(UPDATED_CLIENT);
        assertThat(testVisit.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testVisit.getCarer()).isEqualTo(UPDATED_CARER);
        assertThat(testVisit.getAccesscode()).isEqualTo(UPDATED_ACCESSCODE);
        assertThat(testVisit.getTimein()).isEqualTo(UPDATED_TIMEIN);
        assertThat(testVisit.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testVisit.getTimespent()).isEqualTo(UPDATED_TIMESPENT);
    }

    @Test
    void patchNonExistingVisit() throws Exception {
        int databaseSizeBeforeUpdate = visitRepository.findAll().collectList().block().size();
        visit.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, visit.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(visit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll().collectList().block();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchVisit() throws Exception {
        int databaseSizeBeforeUpdate = visitRepository.findAll().collectList().block().size();
        visit.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(visit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll().collectList().block();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamVisit() throws Exception {
        int databaseSizeBeforeUpdate = visitRepository.findAll().collectList().block().size();
        visit.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(visit))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll().collectList().block();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteVisit() {
        // Initialize the database
        visitRepository.save(visit).block();

        int databaseSizeBeforeDelete = visitRepository.findAll().collectList().block().size();

        // Delete the visit
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, visit.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Visit> visitList = visitRepository.findAll().collectList().block();
        assertThat(visitList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
