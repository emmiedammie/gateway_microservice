package com.auth0.gateway.repository;

import com.auth0.gateway.domain.Visit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Visit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VisitRepository extends ReactiveCrudRepository<Visit, Long>, VisitRepositoryInternal {
    @Query("SELECT * FROM visit entity WHERE entity.rota_id = :id")
    Flux<Visit> findByRota(Long id);

    @Query("SELECT * FROM visit entity WHERE entity.rota_id IS NULL")
    Flux<Visit> findAllWhereRotaIsNull();

    @Override
    <S extends Visit> Mono<S> save(S entity);

    @Override
    Flux<Visit> findAll();

    @Override
    Mono<Visit> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface VisitRepositoryInternal {
    <S extends Visit> Mono<S> save(S entity);

    Flux<Visit> findAllBy(Pageable pageable);

    Flux<Visit> findAll();

    Mono<Visit> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Visit> findAllBy(Pageable pageable, Criteria criteria);

}
