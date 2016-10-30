package io.github.jikuja.vaadin_yamapa.ui;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import io.github.jikuja.vaadin_yamapa.MyUI;
import io.github.jikuja.vaadin_yamapa.auth.Auth;
import io.github.jikuja.vaadin_yamapa.ui.views.About;
import io.github.jikuja.vaadin_yamapa.ui.views.Login;
import io.github.jikuja.vaadin_yamapa.ui.views.PoiList;
import io.github.jikuja.vaadin_yamapa.ui.views.PoiMap;

public class Menu extends CssLayout {
    private Button home = new Button("Home");
    private Button map = new Button("Map");
    private Button list = new Button("List");
    private Button login = new Button("Login");
    private Button logout = new Button("Logout");
    private Button about = new Button("About");
    private MyUI ui;

    public Menu(MyUI ui) {
        this.ui = ui;
        addStyleName("css-menu");
        setHeight(100, Unit.PERCENTAGE);

        setupButtons();
        updateButtons();
    }

    private void setupButtons() {
        addComponents(home, map, list, login, logout, about);
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
    }
}
