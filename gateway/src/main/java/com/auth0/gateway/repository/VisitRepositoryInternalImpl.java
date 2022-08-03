package com.auth0.gateway.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.auth0.gateway.domain.Visit;
import com.auth0.gateway.domain.enumeration.Status;
import com.auth0.gateway.repository.rowmapper.RotaRowMapper;
import com.auth0.gateway.repository.rowmapper.VisitRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Visit entity.
 */
@SuppressWarnings("unused")
class VisitRepositoryInternalImpl extends SimpleR2dbcRepository<Visit, Long> implements VisitRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RotaRowMapper rotaMapper;
    private final VisitRowMapper visitMapper;

    private static final Table entityTable = Table.aliased("visit", EntityManager.ENTITY_ALIAS);
    private static final Table rotaTable = Table.aliased("rota", "rota");

    public VisitRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RotaRowMapper rotaMapper,
        VisitRowMapper visitMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Visit.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.rotaMapper = rotaMapper;
        this.visitMapper = visitMapper;
    }

    @Override
    public Flux<Visit> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Visit> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = VisitSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(RotaSqlHelper.getColumns(rotaTable, "rota"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(rotaTable)
            .on(Column.create("rota_id", entityTable))
            .equals(Column.create("id", rotaTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Visit.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Visit> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Visit> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Visit process(Row row, RowMetadata metadata) {
        Visit entity = visitMapper.apply(row, "e");
        entity.setRota(rotaMapper.apply(row, "rota"));
        return entity;
    }

    @Override
    public <S extends Visit> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
