package com.auth0.gateway.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class RotaSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("client", table, columnPrefix + "_client"));
        columns.add(Column.aliased("carer", table, columnPrefix + "_carer"));
        columns.add(Column.aliased("time", table, columnPrefix + "_time"));
        columns.add(Column.aliased("duration", table, columnPrefix + "_duration"));

        return columns;
    }
}
