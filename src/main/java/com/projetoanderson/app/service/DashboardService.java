package com.projetoanderson.app.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.projetoanderson.app.dto.DashboardResponseDTO;
import com.projetoanderson.app.dto.ManutencaoCustoMesDTO;
import com.projetoanderson.app.model.entity.Empresa;
import com.projetoanderson.app.model.entity.Funcao;
import com.projetoanderson.app.model.entity.enums.StatusVeiculo;
import com.projetoanderson.app.model.entity.enums.TipoPlano;
import com.projetoanderson.app.repository.EmpresaRepository;
import com.projetoanderson.app.repository.ManutencaoRepository;
import com.projetoanderson.app.repository.PerfilMotoristaRepository;
import com.projetoanderson.app.repository.UsuarioRepository;
import com.projetoanderson.app.repository.VeiculoRepository;
import com.projetoanderson.app.security.UsuarioAuthenticated;

@Service
public class DashboardService {

    private final VeiculoRepository veiculoRepository;
    private final PerfilMotoristaRepository perfilMotoristaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ManutencaoRepository manutencaoRepository;
    private final EmpresaRepository empresaRepository;

    // Constantes
    private static final int LIMITE_PLANO_GRATUITO = 5;
    private static final int SCORE_BAIXO_DESEMPENHO = 7;
    private static final int MESES_GRAFICO_CUSTO = 6;

    public DashboardService(VeiculoRepository veiculoRepository,
                            PerfilMotoristaRepository perfilMotoristaRepository,
                            UsuarioRepository usuarioRepository,
                            ManutencaoRepository manutencaoRepository,
                            EmpresaRepository empresaRepository) {
        this.veiculoRepository = veiculoRepository;
        this.perfilMotoristaRepository = perfilMotoristaRepository;
        this.usuarioRepository = usuarioRepository;
        this.manutencaoRepository = manutencaoRepository;
        this.empresaRepository = empresaRepository;
    }

    // --- Métodos de Segurança (copiados de outros serviços) ---
    
    private UsuarioAuthenticated getUsuarioAutenticado() {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         if (authentication == null || !(authentication.getPrincipal() instanceof UsuarioAuthenticated)) {
             throw new IllegalStateException("Usuário não autenticado ou tipo de Principal inesperado.");
         }
         return (UsuarioAuthenticated) authentication.getPrincipal();
    }
    
