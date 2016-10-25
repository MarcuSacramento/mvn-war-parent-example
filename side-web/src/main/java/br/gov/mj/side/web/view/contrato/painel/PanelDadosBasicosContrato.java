package br.gov.mj.side.web.view.contrato.painel;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraLocalDateTextField;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.web.view.contrato.ContratoPage;

@AuthorizeInstantiation({ PanelDadosBasicosContrato.ROLE_MANTER_CONTRATO_VISUALIZAR, PanelDadosBasicosContrato.ROLE_MANTER_CONTRATO_INCLUIR,
		PanelDadosBasicosContrato.ROLE_MANTER_CONTRATO_ALTERAR, PanelDadosBasicosContrato.ROLE_MANTER_CONTRATO_EXCLUIR })
public class PanelDadosBasicosContrato extends Panel {

	private static final long serialVersionUID = 1L;

	public static final String ROLE_MANTER_CONTRATO_VISUALIZAR = "manter_contrato:visualizar";
	public static final String ROLE_MANTER_CONTRATO_INCLUIR = "manter_contrato:incluir";
	public static final String ROLE_MANTER_CONTRATO_ALTERAR = "manter_contrato:alterar";
	public static final String ROLE_MANTER_CONTRATO_EXCLUIR = "manter_contrato:excluir";

	private PanelInformacoesContrato panelInformacoesContrato;
	private Contrato contrato;
	private Page backPage;
	private boolean cadastroNovo;
	private ContratoPage page;
	private boolean readOnly;

	@Inject
	private ComponentFactory componentFactory;

	public PanelDadosBasicosContrato(String id, Page backPage, Contrato contrato, boolean cadastroNovo, boolean readOnly) {
		super(id);
		setOutputMarkupId(true);
		this.backPage = backPage;
		this.contrato = contrato;
		this.cadastroNovo = cadastroNovo;
		this.readOnly = readOnly;

		initVariaveis();
		initComponents();
	}

	private void initComponents() {
		panelInformacoesContrato = new PanelInformacoesContrato("panelInformacoesContrato");
		add(panelInformacoesContrato);
	}

	private void initVariaveis() {
		page = (ContratoPage) backPage;
		contrato = page.getForm().getModelObject();
	}

	private class PanelInformacoesContrato extends WebMarkupContainer {

		private static final long serialVersionUID = 1L;

		public PanelInformacoesContrato(String id) {
			super(id);
			setOutputMarkupId(true);

			add(getDateTextFieldVigenciaInicio());
			add(getDateTextFieldVigenciaFim());
			add(getTextFieldNumeroProcessoSEI());
		}

	}

	// Criação dos componentes do panel

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private InfraLocalDateTextField getDateTextFieldVigenciaInicio() {
		InfraLocalDateTextField field = componentFactory.newDateTextFieldWithDatePicker("dataVigenciaInicioContrato", "Vigência inicio", false, null, "dd/MM/yyyy", "pt-BR");
		field.setEnabled(!isReadOnly());
		return field;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private InfraLocalDateTextField getDateTextFieldVigenciaFim() {
		InfraLocalDateTextField field = componentFactory.newDateTextFieldWithDatePicker("dataVigenciaFimContrato", "Vigência fim", false, null, "dd/MM/yyyy", "pt-BR");
		field.setEnabled(!isReadOnly());
		return field;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TextField<String> getTextFieldNumeroProcessoSEI() {
		TextField<String> field = componentFactory.newTextField("numeroProcessoSEI", "NUP - SEI", false, new PropertyModel(this, "contrato.numeroProcessoSEI"));
		field.add(StringValidator.maximumLength(20));
		field.setEnabled(!isReadOnly());
		return field;
	}

	public void atualizaPanel(AjaxRequestTarget target, Contrato contrato) {
		panelInformacoesContrato.addOrReplace(getTextFieldNumeroProcessoSEI());
		target.add(panelInformacoesContrato);
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}

}
