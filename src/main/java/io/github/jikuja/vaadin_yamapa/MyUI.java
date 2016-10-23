package io.github.jikuja.vaadin_yamapa;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import io.github.jikuja.vaadin_yamapa.ui.About;
import io.github.jikuja.vaadin_yamapa.ui.Menu;
import io.github.jikuja.vaadin_yamapa.ui.PoiList;
import io.github.jikuja.vaadin_yamapa.ui.PoiMap;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {
    @Override
    public Navigator getNavigator() {
        return navigator;
    }

    private Navigator navigator;
    private Panel contentPanel;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final HorizontalLayout layout = new HorizontalLayout();
        contentPanel = new Panel();
        contentPanel.setContent(new Label("asdf"));

        navigator = new Navigator(this, contentPanel);
        navigator.addView("map", PoiMap.class);
        navigator.addView("list", PoiList.class);
        navigator.addView("about", About.class);
        navigator.navigateTo("about");

        layout.setSizeFull();
        layout.addComponent(new Menu());

        layout.addComponent(contentPanel);
        contentPanel.setSizeFull();
        layout.setExpandRatio(contentPanel, 1);

        setContent(layout);
    }



    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
