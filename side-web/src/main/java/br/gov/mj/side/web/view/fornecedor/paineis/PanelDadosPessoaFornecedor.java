package br.gov.mj.side.web.view.fornecedor.paineis;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.web.dto.ContratoDto;
import br.gov.mj.side.web.dto.EntidadePesquisaDto;
import br.gov.mj.side.web.dto.UsuarioPessoaDto;
import br.gov.mj.side.web.service.ContratoService;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.service.SegurancaService;
import br.gov.mj.side.web.util.CPFUtils;
import br.gov.mj.side.web.util.EmailValidator;
import br.gov.mj.side.web.util.MascaraUtils;
import br.gov.mj.side.web.view.fornecedor.FornecedorPage;
import br.gov.mj.side.web.view.planejarLicitacao.PlanejamentoLicitacaoPage;

/**
 * Painel preparado para exibir os dados do tipo Pessoa quando associado a
 * fornecedor, podendo ser acrescentado a qualquer pagina onde esses dados forem
 * manipulados.
 * 
 * 
 * @author diego.mota
 *
 */

@AuthorizeInstantiation({ PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_VISUALIZAR, PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_INCLUIR,
		PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_ALTERAR, PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_EXCLUIR })
public class PanelDadosPessoaFornecedor extends Panel {
	private static final long serialVersionUID = 1L;

	public static final String ROLE_MANTER_FORNECEDOR_VISUALIZAR = "manter_fornecedor:visualizar";
	public static final String ROLE_MANTER_FORNECEDOR_INCLUIR = "manter_fornecedor:incluir";
	public static final String ROLE_MANTER_FORNECEDOR_ALTERAR = "manter_fornecedor:alterar";
	public static final String ROLE_MANTER_FORNECEDOR_EXCLUIR = "manter_fornecedor:excluir";

	private PessoaEntidade pessoaEntidade;
	private List<PessoaEntidade> pessoas;
	private List<PessoaEntidade> segundaLista = new ArrayList<PessoaEntidade>();
	private List<PessoaEntidade> representantesVinculadosAContrato = new ArrayList<PessoaEntidade>();
	private Boolean readOnly = false;
	private Boolean deixarTodosPaineisHabilitados = true;
	private Boolean hideMainPanelWhenEmpty = false;
	private EnumTipoPessoa tipoPessoa;
	private Usuario usuarioLogado;
	private Page backPage;

	private boolean nomeObrigatorio;
	private boolean emailObrigatorio;
	private boolean telefoneObrigatorio;
	private boolean cpfObrigatorio;
	private Boolean ativo = true;
	private Boolean mostrarDropDownativar = false;

	// Variaveis para edição, visualização e exclusão de pessoas da lista
	private boolean modoEdicao = false;
	private Boolean botaoEditarClicado = false;
	private Boolean botaoAdicionarNovoClicado = true;
	private Boolean mostrarBotaoAdicionarPessoaNovo = true;
	private Boolean mostrarBotaoAdicionarPessoa = true;
	private Boolean mostrarBotaoEditarPessoa = false;
	private Boolean mostrarPainelComDadosParaCadastro = false;
	private Boolean acionarPainelComDadosParaCadastro = true;
	private Boolean mostrarCpfMascarado = false;
	private Boolean primeiraVezQueEditaFornecedor = true;
	private int posicaoPessoaLista;
	private String cpfTemporario = "";
	private String cpfTemporarioMascarado = "";
	private boolean mostrarBotaoCancelarEdicao = false;
	private String nomeTemp;
	private String telefoneTemp;
	private String emailTemp;
	private Long idTemp;

	// elementos do Wicket
	private PanelPrincipal panelPrincipal;
	private PanelBotoes panelBotoes;

	private Form<PessoaEntidade> formPessoa;
	private DataView<PessoaEntidade> dataViewPessoa;
	private PanelDataView panelDataView;
	private AjaxSubmitLink buttonCancelarEdicao;
	private AjaxSubmitLink buttonAdicionar;
	private AjaxSubmitLink buttonAdicionarNovo;
	private AjaxButton buttonSalvarEdicao;
	private DropDownChoice<Boolean> dropDownChoice;

	private Label labelMensagem;
	private Model<String> mensagem = Model.of("");
	private String msg = "";

	// ###################################################################################
	// injeçãod e dependencia
	@Inject
	private ComponentFactory componentFactory;

	@Inject
	private GenericEntidadeService genericEntidadeService;

	@Inject
	private ContratoService contratoService;

	@Inject
	private SegurancaService segurancaService;

	// ###################################################################################
	// conscructs, inits & destroiers

	/**
	 * Painel preparado para exibir os dados do tipo Pessoa, podendo ser
	 * acrescentado a qualquer pagina onde esses dados forem manipulados. Caso
	 * seja necessário acrescentar novos componetes os mesmos poderãos ser
	 * passados ao construtor, minimizando a necessidade de refatoração ou
	 * herança
	 * 
	 * @param id
	 * @param form
	 * @param entidade
	 * @param tituloDoPainel
	 * @param components
	 *            - passe aqui 0,1 ou N instancias de componentes que serão
	 *            acrescentadas ao final do painel
	 */
	public PanelDadosPessoaFornecedor(String id, List<PessoaEntidade> pessoas, EnumTipoPessoa tipoPessoa) {
		super(id);
		init(pessoas, null, tipoPessoa);
	}

