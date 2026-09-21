package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.Conexion;
import model.Deposito;

public class DepositoDAO
{
    public List<Deposito> listarTodos()
    {
        List<Deposito> lista = new ArrayList<>();
        String sql = "SELECT * FROM depositos ORDER BY fecha DESC";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                lista.add(mapearDeposito(rs));
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

    public List<Deposito> listarActivos()
    {
        List<Deposito> lista = new ArrayList<>();
        String sql = "SELECT * FROM depositos WHERE estado = 1 ORDER BY fecha DESC";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                lista.add(mapearDeposito(rs));
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

    public Deposito obtenerPorId(int idDeposito)
    {
        Deposito d = null;
        String sql = "SELECT * FROM depositos WHERE id_deposito = ?";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idDeposito);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                d = mapearDeposito(rs);
            }

            rs.close();
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return d;
    }

    public boolean crear(Deposito deposito)
    {
        boolean creado = false;
        int siguienteId = obtenerSiguienteId();
        deposito.setIdDeposito(siguienteId);

        String sql = "INSERT INTO depositos (id_deposito, numero_comprobante, tipo_deposito, fecha, monto, "
                   + "detalle, estado, id_usuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, deposito.getIdDeposito());
            ps.setString(2, deposito.getNumeroComprobante());
            ps.setInt(3, deposito.getTipoDeposito());
            ps.setDate(4, deposito.getFecha());
            ps.setBigDecimal(5, deposito.getMonto());
            ps.setString(6, deposito.getDetalle());
            ps.setInt(7, deposito.getEstado());
            ps.setInt(8, deposito.getIdUsuario());

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

    public boolean actualizar(Deposito deposito)
    {
        boolean actualizado = false;
        String sql = "UPDATE depositos SET numero_comprobante = ?, tipo_deposito = ?, fecha = ?, "
                   + "monto = ?, detalle = ?, estado = ? WHERE id_deposito = ?";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, deposito.getNumeroComprobante());
            ps.setInt(2, deposito.getTipoDeposito());
            ps.setDate(3, deposito.getFecha());
            ps.setBigDecimal(4, deposito.getMonto());
            ps.setString(5, deposito.getDetalle());
            ps.setInt(6, deposito.getEstado());
            ps.setInt(7, deposito.getIdDeposito());

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

    public boolean eliminar(int idDeposito)
    {
        boolean eliminado = false;
        String sql = "DELETE FROM depositos WHERE id_deposito = ?";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idDeposito);

            int filas = ps.executeUpdate();
            if (filas > 0) eliminado = true;

            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return eliminado;
    }

    public List<Deposito> listarPorTipo(int tipoDeposito)
    {
        List<Deposito> lista = new ArrayList<>();
        String sql = "SELECT * FROM depositos WHERE tipo_deposito = ? ORDER BY fecha DESC";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, tipoDeposito);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                lista.add(mapearDeposito(rs));
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

    public List<Deposito> buscar(String texto)
    {
        List<Deposito> lista = new ArrayList<>();
        String sql = "SELECT * FROM depositos WHERE numero_comprobante LIKE ? OR detalle LIKE ? ORDER BY fecha DESC";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                lista.add(mapearDeposito(rs));
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

    public int obtenerSiguienteId()
    {
        int siguienteId = 1;
        String sql = "SELECT MAX(id_deposito) AS max_id FROM depositos";

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

    public boolean cambiarEstado(int idDeposito, int nuevoEstado)
    {
        boolean actualizado = false;
        String sql = "UPDATE depositos SET estado = ? WHERE id_deposito = ?";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, nuevoEstado);
            ps.setInt(2, idDeposito);

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

    // ⭐ Depósitos activos hasta el fin del período que no estén ya conciliados
    public List<Deposito> listarNoConciliadosHastaPeriodo(String periodo)
    {
        List<Deposito> lista = new ArrayList<>();

        String sql = "SELECT * FROM depositos "
                   + "WHERE estado = 1 "
                   + "  AND fecha <= LAST_DAY(STR_TO_DATE(CONCAT(?, '-01'), '%Y-%m-%d')) "
                   + "  AND id_deposito NOT IN (SELECT id_deposito FROM conciliacion_detalle_deposito) "
                   + "ORDER BY fecha ASC";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, periodo);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                lista.add(mapearDeposito(rs));
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

    // ⭐ Depósitos activos del período (para el reporte informativo)
    public List<Deposito> listarActivosHastaPeriodo(String periodo)
    {
        List<Deposito> lista = new ArrayList<>();

        String sql = "SELECT * FROM depositos "
                   + "WHERE estado = 1 "
                   + "  AND fecha <= LAST_DAY(STR_TO_DATE(CONCAT(?, '-01'), '%Y-%m-%d')) "
                   + "ORDER BY fecha ASC";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, periodo);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                lista.add(mapearDeposito(rs));
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

    private Deposito mapearDeposito(ResultSet rs) throws SQLException
    {
        Deposito d = new Deposito();
        d.setIdDeposito(rs.getInt("id_deposito"));
        d.setNumeroComprobante(rs.getString("numero_comprobante"));
        d.setTipoDeposito(rs.getInt("tipo_deposito"));
        d.setFecha(rs.getDate("fecha"));
        d.setMonto(rs.getBigDecimal("monto"));
        d.setDetalle(rs.getString("detalle"));
        d.setEstado(rs.getInt("estado"));
        d.setIdUsuario(rs.getInt("id_usuario"));
        d.setFechaRegistro(rs.getDate("fecha_registro"));
        return d;
    }
}