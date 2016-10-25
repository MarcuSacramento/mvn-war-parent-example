package br.gov.mj.side.web.view.seguranca;

import java.util.Map;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.enums.EnumSiglaSistema;
import br.gov.mj.side.web.service.SegurancaService;
import br.gov.mj.side.web.service.UsuarioService;
import br.gov.mj.side.web.view.template.TemplatePage;

public class AlterarSenhaPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    @Inject
    private SegurancaService segurancaService;

    @Inject
    private UsuarioService usuarioService;

    private String senha;
    private String confirmaSenha;
    private Usuario usuario;

    private boolean permissaoAlterar = false;
    private boolean alterado = false;

    private Form<Void> form;
    private WebMarkupContainer mensagemSucessoContainer;

    public AlterarSenhaPage(final PageParameters pageParameters) {
        super(pageParameters);
        setTitulo("");

        // Removendo usuario da sessão, caso esteja autenticado
        if (AuthenticatedWebSession.get().isSignedIn()) {
            AuthenticatedWebSession.get().invalidate();
        }

        StringValue hash = getRequest().getRequestParameters().getParameterValue("hash");

        try {
            usuario = segurancaService.buscarUsuarioPeloHash(hash.toString(), EnumSiglaSistema.SIDE.getValor());
            setPermission(true);
        } catch (BusinessException e) {
            Map<String, Object[]> originalMessages = e.getErrorMessages();
            for (String originalMsg : originalMessages.keySet()) {
                addMsgError(originalMsg);
            }
        }

        initComponents();
    }

    private void initComponents() {

        form = new Form<Void>("form");

        PasswordTextField txSenha = new PasswordTextField("senha", new LambdaModel<String>(this::getSenha, this::setSenha));
        txSenha.setLabel(new Model<String>("Senha"));
        txSenha.add(StringValidator.maximumLength(16));
        txSenha.add(StringValidator.minimumLength(8));
        txSenha.setVisible(isPermission());
        form.add(txSenha);

        PasswordTextField txConfirmaSenha = new PasswordTextField("confirmaSenha", new LambdaModel<String>(this::getConfirmaSenha, this::setConfirmaSenha));
        txConfirmaSenha.setLabel(new Model<String>("Confirmar senha"));
        txConfirmaSenha.add(StringValidator.maximumLength(16));
        txConfirmaSenha.add(StringValidator.minimumLength(8));
        txConfirmaSenha.setVisible(isPermission());
        form.add(txConfirmaSenha);

        Button btnConfirmar = newButtonConfirmar();
        form.add(btnConfirmar);
        form.setDefaultButton(btnConfirmar);
        form.setVisible(isPermission() && !isAlterado());

        add(form);

        mensagemSucessoContainer = new WebMarkupContainer("mensagemSucessoContainer");
        Label lblMensagemSucesso = new Label("lblMensagemSucesso", "Sua senha foi cadastrada com sucesso!");
        mensagemSucessoContainer.add(lblMensagemSucesso);
       
        BookmarkablePageLink<Void> linkAcesso = new BookmarkablePageLink<Void>("linkAcesso", getApplication().getHomePage());
        mensagemSucessoContainer.add(linkAcesso);
        
        mensagemSucessoContainer.setVisible(isAlterado());
        add(mensagemSucessoContainer);
    }

    public Button newButtonConfirmar() {
        AjaxButton btn = new AjaxButton("btnConfirmar") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                confirmar(target);
            }
        };
        btn.setVisible(isPermission());
        return btn;
    }

    private void confirmar(AjaxRequestTarget target) {
        if (getSenha().equals(getConfirmaSenha())) {
            usuarioService.resetarSenhaUsuario(usuario, getSenha());
            setPermission(false);
            setAlterado(true);
            form.setVisible(false);
            mensagemSucessoContainer.setVisible(isAlterado());
            target.add(mensagemSucessoContainer, form);
        } else {
            addMsgError("A senha informada não é igual a senha confirmada");
        }

    }

    // Método sobrescrito para remover autenticação na página.
    @Override
    protected void onConfigure() {

    }

    // Método sobrescrito para remover o menu da página.
    @Override
    protected boolean isMenuVisible() {
        return false;
    }

    public boolean isPermission() {
        return permissaoAlterar;
    }

    public void setPermission(boolean permission) {
        this.permissaoAlterar = permission;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getConfirmaSenha() {
        return confirmaSenha;
    }

    public void setConfirmaSenha(String confirmaSenha) {
        this.confirmaSenha = confirmaSenha;
    }

    public boolean isAlterado() {
        return alterado;
    }

    public void setAlterado(boolean alterado) {
        this.alterado = alterado;
    }

}
