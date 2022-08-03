package com.auth0.gateway.repository;

import com.auth0.gateway.domain.Rota;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Rota entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RotaRepository extends ReactiveCrudRepository<Rota, Long>, RotaRepositoryInternal {
    @Query("SELECT * FROM rota entity WHERE entity.id not in (select visit_id from visit)")
    Flux<Rota> findAllWhereVisitIsNull();

    @Override
    <S extends Rota> Mono<S> save(S entity);

    @Override
    Flux<Rota> findAll();

    @Override
    Mono<Rota> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RotaRepositoryInternal {
    <S extends Rota> Mono<S> save(S entity);

    Flux<Rota> findAllBy(Pageable pageable);

    Flux<Rota> findAll();

    Mono<Rota> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Rota> findAllBy(Pageable pageable, Criteria criteria);

}
