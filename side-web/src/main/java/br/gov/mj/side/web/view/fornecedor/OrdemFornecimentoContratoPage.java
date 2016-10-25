package br.gov.mj.side.web.view.fornecedor;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.enums.EnumPerfilEntidade;
import br.gov.mj.side.entidades.enums.EnumSituacaoPreenchimentoItemFormatacaoFornecedor;
import br.gov.mj.side.entidades.enums.EnumStatusOrdemFornecimento;
import br.gov.mj.side.entidades.enums.EnumTipoMinuta;
import br.gov.mj.side.entidades.enums.EnumTipoObjeto;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.HistoricoComunicacaoGeracaoOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ItensOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;
import br.gov.mj.side.web.dto.QrCodeOrdemFornecedorDto;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.MascaraUtils;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.util.SortableOrdemFornecDataProvider;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.cadastraritem.ConferenciaCadastrarItemPage;
import br.gov.mj.side.web.view.components.EntityDataProvider;
import br.gov.mj.side.web.view.template.TemplatePage;

/**
 * @author joao.coutinho
 * @since 21/03/2016 - Tela de Ordem de Fornecimento do Cadastro de Fornecedor
 */
@AuthorizeInstantiation({ OrdemFornecimentoContratoPage.ROLE_MANTER_CONTRATO_FORNECEDOR_INCLUIR, OrdemFornecimentoContratoPage.ROLE_MANTER_CONTRATO_FORNECEDOR_BAIXAR_ARQUIVO, OrdemFornecimentoContratoPage.ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_PREPARAR_ENTREGA })
public class OrdemFornecimentoContratoPage extends TemplatePage {

    // #######################################_VARIAVEIS_############################################
    private Form<Contrato> form;
    private Contrato contrato;
    private List<ObjetoFornecimentoContrato> listaOrdemFornecTemp = new ArrayList<ObjetoFornecimentoContrato>();
    private List<ObjetoFornecimentoContrato> listaDevolvidosOrdemFornecTemp = new ArrayList<ObjetoFornecimentoContrato>();
    @SuppressWarnings("unused")
    private SortableOrdemFornecDataProvider ordemFornecProvider;
    private List<QrCodeOrdemFornecedorDto> listaQrCodeOrdemFornecedorDto = new ArrayList<QrCodeOrdemFornecedorDto>();
    private boolean visualizar = false;
    private String abaAnterior = "pageItens";
    // #######################################_CONSTANTE_############################################
    private static final int TAMANHO_PAGINADOR = 10;
    private static final String ONCHANGE = "onchange";
    private Integer itensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;
    private Integer itensDevolvidosPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;
    private Integer itensPorPaginaOrdemFornec = Constants.ITEMS_PER_PAGE_PAGINATION;
    public static final String ROLE_MANTER_CONTRATO_FORNECEDOR_INCLUIR = "manter_contrato_fornecedor:incluir";
    public static final String ROLE_MANTER_CONTRATO_FORNECEDOR_BAIXAR_ARQUIVO = "manter_contrato_fornecedor:baixar_arquivo";
    public static final String ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_PREPARAR_ENTREGA = "manter_parecer_entrega_fornecedor:preparar_entrega";
    private boolean btnRegistrarDevolvidos = Boolean.FALSE;

    /**
     * Página de login
     */
    public static final String PAGINA_CONFERENCIA_ITEM = "conferencia/item";

    // #######################################_ELEMENTOS_DO_WICKET_############################################
    private DataView<OrdemFornecimentoContrato> newListaOrdemFornec;
    private PanelOrdemFornec panelOrdemFornec;
    private DataView<ObjetoFornecimentoContrato> newListaItemContrato;
    private DataView<ObjetoFornecimentoContrato> newListaItemDevolvidosContrato;
    private PanelAbas panelAbas;
    private PanelItensContrato panelItensContrato;
    private PanelItensDevolvidosContrato panelItensDevolvidosContrato;
    private PanelButton panelButton;
    @SuppressWarnings("rawtypes")
    private InfraAjaxFallbackLink btnCadastrarItem;
    private EnumTipoMinuta tipoMinuta;
    private RadioGroup<OrdemFornecimentoContrato> radioGrupOrdem;
    private OrdemFornecimentoContrato radioOrdemFornecimentoContrato;

    private AttributeAppender classeActive = new AttributeAppender("class", "active", " ");
    private AttributeModifier classeInactive = new AttributeModifier("class", "");
    private WebMarkupContainer containerItens;
    private WebMarkupContainer containerItensDevolvidos;

    // #####################################_INJEÇÃO_DE_DEPENDENCIA_##############################################
    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;

    // #####################################_CONSTRUTOR_##############################################

    /**
	 * 
	 */
    private static final long serialVersionUID = 2517746119679038990L;

    /**
     * @author joao.coutinho
     * @param pageParameters
     * @param ordemFornec
     */
    public OrdemFornecimentoContratoPage(PageParameters pageParameters, Contrato contrato) {
        super(pageParameters);
        this.contrato = contrato;
        initVariaveis();
        initComponentes();
    }

