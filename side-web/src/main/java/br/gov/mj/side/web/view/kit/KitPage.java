package br.gov.mj.side.web.view.kit;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.apoio.entidades.Elemento;
import br.gov.mj.apoio.entidades.SubElemento;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.Kit;
import br.gov.mj.side.entidades.KitBem;
import br.gov.mj.side.web.service.BemService;
import br.gov.mj.side.web.service.ElementoService;
import br.gov.mj.side.web.service.KitService;
import br.gov.mj.side.web.service.SubElementoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.template.TemplatePage;

@AuthorizeInstantiation({ KitPage.ROLE_MANTER_KIT_INCLUIR, KitPage.ROLE_MANTER_KIT_ALTERAR, KitPage.ROLE_MANTER_KIT_EXCLUIR, KitPage.ROLE_MANTER_KIT_VISUALIZAR })
public class KitPage extends TemplatePage {

	private static final long serialVersionUID = 1L;

	public static final String ROLE_MANTER_KIT_INCLUIR = "manter_kit:incluir";
	public static final String ROLE_MANTER_KIT_ALTERAR = "manter_kit:alterar";
	public static final String ROLE_MANTER_KIT_EXCLUIR = "manter_kit:excluir";
	public static final String ROLE_MANTER_KIT_VISUALIZAR = "manter_kit:visualizar";

	private static final String QUANTIDADE = "quantidade";

	private BemSelecionadosProvider bemProvider;

	private PanelNomeKit panelNomeKit;
	private PanelAdicionarBem panelAdicionarBem;
	private PanelQuantidade panelQuantidade;
	private PanelDataView panelDataView;

	private Page backPage;

	private DataView<KitBem> dataview;
	private Button btnConfirmar;
	private AjaxButton buttonSalverEdicao;
	private AjaxSubmitLink buttonCancelarEdicao;
	private AjaxButton buttonAdicionarBem;
	private DataView<KitBem> dataviewBensAdicionados;

	private Form<Kit> form;
	private Form<KitPage> formBem;
	private List<Elemento> listaElemento = new ArrayList<Elemento>();
	private List<SubElemento> listaSubelemento = new ArrayList<SubElemento>();
	private List<Bem> listaBem = new ArrayList<Bem>();
	private List<KitBem> listaKit = new ArrayList<KitBem>();

	private DropDownChoice<Elemento> dropElemento;
	private AttributeAppender classDropDown = new AttributeAppender("class", "js-example-basic-single", " ");

	private String descricao;
	private Elemento elemento = new Elemento();
	private SubElemento subElemento = new SubElemento();
	private Bem bem = new Bem();
	private Kit kit = new Kit();
	private String quantidade = "";
	private String valorEstimado = "";
	private int totalBens = 0;
	private String modo;
	private Integer quantidadeBem;
	private boolean editandoQuantidade = false;

	@Inject
	private ElementoService elementoService;
	@Inject
	private SubElementoService subElementoService;
	@Inject
	private BemService bemService;
	@Inject
	private KitService kitService;
	@Inject
	private ComponentFactory componentFactory;

	/**
	 * @param pageParameters
	 * @param backPage
	 *            uma referência a página KitPagePesquisa, quando retornar irá
	 *            mostrar a mesma tela.
	 * @param modo
	 *            = novo para cadastro de kit novo, editar para editar um kit e
	 *            visualizar para visualizar
	 */
	public KitPage(final PageParameters pageParameters, Page backPage, Kit kit, String modo) {
		super(pageParameters);
		this.backPage = backPage;
		this.kit = kit;
		this.modo = modo;
		bemProvider = new BemSelecionadosProvider();
		initComponents();
	}

