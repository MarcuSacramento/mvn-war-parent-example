package br.gov.mj.side.web.view.seguranca;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.enums.EnumSiglaSistema;
import br.gov.mj.side.web.service.MailService;
import br.gov.mj.side.web.service.SegurancaService;
import br.gov.mj.side.web.service.UsuarioService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.LoginPage;
import br.gov.mj.side.web.view.template.TemplateExternalPage;

public class RecuperarSenhaPage extends TemplateExternalPage {

	private static final long serialVersionUID = 1L;

	@Inject
	private SegurancaService segurancaService;

	@Inject
	private UsuarioService usuarioService;

	@Inject
	private MailService mailService;

	@Inject
	private ComponentFactory componentFactory;

	private String email;
	private String cpf;
	private Usuario usuario;

	private boolean enviado = false;

	private Form<Void> form;
	private WebMarkupContainer mensagemSucessoContainer;

	public RecuperarSenhaPage(final PageParameters pageParameters) {
		super(pageParameters);
		setTitulo("");
		initComponents();
	}

	private void initComponents() {

		form = new Form<Void>("form");

		TextField<String> txCpf = componentFactory.newCpfTextField("cpf", "CPF", true, new LambdaModel<String>(this::getCpf, this::setCpf));
		txCpf.add(StringValidator.maximumLength(14));
		form.add(txCpf);

		TextField<String> txEmail = new TextField<String>("email", new LambdaModel<String>(this::getEmail, this::setEmail));
		txEmail.add(RfcCompliantEmailAddressValidator.getInstance());
		txEmail.setLabel(new Model<String>("Email"));
		txEmail.add(StringValidator.maximumLength(100));
		txEmail.setRequired(true);
		form.add(txEmail);

		Button btnConfirmar = newButtonConfirmar();
		form.add(btnConfirmar);
		form.setDefaultButton(btnConfirmar);

		mensagemSucessoContainer = new WebMarkupContainer("mensagemSucessoContainer");
		Label lblMensagemSucesso = new Label("lblMensagemSucesso", "Sua solicitação foi enviada com sucesso! Em instantes você receberá o acesso ao sistema no e-mail cadastrado.");
		mensagemSucessoContainer.add(lblMensagemSucesso);

		BookmarkablePageLink<Void> linkAcesso = new BookmarkablePageLink<Void>("linkAcesso", LoginPage.class);
		mensagemSucessoContainer.add(linkAcesso);
		mensagemSucessoContainer.setVisible(isEnviado());

		add(mensagemSucessoContainer);

		add(form);
	}

	public Button newButtonConfirmar() {
		AjaxButton btn = new AjaxButton("btnConfirmar") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				confirmar(target);
			}
		};
		return btn;
	}

	private void confirmar(AjaxRequestTarget target) {
		usuario = segurancaService.buscarUsuarioPeloEmailECpf(getEmail(), getCpf(), EnumSiglaSistema.SIDE.getValor());
		usuario = usuarioService.prepararNovoHashParaSolicitacaoNovaSenha(usuario);
		mailService.enviarEmailEsquecimentoSenha(usuario, getUrlBase(Constants.PAGINA_ALTERACAO_SENHA));
		setEnviado(true);
		form.setVisible(false);
		mensagemSucessoContainer.setVisible(isEnviado());
		target.add(mensagemSucessoContainer, form);
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCpf() {
		return cpf.replace(".", "").replace("-", "");
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public boolean isEnviado() {
		return enviado;
	}

	public void setEnviado(boolean enviado) {
		this.enviado = enviado;
	}

}
