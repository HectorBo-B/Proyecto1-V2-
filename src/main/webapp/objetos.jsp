<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Usuario, model.ObjetoGasto, java.util.List" %>
<%
    Usuario user = (Usuario) session.getAttribute("usuario");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    List<ObjetoGasto> objetos = (List<ObjetoGasto>) request.getAttribute("objetos");
    boolean puedeModificar = "ADMIN".equalsIgnoreCase(user.getRol()) 
                           || "CONTADOR".equalsIgnoreCase(user.getRol()) 
                           || "AUXILIAR".equalsIgnoreCase(user.getRol());
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Objetos de Gasto - Sistema de Conciliación Bancaria</title>
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
                    <h1>Objetos de Gasto</h1>
                    <p>Clasificación presupuestaria del gasto institucional</p>
                </div>
                <% if (puedeModificar) { %>
                    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalObjeto">
                        <i class="fas fa-plus"></i> Nuevo objeto de gasto
                    </button>
                <% } %>
            </div>

            <div class="card">
                <div class="card-toolbar" style="border-bottom: none; margin-bottom: 0;">
                    <div class="filter-group" style="flex: 0 0 400px; max-width: 400px;">
                        <label>Buscar</label>
                        <input type="text" id="inputBuscar" class="form-control"
                               placeholder="Buscar por código o descripción..."
                               onkeyup="buscarAutomatico()">
                    </div>
                    <div class="filter-group" style="flex: 0 0 auto; max-width: none; display:flex; align-items:flex-end;">
                        <a href="#" class="btn btn-secondary" onclick="cargarTodos(); return false;">
                            <i class="fas fa-sync-alt"></i> Limpiar
                        </a>
                    </div>
                </div>

                <div id="tablaObjetos">
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Código</th>
                                    <th>Descripción</th>
                                    <th>Estado</th>
                                    <th>Fecha creación</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (objetos != null && !objetos.isEmpty()) { %>
                                    <% for (ObjetoGasto o : objetos) { %>
                                        <tr>
                                            <td><%= o.getCodigo() %></td>
                                            <td><%= o.getDescripcion() %></td>
                                            <td>
                                                <span class="badge <%= o.getEstado() == 6 ? "badge-inactivo" : "badge-activo" %>">
                                                    <%= o.getEstado() == 6 ? "INACTIVO" : "ACTIVO" %>
                                                </span>
                                            </td>
                                            <td><%= o.getFechaCreacion() %></td>
                                            <td>
                                                <% if (!puedeModificar) { %>
                                                    <span style="color:#94A3B8; font-size:12px; font-style:italic;">Solo lectura</span>
                                                <% } else { %>
                                                    <button class="btn-icon" onclick="editarObjeto(<%= o.getIdObjetoGasto() %>)" title="Editar">
                                                        <i class="fas fa-edit"></i>
                                                    </button>
                                                    <button class="btn-icon"
                                                            onclick="cambiarEstado({
                                                                id: <%= o.getIdObjetoGasto() %>,
                                                                nombre: '<%= o.getDescripcion().replace("'", "\\'") %>',
                                                                etiquetaNombre: 'Descripción',
                                                                estadoActual: <%= o.getEstado() %>,
                                                                estadoInactivo: 6,
                                                                entidad: 'objeto de gasto',
                                                                url: 'ObjetoGastoServlet?accion=cambiarEstado'
                                                            })"
                                                            title="<%= o.getEstado() == 6 ? "Poner activo" : "Poner inactivo" %>">
                                                        <i class="fas fa-trash-alt"></i>
                                                    </button>
                                                <% } %>
                                            </td>
                                        </tr>
                                    <% } %>
                                <% } else { %>
                                    <tr>
                                        <td colspan="5" class="text-center">No hay objetos de gasto registrados</td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <!-- MODAL CREAR OBJETO -->
    <div class="modal fade" id="modalObjeto" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="ObjetoGastoServlet" method="post" autocomplete="off">
                    <input type="hidden" name="accion" value="crear">
                    <div class="modal-header">
                        <h5 class="modal-title">Nuevo objeto de gasto</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label>Código <span class="required">*</span></label>
                            <input type="text" name="codigo" class="form-control"
                                   placeholder="Ej. 1.2.03" required>
                        </div>
                        <div class="form-group">
                            <label>Descripción <span class="required">*</span></label>
                            <input type="text" name="descripcion" class="form-control"
                                   placeholder="Descripción del objeto de gasto" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-primary">Guardar</button>
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
                fetch('ObjetoGastoServlet?accion=buscar&texto=' + encodeURIComponent(texto))
                    .then(response => response.text())
                    .then(html => {
                        var parser = new DOMParser();
                        var doc = parser.parseFromString(html, 'text/html');
                        var tabla = doc.querySelector('#tablaObjetos');
                        if (tabla) {
                            document.getElementById('tablaObjetos').innerHTML = tabla.innerHTML;
                        }
                    })
                    .catch(error => console.error('Error:', error));
            } else {
                cargarTodos();
            }
        }
        function cargarTodos() {
            fetch('ObjetoGastoServlet?accion=listar')
                .then(response => response.text())
                .then(html => {
                    var parser = new DOMParser();
                    var doc = parser.parseFromString(html, 'text/html');
                    var tabla = doc.querySelector('#tablaObjetos');
                    if (tabla) {
                        document.getElementById('tablaObjetos').innerHTML = tabla.innerHTML;
                    }
                    document.getElementById('inputBuscar').value = '';
                })
                .catch(error => console.error('Error:', error));
        }
        function editarObjeto(id) {
            abrirModalEditar({
                titulo: 'Editar objeto de gasto',
                url: 'ObjetoGastoServlet?accion=obtener&id=' + id,
                action: 'ObjetoGastoServlet',
                campos: [
                    { name: 'codigo', label: 'Código', type: 'text', required: true, placeholder: 'Ej. 1.2.03' },
                    { name: 'descripcion', label: 'Descripción', type: 'text', required: true, placeholder: 'Descripción del objeto de gasto' }
                ]
            });
        }
    </script>
</body>
</html>