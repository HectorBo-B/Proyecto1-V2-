package model;

import java.math.BigDecimal;
import java.sql.Date;

public class Conciliacion
{
    private int idConciliacion;
    private String periodo;
    private BigDecimal saldoLibros;
    private BigDecimal depositosTransito;
    private BigDecimal chequesPendientes;
    private BigDecimal saldoBanco;
    private BigDecimal diferencia;
    private Date fechaConciliacion;
    private int estado;
    private int idUsuario;
    private String observaciones;
    
    public Conciliacion() {}
    
    public Conciliacion(int idConciliacion, String periodo, BigDecimal saldoLibros, 
                        BigDecimal depositosTransito, BigDecimal chequesPendientes, 
                        BigDecimal saldoBanco, BigDecimal diferencia, Date fechaConciliacion, 
                        int estado, int idUsuario, String observaciones)
    {
        this.idConciliacion = idConciliacion;
        this.periodo = periodo;
        this.saldoLibros = saldoLibros;
        this.depositosTransito = depositosTransito;
        this.chequesPendientes = chequesPendientes;
        this.saldoBanco = saldoBanco;
        this.diferencia = diferencia;
        this.fechaConciliacion = fechaConciliacion;
        this.estado = estado;
        this.idUsuario = idUsuario;
        this.observaciones = observaciones;
    }
    
    public int getIdConciliacion() { return idConciliacion; }
    public void setIdConciliacion(int idConciliacion) { this.idConciliacion = idConciliacion; }
    
    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }
    
    public BigDecimal getSaldoLibros() { return saldoLibros; }
    public void setSaldoLibros(BigDecimal saldoLibros) { this.saldoLibros = saldoLibros; }
    
    public BigDecimal getDepositosTransito() { return depositosTransito; }
    public void setDepositosTransito(BigDecimal depositosTransito) { this.depositosTransito = depositosTransito; }
    
    public BigDecimal getChequesPendientes() { return chequesPendientes; }
    public void setChequesPendientes(BigDecimal chequesPendientes) { this.chequesPendientes = chequesPendientes; }
    
    public BigDecimal getSaldoBanco() { return saldoBanco; }
    public void setSaldoBanco(BigDecimal saldoBanco) { this.saldoBanco = saldoBanco; }
    
    public BigDecimal getDiferencia() { return diferencia; }
    public void setDiferencia(BigDecimal diferencia) { this.diferencia = diferencia; }
    
    public Date getFechaConciliacion() { return fechaConciliacion; }
    public void setFechaConciliacion(Date fechaConciliacion) { this.fechaConciliacion = fechaConciliacion; }
    
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
    
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}