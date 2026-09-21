package model;

import java.math.BigDecimal;
import java.sql.Date;

public class Cheque
{
    private int idCheque;
    private String numeroCheque;
    private Date fechaCheque;
    private int idProveedor;
    private BigDecimal monto;
    private String montoLetras;
    private String detalle;
    private int idObjetoGasto;
    private int estado;
    private Date fechaAnulacion;
    private String motivoAnulacion;
    private Date fechaSalidaCirculacion;
    private String observacionSalida;
    private int idUsuarioCreacion;
    private Integer idUsuarioAnulacion;
    private String nombreProveedor;
    private String nombreObjetoGasto; 
    
    public Cheque() {}
    
    public Cheque(int idCheque, String numeroCheque, Date fechaCheque, int idProveedor, 
                  BigDecimal monto, String montoLetras, String detalle, int idObjetoGasto, 
                  int estado, Date fechaAnulacion, String motivoAnulacion, 
                  Date fechaSalidaCirculacion, String observacionSalida, 
                  int idUsuarioCreacion, Integer idUsuarioAnulacion)
    {
        this.idCheque = idCheque;
        this.numeroCheque = numeroCheque;
        this.fechaCheque = fechaCheque;
        this.idProveedor = idProveedor;
        this.monto = monto;
        this.montoLetras = montoLetras;
        this.detalle = detalle;
        this.idObjetoGasto = idObjetoGasto;
        this.estado = estado;
        this.fechaAnulacion = fechaAnulacion;
        this.motivoAnulacion = motivoAnulacion;
        this.fechaSalidaCirculacion = fechaSalidaCirculacion;
        this.observacionSalida = observacionSalida;
        this.idUsuarioCreacion = idUsuarioCreacion;
        this.idUsuarioAnulacion = idUsuarioAnulacion;
    }
    
    public int getIdCheque() { return idCheque; }
    public void setIdCheque(int idCheque) { this.idCheque = idCheque; }
    
    public String getNumeroCheque() { return numeroCheque; }
    public void setNumeroCheque(String numeroCheque) { this.numeroCheque = numeroCheque; }
    
    public Date getFechaCheque() { return fechaCheque; }
    public void setFechaCheque(Date fechaCheque) { this.fechaCheque = fechaCheque; }
    
    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }
    
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    
    public String getMontoLetras() { return montoLetras; }
    public void setMontoLetras(String montoLetras) { this.montoLetras = montoLetras; }
    
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
    
    public int getIdObjetoGasto() { return idObjetoGasto; }
    public void setIdObjetoGasto(int idObjetoGasto) { this.idObjetoGasto = idObjetoGasto; }
    
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
    
    public Date getFechaAnulacion() { return fechaAnulacion; }
    public void setFechaAnulacion(Date fechaAnulacion) { this.fechaAnulacion = fechaAnulacion; }
    
    public String getMotivoAnulacion() { return motivoAnulacion; }
    public void setMotivoAnulacion(String motivoAnulacion) { this.motivoAnulacion = motivoAnulacion; }
    
    public Date getFechaSalidaCirculacion() { return fechaSalidaCirculacion; }
    public void setFechaSalidaCirculacion(Date fechaSalidaCirculacion) { this.fechaSalidaCirculacion = fechaSalidaCirculacion; }
    
    public String getObservacionSalida() { return observacionSalida; }
    public void setObservacionSalida(String observacionSalida) { this.observacionSalida = observacionSalida; }
    
    public int getIdUsuarioCreacion() { return idUsuarioCreacion; }
    public void setIdUsuarioCreacion(int idUsuarioCreacion) { this.idUsuarioCreacion = idUsuarioCreacion; }
    
    public Integer getIdUsuarioAnulacion() { return idUsuarioAnulacion; }
    public void setIdUsuarioAnulacion(Integer idUsuarioAnulacion) { this.idUsuarioAnulacion = idUsuarioAnulacion; }
    
    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }
    
    public String getNombreObjetoGasto() { return nombreObjetoGasto; }
    public void setNombreObjetoGasto(String nombreObjetoGasto) { this.nombreObjetoGasto = nombreObjetoGasto; }
}