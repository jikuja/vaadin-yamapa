package io.github.jikuja.vaadin_yamapa;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.ViewportGeneratorClass;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.ViewportGenerator;
import com.vaadin.ui.*;
import io.github.jikuja.vaadin_yamapa.ui.Menu;
import io.github.jikuja.vaadin_yamapa.ui.views.*;

import java.util.logging.Logger;

@Push
@Theme("mytheme")
@ViewportGeneratorClass(MyUI.MyViewportGenerator.class)
public class MyUI extends UI {
    private final static Logger logger = Logger.getLogger(MyUI.class.getName());

    private Navigator navigator;
    private Menu menu;
    private boolean mobile = false;

    public static MyUI getInstance() {
        return (MyUI)UI.getCurrent();
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        if (isMobile(vaadinRequest)) {
            mobile = true;
        }

        // setup navigator
        Panel contentPanel = new Panel();
        navigator = new Navigator(this, contentPanel);
        navigator.addView("", Front.class);
        navigator.addView(PoiMap.NAME, PoiMap.class);
        navigator.addView(PoiList.NAME, PoiList.class);
        navigator.addView(About.NAME, About.class);
        navigator.addView(Login.NAME, Login.class);
        navigator.setErrorView(ErrorView.class);

        // setup layout - add menu
        HorizontalLayout layout = new HorizontalLayout();
        menu = new Menu(this);
        layout.addComponent(menu);

        // contentPanel will expand and fill layout
        layout.addComponent(contentPanel);
        contentPanel.setSizeFull();
        layout.setExpandRatio(contentPanel, 1);

        // rest of layout setup
        layout.setSizeFull();
        layout.setResponsive(true);
        layout.addStyleName("main");
        setContent(layout);
    }

    public Menu getMenu() {
        return menu;
    }

    public boolean isMobile() {
        return mobile;
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }

    public static class MyViewportGenerator implements ViewportGenerator {
        @Override
        public String getViewport(VaadinRequest request) {
            if ( isMobile(request) ) {
                //return "width=device-width, initial-scale=2, , maximum-scale=2, minimum-scale=2";
                return "width=device-width, initial-scale=1.0";

            }
            return null;
        }
    }

    public static boolean isMobile(VaadinRequest request) {
        boolean mobileUserAgent = request.getHeader("user-agent")
                .toLowerCase().contains("mobile");
        boolean mobileParameter = request.getParameter("mobile") != null;

        if ( mobileUserAgent || mobileParameter ) {
            return true;
        } else {
            return false;
        }
    }
}
