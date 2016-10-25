package br.gov.mj.side.web.view.fornecedor;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
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
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.component.InfraLocalDateTextField;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumBotaoClicadoNotaRemessa;
import br.gov.mj.side.entidades.enums.EnumStatusExecucaoNotaRemessaFornecedor;
import br.gov.mj.side.entidades.enums.EnumStatusOrdemFornecimento;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumTipoArquivoTermoEntrega;
import br.gov.mj.side.entidades.enums.EnumTipoMinuta;
import br.gov.mj.side.entidades.enums.EnumTipoObjeto;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.inscricao.ComissaoRecebimento;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.licitacao.contrato.HistoricoComunicacaoGeracaoOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.AnexoNotaRemessa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.ItensNotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.TermoRecebimentoDefinitivo;
import br.gov.mj.side.web.dto.AnexoDto;
import br.gov.mj.side.web.dto.TermoEntregaDto;
import br.gov.mj.side.web.dto.TermoEntregaitensDto;
import br.gov.mj.side.web.service.AnexoNotaRemessaService;
import br.gov.mj.side.web.service.AnexoTermoRecebimentoDefinitivoService;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.MailService;
import br.gov.mj.side.web.service.NotaRemessaService;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.service.TermoRecebimentoDefinitivoService;
import br.gov.mj.side.web.util.CnpjUtil;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.DataUtil;
import br.gov.mj.side.web.util.MascaraUtils;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.components.EntityDataProvider;
import br.gov.mj.side.web.view.template.TemplatePage;

/**
 * @author joao.coutinho
 * @since 10/05/2016 - Tela de Preparar Entrega
 */
@AuthorizeInstantiation({ PrepararEntregaPage.ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_EXECUTAR_TERMO_ENTREGA, PrepararEntregaPage.ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_EXCLUIR_TERMO_ENTREGA, PrepararEntregaPage.ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_REGISTRAR_TERMO_ENTREGA,
        PrepararEntregaPage.ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_EXCLUIR_ANEXO_TERMO_ENTREGA, PrepararEntregaPage.ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_GERAR_TERMO_ENTREGA })
public class PrepararEntregaPage extends TemplatePage {

    private static final long serialVersionUID = 1540246352696521871L;

    // #######################################_VARIAVEIS_############################################
    private OrdemFornecimentoContrato ordemFornecimentoContrato;
    private NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato;
    private OrdemFornecimentoContrato radioOrdemFornecimentoContrato;
    private HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historicoOrdem;
    private List<ObjetoFornecimentoContrato> listaOrdemFornecTemp;
    private List<ObjetoFornecimentoContrato> listaDevolvidosOrdemFornecTemp;
    private List<NotaRemessaOrdemFornecimentoContrato> listaNotaRemessaLocaisEntrega = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();
    private List<NotaRemessaOrdemFornecimentoContrato> listaNotaRemessaTermoEntrega = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();
    private List<ObjetoFornecimentoContrato> listaDevolvidos = new ArrayList<ObjetoFornecimentoContrato>();
    private List<NotaRemessaOrdemFornecimentoContrato> listaNotasDeItensDevolvidos = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();
    private Integer locaisEntregaPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;
    private Integer termoEntregaPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;
    private boolean visualizar = Boolean.FALSE;
    private boolean painelAlerta = Boolean.FALSE;
    private boolean btnExecutarEntregaVizualizar = Boolean.FALSE;
    private boolean tabelaLocaisEntregaVizualizar = Boolean.FALSE;
    private boolean tabelaTermoEntregaVizualizar = Boolean.FALSE;
    private boolean panelRegistrarEntregaVizualizar = Boolean.FALSE;
    private List<FileUpload> uploads = new ArrayList<FileUpload>();
    private LocalDate dataEfetivaEntrega;
    private List<AnexoNotaRemessa> listaAnexosNotaRemessa = new ArrayList<AnexoNotaRemessa>();
    private EnumTipoArquivoTermoEntrega tipoArquivoTermoEntrega;
    private List<InscricaoPrograma> listaInscricaoPrograma = new ArrayList<InscricaoPrograma>();
    private String tituloNumeroOuCodigo = new String();
    private Boolean exibirPanelCodigo = Boolean.FALSE;
    private Boolean exibirInputNumeroOuCodigo = Boolean.FALSE;
    private String notaFiscal = new String();
    private String codigoRecebimento = new String();
    private List<TermoRecebimentoDefinitivo> listaTermoRecebimentoDefinitivo = new ArrayList<>();
    private List<FileUpload> uploadsNotaFiscal = new ArrayList<FileUpload>();

    // #######################################_CONSTANTE_############################################
    private static final String ONCHANGE = "onchange";
    private static final String ONKEYUP = "onkeyup";
    public static final String PAGINA_INSCRICAO = "inscricao";
    public static final String ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_EXECUTAR_TERMO_ENTREGA = "manter_parecer_entrega_fornecedor:executar_termo_entrega";
    public static final String ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_EXCLUIR_TERMO_ENTREGA = "manter_parecer_entrega_fornecedor:excluir_termo_entrega";
    public static final String ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_REGISTRAR_TERMO_ENTREGA = "manter_parecer_entrega_fornecedor:registrar_termo_entrega";
    public static final String ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_EXCLUIR_ANEXO_TERMO_ENTREGA = "manter_parecer_entrega_fornecedor:excluir_anexo_termo_entrega";
    public static final String ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_GERAR_TERMO_ENTREGA = "manter_parecer_entrega_fornecedor:gerar_termo_entrega";
    public static final String ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_BAIXAR_TERMO_ENTREGA = "manter_parecer_entrega_fornecedor:baixar_termo_entrega";
    // #######################################_ELEMENTOS_DO_WICKET_############################################
    private Form<NotaRemessaOrdemFornecimentoContrato> form;
    private PanelLocaisEntrega panelLocaisEntrega;
    private PanelAlerta panelAlerta;
    private PanelTermoEntrega panelTermoEntrega;
    private PanelButton panelButton;
    private PanelNumeroNotaECodigo panelNumeroNotaECodigo;
    private PanelInserirNotaFiscal panelInserirNotaFiscal;
    private PanelRegistrarEntrega panelRegistrarEntrega;
    private DataView<NotaRemessaOrdemFornecimentoContrato> dataViewLocaisEntrega;
    private DataView<NotaRemessaOrdemFornecimentoContrato> dataViewTermoEntrega;
    private DataView<TermoRecebimentoDefinitivo> dataViewListaTermoRecebimentoDefinitivo;
    private Label labelMensagem;
    private Label labelMensagemAnexo;
    private Model<String> mensagem = Model.of("");
    private Model<String> mensagemAnexo = Model.of("");
    private InfraAjaxFallbackLink<Void> btnExecutarRegistrarEntrega;
    private InfraAjaxFallbackLink<Void> btnExecutarEntrega;
    private InfraAjaxFallbackLink<Void> btnRegistrarEntrega;
    private InfraAjaxConfirmButton btnExcluirEntrega;
    private InfraAjaxConfirmButton btnExcluirAnexo;
    private DataView<AnexoNotaRemessa> newListaAnexoNotaRemessa;

    // #####################################_INJEÇÃO_DE_DEPENDENCIA_##############################################
    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private NotaRemessaService notaRemessaService;
    @Inject
    private MailService mailService;
    @Inject
    private AnexoNotaRemessaService anexoNotaRemessaService;
    @Inject
    private InscricaoProgramaService inscricaoProgramaService;
    @Inject
    private TermoRecebimentoDefinitivoService termoRecebimentoDefinitivoService;
    @Inject
    private AnexoTermoRecebimentoDefinitivoService anexoTermoRecebimentoDefinitivoService;
    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;

