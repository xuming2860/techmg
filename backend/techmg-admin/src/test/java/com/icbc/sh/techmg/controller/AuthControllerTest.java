package com.icbc.sh.techmg.controller;

import com.icbc.sh.techmg.common.config.SsicProperties;
import com.icbc.sh.techmg.common.model.R;
import com.icbc.sh.techmg.config.LoginMockProperties;
import com.icbc.sh.techmg.framework.security.JwtTokenProvider;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.service.SysUserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthControllerTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private SysUserService sysUserService;
    @Mock private LoginMockProperties loginMockProperties;
    @Mock private SsicProperties ssicProperties;

    @InjectMocks
    private AuthController authController;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        when(ssicProperties.isEnabled()).thenReturn(false);

        when(loginMockProperties.getAuthNo()).thenReturn("admin");
        when(loginMockProperties.getRealName()).thenReturn("平台管理员");
        when(loginMockProperties.getBranchId()).thenReturn("12092342");
        when(loginMockProperties.getBranchName()).thenReturn("上海技术部");
        when(loginMockProperties.getNotesId()).thenReturn("admin@sdc.com");
        when(loginMockProperties.getAdAccount()).thenReturn("");
        when(loginMockProperties.getRoles()).thenReturn(List.of("ROLE_PLATFORM_ADMIN"));

        when(jwtTokenProvider.generateToken(eq("admin"), anyList()))
                .thenReturn("mock-jwt-token-string");

        SysUser mockUser = new SysUser();
        mockUser.setId(1L);
        mockUser.setAuthNo("admin");
        mockUser.setRealName("平台管理员");
        when(sysUserService.syncUserInfo(anyMap())).thenReturn(mockUser);
    }

    @Test
    public void mockLoginShouldReturnTokenAndUserInfo() {
        Map<String, String> body = new HashMap<>();
        body.put("authNo", "anyone");
        body.put("password", "any");
        R<Map<String, Object>> result = authController.loginPost(request, response, body);

        assertNotNull(result);
        assertEquals(200, result.getCode());

        Map<String, Object> data = result.getData();
        assertNotNull(data);
        assertEquals("mock-jwt-token-string", data.get("token"));

        @SuppressWarnings("unchecked")
        Map<String, Object> userInfo = (Map<String, Object>) data.get("userInfo");
        assertNotNull(userInfo);
        assertEquals("admin", userInfo.get("authNo"));
        assertEquals("平台管理员", userInfo.get("realName"));
        assertEquals("12092342", userInfo.get("branchId"));
        assertEquals("all", userInfo.get("urlPermission"));
        assertTrue(((List<?>) userInfo.get("roles")).contains("ROLE_PLATFORM_ADMIN"));
    }

    @Test
    public void authConfigShouldReturnSsoDisabled() {
        R<Map<String, Object>> result = authController.authConfig();
        assertEquals(200, result.getCode());
        assertEquals(Boolean.FALSE, result.getData().get("ssoEnabled"));
    }

    @Test
    public void logoutShouldReturnOk() {
        R<Void> result = authController.logout();
        assertEquals(200, result.getCode());
    }

    @Test
    public void mockLoginShouldUseConfiguredUserRegardlessOfInput() {
        Map<String, String> body = new HashMap<>();
        body.put("authNo", "random-user");
        body.put("password", "xxx");
        R<Map<String, Object>> result = authController.loginPost(request, response, body);

        @SuppressWarnings("unchecked")
        Map<String, Object> ui = (Map<String, Object>) result.getData().get("userInfo");
        assertEquals("admin", ui.get("authNo"));
    }

    @Test
    public void loginWithEmptyBodyShouldStillWork() {
        R<Map<String, Object>> result = authController.loginPost(request, response, null);

        assertNotNull(result);
        assertEquals(200, result.getCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> ui = (Map<String, Object>) result.getData().get("userInfo");
        assertEquals("admin", ui.get("authNo"));
    }
}
