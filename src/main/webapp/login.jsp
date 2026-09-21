<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Sistema de Conciliación Bancaria</title>
    
    <!-- ⭐ EVITAR CACHÉ -->
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/estilos.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body class="login-body">
    <div class="login-container">
        <div class="login-card">
            <div class="login-header">
                <div class="login-logo">
                    <i class="fas fa-building-columns"></i>
                </div>
                <p>Sistema de Conciliación Bancaria</p>
            </div>
            
            <div class="login-form">
                <h2>Inicio de sesión</h2>
                
                    <% Integer segundos = (Integer) request.getAttribute("segundosBloqueo"); %>
                    <% if (segundos != null && segundos > 0) { %>
                        <div class="alert alert-danger" id="mensajeError" style="text-align: center;">
                            <span id="contadorBloqueo" style="font-weight:700;">
                                (Vuelva a intentarlo en <span id="segundos"><%= segundos %></span>s)
                            </span>
                        </div>
                    <% } else if (request.getAttribute("error") != null) { %>
                        <div class="alert alert-danger" id="mensajeError" style="text-align: center;">
                            <%= request.getAttribute("error") %>
                        </div>
                    <% } %>
                
                <% 
                    boolean bloqueado = (segundos != null && segundos > 0);
                %>
                <form action="LoginServlet" method="post" autocomplete="off">
                    <div class="form-group">
                        <label for="usuario">Usuario</label>
                        <input type="text" id="usuario" name="usuario" class="form-control" placeholder="Ingrese su usuario" required <%= bloqueado ? "disabled" : "" %>>
                    </div>
                    
                    <div class="form-group">
                        <label for="password">Contraseña</label>
                        <input type="password" id="password" name="password" class="form-control" placeholder="Ingrese su contraseña" required <%= bloqueado ? "disabled" : "" %>>
                    </div>
                    
                    <button type="submit" id="btnLogin" class="btn btn-login" <%= bloqueado ? "disabled" : "" %>>Ingresar al sistema</button>
                </form>
                
            </div>
        </div>
    </div>
    
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

        // ⭐ Contador regresivo del bloqueo
                // ⭐ Contador regresivo del bloqueo
                // ⭐ Contador regresivo del bloqueo
                // ⭐ Contador regresivo del bloqueo
        (function() {
            var contador = document.getElementById('segundos');
            if (contador) {
                var segundos = parseInt(contador.textContent, 10);
                
                var intervalo = setInterval(function() {
                    segundos--;
                    if (segundos > 0) {
                        contador.textContent = segundos;
                    } else {
                        clearInterval(intervalo);
                        // ⭐ Ocultar el recuadro completo
                        var msg = document.getElementById('mensajeError');
                        if (msg) msg.style.display = 'none';

                        // ⭐ Rehabilitar los inputs y el botón
                        var inputUsuario = document.getElementById('usuario');
                        var inputPassword = document.getElementById('password');
                        var btnLogin = document.getElementById('btnLogin');
                        if (inputUsuario) inputUsuario.disabled = false;
                        if (inputPassword) inputPassword.disabled = false;
                        if (btnLogin) btnLogin.disabled = false;

                        // ⭐ Poner el foco en el campo de usuario
                        if (inputUsuario) inputUsuario.focus();
                    }
                }, 1000);
            }
        })();

        // ⭐ Focus automático en el input de usuario si hubo un error
        (function() {
            var mensajeError = document.getElementById('mensajeError');
            var inputUsuario = document.getElementById('usuario');
            
            if (mensajeError && inputUsuario && !inputUsuario.disabled) {
                inputUsuario.focus();
            }
        })();
    </script>
</body>
</html>