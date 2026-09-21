<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Usuario, model.Cheque, model.Proveedor, model.ObjetoGasto, java.util.List" %>
<%
    Usuario user = (Usuario) session.getAttribute("usuario");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    List<Cheque> cheques = (List<Cheque>) request.getAttribute("cheques");
    List<Proveedor> proveedores = (List<Proveedor>) request.getAttribute("proveedores");
    List<ObjetoGasto> objetos = (List<ObjetoGasto>) request.getAttribute("objetos");
    String modo = (String) request.getAttribute("modo");
    Cheque cheque = (Cheque) request.getAttribute("cheque");
    boolean puedeModificar = "ADMIN".equalsIgnoreCase(user.getRol()) 
                           || "CONTADOR".equalsIgnoreCase(user.getRol()) 
                           || "AUXILIAR".equalsIgnoreCase(user.getRol());
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cheques - Sistema de Conciliación Bancaria</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/estilos.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

    <style>
        .modal-preview .modal-dialog {
            max-width: 1100px;
            transition: max-width 0.25s ease;
        }
        .modal-preview .preview-panel { display: none; }
        .modal-preview.preview-open .preview-panel { display: block; }
        .modal-preview.preview-open .modal-dialog { max-width: 1250px; }
        .modal-body-flex { display: flex; gap: 20px; }
        .modal-body-flex .form-side { flex: 1 1 auto; min-width: 0; }
        .modal-body-flex .preview-panel { flex: 0 0 460px; max-width: 460px; }
        .cheque-preview {
            background: #FFFFFF;
            border: 1px solid #CBD5E1;
            padding: 18px 22px;
            color: #1E293B;
            font-size: 13px;
        }
        .cheque-preview-header {
            display: flex;
            justify-content: flex-end;
            align-items: baseline;
            border-bottom: 1px solid #CBD5E1;
            padding-bottom: 6px;
            margin-bottom: 14px;
        }
        .cheque-preview-numero { font-weight: 700; font-size: 14px; }
        .cheque-preview-line { display: flex; align-items: baseline; margin-bottom: 12px; }
        .cheque-preview-line .value {
            flex: 1;
            border-bottom: 1px dotted #94A3B8;
            padding-bottom: 2px;
            font-weight: 600;
            word-break: break-word;
        }
        .cheque-preview-monto { font-size: 15px; font-weight: 700; }
        .btn-preview-toggle {
            background: transparent;
            border: 1px solid var(--border, #D1DBE8);
            border-radius: 6px;
            padding: 4px 10px;
            font-size: 14px;
            color: #1B3F7A;
            cursor: pointer;
            transition: all 0.15s ease;
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }
        .btn-preview-toggle:hover { background: #EFF4FF; border-color: #1B3F7A; }
        .btn-preview-toggle.active { background: #1B3F7A; color: #FFFFFF; }
        @media (max-width: 992px) {
            .modal-body-flex .preview-panel { display: none !important; }
        }
    </style>
</head>
<body>
    <div class="app-container">
        <jsp:include page="includes/sidebar.jsp" />

        <main class="main-content">
            <div class="page-header">
                <div>
                    <h1>Gestión de Cheques</h1>
                    <p>Registro y seguimiento de cheques emitidos</p>
                </div>
                <% if (puedeModificar) { %>
                    <button class="btn btn-primary" onclick="abrirModalCheque()">
                        <i class="fas fa-plus"></i> Registrar cheque
                    </button>
                <% } %>
            </div>

            <div class="card">
                <div class="card-toolbar" style="border-bottom: none; margin-bottom: 0;">
                    <div class="filter-group" style="flex: 0 0 400px; max-width: 400px;">
                        <label>Buscar</label>
                        <input type="text" id="inputBuscar" class="form-control"
                               placeholder="Buscar por N.º o beneficiario..."
                               onkeyup="buscarAutomatico()">
                    </div>
                    <div class="filter-group" style="flex: 0 0 200px; max-width: 200px;">
                        <label>Estado</label>
                        <select id="selectEstado" class="form-control" onchange="buscarAutomatico()">
                            <option value="0">Todos</option>
                            <option value="1">Emitido</option>
                            <option value="4">Anulado</option>
                            <option value="5">Sacado de circulación</option>
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
                <div id="tablaCheques">
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>N.º Cheque</th>
                                    <th>Beneficiario</th>
                                    <th>Monto</th>
                                    <th>Objeto de gasto</th>
                                    <th>Fecha</th>
                                    <th>Estado</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (cheques != null && !cheques.isEmpty()) { %>
                                    <% for (Cheque c : cheques) { %>
                                        <tr>
                                            <td><%= c.getNumeroCheque() %></td>
                                            <td><%= c.getNombreProveedor() != null ? c.getNombreProveedor() : "-" %></td>
                                            <td>B/. <%= String.format("%,.2f", c.getMonto().doubleValue()) %></td>
                                            <td><%= c.getNombreObjetoGasto() != null ? c.getNombreObjetoGasto() : "-" %></td>
                                            <td><%= c.getFechaCheque() %></td>
                                            <td>
                                                <span class="badge <%= 
                                                    c.getEstado() == 4 ? "badge-anulado" :
                                                    c.getEstado() == 5 ? "badge-circulacion" :
                                                    "badge-emitido" %>">
                                                    <%= 
                                                        c.getEstado() == 4 ? "ANULADO" :
                                                        c.getEstado() == 5 ? "FUERA DE CIRCULACIÓN" :
                                                        "EMITIDO" %>
                                                </span>
                                            </td>
                                            <td>
                                                <% if (!puedeModificar) { %>
                                                    <span style="color:#94A3B8; font-size:12px; font-style:italic;">Solo lectura</span>
                                                <% } else if (c.getEstado() == 1) { %>
                                                    <button class="btn-icon" onclick="editarCheque(<%= c.getIdCheque() %>)" title="Editar">
                                                        <i class="fas fa-edit"></i>
                                                    </button>
                                                    <button class="btn-icon" onclick="anularCheque(<%= c.getIdCheque() %>)" style="color:#DC2626;" title="Anular">
                                                        <i class="fas fa-ban"></i>
                                                    </button>
                                                    <button class="btn-icon" onclick="sacarCirculacion(<%= c.getIdCheque() %>)" style="color:purple;" title="Sacar de circulación">
                                                        <i class="fa-regular fa-circle-xmark"></i>
                                                    </button>
                                                <% } else { %>
                                                    <span style="color:#94A3B8; font-size:12px; font-style:italic;">Sin acciones</span>
                                                <% } %>
                                            </td>
                                        </tr>
                                    <% } %>
                                <% } else { %>
                                    <tr>
                                        <td colspan="7" class="text-center">No hay cheques registrados</td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- MODAL CREAR/EDITAR -->
            <div class="modal fade modal-preview" id="modalCheque" tabindex="-1">
                <div class="modal-dialog modal-lg modal-dialog-centered">
                    <div class="modal-content">
                        <form action="ChequeServlet" method="post" id="formCheque" autocomplete="off" onsubmit="return validarFormularioCheque()">
                            <input type="hidden" name="accion" id="formAccion" value="crear">
                            <input type="hidden" name="id" id="formId">
                            <div class="modal-header">
                                <h5 class="modal-title" id="modalTitle">Registrar cheque</h5>
                                <div style="display:flex; align-items:center; gap:10px;">
                                    <button type="button" class="btn-preview-toggle" id="btnPreviewToggle"
                                            onclick="togglePreviewCheque()" title="Previsualizar cheque">
                                        <i class="fa-solid fa-eye"></i>
                                        <span id="btnPreviewLabel">Previsualizar</span>
                                    </button>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                            </div>
                            <div class="modal-body">
                                <div class="modal-body-flex">
                                    <div class="form-side">
                                        <div class="row">
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label>N.º de cheque <span class="required">*</span></label>
                                                    <span id="formNumero" class="form-control" style="background:#f8f9fa; font-weight:bold; color:#1B3F7A; display:block; width:100%;"></span>
                                                    <input type="hidden" name="numero_cheque" id="formNumeroHidden">
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label>Fecha del cheque <span class="required">*</span></label>
                                                    <span id="formFecha" class="form-control" style="background:#f8f9fa; font-weight:bold; color:#1B3F7A; display:block; width:100%;"></span>
                                                    <input type="hidden" name="fecha_cheque" id="formFechaHidden" required>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="row">
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label>Proveedor / Beneficiario <span class="required">*</span></label>
                                                    <select name="id_proveedor" id="formProveedor" class="form-control" required>
                                                        <option value="">--- Seleccionar proveedor ---</option>
                                                        <% if (proveedores != null) {
                                                            for (Proveedor p : proveedores) { %>
                                                                <option value="<%= p.getIdProveedor() %>" data-nombre="<%= p.getNombre() %>"><%= p.getNombre() %></option>
                                                        <%   }
                                                        } %>
                                                    </select>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label>Objeto de gasto <span class="required">*</span></label>
                                                    <select name="id_objeto_gasto" id="formObjeto" class="form-control" required>
                                                        <option value="">--- Seleccionar objeto ---</option>
                                                        <% if (objetos != null) {
                                                            for (ObjetoGasto o : objetos) { %>
                                                                <option value="<%= o.getIdObjetoGasto() %>" data-nombre="<%= o.getDescripcion() %>"><%= o.getDescripcion() %></option>
                                                        <%   }
                                                        } %>
                                                    </select>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="row">
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label>Monto (B/.) <span class="required">*</span></label>
                                                    <input type="text" name="monto" id="formMonto" class="form-control monto-input"
                                                           placeholder="0.00"
                                                           oninput="formatearMontoEnVivo(this); actualizarMontoLetrasModal(); actualizarPreviewCheque();"
                                                           onblur="completarCentavos(this); actualizarMontoLetrasModal(); actualizarPreviewCheque();"
                                                           onfocus="this.select()"
                                                           maxlength="14">
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label>Monto en letras</label>
                                                    <input type="text" name="monto_letras" id="formMontoLetras" class="form-control"
                                                           readonly style="background:#f8f9fa; text-transform:uppercase;">
                                                </div>
                                            </div>
                                        </div>

                                        <div class="row">
                                            <div class="col-md-12">
                                                <div class="form-group">
                                                    <label>Detalle / Concepto</label>
                                                    <textarea name="detalle" id="formDetalle" class="form-control" rows="2" 
                                                              placeholder="Descripción del pago..."
                                                              oninput="actualizarPreviewCheque();"></textarea>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="preview-panel">
                                        <label style="font-size:11px; font-weight:600; text-transform:uppercase; letter-spacing:0.5px; color:#5A6A7E; display:block; margin-bottom:8px;">
                                            Previsualización
                                        </label>
                                        <div class="cheque-preview">
                                            <div class="cheque-preview-header">
                                                <div><span id="pvFecha">—</span></div>
                                            </div>

                                            <div class="cheque-preview-line">
                                                <span class="value" id="pvBeneficiario">—</span>
                                            </div>

                                            <div class="cheque-preview-line">
                                                <span class="value cheque-preview-monto" id="pvMonto">B/. 0.00</span>
                                            </div>

                                            <div class="cheque-preview-line">
                                                <span class="value" id="pvLetras">—</span>
                                            </div>

                                            <div class="cheque-preview-line">
                                                <span class="value" id="pvDetalle">—</span>
                                            </div>

                                            <div class="cheque-preview-line">
                                                <span class="value" id="pvObjeto">—</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                                <button type="submit" class="btn btn-primary">Guardar cheque</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <!-- Modal Anular Cheque -->
    <div class="modal fade" id="modalAnular" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="ChequeServlet" method="get">
                    <input type="hidden" name="accion" value="anular">
                    <input type="hidden" name="id" id="anularId">
                    <div class="modal-header">
                        <h5 class="modal-title" style="color:#DC2626;">
                            <i class="fas fa-exclamation-triangle"></i> Anular cheque
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <p>¿Está seguro de que desea anular este cheque? Esta acción no se puede deshacer.</p>
                        <div class="form-group">
                            <label>Motivo de anulación <span class="required">*</span></label>
                            <textarea name="motivo" class="form-control" rows="3" placeholder="Describa el motivo de la anulación..." required></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="fas fa-times"></i> Cancelar
                        </button>
                        <button type="submit" class="btn btn-danger">
                            <i class="fas fa-ban"></i> Confirmar anulación
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Modal Sacar de Circulación -->
    <div class="modal fade" id="modalCirculacion" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="ChequeServlet" method="get">
                    <input type="hidden" name="accion" value="sacarCirculacion">
                    <input type="hidden" name="id" id="circulacionId">
                    <div class="modal-header">
                        <h5 class="modal-title" style="color:#7C3AED;">
                            <i class="fa-regular fa-circle-xmark"></i> Sacar de circulación
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <p>¿Está seguro de que desea sacar este cheque de circulación? Esta acción no se puede deshacer.</p>
                        <div class="form-group">
                            <label>Observación <span class="required">*</span></label>
                            <textarea name="observacion" class="form-control" rows="3" placeholder="Describa el motivo..." required></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="fas fa-times"></i> Cancelar
                        </button>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-check"></i> Confirmar
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="js/funciones.js"></script>
    <script>
        function anularCheque(id) {
            document.getElementById('anularId').value = id;
            new bootstrap.Modal(document.getElementById('modalAnular')).show();
        }
        function sacarCirculacion(id) {
            document.getElementById('circulacionId').value = id;
            new bootstrap.Modal(document.getElementById('modalCirculacion')).show();
        }
        function obtenerFechaActualFormateada() {
            var hoy = new Date();
            var dia = String(hoy.getDate()).padStart(2, '0');
            var mes = String(hoy.getMonth() + 1).padStart(2, '0');
            var anio = hoy.getFullYear();
            return { display : dia + '/' + mes + '/' + anio, iso : anio + '-' + mes + '-' + dia };
        }
        function togglePreviewCheque() {
            var modal = document.getElementById('modalCheque');
            var btn = document.getElementById('btnPreviewToggle');
            var label = document.getElementById('btnPreviewLabel');
            modal.classList.toggle('preview-open');
            btn.classList.toggle('active');
            if (modal.classList.contains('preview-open')) {
                label.textContent = 'Ocultar';
                actualizarPreviewCheque();
            } else {
                label.textContent = 'Previsualizar';
            }
        }
        function valorTexto(id) {
            var el = document.getElementById(id);
            if (!el) return '';
            return (el.value || el.textContent || '').trim();
        }
        function actualizarPreviewCheque() {
            var fechaDisplay = valorTexto('formFecha');
            document.getElementById('pvFecha').textContent = fechaDisplay || '—';

            var selProv = document.getElementById('formProveedor');
            var nombreProv = '';
            if (selProv && selProv.selectedIndex >= 0) {
                var opt = selProv.options[selProv.selectedIndex];
                if (opt && opt.value) nombreProv = opt.getAttribute('data-nombre') || opt.textContent || '';
            }
            document.getElementById('pvBeneficiario').textContent = nombreProv || '—';

            var monto = valorTexto('formMonto');
            document.getElementById('pvMonto').textContent = monto ? 'B/. ' + monto : 'B/. 0.00';

            var letras = valorTexto('formMontoLetras');
            document.getElementById('pvLetras').textContent = letras || '—';

            var detalle = valorTexto('formDetalle');
            document.getElementById('pvDetalle').textContent = detalle || '—';

            var selObj = document.getElementById('formObjeto');
            var nombreObj = '';
            if (selObj && selObj.selectedIndex >= 0) {
                var opt2 = selObj.options[selObj.selectedIndex];
                if (opt2 && opt2.value) nombreObj = opt2.getAttribute('data-nombre') || opt2.textContent || '';
            }
            document.getElementById('pvObjeto').textContent = nombreObj || '—';
        }
        document.addEventListener('DOMContentLoaded', function () {
            var campos = ['formProveedor', 'formObjeto', 'formMonto', 'formDetalle'];
            campos.forEach(function (id) {
                var el = document.getElementById(id);
                if (el) {
                    el.addEventListener('input', actualizarPreviewCheque);
                    el.addEventListener('change', actualizarPreviewCheque);
                }
            });
        });
        function abrirModalCheque() {
            document.getElementById('formAccion').value = 'crear';
            document.getElementById('formId').value = '';
            document.getElementById('formNumero').textContent = '...';
            document.getElementById('formNumeroHidden').value = '';
            document.getElementById('formProveedor').value = '';
            document.getElementById('formObjeto').value = '';
            document.getElementById('formMonto').value = '';
            document.getElementById('formMontoLetras').value = '';
            document.getElementById('formDetalle').value = '';
            document.getElementById('modalTitle').textContent = 'Registrar cheque';

            var modal = document.getElementById('modalCheque');
            modal.classList.remove('preview-open');
            document.getElementById('btnPreviewToggle').classList.remove('active');
            document.getElementById('btnPreviewLabel').textContent = 'Previsualizar';

            var fechaActual = obtenerFechaActualFormateada();
            document.getElementById('formFecha').textContent = fechaActual.display;
            document.getElementById('formFechaHidden').value = fechaActual.iso;

            fetch('ChequeServlet?accion=obtenerNumero')
                .then(response => response.json())
                .then(data => {
                    document.getElementById('formNumero').textContent = data.numero;
                    document.getElementById('formNumeroHidden').value = data.numero;
                    actualizarPreviewCheque();
                })
                .catch(error => console.error('Error:', error));

            new bootstrap.Modal(modal).show();
        }
        function editarCheque(id) {
            fetch('ChequeServlet?accion=obtener&id=' + id)
                .then(response => response.json())
                .then(data => {
                    document.getElementById('formAccion').value = 'actualizar';
                    document.getElementById('formId').value = data.idCheque;
                    document.getElementById('formNumero').textContent = data.numeroCheque;
                    document.getElementById('formNumeroHidden').value = data.numeroCheque;
                    document.getElementById('formFecha').textContent = data.fechaCheque;
                    document.getElementById('formFechaHidden').value = data.fechaCheque;
                    document.getElementById('formProveedor').value = data.idProveedor;
                    document.getElementById('formObjeto').value = data.idObjetoGasto;
                    document.getElementById('formDetalle').value = data.detalle || '';
                    document.getElementById('modalTitle').textContent = 'Editar cheque';

                    var montoInput = document.getElementById('formMonto');
                    montoInput.value = data.monto || '';
                    if (typeof formatearMontoConComas === 'function') {
                        formatearMontoConComas(montoInput);
                    }
                    document.getElementById('formMontoLetras').value = data.montoLetras || '';

                    var modal = document.getElementById('modalCheque');
                    modal.classList.remove('preview-open');
                    document.getElementById('btnPreviewToggle').classList.remove('active');
                    document.getElementById('btnPreviewLabel').textContent = 'Previsualizar';

                    actualizarPreviewCheque();
                    new bootstrap.Modal(modal).show();
                })
                .catch(error => console.error('Error:', error));
        }
        function validarFormularioCheque() {
            var numero = document.getElementById('formNumeroHidden').value.trim();
            var fecha = document.getElementById('formFechaHidden').value.trim();
            var proveedor = document.getElementById('formProveedor').value;
            var objeto = document.getElementById('formObjeto').value;
            var monto = document.getElementById('formMonto').value.trim();

            if (numero === '') {
                alert('⚠️ El número de cheque aún no se ha generado, espere un momento e intente de nuevo.');
                return false;
            }
            if (fecha === '') {
                alert('⚠️ No se pudo establecer la fecha del cheque.');
                return false;
            }
            if (proveedor === '') {
                alert('⚠️ Por favor, seleccione un proveedor.');
                document.getElementById('formProveedor').focus();
                return false;
            }
            if (objeto === '') {
                alert('⚠️ Por favor, seleccione un objeto de gasto.');
                document.getElementById('formObjeto').focus();
                return false;
            }
            if (monto === '' || parseFloat(monto.replace(/,/g, '')) <= 0) {
                alert('⚠️ Por favor, ingrese un monto mayor que cero.');
                document.getElementById('formMonto').focus();
                return false;
            }
            return true;
        }
        function buscarAutomatico() {
            var texto = document.getElementById('inputBuscar').value.trim();
            var estado = document.getElementById('selectEstado').value;

            if (texto.length === 0 && estado === "0") {
                cargarTodos();
                return;
            }

            var url = 'ChequeServlet?accion=buscar'
                    + '&texto=' + encodeURIComponent(texto)
                    + '&estado=' + encodeURIComponent(estado);

            fetch(url)
                .then(response => response.text())
                .then(html => {
                    var parser = new DOMParser();
                    var doc = parser.parseFromString(html, 'text/html');
                    var tabla = doc.querySelector('#tablaCheques');
                    if (tabla) {
                        document.getElementById('tablaCheques').innerHTML = tabla.innerHTML;
                    }
                })
                .catch(error => console.error('Error:', error));
        }
        function cargarTodos() {
            fetch('ChequeServlet?accion=listar')
                .then(response => response.text())
                .then(html => {
                    var parser = new DOMParser();
                    var doc = parser.parseFromString(html, 'text/html');
                    var tabla = doc.querySelector('#tablaCheques');
                    if (tabla) {
                        document.getElementById('tablaCheques').innerHTML = tabla.innerHTML;
                    }
                    document.getElementById('inputBuscar').value = '';
                    document.getElementById('selectEstado').value = '0';
                })
                .catch(error => console.error('Error:', error));
        }
    </script>
</body>
</html>