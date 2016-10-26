package io.github.jikuja.vaadin_yamapa.ui;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import io.github.jikuja.vaadin_yamapa.MyUI;
import io.github.jikuja.vaadin_yamapa.ui.views.About;
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
    }

    private void setupButtons() {
        map.addClickListener(event -> ui.getNav().navigateTo(PoiMap.NAME));
        list.addClickListener(event -> ui.getNav().navigateTo(PoiList.NAME));
        about.addClickListener(event -> ui.getNav().navigateTo(About.NAME));

        map.setIcon(FontAwesome.MAP);
        list.setIcon(FontAwesome.LIST);
        login.setIcon(FontAwesome.SIGN_IN);
        logout.setIcon(FontAwesome.SIGN_OUT);
        about.setIcon(FontAwesome.INFO_CIRCLE);
    }
}
