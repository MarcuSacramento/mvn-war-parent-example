package br.gov.mj.side.web.view.kit;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.jasperreports.JRConcreteResource;
import org.wicketstuff.jasperreports.handlers.CsvResourceHandler;

import br.gov.mj.apoio.entidades.Elemento;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.Kit;
import br.gov.mj.side.web.service.BemService;
import br.gov.mj.side.web.service.ElementoService;
import br.gov.mj.side.web.service.KitService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.RelatorioKitPesquisaDtoBuilder;
import br.gov.mj.side.web.util.SortableKitDataProvider;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.template.TemplatePage;

@AuthorizeInstantiation({ KitPagePesquisa.ROLE_MANTER_KIT_INCLUIR, KitPagePesquisa.ROLE_MANTER_KIT_ALTERAR, KitPagePesquisa.ROLE_MANTER_KIT_EXCLUIR,
		KitPagePesquisa.ROLE_MANTER_KIT_VISUALIZAR })
public class KitPagePesquisa extends TemplatePage {

	private static final long serialVersionUID = 1L;

	public static final String ROLE_MANTER_KIT_INCLUIR = "manter_kit:incluir";
	public static final String ROLE_MANTER_KIT_ALTERAR = "manter_kit:alterar";
	public static final String ROLE_MANTER_KIT_EXCLUIR = "manter_kit:excluir";
	public static final String ROLE_MANTER_KIT_VISUALIZAR = "manter_kit:visualizar";

	private static final String DESCRICAO = "descricao";
	private static final String VALOR = "valor";
	private static final String PAGINATOR = "paginator";
	private static final String CLASS = "class";

	private Form<Void> form;
	private Kit kit = new Kit();
	private Bem bem = new Bem();
	private Elemento elemento = new Elemento();
	private int paginas = 10;
	private int valor = 10;
	private int order = 1;
	private String coluna = "nome";

	private AttributeAppender classArrow = new AttributeAppender(CLASS, "mj_arrow_asc", " ");
	private AttributeAppender classArrowUnsorted = new AttributeAppender(CLASS, "mj_arrow_unsorted", " ");

	private List<Kit> listaKits;
	private List<Bem> listaBens;
	private List<Elemento> listaElementos;
	private List<Kit> listaKitsPesquisa;
	private DataView<Kit> dataview;
	private InfraAjaxPagingNavigator paginator;

	private PanelPesquisa panelPesquisa;
	private PanelDataView panelDataView;

	@Inject
	private ComponentFactory componentFactory;

	@Inject
	private ElementoService elementoService;

	@Inject
	private BemService bemService;

	@Inject
	private KitService kitService;

	public KitPagePesquisa(final PageParameters pageParameters) {
		super(pageParameters);
		initComponents();
	}

	public KitPagePesquisa(final PageParameters param, String mensagemSucesso) {
		super(param);

		initComponents();

		if (!"".equalsIgnoreCase(mensagemSucesso)) {
			addMsgInfo(mensagemSucesso);
		}
	}

	private void initComponents() {
		form = new Form<Void>("form");

		setTitulo("Pesquisar Kits de Bens");

		panelPesquisa = new PanelPesquisa("panelPesquisa");
		panelDataView = new PanelDataView("panelDataView");
		panelDataView.setVisible(false);

		form.add(panelPesquisa);
		form.add(panelDataView);
		form.add(componentFactory.newLink("lnkDashboard", HomePage.class));
		form.add(getButtonNovo());// btnNovo

		add(form);
	}

	/*
	 * ABAIXO SERÃO DESENVOLVIDOS OS PAINEIS
	 */

	public class PanelPesquisa extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelPesquisa(String id) {
			super(id);
			this.setOutputMarkupId(true);

