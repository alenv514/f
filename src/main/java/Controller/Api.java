package Controller;

import Modelo.Producto;
import Servicios.ProductoServelet;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "Api", urlPatterns = {"/api/productos"})
public class Api extends HttpServlet {

    private static final Logger REGISTRO = Logger.getLogger(Api.class.getName());
    private static final int CODIGO_MYSQL_DUPLICADO = 1062;

    private final ProductoServelet servicio = new ProductoServelet();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws IOException {
        prepararRespuesta(solicitud, respuesta);
        String termino = solicitud.getParameter("buscar");
        try {
            List<Producto> productos;
            if (termino == null || termino.trim().isEmpty()) {
                productos = servicio.listar();
            } else {
                productos = servicio.buscar(termino.trim());
            }
            escribir(respuesta, HttpServletResponse.SC_OK, productos);
        } catch (SQLException e) {
            REGISTRO.log(Level.SEVERE, "Error al consultar los productos", e);
            escribir(respuesta, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    mensaje(false, "No se pudo consultar la base de datos"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws IOException {
        prepararRespuesta(solicitud, respuesta);
        String accion = solicitud.getParameter("accion");
        if (accion == null) {
            escribir(respuesta, HttpServletResponse.SC_BAD_REQUEST, mensaje(false, "Falta la acción a realizar"));
            return;
        }
        try {
            switch (accion) {
                case "guardar":
                    guardar(solicitud, respuesta);
                    break;
                case "editar":
                    editar(solicitud, respuesta);
                    break;
                case "eliminar":
                    eliminar(solicitud, respuesta);
                    break;
                default:
                    escribir(respuesta, HttpServletResponse.SC_BAD_REQUEST, mensaje(false, "Acción no válida"));
            }
        } catch (IllegalArgumentException e) {
            escribir(respuesta, HttpServletResponse.SC_BAD_REQUEST, mensaje(false, e.getMessage()));
        } catch (SQLException e) {
            if (e.getErrorCode() == CODIGO_MYSQL_DUPLICADO) {
                escribir(respuesta, HttpServletResponse.SC_CONFLICT,
                        mensaje(false, "Ya existe un producto con ese código"));
                return;
            }
            REGISTRO.log(Level.SEVERE, "Error al procesar la operación", e);
            escribir(respuesta, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    mensaje(false, "No se pudo completar la operación en la base de datos"));
        }
    }

    private void guardar(HttpServletRequest solicitud, HttpServletResponse respuesta)
            throws SQLException, IOException {
        Producto producto = construirProducto(solicitud, false);
        boolean correcto = servicio.insertar(producto);
        escribir(respuesta, HttpServletResponse.SC_OK,
                mensaje(correcto, correcto ? "Producto guardado correctamente" : "No se pudo guardar el producto"));
    }

    private void editar(HttpServletRequest solicitud, HttpServletResponse respuesta)
            throws SQLException, IOException {
        Producto producto = construirProducto(solicitud, true);
        boolean correcto = servicio.actualizar(producto);
        escribir(respuesta, HttpServletResponse.SC_OK,
                mensaje(correcto, correcto ? "Producto actualizado correctamente" : "No se encontró el producto a actualizar"));
    }

    private void eliminar(HttpServletRequest solicitud, HttpServletResponse respuesta)
            throws SQLException, IOException {
        int id = leerEntero(solicitud.getParameter("id"), "El identificador del producto no es válido");
        boolean correcto = servicio.eliminar(id);
        escribir(respuesta, HttpServletResponse.SC_OK,
                mensaje(correcto, correcto ? "Producto eliminado correctamente" : "No se encontró el producto a eliminar"));
    }

    private Producto construirProducto(HttpServletRequest solicitud, boolean requiereId) {
        String codigo = texto(solicitud.getParameter("codigo"));
        String nombre = texto(solicitud.getParameter("nombre"));
        String estante = texto(solicitud.getParameter("estante"));

        if (codigo.isEmpty() || nombre.isEmpty() || estante.isEmpty()) {
            throw new IllegalArgumentException("Código, nombre y estante son obligatorios");
        }
        if (codigo.length() > 30 || estante.length() > 30 || nombre.length() > 100) {
            throw new IllegalArgumentException("Alguno de los campos supera la longitud permitida");
        }

        int cantidad = leerEntero(solicitud.getParameter("cantidad"), "La cantidad debe ser un número entero");
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }

        Producto producto = new Producto(codigo, nombre, cantidad, estante);
        if (requiereId) {
            producto.setId(leerEntero(solicitud.getParameter("id"), "El identificador del producto no es válido"));
        }
        return producto;
    }

    private int leerEntero(String valor, String mensajeError) {
        try {
            return Integer.parseInt(valor == null ? "" : valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(mensajeError);
        }
    }

    private String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private Map<String, Object> mensaje(boolean exito, String texto) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("exito", exito);
        cuerpo.put("mensaje", texto);
        return cuerpo;
    }

    private void prepararRespuesta(HttpServletRequest solicitud, HttpServletResponse respuesta)
            throws IOException {
        solicitud.setCharacterEncoding("UTF-8");
        respuesta.setCharacterEncoding("UTF-8");
        respuesta.setContentType("application/json");
        respuesta.setHeader("Cache-Control", "no-store");
    }

    private void escribir(HttpServletResponse respuesta, int estado, Object cuerpo) throws IOException {
        respuesta.setStatus(estado);
        try (PrintWriter salida = respuesta.getWriter()) {
            salida.print(gson.toJson(cuerpo));
        }
    }
}
