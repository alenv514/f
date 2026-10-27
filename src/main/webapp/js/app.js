(function () {
    "use strict";

    var URL_API = "api/productos";

    var campoId = document.getElementById("id");
    var campoCodigo = document.getElementById("codigo");
    var campoNombre = document.getElementById("nombre");
    var campoCantidad = document.getElementById("cantidad");
    var campoEstante = document.getElementById("estante");
    var campoBusqueda = document.getElementById("terminoBusqueda");
    var cuerpoTabla = document.getElementById("cuerpoTabla");
    var contenedorMensaje = document.getElementById("mensaje");
    var tituloFormulario = document.getElementById("tituloFormulario");
    var botonGuardar = document.getElementById("btnGuardar");
    var botonEditar = document.getElementById("btnEditar");
    var botonCancelar = document.getElementById("btnCancelar");
    var botonBuscar = document.getElementById("btnBuscar");
    var botonVerTodos = document.getElementById("btnVerTodos");
    var temporizadorMensaje = null;

    function mostrarMensaje(texto, exito) {
        clearTimeout(temporizadorMensaje);
        contenedorMensaje.textContent = texto;
        contenedorMensaje.className = "mensaje " + (exito ? "exito" : "error");
        temporizadorMensaje = setTimeout(function () {
            contenedorMensaje.textContent = "";
            contenedorMensaje.className = "mensaje";
        }, 4000);
    }

    function datosFormulario(accion) {
        var datos = new URLSearchParams();
        datos.append("accion", accion);
        datos.append("id", campoId.value);
        datos.append("codigo", campoCodigo.value.trim());
        datos.append("nombre", campoNombre.value.trim());
        datos.append("cantidad", campoCantidad.value.trim());
        datos.append("estante", campoEstante.value.trim());
        return datos;
    }

    function formularioValido() {
        if (!campoCodigo.value.trim() || !campoNombre.value.trim() || !campoEstante.value.trim()) {
            mostrarMensaje("Código, nombre y estante son obligatorios", false);
            return false;
        }
        var cantidad = campoCantidad.value.trim();
        if (cantidad === "" || !/^\d+$/.test(cantidad)) {
            mostrarMensaje("La cantidad debe ser un número entero mayor o igual a cero", false);
            return false;
        }
        return true;
    }

    function enviar(datos) {
        return fetch(URL_API, {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
            body: datos.toString()
        }).then(function (respuesta) {
            return respuesta.json();
        });
    }

    function limpiarFormulario() {
        campoId.value = "";
        campoCodigo.value = "";
        campoNombre.value = "";
        campoCantidad.value = "";
        campoEstante.value = "";
        tituloFormulario.textContent = "Registrar producto";
        botonGuardar.disabled = false;
        botonEditar.disabled = true;
    }

    function cargarEnFormulario(producto) {
        campoId.value = producto.id;
        campoCodigo.value = producto.codigo;
        campoNombre.value = producto.nombre;
        campoCantidad.value = producto.cantidad;
        campoEstante.value = producto.estante;
        tituloFormulario.textContent = "Editar producto";
        botonGuardar.disabled = true;
        botonEditar.disabled = false;
        window.scrollTo({ top: 0, behavior: "smooth" });
        campoNombre.focus();
    }

    function crearCelda(texto) {
        var celda = document.createElement("td");
        celda.textContent = texto;
        return celda;
    }

    function crearBoton(texto, clase, accion) {
        var boton = document.createElement("button");
        boton.type = "button";
        boton.className = "boton pequeno " + clase;
        boton.textContent = texto;
        boton.addEventListener("click", accion);
        return boton;
    }

    function pintarTabla(productos, mensajeVacio) {
        cuerpoTabla.innerHTML = "";
        if (!productos.length) {
            var filaVacia = document.createElement("tr");
            var celdaVacia = document.createElement("td");
            celdaVacia.colSpan = 5;
            celdaVacia.className = "sin-datos";
            celdaVacia.textContent = mensajeVacio;
            filaVacia.appendChild(celdaVacia);
            cuerpoTabla.appendChild(filaVacia);
            return;
        }
        productos.forEach(function (producto) {
            var fila = document.createElement("tr");
            fila.appendChild(crearCelda(producto.codigo));
            fila.appendChild(crearCelda(producto.nombre));
            fila.appendChild(crearCelda(producto.cantidad));
            fila.appendChild(crearCelda(producto.estante));

            var celdaAcciones = document.createElement("td");
            var contenedor = document.createElement("div");
            contenedor.className = "celda-acciones";
            contenedor.appendChild(crearBoton("Editar", "secundario", function () {
                cargarEnFormulario(producto);
            }));
            contenedor.appendChild(crearBoton("Eliminar", "peligro", function () {
                eliminarProducto(producto);
            }));
            celdaAcciones.appendChild(contenedor);
            fila.appendChild(celdaAcciones);
            cuerpoTabla.appendChild(fila);
        });
    }

    function consultar(termino) {
        var direccion = termino ? URL_API + "?buscar=" + encodeURIComponent(termino) : URL_API;
        return fetch(direccion)
            .then(function (respuesta) {
                if (!respuesta.ok) {
                    throw new Error("Error en la consulta");
                }
                return respuesta.json();
            })
            .then(function (productos) {
                var vacio = termino
                    ? "No se encontraron productos para \"" + termino + "\""
                    : "No hay productos registrados";
                pintarTabla(productos, vacio);
            })
            .catch(function () {
                pintarTabla([], "No se pudo cargar la información");
                mostrarMensaje("No se pudo conectar con el servidor", false);
            });
    }

    function guardarProducto() {
        if (!formularioValido()) {
            return;
        }
        enviar(datosFormulario("guardar"))
            .then(function (resultado) {
                mostrarMensaje(resultado.mensaje, resultado.exito);
                if (resultado.exito) {
                    limpiarFormulario();
                    campoBusqueda.value = "";
                    consultar("");
                }
            })
            .catch(function () {
                mostrarMensaje("No se pudo conectar con el servidor", false);
            });
    }

    function editarProducto() {
        if (!campoId.value || !formularioValido()) {
            return;
        }
        enviar(datosFormulario("editar"))
            .then(function (resultado) {
                mostrarMensaje(resultado.mensaje, resultado.exito);
                if (resultado.exito) {
                    limpiarFormulario();
                    consultar(campoBusqueda.value.trim());
                }
            })
            .catch(function () {
                mostrarMensaje("No se pudo conectar con el servidor", false);
            });
    }

    function eliminarProducto(producto) {
        if (!window.confirm("¿Desea eliminar el producto \"" + producto.nombre + "\"?")) {
            return;
        }
        var datos = new URLSearchParams();
        datos.append("accion", "eliminar");
        datos.append("id", producto.id);
        enviar(datos)
            .then(function (resultado) {
                mostrarMensaje(resultado.mensaje, resultado.exito);
                if (resultado.exito) {
                    if (campoId.value === String(producto.id)) {
                        limpiarFormulario();
                    }
                    consultar(campoBusqueda.value.trim());
                }
            })
            .catch(function () {
                mostrarMensaje("No se pudo conectar con el servidor", false);
            });
    }

    botonGuardar.addEventListener("click", guardarProducto);
    botonEditar.addEventListener("click", editarProducto);
    botonCancelar.addEventListener("click", limpiarFormulario);

    botonBuscar.addEventListener("click", function () {
        var termino = campoBusqueda.value.trim();
        if (!termino) {
            mostrarMensaje("Ingrese un código o nombre para buscar", false);
            return;
        }
        consultar(termino);
    });

    botonVerTodos.addEventListener("click", function () {
        campoBusqueda.value = "";
        consultar("");
    });

    campoBusqueda.addEventListener("keydown", function (evento) {
        if (evento.key === "Enter") {
            evento.preventDefault();
            botonBuscar.click();
        }
    });

    document.getElementById("formularioProducto").addEventListener("submit", function (evento) {
        evento.preventDefault();
    });

    consultar("");
})();
