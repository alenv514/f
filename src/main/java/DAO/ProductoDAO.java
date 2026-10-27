package DAO;

import Conexion.conexion;
import Modelo.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private static final String SQL_LISTAR =
            "SELECT id, codigo, nombre, cantidad, estante FROM productos ORDER BY nombre ASC";

    private static final String SQL_BUSCAR =
            "SELECT id, codigo, nombre, cantidad, estante FROM productos "
            + "WHERE codigo LIKE ? OR nombre LIKE ? ORDER BY nombre ASC";

    private static final String SQL_INSERTAR =
            "INSERT INTO productos (codigo, nombre, cantidad, estante) VALUES (?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE productos SET codigo = ?, nombre = ?, cantidad = ?, estante = ? WHERE id = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM productos WHERE id = ?";

    public List<Producto> listar() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        try (Connection cn = conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(mapear(rs));
            }
        }
        return productos;
    }

    public List<Producto> buscar(String termino) throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String patron = "%" + termino + "%";
        try (Connection cn = conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_BUSCAR)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapear(rs));
                }
            }
        }
        return productos;
    }

    public boolean insertar(Producto producto) throws SQLException {
        try (Connection cn = conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERTAR)) {
            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setInt(3, producto.getCantidad());
            ps.setString(4, producto.getEstante());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Producto producto) throws SQLException {
        try (Connection cn = conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_ACTUALIZAR)) {
            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setInt(3, producto.getCantidad());
            ps.setString(4, producto.getEstante());
            ps.setInt(5, producto.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        try (Connection cn = conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_ELIMINAR)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getInt("cantidad"),
                rs.getString("estante"));
    }
}
