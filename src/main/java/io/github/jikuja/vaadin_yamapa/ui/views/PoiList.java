package io.github.jikuja.vaadin_yamapa.ui.views;

import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import io.github.jikuja.vaadin_yamapa.database.Containers;
import io.github.jikuja.vaadin_yamapa.database.Database;
import io.github.jikuja.vaadin_yamapa.ui.forms.PoiForm;

import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PoiList extends CssLayout implements View {
    private static Logger logger = Logger.getLogger(PoiList.class.getName());
    public static final String NAME = "list";

    private SQLContainer container;
    private final Grid grid;

    public PoiList() {
        try {
            container = Containers.getItemsUsers(Database.getInstance().getPool());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        grid = new Grid(container);

        addComponent(grid);

        // TODO: make responsive
        grid.setWidth(700, Unit.PIXELS);
        grid.setHeight(500, Unit.PIXELS);
        setupGrid();
    }

    private void setupGrid() {
        // setup grid headers
        grid.getColumn("ID").setHidden(true);
        grid.getColumn("TITLE").setHeaderCaption("Title");
        grid.getColumn("DESCRIPTION").setHidden(true);
        grid.getColumn("OPTLOCK").setHidden(true);
        grid.getColumn("USER_ID").setHidden(true);
        grid.getColumn("LAT").setHeaderCaption("Latitude");
        grid.getColumn("LONG").setHeaderCaption("Longitude");
        grid.getColumn("NAME").setHeaderCaption("Added by");
        grid.getColumn("OAUTH").setHidden(true);
        grid.getColumn("PASSWORD").setHidden(true); //TODO: does this leak pass words to client?
        // probably better to exclude password field from requests

        // setup grid details
        grid.setDetailsGenerator(new PoiDetailsGenerator());
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        // setup click handler for grid details
        grid.addItemClickListener(event -> {
            Object id = event.getItemId();
            grid.setDetailsVisible(id, !grid.isDetailsVisible(id));
        });
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // TODO: check if tables updated and redraw?
    }

    private class PoiDetailsGenerator implements Grid.DetailsGenerator {
        @Override
        public Component getDetails(Grid.RowReference rowReference) {
            HorizontalLayout layout = new HorizontalLayout();

            Item item = rowReference.getItem();
            Label label = new Label((String)item.getItemProperty("DESCRIPTION").getValue());
            layout.addComponent(label);


            if (Objects.equals(VaadinSession.getCurrent().getAttribute("userid"), item.getItemProperty("USER_ID").getValue())) {
                Button edit = new Button("Edit", event -> {
                    UI.getCurrent().addWindow(new PoiForm("Edit POI", container, rowReference.getItemId(), true, true));
                });
                Button delete = new Button("Delete", event -> {
                    container.removeItem(rowReference.getItemId());
                    try {
                        container.commit();
                    } catch (SQLException e) {
                        logger.log(Level.SEVERE, "failed: ", e);
                    }
                    grid.setEditorEnabled(true);
                    grid.setEditorEnabled(false);
                });
                layout.addComponents(edit, delete);
            }

            return layout;
        }
    }
}
