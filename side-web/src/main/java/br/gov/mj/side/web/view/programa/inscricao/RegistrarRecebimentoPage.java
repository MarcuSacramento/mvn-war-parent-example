package br.gov.mj.side.web.view.programa.inscricao;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumBotaoClicadoNotaRemessa;
import br.gov.mj.side.entidades.enums.EnumSituacaoPreenchimentoItemFormatacaoBeneficiario;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumTipoArquivoTermoEntrega;
import br.gov.mj.side.entidades.enums.EnumTipoMinuta;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.inscricao.ComissaoRecebimento;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.licitacao.LicitacaoPrograma;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.AnexoNotaRemessa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.ItensNotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.web.dto.AnexoDto;
import br.gov.mj.side.web.dto.ObjetoFornecimentoContratoDto;
import br.gov.mj.side.web.dto.PesquisaLicitacaoDto;
import br.gov.mj.side.web.dto.RelatorioRecebimentoDto;
import br.gov.mj.side.web.dto.TermoEntregaitensDto;
import br.gov.mj.side.web.service.AnexoNotaRemessaService;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.LicitacaoProgramaService;
import br.gov.mj.side.web.service.NotaRemessaService;
import br.gov.mj.side.web.service.ObjetoFornecimentoContratoService;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.MascaraUtils;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.util.SortableRecebimentoDataProvider;
import br.gov.mj.side.web.view.cadastraritem.ConferenciaCadastrarItemPage;
import br.gov.mj.side.web.view.components.EntityDataProvider;
import br.gov.mj.side.web.view.dashboard.DashboardInscricoesPage;
import br.gov.mj.side.web.view.planejarLicitacao.minutaTR.GerarMinutaTR;
import br.gov.mj.side.web.view.template.TemplatePage;

@AuthorizeInstantiation({ RegistrarRecebimentoPage.ROLE_MANTER_REGISTRAR_RECEBIMENTO_VISUZALIZAR, RegistrarRecebimentoPage.ROLE_MANTER_REGISTRAR_RECEBIMENTO_BAIXAR, RegistrarRecebimentoPage.ROLE_MANTER_REGISTRAR_RECEBIMENTO_EXCLUIR, RegistrarRecebimentoPage.ROLE_MANTER_REGISTRAR_RECEBIMENTO_INCLUIR })
public class RegistrarRecebimentoPage extends TemplatePage {

    private static final long serialVersionUID = 5865484103351089292L;

    // #######################################_VARIAVEIS_############################################
    Form<RegistrarRecebimentoPage> form;
    private InscricaoPrograma inscricao;
    private List<FileUpload> uploads = new ArrayList<FileUpload>();
    private List<ObjetoFornecimentoContrato> listaOrdemFornecTemp = new ArrayList<ObjetoFornecimentoContrato>();
    private List<RelatorioRecebimentoDto> listaRelatorioRecebimentoDto = new ArrayList<RelatorioRecebimentoDto>();
    private List<AnexoNotaRemessa> listaAnexosNotaRemessa = new ArrayList<AnexoNotaRemessa>();
    private List<InscricaoPrograma> listaInscricaoPrograma = new ArrayList<InscricaoPrograma>();
    private Entidade entidadeEscolhida = new Entidade();
    private Programa programa;
    private NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato;

    // #######################################_CONSTANTE_############################################
    public static final String ROLE_MANTER_REGISTRAR_RECEBIMENTO_VISUZALIZAR = "manter_registrar_recebimento:visualizar";
    public static final String ROLE_MANTER_REGISTRAR_RECEBIMENTO_BAIXAR = "manter_registrar_recebimento:baixar";
    public static final String ROLE_MANTER_REGISTRAR_RECEBIMENTO_EXCLUIR = "manter_registrar_recebimento:excluir";
    public static final String ROLE_MANTER_REGISTRAR_RECEBIMENTO_INCLUIR = "manter_registrar_recebimento:incluir";
    private Integer itensPorPaginaRecebimentos = Constants.ITEMS_PER_PAGE_PAGINATION;
    private static final String ONCHANGE = "onchange";
    private static final boolean VERDADEIRO = true;
    private static final boolean FALSO = false;
    private boolean painelItens = false;
    private boolean painelRelatorio = false;
    private boolean painelAnexo = false;
    private boolean botaoEnviarTR = true;
    private boolean botaoEditarTR = false;

    // #######################################_PAINEIS_############################################
    private PanelRecebimentos panelRecebimentos;
    private PanelItens panelItens;
    private PanelAnexos panelAnexos;
    private PanelRelatorio panelRelatorio;

    // #######################################_ELEMENTOS_WICKET_############################################
    private RadioGroup<NotaRemessaOrdemFornecimentoContrato> radioRecebimentoGroup;
    private DataView<NotaRemessaOrdemFornecimentoContrato> newListaRecebimentos;
    private DataView<ObjetoFornecimentoContrato> newListaItens;
    private DataView<AnexoNotaRemessa> newListaTRAnexo;
    private InfraAjaxFallbackLink btnEnviarTR;
    private InfraAjaxFallbackLink btnEnviarTREditar;
    private InfraAjaxFallbackLink btnCadItem;
    private InfraAjaxFallbackLink btnAnexar;
    private Label labelMensagemAnexo;
    private Model<String> mensagem = Model.of("");

