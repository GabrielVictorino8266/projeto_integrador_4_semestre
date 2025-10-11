package com.projetoanderson.app.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.projetoanderson.app.model.entity.Empresa;
import com.projetoanderson.app.model.entity.Funcao;
import com.projetoanderson.app.model.entity.Incidente;
import com.projetoanderson.app.model.entity.Manutencao;
import com.projetoanderson.app.model.entity.PerfilMotorista;
import com.projetoanderson.app.model.entity.Usuario;
import com.projetoanderson.app.model.entity.Veiculo;
import com.projetoanderson.app.model.entity.enums.Severidade;
import com.projetoanderson.app.model.entity.enums.StatusVeiculo;
import com.projetoanderson.app.model.entity.enums.TipoCNH;
import com.projetoanderson.app.model.entity.enums.TipoManutencao;
import com.projetoanderson.app.model.entity.enums.TipoVeiculo;
import com.projetoanderson.app.repository.EmpresaRepository;
import com.projetoanderson.app.repository.FuncaoRepository;
import com.projetoanderson.app.repository.IncidenteRepository;
import com.projetoanderson.app.repository.ManutencaoRepository;
import com.projetoanderson.app.repository.PerfilMotoristaRepository;
import com.projetoanderson.app.repository.UsuarioRepository;
import com.projetoanderson.app.repository.VeiculoRepository;

/**
 * Classe responsável por carregar dados iniciais no banco de dados.
 * Executa automaticamente na inicialização da aplicação.
 */
