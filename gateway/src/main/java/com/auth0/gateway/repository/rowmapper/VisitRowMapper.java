package com.auth0.gateway.repository.rowmapper;

import com.auth0.gateway.domain.Visit;
import com.auth0.gateway.domain.enumeration.Status;
import io.r2dbc.spi.Row;
import java.time.Duration;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Visit}, with proper type conversions.
 */
@Service
public class VisitRowMapper implements BiFunction<Row, String, Visit> {

    private final ColumnConverter converter;

    public VisitRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Visit} stored in the database.
     */
    @Override
    public Visit apply(Row row, String prefix) {
        Visit entity = new Visit();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setClient(converter.fromRow(row, prefix + "_client", String.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        entity.setCarer(converter.fromRow(row, prefix + "_carer", String.class));
        entity.setAccesscode(converter.fromRow(row, prefix + "_accesscode", Integer.class));
        entity.setTimein(converter.fromRow(row, prefix + "_timein", Instant.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", Status.class));
        entity.setTimespent(converter.fromRow(row, prefix + "_timespent", Duration.class));
        entity.setRotaId(converter.fromRow(row, prefix + "_rota_id", Long.class));
        return entity;
    }
}
