package com.icbc.sh.techmg.framework.security;

import org.junit.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.Assert.*;

/**
 * JwtTokenProvider 单元测试 — JUnit 4 + 纯 Java (无 Mockito)
 */
public class JwtTokenProviderTest {

    // HS384 requires key >= 256 bits (32 bytes for HS256, 48 bytes for HS384)
    private static final String SECRET = "tdd-test-secret-key-for-junit-2026-very-long!";
    private static final long EXPIRATION = 3600000L; // 1 hour

    private final JwtTokenProvider provider = new JwtTokenProvider(SECRET, EXPIRATION);

    @Test
    public void shouldGenerateTokenAndExtractAuthNo() {
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_PLATFORM_ADMIN"));

        String token = provider.generateToken("admin", authorities);

        assertNotNull("token should not be null", token);
        assertFalse("token should not be blank", token.isBlank());
        assertEquals("authNo should be extracted from token", "admin", provider.getUserId(token));
    }

    @Test
    public void shouldValidateTokenSuccessfully() {
        String token = provider.generateToken("admin",
                List.of(new SimpleGrantedAuthority("ROLE_PLATFORM_ADMIN")));

        assertTrue("freshly generated token should be valid", provider.validateToken(token));
    }

    @Test
    public void shouldRejectExpiredToken() throws Exception {
        JwtTokenProvider shortLived = new JwtTokenProvider(SECRET, 1L); // 1ms

        String token = shortLived.generateToken("admin",
                List.of(new SimpleGrantedAuthority("ROLE_PLATFORM_ADMIN")));

        Thread.sleep(10); // wait for token to expire

        assertFalse("expired token should be invalid", shortLived.validateToken(token));
    }

    @Test
    public void shouldRejectTokenWithWrongSecret() {
        String token = provider.generateToken("admin",
                List.of(new SimpleGrantedAuthority("ROLE_PLATFORM_ADMIN")));

        JwtTokenProvider otherProvider = new JwtTokenProvider(
                "another-secret-key-that-is-different-and-long!!", EXPIRATION);

        assertFalse("token signed with different key should be invalid",
                otherProvider.validateToken(token));
    }

    @Test
    public void shouldRejectMalformedToken() {
        assertFalse("malformed string should be invalid",
                provider.validateToken("not-a-valid-jwt-token"));
        assertFalse("empty string should be invalid",
                provider.validateToken(""));
        assertFalse("null should be invalid",
                provider.validateToken(null));
    }
}
