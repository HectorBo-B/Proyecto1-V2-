<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Usuario, model.Proveedor, java.util.List" %>
<%
    Usuario user = (Usuario) session.getAttribute("usuario");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    List<Proveedor> proveedores = (List<Proveedor>) request.getAttribute("proveedores");
    boolean puedeModificar = "ADMIN".equalsIgnoreCase(user.getRol()) 
                           || "CONTADOR".equalsIgnoreCase(user.getRol()) 
                           || "AUXILIAR".equalsIgnoreCase(user.getRol());
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Proveedores - Sistema de Conciliación Bancaria</title>
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
                    <h1>Proveedores</h1>
                    <p>Administración del catálogo de proveedores</p>
                </div>
                <% if (puedeModificar) { %>
                    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalProveedor">
                        <i class="fas fa-plus"></i> Nuevo proveedor
                    </button>
                <% } %>
            </div>

            <div class="card">
                <div class="card-toolbar" style="border-bottom: none; margin-bottom: 0;">
                    <div class="filter-group" style="flex: 0 0 400px; max-width: 400px;">
                        <label>Buscar</label>
                        <input type="text" id="inputBuscar" class="form-control"
                               placeholder="Buscar por nombre o RUC..."
                               onkeyup="buscarAutomatico()">
                    </div>
                    <div class="filter-group" style="flex: 0 0 auto; max-width: none; display:flex; align-items:flex-end;">
                        <a href="#" class="btn btn-secondary" onclick="cargarTodos(); return false;">
                            <i class="fas fa-sync-alt"></i> Limpiar
                        </a>
                    </div>
                </div>

                <div id="tablaProveedores">
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>RUC</th>
                                    <th>Nombre</th>
                                    <th>Teléfono</th>
                                    <th>Correo</th>
                                    <th>Estado</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (proveedores != null && !proveedores.isEmpty()) { %>
                                    <% for (Proveedor p : proveedores) { %>
                                        <tr>
                                            <td><%= p.getIdProveedor() %></td>
                                            <td><%= p.getRuc() %></td>
                                            <td><%= p.getNombre() %></td>
                                            <td><%= p.getTelefono() != null ? p.getTelefono() : "-" %></td>
                                            <td><%= p.getCorreo() != null ? p.getCorreo() : "-" %></td>
                                            <td>
                                                <span class="badge <%= p.getEstado() == 1 ? "badge-activo" : "badge-inactivo" %>">
                                                    <%= p.getEstado() == 1 ? "ACTIVO" : "INACTIVO" %>
                                                </span>
                                            </td>
                                            <td>
                                                <% if (!puedeModificar) { %>
                                                    <span style="color:#94A3B8; font-size:12px; font-style:italic;">Solo lectura</span>
                                                <% } else { %>
                                                    <button class="btn-icon" onclick="editarProveedor(<%= p.getIdProveedor() %>)" title="Editar">
                                                        <i class="fas fa-edit"></i>
                                                    </button>
                                                    <button class="btn-icon"
                                                            onclick="cambiarEstado({
                                                                id: <%= p.getIdProveedor() %>,
                                                                nombre: '<%= p.getNombre().replace("'", "\\'") %>',
                                                                etiquetaNombre: 'Nombre',
                                                                estadoActual: <%= p.getEstado() %>,
                                                                estadoInactivo: 0,
                                                                entidad: 'proveedor',
                                                                url: 'ProveedorServlet?accion=cambiarEstado'
                                                            })"
                                                            title="<%= p.getEstado() == 1 ? "Poner inactivo" : "Poner activo" %>">
                                                        <i class="fas fa-trash-alt"></i>
                                                    </button>
                                                <% } %>
                                            </td>
                                        </tr>
                                    <% } %>
                                <% } else { %>
                                    <tr>
                                        <td colspan="7" class="text-center">No hay proveedores registrados</td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <!-- MODAL CREAR PROVEEDOR -->
    <div class="modal fade" id="modalProveedor" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="ProveedorServlet" method="post" autocomplete="off">
                    <input type="hidden" name="accion" value="crear">
                    <div class="modal-header">
                        <h5 class="modal-title">Nuevo proveedor</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label>Nombre <span class="required">*</span></label>
                            <input type="text" name="nombre" class="form-control"
                                   placeholder="Ej. Servicios Integrales, S.A." required>
                        </div>
                        <div class="form-group">
                            <label>RUC <span class="required">*</span></label>
                            <input id= "ruc" type="text" name="ruc" class="form-control"
                                   placeholder="Ej. 123456-1-123456" required
                                   oninput = "SPchar(id, 1)">
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Teléfono</label>
                                    <input id= "telF" type="text" name="telefono" class="form-control"
                                           placeholder="Ej. 507-6123-4567"
                                           oninput = "SPchar(id, 0)">
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Correo</label>
                                    <input id = "mail" type="email" name="correo" class="form-control"
                                           placeholder="Ej. correo@empresa.com"
                                           oninput = "SPchar(id, 1)">
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-primary">Guardar proveedor</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="js/funciones.js"></script>
    <script>
        function buscarAutomatico() {
            var input = document.getElementById('inputBuscar');
            var texto = input.value.trim();
            if (texto.length >= 1) {
                fetch('ProveedorServlet?accion=buscar&texto=' + encodeURIComponent(texto))
                    .then(response => response.text())
                    .then(html => {
                        var parser = new DOMParser();
                        var doc = parser.parseFromString(html, 'text/html');
                        var tabla = doc.querySelector('#tablaProveedores');
                        if (tabla) {
                            document.getElementById('tablaProveedores').innerHTML = tabla.innerHTML;
                        }
                    })
                    .catch(error => console.error('Error:', error));
            } else {
                cargarTodos();
            }
        }
        function cargarTodos() {
            fetch('ProveedorServlet?accion=listar')
                .then(response => response.text())
                .then(html => {
                    var parser = new DOMParser();
                    var doc = parser.parseFromString(html, 'text/html');
                    var tabla = doc.querySelector('#tablaProveedores');
                    if (tabla) {
                        document.getElementById('tablaProveedores').innerHTML = tabla.innerHTML;
                    }
                    document.getElementById('inputBuscar').value = '';
                })
                .catch(error => console.error('Error:', error));
        }
        function editarProveedor(id) {
            abrirModalEditar({
                titulo: 'Editar proveedor',
                url: 'ProveedorServlet?accion=obtener&id=' + id,
                action: 'ProveedorServlet',
                campos: [
                    { name: 'nombre', label: 'Nombre', type: 'text', required: true, placeholder: 'Ej. Servicios Integrales, S.A.' },
                    { name: 'ruc', label: 'RUC', type: 'text', required: true, placeholder: 'Ej. 123456-1-123456' },
                    { name: 'telefono', label: 'Teléfono', type: 'text', col: 6, row: 'start', placeholder: 'Ej. 507-6123-4567' },
                    { name: 'correo', label: 'Correo', type: 'email', col: 6, row: 'end', placeholder: 'Ej. correo@empresa.com' }
                ]
            });
        }
    </script>
</body>
</html>