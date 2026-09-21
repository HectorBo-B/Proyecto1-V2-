package model;

import java.math.BigDecimal;

public class ConciliacionDetalleDeposito
{
    private int idConciliacion;
    private int idDeposito;
    private BigDecimal montoAlMomento;
    
    public ConciliacionDetalleDeposito() {}
    
    public ConciliacionDetalleDeposito(int idConciliacion, int idDeposito, BigDecimal montoAlMomento)
    {
        this.idConciliacion = idConciliacion;
        this.idDeposito = idDeposito;
        this.montoAlMomento = montoAlMomento;
    }
    
    public int getIdConciliacion() { return idConciliacion; }
    public void setIdConciliacion(int idConciliacion) { this.idConciliacion = idConciliacion; }
    
    public int getIdDeposito() { return idDeposito; }
    public void setIdDeposito(int idDeposito) { this.idDeposito = idDeposito; }
    
    public BigDecimal getMontoAlMomento() { return montoAlMomento; }
    public void setMontoAlMomento(BigDecimal montoAlMomento) { this.montoAlMomento = montoAlMomento; }
}