	/**
	 * Painel preparado para exibir os dados do tipo Pessoa, podendo ser
	 * acrescentado a qualquer pagina onde esses dados forem manipulados. Caso
	 * seja necessário acrescentar novos componetes os mesmos poderãos ser
	 * passados ao construtor, minimizando a necessidade de refatoração ou
	 * herança
	 * 
	 * @param id
	 * @param form
	 * @param entidade
	 * @param tituloDoPainel
	 * @param components
	 *            - passe aqui 0,1 ou N instancias de componentes que serão
	 *            acrescentadas ao final do painel
	 */
	public PanelDadosPessoaFornecedor(String id, List<PessoaEntidade> pessoas, List<PessoaEntidade> segundaLista, Page backPage, String tituloDoPainel, EnumTipoPessoa tipoPessoa,
			Usuario usuarioLogado, Boolean readOnly, Boolean deixarTodosPaineisHabilitados) {
		super(id);
		this.readOnly = readOnly;
		this.deixarTodosPaineisHabilitados = deixarTodosPaineisHabilitados;
		this.segundaLista = segundaLista;
		this.usuarioLogado = usuarioLogado;
		this.backPage = backPage;

		init(pessoas, tituloDoPainel, tipoPessoa);

	}

	private void init(List<PessoaEntidade> pessoas, String tituloDoPainel, EnumTipoPessoa tipoPessoa) {
		setOutputMarkupId(true);
		this.pessoas = pessoas;
		this.tipoPessoa = tipoPessoa;
		pessoaEntidade = new PessoaEntidade();

		formPessoa = componentFactory.newForm("formPessoa", new CompoundPropertyModel<PessoaEntidade>(pessoaEntidade));

		setTodosCamposObrigatorios();

		formPessoa.add(newPanelPrincipal());
		formPessoa.add(panelBotoes = new PanelBotoes("panelBotoes"));
		formPessoa.add(newPanelDataView());

		// Mensagens de erro
		labelMensagem = new Label("mensagemErroRepresentante", mensagem);
		labelMensagem.setEscapeModelStrings(false);
		formPessoa.add(labelMensagem);
		add(formPessoa);

	}

	// ###################################################################################
	// classes privadas

	// paineis

