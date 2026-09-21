package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.Conexion;
import model.Conciliacion;

public class ConciliacionDAO
{
    public List<Conciliacion> listarTodos()
    {
        List<Conciliacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM conciliaciones ORDER BY periodo DESC";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                Conciliacion c = new Conciliacion();
                c.setIdConciliacion(rs.getInt("id_conciliacion"));
                c.setPeriodo(rs.getString("periodo"));
                c.setSaldoLibros(rs.getBigDecimal("saldo_libros"));
                c.setDepositosTransito(rs.getBigDecimal("depositos_transito"));
                c.setChequesPendientes(rs.getBigDecimal("cheques_pendientes"));
                c.setSaldoBanco(rs.getBigDecimal("saldo_banco"));
                c.setDiferencia(rs.getBigDecimal("diferencia"));
                c.setFechaConciliacion(rs.getDate("fecha_conciliacion"));
                c.setEstado(rs.getInt("estado"));
                c.setIdUsuario(rs.getInt("id_usuario"));
                c.setObservaciones(rs.getString("observaciones"));
                lista.add(c);
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

    public Conciliacion obtenerPorId(int idConciliacion)
    {
        Conciliacion c = null;
        String sql = "SELECT * FROM conciliaciones WHERE id_conciliacion = ?";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idConciliacion);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                c = new Conciliacion();
                c.setIdConciliacion(rs.getInt("id_conciliacion"));
                c.setPeriodo(rs.getString("periodo"));
                c.setSaldoLibros(rs.getBigDecimal("saldo_libros"));
                c.setDepositosTransito(rs.getBigDecimal("depositos_transito"));
                c.setChequesPendientes(rs.getBigDecimal("cheques_pendientes"));
                c.setSaldoBanco(rs.getBigDecimal("saldo_banco"));
                c.setDiferencia(rs.getBigDecimal("diferencia"));
                c.setFechaConciliacion(rs.getDate("fecha_conciliacion"));
                c.setEstado(rs.getInt("estado"));
                c.setIdUsuario(rs.getInt("id_usuario"));
                c.setObservaciones(rs.getString("observaciones"));
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

    public Conciliacion obtenerPorPeriodo(String periodo)
    {
        Conciliacion c = null;
        String sql = "SELECT * FROM conciliaciones WHERE periodo = ?";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, periodo);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                c = new Conciliacion();
                c.setIdConciliacion(rs.getInt("id_conciliacion"));
                c.setPeriodo(rs.getString("periodo"));
                c.setSaldoLibros(rs.getBigDecimal("saldo_libros"));
                c.setDepositosTransito(rs.getBigDecimal("depositos_transito"));
                c.setChequesPendientes(rs.getBigDecimal("cheques_pendientes"));
                c.setSaldoBanco(rs.getBigDecimal("saldo_banco"));
                c.setDiferencia(rs.getBigDecimal("diferencia"));
                c.setFechaConciliacion(rs.getDate("fecha_conciliacion"));
                c.setEstado(rs.getInt("estado"));
                c.setIdUsuario(rs.getInt("id_usuario"));
                c.setObservaciones(rs.getString("observaciones"));
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

    public boolean crear(Conciliacion conciliacion)
    {
        boolean creado = false;
        int siguienteId = obtenerSiguienteId();
        // ⭐ Asignar el ID ANTES de insertar, para que el servlet pueda leerlo después
        conciliacion.setIdConciliacion(siguienteId);

        String sql = "INSERT INTO conciliaciones (id_conciliacion, periodo, saldo_libros, depositos_transito, "
                   + "cheques_pendientes, saldo_banco, diferencia, estado, id_usuario, observaciones) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, conciliacion.getIdConciliacion());
            ps.setString(2, conciliacion.getPeriodo());
            ps.setBigDecimal(3, conciliacion.getSaldoLibros());
            ps.setBigDecimal(4, conciliacion.getDepositosTransito());
            ps.setBigDecimal(5, conciliacion.getChequesPendientes());
            ps.setBigDecimal(6, conciliacion.getSaldoBanco());
            ps.setBigDecimal(7, conciliacion.getDiferencia());
            ps.setInt(8, conciliacion.getEstado());
            ps.setInt(9, conciliacion.getIdUsuario());
            ps.setString(10, conciliacion.getObservaciones());

            int filas = ps.executeUpdate();
            if (filas > 0)
            {
                creado = true;
            }

            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return creado;
    }

    public boolean actualizar(Conciliacion conciliacion)
    {
        boolean actualizado = false;

        String sql = "UPDATE conciliaciones SET saldo_libros = ?, depositos_transito = ?, "
                   + "cheques_pendientes = ?, saldo_banco = ?, diferencia = ?, "
                   + "estado = ?, observaciones = ? WHERE id_conciliacion = ?";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBigDecimal(1, conciliacion.getSaldoLibros());
            ps.setBigDecimal(2, conciliacion.getDepositosTransito());
            ps.setBigDecimal(3, conciliacion.getChequesPendientes());
            ps.setBigDecimal(4, conciliacion.getSaldoBanco());
            ps.setBigDecimal(5, conciliacion.getDiferencia());
            ps.setInt(6, conciliacion.getEstado());
            ps.setString(7, conciliacion.getObservaciones());
            ps.setInt(8, conciliacion.getIdConciliacion());

            int filas = ps.executeUpdate();
            if (filas > 0)
            {
                actualizado = true;
            }

            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return actualizado;
    }

    public boolean eliminar(int idConciliacion)
    {
        boolean eliminado = false;
        String sql = "DELETE FROM conciliaciones WHERE id_conciliacion = ?";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idConciliacion);

            int filas = ps.executeUpdate();
            if (filas > 0)
            {
                eliminado = true;
            }

            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return eliminado;
    }

