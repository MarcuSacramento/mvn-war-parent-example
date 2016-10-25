package br.gov.mj.side.web.view.kit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.apoio.entidades.Elemento;
import br.gov.mj.apoio.entidades.SubElemento;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.listener.FeedbackPanelListener;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.KitBem;
import br.gov.mj.side.web.service.BemService;
import br.gov.mj.side.web.service.ElementoService;
import br.gov.mj.side.web.service.SubElementoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.bem.BemPage;
import br.gov.mj.side.web.view.template.TemplatePage;

public class PanelVincularBem extends TemplatePage {

	private static final long serialVersionUID = 1L;

	private static final String NOME_BEM_ADICIONADO = "nomeBemAdicionado";
	private static final String NOME_BEM = "nomeBem";
	private static final String VALORS = "valorSelecionado";
	private static final String PAGINATOR1 = "pagination";

	private Page backPage;

	private Bem entity; // Model
	private Elemento elemento = new Elemento();
	private Integer itensPorPagina = 10;
	private boolean pesquisado = false;
	private List<Bem> listBensPesquisados;
	private List<KitBem> listaBensSelecionadosDireito;
	private List<KitBem> listaBensSelecionadosDireitoTemp;
	private String quantidade;
	private int paginasAdicionado = 10;
	private int valorSelecionado = 10;
	private boolean atualizandoLista;
	private BemSelecionadosProvider dp;
	private BemProvider bemProvider;
	private String modo;

	private Form<Bem> form;
	private InfraDropDownChoice<SubElemento> dropSubElemento;
	private PanelGridResultados panelGridResultados;
	private PanelDataViewAdicionado panelDataViewAdicionado;
	private DataView<Bem> dataViewBem;
	private DataView<KitBem> dataviewAdicionado;
	private PanelSubelemento panelSubelemento;
	private InfraAjaxPagingNavigator paginatorAdicionado;
	private InfraAjaxPagingNavigator paginator;

	private AjaxSubmitLink btnPesquisar;

	@Inject
	private ElementoService elementoService;
	@Inject
	private SubElementoService subElementoService;
	@Inject
	private BemService bemService;
	@Inject
	private ComponentFactory componentFactory;

	public PanelVincularBem(final PageParameters pageParameters) {
		super(pageParameters);
		setTitulo("Pesquisar Bem");
		setEntity(new Bem());
		initComponents();
		criarBreadcrumb();
		initVariaveis();
		visualizarOcultarDataViews();
	}

	public PanelVincularBem(final PageParameters pageParameters, Page backPage, String modo) {
		super(pageParameters);

		this.backPage = backPage;
		this.modo = modo;

		setTitulo("Vincular Bem");
		setEntity(new Bem());
		initComponents();
		criarBreadcrumb();
		initVariaveis();
		visualizarOcultarDataViews();
	}

	protected void initComponents() {

		form = componentFactory.newForm("form", new CompoundPropertyModel<Bem>(entity));

		panelSubelemento = new PanelSubelemento("panelSubelemento");
		btnPesquisar = newButtonPesquisar();
		panelGridResultados = new PanelGridResultados("panelGridResultados");
		panelGridResultados.setOutputMarkupId(true);
		panelGridResultados.setVisible(isPesquisado());
		panelDataViewAdicionado = new PanelDataViewAdicionado("panelDataViewAdicionado");

		form.add(componentFactory.newTextField(NOME_BEM, "Nome do Bem", false, null));
		form.add(panelSubelemento);
		form.add(btnPesquisar);
		form.add(panelGridResultados);
		form.add(panelDataViewAdicionado);
		form.add(getButtonConfirmar());
		form.add(getButtonVoltar());

		add(form);
	}

	public void initVariaveis() {
		if (listBensPesquisados == null) {
			listBensPesquisados = new ArrayList<Bem>();
		}

		listaBensSelecionadosDireito = new ArrayList<KitBem>();
		KitPage page = (KitPage) backPage;
		for (KitBem kitBem : page.getListaKit()) {
			listaBensSelecionadosDireito.add(kitBem);
		}
		copiarParaListaTemp();
	}

	public void visualizarOcultarDataViews() {
		if (!listaBensSelecionadosDireito.isEmpty()) {
			panelDataViewAdicionado.setVisible(true);
			panelGridResultados.setVisible(true);
			atualizandoLista = false;
		} else {
			panelDataViewAdicionado.setVisible(false);
			panelGridResultados.setVisible(false);
			atualizandoLista = true;
		}
	}

