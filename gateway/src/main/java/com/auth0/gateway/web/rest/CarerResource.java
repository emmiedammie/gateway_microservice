package com.auth0.gateway.web.rest;

import com.auth0.gateway.domain.Carer;
import com.auth0.gateway.repository.CarerRepository;
import com.auth0.gateway.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.auth0.gateway.domain.Carer}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CarerResource {

    private final Logger log = LoggerFactory.getLogger(CarerResource.class);

    private static final String ENTITY_NAME = "carer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CarerRepository carerRepository;

    public CarerResource(CarerRepository carerRepository) {
        this.carerRepository = carerRepository;
    }

    /**
     * {@code POST  /carers} : Create a new carer.
     *
     * @param carer the carer to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new carer, or with status {@code 400 (Bad Request)} if the carer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/carers")
    public Mono<ResponseEntity<Carer>> createCarer(@Valid @RequestBody Carer carer) throws URISyntaxException {
        log.debug("REST request to save Carer : {}", carer);
        if (carer.getId() != null) {
            throw new BadRequestAlertException("A new carer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return carerRepository
            .save(carer)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/carers/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /carers/:id} : Updates an existing carer.
     *
     * @param id the id of the carer to save.
     * @param carer the carer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carer,
     * or with status {@code 400 (Bad Request)} if the carer is not valid,
     * or with status {@code 500 (Internal Server Error)} if the carer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/carers/{id}")
    public Mono<ResponseEntity<Carer>> updateCarer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Carer carer
    ) throws URISyntaxException {
        log.debug("REST request to update Carer : {}, {}", id, carer);
        if (carer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return carerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return carerRepository
                    .save(carer)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /carers/:id} : Partial updates given fields of an existing carer, field will ignore if it is null
     *
     * @param id the id of the carer to save.
     * @param carer the carer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carer,
     * or with status {@code 400 (Bad Request)} if the carer is not valid,
     * or with status {@code 404 (Not Found)} if the carer is not found,
     * or with status {@code 500 (Internal Server Error)} if the carer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/carers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Carer>> partialUpdateCarer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Carer carer
    ) throws URISyntaxException {
        log.debug("REST request to partial update Carer partially : {}, {}", id, carer);
        if (carer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return carerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Carer> result = carerRepository
                    .findById(carer.getId())
                    .map(existingCarer -> {
                        if (carer.getName() != null) {
                            existingCarer.setName(carer.getName());
                        }
                        if (carer.getPhone() != null) {
                            existingCarer.setPhone(carer.getPhone());
                        }
                        if (carer.getDaysavailable() != null) {
                            existingCarer.setDaysavailable(carer.getDaysavailable());
                        }

                        return existingCarer;
                    })
                    .flatMap(carerRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /carers} : get all the carers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of carers in body.
     */
    @GetMapping("/carers")
    public Mono<List<Carer>> getAllCarers() {
        log.debug("REST request to get all Carers");
        return carerRepository.findAll().collectList();
    }

    /**
     * {@code GET  /carers} : get all the carers as a stream.
     * @return the {@link Flux} of carers.
     */
    @GetMapping(value = "/carers", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Carer> getAllCarersAsStream() {
        log.debug("REST request to get all Carers as a stream");
        return carerRepository.findAll();
    }

    /**
     * {@code GET  /carers/:id} : get the "id" carer.
     *
     * @param id the id of the carer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the carer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/carers/{id}")
    public Mono<ResponseEntity<Carer>> getCarer(@PathVariable Long id) {
        log.debug("REST request to get Carer : {}", id);
        Mono<Carer> carer = carerRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(carer);
    }

    /**
     * {@code DELETE  /carers/:id} : delete the "id" carer.
     *
     * @param id the id of the carer to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/carers/{id}")
    public Mono<ResponseEntity<Void>> deleteCarer(@PathVariable Long id) {
        log.debug("REST request to delete Carer : {}", id);
        return carerRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
