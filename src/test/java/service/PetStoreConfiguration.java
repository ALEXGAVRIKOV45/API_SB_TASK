package service;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Reloadable;

@Config.Sources({"file:src/test/resources/petStoreTest.properties"})
public interface PetStoreConfiguration extends Config, Reloadable {

    @Key("status")
    @DefaultValue("")
    String status();

    @Key("orderIdGET")
    @DefaultValue("")
    int orderIdGET();
    @Key("orderGetZeroType")
    @DefaultValue("")
    String orderGetZeroType();
    @Key("orderGetZeroMessage")
    @DefaultValue("")
    String orderGetZeroMessage();
    @Key("orderIdDELETE")
    @DefaultValue("")
    int orderIdDELETE();
    @Key("orderDeleteZeroType")
    @DefaultValue("")
    String orderDeleteZeroType();
    @Key("orderDeleteZeroMessage")
    @DefaultValue("")
    String orderDeleteZeroMessage();
    @Key("orderPostWithoutIdType")
    @DefaultValue("")
    String orderPostWithoutIdType();
    @Key("orderPostWithoutIdMessage")
    @DefaultValue("")
    String orderPostWithoutIdMessage();
}
