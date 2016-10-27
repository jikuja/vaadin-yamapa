package io.github.jikuja.vaadin_yamapa.ui.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

public class ErrorView extends CssLayout implements View {
    public ErrorView() {
        Label label = new Label("Subpage does not exist!");
        addComponent(label);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
