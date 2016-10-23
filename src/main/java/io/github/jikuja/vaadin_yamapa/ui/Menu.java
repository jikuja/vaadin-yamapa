package io.github.jikuja.vaadin_yamapa.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.UI;

public class Menu extends CssLayout {
    private Button map = new Button("Map", event -> UI.getCurrent().getNavigator().navigateTo("map"));
    private Button list = new Button("List", event -> UI.getCurrent().getNavigator().navigateTo("list"));
    private Button login = new Button("Login");
    private Button logout = new Button("Logout");
    private Button about = new Button("About", event -> UI.getCurrent().getNavigator().navigateTo("about"));

    public Menu() {
        setHeight(100, Unit.PERCENTAGE);
        addComponents(map, list, login, logout, about);
    }
}