    public int obtenerSiguienteId()
    {
        int siguienteId = 1;
        String sql = "SELECT MAX(id_conciliacion) AS max_id FROM conciliaciones";

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
    
    // ⭐ Buscar conciliaciones por período (búsqueda parcial)
    public List<Conciliacion> buscarPorPeriodo(String texto)
    {
        List<Conciliacion> lista = new ArrayList<>();

        String sql = "SELECT * FROM conciliaciones "
                   + "WHERE periodo LIKE ? "
                   + "ORDER BY periodo DESC";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + texto + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                Conciliacion c = new Conciliacion();
                c.setIdConciliacion(rs.getInt("id_conciliacion"));
                c.setPeriodo(rs.getString("periodo"));
                c.setSaldoLibros(rs.getBigDecimal("saldo_libros"));
                c.setDepositosTransito(rs.getBigDecimal("depositos_transito"));
                c.setChequesPendientes(rs.getBigDecimal("cheques_pendientes"));
                c.setSaldoBanco(rs.getBigDecimal("saldo_banco"));
                c.setDiferencia(rs.getBigDecimal("diferencia"));
                c.setFechaConciliacion(rs.getDate("fecha_conciliacion"));
                c.setEstado(rs.getInt("estado"));
                c.setIdUsuario(rs.getInt("id_usuario"));
                c.setObservaciones(rs.getString("observaciones"));
                lista.add(c);
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
    
    // ⭐ Obtener la última conciliación registrada
    public Conciliacion obtenerUltima()
    {
        Conciliacion c = null;
        String sql = "SELECT * FROM conciliaciones ORDER BY fecha_conciliacion DESC LIMIT 1";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                c = new Conciliacion();
                c.setIdConciliacion(rs.getInt("id_conciliacion"));
                c.setPeriodo(rs.getString("periodo"));
                c.setSaldoLibros(rs.getBigDecimal("saldo_libros"));
                c.setDepositosTransito(rs.getBigDecimal("depositos_transito"));
                c.setChequesPendientes(rs.getBigDecimal("cheques_pendientes"));
                c.setSaldoBanco(rs.getBigDecimal("saldo_banco"));
                c.setDiferencia(rs.getBigDecimal("diferencia"));
                c.setFechaConciliacion(rs.getDate("fecha_conciliacion"));
                c.setEstado(rs.getInt("estado"));
                c.setIdUsuario(rs.getInt("id_usuario"));
                c.setObservaciones(rs.getString("observaciones"));
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
    
}
