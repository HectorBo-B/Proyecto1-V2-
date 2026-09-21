<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Usuario, java.util.List" %>
<%
    Usuario user = (Usuario) session.getAttribute("usuario");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    if (!"ADMIN".equalsIgnoreCase(user.getRol())) {
        response.sendRedirect("DashboardServlet");
        return;
    }
    List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");
    String error = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Usuarios - Sistema de Conciliación Bancaria</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/estilos.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="includes/sidebar.jsp" />

        <main class="main-content">
            <div class="page-header">
                <div>
                    <h1>Gestión de Usuarios</h1>
                    <p>Administración de cuentas del sistema</p>
                </div>
                <button class="btn btn-primary" onclick="abrirModalUsuario()">
                    <i class="fas fa-plus"></i> Nuevo usuario
                </button>
            </div>

            <% if ("usuarioExiste".equals(error)) { %>
                <div class="alert alert-danger">❌ Ya existe un usuario con ese nombre de usuario.</div>
            <% } %>
            <% if ("correoExiste".equals(error)) { %>
                <div class="alert alert-danger">❌ Ya existe un usuario con ese correo.</div>
            <% } %>

            <div class="card">
                <div class="card-toolbar" style="border-bottom: none; margin-bottom: 0;">
                    <div class="filter-group" style="flex: 0 0 400px; max-width: 400px;">
                        <label>Buscar</label>
                        <input type="text" id="inputBuscar" class="form-control"
                               placeholder="Buscar por nombre, usuario o correo..."
                               onkeyup="buscarAutomatico()">
                    </div>
                    <div class="filter-group" style="flex: 0 0 200px; max-width: 200px;">
                        <label>Rol</label>
                        <select id="selectRol" class="form-control" onchange="buscarAutomatico()">
                            <option value="TODOS">Todos</option>
                            <option value="ADMIN">Administrador</option>
                            <option value="CONTADOR">Contador</option>
                            <option value="AUXILIAR">Auxiliar</option>
                            <option value="AUDITOR">Auditor</option>
                        </select>
                    </div>
                    <div class="filter-group" style="flex: 0 0 auto; max-width: none; display:flex; align-items:flex-end;">
                        <a href="#" class="btn btn-secondary" onclick="cargarTodos(); return false;">
                            <i class="fas fa-sync-alt"></i> Limpiar
                        </a>
                    </div>
                </div>
            </div>

            <div class="card">
                <div id="tablaUsuarios">
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Nombre completo</th>
                                    <th>Usuario</th>
                                    <th>Correo</th>
                                    <th>Rol</th>
                                    <th>Estado</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (usuarios != null && !usuarios.isEmpty()) { %>
                                    <% for (Usuario u : usuarios) { %>
                                        <tr>
                                            <td><%= u.getIdUsuario() %></td>
                                            <td><%= u.getNombre() %> <%= u.getApellido() %></td>
                                            <td><%= u.getUsuario() %></td>
                                            <td><%= u.getCorreo() %></td>
                                            <td>
                                                <span class="badge <%= 
                                                    "ADMIN".equals(u.getRol()) ? "badge-emitido" :
                                                    "CONTADOR".equals(u.getRol()) ? "badge-cobrado" :
                                                    "AUXILIAR".equals(u.getRol()) ? "badge-pendiente" :
                                                    "badge-circulacion" %>">
                                                    <%= u.getRol() %>
                                                </span>
                                            </td>
                                            <td>
                                                <span class="badge <%= u.getEstado() == 1 ? "badge-activo" : "badge-inactivo" %>">
                                                    <%= u.getEstado() == 1 ? "ACTIVO" : "INACTIVO" %>
                                                </span>
                                            </td>
                                            <td>
                                                <button class="btn-icon" onclick="editarUsuario(<%= u.getIdUsuario() %>)" title="Editar">
                                                    <i class="fas fa-edit"></i>
                                                </button>
                                                <% if (u.getIdUsuario() != user.getIdUsuario()) { %>
                                                 <button class="btn-icon"
                                                        onclick="cambiarEstado({
                                                            id: <%= u.getIdUsuario() %>,
                                                            nombre: '<%= u.getNombre().replace("'", "\\'") %> <%= u.getApellido().replace("'", "\\'") %>',
                                                            etiquetaNombre: 'Usuario',
                                                            estadoActual: <%= u.getEstado() %>,
                                                            estadoInactivo: 0,
                                                            entidad: 'usuario',
                                                            url: 'UsuarioServlet?accion=cambiarEstado'
                                                        })"
                                                        title="<%= u.getEstado() == 1 ? "Poner inactivo" : "Poner activo" %>">
                                                    <i class="fas fa-trash-alt"></i>
                                                </button>
                                                <% } %>
                                            </td>
                                        </tr>
                                    <% } %>
                                <% } else { %>
                                    <tr><td colspan="7" class="text-center">No hay usuarios registrados</td></tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <!-- MODAL CREAR/EDITAR USUARIO -->
    <div class="modal fade" id="modalUsuario" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="UsuarioServlet" method="post" autocomplete="off" id="formUsuario">
                    <input type="hidden" name="accion" id="formAccion" value="crear">
                    <input type="hidden" name="id" id="formId">
                    <div class="modal-header">
                        <h5 class="modal-title" id="modalTitle">Nuevo usuario</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Nombre <span class="required">*</span></label>
                                    <input type="text" name="nombre" id="formNombre" class="form-control" required>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Apellido <span class="required">*</span></label>
                                    <input type="text" name="apellido" id="formApellido" class="form-control" required>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label>Nombre de usuario <span class="required">*</span></label>
                            <input type="text" name="usuario" id="formUsuarioNombre" class="form-control" required>
                        </div>
                        <div class="form-group">
                            <label>Contraseña <span class="required" id="reqPass">*</span></label>
                            <input type="password" name="password" id="formPassword" class="form-control">
                            <small id="ayudaPass" style="color:#5A6A7E; font-size:11px;"></small>
                        </div>
                        <div class="form-group">
                            <label>Correo <span class="required">*</span></label>
                            <input type="email" name="correo" id="formCorreo" class="form-control" required>
                        </div>
                        <div class="form-group">
                            <label>Rol <span class="required">*</span></label>
                            <select name="rol" id="formRol" class="form-control" required>
                                <option value="">--- Seleccionar rol ---</option>
                                <option value="ADMIN">Administrador</option>
                                <option value="CONTADOR">Contador</option>
                                <option value="AUXILIAR">Auxiliar</option>
                                <option value="AUDITOR">Auditor</option>
                            </select>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-primary">Guardar usuario</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="<%= request.getContextPath() %>/js/funciones.js"></script>
    <script>
        // ⭐ Abrir modal para crear
        function abrirModalUsuario() {
            document.getElementById('formAccion').value = 'crear';
            document.getElementById('formId').value = '';
            document.getElementById('formNombre').value = '';
            document.getElementById('formApellido').value = '';
            document.getElementById('formUsuarioNombre').value = '';
            document.getElementById('formPassword').value = '';
            document.getElementById('formPassword').required = true;
            document.getElementById('formCorreo').value = '';
            document.getElementById('formRol').value = '';
            document.getElementById('modalTitle').textContent = 'Nuevo usuario';
            document.getElementById('ayudaPass').textContent = '';
            new bootstrap.Modal(document.getElementById('modalUsuario')).show();
        }

        // ⭐ Abrir modal para editar
        function editarUsuario(id) {
            fetch('UsuarioServlet?accion=obtener&id=' + id)
                .then(response => response.json())
                .then(data => {
                    document.getElementById('formAccion').value = 'actualizar';
                    document.getElementById('formId').value = data.idUsuario;
                    document.getElementById('formNombre').value = data.nombre;
                    document.getElementById('formApellido').value = data.apellido;
                    document.getElementById('formUsuarioNombre').value = data.usuario;
                    document.getElementById('formPassword').value = '';
                    document.getElementById('formPassword').required = false;
                    document.getElementById('formCorreo').value = data.correo;
                    document.getElementById('formRol').value = data.rol;
                    document.getElementById('modalTitle').textContent = 'Editar usuario';
                    document.getElementById('ayudaPass').textContent = 'Dejar vacío para no cambiar la contraseña.';
                    new bootstrap.Modal(document.getElementById('modalUsuario')).show();
                })
                .catch(error => console.error('Error:', error));
        }

        // ⭐ Búsqueda automática
        function buscarAutomatico() {
            var texto = document.getElementById('inputBuscar').value.trim();
            var rol = document.getElementById('selectRol').value;

            if (texto.length === 0 && rol === 'TODOS') {
                cargarTodos();
                return;
            }

            fetch('UsuarioServlet?accion=buscar&texto=' + encodeURIComponent(texto) + '&rol=' + encodeURIComponent(rol))
                .then(response => response.text())
                .then(html => {
                    var parser = new DOMParser();
                    var doc = parser.parseFromString(html, 'text/html');
                    var tabla = doc.querySelector('#tablaUsuarios');
                    if (tabla) {
                        document.getElementById('tablaUsuarios').innerHTML = tabla.innerHTML;
                    }
                })
                .catch(error => console.error('Error:', error));
        }

        function cargarTodos() {
            fetch('UsuarioServlet?accion=listar')
                .then(response => response.text())
                .then(html => {
                    var parser = new DOMParser();
                    var doc = parser.parseFromString(html, 'text/html');
                    var tabla = doc.querySelector('#tablaUsuarios');
                    if (tabla) {
                        document.getElementById('tablaUsuarios').innerHTML = tabla.innerHTML;
                    }
                    document.getElementById('inputBuscar').value = '';
                    document.getElementById('selectRol').value = 'TODOS';
                })
                .catch(error => console.error('Error:', error));
        }
    </script>
</body>
</html>