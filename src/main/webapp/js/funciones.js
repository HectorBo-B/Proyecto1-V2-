/**
 * Sistema de Conciliación Bancaria - UTP
 * Funciones JavaScript para validaciones y comportamiento dinámico
 */

// ============================================================
// 1. CONVERSIÓN DE MONTO A LETRAS
// ============================================================

function convertirMontoALetras(monto) {
    if (isNaN(monto) || monto <= 0) {
        return '';
    }

    monto = Math.round(monto * 100) / 100;

    var unidades = ['', 'UNO', 'DOS', 'TRES', 'CUATRO', 'CINCO', 'SEIS', 'SIETE', 'OCHO', 'NUEVE'];
    var especiales = ['DIEZ', 'ONCE', 'DOCE', 'TRECE', 'CATORCE', 'QUINCE', 'DIECISÉIS', 'DIECISIETE', 'DIECIOCHO', 'DIECINUEVE'];
    var decenas = ['', '', 'VEINTE', 'TREINTA', 'CUARENTA', 'CINCUENTA', 'SESENTA', 'SETENTA', 'OCHENTA', 'NOVENTA'];
    var centenas = ['', 'CIEN', 'DOSCIENTOS', 'TRESCIENTOS', 'CUATROCIENTOS', 'QUINIENTOS', 'SEISCIENTOS', 'SETECIENTOS', 'OCHOCIENTOS', 'NOVECIENTOS'];

    var entero = Math.floor(monto);
    var decimal = Math.round((monto - entero) * 100);

    function convertirNumero(num, esAntesDeMil) {
        if (num === 0) return '';
        if (num === 1) {
            return esAntesDeMil ? 'UN' : 'UNO';
        }
        if (num < 10) return unidades[num];
        if (num < 20) return especiales[num - 10];
        if (num < 30) {
            if (num === 20) return 'VEINTE';
            var u = num - 20;
            if (u === 1) {
                return esAntesDeMil ? 'VEINTIUN' : 'VEINTIUNO';
            }
            return 'VEINTI' + unidades[u];
        }
        if (num < 100) {
            var d = Math.floor(num / 10);
            var u = num % 10;
            if (u === 0) return decenas[d];
            var resultado = decenas[d] + ' Y ';
            if (u === 1) {
                resultado += esAntesDeMil ? 'UN' : 'UNO';
            } else {
                resultado += unidades[u];
            }
            return resultado;
        }
        if (num < 1000) {
            var c = Math.floor(num / 100);
            var r = num % 100;
            if (r === 0) {
                if (c === 1) return 'CIEN';
                return centenas[c];
            }
            if (c === 1) return 'CIENTO ' + convertirNumero(r, esAntesDeMil);
            return centenas[c] + ' ' + convertirNumero(r, esAntesDeMil);
        }
        if (num < 1000000) {
            var m = Math.floor(num / 1000);
            var r = num % 1000;
            var resultado = '';
            if (m === 1) {
                resultado = 'MIL';
            } else {
                resultado = convertirNumero(m, true) + ' MIL';
            }
            if (r > 0) {
                resultado += ' ' + convertirNumero(r, false);
            }
            return resultado;
        }
        if (num < 1000000000) {
            var mm = Math.floor(num / 1000000);
            var r = num % 1000000;
            var resultado = '';
            if (mm === 1) {
                resultado = 'UN MILLON';
            } else {
                resultado = convertirNumero(mm, true) + ' MILLONES';
            }
            if (r > 0) {
                resultado += ' ' + convertirNumero(r, false);
            }
            return resultado;
        }
        return 'CANTIDAD DEMASIADO GRANDE';
    }

    var letras = '';
    if (entero === 0) {
        letras = 'CERO';
    } else {
        letras = convertirNumero(entero, false);
    }

    letras = letras.replace(/\bUNO MIL\b/g, 'UN MIL');
    letras = letras.replace(/\bUNO MILLONES\b/g, 'UN MILLONES');
    letras = letras.replace(/\bVEINTIUNO MIL\b/g, 'VEINTIUN MIL');
    letras = letras.replace(/\bVEINTIUNO MILLONES\b/g, 'VEINTIUN MILLONES');
    letras = letras.replace(/\bCIEN UN\b/g, 'CIENTO UN');
    letras = letras.replace(/\bCIEN UNO\b/g, 'CIENTO UNO');
    letras = letras.replace(/\bCIEN UN MIL\b/g, 'CIENTO UN MIL');
    letras = letras.replace(/\bCIEN UNO MIL\b/g, 'CIENTO UN MIL');

    var centavosStr = decimal < 10 ? '0' + decimal : '' + decimal;
    letras += ' BALBOAS CON ' + centavosStr + '/100';

    return letras;
}

