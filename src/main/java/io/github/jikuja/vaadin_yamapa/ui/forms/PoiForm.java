package io.github.jikuja.vaadin_yamapa.ui.forms;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PoiForm extends Window {
    private final static Logger logger = Logger.getLogger(FormLayout.class.getName());
    private final FormLayout layout = new FormLayout();
    private final CssLayout buttons = new CssLayout();
    
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
    private final Button delete = new Button("Delete");

    private final FieldGroup fieldGroup = new FieldGroup();
    private SQLContainer container;
    private Object itemId;


    /**
     * for internal use only
     *
     * some of the fields are properly initialized in other constructors
     */
    private PoiForm() {
        setupWindow();
        setupForm();
        setupButtonHandlers();
    }

    /**
     * New Window with POI Form
     *
     * Fills form with data from given item object or displays default input prompts
     *
     * @param caption Window title to show user
     * @param container Related contained for database access
     * @param itemId ItemId for related item for form field bindings
     */
    public PoiForm(String caption, SQLContainer container, Object itemId, boolean canDelete, boolean canEdit) {
        this();
        this.setCaption(caption);
        this.container = container;
        this.itemId = itemId;
        this.fieldGroup.setItemDataSource(container.getItem(itemId));
        this.fieldGroup.bindMemberFields(this);
        if (canDelete) { //
            // editing old item. Make delete button visible
            delete.setVisible(true);
        }

        if (!canEdit) {
            layout.setReadOnly(true);
            layout.setEnabled(false);
            buttons.setVisible(false);
            setClosable(true);
        }
    }

    /**
     * New Window with POI form
     *
     * Fills coordinateswith given values. Rest of the form is filled with with data from item object or
     * displays default input prompts
     *
     * @param caption Window title to show user
     * @param container Related contained for database access
     * @param itemId  ItemId for related item for form field bindings
     * @param lat Latitude to use as coordinate
     * @param lon Lognitude to use as coordinate
     */
    public PoiForm(String caption, SQLContainer container, Object itemId, double lat, double lon) {
        this(caption, container, itemId, false, true);
        this.setCoordinates(lat, lon);
        this.setUserId();
    }

    /**
     * Setup Window related settings. (Top component of the class)
     */
    private void setupWindow() {
        setSizeUndefined();
        center();
        setModal(true);
        setResizable(false);
        setClosable(false);
        setContent(layout);
    }

    /**
     * Setup Formlayer, add fields, configure visible default values for fields
     */
    private void setupForm() {
        layout.setSizeUndefined();

        // fields
        layout.setMargin(true);
        layout.addComponents(title, description, lat, lon);

        // Why my item has null objects even my database schema has "DEFAULT '' NOT NULL"?
        // TODO: QA: Did I derp?
        title.setNullRepresentation("");
        description.setNullRepresentation("");

        // visible default field values
        title.setInputPrompt("Add Title");
        description.setInputPrompt("Add description");
        lat.setInputPrompt("Add latitude");
        lon.setInputPrompt("Add lognitude");

        // setup converters for fields
        lat.setConverter(new AccurateStringToDoubleConverter());
        lon.setConverter(new AccurateStringToDoubleConverter());

        // TODO: setup validators

        // move cursor to first field of the form
        title.focus();

        // buttons
        buttons.addComponents(save, cancel, delete);
        delete.setVisible(false);   // not shown by default
        layout.addComponent(buttons);
    }

    /**
     * Setup click listeners for buttons
     */
    private void setupButtonHandlers() {
        save.addClickListener(event -> {
            try {
                // TODO: QA: Do I really need to call both commit()s?
                fieldGroup.commit();
                container.commit();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "failed: ", e);
            }
            close();
        });
        
        cancel.addClickListener(event -> {
            fieldGroup.discard();
            try {
                container.rollback();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "failed: ", e);
            }
            close();
        });

        // BUG? This does not work, why?
        cancel.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);

        delete.addClickListener(event -> {
            container.removeItem(itemId);
            try {
                container.commit();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "failed: ", e);
            }
            close();
        });
    }

    @SuppressWarnings("unchecked") // Double to Double
    private void setCoordinates(double lat, double lon) {
        fieldGroup.getItemDataSource().getItemProperty("LAT").setValue(lat);
        fieldGroup.getItemDataSource().getItemProperty("LONG").setValue(lon);
    }

    @SuppressWarnings("unchecked") // String to string
    private void setUserId() {
        fieldGroup.getItemDataSource().getItemProperty("USER_ID").setValue(VaadinSession.getCurrent().getAttribute("userid"));
    }

    /**
     * Converter from String to Double. Conversion uses 5 decimal precision => 1.1 meters
     */
    private static class AccurateStringToDoubleConverter extends StringToDoubleConverter {
        @Override
        protected NumberFormat getFormat(Locale locale) {
            NumberFormat format = super.getFormat(locale);
            format.setMinimumFractionDigits(5);
            format.setMaximumFractionDigits(5);
            return format;
        }
    }
}
