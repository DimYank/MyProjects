package net.thumbtack.forums.integration;

import net.thumbtack.forums.debug.DebugService;
import net.thumbtack.forums.error.ServerException;
import org.junit.jupiter.api.BeforeEach;

public abstract class IntegrationTestBase {

    private DebugService debugService;

    public IntegrationTestBase(DebugService debugService) {
        this.debugService = debugService;
    }

    @BeforeEach
    void clearDB() throws ServerException {
        debugService.clearDataBase();
    }
}
