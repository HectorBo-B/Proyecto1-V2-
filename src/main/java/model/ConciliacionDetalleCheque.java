package model;

import java.math.BigDecimal;

public class ConciliacionDetalleCheque
{
    private int idConciliacion;
    private int idCheque;
    private BigDecimal montoAlMomento;
    
    public ConciliacionDetalleCheque() {}
    
    public ConciliacionDetalleCheque(int idConciliacion, int idCheque, BigDecimal montoAlMomento)
    {
        this.idConciliacion = idConciliacion;
        this.idCheque = idCheque;
        this.montoAlMomento = montoAlMomento;
    }
    
    public int getIdConciliacion() { return idConciliacion; }
    public void setIdConciliacion(int idConciliacion) { this.idConciliacion = idConciliacion; }
    
    public int getIdCheque() { return idCheque; }
    public void setIdCheque(int idCheque) { this.idCheque = idCheque; }
    
    public BigDecimal getMontoAlMomento() { return montoAlMomento; }
    public void setMontoAlMomento(BigDecimal montoAlMomento) { this.montoAlMomento = montoAlMomento; }
}