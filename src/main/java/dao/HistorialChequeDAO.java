package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import config.Conexion;

public class HistorialChequeDAO
{
    // ⭐ Registrar un cambio de estado en el historial
    public boolean registrar(int idCheque, int estadoAnterior, int estadoNuevo, int idUsuario, String observacion)
    {
        boolean registrado = false;
        int siguienteId = obtenerSiguienteId();

        String sql = "INSERT INTO historial_cheque "
                   + "(id_historial, id_cheque, estado_anterior, estado_nuevo, fecha_cambio, id_usuario, observacion) "
                   + "VALUES (?, ?, ?, ?, NOW(), ?, ?)";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, siguienteId);
            ps.setInt(2, idCheque);
            ps.setInt(3, estadoAnterior);
            ps.setInt(4, estadoNuevo);
            ps.setInt(5, idUsuario);
            ps.setString(6, observacion);

            int filas = ps.executeUpdate();
            if (filas > 0) registrado = true;

            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return registrado;
    }

    // ⭐ Siguiente id
    public int obtenerSiguienteId()
    {
        int siguienteId = 1;
        String sql = "SELECT MAX(id_historial) AS max_id FROM historial_cheque";

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
}