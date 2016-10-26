package io.github.jikuja.vaadin_yamapa.ui.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import org.vaadin.viritin.label.RichText;

public class About extends CssLayout implements View {
    public static final String NAME = "about";

    public About() {
        Label about = new RichText().withMarkDownResource("/ABOUT.md");
        addComponent(about);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }


}
