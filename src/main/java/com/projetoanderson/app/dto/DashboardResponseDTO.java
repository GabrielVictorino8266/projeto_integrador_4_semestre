package com.projetoanderson.app.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
public class DashboardResponseDTO {

    private KpisDTO kpis;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LimitesPlanoDTO limitesPlano;

    private List<ChartDataDTO> frotaPorStatus;
    private List<ChartDataDTO> custosManutencaoUltimos6Meses;
    private List<AlertaKmDTO> alertasManutencaoKm;
    private List<MotoristaDesempenhoDTO> motoristasBaixoDesempenho;

    public static class KpisDTO {
        private long totalVeiculos;
        private long veiculosAtivos;
        private long veiculosEmManutencao;
        private long totalMotoristas;

        public long getTotalVeiculos() { return totalVeiculos; }
        public void setTotalVeiculos(long totalVeiculos) { this.totalVeiculos = totalVeiculos; }
        public long getVeiculosAtivos() { return veiculosAtivos; }
        public void setVeiculosAtivos(long veiculosAtivos) { this.veiculosAtivos = veiculosAtivos; }
        public long getVeiculosEmManutencao() { return veiculosEmManutencao; }
        public void setVeiculosEmManutencao(long veiculosEmManutencao) { this.veiculosEmManutencao = veiculosEmManutencao; }
        public long getTotalMotoristas() { return totalMotoristas; }
        public void setTotalMotoristas(long totalMotoristas) { this.totalMotoristas = totalMotoristas; }
    }

    public static class LimitesPlanoDTO {
        private long veiculosUsados;
        private final long veiculosLimite = 5;
        private long motoristasUsados;
        private final long motoristasLimite = 5; 
        private long usuariosUsados;
        private final long usuariosLimite = 5; 

        public long getVeiculosUsados() { return veiculosUsados; }
        public void setVeiculosUsados(long veiculosUsados) { this.veiculosUsados = veiculosUsados; }
        public long getVeiculosLimite() { return veiculosLimite; }
        public long getMotoristasUsados() { return motoristasUsados; }
        public void setMotoristasUsados(long motoristasUsados) { this.motoristasUsados = motoristasUsados; }
        public long getMotoristasLimite() { return motoristasLimite; }
        public long getUsuariosUsados() { return usuariosUsados; }
        public void setUsuariosUsados(long usuariosUsados) { this.usuariosUsados = usuariosUsados; }
        public long getUsuariosLimite() { return usuariosLimite; }
    }

    public static class ChartDataDTO {
        private String name;
        private Number value;
        private String fill;

        public ChartDataDTO(String name, Number value) {
            this.name = name;
            this.value = value;
        }
        
        public ChartDataDTO(String name, Number value, String fill) {
            this.name = name;
            this.value = value;
            this.fill = fill;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Number getValue() { return value; }
        public void setValue(Number value) { this.value = value; }
        public String getFill() { return fill; }
        public void setFill(String fill) { this.fill = fill; }
        
        @com.fasterxml.jackson.annotation.JsonProperty("Custo")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public Number getCusto() {
             if (name != null && name.length() == 3) { // "Jan", "Fev", etc.
                 return value;
             }
             return null;
         }
        
         @com.fasterxml.jackson.annotation.JsonIgnore
         public Number getRawValue() {
             return value;
         }
    }

    public static class AlertaKmDTO {
        private Long id;
        private String placa;
        private Integer kmAtual;
        private Integer limiteKm;
        private String marca;

        public AlertaKmDTO(Long id, String placa, Integer kmAtual, Integer limiteKm, String marca) {
            this.id = id;
            this.placa = placa;
            this.kmAtual = kmAtual;
            this.limiteKm = limiteKm;
            this.marca = marca;
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getPlaca() { return placa; }
        public void setPlaca(String placa) { this.placa = placa; }
        public Integer getKmAtual() { return kmAtual; }
        public void setKmAtual(Integer kmAtual) { this.kmAtual = kmAtual; }
        public Integer getLimiteKm() { return limiteKm; }
        public void setLimiteKm(Integer limiteKm) { this.limiteKm = limiteKm; }
        public String getMarca() { return marca; }
        public void setMarca(String marca) { this.marca = marca; }
    }

    public static class MotoristaDesempenhoDTO {
        private Long id;
        private String nome;
        private Integer desempenho;
        private Long incidentesRecentes;

        public MotoristaDesempenhoDTO(Long id, String nome, Integer desempenho, Long incidentesRecentes) {
            this.id = id;
            this.nome = nome;
            this.desempenho = desempenho;
            this.incidentesRecentes = incidentesRecentes;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public Integer getDesempenho() { return desempenho; }
        public void setDesempenho(Integer desempenho) { this.desempenho = desempenho; }
        public Long getIncidentesRecentes() { return incidentesRecentes; }
        public void setIncidentesRecentes(Long incidentesRecentes) { this.incidentesRecentes = incidentesRecentes; }
    }

    public KpisDTO getKpis() { return kpis; }
    public void setKpis(KpisDTO kpis) { this.kpis = kpis; }
    public LimitesPlanoDTO getLimitesPlano() { return limitesPlano; }
    public void setLimitesPlano(LimitesPlanoDTO limitesPlano) { this.limitesPlano = limitesPlano; }
    public List<ChartDataDTO> getFrotaPorStatus() { return frotaPorStatus; }
    public void setFrotaPorStatus(List<ChartDataDTO> frotaPorStatus) { this.frotaPorStatus = frotaPorStatus; }
    public List<ChartDataDTO> getCustosManutencaoUltimos6Meses() { return custosManutencaoUltimos6Meses; }
    public void setCustosManutencaoUltimos6Meses(List<ChartDataDTO> custosManutencaoUltimos6Meses) { this.custosManutencaoUltimos6Meses = custosManutencaoUltimos6Meses; }
    public List<AlertaKmDTO> getAlertasManutencaoKm() { return alertasManutencaoKm; }
    public void setAlertasManutencaoKm(List<AlertaKmDTO> alertasManutencaoKm) { this.alertasManutencaoKm = alertasManutencaoKm; }
    public List<MotoristaDesempenhoDTO> getMotoristasBaixoDesempenho() { return motoristasBaixoDesempenho; }
    public void setMotoristasBaixoDesempenho(List<MotoristaDesempenhoDTO> motoristasBaixoDesempenho) { this.motoristasBaixoDesempenho = motoristasBaixoDesempenho; }
}