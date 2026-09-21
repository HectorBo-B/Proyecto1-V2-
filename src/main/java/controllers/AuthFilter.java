package controllers;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {
    "/dashboard.jsp",
    "/cheques.jsp",
    "/proveedores.jsp",
    "/objetos.jsp",
    "/depositos.jsp",
    "/conciliacion.jsp",
    "/reportes.jsp",
    "/DashboardServlet",
    "/ChequeServlet",
    "/ProveedorServlet",
    "/ObjetoGastoServlet",
    "/DepositoServlet",
    "/ConciliacionServlet",
    "/ReporteServlet"
})
public class AuthFilter implements Filter
{
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // ⭐ Deshabilitar caché (muy importante)
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setHeader("Expires", "0");
        
        // ⭐ Verificar sesión
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("usuario") == null)
        {
            httpResponse.sendRedirect("login.jsp");
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    public void destroy()
    {
    }
}