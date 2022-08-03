package com.auth0.gateway.repository.rowmapper;

import com.auth0.gateway.domain.Rota;
import io.r2dbc.spi.Row;
import java.time.Duration;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Rota}, with proper type conversions.
 */
@Service
public class RotaRowMapper implements BiFunction<Row, String, Rota> {

    private final ColumnConverter converter;

    public RotaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Rota} stored in the database.
     */
    @Override
    public Rota apply(Row row, String prefix) {
        Rota entity = new Rota();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setClient(converter.fromRow(row, prefix + "_client", String.class));
        entity.setCarer(converter.fromRow(row, prefix + "_carer", String.class));
        entity.setTime(converter.fromRow(row, prefix + "_time", Instant.class));
        entity.setDuration(converter.fromRow(row, prefix + "_duration", Duration.class));
        return entity;
    }
}
