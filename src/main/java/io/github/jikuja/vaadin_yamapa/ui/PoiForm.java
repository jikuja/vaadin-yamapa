package io.github.jikuja.vaadin_yamapa.ui;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.ui.*;
import io.github.jikuja.vaadin_yamapa.database.Containers;
import io.github.jikuja.vaadin_yamapa.database.Database;

import javax.xml.crypto.Data;
import java.util.logging.Logger;

/*
 * Form for POI creation
 * User interacts with map and from is shown
 * Coordinates prefilled by callee
 */
public class PoiForm extends FormLayout {
    private final static Logger logger = Logger.getLogger(FormLayout.class.getName());

    @PropertyId("TITLE")
    private final TextField title = new TextField("Title");
    @PropertyId("DESCRIPTION")
    private final TextArea description = new TextArea("Description");
    @PropertyId("LAT")
    private final TextField lat = new TextField("Latitude");
    @PropertyId("LONG")
    private final TextField lon = new TextField("Longitude");

    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel");

    private final FieldGroup fieldGroup = new FieldGroup();
    private Item item;

    public PoiForm() {
        setSizeUndefined();
        addComponents(title, description, lat, lon);
        CssLayout buttons = new CssLayout();
        buttons.addComponents(save, cancel);
        addComponent(buttons);

        setupButtonHandlers();
        setupFields();
    }

    private void setupFields() {
        lat.setConverter(new StringToDoubleConverter());
        lon.setConverter(new StringToDoubleConverter());
    }

    private void setupButtonHandlers() {
        save.addClickListener(event -> {
            try {
                fieldGroup.commit();
            } catch (Exception e) {
                logger.warning("failed: " + e.toString());
            }
        });

        // TODO: not implemented / form re-using for other purposes(edit)
        cancel.addClickListener(event -> {
            Notification.show("Not implemented", Notification.Type.ERROR_MESSAGE);
        });
    }

    public void setItem(Item item) {
        if (fieldGroup.getItemDataSource() != item) {
            this.item = item;
            fieldGroup.discard();
            fieldGroup.setItemDataSource(item);
            fieldGroup.bindMemberFields(this);
        }
    }

    public void setCoordinates(double lat, double lon) {
        if (item != null) {
            item.getItemProperty("LAT").setValue(lat);
            item.getItemProperty("LONG").setValue(lon);
        }
    }


}