// ============================================================
// 2. ACTUALIZAR MONTO EN LETRAS
// ============================================================

function actualizarMontoLetras() {
    var montoInput = document.getElementById('monto');
    var letrasInput = document.getElementById('monto_letras');

    if (montoInput && letrasInput) {
        var valor = montoInput.value.replace(/,/g, '');
        var monto = parseFloat(valor);
        if (!isNaN(monto) && monto > 0) {
            letrasInput.value = convertirMontoALetras(monto);
        } else {
            letrasInput.value = '';
        }
    }
}

function actualizarMontoLetrasModal() {
    var montoInput = document.getElementById('formMonto');
    var letrasInput = document.getElementById('formMontoLetras');

    if (montoInput && letrasInput) {
        var valor = montoInput.value.replace(/,/g, '');
        var monto = parseFloat(valor);
        if (!isNaN(monto) && monto > 0) {
            letrasInput.value = convertirMontoALetras(monto);
        } else {
            letrasInput.value = '';
        }
    }
}

// ============================================================
// 3. FORMATO DE MONEDA CON COMAS
// ============================================================

function formatearMontoConComas(input) {
    var valor = input.value.replace(/[^0-9.]/g, '');

    if (valor === '') {
        return;
    }

    var partes = valor.split('.');
    var entero = partes[0];
    var decimal = partes[1] || '';

    if (decimal.length > 2) {
        decimal = decimal.substring(0, 2);
    }

    if (decimal === '') {
        decimal = '00';
    } else if (decimal.length === 1) {
        decimal = decimal + '0';
    }

    var montoMaximo = 100000000, numero = parseFloat(entero + '.' + decimal);

    if (numero > montoMaximo) {
        entero = '100000000';
        decimal = '00';
    }

    var enteroFormateado = '', contador = 0, i;
    for (i = entero.length - 1; i >= 0; i--) {
        enteroFormateado = entero[i] + enteroFormateado;
        contador++;
        if (contador % 3 === 0 && i > 0) {
            enteroFormateado = ',' + enteroFormateado;
        }
    }

    if (enteroFormateado === '') {
        enteroFormateado = '0';
    }

    input.value = enteroFormateado + '.' + decimal;
}

function formatearMontoEnVivo(input) {
    var cursorPos = input.selectionStart;
    var valorAnterior = input.value;
    var valor = input.value.replace(/,/g, '').replace(/[^0-9.]/g, '');

    var primerPunto = valor.indexOf('.');
    if (primerPunto !== -1) {
        valor = valor.substring(0, primerPunto + 1) + valor.substring(primerPunto + 1).replace(/\./g, '');
    }

    if (valor === '' || valor === '.') {
        input.value = valor;
        return;
    }

    var partes = valor.split('.');
    var entero = partes[0].replace(/^0+(?=\d)/, '');
    var decimal = partes.length > 1 ? partes[1] : null;

    if (decimal !== null && decimal.length > 2) {
        decimal = decimal.substring(0, 2);
    }

    var montoMaximo = 100000000;
    if (entero !== '' && parseInt(entero, 10) > montoMaximo) {
        entero = String(montoMaximo);
        decimal = decimal !== null ? decimal : null;
    }

    var enteroFormateado = '', contador = 0, i;
    for (i = entero.length - 1; i >= 0; i--) {
        enteroFormateado = entero[i] + enteroFormateado;
        contador++;
        if (contador % 3 === 0 && i > 0) {
            enteroFormateado = ',' + enteroFormateado;
        }
    }

    var valorFinal = enteroFormateado;
    if (decimal !== null) {
        valorFinal += '.' + decimal;
    }

    if (valorFinal !== valorAnterior) {
        input.value = valorFinal;
        var diff = valorFinal.length - valorAnterior.length;
        var nuevaPos = cursorPos + diff;
        if (nuevaPos < 0) nuevaPos = 0;
        if (nuevaPos > valorFinal.length) nuevaPos = valorFinal.length;
        input.setSelectionRange(nuevaPos, nuevaPos);
    }
}

