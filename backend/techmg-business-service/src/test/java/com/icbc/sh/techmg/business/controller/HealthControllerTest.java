package com.icbc.sh.techmg.business.controller;

import com.icbc.sh.techmg.common.model.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * HealthController unit test — health check endpoint
 */
@RunWith(MockitoJUnitRunner.class)
public class HealthControllerTest {

    @InjectMocks
    private HealthController healthController;

    @Test
    public void healthShouldReturnUp() {
        R<Map<String, String>> result = healthController.health();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("UP", result.getData().get("status"));
        assertEquals("techmg", result.getData().get("service"));
    }
}
