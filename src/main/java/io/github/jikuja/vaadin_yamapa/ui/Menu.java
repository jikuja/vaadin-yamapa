package io.github.jikuja.vaadin_yamapa.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import io.github.jikuja.vaadin_yamapa.MyUI;

public class Menu extends CssLayout {
    private Button map = new Button("Map");
    private Button list = new Button("List");
    private Button login = new Button("Login");
    private Button logout = new Button("Logout");
    private Button about = new Button("About");
    private MyUI ui;

    public Menu(MyUI ui) {
        this.ui = ui;
        setHeight(100, Unit.PERCENTAGE);
        addComponents(map, list, login, logout, about);

        setupButtons();
    }

    private void setupButtons() {
        map.addClickListener(event -> ui.getNav().navigateTo("map"));
        list.addClickListener(event -> ui.getNav().navigateTo("list"));
        about.addClickListener(event -> ui.getNav().navigateTo("about"));
    }
}
