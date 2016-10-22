package io.github.jikuja.vaadin_yamapa.database;

import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.server.VaadinServlet;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Database {
    private static Database instance;

    private static final String JDBC_DRIVER = "org.hsqldb.jdbc.JDBCDriver";
    private static final String JDBC_URL = "jdbc:hsqldb:mem:vaadin";
    private static final  String JDBC_USER = "SA";
    private static final String JDBC_PASS = "";

    private ServletContext context;
    private JDBCConnectionPool pool;

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database(true);
        }
        return instance;
    }

    Database(boolean all) {
        createPool();

        if (all) {
            createTables();
            addData();
        }
    }

    public JDBCConnectionPool getPool() {
        return pool;
    }

    private void createPool() {
        try {
            pool = new SimpleJDBCConnectionPool(
                    Database.JDBC_DRIVER, // driver
                    Database.JDBC_URL,  // connection url
                    Database.JDBC_USER, Database.JDBC_PASS,
                    2, 5);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void createTables() {
        boolean create = true;
        // TODO: add checking if tables exist

        if (create) {
            executeFromResource("/WEB-INF/classes/ddl.sql");
        }

    }

    void dropTables() {
        try {
            if (tableExists("ITEMS"))
                executeFromString("DROP TABLE ITEMS");
            if (tableExists("USERS"))
                executeFromString("DROP TABLE USERS");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    boolean tableExists(String table) {
        Connection connection = null;
        DatabaseMetaData md = null;
        ResultSet rs = null;
        boolean result = false;

        try {
            connection = pool.reserveConnection();
            md = connection.getMetaData();
            rs = md.getTables(null, null, table, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            if (rs.next()) {
                result = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                //
            }
        }
        if (connection != null) {
            pool.releaseConnection(connection);
        }

        return result;
    }

    void addData() {

            List<String> resources = new ArrayList<>();
            //resources.add("/WEB-INF/classes/ddl.sql");
            resources.add("/WEB-INF/classes/sql/PUBLIC_PUBLIC_USERS.sql");
            resources.add("/WEB-INF/classes/sql/PUBLIC_PUBLIC_ITEMS.sql");

            for (String res: resources) {
                executeFromResource(res);
            }

    }

    private void executeFromResource(String res) {
        if (context == null ) {
            context = VaadinServlet.getCurrent().getServletContext();
        }
        InputStream in = context.getResourceAsStream(res);
        String sql = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
        try {
            executeFromString(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void executeFromString(String s) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        SQLException sqlException = null;

        try {
            connection = pool.reserveConnection();
            statement = connection.createStatement();
            statement.execute(s);
            connection.commit();
        } catch (SQLException e) {
            sqlException = e;
        } finally {
            try {
                pool.releaseConnection(connection);
            } catch (Exception e) {
                //
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    //
                }
            }
        }

        if (sqlException != null) {
            throw sqlException;
        }
    }

    /**
     * Setter injector for test mocups
     * @param context
     */
    void setConText(ServletContext context) {
        this.context = context;
    }
}