			add(getDropDownNomeKit());// nomeKitAuto
			add(getDropDownNomeBem());// nomeBemAuto
			add(getDropDownNomeElemento());// nomeElementoAuto
			add(getButtonPesquisar());// btnPesquisar
		}
	}

	public class PanelDataView extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelDataView(String id) {
			super(id);
			this.setOutputMarkupId(true);

			SortableKitDataProvider dp = new SortableKitDataProvider(kit, bem, elemento, kitService, coluna, order);

			add(newDataViewKits(dp));
			add(getPaginator());
			add(getDropDownPaginator());
			add(getButtonExportarCsv());

			add(getButtonOrdenarNome());// btnOrdenarNome
			add(getButtonOrdenarDescricao());// btnOrdenarDescricao
			add(getButtonOrdenarValor());// btnOrdenarValor
			add(getLabelSortNome());// lblOrderNome
			add(getLabelSortDescricao());// lblOrderDescricao
			add(getLabelSortValor());// lblOrderValor
		}
	}

	/*
	 * ABAIXO SERÃO IMPLEMENTADOS OS COMPONENTES DOS PAINEIS
	 */

	public DropDownChoice<Kit> getDropDownNomeKit() {
		if (listaKits == null) {
			listaKits = kitService.buscar(new Kit());
		}
		DropDownChoice<Kit> drop = new DropDownChoice<Kit>("nomeKitAuto", new PropertyModel<Kit>(this, "kit"), listaKits, new ChoiceRenderer<Kit>("nomeKit"));
		drop.setNullValid(true);
		return drop;
	}

	public DropDownChoice<Bem> getDropDownNomeBem() {
		listaBens = bemService.buscar(new Bem());
		DropDownChoice<Bem> drop = new DropDownChoice<Bem>("nomeBemAuto", new PropertyModel<Bem>(this, "bem"), listaBens, new ChoiceRenderer<Bem>("nomeBem"));
		drop.setNullValid(true);
		return drop;
	}

	public DropDownChoice<Elemento> getDropDownNomeElemento() {
		if (listaElementos == null) {
			listaElementos = elementoService.buscar(new Elemento());
		}
		DropDownChoice<Elemento> drop = new DropDownChoice<Elemento>("nomeElementoAuto", new PropertyModel<Elemento>(this, "elemento"), listaElementos,
				new ChoiceRenderer<Elemento>("nomeECodigo"));
		drop.setNullValid(true);
		return drop;
	}

	public InfraAjaxFallbackLink<Void> getButtonNovo() {
		return componentFactory.newAjaxFallbackLink("btnNovo", (target) -> adicionarNovo());
	}

	private void adicionarNovo() {
		setResponsePage(new KitPage(null, this, new Kit(), "novo"));
	}

	private void editarKit(Item<Kit> item) {
		setResponsePage(new KitPage(null, this, item.getModelObject(), "editar"));
	}

	private void acaoExcluir(AjaxRequestTarget target, Item<Kit> item) {
		if (!getSideSession().hasRole(ROLE_MANTER_KIT_EXCLUIR)) {
			throw new SecurityException();
		}

		Kit k = item.getModelObject();
		kitService.excluir(k.getId());
		atualizarSetasOrdenacao(target);
		getSession().info("Excluído com sucesso");
	}

	private void visualizarKit(Item<Kit> item) {
		setResponsePage(new KitPage(null, this, item.getModelObject(), "visualizar"));
	}

	public AjaxSubmitLink getButtonPesquisar() {
		AjaxSubmitLink button = new AjaxSubmitLink("btnPesquisar", form) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				pesquisar();

				SortableKitDataProvider dp = new SortableKitDataProvider(kit, bem, elemento, kitService, coluna, order);

				panelDataView.addOrReplace(newDataViewKits(dp));
				panelDataView.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR, dataview));
				target.add(panelDataView);
				target.appendJavaScript("ancorarResultadoPesquisa('#ancoraPesquisa');");
			}
		};
		authorize(button, RENDER, KitPagePesquisa.ROLE_MANTER_KIT_VISUALIZAR);
		return button;
	}

	public DropDownChoice<Integer> getDropDownPaginator() {
		DropDownChoice<Integer> dropPaginator = new DropDownChoice<Integer>("valoresPaginator", new PropertyModel<Integer>(this, VALOR), Constants.QUANTIDADE_ITENS_TABELA);
		dropPaginator.setOutputMarkupId(true);
		actionDropDownPaginator(dropPaginator);
		return dropPaginator;
	}

	@SuppressWarnings("rawtypes")
	public void actionDropDownPaginator(DropDownChoice dropElemento) {
		dropElemento.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {

				paginas = valor;
				dataview.setItemsPerPage(paginas);
				panelDataView.addOrReplace(dataview);
				target.add(panelDataView);
			}
		});
	}

	private Button getButtonExportarCsv() {
		Button buttonCsv = componentFactory.newButton("btnExportarCsv", () -> exportarCsv());
		authorize(buttonCsv, RENDER, KitPagePesquisa.ROLE_MANTER_KIT_VISUALIZAR);
		return buttonCsv;
	}

	private void exportarCsv() {

		List<Kit> listaKit = kitService.pesquisar(kit, bem, elemento);
		if (!listaKit.isEmpty()) {
			RelatorioKitPesquisaDtoBuilder builder = new RelatorioKitPesquisaDtoBuilder();
			JRConcreteResource<CsvResourceHandler> relatorioResource = builder.exportCsv(listaKit);
			ResourceRequestHandler handler = new ResourceRequestHandler(relatorioResource, getPageParameters());
			RequestCycle requestCycle = getRequestCycle();
			requestCycle.scheduleRequestHandlerAfterCurrent(handler);
		} else {
			addMsgError("MSG004");
		}
	}

	// DATAVIEW
	public DataView<Kit> newDataViewKits(SortableKitDataProvider dp) {
		if (listaKitsPesquisa == null) {
			listaKitsPesquisa = new ArrayList<Kit>();
		}

		dataview = new DataView<Kit>("dataBens", dp) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<Kit> item) {
				item.clearOriginalDestination();
				item.add(new Label("nomeKit", item.getModelObject().getNomeKit()));
				item.add(new Label(DESCRICAO, item.getModelObject().getDescricaoKit()));
				String valorz = formatoDinheiro(item.getModelObject().getValorEstimado().doubleValue());
				item.add(new Label("valorEstimado", valorz));
				item.add(getButtonEditar(item));
				item.add(getButtonVisualizar(item));
				item.add(newButtonExcluir(item));
			}
		};
		dataview.setItemsPerPage(paginas);
		return dataview;
	}

	public InfraAjaxPagingNavigator getPaginator() {
		if (paginator == null) {
			paginator = new InfraAjaxPagingNavigator(PAGINATOR, dataview);
		}
		return paginator;
	}

	public void mudarOrdemTabela() {
		if (order == 1) {
			order = -1;
			classArrow = new AttributeAppender(CLASS, "mj_arrow_desc", " ");
		} else {
			order = 1;
			classArrow = new AttributeAppender(CLASS, "mj_arrow_asc", " ");
		}
	}

	public AjaxSubmitLink getButtonOrdenarNome() {
		return new AjaxSubmitLink("btnOrdenarNome", form) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				coluna = "nome";
				mudarOrdemTabela();
				atualizarSetasOrdenacao(target);
			}
		};
	}

	public AjaxSubmitLink getButtonOrdenarValor() {
		return new AjaxSubmitLink("btnOrdenarValor", form) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				coluna = VALOR;
				mudarOrdemTabela();
				atualizarSetasOrdenacao(target);
			}
		};
	}

	public AjaxSubmitLink getButtonOrdenarDescricao() {
		return new AjaxSubmitLink("btnOrdenarDescricao", form) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				coluna = DESCRICAO;
				mudarOrdemTabela();
				atualizarSetasOrdenacao(target);
			}
		};
	}

	public Label getLabelSortNome() {
		Label label = null;
		label = new Label("lblOrderNome", "...");

		if ("nome".equalsIgnoreCase(coluna)) {
			label.add(classArrow);
		} else {
			label.add(classArrowUnsorted);
		}
		return label;
	}

	public Label getLabelSortDescricao() {
		Label label = new Label("lblOrderDescricao", "...");

		if (DESCRICAO.equalsIgnoreCase(coluna)) {
			label.add(classArrow);
		} else {
			label.add(classArrowUnsorted);
		}
		return label;
	}

	public Label getLabelSortValor() {
		Label label = new Label("lblOrderValor", "...");
		if (VALOR.equalsIgnoreCase(coluna)) {
			label.add(classArrow);
		} else {
			label.add(classArrowUnsorted);
		}

		label.setEscapeModelStrings(false);
		return label;
	}

	/*
	 * AS AÇÕES SERÃO INSERIDAS ABAIXO
	 */

	public void pesquisar() {
		panelDataView.setVisible(true);
	}

	public void apagarKit(Item<Kit> item) {
		Kit apagar = item.getModelObject();
		listaKitsPesquisa.remove(apagar);
		kitService.excluir(apagar.getId());
	}

	public void atualizarDataView(AjaxRequestTarget target) {
		SortableKitDataProvider dp = new SortableKitDataProvider(kit, bem, elemento, kitService, coluna, order);
		panelDataView.addOrReplace(newDataViewKits(dp));
		target.add(panelDataView);
	}

	private InfraAjaxFallbackLink<Void> getButtonEditar(Item<Kit> item) {
		return componentFactory.newAjaxFallbackLink("btnEditar", (target) -> editarKit(item));
	}

	private InfraAjaxConfirmButton newButtonExcluir(Item<Kit> item) {
		InfraAjaxConfirmButton btnExcluir = componentFactory.newAJaxConfirmButton("btnExcluir", "MSG001", form, (target, formz) -> acaoExcluir(target, item));
		authorize(btnExcluir, RENDER, ROLE_MANTER_KIT_EXCLUIR);
		return btnExcluir;
	}

	public InfraAjaxFallbackLink<Void> getButtonVisualizar(Item<Kit> item) {
		return componentFactory.newAjaxFallbackLink("btnVisualizar", (target) -> visualizarKit(item));
	}

	public void atualizarSetasOrdenacao(AjaxRequestTarget target) {
		SortableKitDataProvider dp = new SortableKitDataProvider(kit, bem, elemento, kitService, coluna, order);
		panelDataView.addOrReplace(newDataViewKits(dp));
		panelDataView.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR, dataview));
		panelDataView.addOrReplace(getLabelSortNome());
		panelDataView.addOrReplace(getLabelSortDescricao());
		panelDataView.addOrReplace(getLabelSortValor());
		target.add(panelDataView);
	}

	public String formatoDinheiro(double bigDecimal) {
		return NumberFormat.getCurrencyInstance().format(bigDecimal);
	}

	public Integer getValor() {
		return valor;
	}

	public void setValor(Integer valor) {
		this.valor = valor;
	}
}