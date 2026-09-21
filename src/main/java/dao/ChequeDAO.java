package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.Conexion;
import model.Cheque;

public class ChequeDAO
{
    private static final String SELECT_BASE =
            "SELECT c.*, "
          + "       p.nombre AS nombre_proveedor, "
          + "       og.descripcion AS nombre_objeto_gasto "
          + "FROM cheques c "
          + "LEFT JOIN proveedores p ON c.id_proveedor = p.id_proveedor "
          + "LEFT JOIN objeto_gasto og ON c.id_objeto_gasto = og.id_objeto_gasto ";

    public List<Cheque> listarTodos()
    {
        List<Cheque> lista = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY c.fecha_cheque DESC";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                lista.add(mapearCheque(rs));
            }

            rs.close();
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Cheque> listarPorEstado(int estado)
    {
        List<Cheque> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE c.estado = ? ORDER BY c.fecha_cheque ASC";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, estado);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                lista.add(mapearCheque(rs));
            }

            rs.close();
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return lista;
    }

    public Cheque obtenerPorId(int idCheque)
    {
        Cheque c = null;
        String sql = SELECT_BASE + "WHERE c.id_cheque = ?";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCheque);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                c = mapearCheque(rs);
            }

            rs.close();
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return c;
    }

    public Cheque obtenerPorNumero(String numeroCheque)
    {
        Cheque c = null;
        String sql = SELECT_BASE + "WHERE c.numero_cheque = ?";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, numeroCheque);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                c = mapearCheque(rs);
            }

            rs.close();
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return c;
    }

    public List<Cheque> buscar(String texto, int estado)
    {
        List<Cheque> lista = new ArrayList<>();

        String sql = SELECT_BASE
                   + "WHERE (c.numero_cheque LIKE ? "
                   + "   OR p.nombre LIKE ? "
                   + "   OR og.codigo LIKE ? "
                   + "   OR og.descripcion LIKE ?) ";

        if (estado > 0) {
            sql += "AND c.estado = ? ";
        }
        sql += "ORDER BY c.fecha_cheque DESC";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            String filtro = "%" + texto + "%";
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            ps.setString(3, filtro);
            ps.setString(4, filtro);
            if (estado > 0) {
                ps.setInt(5, estado);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                lista.add(mapearCheque(rs));
            }

            rs.close();
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return lista;
    }

    // ⭐ Cheques emitidos hasta el fin del período que no estén ya conciliados
    public List<Cheque> listarNoConciliadosHastaPeriodo(String periodo)
    {
        List<Cheque> lista = new ArrayList<>();

        String sql = SELECT_BASE
                   + "WHERE c.estado = 1 "
                   + "  AND c.fecha_cheque <= LAST_DAY(STR_TO_DATE(CONCAT(?, '-01'), '%Y-%m-%d')) "
                   + "  AND c.id_cheque NOT IN (SELECT id_cheque FROM conciliacion_detalle_cheque) "
                   + "ORDER BY c.fecha_cheque ASC";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, periodo);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                lista.add(mapearCheque(rs));
            }

            rs.close();
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return lista;
    }

    // ⭐ Cheques anulados hasta el fin del período (para el reporte)
    public List<Cheque> listarAnuladosHastaPeriodo(String periodo)
    {
        List<Cheque> lista = new ArrayList<>();

        String sql = SELECT_BASE
                   + "WHERE c.estado = 4 "
                   + "  AND c.fecha_anulacion <= LAST_DAY(STR_TO_DATE(CONCAT(?, '-01'), '%Y-%m-%d')) "
                   + "  AND c.id_cheque NOT IN (SELECT id_cheque FROM conciliacion_detalle_cheque) "
                   + "ORDER BY c.fecha_anulacion ASC";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, periodo);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                lista.add(mapearCheque(rs));
            }

            rs.close();
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean crear(Cheque cheque)
    {
        boolean creado = false;
        int siguienteId = obtenerSiguienteId();
        cheque.setIdCheque(siguienteId);

        String sql = "INSERT INTO cheques (id_cheque, numero_cheque, fecha_cheque, id_proveedor, monto, "
                   + "monto_letras, detalle, id_objeto_gasto, estado, id_usuario_creacion) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, cheque.getIdCheque());
            ps.setString(2, cheque.getNumeroCheque());
            ps.setDate(3, cheque.getFechaCheque());
            ps.setInt(4, cheque.getIdProveedor());
            ps.setBigDecimal(5, cheque.getMonto());
            ps.setString(6, cheque.getMontoLetras());
            ps.setString(7, cheque.getDetalle());
            ps.setInt(8, cheque.getIdObjetoGasto());
            ps.setInt(9, cheque.getEstado());
            ps.setInt(10, cheque.getIdUsuarioCreacion());

            int filas = ps.executeUpdate();
            if (filas > 0) creado = true;

            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return creado;
    }

    public boolean actualizar(Cheque cheque)
    {
        boolean actualizado = false;

        String sql = "UPDATE cheques SET numero_cheque = ?, fecha_cheque = ?, id_proveedor = ?, "
                   + "monto = ?, monto_letras = ?, detalle = ?, id_objeto_gasto = ?, estado = ? "
                   + "WHERE id_cheque = ?";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, cheque.getNumeroCheque());
            ps.setDate(2, cheque.getFechaCheque());
            ps.setInt(3, cheque.getIdProveedor());
            ps.setBigDecimal(4, cheque.getMonto());
            ps.setString(5, cheque.getMontoLetras());
            ps.setString(6, cheque.getDetalle());
            ps.setInt(7, cheque.getIdObjetoGasto());
            ps.setInt(8, cheque.getEstado());
            ps.setInt(9, cheque.getIdCheque());

            int filas = ps.executeUpdate();
            if (filas > 0) actualizado = true;

            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return actualizado;
    }

    public boolean cambiarEstado(int idCheque, int nuevoEstado)
    {
        boolean actualizado = false;
        String sql = "UPDATE cheques SET estado = ? WHERE id_cheque = ?";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, nuevoEstado);
            ps.setInt(2, idCheque);

            int filas = ps.executeUpdate();
            if (filas > 0) actualizado = true;

            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return actualizado;
    }

    public boolean anularCheque(int idCheque, String motivo, int idUsuarioAnulacion)
    {
        boolean actualizado = false;

        String sql = "UPDATE cheques SET estado = 4, fecha_anulacion = NOW(), "
                   + "motivo_anulacion = ?, id_usuario_anulacion = ? WHERE id_cheque = ?";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, motivo);
            ps.setInt(2, idUsuarioAnulacion);
            ps.setInt(3, idCheque);

            int filas = ps.executeUpdate();
            if (filas > 0) actualizado = true;

            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return actualizado;
    }

    public boolean sacarDeCirculacion(int idCheque, String observacion)
    {
        boolean actualizado = false;

        String sql = "UPDATE cheques SET estado = 5, fecha_salida_circulacion = NOW(), "
                   + "observacion_salida = ? WHERE id_cheque = ?";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, observacion);
            ps.setInt(2, idCheque);

            int filas = ps.executeUpdate();
            if (filas > 0) actualizado = true;

            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return actualizado;
    }

    public String obtenerSiguienteNumeroCheque()
    {
        String siguiente = "1";
        String sql = "SELECT MAX(CAST(numero_cheque AS UNSIGNED)) AS max_numero FROM cheques";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                int maxNumero = rs.getInt("max_numero");
                siguiente = String.valueOf(maxNumero + 1);
            }

            rs.close();
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return siguiente;
    }

    public int obtenerSiguienteId()
    {
        int siguienteId = 1;
        String sql = "SELECT MAX(id_cheque) AS max_id FROM cheques";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                siguienteId = rs.getInt("max_id") + 1;
            }

            rs.close();
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return siguienteId;
    }

    private Cheque mapearCheque(ResultSet rs) throws SQLException
    {
        Cheque c = new Cheque();
        c.setIdCheque(rs.getInt("id_cheque"));
        c.setNumeroCheque(rs.getString("numero_cheque"));
        c.setFechaCheque(rs.getDate("fecha_cheque"));
        c.setIdProveedor(rs.getInt("id_proveedor"));
        c.setMonto(rs.getBigDecimal("monto"));
        c.setMontoLetras(rs.getString("monto_letras"));
        c.setDetalle(rs.getString("detalle"));
        c.setIdObjetoGasto(rs.getInt("id_objeto_gasto"));
        c.setEstado(rs.getInt("estado"));
        c.setFechaAnulacion(rs.getDate("fecha_anulacion"));
        c.setMotivoAnulacion(rs.getString("motivo_anulacion"));
        c.setFechaSalidaCirculacion(rs.getDate("fecha_salida_circulacion"));
        c.setObservacionSalida(rs.getString("observacion_salida"));
        c.setIdUsuarioCreacion(rs.getInt("id_usuario_creacion"));
        c.setIdUsuarioAnulacion(rs.getInt("id_usuario_anulacion"));
        c.setNombreProveedor(rs.getString("nombre_proveedor"));
        c.setNombreObjetoGasto(rs.getString("nombre_objeto_gasto"));
        return c;
    }
}