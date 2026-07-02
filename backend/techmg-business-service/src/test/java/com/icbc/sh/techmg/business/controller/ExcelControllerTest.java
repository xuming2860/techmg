package com.icbc.sh.techmg.business.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * ExcelController unit test — template download endpoint
 */
@RunWith(MockitoJUnitRunner.class)
public class ExcelControllerTest {

    @InjectMocks
    private ExcelController excelController;

    @Mock
    private HttpServletResponse response;

    @Test
    public void shouldRejectUnknownTemplateType() throws IOException {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        excelController.downloadTemplate("unknown", response);

        verify(response).setStatus(400);
        String output = stringWriter.toString();
        assertTrue(output.contains("未知模板类型"));
        assertTrue(output.contains("400"));
    }

    @Test
    public void shouldAcceptGovernanceTemplateType() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ServletOutputStream sos = new ServletOutputStream() {
            @Override
            public void write(int b) { baos.write(b); }
            @Override
            public boolean isReady() { return true; }
            @Override
            public void setWriteListener(jakarta.servlet.WriteListener listener) { }
        };

        when(response.getOutputStream()).thenReturn(sos);

        excelController.downloadTemplate("governance", response);

        verify(response).setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        verify(response).setHeader(eq("Content-Disposition"), anyString());
        assertTrue(baos.size() > 0);
    }

    @Test
    public void shouldAcceptDbInspectionTemplateType() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ServletOutputStream sos = new ServletOutputStream() {
            @Override
            public void write(int b) { baos.write(b); }
            @Override
            public boolean isReady() { return true; }
            @Override
            public void setWriteListener(jakarta.servlet.WriteListener listener) { }
        };

        when(response.getOutputStream()).thenReturn(sos);

        excelController.downloadTemplate("db-inspection", response);

        assertTrue(baos.size() > 0);
    }

    @Test
    public void shouldAcceptAssetTemplateType() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ServletOutputStream sos = new ServletOutputStream() {
            @Override
            public void write(int b) { baos.write(b); }
            @Override
            public boolean isReady() { return true; }
            @Override
            public void setWriteListener(jakarta.servlet.WriteListener listener) { }
        };

        when(response.getOutputStream()).thenReturn(sos);

        excelController.downloadTemplate("asset", response);

        assertTrue(baos.size() > 0);
    }
}
