package io.github.jikuja.vaadin_yamapa.ui;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import io.github.jikuja.vaadin_yamapa.database.Containers;
import io.github.jikuja.vaadin_yamapa.database.Database;

public class PoiList extends CssLayout implements View {
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

        setupGrid();
    }

    private void setupGrid() {
        // setup grid headers
        grid.getColumn("ID").setHidden(true);
        grid.getColumn("TITLE").setHeaderCaption("Title");
        // TODO: insert description into details
        // ref: https://vaadin.com/api/7.7.3/com/vaadin/ui/Grid.html#setDetailsGenerator-com.vaadin.ui.Grid.DetailsGenerator-
        // same area should contain add and delete buttons
        grid.getColumn("DESCRIPTION").setHeaderCaption("Description");
        grid.getColumn("OPTLOCK").setHidden(true);
        grid.getColumn("USER_ID").setHidden(true);
        grid.getColumn("LAT").setHeaderCaption("Latitude");
        grid.getColumn("LONG").setHeaderCaption("Longitude");
        grid.getColumn("NAME").setHeaderCaption("Added by");
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // TODO: check if tables updated and redraw?
    }
}
