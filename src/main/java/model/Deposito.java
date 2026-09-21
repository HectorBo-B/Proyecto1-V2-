package model;

import java.math.BigDecimal;
import java.sql.Date;

public class Deposito
{
    private int idDeposito;
    private String numeroComprobante;
    private int tipoDeposito;
    private Date fecha;
    private BigDecimal monto;
    private String detalle;
    private int estado;
    private int idUsuario;
    private Date fechaRegistro;
    
    public Deposito() {}
    
    public Deposito(int idDeposito, String numeroComprobante, int tipoDeposito, Date fecha, 
                    BigDecimal monto, String detalle, int estado, int idUsuario, Date fechaRegistro)
    {
        this.idDeposito = idDeposito;
        this.numeroComprobante = numeroComprobante;
        this.tipoDeposito = tipoDeposito;
        this.fecha = fecha;
        this.monto = monto;
        this.detalle = detalle;
        this.estado = estado;
        this.idUsuario = idUsuario;
        this.fechaRegistro = fechaRegistro;
    }
    
    public int getIdDeposito() { return idDeposito; }
    public void setIdDeposito(int idDeposito) { this.idDeposito = idDeposito; }
    
    public String getNumeroComprobante() { return numeroComprobante; }
    public void setNumeroComprobante(String numeroComprobante) { this.numeroComprobante = numeroComprobante; }
    
    public int getTipoDeposito() { return tipoDeposito; }
    public void setTipoDeposito(int tipoDeposito) { this.tipoDeposito = tipoDeposito; }
    
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
    
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
    
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}