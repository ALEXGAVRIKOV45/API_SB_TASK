package service;

import org.aeonbits.owner.ConfigFactory;

public class ConfigManager {
    public static final TestConfiguration TEST_CONFIG;
    public static final PetStoreConfiguration PET_STORE_CONFIG;

    public ConfigManager() {
    }

    static {
        TEST_CONFIG = (TestConfiguration) ConfigFactory.create(TestConfiguration.class);
        PET_STORE_CONFIG = (PetStoreConfiguration) ConfigFactory.create(PetStoreConfiguration.class);
    }
}