    /**
     * @author joao.coutinho
     * @param pageParameters
     * @param ordemFornec
     */
    public OrdemFornecimentoContratoPage(PageParameters pageParameters) {
        super(pageParameters);
        String id = getPageParameters().get("idObjetoOrdemFornecimento").toString();
        ObjetoFornecimentoContrato objetoFornecimentoContrato = ordemFornecimentoContratoService.buscarObjetoFornecimentoContrato(Long.parseLong(id));
        this.contrato = objetoFornecimentoContrato.getOrdemFornecimento().getContrato();
        this.radioOrdemFornecimentoContrato = objetoFornecimentoContrato.getOrdemFornecimento();
        
        List<ObjetoFornecimentoContrato> listaObjetos = new ArrayList<ObjetoFornecimentoContrato>();
        listaObjetos = ordenarObjetoFornecimentoContrato(ordemFornecimentoContratoService.buscarListaObjetoFornecimentoContrato(objetoFornecimentoContrato.getOrdemFornecimento(), null, EnumTipoObjeto.ORIGINAIS));
        listaOrdemFornecTemp.addAll(ordemFornecimentoContratoService.retirarObjetosQueNaoSaoDoPerfil(listaObjetos, EnumPerfilEntidade.FORNECEDOR));
        
        listaDevolvidosOrdemFornecTemp = new ArrayList<ObjetoFornecimentoContrato>();
        listaDevolvidosOrdemFornecTemp.addAll(ordenarObjetoFornecimentoContrato(ordemFornecimentoContratoService.buscarListaObjetoFornecimentoContrato(objetoFornecimentoContrato.getOrdemFornecimento(), null, EnumTipoObjeto.DEVOLVIDOS_SEM_VINCULO_COM_NOTA_REMESSA)));
        visualizar = true;
        initVariaveis();
        initComponentes();
    }

    /**
     * @author joao.coutinho
     * @param pageParameters
     * @param listaOrdemFornecTemp
     * @param ordemFornec
     */
    public OrdemFornecimentoContratoPage(PageParameters pageParameters, Contrato contrato, OrdemFornecimentoContrato radioOrdemFornecimentoContrato, boolean visualizar, List<ObjetoFornecimentoContrato> listaOrdemFornecTemp, List<ObjetoFornecimentoContrato> listaDevolvidosOrdemFornecTemp) {
        super(pageParameters);
        this.contrato = contrato;
        this.radioOrdemFornecimentoContrato = radioOrdemFornecimentoContrato;
        this.visualizar = visualizar;
        this.listaOrdemFornecTemp = listaOrdemFornecTemp;
        this.listaDevolvidosOrdemFornecTemp = listaDevolvidosOrdemFornecTemp;
        initVariaveis();
        initComponentes();
    }

    /**
     * @author joao.coutinho
     * @since 21/03/2016
     * @see OrdemFornecimentoContratoPage
     */
    private void initVariaveis() {
        setTitulo("Ordem de Fornecimento");
    }

    /**
     * @author joao.coutinho
     * @since 21/03/2016
     * @see OrdemFornecimentoContratoPage
     */
    private void initComponentes() {
        form = componentFactory.newForm("form", new CompoundPropertyModel<Contrato>(contrato));
        form.add(componentFactory.newLink("lnkDashboard", HomePage.class));
        form.add(componentFactory.newLink("lnkFornecedorContrato", FornecedorContratoPage.class));
        form.add(new Label("lblNomePage", getTitulo()));
        form.add(panelOrdemFornec = newPanelOrdemFornec());
        form.add(panelAbas = newPanelAbas());
        form.add(panelItensContrato = newPanelItensContrato());
        form.add(panelItensDevolvidosContrato = newPanelItensDevolvidosContrato());
        form.add(panelButton = newPanelButton());
        add(form);
    }

    // ####################################_PAINEIS_###############################################
    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 21/03/2016 - Painel Ordem Fornecimento
     */
    public PanelOrdemFornec newPanelOrdemFornec() {
        panelOrdemFornec = new PanelOrdemFornec();
        setOutputMarkupId(Boolean.TRUE);
        return panelOrdemFornec;
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 21/03/2016 - Painel Ordem Fornecimento
     * @see newPanelOrdemFornec()
     */
    private class PanelOrdemFornec extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelOrdemFornec() {
            super("panelOrdemFornec");
            List<OrdemFornecimentoContrato> listaOrdemFornecimentoContrato = ordemFornecimentoContratoService.buscarOrdemFornecimentoContrato(contrato);
            listaOrdemFornecimentoContrato = formatarListaComunicadaOrdemFornecimentoContrato(listaOrdemFornecimentoContrato);
            ordemFornecProvider = new SortableOrdemFornecDataProvider(listaOrdemFornecimentoContrato);
            radioGrupOrdem = new RadioGroup<OrdemFornecimentoContrato>("radioGrupOrdem", new Model<OrdemFornecimentoContrato>());
            radioGrupOrdem.add(newListaOrdemFornec = newDataViewOrdemFornec(listaOrdemFornecimentoContrato)); // listaContrato
            add(radioGrupOrdem);
            add(newDropItensPorPaginaOrdemFornec());
            add(new InfraAjaxPagingNavigator("paginatorOrdemFornec", newListaOrdemFornec));
        }
    }

    // Esse metodo monta uma nova lista apenas com as ordem de fornecimento
    // comunicada.
    private List<OrdemFornecimentoContrato> formatarListaComunicadaOrdemFornecimentoContrato(List<OrdemFornecimentoContrato> listaOrdemFornecimentoContrato) {
        List<OrdemFornecimentoContrato> listaTemporaria = new ArrayList<OrdemFornecimentoContrato>();
        for (OrdemFornecimentoContrato ordemFornecimentoContrato : listaOrdemFornecimentoContrato) {
            HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historicoComunicacao = ordemFornecimentoContratoService.buscarUltimoHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(ordemFornecimentoContrato, true);
            if (historicoComunicacao != null) {
                listaTemporaria.add(ordemFornecimentoContrato);
            }
        }
        return listaTemporaria;
    }