	@SuppressWarnings("rawtypes")
	private void initComponents() {
		form = componentFactory.newForm("form", new CompoundPropertyModel<Kit>(new Kit()));
		formBem = componentFactory.newForm("formBem", new CompoundPropertyModel<KitPage>(this));

		form.add(getButtonConfirmar());// btnConfirmar
		form.add(newButtonVoltar());// btnCancelar

		if (kit != null) {
			editando();
		}

		panelNomeKit = new PanelNomeKit("panelNomeKit");
		panelAdicionarBem = new PanelAdicionarBem("panelAdicionarBem");
		panelQuantidade = new PanelQuantidade("panelQuantidade");
		panelDataView = new PanelDataView("panelDataView");

		buttonAdicionarBem.setVisible(!editandoQuantidade);
		buttonSalverEdicao.setVisible(editandoQuantidade);
		buttonCancelarEdicao.setVisible(editandoQuantidade);

		if ("visualizar".equalsIgnoreCase(modo)) {
			panelNomeKit.setEnabled(false);
			panelAdicionarBem.setVisible(false);
			panelQuantidade.setEnabled(false);
			panelDataView.setEnabled(false);
			btnConfirmar.setVisible(false);
		}

		form.add(getLabelBreadcrump());// labelBreadcrumb
		form.add(panelNomeKit);
		formBem.add(panelAdicionarBem);
		formBem.add(panelQuantidade);
		form.add(formBem);
		form.add(panelDataView);
		form.add(new Link("homePage") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(HomePage.class);
			}
		});
		form.add(componentFactory.newLink("lnkKitPagePesquisa", KitPagePesquisa.class));

		add(form);
	}

	private void editando() {
		form.setModelObject(kit);
		listaKit = kit.getKitsBens();
		totalBens = listaKit.size();
		descricao = kit.getDescricaoKit();
		valorEstimado = formatoDinheiro(kit.getValorEstimado());
	}

	/**
	 * OS PAINEIS SÃO ADICIONADOS ABAIXO
	 */

	private class PanelNomeKit extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelNomeKit(String id) {
			super(id);
			setOutputMarkupId(true);
			add(getTextFieldNomeKit());// nomeKit
			add(getTextAreaDescricao());// descricao
			add(getTextValorEstimado());// valorEstimado
		}
	}

	private class PanelAdicionarBem extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelAdicionarBem(String id) {
			super(id);
			setOutputMarkupId(true);
			add(getDropDownElemento());// elemento
			add(getDropDownSubElemento());// subElemento
			add(getDropDownBem());// bem
			add(getButtonPesquisar()); // btnPesquisar
		}
	}

	private class PanelQuantidade extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelQuantidade(String id) {
			super(id);
			setOutputMarkupId(true);
			add(getTextFieldQuantidade());// quantidade
			add(getButtonAdicionar());// btnAdicionar

			// Irá aparecer somente se clicar no botão de editar quantidade do
			// bem
			add(getButtonSalvarEdicao());
			add(getButtonCancelarEdicao());
		}
	}

	private class PanelDataView extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelDataView(String id) {
			super(id);
			setOutputMarkupId(true);
			add(getDataViewBensAdicionados()); // dataBensAdicionados
			add(new InfraAjaxPagingNavigator("paginator", dataviewBensAdicionados));
		}
	}

	/*
	 * OS COMPONENTES DOS PAINEIS ESTÃO ABAIXO
	 */

	public Label getLabelBreadcrump() {
		String label = "";
		if ("novo".equalsIgnoreCase(modo)) {
			label = "Cadastrar Kit de Bens";
		} else {
			if ("editar".equalsIgnoreCase(modo)) {
				label = "Alterar Kit de Bens";
			} else {
				label = "Visualizar Kit de Bens";
			}
		}
		setTitulo(label); // Seta o titulo da página
		return new Label("labelBreadcrump", label);
	}

	public TextField<String> getTextFieldNomeKit() {
		TextField<String> text = componentFactory.newTextField("nomeKit", "Nome do Kit", false, null);
		text.setRequired(true);
		text.add(StringValidator.maximumLength(198));
		return text;
	}

	public TextArea<String> getTextAreaDescricao() {
		TextArea text = new TextArea<String>("descricao", new PropertyModel(this, "descricao"));
		text.setLabel(Model.of("Descrição"));
		text.setRequired(true);
		text.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));

		return text;
	}

	public TextField<BigDecimal> getTextValorEstimado() {
		return new TextField("valorEstimado", new PropertyModel(this, "valorEstimado"));
	}

	// ADICIONAR BEM
	public DropDownChoice<Elemento> getDropDownElemento() {
		listaElemento = elementoService.buscar(new Elemento());
		dropElemento = new DropDownChoice<Elemento>("elemento", new PropertyModel<Elemento>(this, "elemento"), listaElemento, new ChoiceRenderer<Elemento>("nomeECodigo"));
		actionDropDownElemento(dropElemento);
		dropElemento.setNullValid(true);
		dropElemento.add(classDropDown);
		dropElemento.setOutputMarkupId(true);
		dropElemento.setRequired(validarDropDown());

		return dropElemento;
	}

	public DropDownChoice<SubElemento> getDropDownSubElemento() {
		DropDownChoice<SubElemento> drop = new DropDownChoice<SubElemento>("subElemento", new PropertyModel<SubElemento>(this, "subElemento"), listaSubelemento,
				new ChoiceRenderer<SubElemento>("nomeECodigo"));
		drop.setOutputMarkupId(true);
		actionDropDownSubElemento(drop);
		drop.setNullValid(true);
		drop.setRequired(validarDropDown());

		return drop;
	}

	public boolean validarDropDown() {
		boolean validar = true;

		if (elemento == null || elemento.getId() == null) {
			validar = false;
		} else {
			validar = false;
		}
		return validar;
	}

	public DropDownChoice<Bem> getDropDownBem() {
		DropDownChoice<Bem> drop = new DropDownChoice<Bem>("bem", new PropertyModel<Bem>(this, "bem"), listaBem, new ChoiceRenderer<Bem>("nomeBem"));
		drop.setOutputMarkupId(true);
		drop.setNullValid(true);
		drop.setRequired(validarDropDown());

		return drop;
	}

	public TextField<String> getTextFieldQuantidade() {
		TextField<String> text = new TextField(QUANTIDADE, new PropertyModel(this, QUANTIDADE));

		if (elemento == null || elemento.getId() == null) {
			text.setRequired(false);
		} else {
			text.setRequired(false);
		}
		text.add(StringValidator.maximumLength(10));
		return text;
	}

	public TextField<KitBem> getTextFieldQuantidadeEditar(Item<KitBem> item) {
		TextField<KitBem> text = new TextField(QUANTIDADE, Model.of(item.getModelObject().getQuantidade()));

		OnChangeAjaxBehavior onChangeAjaxBehavior = new OnChangeAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				item.getModelObject().setQuantidade(text.getModelObject().getQuantidade());
				target.add(panelDataView);
			}
		};
		text.add(onChangeAjaxBehavior);
		return text;
	}

	public Button getButtonAdicionar() {
		buttonAdicionarBem = new AjaxButton("btnAdicionar") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				adicionar(target);
			}
		};
		authorize(buttonAdicionarBem, RENDER, KitPage.ROLE_MANTER_KIT_INCLUIR);
		return buttonAdicionarBem;
	}

	public AjaxSubmitLink getButtonPesquisar() {
		AjaxSubmitLink link = new AjaxSubmitLink("btnPesquisar", form) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form form) {
				pesquisar(target);
			}
		};
		link.setDefaultFormProcessing(false);
		return link;
	}

	// DATAVIEW
	public DataView<KitBem> getDataViewBensAdicionados() {

		dataviewBensAdicionados = new DataView<KitBem>("dataBensAdicionados", bemProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<KitBem> item) {
				item.clearOriginalDestination();
				item.add(new Label("nomeElemento", item.getModelObject().getBem().getSubElemento().getElemento().getNomeECodigo()));
				item.add(new Label("nomeSubElemento", item.getModelObject().getBem().getSubElemento().getNomeECodigo()));
				item.add(new Label("nomeBem", item.getModelObject().getBem().getNomeBem()));
				item.add(new Label(QUANTIDADE, item.getModelObject().getQuantidade()));

				if (item.getModelObject().getQuantidade() != null && item.getModelObject().getQuantidade().intValue() != 0) {
					int quantidadez = item.getModelObject().getQuantidade().intValue();
					BigDecimal valor = item.getModelObject().getBem().getValorEstimadoBem();
					valor = valor.multiply(new BigDecimal(quantidadez));
					item.add(new Label("valorEstimadoData", formatoDinheiro(valor)));
				} else {
					item.add(new Label("valorEstimadoData", formatoDinheiro(new BigDecimal(0))));
				}
				item.add(getButtonExcluir(item));
				item.add(getButtonEditar(item));
			}
		};
		dataviewBensAdicionados.setItemsPerPage(10);
		return dataviewBensAdicionados;
	}

	public InfraAjaxConfirmButton getButtonExcluir(Item<KitBem> item) {
		return componentFactory.newAJaxConfirmButton("btnExcluir", "MSG001", form, (target, formz) -> excluirAtributo(target, item));
	}

	public AjaxSubmitLink getButtonEditar(Item<KitBem> item) {
		AjaxSubmitLink button = new AjaxSubmitLink("btnEditar", form) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form form) {
				if (item.getModelObject().getQuantidade() == null || item.getModelObject().getQuantidade() <= 0) {
					addMsgError("O Campo 'Quantidade' não pode ser nulo nem inferior a '0'.");
					return;
				}
				editandoQuantidade = true;

				elemento = item.getModelObject().getBem().getSubElemento().getElemento();
				subElemento = item.getModelObject().getBem().getSubElemento();
				listaSubelemento = subElementoService.buscarPeloElementoId(elemento.getId());
				bem = item.getModelObject().getBem();

				Bem bemz = new Bem();
				bemz.setSubElemento(subElemento);
				listaBem = bemService.buscar(bemz);

				quantidade = item.getModelObject().getQuantidade().toString();

				buttonSalverEdicao.setVisible(editandoQuantidade);
				buttonCancelarEdicao.setVisible(editandoQuantidade);
				buttonAdicionarBem.setVisible(!editandoQuantidade);

				panelAdicionarBem.addOrReplace(getDropDownElemento().setEnabled(false));
				panelAdicionarBem.addOrReplace(getDropDownSubElemento().setEnabled(false));
				panelAdicionarBem.addOrReplace(getDropDownBem().setEnabled(false));

				panelQuantidade.addOrReplace(getTextFieldQuantidade());
				panelQuantidade.addOrReplace(buttonSalverEdicao);
				panelQuantidade.addOrReplace(buttonCancelarEdicao);
				panelQuantidade.addOrReplace(buttonAdicionarBem);

				target.add(panelAdicionarBem);
				target.add(panelQuantidade);
			}
		};

		button.setDefaultFormProcessing(false);
		return button;
	}

	public AjaxButton getButtonSalvarEdicao() {
		buttonSalverEdicao = new AjaxButton("btnSalvarEdicao") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				for (KitBem k : listaKit) {
					if (k.getBem().getId() == bem.getId()) {
						if (quantidade != null) {
							k.setQuantidade(Integer.parseInt(quantidade));
						}
					}
				}

				// Irá validar somente o campo de quantidade.
				if (!validarEdicaoAdicionarBem()) {
					return;
				}

				editandoQuantidade = false;

				buttonSalverEdicao.setVisible(editandoQuantidade);
				buttonCancelarEdicao.setVisible(editandoQuantidade);
				buttonAdicionarBem.setVisible(!editandoQuantidade);

				panelAdicionarBem.addOrReplace(getDropDownElemento().setEnabled(true));
				panelAdicionarBem.addOrReplace(getDropDownSubElemento().setEnabled(true));
				panelAdicionarBem.addOrReplace(getDropDownBem().setEnabled(true));

				atualizarAposInserirOuRemoverBem(target);
			}
		};
		authorize(buttonSalverEdicao, RENDER, KitPage.ROLE_MANTER_KIT_INCLUIR);
		return buttonSalverEdicao;
	}

	public AjaxSubmitLink getButtonCancelarEdicao() {
		buttonCancelarEdicao = new AjaxSubmitLink("btnCancelarEdicao", form) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form form) {

				editandoQuantidade = false;

				buttonSalverEdicao.setVisible(editandoQuantidade);
				buttonCancelarEdicao.setVisible(editandoQuantidade);
				buttonAdicionarBem.setVisible(!editandoQuantidade);

				panelAdicionarBem.addOrReplace(getDropDownElemento().setEnabled(true));
				panelAdicionarBem.addOrReplace(getDropDownSubElemento().setEnabled(true));
				panelAdicionarBem.addOrReplace(getDropDownBem().setEnabled(true));

				atualizarAposInserirOuRemoverBem(target);
			}
		};
		buttonCancelarEdicao.setDefaultFormProcessing(false);
		return buttonCancelarEdicao;
	}

	public Label getLabelTotalBens() {
		return new Label("totalBens", this.totalBens);
	}

	public Button getButtonConfirmar() {
		btnConfirmar = componentFactory.newButton("btnConfirmar", () -> salvar());
		return btnConfirmar;
	}

	private Button newButtonVoltar() {
		Button buttonVoltar = componentFactory.newButton("btnVoltar", () -> voltar());
		buttonVoltar.setDefaultFormProcessing(false);
		return buttonVoltar;
	}

	// PROVIDER

	private class BemSelecionadosProvider extends SortableDataProvider<KitBem, String> {
		private static final long serialVersionUID = 1L;

		public BemSelecionadosProvider() {
			setSort("nomeBem", SortOrder.ASCENDING);
		}

		@Override
		public Iterator<KitBem> iterator(long first, long size) {

			List<KitBem> bemTemp = new ArrayList<KitBem>();

			int firstTemp = 0;
			int flagTemp = 0;
			for (KitBem k : listaKit) {
				if (firstTemp >= first) {
					if (flagTemp <= size) {
						bemTemp.add(k);
						flagTemp++;
					}
				}
				firstTemp++;
			}
			return bemTemp.iterator();
		}

		@Override
		public long size() {
			return listaKit.size();
		}

		@Override
		public IModel<KitBem> model(KitBem object) {
			return new CompoundPropertyModel<KitBem>(object);
		}
	}

	/*
	 * As ações serão inseridas abaixo
	 */

	private void pesquisar(AjaxRequestTarget target) {
		setResponsePage(new PanelVincularBem(null, this, modo));
	}

	public void salvar() {
		if (!getSideSession().hasAnyRole(new String[] { KitPage.ROLE_MANTER_KIT_INCLUIR, KitPage.ROLE_MANTER_KIT_ALTERAR })) {
			throw new SecurityException();
		}

		if (!validarFormularioInteiro()) {
			return;
		}

		Kit kitSalvar = new Kit();
		kitSalvar = form.getModelObject();
		kitSalvar.setKitsBens(listaKit);
		kitSalvar.setDescricaoKit(descricao);

		Kit kitSalvo = kitService.incluirAlterar(kitSalvar);

		if ("novo".equalsIgnoreCase(modo)) {
			getSession().info("Cadastrado com sucesso");
		} else {
			getSession().info("Alterado com sucesso.");
		}
		setResponsePage(new KitPage(null, backPage, kitSalvo, "editar"));
	}

	public void adicionar(AjaxRequestTarget target) {

		if (!validarAdicionarBem()) {
			return;
		}

		KitBem novo = new KitBem();
		novo.setBem(bem);
		novo.setQuantidade(Integer.parseInt(quantidade));
		listaKit.add(novo);

		atualizarAposInserirOuRemoverBem(target);
	}

	private void voltar() {
		setResponsePage(backPage);
	}

	private void excluirAtributo(AjaxRequestTarget target, Item<KitBem> item) {
		KitBem apagar = item.getModelObject();
		listaKit.remove(apagar);

		editandoQuantidade = false;

		buttonSalverEdicao.setVisible(editandoQuantidade);
		buttonCancelarEdicao.setVisible(editandoQuantidade);
		buttonAdicionarBem.setVisible(!editandoQuantidade);

		panelAdicionarBem.addOrReplace(getDropDownElemento().setEnabled(true));
		panelAdicionarBem.addOrReplace(getDropDownSubElemento().setEnabled(true));
		panelAdicionarBem.addOrReplace(getDropDownBem().setEnabled(true));

		atualizarAposInserirOuRemoverBem(target);

		target.add(panelDataView);
		target.add(panelNomeKit);
	}

	public void adicionarBens(AjaxRequestTarget target) {
		KitBem novo = new KitBem();
		novo.setBem(bem);
		novo.setQuantidade(Integer.parseInt(quantidade));
		listaKit.add(novo);

		target.add(panelAdicionarBem);
	}

	// valida o formulário inteiro
	public boolean validarFormularioInteiro() {
		boolean validar = true;
		if (form.getModelObject().getNomeKit() == null || "".equals(form.getModelObject())) {
			addMsgError("O campo 'Nome do Kit' é obrigatório.");
			validar = false;
		}

		if (descricao == null || "".equalsIgnoreCase(descricao)) {
			addMsgError("O campo 'Descrição' é obrigatório.");
			validar = false;
		}

		if (listaKit.isEmpty()) {
			addMsgError("Não é possível cadastrar um kit novo sem selecionar nenhum bem.");
			validar = false;
		}

		if (!verificarQuantidadesNulas()) {
			addMsgError("O Campo 'Quantidade' não pode ser nulo nem inferior a '0'.");
			quantidade = "0";
			validar = false;
		}
		return validar;
	}

	public boolean verificarQuantidadesNulas() {
		boolean validar = true;
		for (KitBem k : listaKit) {
			if (k.getQuantidade() == null || k.getQuantidade() <= 0) {
				validar = false;
			}
		}
		return validar;
	}

	// Valida somente o campo adicionar Bem
	public boolean validarAdicionarBem() {
		boolean validar = true;
		if (elemento == null) {
			addMsgError("O campo 'Elemento' é obrigatório.");
			validar = false;
		}

		if (subElemento == null) {
			addMsgError("O campo 'Subelemento' é obrigatório.");
			validar = false;
		}

		if (bem == null) {
			addMsgError("O campo 'Bem' é obrigatório.");
			validar = false;
		}

		if (quantidade == null || "".equalsIgnoreCase(quantidade)) {
			addMsgError("O campo 'Quantidade' é obrigatório.");
			validar = false;
		} else {
			if ("0".equalsIgnoreCase(quantidade)) {
				addMsgError("A quantidade não pode ser igual a '0'.");
				validar = false;
			}
		}

		for (KitBem bemz : listaKit) {
			if (bemz.getBem().getId().intValue() == this.bem.getId().intValue()) {
				addMsgError("O Bem que selecionado já esta na lista, edite-o alterando a quantidade caso necessário.");
				validar = false;
				break;
			}
		}
		return validar;
	}

	public boolean validarEdicaoAdicionarBem() {
		boolean validar = true;

		if (quantidade == null || "".equalsIgnoreCase(quantidade)) {
			addMsgError("O campo 'Quantidade' é obrigatório.");
			validar = false;
		} else {
			if ("0".equalsIgnoreCase(quantidade)) {
				addMsgError("A quantidade não pode ser igual a '0'.");
				validar = false;
			}
		}
		return validar;
	}

	@SuppressWarnings("rawtypes")
	public void actionDropDownElemento(DropDownChoice dropElemento) {
		dropElemento.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {

				listaBem.clear();
				listaSubelemento.clear();
				if (elemento != null) {
					listaSubelemento = subElementoService.buscarPeloElementoId(elemento.getId());
				}

				panelAdicionarBem.addOrReplace(getDropDownSubElemento());
				panelAdicionarBem.addOrReplace(getDropDownBem());
				panelQuantidade.addOrReplace(getTextFieldQuantidade());
				target.add(panelAdicionarBem);
				target.add(panelQuantidade);
			}
		});
	}

	@SuppressWarnings("rawtypes")
	public void actionDropDownSubElemento(DropDownChoice dropElemento) {
		dropElemento.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {

				listaBem.clear();
				if (subElemento != null) {
					Bem bemz = new Bem();
					bemz.setSubElemento(subElemento);
					listaBem = bemService.buscar(bemz);
				}

				panelAdicionarBem.addOrReplace(getDropDownBem());
				target.add(panelAdicionarBem);
			}
		});
	}

	// Limpa o formulário quando clicar no botão de confirmar
	public void limparFormularioInteiro() {
		form.setModelObject(new Kit());
		elemento = listaElemento.get(0);
		listaSubelemento = subElementoService.buscarPeloElementoId(elemento.getId());
		descricao = "";
		quantidade = "";
		totalBens = 0;
		valorEstimado = "";
		listaKit.clear();
	}

	// Limpa apenas o campo para adicionar bem
	public void limparFormularioAdicionarBem() {
		elemento = listaElemento.get(0);
		listaSubelemento = subElementoService.buscarPeloElementoId(elemento.getId());
		listaBem.clear();
		quantidade = "";
	}

	public void atualizarAposInserirOuRemoverBem(AjaxRequestTarget target) {
		Kit kitz = new Kit();
		kitz.setKitsBens(listaKit);
		valorEstimado = formatoDinheiro(kitz.getValorEstimado());
		limparFormularioAdicionarBem();

		// Atualiza a quantidade de itens na lista
		totalBens = listaKit.size();

		// Zera as listas que mostram os dropDown
		elemento = new Elemento();
		subElemento = new SubElemento();
		bem = new Bem();

		listaSubelemento.clear();
		listaBem.clear();

		// Irá atualizar os componentes abaixo
		// panelDataView.addOrReplace(getLabelTotalBens());
		panelAdicionarBem.addOrReplace(getDropDownElemento());
		panelAdicionarBem.addOrReplace(getDropDownSubElemento());
		panelAdicionarBem.addOrReplace(getDropDownBem());
		panelQuantidade.addOrReplace(getTextFieldQuantidade());
		panelNomeKit.addOrReplace(getTextValorEstimado());

		// Atualiza os paineis
		target.add(panelDataView);
		target.add(panelAdicionarBem);
		target.add(panelQuantidade);
		target.add(panelNomeKit);
	}

	public String formatoDinheiro(BigDecimal valor) {
		return NumberFormat.getCurrencyInstance().format(valor);
	}

	public String getValorEstimado() {
		return valorEstimado;
	}

	public void setValorEstimado(String valorEstimado) {
		this.valorEstimado = valorEstimado;
	}

	public Integer getQuantidadeBem() {
		return quantidadeBem;
	}

	public void setQuantidadeBem(Integer quantidadeBem) {
		this.quantidadeBem = quantidadeBem;
	}

	public List<KitBem> getListaKit() {
		return listaKit;
	}

	public void setListaKit(List<KitBem> listaKit) {
		this.listaKit = listaKit;
	}
}
