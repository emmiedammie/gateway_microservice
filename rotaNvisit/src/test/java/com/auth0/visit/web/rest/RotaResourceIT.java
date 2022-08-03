package com.auth0.visit.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.auth0.visit.IntegrationTest;
import com.auth0.visit.domain.Rota;
import com.auth0.visit.repository.RotaRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link RotaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restRotaMockMvc;

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

    @BeforeEach
    public void initTest() {
        rota = createEntity(em);
    }

    @Test
    @Transactional
    void createRota() throws Exception {
        int databaseSizeBeforeCreate = rotaRepository.findAll().size();
        // Create the Rota
        restRotaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rota)))
            .andExpect(status().isCreated());

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll();
        assertThat(rotaList).hasSize(databaseSizeBeforeCreate + 1);
        Rota testRota = rotaList.get(rotaList.size() - 1);
        assertThat(testRota.getClient()).isEqualTo(DEFAULT_CLIENT);
        assertThat(testRota.getCarer()).isEqualTo(DEFAULT_CARER);
        assertThat(testRota.getTime()).isEqualTo(DEFAULT_TIME);
        assertThat(testRota.getDuration()).isEqualTo(DEFAULT_DURATION);
    }

    @Test
    @Transactional
    void createRotaWithExistingId() throws Exception {
        // Create the Rota with an existing ID
        rota.setId(1L);

        int databaseSizeBeforeCreate = rotaRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRotaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rota)))
            .andExpect(status().isBadRequest());

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll();
        assertThat(rotaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkClientIsRequired() throws Exception {
        int databaseSizeBeforeTest = rotaRepository.findAll().size();
        // set the field null
        rota.setClient(null);

        // Create the Rota, which fails.

        restRotaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rota)))
            .andExpect(status().isBadRequest());

        List<Rota> rotaList = rotaRepository.findAll();
        assertThat(rotaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCarerIsRequired() throws Exception {
        int databaseSizeBeforeTest = rotaRepository.findAll().size();
        // set the field null
        rota.setCarer(null);

        // Create the Rota, which fails.

        restRotaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rota)))
            .andExpect(status().isBadRequest());

        List<Rota> rotaList = rotaRepository.findAll();
        assertThat(rotaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = rotaRepository.findAll().size();
        // set the field null
        rota.setTime(null);

        // Create the Rota, which fails.

        restRotaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rota)))
            .andExpect(status().isBadRequest());

        List<Rota> rotaList = rotaRepository.findAll();
        assertThat(rotaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDurationIsRequired() throws Exception {
        int databaseSizeBeforeTest = rotaRepository.findAll().size();
        // set the field null
        rota.setDuration(null);

        // Create the Rota, which fails.

        restRotaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rota)))
            .andExpect(status().isBadRequest());

        List<Rota> rotaList = rotaRepository.findAll();
        assertThat(rotaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRotas() throws Exception {
        // Initialize the database
        rotaRepository.saveAndFlush(rota);

        // Get all the rotaList
        restRotaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rota.getId().intValue())))
            .andExpect(jsonPath("$.[*].client").value(hasItem(DEFAULT_CLIENT)))
            .andExpect(jsonPath("$.[*].carer").value(hasItem(DEFAULT_CARER)))
            .andExpect(jsonPath("$.[*].time").value(hasItem(DEFAULT_TIME.toString())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.toString())));
    }

    @Test
    @Transactional
    void getRota() throws Exception {
        // Initialize the database
        rotaRepository.saveAndFlush(rota);

        // Get the rota
        restRotaMockMvc
            .perform(get(ENTITY_API_URL_ID, rota.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rota.getId().intValue()))
            .andExpect(jsonPath("$.client").value(DEFAULT_CLIENT))
            .andExpect(jsonPath("$.carer").value(DEFAULT_CARER))
            .andExpect(jsonPath("$.time").value(DEFAULT_TIME.toString()))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION.toString()));
    }

    @Test
    @Transactional
    void getNonExistingRota() throws Exception {
        // Get the rota
        restRotaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewRota() throws Exception {
        // Initialize the database
        rotaRepository.saveAndFlush(rota);

        int databaseSizeBeforeUpdate = rotaRepository.findAll().size();

        // Update the rota
        Rota updatedRota = rotaRepository.findById(rota.getId()).get();
        // Disconnect from session so that the updates on updatedRota are not directly saved in db
        em.detach(updatedRota);
        updatedRota.client(UPDATED_CLIENT).carer(UPDATED_CARER).time(UPDATED_TIME).duration(UPDATED_DURATION);

        restRotaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRota.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRota))
            )
            .andExpect(status().isOk());

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll();
        assertThat(rotaList).hasSize(databaseSizeBeforeUpdate);
        Rota testRota = rotaList.get(rotaList.size() - 1);
        assertThat(testRota.getClient()).isEqualTo(UPDATED_CLIENT);
        assertThat(testRota.getCarer()).isEqualTo(UPDATED_CARER);
        assertThat(testRota.getTime()).isEqualTo(UPDATED_TIME);
        assertThat(testRota.getDuration()).isEqualTo(UPDATED_DURATION);
    }

    @Test
    @Transactional
    void putNonExistingRota() throws Exception {
        int databaseSizeBeforeUpdate = rotaRepository.findAll().size();
        rota.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRotaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rota.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rota))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll();
        assertThat(rotaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRota() throws Exception {
        int databaseSizeBeforeUpdate = rotaRepository.findAll().size();
        rota.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRotaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rota))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll();
        assertThat(rotaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRota() throws Exception {
        int databaseSizeBeforeUpdate = rotaRepository.findAll().size();
        rota.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRotaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rota)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll();
        assertThat(rotaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRotaWithPatch() throws Exception {
        // Initialize the database
        rotaRepository.saveAndFlush(rota);

        int databaseSizeBeforeUpdate = rotaRepository.findAll().size();

        // Update the rota using partial update
        Rota partialUpdatedRota = new Rota();
        partialUpdatedRota.setId(rota.getId());

        partialUpdatedRota.time(UPDATED_TIME).duration(UPDATED_DURATION);

        restRotaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRota.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRota))
            )
            .andExpect(status().isOk());

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll();
        assertThat(rotaList).hasSize(databaseSizeBeforeUpdate);
        Rota testRota = rotaList.get(rotaList.size() - 1);
        assertThat(testRota.getClient()).isEqualTo(DEFAULT_CLIENT);
        assertThat(testRota.getCarer()).isEqualTo(DEFAULT_CARER);
        assertThat(testRota.getTime()).isEqualTo(UPDATED_TIME);
        assertThat(testRota.getDuration()).isEqualTo(UPDATED_DURATION);
    }

    @Test
    @Transactional
    void fullUpdateRotaWithPatch() throws Exception {
        // Initialize the database
        rotaRepository.saveAndFlush(rota);

        int databaseSizeBeforeUpdate = rotaRepository.findAll().size();

        // Update the rota using partial update
        Rota partialUpdatedRota = new Rota();
        partialUpdatedRota.setId(rota.getId());

        partialUpdatedRota.client(UPDATED_CLIENT).carer(UPDATED_CARER).time(UPDATED_TIME).duration(UPDATED_DURATION);

        restRotaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRota.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRota))
            )
            .andExpect(status().isOk());

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll();
        assertThat(rotaList).hasSize(databaseSizeBeforeUpdate);
        Rota testRota = rotaList.get(rotaList.size() - 1);
        assertThat(testRota.getClient()).isEqualTo(UPDATED_CLIENT);
        assertThat(testRota.getCarer()).isEqualTo(UPDATED_CARER);
        assertThat(testRota.getTime()).isEqualTo(UPDATED_TIME);
        assertThat(testRota.getDuration()).isEqualTo(UPDATED_DURATION);
    }

    @Test
    @Transactional
    void patchNonExistingRota() throws Exception {
        int databaseSizeBeforeUpdate = rotaRepository.findAll().size();
        rota.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRotaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, rota.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rota))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll();
        assertThat(rotaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRota() throws Exception {
        int databaseSizeBeforeUpdate = rotaRepository.findAll().size();
        rota.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRotaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rota))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll();
        assertThat(rotaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRota() throws Exception {
        int databaseSizeBeforeUpdate = rotaRepository.findAll().size();
        rota.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRotaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(rota)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Rota in the database
        List<Rota> rotaList = rotaRepository.findAll();
        assertThat(rotaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRota() throws Exception {
        // Initialize the database
        rotaRepository.saveAndFlush(rota);

        int databaseSizeBeforeDelete = rotaRepository.findAll().size();

        // Delete the rota
        restRotaMockMvc
            .perform(delete(ENTITY_API_URL_ID, rota.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Rota> rotaList = rotaRepository.findAll();
        assertThat(rotaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
