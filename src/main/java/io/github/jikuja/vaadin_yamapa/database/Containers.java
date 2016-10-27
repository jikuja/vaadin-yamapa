package io.github.jikuja.vaadin_yamapa.database;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;

import java.sql.SQLException;

public class Containers {
    private Containers() {

    }

    public static SQLContainer getItemsUsers(JDBCConnectionPool pool) throws SQLException {
        String q = "SELECT * FROM items i LEFT JOIN users u ON i.USER_ID=u.ID";

        // this query string is not really used when using deletages but cannot be null or empty
        FreeformQuery query = new FreeformQuery(q, pool, "id");
        query.setDelegate(new ItemsUsersDelegate(q));
        SQLContainer sqlContainer = new SQLContainer(query);
        return sqlContainer;
    }

    public static SQLContainer getItems(JDBCConnectionPool pool) throws SQLException {
        TableQuery query = new TableQuery("items", pool);
        query.setVersionColumn("OPTLOCK");
        SQLContainer sqlContainer = new SQLContainer(query);
        return sqlContainer;
    }

    public static SQLContainer getUsers(JDBCConnectionPool pool) throws SQLException {
        TableQuery query = new TableQuery("users", pool);
        query.setVersionColumn("OPTLOCK");
        SQLContainer sqlContainer = new SQLContainer(query);
        return sqlContainer;
    }
}
