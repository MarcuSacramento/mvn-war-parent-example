package br.gov.mj.side.web.view.fornecedor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.HistoricoComunicacaoGeracaoOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;
import br.gov.mj.side.web.dto.ContratoDto;
import br.gov.mj.side.web.service.ContratoService;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.MascaraUtils;
import br.gov.mj.side.web.util.SortableContratoDataProvider;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.template.TemplatePage;

/**
 * @author joao.coutinho
 * @since 18/03/2016 - Tela de contratos do Painel Fornecedor
 */
@AuthorizeInstantiation({
		FornecedorContratoPage.ROLE_MANTER_CONTRATO_FORNECEDOR_VISUALIZAR,
		FornecedorContratoPage.ROLE_MANTER_CONTRATO_FORNECEDOR_EXECUTAR })
public class FornecedorContratoPage extends TemplatePage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1540246352696521871L;

	// #######################################_VARIAVEIS_############################################
	private Form<Contrato> form;
	//private List<Contrato> listaContrato = new ArrayList<Contrato>();
	private Contrato contrato = new Contrato();
	private SortableContratoDataProvider dp;
	private List<OrdemFornecimentoContrato> listaTemporariaOrdemFornecimento = new ArrayList<OrdemFornecimentoContrato>();
	private List<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> listaTemporariaHistoricoComunicacao = new ArrayList<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato>();
	private ContratoDto contratoDto = new ContratoDto();

	// #######################################_CONSTANTE_############################################
	private static final boolean VERDADEIRO = true;
	private static final int TAMANHO_PAGINADOR = 10;
	private Integer itensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;
	public static final String ROLE_MANTER_CONTRATO_FORNECEDOR_VISUALIZAR = "manter_contrato_fornecedor:visualizar";
	public static final String ROLE_MANTER_CONTRATO_FORNECEDOR_EXECUTAR = "manter_contrato_fornecedor:executar";

	// #######################################_ELEMENTOS_DO_WICKET_############################################
	private DataView<Contrato> newListaContrato;
	private PanelContrato panelContrato;
	@SuppressWarnings("rawtypes")
	private InfraAjaxFallbackLink btnVisualizarRegistro;
	@SuppressWarnings("rawtypes")
	private InfraAjaxFallbackLink btnExecutarOF;
	private PanelButton panelButton;

	// #####################################_INJEÇÃO_DE_DEPENDENCIA_##############################################
	@Inject
	private ComponentFactory componentFactory;

	@Inject
	private ContratoService contratoService;

	@Inject
	private GenericEntidadeService genericEntidadeService;
	@Inject
	private OrdemFornecimentoContratoService ordemFornecimentoContratoService;

	// #####################################_CONSTRUTOR_##############################################
	public FornecedorContratoPage(PageParameters pageParameters) {
		super(pageParameters);
		initVariaveis();
		initComponentes();
	}

	/**
	 * Sprint 9
	 * 
	 * @author joao.coutinho
	 * @since 18/03/2016
	 * @see FornecedorContratoPage
	 */
	private void initVariaveis() {
		setTitulo("Contratos");
		contratoDto.setFornecedor((Entidade) getSessionAttribute("entidade"));
		//listaContrato = contratoService.buscarSemPaginacao(contratoDto);
	}

	/**
	 * Sprint 9
	 * 
	 * @author joao.coutinho
	 * @since 18/03/2016
	 * @see FornecedorContratoPage
	 */
	private void initComponentes() {
		form = componentFactory.newForm("form",
				new CompoundPropertyModel<Contrato>(contrato));
		form.add(componentFactory.newLink("lnkDashboard", HomePage.class));
		form.add(new Label("lblNomePage", getTitulo()));
		form.add(panelContrato = newPanelContrato());
		form.add(panelButton = newPanelButton());
		add(form);
	}

	// ####################################_PAINES_###############################################

	/**
	 * Sprint 9
	 * 
	 * @author joao.coutinho
	 * @since 18/03/2016 - Painel Contratos Fornecedor
	 */
	public PanelContrato newPanelContrato() {
		panelContrato = new PanelContrato();
		setOutputMarkupId(VERDADEIRO);
		return panelContrato;
	}

	/**
	 * Sprint 9
	 * 
	 * @author joao.coutinho
	 * @since 18/03/2016
	 * @see newPanelContrato()
	 */
	private class PanelContrato extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelContrato() {
			super("panelContrato");

			dp = new SortableContratoDataProvider(contratoService,contratoDto);
			add(newDataViewContrato(dp));
			add(newDropItensPorPagina());
			add(new OrderByBorder<String>("orderByContrato", "numeroContrato", dp));
			add(new OrderByBorder<String>("orderByDataVigenciaFimContrato", "dataVigenciaFimContrato", dp));
			add(new InfraAjaxPagingNavigator("paginator", newListaContrato));
		}
	}

	/**
	 * Sprint 10 - painel do botão
	 * 
	 * @author joao.coutinho
	 * @since 21/03/2016
	 */
	public PanelButton newPanelButton() {
		panelButton = new PanelButton();
		return panelButton;
	}

	/**
	 * Sprint 10 - painel do botão
	 * 
	 * @author joao.coutinho
	 * @since 21/03/2016
	 * @see newPanelButton()
	 */
	@SuppressWarnings("serial")
	private class PanelButton extends WebMarkupContainer {
		public PanelButton() {
			super("panelButton");
			setOutputMarkupId(Boolean.TRUE);
			add(newButtonVoltar());
		}
	}

	// ####################################_COMPONENTE_WICKET_###############################################

	/**
	 * Sprint 9
	 * 
	 * @author joao.coutinho
	 * @since 18/03/2016
	 * @see PanelContrato
	 */
	@SuppressWarnings("serial")
	private DataView<Contrato> newDataViewContrato(
			SortableContratoDataProvider dp) {	newListaContrato = new DataView<Contrato>("listaContratosNewDataView",dp) {
			@Override
			protected void populateItem(Item<Contrato> item) {
				List<OrdemFornecimentoContrato> listaOrdemFornecimento = verificarComunicadoOrdemFornecimento(item.getModelObject());
				item.getModelObject().setListaOrdemFornecimento(listaOrdemFornecimento);
				BigDecimal saldoContratual = contratoService.buscarValorDoContrato(item.getModelObject());
				BigDecimal saldoExecutadoDoContrato = contratoService.buscarSaldoExecutadoDoContrato(item.getModelObject(),false);
				BigDecimal saldoAExecutar = saldoContratual.subtract(saldoExecutadoDoContrato);

				item.add(new Label("numeroContrato"));
				item.add(new Label("programa.codigoIdentificadorProgramaPublicadoENomePrograma"));
				item.add(new Label("ofEmitida",	(listaOrdemFornecimento.size() > 0 ? "Sim" : "Não")));
				item.add(new Label("saldoContratual", MascaraUtils.formatarMascaraDinheiro(saldoContratual)));
				item.add(new Label("saldoExecutar", MascaraUtils.formatarMascaraDinheiro(saldoAExecutar)));
				item.add(new Label("dataVigenciaFimContrato",formatarDataBr(item.getModelObject().getDataVigenciaFimContrato())));

				btnVisualizarRegistro = componentFactory.newAjaxFallbackLink("btnVisualizarRegistro",(target) -> visualizar(target, item));
				authorize(btnVisualizarRegistro, RENDER,ROLE_MANTER_CONTRATO_FORNECEDOR_VISUALIZAR);
				item.add(btnVisualizarRegistro);

				btnExecutarOF = componentFactory.newAjaxFallbackLink("btnExecutarOF", (target) -> executarOF(target, item));
				btnExecutarOF.setVisible(listaOrdemFornecimento.size() > 0 ? true: false);
				authorize(btnExecutarOF, RENDER,ROLE_MANTER_CONTRATO_FORNECEDOR_EXECUTAR);
				item.add(btnExecutarOF);

			}
		};
		newListaContrato.setItemsPerPage(TAMANHO_PAGINADOR);
		return newListaContrato;
	}

	/**
	 * Sprint 9
	 * 
	 * @author joao.coutinho
	 * @since 30/03/2016
	 * @see newDataViewContrato
	 */
	private List<OrdemFornecimentoContrato> verificarComunicadoOrdemFornecimento(
			Contrato contrato) {
		List<OrdemFornecimentoContrato> listaOrdemFornecimento = ordemFornecimentoContratoService
				.buscarOrdemFornecimentoContrato(contrato);
		this.listaTemporariaOrdemFornecimento = new ArrayList<OrdemFornecimentoContrato>();
		this.listaTemporariaHistoricoComunicacao = new ArrayList<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato>();
		for (OrdemFornecimentoContrato ordemFornecedor : listaOrdemFornecimento) {
			HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historicoOrdem = ordemFornecimentoContratoService
					.buscarUltimoHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(
							ordemFornecedor, true);
			if (historicoOrdem != null) {
				this.listaTemporariaHistoricoComunicacao.add(historicoOrdem);
				ordemFornecedor
						.setListaHistoricoComunicacaoGeracao(this.listaTemporariaHistoricoComunicacao);
				this.listaTemporariaOrdemFornecimento.add(ordemFornecedor);
			}
		}
		return this.listaTemporariaOrdemFornecimento;
	}

	/**
	 * Sprint 9
	 * 
	 * @author joao.coutinho
	 * @since 18/03/2016
	 * @see newButtonVisualizarDataView
	 */
	private void executarOF(AjaxRequestTarget target, Item<Contrato> item) {
		Contrato contrato = item.getModelObject();
		setResponsePage(new OrdemFornecimentoContratoPage(getPageParameters(),
				contrato));
	}

	/**
	 * Sprint 9
	 * 
	 * @author joao.coutinho
	 * @since 18/03/2016
	 * @see newButtonVisualizarDataView
	 */
	private void visualizar(AjaxRequestTarget target, Item<Contrato> item) {
		Contrato contrato = item.getModelObject();
		setResponsePage(new ContratoPage2(getPageParameters(), this, contrato,
				contrato.getPrograma(), true, false));
	}

	/**
	 * Sprint 10 - painel do botão
	 * 
	 * @author joao.coutinho
	 * @since 21/03/2016
	 * @see PanelButton
	 */
	private InfraAjaxFallbackLink<Void> newButtonVoltar() {
	    return componentFactory.newAjaxFallbackLink("btnVoltar", (target) -> setResponsePage(HomePage.class));
	}

	private DropDownChoice<Integer> newDropItensPorPagina() {
		DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>(
				"itensPorPagina", new LambdaModel<Integer>(
						this::getItensPorPagina, this::setItensPorPagina),
						Constants.QUANTIDADE_ITENS_TABELA);
		dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				newListaContrato.setItemsPerPage(getItensPorPagina());
				target.add(panelContrato);
			};
		});
		return dropDownChoice;
	}

	public String formatarDataBr(LocalDate dataDocumento) {
		DateTimeFormatter sdfPadraoBR = DateTimeFormatter
				.ofPattern("dd/MM/yyyy");
		if (dataDocumento != null) {
			dataDocumento.format(sdfPadraoBR);
			return sdfPadraoBR.format(dataDocumento);
		}
		return " - ";
	}

	public Integer getItensPorPagina() {
		return itensPorPagina;
	}

	public void setItensPorPagina(Integer itensPorPagina) {
		this.itensPorPagina = itensPorPagina;
	}

}
