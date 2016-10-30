package io.github.jikuja.vaadin_yamapa.ui.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import io.github.jikuja.vaadin_yamapa.ui.Hr;

public class Front extends CssLayout implements View {
    public Front() {
        String username = (String) VaadinSession.getCurrent().getAttribute("name");
        Label title = new Label("Cycling routes in Turku");
        title.addStyleName("h1");

        Label text;
        if (username != null) {
            text = new Label("Is Turku safe for cyclist or not? " +
                    "You can leave your feedback by adding a new POI in the map.");
        } else {
            text = new Label("Is Turku safe for cyclist or not? " +
                    "If you have any comments log in with Google account " +
                    "and leave your feedback as a new POI in the map.");
        }

        Label text2 = new Label("This project is done for TUCS Vaadin course. " +
                "Feedback given in this site will not be forwarded to " +
                "<a href=\"https://opaskartta.turku.fi/eFeedback/en\">Turku Feedback Service</a>",
                ContentMode.HTML);
        Label text3 = new Label(
                "Planned features: <ul>" +
                "<li>POI comments <li>POI votes <li>interaction with Feedback system of City</ul>" +
                "This page will be updated as soon as there are new status updates.",
                ContentMode.HTML);

        addComponents(title, text, new Hr(), text2, new Hr(), text3);
        setSizeFull();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
