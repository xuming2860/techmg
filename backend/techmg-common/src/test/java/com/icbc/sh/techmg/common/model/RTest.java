package com.icbc.sh.techmg.common.model;

import com.icbc.sh.techmg.common.constant.ResultCode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RTest {

    @Test
    public void testOkWithData() {
        String data = "test-data";
        R<String> r = R.ok(data);
        assertEquals("code should be 200", 200, r.getCode());
        assertEquals("message should be 操作成功", "操作成功", r.getMessage());
        assertEquals("data should match", "test-data", r.getData());
    }

    @Test
    public void testOkWithoutData() {
        R<Object> r = R.ok();
        assertEquals("code should be 200", 200, r.getCode());
        assertEquals("message should be 操作成功", "操作成功", r.getMessage());
        assertNull("data should be null", r.getData());
    }

    @Test
    public void testOkWithNullData() {
        R<String> r = R.ok(null);
        assertEquals("code should be 200", 200, r.getCode());
        assertEquals("message should be 操作成功", "操作成功", r.getMessage());
        assertNull("data should be null", r.getData());
    }

    @Test
    public void testOkWithIntegerData() {
        R<Integer> r = R.ok(42);
        assertEquals("code should be 200", 200, r.getCode());
        assertEquals("data should be 42", Integer.valueOf(42), r.getData());
    }

    @Test
    public void testFailWithResultCodeOnly() {
        R<Object> r = R.fail(ResultCode.PARAM_ERROR);
        assertEquals("code should be 400", 400, r.getCode());
        assertEquals("message should match ResultCode", "参数错误", r.getMessage());
        assertNull("data should be null", r.getData());
    }

    @Test
    public void testFailWithResultCodeAndCustomMessage() {
        R<Object> r = R.fail(ResultCode.BUSINESS_ERROR, "自定义业务错误");
        assertEquals("code should be 1001", 1001, r.getCode());
        assertEquals("message should be custom", "自定义业务错误", r.getMessage());
        assertNull("data should be null", r.getData());
    }

    @Test
    public void testFailWithIntCodeAndMessage() {
        R<Object> r = R.fail(9999, "自定义错误码");
        assertEquals("code should be 9999", 9999, r.getCode());
        assertEquals("message should match", "自定义错误码", r.getMessage());
        assertNull("data should be null", r.getData());
    }

    @Test
    public void testFailWithUnauthorized() {
        R<Object> r = R.fail(ResultCode.UNAUTHORIZED);
        assertEquals("code should be 401", 401, r.getCode());
        assertEquals("message should match", "未登录或登录已过期", r.getMessage());
    }

    @Test
    public void testFailWithForbidden() {
        R<Object> r = R.fail(ResultCode.FORBIDDEN);
        assertEquals("code should be 403", 403, r.getCode());
        assertEquals("message should match", "无权限", r.getMessage());
    }

    @Test
    public void testFailWithNotFound() {
        R<Object> r = R.fail(ResultCode.NOT_FOUND);
        assertEquals("code should be 404", 404, r.getCode());
        assertEquals("message should match", "资源不存在", r.getMessage());
    }

    @Test
    public void testFailWithInternalError() {
        R<Object> r = R.fail(ResultCode.INTERNAL_ERROR);
        assertEquals("code should be 500", 500, r.getCode());
        assertEquals("message should match", "系统内部错误", r.getMessage());
    }

    @Test
    public void testOkSetsSuccessCodeAndMessage() {
        // Verify that custom message on fail doesn't affect ok()
        R<String> r = R.ok("hello");
        assertEquals(200, r.getCode());
        assertEquals("操作成功", r.getMessage());
        assertEquals("hello", r.getData());
    }

    @Test
    public void testFailWithCustomMessageOverridesDefault() {
        // fail(ResultCode, String) overrides the ResultCode's default message
        R<Object> r = R.fail(ResultCode.SUCCESS, "强制失败");
        assertEquals(200, r.getCode()); // code is from SUCCESS, but message is overridden
        assertEquals("强制失败", r.getMessage());
    }
}