	/**
	 * O sub-painel principal foi declarado como publico, assim pode ser
	 * manipulado diretamente a partir da pagina por meio do atributo
	 * panelPrincipal
	 * 
	 * @author eder.alves
	 *
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private TextField<String> getTextFieldCpf() {
		TextField<String> fieldNome = componentFactory.newTextField("pessoa.numeroCpf", "CPF", false, new PropertyModel(this, "cpfTemporario"));
		fieldNome.add(StringValidator.maximumLength(14));
		fieldNome.setEnabled(!mostrarCpfMascarado);
		return fieldNome;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private TextField<String> getTextFieldNome() {
		TextField<String> fieldNome = componentFactory.newTextField("pessoa.nomePessoa", "Nome", false, new PropertyModel(this, "nomeTemp"));
		fieldNome.add(StringValidator.maximumLength(100));
		return fieldNome;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private TextField<String> getTextFieldTelefone() {
		TextField<String> fieldNome = componentFactory.newTextField("pessoa.numeroTelefone", "Telefone", false, new PropertyModel(this, "telefoneTemp"));
		fieldNome.add(StringValidator.maximumLength(100));
		return fieldNome;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private TextField<String> getTextFieldEmail() {
		TextField<String> fieldNome = componentFactory.newTextField("pessoa.email", "Email", false, new PropertyModel(this, "emailTemp"));
		fieldNome.add(StringValidator.maximumLength(100));
		return fieldNome;
	}

	private boolean mostrarDropDownAtivar() {
		if (readOnly) {
			return false;
		} else {
			if (representantesVinculadosAContrato.contains(pessoaEntidade)) {
				return false;
			} else {
				return mostrarDropDownativar;
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private DropDownChoice<Boolean> getDropDownAtivarDesativarRepresentante() {
		dropDownChoice = new DropDownChoice<Boolean>("dropAtivo", new PropertyModel(this, "ativo"), Arrays.asList(Boolean.TRUE, Boolean.FALSE));
		dropDownChoice.setLabel(Model.of("Ativar Usuário"));
		dropDownChoice.setNullValid(false);
		dropDownChoice.setEnabled(mostrarDropDownAtivar());
		dropDownChoice.setChoiceRenderer(new IChoiceRenderer<Boolean>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object getDisplayValue(Boolean object) {
				if (object != null && object) {
					return "Sim";
				} else {
					return "Não";
				}
			}

			@Override
			public String getIdValue(Boolean object, int index) {
				return object.toString();
			}

		});

		dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			protected void onUpdate(AjaxRequestTarget target) {
				acaoDrop(target);
			}

		});

		return dropDownChoice;
	}

	public class PanelPrincipal extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelPrincipal() {
			super("panelPrincipal");
			setOutputMarkupId(true);
			setVisible(mostrarPainelComDadosParaCadastro);
			setEnabled(acionarPainelComDadosParaCadastro);

			add(getTextFieldCpf());
			add(getTextFieldNome());
			add(getTextFieldTelefone());
			add(getTextFieldEmail());
			add(getDropDownAtivarDesativarRepresentante());
		}
	}

	public class PanelBotoes extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelBotoes(String id) {
			super(id);
			setOutputMarkupId(true);
			add(getButtonCancelar());
			add(getButtonAdicionar());
			add(getButtonAdicionarNovo());
			add(getButtonSalvarEdicao());
		}
	}

	/**
	 * O sub-painel da data view foi declarado como publico, assim pode ser
	 * manipulado diretamente a partir da pagina por meio do atributo
	 * panelDataView
	 * 
	 * @author eder.alves
	 *
	 */
	private class PanelDataView extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelDataView() {
			super("panelDataView");
			add(newDataViewPessoa());
			add(new InfraAjaxPagingNavigator("pagination", dataViewPessoa));
		}
	}

	// ###################################################################################
	// criação e configuração de componentes
	/**
	 * Metodo generico para boteção de um componente TextField configurado
	 * 
	 * @param type
	 *            - Define o tipo de dados a ser recebido pelo textField, ex:
	 *            String, Integer, long, etc.
	 * @param id
	 *            - Nome do componetne a ser referenciado por meio do wicket:id
	 *            no html
	 * @param label
	 *            - Label a ser exibido
	 * @param required
	 * @param model
	 *            - ao receber true ira instanciar um PropertyModel configurado
	 *            para o tipo PessoaEntidade com o mesmo identificador passado
	 *            no atributo id
	 * @param maxLength
	 * @param converter
	 *            - recebe um conversor. 'null para anular'
	 * @param behaviors
	 * @return TextField<String>
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> TextField<T> getTextField(Class<T> type, String id, String label, Boolean required, Boolean model, Integer maxLength, IConverter converter, Behavior... behaviors) {

		TextField<T> text = new TextField<T>(id) {
			private static final long serialVersionUID = 1L;

			@Override
			public <C> IConverter<C> getConverter(java.lang.Class<C> type) {
				if (converter != null) {
					return converter;
				} else {
					return super.getConverter(type);
				}
			};
		};
		if (model) {
			text.setModel((IModel<T>) new PropertyModel<PessoaEntidade>(pessoaEntidade, id));
		}
		text.setRequired(required);
		text.setLabel(new ResourceModel(label, label));

		for (Behavior b : behaviors) {
			text.add(b);
		}
		text.add(StringValidator.maximumLength(maxLength));

		return text;
	}

	/**
	 * Metodo generico para recupera ruma instancia configurada de um dropDow.
	 * 
	 * @param type
	 * @param id
	 * @param label
	 * @param required
	 * @param idExpression
	 *            - Atributo do elemento selecionado a server como identificador
	 * @param labelExpression
	 *            - Atributo do elemento selecionado a ser exibido.
	 * @param model
	 * @param choices
	 * @param behaviors
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> DropDownChoice<T> getDropDown(Class<T> type, String id, String label, boolean required, String idExpression, String labelExpression, Boolean model, List choices,
			Behavior... behaviors) {

		InfraDropDownChoice<T> drop = componentFactory.newDropDownChoice(id, label, required, idExpression, labelExpression, model ? (new PropertyModel<PessoaEntidade>(
				pessoaEntidade, id)) : null, choices, null, behaviors);
		drop.setNullValid(true);
		drop.setOutputMarkupId(true);

		return drop;
	}

	/**
	 * Monta e configura a dataView para exibição de pessoass
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private DataView newDataViewPessoa() {
		dataViewPessoa = new DataView<PessoaEntidade>("dataTitulares", new ListDataProvider(pessoas != null ? pessoas : new ArrayList<PessoaEntidade>())) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<PessoaEntidade> item) {
				item.clearOriginalDestination();
				item.add(new Label("pessoa.nomePessoa", item.getModelObject().getPessoa().getNomePessoa()));
				item.add(new Label("pessoa.numeroCpf", CPFUtils.format(item.getModelObject().getPessoa().getNumeroCpf())));
				item.add(new Label("pessoa.numeroTelefone", item.getModelObject().getPessoa().getNumeroTelefone()));
				item.add(new Label("pessoa.email", item.getModelObject().getPessoa().getEmail()));
				item.add(new Label("situacao", item.getModelObject().getPessoa().getStatusPessoa().getDescricao()));
				item.add(getButtonExcluir(item)); // btnRemover
				item.add(getButtonEditar(item)); // btnEditar
			}
		};
		return dataViewPessoa;
	}

	/**
	 * retorna uma instancia de AjaxSubmitLink configuarada para remover um item
	 * da dataView
	 * 
	 * @return
	 */
	public InfraAjaxConfirmButton getButtonExcluir(Item<PessoaEntidade> item) {
		InfraAjaxConfirmButton buttonExcluir = componentFactory.newAJaxConfirmButton("btnRemover", "MSG001", null, (target, formz) -> remover(target, item));
		buttonExcluir.setEnabled(acionarComponente());
		authorize(buttonExcluir, RENDER, ROLE_MANTER_FORNECEDOR_EXCLUIR);
		boolean mostrarBotao = item.getModelObject().getId() == null;
		buttonExcluir.setVisible(mostrarBotao);
		return buttonExcluir;
	}

	public AjaxSubmitLink getButtonCancelar() {
		buttonCancelarEdicao = new AjaxSubmitLink("btnCancelarEdicao") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				cancelarEdicao(target);
			}
		};
		buttonCancelarEdicao.setDefaultFormProcessing(false);
		buttonCancelarEdicao.setVisible(mostrarBotaoCancelarEdicao);
		buttonCancelarEdicao.setOutputMarkupId(true);
		return buttonCancelarEdicao;
	}

	private AjaxSubmitLink getButtonAdicionar() {
		buttonAdicionar = new AjaxSubmitLink("btnAdicionar") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				super.onSubmit(target, form);

				adicionar(target);
			}
		};
		buttonAdicionar.setOutputMarkupId(true);
		buttonAdicionar.setEnabled(true);
		buttonAdicionar.setVisible(mostrarBotaoAdicionar());
		authorize(buttonAdicionar, RENDER, ROLE_MANTER_FORNECEDOR_INCLUIR);

		return buttonAdicionar;
	}

	// Este botão é parecido com o botão acima
	// Só que ele ira apenas tornar o painel com
	// Os dados basicos visiveis
	private AjaxSubmitLink getButtonAdicionarNovo() {
		buttonAdicionarNovo = new AjaxSubmitLink("btnAdicionarNovo") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				super.onSubmit(target, form);
				adicionarNovo(target);
			}
		};
		buttonAdicionarNovo.setOutputMarkupId(true);
		buttonAdicionarNovo.add(getLabelBotao());
		buttonAdicionarNovo.setEnabled(true);
		buttonAdicionarNovo.setVisible(mostrarBotaoAdicionarNovo());
		authorize(buttonAdicionar, RENDER, ROLE_MANTER_FORNECEDOR_INCLUIR);
		return buttonAdicionarNovo;
	}

	private Label getLabelBotao() {
		return new Label("lblNome", "Adicionar " + tipoPessoa.getDescricao());
	}

	public AjaxButton getButtonSalvarEdicao() {
		buttonSalvarEdicao = new AjaxButton("btnSalvarEdicao") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				salvarEdicao(target);
			}
		};
		buttonSalvarEdicao.setVisible(mostrarBotaoEditarPessoa);
		buttonSalvarEdicao.setOutputMarkupId(true);
		authorize(buttonSalvarEdicao, RENDER, ROLE_MANTER_FORNECEDOR_ALTERAR);
		return buttonSalvarEdicao;
	}

	// Botões do DataView
	private AjaxSubmitLink getButtonEditar(Item<PessoaEntidade> item) {
		AjaxSubmitLink button = new AjaxSubmitLink("btnEditar") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				acaoVisualizarEditar(target, item, false);
			}
		};
		button.setDefaultFormProcessing(false);
		button.setEnabled(acionarComponente());
		authorize(button, RENDER, ROLE_MANTER_FORNECEDOR_ALTERAR);
		return button;
	}

	public AjaxSubmitLink getButtonVisualizar(Item<PessoaEntidade> item, boolean modoVisualizar) {
		AjaxSubmitLink button = new AjaxSubmitLink("btnVisualizar") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				acaoVisualizarEditar(target, item, modoVisualizar);
			}
		};
		button.setDefaultFormProcessing(false);
		return button;
	}

	private PanelDataView newPanelDataView() {
		panelDataView = new PanelDataView();

		return panelDataView;
	}

	/**
	 * Retorna uma instancia configura de PainelPrincipal
	 * 
	 * @return
	 */
	private PanelPrincipal newPanelPrincipal() {
		panelPrincipal = new PanelPrincipal();
		panelPrincipal.setEnabled(acionarPanelPrincipal());
		panelPrincipal.setVisible(mostrarPainelComDadosParaCadastro);

		return panelPrincipal;
	}

	/**
	 * Exibe os dados apenas para leitura
	 * 
	 * @param target
	 * @param item
	 */
	@SuppressWarnings("unused")
	private void visualizar(AjaxRequestTarget target, Item<PessoaEntidade> item) {
		pessoaEntidade = item.getModelObject();

		if (hideMainPanelWhenEmpty) {
			panelPrincipal.setVisible(true);
			buttonCancelarEdicao.setVisible(true);
		}
		formPessoa.addOrReplace(panelPrincipal);

		target.add(formPessoa);

	}

	/**
	 * Cancela a visuzalização ou edição de uma pessoa, limpando o painel
	 * 
	 * @param target
	 */
	@SuppressWarnings("unused")
	private void cancelar(AjaxRequestTarget target) {
		pessoaEntidade = new PessoaEntidade();
		formPessoa.addOrReplace(newPanelPrincipal());
		if (hideMainPanelWhenEmpty) {
			buttonCancelarEdicao.setVisible(false);
		}
		target.add(formPessoa);
	}

	private void adicionarNovo(AjaxRequestTarget target) {
		mostrarPainelComDadosParaCadastro = true;
		botaoAdicionarNovoClicado = true;
		panelPrincipal.setVisible(true);
		buttonAdicionarNovo.setVisible(false);
		buttonAdicionar.setVisible(true);
		buttonCancelarEdicao.setVisible(true);
		mostrarDropDownativar = false;
		mostrarDropDownativar = false;
		ativo = true;

		panelPrincipal.addOrReplace(getDropDownAtivarDesativarRepresentante());

		target.add(panelBotoes);
		target.add(panelPrincipal);
	}

	private void adicionar(AjaxRequestTarget target) {
		if (pessoas == null) {
			pessoas = new ArrayList<PessoaEntidade>();
		}

		msg = "";
		if (validarCamposObrigatorios()) {
			pessoaEntidade = new PessoaEntidade();
			pessoaEntidade.setPessoa(new Pessoa());

			pessoaEntidade.getPessoa().setNomePessoa(nomeTemp);
			pessoaEntidade.getPessoa().setNumeroCpf(CPFUtils.clean(cpfTemporario));
			pessoaEntidade.getPessoa().setNumeroTelefone(MascaraUtils.limparFormatacaoMascara(telefoneTemp));
			pessoaEntidade.getPessoa().setEmail(emailTemp);

			// compementa o preenchimento de campos obrigatórios
			pessoaEntidade.getPessoa().setTipoPessoa(tipoPessoa);
			pessoaEntidade.getPessoa().setStatusPessoa(EnumStatusPessoa.ATIVO);
			pessoaEntidade.getPessoa().setPossuiFuncaoDeRepresentante(false);
			pessoaEntidade.getPessoa().setDataInicioExercicio(LocalDate.now());
			pessoaEntidade.getPessoa().setDescricaoCargo(tipoPessoa.getDescricao());

			boolean validar = true;

			boolean validarCpf = validarCpfCompleto(validar);
			boolean validarEmail = validarEmailCompleto();

			if (!validarCpf || !validarEmail) {
				mensagem.setObject(msg);
				target.add(labelMensagem);
				return;
			}

			pessoas.add(pessoaEntidade);
			pessoaEntidade = new PessoaEntidade();
			formPessoa.setModelObject(pessoaEntidade);
			panelPrincipal = new PanelPrincipal();

			mostrarPainelComDadosParaCadastro = false;

			panelPrincipal.setVisible(false);
			buttonAdicionar.setVisible(false);
			buttonAdicionarNovo.setVisible(true);
			buttonCancelarEdicao.setVisible(false);
			mostrarPainelComDadosParaCadastro = false;
			ativo = true;

			msg = "";
			mensagem.setObject(msg);
			target.add(labelMensagem);

			zerarVariaveis();
			atualizarInputs(target);
			atualizarPaineis(target);

			panelDataView = new PanelDataView();
		}
		mensagem.setObject(msg);
		target.add(labelMensagem);
	}

	private void atualizarPaineis(AjaxRequestTarget target) {
		panelPrincipal.addOrReplace(getTextFieldCpf());
		panelPrincipal.addOrReplace(getTextFieldNome());
		panelPrincipal.addOrReplace(getTextFieldTelefone());
		panelPrincipal.addOrReplace(getTextFieldEmail());
		panelPrincipal.addOrReplace(getDropDownAtivarDesativarRepresentante());

		formPessoa.addOrReplace(panelDataView);

		panelBotoes.addOrReplace(getButtonAdicionar());
		panelBotoes.addOrReplace(getButtonCancelar());
		panelBotoes.addOrReplace(getButtonSalvarEdicao());
		panelBotoes.addOrReplace(getButtonAdicionarNovo());

		target.add(panelBotoes);
		target.add(formPessoa);
		target.add(panelPrincipal);
	}

	private void zerarVariaveis() {
		cpfTemporario = "";
		nomeTemp = "";
		telefoneTemp = "";
		emailTemp = "";
	}

	/**
	 * Remove um item do data view
	 * 
	 * @param target
	 * @param item
	 */
	private void remover(AjaxRequestTarget target, Item<PessoaEntidade> item) {
		pessoas.remove(item.getModelObject());
		formPessoa.addOrReplace(newPanelDataView());

		target.add(formPessoa);
	}

	/**
	 * Valida os campos obrigatorios de acorod com aqueles que foram informados
	 */
	private boolean validarCamposObrigatorios() {
		boolean retorno = true;

		if (cpfTemporario == null || "".equalsIgnoreCase(cpfTemporario)) {
			msg += "<p><li> O Campo 'CPF' é obrigatório.</li><p />";
			retorno = false;
		}
		if (nomeTemp == null || "".equalsIgnoreCase(nomeTemp)) {
			msg += "<p><li> O Campo 'Nome' é obrigatório.</li><p />";
			retorno = false;
		}
		if (emailTemp == null || "".equalsIgnoreCase(emailTemp)) {
			msg += "<p><li> O Campo 'E-Mail' é obrigatório.</li><p />";
			retorno = false;
		}

		if (telefoneTemp == null || "".equalsIgnoreCase(telefoneTemp)) {
			msg += "<p><li> O Campo 'Telefone' é obrigatório.</li><p />";
			retorno = false;
		} else {
			int tamanhoTelefoneTitular = MascaraUtils.limparFormatacaoMascara(telefoneTemp).length();
			if (tamanhoTelefoneTitular < 10) {
				msg += "<p><li> O Telefone do Titular deverá conter ao menos 10 números.</li><p />";
				retorno = false;
			}
		}
		return retorno;
	}

	// AÇÕES
	/**
	 * Habilita a opção de esconder o painel principal quando não possuir dados
	 * a serem exibidos. Re-instancia o painel principal
	 */

	private void buscarContratosDesteFornecedor() {

		if (primeiraVezQueEditaFornecedor) {

			FornecedorPage page = (FornecedorPage) backPage;
			Entidade fornecedor = page.getFornecedor();

			/*
			 * Caso o fornecedor seja igual a nulo então é um cadastro novo e
			 * não existem contratos vinculados
			 */
			if (fornecedor == null || fornecedor.getId() == null) {
				return;
			}

			ContratoDto contrato = new ContratoDto();
			List<Contrato> lista = contratoService.buscarSemPaginacao(contrato);
			List<Contrato> listaContratosDesteFornecedor = new ArrayList<Contrato>();

			// Irá encontrar todos os contratos deste fornecedor
			for (Contrato ct : lista) {
				if (ct.getFornecedor().getId().intValue() == fornecedor.getId().intValue()) {
					listaContratosDesteFornecedor.add(ct);
				}
			}

			// Irá pegar os contratos desta entidade e listar as pessoas que já
			// estão vinculadas
			for (Contrato ct2 : listaContratosDesteFornecedor) {
				for (PessoaEntidade pe : pessoas) {
					if (tipoPessoa == EnumTipoPessoa.REPRESENTANTE_LEGAL && pe.getPessoa().getId().intValue() == ct2.getRepresentanteLegal().getId().intValue()) {
						representantesVinculadosAContrato.add(pe);
						break;
					}

					if (tipoPessoa == EnumTipoPessoa.PREPOSTO_FORNECEDOR && pe.getPessoa().getId().intValue() == ct2.getPreposto().getId().intValue()) {
						representantesVinculadosAContrato.add(pe);
						break;
					}
				}
			}
		}
		primeiraVezQueEditaFornecedor = false;
	}

	private void acaoDrop(AjaxRequestTarget target) {
		ativo = dropDownChoice.getModelObject();
	}

	private void salvarEdicao(AjaxRequestTarget target) {
		int index = 0;

		if (!validarEditarPessoa(target)) {
			return;
		}

		for (PessoaEntidade pe : pessoas) {
			if (index == posicaoPessoaLista) {
				pe.getPessoa().setNomePessoa(nomeTemp);
				pe.getPessoa().setNumeroCpf(CPFUtils.clean(cpfTemporario));
				pe.getPessoa().setNumeroTelefone(MascaraUtils.limparFormatacaoMascara(telefoneTemp));
				pe.getPessoa().setEmail(emailTemp);

				EnumStatusPessoa statusPessoa = ativo ? EnumStatusPessoa.ATIVO : EnumStatusPessoa.INATIVO;
				pe.getPessoa().setStatusPessoa(statusPessoa);
				break;
			}
			index++;
		}
		pessoaEntidade = new PessoaEntidade();

		mostrarBotaoAdicionarPessoaNovo = true;
		panelPrincipal.setVisible(false);
		buttonAdicionar.setVisible(false);
		buttonAdicionarNovo.setVisible(true);
		buttonCancelarEdicao.setVisible(false);
		buttonSalvarEdicao.setVisible(false);
		mostrarBotaoEditarPessoa = false;
		mostrarBotaoCancelarEdicao = false;
		mostrarBotaoAdicionarPessoa = false;
		mostrarPainelComDadosParaCadastro = false;
		botaoAdicionarNovoClicado = true;
		mostrarCpfMascarado = false;

		zerarVariaveis();
		atualizarPaineis(target);
	}

	private void cancelarEdicao(AjaxRequestTarget target) {
		pessoaEntidade = new PessoaEntidade();

		buttonAdicionar.setVisible(false);
		buttonAdicionarNovo.setVisible(true);
		buttonCancelarEdicao.setVisible(false);
		buttonSalvarEdicao.setVisible(false);
		mostrarBotaoEditarPessoa = false;
		mostrarBotaoCancelarEdicao = false;
		mostrarBotaoAdicionarPessoa = false;
		panelPrincipal.setVisible(false);
		mostrarPainelComDadosParaCadastro = false;
		botaoAdicionarNovoClicado = true;
		mostrarCpfMascarado = false;
		acionarPainelComDadosParaCadastro = true;
		mostrarBotaoAdicionarPessoaNovo = true;
		mostrarDropDownativar = true;

		msg = "";
		mensagem.setObject(msg);
		target.add(labelMensagem);

		zerarVariaveis();
		atualizarInputs(target);
		atualizarPaineis(target);
	}

	private void acaoVisualizarEditar(AjaxRequestTarget target, Item<PessoaEntidade> item, Boolean modoVisualizar) {
		modoEdicao = true;

		pessoaEntidade = item.getModelObject();

		nomeTemp = pessoaEntidade.getPessoa().getNomePessoa();
		telefoneTemp = pessoaEntidade.getPessoa().getNumeroTelefone();
		emailTemp = pessoaEntidade.getPessoa().getEmail();
		idTemp = pessoaEntidade.getPessoa().getId();

		boolean isAtivo = pessoaEntidade.getPessoa().getStatusPessoa().getValor().equalsIgnoreCase("A") ? true : false;
		ativo = isAtivo;

		cpfTemporario = pessoaEntidade.getPessoa().getNumeroCpf();
		cpfTemporarioMascarado = CPFUtils.format(cpfTemporario);
		posicaoPessoaLista = item.getIndex();
		botaoEditarClicado = true;
		mostrarDropDownativar = true;
		botaoAdicionarNovoClicado = true;
		mostrarBotaoAdicionarPessoaNovo = false;

		if (modoVisualizar) {

			panelPrincipal.setVisible(true);
			acionarPainelComDadosParaCadastro = false;
			buttonAdicionar.setVisible(false);
			buttonAdicionarNovo.setVisible(false);
			buttonCancelarEdicao.setVisible(true);
			buttonSalvarEdicao.setVisible(false);
			mostrarPainelComDadosParaCadastro = true;
			mostrarBotaoEditarPessoa = false;
			mostrarBotaoCancelarEdicao = true;
			mostrarBotaoAdicionarPessoa = false;
			mostrarCpfMascarado = true;

		} else {

			panelPrincipal.setVisible(true);
			acionarPainelComDadosParaCadastro = true;
			buttonAdicionar.setVisible(false);
			buttonAdicionarNovo.setVisible(false);
			buttonCancelarEdicao.setVisible(true);
			buttonSalvarEdicao.setVisible(true);
			mostrarPainelComDadosParaCadastro = true;
			mostrarBotaoEditarPessoa = true;
			mostrarBotaoCancelarEdicao = true;
			mostrarBotaoAdicionarPessoa = false;

			if (pessoaEntidade.getId() == null) {
				mostrarCpfMascarado = false;
			} else {
				mostrarCpfMascarado = true;
			}

		}
		/*
		 * Executado somente a primeira vez que clicar no botão de editar, irá
		 * buscar todos os contratos e as pessoas que estão vinculadas a este
		 * contrato Qualquer representante vinculado a qualquer contrato não
		 * poderá ser desabilitado, esta verificação será feita justamente para
		 * encontrar os representantes que não poderão ser desabilitados.
		 */

		buscarContratosDesteFornecedor();
		atualizarInputs(target);
	}

	private void atualizarInputs(AjaxRequestTarget target) {
		formPessoa.addOrReplace(panelDataView);
		formPessoa.addOrReplace(newPanelPrincipal());
		panelBotoes.addOrReplace(getButtonAdicionarNovo());
		panelBotoes.addOrReplace(getButtonAdicionar());
		panelBotoes.addOrReplace(getButtonCancelar());
		panelBotoes.addOrReplace(getButtonSalvarEdicao());

		target.add(formPessoa);
		target.add(panelBotoes);
		target.add(panelPrincipal);
	}

	public boolean validarCpfCompleto(boolean validar) {

		String cpf = cpfTemporario;

		if (cpf == null || "".equalsIgnoreCase(cpf)) {
			msg += "<p><li> O Campo CPF é obrigatório.</li><p />";
			validar = false;
		} else {
			if (compararCpfsCadastradosDosDoisPaineis(cpf)) {

				if (!verificarSeCpfJaCadastrado(cpf)) {
					validar = false;
				}

				if (validar) {
					int i = 0;
					int contador = 0;
					for (PessoaEntidade b : pessoas) {
						String cpfLista = CPFUtils.clean(b.getPessoa().getNumeroCpf());

						if (CPFUtils.clean(cpf).equalsIgnoreCase(cpfLista)) {
							if (contador != posicaoPessoaLista) {
								msg += "<p><li> O 'CPF' informado já esta em uso.</li><p />";
								validar = false;
								i++;
							}
						}
						contador++;
					}

					if (i > 0) {
						validar = false;
					} else {
						validar = validarCPf(cpf, validar);
					}
				}
			} else {
				validar = false;
			}
		}
		return validar;
	}

	public boolean validarCPf(String cpf, boolean validar) {
		boolean valido = validar;
		if (CPFUtils.clean(cpf).length() < 11) {
			msg += "<p><li> O 'CPF' deverá conter 11 digitos.</li><p />";
			valido = false;
		} else {
			if (!CPFUtils.validate(cpf)) {
				msg += "<p><li> O 'CPF' informado esta em um formato inválido.</li><p />";
				valido = false;
			}
		}
		return valido;
	}

	private boolean verificarSeCpfJaCadastrado(String cpf) {

		boolean valido = true;
		Pessoa pessoa = new Pessoa();
		pessoa.setNumeroCpf(CPFUtils.clean(cpf));
		EntidadePesquisaDto entiRepresentante = new EntidadePesquisaDto();
		entiRepresentante.setPesquisarTodos(true);
		entiRepresentante.setTodos(pessoa);

		entiRepresentante.setUsuarioLogado(usuarioLogado);
		List<Entidade> lista = genericEntidadeService.buscarSemPaginacao(entiRepresentante);
		if (lista.size() > 0) {
			if (pessoaEntidade.getId() == null) {
				valido = false;
			} else {

				// Ira verificar se a pessoa que esta editando este CPF é a
				// propria pessoa.
				boolean mesmaPessoaEditando = false;

				List<PessoaEntidade> listaPessoas = lista.get(0).getPessoas();
				for (PessoaEntidade pe : listaPessoas) {
					if (pessoaEntidade.getPessoa().getId().intValue() == pe.getPessoa().getId().intValue()) {
						mesmaPessoaEditando = true;
						break;
					}
				}

				if (!mesmaPessoaEditando) {
					valido = false;
				}
			}
		}

		if (!valido) {
			msg += "<p><li> O 'CPF' informado já esta em uso.</li><p />";
		}
		return valido;
	}

	private boolean compararCpfsCadastradosDosDoisPaineis(String cpf) {

		Boolean cpfUnico = true;
		for (PessoaEntidade entidadeExterna : segundaLista) {
			if (CPFUtils.clean(entidadeExterna.getPessoa().getNumeroCpf()).equalsIgnoreCase(CPFUtils.clean(cpf))) {

				String tipoPessoa = entidadeExterna.getPessoa().getTipoPessoa().getDescricao();

				msg += "<p><li> O CPF informado já esta cadastrado na lista de " + tipoPessoa + ".</li><p />";
				cpfUnico = false;
				break;
			}
			if (!cpfUnico) {
				break;
			}
		}
		return cpfUnico;
	}

	private boolean validarEmailCompleto() {
		String email = emailTemp;

		if (!EmailValidator.validate(email)) {
			msg += "<p><li> O 'E-Mail' informado esta em um formato inválido.</li><p />";
			return false;
		}

		if (!verificarSeEmailJaCadastrado()) {
			return false;
		}

		if (!compararEmailsCadastradosDosDoisPaineis()) {
			return false;
		}

		if (!verificarSeEmailJaCadastradoEmBanco()) {
			return false;
		}
		return true;
	}

	private boolean compararEmailsCadastradosDosDoisPaineis() {

		String email = pessoaEntidade.getPessoa().getEmail();
		boolean validar = true;
		for (PessoaEntidade entidadeExterna : segundaLista) {
			if (entidadeExterna.getPessoa().getEmail().equalsIgnoreCase(email)) {

				String tipoPessoa = entidadeExterna.getPessoa().getTipoPessoa().getDescricao();

				msg += "<p><li> O E-Mail informado já esta cadastrado na lista de " + tipoPessoa + ".</li><p />";
				validar = false;
				break;
			}
			if (!validar) {
				break;
			}
		}
		return validar;
	}

	private boolean verificarSeEmailJaCadastrado() {

		boolean valido = true;

		int contador = 0;
		for (PessoaEntidade entidade : pessoas) {
			if (entidade.getPessoa().getEmail().equalsIgnoreCase(emailTemp)) {
				if (contador != posicaoPessoaLista) {
					msg += "<p><li> O 'E-Mail' já esta em uso.</li><p />";
					valido = false;
					break;
				}
			}
			contador++;
		}
		return valido;
	}

	private boolean verificarSeEmailJaCadastradoEmBanco() {

		boolean valido = true;

		UsuarioPessoaDto busca = segurancaService.buscarPessoaOuUsuarioComEmail(emailTemp);
		if (busca.getPessoa() != null || busca.getUsuario() != null) {
			boolean unico = true;

			/*
			 * Se for um cadastro novo e tiver sido encontrado alguém com o
			 * email retornar false Caso o idTemp não seja nulo então esta sendo
			 * editado, neste caso se o idTemp e o id retornado da pessoa for o
			 * mesmo então blz, é a mesma pessoa.
			 */
			if (idTemp == null || busca.getPessoa().getId().intValue() != idTemp.intValue()) {
				unico = false;
			}
			if (busca.getUsuario() != null && !pessoaEntidade.getPessoa().getUsuario().getId().equals(busca.getUsuario().getId())) {
				unico = false;
			}
			if (!unico) {
				msg += "<p><li> O 'E-Mail' já esta em uso.</li><p />";
				valido = false;
			}
		}
		return valido;
	}

	private boolean validarEditarPessoa(AjaxRequestTarget target) {
		boolean validar = true;
		msg = "";

		if (!validarCamposObrigatorios()) {
			mensagem.setObject(msg);
			target.add(labelMensagem);
			return false;
		}

		boolean validarCpf = validarCpfCompleto(validar);
		boolean validarEmail = validarEmailCompleto();

		if (!validarCpf || !validarEmail) {
			validar = false;
		}
		mensagem.setObject(msg);
		target.add(labelMensagem);

		return validar;
	}

	private boolean acionarComponente() {
		if (readOnly) {
			return false;
		} else {
			if (deixarTodosPaineisHabilitados) {
				return true;
			} else {
				return false;
			}
		}
	}

	private boolean acionarPanelPrincipal() {
		if (readOnly) {
			return false;
		} else {
			if (acionarPainelComDadosParaCadastro) {
				if (deixarTodosPaineisHabilitados) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	private boolean mostrarBotaoAdicionar() {
		if (readOnly) {
			return false;
		} else {
			if (deixarTodosPaineisHabilitados) {
				if (botaoAdicionarNovoClicado) {
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		}
	}

	private boolean mostrarBotaoAdicionarNovo() {
		if (readOnly) {
			return false;
		} else {
			if (deixarTodosPaineisHabilitados) {
				if (mostrarBotaoAdicionarPessoaNovo) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	public void enableHideMainPanelWhenEmpty() {
		hideMainPanelWhenEmpty = true;
		addOrReplace(newPanelPrincipal());
	}

	// ###################################################################################
	// getters & setters
	public PanelPrincipal getPanelPrincipal() {
		return panelPrincipal;
	}

	public void setPanelPrincipal(PanelPrincipal panelPrincipal) {
		this.panelPrincipal = panelPrincipal;
	}

	public PanelDataView getPanelDataView() {
		return panelDataView;
	}

	public void setPanelDataView(PanelDataView panelDataView) {
		this.panelDataView = panelDataView;
	}

	public DataView<PessoaEntidade> getDataViewPessoa() {
		return dataViewPessoa;
	}

	public void setDataViewPessoa(DataView<PessoaEntidade> dataViewPessoa) {
		this.dataViewPessoa = dataViewPessoa;
	}

	public Boolean getHideMainPanelWhenEmpty() {
		return hideMainPanelWhenEmpty;
	}

	public void setHideMainPanelWhenEmpty(Boolean hideMainPanelWhenEmpty) {
		this.hideMainPanelWhenEmpty = hideMainPanelWhenEmpty;
	}

	public boolean isNomeObrigatorio() {
		return nomeObrigatorio;
	}

	public void setNomeObrigatorio(boolean nomeObrigatorio) {
		this.nomeObrigatorio = nomeObrigatorio;
	}

	public boolean isEmailObrigatorio() {
		return emailObrigatorio;
	}

	public void setEmailObrigatorio(boolean emailObrigatorio) {
		this.emailObrigatorio = emailObrigatorio;
	}

	public boolean isTelefoneObrigatorio() {
		return telefoneObrigatorio;
	}

	public void setTelefoneObrigatorio(boolean telefoneObrigatorio) {
		this.telefoneObrigatorio = telefoneObrigatorio;
	}

	public boolean isCpfObrigatorio() {
		return cpfObrigatorio;
	}

	public void setCpfObrigatorio(boolean cpfObrigatorio) {
		this.cpfObrigatorio = cpfObrigatorio;
	}

	public void setTodosCamposObrigatorios() {
		nomeObrigatorio = true;
		cpfObrigatorio = true;
		emailObrigatorio = true;
		telefoneObrigatorio = true;
	}

	public void setTodosCamposOpcionais() {
		nomeObrigatorio = false;
		cpfObrigatorio = false;
		emailObrigatorio = false;
		telefoneObrigatorio = false;
	}

	private void authorize(Component component, Action action, String... roles) {
		String s = StringUtils.join(roles, ",");
		MetaDataRoleAuthorizationStrategy.authorize(component, action, s);
	}

	protected void addMsgInfo(String key, Object... args) {
		String msg = getString(key, new Model<Object[]>(args), key);
		info(msg);
	}

	protected void addMsgError(String key, Object... args) {
		String msg = this.getString(key, new Model<Object[]>(args), key);
		this.error(msg);
	}
}
