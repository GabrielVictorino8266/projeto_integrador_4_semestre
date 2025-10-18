// CRIE ESTE NOVO ARQUIVO:
// package com.projetoanderson.app.controller;

package com.projetoanderson.app.controller;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional; // Importe

import com.projetoanderson.app.dto.AdminCriaUsuarioRequestDTO;
import com.projetoanderson.app.model.entity.Empresa;
import com.projetoanderson.app.model.entity.Funcao;
import com.projetoanderson.app.model.entity.Usuario;
import com.projetoanderson.app.repository.EmpresaRepository;
import com.projetoanderson.app.repository.FuncaoRepository;
import com.projetoanderson.app.repository.UsuarioRepository;

import jakarta.validation.Valid;

/**
 * Controlador REST para o Super Admin gerenciar usuários de outras empresas.
 */
@RestController
@RequestMapping("/api/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final FuncaoRepository funcaoRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUsuarioController(UsuarioRepository usuarioRepository,
                                  EmpresaRepository empresaRepository,
                                  FuncaoRepository funcaoRepository,
                                  PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.funcaoRepository = funcaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Cria o primeiro usuário (Admin) para uma empresa específica.
     * Requer a role 'SUPER_ADMIN'.
     */
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional // Garante que tudo seja salvo ou nada
    public ResponseEntity<String> criarAdminParaEmpresa(
            @Valid @RequestBody AdminCriaUsuarioRequestDTO dto) {

        // 1. Validar se o usuário já existe
        if (usuarioRepository.findByCpf(dto.getCpf()).isPresent() || 
            usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("CPF ou E-mail já cadastrado no sistema.");
        }

        // 2. Buscar a Empresa
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new RuntimeException("Empresa com ID " + dto.getEmpresaId() + " não encontrada."));

        // 3. Buscar a Função (Role) de Admin
        Funcao adminRole = funcaoRepository.findByNome(Funcao.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Função 'ROLE_ADMIN' não encontrada no banco."));

        // 4. Criar o novo Usuário
        Usuario novoAdmin = new Usuario();
        novoAdmin.setNome(dto.getNome());
        novoAdmin.setEmail(dto.getEmail());
        novoAdmin.setCpf(dto.getCpf());
        novoAdmin.setSenha(passwordEncoder.encode(dto.getSenha()));
        novoAdmin.setDataNascimento(dto.getDataNascimento());
        novoAdmin.setTelefone(dto.getTelefone());
        novoAdmin.setAtivo(true);
        
        // 5. Associar as entidades
        novoAdmin.setEmpresa(empresa);
        Set<Funcao> funcoes = new HashSet<>();
        funcoes.add(adminRole);
        novoAdmin.setFuncoes(funcoes);

        // 6. Salvar
        usuarioRepository.save(novoAdmin);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuário Admin criado com sucesso para a empresa: " + empresa.getNomeFantasia());
    }
}