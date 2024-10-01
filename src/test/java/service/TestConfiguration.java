package service;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Reloadable;

@Config.Sources({"file:src/test/resources/apiTest.properties"})
public interface TestConfiguration extends Config, Reloadable {
    @Key("baseUrl")
    @DefaultValue("")
    String baseUrl();
}
