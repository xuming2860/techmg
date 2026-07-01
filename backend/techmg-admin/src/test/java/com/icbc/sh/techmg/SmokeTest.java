package com.icbc.sh.techmg;

import org.junit.Test;
import static org.junit.Assert.*;

/** Smoke test — 验证 JUnit 4 能正常运行 */
public class SmokeTest {

    @Test
    public void shouldPass() {
        assertTrue(true);
    }

    @Test
    public void shouldAdd() {
        assertEquals(4, 2 + 2);
    }
}
