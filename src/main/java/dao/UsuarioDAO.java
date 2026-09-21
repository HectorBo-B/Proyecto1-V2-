package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

import config.Conexion;
import model.Usuario;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO
{
    public Usuario validarLogin(String usuario, String password)
{
    Usuario user = null;
    
    // ⭐ ENCRIPTAR LA CONTRASEÑA ANTES DE BUSCAR
    String passwordHash = config.Encriptador.encriptarSHA256(password);
    
    String sql = "SELECT * FROM usuarios WHERE usuario = ? AND password = ? AND estado = 1";
    
    try
    {
        Connection conn = Conexion.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, usuario);
        ps.setString(2, passwordHash);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next())
        {
            user = new Usuario();
            user.setIdUsuario(rs.getInt("id_usuario"));
            user.setNombre(rs.getString("nombre"));
            user.setApellido(rs.getString("apellido"));
            user.setUsuario(rs.getString("usuario"));
            user.setPassword(rs.getString("password"));
            user.setCorreo(rs.getString("correo"));
            user.setRol(rs.getString("rol"));
            user.setEstado(rs.getInt("estado"));
            user.setFechaCreacion(rs.getDate("fecha_creacion"));
            user.setIntentosFallidos(rs.getInt("intentos_fallidos"));
            user.setBloqueadoHasta(rs.getDate("bloqueado_hasta"));
        }
        
        rs.close();
        ps.close();
    }
    catch (SQLException e)
    {
        e.printStackTrace();
    }
    
    return user;
}
    
    public Usuario obtenerPorId(int idUsuario)
    {
        Usuario user = null;
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
        
        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next())
            {
                user = new Usuario();
                user.setIdUsuario(rs.getInt("id_usuario"));
                user.setNombre(rs.getString("nombre"));
                user.setApellido(rs.getString("apellido"));
                user.setUsuario(rs.getString("usuario"));
                user.setPassword(rs.getString("password"));
                user.setCorreo(rs.getString("correo"));
                user.setRol(rs.getString("rol"));
                user.setEstado(rs.getInt("estado"));
                user.setFechaCreacion(rs.getDate("fecha_creacion"));
                user.setIntentosFallidos(rs.getInt("intentos_fallidos"));
                user.setBloqueadoHasta(rs.getDate("bloqueado_hasta"));
            }
            
            rs.close();
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return user;
    }
    
    public boolean actualizarIntentosFallidos(int idUsuario, int intentos)
    {
        boolean actualizado = false;
        String sql = "UPDATE usuarios SET intentos_fallidos = ? WHERE id_usuario = ?";
        
        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, intentos);
            ps.setInt(2, idUsuario);
            
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
    
    public boolean bloquearUsuario(int idUsuario, Date fechaBloqueo)
    {
        boolean actualizado = false;
        String sql = "UPDATE usuarios SET bloqueado_hasta = ? WHERE id_usuario = ?";
        
        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDate(1, fechaBloqueo);
            ps.setInt(2, idUsuario);
            
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
    
    // ⭐ Listar todos los usuarios
    public List<Usuario> listarTodos()
    {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY id_usuario ASC";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                lista.add(mapearUsuario(rs));
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

    // ⭐ Buscar por nombre, apellido, usuario o correo (búsqueda parcial)
    public List<Usuario> buscar(String texto)
    {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios "
                   + "WHERE nombre LIKE ? OR apellido LIKE ? OR usuario LIKE ? OR correo LIKE ? "
                   + "ORDER BY id_usuario ASC";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            String filtro = "%" + texto + "%";
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            ps.setString(3, filtro);
            ps.setString(4, filtro);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                lista.add(mapearUsuario(rs));
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

    // ⭐ Buscar por rol
    public List<Usuario> buscarPorRol(String rol)
    {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol = ? ORDER BY id_usuario ASC";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, rol);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                lista.add(mapearUsuario(rs));
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

    // ⭐ Buscar combinando texto y rol
    public List<Usuario> buscarConFiltros(String texto, String rol)
    {
        List<Usuario> lista = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder(
            "SELECT * FROM usuarios WHERE (nombre LIKE ? OR apellido LIKE ? OR usuario LIKE ? OR correo LIKE ?) "
        );
        
        if (rol != null && !rol.isEmpty()) {
            sql.append("AND rol = ? ");
        }
        sql.append("ORDER BY id_usuario ASC");

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            String filtro = "%" + texto + "%";
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            ps.setString(3, filtro);
            ps.setString(4, filtro);
            
            if (rol != null && !rol.isEmpty()) {
                ps.setString(5, rol);
            }
            
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                lista.add(mapearUsuario(rs));
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

    // ⭐ Verificar si ya existe un usuario con ese username
    public boolean existeUsuario(String usuario)
    {
        boolean existe = false;
        String sql = "SELECT COUNT(*) AS total FROM usuarios WHERE usuario = ?";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next() && rs.getInt("total") > 0)
            {
                existe = true;
            }

            rs.close();
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return existe;
    }

    // ⭐ Verificar si ya existe un correo
    public boolean existeCorreo(String correo)
    {
        boolean existe = false;
        String sql = "SELECT COUNT(*) AS total FROM usuarios WHERE correo = ?";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();

            if (rs.next() && rs.getInt("total") > 0)
            {
                existe = true;
            }

            rs.close();
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return existe;
    }

    // ⭐ Crear usuario
    public boolean crear(Usuario usuario)
    {
        boolean creado = false;
        int siguienteId = obtenerSiguienteIdUsuarios();
        usuario.setIdUsuario(siguienteId);

        String sql = "INSERT INTO usuarios (id_usuario, nombre, apellido, usuario, password, correo, rol, estado) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, 1)";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, usuario.getIdUsuario());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellido());
            ps.setString(4, usuario.getUsuario());
            ps.setString(5, usuario.getPassword()); // ya viene hasheado
            ps.setString(6, usuario.getCorreo());
            ps.setString(7, usuario.getRol());

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

    // ⭐ Actualizar usuario (sin tocar password, a menos que venga uno nuevo)
    public boolean actualizar(Usuario usuario, boolean cambiarPassword)
    {
        boolean actualizado = false;

        String sql;
        if (cambiarPassword) {
            sql = "UPDATE usuarios SET nombre = ?, apellido = ?, usuario = ?, password = ?, correo = ?, rol = ? WHERE id_usuario = ?";
        } else {
            sql = "UPDATE usuarios SET nombre = ?, apellido = ?, usuario = ?, correo = ?, rol = ? WHERE id_usuario = ?";
        }

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getUsuario());
            
            if (cambiarPassword) {
                ps.setString(4, usuario.getPassword());
                ps.setString(5, usuario.getCorreo());
                ps.setString(6, usuario.getRol());
                ps.setInt(7, usuario.getIdUsuario());
            } else {
                ps.setString(4, usuario.getCorreo());
                ps.setString(5, usuario.getRol());
                ps.setInt(6, usuario.getIdUsuario());
            }

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

    // ⭐ Cambiar estado (activar/inactivar)
    public boolean cambiarEstado(int idUsuario, int nuevoEstado)
    {
        boolean actualizado = false;
        String sql = "UPDATE usuarios SET estado = ? WHERE id_usuario = ?";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, nuevoEstado);
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
    public int obtenerSiguienteIdUsuarios()
    {
        int siguienteId = 1;
        String sql = "SELECT MAX(id_usuario) AS max_id FROM usuarios";

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

    // ⭐ Método auxiliar para mapear usuario desde el ResultSet
    private Usuario mapearUsuario(ResultSet rs) throws SQLException
    {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNombre(rs.getString("nombre"));
        u.setApellido(rs.getString("apellido"));
        u.setUsuario(rs.getString("usuario"));
        u.setPassword(rs.getString("password"));
        u.setCorreo(rs.getString("correo"));
        u.setRol(rs.getString("rol"));
        u.setEstado(rs.getInt("estado"));
        u.setFechaCreacion(rs.getDate("fecha_creacion"));
        u.setIntentosFallidos(rs.getInt("intentos_fallidos"));
        u.setBloqueadoHasta(rs.getDate("bloqueado_hasta"));
        return u;
    }
    // ⭐ Obtener usuario por nombre de usuario (sin importar contraseña)
    public Usuario obtenerPorUsuario(String usuario)
    {
        Usuario user = null;
        String sql = "SELECT * FROM usuarios WHERE usuario = ?";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                user = mapearUsuario(rs);
            }

            rs.close();
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return user;
    }
    // ⭐ Resetear intentos fallidos y desbloquear usuario
    public boolean resetearIntentos(int idUsuario)
    {
        boolean actualizado = false;
        String sql = "UPDATE usuarios SET intentos_fallidos = 0, bloqueado_hasta = NULL WHERE id_usuario = ?";

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
    // ⭐ Verificar si un usuario está bloqueado
    // Retorna 0 si NO está bloqueado, o los SEGUNDOS RESTANTES si lo está
    public int segundosBloqueoRestantes(int idUsuario)
    {
        int segundos = 0;
        String sql = "SELECT TIMESTAMPDIFF(SECOND, NOW(), bloqueado_hasta) AS segundos "
                   + "FROM usuarios WHERE id_usuario = ?";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                segundos = rs.getInt("segundos");
                if (segundos < 0) segundos = 0;
            }

            rs.close();
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return segundos;
    }

    // ⭐ Bloquear usuario por N segundos
    public boolean bloquearUsuarioPorSegundos(int idUsuario, int segundos)
    {
        boolean actualizado = false;
        String sql = "UPDATE usuarios SET bloqueado_hasta = DATE_ADD(NOW(), INTERVAL ? SECOND) WHERE id_usuario = ?";

        try
        {
            Connection conn = Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, segundos);
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
}

