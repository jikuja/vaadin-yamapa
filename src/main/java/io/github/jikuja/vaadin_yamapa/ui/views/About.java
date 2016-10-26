package io.github.jikuja.vaadin_yamapa.ui.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

public class About extends CssLayout implements View {
    public static final String NAME = "about";

    public About() {
        Label about = new Label("About");
        addComponent(about);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }


}
