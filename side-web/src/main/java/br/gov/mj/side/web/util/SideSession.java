package br.gov.mj.side.web.util;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.seg.entidades.PerfilFuncionalidade;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.seg.entidades.UsuarioPerfil;
import br.gov.mj.seg.entidades.enums.EnumTipoUsuario;
import br.gov.mj.side.entidades.enums.EnumSiglaSistema;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.web.dto.UsuarioLDAPDto;
import br.gov.mj.side.web.service.SegurancaService;
import br.gov.mj.side.web.service.UsuarioService;
import br.gov.mj.side.web.service.ldap.LDAPService;

/**
 * Session personalizada onde deve ser implementada a lógica de autenticação.
 * 
 * @author Rodrigo Uchoa (rodrigo.uchoa@gmail.com)
 *
 */

public class SideSession extends AuthenticatedWebSession {

    private static final long serialVersionUID = 1L;

    private Roles roles = new Roles();
    private Usuario usuarioLogado;
    private static Log log = LogFactory.getLog(SideSession.class);

    @Inject
    private SegurancaService segurancaService;

    @Inject
    private UsuarioService usuarioService;

    @Inject
    private LDAPService ldapService;

    public SideSession(Request request) {
        super(request);
    }

    public static SideSession get() {
        Locale.setDefault(new Locale( "pt", "BR" ));
        return (SideSession) Session.get();
    }

    /**
     * Retorna objeto com as informações do usuário logado.
     * 
     * @return o objeto UsuarioLogado.
     */
    public Usuario getUsuarioLogado() {
        return this.usuarioLogado;
    }

    @Override
    public boolean authenticate(String username, String password) {

        if (log.isDebugEnabled()) {
            log.debug("Entering SideSession.authenticate(" + username + ")");
        }

        boolean authenticated = false;

        Usuario usuarioCadastrado = segurancaService.buscarUsuarioPeloLoginValidandoSenhaNula(username, password, EnumSiglaSistema.SIDE.getValor());
        Usuario usuario = null;
        UsuarioLDAPDto usuarioLDAPDto = null;

        if (usuarioCadastrado != null) {
            if (EnumTipoUsuario.INTERNO.equals(usuarioCadastrado.getTipoUsuario())) {
                usuarioLDAPDto = ldapService.buscarCredencial(username, password);
                if (usuarioLDAPDto != null) {
                    usuario = usuarioCadastrado;

                    BusinessException ex = new BusinessException();
                    if (usuario.getSituacaoUsuario().equals(EnumStatusPessoa.INATIVO)) {
                        ex.addErrorMessage("Usuário inativo. Contate o Administrador do Sistema");
                        throw ex;
                    }

                    authenticated = true;
                }
            } else if (EnumTipoUsuario.EXTERNO.equals(usuarioCadastrado.getTipoUsuario())) {
                usuario = segurancaService.buscarUsuarioPorLoginESenha(username, password, EnumSiglaSistema.SIDE.getValor());
                if (usuario != null) {
                    authenticated = true;
                }
            }
        } else {
            usuarioLDAPDto = ldapService.buscarCredencial(username, password);
            if (usuarioLDAPDto != null) {
                usuario = usuarioService.incluirUsuarioInterno(usuarioLDAPDto);
                authenticated = true;
            }

        }

        if (authenticated) {

            this.usuarioLogado = usuario;

            for (UsuarioPerfil usuarioPerfil : usuario.getPerfis()) {
                for (PerfilFuncionalidade perfilFuncionalidade : usuarioPerfil.getPerfil().getFuncionalidades()) {

                    this.roles.add(perfilFuncionalidade.getFuncionalidade().getTokenFuncionalidade());
                    if (log.isDebugEnabled()) {
                        log.debug("Adicionada role: '" + perfilFuncionalidade.getFuncionalidade().getTokenFuncionalidade() + "' para o usuário " + username);
                    }

                }

            }

        }

        return authenticated;

    }

    @Override
    public Roles getRoles() {
        return this.roles;
    }

    public boolean hasRole(String role) {
        return this.roles.hasRole(role);
    }

    public boolean hasAnyRole(List<String> roles) {
        Roles r = new Roles();
        r.addAll(roles);
        return this.roles.hasAnyRole(r);
    }

    public boolean hasAnyRole(String[] roles) {
        Roles r = new Roles();
        r.addAll(Arrays.asList(roles));
        return this.roles.hasAnyRole(r);
    }

    public boolean hasAllRoles(List<String> roles) {
        Roles r = new Roles();
        r.addAll(roles);
        return this.roles.hasAllRoles(r);
    }

    public boolean hasAllRoles(String[] roles) {
        Roles r = new Roles();
        r.addAll(Arrays.asList(roles));
        return this.roles.hasAllRoles(r);
    }

    @Override
    public void signOut() {
        super.signOut();
        this.roles = new Roles();
        this.usuarioLogado = null;
    }

}