function completarCentavos(input) {
    var valor = input.value.trim();

    if (valor === '' || valor === '.') {
        input.value = '';
        return;
    }

    var partes = valor.split('.');
    var decimal = partes[1] || '';

    if (decimal.length === 0) {
        decimal = '00';
    } else if (decimal.length === 1) {
        decimal = decimal + '0';
    }

    input.value = partes[0] + '.' + decimal;
}

// ============================================================
// 4. VALIDACIÓN Y CONFIRMACIONES
// ============================================================

function validarFormulario(formId) {
    var formulario = document.getElementById(formId);
    if (!formulario) return true;

    var camposRequeridos = formulario.querySelectorAll('[required]');
    var valido = true;

    camposRequeridos.forEach(function(campo) {
        if (!campo.value.trim()) {
            campo.classList.add('is-invalid');
            valido = false;
        } else {
            campo.classList.remove('is-invalid');
        }
    });

    if (!valido) {
        alert('Por favor, complete todos los campos obligatorios.');
        return false;
    }

    return true;
}

function confirmarAccion(mensaje) {
    if (!mensaje) {
        mensaje = '¿Está seguro de realizar esta acción?';
    }
    return confirm(mensaje);
}

function confirmarAnulacion(cheque) {
    var mensaje = '¿Está seguro de que desea anular el cheque N.º ' + cheque + '?';
    mensaje += '\nEsta acción no se puede deshacer.';
    return confirm(mensaje);
}

function confirmarEliminar(entidad, nombre) {
    var mensaje = '¿Está seguro de que desea eliminar ' + entidad + ' "' + nombre + '"?';
    return confirm(mensaje);
}

// ============================================================
// 5. MOSTRAR/OCULTAR SECCIONES
// ============================================================

function toggleSeccion(id) {
    var elemento = document.getElementById(id);
    if (elemento) {
        if (elemento.style.display === 'none' || elemento.style.display === '') {
            elemento.style.display = 'block';
        } else {
            elemento.style.display = 'none';
        }
    }
}

function mostrarSeccion(id) {
    var elemento = document.getElementById(id);
    if (elemento) {
        elemento.style.display = 'block';
    }
}

function ocultarSeccion(id) {
    var elemento = document.getElementById(id);
    if (elemento) {
        elemento.style.display = 'none';
    }
}

// ============================================================
// 6. FILTROS EN TABLAS
// ============================================================

function filtrarTabla(tablaId, inputId) {
    var tabla = document.getElementById(tablaId);
    var input = document.getElementById(inputId);

    if (!tabla || !input) return;

    var filtro = input.value.toLowerCase();
    var filas = tabla.getElementsByTagName('tr');

    for (var i = 1; i < filas.length; i++) {
        var fila = filas[i];
        var texto = fila.textContent.toLowerCase();
        if (texto.indexOf(filtro) > -1) {
            fila.style.display = '';
        } else {
            fila.style.display = 'none';
        }
    }
}

// ============================================================
// 7. LIMPIAR FORMULARIO
// ============================================================

function limpiarFormulario(formId) {
    var formulario = document.getElementById(formId);
    if (!formulario) return;

    var inputs = formulario.querySelectorAll('input, textarea, select');
    inputs.forEach(function(input) {
        if (input.type !== 'submit' && input.type !== 'button') {
            if (input.type === 'checkbox' || input.type === 'radio') {
                input.checked = false;
            } else if (input.tagName === 'SELECT') {
                input.selectedIndex = 0;
            } else {
                input.value = '';
            }
            input.classList.remove('is-invalid');
            input.classList.remove('is-valid');
        }
    });
}

// ============================================================
// 8. VALIDACIONES ESPECIALES
// ============================================================

function validarNumeroCheque(input) {
    var valor = input.value.replace(/\D/g, '');
    input.value = valor;
    return valor;
}

function validarSinE(input) {
    input.value = input.value.replace(/[eE]/g, '');
}

function bloquearE(event) {
    if (event.key === 'e' || event.key === 'E') {
        event.preventDefault();
        return false;
    }
}

// ============================================================
// 9. CALCULAR DIFERENCIA EN CONCILIACIÓN
// ============================================================

