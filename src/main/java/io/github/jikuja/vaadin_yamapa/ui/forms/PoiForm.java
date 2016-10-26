package io.github.jikuja.vaadin_yamapa.ui.forms;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.ui.*;
import io.github.jikuja.vaadin_yamapa.database.Containers;
import io.github.jikuja.vaadin_yamapa.database.Database;

import javax.xml.crypto.Data;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Form for POI creation
 * User interacts with map and from is shown
 * Coordinates prefilled by callee
 *
 * This Form Windows really should be reusable:
 * * New POI from map => marker update
 * * POI edit from map
 * * new POI from list with current location => List View update
 * * POI edit from the list => List View update
 *
 */
public class PoiForm extends Window {
    private final static Logger logger = Logger.getLogger(FormLayout.class.getName());
    private FormLayout layout = new FormLayout();
    
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
    private SQLContainer container;


    private PoiForm() {
        setupForm();
        setupButtonHandlers();
        setupFields();
        setupWindow();
    }

    /*
     * use case: POI with filled item
     */
    public PoiForm(String caption, SQLContainer container, Item item) {
        this();
        setCaption(caption);
        this.container = container;
        setItem(item);
    }

    /*
     * use case: new POI with given coordinates
     */
    public PoiForm(String caption, SQLContainer container, Item item, double lat, double lon) {
        this();
        setCaption(caption);
        this.container = container;
        setItem(item);
        setCoordinates(lat, lon);
    }

    private void setupForm() {
        layout.setSizeUndefined();
        layout.setMargin(true);
        layout.addComponents(title, description, lat, lon);
        CssLayout buttons = new CssLayout();
        buttons.addComponents(save, cancel);
        layout.addComponent(buttons);
        setContent(layout);
    }

    private void setupWindow() {
        setSizeUndefined();
        center();
        setModal(true);
        setResizable(false);
    }

    private void setupFields() {
        lat.setConverter(new StringToDoubleConverter());
        lon.setConverter(new StringToDoubleConverter());
    }

    private void setupButtonHandlers() {
        save.addClickListener(event -> {
            try {
                fieldGroup.commit();
                container.commit();
                // TODO: make this to reload POI marks in the map
                // partially done for MAP!
            } catch (Exception e) {
                logger.log(Level.SEVERE, "failed: ", e);
            }
            close();
        });
        
        cancel.addClickListener(event -> {
            close();
        });
    }

    private void setItem(Item item) {
        if (fieldGroup.getItemDataSource() != item) {
            this.item = item;
            fieldGroup.discard();
            fieldGroup.setItemDataSource(item);
            fieldGroup.bindMemberFields(this);
        }
    }

    private void setCoordinates(double lat, double lon) {
        if (item != null) {
            item.getItemProperty("LAT").setValue(lat);
            item.getItemProperty("LONG").setValue(lon);
        }
    }


    public void setContainer(SQLContainer container) {
        this.container = container;
    }
}
