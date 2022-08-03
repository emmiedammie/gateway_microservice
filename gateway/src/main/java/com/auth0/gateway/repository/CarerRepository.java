package com.auth0.gateway.repository;

import com.auth0.gateway.domain.Carer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Carer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CarerRepository extends ReactiveCrudRepository<Carer, Long>, CarerRepositoryInternal {
    @Query("SELECT * FROM carer entity WHERE entity.client_id = :id")
    Flux<Carer> findByClient(Long id);

    @Query("SELECT * FROM carer entity WHERE entity.client_id IS NULL")
    Flux<Carer> findAllWhereClientIsNull();

    @Override
    <S extends Carer> Mono<S> save(S entity);

    @Override
    Flux<Carer> findAll();

    @Override
    Mono<Carer> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CarerRepositoryInternal {
    <S extends Carer> Mono<S> save(S entity);

    Flux<Carer> findAllBy(Pageable pageable);

    Flux<Carer> findAll();

    Mono<Carer> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Carer> findAllBy(Pageable pageable, Criteria criteria);

}
