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
        addComponents(map, list, login, logout, about);

        setupButtons();
        updateButtons();
    }

    private void setupButtons() {
        map.addClickListener(event -> ui.getNavigator().navigateTo(PoiMap.NAME));
        list.addClickListener(event -> ui.getNavigator().navigateTo(PoiList.NAME));
        about.addClickListener(event -> ui.getNavigator().navigateTo(About.NAME));
        login.addClickListener(event -> ui.getNavigator().navigateTo(Login.NAME));
        logout.addClickListener(event -> {
            Auth.logout();
            updateButtons();
            ui.getNavigator().navigateTo(Login.NAME);
        });

        map.setIcon(FontAwesome.MAP);
        list.setIcon(FontAwesome.LIST);
        login.setIcon(FontAwesome.SIGN_IN);
        logout.setIcon(FontAwesome.SIGN_OUT);
        about.setIcon(FontAwesome.INFO_CIRCLE);
    }

    /**
     * Updates buttons visibility.
     *
     * Call after login/logout to update buttons
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
