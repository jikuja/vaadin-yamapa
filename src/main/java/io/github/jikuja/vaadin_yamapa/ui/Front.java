package io.github.jikuja.vaadin_yamapa.ui;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

public class Front extends CssLayout implements View {
    public Front() {
        Label about = new Label("Front");
        addComponent(about);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
