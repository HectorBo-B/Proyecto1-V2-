package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.Conexion;
import model.ObjetoGasto;

public class ObjetoGastoDAO
{
    public List<ObjetoGasto> listarTodos()
    {
        List<ObjetoGasto> lista = new ArrayList<>();
        String sql = "SELECT * FROM objeto_gasto ORDER BY codigo";
        
        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next())
            {
                ObjetoGasto o = new ObjetoGasto();
                o.setIdObjetoGasto(rs.getInt("id_objeto_gasto"));
                o.setCodigo(rs.getString("codigo"));
                o.setDescripcion(rs.getString("descripcion"));
                o.setEstado(rs.getInt("estado"));
                o.setFechaCreacion(rs.getDate("fecha_creacion"));
                lista.add(o);
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
    
    public List<ObjetoGasto> listarActivos()
    {
        List<ObjetoGasto> lista = new ArrayList<>();
        String sql = "SELECT * FROM objeto_gasto WHERE estado IN (1, 2, 3, 6) ORDER BY codigo";
        
        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next())
            {
                ObjetoGasto o = new ObjetoGasto();
                o.setIdObjetoGasto(rs.getInt("id_objeto_gasto"));
                o.setCodigo(rs.getString("codigo"));
                o.setDescripcion(rs.getString("descripcion"));
                o.setEstado(rs.getInt("estado"));
                o.setFechaCreacion(rs.getDate("fecha_creacion"));
                lista.add(o);
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
    
    public ObjetoGasto obtenerPorId(int idObjetoGasto)
    {
        ObjetoGasto o = null;
        String sql = "SELECT * FROM objeto_gasto WHERE id_objeto_gasto = ?";
        
        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idObjetoGasto);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next())
            {
                o = new ObjetoGasto();
                o.setIdObjetoGasto(rs.getInt("id_objeto_gasto"));
                o.setCodigo(rs.getString("codigo"));
                o.setDescripcion(rs.getString("descripcion"));
                o.setEstado(rs.getInt("estado"));
                o.setFechaCreacion(rs.getDate("fecha_creacion"));
            }
            
            rs.close();
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return o;
    }
    
    public ObjetoGasto obtenerPorCodigo(String codigo)
    {
        ObjetoGasto o = null;
        String sql = "SELECT * FROM objeto_gasto WHERE codigo = ?";
        
        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next())
            {
                o = new ObjetoGasto();
                o.setIdObjetoGasto(rs.getInt("id_objeto_gasto"));
                o.setCodigo(rs.getString("codigo"));
                o.setDescripcion(rs.getString("descripcion"));
                o.setEstado(rs.getInt("estado"));
                o.setFechaCreacion(rs.getDate("fecha_creacion"));
            }
            
            rs.close();
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return o;
    }
    
    public boolean crear(ObjetoGasto objeto)
{
    boolean creado = false;
    
    int siguienteId = obtenerSiguienteId();
    objeto.setIdObjetoGasto(siguienteId);
    
    String sql = "INSERT INTO objeto_gasto (id_objeto_gasto, codigo, descripcion, estado) VALUES (?, ?, ?, ?)";
    
    try
    {
        Connection conn = Conexion.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, objeto.getIdObjetoGasto());
        ps.setString(2, objeto.getCodigo());
        ps.setString(3, objeto.getDescripcion());
        ps.setInt(4, objeto.getEstado());
        
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
    
    public boolean actualizar(ObjetoGasto objeto)
    {
        boolean actualizado = false;
        String sql = "UPDATE objeto_gasto SET codigo = ?, descripcion = ?, estado = ? WHERE id_objeto_gasto = ?";
        
        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, objeto.getCodigo());
            ps.setString(2, objeto.getDescripcion());
            ps.setInt(3, objeto.getEstado());
            ps.setInt(4, objeto.getIdObjetoGasto());
            
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
    
    public boolean eliminar(int idObjetoGasto)
    {
        boolean eliminado = false;
        String sql = "DELETE FROM objeto_gasto WHERE id_objeto_gasto = ?";
        
        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idObjetoGasto);
            
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
    
    public List<ObjetoGasto> buscar(String texto)
    {
        List<ObjetoGasto> lista = new ArrayList<>();
        String sql = "SELECT * FROM objeto_gasto " +
                     "WHERE codigo LIKE ? OR descripcion LIKE ? " +
                     "ORDER BY " +
                     "  CASE " +
                     "    WHEN LOWER(codigo) = LOWER(?) THEN 1 " +
                     "    WHEN LOWER(codigo) LIKE LOWER(?) THEN 2 " +
                     "    WHEN LOWER(descripcion) LIKE LOWER(?) THEN 3 " +
                     "    ELSE 4 " +
                     "  END, codigo";
        
        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            ps.setString(3, texto);
            ps.setString(4, texto + "%");
            ps.setString(5, "%" + texto + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next())
            {
                ObjetoGasto o = new ObjetoGasto();
                o.setIdObjetoGasto(rs.getInt("id_objeto_gasto"));
                o.setCodigo(rs.getString("codigo"));
                o.setDescripcion(rs.getString("descripcion"));
                o.setEstado(rs.getInt("estado"));
                o.setFechaCreacion(rs.getDate("fecha_creacion"));
                lista.add(o);
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
        String sql = "SELECT MAX(id_objeto_gasto) AS max_id FROM objeto_gasto";
        
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
    
    public boolean cambiarEstado(int idObjetoGasto, int nuevoEstado)
{
    boolean actualizado = false;
    String sql = "UPDATE objeto_gasto SET estado = ? WHERE id_objeto_gasto = ?";
    
    try
    {
        Connection conn = Conexion.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, nuevoEstado);
        ps.setInt(2, idObjetoGasto);
        
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
}