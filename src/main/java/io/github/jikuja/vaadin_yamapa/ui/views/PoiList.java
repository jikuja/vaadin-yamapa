package io.github.jikuja.vaadin_yamapa.ui.views;

import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
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

        // TODO: make responsive
        grid.setWidth(700, Unit.PIXELS);
        grid.setHeight(500, Unit.PIXELS);
        setupGrid();
    }

    private void setupGrid() {
        // setup grid headers
        grid.getColumn("ID").setHidden(true);
        grid.getColumn("TITLE").setHeaderCaption("Title");
        // TODO: insert description into details
        // ref: https://vaadin.com/api/7.7.3/com/vaadin/ui/Grid.html#setDetailsGenerator-com.vaadin.ui.Grid.DetailsGenerator-
        // same area should contain add and delete buttons
        //grid.getColumn("DESCRIPTION").setHeaderCaption("Description");
        grid.getColumn("DESCRIPTION").setHidden(true);
        grid.getColumn("OPTLOCK").setHidden(true);
        grid.getColumn("USER_ID").setHidden(true);
        grid.getColumn("LAT").setHeaderCaption("Latitude");
        grid.getColumn("LONG").setHeaderCaption("Longitude");
        grid.getColumn("NAME").setHeaderCaption("Added by");

        grid.setDetailsGenerator(new PoiDetailsGenerator());
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        // I remember seeing an example which did not add listener for detail, can't find it
        grid.addItemClickListener(event -> {
            Object id = event.getItemId();
            grid.setDetailsVisible(id, !grid.isDetailsVisible(id));
        });
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // TODO: check if tables updated and redraw?
    }

    private static class PoiDetailsGenerator implements Grid.DetailsGenerator {
        @Override
        public Component getDetails(Grid.RowReference rowReference) {
            Item item = rowReference.getItem();
            Label label = new Label((String)item.getItemProperty("DESCRIPTION").getValue());
            Button edit = new Button("Edit");
            Button delete = new Button("Delete");

            HorizontalLayout layout = new HorizontalLayout();
            layout.addComponents(label, edit, delete);

            return layout;
        }
    }
}