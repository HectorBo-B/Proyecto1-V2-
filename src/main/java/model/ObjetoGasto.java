package model;

import java.sql.Date;

public class ObjetoGasto
{
    private int idObjetoGasto;
    private String codigo;
    private String descripcion;
    private int estado;
    private Date fechaCreacion;
    
    public ObjetoGasto() {}
    
    public ObjetoGasto(int idObjetoGasto, String codigo, String descripcion, int estado, Date fechaCreacion)
    {
        this.idObjetoGasto = idObjetoGasto;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }
    
    public int getIdObjetoGasto() { return idObjetoGasto; }
    public void setIdObjetoGasto(int idObjetoGasto) { this.idObjetoGasto = idObjetoGasto; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
    
    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}