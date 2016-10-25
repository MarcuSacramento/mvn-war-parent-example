package br.gov.mj.side.web.view.contrato.painel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import br.gov.mj.apoio.entidades.Regiao;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.BemUf;
import br.gov.mj.side.entidades.programa.licitacao.SelecaoItem;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.web.dto.BemUfDto;
import br.gov.mj.side.web.dto.ItemBemDto;
import br.gov.mj.side.web.dto.LicitacaoNomeModelDto;
import br.gov.mj.side.web.service.FormatacaoItensContratoService;
import br.gov.mj.side.web.service.LicitacaoProgramaService;
import br.gov.mj.side.web.view.contrato.ContratoPage;

@AuthorizeInstantiation({ PanelObjetosContrato.ROLE_MANTER_CONTRATO_VISUALIZAR, PanelObjetosContrato.ROLE_MANTER_CONTRATO_INCLUIR, PanelObjetosContrato.ROLE_MANTER_CONTRATO_ALTERAR,
		PanelObjetosContrato.ROLE_MANTER_CONTRATO_EXCLUIR })
public class PanelObjetosContrato extends Panel {

	private static final long serialVersionUID = 1L;

	public static final String ROLE_MANTER_CONTRATO_VISUALIZAR = "manter_contrato:visualizar";
	public static final String ROLE_MANTER_CONTRATO_INCLUIR = "manter_contrato:incluir";
	public static final String ROLE_MANTER_CONTRATO_ALTERAR = "manter_contrato:alterar";
	public static final String ROLE_MANTER_CONTRATO_EXCLUIR = "manter_contrato:excluir";

	InfraDropDownChoice<AgrupamentoLicitacao> dropAgrup;

	private PanelDrop panelDrop;
	private PanelDataView panelDataView;
	private Contrato contrato;
	private Page backPage;
	private Boolean cadastroNovo;
	private boolean readOnly = false;
	private ContratoPage page;
	private AgrupamentoLicitacao agrupamentoEscolhido = new AgrupamentoLicitacao();
	private DataView<ItemBemDto> dataViewGruposSelecionados;

	private List<AgrupamentoLicitacao> listaDeGruposEmUsoFixa = new ArrayList<AgrupamentoLicitacao>();
	private List<AgrupamentoLicitacao> todosAgrupamentosDoPrograma = new ArrayList<AgrupamentoLicitacao>();
	private List<AgrupamentoLicitacao> listaDeItensDisponiveis = new ArrayList<AgrupamentoLicitacao>();
	private List<AgrupamentoLicitacao> listaDeItensSelecionados = new ArrayList<AgrupamentoLicitacao>();
	private List<AgrupamentoLicitacao> listaDeItensPersistidos = new ArrayList<AgrupamentoLicitacao>();

	private List<ItemBemDto> listaDeGrupos = new ArrayList<ItemBemDto>();

	private boolean primeiroLoop = true;
	private Integer contadorDeItensCriados = 1;

	private Label labelMensagem;
	private Model<String> mensagem = Model.of("");
	private String msg = "";

	@Inject
	private ComponentFactory componentFactory;
	@Inject
	private LicitacaoProgramaService licitacaoService;
	@Inject
	private FormatacaoItensContratoService formatacaoItensContratoService;

	public PanelObjetosContrato(String id, Page backPage, Contrato contrato, List<AgrupamentoLicitacao> agrupamentoItem, Boolean cadastroNovo) {
		super(id);
		setOutputMarkupId(true);
		this.backPage = backPage;
		this.contrato = contrato;
		this.todosAgrupamentosDoPrograma = agrupamentoItem;
		this.cadastroNovo = cadastroNovo;

		initVariaveis();
		initComponents();
	}

	private void initVariaveis() {
		page = (ContratoPage) backPage;

		if (contrato != null || contrato.getId() != null) {
			montaODropDownSomenteComOsGruposNaoSelecionados();
		}

		// Monta o dataView com os grupos selecionados
		montarListasDeItensEGruposSalvas();
	}

