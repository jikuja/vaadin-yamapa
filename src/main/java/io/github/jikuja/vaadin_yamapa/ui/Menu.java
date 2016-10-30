package io.github.jikuja.vaadin_yamapa.ui;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import io.github.jikuja.vaadin_yamapa.MyUI;
import io.github.jikuja.vaadin_yamapa.auth.Auth;
import io.github.jikuja.vaadin_yamapa.database.Containers;
import io.github.jikuja.vaadin_yamapa.database.Database;
import io.github.jikuja.vaadin_yamapa.ui.forms.PoiForm;
import io.github.jikuja.vaadin_yamapa.ui.views.About;
import io.github.jikuja.vaadin_yamapa.ui.views.Login;
import io.github.jikuja.vaadin_yamapa.ui.views.PoiList;
import io.github.jikuja.vaadin_yamapa.ui.views.PoiMap;

import java.sql.SQLException;

public class Menu extends CssLayout {
    private final Button home = new Button("Home");
    private final Button map = new Button("Map");
    private final Button list = new Button("List");
    private final Button login = new Button("Login");
    private final Button logout = new Button("Logout");
    private final Button about = new Button("About");
    private final Label empty = new Label("&nbsp;", ContentMode.HTML);
    private final Button newPoi = new Button("New POI");
    private final MyUI ui;

    private SQLContainer items;

    public Menu(MyUI ui) {
        this.ui = ui;
        addStyleName("css-menu");
        setHeight(100, Unit.PERCENTAGE);

        try {
            items = Containers.getItems(Database.getInstance().getPool());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        setupButtons();
        updateButtons();
    }

    private void setupButtons() {
        addComponents(home, map, list, login, logout, about, empty, newPoi);

        home.addClickListener(event -> ui.getNavigator().navigateTo(""));
        map.addClickListener(event -> ui.getNavigator().navigateTo(PoiMap.NAME));
        list.addClickListener(event -> ui.getNavigator().navigateTo(PoiList.NAME));
        about.addClickListener(event -> ui.getNavigator().navigateTo(About.NAME));
        login.addClickListener(event -> ui.getNavigator().navigateTo(Login.NAME));

        logout.addClickListener(event -> {
            Auth.logout();
            updateButtons();
            ui.getNavigator().navigateTo(Login.NAME);
        });

        newPoi.addClickListener(event -> {

            VaadinSession session = VaadinSession.getCurrent();
            SQLContainer container;

            // ugly hack: enables automated map/list updates
            View view = ui.getNavigator().getCurrentView();
            if (view instanceof PoiList) {
                container = ((PoiList) view).getContainer();
            } else if (view instanceof PoiMap) {
                container = ((PoiMap) view).getItems();
            } else {
                container = items;
            }
            Object iid = container.addItem();

            PoiForm form = new PoiForm("New POI", container, iid,
                    (Double) session.getAttribute("lat"), (Double) session.getAttribute("lon"));
            ui.addWindow(form);
        });

        home.setIcon(FontAwesome.HOME);
        home.setDescription("Front page");
        map.setIcon(FontAwesome.MAP);
        map.setDescription("Map view of POIs");
        list.setIcon(FontAwesome.LIST);
        list.setDescription("List view of POIs");
        login.setIcon(FontAwesome.SIGN_IN);
        login.setDescription("Login area");
        logout.setIcon(FontAwesome.SIGN_OUT);
        logout.setDescription("Logout current user");
        about.setIcon(FontAwesome.INFO_CIRCLE);
        about.setDescription("About the project");
        newPoi.setIcon(FontAwesome.PLUS_CIRCLE);
        newPoi.setDescription("Add a new POI");
    }

    /**
     * Update visibility of buttons.
     *
     * Usualla called cafter user logs in or logs out
     *
     */
    public void updateButtons() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session.getAttribute("userid") == null) {
            login.setVisible(true);
            logout.setVisible(false);
        } else {
            login.setVisible(false);
            logout.setVisible(true);
        }

        if (session.getAttribute("userid") != null && session.getAttribute("lat") != null ) {
            newPoi.setVisible(true);
            empty.setVisible(true);
        } else {
            newPoi.setVisible(false);
            empty.setVisible(false);
        }
    }
}
