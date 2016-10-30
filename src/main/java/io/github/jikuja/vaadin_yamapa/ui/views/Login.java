package io.github.jikuja.vaadin_yamapa.ui.views;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import io.github.jikuja.vaadin_yamapa.MyUI;
import io.github.jikuja.vaadin_yamapa.auth.Auth;
import io.github.jikuja.vaadin_yamapa.database.Containers;
import io.github.jikuja.vaadin_yamapa.database.Database;
import org.vaadin.addon.oauthpopup.OAuthListener;
import org.vaadin.addon.oauthpopup.OAuthPopupButton;
import org.vaadin.addon.oauthpopup.buttons.FacebookButton;
import org.vaadin.addon.oauthpopup.buttons.GoogleButton;
import org.vaadin.addon.oauthpopup.buttons.TwitterButton;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Login extends CssLayout implements View {
    private static final Logger logger = Logger.getLogger(Login.class.getName());
    public static final String NAME =  "login";

    private final FormLayout layout = new FormLayout();

    private final TextField username = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");
    private final Button login = new Button("Login");

    private OAuthPopupButton googleButton;
    private OAuthPopupButton twitterButton;
    private OAuthPopupButton facebookButton;


    public Login() {
        layout.addComponents(username, password, login);
        username.focus();
        setupButtons();

        layout.addComponent(new Label("Or use one of the external services:"));
        addGoogleButton();
        addTwitterButton();
        addFacebookButton();
        addComponent(layout);
    }

    private void setupButtons() {
        login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
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
        JsonNode json;
        URL resource = getClass().getResource("/client_secret.json");
        if (resource != null) {
            try {
                json = new ObjectMapper().readTree(resource);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Failed to read client_secrets.json", e);
                return;
            }
            String apiKey = json.get("web").get("client_id").asText();
            String apiSecret = json.get("web").get("client_secret").asText();


            googleButton = new GoogleButton(apiKey, apiSecret, "https://www.googleapis.com/auth/plus.login");
            googleButton.setPopupWindowFeatures("resizable,width=400,height=300");
            googleButton.addOAuthListener(new Listener(apiKey, apiSecret));
            layout.addComponent(googleButton);
        } else {
            googleButton = new GoogleButton(null, null, null);
            googleButton.setEnabled(false);
            googleButton.setDescription("Disabled feature :(");
            layout.addComponent(googleButton);
        }
    }

    private void addTwitterButton() {
        twitterButton = new TwitterButton(null, null);
        twitterButton.setEnabled(false);
        twitterButton.setDescription("Disabled feature :(");
        layout.addComponent(twitterButton);
    }

    private void addFacebookButton() {
        facebookButton = new FacebookButton(null, null);
        facebookButton.setEnabled(false);
        facebookButton.setDescription("Disabled feature :(");
        layout.addComponent(facebookButton);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    private class Listener implements OAuthListener {
        private final static String TAG = "GOOGLE";
        private final String apiKey;
        private final String apiSecret;

        private Listener(String apiKey, String apiSecret) {
            this.apiKey = apiKey;
            this.apiSecret = apiSecret;
        }

        @Override
        public void authSuccessful(final Token token, final boolean isOAuth20) {
            Notification.show("Google oauth ok!");

            //
            ServiceBuilder sb = new ServiceBuilder()
                    .apiKey(apiKey)
                    .apiSecret(apiSecret)
                    .callback("http://google.fi");
            OAuth20Service service = sb.build(GoogleApi20.instance());

            OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.googleapis.com/plus/v1/people/me", service);
            service.signRequest((OAuth2AccessToken) token, request);

            Response response = request.send();
            JsonNode json;
            try {
                json = new ObjectMapper().readTree(response.getBody());
            } catch (IOException e) {
                logger.log(Level.WARNING, "Failed to read user data from google service", e);
                Notification.show("Authentication failed", "Please, consult lgo", Notification.Type.ERROR_MESSAGE);
                return;
            }

            String googleId = json.get("id").asText();
            String googleName = json.get("displayName").asText();

            Container.Filter filter = new And(
                    new SimpleStringFilter("OAUTH", TAG, false, false),
                    new SimpleStringFilter("EXTERNAL_ID", googleId, false, false)
            );

            SQLContainer users;
            try {
                users = Containers.getUsers(Database.getInstance().getPool());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            users.addContainerFilter(filter);

            if (users.size() == 1) {
                // old user
                Item item = users.getItem(users.firstItemId());
                VaadinSession session = VaadinSession.getCurrent();
                session.setAttribute("userid", item.getItemProperty("ID").getValue());
                session.setAttribute("name", item.getItemProperty("NAME").getValue());
            } else if (users.size() == 0) {
                // new user
                Item item = users.getItemUnfiltered(users.addItem());
                item.getItemProperty("OAUTH").setValue(TAG);
                item.getItemProperty("EXTERNAL_ID").setValue(googleId);
                item.getItemProperty("NAME").setValue(googleName);

                try {
                    users.commit();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                item = users.getItem(users.firstItemId());
                VaadinSession session = VaadinSession.getCurrent();
                session.setAttribute("userid", item.getItemProperty("ID").getValue());
                session.setAttribute("name", item.getItemProperty("NAME").getValue());
            } else {
                throw new RuntimeException("Database query resulted too many rows!");
            }

            //*/
            MyUI.getInstance().getMenu().updateButtons();
            // TODO: QA Don't do this. Login will have no parent anymore
            // => Rest of the Listener will throw errors!
            // Espacially OAuthPopupOpener.this.getUI().push(); won't find the UI
            //MyUI.getInstance().getNavigator().navigateTo(PoiMap.NAME);

;
        }

        @Override
        public void authDenied(String reason) {
            logger.warning("Auth failed: " + reason);
            Notification.show("Google oauth failed!", "", Notification.Type.ERROR_MESSAGE);
        }
    }
}