    // #####################################_CONSTRUTOR_##############################################
    public PrepararEntregaPage(PageParameters pageParameters, OrdemFornecimentoContrato ordemFornecimentoContrato, OrdemFornecimentoContrato radioOrdemFornecimentoContrato, boolean visualizar, List<ObjetoFornecimentoContrato> listaOrdemFornecTemp,
            List<ObjetoFornecimentoContrato> listaDevolvidosOrdemFornecTemp, HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historicoOrdem) {
        super(pageParameters);
        this.ordemFornecimentoContrato = ordemFornecimentoContrato;
        this.radioOrdemFornecimentoContrato = radioOrdemFornecimentoContrato;
        this.visualizar = visualizar;
        this.listaOrdemFornecTemp = listaOrdemFornecTemp;
        this.listaDevolvidosOrdemFornecTemp = listaDevolvidosOrdemFornecTemp;
        this.historicoOrdem = historicoOrdem;

        initVariaveis();
        initComponentes();
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see PrepararEntregaPage
     */
    private void initVariaveis() {
        setTitulo("Gerar Termo de Entrega");

        this.listaNotaRemessaLocaisEntrega = notaRemessaService.buscarNotasRemessasRemanecentes(ordemFornecimentoContrato);
        this.listaNotaRemessaTermoEntrega = notaRemessaService.buscarListaNotasRemessasCadastradas(ordemFornecimentoContrato);

        listaDevolvidos = ordemFornecimentoContratoService.buscarListaObjetoFornecimentoContrato(ordemFornecimentoContrato, null, EnumTipoObjeto.DEVOLVIDOS_SEM_VINCULO_COM_NOTA_REMESSA);
        listaNotasDeItensDevolvidos = notaRemessaService.buscarNotasRemessasDeItensDevolvidos(listaDevolvidos);

        if (listaNotasDeItensDevolvidos != null && !listaNotasDeItensDevolvidos.isEmpty()) {
            listaNotaRemessaLocaisEntrega.addAll(listaNotasDeItensDevolvidos);
        }

        if (this.listaNotaRemessaLocaisEntrega.size() > 0) {
            this.tabelaLocaisEntregaVizualizar = Boolean.TRUE;
        } else {
            this.btnExecutarEntregaVizualizar = Boolean.TRUE;
        }

        if (this.listaNotaRemessaTermoEntrega.size() > 0) {
            this.tabelaTermoEntregaVizualizar = Boolean.TRUE;
        }
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see PrepararEntregaPage
     */
    private void initComponentes() {
        form = componentFactory.newForm("form", new CompoundPropertyModel<NotaRemessaOrdemFornecimentoContrato>(notaRemessaOrdemFornecimentoContrato));
        form.add(componentFactory.newLink("lnkDashboard", HomePage.class));
        form.add(componentFactory.newLink("lnkFornecedorContrato", FornecedorContratoPage.class));
        form.add(new Link<Void>("lnkOrdemFornecimentoContrato") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(new OrdemFornecimentoContratoPage(getPageParameters(), ordemFornecimentoContrato.getContrato(), radioOrdemFornecimentoContrato, visualizar, listaOrdemFornecTemp, listaDevolvidosOrdemFornecTemp));
            }
        });
        form.add(new Label("lblNomePage", getTitulo()));
        form.add(newLabelCodigoPrograma());
        form.add(newLabelOrdemFornecimento());
        form.add(newPanelLocaisEntrega());
        form.add(newPanelAlerta());
        form.add(newPanelTermoEntrega());
        form.add(newPanelButton());

        panelRegistrarEntrega = new PanelRegistrarEntrega("panelRegistrarEntrega", null);
        panelRegistrarEntrega.setOutputMarkupId(true);
        panelRegistrarEntrega.setVisible(this.panelRegistrarEntregaVizualizar);
        form.add(panelRegistrarEntrega);

        panelInserirNotaFiscal = new PanelInserirNotaFiscal("panelInserirNotaFiscal", null);
        panelInserirNotaFiscal.setVisible(Boolean.FALSE);
        form.add(panelInserirNotaFiscal);

        add(form);
    }

    // ####################################_PAINES_###############################################

