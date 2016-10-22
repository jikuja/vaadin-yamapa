package io.github.jikuja.vaadin_yamapa.database;

import com.vaadin.data.Item;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.ui.Table;
import io.github.jikuja.vaadin_yamapa.FailAnswer;
import org.junit.*;
import org.mockito.Mockito;

import javax.servlet.ServletContext;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

public class DatabaseTest {
    private static Database database;
    private static JDBCConnectionPool pool;

    @Before
    public void setupData() {
        // TODO: REFACTOR: this block should be in @BeforeClass
        // does not work there: Database.executeFromResource fails to read sql String from the file
        // BufferedReader.readLine() return null when reading same file again
        ServletContext mockedServletContextInstance = Mockito.mock(ServletContext.class, new FailAnswer());
        Mockito.doReturn(getIS("ddl.sql")).when(mockedServletContextInstance).getResourceAsStream("/WEB-INF/classes/ddl.sql");
        Mockito.doReturn(getIS("sql/PUBLIC_PUBLIC_USERS.sql")).when(mockedServletContextInstance).getResourceAsStream("/WEB-INF/classes/sql/PUBLIC_PUBLIC_USERS.sql");
        Mockito.doReturn(getIS("sql/PUBLIC_PUBLIC_ITEMS.sql")).when(mockedServletContextInstance).getResourceAsStream("/WEB-INF/classes/sql/PUBLIC_PUBLIC_ITEMS.sql");
        database.setConText(mockedServletContextInstance);


        database.dropTables();
        database.createTables();
        database.addData();
    }

    @BeforeClass
    public static void setupDatabasePool() throws Exception {
        database = new Database(false);
        pool = database.getPool();
    }

    private static InputStream getIS(String s) {
        InputStream is = null;
        try {
            is = new FileInputStream("d:/projects/vaadin/vaadin-test-app/src/main/resources/" + s);
        } catch (Exception e) {
            fail(e.toString());
        }
        assertNotNull(is);
        return is;
    }

    @Test
    public void tablequeryfiltering() throws Exception {
        TableQuery itemsTableQuery = new TableQuery("items", pool);
        itemsTableQuery.setVersionColumn("OPTLOCK");
        SQLContainer items = new SQLContainer(itemsTableQuery);

        assertEquals(5, items.size());
        // note. hsqldb converts identifiers to uppercase => property IDs are uppercased
        items.addContainerFilter("DESCRIPTION", "asdf", true, false);
        assertEquals(1, items.size());
        items.removeAllContainerFilters();
        assertEquals(5, items.size());
        items.addContainerFilter(new SimpleStringFilter("DESCRIPTION", "asdf", true, false));
        assertEquals(1, items.size());
    }

    @Test
    public void filteringWithDelegate() throws Exception {
        SQLContainer c = Containers.getItemsUsers(pool);
        assertEquals(5, c.size());
        c.addContainerFilter("DESCRIPTION", "asdf", true, false);
        assertEquals(1, c.size());
    }

    @Test
    public void sortWithDelegate() throws Exception {
        SQLContainer c = Containers.getItemsUsers(pool);
        assertEquals(5, c.size());

        c.sort(new Object[] {"DESCRIPTION"}, new boolean[] {true} );
        String s = (String) c.getItem(c.firstItemId()).getItemProperty("DESCRIPTION").getValue();
        assertTrue(s.startsWith("asdf"));

        s = (String) c.getItem(c.lastItemId()).getItemProperty("DESCRIPTION").getValue();
        assertTrue(s.startsWith("zzzz"));

        assertEquals(5, c.size());
    }

    @Ignore
    @Test
    public void lockingWithDelegate() throws Exception {
        fail();
    }

    @Test
    public void deleteWithDelegate() throws Exception {
        SQLContainer c = Containers.getItemsUsers(pool);
        assertTrue(c.size() == 5);
        Object itemid = c.firstItemId();
        c.removeItem(itemid);
        assertTrue(c.size() == 4);
    }

    @Test
    public void insertWithDelegate() throws Exception {
        SQLContainer c = Containers.getItemsUsers(pool);
        assertTrue(c.size() == 5);

        Table table = new Table("", c);
        Object o = table.addItem();
        Item i = table.getItem(o);
        i.getItemProperty("TITLE").setValue("foo");
        table.commit();
        assertTrue(c.size() == 6);
    }

    @Test
    public void updateWithDelegate() throws Exception {
        SQLContainer c = Containers.getItemsUsers(pool);
        assertTrue(c.size() == 5);
        Object itemid = c.firstItemId();
        Item i = c.getItem(itemid);
        i.getItemProperty("TITLE").setValue("bbbb");
        c.commit();
        assertTrue(c.size() == 5);
    }
}

