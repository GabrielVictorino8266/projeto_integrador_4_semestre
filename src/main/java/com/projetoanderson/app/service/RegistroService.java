// CRIE ESTE NOVO ARQUIVO:
// package com.projetoanderson.app.service;
package com.projetoanderson.app.service;

import com.projetoanderson.app.dto.RegistroClienteRequestDTO;
import com.projetoanderson.app.model.entity.Empresa;
import com.projetoanderson.app.model.entity.Funcao;
import com.projetoanderson.app.model.entity.Usuario;
import com.projetoanderson.app.repository.EmpresaRepository;
import com.projetoanderson.app.repository.FuncaoRepository;
import com.projetoanderson.app.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;

@Service
public class RegistroService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final FuncaoRepository funcaoRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistroService(EmpresaRepository empresaRepository,
                           UsuarioRepository usuarioRepository,
                           FuncaoRepository funcaoRepository,
                           PasswordEncoder passwordEncoder) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.funcaoRepository = funcaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional // Garante que tudo seja salvo ou nada
    public void registrarNovoCliente(RegistroClienteRequestDTO dto) {

        // 1. Validar Conflitos (CNPJ, CPF, Email)
        if (empresaRepository.existsByCnpj(dto.getCnpj())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Conflito: Já existe uma empresa cadastrada com o CNPJ " + formatarCnpj(dto.getCnpj()) + ".");
        }
        if (usuarioRepository.findByCpf(dto.getAdminCpf()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Conflito: Já existe um usuário cadastrado com o CPF " + dto.getAdminCpf() + ".");
        }
        if (usuarioRepository.findByEmail(dto.getAdminEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Conflito: Já existe um usuário cadastrado com o E-mail " + dto.getAdminEmail() + ".");
        }

        // 2. Criar a Empresa
        Empresa novaEmpresa = new Empresa();
        novaEmpresa.setRazaoSocial(dto.getRazaoSocial());
        novaEmpresa.setNomeFantasia(dto.getNomeFantasia());
        novaEmpresa.setCnpj(dto.getCnpj());
        novaEmpresa.setAtivo(true); // Ou false, se precisar de ativação por email
        Empresa empresaSalva = empresaRepository.save(novaEmpresa);

        // 3. Buscar a Função (Role) de Admin
        Funcao adminRole = funcaoRepository.findByNome(Funcao.ROLE_ADMIN)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Configuração: Função 'ROLE_ADMIN' não encontrada no banco."));

        // 4. Criar o Usuário Admin
        Usuario novoAdmin = new Usuario();
        novoAdmin.setNome(dto.getAdminNome());
        novoAdmin.setEmail(dto.getAdminEmail());
        novoAdmin.setCpf(dto.getAdminCpf());
        novoAdmin.setSenha(passwordEncoder.encode(dto.getAdminSenha()));
        novoAdmin.setDataNascimento(dto.getAdminDataNascimento());
        novoAdmin.setTelefone(dto.getAdminTelefone());
        novoAdmin.setAtivo(true); // Ou false, se precisar de ativação por email

        // 5. Associar as entidades
        novoAdmin.setEmpresa(empresaSalva);
        Set<Funcao> funcoes = new HashSet<>();
        funcoes.add(adminRole);
        novoAdmin.setFuncoes(funcoes);

        // 6. Salvar o Usuário (o @Transactional garante o commit)
        usuarioRepository.save(novoAdmin);

        // Opcional: Enviar email de boas-vindas ou de verificação aqui
    }

    // Método auxiliar (pode ficar aqui ou em uma classe utilitária)
    private String formatarCnpj(String cnpj) {
        if (cnpj == null || cnpj.length() != 14) return cnpj;
        return cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "." + cnpj.substring(5, 8) + "/" + cnpj.substring(8, 12) + "-" + cnpj.substring(12, 14);
    }
}