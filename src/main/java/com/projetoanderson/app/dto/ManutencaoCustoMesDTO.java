package com.projetoanderson.app.dto;

import java.math.BigDecimal;
public class ManutencaoCustoMesDTO {
    
    private String mesAno; 
    private BigDecimal custo;

    public ManutencaoCustoMesDTO(String mesAno, BigDecimal custo) {
        this.mesAno = mesAno;
        this.custo = custo;
    }
    
    public ManutencaoCustoMesDTO(String mesAno, Double custo) {
         this.mesAno = mesAno;
         this.custo = (custo != null) ? BigDecimal.valueOf(custo) : BigDecimal.ZERO;
    }

    public String getMesAno() {
        return mesAno;
    }

    public BigDecimal getCusto() {
        return custo;
    }
}