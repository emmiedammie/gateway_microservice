package com.auth0.gateway.web.rest;

import com.auth0.gateway.domain.Rota;
import com.auth0.gateway.repository.RotaRepository;
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
 * REST controller for managing {@link com.auth0.gateway.domain.Rota}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class RotaResource {

    private final Logger log = LoggerFactory.getLogger(RotaResource.class);

    private static final String ENTITY_NAME = "rota";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RotaRepository rotaRepository;

    public RotaResource(RotaRepository rotaRepository) {
        this.rotaRepository = rotaRepository;
    }

    /**
     * {@code POST  /rotas} : Create a new rota.
     *
     * @param rota the rota to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rota, or with status {@code 400 (Bad Request)} if the rota has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rotas")
    public Mono<ResponseEntity<Rota>> createRota(@Valid @RequestBody Rota rota) throws URISyntaxException {
        log.debug("REST request to save Rota : {}", rota);
        if (rota.getId() != null) {
            throw new BadRequestAlertException("A new rota cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return rotaRepository
            .save(rota)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/rotas/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /rotas/:id} : Updates an existing rota.
     *
     * @param id the id of the rota to save.
     * @param rota the rota to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rota,
     * or with status {@code 400 (Bad Request)} if the rota is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rota couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rotas/{id}")
    public Mono<ResponseEntity<Rota>> updateRota(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Rota rota
    ) throws URISyntaxException {
        log.debug("REST request to update Rota : {}, {}", id, rota);
        if (rota.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rota.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rotaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return rotaRepository
                    .save(rota)
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
     * {@code PATCH  /rotas/:id} : Partial updates given fields of an existing rota, field will ignore if it is null
     *
     * @param id the id of the rota to save.
     * @param rota the rota to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rota,
     * or with status {@code 400 (Bad Request)} if the rota is not valid,
     * or with status {@code 404 (Not Found)} if the rota is not found,
     * or with status {@code 500 (Internal Server Error)} if the rota couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/rotas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Rota>> partialUpdateRota(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Rota rota
    ) throws URISyntaxException {
        log.debug("REST request to partial update Rota partially : {}, {}", id, rota);
        if (rota.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rota.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rotaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Rota> result = rotaRepository
                    .findById(rota.getId())
                    .map(existingRota -> {
                        if (rota.getClient() != null) {
                            existingRota.setClient(rota.getClient());
                        }
                        if (rota.getCarer() != null) {
                            existingRota.setCarer(rota.getCarer());
                        }
                        if (rota.getTime() != null) {
                            existingRota.setTime(rota.getTime());
                        }
                        if (rota.getDuration() != null) {
                            existingRota.setDuration(rota.getDuration());
                        }

                        return existingRota;
                    })
                    .flatMap(rotaRepository::save);

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
     * {@code GET  /rotas} : get all the rotas.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rotas in body.
     */
    @GetMapping("/rotas")
    public Mono<List<Rota>> getAllRotas(@RequestParam(required = false) String filter) {
        if ("visit-is-null".equals(filter)) {
            log.debug("REST request to get all Rotas where visit is null");
            return rotaRepository.findAllWhereVisitIsNull().collectList();
        }
        log.debug("REST request to get all Rotas");
        return rotaRepository.findAll().collectList();
    }

    /**
     * {@code GET  /rotas} : get all the rotas as a stream.
     * @return the {@link Flux} of rotas.
     */
    @GetMapping(value = "/rotas", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Rota> getAllRotasAsStream() {
        log.debug("REST request to get all Rotas as a stream");
        return rotaRepository.findAll();
    }

    /**
     * {@code GET  /rotas/:id} : get the "id" rota.
     *
     * @param id the id of the rota to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rota, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rotas/{id}")
    public Mono<ResponseEntity<Rota>> getRota(@PathVariable Long id) {
        log.debug("REST request to get Rota : {}", id);
        Mono<Rota> rota = rotaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(rota);
    }

    /**
     * {@code DELETE  /rotas/:id} : delete the "id" rota.
     *
     * @param id the id of the rota to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rotas/{id}")
    public Mono<ResponseEntity<Void>> deleteRota(@PathVariable Long id) {
        log.debug("REST request to delete Rota : {}", id);
        return rotaRepository
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