    public PanelAbas newPanelAbas() {
        panelAbas = new PanelAbas();
        panelAbas.setVisible(visualizar);
        setOutputMarkupId(Boolean.TRUE);
        return panelAbas;
    }

    private class PanelAbas extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelAbas() {
            super("panelAbas");

            add(newContainerItens()); // abaItens
            add(newContainerItensDevolvidos()); // abaItensDevolvidos
        }
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 21/03/2016 - Painel Itens Contrato
     */
    public PanelItensContrato newPanelItensContrato() {
        panelItensContrato = new PanelItensContrato();
        panelItensContrato.setVisible(visualizar);
        setOutputMarkupId(Boolean.TRUE);
        return panelItensContrato;
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 21/03/2016 - Painel Itens Contrato
     * @see newPanelItensContrato()
     */
    private class PanelItensContrato extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelItensContrato() {
            super("panelItensContrato");
            add(newListaItemContrato = newDataViewItemContrato());
            add(newDropItensPorPagina());
            add(new InfraAjaxPagingNavigator("paginatorItensContrato", newListaItemContrato));
        }
    }

    public PanelItensDevolvidosContrato newPanelItensDevolvidosContrato() {
        panelItensDevolvidosContrato = new PanelItensDevolvidosContrato();
        panelItensDevolvidosContrato.setVisible(visualizar);
        setOutputMarkupId(Boolean.TRUE);
        return panelItensDevolvidosContrato;
    }

