package br.gov.mj.side.web.view.contrato.painel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.web.dto.ContratoDto;
import br.gov.mj.side.web.dto.EntidadePesquisaDto;
import br.gov.mj.side.web.service.FornecedorService;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.service.UfService;
import br.gov.mj.side.web.view.contrato.ContratoPage;
import br.gov.mj.side.web.view.fornecedor.FornecedorPage;

@AuthorizeInstantiation({ PanelDadosFornecedor.ROLE_MANTER_CONTRATO_VISUALIZAR, PanelDadosFornecedor.ROLE_MANTER_CONTRATO_INCLUIR,
		PanelDadosFornecedor.ROLE_MANTER_CONTRATO_ALTERAR, PanelDadosFornecedor.ROLE_MANTER_CONTRATO_EXCLUIR })
public class PanelDadosFornecedor extends Panel {

	private static final long serialVersionUID = 1L;

	public static final String ROLE_MANTER_CONTRATO_VISUALIZAR = "manter_contrato:visualizar";
	public static final String ROLE_MANTER_CONTRATO_INCLUIR = "manter_contrato:incluir";
	public static final String ROLE_MANTER_CONTRATO_ALTERAR = "manter_contrato:alterar";
	public static final String ROLE_MANTER_CONTRATO_EXCLUIR = "manter_contrato:excluir";

	private PanelInformacoesFornecedro panelInformacoesFornecedor;
	private PanelDadosDoFornecedorEscolhido panelDadosDoFornecedorEscolhido;
	private PanelAdicionarRepresentantePreposto panelAdicionarRepresentante;
	private PanelAdicionarRepresentantePreposto panelAdicionarPreposto;
	private PanelDropRepresentante panelDropRepresentante;
	private PanelDropPreposto panelDropPreposto;

	private DropDownChoice<Entidade> dropFornecedor;
	private AjaxSubmitLink buttonAdicionarRepresentante;
	private AjaxSubmitLink buttonAdicionarPreposto;
	private AjaxSubmitLink buttonOcultarAdicionarRepresentante;
	private AjaxSubmitLink buttonOcultarAdicionarPreposto;
	private TextField<String> fieldNomeEntidade;
	private InfraDropDownChoice<Uf> dropEstado;

	private Contrato contrato;
	private Page backPage;
	private Boolean cadastroNovo;
	private ContratoPage page;
	private ContratoDto contratoDto;
	private Pessoa pessoa;
	private Boolean mostrarBotaoAdicionarRepresentante = true;
	private Boolean mostrarBotaoAdicionarPreposto = true;
	private Boolean mostrarPanelAdicionarRepresentante = false;
	private Boolean mostrarPanelAdicionarPreposto = false;
	private Boolean mostrarBotaoCancelarAdicaoRepresentante = false;
	private Boolean mostrarBotaoCancelarAdicaoPreposto = false;
	private Boolean ativarBotaoAdicionarRepresentante = false;
	private Boolean ativarBotaoAdicionarPreposto = false;
	private Usuario usuarioLogado;

	private String razaoSocial;

	private PessoaEntidade prepostoEscolhido;
	private PessoaEntidade representanteEscolhido;

	private List<PessoaEntidade> listaDePessoasDesteFornecedor = new ArrayList<PessoaEntidade>();
	private List<PessoaEntidade> listaRepresentantes = new ArrayList<PessoaEntidade>();
	private List<PessoaEntidade> listaPreposto = new ArrayList<PessoaEntidade>();

	@Inject
	private FornecedorService fornecedorService;

	@Inject
	private GenericEntidadeService genericService;

	@Inject
	private ComponentFactory componentFactory;

	@Inject
	private UfService ufService;

	@Inject
	private GenericEntidadeService entidadeService;

	public PanelDadosFornecedor(String id, Page backPage, Contrato contrato, Boolean cadastroNovo, Usuario usuarioLogado) {
		super(id);
		setOutputMarkupId(true);
		this.backPage = backPage;
		this.contrato = contrato;
		this.cadastroNovo = cadastroNovo;
		this.usuarioLogado = usuarioLogado;

		initVariaveis();
		initComponents();
	}

	private class PanelInformacoesFornecedro extends WebMarkupContainer {

		private static final long serialVersionUID = 1L;

		public PanelInformacoesFornecedro(String id) {
			super(id);
			setOutputMarkupId(true);

			add(newDropDownFornecedor());
		}
	}

