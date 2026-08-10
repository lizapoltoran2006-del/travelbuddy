package com.travelbuddy.config;

import com.travelbuddy.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @Test
    void doFilterInternal_ShouldSetAuthentication_WhenValidToken() throws Exception {

        String token = "valid-token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractEmail(anyString())).thenReturn("test@buddy.by");
        when(jwtService.isTokenValid(anyString(), anyString())).thenReturn(true);


        filter.doFilterInternal(request, response, (req, res) -> {});


        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("test@buddy.by",
                SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    void doFilterInternal_ShouldNotSetAuthentication_WhenNoToken() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();


        filter.doFilterInternal(request, response, (req, res) -> {});


        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ShouldNotSetAuthentication_WhenInvalidToken() throws Exception {

        String token = "invalid-token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractEmail(anyString())).thenReturn("test@buddy.by");
        when(jwtService.isTokenValid(anyString(), anyString())).thenReturn(false);


        filter.doFilterInternal(request, response, (req, res) -> {});


        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