	@SuppressWarnings("rawtypes")
	private void criarBreadcrumb() {
		form.add(new Link("homePage") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(HomePage.class);
			}
		});
		form.add(new Link("kitPagePesquisa") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(KitPagePesquisa.class);
			}
		});
		form.add(new Link("kitPage") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				cancelarAcoesLista();
				KitPage page = (KitPage) backPage;
				page.setListaKit(listaBensSelecionadosDireito);

				setResponsePage(backPage);
			}
		});
	}

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
		return new Label("labelBreadcrump", label);
	}

	public AjaxSubmitLink newButtonPesquisar() {
		AjaxSubmitLink button = new AjaxSubmitLink("btnPesquisar", form) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				pesquisar();

				target.add(panelGridResultados, panelDataViewAdicionado);
				target.appendJavaScript("ancorarResultadoPesquisa('#ancoraPesquisa');");
			}
		};
		return button;
	}

	private void pesquisar() {
		if (getEntity().getSubElemento() == null) {
			getEntity().setSubElemento(new SubElemento());
		}
		getEntity().getSubElemento().setElemento(getElemento());

		listBensPesquisados = bemService.buscar(getEntity());
		verificarCamposJaAdicionados();

		setPesquisado(true);
		panelGridResultados.setVisible(isPesquisado());
		panelDataViewAdicionado.setVisible(isPesquisado());
	}

	private void verificarCamposJaAdicionados() {
		for (int i = listaBensSelecionadosDireito.size() - 1; i > -1; i--) {
			int contain = 0;
			int posicao = 0;
			for (Bem selecionado : listBensPesquisados) {
				if (listaBensSelecionadosDireito.get(i).getBem().getId().intValue() == selecionado.getId().intValue()) {
					contain++;
					break;
				}
				posicao++;
			}

			if (contain != 0) {
				listBensPesquisados.remove(posicao);
			}
		}
	}

	private DropDownChoice<Elemento> getDropDownChoiceElemento() {
		InfraDropDownChoice<Elemento> dropDownChoice = componentFactory.newDropDownChoice("elemento", "Elemento", false, "id", "nomeECodigo", new LambdaModel<Elemento>(
				this::getElemento, this::setElemento), listaElementos(), (target) -> atualizarListaSubelementos(target));
		dropDownChoice.setNullValid(true);
		return dropDownChoice;
	}

	private List<Elemento> listaElementos() {
		return elementoService.buscarTodos();
	}

	void atualizarListaSubelementos(AjaxRequestTarget target) {
		dropSubElemento.setChoices(listaSubelementos());
		target.appendJavaScript("atualizaCssDropDown();");
		target.add(panelSubelemento);
	}

	private List<SubElemento> listaSubelementos() {

		if (elemento != null && elemento.getId() != null) {
			Long id = elemento.getId();
			return subElementoService.buscarPeloElementoId(id);
		}
		return Collections.emptyList();
	}

	private InfraDropDownChoice<SubElemento> getDropDownChoiceSubelemento() {
		InfraDropDownChoice<SubElemento> dropDownChoice = componentFactory.newDropDownChoice("subElemento", "Subelemento", false, "id", "nomeECodigo", null, listaSubelementos(),
				null);
		dropDownChoice.setOutputMarkupId(true);
		dropDownChoice.setNullValid(true);
		return dropDownChoice;
	}

	private class PanelGridResultados extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelGridResultados(String id) {
			super(id);

			bemProvider = new BemProvider();
			add(getDataViewBem());
			add(getDropItensPorPagina());

			// add(getDataViewBensAdicionados());//dataBensAdicionado
			add(getButtonPick1()); // btnPick1
			add(getPaginator());

			add(newOrderByBorderResult("orderByNomeBemResult", NOME_BEM, bemProvider));
			add(newOrderByBorderResult("orderByDescricaoBemResult", "descricaoBem", bemProvider));
		}
	}

	private OrderByBorder<String> newOrderByBorder(String id, String property, BemSelecionadosProvider dp) {
		return new OrderByBorder<String>(id, property, dp);
	}

	private OrderByBorder<String> newOrderByBorderResult(String id, String property, BemProvider dp) {
		return new OrderByBorder<String>(id, property, dp);
	}

	public class PanelDataViewAdicionado extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelDataViewAdicionado(String id) {
			super(id);

			this.setOutputMarkupId(true);
			dp = new BemSelecionadosProvider();

			add(getDataViewBensAdicionados(dp));
			add(getPaginatorAdicionado());
			add(getButtonPick2()); // btnPick2
			add(getDropDownPaginatorAdicionado());

			add(newOrderByBorder("orderByNomeBem", NOME_BEM_ADICIONADO, dp));
			add(newOrderByBorder("orderByDescricao", "descricaoBemAdicionado", dp));
		}
	}

	private class BemProvider extends SortableDataProvider<Bem, String> {
		private static final long serialVersionUID = 1L;

		public BemProvider() {
			setSort("nomeBemAdicionadoResult", SortOrder.ASCENDING);
		}

		@Override
		public Iterator<Bem> iterator(long first, long size) {

			SortParam<String> sort = this.getSort();
			String property = sort.getProperty();

			if (NOME_BEM.equalsIgnoreCase(property)) {
				Collections.sort(listBensPesquisados, Bem.getComparator(sort.isAscending() ? 1 : -1, "nome"));
			} else {
				Collections.sort(listBensPesquisados, Bem.getComparator(sort.isAscending() ? 1 : -1, "descricao"));
			}

			List<Bem> bemTemp = new ArrayList<Bem>();
			int firstTemp = 0;
			int flagTemp = 0;
			for (Bem k : listBensPesquisados) {
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
			return listBensPesquisados.size();
		}

		@Override
		public IModel<Bem> model(Bem object) {
			return new CompoundPropertyModel<Bem>(object);
		}
	}

	private class BemSelecionadosProvider extends SortableDataProvider<KitBem, String> {
		private static final long serialVersionUID = 1L;

		public BemSelecionadosProvider() {
			setSort(NOME_BEM_ADICIONADO, SortOrder.ASCENDING);
		}

		@Override
		public Iterator<KitBem> iterator(long first, long size) {

			List<KitBem> bemTemp = new ArrayList<KitBem>();

			SortParam<String> sort = this.getSort();
			String property = sort.getProperty();

			if (NOME_BEM_ADICIONADO.equalsIgnoreCase(property)) {
				Collections.sort(listaBensSelecionadosDireito, KitBem.getComparator(sort.isAscending() ? 1 : -1, "nome"));
			} else {
				Collections.sort(listaBensSelecionadosDireito, KitBem.getComparator(sort.isAscending() ? 1 : -1, "descricao"));
			}

			int firstTemp = 0;
			int flagTemp = 0;
			for (KitBem k : listaBensSelecionadosDireito) {
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
			return listaBensSelecionadosDireito.size();
		}

		@Override
		public IModel<KitBem> model(KitBem object) {
			return new CompoundPropertyModel<KitBem>(object);
		}
	}

	public InfraAjaxPagingNavigator getPaginator() {
		if (paginator == null) {
			paginator = new InfraAjaxPagingNavigator(PAGINATOR1, dataViewBem);
		}
		return paginator;
	}

	public InfraAjaxPagingNavigator getPaginatorAdicionado() {
		if (paginatorAdicionado == null) {
			paginatorAdicionado = new InfraAjaxPagingNavigator("paginatorAdicionado", dataviewAdicionado);
		}
		return paginatorAdicionado;
	}

	public DropDownChoice<Integer> getDropDownPaginatorAdicionado() {
		DropDownChoice<Integer> dropPaginator = new DropDownChoice<Integer>("valoresPaginatorAdicionado", new PropertyModel<Integer>(this, VALORS), Constants.QUANTIDADE_ITENS_TABELA);
		dropPaginator.setOutputMarkupId(true);
		actionDropDownPaginatorAdicionado(dropPaginator);
		return dropPaginator;
	}

	private class PanelSubelemento extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelSubelemento(String id) {
			super(id);

			add(getDropDownChoiceElemento());
			dropSubElemento = getDropDownChoiceSubelemento();
			add(dropSubElemento);
		}

	}

	private DataView<Bem> getDataViewBem() {
		dataViewBem = new DataView<Bem>("bens", bemProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<Bem> item) {
				item.add(new Label(NOME_BEM));
				item.add(new Label("descricaoBem"));
				item.add(getButtonAdicionarBem(item)); // btnAdicionarBem

				item.add(AttributeModifier.replace("class", new AbstractReadOnlyModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					public String getObject() {
						return (item.getIndex() % 2 == 1) ? "even" : "odd";
					}
				}));
			}

		};
		dataViewBem.setItemsPerPage(itensPorPagina);
		return dataViewBem;
	}

	public DataView<KitBem> getDataViewBensAdicionados(BemSelecionadosProvider dp) {
		if (listaBensSelecionadosDireito == null) {
			listaBensSelecionadosDireito = new ArrayList<KitBem>();
		}

		dataviewAdicionado = new DataView<KitBem>("dataBensAdicionado", dp) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<KitBem> item) {
				item.clearOriginalDestination();
				item.add(new Label(NOME_BEM_ADICIONADO, item.getModelObject().getBem().getNomeBem()));
				item.add(new Label("descricaoBemAdicionado", item.getModelObject().getBem().getDescricaoBem()));
				item.add(getButtonRemoverBem(item)); // btnRemoverBem
				item.add(new QuantidadeItemPanel("quantidadeItemPanel", item));
			}
		};
		dataviewAdicionado.setItemsPerPage(valorSelecionado);

		return dataviewAdicionado;
	}

	private class QuantidadeItemPanel extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public QuantidadeItemPanel(String id, Item<KitBem> item) {
			super(id);
			TextField<Integer> tfValorUtilizar = new TextField<Integer>("quantidade", new PropertyModel<Integer>(item.getModel(), "quantidade"));
			tfValorUtilizar.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					// Desabilita o Feedback listener
					RequestCycle.get().setMetaData(FeedbackPanelListener.SKIP_FEEDBACK_LISTENER, Boolean.TRUE);
					item.getModelObject();

				}
			});
			add(tfValorUtilizar);
		}
	}

	private DropDownChoice<Integer> getDropItensPorPagina() {
		DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPagina", new LambdaModel<Integer>(this::getItensPorPagina, this::setItensPorPagina),
		        Constants.QUANTIDADE_ITENS_TABELA);
		dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				dataViewBem.setItemsPerPage(getItensPorPagina());
				target.add(panelGridResultados);
			};
		});
		return dropDownChoice;
	}

	private Button getButtonConfirmar() {
		return componentFactory.newButton("btnAvancar", () -> acaoConfirmar());
	}

	public InfraAjaxFallbackLink<Void> getButtonVoltar() {
		return componentFactory.newAjaxFallbackLink("btnVoltar", (target) -> acaoVoltar());
	}

	public InfraAjaxFallbackLink<Void> getButtonPick1() {
		return componentFactory.newAjaxFallbackLink("btnPick1", (target) -> actionPick1(target));
	}

	public InfraAjaxFallbackLink<Void> getButtonPick2() {
		return componentFactory.newAjaxFallbackLink("btnPick2", (target) -> actionPick2(target));
	}

	public AjaxSubmitLink getButtonAdicionarBem(Item<Bem> item) {
		return new AjaxSubmitLink("btnAdicionarBem", form) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				actionAdicionarKit(target, item);
			}
		};
	}

	public AjaxSubmitLink getButtonRemoverBem(Item<KitBem> item) {
		return new AjaxSubmitLink("btnRemoverBem", form) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				actionRemoverKit(target, item);
			}
		};
	}

	/*
	 * AS AÇÕES SERÃO IMPLEMENTAS ABAIXO
	 */

	public void copiarParaListaTemp() {
		listaBensSelecionadosDireitoTemp = new ArrayList<KitBem>();
		for (KitBem bem : listaBensSelecionadosDireito) {
			KitBem bem2 = new KitBem();
			bem2.setBem(bem.getBem());
			bem2.setId(bem.getId());
			bem2.setKit(bem.getKit());
			bem2.setQuantidade(bem.getQuantidade());
			listaBensSelecionadosDireitoTemp.add(bem2);
		}
	}

	public void cancelarAcoesLista() {
		listaBensSelecionadosDireito.clear();
		for (KitBem bem : listaBensSelecionadosDireitoTemp) {
			listaBensSelecionadosDireito.add(bem);
		}
	}

	private void actionPick1(AjaxRequestTarget target) {
		for (int i = listBensPesquisados.size() - 1; i > -1; i--) {
			int contain = 0;
			for (KitBem select : listaBensSelecionadosDireito) {
				if (select.getBem().equals(listBensPesquisados.get(i))) {
					contain++;
					break;
				}
			}

			// Se este bem não estiver adicionado ainda a lista a direita e
			// removendo a esquerda
			if (contain == 0) {
				KitBem bem = new KitBem();
				bem.setBem(listBensPesquisados.get(i));

				listaBensSelecionadosDireito.add(bem);
			}
			contain = 0;
		}

		listBensPesquisados.clear();

		atualizarDataViews(target);
	}

	private void actionPick2(AjaxRequestTarget target) {
		for (KitBem bem : listaBensSelecionadosDireito) {
			if (!listBensPesquisados.contains(bem.getBem())) {
				listBensPesquisados.add(bem.getBem());
			}
		}

		listaBensSelecionadosDireito.clear();

		atualizarDataViews(target);
	}

	private void actionAdicionarKit(AjaxRequestTarget target, Item<Bem> item) {

		KitBem bem = new KitBem();
		bem.setBem(item.getModelObject());
		listaBensSelecionadosDireito.add(0, bem);

		listBensPesquisados.remove(item.getModelObject());

		atualizarDataViews(target);
	}

	private void actionRemoverKit(AjaxRequestTarget target, Item<KitBem> item) {

		KitBem bemRemove = item.getModelObject();
		int cont = 0;
		for (KitBem bem : listaBensSelecionadosDireito) {
			if (bemRemove.getBem().getId().intValue() == bem.getBem().getId().intValue()) {
				listBensPesquisados.add(0, bem.getBem());
				listaBensSelecionadosDireito.remove(cont);
				break;
			}
			cont++;
		}
		atualizarDataViews(target);
	}

	@SuppressWarnings("rawtypes")
	public void actionDropDownPaginatorAdicionado(DropDownChoice dropElemento) {
		dropElemento.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {

				paginasAdicionado = valorSelecionado;
				dataviewAdicionado.setItemsPerPage(paginasAdicionado);
				panelDataViewAdicionado.addOrReplace(dataviewAdicionado);
				target.add(panelDataViewAdicionado);
			}
		});
	}

	private void atualizarDataViews(AjaxRequestTarget target) {
		panelGridResultados.addOrReplace(getDataViewBem());
		panelGridResultados.addOrReplace(new InfraAjaxPagingNavigator("pagination", dataViewBem));
		panelDataViewAdicionado.addOrReplace(getDataViewBensAdicionados(dp));
		panelDataViewAdicionado.addOrReplace(new InfraAjaxPagingNavigator("paginatorAdicionado", dataviewAdicionado));
		panelGridResultados.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR1, dataViewBem));

		target.add(panelGridResultados);
		target.add(panelDataViewAdicionado);
	}

	private void acaoConfirmar() {

		int cont = 0;
		if (listaBensSelecionadosDireito != null && listaBensSelecionadosDireito.isEmpty()) {
			addMsgError("Não existem Bens selecionados na lista.");
			return;
		}

		for (KitBem prog : listaBensSelecionadosDireito) {
			if (prog.getQuantidade() == null || prog.getQuantidade() < 1) {
				cont++;
			}
		}

		if (cont > 0) {
			addMsgError("Existe Bem adicionado a lista com a quantidade '0', informe um valor para estes campos.");
			return;
		}

		if (atualizandoLista) {
			getSession().info("Lista de Bens atualizada com sucesso.");
		} else {
			getSession().info("Lista de Bens criada com sucesso.");
		}

		KitPage page = (KitPage) backPage;
		page.setListaKit(listaBensSelecionadosDireito);
		setResponsePage(backPage);
	}

	private void acaoVoltar() {
		cancelarAcoesLista();
		KitPage page = (KitPage) backPage;
		page.setListaKit(listaBensSelecionadosDireito);
		setResponsePage(backPage);
	}

	protected void visualizar(Item<Bem> item) {
		Bem b = item.getModelObject();
		setResponsePage(new BemPage(null, b, this, true,false));
	}

	public Elemento getElemento() {
		return elemento;
	}

	public void setElemento(Elemento elemento) {
		this.elemento = elemento;
	}

	public Bem getEntity() {
		return entity;
	}

	public void setEntity(Bem entity) {
		this.entity = entity;
	}

	public Integer getItensPorPagina() {
		return itensPorPagina;
	}

	public void setItensPorPagina(Integer itensPorPagina) {
		this.itensPorPagina = itensPorPagina;
	}

	public boolean isPesquisado() {
		return pesquisado;
	}

	public void setPesquisado(boolean pesquisado) {
		this.pesquisado = pesquisado;
	}

	public DataView<KitBem> getDataviewAdicionado() {
		return dataviewAdicionado;
	}

	public void setDataviewAdicionado(DataView<KitBem> dataviewAdicionado) {
		this.dataviewAdicionado = dataviewAdicionado;
	}

	public String getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(String quantidade) {
		this.quantidade = quantidade;
	}

}