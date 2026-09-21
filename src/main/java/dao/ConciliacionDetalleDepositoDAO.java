package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.Conexion;
import model.ConciliacionDetalleDeposito;

public class ConciliacionDetalleDepositoDAO
{
    public List<ConciliacionDetalleDeposito> listarPorConciliacion(int idConciliacion)
    {
        List<ConciliacionDetalleDeposito> lista = new ArrayList<>();
        String sql = "SELECT * FROM conciliacion_detalle_deposito WHERE id_conciliacion = ?";
        
        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idConciliacion);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next())
            {
                ConciliacionDetalleDeposito detalle = new ConciliacionDetalleDeposito();
                detalle.setIdConciliacion(rs.getInt("id_conciliacion"));
                detalle.setIdDeposito(rs.getInt("id_deposito"));
                detalle.setMontoAlMomento(rs.getBigDecimal("monto_al_momento"));
                lista.add(detalle);
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
    
    public boolean agregarDetalle(ConciliacionDetalleDeposito detalle)
    {
        boolean agregado = false;
        String sql = "INSERT INTO conciliacion_detalle_deposito (id_conciliacion, id_deposito, monto_al_momento) VALUES (?, ?, ?)";
        
        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, detalle.getIdConciliacion());
            ps.setInt(2, detalle.getIdDeposito());
            ps.setBigDecimal(3, detalle.getMontoAlMomento());
            
            int filas = ps.executeUpdate();
            if (filas > 0)
            {
                agregado = true;
            }
            
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return agregado;
    }
    
    public boolean eliminarDetallesPorConciliacion(int idConciliacion)
    {
        boolean eliminado = false;
        String sql = "DELETE FROM conciliacion_detalle_deposito WHERE id_conciliacion = ?";
        
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
}