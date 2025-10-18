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
 * (Versão com Super Admin).
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

            // --- ADICIONADO SUPER ADMIN ---
            Funcao funcaoSuperAdmin = new Funcao();
            funcaoSuperAdmin.setNome(Funcao.ROLE_SUPER_ADMIN);
            funcaoSuperAdmin = funcaoRepository.save(funcaoSuperAdmin);
            // --------------------------------

            // 2. CRIAR EMPRESAS
            logger.info("Criando empresas...");
            
            // --- ADICIONADO EMPRESA SISTEMA ---
            // Empresa especial para o Super Admin
            Empresa empresaSistema = criarEmpresa("Sistema Interno Admin", "Sistema", "00000000000000");
            empresaSistema = empresaRepository.save(empresaSistema);
            // -----------------------------------

            // Empresas de clientes
            Empresa empresa1 = criarEmpresa("Transportadora Rápida Ltda", "Rápida Transportes", "12345678000190");
            empresa1 = empresaRepository.save(empresa1);

            Empresa empresa2 = criarEmpresa("Logística Express S.A.", "Express Log", "98765432000111");
            empresa2 = empresaRepository.save(empresa2);

            // 3. CRIAR USUÁRIOS E PERFIS
            logger.info("Criando usuários...");

            // --- ADICIONADO USUÁRIO SUPER ADMIN ---
            Usuario superAdmin = criarUsuario("Super Admin", "superadmin@sistema.com", "00000000000",
                    "1990-01-01", "00000000000", "superadmin123", passwordEncoder, empresaSistema);
            Set<Funcao> funcoesSuperAdmin = new HashSet<>();
            funcoesSuperAdmin.add(funcaoSuperAdmin); // Role principal
            funcoesSuperAdmin.add(funcaoAdmin);      // Também é um admin
            superAdmin.setFuncoes(funcoesSuperAdmin);
            superAdmin = usuarioRepository.save(superAdmin);
            // ----------------------------------------

            // --- Usuários Clientes ---

            // Empresa 1 - Rápida Transportes (1 admin, 1 motorista)
            Usuario joao = criarUsuario("João Silva (Admin)", "joao.silva@rapida.com.br", "12345678901",
                    "1985-03-15", "11987654321", "admin123", passwordEncoder, empresa1);
            Set<Funcao> funcoesJoao = new HashSet<>();
            funcoesJoao.add(funcaoAdmin);
            joao.setFuncoes(funcoesJoao);
            joao = usuarioRepository.save(joao); // Admin não tem perfil, pode salvar direto.

            // --- Motorista Carlos (Empresa 1) ---
            Usuario carlos = criarUsuario("Carlos Santos (Motorista)", "carlos.santos@rapida.com.br", "23456789012",
                    "1990-07-22", "11976543210", "motorista456", passwordEncoder, empresa1);
            Set<Funcao> funcoesCarlos = new HashSet<>();
            funcoesCarlos.add(funcaoMotorista);
            carlos.setFuncoes(funcoesCarlos);
            // Cria o perfil ANTES de salvar o usuário
            PerfilMotorista perfilCarlos = criarPerfilMotorista(TipoCNH.D, "23456789012", 9, carlos);
            carlos.setPerfilMotorista(perfilCarlos); // Associa o perfil ao usuário
            carlos = usuarioRepository.save(carlos); // Salva o usuário (o perfil será salvo via cascade)


            // Empresa 2 - Express Log (1 admin, 1 motorista)
            Usuario maria = criarUsuario("Maria Oliveira (Admin)", "maria.oliveira@expresslog.com.br", "67890123456",
                    "1992-05-18", "21987654321", "carlos123", passwordEncoder, empresa2);
            Set<Funcao> funcoesMaria = new HashSet<>();
            funcoesMaria.add(funcaoAdmin);
            maria.setFuncoes(funcoesMaria);
            maria = usuarioRepository.save(maria); // Admin não tem perfil, pode salvar direto.

            // --- Motorista Pedro (Empresa 2) ---
            Usuario pedro = criarUsuario("Pedro Almeida (Motorista)", "pedro.almeida@expresslog.com.br", "78901234567",
                    "1987-09-25", "21976543210", "maria456", passwordEncoder, empresa2);
            Set<Funcao> funcoesPedro = new HashSet<>();
            funcoesPedro.add(funcaoMotorista);
            pedro.setFuncoes(funcoesPedro);
            // Cria o perfil ANTES de salvar o usuário
            PerfilMotorista perfilPedro = criarPerfilMotorista(TipoCNH.C, "78901234567", 8, pedro);
            pedro.setPerfilMotorista(perfilPedro); // Associa o perfil ao usuário
            pedro = usuarioRepository.save(pedro); // Salva o usuário (o perfil será salvo via cascade)
            

            // 5. CRIAR VEÍCULOS (2 por empresa cliente)
            logger.info("Criando veículos...");

            // Empresa 1 - Rápida Transportes
            Veiculo veiculo1 = criarVeiculo("V001", "ABC1D23", TipoVeiculo.CAMINHAO, 2020,
                    "Mercedes-Benz", 85000, 100000, StatusVeiculo.ATIVO, empresa1);
            veiculo1 = veiculoRepository.save(veiculo1);

            Veiculo veiculo2 = criarVeiculo("V002", "DEF4G56", TipoVeiculo.VAN, 2021,
                    "Fiat", 45000, 80000, StatusVeiculo.ATIVO, empresa1);
            veiculo2 = veiculoRepository.save(veiculo2);

            // Empresa 2 - Express Log
            Veiculo veiculo6 = criarVeiculo("V006", "PQR6S78", TipoVeiculo.CAMINHAO, 2020,
                    "Mercedes-Benz", 95000, 110000, StatusVeiculo.ATIVO, empresa2);
            veiculo6 = veiculoRepository.save(veiculo6);

            Veiculo veiculo7 = criarVeiculo("V007", "STU9V01", TipoVeiculo.VAN, 2021,
                    "Fiat", 55000, 90000, StatusVeiculo.ATIVO, empresa2);
            veiculo7 = veiculoRepository.save(veiculo7);


            // 6. CRIAR INCIDENTES (2 por motorista)
            logger.info("Criando incidentes...");

            incidenteRepository.save(criarIncidente("Pequeno arranhão no para-choque ao estacionar",
                    Severidade.LEVE, perfilCarlos));
            incidenteRepository.save(criarIncidente("Freada brusca para evitar colisão",
                    Severidade.MODERADO, perfilCarlos));

            incidenteRepository.save(criarIncidente("Excesso de velocidade detectado em rodovia",
                    Severidade.GRAVE, perfilPedro));
            incidenteRepository.save(criarIncidente("Colisão leve em estacionamento",
                    Severidade.MODERADO, perfilPedro));


            // 7. CRIAR MANUTENÇÕES (2 por veículo)
            logger.info("Criando manutenções...");

            // Veículo 1
            manutencaoRepository.save(criarManutencao("2024-01-15", "Troca de óleo e filtros",
                    850.00, TipoManutencao.PREVENTIVA, veiculo1));
            manutencaoRepository.save(criarManutencao("2024-06-20", "Substituição de pastilhas de freio",
                    1200.00, TipoManutencao.PREVENTIVA, veiculo1));

            // Veículo 2
            manutencaoRepository.save(criarManutencao("2024-02-10", "Troca de óleo e filtros",
                    450.00, TipoManutencao.PREVENTIVA, veiculo2));
            manutencaoRepository.save(criarManutencao("2024-07-15", "Substituição de pastilhas de freio",
                    800.00, TipoManutencao.PREVENTIVA, veiculo2));

            // Veículo 6
            manutencaoRepository.save(criarManutencao("2024-01-20", "Troca de óleo e filtros",
                    880.00, TipoManutencao.PREVENTIVA, veiculo6));
            manutencaoRepository.save(criarManutencao("2024-06-25", "Substituição de pastilhas de freio",
                    1250.00, TipoManutencao.PREVENTIVA, veiculo6));

            // Veículo 7
            manutencaoRepository.save(criarManutencao("2024-02-15", "Troca de óleo e filtros",
                    480.00, TipoManutencao.PREVENTIVA, veiculo7));
            manutencaoRepository.save(criarManutencao("2024-07-20", "Substituição de pastilhas de freio",
                    850.00, TipoManutencao.PREVENTIVA, veiculo7));


            logger.info("Carga de dados reduzida concluída com sucesso!");
            logger.info("Total de funções: {}", funcaoRepository.count());
            logger.info("Total de empresas: {}", empresaRepository.count());
            logger.info("Total de usuários: {}", usuarioRepository.count());
            logger.info("Total de perfis de motorista: {}", perfilMotoristaRepository.count());
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
        perfil.setUsuario(usuario); // Associa o usuário ao perfil
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