package br.gov.mj.side.web.view.execucao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumStatusExecucaoNotaRemessaBeneficiario;
import br.gov.mj.side.entidades.enums.EnumStatusExecucaoNotaRemessaContratante;
import br.gov.mj.side.entidades.enums.EnumStatusExecucaoNotaRemessaFornecedor;
import br.gov.mj.side.entidades.enums.EnumStatusOrdemFornecimento;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.HistoricoComunicacaoGeracaoOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.ItensNotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.web.dto.ContratoDto;
import br.gov.mj.side.web.service.ContratoService;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.NotaRemessaService;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.service.UfService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.DataUtil;
import br.gov.mj.side.web.util.MascaraUtils;
import br.gov.mj.side.web.view.components.EntityDataProvider;
import br.gov.mj.side.web.view.fornecedor.ContratoPage2;
import br.gov.mj.side.web.view.programa.PanelFasePrograma;
import br.gov.mj.side.web.view.template.TemplatePage;

public class AcompanharOrdemFornecimentoPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    // ##################### Constantes #######################
    private static final String ONCHANGE = "onchange";

    // ##################### Paineis #######################

    private Form<AcompanharOrdemFornecimentoPage> form;
    private PanelFasePrograma panelFasePrograma;
    private PanelListaDeContratos panelListaDeContratos;
    private PanelListaDeOfs panelListaDeOfs;
    private PanelItensContrato panelItensContrato;
    private PanelBotoes panelBotoes;

    // ##################### Variáveis #######################
    private Programa programa;
    private Page backPage;
    private Integer abaClicada;
    private Integer itensPorPaginaContrato = Constants.ITEMS_PER_PAGE_PAGINATION;
    private Integer itensPorPaginaOf = Constants.ITEMS_PER_PAGE_PAGINATION;
    private Integer itensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;

    private Contrato contratoSelecionado;
    private OrdemFornecimentoContrato ordemFornecimentoSelecionada;

    private List<Contrato> listaDeContratos = new ArrayList<Contrato>();
    private List<OrdemFornecimentoContrato> listaDeOrdemFornecimento = new ArrayList<OrdemFornecimentoContrato>();
    List<NotaRemessaOrdemFornecimentoContrato> listaDeExecucao = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();
    private List<InscricaoPrograma> listaInscricaoPrograma = new ArrayList<InscricaoPrograma>();
    private ExecucaoPanelBotoes execucaoPanelBotoes;
    private Boolean habilitarOF = Boolean.FALSE;
    private Boolean habilitarOFR = Boolean.FALSE;

    // ###################### Componentes Wicket #######################
    private RadioGroup<Contrato> radioContratoGrup;
    private RadioGroup<OrdemFornecimentoContrato> radioOrdemFornecimentoGrup;
    private DataView<Contrato> dataViewListaContrato;
    private DataView<OrdemFornecimentoContrato> dataViewOrdemFornecimento;
    private DataView<NotaRemessaOrdemFornecimentoContrato> dataViewListaItens;

    // ##################### Injeções de dependências #########

    @Inject
    private ContratoService contratoService;
    @Inject
    private UfService ufService;
    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;
    @Inject
    private NotaRemessaService notaRemessaService;
    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private InscricaoProgramaService inscricaoProgramaService;

    public AcompanharOrdemFornecimentoPage(final PageParameters pageParameters) {
        super(pageParameters);
    }

    // Esta sendo usado este
    public AcompanharOrdemFornecimentoPage(final PageParameters pageParameters, Programa programa, Page backPage, Integer abaClicada) {
        super(pageParameters);
        setTitulo("Gerenciar Programa");
        this.backPage = backPage;
        this.programa = programa;
        this.abaClicada = abaClicada;

        initVariaveis();
        initComponents();
    }

    private void initVariaveis() {
        ContratoDto contratoDto = new ContratoDto();
        contratoDto.setNomePrograma(programa.getNomePrograma());

        listaDeContratos = contratoService.buscarSemPaginacao(contratoDto);
    }

    private void initComponents() {

        form = new Form<AcompanharOrdemFornecimentoPage>("form", new CompoundPropertyModel<AcompanharOrdemFornecimentoPage>(this));
        add(form);

        panelFasePrograma = new PanelFasePrograma("panelFasePrograma", programa, backPage, abaClicada);
        form.add(execucaoPanelBotoes = new ExecucaoPanelBotoes("execucaoPanelPotoes", programa, backPage, "AnaliseEntrega"));
        form.add(panelListaDeContratos = new PanelListaDeContratos("panelListaDeContratos"));
        panelListaDeOfs = new PanelListaDeOfs("panelListaDeOfs");
        panelListaDeOfs.setVisible(this.habilitarOF);
        form.add(panelListaDeOfs);
        panelItensContrato = new PanelItensContrato("panelItensContrato");
        panelItensContrato.setVisible(habilitarOFR);
        panelBotoes = new PanelBotoes("panelBotoes");
        form.add(panelItensContrato);
        form.add(panelFasePrograma);
        form.add(panelBotoes);
    }

    // PAINEIS
    private class PanelListaDeContratos extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelListaDeContratos(String id) {
            super(id);
            setOutputMarkupId(true);

            ContratoDto contratoDto = new ContratoDto();
            contratoDto.setNomePrograma(programa.getNomePrograma());
            contratoDto.setCodigoPrograma(programa.getCodigoIdentificadorProgramaPublicado());
            contratoDto.setPesquisarProgramasComFormatacaoDeItens(true);

            radioContratoGrup = new RadioGroup<Contrato>("radioGrupContrato", new Model<Contrato>());
            radioContratoGrup.add(dataViewListaContrato = newDataViewListaContrato()); // listaContrato
            add(radioContratoGrup);

            add(newDropItensPorPaginaContrato()); // itensPorPaginaContrato
            add(new InfraAjaxPagingNavigator("pagination", dataViewListaContrato));
        }
    }

    private class PanelListaDeOfs extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelListaDeOfs(String id) {
            super(id);
            setOutputMarkupId(true);

            radioOrdemFornecimentoGrup = new RadioGroup<OrdemFornecimentoContrato>("radioOrdemFornecimentoGrup", new Model<OrdemFornecimentoContrato>());
            radioOrdemFornecimentoGrup.add(dataViewOrdemFornecimento = newDataViewOrdensDeFornecimento()); // listaOrdemFornecimento
            add(radioOrdemFornecimentoGrup);

            add(new InfraAjaxPagingNavigator("paginationOf", dataViewOrdemFornecimento));
            add(newDropItensPorPaginaOf()); // itensPorPaginaOf
        }
    }

    private class PanelItensContrato extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelItensContrato(String id) {
            super(id);
            setOutputMarkupId(true);
            add(newDataViewItens()); // listaItensContratoNewDataView
            add(new InfraAjaxPagingNavigator("paginatorItensContrato", dataViewListaItens));
            add(newDropItensPorPagina()); // itensPorPagina
        }
    }

    private class PanelBotoes extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBotoes(String id) {
            super(id);
            setOutputMarkupId(true);
            add(newButtonVoltar()); // btnVoltar
        }
    }

    // COMPONENTES

    private DropDownChoice<Integer> newDropItensPorPaginaContrato() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPaginaContrato", new LambdaModel<Integer>(this::getItensPorPaginaContrato, this::setItensPorPaginaContrato), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewListaContrato.setItemsPerPage(getItensPorPaginaContrato());
                target.add(panelListaDeContratos);
            };
        });
        return dropDownChoice;
    }

    private DropDownChoice<Integer> newDropItensPorPaginaOf() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPaginaOf", new LambdaModel<Integer>(this::getItensPorPaginaOf, this::setItensPorPaginaOf), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewOrdemFornecimento.setItemsPerPage(getItensPorPaginaOf());
                target.add(panelListaDeOfs);
            };
        });
        return dropDownChoice;
    }

    private DropDownChoice<Integer> newDropItensPorPagina() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPagina", new LambdaModel<Integer>(this::getItensPorPagina, this::setItensPorPagina), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewListaItens.setItemsPerPage(getItensPorPagina());
                atualizarDataViewItens(target);
            };
        });
        return dropDownChoice;
    }

    public DataView<Contrato> newDataViewListaContrato() {
        dataViewListaContrato = new DataView<Contrato>("listaContrato", new EntityDataProvider<Contrato>(listaDeContratos)) {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("rawtypes")
            @Override
            protected void populateItem(final Item<Contrato> item) {

                BigDecimal valorTotal = contratoService.buscarValorDoContrato(item.getModelObject());
                BigDecimal saldoExecutado = contratoService.buscarSaldoExecutadoDoContrato(item.getModelObject(), true);
                BigDecimal saldoAExecutar = valorTotal.subtract(saldoExecutado);

                // Se o saldo a executar for 0 então a linha ficará verde.
                if (saldoAExecutar.doubleValue() == 0.0) {
                    AttributeAppender style = new AttributeAppender("style", "background:#defaea;", " ");
                    item.add(style);
                }

                Radio<Contrato> radioContrato = new Radio<Contrato>("radioContrato", Model.of(item.getModelObject()));
                radioContrato.add(new AjaxEventBehavior(ONCHANGE) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        exibirListaOrdensFornecimentos(item, target, saldoAExecutar);
                    }
                });

                if (contratoSelecionado != null && contratoSelecionado.getId() != null && contratoSelecionado.getId().intValue() == item.getModelObject().getId().intValue()) {
                    radioContratoGrup.setConvertedInput(contratoSelecionado);
                    radioContratoGrup.setDefaultModelObject(contratoSelecionado);
                }

                item.add(radioContrato);
                item.add(new Label("numeroContrato"));
                item.add(new Label("saldoContratado", MascaraUtils.formatarMascaraDinheiro(valorTotal)));
                item.add(new Label("saldoExecutar", MascaraUtils.formatarMascaraDinheiro(saldoAExecutar)));
                item.add(new Label("vencimentoCtr", DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataVigenciaFimContrato(), "dd/MM/yyyy")));
                item.add(new Label("situacaoContrato", (item.getModelObject().getStatusContrato() != null) ? item.getModelObject().getStatusContrato().getDescricao() : ""));

                InfraAjaxFallbackLink btnVisualizarContrato = componentFactory.newAjaxFallbackLink("btnVisualizarContrato", (target) -> visualizar(target, item));
                item.add(btnVisualizarContrato);
            }
        };
        dataViewListaContrato.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataViewListaContrato;
    }

    private void visualizar(AjaxRequestTarget target, Item<Contrato> item) {
        Contrato contrato = item.getModelObject();
        setResponsePage(new ContratoPage2(getPageParameters(), this, contrato, contrato.getPrograma(), false, false));
    }

    public DataView<OrdemFornecimentoContrato> newDataViewOrdensDeFornecimento() {
        dataViewOrdemFornecimento = new DataView<OrdemFornecimentoContrato>("listaOrdemFornecimento", new EntityDataProvider<OrdemFornecimentoContrato>(listaDeOrdemFornecimento)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<OrdemFornecimentoContrato> item) {

                HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historicoOrdem = ordemFornecimentoContratoService.buscarUltimoHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(item.getModelObject(), true);
                List<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> listaHistoricoComunicacaoGeracao = new ArrayList<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato>();
                listaHistoricoComunicacaoGeracao.add(historicoOrdem);
                item.getModelObject().setListaHistoricoComunicacaoGeracao(listaHistoricoComunicacaoGeracao);

                Radio<OrdemFornecimentoContrato> radioOfs = new Radio<OrdemFornecimentoContrato>("radioOfs", Model.of(item.getModelObject()));
                radioOfs.add(new AjaxEventBehavior(ONCHANGE) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        exibirListaItens(item, target);
                        listaDeExecucao.clear();
                        listaDeExecucao.addAll(notaRemessaService.buscarListaNotasRemessasCadastradas(ordemFornecimentoSelecionada));
                        if (listaDeExecucao.size() > 0) {
                            habilitarOFR = Boolean.TRUE;
                            atualizarDataViewItens(target);
                            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraOFR');");
                        } else {
                            habilitarOFR = Boolean.FALSE;
                            atualizarDataViewItens(target);
                            addMsgInfo("Nenhum resultado foi encontrado!");
                            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
                        }
                    }
                });

                if (ordemFornecimentoSelecionada != null && ordemFornecimentoSelecionada.getId() != null && ordemFornecimentoSelecionada.getId().intValue() == item.getModelObject().getId().intValue()) {
                    radioOrdemFornecimentoGrup.setConvertedInput(ordemFornecimentoSelecionada);
                    radioOrdemFornecimentoGrup.setDefaultModelObject(ordemFornecimentoSelecionada);
                }

                item.add(radioOfs);
                item.add(new Label("numeroOf", historicoOrdem == null || historicoOrdem.getNumeroDocumentoSei() == null ? " (não comunicado) " : historicoOrdem.getNumeroDocumentoSei()));
                item.add(new Label("situacaoOf", (item.getModelObject().getStatusOrdemFornecimento() != null) ? item.getModelObject().getStatusOrdemFornecimento().getDescricao() : ""));

                if (item.getModelObject().getStatusOrdemFornecimento() != null && (item.getModelObject().getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.COM_PENDENCIA))) {
                    AttributeAppender style = new AttributeAppender("style", "background:#fd8e82;", " ");
                    item.add(style);
                }

                if (item.getModelObject().getStatusOrdemFornecimento() != null && (item.getModelObject().getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.CONCLUIDA))) {
                    AttributeAppender style = new AttributeAppender("style", "background:#defaea;", " ");
                    item.add(style);
                }

            }
        };
        dataViewOrdemFornecimento.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataViewOrdemFornecimento;
    }

    private DataView<NotaRemessaOrdemFornecimentoContrato> newDataViewItens() {
        dataViewListaItens = new DataView<NotaRemessaOrdemFornecimentoContrato>("listaItensContratoNewDataView", new EntityDataProvider<NotaRemessaOrdemFornecimentoContrato>(listaDeExecucao)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<NotaRemessaOrdemFornecimentoContrato> item) {
                AttributeAppender classeAtivarPopover = new AttributeAppender("class", "pop", " ");
                
                item.getModelObject().setListaItensNotaRemessaOrdemFornecimentoContratos(notaRemessaService.buscarItensNotaRemessa(item.getModelObject()));
                String[] lista = formatarLista(item.getModelObject(), false);

                item.add(new Label("numeroNotaRemessa"));
                item.add(new Label("nomeBeneficiarioitem", lista[5]));
                item.add(new Label("ufItem", lista[0]));

                String iconeItensQuant = "<a tabindex=\"0\" role=\"button\" data-toggle=\"popover\" data-trigger=\"focus\" data-content=\"" + lista[7] + "\"><i style=\"color:green;\" class=\"fa fa-info-circle\"> </i></a>";
                Label lblDescricaoItensQuant = new Label("itensQuant2", iconeItensQuant);
                lblDescricaoItensQuant.setEscapeModelStrings(false);
                lblDescricaoItensQuant.setOutputMarkupId(true);
                if (item.getModelObject().getListaItensNotaRemessaOrdemFornecimentoContratos().size() > 10)
                    lblDescricaoItensQuant.add(classeAtivarPopover);
                item.add(lblDescricaoItensQuant);

                String cor = buscarCor(item.getModelObject(), 1);
                String iconeContratado = "<a  title=\"" + (item.getModelObject().getStatusExecucaoFornecedor() == null ? "" : item.getModelObject().getStatusExecucaoFornecedor().getDescricao())
                        + "\"><i  style=\"border-radius: 50%;display: inline-block;height: 20px;width: 20px;border: 1px solid #000000;background-color: " + cor + "\"> </i></a>";
                Label lblIconeContratado = new Label("iconeContratado", iconeContratado);
                lblIconeContratado.setEscapeModelStrings(false);
                lblIconeContratado.setOutputMarkupId(true);
                item.add(lblIconeContratado);

                cor = buscarCor(item.getModelObject(), 2);
                String iconeBeneficiario = "<a  title=\"" + (item.getModelObject().getStatusExecucaoBeneficiario() == null ? "" : item.getModelObject().getStatusExecucaoBeneficiario().getDescricao())
                        + "\"><i  style=\"border-radius: 50%;display: inline-block;height: 20px;width: 20px;border: 1px solid #000000;background-color: " + cor + "\"> </i></a>";
                Label lblIconeBeneficiario = new Label("iconeBeneficiario", iconeBeneficiario);
                lblIconeBeneficiario.setEscapeModelStrings(false);
                lblIconeBeneficiario.setOutputMarkupId(true);
                item.add(lblIconeBeneficiario);

                cor = buscarCor(item.getModelObject(), 3);
                String iconeContratante = "<a  title=\"" + (item.getModelObject().getStatusExecucaoContratante() == null ? "" : item.getModelObject().getStatusExecucaoContratante().getDescricao())
                        + "\"><i  style=\"border-radius: 50%;display: inline-block;height: 20px;width: 20px;border: 1px solid #000000;background-color: " + cor + "\"> </i></a>";
                Label lblIconeContratante = new Label("iconeContratante", iconeContratante);
                lblIconeContratante.setEscapeModelStrings(false);
                lblIconeContratante.setOutputMarkupId(true);
                item.add(lblIconeContratante);
                item.add(newButtonDetalharItem(item)); // btnDetalharItem
            }
        };
        dataViewListaItens.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataViewListaItens;
    }

    private String buscarCor(NotaRemessaOrdemFornecimentoContrato notaRemessa, int tipo) {
        String cor = "white";
        if (notaRemessa.getStatusExecucaoFornecedor() != null && tipo == 1) {

            if (notaRemessa.getStatusExecucaoFornecedor().equals(EnumStatusExecucaoNotaRemessaFornecedor.EM_PREPARACAO)) {
                cor = "yellow";
            } else if (notaRemessa.getStatusExecucaoFornecedor().equals(EnumStatusExecucaoNotaRemessaFornecedor.EMITIDA)) {
                cor = "yellow";
            } else if (notaRemessa.getStatusExecucaoFornecedor().equals(EnumStatusExecucaoNotaRemessaFornecedor.ENTREGUE)) {
                cor = "green";
            } else if (notaRemessa.getStatusExecucaoFornecedor().equals(EnumStatusExecucaoNotaRemessaFornecedor.CONCLUIDA)) {
                cor = "green";
            }
        }

        if (notaRemessa.getStatusExecucaoBeneficiario() != null && tipo == 2) {

            if (notaRemessa.getStatusExecucaoBeneficiario().equals(EnumStatusExecucaoNotaRemessaBeneficiario.RECEBIDO)) {
                cor = "yellow";
            } else if (notaRemessa.getStatusExecucaoBeneficiario().equals(EnumStatusExecucaoNotaRemessaBeneficiario.EM_ANALISE)) {
                cor = "yellow";
            } else if (notaRemessa.getStatusExecucaoBeneficiario().equals(EnumStatusExecucaoNotaRemessaBeneficiario.EM_ATRASO)) {
                cor = "red";
            } else if (notaRemessa.getStatusExecucaoBeneficiario().equals(EnumStatusExecucaoNotaRemessaBeneficiario.INADIPLENTE)) {
                cor = "red";
            } else if (notaRemessa.getStatusExecucaoBeneficiario().equals(EnumStatusExecucaoNotaRemessaBeneficiario.ENVIADO)) {
                cor = "green";
            }
        }

        if (notaRemessa.getStatusExecucaoContratante() != null && tipo == 3) {

            if (notaRemessa.getStatusExecucaoContratante().equals(EnumStatusExecucaoNotaRemessaContratante.ACEITO)) {
                cor = "green";
            } else if (notaRemessa.getStatusExecucaoContratante().equals(EnumStatusExecucaoNotaRemessaContratante.NAO_ACEITO)) {
                cor = "red";
            } else if (notaRemessa.getStatusExecucaoContratante().equals(EnumStatusExecucaoNotaRemessaContratante.ACEITO_COM_RESALVA)) {
                cor = "yellow";
            } else if (notaRemessa.getStatusExecucaoContratante().equals(EnumStatusExecucaoNotaRemessaContratante.EM_ANALISE)) {
                cor = "yellow";
            } else if (notaRemessa.getStatusExecucaoContratante().equals(EnumStatusExecucaoNotaRemessaContratante.RELATORIO_RECEBIMENTO_ENVIADO)) {
                cor = "yellow";
            }
        }
        return cor;
    }

    private InfraAjaxFallbackLink<Void> newButtonDetalharItem(Item<NotaRemessaOrdemFornecimentoContrato> item) {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnDetalharItem", (target) -> actionDetalharItem(item, target));

        btn.setOutputMarkupId(true);
        btn.setVisible((item.getModelObject().getStatusExecucaoFornecedor() != null && !item.getModelObject().getStatusExecucaoFornecedor().equals(EnumStatusExecucaoNotaRemessaFornecedor.EM_PREPARACAO)));
        return btn;
    }

    private void verificarOrdensDeFornecimentoSemComunicacao(List<OrdemFornecimentoContrato> listaTemp) {

        listaDeOrdemFornecimento.clear();
        for (OrdemFornecimentoContrato ofc : listaTemp) {
            HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historico = ordemFornecimentoContratoService.buscarUltimoHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(ofc, false);

            // Irá adicionar somente as ordens de fornecimento com historico, ou
            // seja, as comunicadas.
            if (historico != null && historico.getPossuiComunicado()) {
                listaDeOrdemFornecimento.add(ofc);
            }
        }
    }

    // AÇÕES

    private void actionDetalharItem(Item<NotaRemessaOrdemFornecimentoContrato> item, AjaxRequestTarget target) {
        setResponsePage(new DetalharTermoEntregaPage(new PageParameters(), programa, item.getModelObject(), this, abaClicada));
    }

    // Metodo que irá organizar todos os itens da ordem de fornecimento por tipo
    // e somar os valores que são do mesmo bem.
    private String[] formatarLista(NotaRemessaOrdemFornecimentoContrato notaRemessaLocaisEntrega, boolean buscarMenbros) {
        String[] lista = new String[8];

        if (notaRemessaLocaisEntrega.getOrdemFornecimento().getStatusOrdemFornecimento() == null || notaRemessaLocaisEntrega.getOrdemFornecimento().getStatusOrdemFornecimento() == EnumStatusOrdemFornecimento.NAO_COMUNICADA
                || notaRemessaLocaisEntrega.getOrdemFornecimento().getStatusOrdemFornecimento() == EnumStatusOrdemFornecimento.EMITIDA) {
            lista[6] = "false";
        } else {
            lista[6] = "true";
        }

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
                listaItemQuantidade.append("<tr><td>" + INFOFC.getItemOrdemFornecimentoContrato().getItem().getNomeBem() + "</td><td><center>" + INFOFC.getItemOrdemFornecimentoContrato().getQuantidade().toString() + "</center></td></tr>");
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

    private void exibirListaOrdensFornecimentos(Item<Contrato> item, AjaxRequestTarget target, BigDecimal saldoAExecutar) {
        contratoSelecionado = item.getModelObject();
        ordemFornecimentoSelecionada = new OrdemFornecimentoContrato();

        if (contratoSelecionado != null && contratoSelecionado.getId() != null) {

            List<OrdemFornecimentoContrato> novaLista = new ArrayList<OrdemFornecimentoContrato>();
            for (OrdemFornecimentoContrato objeto : ordemFornecimentoContratoService.buscarOrdemFornecimentoContrato(contratoSelecionado)) {
                if (objeto.getStatusOrdemFornecimento() != null
                        && (!objeto.getStatusOrdemFornecimento().equals(EnumStatusOrdemFornecimento.NAO_COMUNICADA))) {
                    novaLista.add(objeto);

                }
            }

            verificarOrdensDeFornecimentoSemComunicacao(novaLista);
        }

        listaDeExecucao.clear();
        this.habilitarOFR = Boolean.FALSE;
        if (listaDeOrdemFornecimento.size() > 0) {
            this.habilitarOF = Boolean.TRUE;
            ordemFornecimentoSelecionada = null;
            atualizarDataViewOrdemFornecimento(target, saldoAExecutar);
            atualizarDataViewItens(target);
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraOF');");
        } else {
            this.habilitarOF = Boolean.FALSE;
            atualizarDataViewOrdemFornecimento(target, saldoAExecutar);
            addMsgInfo("Nenhum resultado foi encontrado!");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        }
    }

    private void exibirListaItens(Item<OrdemFornecimentoContrato> item, AjaxRequestTarget target) {
        ordemFornecimentoSelecionada = item.getModelObject();
        atualizarDataViewItens(target);
    }

    private void atualizarDataViewOrdemFornecimento(AjaxRequestTarget target, BigDecimal saldoAExecutar) {
        radioOrdemFornecimentoGrup.addOrReplace(newDataViewOrdensDeFornecimento());
        panelListaDeOfs.addOrReplace(radioOrdemFornecimentoGrup);
        panelListaDeOfs.addOrReplace(new InfraAjaxPagingNavigator("paginationOf", dataViewOrdemFornecimento));
        panelListaDeOfs.setVisible(this.habilitarOF);
        target.add(panelListaDeOfs);
    }

    private void atualizarDataViewItens(AjaxRequestTarget target) {
        panelItensContrato.addOrReplace(newDataViewItens());
        panelItensContrato.addOrReplace(new InfraAjaxPagingNavigator("paginatorItensContrato", dataViewListaItens));
        panelItensContrato.setVisible(this.habilitarOFR);
        target.add(panelItensContrato);
    }

    private InfraAjaxFallbackLink<Void> newButtonVoltar() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnVoltar", (target) -> actionVoltar());
        btn.setOutputMarkupId(true);
        return btn;
    }

    private void actionVoltar() {
        setResponsePage(backPage);
    }

    public Integer getItensPorPaginaContrato() {
        return itensPorPaginaContrato;
    }

    public void setItensPorPaginaContrato(Integer itensPorPaginaContrato) {
        this.itensPorPaginaContrato = itensPorPaginaContrato;
    }

    public Integer getItensPorPaginaOf() {
        return itensPorPaginaOf;
    }

    public void setItensPorPaginaOf(Integer itensPorPaginaOf) {
        this.itensPorPaginaOf = itensPorPaginaOf;
    }

    public Integer getItensPorPagina() {
        return itensPorPagina;
    }

    public void setItensPorPagina(Integer itensPorPagina) {
        this.itensPorPagina = itensPorPagina;
    }

}
