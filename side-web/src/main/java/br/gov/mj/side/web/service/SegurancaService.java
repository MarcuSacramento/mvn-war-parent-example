package br.gov.mj.side.web.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.seg.entidades.Perfil;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.seg.entidades.UsuarioPerfil;
import br.gov.mj.seg.entidades.enums.EnumTipoUsuario;
import br.gov.mj.side.entidades.enums.EnumSiglaSistema;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.web.dao.SegurancaDAO;
import br.gov.mj.side.web.dto.UsuarioPessoaDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class SegurancaService {

    @Inject
    private SegurancaDAO segurancaDAO;

    public Usuario buscarUsuarioPorLoginESenha(String login, String senha, String siglaSistema) {

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(siglaSistema)) {
            ex.addErrorMessage("Sigla do sistema obrigatória");
            throw ex;
        }

        if (StringUtils.isBlank(login) || StringUtils.isBlank(senha)) {
            ex.addErrorMessage("Usuário e senha são obrigatórios.");
            throw ex;
        }

        Set<UsuarioPerfil> listaUsuarioPerfil = new HashSet<UsuarioPerfil>();
        Usuario usuario = segurancaDAO.buscarUsuario(login, senha);
        if (usuario != null) {

            setPerfisDoSistema(siglaSistema, listaUsuarioPerfil, usuario);

            if (usuario.getSituacaoUsuario().equals(EnumStatusPessoa.INATIVO)) {
                ex.addErrorMessage("Usuário inativo. Contate o Administrador do Sistema");
                throw ex;
            }

            if (usuario.getDataExpiracaoSenha().isBefore(LocalDate.now())) {
                ex.addErrorMessage("Senha expirada. Acesse o 'Esqueci minha senha' para solicitar uma nova Senha.");
                throw ex;
            }

        } else {
            ex.addErrorMessage("Usuário e/ou senha inválido(s).");
            throw ex;
        }

        return usuario;
    }

    private void setPerfisDoSistema(String siglaSistema, Set<UsuarioPerfil> listaUsuarioPerfil, Usuario usuario) {
        for (UsuarioPerfil usuarioPerfil : usuario.getPerfis()) {
            if (usuarioPerfil.getPerfil().getSistema().getSiglaSistema().equals(siglaSistema)) {
                listaUsuarioPerfil.add(usuarioPerfil);
            }
        }
        usuario.setPerfis(listaUsuarioPerfil);
    }

    public Usuario buscarUsuarioPeloHash(String hash, String siglaSistema) {

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(siglaSistema)) {
            ex.addErrorMessage("Sigla do sistema obrigatória");
            throw ex;
        }

        if (StringUtils.isBlank(hash)) {
            ex.addErrorMessage("String Hash de verificação obrigatória.");
            throw ex;
        }

        Set<UsuarioPerfil> listaUsuarioPerfil = new HashSet<UsuarioPerfil>();
        Usuario usuario = segurancaDAO.buscarUsuarioPeloHash(hash);
        if (usuario != null) {
            setPerfisDoSistema(siglaSistema, listaUsuarioPerfil, usuario);

            if (usuario.getSituacaoUsuario().equals(EnumStatusPessoa.INATIVO)) {
                ex.addErrorMessage("Usuário inativo. Contate o Administrador do Sistema");
                throw ex;
            }

            if (usuario.getDataLimiteTrocaSenha().isBefore(LocalDate.now())) {
                ex.addErrorMessage("Data limite para troca de senha expirada. Acesse o 'Esqueci minha senha' para solicitar uma nova Senha.");
                throw ex;
            }

        } else {
            ex.addErrorMessage("Hash de Autenticação inválido.");
            throw ex;
        }

        return usuario;
    }

    public Usuario buscarUsuarioPeloHashDeAlteracaoEntidade(String hash, String siglaSistema) {

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(siglaSistema)) {
            ex.addErrorMessage("Sigla do sistema obrigatória");
            throw ex;
        }

        if (StringUtils.isBlank(hash)) {
            ex.addErrorMessage("String Hash de verificação obrigatória.");
            throw ex;
        }

        Set<UsuarioPerfil> listaUsuarioPerfil = new HashSet<UsuarioPerfil>();
        Usuario usuario = segurancaDAO.buscarUsuarioPeloHashDeAlteracaoEntidade(hash);
        if (usuario != null) {
            setPerfisDoSistema(siglaSistema, listaUsuarioPerfil, usuario);
        } else {
            ex.addErrorMessage("Hash de Autenticação inválido.");
            throw ex;
        }

        return usuario;
    }

    public Usuario buscarUsuarioPeloEmail(String email, String siglaSistema) {

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(siglaSistema)) {
            ex.addErrorMessage("Sigla do sistema obrigatória");
            throw ex;
        }

        if (StringUtils.isBlank(email)) {
            ex.addErrorMessage("Email de verificação obrigatório.");
            throw ex;
        }

        Set<UsuarioPerfil> listaUsuarioPerfil = new HashSet<UsuarioPerfil>();
        Usuario usuario = segurancaDAO.buscarUsuarioPeloEmail(email);
        if (usuario != null) {
            setPerfisDoSistema(siglaSistema, listaUsuarioPerfil, usuario);

            if (usuario.getSituacaoUsuario().equals(EnumStatusPessoa.INATIVO)) {
                ex.addErrorMessage("Usuário inativo. Contate o Administrador do Sistema");
                throw ex;
            }

        } else {
            ex.addErrorMessage("Email de Autenticação inexistente.");
            throw ex;
        }

        return usuario;
    }

    public UsuarioPessoaDto buscarPessoaOuUsuarioComEmail(String email) {

        UsuarioPessoaDto dto = new UsuarioPessoaDto();

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(email)) {
            ex.addErrorMessage("Email de verificação obrigatório.");
            throw ex;
        }
        dto.setUsuario(segurancaDAO.buscarUsuarioPeloEmail(email));
        dto.setPessoa(segurancaDAO.buscarPessoaPeloEmail(email));
        return dto;
    }

    public Usuario buscarUsuarioPeloEmailECpf(String email, String numeroCpf, String siglaSistema) {

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(siglaSistema)) {
            ex.addErrorMessage("Sigla do sistema obrigatória");
            throw ex;
        }

        if (StringUtils.isBlank(email) || StringUtils.isBlank(numeroCpf)) {
            ex.addErrorMessage("Email e CPF de verificação obrigatórios.");
            throw ex;
        }

        Set<UsuarioPerfil> listaUsuarioPerfil = new HashSet<UsuarioPerfil>();
        Usuario usuario = segurancaDAO.buscarUsuarioPeloEmailECpf(email, numeroCpf);
        if (usuario != null) {
            setPerfisDoSistema(siglaSistema, listaUsuarioPerfil, usuario);

            if (usuario.getSituacaoUsuario().equals(EnumStatusPessoa.INATIVO)) {
                ex.addErrorMessage("Usuário inativo. Contate o Administrador do Sistema");
                throw ex;
            }

            if (usuario.getTipoUsuario().equals(EnumTipoUsuario.INTERNO)) {
                ex.addErrorMessage("Usuário autenticado pelo AD. Consulte o suporte de rede.");
                throw ex;
            }

        } else {
            ex.addErrorMessage("Email e/ou CPF  inexistentes.");
            throw ex;
        }

        return usuario;
    }

    public Usuario buscarUsuarioPeloLogin(String login, String siglaSistema) {

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(siglaSistema)) {
            ex.addErrorMessage("Sigla do sistema obrigatória");
            throw ex;
        }

        if (StringUtils.isBlank(login)) {
            ex.addErrorMessage("Login de Verificacao obrigatorio.");
            throw ex;
        }

        Usuario usuario = segurancaDAO.buscarUsuarioPeloLogin(login);
        return usuario;
    }

    public Usuario buscarUsuarioPeloLoginValidandoSenhaNula(String login, String senha, String siglaSistema) {

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(siglaSistema)) {
            ex.addErrorMessage("Sigla do sistema obrigatória");
            throw ex;
        }

        if (StringUtils.isBlank(login) || StringUtils.isBlank(senha)) {
            ex.addErrorMessage("Usuário e senha são obrigatórios.");
            throw ex;
        }

        Usuario usuario = segurancaDAO.buscarUsuarioPeloLogin(login);
        return usuario;
    }

    public Perfil buscarPerfil(String nomePerfil) {
        return segurancaDAO.buscarPerfil(nomePerfil);
    }

    public List<Perfil> buscarTodosPerfis(EnumTipoUsuario tipoUsuario) {
        return segurancaDAO.buscarTodosPerfis(EnumSiglaSistema.SIDE.getDescricao(), tipoUsuario);
    }

}
