package io.github.jikuja.vaadin_yamapa.ui.views;

import com.vaadin.data.Item;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import io.github.jikuja.vaadin_yamapa.database.Containers;
import io.github.jikuja.vaadin_yamapa.database.Database;
import io.github.jikuja.vaadin_yamapa.ui.converters.AccurateStringToDoubleConverter;
import io.github.jikuja.vaadin_yamapa.ui.forms.PoiForm;

import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PoiList extends CssLayout implements View {
    private static final Logger logger = Logger.getLogger(PoiList.class.getName());
    public static final String NAME = "list";

    public SQLContainer getContainer() {
        return container;
    }

    private final SQLContainer container;
    private final Grid grid;

    public PoiList() {
        try {
            container = Containers.getItemsUsers(Database.getInstance().getPool());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        grid = new Grid(container);
        setupGrid();

        setSizeFull();
    }

    private void setupGrid() {
        // Does not look good but better then default which is
        // 10 rows.
        grid.setSizeFull();

        grid.setColumns("TITLE", "LAT", "LONG", "NAME");

        // setup grid headers
        grid.getColumn("TITLE").setHeaderCaption("Title");
        grid.getColumn("LAT").setHeaderCaption("Latitude");
        grid.getColumn("LONG").setHeaderCaption("Longitude");
        grid.getColumn("NAME").setHeaderCaption("Added by");

        // setup converters for coordinates
        grid.getColumn("LAT").setConverter(new AccurateStringToDoubleConverter());
        grid.getColumn("LONG").setConverter(new AccurateStringToDoubleConverter());

        // setup grid details
        grid.setDetailsGenerator(new PoiDetailsGenerator());
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        // setup click handler for grid details
        grid.addItemClickListener(event -> {
            Object id = event.getItemId();
            grid.setDetailsVisible(id, !grid.isDetailsVisible(id));
        });

        Grid.HeaderRow filters = grid.appendHeaderRow();

        for (Object pid: grid.getContainerDataSource().getContainerPropertyIds()) {
            if (Objects.equals(pid, "TITLE") || Objects.equals(pid, "NAME")) {
                Grid.HeaderCell cell = filters.getCell(pid);

                TextField filterText = new TextField();
                // TODO: fill fully fill headers. Requires CSS changes
                //filterText.setColumns(12);
                filterText.setWidth(100, Unit.PERCENTAGE);

                filterText.addTextChangeListener(change -> {
                    container.removeContainerFilters(pid);
                    if (!change.getText().isEmpty())
                        container.addContainerFilter(
                                new SimpleStringFilter(pid, change.getText(), true, false));
                });
                cell.setStyleName("grid-filter");
                cell.setComponent(filterText);
            }
        }

        addComponent(grid);
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
            // TODO: QA: use sizes from the grid
            label.setWidth(221, Unit.PIXELS);
            label.setHeight(120, Unit.PIXELS);
            label.addStyleName("border");
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
                edit.addStyleName(ValoTheme.BUTTON_FRIENDLY);
                delete.addStyleName(ValoTheme.BUTTON_DANGER);
                layout.addComponents(edit, delete);
            }

            return layout;
        }
    }
}
