package io.github.jikuja.vaadin_yamapa.database;

import com.vaadin.data.Container;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;
import com.vaadin.shared.annotations.Delayed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public  class ItemsUsersDelegate implements FreeformStatementDelegate {
    private List<Container.Filter> filters;
    private List<OrderBy> orderBys;
    private List<String> fields;
    private String query;

    public ItemsUsersDelegate(String query) {
        this.query = query;
    }

    // FreeformStatementDelegate
    @Override
    public StatementHelper getQueryStatement(int offset, int limit) throws UnsupportedOperationException {
        StatementHelper statementHelper = new StatementHelper();
        StringBuilder sb = new StringBuilder(query);

        if (filters != null) {
            sb.append(QueryBuilder.getWhereStringForFilters(filters, statementHelper));
        }

        if (orderBys != null) {
            for (OrderBy o: orderBys) {
                generateOrderBy(sb, o, orderBys.indexOf(o) == 0);
            }
        }

        if (limit != 0) {
            sb.append(" LIMIT ").append(limit);
            sb.append(" OFFSET ").append(offset);
        }

        statementHelper.setQueryString(sb.toString());
        return statementHelper;
    }

    @Override
    public StatementHelper getCountStatement() throws UnsupportedOperationException {
        StatementHelper statementHelper = new StatementHelper();
        StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM items i LEFT JOIN users u ON i.USER_ID=u.ID");

        if (filters != null) {
            sb.append(QueryBuilder.getWhereStringForFilters(filters, statementHelper));
        }

        statementHelper.setQueryString(sb.toString());
        return statementHelper;
    }

    @Override
    public StatementHelper getContainsRowQueryStatement(Object... keys) throws UnsupportedOperationException {
        // TODO: needed?
        throw new UnsupportedOperationException();
    }

    // FreeformQueryDelegate
    @Override
    @Deprecated
    public String getQueryString(int offset, int limit) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Deprecated method");
    }

    @Override
    @Deprecated
    public String getCountQuery() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Deprecated method");
    }

    @Override
    public void setFilters(List<Container.Filter> filters) throws UnsupportedOperationException {
        this.filters = filters;
    }

    @Override
    public void setOrderBy(List<OrderBy> orderBys) throws UnsupportedOperationException {
        // undocumented(?) implementation detail:
        // SQLContainer.<init>() calls getPropertyIds which calls setOrderBy(null) which calls
        // this.setOrderBy(null). If not checked constructor will fail. Same for this.setFilters()
        this.orderBys = orderBys;
    }

    @Override
    public int storeRow(Connection conn, RowItem row) throws UnsupportedOperationException, SQLException {
        PreparedStatement statement = null;
        if (row.getId() instanceof TemporaryRowId) {
            // insert
            statement = conn.prepareStatement("INSERT INTO ITEMS (TITLE, DESCRIPTION, USER_ID, LAT, LONG) VALUES (?, ?, ?, ?, ?)");
            setRowValues(statement, row);
        } else {
            // update
            statement = conn.prepareStatement("UPDATE ITEMS SET TITLE = ?, DESCRIPTION = ?, USER_ID = ?, " +
                    "LAT = ?, LONG = ? WHERE id=?");
            setRowValues(statement, row);
            statement.setInt(6, (Integer) row.getItemProperty("ID").getValue());
        }

        int retval = statement.executeUpdate();
        statement.close();
        return retval;
    }

    protected void setRowValues(PreparedStatement s, RowItem row) throws SQLException {
        s.setString(1, (String) row.getItemProperty("TITLE").getValue());
        s.setString(2, (String) row.getItemProperty("DESCRIPTION").getValue());
        s.setInt(3, (Integer) row.getItemProperty("USER_ID").getValue());
        s.setDouble(4, (Double) row.getItemProperty("LAT").getValue());
        s.setDouble(5, (Double) row.getItemProperty("LONG").getValue());
    }

    @Override
    public boolean removeRow(Connection conn, RowItem row) throws UnsupportedOperationException, SQLException {
        // Note: no locking support
        PreparedStatement statement = conn.prepareStatement("DELETE FROM ITEMS WHERE ID=?");
        statement.setInt(1, (Integer)row.getItemProperty("ID").getValue()); // fixme id or ID
        int retval = statement.executeUpdate();
        statement.close();
        return retval == 1;
    }

    @Override
    @Deprecated
    public String getContainsRowQueryString(Object... keys) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Deprecated method");
    }

    // Source: DefaultSQLGeneretor.generateOrderBy
    // changed to use StringBuilder
    protected StringBuilder generateOrderBy(StringBuilder sb, OrderBy o, boolean firstOrderBy) {
        if (firstOrderBy) {
            sb.append(" ORDER BY ");
        } else {
            sb.append(", ");
        }
        sb.append(QueryBuilder.quote(o.getColumn()));
        if (o.isAscending()) {
            sb.append(" ASC");
        } else {
            sb.append(" DESC");
        }
        return sb;
    }
}
