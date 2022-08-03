package com.auth0.gateway.repository.rowmapper;

import com.auth0.gateway.domain.Carer;
import com.auth0.gateway.domain.enumeration.Days;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Carer}, with proper type conversions.
 */
@Service
public class CarerRowMapper implements BiFunction<Row, String, Carer> {

    private final ColumnConverter converter;

    public CarerRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Carer} stored in the database.
     */
    @Override
    public Carer apply(Row row, String prefix) {
        Carer entity = new Carer();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setPhone(converter.fromRow(row, prefix + "_phone", Long.class));
        entity.setDaysavailable(converter.fromRow(row, prefix + "_daysavailable", Days.class));
        entity.setClientId(converter.fromRow(row, prefix + "_client_id", Long.class));
        return entity;
    }
}
