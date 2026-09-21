package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.Conexion;
import model.Proveedor;

public class ProveedorDAO
{
    public List<Proveedor> listarTodos()
    {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM proveedores ORDER BY id_proveedor ASC";
        
        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next())
            {
                Proveedor p = new Proveedor();
                p.setIdProveedor(rs.getInt("id_proveedor"));
                p.setNombre(rs.getString("nombre"));
                p.setTelefono(rs.getString("telefono"));
                p.setCorreo(rs.getString("correo"));
                p.setEstado(rs.getInt("estado"));
                p.setRuc(rs.getString("ruc"));
                lista.add(p);
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
    
    public List<Proveedor> listarActivos()
    {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM proveedores WHERE estado = 1 ORDER BY id_proveedor ASC";
        
        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next())
            {
                Proveedor p = new Proveedor();
                p.setIdProveedor(rs.getInt("id_proveedor"));
                p.setNombre(rs.getString("nombre"));
                p.setTelefono(rs.getString("telefono"));
                p.setCorreo(rs.getString("correo"));
                p.setEstado(rs.getInt("estado"));
                p.setRuc(rs.getString("ruc"));
                lista.add(p);
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
    
    public Proveedor obtenerPorId(int idProveedor)
    {
        Proveedor p = null;
        String sql = "SELECT * FROM proveedores WHERE id_proveedor = ?";
        
        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idProveedor);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next())
            {
                p = new Proveedor();
                p.setIdProveedor(rs.getInt("id_proveedor"));
                p.setNombre(rs.getString("nombre"));
                p.setTelefono(rs.getString("telefono"));
                p.setCorreo(rs.getString("correo"));
                p.setEstado(rs.getInt("estado"));
                p.setRuc(rs.getString("ruc"));
            }
            
            rs.close();
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return p;
    }
    
    public boolean crear(Proveedor proveedor)
{
    boolean creado = false;
    
    int siguienteId = obtenerSiguienteId();
    proveedor.setIdProveedor(siguienteId);
    
    String sql = "INSERT INTO proveedores (id_proveedor, nombre, telefono, correo, estado, ruc) VALUES (?, ?, ?, ?, ?, ?)";
    
    try
    {
        Connection conn = Conexion.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, proveedor.getIdProveedor());
        ps.setString(2, proveedor.getNombre());
        ps.setString(3, proveedor.getTelefono());
        ps.setString(4, proveedor.getCorreo());
        ps.setInt(5, proveedor.getEstado());
        ps.setString(6, proveedor.getRuc());
        
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
    
    public boolean actualizar(Proveedor proveedor)
    {
        boolean actualizado = false;
        String sql = "UPDATE proveedores SET nombre = ?, telefono = ?, correo = ?, estado = ?, ruc = ? WHERE id_proveedor = ?";
        
        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getTelefono());
            ps.setString(3, proveedor.getCorreo());
            ps.setInt(4, proveedor.getEstado());
            ps.setString(5, proveedor.getRuc());
            ps.setInt(6, proveedor.getIdProveedor());
            
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
    
    public boolean eliminar(int idProveedor)
    {
        boolean eliminado = false;
        String sql = "DELETE FROM proveedores WHERE id_proveedor = ?";
        
        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idProveedor);
            
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
    
    public List<Proveedor> buscar(String texto)
    {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM proveedores WHERE nombre LIKE ? OR ruc LIKE ? ORDER BY id_proveedor ASC";
        
        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next())
            {
                Proveedor p = new Proveedor();
                p.setIdProveedor(rs.getInt("id_proveedor"));
                p.setNombre(rs.getString("nombre"));
                p.setTelefono(rs.getString("telefono"));
                p.setCorreo(rs.getString("correo"));
                p.setEstado(rs.getInt("estado"));
                p.setRuc(rs.getString("ruc"));
                lista.add(p);
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
        String sql = "SELECT MAX(id_proveedor) AS max_id FROM proveedores";
        
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
    
    public boolean cambiarEstado(int idProveedor, int nuevoEstado)
{
    boolean actualizado = false;
    String sql = "UPDATE proveedores SET estado = ? WHERE id_proveedor = ?";
    
    try
    {
        Connection conn = Conexion.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, nuevoEstado);
        ps.setInt(2, idProveedor);
        
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