    // #####################################_INJEÇÃO_DE_DEPENDENCIA_##############################################
    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private LicitacaoProgramaService licitacaoProgramaService;
    @Inject
    private NotaRemessaService notaRemessaService;
    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;
    @Inject
    private AnexoNotaRemessaService anexoNotaRemessaService;
    @Inject
    private InscricaoProgramaService inscricaoProgramaService;
    @Inject
    private ProgramaService programaService;
    @Inject
    private ObjetoFornecimentoContratoService objetoFornecimentoContratoService;

    // #####################################_CONSTRUTOR_##############################################
    public RegistrarRecebimentoPage(PageParameters pageParameters) {
        super(pageParameters);

        String id = getPageParameters().get("idInscricao").toString();
        this.inscricao = inscricaoProgramaService.buscarPeloId(Long.parseLong(id));

        String idNotaRemessa = getPageParameters().get("idNotaRemessa").toString();
        notaRemessaOrdemFornecimentoContrato = notaRemessaService.buscarPeloId(Long.parseLong(idNotaRemessa));

        listaOrdemFornecTemp = new ArrayList<ObjetoFornecimentoContrato>();

        ObjetoFornecimentoContratoDto dto = new ObjetoFornecimentoContratoDto();
        ObjetoFornecimentoContrato ofc = new ObjetoFornecimentoContrato();
        ofc.setNotaRemessaOrdemFornecimentoContrato(notaRemessaOrdemFornecimentoContrato);
        dto.setObjetoFornecimentoContrato(ofc);

        listaOrdemFornecTemp = objetoFornecimentoContratoService.buscarSemPaginacao(dto);

        painelItens = true;
        painelRelatorio = true;
        initVariaveis();
        initComponentes();
        panelRelatorio.addOrReplace(newButtonGerarRR().setVisible(!verificarSituacaoItem()));
        panelRelatorio.addOrReplace(newButtonDownloadRR().setVisible(verificarSituacaoItem()));
        setTitulo(getTituloNomePrograma());
    }

    public RegistrarRecebimentoPage(PageParameters pageParameters, InscricaoPrograma inscricao) {
        super(pageParameters);
        this.inscricao = inscricao;

        initVariaveis();
        initComponentes();

        setTitulo(getTituloNomePrograma());

    }

    private void initVariaveis() {
        programa = inscricao.getPrograma();
        entidadeEscolhida = (Entidade) getSessionAttribute("entidade");
    }

    private void initComponentes() {
        form = componentFactory.newForm("form", new CompoundPropertyModel<RegistrarRecebimentoPage>(this));
        form.add(new InscricaoNavPanel("navPanel", inscricao, null, this));
        form.add(panelRecebimentos = newPanelRecebimentos());
        form.add(panelItens = newPanelItens());
        form.add(panelAnexos = newPanelAnexos());
        form.add(panelRelatorio = newPanelRelatorio());
        form.add(new PanelButton());

        add(form);
    }

    // ####################################_PAINEIS_##############################################
    // PAINEL RECEBIMENTOS
    public PanelRecebimentos newPanelRecebimentos() {
        panelRecebimentos = new PanelRecebimentos();
        setOutputMarkupId(Boolean.TRUE);
        return panelRecebimentos;
    }

    private class PanelRecebimentos extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelRecebimentos() {
            super("panelRecebimentos");

            SortableRecebimentoDataProvider dp1 = new SortableRecebimentoDataProvider(notaRemessaService, programa, entidadeEscolhida);

            radioRecebimentoGroup = new RadioGroup<NotaRemessaOrdemFornecimentoContrato>("radioGroupRecebimento", new Model<NotaRemessaOrdemFornecimentoContrato>());
            radioRecebimentoGroup.add(newListaRecebimentos = newDataViewRecebimentos(dp1));
            add(radioRecebimentoGroup);
            add(newDropItensPorPaginaTermosDoacaoGerados());

