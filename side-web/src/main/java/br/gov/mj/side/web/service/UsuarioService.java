package br.gov.mj.side.web.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.seg.entidades.Perfil;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.seg.entidades.UsuarioPerfil;
import br.gov.mj.seg.entidades.enums.EnumTipoUsuario;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.enums.EnumOrigemCadastro;
import br.gov.mj.side.entidades.enums.EnumPerfilUsuario;
import br.gov.mj.side.entidades.enums.EnumSiglaSistema;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.web.dao.SegurancaDAO;
import br.gov.mj.side.web.dao.UsuarioDAO;
import br.gov.mj.side.web.dto.UsuarioLDAPDto;
import br.gov.mj.side.web.util.SenhaPadraoUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UsuarioService {

    @Inject
    private UsuarioDAO usuarioDAO;

    @Inject
    private SegurancaDAO segurancaDAO;

    @Inject
    private EntityManager em;

    public Usuario alterarUsuarioMembroComissao(Pessoa pessoa, String usuarioLogado) {

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        if (pessoa == null) {
            ex.addErrorMessage("Pessoa inexistente.");
            throw ex;
        }

        if (pessoa.getUsuario() == null) {
            ex.addErrorMessage("Usuario inexistente.");
            throw ex;
        }

        Usuario usuarioParaMerge = em.find(Usuario.class, pessoa.getId());

        usuarioParaMerge.setEmail(pessoa.getEmail());
        usuarioParaMerge.setUsuarioAlteracao(usuarioLogado);
        usuarioParaMerge.setDataAlteracao(LocalDateTime.now());
        usuarioParaMerge.setNomeCompleto(pessoa.getNomePessoa());
        usuarioParaMerge.setPrimeiroNome(pessoa.getNomePessoa().split(" ")[0]);
        return usuarioDAO.alterarUsuario(usuarioParaMerge);

    }

    public Usuario incluirPerfilAoUsuario(Usuario usuario, EnumPerfilUsuario perfil, Pessoa pessoa) {

        BusinessException ex = new BusinessException();

        if (usuario == null) {
            ex.addErrorMessage("Usuario inexistente.");
            throw ex;
        }

        Usuario usuarioParaMerge = em.find(Usuario.class, usuario.getId());

        if (usuarioParaMerge == null) {
            usuarioParaMerge = incluirUsuario(pessoa, "", perfil);
            return usuarioParaMerge;
        } else {
            if (usuarioParaMerge == null || !possuiPerfil(usuarioParaMerge.getPerfis(), perfil)) {
                UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
                usuarioPerfil.setPerfil(segurancaDAO.buscarPerfil(perfil.getDescricao()));
                usuarioPerfil.setUsuario(usuarioParaMerge);
                usuarioParaMerge.getPerfis().add(usuarioPerfil);
                return usuarioDAO.alterarUsuario(usuarioParaMerge);
            } else {
                return usuarioParaMerge;
            }
        }
    }

    public boolean possuiPerfil(Set<UsuarioPerfil> perfis, EnumPerfilUsuario perfil) {

        for (UsuarioPerfil usuarioPerfil : perfis) {
            if (usuarioPerfil.getPerfil().getNomePerfil().equals(perfil.getDescricao())) {
                return true;
            }
        }
        return false;
    }

    public void removerPerfilDoUsuario(Usuario usuario, EnumPerfilUsuario perfil) {
        BusinessException ex = new BusinessException();
        if (usuario == null) {
            ex.addErrorMessage("Usuario inexistente.");
            throw ex;
        }
        Map<Long, UsuarioPerfil> mapa = new HashMap<Long, UsuarioPerfil>();
        for (UsuarioPerfil usuarioPerfil : usuario.getPerfis()) {
            Long chave = usuarioPerfil.getId();
            if (!usuarioPerfil.getPerfil().getNomePerfil().equals(perfil.getDescricao())) {
                mapa.put(chave, usuarioPerfil);
            }
        }
        Usuario usuarioParaMerge = em.find(Usuario.class, usuario.getId());
        usuarioParaMerge.getPerfis().clear();
        usuarioParaMerge.getPerfis().addAll(transformaMapEmListaUsuarioPerfil(mapa));
        usuarioDAO.alterarUsuario(usuarioParaMerge);

    }

    public void removerUsuario(Usuario usuario) {
        usuarioDAO.removerUsuario(usuario);
    }

    private List<UsuarioPerfil> transformaMapEmListaUsuarioPerfil(Map<Long, UsuarioPerfil> mapa) {
        List<UsuarioPerfil> lista = new ArrayList<UsuarioPerfil>();
        lista.addAll(mapa.values());
        return lista;
    }

    public Usuario incluirUsuario(Pessoa pessoa, String usuarioLogado, EnumPerfilUsuario perfil) {

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        if (pessoa == null) {
            ex.addErrorMessage("Pessoa inexistente.");
            throw ex;
        }

        Usuario usuario = new Usuario();
        usuario.setNumeroCpf(pessoa.getNumeroCpf());
        usuario.setLogin(pessoa.getNumeroCpf());
        usuario.setSituacaoUsuario(EnumStatusPessoa.ATIVO);
        usuario.setPossuiPrimeiroAcesso(Boolean.TRUE);
        usuario.setEmail(pessoa.getEmail());
        usuario.setUsuarioCadastro(usuarioLogado);
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setHashEnvioTrocaSenha(UUID.randomUUID().toString());
        usuario.setTipoUsuario(EnumTipoUsuario.EXTERNO);
        usuario.setNomeCompleto(pessoa.getNomePessoa());
        usuario.setPrimeiroNome(pessoa.getNomePessoa().split(" ")[0]);

        UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
        usuarioPerfil.setPerfil(segurancaDAO.buscarPerfil(perfil.getDescricao()));
        usuarioPerfil.setUsuario(usuario);
        usuario.getPerfis().add(usuarioPerfil);

        return usuarioDAO.cadastrarLogin(usuario);

    }

    public Usuario incluirUsuarioRepresentanteOuPreposto(Pessoa pessoa, String usuarioLogado, EnumOrigemCadastro origemCadastro, EnumPerfilUsuario perfilUsuario) {

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        if (pessoa == null) {
            ex.addErrorMessage("Pessoa inexistente.");
            throw ex;
        }

        Usuario usuario = new Usuario();
        usuario.setNumeroCpf(pessoa.getNumeroCpf());
        usuario.setLogin(pessoa.getNumeroCpf());
        usuario.setSituacaoUsuario(pessoa.getStatusPessoa());
        usuario.setPossuiPrimeiroAcesso(Boolean.TRUE);
        usuario.setEmail(pessoa.getEmail());
        usuario.setUsuarioCadastro(usuarioLogado);
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setHashEnvioTrocaSenha(UUID.randomUUID().toString());
        usuario.setTipoUsuario(EnumTipoUsuario.EXTERNO);
        usuario.setNomeCompleto(pessoa.getNomePessoa());
        usuario.setPrimeiroNome(pessoa.getNomePessoa().split(" ")[0]);

        if (origemCadastro.getValor().equals(EnumOrigemCadastro.CADASTRO_EXTERNO.getValor())) {
            usuario.setHashEnvioAlteraEntidade(UUID.randomUUID().toString());
        }

        UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
        usuarioPerfil.setPerfil(segurancaDAO.buscarPerfil(perfilUsuario.getDescricao()));
        usuarioPerfil.setUsuario(usuario);
        usuario.getPerfis().add(usuarioPerfil);

        return usuarioDAO.cadastrarLogin(usuario);

    }

    public Usuario incluirUsuarioInterno(UsuarioLDAPDto usuarioLDAPDto) {

        BusinessException ex = new BusinessException();

        if (usuarioLDAPDto == null) {
            ex.addErrorMessage("Usuário LDAP inexistente.");
            throw ex;
        }

        if (usuarioLDAPDto.getNumeroCpf() == null) {
            ex.addErrorMessage("Usuário autenticado pelo AD não possui número de cpf (CPF).");
            throw ex;
        }

        if (usuarioLDAPDto.getLogin() == null) {
            ex.addErrorMessage("Usuário autenticado pelo AD não possui login (sAMAccountName).");
            throw ex;
        }

        if (usuarioLDAPDto.getEmail() == null) {
            ex.addErrorMessage("Usuário autenticado pelo AD não possui e-mail (mail).");
            throw ex;
        }

        if (usuarioLDAPDto.getNomeCompleto() == null) {
            ex.addErrorMessage("Usuário autenticado pelo AD não possui nome completo (cn).");
            throw ex;
        }

        if (usuarioLDAPDto.getPrimeiroNome() == null) {
            ex.addErrorMessage("Usuário autenticado pelo AD não possui primeiro nome (givenName).");
            throw ex;
        }

        // if (usuarioLDAPDto.getLocalTrabalho() == null) {
        // ex.addErrorMessage("Usuário autenticado pelo AD não possui lotação (physicalDeliveryOfficeName).");
        // throw ex;
        // }
        //
        //
        // if (usuarioLDAPDto.getRamal() == null) {
        // ex.addErrorMessage("Usuário autenticado pelo AD não possui ramal (telephoneNumber).");
        // throw ex;
        // }

        Usuario usuario = new Usuario();
        usuario.setNumeroCpf(usuarioLDAPDto.getNumeroCpf());
        usuario.setLogin(usuarioLDAPDto.getLogin());
        usuario.setSituacaoUsuario(EnumStatusPessoa.ATIVO);
        usuario.setPossuiPrimeiroAcesso(Boolean.TRUE);
        usuario.setEmail(usuarioLDAPDto.getEmail());
        usuario.setUsuarioCadastro(EnumSiglaSistema.SIDE.getDescricao());
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setTipoUsuario(EnumTipoUsuario.INTERNO);
        usuario.setNomeCompleto(usuarioLDAPDto.getNomeCompleto());
        usuario.setPrimeiroNome(usuarioLDAPDto.getPrimeiroNome());
        return usuarioDAO.cadastrarLoginUsuarioInterno(usuario);
    }

    public Usuario resetarSenhaUsuario(Usuario usuario, String senha) {

        BusinessException ex = new BusinessException();

        if (usuario == null || StringUtils.isBlank(senha)) {
            ex.addErrorMessage("Usuário e senha são obrigatórios.");
            throw ex;
        }

        if (!validaPadraoSenha(senha)) {
            ex.addErrorMessage("Senha não atende aos padrões estabelecidos.");
            throw ex;
        }

        return usuarioDAO.resetarSenhaUsuario(usuario, senha);

    }

    public Usuario resetarExpiracaoData(Usuario usuario) {

        BusinessException ex = new BusinessException();

        if (usuario == null) {
            ex.addErrorMessage("Usuário obrigatório");
            throw ex;
        }
        return usuarioDAO.resetarExpiracaoData(usuario);

    }

    public Usuario prepararNovoHashParaSolicitacaoNovaSenha(Usuario usuario) {
        return usuarioDAO.prepararNovoHashParaSolicitacaoNovaSenha(usuario);
    }

    private boolean validaPadraoSenha(String senha) {
        return SenhaPadraoUtil.buildPadrao(false, true, true, 8, 16).validarSenha(senha);
    }

    public Usuario vincularPerfisUsuario(Usuario usuario, String usuarioLogado) {

        BusinessException ex = new BusinessException();
        if (usuario == null || usuario.getId() == null) {
            ex.addErrorMessage("Usuario inexistente.");
            throw ex;
        }

        return usuarioDAO.vincularPerfisUsuario(usuario, usuarioLogado);
    }

    public List<Usuario> buscarUsuariosInternos(Usuario usuario) {

        if (usuario == null) {
            usuario = new Usuario();
        }
        usuario.setTipoUsuario(EnumTipoUsuario.INTERNO);
        return usuarioDAO.buscar(usuario);
    }

    public List<Usuario> buscarUsuariosInternosPaginado(Usuario usuario, int first, int size) {

        if (usuario == null) {
            usuario = new Usuario();
        }

        usuario.setTipoUsuario(EnumTipoUsuario.INTERNO);
        return usuarioDAO.buscarPaginado(usuario, first, size);
    }

    public List<Usuario> buscarUsuariosInternosPaginadoOrdenado(Usuario usuario, int first, int size, EnumOrder order, String propertyOrder) {

        if (usuario == null) {
            usuario = new Usuario();
        }

        usuario.setTipoUsuario(EnumTipoUsuario.INTERNO);
        return usuarioDAO.buscarPaginadoOrdenado(usuario, first, size, order, propertyOrder);
    }

    public Long contarUsuariosInternosPaginado(Usuario usuario) {

        if (usuario == null) {
            usuario = new Usuario();
        }

        usuario.setTipoUsuario(EnumTipoUsuario.INTERNO);
        return usuarioDAO.contarPaginado(usuario);
    }

    public List<Perfil> buscarPerfisDoUsuario(Usuario usuario) {

        BusinessException ex = new BusinessException();
        if (usuario == null) {
            ex.addErrorMessage("Usuario inexistente.");
            throw ex;
        }

        List<Perfil> lista = new ArrayList<Perfil>();
        for (UsuarioPerfil usuarioPerfil : usuario.getPerfis()) {
            lista.add(usuarioPerfil.getPerfil());
        }
        return lista;
    }

}
