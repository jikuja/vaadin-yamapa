package io.github.jikuja.vaadin_yamapa.ui.views;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.model.Token;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import io.github.jikuja.vaadin_yamapa.MyUI;
import io.github.jikuja.vaadin_yamapa.auth.Auth;
import io.github.jikuja.vaadin_yamapa.oauth.ApiInfo;
import io.github.jikuja.vaadin_yamapa.oauth.GetTestComponent;
import org.vaadin.addon.oauthpopup.OAuthListener;
import org.vaadin.addon.oauthpopup.OAuthPopupButton;
import org.vaadin.addon.oauthpopup.buttons.GoogleButton;

public class Login extends CssLayout implements View {
    private static final ApiInfo GOOGLE_API = new ApiInfo(
            "Google",
            GoogleApi20.instance(),
            "918039249554-2oom5930f2auda86ft6tphpcdv0t7us2.apps.googleusercontent.com",
            "OXg7hKIxaI9CzgoacGKe1584",
            "https://www.googleapis.com/plus/v1/people/me");
    public static final String NAME =  "login";

    private final FormLayout layout = new FormLayout();

    private final TextField username = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");
    private final Button login = new Button("Login");

    private OAuthPopupButton google;

    public Login() {
        layout.addComponents(username, password, login);
        addComponent(layout);


        layout.addComponent(new Label("Or use one of the external services:"));
        addGoogleButton();

        login.addClickListener(event -> {
            if ( Auth.login(username.getValue(), password.getValue()) ) {
                Notification.show("Valid Credentials", null, Notification.Type.HUMANIZED_MESSAGE);
                MyUI.getInstance().getMenu().updateButtons();
                UI.getCurrent().getNavigator().navigateTo(PoiMap.NAME);
            } else {
                Notification.show("Invalid Credentials", null, Notification.Type.ERROR_MESSAGE);
            }
        });
    }

    private void addGoogleButton() {
        ApiInfo api = GOOGLE_API;
        if (api == null) {
            return;
        }
        OAuthPopupButton button = new GoogleButton(api.apiKey, api.apiSecret, "https://www.googleapis.com/auth/plus.login");
        addButton(api, button);
    }

    private void addButton(final ApiInfo service, OAuthPopupButton button) {

        // In most browsers "resizable" makes the popup
        // open in a new window, not in a tab.
        // You can also set size with eg. "resizable,width=400,height=300"
        button.setPopupWindowFeatures("resizable,width=400,height=300");

        HorizontalLayout hola = new HorizontalLayout();
        hola.setSpacing(true);
        hola.addComponent(button);

        layout.addComponent(hola);

        button.addOAuthListener(new Listener(service, hola));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    private class Listener implements OAuthListener {

        private final ApiInfo service;
        private final HorizontalLayout hola;

        private Listener(ApiInfo service, HorizontalLayout hola) {
            this.service = service;
            this.hola = hola;
        }

        @Override
        public void authSuccessful(final Token token, final boolean isOAuth20) {
            Label l = new Label("Authorized.");
            hola.addComponent(l);
            hola.setComponentAlignment(l, Alignment.MIDDLE_CENTER);

            Button testButton = new Button("Test " + service.name + " API");
            testButton.addStyleName(ValoTheme.BUTTON_LINK);
            hola.addComponent(testButton);
            hola.setComponentAlignment(testButton, Alignment.MIDDLE_CENTER);

            testButton.addClickListener(new Button.ClickListener() {
                private static final long serialVersionUID = 7561258877089832115L;

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    GetTestComponent get = new GetTestComponent(service, token);
                    Window w = new Window(service.name, get);
                    w.center();
                    w.setWidth("75%");
                    w.setHeight("75%");
                    UI.getCurrent().addWindow(w);
                }
            });
        }

        @Override
        public void authDenied(String reason) {
            Label l = new Label("Auth failed.");
            hola.addComponent(l);
            hola.setComponentAlignment(l, Alignment.MIDDLE_CENTER);
        }
    }
}
