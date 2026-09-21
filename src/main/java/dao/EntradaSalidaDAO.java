package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import config.Conexion;

public class EntradaSalidaDAO
{
    // ⭐ Registrar entrada (login exitoso)
    public boolean registrarEntrada(int idUsuario, int intentosFallidos)
    {
        boolean registrado = false;
        int siguienteId = obtenerSiguienteId();

        String sql = "INSERT INTO entradas_y_salidas (id_log, id_usuario, fecha_entrada, fecha_salida, intentos_fallidos) "
                   + "VALUES (?, ?, NOW(), NULL, ?)";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, siguienteId);
            ps.setInt(2, idUsuario);
            ps.setInt(3, intentosFallidos);

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

    // ⭐ Registrar salida (logout) - actualiza el último registro abierto del usuario
    public boolean registrarSalida(int idUsuario)
    {
        boolean actualizado = false;

        String sql = "UPDATE entradas_y_salidas "
                   + "SET fecha_salida = NOW() "
                   + "WHERE id_usuario = ? AND fecha_salida IS NULL "
                   + "ORDER BY id_log DESC LIMIT 1";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idUsuario);

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

    // ⭐ Actualizar intentos fallidos en el último registro del usuario
    public boolean actualizarIntentosFallidos(int idUsuario, int intentos)
    {
        boolean actualizado = false;

        String sql = "UPDATE entradas_y_salidas "
                   + "SET intentos_fallidos = ? "
                   + "WHERE id_usuario = ? "
                   + "ORDER BY id_log DESC LIMIT 1";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, intentos);
            ps.setInt(2, idUsuario);

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

    // ⭐ Siguiente id
    public int obtenerSiguienteId()
    {
        int siguienteId = 1;
        String sql = "SELECT MAX(id_log) AS max_id FROM entradas_y_salidas";

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