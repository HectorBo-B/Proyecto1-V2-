<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Usuario" %>
<%@ page import="java.math.BigDecimal" %>
<%
    Usuario user = (Usuario) session.getAttribute("usuario");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Sistema de Conciliación Bancaria</title>

    <!-- ⭐ EVITAR CACHÉ -->
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>
    <div class="app-container">
        <!-- Sidebar -->
        <jsp:include page="includes/sidebar.jsp" />

        <!-- Main Content -->
        <main class="main-content">
            <div class="page-header">
                        <% if ("sinPermiso".equals(request.getParameter("error"))) { %>
                <div class="alert alert-danger">
                    ⚠️ No tiene permiso para acceder a esa sección.
                </div>
            <% } %>
                <h1>Inicio</h1>
                <p>Resumen General De Operaciones Bancarias</p>
            </div>

            <!-- Bienvenida -->
            <div class="card" style="padding: 24px 28px; margin-bottom: 20px;">
                <div style="display: flex; align-items: center; gap: 20px; flex-wrap: wrap;">

                    <!-- Avatar con iniciales -->
                    <div style="width: 64px; height: 64px; border-radius: 50%; background: linear-gradient(135deg, #1B3F7A, #2E5FA3); display: flex; align-items: center; justify-content: center; color: #FFFFFF; font-size: 24px; font-weight: 700; letter-spacing: 1px; flex-shrink: 0;">
                        <%= user.getNombre().substring(0, 1).toUpperCase() %><%= user.getApellido().substring(0, 1).toUpperCase() %>
                    </div>

                    <!-- Info del usuario -->
                    <div style="flex: 1; min-width: 220px;">
                        <div style="font-size: 11px; font-weight: 600; letter-spacing: 0.05em; text-transform: uppercase; color: #5A6A7E; margin-bottom: 4px;">
                            Bienvenido de nuevo
                        </div>
                        <h3 style="font-family: 'DM Serif Display', Georgia, serif; font-size: 22px; color: #0D1B2A; margin: 0 0 10px;">
                            <%= user.getNombre() %> <%= user.getApellido() %>
                        </h3>
                        <div style="display: flex; gap: 10px; flex-wrap: wrap;">
                            <span style="display: inline-flex; align-items: center; gap: 6px; background: #EFF4FF; color: #1B3F7A; padding: 4px 12px; border-radius: 99px; font-size: 12px; font-weight: 600;">
                                <i class="fas fa-user-shield"></i>
                                <%= user.getRol() %>
                            </span>
                            <span style="display: inline-flex; align-items: center; gap: 6px; background: #F0FDF4; color: #166534; padding: 4px 12px; border-radius: 99px; font-size: 12px; font-weight: 600;">
                                <i class="fas fa-envelope"></i>
                                <%= user.getCorreo() %>
                            </span>
                        </div>
                    </div>

                    <!-- Último acceso -->
                    <div style="text-align: right; min-width: 160px;">
                        <div style="font-size: 11px; font-weight: 600; letter-spacing: 0.05em; text-transform: uppercase; color: #5A6A7E; margin-bottom: 4px;">
                            Último acceso
                        </div>
                        <div style="font-size: 14px; color: #0D1B2A; font-weight: 500;">
                            <%
                                java.text.SimpleDateFormat sdfAcceso = new java.text.SimpleDateFormat("dd/MM/yyyy");
                                java.text.SimpleDateFormat sdfHora = new java.text.SimpleDateFormat("HH:mm");
                                java.util.Date ahora = new java.util.Date();
                            %>
                            <%= sdfAcceso.format(ahora) %>
                            <span style="color: #5A6A7E; font-weight: 400; margin-left: 6px;">
                                <i class="fas fa-clock"></i> <%= sdfHora.format(ahora) %>
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Estadísticas -->
            <div class="stats-grid">
                <div class="stat-card stat-card-blue">
                    <span class="stat-label">Total cheques emitidos</span>
                    <span class="stat-value"><%= request.getAttribute("emitidos") %></span>
                    <span class="stat-sub">este mes</span>
                </div>

                <div class="stat-card stat-card-red">
                    <span class="stat-label">Cheques anulados</span>
                    <span class="stat-value"><%= request.getAttribute("anulados") %></span>
                    <span class="stat-sub">este mes</span>
                </div>

                <div class="stat-card stat-card-violet">
                    <span class="stat-label">Sacados de circulación</span>
                    <span class="stat-value"><%= request.getAttribute("circulacion") %></span>
                    <span class="stat-sub">este mes</span>
                </div>

                <div class="stat-card stat-card-teal">
                    <span class="stat-label">Monto total depósitos</span>
                    <span class="stat-value amount-mono">B/. <%= String.format("%,.2f", ((BigDecimal) request.getAttribute("totalDepositos")).doubleValue()) %></span>
                    <span class="stat-sub">acumulado</span>
                </div>

                <div class="stat-card stat-card-green">
                    <span class="stat-label">Monto total cheques</span>
                    <span class="stat-value amount-mono">B/. <%= String.format("%,.2f", ((BigDecimal) request.getAttribute("totalCheques")).doubleValue()) %></span>
                    <span class="stat-sub">acumulado</span>
                </div>

                <% 
                    model.Conciliacion ultima = (model.Conciliacion) request.getAttribute("ultimaConciliacion");
                    if (ultima != null) {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                        String fechaUltima = sdf.format(ultima.getFechaConciliacion());
                %>
                <div class="stat-card stat-card-gold">
                    <span class="stat-label">Última conciliación</span>
                    <span class="stat-value"><%= fechaUltima %></span>
                    <span class="stat-sub">conciliado exitosamente</span>
                </div>
                <% } %>
            </div>
        </main>
    </div>

    <script src="<%= request.getContextPath() %>/js/funciones.js"></script>

<script>
    (function() {
        // ⭐ Bloquear navegación hacia atrás
        window.history.pushState(null, null, window.location.href);

        window.addEventListener('popstate', function() {
            window.history.pushState(null, null, window.location.href);
        });

        // ⭐ Forzar recarga si la página viene de caché (retroceso)
        window.addEventListener('pageshow', function(event) {
            if (event.persisted || (typeof performance !== 'undefined' && performance.navigation.type === 2)) {
                window.location.reload();
            }
        });
    })();
</script>
</body>
</html>