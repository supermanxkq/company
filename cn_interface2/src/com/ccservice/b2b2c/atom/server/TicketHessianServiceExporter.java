package com.ccservice.b2b2c.atom.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.remoting.caucho.HessianExporter;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.util.NestedServletException;

import com.ccservice.b2b2c.atom.service.ITicketSearchService;

public class TicketHessianServiceExporter extends HessianExporter implements HttpRequestHandler {

    public TicketHessianServiceExporter() {
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        if (!"POST".equals(request.getMethod())) {
            throw new HttpRequestMethodNotSupportedException(request.getMethod(), new String[] { "POST" },
                    "HessianServiceExporter only supports POST requests");
        }

        ((ITicketSearchService) (getService())).setRemoteAddr(request.getRemoteAddr());

        try {
            invoke(request.getInputStream(), response.getOutputStream());
        }
        catch (Throwable ex) {
            throw new NestedServletException("Hessian skeleton invocation failed", ex);
        }
    }
}