function calcularDiferencia() {
    var saldoLibros = parseFloat(document.getElementById('saldo_libros').value.replace(/,/g, '')) || 0;
    var depositosTransito = parseFloat(document.getElementById('depositos_transito').value.replace(/,/g, '')) || 0;
    var chequesPendientes = parseFloat(document.getElementById('cheques_pendientes').value.replace(/,/g, '')) || 0;
    var saldoBanco = parseFloat(document.getElementById('saldo_banco').value.replace(/,/g, '')) || 0;

    var saldoConciliado = saldoLibros + depositosTransito - chequesPendientes;
    var diferencia = saldoBanco - saldoConciliado;

    document.getElementById('saldo_conciliado').value = saldoConciliado.toFixed(2);
    document.getElementById('diferencia').value = diferencia.toFixed(2);

    var diferenciaElement = document.getElementById('diferencia');
    if (diferencia === 0) {
        diferenciaElement.style.color = '#059669';
        document.getElementById('mensaje_diferencia').innerHTML = '✅ Conciliación exitosa - La diferencia es cero.';
        document.getElementById('mensaje_diferencia').className = 'alert alert-success';
        document.getElementById('btn_guardar').disabled = false;
    } else {
        diferenciaElement.style.color = '#DC2626';
        document.getElementById('mensaje_diferencia').innerHTML = '❌ Conciliación no válida - Diferencia: B/. ' + Math.abs(diferencia).toFixed(2);
        document.getElementById('mensaje_diferencia').className = 'alert alert-danger';
        document.getElementById('btn_guardar').disabled = true;
    }
}

// ============================================================
// 10. ABRIR MODAL DE CHEQUE
// ============================================================

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
    document.getElementById('formEstado').value = '1';
    document.getElementById('modalTitle').textContent = 'Registrar cheque';

    // ⭐ Verificar si existe el elemento pvNumero antes de tocarlo (por si está oculto)
    var pvNumero = document.getElementById('pvNumero');
    if (pvNumero) pvNumero.textContent = '—';

    fetch('ChequeServlet?accion=obtenerNumero')
        .then(response => response.json())
        .then(data => {
            document.getElementById('formNumero').textContent = data.numero;
            document.getElementById('formNumeroHidden').value = data.numero;
            // ⭐ Refrescar la previsualización si existe
            if (typeof actualizarPreviewCheque === 'function') {
                actualizarPreviewCheque();
            }
        })
        .catch(error => console.error('Error:', error));

    new bootstrap.Modal(document.getElementById('modalCheque')).show();
}

// ============================================================
// 11. INICIALIZAR EVENTOS
// ============================================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('Sistema de Conciliación Bancaria - UTP');
    console.log('JavaScript cargado correctamente.');

    var montos = document.querySelectorAll('.monto-input');
    montos.forEach(function(input) {
        input.addEventListener('blur', function() {
            formatearMontoConComas(this);
        });
        input.addEventListener('input', function() {
            if (this.id === 'monto') {
                actualizarMontoLetras();
            } else if (this.id === 'formMonto') {
                actualizarMontoLetrasModal();
            }
        });
    });

    var requeridos = document.querySelectorAll('[required]');
    requeridos.forEach(function(campo) {
        campo.addEventListener('blur', function() {
            if (!this.value.trim()) {
                this.classList.add('is-invalid');
            } else {
                this.classList.remove('is-invalid');
            }
        });
    });

    var selectProveedor = document.getElementById('formProveedor');
    if (selectProveedor) {
        selectProveedor.addEventListener('change', function() {
            if (this.value !== '') {
                this.classList.remove('is-invalid');
                this.blur();
            } else {
                this.classList.add('is-invalid');
            }
        });
    }

    var selectObjeto = document.getElementById('formObjeto');
    if (selectObjeto) {
        selectObjeto.addEventListener('change', function() {
            if (this.value !== '') {
                this.classList.remove('is-invalid');
                this.blur();
            } else {
                this.classList.add('is-invalid');
            }
        });
    }
});

// ============================================================
// 12. MODAL DE EDICIÓN GENÉRICO
// ============================================================

