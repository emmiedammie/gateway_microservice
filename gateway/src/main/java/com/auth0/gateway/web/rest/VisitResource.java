package com.auth0.gateway.web.rest;

import com.auth0.gateway.domain.Visit;
import com.auth0.gateway.repository.VisitRepository;
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
 * REST controller for managing {@link com.auth0.gateway.domain.Visit}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class VisitResource {

    private final Logger log = LoggerFactory.getLogger(VisitResource.class);

    private static final String ENTITY_NAME = "visit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VisitRepository visitRepository;

    public VisitResource(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    /**
     * {@code POST  /visits} : Create a new visit.
     *
     * @param visit the visit to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new visit, or with status {@code 400 (Bad Request)} if the visit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/visits")
    public Mono<ResponseEntity<Visit>> createVisit(@Valid @RequestBody Visit visit) throws URISyntaxException {
        log.debug("REST request to save Visit : {}", visit);
        if (visit.getId() != null) {
            throw new BadRequestAlertException("A new visit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return visitRepository
            .save(visit)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/visits/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /visits/:id} : Updates an existing visit.
     *
     * @param id the id of the visit to save.
     * @param visit the visit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated visit,
     * or with status {@code 400 (Bad Request)} if the visit is not valid,
     * or with status {@code 500 (Internal Server Error)} if the visit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/visits/{id}")
    public Mono<ResponseEntity<Visit>> updateVisit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Visit visit
    ) throws URISyntaxException {
        log.debug("REST request to update Visit : {}, {}", id, visit);
        if (visit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, visit.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return visitRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return visitRepository
                    .save(visit)
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
     * {@code PATCH  /visits/:id} : Partial updates given fields of an existing visit, field will ignore if it is null
     *
     * @param id the id of the visit to save.
     * @param visit the visit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated visit,
     * or with status {@code 400 (Bad Request)} if the visit is not valid,
     * or with status {@code 404 (Not Found)} if the visit is not found,
     * or with status {@code 500 (Internal Server Error)} if the visit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/visits/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Visit>> partialUpdateVisit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Visit visit
    ) throws URISyntaxException {
        log.debug("REST request to partial update Visit partially : {}, {}", id, visit);
        if (visit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, visit.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return visitRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Visit> result = visitRepository
                    .findById(visit.getId())
                    .map(existingVisit -> {
                        if (visit.getClient() != null) {
                            existingVisit.setClient(visit.getClient());
                        }
                        if (visit.getAddress() != null) {
                            existingVisit.setAddress(visit.getAddress());
                        }
                        if (visit.getCarer() != null) {
                            existingVisit.setCarer(visit.getCarer());
                        }
                        if (visit.getAccesscode() != null) {
                            existingVisit.setAccesscode(visit.getAccesscode());
                        }
                        if (visit.getTimein() != null) {
                            existingVisit.setTimein(visit.getTimein());
                        }
                        if (visit.getStatus() != null) {
                            existingVisit.setStatus(visit.getStatus());
                        }
                        if (visit.getTimespent() != null) {
                            existingVisit.setTimespent(visit.getTimespent());
                        }

                        return existingVisit;
                    })
                    .flatMap(visitRepository::save);

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
     * {@code GET  /visits} : get all the visits.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of visits in body.
     */
    @GetMapping("/visits")
    public Mono<List<Visit>> getAllVisits() {
        log.debug("REST request to get all Visits");
        return visitRepository.findAll().collectList();
    }

    /**
     * {@code GET  /visits} : get all the visits as a stream.
     * @return the {@link Flux} of visits.
     */
    @GetMapping(value = "/visits", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Visit> getAllVisitsAsStream() {
        log.debug("REST request to get all Visits as a stream");
        return visitRepository.findAll();
    }

    /**
     * {@code GET  /visits/:id} : get the "id" visit.
     *
     * @param id the id of the visit to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the visit, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/visits/{id}")
    public Mono<ResponseEntity<Visit>> getVisit(@PathVariable Long id) {
        log.debug("REST request to get Visit : {}", id);
        Mono<Visit> visit = visitRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(visit);
    }

    /**
     * {@code DELETE  /visits/:id} : delete the "id" visit.
     *
     * @param id the id of the visit to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/visits/{id}")
    public Mono<ResponseEntity<Void>> deleteVisit(@PathVariable Long id) {
        log.debug("REST request to delete Visit : {}", id);
        return visitRepository
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