    private class PanelItensDevolvidosContrato extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelItensDevolvidosContrato() {
            super("panelItensDevolvidosContrato");
            add(newListaItemDevolvidosContrato = newDataViewItemDevolvidosContrato());
            add(newDropItensDevolvidosPorPagina());
            add(new InfraAjaxPagingNavigator("paginatorItensDevolvidosContrato", newListaItemDevolvidosContrato));
        }
    }

    private DropDownChoice<Integer> newDropItensPorPagina() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPagina", new LambdaModel<Integer>(this::getItensPorPagina, this::setItensPorPagina), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                newListaItemContrato.setItemsPerPage(getItensPorPagina());
                target.add(panelItensContrato);
            };
        });
        return dropDownChoice;
    }

    private DropDownChoice<Integer> newDropItensDevolvidosPorPagina() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensDevolvidosPorPagina", new LambdaModel<Integer>(this::getItensDevolvidosPorPagina, this::setItensDevolvidosPorPagina), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                newListaItemDevolvidosContrato.setItemsPerPage(getItensDevolvidosPorPagina());
                target.add(panelItensDevolvidosContrato);
            };
        });
        return dropDownChoice;
    }

    private DropDownChoice<Integer> newDropItensPorPaginaOrdemFornec() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPaginaOrdemFornec", new LambdaModel<Integer>(this::getItensPorPaginaOrdemFornec, this::setItensPorPaginaOrdemFornec), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                newListaOrdemFornec.setItemsPerPage(getItensPorPaginaOrdemFornec());
                target.add(panelOrdemFornec);
            };
        });
        return dropDownChoice;
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
     * Sprint 10 - painel do botão
     * 
     * @author joao.coutinho
     * @since 21/03/2016
     * @see PanelOrdemFornec
     */
    @SuppressWarnings("serial")
    private DataView<OrdemFornecimentoContrato> newDataViewOrdemFornec(List<OrdemFornecimentoContrato> listaOrdemFornecimentoContrato) {
        newListaOrdemFornec = new DataView<OrdemFornecimentoContrato>("listaOrdemFornecNewDataView", new EntityDataProvider<OrdemFornecimentoContrato>(listaOrdemFornecimentoContrato)) {
            @SuppressWarnings("rawtypes")
            @Override
            protected void populateItem(Item<OrdemFornecimentoContrato> item) {
                HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historicoOrdem = ordemFornecimentoContratoService.buscarUltimoHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(item.getModelObject(), true);
                List<String> regiaoEUf = verificarString(item);
                item.add(new Label("id", historicoOrdem == null ? "" : historicoOrdem.getNumeroDocumentoSei()));
                item.add(new Label("itemOf", regiaoEUf.get(0)));
                item.add(new Label("ufOf", regiaoEUf.get(1)));
                item.add(new Label("municipioOf", regiaoEUf.get(2)));
                item.add(new Label("situacao", historicoOrdem.getOrdemFornecimento().getStatusOrdemFornecimento() == null ? "" : historicoOrdem.getOrdemFornecimento().getStatusOrdemFornecimento().getDescricao()));
                Radio<OrdemFornecimentoContrato> radioContrato = new Radio<OrdemFornecimentoContrato>("radioOrdem", Model.of(item.getModelObject()));
                radioContrato.add(new AjaxEventBehavior(ONCHANGE) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        exibirListaItens(item.getModelObject(), target, historicoOrdem);
                    }
                });

                if (radioOrdemFornecimentoContrato != null && radioOrdemFornecimentoContrato.getId() != null && radioOrdemFornecimentoContrato.getId().intValue() == item.getModelObject().getId().intValue()) {
                    radioGrupOrdem.setConvertedInput(radioOrdemFornecimentoContrato);
                    radioGrupOrdem.setDefaultModelObject(radioOrdemFornecimentoContrato);
                }
                item.add(radioContrato);

                Boolean validarPreparacaoDaEntrega = validarPreparacaoDaEntrega(item.getModelObject());

                InfraAjaxFallbackLink btnPrepararEntrega = componentFactory.newAjaxFallbackLink("btnPrepararEntrega", (target) -> prepararEntrega(target, item.getModelObject(), historicoOrdem));
                authorize(btnPrepararEntrega, RENDER, ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_PREPARAR_ENTREGA);
                btnPrepararEntrega.setVisible((validarPreparacaoDaEntrega && (historicoOrdem.getOrdemFornecimento().getStatusOrdemFornecimento() != null && historicoOrdem.getOrdemFornecimento().getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.EMITIDA))));
                item.add(btnPrepararEntrega);

                InfraAjaxFallbackLink btnRegistarEntregaOF = componentFactory.newAjaxFallbackLink("btnRegistarEntregaOF", (target) -> prepararEntrega(target, item.getModelObject(), historicoOrdem));
                authorize(btnRegistarEntregaOF, RENDER, ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_PREPARAR_ENTREGA);
                btnRegistarEntregaOF.setVisible((validarPreparacaoDaEntrega && (historicoOrdem.getOrdemFornecimento().getStatusOrdemFornecimento() != null && historicoOrdem.getOrdemFornecimento().getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.EXECUTADA))));
                item.add(btnRegistarEntregaOF);

                InfraAjaxFallbackLink btnVisualizarEntregaOF = componentFactory.newAjaxFallbackLink("btnVisualizarEntregaOF", (target) -> prepararEntrega(target, item.getModelObject(), historicoOrdem));
                authorize(btnVisualizarEntregaOF, RENDER, ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_PREPARAR_ENTREGA);
                btnVisualizarEntregaOF.setVisible((validarPreparacaoDaEntrega)
                        && (historicoOrdem.getOrdemFornecimento().getStatusOrdemFornecimento() != null && !historicoOrdem.getOrdemFornecimento().getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.EMITIDA)
                                && !historicoOrdem.getOrdemFornecimento().getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.EXECUTADA) && !historicoOrdem.getOrdemFornecimento().getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.DEVOLVIDA)));
                item.add(btnVisualizarEntregaOF);

                InfraAjaxFallbackLink btnPrepararEntregaDevolvido = componentFactory.newAjaxFallbackLink("btnPrepararEntregaDevolvido", (target) -> prepararEntrega(target, item.getModelObject(), historicoOrdem));
                authorize(btnPrepararEntregaDevolvido, RENDER, ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_PREPARAR_ENTREGA);
                btnPrepararEntregaDevolvido.setVisible((validarPreparacaoDaEntregaDevolvidos(item)) && (historicoOrdem.getOrdemFornecimento().getStatusOrdemFornecimento() != null && historicoOrdem.getOrdemFornecimento().getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.DEVOLVIDA)));
                item.add(btnPrepararEntregaDevolvido);

                Button btnDownload = componentFactory.newButton("btnDonwload", () -> download(historicoOrdem, historicoOrdem.getNumeroDocumentoSei()));
                item.add(btnDownload);

                Button btnQrCode = componentFactory.newButton("btnQrCode", () -> gerarQrCode(item, historicoOrdem.getNumeroDocumentoSei()));
                item.add(btnQrCode);
            }
        };
        newListaOrdemFornec.setItemsPerPage(TAMANHO_PAGINADOR);
        return newListaOrdemFornec;
    }

    /**
     * @author joao.coutinho
     * @param ordemFornecimentoContrato
     * @return
     * @see newDataViewOrdemFornec
     */
    private boolean validarPreparacaoDaEntrega(OrdemFornecimentoContrato ordemFornecimentoContrato) {
        boolean retorno = Boolean.TRUE;
        List<ObjetoFornecimentoContrato> lista = ordenarObjetoFornecimentoContrato(ordemFornecimentoContratoService.buscarListaObjetoFornecimentoContrato(ordemFornecimentoContrato, null, EnumTipoObjeto.ORIGINAIS));
        List<ObjetoFornecimentoContrato> listaOf = new ArrayList<ObjetoFornecimentoContrato>();
        listaOf.addAll(ordemFornecimentoContratoService.retirarObjetosQueNaoSaoDoPerfil(lista, EnumPerfilEntidade.FORNECEDOR));
        for (ObjetoFornecimentoContrato objetoTemp : listaOf) {
            if (!objetoTemp.getSituacaoPreenchimentoFornecedor().equals(EnumSituacaoPreenchimentoItemFormatacaoFornecedor.PREENCHIDO)) {
                retorno = Boolean.FALSE;
                break;
            }
        }
        return retorno;
    }

    private boolean validarPreparacaoDaEntregaDevolvidos(Item<OrdemFornecimentoContrato> item) {
        boolean retorno = Boolean.TRUE;
        List<ObjetoFornecimentoContrato> lista1 = ordenarObjetoFornecimentoContrato(ordemFornecimentoContratoService.buscarListaObjetoFornecimentoContrato(item.getModelObject(), null, EnumTipoObjeto.DEVOLVIDOS_SEM_VINCULO_COM_NOTA_REMESSA));
        for (ObjetoFornecimentoContrato objetoTemp : lista1) {
            if (!objetoTemp.getSituacaoPreenchimentoFornecedor().equals(EnumSituacaoPreenchimentoItemFormatacaoFornecedor.PREENCHIDO)) {
                retorno = Boolean.FALSE;
                break;
            }
        }
        return retorno;
    }

    /**
     * @author joao.coutinho
     * @param target
     * @param item
     * @param historicoOrdem
     * @param radioOrdemFornecimentoContrato2
     */
    private void prepararEntrega(AjaxRequestTarget target, OrdemFornecimentoContrato objetoOF, HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historicoOrdem) {
        setResponsePage(new PrepararEntregaPage(getPageParameters(), objetoOF, this.radioOrdemFornecimentoContrato, this.visualizar, this.listaOrdemFornecTemp, this.listaDevolvidosOrdemFornecTemp, historicoOrdem));
    }

    /**
     * Sprint 10 - painel do botão
     * 
     * @author joao.coutinho
     * @param numeroDocumentoSei
     * @since 21/03/2016
     * @see newDataViewOrdemFornec
     */
    private void download(HistoricoComunicacaoGeracaoOrdemFornecimentoContrato item, String numeroDocumentoSei) {
        SideUtil.download(item.getMinutaGerada(), "Ordem_Fornecimento_" + numeroDocumentoSei + ".pdf");
    }

    /**
     * Sprint 10 - painel do botão
     * 
     * @author joao.coutinho
     * @since 21/03/2016
     * @see newDataViewOrdemFornec
     */
    private void gerarQrCode(Item<OrdemFornecimentoContrato> item, String numeroDocumentoSei) {

        tipoMinuta = EnumTipoMinuta.PDF;
        QrCodeOfBuilder jasper = new QrCodeOfBuilder(SideUtil.getCaminhoCompletoRelatorios(getHttpServletRequest().getServletContext()));
        jasper.setNomeQrCode("QrCode - " + "Ordem Fornecimento");
        jasper.setTipoQrCode(tipoMinuta);
        jasper.setDataList(popularLista(item));
        ByteArrayOutputStream exportar = jasper.exportToByteArray();
        SideUtil.download(exportar.toByteArray(), "QrCode_" + numeroDocumentoSei + ".pdf");
    }

    /**
     * Sprint 10 - painel do botão
     * 
     * @author joao.coutinho
     * @since 21/03/2016
     * @param item
     * @see gerarQrCode
     * @return
     */
    private List<QrCodeOrdemFornecedorDto> popularLista(Item<OrdemFornecimentoContrato> item) {
        listaQrCodeOrdemFornecedorDto = new ArrayList<QrCodeOrdemFornecedorDto>();
        List<ObjetoFornecimentoContrato> lista2 = ordemFornecimentoContratoService.buscarListaObjetoFornecimentoContrato(item.getModelObject(), null, EnumTipoObjeto.ORIGINAIS);
        for (ObjetoFornecimentoContrato objeto : lista2) {
            QrCodeOrdemFornecedorDto qrCodeOrdemFornecedorDto = new QrCodeOrdemFornecedorDto();
            qrCodeOrdemFornecedorDto.setId(objeto.getId());
            qrCodeOrdemFornecedorDto.setItem(objeto.getItem().getNomeBem());
            qrCodeOrdemFornecedorDto.setNomeEntidade(objeto.getLocalEntrega().getEntidade().getNomeEntidade());
            qrCodeOrdemFornecedorDto.setNumeroCnpj(MascaraUtils.formatarMascaraCpfCnpj(objeto.getLocalEntrega().getEntidade().getNumeroCnpj()));
            qrCodeOrdemFornecedorDto.setLocalEntrega(objeto.getLocalEntrega().getEnderecoCompleto());
            qrCodeOrdemFornecedorDto.setFormaVerificacao(objeto.getFormaVerificacao().getValor());
            qrCodeOrdemFornecedorDto.setQuantidade(objeto.getQuantidade());
            qrCodeOrdemFornecedorDto.setVisualizar(objeto.getFormaVerificacao().getValor().equals("U") ? false : true);
            qrCodeOrdemFornecedorDto.setLink(getUrlBase(PAGINA_CONFERENCIA_ITEM + "?hash=" + objeto.getId()));
            listaQrCodeOrdemFornecedorDto.add(qrCodeOrdemFornecedorDto);
        }
        return listaQrCodeOrdemFornecedorDto;
    }

    private WebMarkupContainer newContainerItens() {
        containerItens = new WebMarkupContainer("abaItens");
        containerItens.setOutputMarkupId(true);

        AjaxFallbackLink button = newLinkItens(); // btnDadosLicitacao
        containerItens.add(button);

        return containerItens;
    }

    private WebMarkupContainer newContainerItensDevolvidos() {
        containerItensDevolvidos = new WebMarkupContainer("abaItensDevolvidos");
        containerItensDevolvidos.setOutputMarkupId(true);

        AjaxFallbackLink button = newLinkItensDevolvidos(); // btnElegibilidade
        containerItensDevolvidos.add(button);
        return containerItensDevolvidos;
    }

    private AjaxFallbackLink<Void> newLinkItens() {
        AjaxFallbackLink<Void> btnInfo = componentFactory.newAjaxFallbackLink("btnItens", (target) -> actionAba(target, "pageItens"));
        btnInfo.setOutputMarkupId(true);
        return btnInfo;
    }

    private AjaxFallbackLink<Void> newLinkItensDevolvidos() {
        AjaxFallbackLink<Void> btnInfo = componentFactory.newAjaxFallbackLink("btnItensDevolvidos", (target) -> actionAba(target, "pageItensDevolvidos"));
        btnInfo.setOutputMarkupId(true);
        return btnInfo;
    }

    public void actionAba(AjaxRequestTarget target, String abaClicada) {

        // Se a aba clicada agora for igual a última clicada não atualizar nada.
        if (abaClicada.equalsIgnoreCase(abaAnterior)) {
            return;
        }

        // Mostra a aba clicada
        target.appendJavaScript("mostrarAba('" + abaClicada + "');");

        // oculta a aba Anterior
        target.appendJavaScript("ocultarAba('" + abaAnterior + "');");

        abaAnterior = abaClicada;

        atualizarAbas(target, abaClicada);
    }

    private void atualizarAbas(AjaxRequestTarget target, String aba) {

        if ("pageItens".equalsIgnoreCase(aba)) {
            containerItens.add(classeActive);
            containerItensDevolvidos.add(classeInactive);
        } else {
            containerItens.add(classeInactive);
            containerItensDevolvidos.add(classeActive);
        }

        containerItens.addOrReplace(newLinkItens());
        containerItensDevolvidos.addOrReplace(newLinkItensDevolvidos());

        target.add(containerItens, containerItensDevolvidos, panelAbas);
    }

    /**
     * Sprint 10 - painel do botão
     * 
     * @author joao.coutinho
     * @param ordemFornecimentoContrato
     * @since 21/03/2016
     * @see PanelItensContrato
     */
    @SuppressWarnings("serial")
    private DataView<ObjetoFornecimentoContrato> newDataViewItemContrato() {
        newListaItemContrato = new DataView<ObjetoFornecimentoContrato>("listaItensContratoNewDataView", new EntityDataProvider<ObjetoFornecimentoContrato>(listaOrdemFornecTemp)) {
            @Override
            protected void populateItem(Item<ObjetoFornecimentoContrato> item) {
                item.add(new Label("identificador", item.getModelObject().getId()));
                item.add(newLabelNomeBem(item.getModelObject()));
                item.add(new Label("quantidadeTotal", (item.getModelObject().getQuantidade() == null ? "" : item.getModelObject().getQuantidade())));
                item.add(new Label("formaVerificacao", item.getModelObject().getFormaVerificacao().getDescricao()));
                item.add(new Label("situacao", item.getModelObject().getSituacaoPreenchimentoFornecedor().getDescricao()));

                if (item.getModelObject().getSituacaoPreenchimentoFornecedor() == EnumSituacaoPreenchimentoItemFormatacaoFornecedor.PREENCHIDO && (item.getModelObject().getObjetoDevolvido() == null || item.getModelObject().getObjetoDevolvido().equals(Boolean.FALSE))) {
                    AttributeAppender style = new AttributeAppender("style", "background:#defaea;", " ");
                    item.add(style);
                } else if (item.getModelObject().getObjetoDevolvido() != null && item.getModelObject().getObjetoDevolvido().equals(Boolean.TRUE)) {
                    AttributeAppender style = new AttributeAppender("style", "background:#ff9999;", " ");
                    item.add(style);
                }

                Integer obrigatorioPreenchido = item.getModelObject().getQuantidadeQuesitosObrigatoriosPreenchidosFornecedor();
                Integer opcionalPreenchido = item.getModelObject().getQuantidadeQuesitosOpcionaisPreenchidosFornecedor();

                Integer obrigatorioTotal = item.getModelObject().getQuantidadeQuesitosObrigatoriosFornecedor();
                Integer opcionalTotal = item.getModelObject().getQuantidadeQuesitosOpcionaisFornecedor();
                Integer total = item.getModelObject().getQuantidadeQuesitosFornecedor();

                item.add(new Label("quesitosOb", (obrigatorioPreenchido == null ? 0 : obrigatorioPreenchido) + " / " + (obrigatorioTotal == null ? 0 : obrigatorioTotal)));
                item.add(new Label("quesitosOp", (opcionalPreenchido == null ? 0 : opcionalPreenchido) + " / " + (opcionalTotal == null ? 0 : opcionalTotal)));
                item.add(new Label("quesitos", (total == null ? 0 : total)));

                btnCadastrarItem = componentFactory.newAjaxFallbackLink("btnCadastrarItem", (target) -> cadastrarItem(target, item));
                authorize(btnCadastrarItem, RENDER, ROLE_MANTER_CONTRATO_FORNECEDOR_INCLUIR);
                item.add(btnCadastrarItem);
            }
        };
        newListaItemContrato.setItemsPerPage(TAMANHO_PAGINADOR);
        return newListaItemContrato;
    }

    private Label newLabelNomeBem(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        String textoNomeBem = objetoFornecimentoContrato.getItem().getNomeBem();

        if (verificaItensDevolvidos(objetoFornecimentoContrato)) {
            textoNomeBem = textoNomeBem + "<p style=\"color: red;\">Item Devolvido.</p>";
        }

        Label lbl = new Label("nomeBem", textoNomeBem);
        lbl.setEscapeModelStrings(Boolean.FALSE);
        return lbl;
    }

    private boolean verificaItensDevolvidos(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        return objetoFornecimentoContrato.getObjetoDevolvido() != null && objetoFornecimentoContrato.getObjetoDevolvido().equals(Boolean.TRUE);
    }

    @SuppressWarnings("serial")
    private DataView<ObjetoFornecimentoContrato> newDataViewItemDevolvidosContrato() {
        newListaItemDevolvidosContrato = new DataView<ObjetoFornecimentoContrato>("listaItensDevolvidosContratoNewDataView", new EntityDataProvider<ObjetoFornecimentoContrato>(listaDevolvidosOrdemFornecTemp)) {
            @Override
            protected void populateItem(Item<ObjetoFornecimentoContrato> item) {
                item.add(newLabelIdentificador(item.getModelObject()));
                item.add(newLabelNomeBem(item.getModelObject()));
                item.add(new Label("quantidadeTotal", (item.getModelObject().getQuantidade() == null ? "" : item.getModelObject().getQuantidade())));
                item.add(new Label("formaVerificacao", item.getModelObject().getFormaVerificacao().getDescricao()));
                item.add(new Label("situacao", item.getModelObject().getSituacaoPreenchimentoFornecedor().getDescricao()));

                if (item.getModelObject().getSituacaoPreenchimentoFornecedor() == EnumSituacaoPreenchimentoItemFormatacaoFornecedor.PREENCHIDO && (item.getModelObject().getObjetoDevolvido() == null || item.getModelObject().getObjetoDevolvido().equals(Boolean.FALSE))) {
                    AttributeAppender style = new AttributeAppender("style", "background:#defaea;", " ");
                    item.add(style);
                } else if (item.getModelObject().getObjetoDevolvido() != null && item.getModelObject().getObjetoDevolvido().equals(Boolean.TRUE)) {
                    AttributeAppender style = new AttributeAppender("style", "background:#ff9999;", " ");
                    item.add(style);
                }

                Integer obrigatorioPreenchido = item.getModelObject().getQuantidadeQuesitosObrigatoriosPreenchidosFornecedor();
                Integer opcionalPreenchido = item.getModelObject().getQuantidadeQuesitosOpcionaisPreenchidosFornecedor();

                Integer obrigatorioTotal = item.getModelObject().getQuantidadeQuesitosObrigatoriosFornecedor();
                Integer opcionalTotal = item.getModelObject().getQuantidadeQuesitosOpcionaisFornecedor();
                Integer total = item.getModelObject().getQuantidadeQuesitosFornecedor();

                item.add(new Label("quesitosOb", (obrigatorioPreenchido == null ? 0 : obrigatorioPreenchido) + " / " + (obrigatorioTotal == null ? 0 : obrigatorioTotal)));
                item.add(new Label("quesitosOp", (opcionalPreenchido == null ? 0 : opcionalPreenchido) + " / " + (opcionalTotal == null ? 0 : opcionalTotal)));
                item.add(new Label("quesitos", (total == null ? 0 : total)));

                btnCadastrarItem = componentFactory.newAjaxFallbackLink("btnCadastrarItem", (target) -> cadastrarItem(target, item));
                authorize(btnCadastrarItem, RENDER, ROLE_MANTER_CONTRATO_FORNECEDOR_INCLUIR);
                item.add(btnCadastrarItem);
            }
        };
        newListaItemDevolvidosContrato.setItemsPerPage(TAMANHO_PAGINADOR);
        return newListaItemDevolvidosContrato;
    }

    private Label newLabelIdentificador(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        Long textoNomeBem = objetoFornecimentoContrato.getObjetoFornecimentoContratoPai() == null ? objetoFornecimentoContrato.getId() : objetoFornecimentoContrato.getObjetoFornecimentoContratoPai();

        Label lbl = new Label("identificador", textoNomeBem);
        return lbl;
    }

    private void cadastrarItem(AjaxRequestTarget target, Item<ObjetoFornecimentoContrato> item) {
        PageParameters p = new PageParameters();
        p.add("tipoPessoa", EnumTipoPessoa.PREPOSTO_FORNECEDOR.getValor());
        p.add("idObjetoOrdemFornecimento", item.getModelObject().getId());
        setResponsePage(new ConferenciaCadastrarItemPage(p, this, item.getModelObject().getId()));
    }

    /**
     * Sprint 10 - painel do botão
     * 
     * @author joao.coutinho
     * @param historicoOrdem
     * @since 21/03/2016
     * @see newDataViewOrdemFornec
     */
    private void exibirListaItens(OrdemFornecimentoContrato objetoOF, AjaxRequestTarget target, HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historicoOrdem) {
        radioOrdemFornecimentoContrato = objetoOF;
        listaOrdemFornecTemp = new ArrayList<ObjetoFornecimentoContrato>();
        listaDevolvidosOrdemFornecTemp = new ArrayList<ObjetoFornecimentoContrato>();
        
        List<ObjetoFornecimentoContrato> listaObjetos = new ArrayList<ObjetoFornecimentoContrato>();
        listaObjetos = ordenarObjetoFornecimentoContrato(ordemFornecimentoContratoService.buscarListaObjetoFornecimentoContrato(objetoOF, null, EnumTipoObjeto.ORIGINAIS));
        listaOrdemFornecTemp.addAll(ordemFornecimentoContratoService.retirarObjetosQueNaoSaoDoPerfil(listaObjetos, EnumPerfilEntidade.FORNECEDOR));
        listaDevolvidosOrdemFornecTemp.addAll(ordenarObjetoFornecimentoContrato(ordemFornecimentoContratoService.buscarListaObjetoFornecimentoContrato(objetoOF, null, EnumTipoObjeto.TODOS_DEVOLVIDOS)));
        if (listaOrdemFornecTemp.size() != 0) {
            visualizar = true;
            panelAbas.setVisible(visualizar);
            panelItensContrato.setVisible(visualizar);
            panelItensDevolvidosContrato.setVisible(visualizar);
            panelItensContrato.addOrReplace(newDataViewItemContrato());
            panelItensContrato.addOrReplace(new InfraAjaxPagingNavigator("paginatorItensContrato", newListaItemContrato));
            panelItensDevolvidosContrato.addOrReplace(newDataViewItemDevolvidosContrato());
            panelItensDevolvidosContrato.addOrReplace(new InfraAjaxPagingNavigator("paginatorItensDevolvidosContrato", newListaItemDevolvidosContrato));
            target.add(panelAbas, panelItensContrato, panelItensDevolvidosContrato);
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraPesquisa');");
        }
    }

    private List<ObjetoFornecimentoContrato> ordenarObjetoFornecimentoContrato(List<ObjetoFornecimentoContrato> listaFormatacaoObjetoFornecimento) {
        List<String> listaId = new ArrayList<String>();
        List<ObjetoFornecimentoContrato> novaLista = new ArrayList<ObjetoFornecimentoContrato>();
        for (ObjetoFornecimentoContrato obj : listaFormatacaoObjetoFornecimento) {
            listaId.add(String.valueOf(obj.getId()));
        }
        Collections.sort(listaId);

        for (String objId : listaId) {
            for (ObjetoFornecimentoContrato obj : listaFormatacaoObjetoFornecimento) {
                if (objId.equals(String.valueOf(obj.getId()))) {
                    novaLista.add(obj);
                }
            }
        }
        return novaLista;
    }

    /**
     * Sprint 10 - painel do botão
     * 
     * @author joao.coutinho
     * @since 21/03/2016
     * @see PanelButton
     */
    private Button newButtonVoltar() {
        return componentFactory.newButton("btnVoltar", () -> setResponsePage(FornecedorContratoPage.class));
    }

    private List<String> verificarString(Item<OrdemFornecimentoContrato> item) {

        String stringUf = new String("");
        String stringMunicipio = new String("");
        String stringItem = new String("");

        List<Uf> uf = new ArrayList<Uf>();
        List<Municipio> municipio = new ArrayList<Municipio>();
        List<Bem> bem = new ArrayList<Bem>();

        List<ItensOrdemFornecimentoContrato> listItem = ordemFornecimentoContratoService.buscarItensOrdemFornecimentoContrato(item.getModelObject());

        for (ItensOrdemFornecimentoContrato itemMun : listItem) {

            // Verifica se o municipio já foi adicionada a lista temporaria
            // de regiões.
            if (!municipio.contains(itemMun.getLocalEntrega().getMunicipio())) {
                municipio.add(itemMun.getLocalEntrega().getMunicipio());

                if (!stringMunicipio.equalsIgnoreCase("")) {
                    stringMunicipio = stringMunicipio.concat(" / ");
                }
                stringMunicipio = stringMunicipio.concat(" ").concat(itemMun.getLocalEntrega().getMunicipio().getNomeMunicipio()).concat(" ");
            }

            // Irá verificar se a UF já foi adicionada a lista temporária de
            // UF's.
            if (!uf.contains(itemMun.getLocalEntrega().getMunicipio().getUf())) {
                uf.add(itemMun.getLocalEntrega().getMunicipio().getUf());

                if (!stringUf.equalsIgnoreCase("")) {
                    stringUf = stringUf.concat(" / ");
                }
                stringUf = stringUf.concat(" ").concat(itemMun.getLocalEntrega().getMunicipio().getUf().getSiglaUf()).concat(" ");
            }

            if (!bem.contains(itemMun.getItem())) {
                bem.add(itemMun.getItem());

                if (!stringItem.equalsIgnoreCase("")) {
                    stringItem = stringItem.concat(" / ");
                }
                stringItem = stringItem.concat(" ").concat(itemMun.getItem().getNomeBem()).concat(" ");
            }
        }

        List<String> itemUfMunicipio = new ArrayList<String>();

        itemUfMunicipio.add(stringItem);
        itemUfMunicipio.add(stringUf);
        itemUfMunicipio.add(stringMunicipio);
        return itemUfMunicipio;
    }

    public Integer getItensPorPagina() {
        return itensPorPagina;
    }

    public void setItensPorPagina(Integer itensPorPagina) {
        this.itensPorPagina = itensPorPagina;
    }

    public Integer getItensDevolvidosPorPagina() {
        return itensDevolvidosPorPagina;
    }

    public void setItensDevolvidosPorPagina(Integer itensDevolvidosPorPagina) {
        this.itensDevolvidosPorPagina = itensDevolvidosPorPagina;
    }

    public Integer getItensPorPaginaOrdemFornec() {
        return itensPorPaginaOrdemFornec;
    }

    public void setItensPorPaginaOrdemFornec(Integer itensPorPaginaOrdemFornec) {
        this.itensPorPaginaOrdemFornec = itensPorPaginaOrdemFornec;
    }

}
