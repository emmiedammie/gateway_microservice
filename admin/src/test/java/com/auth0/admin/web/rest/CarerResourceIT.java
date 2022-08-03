package com.auth0.admin.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.auth0.admin.IntegrationTest;
import com.auth0.admin.domain.Carer;
import com.auth0.admin.domain.enumeration.Days;
import com.auth0.admin.repository.CarerRepository;
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
 * Integration tests for the {@link CarerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restCarerMockMvc;

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

    @BeforeEach
    public void initTest() {
        carer = createEntity(em);
    }

    @Test
    @Transactional
    void createCarer() throws Exception {
        int databaseSizeBeforeCreate = carerRepository.findAll().size();
        // Create the Carer
        restCarerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(carer)))
            .andExpect(status().isCreated());

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll();
        assertThat(carerList).hasSize(databaseSizeBeforeCreate + 1);
        Carer testCarer = carerList.get(carerList.size() - 1);
        assertThat(testCarer.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCarer.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testCarer.getDaysavailable()).isEqualTo(DEFAULT_DAYSAVAILABLE);
    }

    @Test
    @Transactional
    void createCarerWithExistingId() throws Exception {
        // Create the Carer with an existing ID
        carer.setId(1L);

        int databaseSizeBeforeCreate = carerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCarerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(carer)))
            .andExpect(status().isBadRequest());

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll();
        assertThat(carerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = carerRepository.findAll().size();
        // set the field null
        carer.setName(null);

        // Create the Carer, which fails.

        restCarerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(carer)))
            .andExpect(status().isBadRequest());

        List<Carer> carerList = carerRepository.findAll();
        assertThat(carerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDaysavailableIsRequired() throws Exception {
        int databaseSizeBeforeTest = carerRepository.findAll().size();
        // set the field null
        carer.setDaysavailable(null);

        // Create the Carer, which fails.

        restCarerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(carer)))
            .andExpect(status().isBadRequest());

        List<Carer> carerList = carerRepository.findAll();
        assertThat(carerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCarers() throws Exception {
        // Initialize the database
        carerRepository.saveAndFlush(carer);

        // Get all the carerList
        restCarerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carer.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE.intValue())))
            .andExpect(jsonPath("$.[*].daysavailable").value(hasItem(DEFAULT_DAYSAVAILABLE.toString())));
    }

    @Test
    @Transactional
    void getCarer() throws Exception {
        // Initialize the database
        carerRepository.saveAndFlush(carer);

        // Get the carer
        restCarerMockMvc
            .perform(get(ENTITY_API_URL_ID, carer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(carer.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE.intValue()))
            .andExpect(jsonPath("$.daysavailable").value(DEFAULT_DAYSAVAILABLE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCarer() throws Exception {
        // Get the carer
        restCarerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCarer() throws Exception {
        // Initialize the database
        carerRepository.saveAndFlush(carer);

        int databaseSizeBeforeUpdate = carerRepository.findAll().size();

        // Update the carer
        Carer updatedCarer = carerRepository.findById(carer.getId()).get();
        // Disconnect from session so that the updates on updatedCarer are not directly saved in db
        em.detach(updatedCarer);
        updatedCarer.name(UPDATED_NAME).phone(UPDATED_PHONE).daysavailable(UPDATED_DAYSAVAILABLE);

        restCarerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCarer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCarer))
            )
            .andExpect(status().isOk());

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll();
        assertThat(carerList).hasSize(databaseSizeBeforeUpdate);
        Carer testCarer = carerList.get(carerList.size() - 1);
        assertThat(testCarer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCarer.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testCarer.getDaysavailable()).isEqualTo(UPDATED_DAYSAVAILABLE);
    }

    @Test
    @Transactional
    void putNonExistingCarer() throws Exception {
        int databaseSizeBeforeUpdate = carerRepository.findAll().size();
        carer.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(carer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll();
        assertThat(carerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCarer() throws Exception {
        int databaseSizeBeforeUpdate = carerRepository.findAll().size();
        carer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(carer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll();
        assertThat(carerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCarer() throws Exception {
        int databaseSizeBeforeUpdate = carerRepository.findAll().size();
        carer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(carer)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll();
        assertThat(carerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCarerWithPatch() throws Exception {
        // Initialize the database
        carerRepository.saveAndFlush(carer);

        int databaseSizeBeforeUpdate = carerRepository.findAll().size();

        // Update the carer using partial update
        Carer partialUpdatedCarer = new Carer();
        partialUpdatedCarer.setId(carer.getId());

        partialUpdatedCarer.name(UPDATED_NAME);

        restCarerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCarer))
            )
            .andExpect(status().isOk());

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll();
        assertThat(carerList).hasSize(databaseSizeBeforeUpdate);
        Carer testCarer = carerList.get(carerList.size() - 1);
        assertThat(testCarer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCarer.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testCarer.getDaysavailable()).isEqualTo(DEFAULT_DAYSAVAILABLE);
    }

    @Test
    @Transactional
    void fullUpdateCarerWithPatch() throws Exception {
        // Initialize the database
        carerRepository.saveAndFlush(carer);

        int databaseSizeBeforeUpdate = carerRepository.findAll().size();

        // Update the carer using partial update
        Carer partialUpdatedCarer = new Carer();
        partialUpdatedCarer.setId(carer.getId());

        partialUpdatedCarer.name(UPDATED_NAME).phone(UPDATED_PHONE).daysavailable(UPDATED_DAYSAVAILABLE);

        restCarerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCarer))
            )
            .andExpect(status().isOk());

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll();
        assertThat(carerList).hasSize(databaseSizeBeforeUpdate);
        Carer testCarer = carerList.get(carerList.size() - 1);
        assertThat(testCarer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCarer.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testCarer.getDaysavailable()).isEqualTo(UPDATED_DAYSAVAILABLE);
    }

    @Test
    @Transactional
    void patchNonExistingCarer() throws Exception {
        int databaseSizeBeforeUpdate = carerRepository.findAll().size();
        carer.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, carer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(carer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll();
        assertThat(carerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCarer() throws Exception {
        int databaseSizeBeforeUpdate = carerRepository.findAll().size();
        carer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(carer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll();
        assertThat(carerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCarer() throws Exception {
        int databaseSizeBeforeUpdate = carerRepository.findAll().size();
        carer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(carer)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Carer in the database
        List<Carer> carerList = carerRepository.findAll();
        assertThat(carerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCarer() throws Exception {
        // Initialize the database
        carerRepository.saveAndFlush(carer);

        int databaseSizeBeforeDelete = carerRepository.findAll().size();

        // Delete the carer
        restCarerMockMvc
            .perform(delete(ENTITY_API_URL_ID, carer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Carer> carerList = carerRepository.findAll();
        assertThat(carerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
