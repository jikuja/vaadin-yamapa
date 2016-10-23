package io.github.jikuja.vaadin_yamapa.ui;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

public class ErrorView extends CssLayout implements View {
    public ErrorView() {
        addComponent(new Label("Subpage does not exist!"));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