function abrirModalEditar(config) {
    var modalAnterior = document.getElementById('modalEditarDinamico');
    if (modalAnterior) {
        modalAnterior.remove();
    }

    var camposHtml = '';

    config.campos.forEach(function(campo) {
        if (campo.type === 'hidden') {
            camposHtml += '<input type="hidden" name="' + campo.name + '" id="edit_' + campo.name + '">';
            if (campo.row === 'end') {
                camposHtml += '</div>';
            }
            return;
        }

        if (campo.type === 'message') {
            if (campo.row === 'start') {
                camposHtml += '<div class="row">';
            }
            if (campo.col) {
                camposHtml += '<div class="col-md-' + campo.col + '">';
            }
            camposHtml += '<div style="' + (campo.style || '') + '">';
            camposHtml += campo.mensaje;
            camposHtml += '</div>';
            if (campo.col) {
                camposHtml += '</div>';
            }
            if (campo.row === 'end') {
                camposHtml += '</div>';
            }
            return;
        }

        if (campo.row === 'start') {
            camposHtml += '<div class="row">';
        }

        if (campo.col) {
            camposHtml += '<div class="col-md-' + campo.col + '">';
        }

        camposHtml += '<div class="form-group">';
        camposHtml += '<label>' + campo.label;
        if (campo.required) camposHtml += ' <span class="required">*</span>';
        camposHtml += '</label>';

        if (campo.type === 'select') {
            camposHtml += '<select name="' + campo.name + '" id="edit_' + campo.name + '" class="form-control"';
            if (campo.required) camposHtml += ' required';
            camposHtml += '>';
            campo.options.forEach(function(opt) {
                camposHtml += '<option value="' + opt.value + '">' + opt.label + '</option>';
            });
            camposHtml += '</select>';
        } else if (campo.type === 'textarea') {
            camposHtml += '<textarea name="' + campo.name + '" id="edit_' + campo.name + '" class="form-control" rows="2"';
            if (campo.placeholder) camposHtml += ' placeholder="' + campo.placeholder + '"';
            if (campo.required) camposHtml += ' required';
            camposHtml += '></textarea>';
        } else {
            camposHtml += '<input type="' + campo.type + '" name="' + campo.name + '" id="edit_' + campo.name + '" class="form-control"';
            if (campo.placeholder) camposHtml += ' placeholder="' + campo.placeholder + '"';
            if (campo.required) camposHtml += ' required';
            if (campo.oninput) camposHtml += ' oninput="' + campo.oninput + '"';
            if (campo.onblur) camposHtml += ' onblur="' + campo.onblur + '"';
            camposHtml += '>';
        }

        if (campo.ayuda) {
            camposHtml += '<small style="display:block; margin-top:5px; color:#5A6A7E; font-size:11px;">';
            camposHtml += campo.ayuda;
            camposHtml += '</small>';
        }

        camposHtml += '</div>';

        if (campo.col) {
            camposHtml += '</div>';
        }

        if (campo.row === 'end') {
            camposHtml += '</div>';
        }
    });

    var modalHtml =
        '<div class="modal fade" id="modalEditarDinamico" tabindex="-1">' +
            '<div class="modal-dialog">' +
                '<div class="modal-content">' +
                    '<form action="' + config.action + '" method="post" autocomplete="off">' +
                        '<input type="hidden" name="accion" value="actualizar">' +
                        '<input type="hidden" name="id" id="edit_id">' +
                        '<div class="modal-header">' +
                            '<h5 class="modal-title">' + config.titulo + '</h5>' +
                            '<button type="button" class="btn-close" data-bs-dismiss="modal"></button>' +
                        '</div>' +
                        '<div class="modal-body">' +
                            camposHtml +
                        '</div>' +
                        '<div class="modal-footer">' +
                            '<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>' +
                            '<button type="submit" class="btn btn-primary">Guardar cambios</button>' +
                        '</div>' +
                    '</form>' +
                '</div>' +
            '</div>' +
        '</div>';

    document.body.insertAdjacentHTML('beforeend', modalHtml);

    fetch(config.url)
        .then(response => response.json())
        .then(data => {
            document.getElementById('edit_id').value = data.id;

            config.campos.forEach(function(campo) {
                var input = document.getElementById('edit_' + campo.name);
                if (input && data[campo.name] !== undefined) {
                    input.value = data[campo.name];
                }
            });

            var modal = new bootstrap.Modal(document.getElementById('modalEditarDinamico'));
            modal.show();

            document.getElementById('modalEditarDinamico').addEventListener('hidden.bs.modal', function() {
                this.remove();
            });
        })
        .catch(error => console.error('Error:', error));
}

