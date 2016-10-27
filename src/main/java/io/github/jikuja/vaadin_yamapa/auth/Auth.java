package io.github.jikuja.vaadin_yamapa.auth;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.VaadinSession;
import io.github.jikuja.vaadin_yamapa.database.Containers;
import io.github.jikuja.vaadin_yamapa.database.Database;

import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Auth {
    private static final Logger logger = Logger.getLogger(Auth.class.getName());
    private static SQLContainer users = null;


    static {
        try {
            users = Containers.getUsers(Database.getInstance().getPool());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "failed: ", e);
        }
    }

    public static boolean login(String user, String pass) {
        if (users != null) {
            Container.Filter filter = new And(
                    new IsNull("OAUTH"),
                    // TODO: better filters and proper password!!!
                    // yes this is BAD. Not really scope of the Vaadin => low priority
                    // prototyping and adding more important features have higher priority
                    // Fix this as soon as adding a way to add local users
                    new SimpleStringFilter("NAME", user, false, false),
                    new SimpleStringFilter("PASSWORD", pass, false, false)
            );
            users.addContainerFilter(filter);
            Collection<?> result = users.getItemIds();
            if (result.size() != 1) {
                return false;
            } else {
                VaadinSession session = VaadinSession.getCurrent();
                Item item = users.getItem(result.iterator().next());
                session.setAttribute("userid", item.getItemProperty("ID").getValue());
                session.setAttribute("name", item.getItemProperty("NAME").getValue());
                return true;
            }
        }
        return false;
    }

    public static boolean logout() {
        VaadinSession session = VaadinSession.getCurrent();
        session.setAttribute("userid", null);
        session.setAttribute("name", null);
        return true;
    }
}
