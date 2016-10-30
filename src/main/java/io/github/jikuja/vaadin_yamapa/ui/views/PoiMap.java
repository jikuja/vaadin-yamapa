package io.github.jikuja.vaadin_yamapa.ui.views;

import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import io.github.jikuja.vaadin_yamapa.database.Containers;
import io.github.jikuja.vaadin_yamapa.database.Database;
import io.github.jikuja.vaadin_yamapa.ui.forms.PoiForm;
import org.vaadin.addon.leaflet.*;
import org.vaadin.addon.leaflet.control.LScale;
import org.vaadin.addon.leaflet.shared.ControlPosition;
import org.vaadin.addon.leaflet.shared.Point;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.logging.Logger;


public class PoiMap extends CssLayout implements View {
    private static Logger logger = Logger.getLogger(PoiMap.class.getName());
    public static final String NAME = "map";
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
        setupCenterCrossHair();

        try {
            items = Containers.getItems(Database.getInstance().getPool());
            users = Containers.getUsers(Database.getInstance().getPool());
        } catch (SQLException e) {
            // just fail
            throw new RuntimeException(e);
        }

        items.addItemSetChangeListener(event -> {
            UI.getCurrent().access(this::updateMarks);
        });
    }



    private void setupMap() {
        // sane default zoom
        VaadinSession session = VaadinSession.getCurrent();
        if (session.getAttribute("lat") != null) {
            map.setCenter(new Point((Double)session.getAttribute("lat"), (Double)session.getAttribute("lon")),
                    14.0);
        } else {
            map.setCenter(new Point(60.440963, 22.25122), 14.0);
        }

        // single click / tapping is used to add POIs
        // using context meny would be even nicer thing to have. Maybe later
        map.setDoubleClickZoomEnabled(false);
        setupClickHandlers();
    }

    private void setupClickHandlers() {
        map.addClickListener(event -> {
            if (event.getSource() == map && VaadinSession.getCurrent().getAttribute("userid") != null) {
                Object iid = items.addItem();
                PoiForm form = new PoiForm("New POI", items, iid,
                        event.getPoint().getLat(), event.getPoint().getLon());
                UI.getCurrent().addWindow(form);
            }
        });
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

    private void setupLocationButton() {
        locate.addStyleName("locatebutton");
        locate.setIcon(FontAwesome.CROSSHAIRS);
        locate.addStyleName(ValoTheme.BUTTON_LARGE);
        locate.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        locate.addClickListener(event -> {
            Notification.show("Attention",
                    "No real geolocation support. Map center is used as coordinates",
                    Notification.Type.HUMANIZED_MESSAGE);

            VaadinSession session = VaadinSession.getCurrent();
            session.setAttribute("lat", map.getCenter().getLat());
            session.setAttribute("lon", map.getCenter().getLon());
        });
    }

    private void setupCenterCrossHair() {
        Button helper = new Button();
        helper.addStyleName("helper");
        helper.setIcon(FontAwesome.CROSSHAIRS);
        helper.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        helper.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        addComponent(helper);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        updateMarks();
    }

    // Updates markers in the mao
    private void updateMarks() {
        deleteMarkers();
        addMarks();
    }

    /**
     * Removes markers from the map
     */
    private void deleteMarkers() {
        Iterator<Component> it = map.iterator();
        Collection<Component> toRemove = new ArrayList<>();

        while (it.hasNext()) {
            Component next = it.next();
            if (next instanceof LMarker) {
                toRemove.add(next);
            }
        }

        toRemove.forEach(map::removeComponent);
    }

    /**
     * Adds markers to the map
     */
    private void addMarks() {
        for (Object iid: items.getItemIds()) {
            // new marker being added by someone
            if (iid instanceof TemporaryRowId) {
                Item item = items.getItem(iid);
                double lat = (Double) item.getItemProperty("LAT").getValue();
                double lon = (Double) item.getItemProperty("LONG").getValue();

                LMarker marker = new LMarker(lat, lon);
                marker.setIcon(FontAwesome.PLUS_CIRCLE);
                marker.addStyleName("temp-marker");
                map.addComponent(marker);
            // persistent / saved markers
            } else {
                Item item = items.getItem(iid);
                double lat = (Double) item.getItemProperty("LAT").getValue();
                double lon = (Double) item.getItemProperty("LONG").getValue();

                LMarker marker = new LMarker(lat, lon);
                marker.addClickListener(event -> {
                    PoiForm form;
                    if (Objects.equals(VaadinSession.getCurrent().getAttribute("userid"), item.getItemProperty("USER_ID").getValue())) {
                        form = new PoiForm("Edit POI", items, iid, true, true);
                    } else {
                        form = new PoiForm("POI Details", items, iid, false, false);
                    }
                    UI.getCurrent().addWindow(form);
                });
                map.addComponent(marker);
            }
        }
    }
}
