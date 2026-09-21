<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Usuario" %>
<%
    Usuario user = (Usuario) session.getAttribute("usuario");
    String currentPage = request.getServletPath();
    
    // ⭐ Banderas por rol
    boolean esAdmin    = user != null && "ADMIN".equalsIgnoreCase(user.getRol());
    boolean esContador = user != null && "CONTADOR".equalsIgnoreCase(user.getRol());
    boolean esAuxiliar = user != null && "AUXILIAR".equalsIgnoreCase(user.getRol());
    boolean esAuditor  = user != null && "AUDITOR".equalsIgnoreCase(user.getRol());
%>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

<aside class="sidebar">
    <div class="sidebar-header">
        <div class="sidebar-logo">
            <i class="fa-solid fa-building-columns"></i>
        </div>
        <div>
            <div class="sidebar-title">Finanzas</div>
            <div class="sidebar-subtitle">Sistema de Conciliación Bancaria</div>
        </div>
    </div>

    <hr class="sidebar-divider">

    <nav class="sidebar-nav">
        <!-- Dashboard: todos -->
        <a href="DashboardServlet" class="sidebar-link <%= currentPage.contains("dashboard") ? "active" : "" %>">
            <i class="fa-solid fa-chart-line"></i> Inicio
        </a>

        <!-- Proveedores: todos -->
        <a href="ProveedorServlet?accion=listar" class="sidebar-link <%= currentPage.contains("proveedores") ? "active" : "" %>">
            <i class="fa-solid fa-users"></i> Proveedores
        </a>

        <!-- Objetos: todos -->
        <a href="ObjetoGastoServlet?accion=listar" class="sidebar-link <%= currentPage.contains("objetos") ? "active" : "" %>">
            <i class="fa-solid fa-tags"></i> Objetos de Gasto
        </a>

        <!-- Cheques: todos -->
        <a href="ChequeServlet?accion=listar" class="sidebar-link <%= currentPage.contains("cheques") ? "active" : "" %>">
            <i class="fa-solid fa-money-check-dollar"></i> Cheques
        </a>

        <!-- Depósitos: todos -->
        <a href="DepositoServlet?accion=listar" class="sidebar-link <%= currentPage.contains("depositos") ? "active" : "" %>">
            <i class="fa-solid fa-money-bill-transfer"></i> Depósitos
        </a>

        <!-- Conciliación: ADMIN, CONTADOR, AUDITOR -->
        <% if (esAdmin || esContador || esAuditor) { %>
            <a href="ConciliacionServlet?accion=listar" class="sidebar-link <%= currentPage.contains("conciliacion") ? "active" : "" %>">
                <i class="fa-solid fa-dollar-sign"></i> Conciliación
            </a>
        <% } %>

        <!-- Reportes: ADMIN, CONTADOR, AUDITOR -->
        <% if (esAdmin || esContador || esAuditor) { %>
            <a href="ReporteServlet" class="sidebar-link <%= currentPage.contains("reportes") ? "active" : "" %>">
                <i class="fa-regular fa-file-lines"></i> Consultas / Reportes
            </a>
        <% } %>

        <!-- Usuarios: solo ADMIN -->
        <% if (esAdmin) { %>
            <a href="UsuarioServlet?accion=listar" class="sidebar-link <%= currentPage.contains("usuarios") ? "active" : "" %>">
                <i class="fa-solid fa-users-gear"></i> Usuarios
            </a>
        <% } %>
    </nav>

    <div class="sidebar-footer">
        <div class="sidebar-user">
            <div class="sidebar-user-name"><%= user != null ? user.getNombre() + " " + user.getApellido() : "" %></div>
            <div class="sidebar-user-date"><%= new java.text.SimpleDateFormat("EEEE, d MMMM yyyy").format(new java.util.Date()) %></div>
        </div>
        <a href="LoginServlet?accion=logout" class="sidebar-link">
            <i class="fa-solid fa-right-from-bracket"></i> Cerrar sesión
        </a>
    </div>
</aside>