	private void initComponents() {
		add(panelDrop = new PanelDrop("panelDrop"));
		add(panelDataView = new PanelDataView("panelDataView"));

		// Mensagens de erro
		labelMensagem = new Label("mensagemErro", mensagem);
		labelMensagem.setEscapeModelStrings(false);
		add(labelMensagem);
	}

	// PAINEIS
	private class PanelDrop extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelDrop(String id) {
			super(id);
			setOutputMarkupId(true);

			add(newButtonAdicionarItem()); // btnAdicionarItem
			add(newDropDownGrupoItem()); // dropGrupoItem
		}
	}

	// Panel para informar o nome do item
	private class PanelNomeItem extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelNomeItem(String id, Item<ItemBemDto> item) {
			super(id);
			setOutputMarkupId(true);

			add(newLabelNomeGrupo(item)); // lblNomeGrupoItem
			setEnabled(botoesTabelaEnabled());
		}
	}

	private class PanelDataView extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelDataView(String id) {
			super(id);
			setOutputMarkupId(true);

			add(newDataViewGruposSelecionados()); // dataGruposSelecionados
		}
	}

	// CRIAÇÃO DOS COMPONENTES DO PANEL

	private AjaxFallbackButton newButtonRemoverGrupo(Item<ItemBemDto> item) {
		AjaxFallbackButton buttonRemover = componentFactory.newAjaxFallbackButton("btnRemoverGrupo", null, (target, form) -> removerGrupo(target, item));
		buttonRemover.setDefaultFormProcessing(false);
		buttonRemover.setEnabled(habilitarBotaoExcluirGrupoItem());
		return buttonRemover;
	}

	private Label newLabelNomeGrupo(Item<ItemBemDto> item) {
		Label label = new Label("lblNomeGrupoItem", item.getModelObject().getNomeModelDto().getNomeGrupo());
		label.setOutputMarkupId(true);
		return label;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public InfraDropDownChoice<AgrupamentoLicitacao> newDropDownGrupoItem() {
		dropAgrup = componentFactory.newDropDownChoice("dropGrupoItem", "Grupo / Item", false, "id", "nomeAgrupamento", new PropertyModel(this, "agrupamentoEscolhido"), listaDeItensDisponiveis, null);
		actionDropDown(dropAgrup);
		dropAgrup.setOutputMarkupId(true);
		dropAgrup.setNullValid(true);
		return dropAgrup;
	}

	private AjaxSubmitLink newButtonAdicionarItem() {
		AjaxSubmitLink buttonAdicionar = new AjaxSubmitLink("btnAdicionarItem") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				super.onSubmit(target, form);

				adicionarItem(target);
			}
		};
		buttonAdicionar.setDefaultFormProcessing(false);
		buttonAdicionar.setOutputMarkupId(true);
		buttonAdicionar.setEnabled(true);
		buttonAdicionar.setVisible(true);
		return buttonAdicionar;
	}

	private DataView<ItemBemDto> newDataViewGruposSelecionados() {
		dataViewGruposSelecionados = new DataView<ItemBemDto>("dataGruposSelecionados", new ProviderGruposSelecionados(listaDeGrupos)) {
			private static final long serialVersionUID = 1L;
			private String grupoAtual = "";
			private Integer idGrupoAtual = 0;
			private Integer idGrupoAnterior;
			private String nomeAnterior = "";
			private boolean clicadoPaginator = true;

			@Override
			protected void populateItem(Item<ItemBemDto> item) {

				String regiao = verificarRegioes(item);
				String uf = verificarUfs(item);
				Integer quantidade = somarQuantidadeItens(item);

				WebMarkupContainer containerRowspan = new WebMarkupContainer("containerRowspan");
				WebMarkupContainer containerBotao = new WebMarkupContainer("containerExcluir");

				if (idGrupoAtual == 0) {
					idGrupoAtual = item.getModelObject().getContadorTemp();
					idGrupoAnterior = item.getModelObject().getContadorTemp();
				} else {
					if (idGrupoAtual != item.getModelObject().getContadorTemp()) {
						idGrupoAtual = item.getModelObject().getContadorTemp();
					}
				}
				boolean mostrarPainel = true;

				int tamanho = quantidadeItensMesclar(clicadoPaginator, idGrupoAtual, item);
				clicadoPaginator = false;

				if (primeiroLoop || item.getModelObject().getContadorTemp() != idGrupoAnterior) {
					containerRowspan.add(new AttributeAppender("rowspan", new Model<Integer>(tamanho), " "));
					containerBotao.add(new AttributeAppender("rowspan", new Model<Integer>(tamanho), " "));
					primeiroLoop = false;
				} else {
					containerRowspan.setVisible(false);
					containerRowspan.setVisible(false);
					mostrarPainel = false;
				}

				PanelNomeItem panel = new PanelNomeItem("panelNomeGrupo", item);
				containerRowspan.add(panel);
				item.add(containerRowspan);

				item.add(new Label("lblNomeBemGrupo", item.getModelObject().getListaDeBens().get(0).getBem().getNomeBem()));
				item.add(new Label("lblDescricaoBemGrupo", item.getModelObject().getListaDeBens().get(0).getBem().getDescricaoBem()));
				item.add(new Label("lblRegiaoGrupo", regiao));
				item.add(new Label("lblUfGrupo", uf));
				item.add(new Label("lblQuantidadeARegistrarGrupo", quantidade));

				Button buttonExcluir = newButtonRemoverGrupo(item); // btnRemoverGrupo
				buttonExcluir.setVisible(mostrarPainel);
				containerBotao.add(buttonExcluir); // btnRemoverItem
				item.add(containerBotao);

				if (idGrupoAnterior != idGrupoAtual) {
					idGrupoAnterior = idGrupoAtual;
				}
			}

			@Override
			protected void onBeforeRender() {
				primeiroLoop = true;
				clicadoPaginator = true;
				super.onBeforeRender();
			}

			@Override
			protected void onAfterRender() {
				idGrupoAtual = 0;
				super.onAfterRender();
			}
		};
		dataViewGruposSelecionados.setOutputMarkupId(true);
		return dataViewGruposSelecionados;
	}

	// PROVIDERS
	private class ProviderGruposSelecionados extends SortableDataProvider<ItemBemDto, String> {
		private static final long serialVersionUID = 1L;

		private List<ItemBemDto> lista = new ArrayList<ItemBemDto>();

		public ProviderGruposSelecionados(List<ItemBemDto> item) {
			this.lista = item;
		}

		@Override
		public Iterator<ItemBemDto> iterator(long first, long size) {

			List<ItemBemDto> listTemp = new ArrayList<ItemBemDto>();
			int firstTemp = 0;
			int flagTemp = 0;
			for (ItemBemDto k : lista) {
				if (firstTemp >= first) {
					if (flagTemp <= size) {
						listTemp.add(k);
						flagTemp++;
					}
				}
				firstTemp++;
			}

			return listTemp.iterator();
		}

		@Override
		public long size() {
			return lista.size();
		}

		@Override
		public IModel<ItemBemDto> model(ItemBemDto object) {
			return new CompoundPropertyModel<ItemBemDto>(object);
		}
	}

	// ações
	/*private boolean verificaItemFormatado(Item<ItemBemDto> item){
		 List<Bem> listaBensRemanescentes = formatacaoItensContratoService.buscarListaBensRemanescentes(contrato);
		 for (Bem bem : listaBensRemanescentes) {
			if(bem.equals(item.getModelObject().get))
		}
		
	}*/
	

	// Monta o DropDown com os grupos restantes
	private void montaODropDownSomenteComOsGruposNaoSelecionados() {
		for (AgrupamentoLicitacao agrup : todosAgrupamentosDoPrograma) {
			if (agrup.getContrato() == null) {
				listaDeItensDisponiveis.add(agrup);
				listaDeGruposEmUsoFixa.add(agrup);
			} else {
				if (contrato != null && contrato.getId() != null && contrato.getId().intValue() == agrup.getContrato().getId().intValue()) {
					listaDeItensSelecionados.add(agrup);
					listaDeGruposEmUsoFixa.add(agrup);
				}

				// Grava todos os grupos já persistidos
				listaDeItensPersistidos.add(agrup);
			}
		}
	}

	private void montarListasDeItensEGruposSalvas() {

		LicitacaoNomeModelDto nome = new LicitacaoNomeModelDto();
		int flag = 0;
		listaDeGrupos.clear();

		for (AgrupamentoLicitacao agrup : listaDeItensSelecionados) {
			contadorDeItensCriados = agrup.getId().intValue();

			List<SelecaoItem> listaSelecaoItem = licitacaoService.buscarSelecaoItem(agrup);
			for (SelecaoItem si : listaSelecaoItem) {

				List<BemUfDto> listaBem = new ArrayList<BemUfDto>();

				if (flag == 0) {
					nome = new LicitacaoNomeModelDto();
				}
				flag++;

				ItemBemDto itemBem = new ItemBemDto();
				itemBem.setContadorTemp(contadorDeItensCriados);
				nome.setNomeGrupo(agrup.getNomeAgrupamento());
				itemBem.setContadorTemp(contadorDeItensCriados);

				if (agrup.getContrato() != null) {
					itemBem.setObjetoJaPersistido(true);
				}

				itemBem.setNomeModelDto(nome);
				itemBem.setQuantidadeRegistrar(si.getQuantidadeImediata());
				itemBem.setTipoAgrupamentoLicitacao(agrup.getTipoAgrupamentoLicitacao());
				itemBem.setUnidadeDeMedida(si.getUnidadeMedida());
				itemBem.setValorTotalImediato(si.getValorTotalImediato());
				itemBem.setValorUnitario(si.getValorUnitario());
				itemBem.setAgrupamentoLicitacao(si.getAgrupamentoLicitacao());

				List<BemUf> listBem = licitacaoService.buscarBemUf(si);
				for (BemUf bemUf : listBem) {
					BemUfDto buf = new BemUfDto();
					buf.setBem(bemUf.getBem());
					buf.setQuantidade(bemUf.getQuantidade());
					buf.setUf(bemUf.getUf());
					buf.setSelecaoItem(si);
					buf.setId(bemUf.getId());

					listaBem.add(buf);
				}
				itemBem.setListaDeBens(listaBem);
				listaDeGrupos.add(itemBem);
			}
			flag = 0;
		}
	}

	private String verificarRegioes(Item<ItemBemDto> item) {

		String stringRegiao = new String("");
		List<Regiao> reg = new ArrayList<Regiao>();

		for (BemUfDto bem : item.getModelObject().getListaDeBens()) {
			if (!reg.contains(bem.getUf().getRegiao())) {
				reg.add(bem.getUf().getRegiao());

				if (!stringRegiao.equalsIgnoreCase("")) {
					stringRegiao = stringRegiao + "/ ";
				}
				stringRegiao = stringRegiao + " " + bem.getUf().getRegiao().getSiglaRegiao() + "";
			}
		}
		return stringRegiao;
	}

	private String verificarUfs(Item<ItemBemDto> item) {

		String stringUf = new String("");
		List<Uf> reg = new ArrayList<Uf>();
		int index = 0;

		for (BemUfDto bem : item.getModelObject().getListaDeBens()) {
			if (!reg.contains(bem.getUf().getNomeSigla())) {
				reg.add(bem.getUf());

				if (index != 0) {
					stringUf += " / ";
				}
				stringUf += bem.getUf().getSiglaUf();

				index++;
			}
		}
		return stringUf;
	}

	private Integer somarQuantidadeItens(Item<ItemBemDto> item) {
		Integer quantidade = 0;

		for (BemUfDto bem : item.getModelObject().getListaDeBens()) {
			quantidade += bem.getQuantidade().intValue();
		}
		return quantidade;
	}

	/*
	 * Este metodo irá controlar a quantidade de linhas mescladas... por
	 * exemplo, existem 10 itens em um grupo chamado 'grupo ' na primeira página
	 * da tabela apareceram 4 os outros 6 deverão aparecer na página 2... ao
	 * licar no páginator para a página 2 será verificado qual o último item da
	 * página 1... se pertence ao 'grupo 1' deverá ser subtraido pelo total
	 * geral, ou seja 10 - os 4 que apareceram = 6... na página 2 o grupo 1 será
	 * mesclado em 6 linhas.
	 */
	private Integer quantidadeItensMesclar(boolean clicadoPaginator, Integer grupoAtual, Item<ItemBemDto> item) {
		List<ItemBemDto> listaComTodosBens = buscarOsItensDoGrupoSelecionado(grupoAtual);
		int quantidade = listaComTodosBens.size();
		return quantidade;
	}

	@SuppressWarnings("rawtypes")
	private void actionDropDown(DropDownChoice dropPrograma) {
		dropPrograma.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				agrupamentoEscolhido = dropAgrup.getConvertedInput();
			}
		});
	}

	public void atualizarPainelGrupoItem(AjaxRequestTarget target, List<AgrupamentoLicitacao> lista) {
		listaDeItensSelecionados.clear();
		listaDeItensDisponiveis.clear();
		listaDeItensPersistidos.clear();
		// listaDeItensDisponiveis = lista;
		todosAgrupamentosDoPrograma.clear();

		for (AgrupamentoLicitacao agrup : lista) {
			AgrupamentoLicitacao a1 = new AgrupamentoLicitacao();
			a1.setContrato(agrup.getContrato());
			a1.setId(agrup.getId());
			a1.setLicitacaoPrograma(agrup.getLicitacaoPrograma());
			a1.setListaSelecaoItem(agrup.getListaSelecaoItem());
			a1.setNomeAgrupamento(agrup.getNomeAgrupamento());
			a1.setTipoAgrupamentoLicitacao(agrup.getTipoAgrupamentoLicitacao());

			todosAgrupamentosDoPrograma.add(a1);
		}

		montaODropDownSomenteComOsGruposNaoSelecionados();
		montarListasDeItensEGruposSalvas();
		nivelarListasDisponiveisQuandoTrocarPrograma();

		listaDeGruposEmUsoFixa.clear();
		for (AgrupamentoLicitacao ag : listaDeItensDisponiveis) {
			AgrupamentoLicitacao a1 = new AgrupamentoLicitacao();
			a1.setContrato(ag.getContrato());
			a1.setId(ag.getId());
			a1.setLicitacaoPrograma(ag.getLicitacaoPrograma());
			a1.setListaSelecaoItem(ag.getListaSelecaoItem());
			a1.setNomeAgrupamento(ag.getNomeAgrupamento());
			a1.setTipoAgrupamentoLicitacao(ag.getTipoAgrupamentoLicitacao());

			listaDeGruposEmUsoFixa.add(a1);
		}

		for (AgrupamentoLicitacao ag : listaDeItensSelecionados) {
			AgrupamentoLicitacao a1 = new AgrupamentoLicitacao();
			a1.setContrato(ag.getContrato());
			a1.setId(ag.getId());
			a1.setLicitacaoPrograma(ag.getLicitacaoPrograma());
			a1.setListaSelecaoItem(ag.getListaSelecaoItem());
			a1.setNomeAgrupamento(ag.getNomeAgrupamento());
			a1.setTipoAgrupamentoLicitacao(ag.getTipoAgrupamentoLicitacao());

			listaDeGruposEmUsoFixa.add(a1);
		}

		panelDrop.addOrReplace(newDropDownGrupoItem());
		panelDataView.addOrReplace(newDataViewGruposSelecionados());

		target.appendJavaScript("atualizaCssDropDown();");
		target.add(panelDrop);
		target.add(panelDataView);
	}

	private void nivelarListasDisponiveisESelecionadas() {
		listaDeItensDisponiveis.clear();

		for (AgrupamentoLicitacao agrup : todosAgrupamentosDoPrograma) {
			if (!listaDeItensSelecionados.contains(agrup)) {
				listaDeItensDisponiveis.add(agrup);
			}
		}
	}

	private void nivelarListasDisponiveisQuandoTrocarPrograma() {
		listaDeItensDisponiveis.clear();

		for (AgrupamentoLicitacao agrup : todosAgrupamentosDoPrograma) {
			if (!listaDeItensPersistidos.contains(agrup)) {
				listaDeItensDisponiveis.add(agrup);
			}
		}
	}

	private void removerGrupo(AjaxRequestTarget target, Item<ItemBemDto> item) {

		for (AgrupamentoLicitacao agrup : listaDeItensSelecionados) {
			if (agrup.getId().intValue() == item.getModelObject().getContadorTemp()) {
				listaDeItensSelecionados.remove(agrup);
				break;
			}
		}
		atualizarENivelarPaineisAoRemoverItem(target);
	}

	private void adicionarItem(AjaxRequestTarget target) {

		msg = "";
		boolean validar = true;
		if (agrupamentoEscolhido == null || agrupamentoEscolhido.getId() == null) {
			msg = "Escolha um Item / Grupo para adicionar a lista.";
			validar = false;
		}

		mensagem.setObject(msg);
		target.add(labelMensagem);

		if (!validar) {
			return;
		}

		mensagem.setObject(msg);
		target.add(labelMensagem);

		listaDeItensSelecionados.add(agrupamentoEscolhido);
		atualizarENivelarPaineisAoRemoverItem(target);
		agrupamentoEscolhido = new AgrupamentoLicitacao();
	}

	private void atualizarENivelarPaineisAoRemoverItem(AjaxRequestTarget target) {

		nivelarListasDisponiveisESelecionadas();
		montarListasDeItensEGruposSalvas();

		listaDeItensDisponiveis.clear();

		for (AgrupamentoLicitacao agrup : listaDeGruposEmUsoFixa) {
			if (!listaDeItensSelecionados.contains(agrup)) {
				listaDeItensDisponiveis.add(agrup);
			}
		}

		panelDrop.addOrReplace(newDropDownGrupoItem());
		panelDataView.addOrReplace(newDataViewGruposSelecionados());

		target.appendJavaScript("atualizaCssDropDown();");
		target.add(panelDrop);
		target.add(panelDataView);
	}

	private void acaoTextFieldSetarModelNomeItemGrupo(TextField<?> text, Item<ItemBemDto> item) {
		text.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// Setar no model
			}
		});
	}

	private List<ItemBemDto> buscarOsItensDoGrupoSelecionado(Integer nomeGrupoApagar) {
		List<ItemBemDto> listaComTodosBens = new ArrayList<ItemBemDto>();
		for (ItemBemDto lista : listaDeGrupos) {
			if (lista.getContadorTemp() == nomeGrupoApagar) {
				listaComTodosBens.add(lista);
			}
		}
		return listaComTodosBens;
	}

	private boolean botoesTabelaEnabled() {
		if (readOnly) {
			return false;
		} else {
			return true;
		}
	}

	private boolean habilitarBotaoExcluirGrupoItem() {
		if (readOnly) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public List<AgrupamentoLicitacao> getListaDeItensSelecionados() {
		return listaDeItensSelecionados;
	}

}