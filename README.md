# Light ComboBox project

Light version of ComboBox for Vaadin 24 based on existing web components of Vaadin 24. This version of the ComboBox specifically has been simplified by removal of client - server lazyloading mechanism. The benefit of this is that with small datasets of options faster response time of ComboBox opening is achieved. The component however is not suitable when dataset of options is larger.  The Java API is reduced version of the Vaadin 24 ComboBox due removal of some features. Also this removal of lazy loading makes it possible to have scroll to selected item working all the time.

## Development instructions

### Important Files 
* ComboBoxLight.java: this is the addon-on component class.
* CompView.java: A View class that let's you test the component you are building. 

### Deployment

Starting the test/demo server:
```
mvn -Pjetty jetty:run
```

This deploys demo at http://localhost:8080/light
 
### Integration test

To run Integration Tests, execute `mvn verify -Pit,production`.

## Publishing to Vaadin Directory

You should change the `organisation.name` property in `pom.xml` to your own name/organization.

```
    <organization>
        <name>###author###</name>
    </organization>
```

You can create the zip package needed for [Vaadin Directory](https://vaadin.com/directory/) using

```
mvn versions:set -DnewVersion=1.0.0 # You cannot publish snapshot versions 
mvn install -Pdirectory
```

The package is created as `target/combo-box-light-1.0.0.zip`

For more information or to upload the package, visit https://vaadin.com/directory/my-components?uploadNewComponent