	public class PanelDropRepresentante extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelDropRepresentante(String id) {
			super(id);
			setOutputMarkupId(true);

			add(newDropDownRepresentante()); // dropDownRepresentante
			add(newButtonAdicionarRepresentante()); // btnAdicionarRepresentante
		}
	}

	public class PanelDropPreposto extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelDropPreposto(String id) {
			super(id);
			setOutputMarkupId(true);

			add(newDropDownPreposto()); // dropDownPreposto
			add(newButtonAdicionarPreposto()); // btnAdicionarPreposto
		}
	}

	private class PanelDadosDoFornecedorEscolhido extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelDadosDoFornecedorEscolhido(String id) {
			super(id);
			setOutputMarkupId(true);

			// FORNECEDOR
			add(newTextFieldRazaoSocial());
			add(newDropDownEstado());
			add(newTextFieldContato());
			add(newTextFieldTelefoneFornecedor());
		}

	}

	private void initVariaveis() {
		page = (ContratoPage) backPage;

		if (contrato != null && contrato.getId() != null) {
			setarListasDeRepresentantesEPreposto();
			setarRepresentanteEPrepostoSelecionado();

			PessoaEntidade pe1 = new PessoaEntidade();
			pe1.setPessoa(contrato.getRepresentanteLegal());
			representanteEscolhido = pe1;

			PessoaEntidade pe2 = new PessoaEntidade();
			pe2.setPessoa(contrato.getPreposto());
			prepostoEscolhido = pe2;
		}

	}

	private void initComponents() {

		add(panelInformacoesFornecedor = new PanelInformacoesFornecedro("panelInformacoesFornecedor"));
		add(panelDadosDoFornecedorEscolhido = new PanelDadosDoFornecedorEscolhido("panelDadosDoFornecedorEscolhido"));
		add(panelDropRepresentante = new PanelDropRepresentante("panelDropRepresentante"));
		add(panelDropPreposto = new PanelDropPreposto("panelDropPreposto"));

		add(panelAdicionarRepresentante = new PanelAdicionarRepresentantePreposto("panelAdicionarRepresentante", listaRepresentantes, listaPreposto,
				EnumTipoPessoa.REPRESENTANTE_LEGAL, page));
		panelAdicionarRepresentante.setVisible(false);

		add(panelAdicionarPreposto = new PanelAdicionarRepresentantePreposto("panelAdicionarPreposto", listaPreposto, listaRepresentantes, EnumTipoPessoa.PREPOSTO_FORNECEDOR, page));
		panelAdicionarPreposto.setVisible(false);
	}

	// CRIAÇÃO DOS COMPONENTES

	private DropDownChoice<Entidade> newDropDownFornecedor() {
		EntidadePesquisaDto entidadePesquisaDto = new EntidadePesquisaDto();
		Entidade entidade = new Entidade();
		entidade.setStatusEntidade(EnumStatusEntidade.ATIVA);
		entidadePesquisaDto.setEntidade(entidade);
		entidadePesquisaDto.setUsuarioLogado(getUsuarioLogado());

		List<Entidade> listaFornecedores = fornecedorService.buscarSemPaginacao(entidadePesquisaDto);
		dropFornecedor = new DropDownChoice<Entidade>("dropDownFornecedor", new PropertyModel<Entidade>(this, "contrato.fornecedor"), listaFornecedores,
				new ChoiceRenderer<Entidade>("cnpjENome"));
		dropFornecedor.setNullValid(true);
		actionDropDown(dropFornecedor);
		return dropFornecedor;
	}

	public DropDownChoice<PessoaEntidade> newDropDownRepresentante() {
		DropDownChoice<PessoaEntidade> drop = new DropDownChoice<PessoaEntidade>("dropDownRepresentante", new PropertyModel<PessoaEntidade>(this, "representanteEscolhido"),
				listaRepresentantes);
		drop.setChoiceRenderer(new ChoiceRenderer<PessoaEntidade>("pessoa.nomePessoa", "pessoa.id"));
		actionDropDown(drop);
		drop.setEnabled(true);
		drop.setOutputMarkupId(true);
		return drop;
	}

	public DropDownChoice<PessoaEntidade> newDropDownPreposto() {
		DropDownChoice<PessoaEntidade> drop = new DropDownChoice<PessoaEntidade>("dropDownPreposto", new PropertyModel<PessoaEntidade>(this, "prepostoEscolhido"), listaPreposto);
		drop.setChoiceRenderer(new ChoiceRenderer<PessoaEntidade>("pessoa.nomePessoa", "pessoa.id"));
		actionDropDown(drop);
		drop.setEnabled(true);
		drop.setOutputMarkupId(true);
		return drop;
	}

	public TextField<String> newTextFieldRazaoSocial() {
		fieldNomeEntidade = componentFactory.newTextField("razaoSocial", "Razão Social", false, new PropertyModel(this, "contrato.fornecedor.nomeEntidade"));
		fieldNomeEntidade.setOutputMarkupId(true);
		fieldNomeEntidade.add(StringValidator.maximumLength(200));
		fieldNomeEntidade.setEnabled(false);
		actionTextFieldNome(fieldNomeEntidade);
		return fieldNomeEntidade;
	}

	private InfraDropDownChoice<Uf> newDropDownEstado() {
		List<Uf> listaEstados = ufService.buscarTodos();
		dropEstado = componentFactory.newDropDownChoice("dropDownEstado", "Estado", false, "id", "nomeUf", new PropertyModel(this, "contrato.fornecedor.municipio.uf"),
				listaEstados, null);
		dropEstado.setOutputMarkupId(true);
		dropEstado.setEnabled(false);
		return dropEstado;
	}

	public TextField<String> newTextFieldContato() {
		TextField<String> fieldContato = componentFactory.newTextField("contato", "Contato", false, new PropertyModel(this, "contrato.fornecedor.nomeContato"));
		fieldContato.add(StringValidator.maximumLength(200));
		fieldContato.setOutputMarkupId(true);
		fieldContato.setEnabled(false);
		actionTextFieldNome(fieldContato);
		return fieldContato;
	}

	public TextField<String> newTextFieldTelefoneFornecedor() {
		TextField<String> fieldTelefone = componentFactory.newTextField("telefoneFornecedor", "Telefone Fornecedor", false, new PropertyModel(this,
				"contrato.fornecedor.numeroTelefone"));
		fieldTelefone.add(StringValidator.maximumLength(200));
		fieldTelefone.setOutputMarkupId(true);
		fieldTelefone.setEnabled(false);
		return fieldTelefone;
	}

	private AjaxSubmitLink newButtonAdicionarRepresentante() {
		buttonAdicionarRepresentante = new AjaxSubmitLink("btnAdicionarRepresentante") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				super.onSubmit(target, form);

				actionAdicionarRepresentante(target);
			}
		};
		buttonAdicionarRepresentante.setDefaultFormProcessing(false);
		buttonAdicionarRepresentante.setOutputMarkupId(true);
		buttonAdicionarRepresentante.setEnabled(ativarBotaoAdicionarRepresentante);
		buttonAdicionarRepresentante.setVisible(mostrarBotaoAdicionarRepresentante);
		return buttonAdicionarRepresentante;
	}

	private AjaxSubmitLink newButtonAdicionarPreposto() {
		buttonAdicionarPreposto = new AjaxSubmitLink("btnAdicionarPreposto") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				super.onSubmit(target, form);

				actionAdicionarPreposto(target);
			}
		};
		buttonAdicionarPreposto.setDefaultFormProcessing(false);
		buttonAdicionarPreposto.setOutputMarkupId(true);
		buttonAdicionarPreposto.setEnabled(ativarBotaoAdicionarPreposto);
		buttonAdicionarPreposto.setVisible(mostrarBotaoAdicionarPreposto);
		return buttonAdicionarPreposto;
	}

	// AÇÕES
	private void setarListasDeRepresentantesEPreposto() {
		listaDePessoasDesteFornecedor = genericService.buscarPessoa(contrato.getFornecedor());
		for (PessoaEntidade pe : listaDePessoasDesteFornecedor) {
			if (pe.getPessoa().getTipoPessoa() == EnumTipoPessoa.REPRESENTANTE_LEGAL && pe.getPessoa().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
				listaRepresentantes.add(pe);
			} else {
				if (pe.getPessoa().getTipoPessoa() == EnumTipoPessoa.PREPOSTO_FORNECEDOR && pe.getPessoa().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
					listaPreposto.add(pe);
				}
			}
		}
	}

	public void atualizarListaDeRepresentantesAposAdicionarNovoFornecedor() {

		listaRepresentantes.clear();
		listaPreposto.clear();
		for (PessoaEntidade pe : listaDePessoasDesteFornecedor) {
			if (pe.getPessoa().getTipoPessoa() == EnumTipoPessoa.REPRESENTANTE_LEGAL && pe.getPessoa().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
				listaRepresentantes.add(pe);
			} else {
				if (pe.getPessoa().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
					listaPreposto.add(pe);
				}
			}
		}
	}

	private void setarRepresentanteEPrepostoSelecionado() {
		PessoaEntidade pe = new PessoaEntidade();
		pe.setPessoa(contrato.getRepresentanteLegal());

		PessoaEntidade prep = new PessoaEntidade();
		prep.setPessoa(contrato.getPreposto());

		representanteEscolhido = pe;
		prepostoEscolhido = prep;

		ativarBotaoAdicionarRepresentante = true;
		ativarBotaoAdicionarPreposto = true;
	}

	public void actionAtualizarDrop(AjaxRequestTarget target, EnumTipoPessoa tipoPessoa) {

		if (tipoPessoa == EnumTipoPessoa.REPRESENTANTE_LEGAL) {
			panelDropRepresentante.addOrReplace(newDropDownRepresentante());

			panelAdicionarRepresentante.setVisible(false);
			mostrarBotaoAdicionarRepresentante = true;
			mostrarBotaoCancelarAdicaoRepresentante = false;

			atualizarPanelAdicionarRepresentante(target);
			target.appendJavaScript("atualizarDropDown();");
			target.add(panelDropRepresentante);
		} else {
			panelDropPreposto.addOrReplace(newDropDownPreposto());

			panelAdicionarPreposto.setVisible(false);
			mostrarBotaoAdicionarPreposto = true;
			mostrarBotaoCancelarAdicaoPreposto = false;

			atualizarPanelAdicionarPreposto(target);
			target.appendJavaScript("atualizarDropDown();");
			target.add(panelDropPreposto);
		}

	}

	private void actionAdicionarRepresentante(AjaxRequestTarget target) {
		setResponsePage(new FornecedorPage(null, backPage, contrato.getFornecedor(), false, true));
	}

	private void actionAdicionarPreposto(AjaxRequestTarget target) {
		setResponsePage(new FornecedorPage(null, backPage, contrato.getFornecedor(), false, true));
	}

	private void actionOcultarPanelAdicionarRepresentante(AjaxRequestTarget target) {
		panelAdicionarRepresentante.setVisible(false);

		mostrarBotaoAdicionarRepresentante = true;
		mostrarBotaoCancelarAdicaoRepresentante = false;

		atualizarPanelAdicionarRepresentante(target);
	}

	private void actionOcultarPanelAdicionarPreposto(AjaxRequestTarget target) {
		panelAdicionarPreposto.setVisible(false);

		mostrarBotaoAdicionarPreposto = true;
		mostrarBotaoCancelarAdicaoPreposto = false;

		atualizarPanelAdicionarPreposto(target);
	}

	public void actionOcultarPanel(AjaxRequestTarget target, EnumTipoPessoa tipoPessoa) {
		if (tipoPessoa == EnumTipoPessoa.REPRESENTANTE_LEGAL) {
			actionOcultarPanelAdicionarRepresentante(target);
		} else {
			actionOcultarPanelAdicionarPreposto(target);
		}
	}

	/*
	 * Esta ação será execultada sempre que clicar ou no botão de adicionar ou
	 * de cancelar adição de um novo representante
	 */
	private void atualizarPanelAdicionarRepresentante(AjaxRequestTarget target) {
		panelDropRepresentante.addOrReplace(newButtonAdicionarRepresentante());

		target.appendJavaScript("atualizarDropDown();");
		target.add(panelAdicionarRepresentante);
		target.add(panelDropRepresentante);
	}

	/*
	 * Esta ação será execultada sempre que clicar ou no botão de adicionar ou
	 * de cancelar adição de um novo preposto
	 */

	private void actionTextFieldNome(TextField field) {
		AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// setar no model
			}
		};
		field.add(onChangeAjaxBehavior);
	}

	private void atualizarPanelAdicionarPreposto(AjaxRequestTarget target) {
		panelDropPreposto.addOrReplace(newButtonAdicionarPreposto());

		target.appendJavaScript("atualizarDropDown();");
		target.add(panelAdicionarPreposto);
		target.add(panelDropPreposto);
	}

	private void actionDropDown(DropDownChoice dropPrograma) {
		dropPrograma.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {				
				atualizarPanelComDadosDoFornecedor(target);
			}
		});
	}

	private void atualizarPanelComDadosDoFornecedor(AjaxRequestTarget target) {

		List<PessoaEntidade> listaDePessoas = new ArrayList<PessoaEntidade>();

		// Retirar esta validação quando for mexer neste campo, esta aqui pois
		// esta dando um tilt.
		if (contrato == null || contrato.getFornecedor() == null) {
			// return;
		}

		if (contrato.getFornecedor() == null) {
			ativarBotaoAdicionarRepresentante = false;
			ativarBotaoAdicionarPreposto = false;
		} else {
			EntidadePesquisaDto busca = new EntidadePesquisaDto();
			busca.setEntidade(contrato.getFornecedor());
			listaDePessoas = entidadeService.buscarPessoa(contrato.getFornecedor());
			ativarBotaoAdicionarRepresentante = true;
			ativarBotaoAdicionarPreposto = true;
		}

		listaRepresentantes.clear();
		listaPreposto.clear();

		atualizarLista(listaDePessoas);

		panelDropRepresentante.addOrReplace(newDropDownRepresentante());
		panelDropRepresentante.addOrReplace(newButtonAdicionarRepresentante());

		panelDropPreposto.addOrReplace(newDropDownPreposto());
		panelDropPreposto.addOrReplace(newButtonAdicionarPreposto());

		panelDadosDoFornecedorEscolhido.addOrReplace(newTextFieldRazaoSocial());
		panelDadosDoFornecedorEscolhido.addOrReplace(newDropDownEstado());
		panelDadosDoFornecedorEscolhido.addOrReplace(newTextFieldTelefoneFornecedor());

		target.appendJavaScript("atualizaCssDropDown();");
		target.add(panelDadosDoFornecedorEscolhido);
		target.add(panelDropRepresentante);
		target.add(panelDropPreposto);
	}

	private void atualizarLista(List<PessoaEntidade> listaDePessoas) {
		for (PessoaEntidade pe : listaDePessoas) {
			if (pe.getPessoa().getTipoPessoa() == EnumTipoPessoa.REPRESENTANTE_LEGAL && pe.getPessoa().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
				listaRepresentantes.add(pe);
			} else {
				if (pe.getPessoa().getTipoPessoa() == EnumTipoPessoa.PREPOSTO_FORNECEDOR && pe.getPessoa().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
					listaPreposto.add(pe);
				}
			}
		}
	}

	public Usuario getUsuarioLogado() {
		return usuarioLogado;
	}

	public PessoaEntidade getPrepostoEscolhido() {
		return prepostoEscolhido;
	}

	public PessoaEntidade getRepresentanteEscolhido() {
		return representanteEscolhido;
	}

	public List<PessoaEntidade> getListaRepresentantes() {
		return listaRepresentantes;
	}

	public void setListaRepresentantes(List<PessoaEntidade> listaRepresentantes) {
		this.listaRepresentantes = listaRepresentantes;
	}

	public List<PessoaEntidade> getListaPreposto() {
		return listaPreposto;
	}

	public void setListaPreposto(List<PessoaEntidade> listaPreposto) {
		this.listaPreposto = listaPreposto;
	}

	public PanelDropRepresentante getPanelDropRepresentante() {
		return panelDropRepresentante;
	}

	public void setPanelDropRepresentante(PanelDropRepresentante panelDropRepresentante) {
		this.panelDropRepresentante = panelDropRepresentante;
	}

	public PanelDropPreposto getPanelDropPreposto() {
		return panelDropPreposto;
	}

	public void setPanelDropPreposto(PanelDropPreposto panelDropPreposto) {
		this.panelDropPreposto = panelDropPreposto;
	}

	public List<PessoaEntidade> getListaDePessoasDesteFornecedor() {
		return listaDePessoasDesteFornecedor;
	}

	public void setListaDePessoasDesteFornecedor(List<PessoaEntidade> listaDePessoasDesteFornecedor) {
		this.listaDePessoasDesteFornecedor = listaDePessoasDesteFornecedor;
	}

	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}

	public TextField<String> getFieldNomeEntidade() {
		return fieldNomeEntidade;
	}

	public void setFieldNomeEntidade(TextField<String> fieldNomeEntidade) {
		this.fieldNomeEntidade = fieldNomeEntidade;
	}

	public InfraDropDownChoice<Uf> getDropEstado() {
		return dropEstado;
	}

	public void setDropEstado(InfraDropDownChoice<Uf> dropEstado) {
		this.dropEstado = dropEstado;
	}

}
