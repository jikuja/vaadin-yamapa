yamapa
==============

Course assignment for TUCS course "Development of Modern Web Applications (with Vaadin)" fall 2016.

More information available in http://dy.fi/iagw

Description
--------------

Simple application which lists Points of Interest(POI) in the map and in table. User who have
logged in with local credential or using google oauth can add POIs or edit/delete POIs they
have created earlier.

Theme for this instance is cycling routes in Turku area. Users can comment local cycling routes.
But it's easy to change overlay map and description in front page to create application for other
purposes.

Usage Notes
--------------

* Demo
  * Database has two local user joona and juuso. Their passwords are "joona" and "juuso"
  * Live demo site is available in https://jikuja.kapsi.fi/yamapa/
  * Google auth is enabled in demo site. Disabled with local dev environments unless developer
    adds `src/main/resources/client_secret.json` (google's format)

License
==============

MIT. Don't ask, everything is MIT licensed here.