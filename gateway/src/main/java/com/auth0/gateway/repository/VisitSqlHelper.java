package com.auth0.gateway.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class VisitSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("client", table, columnPrefix + "_client"));
        columns.add(Column.aliased("address", table, columnPrefix + "_address"));
        columns.add(Column.aliased("carer", table, columnPrefix + "_carer"));
        columns.add(Column.aliased("accesscode", table, columnPrefix + "_accesscode"));
        columns.add(Column.aliased("timein", table, columnPrefix + "_timein"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("timespent", table, columnPrefix + "_timespent"));

        columns.add(Column.aliased("rota_id", table, columnPrefix + "_rota_id"));
        return columns;
    }
}