    private boolean temRole(UsuarioAuthenticated usuarioAuth, String roleName) {
        return usuarioAuth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(roleName));
    }

    private Empresa getEmpresaDoUsuarioLogado() {
        UsuarioAuthenticated usuarioAuth = getUsuarioAutenticado();
        if (temRole(usuarioAuth, Funcao.ROLE_SUPER_ADMIN)) {
            // Dashboard do Super Admin pode ser implementado no futuro
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Dashboard não disponível para Super Admin.");
        }
        Empresa empresaDoUsuario = usuarioAuth.getUsuario().getEmpresa();
        if (empresaDoUsuario == null) {
             throw new IllegalStateException("Usuário autenticado não está associado a nenhuma empresa.");
        }
        // Recarrega a empresa para garantir dados atualizados (plano)
        return empresaRepository.findById(empresaDoUsuario.getId())
             .orElseThrow(() -> new IllegalStateException("Empresa do usuário logado não encontrada no banco."));
    }

    // --- Método Principal ---

    @Transactional(readOnly = true)
    public DashboardResponseDTO getDashboardData() {
        Empresa empresa = getEmpresaDoUsuarioLogado();
        Long empresaId = empresa.getId();

        DashboardResponseDTO response = new DashboardResponseDTO();

        response.setKpis(buildKpis(empresaId));

        if (empresa.getTipoPlano() == TipoPlano.GRATUITO) {
            response.setLimitesPlano(buildLimitesPlano(empresaId));
        }

        response.setFrotaPorStatus(buildFrotaPorStatus(empresaId));

        response.setCustosManutencaoUltimos6Meses(buildCustosUltimosMeses(empresaId));
        
        response.setAlertasManutencaoKm(
            veiculoRepository.findAlertaManutencaoKm(empresaId)
        );

        response.setMotoristasBaixoDesempenho(
            perfilMotoristaRepository.findBaixoDesempenho(empresaId, SCORE_BAIXO_DESEMPENHO)
        );

        return response;
    }


    private DashboardResponseDTO.KpisDTO buildKpis(Long empresaId) {
        DashboardResponseDTO.KpisDTO kpis = new DashboardResponseDTO.KpisDTO();
        kpis.setTotalVeiculos(veiculoRepository.countByEmpresaId(empresaId));
        kpis.setVeiculosAtivos(veiculoRepository.countByEmpresaIdAndStatus(empresaId, StatusVeiculo.ATIVO));
        kpis.setVeiculosEmManutencao(veiculoRepository.countByEmpresaIdAndStatus(empresaId, StatusVeiculo.MANUTENCAO));
        kpis.setTotalMotoristas(perfilMotoristaRepository.countByEmpresaId(empresaId));
        return kpis;
    }

    private DashboardResponseDTO.LimitesPlanoDTO buildLimitesPlano(Long empresaId) {
        DashboardResponseDTO.LimitesPlanoDTO limites = new DashboardResponseDTO.LimitesPlanoDTO();
        limites.setVeiculosUsados(veiculoRepository.countByEmpresaId(empresaId));
        limites.setMotoristasUsados(perfilMotoristaRepository.countByEmpresaId(empresaId));
        limites.setUsuariosUsados(usuarioRepository.countByEmpresaId(empresaId));
        return limites;
    }

    private List<DashboardResponseDTO.ChartDataDTO> buildFrotaPorStatus(Long empresaId) {
        // Mapeia os Enums para os Nomes e Cores do prompt
        Map<StatusVeiculo, DashboardResponseDTO.ChartDataDTO> statusMap = Map.of(
            StatusVeiculo.ATIVO, new DashboardResponseDTO.ChartDataDTO("Ativos", 0, "#22c55e"),
            StatusVeiculo.MANUTENCAO, new DashboardResponseDTO.ChartDataDTO("Em Manutenção", 0, "#f97316"),
            StatusVeiculo.INDISPONIVEL, new DashboardResponseDTO.ChartDataDTO("Indisponíveis", 0, "#ef4444"),
            StatusVeiculo.EXCLUIDO, new DashboardResponseDTO.ChartDataDTO("Excluídos", 0, "#6b7280") // Cor bônus
        );

        List<Object[]> results = veiculoRepository.countGroupByStatus(empresaId);
        for (Object[] result : results) {
            StatusVeiculo status = (StatusVeiculo) result[0];
            Long count = (Long) result[1];
            if (statusMap.containsKey(status)) {
                statusMap.get(status).setValue(count);
            }
        }
        return statusMap.values().stream()
                .filter(dto -> dto.getRawValue().longValue() > 0)
                .collect(Collectors.toList());
    }

    private List<DashboardResponseDTO.ChartDataDTO> buildCustosUltimosMeses(Long empresaId) {
        LocalDate dataInicio = LocalDate.now().minusMonths(MESES_GRAFICO_CUSTO - 1).withDayOfMonth(1);
        
        Map<String, BigDecimal> custosPorMes = manutencaoRepository
                .findCustosUltimosMeses(empresaId, dataInicio)
                .stream()
                .collect(Collectors.toMap(
                    ManutencaoCustoMesDTO::getMesAno,
                    ManutencaoCustoMesDTO::getCusto
                ));

        List<DashboardResponseDTO.ChartDataDTO> chartData = new ArrayList<>();
        Locale localePtBr = new Locale("pt", "BR");
        
        for (int i = MESES_GRAFICO_CUSTO - 1; i >= 0; i--) {
            LocalDate dataMes = LocalDate.now().minusMonths(i);
            String mesAnoChave = dataMes.format(DateTimeFormatter.ofPattern("YYYY-MM")); // "2025-11"
            String nomeMes = dataMes.getMonth().getDisplayName(TextStyle.SHORT, localePtBr); // "Nov"
            
            BigDecimal custo = custosPorMes.getOrDefault(mesAnoChave, BigDecimal.ZERO);
            
            String nomeMesCapitalizado = StringUtils.capitalize(nomeMes);
            
            chartData.add(new DashboardResponseDTO.ChartDataDTO(nomeMesCapitalizado, custo));
        }
        
        return chartData;
    }
}