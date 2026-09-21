package model;

import java.sql.Date;

public class Usuario
{
    private int idUsuario;
    private String nombre;
    private String apellido;
    private String usuario;
    private String password;
    private String correo;
    private String rol;
    private int estado;
    private Date fechaCreacion;
    private int intentosFallidos;
    private Date bloqueadoHasta;
    
    public Usuario() {}
    
    public Usuario(int idUsuario, String nombre, String apellido, String usuario, 
                   String password, String correo, String rol, int estado, 
                   Date fechaCreacion, int intentosFallidos, Date bloqueadoHasta)
    {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.usuario = usuario;
        this.password = password;
        this.correo = correo;
        this.rol = rol;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.intentosFallidos = intentosFallidos;
        this.bloqueadoHasta = bloqueadoHasta;
    }
    
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
    
    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public int getIntentosFallidos() { return intentosFallidos; }
    public void setIntentosFallidos(int intentosFallidos) { this.intentosFallidos = intentosFallidos; }
    
    public Date getBloqueadoHasta() { return bloqueadoHasta; }
    public void setBloqueadoHasta(Date bloqueadoHasta) { this.bloqueadoHasta = bloqueadoHasta; }
}