@Configuration
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    CommandLineRunner initDatabase(
            FuncaoRepository funcaoRepository,
            EmpresaRepository empresaRepository,
            UsuarioRepository usuarioRepository,
            PerfilMotoristaRepository perfilMotoristaRepository,
            VeiculoRepository veiculoRepository,
            IncidenteRepository incidenteRepository,
            ManutencaoRepository manutencaoRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            logger.info("Iniciando carga de dados...");

            // Verifica se já existem dados
            if (funcaoRepository.count() > 0) {
                logger.info("Dados já existem no banco. Pulando carga inicial.");
                return;
            }

            // 1. CRIAR FUNÇÕES
            logger.info("Criando funções...");
            Funcao funcaoAdmin = new Funcao();
            funcaoAdmin.setNome(Funcao.ROLE_ADMIN);
            funcaoAdmin = funcaoRepository.save(funcaoAdmin);

            Funcao funcaoMotorista = new Funcao();
            funcaoMotorista.setNome(Funcao.ROLE_MOTORISTA);
            funcaoMotorista = funcaoRepository.save(funcaoMotorista);

            // 2. CRIAR EMPRESAS
            logger.info("Criando empresas...");
            Empresa empresa1 = criarEmpresa("Transportadora Rápida Ltda", "Rápida Transportes", "12345678000190");
            empresa1 = empresaRepository.save(empresa1);

            Empresa empresa2 = criarEmpresa("Logística Express S.A.", "Express Log", "98765432000111");
            empresa2 = empresaRepository.save(empresa2);

            Empresa empresa3 = criarEmpresa("Frota Brasil Transportes ME", "Frota Brasil", "11223344000155");
            empresa3 = empresaRepository.save(empresa3);

            // 3. CRIAR USUÁRIOS
            logger.info("Criando usuários...");
            
            // Empresa 1 - Admins e Motoristas
            Usuario joao = criarUsuario("João Silva", "joao.silva@rapida.com.br", "12345678901", 
                    "1985-03-15", "11987654321", "admin123", passwordEncoder, empresa1);
            Set<Funcao> funcoesJoao = new HashSet<>();
            funcoesJoao.add(funcaoAdmin);
            joao.setFuncoes(funcoesJoao);
            joao = usuarioRepository.save(joao);

            Usuario carlos = criarUsuario("Carlos Santos", "carlos.santos@rapida.com.br", "23456789012",
                    "1990-07-22", "11976543210", "motorista456", passwordEncoder, empresa1);
            Set<Funcao> funcoesCarlos = new HashSet<>();
            funcoesCarlos.add(funcaoMotorista);
            carlos.setFuncoes(funcoesCarlos);
            carlos = usuarioRepository.save(carlos);

            Usuario ana = criarUsuario("Ana Costa", "ana.costa@rapida.com.br", "34567890123",
                    "1988-11-30", "11965432109", "gestor789", passwordEncoder, empresa1);
            Set<Funcao> funcoesAna = new HashSet<>();
            funcoesAna.add(funcaoAdmin);
            ana.setFuncoes(funcoesAna);
            ana = usuarioRepository.save(ana);

            // Empresa 2
            Usuario maria = criarUsuario("Maria Oliveira", "maria.oliveira@expresslog.com.br", "45678901234",
                    "1992-05-18", "21987654321", "carlos123", passwordEncoder, empresa2);
            Set<Funcao> funcoesMaria = new HashSet<>();
            funcoesMaria.add(funcaoAdmin);
            maria.setFuncoes(funcoesMaria);
            maria = usuarioRepository.save(maria);

            Usuario pedro = criarUsuario("Pedro Almeida", "pedro.almeida@expresslog.com.br", "56789012345",
                    "1987-09-25", "21976543210", "maria456", passwordEncoder, empresa2);
            Set<Funcao> funcoesPedro = new HashSet<>();
            funcoesPedro.add(funcaoMotorista);
            pedro.setFuncoes(funcoesPedro);
            pedro = usuarioRepository.save(pedro);

            // Empresa 3
            Usuario fernanda = criarUsuario("Fernanda Lima", "fernanda.lima@frotabrasil.com.br", "67890123456",
                    "1995-01-10", "31987654321", "joao789", passwordEncoder, empresa3);
            Set<Funcao> funcoesFernanda = new HashSet<>();
            funcoesFernanda.add(funcaoMotorista);
            fernanda.setFuncoes(funcoesFernanda);
            fernanda = usuarioRepository.save(fernanda);

            // 4. CRIAR PERFIS DE MOTORISTA
            logger.info("Criando perfis de motorista...");
            PerfilMotorista perfilCarlos = criarPerfilMotorista(TipoCNH.D, "12345678901", 9, carlos);
            perfilCarlos.setId(carlos.getId()); // Define o ID manualmente por causa do @MapsId
            perfilCarlos = perfilMotoristaRepository.save(perfilCarlos);

            PerfilMotorista perfilPedro = criarPerfilMotorista(TipoCNH.C, "23456789012", 10, pedro);
            perfilPedro.setId(pedro.getId()); // Define o ID manualmente por causa do @MapsId
            perfilPedro = perfilMotoristaRepository.save(perfilPedro);

            PerfilMotorista perfilFernanda = criarPerfilMotorista(TipoCNH.B, "34567890123", 8, fernanda);
            perfilFernanda.setId(fernanda.getId()); // Define o ID manualmente por causa do @MapsId
            perfilFernanda = perfilMotoristaRepository.save(perfilFernanda);

            // 5. CRIAR VEÍCULOS
            logger.info("Criando veículos...");
            
            // Empresa 1
            Veiculo veiculo1 = criarVeiculo("V001", "ABC1D23", TipoVeiculo.CAMINHAO, 2020, 
                    "Mercedes-Benz", 85000, 100000, StatusVeiculo.ATIVO, empresa1);
            veiculo1 = veiculoRepository.save(veiculo1);

            Veiculo veiculo2 = criarVeiculo("V002", "DEF4G56", TipoVeiculo.VAN, 2021,
                    "Fiat", 45000, 80000, StatusVeiculo.ATIVO, empresa1);
            veiculo2 = veiculoRepository.save(veiculo2);

            Veiculo veiculo3 = criarVeiculo("V003", "GHI7J89", TipoVeiculo.CARRO, 2022,
                    "Volkswagen", 25000, 60000, StatusVeiculo.MANUTENCAO, empresa1);
            veiculo3 = veiculoRepository.save(veiculo3);

            // Empresa 2
            Veiculo veiculo4 = criarVeiculo("V004", "JKL0M12", TipoVeiculo.CAMINHAO, 2019,
                    "Volvo", 120000, 150000, StatusVeiculo.ATIVO, empresa2);
            veiculo4 = veiculoRepository.save(veiculo4);

            Veiculo veiculo5 = criarVeiculo("V005", "NOP3Q45", TipoVeiculo.ONIBUS, 2021,
                    "Scania", 95000, 120000, StatusVeiculo.ATIVO, empresa2);
            veiculo5 = veiculoRepository.save(veiculo5);

            // Empresa 3
            Veiculo veiculo6 = criarVeiculo("V006", "RST6U78", TipoVeiculo.VAN, 2023,
                    "Renault", 15000, 70000, StatusVeiculo.ATIVO, empresa3);
            veiculo6 = veiculoRepository.save(veiculo6);

            // 6. CRIAR INCIDENTES
            logger.info("Criando incidentes...");
            incidenteRepository.save(criarIncidente("Pequeno arranhão no para-choque ao estacionar", 
                    Severidade.LEVE, perfilCarlos));
            
            incidenteRepository.save(criarIncidente("Freada brusca para evitar colisão", 
                    Severidade.MODERADO, perfilCarlos));
            
            incidenteRepository.save(criarIncidente("Excesso de velocidade detectado em rodovia", 
                    Severidade.GRAVE, perfilPedro));
            
            incidenteRepository.save(criarIncidente("Esqueceu de verificar espelhos antes de manobra", 
                    Severidade.LEVE, perfilFernanda));
            
            incidenteRepository.save(criarIncidente("Colisão leve em estacionamento", 
                    Severidade.MODERADO, perfilFernanda));
            
            incidenteRepository.save(criarIncidente("Dirigiu por 6 horas sem pausa obrigatória", 
                    Severidade.GRAVE, perfilCarlos));

            // 7. CRIAR MANUTENÇÕES
            logger.info("Criando manutenções...");
            
            // Veículo 1 - Caminhão Mercedes
            manutencaoRepository.save(criarManutencao("2024-01-15", "Troca de óleo e filtros", 
                    850.00, TipoManutencao.PREVENTIVA, veiculo1));
            manutencaoRepository.save(criarManutencao("2024-06-20", "Substituição de pastilhas de freio", 
                    1200.00, TipoManutencao.PREVENTIVA, veiculo1));
            manutencaoRepository.save(criarManutencao("2024-09-10", "Reparo no sistema de arrefecimento", 
                    2500.00, TipoManutencao.CORRETIVA, veiculo1));

            // Veículo 2 - Van Fiat
            manutencaoRepository.save(criarManutencao("2024-03-05", "Revisão dos 40.000 km", 
                    650.00, TipoManutencao.PREVENTIVA, veiculo2));
            manutencaoRepository.save(criarManutencao("2024-08-12", "Troca de pneus", 
                    1800.00, TipoManutencao.PREVENTIVA, veiculo2));

            // Veículo 3 - Carro VW (em manutenção)
            manutencaoRepository.save(criarManutencao("2024-10-01", "Reparo na transmissão", 
                    4500.00, TipoManutencao.CORRETIVA, veiculo3));

            // Veículo 4 - Caminhão Volvo
            manutencaoRepository.save(criarManutencao("2024-02-20", "Troca de óleo e filtros", 
                    950.00, TipoManutencao.PREVENTIVA, veiculo4));
            manutencaoRepository.save(criarManutencao("2024-07-15", "Manutenção do sistema de injeção", 
                    3200.00, TipoManutencao.CORRETIVA, veiculo4));

            // Veículo 5 - Ônibus Scania
            manutencaoRepository.save(criarManutencao("2024-04-10", "Revisão completa dos 90.000 km", 
                    5500.00, TipoManutencao.PREVENTIVA, veiculo5));

            // Veículo 6 - Van Renault
            manutencaoRepository.save(criarManutencao("2024-05-25", "Troca de óleo e filtros", 
                    450.00, TipoManutencao.PREVENTIVA, veiculo6));

            logger.info("Carga de dados concluída com sucesso!");
            logger.info("Total de empresas: {}", empresaRepository.count());
            logger.info("Total de usuários: {}", usuarioRepository.count());
            logger.info("Total de veículos: {}", veiculoRepository.count());
            logger.info("Total de incidentes: {}", incidenteRepository.count());
            logger.info("Total de manutenções: {}", manutencaoRepository.count());
        };
    }

    // Métodos auxiliares para criação de entidades
    
    private Empresa criarEmpresa(String razaoSocial, String nomeFantasia, String cnpj) {
        Empresa empresa = new Empresa();
        empresa.setRazaoSocial(razaoSocial);
        empresa.setNomeFantasia(nomeFantasia);
        empresa.setCnpj(cnpj);
        empresa.setAtivo(true);
        return empresa;
    }

    private Usuario criarUsuario(String nome, String email, String cpf, String dataNascimento,
                                 String telefone, String senha, PasswordEncoder passwordEncoder, Empresa empresa) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setCpf(cpf);
        usuario.setDataNascimento(LocalDate.parse(dataNascimento));
        usuario.setTelefone(telefone);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setEmpresa(empresa);
        usuario.setAtivo(true);
        return usuario;
    }

    private PerfilMotorista criarPerfilMotorista(TipoCNH tipoCnh, String numeroCnh, 
                                                  Integer desempenho, Usuario usuario) {
        PerfilMotorista perfil = new PerfilMotorista();
        perfil.setTipoCnh(tipoCnh);
        perfil.setNumeroCnh(numeroCnh);
        perfil.setDesempenho(desempenho);
        perfil.setUsuario(usuario);
        perfil.setAtivo(true);
        return perfil;
    }

    private Veiculo criarVeiculo(String numeroVeiculo, String placa, TipoVeiculo tipoVeiculo,
                                 Integer anoFabricacao, String marca, Integer kmAtual,
                                 Integer limiteAvisoKm, StatusVeiculo status, Empresa empresa) {
        Veiculo veiculo = new Veiculo();
        veiculo.setNumeroVeiculo(numeroVeiculo);
        veiculo.setPlaca(placa);
        veiculo.setTipoVeiculo(tipoVeiculo);
        veiculo.setAnoFabricacao(anoFabricacao);
        veiculo.setMarca(marca);
        veiculo.setKmAtual(kmAtual);
        veiculo.setLimiteAvisoKm(limiteAvisoKm);
        veiculo.setStatus(status);
        veiculo.setEmpresa(empresa);
        veiculo.setAtivo(true);
        return veiculo;
    }

    private Incidente criarIncidente(String descricao, Severidade severidade, PerfilMotorista perfilMotorista) {
        Incidente incidente = new Incidente();
        incidente.setDescricao(descricao);
        incidente.setSeveridade(severidade);
        incidente.setPerfilMotorista(perfilMotorista);
        incidente.setAtivo(true);
        return incidente;
    }

    private Manutencao criarManutencao(String dataManutencao, String descricao, Double custo,
                                       TipoManutencao tipoManutencao, Veiculo veiculo) {
        Manutencao manutencao = new Manutencao();
        manutencao.setDataManutencao(LocalDate.parse(dataManutencao));
        manutencao.setDescricao(descricao);
        manutencao.setCusto(BigDecimal.valueOf(custo));
        manutencao.setTipoManutencao(tipoManutencao);
        manutencao.setVeiculo(veiculo);
        manutencao.setAtivo(true);
        return manutencao;
    }
}
