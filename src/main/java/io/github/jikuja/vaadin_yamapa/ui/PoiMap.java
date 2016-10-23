package io.github.jikuja.vaadin_yamapa.ui;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LOpenStreetMapLayer;
import org.vaadin.addon.leaflet.LTileLayer;
import org.vaadin.addon.leaflet.LWmsLayer;
import org.vaadin.addon.leaflet.control.LScale;
import org.vaadin.addon.leaflet.shared.ControlPosition;
import org.vaadin.addon.leaflet.shared.Point;


public class PoiMap extends CssLayout implements View {
    private final LMap map = new LMap();
    private final Button locate = new Button("");

    public PoiMap() {
        setSizeFull();
        addComponent(map);
        addComponent(locate);
        addStyleName("mapwindow");

        setupLayers();

        setupLocationButton();
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

    private void setupLayers() {
        // sane default zoom
        map.setCenter(new Point(60.440963, 22.25122), 14.0);

        // OSM!
        LTileLayer osm = new LOpenStreetMapLayer();
        map.addBaseLayer(osm, "OSM");
        osm.setActive(true);

        // Turku opaskartta.
        LWmsLayer wmsok = new LWmsLayer();
        wmsok.setUrl("http://opaskartta.turku.fi/TeklaOGCWeb/WMS.ashx");
        wmsok.setTransparent(false);
        wmsok.setLayers("Opaskartta");
        wmsok.setAttributionString("&copy;Turun kaupunki: <a href=\"http://creativecommons.org/licenses/by/4.0/legalcode.fi\">CC BY 4.0</a>");
        map.addBaseLayer(wmsok, "Guidemap");
        wmsok.setActive(false);

        // Add overlay layer top of the basemap.
        LWmsLayer wms = new LWmsLayer();
        wms.setUrl("http://opaskartta.turku.fi/TeklaOGCWeb/WMS.ashx");
        wms.setTransparent(true);
        wms.setLayers("Opaskartta_pyoratiet");
        wms.setAttributionString("&copy;Turun kaupunki: <a href=\"http://creativecommons.org/licenses/by/4.0/legalcode.fi\">CC BY 4.0</a>");
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

    }
}