    private class PanelNumeroNotaECodigo extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelNumeroNotaECodigo(String id, Item<NotaRemessaOrdemFornecimentoContrato> item) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);

            add(newTextFieldCodigoRecebimento(item).setVisible(exibirPanelCodigo));
        }

    }

    private class PanelInserirNotaFiscal extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelInserirNotaFiscal(String id, Item<NotaRemessaOrdemFornecimentoContrato> item) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);

            add(newDataViewTermoRecebimentoDefinitivo(new SortableTermoRecebimentoDefinitivoDataProvider()));
        }

    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see initComponentes
     * @return
     */
    public PanelLocaisEntrega newPanelLocaisEntrega() {
        panelLocaisEntrega = new PanelLocaisEntrega("panelLocaisEntrega");
        panelLocaisEntrega.setVisible(this.tabelaLocaisEntregaVizualizar);
        return panelLocaisEntrega;
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see newPanelLocaisEntrega
     */
    private class PanelLocaisEntrega extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelLocaisEntrega(String id) {
            super(id);
            labelMensagem = new Label("mensagem", mensagem);
            labelMensagem.setEscapeModelStrings(Boolean.FALSE);
            add(labelMensagem);
            add(newDataViewLocaisEntrega());
            add(new InfraAjaxPagingNavigator("paginatorLocaisEntrega", dataViewLocaisEntrega));
            add(newDropLocaisEntregaPorPagina());
        }
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see newDataViewLocaisEntrega
     */
    private class PanelNotaRemessa extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelNotaRemessa(String id, Item<NotaRemessaOrdemFornecimentoContrato> item) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);
            add(newTextFieldNotaRemessa(item)); // txtQuatidadeOf
        }
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see newDataViewLocaisEntrega
     */
    private class PanelDataPrevisaoEntrega extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDataPrevisaoEntrega(String id, Item<NotaRemessaOrdemFornecimentoContrato> item) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);
            add(newDateTextFieldDataPrevisaoEntrega(item));
        }
    }

    public PanelAlerta newPanelAlerta() {
        panelAlerta = new PanelAlerta("panelAlerta");
        panelAlerta.setVisible(painelAlerta);
        return panelAlerta;
    }

    private class PanelAlerta extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelAlerta(String id) {
            super(id);
            add(new Label("alerta", "Para concluir o processo de entrega é obrigatório informar a data efetiva da entrega e anexar os Termos de Entregas e Notas de Remessas devidamente assinados pelo beneficiário."));
        }
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see initComponentes
     * @return
     */
    public PanelTermoEntrega newPanelTermoEntrega() {
        panelTermoEntrega = new PanelTermoEntrega("panelTermoEntrega");
        panelTermoEntrega.setVisible(this.tabelaTermoEntregaVizualizar);
        return panelTermoEntrega;
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see newPanelTermoEntrega
     */
    private class PanelTermoEntrega extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelTermoEntrega(String id) {
            super(id);
            add(newDataViewTermoEntrega());
            add(new InfraAjaxPagingNavigator("paginatorTermoEntrega", dataViewTermoEntrega));
            add(newDropTermoEntregaPorPagina());
        }
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see initComponentes
     * @return
     */
    public PanelButton newPanelButton() {
        panelButton = new PanelButton();
        return panelButton;
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see initComponentes
     */
    private class PanelButton extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelButton() {
            super("panelButton");
            setOutputMarkupId(true);
            add(newButtonExecutarEntrega());
            add(newButtonVoltar());
        }
    }

    private InfraAjaxFallbackLink<Void> newButtonVoltar() {
        InfraAjaxFallbackLink<Void> btnVoltar = componentFactory.newAjaxFallbackLink("btnVoltar", (target) -> voltar());
        btnVoltar.setOutputMarkupId(Boolean.TRUE);
        return btnVoltar;
    }

    protected void voltar() {
        setResponsePage(new OrdemFornecimentoContratoPage(getPageParameters(), ordemFornecimentoContrato.getContrato(), radioOrdemFornecimentoContrato, visualizar, listaOrdemFornecTemp, listaDevolvidosOrdemFornecTemp));
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see initComponentes
     */
    private class PanelRegistrarEntrega extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelRegistrarEntrega(String id, Item<NotaRemessaOrdemFornecimentoContrato> item) {
            super(id);
            FileUploadForm fileUploadForm = newFormAnexo(item);
            fileUploadForm.add(new Label("dataPrevistaAnexo", (item == null ? "" : dataCadastroBR(item.getModelObject().getDataPrevistaEntrega()))));
            fileUploadForm.add(newDropDownTipoMinuta(item));
            fileUploadForm.add(newDateTextFieldDataEfetivaEntrega(item));
            add(fileUploadForm);
            labelMensagemAnexo = new Label("mensagemAnexo", mensagemAnexo);
            labelMensagemAnexo.setEscapeModelStrings(false);
            add(labelMensagemAnexo);
            add(newDataViewAnexo(item));
            add(new InfraAjaxPagingNavigator("paginatorAnexo", newListaAnexoNotaRemessa));
            add(new PanelAnexoButton("panelAnexoButton", item));
        }
    }

    private class PanelAnexoButton extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("rawtypes")
        public PanelAnexoButton(String id, Item<NotaRemessaOrdemFornecimentoContrato> item) {
            super(id);
            btnExecutarRegistrarEntrega = componentFactory.newAjaxFallbackLink("btnExecRegistrarEntrega", (target) -> btnExecutarRegistrarEntrega(target, item));
            authorize(btnExecutarRegistrarEntrega, RENDER, ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_REGISTRAR_TERMO_ENTREGA);
            add(btnExecutarRegistrarEntrega);
            InfraAjaxFallbackLink btnCancelarAnexo = componentFactory.newAjaxFallbackLink("btnCancelarAnexo", (target) -> cancelarRegistroEntrega(target, item));
            add(btnCancelarAnexo);
        }

    }

    private void btnExecutarRegistrarEntrega(AjaxRequestTarget target, Item<NotaRemessaOrdemFornecimentoContrato> item) {
        if (!validarListaAnexos(target)) {
            return;
        }

        List<AnexoDto> lista = anexoNotaRemessaService.buscarPeloIdNotaRemessa(item.getModelObject().getId());
        List<AnexoDto> listaTemp = new ArrayList<AnexoDto>();
        for (AnexoDto anexo : lista) {
            if (anexo.getTipoArquivoTermoEntrega().equals(EnumTipoArquivoTermoEntrega.RELATORIO_RECEBIMENTO_ASSINADO)) {
                listaTemp.add(anexo);
            }
        }
        List<AnexoNotaRemessa> listaAnexoNotaRemessaTemp = SideUtil.convertAnexoDtoToEntityAnexoNotaRemessa(listaTemp);
        listaAnexoNotaRemessaTemp.addAll(this.listaAnexosNotaRemessa);

        item.getModelObject().setListaAnexosNotaRemessa(listaAnexoNotaRemessaTemp);
        notaRemessaService.incluirAlterar(item.getModelObject(), EnumBotaoClicadoNotaRemessa.REGISTRAR_ENTREGA);
        addMsgInfo("Termo de Entrega registrado com sucesso!");
        formatarLista(item.getModelObject(), Boolean.TRUE);
        mailService.enviarEmailRegistrarTermoDeEntraga(item.getModelObject(), listaInscricaoPrograma, getUrlBase(PAGINA_INSCRICAO));
        this.listaNotaRemessaTermoEntrega = notaRemessaService.buscarListaNotasRemessasCadastradas(ordemFornecimentoContrato);
        this.listaAnexosNotaRemessa = new ArrayList<AnexoNotaRemessa>();
        this.tipoArquivoTermoEntrega = null;
        panelRegistrarEntrega = new PanelRegistrarEntrega("panelRegistrarEntrega", null);
        panelRegistrarEntrega.setVisible(panelRegistrarEntregaVizualizar = Boolean.FALSE);
        form.addOrReplace(panelRegistrarEntrega);
        target.add(form);
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @param item
     * @since 29/02/2016
     * @see PanelAnexos
     * @return
     */
    private DataView<AnexoNotaRemessa> newDataViewAnexo(Item<NotaRemessaOrdemFornecimentoContrato> itemNota) {
        newListaAnexoNotaRemessa = new DataView<AnexoNotaRemessa>("anexos", new EntityDataProvider<AnexoNotaRemessa>(this.listaAnexosNotaRemessa)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<AnexoNotaRemessa> item) {
                item.add(new Label("nomeAnexo"));
                item.add(new Label("tipoArquivoTermoEntrega", item.getModelObject().getTipoArquivoTermoEntrega().getDescricao()));

                Button btnDownload = componentFactory.newButton("btnDonwload", () -> downloadAnexo(item));
                item.add(btnDownload);

                btnExcluirAnexo = componentFactory.newAJaxConfirmButton("btnExcluirAnexo", "MSG002", form, (target, form) -> excluirAnexo(target, item, itemNota));
                authorize(btnExcluirAnexo, RENDER, ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_EXCLUIR_ANEXO_TERMO_ENTREGA);
                item.add(btnExcluirAnexo);
            }
        };
        newListaAnexoNotaRemessa.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return newListaAnexoNotaRemessa;
    }

    // ####################################_COMPONENTE_WICKET_###############################################

    private TextField<String> newTextFieldCodigoRecebimento(Item<NotaRemessaOrdemFornecimentoContrato> item) {
        TextField<String> field = componentFactory.newTextField("codigoRecebimento", "Código de Recebimento", Boolean.FALSE, new PropertyModel<String>(this, "codigoRecebimento"));
        actionCodigoRecebimento(field, item);
        field.setVisible(!exibirInputNumeroOuCodigo);
        field.setOutputMarkupId(Boolean.TRUE);
        return field;
    }

    private void actionCodigoRecebimento(TextField<String> field, Item<NotaRemessaOrdemFornecimentoContrato> item) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no Model
                if (codigoRecebimento != null) {
                    item.getModelObject().setCodigoInformadoPeloFornecedor(codigoRecebimento.toUpperCase());
                }
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see initComponentes
     * @return
     */
    private InfraDropDownChoice<EnumTipoArquivoTermoEntrega> newDropDownTipoMinuta(Item<NotaRemessaOrdemFornecimentoContrato> item) {
        List<EnumTipoArquivoTermoEntrega> listaEnum = new ArrayList<EnumTipoArquivoTermoEntrega>();
        if (item != null) {
            listaEnum.add(EnumTipoArquivoTermoEntrega.NOTA_REMESSA);
            listaEnum.add(EnumTipoArquivoTermoEntrega.TERMO_ENTREGA_ASSINADO);
        }
        InfraDropDownChoice<EnumTipoArquivoTermoEntrega> dropDownChoice = componentFactory.newDropDownChoice("tipoArquivo", "Escolha Tipo de Arquivo", Boolean.FALSE, "valor", "descricao", new PropertyModel<EnumTipoArquivoTermoEntrega>(this, "tipoArquivoTermoEntrega"), listaEnum, null);
        acaoDropDown(dropDownChoice, item);
        dropDownChoice.setNullValid(Boolean.TRUE);
        return dropDownChoice;
    }

    private void acaoDropDown(InfraDropDownChoice<EnumTipoArquivoTermoEntrega> drop, Item<NotaRemessaOrdemFornecimentoContrato> item) {
        drop.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (drop.getValue().equalsIgnoreCase("T")) {
                    exibirPanelCodigo = Boolean.TRUE;
                } else {
                    exibirPanelCodigo = Boolean.FALSE;
                }

                panelNumeroNotaECodigo.addOrReplace(newTextFieldCodigoRecebimento(item));
                panelNumeroNotaECodigo.setVisible(exibirPanelCodigo);
                target.add(panelNumeroNotaECodigo);
            }
        });
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see initComponentes
     * @return
     */
    private Label newLabelCodigoPrograma() {
        return new Label("nomePrograma", ordemFornecimentoContrato.getContrato().getPrograma().getCodigoIdentificadorProgramaPublicado());
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see initComponentes
     * @return
     */
    private Label newLabelOrdemFornecimento() {
        return new Label("ordemFornecimento", historicoOrdem.getNumeroDocumentoSei());
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see PanelLocaisEntrega
     * @return
     */
    private DropDownChoice<Integer> newDropLocaisEntregaPorPagina() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("locaisEntregaPorPagina", new LambdaModel<Integer>(this::getLocaisEntregaPorPagina, this::setLocaisEntregaPorPagina), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewLocaisEntrega.setItemsPerPage(getLocaisEntregaPorPagina());
                atualizarDataViewLocaisEntrega(target);
            };
        });
        return dropDownChoice;
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see PanelLocaisEntrega
     * @return
     */
    private DataView<NotaRemessaOrdemFornecimentoContrato> newDataViewLocaisEntrega() {
        dataViewLocaisEntrega = new DataView<NotaRemessaOrdemFornecimentoContrato>("listaLocaisEntregaNewDataView", new ProviderLocaisEntrega()) {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("rawtypes")
            @Override
            protected void populateItem(Item<NotaRemessaOrdemFornecimentoContrato> item) {
                AttributeAppender classeAtivarPopover = new AttributeAppender("class", "pop", " ");

                String[] lista = formatarLista(item.getModelObject(), false);
                item.add(new Label("ufItem", lista[0]));
                item.add(new Label("minucipioItem", lista[1]));
                item.add(new Label("enderecoItens", lista[2]));

                String iconeInformativo1 = "<a tabindex=\"0\" role=\"button\" data-toggle=\"popover\" data-trigger=\"focus\" data-content=\"" + lista[7] + "\"><i style=\"color:green;\" class=\"fa fa-info-circle\"> </i></a>";
                Label lblDescricao1 = new Label("itensQuant", iconeInformativo1);
                lblDescricao1.setEscapeModelStrings(false);
                lblDescricao1.setOutputMarkupId(true);
                if (item.getModelObject().getListaItensNotaRemessaOrdemFornecimentoContratos().size() > 10)
                    lblDescricao1.add(classeAtivarPopover);
                item.add(lblDescricao1);

                item.add(new PanelDataPrevisaoEntrega("dataPrevisaoEntregaPanel", item));
                item.add(new PanelNotaRemessa("notaRemessaPanel", item));
                InfraAjaxFallbackLink btnGerarTermoEntrega = componentFactory.newAjaxFallbackLink("btnGerarTermoEntrega", (target) -> gerarTermoEntrega(target, item));
                authorize(btnGerarTermoEntrega, RENDER, ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_GERAR_TERMO_ENTREGA);
                item.add(btnGerarTermoEntrega);
            }
        };
        dataViewLocaisEntrega.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataViewLocaisEntrega;
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see PanelDataPrevisaoEntrega
     * @param item
     * @return
     * 
     */
    private InfraLocalDateTextField newDateTextFieldDataPrevisaoEntrega(Item<NotaRemessaOrdemFornecimentoContrato> item) {
        InfraLocalDateTextField field = componentFactory.newDateTextFieldWithDatePicker("dataPrevistaEntrega", "Data Previsão Entrega", false, new PropertyModel(item.getModelObject(), "dataPrevistaEntrega"), "dd/MM/yyyy", "pt-BR");
        actionDataPrevisaoEntrega(field);
        return field;
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see newDateTextFieldDataPrevisaoEntrega
     * @param field
     */
    private void actionDataPrevisaoEntrega(TextField<LocalDate> field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no Model
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @param item
     * @since 10/05/2016
     * @see PanelRegistrarEntrega
     * @return
     */
    private InfraLocalDateTextField newDateTextFieldDataEfetivaEntrega(Item<NotaRemessaOrdemFornecimentoContrato> item) {
        InfraLocalDateTextField field = componentFactory.newDateTextFieldWithDatePicker("dataEfetivaEntrega", "Data Efetiva da Entrega", false, new PropertyModel(this, "dataEfetivaEntrega"), "dd/MM/yyyy", "pt-BR");
        if (item != null) {
            /*field.setEnabled(!(item.getModelObject().getOrdemFornecimento().getStatusOrdemFornecimento() != null && !item.getModelObject().getOrdemFornecimento().getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.EMITIDA) && !item.getModelObject().getOrdemFornecimento()
                    .getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.EXECUTADA ) && !item.getModelObject().getOrdemFornecimento().getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.RECEBIDA)));*/
            field.setEnabled(item.getModelObject().getDataEfetivaEntrega() == null);
        }
        actionDataEfetivaEntrega(field, item);
        return field;
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see newDateTextFieldDataEfetivaEntrega
     * @param field
     * @param item
     */
    private void actionDataEfetivaEntrega(TextField<LocalDate> field, Item<NotaRemessaOrdemFornecimentoContrato> item) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no Model
                if (dataEfetivaEntrega != null) {
                    item.getModelObject().setDataEfetivaEntrega(dataEfetivaEntrega);
                }
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see PanelNotaRemessa
     * @param item
     * @return
     */
    public TextField<String> newTextFieldNotaRemessa(Item<NotaRemessaOrdemFornecimentoContrato> item) {
        TextField<String> field = componentFactory.newTextField("numeroNotaRemessa", "Nota Remessa", false, new PropertyModel<String>(item.getModelObject(), "numeroNotaRemessa"));
        acaoNotaRemessa(field, item);
        return field;
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see newTextFieldNotaRemessa
     * @param textQuantidade
     * @param item
     * @return
     */
    private TextField<String> acaoNotaRemessa(TextField<String> textQuantidade, Item<NotaRemessaOrdemFornecimentoContrato> item) {
        textQuantidade.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
            private static final long serialVersionUID = 1L;

            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no model
            }
        });
        return textQuantidade;
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see newDataViewLocaisEntrega
     * @param item
     * @param target
     */
    private void gerarTermoEntrega(AjaxRequestTarget target, Item<NotaRemessaOrdemFornecimentoContrato> item) {

        if (!validarObrigatoriedade(target, item)) {
            return;
        }

        this.listaNotaRemessaTermoEntrega.add(item.getModelObject());
        this.listaNotaRemessaLocaisEntrega.remove(item.getModelObject());
        panelTermoEntrega.setVisible((this.tabelaTermoEntregaVizualizar = Boolean.TRUE));
        notaRemessaService.incluirAlterar(item.getModelObject(), null);
        addMsgInfo("Termo de Entrega gerado com sucesso!");
        if (this.listaNotaRemessaLocaisEntrega.size() == 0) {
            panelLocaisEntrega.setVisible((this.tabelaLocaisEntregaVizualizar = Boolean.FALSE));
            panelButton.setVisible((this.btnExecutarEntregaVizualizar = Boolean.TRUE));
            atualizarPainelButton(target);
        }
        atualizarDataViewTermoEntrega(target);
        atualizarDataViewLocaisEntrega(target);
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see PanelTermoEntrega
     * @return
     */
    private DropDownChoice<Integer> newDropTermoEntregaPorPagina() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("termoEntregaPorPagina", new LambdaModel<Integer>(this::getTermoEntregaPorPagina, this::setTermoEntregaPorPagina), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewTermoEntrega.setItemsPerPage(getTermoEntregaPorPagina());
                atualizarDataViewTermoEntrega(target);
            };
        });
        return dropDownChoice;
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see PanelTermoEntrega
     * @return
     */
    private DataView<NotaRemessaOrdemFornecimentoContrato> newDataViewTermoEntrega() {
        dataViewTermoEntrega = new DataView<NotaRemessaOrdemFornecimentoContrato>("listaTermoEntregaNewDataView", new ProviderTermoEntrega()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<NotaRemessaOrdemFornecimentoContrato> item) {
                AttributeAppender classeAtivarPopover = new AttributeAppender("class", "pop", " ");

                item.getModelObject().setListaItensNotaRemessaOrdemFornecimentoContratos(notaRemessaService.buscarItensNotaRemessa(item.getModelObject()));
                String[] lista = formatarLista(item.getModelObject(), false);
                item.add(new Label("nomeBeneficiarioitem", lista[5]));
                item.add(new Label("ufItem", lista[0]));
                item.add(new Label("enderecoItens", lista[2]));

                String iconeInformativo2 = "<a tabindex=\"0\" role=\"button\" data-toggle=\"popover\" data-trigger=\"focus\" data-content=\"" + lista[7] + "\"><i style=\"color:green;\" class=\"fa fa-info-circle\"> </i></a>";
                Label lblDescricao2 = new Label("itensQuant2", iconeInformativo2);
                lblDescricao2.setEscapeModelStrings(false);
                lblDescricao2.setOutputMarkupId(true);
                if (item.getModelObject().getListaItensNotaRemessaOrdemFornecimentoContratos().size() > 10)
                    lblDescricao2.add(classeAtivarPopover);
                item.add(lblDescricao2);

                item.add(new Label("dataPrevistaEntrega", DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataPrevistaEntrega(), "dd/MM/yyyy")));
                item.add(new Label("numeroNotaRemessa"));

                String dataEfetivaEntrega1 = DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataEfetivaEntrega(), "dd/MM/yyyy");
                item.add(new Label("dataEfetivaEntrega1", dataEfetivaEntrega1 == null ? "Não Registrada" : dataEfetivaEntrega1));

                if (item.getModelObject().getDataEfetivaEntrega() != null) {
                    AttributeAppender style = new AttributeAppender("style", "background:#defaea;", " ");
                    item.add(style);
                }

                Button btnDownload = componentFactory.newButton("btnDonwloadTermoEntrega", () -> download(item));
                authorize(btnDownload, RENDER, ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_BAIXAR_TERMO_ENTREGA);
                item.add(btnDownload);

                btnExcluirEntrega = componentFactory.newAJaxConfirmButton("btnExcluirEntrega", "MSG012", form, (target, formz) -> excluirEntrega(target, item));
                authorize(btnExcluirEntrega, RENDER, ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_EXCLUIR_TERMO_ENTREGA);
                btnExcluirEntrega.setVisible(validabtnExcluirEntrega(item.getModelObject()));
                item.add(btnExcluirEntrega);

                btnRegistrarEntrega = componentFactory.newAjaxFallbackLink("btnRegistrarEntrega", (target) -> registrarEntrega(target, item));
                authorize(btnRegistrarEntrega, RENDER, ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_REGISTRAR_TERMO_ENTREGA);
                btnRegistrarEntrega.setVisible(validaRegistrarEntrega(item.getModelObject()));
                item.add(btnRegistrarEntrega);

                InfraAjaxFallbackLink<Void> btnInserirNotaFiscal = componentFactory.newAjaxFallbackLink("btnInserirNotaFiscal", (target) -> enviarNotaFiscal(target, item));
                List<TermoRecebimentoDefinitivo> listaTRD = termoRecebimentoDefinitivoService.buscarTodosOsTermosRecebimentoDefinitivoPorNotaRemessa(item.getModelObject().getId());
                btnInserirNotaFiscal.setVisible(listaTRD.size() > 0);
                item.add(btnInserirNotaFiscal);

            }
        };
        dataViewTermoEntrega.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataViewTermoEntrega;
    }

    private Boolean validabtnExcluirEntrega(NotaRemessaOrdemFornecimentoContrato notaRemessaLocaisEntrega) {
        if (notaRemessaLocaisEntrega.getOrdemFornecimento().getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.EMITIDA)) {
            return true;
        } else if (notaRemessaLocaisEntrega.getOrdemFornecimento().getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.DEVOLVIDA)) {
            if (notaRemessaLocaisEntrega.getNotaDevolucao() == null || notaRemessaLocaisEntrega.getNotaDevolucao() == false || !notaRemessaLocaisEntrega.getStatusExecucaoFornecedor().equals(EnumStatusExecucaoNotaRemessaFornecedor.EM_PREPARACAO)) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }

    }

    private Boolean validaRegistrarEntrega(NotaRemessaOrdemFornecimentoContrato notaRemessaLocaisEntrega) {
        if (notaRemessaLocaisEntrega.getOrdemFornecimento().getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.EXECUTADA) || notaRemessaLocaisEntrega.getOrdemFornecimento().getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.ENTREGUE)
                || notaRemessaLocaisEntrega.getOrdemFornecimento().getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.RECEBIDA)) {
            return true;
        } else {
            return false;
        }
    }

    private DataView<TermoRecebimentoDefinitivo> newDataViewTermoRecebimentoDefinitivo(SortableTermoRecebimentoDefinitivoDataProvider dp) {
        dataViewListaTermoRecebimentoDefinitivo = new DataView<TermoRecebimentoDefinitivo>("listaTermoRecebimentoDefinitivo", dp) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<TermoRecebimentoDefinitivo> item) {

                item.add(new Label("numeroTermoRecebimentoDefinitivo", item.getModelObject().getNomeAnexo()));

                StringBuilder sb = new StringBuilder(1);
                sb.append("<table><tr><td><center>ID</center></td><td>&emsp;</td><td><center>ITEM</center></td></tr><tr><td colspan='3'><hr></hr></td></tr>");

                for (ObjetoFornecimentoContrato obj : termoRecebimentoDefinitivoService.buscarTodosOsObjetosFornecimentoContratoPeloIdTermo(item.getModelObject().getId())) {
                    sb.append("<tr><td><center>" + obj.getId() + "</center></td><td>&emsp;</td><td><center>" + obj.getItem().getNomeBem() + "</center></td></tr>");
                }
                sb.append("</table>");

                String iconeIdItem = "<a type=\"button\" data-toggle=\"popover\" data-content=\"" + sb.toString() + "\" data-trigger=\"hover\"><i style=\"color:green;\" class=\"fa fa-info-circle\"> </i></a>";

                Label lblDescricaoiconeIdItem = new Label("itemTermoDefinitivo", iconeIdItem);
                lblDescricaoiconeIdItem.setEscapeModelStrings(Boolean.FALSE);
                lblDescricaoiconeIdItem.setOutputMarkupId(Boolean.TRUE);
                item.add(lblDescricaoiconeIdItem);

                item.add(newTextFieldNumeroNotaFiscal(item.getModelObject()));

                Button btnDownload = componentFactory.newButton("btnBaixarTRD", () -> baixarTRD(item.getModelObject()));
                item.add(btnDownload);

                item.add(newFormUploadNotaFiscal(item.getModelObject()));
                
                Button btnDownloadNF = componentFactory.newButton("btnDownloadNF", () -> downloadNF(item.getModelObject()));
                item.add(btnDownloadNF);
            }
        };
        dataViewListaTermoRecebimentoDefinitivo.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataViewListaTermoRecebimentoDefinitivo;
    }

    private void baixarTRD(TermoRecebimentoDefinitivo termo) {
        if (termo.getId() != null) {
            AnexoDto retorno = this.anexoTermoRecebimentoDefinitivoService.buscarPeloId(termo.getId());
            SideUtil.download(retorno.getConteudo(), "Termo_Recebimento_Definitivo_".concat(retorno.getNomeAnexo()).concat(".pdf"));
        }
    }
    
    private void downloadNF(TermoRecebimentoDefinitivo termoRecebimentoDefinitivo) {
        if (termoRecebimentoDefinitivo.getId() != null) {
            AnexoDto retorno = anexoTermoRecebimentoDefinitivoService.buscarNotaFiscalPeloIdTermoRecebimento(termoRecebimentoDefinitivo.getId());
            SideUtil.download(retorno.getConteudo(), termoRecebimentoDefinitivo.getNomeAnexoNotaFiscal());
        }
    }

    private FileUploadFormInserirNotaFiscal newFormUploadNotaFiscal(TermoRecebimentoDefinitivo termo) {
        FileUploadFormInserirNotaFiscal file = new FileUploadFormInserirNotaFiscal("formUploadNotaFiscal", new PropertyModel<List<FileUpload>>(this, "uploadsNotaFiscal"));
        file.add(new AjaxFormSubmitBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                super.onSubmit(target);
                inserirNotaFiscal(target, termo);
            }

        });
        return file;
    }

    protected void inserirNotaFiscal(AjaxRequestTarget target, TermoRecebimentoDefinitivo termo) {
        if (this.uploadsNotaFiscal != null) {
            if (!this.uploadsNotaFiscal.isEmpty()) {
                if (termo.getNumeroNotaFiscal() != null && !termo.getNumeroNotaFiscal().equalsIgnoreCase("")) {
                    for (FileUpload component : uploadsNotaFiscal) {
                        termo.setNomeAnexoNotaFiscal(component.getClientFileName());
                        termo.setNotaFiscal(component.getBytes());

                        this.termoRecebimentoDefinitivoService.alterar(termo);
                    }
                    addMsgInfo("Nota Fiscal inserida com sucesso!");
                } else {
                    addMsgError("Informe o 'N° Nota Fiscal'.");
                }
            }
        } else {
            addMsgError("Selecione um arquivo para anexar.");

        }
        target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        target.add(panelInserirNotaFiscal);
    }

    private class FileUploadFormInserirNotaFiscal extends Form<List<FileUpload>> {
        private static final long serialVersionUID = 1L;

        public FileUploadFormInserirNotaFiscal(String id, IModel<List<FileUpload>> model) {
            super(id, model);
            setMultiPart(Boolean.TRUE);

            FileUploadField fileUploadField = new FileUploadField("inputUploadNotaFiscal", model);
            fileUploadField.add(new UploadValidator());
            add(fileUploadField);

        }

        /* Irá validar para não receber arquivos do tipo : .exe, .bat */
        private class UploadValidator implements IValidator<List<FileUpload>> {
            private static final long serialVersionUID = 1L;

            @Override
            public void validate(IValidatable<List<FileUpload>> validatable) {
                List<FileUpload> list = validatable.getValue();
                if (!list.isEmpty()) {
                    FileUpload fileUpload = list.get(0);
                    String extension = FilenameUtils.getExtension(fileUpload.getClientFileName());
                    if ("exe".equalsIgnoreCase(extension) || "bat".equalsIgnoreCase(extension)) {
                        ValidationError error = new ValidationError("Não são permitidos arquivos executáveis como .exe,.bat e etc.");
                        validatable.error(error);
                    }
                }
            }
        }
    }

    private class SortableTermoRecebimentoDefinitivoDataProvider extends SortableDataProvider<TermoRecebimentoDefinitivo, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<TermoRecebimentoDefinitivo> iterator(long first, long count) {
            return listaTermoRecebimentoDefinitivo.iterator();
        }

        @Override
        public long size() {
            return listaTermoRecebimentoDefinitivo.size();
        }

        @Override
        public IModel<TermoRecebimentoDefinitivo> model(TermoRecebimentoDefinitivo object) {
            return new CompoundPropertyModel<TermoRecebimentoDefinitivo>(object);
        }

    }

    private TextField<String> newTextFieldNumeroNotaFiscal(TermoRecebimentoDefinitivo termo) {
        TextField<String> field = componentFactory.newTextField("numeroNotaFiscal", "Nota Fiscal", Boolean.FALSE, new PropertyModel<String>(termo, "numeroNotaFiscal"));
        acaoNumeroNotaFiscal(field, termo);
        field.add(StringValidator.maximumLength(20));
        field.setOutputMarkupId(Boolean.TRUE);
        return field;
    }

    private TextField<String> acaoNumeroNotaFiscal(TextField<String> textNumeroNotaFiscal, TermoRecebimentoDefinitivo nota) {
        textNumeroNotaFiscal.add(new AjaxFormComponentUpdatingBehavior(ONKEYUP) {
            private static final long serialVersionUID = 1L;

            protected void onUpdate(AjaxRequestTarget target) {

            }
        });
        return textNumeroNotaFiscal;
    }

    private void enviarNotaFiscal(AjaxRequestTarget target, Item<NotaRemessaOrdemFornecimentoContrato> item) {
        this.listaTermoRecebimentoDefinitivo = this.termoRecebimentoDefinitivoService.buscarTodosOsTermosRecebimentoDefinitivoPorNotaRemessa(item.getModelObject().getId());

        panelRegistrarEntrega = new PanelRegistrarEntrega("panelRegistrarEntrega", item);
        panelRegistrarEntrega.setVisible(this.panelRegistrarEntregaVizualizar = Boolean.FALSE);
        form.addOrReplace(panelRegistrarEntrega);

        panelInserirNotaFiscal = new PanelInserirNotaFiscal("panelInserirNotaFiscal", item);
        panelInserirNotaFiscal.setVisible(Boolean.TRUE);
        form.addOrReplace(panelInserirNotaFiscal);
        target.appendJavaScript("ancorarResultadoPesquisa('#ancoraPanelRegistrarEntrega');");
        target.add(form);
    }

    private void download(Item<NotaRemessaOrdemFornecimentoContrato> item) {
        TermoEntregaCodeOfBuilder jasper = new TermoEntregaCodeOfBuilder(SideUtil.getCaminhoCompletoRelatorios(getHttpServletRequest().getServletContext()));
        jasper.setNomeQrCode("Termo de Entrega");
        jasper.setTipoQrCode(EnumTipoMinuta.PDF);
        jasper.setDataList(popularLista(item));
        ByteArrayOutputStream exportar = jasper.exportToByteArray();
        SideUtil.download(exportar.toByteArray(), "Termo_Entrega" + historicoOrdem.getNumeroDocumentoSei() + ".pdf");

    }

    private List<TermoEntregaDto> popularLista(Item<NotaRemessaOrdemFornecimentoContrato> item) {
        List<TermoEntregaDto> novaLista = new ArrayList<TermoEntregaDto>();
        TermoEntregaDto termoEntregaDto = new TermoEntregaDto();

        termoEntregaDto.setNomeFornecedor(item.getModelObject().getOrdemFornecimento().getContrato().getFornecedor().getNomeEntidade());
        termoEntregaDto.setCnpjFornecedor(CnpjUtil.imprimeCNPJ(item.getModelObject().getOrdemFornecimento().getContrato().getFornecedor().getNumeroCnpj()));
        termoEntregaDto.setNumeroContrato(item.getModelObject().getOrdemFornecimento().getContrato().getNumeroContrato());
        termoEntregaDto.setPeriodoVigenciaContrato(DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getOrdemFornecimento().getContrato().getDataVigenciaFimContrato(), "dd/MM/yyyy"));
        termoEntregaDto.setNomePreposto(item.getModelObject().getOrdemFornecimento().getContrato().getPreposto().getNomePessoa());
        termoEntregaDto.setTelefonePreposto(MascaraUtils.formatarMascaraTelefone(item.getModelObject().getOrdemFornecimento().getContrato().getPreposto().getNumeroTelefone()));
        termoEntregaDto.setEmailPreposto(item.getModelObject().getOrdemFornecimento().getContrato().getPreposto().getEmail());
        termoEntregaDto.setNumeroOF(historicoOrdem.getNumeroDocumentoSei());
        termoEntregaDto.setNumeroNF(item.getModelObject().getNumeroNotaRemessa());

        String[] lista = formatarLista(item.getModelObject(), Boolean.TRUE);
        termoEntregaDto.setNomeBeneficiario(lista[5]);
        termoEntregaDto.setEnderecoBeneficiario(lista[2]);

        for (InscricaoPrograma inscricaoPrograma : listaInscricaoPrograma) {
            termoEntregaDto.setNomeRepresentante(inscricaoPrograma.getPessoaEntidade().getPessoa().getNomePessoa());
            termoEntregaDto.setTelefoneRepresentante(MascaraUtils.formatarMascaraTelefone(inscricaoPrograma.getPessoaEntidade().getPessoa().getNumeroTelefone()));
            termoEntregaDto.setEmailRepresentante(inscricaoPrograma.getPessoaEntidade().getPessoa().getEmail());
        }

        for (ItensNotaRemessaOrdemFornecimentoContrato INFOFC : item.getModelObject().getListaItensNotaRemessaOrdemFornecimentoContratos()) {
            TermoEntregaitensDto termoEntregaitensDto = new TermoEntregaitensDto();
            termoEntregaitensDto.setNomeBem(INFOFC.getItemOrdemFornecimentoContrato().getItem().getNomeBem());
            termoEntregaitensDto.setQuantidade(INFOFC.getItemOrdemFornecimentoContrato().getQuantidade());
            termoEntregaDto.getListaItens().add(termoEntregaitensDto);
        }

        for (InscricaoPrograma inscricaoPrograma : listaInscricaoPrograma) {
            TermoEntregaitensDto termoEntregaitensDto = new TermoEntregaitensDto();
            termoEntregaitensDto.setNomeMembros(inscricaoPrograma.getPessoaEntidade().getPessoa().getNomePessoa().toUpperCase());
            termoEntregaDto.getListaMembros().add(termoEntregaitensDto);
            for (ComissaoRecebimento comissaoRecebimento : inscricaoProgramaService.buscarComissaoRecebimento(inscricaoPrograma)) {
                if (comissaoRecebimento.getMembroComissao().getStatusPessoa() == EnumStatusPessoa.ATIVO && !inscricaoPrograma.getPessoaEntidade().getPessoa().getNomePessoa().toUpperCase().equals(comissaoRecebimento.getMembroComissao().getNomePessoa().toUpperCase())) {
                    termoEntregaitensDto = new TermoEntregaitensDto();
                    termoEntregaitensDto.setNomeMembros(comissaoRecebimento.getMembroComissao().getNomePessoa().toUpperCase());
                    termoEntregaDto.getListaMembros().add(termoEntregaitensDto);
                }
            }
        }

        novaLista.add(termoEntregaDto);
        return novaLista;
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see PanelButton
     * @return
     */
    @SuppressWarnings("rawtypes")
    private InfraAjaxFallbackLink newButtonExecutarEntrega() {
        btnExecutarEntrega = componentFactory.newAjaxFallbackLink("btnExecutarEntrega", (target) -> executarEntrega(target));
        for (NotaRemessaOrdemFornecimentoContrato notaRemessa : this.listaNotaRemessaTermoEntrega) {
            if (notaRemessa.getOrdemFornecimento().getStatusOrdemFornecimento() != null
                    && notaRemessa.getOrdemFornecimento().getStatusOrdemFornecimento() == EnumStatusOrdemFornecimento.EXECUTADA
                    || (notaRemessa.getOrdemFornecimento().getStatusOrdemFornecimento() != null && !notaRemessa.getOrdemFornecimento().getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.EMITIDA)
                            && !notaRemessa.getOrdemFornecimento().getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.EXECUTADA) && !notaRemessa.getOrdemFornecimento().getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.DEVOLVIDA))) {
                this.btnExecutarEntregaVizualizar = Boolean.FALSE;
                this.painelAlerta = Boolean.TRUE;
                break;
            }
        }
        authorize(btnExecutarEntrega, RENDER, ROLE_MANTER_PARECER_ENTREGA_FORNECEDOR_EXECUTAR_TERMO_ENTREGA);
        btnExecutarEntrega.setVisible(btnExecutarEntregaVizualizar);
        panelAlerta.setVisible(painelAlerta);
        return btnExecutarEntrega;
    }

    private void executarEntrega(AjaxRequestTarget target) {
        List<NotaRemessaOrdemFornecimentoContrato> listaTempItens = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();
        List<NotaRemessaOrdemFornecimentoContrato> listaTempItensDevolvidos = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();
        for (NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato : listaNotaRemessaTermoEntrega) {
            if (notaRemessaOrdemFornecimentoContrato.getNotaDevolucao() == null || notaRemessaOrdemFornecimentoContrato.getNotaDevolucao() == false) {
                listaTempItens.add(notaRemessaOrdemFornecimentoContrato);
            } else
                listaTempItensDevolvidos.add(notaRemessaOrdemFornecimentoContrato);
        }

        if (listaTempItensDevolvidos.size() > 0) {
            executaListaEntrega(listaTempItensDevolvidos);
        } else {
            executaListaEntrega(listaTempItens);
        }

        addMsgInfo("Entrega executada com sucesso!");
        atualizarPainelButton(target);
        atualizarDataViewTermoEntrega(target);
    }

    private void executaListaEntrega(List<NotaRemessaOrdemFornecimentoContrato> listaTemp) {
        for (NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato : listaTemp) {
            notaRemessaOrdemFornecimentoContrato.setListaAnexosNotaRemessa(new ArrayList<AnexoNotaRemessa>());
            notaRemessaOrdemFornecimentoContrato = notaRemessaService.executarOrdemFornecimento(notaRemessaOrdemFornecimentoContrato);
            String[] lista = formatarLista(notaRemessaOrdemFornecimentoContrato, Boolean.TRUE);
            mailService.enviarEmailBeneficiariosTermoDeEntraga(notaRemessaOrdemFornecimentoContrato, listaInscricaoPrograma, lista[3]);
        }
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see newDataViewTermoEntrega
     * @param target
     * @param item
     * @return
     */
    private void registrarEntrega(AjaxRequestTarget target, Item<NotaRemessaOrdemFornecimentoContrato> item) {
        atualizarPainelRegistrarEntrega(target, item);
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @param item
     * @since 10/05/2016
     * @see PanelRegistrarEntrega
     * @return
     */
    private FileUploadForm newFormAnexo(Item<NotaRemessaOrdemFornecimentoContrato> item) {
        return new FileUploadForm("anexoForm", new LambdaModel<List<FileUpload>>(this::getUploads, this::setUploads), item);
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see newDataViewTermoEntrega
     * @param target
     * @param item
     */
    private void excluirEntrega(AjaxRequestTarget target, Item<NotaRemessaOrdemFornecimentoContrato> item) {
        notaRemessaService.excluir(item.getModelObject());
        addMsgInfo("Excluido com sucesso!");
        this.listaNotaRemessaLocaisEntrega = notaRemessaService.buscarNotasRemessasRemanecentes(ordemFornecimentoContrato);
        this.listaNotaRemessaTermoEntrega = notaRemessaService.buscarListaNotasRemessasCadastradas(ordemFornecimentoContrato);
        this.listaDevolvidos = ordemFornecimentoContratoService.buscarListaObjetoFornecimentoContrato(ordemFornecimentoContrato, null, EnumTipoObjeto.DEVOLVIDOS_SEM_VINCULO_COM_NOTA_REMESSA);
        this.listaNotasDeItensDevolvidos = notaRemessaService.buscarNotasRemessasDeItensDevolvidos(listaDevolvidos);
        this.listaNotaRemessaLocaisEntrega.addAll(listaNotasDeItensDevolvidos);
        panelLocaisEntrega.setVisible((this.tabelaLocaisEntregaVizualizar = Boolean.TRUE));
        panelButton.setVisible((this.btnExecutarEntregaVizualizar = Boolean.TRUE));

        btnExecutarEntrega.setVisible(btnExecutarEntregaVizualizar = Boolean.FALSE);
        atualizarPainelButton(target);

        if (this.listaNotaRemessaTermoEntrega.size() == 0) {
            panelTermoEntrega.setVisible(this.tabelaTermoEntregaVizualizar = Boolean.FALSE);
        }

        atualizarDataViewTermoEntrega(target);
        atualizarDataViewLocaisEntrega(target);
        panelRegistrarEntrega = new PanelRegistrarEntrega("panelRegistrarEntrega", null);
        panelRegistrarEntrega.setVisible(panelRegistrarEntregaVizualizar = Boolean.FALSE);
        form.addOrReplace(panelRegistrarEntrega);
        target.add(form);
    }

    // ####################################_METODOS_COMPLEMENTAR_###############################################

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see gerarTermoEntrega
     * @param item
     * @param target
     * @return
     */
    private boolean validarObrigatoriedade(AjaxRequestTarget target, Item<NotaRemessaOrdemFornecimentoContrato> item) {
        boolean validar = Boolean.TRUE;
        LocalDate dataAtual = LocalDate.now();
        String msg = "";

        if ("".equals(item.getModelObject().getDataPrevistaEntrega()) || item.getModelObject().getDataPrevistaEntrega() == null) {
            msg += "<p><li> Campo 'Data Previsão Entrega' é obrigatório.</li><p />";
            validar = Boolean.FALSE;
        }

        if (item.getModelObject().getDataPrevistaEntrega() != null && item.getModelObject().getDataPrevistaEntrega().isBefore(dataAtual)) {
            msg += "<p><li> Campo 'Data Previsão Entrega' não pode ser inferior a data atual.</li><p />";
            validar = Boolean.FALSE;
        }

        if ("".equals(item.getModelObject().getNumeroNotaRemessa()) || item.getModelObject().getNumeroNotaRemessa() == null) {
            msg += "<p><li> Campo 'Nota Remessa' é obrigatório.</li><p />";
            validar = Boolean.FALSE;
        }

        if (!validar) {
            mensagem.setObject(msg);
        } else {
            mensagem.setObject("");
        }
        target.add(labelMensagem);
        return validar;
    }

    /**
     * @param target
     */
    private void atualizarDataViewLocaisEntrega(AjaxRequestTarget target) {
        panelLocaisEntrega.addOrReplace(newDataViewLocaisEntrega());
        panelLocaisEntrega.addOrReplace(new InfraAjaxPagingNavigator("paginatorLocaisEntrega", dataViewLocaisEntrega));
        target.add(form);
    }

    /**
     * @param target
     */
    private void atualizarDataViewTermoEntrega(AjaxRequestTarget target) {
        panelTermoEntrega.addOrReplace(newDataViewTermoEntrega());
        panelTermoEntrega.addOrReplace(new InfraAjaxPagingNavigator("paginatorTermoEntrega", dataViewTermoEntrega));
        target.add(form);
    }

    /**
     * @param target
     */
    private void atualizarPainelButton(AjaxRequestTarget target) {
        panelButton.addOrReplace(newButtonExecutarEntrega());
        target.add(panelButton);
    }

    /**
     * @param target
     * @param item
     */
    private void atualizarPainelRegistrarEntrega(AjaxRequestTarget target, Item<NotaRemessaOrdemFornecimentoContrato> item) {
        List<AnexoDto> lista = anexoNotaRemessaService.buscarPeloIdNotaRemessa(item.getModelObject().getId());
        List<AnexoDto> listaTemp = new ArrayList<AnexoDto>();

        for (AnexoDto anexo : lista) {
            if (!anexo.getTipoArquivoTermoEntrega().equals(EnumTipoArquivoTermoEntrega.RELATORIO_RECEBIMENTO_ASSINADO)) {
                listaTemp.add(anexo);
            }
        }

        this.listaAnexosNotaRemessa = SideUtil.convertAnexoDtoToEntityAnexoNotaRemessa(listaTemp);
        this.dataEfetivaEntrega = item.getModelObject().getDataEfetivaEntrega();
        this.tipoArquivoTermoEntrega = null;
        this.exibirPanelCodigo = Boolean.FALSE;
        panelRegistrarEntrega = new PanelRegistrarEntrega("panelRegistrarEntrega", item);
        panelRegistrarEntrega.setVisible(this.panelRegistrarEntregaVizualizar = Boolean.TRUE);
        form.addOrReplace(panelRegistrarEntrega);

        panelInserirNotaFiscal = new PanelInserirNotaFiscal("panelInserirNotaFiscal", item);
        panelInserirNotaFiscal.setVisible(Boolean.FALSE);
        form.addOrReplace(panelInserirNotaFiscal);

        target.appendJavaScript("ancorarResultadoPesquisa('#ancoraPanelRegistrarEntrega');");
        target.add(form);
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @param item
     * @since 10/05/2016
     * @see PanelRegistrarEntrega
     */
    private void cancelarRegistroEntrega(AjaxRequestTarget target, Item<NotaRemessaOrdemFornecimentoContrato> item) {
        this.listaAnexosNotaRemessa = new ArrayList<AnexoNotaRemessa>();
        this.tipoArquivoTermoEntrega = null;
        if (item.getModelObject().getOrdemFornecimento().getStatusOrdemFornecimento() != null
                && (item.getModelObject().getOrdemFornecimento().getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.EMITIDA) || item.getModelObject().getOrdemFornecimento().getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.EXECUTADA))) {
            dataEfetivaEntrega = null;
            item.getModelObject().setDataEfetivaEntrega(null);
        }
        panelRegistrarEntrega = new PanelRegistrarEntrega("panelRegistrarEntrega", null);
        panelRegistrarEntrega.setVisible(panelRegistrarEntregaVizualizar = Boolean.FALSE);
        form.addOrReplace(panelRegistrarEntrega);
        target.add(form);
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see newDataViewLocaisEntrega
     * @return
     */
    private class ProviderLocaisEntrega extends SortableDataProvider<NotaRemessaOrdemFornecimentoContrato, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<NotaRemessaOrdemFornecimentoContrato> iterator(long first, long size) {
            return listaNotaRemessaLocaisEntrega.iterator();
        }

        @Override
        public long size() {
            if (listaNotaRemessaLocaisEntrega != null) {
                return listaNotaRemessaLocaisEntrega.size();
            } else {
                return 0;
            }
        }

        @Override
        public IModel<NotaRemessaOrdemFornecimentoContrato> model(NotaRemessaOrdemFornecimentoContrato object) {
            return new CompoundPropertyModel<NotaRemessaOrdemFornecimentoContrato>(object);
        }
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see newDataViewTermoEntrega
     * @return
     */
    private class ProviderTermoEntrega extends SortableDataProvider<NotaRemessaOrdemFornecimentoContrato, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<NotaRemessaOrdemFornecimentoContrato> iterator(long first, long size) {
            return listaNotaRemessaTermoEntrega.iterator();
        }

        @Override
        public long size() {
            if (listaNotaRemessaTermoEntrega != null) {
                return listaNotaRemessaTermoEntrega.size();
            } else {
                return 0;
            }
        }

        @Override
        public IModel<NotaRemessaOrdemFornecimentoContrato> model(NotaRemessaOrdemFornecimentoContrato object) {
            return new CompoundPropertyModel<NotaRemessaOrdemFornecimentoContrato>(object);
        }
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see initComponentes
     */
    private class FileUploadForm extends Form<List<FileUpload>> {
        private static final long serialVersionUID = 1L;

        public FileUploadForm(String id, IModel<List<FileUpload>> model, Item<NotaRemessaOrdemFornecimentoContrato> item) {
            super(id, model);
            setMultiPart(true);
            FileUploadField fileUploadField = new FileUploadField("fileInput", model);
            fileUploadField.add(new UploadValidator());
            add(fileUploadField);
            AjaxSubmitLink btnAdicionarAnexo = newButtonAdicionarAnexo(item);
            add(btnAdicionarAnexo);

            panelNumeroNotaECodigo = new PanelNumeroNotaECodigo("panelNumeroNotaECodigo", item);
            panelNumeroNotaECodigo.setVisible(exibirPanelCodigo);
            add(panelNumeroNotaECodigo);
        }

        /* Irá validar para não receber arquivos do tipo : .exe, .bat */
        private class UploadValidator implements IValidator<List<FileUpload>> {
            private static final long serialVersionUID = 1L;

            @Override
            public void validate(IValidatable<List<FileUpload>> validatable) {
                List<FileUpload> list = validatable.getValue();
                if (!list.isEmpty()) {
                    FileUpload fileUpload = list.get(0);
                    if (fileUpload.getSize() > Bytes.megabytes(Constants.LIMITE_MEGABYTES).bytes()) {
                        ValidationError error = new ValidationError("Arquivo para Download maior que " + Constants.LIMITE_MEGABYTES + "MB.");
                        validatable.error(error);
                    }
                    String extension = FilenameUtils.getExtension(fileUpload.getClientFileName());
                    if ("exe".equalsIgnoreCase(extension) || "bat".equalsIgnoreCase(extension)) {
                        ValidationError error = new ValidationError("Não são permitidos arquivos executáveis como .exe,.bat e etc.");
                        validatable.error(error);
                    }
                }
            }
        }
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @param item
     * @param item
     * @since 13/05/2016
     * @see FileUploadForm
     * @return
     */
    private AjaxSubmitLink newButtonAdicionarAnexo(Item<NotaRemessaOrdemFornecimentoContrato> item) {
        return new AjaxSubmitLink("btnAdicionarAnexo") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                adicionarAnexo(target, item);
            }
        };

    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @param item
     * @param item
     * @since 13/05/2016
     * @see newButtonAdicionarAnexo()
     * @return
     */
    private void adicionarAnexo(AjaxRequestTarget target, Item<NotaRemessaOrdemFornecimentoContrato> item) {
        if (!validarObrigatoriedadeAnexos(target, item)) {
            return;
        }
        try {
            if (!uploads.isEmpty()) {
                for (FileUpload component : uploads) {
                    AnexoNotaRemessa anexoNotaRemessa = new AnexoNotaRemessa();
                    anexoNotaRemessa.setNomeAnexo(component.getClientFileName());
                    anexoNotaRemessa.setConteudo(component.getBytes());
                    anexoNotaRemessa.setTipoArquivoTermoEntrega(this.tipoArquivoTermoEntrega);
                    this.listaAnexosNotaRemessa.add(anexoNotaRemessa);
                    this.tipoArquivoTermoEntrega = null;
                    

                    this.notaFiscal = new String();
                    this.codigoRecebimento = new String();
                    this.exibirPanelCodigo = Boolean.FALSE;
                }
                addMsgInfo("Arquivo adicionado com sucesso");
            }
            panelNumeroNotaECodigo.setVisible(exibirPanelCodigo);
            target.add(this.panelRegistrarEntrega, this.panelNumeroNotaECodigo);
        } catch (NullPointerException e) {
            addMsgInfo("Adicione um arquivo a ser anexado.");
        }

    }

    private boolean validarObrigatoriedadeAnexos(AjaxRequestTarget target, Item<NotaRemessaOrdemFornecimentoContrato> item) {
        boolean validar = Boolean.TRUE;
        LocalDate dataAtual = LocalDate.now();
        String msg = "";
        if (this.tipoArquivoTermoEntrega == null) {
            msg += "<p><li> Campo 'Tipo de Arquivo' é obrigatório.</li><p />";
            validar = Boolean.FALSE;
        }

        if (validar) {
            if (this.tipoArquivoTermoEntrega.getValor().equalsIgnoreCase("N")) {
                if (this.notaFiscal == null || this.notaFiscal.equalsIgnoreCase("")) {
                    msg += "<p><li> Campo 'Nº Nota Fiscal' é obrigatório.</li><p />";
                    validar = Boolean.FALSE;
                }
            } else if (this.tipoArquivoTermoEntrega.getValor().equalsIgnoreCase("T")) {
                if (this.codigoRecebimento == null || this.codigoRecebimento.equalsIgnoreCase("")) {
                    msg += "<p><li> Campo 'Código de Recebimento' é obrigatório.</li><p />";
                    validar = Boolean.FALSE;
                }
            }
        }

        if (this.dataEfetivaEntrega == null) {
            msg += "<p><li> Campo 'Data Efetiva da Entrega' é obrigatório.</li><p />";
            validar = Boolean.FALSE;
        }

        if (dataEfetivaEntrega != null && dataEfetivaEntrega.isAfter(dataAtual)) {
            msg += "<p><li> Campo 'Data Efetiva Entrega' não pode ser maior a data atual.</li><p />";
            validar = Boolean.FALSE;
        }

        /*
         * if(this.dataEfetivaEntrega != null &&
         * this.dataEfetivaEntrega.isBefore
         * (item.getModelObject().getDataPrevistaEntrega())){ msg +=
         * "<p><li> Campo 'Data Efetiva da Entrega' não pode ser inferior a 'Data Previsão Entrega'.</li><p />"
         * ; validar = Boolean.FALSE; }
         */

        if (uploads == null) {
            msg += "<p><li> Selecione um arquivo para anexar.</li><p />";
            validar = Boolean.FALSE;
        }

        /*
         * for (AnexoNotaRemessa tipoEnum : this.listaAnexosNotaRemessa) { if
         * (tipoEnum.getTipoArquivoTermoEntrega() ==
         * this.tipoArquivoTermoEntrega) { msg +=
         * "<p><li> O Tipo de Arquivo selecionado já foi anexado.</li><p />";
         * validar = Boolean.FALSE; break; } }
         */
        if (!validar) {
            mensagemAnexo.setObject(msg);
        } else {
            mensagemAnexo.setObject("");
        }
        target.add(labelMensagemAnexo);
        return validar;
    }

    private boolean validarListaAnexos(AjaxRequestTarget target) {
        boolean validar = Boolean.TRUE;
        String msg = "";

        if (this.listaAnexosNotaRemessa.size() == 0) {
            msg += "<p><li> É necessário anexar pelo menos um Termo de Entrega!</li><p />";
            validar = Boolean.FALSE;
        }

        if (!validar) {
            mensagemAnexo.setObject(msg);
        } else {
            mensagemAnexo.setObject("");
        }
        target.add(labelMensagemAnexo);
        return validar;
    }

    private void downloadAnexo(Item<AnexoNotaRemessa> item) {
        AnexoNotaRemessa a = item.getModelObject();
        if (a.getId() != null) {
            AnexoDto retorno = anexoNotaRemessaService.buscarPeloId(a.getId());
            SideUtil.download(retorno.getConteudo(), retorno.getNomeAnexo());
        } else {
            SideUtil.download(a.getConteudo(), a.getNomeAnexo());
        }
    }

    private void excluirAnexo(AjaxRequestTarget target, Item<AnexoNotaRemessa> item, Item<NotaRemessaOrdemFornecimentoContrato> itemNota) {
        this.listaAnexosNotaRemessa.remove(item.getModelObject());
        itemNota.getModelObject().setListaAnexosNotaRemessa(this.listaAnexosNotaRemessa);
        notaRemessaService.incluirAlterar(itemNota.getModelObject(), null);

        this.notaFiscal = new String();
        this.codigoRecebimento = new String();
        this.exibirPanelCodigo = Boolean.FALSE;

        target.add(this.panelRegistrarEntrega);
    }

    private String[] formatarLista(NotaRemessaOrdemFornecimentoContrato notaRemessaLocaisEntrega, boolean buscarMenbros) {
        String[] lista = new String[8];

        Programa programa = new Programa();
        programa = notaRemessaLocaisEntrega.getOrdemFornecimento().getContrato().getPrograma();
        StringBuilder listaItens = new StringBuilder(1);
        StringBuilder listaQuantidades = new StringBuilder(1);
        StringBuilder listaItemQuantidade = new StringBuilder(1);
        // Vamos pegar a UF, MUNICIPIO e ENDEREÇO.
        for (ItensNotaRemessaOrdemFornecimentoContrato INFOFC : notaRemessaLocaisEntrega.getListaItensNotaRemessaOrdemFornecimentoContratos()) {
            lista[0] = INFOFC.getItemOrdemFornecimentoContrato().getLocalEntrega().getMunicipio().getUf().getSiglaUf();
            lista[1] = INFOFC.getItemOrdemFornecimentoContrato().getLocalEntrega().getMunicipio().getNomeMunicipio();
            lista[2] = INFOFC.getItemOrdemFornecimentoContrato().getLocalEntrega().getEnderecoCompleto();
            lista[5] = INFOFC.getItemOrdemFornecimentoContrato().getLocalEntrega().getEntidade().getNomeEntidade();
            if (buscarMenbros) {
                Entidade entidade = new Entidade();
                entidade = INFOFC.getItemOrdemFornecimentoContrato().getLocalEntrega().getEntidade();
                listaInscricaoPrograma = inscricaoProgramaService.buscarInscricaoProgramaPeloProgramaEEntidade(programa, entidade);
            }
            break;
        }

        int count = 0;
        // Vamos montar os itens e quantidades
        listaItemQuantidade.append("<table ><tr><td><b>Item</b></td><td><b>Quantidade</b></td></tr><tr><td colspan='2'><hr></hr></td></tr>");
        for (ItensNotaRemessaOrdemFornecimentoContrato INFOFC : notaRemessaLocaisEntrega.getListaItensNotaRemessaOrdemFornecimentoContratos()) {
            if (count > 0) {
                listaItens.append("/ ");
                listaQuantidades.append("/ ");
                //listaItemQuantidade.append("<tr><td>" + INFOFC.getItemOrdemFornecimentoContrato().getItem().getNomeBem() + "</td><td><center>" + INFOFC.getItemOrdemFornecimentoContrato().getQuantidade().toString() + "</center></td></tr>");
            }
            listaItens.append(INFOFC.getItemOrdemFornecimentoContrato().getItem().getNomeBem());
            listaQuantidades.append(INFOFC.getItemOrdemFornecimentoContrato().getQuantidade().toString());
            listaItemQuantidade.append("<tr><td>" + INFOFC.getItemOrdemFornecimentoContrato().getItem().getNomeBem() + "</td><td><center>" + INFOFC.getItemOrdemFornecimentoContrato().getQuantidade().toString() + "</center></td></tr>");
            count++;
        }
        listaItemQuantidade.append("</table>");

        lista[3] = listaItens.toString();
        lista[4] = listaQuantidades.toString();
        lista[7] = listaItemQuantidade.toString();

        return lista;
    }

    /**
     * Sprint 10
     * 
     * @author joao.coutinho
     * @since 10/05/2016
     * @see PanelRegistrarEntrega
     * @return
     */
    public String dataCadastroBR(LocalDate dataCadastro) {
        DateTimeFormatter sdfPadraoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (dataCadastro != null) {
            return sdfPadraoBR.format(dataCadastro);
        }
        return " - ";
    }

    // GET'S AND SET'S
    public Integer getLocaisEntregaPorPagina() {
        return locaisEntregaPorPagina;
    }

    public void setLocaisEntregaPorPagina(Integer locaisEntregaPorPagina) {
        this.locaisEntregaPorPagina = locaisEntregaPorPagina;
    }

    public Integer getTermoEntregaPorPagina() {
        return termoEntregaPorPagina;
    }

    public void setTermoEntregaPorPagina(Integer termoEntregaPorPagina) {
        this.termoEntregaPorPagina = termoEntregaPorPagina;
    }

    public List<FileUpload> getUploads() {
        return uploads;
    }

    public void setUploads(List<FileUpload> uploads) {
        this.uploads = uploads;
    }
}