// ============================================================
// 13. CAMBIAR ESTADO GENÉRICO
// ============================================================

function cambiarEstado(config) {
	// ⭐ Detecta el estado inactivo según el módulo. Si no se especifica, asume 0.
	var estadoInactivo = (typeof config.estadoInactivo !== 'undefined') ? config.estadoInactivo : 0;
	var nuevoEstado = (config.estadoActual === estadoInactivo) ? 1 : 0;
	var nuevoEstadoTexto   = (nuevoEstado === 0) ? 'INACTIVO' : 'ACTIVO';
	var estadoActualTexto  = (config.estadoActual === estadoInactivo) ? 'INACTIVO' : 'ACTIVO';
    var etiquetaNombre = config.etiquetaNombre || 'Registro';

    var modalAnterior = document.getElementById('modalCambiarEstado');
    if (modalAnterior) {
        modalAnterior.remove();
    }

    var urlFinal = config.url + '&id=' + config.id + '&estado=' + nuevoEstado;

    var modalHtml =
        '<div class="modal fade" id="modalCambiarEstado" tabindex="-1">' +
            '<div class="modal-dialog">' +
                '<div class="modal-content">' +
                    '<div class="modal-header">' +
                        '<h5 class="modal-title">' +
                            '<i class="fas fa-exchange-alt" style="color:#1B3F7A; margin-right:8px;"></i>' +
                            'Cambiar estado' +
                        '</h5>' +
                        '<button type="button" class="btn-close" data-bs-dismiss="modal"></button>' +
                    '</div>' +
                    '<div class="modal-body">' +
                        '<p>¿Está seguro de que desea cambiar el estado del <strong>' + config.entidad + '</strong>?</p>' +
                        '<div class="card" style="background:#F8FAFC; border:1px solid #D1DBE8; padding:15px; margin-bottom:15px;">' +
                            '<div style="margin-bottom:10px;">' +
                                '<strong style="color:#5A6A7E; font-size:11px; text-transform:uppercase;">' + etiquetaNombre + ':</strong>' +
                                '<div style="color:#0D1B2A; font-weight:600;">' + config.nombre + '</div>' +
                            '</div>' +
                            '<div style="display:flex; gap:20px;">' +
                                '<div>' +
                                    '<strong style="color:#5A6A7E; font-size:11px; text-transform:uppercase;">Estado actual:</strong>' +
                                    '<div><span class="badge ' + (config.estadoActual === estadoInactivo ? 'badge-inactivo' : 'badge-activo') + '">' + estadoActualTexto + '</span></div>' +
                                '</div>' +
                                '<div>' +
                                    '<strong style="color:#5A6A7E; font-size:11px; text-transform:uppercase;">Nuevo estado:</strong>' +
                                    '<div><span class="badge ' + (nuevoEstado === 1 ? 'badge-activo' : 'badge-inactivo') + '">' + nuevoEstadoTexto + '</span></div>' +
                                '</div>' +
                            '</div>' +
                        '</div>' +
                    '</div>' +
                    '<div class="modal-footer">' +
                        '<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>' +
                        '<button type="button" class="btn btn-primary" onclick="window.location.href=\'' + urlFinal + '\'">' +
                            '<i class="fas fa-check"></i> Confirmar' +
                        '</button>' +
                    '</div>' +
                '</div>' +
            '</div>' +
        '</div>';

    document.body.insertAdjacentHTML('beforeend', modalHtml);

    var modal = new bootstrap.Modal(document.getElementById('modalCambiarEstado'));
    modal.show();

    document.getElementById('modalCambiarEstado').addEventListener('hidden.bs.modal', function() {
        this.remove();
    });
}

window.cambiarEstado = cambiarEstado;

//Limitador de carácteres

function SPchar(id, num){	
	
	const inputField = document.getElementById(id);
	
	//Limitante para 
	if (num == 0){
		
	}
	
	
	inputField.addEventListener('input', function(event){
		let ognTxt = event.target.value;
		
		let clnTxt = ognTxt.replace(/[^a-zA-Z0-9\s]/g, '');
		
		event.target.value = clnTxt;
	})
}