package com.icbc.sh.techmg.common.enums;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ReformStatusTest {

    @Test
    public void testEnumValues() {
        ReformStatus[] values = ReformStatus.values();
        assertEquals("Should have 4 enum values", 4, values.length);
    }

    @Test
    public void testPendingLabel() {
        assertEquals("待开始", ReformStatus.PENDING.getLabel());
    }

    @Test
    public void testInProgressLabel() {
        assertEquals("进行中", ReformStatus.IN_PROGRESS.getLabel());
    }

    @Test
    public void testCompletedLabel() {
        assertEquals("已完成", ReformStatus.COMPLETED.getLabel());
    }

    @Test
    public void testClosedLabel() {
        assertEquals("已关闭", ReformStatus.CLOSED.getLabel());
    }

    @Test
    public void testValueOfPending() {
        assertEquals(ReformStatus.PENDING, ReformStatus.valueOf("PENDING"));
    }

    @Test
    public void testValueOfInProgress() {
        assertEquals(ReformStatus.IN_PROGRESS, ReformStatus.valueOf("IN_PROGRESS"));
    }

    @Test
    public void testValueOfCompleted() {
        assertEquals(ReformStatus.COMPLETED, ReformStatus.valueOf("COMPLETED"));
    }

    @Test
    public void testValueOfClosed() {
        assertEquals(ReformStatus.CLOSED, ReformStatus.valueOf("CLOSED"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfInvalid() {
        ReformStatus.valueOf("INVALID_STATUS");
    }

    @Test
    public void testGetLabelNotNull() {
        for (ReformStatus status : ReformStatus.values()) {
            assertNotNull("Label for " + status.name() + " should not be null", status.getLabel());
        }
    }
}