            add(new OrderByBorder<String>("orderByNF", "notaRemessaOrdemFornecimento.numeroNotaRemessa", dp1));
            add(new OrderByBorder<String>("orderByDataPrevistaEntrega", "notaRemessaOrdemFornecimento.dataPrevistaEntrega", dp1));
            add(new InfraAjaxPagingNavigator("paginatorR", newListaRecebimentos));
        }
    }

    // PAINEL ITENS
    public PanelItens newPanelItens() {
        panelItens = new PanelItens();
        setOutputMarkupId(Boolean.TRUE);
        panelItens.setVisible(painelItens);
        return panelItens;
    }

    private class PanelItens extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelItens() {
            super("panelItens");

            add(newListaItens = newDataViewItens());
            add(new InfraAjaxPagingNavigator("paginatorI", newListaItens));
        }
    }

    // PAINEL RELATORIO
    public PanelRelatorio newPanelRelatorio() {
        panelRelatorio = new PanelRelatorio();
        setOutputMarkupId(Boolean.TRUE);
        panelRelatorio.setVisible(painelRelatorio);
        return panelRelatorio;
    }

    private class PanelRelatorio extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelRelatorio() {
            super("panelRelatorio");

            add(newButtonGerarRR());
            add(newButtonDownloadRR());
        }
    }

    // PAINEL ANEXO
    public PanelAnexos newPanelAnexos() {
        panelAnexos = new PanelAnexos(null);
        setOutputMarkupId(Boolean.TRUE);
        panelAnexos.setVisible(painelAnexo);
        return panelAnexos;
    }

    private class PanelAnexos extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelAnexos(Item<NotaRemessaOrdemFornecimentoContrato> item) {
            super("panelAnexos");
            FileUploadForm fileUploadForm = newFormAnexo();
            add(fileUploadForm);

            labelMensagemAnexo = new Label("mensagemAnexo", mensagem);
            labelMensagemAnexo.setEscapeModelStrings(false);
            add(labelMensagemAnexo);

            add(newDataViewAnexo(item));
            add(new InfraAjaxPagingNavigator("paginatorAnexo", newListaTRAnexo));
            add(new PanelAnexoButton("panelAnexoButton", item));
        }
    }

    // PAINEL ANEXOBUTTON
    private class PanelAnexoButton extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("rawtypes")
        public PanelAnexoButton(String id, Item<NotaRemessaOrdemFornecimentoContrato> item) {
            super(id);
            btnAnexar = componentFactory.newAjaxFallbackLink("btnAnexar", (target) -> btnAnexar(target, item));
            authorize(btnAnexar, RENDER, ROLE_MANTER_REGISTRAR_RECEBIMENTO_INCLUIR);
            add(btnAnexar);
            InfraAjaxFallbackLink btnCancelarAnexo = componentFactory.newAjaxFallbackLink("btnCancelarAnexo", (target) -> cancelarAnexo(target));
            add(btnCancelarAnexo);
        }

    }

    // PAINEL BUTTON
    private class PanelButton extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelButton() {
            super("panelButton");
            setOutputMarkupId(Boolean.TRUE);
            add(newButtonVoltar());
        }
    }

    // ####################################_COMPONENTE_WICKET_##############################################
    // DataView Recebimentos
    public DataView<NotaRemessaOrdemFornecimentoContrato> newDataViewRecebimentos(SortableRecebimentoDataProvider dp1) {
        newListaRecebimentos = new DataView<NotaRemessaOrdemFornecimentoContrato>("listaRecebimento", dp1) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<NotaRemessaOrdemFornecimentoContrato> item) {
                item.getModelObject().setListaItensNotaRemessaOrdemFornecimentoContratos(notaRemessaService.buscarItensNotaRemessa(item.getModelObject()));
                List<AnexoDto> listaanexos = anexoNotaRemessaService.buscarPeloIdNotaRemessa(item.getModelObject().getId());

                Radio<NotaRemessaOrdemFornecimentoContrato> radioRecebimento = new Radio<NotaRemessaOrdemFornecimentoContrato>("radioRecebimento", Model.of(item.getModelObject()));
                radioRecebimento.add(new AjaxEventBehavior(ONCHANGE) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        exibirItens(item, target);
                    }
                });
                if (notaRemessaOrdemFornecimentoContrato != null && notaRemessaOrdemFornecimentoContrato.getId() != null && notaRemessaOrdemFornecimentoContrato.getId().intValue() == item.getModelObject().getId().intValue()) {
                    radioRecebimentoGroup.setConvertedInput(notaRemessaOrdemFornecimentoContrato);
                    radioRecebimentoGroup.setDefaultModelObject(notaRemessaOrdemFornecimentoContrato);
                }
                item.add(radioRecebimento);

                String[] listaRecebimentos1 = formatarLista(item.getModelObject(), false);
                item.add(new Label("numeroNotaRemessa"));

                String iconeInformativo1 = "<a type=\"button\" data-toggle=\"popover\" data-content=\"" + listaRecebimentos1[5] + "\" data-trigger=\"hover\"><i style=\"color:green;\" class=\"fa fa-info-circle\"> </i></a>";
                Label lblDescricao1 = new Label("itensQuant", iconeInformativo1);
                lblDescricao1.setEscapeModelStrings(false);
                lblDescricao1.setOutputMarkupId(true);
                item.add(lblDescricao1);

                item.add(new Label("municipio", listaRecebimentos1[2]));

                String iconeInformativo = "<a type=\"button\" data-toggle=\"popover\" title=\"Endereço de Entrega\" data-content=\"" + listaRecebimentos1[3] + "\" data-trigger=\"hover\"><i style=\"color:green;\" class=\"fa fa-info-circle\"> </i></a>";
                Label lblDescricao = new Label("lblDescricaoEnderecoEntrega", iconeInformativo);
                lblDescricao.setEscapeModelStrings(false);
                lblDescricao.setOutputMarkupId(true);
                item.add(lblDescricao);

                item.add(new Label("dataPrevistaEntrega"));
                item.add(new Label("dataEfetivaEntrega"));
                item.add(new Label("situacaoNF", item.getModelObject().getStatusExecucaoBeneficiario().getDescricao()));

                for (AnexoDto anexo : listaanexos) {
                    if (anexo.getTipoArquivoTermoEntrega() == EnumTipoArquivoTermoEntrega.RELATORIO_RECEBIMENTO_ASSINADO) {
                        AttributeAppender style = new AttributeAppender("style", "background:#defaea;", " ");
                        botaoEditarTR = true;
                        botaoEnviarTR = false;
                        item.add(style);
                        break;
                    } else {
                        botaoEnviarTR = true;
                        botaoEditarTR = false;
                    }
                }

                Button btnDownload = componentFactory.newButton("btnBaixarTR", () -> download(item));
                item.add(btnDownload);

                btnEnviarTR = componentFactory.newAjaxFallbackLink("btnEnviarTR", (target) -> enviarTR(target, item));
                btnEnviarTR.setVisible(botaoEnviarTR);
                item.add(btnEnviarTR);

                btnEnviarTREditar = componentFactory.newAjaxFallbackLink("btnEnviarTREditar", (target) -> enviarTR(target, item));
                btnEnviarTREditar.setVisible(botaoEditarTR);
                item.add(btnEnviarTREditar);
            }
        };
        newListaRecebimentos.setItemsPerPage(30L);
        return newListaRecebimentos;
    }

    // DataView Itens
    public DataView<ObjetoFornecimentoContrato> newDataViewItens() {
        newListaItens = new DataView<ObjetoFornecimentoContrato>("listaItens", new EntityDataProvider<ObjetoFornecimentoContrato>(listaOrdemFornecTemp)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<ObjetoFornecimentoContrato> item) {
                item.add(newLabelNomeBem(item.getModelObject()));
                item.add(newLabelIdentificador(item.getModelObject()));
                item.add(new Label("formaVerificacao", item.getModelObject().getFormaVerificacao().getDescricao()));
                item.add(new Label("situacao", item.getModelObject().getSituacaoPreenchimentoBeneficiario().getDescricao()));

                Integer obrigatorioPreenchido = item.getModelObject().getQuantidadeQuesitosObrigatoriosPreenchidosBeneficiario();
                Integer opcionalPreenchido = item.getModelObject().getQuantidadeQuesitosOpcionaisPreenchidosBeneficiario();

                Integer obrigatorioTotal = item.getModelObject().getQuantidadeQuesitosObrigatoriosBeneficiario();
                Integer opcionalTotal = item.getModelObject().getQuantidadeQuesitosOpcionaisBeneficiario();
                Integer total = item.getModelObject().getQuantidadeQuesitosBeneficiario();

                item.add(new Label("quesitosOb", (obrigatorioPreenchido == null ? 0 : obrigatorioPreenchido) + " / " + (obrigatorioTotal == null ? 0 : obrigatorioTotal)));
                item.add(new Label("quesitosOp", (opcionalPreenchido == null ? 0 : opcionalPreenchido) + " / " + (opcionalTotal == null ? 0 : opcionalTotal)));
                item.add(new Label("quesitos", (total == null ? 0 : total)));

                if (item.getModelObject().getSituacaoPreenchimentoBeneficiario() == EnumSituacaoPreenchimentoItemFormatacaoBeneficiario.PREENCHIDO && (item.getModelObject().getObjetoDevolvido() == null || item.getModelObject().getObjetoDevolvido().equals(Boolean.FALSE))) {
                    AttributeAppender style = new AttributeAppender("style", "background:#defaea;", " ");
                    item.add(style);
                } else if (item.getModelObject().getObjetoDevolvido() != null && item.getModelObject().getObjetoDevolvido().equals(Boolean.TRUE)) {
                    AttributeAppender style = new AttributeAppender("style", "background:#ff9999;", " ");
                    item.add(style);
                }

                btnCadItem = componentFactory.newAjaxFallbackLink("btnCadItem", (target) -> cadastrarItem(target, item));
                item.add(btnCadItem);
            }
        };
        newListaItens.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return newListaItens;
    }

    // DataView Anexos
    private DataView<AnexoNotaRemessa> newDataViewAnexo(Item<NotaRemessaOrdemFornecimentoContrato> itemNota) {
        newListaTRAnexo = new DataView<AnexoNotaRemessa>("anexos", new EntityDataProvider<AnexoNotaRemessa>(this.listaAnexosNotaRemessa)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<AnexoNotaRemessa> item) {
                item.add(new Label("nomeAnexo"));
                item.add(new Label("tipoArquivoTermoEntrega", item.getModelObject().getTipoArquivoTermoEntrega().getDescricao()));

                Button btnDownload = componentFactory.newButton("btnDonwload", () -> downloadAnexo(item));
                item.add(btnDownload);
                InfraAjaxConfirmButton btnExcluir = componentFactory.newAJaxConfirmButton("btnExcluir", "MSG002", form, (target, form) -> excluirAnexo(target, item, itemNota));
                authorize(btnExcluir, RENDER, ROLE_MANTER_REGISTRAR_RECEBIMENTO_EXCLUIR);
                item.add(btnExcluir);
            }
        };
        newListaTRAnexo.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return newListaTRAnexo;
    }

    // ####################################_COMPONENTES_WICKET_###############################################
    private DropDownChoice<Integer> newDropItensPorPaginaTermosDoacaoGerados() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPaginaRecebimentos", new LambdaModel<Integer>(this::getItensPorPaginaRecebimentos, this::setItensPorPaginaRecebimetentos), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                newListaRecebimentos.setItemsPerPage(getItensPorPaginaRecebimentos());
                target.add(panelRecebimentos);
            };
        });
        return dropDownChoice;
    }

    private String[] formatarLista(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato, boolean buscarMenbros) {
        String[] lista = new String[6];
        List<ItensNotaRemessaOrdemFornecimentoContrato> listaItens = notaRemessaOrdemFornecimentoContrato.getListaItensNotaRemessaOrdemFornecimentoContratos();

        StringBuilder listadeItens = new StringBuilder(1);
        StringBuilder listaQuantidades = new StringBuilder(1);
        StringBuilder listaItemQuantidade = new StringBuilder(1);
        // Vamos pegar a UF, MUNICIPIO e ENDEREÇO.
        for (ItensNotaRemessaOrdemFornecimentoContrato INFOFC : listaItens) {
            lista[2] = INFOFC.getItemOrdemFornecimentoContrato().getLocalEntrega().getMunicipio().getNomeMunicipio();
            lista[3] = INFOFC.getItemOrdemFornecimentoContrato().getLocalEntrega().getEnderecoCompleto();
            lista[4] = INFOFC.getItemOrdemFornecimentoContrato().getLocalEntrega().getEntidade().getNomeEntidade();
            if (buscarMenbros) {
                listaInscricaoPrograma = inscricaoProgramaService.buscarInscricaoProgramaPeloProgramaEEntidade(programa, entidadeEscolhida);
            }
            break;
        }

        int count = 0;
        // Vamos montar os itens e quantidades
        listaItemQuantidade.append("<table ><tr><td><b>Item</b></td><td><b>Quantidade</b></td></tr><tr><td colspan='2'><hr></hr></td></tr>");
        for (ItensNotaRemessaOrdemFornecimentoContrato INFOFC : listaItens) {
            if (count > 0) {
                listadeItens.append("/ ");
                listaQuantidades.append("/ ");
                listaItemQuantidade.append("<tr><td>" + INFOFC.getItemOrdemFornecimentoContrato().getItem().getNomeBem() + "</td><td><center>" + INFOFC.getItemOrdemFornecimentoContrato().getQuantidade().toString() + "</center></td></tr>");

            }
            listadeItens.append(INFOFC.getItemOrdemFornecimentoContrato().getItem().getNomeBem());
            listaQuantidades.append(INFOFC.getItemOrdemFornecimentoContrato().getQuantidade().toString());
            listaItemQuantidade.append("<tr><td>" + INFOFC.getItemOrdemFornecimentoContrato().getItem().getNomeBem() + "</td><td><center>" + INFOFC.getItemOrdemFornecimentoContrato().getQuantidade().toString() + "</center></td></tr>");
            count++;
        }
        listaItemQuantidade.append("</table>");

        lista[0] = listadeItens.toString();
        lista[1] = listaQuantidades.toString();
        lista[5] = listaItemQuantidade.toString();

        return lista;
    }

    private Label newLabelNomeBem(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        String textoNomeBem = objetoFornecimentoContrato.getItem().getNomeBem();

        if (verificaItensDevolvidos(objetoFornecimentoContrato)) {
            textoNomeBem = textoNomeBem + "<p style=\"color: red;\">Item Devolvido.</p>";
        }

        Label lbl = new Label("item", textoNomeBem);
        lbl.setEscapeModelStrings(Boolean.FALSE);
        return lbl;
    }

    private boolean verificaItensDevolvidos(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        return objetoFornecimentoContrato.getObjetoDevolvido() != null && objetoFornecimentoContrato.getObjetoDevolvido().equals(Boolean.TRUE);
    }

    private Label newLabelIdentificador(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        Long textoNomeBem = objetoFornecimentoContrato.getObjetoFornecimentoContratoPai() == null ? objetoFornecimentoContrato.getId() : objetoFornecimentoContrato.getObjetoFornecimentoContratoPai();

        Label lbl = new Label("identificador", textoNomeBem);
        return lbl;
    }

    private class FileUploadForm extends Form<List<FileUpload>> {
        private static final long serialVersionUID = 1L;

        public FileUploadForm(String id, IModel<List<FileUpload>> model) {
            super(id, model);
            setMultiPart(true);
            FileUploadField fileUploadField = new FileUploadField("fileInput", model);
            fileUploadField.add(new UploadValidator());
            add(fileUploadField);
            AjaxSubmitLink btnAdicionarAnexo = newButtonAdicionarAnexo();
            add(btnAdicionarAnexo);
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

    private FileUploadForm newFormAnexo() {
        return new FileUploadForm("anexoForm", new LambdaModel<List<FileUpload>>(this::getUploads, this::setUploads));
    }

    public List<FileUpload> getUploads() {
        return uploads;
    }

    public void setUploads(List<FileUpload> uploads) {
        this.uploads = uploads;
    }

    private AjaxSubmitLink newButtonAdicionarAnexo() {
        return new AjaxSubmitLink("btnAdicionarAnexo") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                adicionarAnexo(target);
            }
        };
    }

    public String dataCadastroBR(LocalDateTime dataCadastro) {
        DateTimeFormatter sdfPadraoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        if (dataCadastro != null) {
            return sdfPadraoBR.format(dataCadastro);
        }
        return " - ";
    }

    private void gerarRR(AjaxRequestTarget target1) {
        panelRelatorio.addOrReplace(newButtonDownloadRR().setVisible(Boolean.TRUE));
        target1.add(panelRelatorio);
    }

    private Boolean verificarSituacaoItem() {
        Boolean resposta = true;
        for (ObjetoFornecimentoContrato INFOFC : listaOrdemFornecTemp) {
            TermoEntregaitensDto termoEntregaitensDto = new TermoEntregaitensDto();
            termoEntregaitensDto.setSituacao(INFOFC.getSituacaoPreenchimentoBeneficiario().getDescricao());
            if (!termoEntregaitensDto.getSituacao().equals(EnumSituacaoPreenchimentoItemFormatacaoBeneficiario.PREENCHIDO.getDescricao())) {
                resposta = false;
                break;
            }
        }
        return resposta;
    }

    private void downloadRR() {
        RelatorioRecebimentoOfBuilder jasper = new RelatorioRecebimentoOfBuilder(SideUtil.getCaminhoCompletoRelatorios(getHttpServletRequest().getServletContext()));
        jasper.setNomeRR("Relatório de Recebimento");
        jasper.setTipoRR(EnumTipoMinuta.PDF);
        jasper.setDataList(popularLista(notaRemessaOrdemFornecimentoContrato));
        ByteArrayOutputStream exportar = jasper.exportToByteArray();
        SideUtil.download(exportar.toByteArray(), "Relatório de Recebimento" + ".pdf");
    }

    private List<RelatorioRecebimentoDto> popularLista(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato) {
        listaRelatorioRecebimentoDto = new ArrayList<RelatorioRecebimentoDto>();
        RelatorioRecebimentoDto relatorioRecebimentoDto = new RelatorioRecebimentoDto();

        relatorioRecebimentoDto.setCodigoPrograma(programa.getCodigoIdentificadorProgramaPublicadoENomePrograma());
        relatorioRecebimentoDto.setUnidadeExecutoraPrograma(programa.getUnidadeExecutora().getNomeUnidadeExecutora());
        relatorioRecebimentoDto.setNumeroOF(notaRemessaOrdemFornecimentoContrato.getOrdemFornecimento().getContrato().getNumeroProcessoSEI());
        relatorioRecebimentoDto.setNumeroNF(notaRemessaOrdemFornecimentoContrato.getNumeroNotaRemessa());

        String[] lista = formatarLista(notaRemessaOrdemFornecimentoContrato, true);
        relatorioRecebimentoDto.setNomeBeneficiario(lista[4]);
        relatorioRecebimentoDto.setEnderecoBeneficiario(lista[3]);

        for (InscricaoPrograma inscricaoPrograma : listaInscricaoPrograma) {
            relatorioRecebimentoDto.setNomeRepresentante(inscricaoPrograma.getPessoaEntidade().getPessoa().getNomePessoa());
            relatorioRecebimentoDto.setTelefoneRepresentante(MascaraUtils.formatarMascaraTelefone(inscricaoPrograma.getPessoaEntidade().getPessoa().getNumeroTelefone()));
            relatorioRecebimentoDto.setEmailRepresentante(inscricaoPrograma.getPessoaEntidade().getPessoa().getEmail());
        }

        /*
         * TermoDefinitivoItensDto termoDefinitivoItensDto = new
         * TermoDefinitivoItensDto();
         * termoDefinitivoItensDto.setIdItem(String.valueOf(obj.getId()));
         * termoDefinitivoItensDto.setNomeBem(obj.getItem().getNomeBem());
         */
        for (ObjetoFornecimentoContrato INFOFC : listaOrdemFornecTemp) {
            TermoEntregaitensDto termoEntregaitensDto = new TermoEntregaitensDto();
            termoEntregaitensDto.setIdItem(String.valueOf(INFOFC.getId()));
            termoEntregaitensDto.setNomeBem(INFOFC.getItem().getNomeBem());

            if (INFOFC.getEstadoDeNovo() != null && INFOFC.getEstadoDeNovo()) {
                termoEntregaitensDto.setEstadoDeNovo("Sim");
            } else if (INFOFC.getEstadoDeNovo() != null && !INFOFC.getEstadoDeNovo()) {
                termoEntregaitensDto.setEstadoDeNovo("Não");
            } else {
                termoEntregaitensDto.setEstadoDeNovo("-");
            }

            if (INFOFC.getFuncionandoDeAcordo() != null && INFOFC.getFuncionandoDeAcordo()) {
                termoEntregaitensDto.setFuncionandoDeAcordo("Sim");
                termoEntregaitensDto.setDescricaoNaoFuncionandoDeAcordo("-");
            } else if (INFOFC.getFuncionandoDeAcordo() != null && !INFOFC.getFuncionandoDeAcordo()) {
                termoEntregaitensDto.setFuncionandoDeAcordo("Não");
                termoEntregaitensDto.setDescricaoNaoFuncionandoDeAcordo(INFOFC.getDescricaoNaoFuncionandoDeAcordo());
            } else {
                termoEntregaitensDto.setFuncionandoDeAcordo("-");
                termoEntregaitensDto.setDescricaoNaoFuncionandoDeAcordo("-");
            }

            if (INFOFC.getConfiguradoDeAcordo() != null && INFOFC.getConfiguradoDeAcordo()) {
                termoEntregaitensDto.setConfiguradoDeAcordo("Sim");
                termoEntregaitensDto.setDescricaoNaoConfiguradoDeAcordo("-");
            } else if (INFOFC.getConfiguradoDeAcordo() != null && !INFOFC.getConfiguradoDeAcordo()) {
                termoEntregaitensDto.setConfiguradoDeAcordo("Não");
                termoEntregaitensDto.setDescricaoNaoConfiguradoDeAcordo(INFOFC.getDescricaoNaoConfiguradoDeAcordo());
            } else {
                termoEntregaitensDto.setConfiguradoDeAcordo("-");
                termoEntregaitensDto.setDescricaoNaoConfiguradoDeAcordo("-");
            }

            relatorioRecebimentoDto.getListaItens().add(termoEntregaitensDto);
        }

        for (InscricaoPrograma inscricaoPrograma : listaInscricaoPrograma) {
            for (ComissaoRecebimento comissaoRecebimento : inscricaoProgramaService.buscarComissaoRecebimento(inscricaoPrograma)) {
                if (comissaoRecebimento.getMembroComissao().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
                    TermoEntregaitensDto termoEntregaitensDto = new TermoEntregaitensDto();
                    termoEntregaitensDto.setNomeMembros(comissaoRecebimento.getMembroComissao().getNomePessoa().toUpperCase());
                    relatorioRecebimentoDto.getListaMembros().add(termoEntregaitensDto);
                }
            }
        }

        listaRelatorioRecebimentoDto.add(relatorioRecebimentoDto);

        return listaRelatorioRecebimentoDto;
    }

    private InfraAjaxConfirmButton newButtonGerarRR() {
        InfraAjaxConfirmButton btnGerarRR = componentFactory.newAJaxConfirmButton("btnGerarRR", "MSG013", form, (target, formz) -> gerarRR(target));
        return btnGerarRR;
    }

    private Button newButtonDownloadRR() {
        Button btndownloadRR = componentFactory.newButton("btndownloadRR", () -> downloadRR());
        authorize(btndownloadRR, RENDER, ROLE_MANTER_REGISTRAR_RECEBIMENTO_BAIXAR);
        return btndownloadRR;
    }

    private Button newButtonVoltar() {
        return componentFactory.newButton("btnVoltar", () -> setResponsePage(DashboardInscricoesPage.class));
    }

    // ####################################_AÇÕES_###############################################

    // metodo para retornar o titulo da pagina(nome do programa).
    private String getTituloNomePrograma() {
        String titulo = new String();
        if (this.programa == null) {
            titulo = "";
        } else {
            titulo = "Programa: ".concat(this.programa.getNomePrograma());
        }

        return titulo;
    }

    private void download(Item<NotaRemessaOrdemFornecimentoContrato> item) {

        PesquisaLicitacaoDto licitacaoDto = new PesquisaLicitacaoDto();
        licitacaoDto.setPrograma(item.getModelObject().getOrdemFornecimento().getContrato().getPrograma());

        List<LicitacaoPrograma> listaLicitacao = licitacaoProgramaService.buscarSemPaginacao(licitacaoDto);
        if (listaLicitacao != null || !listaLicitacao.isEmpty()) {

            GerarMinutaTR gerarMinuta = new GerarMinutaTR(SideUtil.getCaminhoCompletoRelatorios(getHttpServletRequest().getServletContext()));
            gerarMinuta.setTipoMinuta(EnumTipoMinuta.PDF);
            ByteArrayOutputStream export = gerarMinuta.exportToByteArray(listaLicitacao.get(0), programaService, licitacaoProgramaService, inscricaoProgramaService);

            SideUtil.download(export.toByteArray(), "TR_" + listaLicitacao.get(0).getPrograma().getNomePrograma() + ".pdf");

        }

    }

    private void exibirItens(Item<NotaRemessaOrdemFornecimentoContrato> item, AjaxRequestTarget target1) {
        // List<ItensNotaRemessaOrdemFornecimentoContrato> listaEndereco =
        // notaRemessaService.buscarItensNotaRemessa(item.getModelObject());
        notaRemessaOrdemFornecimentoContrato = new NotaRemessaOrdemFornecimentoContrato();
        notaRemessaOrdemFornecimentoContrato = item.getModelObject();
        listaOrdemFornecTemp = new ArrayList<ObjetoFornecimentoContrato>();
        // listaOrdemFornecTemp.addAll(ordenarObjetoFornecimentoContrato(ordemFornecimentoContratoService.buscarListaObjetoFornecimentoContrato(item.getModelObject().getOrdemFornecimento(),
        // listaEndereco.get(0).getItemOrdemFornecimentoContrato().getLocalEntrega(),
        // EnumTipoObjeto.AMBOS)));

        ObjetoFornecimentoContratoDto dto = new ObjetoFornecimentoContratoDto();
        ObjetoFornecimentoContrato ofc = new ObjetoFornecimentoContrato();
        ofc.setNotaRemessaOrdemFornecimentoContrato(notaRemessaOrdemFornecimentoContrato);
        dto.setObjetoFornecimentoContrato(ofc);

        // listaObjetoFornecimentoContrato.clear();
        listaOrdemFornecTemp = objetoFornecimentoContratoService.buscarSemPaginacao(dto);

        if (listaOrdemFornecTemp.size() > 0) {
            panelItens.addOrReplace(newDataViewItens());
            panelRelatorio.addOrReplace(newButtonGerarRR().setVisible(!verificarSituacaoItem()));
            panelRelatorio.addOrReplace(newButtonDownloadRR().setVisible(verificarSituacaoItem()));
            panelItens.addOrReplace(new InfraAjaxPagingNavigator("paginatorI", newListaItens));
            panelItens.setVisible(painelItens = true);
            panelRelatorio.setVisible(true);
            target1.add(panelRelatorio);
            target1.add(panelItens);
            target1.appendJavaScript("ancorarResultadoPesquisa('#ancoraPanelItens');");
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

    private void enviarTR(AjaxRequestTarget target, Item<NotaRemessaOrdemFornecimentoContrato> item) {
        List<AnexoDto> lista = anexoNotaRemessaService.buscarPeloIdNotaRemessa(item.getModelObject().getId());
        List<AnexoDto> listaTemp = new ArrayList<AnexoDto>();
        notaRemessaOrdemFornecimentoContrato = new NotaRemessaOrdemFornecimentoContrato();
        notaRemessaOrdemFornecimentoContrato = item.getModelObject();

        for (AnexoDto anexo : lista) {
            if (anexo.getTipoArquivoTermoEntrega() == EnumTipoArquivoTermoEntrega.RELATORIO_RECEBIMENTO_ASSINADO) {
                listaTemp.add(anexo);
            }
        }

        this.listaAnexosNotaRemessa = SideUtil.convertAnexoDtoToEntityAnexoNotaRemessa(listaTemp);

        panelAnexos = new PanelAnexos(item);
        panelAnexos.setVisible(true);
        setOutputMarkupId(Boolean.TRUE);
        form.addOrReplace(panelAnexos);
        target.add(form);
        target.appendJavaScript("ancorarResultadoPesquisa('#ancoraPanelAnexo');");
    }

    private void cadastrarItem(AjaxRequestTarget target, Item<ObjetoFornecimentoContrato> item) {
        PageParameters p = new PageParameters();
        p.add("tipoPessoa", EnumTipoPessoa.REPRESENTANTE_LEGAL.getValor());
        p.add("idInscricao", inscricao.getId());
        p.add("idObjetoOrdemFornecimento", item.getModelObject().getId());
        p.add("idNotaRemessa", notaRemessaOrdemFornecimentoContrato.getId());
        setResponsePage(new ConferenciaCadastrarItemPage(p, new RegistrarRecebimentoPage(getPageParameters(), inscricao), item.getModelObject().getId()));
    }

    private void adicionarAnexo(AjaxRequestTarget target) {
        if (!validarObrigatoriedadeAnexos(target)) {
            return;
        }
        try {
            if (!uploads.isEmpty()) {
                for (FileUpload component : uploads) {
                    AnexoNotaRemessa anexoNotaRemessa = new AnexoNotaRemessa();
                    anexoNotaRemessa.setNomeAnexo(component.getClientFileName());
                    anexoNotaRemessa.setConteudo(component.getBytes());
                    anexoNotaRemessa.setTipoArquivoTermoEntrega(EnumTipoArquivoTermoEntrega.RELATORIO_RECEBIMENTO_ASSINADO);
                    this.listaAnexosNotaRemessa.add(anexoNotaRemessa);
                }
                addMsgInfo("Arquivo adicionado com sucesso");
            }
            target.add(this.panelAnexos);

        } catch (NullPointerException e) {
            addMsgInfo("Adicione um arquivo a ser anexado.");
        }
    }

    private void btnAnexar(AjaxRequestTarget target, Item<NotaRemessaOrdemFornecimentoContrato> item) {
        List<AnexoDto> lista = anexoNotaRemessaService.buscarPeloIdNotaRemessa(item.getModelObject().getId());
        List<AnexoDto> listaTemp = new ArrayList<AnexoDto>();
        for (AnexoDto anexo : lista) {
            if (!anexo.getTipoArquivoTermoEntrega().equals(EnumTipoArquivoTermoEntrega.RELATORIO_RECEBIMENTO_ASSINADO)) {
                listaTemp.add(anexo);
            }
        }
        List<AnexoNotaRemessa> listaAnexoNotaRemessaTemp = SideUtil.convertAnexoDtoToEntityAnexoNotaRemessa(listaTemp);
        listaAnexoNotaRemessaTemp.addAll(this.listaAnexosNotaRemessa);
        item.getModelObject().setListaAnexosNotaRemessa(listaAnexoNotaRemessaTemp);
        notaRemessaService.incluirAlterar(item.getModelObject(), EnumBotaoClicadoNotaRemessa.TERMO_RECEBIMENTO_ENVIADO);
        addMsgInfo("Termo de Recebimento anexado com sucesso!");
        this.listaAnexosNotaRemessa = new ArrayList<AnexoNotaRemessa>();
        notaRemessaOrdemFornecimentoContrato = new NotaRemessaOrdemFornecimentoContrato();
        notaRemessaOrdemFornecimentoContrato = item.getModelObject();

        panelAnexos = new PanelAnexos(null);
        setOutputMarkupId(Boolean.TRUE);
        panelAnexos.setVisible(false);
        panelItens.setVisible(false);
        panelRelatorio.setVisible(false);
        form.addOrReplace(panelAnexos);
        form.addOrReplace(panelItens);
        form.addOrReplace(panelRelatorio);
        target.add(form);
        target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
    }

    private void cancelarAnexo(AjaxRequestTarget target) {
        this.listaAnexosNotaRemessa = new ArrayList<AnexoNotaRemessa>();

        panelAnexos = new PanelAnexos(null);
        panelAnexos.setVisible(false);
        setOutputMarkupId(Boolean.TRUE);
        form.addOrReplace(panelAnexos);
        target.add(panelAnexos);
    }

    private boolean validarObrigatoriedadeAnexos(AjaxRequestTarget target) {
        boolean validar = VERDADEIRO;
        String msg = "";

        if (uploads == null) {
            msg += "<p><li> Selecione um arquivo para anexar.</li><p />";
            validar = FALSO;
        }
        if (!validar) {
            mensagem.setObject(msg);
        } else {
            mensagem.setObject("");
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
        target.add(this.panelAnexos);
    }

    // ####################################_GETTERS_E_SETTERS_###############################################
    public Integer getItensPorPaginaRecebimentos() {
        return itensPorPaginaRecebimentos;
    }

    public void setItensPorPaginaRecebimetentos(Integer itensPorPaginaRecebimentos) {
        this.itensPorPaginaRecebimentos = itensPorPaginaRecebimentos;
    }
}
