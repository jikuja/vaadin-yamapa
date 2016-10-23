package io.github.jikuja.vaadin_yamapa.ui;

import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;
import io.github.jikuja.vaadin_yamapa.database.Containers;
import io.github.jikuja.vaadin_yamapa.database.Database;
import org.vaadin.addon.leaflet.*;
import org.vaadin.addon.leaflet.control.LScale;
import org.vaadin.addon.leaflet.shared.ControlPosition;
import org.vaadin.addon.leaflet.shared.Point;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;


public class PoiMap extends CssLayout implements View {
    private static Logger logger = Logger.getLogger(PoiMap.class.getName());
    private final LMap map = new LMap();
    private final Button locate = new Button("");

    private SQLContainer items;
    private SQLContainer users;

    public PoiMap() {
        setSizeFull();
        addComponent(map);
        addComponent(locate);
        addStyleName("mapwindow");

        setupMap();
        setupLayers();
        setupLocationButton();
        setupClickHandlers();

        try {
            items = Containers.getItems(Database.getInstance().getPool());
            users = Containers.getUsers(Database.getInstance().getPool());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupClickHandlers() {
        // ContextClickEvent does not provide geographical coordinates
        map.addClickListener(event -> {
            if (event.getSource() == map) {
                logger.info("map: Click listener: " + event.getPoint());
                Notification.show("Thanks for tapping. Not implemented yet");
            }
        });
    }

    /*
     * TODO: QA: how to make square button from font icons
     */
    private void setupLocationButton() {
        locate.addStyleName("locatebutton");
        locate.setIcon(FontAwesome.CROSSHAIRS);
        locate.addStyleName(ValoTheme.BUTTON_LARGE);
        //locate.setWidth(64, Unit.PIXELS);
        //locate.setHeight(64, Unit.PIXELS);
        locate.addClickListener(event -> {
            Notification.show("Not supported", "WIP: support will be added later. Meanwhile don't press the button", Notification.Type.ERROR_MESSAGE);
        });
    }

    private void setupMap() {
        // sane default zoom
        map.setCenter(new Point(60.440963, 22.25122), 14.0);

        // single click / tapping is used to add POIs
        map.setDoubleClickZoomEnabled(false);
    }

    private void setupLayers() {


        // add OSM layer, includes proper attribution
        LTileLayer osm = new LOpenStreetMapLayer();
        map.addBaseLayer(osm, "OSM");
        osm.setActive(true);

        // add Guidemap from city OSM
        LWmsLayer wmsok = new LWmsLayer();
        wmsok.setUrl("http://opaskartta.turku.fi/TeklaOGCWeb/WMS.ashx");
        wmsok.setTransparent(false);
        wmsok.setLayers("Opaskartta");
        wmsok.setAttributionString("Map data © Turun kaupunki: <a href=\"http://creativecommons.org/licenses/by/4.0/legalcode.fi\">CC BY 4.0</a>");
        map.addBaseLayer(wmsok, "Guidemap");
        wmsok.setActive(false);

        // add cycling routes as transparent overlay top of basemap layers
        LWmsLayer wms = new LWmsLayer();
        wms.setUrl("http://opaskartta.turku.fi/TeklaOGCWeb/WMS.ashx");
        wms.setTransparent(true);
        wms.setLayers("Opaskartta_pyoratiet");
        wms.setAttributionString("Map data © Turun kaupunki: <a href=\"http://creativecommons.org/licenses/by/4.0/legalcode.fi\">CC BY 4.0</a>");
        map.addOverlay(wms, "Cycling routes");

        // Multiple layers can be added if source is transparent
        // TODO: add others for ???

        // add scale to map
        LScale scale = new LScale();
        scale.setMetric(true);
        scale.setImperial(true);
        scale.setPosition(ControlPosition.topright);
        map.addControl(scale);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        deleteMarks();
        addMarks();
    }

    private void deleteMarks() {
        Iterator<Component> it = map.iterator();
        Collection<Component> toRemove = new ArrayList<>();

        while (it.hasNext()) {
            Component next = it.next();
            if (next instanceof LMarker) {
                toRemove.add(next);
            }
        }

        for (Component c: toRemove) {
            map.removeComponent(c);
        }
    }

    private void addMarks() {
        for (Object iid: items.getItemIds()) {
            Item item = items.getItem(iid);
            double lat = (Double) item.getItemProperty("LAT").getValue();
            double lon = (Double) item.getItemProperty("LONG").getValue();

            LMarker marker = new LMarker(lat, lon);
            map.addComponent(marker);
        }
    }
}
