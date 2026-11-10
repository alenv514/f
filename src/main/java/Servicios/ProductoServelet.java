package Servicios;

import DAO.ProductoDAO;
import Modelo.Producto;
import java.sql.SQLException;
import java.util.List;

public class ProductoServelet {

    private final ProductoDAO dao = new ProductoDAO();

    public List<Producto> listar() throws SQLException {
        return dao.listar();
    }

    public List<Producto> buscar(String termino) throws SQLException {
        return dao.buscar(termino);
    }

    public boolean insertar(Producto producto) throws SQLException {
        return dao.insertar(producto);
    }

    public boolean actualizar(Producto producto) throws SQLException {
        return dao.actualizar(producto);
    }

    public boolean eliminar(int id) throws SQLException {
        return dao.eliminar(id);
    }
}
