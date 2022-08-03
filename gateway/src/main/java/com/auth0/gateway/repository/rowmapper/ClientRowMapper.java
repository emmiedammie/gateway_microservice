package com.auth0.gateway.repository.rowmapper;

import com.auth0.gateway.domain.Client;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Client}, with proper type conversions.
 */
@Service
public class ClientRowMapper implements BiFunction<Row, String, Client> {

    private final ColumnConverter converter;

    public ClientRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Client} stored in the database.
     */
    @Override
    public Client apply(Row row, String prefix) {
        Client entity = new Client();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setPhone(converter.fromRow(row, prefix + "_phone", Long.class));
        entity.setAge(converter.fromRow(row, prefix + "_age", Integer.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        entity.setAccesscode(converter.fromRow(row, prefix + "_accesscode", Integer.class));
        entity.setTask(converter.fromRow(row, prefix + "_task", String.class));
        entity.setCarerassigned(converter.fromRow(row, prefix + "_carerassigned", String.class));
        return entity;
    }
}
