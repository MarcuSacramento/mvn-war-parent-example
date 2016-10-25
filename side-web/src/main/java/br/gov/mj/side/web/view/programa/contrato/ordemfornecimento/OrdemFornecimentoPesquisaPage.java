package br.gov.mj.side.web.view.programa.contrato.ordemfornecimento;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.apoio.entidades.Regiao;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.BemUf;
import br.gov.mj.side.entidades.programa.licitacao.SelecaoItem;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.HistoricoComunicacaoGeracaoOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ItensOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;
import br.gov.mj.side.web.dto.ContratoDto;
import br.gov.mj.side.web.service.ContratoService;
import br.gov.mj.side.web.service.FormatacaoItensContratoService;
import br.gov.mj.side.web.service.LicitacaoProgramaService;
import br.gov.mj.side.web.service.MailService;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.DataUtil;
import br.gov.mj.side.web.util.MascaraUtils;
import br.gov.mj.side.web.view.planejarLicitacao.ContratoPanelBotoes;
import br.gov.mj.side.web.view.programa.PanelFasePrograma;
import br.gov.mj.side.web.view.programa.contrato.comunicarFornecedor.ComunicarFornecedorPage;
import br.gov.mj.side.web.view.template.TemplatePage;

public class OrdemFornecimentoPesquisaPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    // ##################### Constantes #######################
    private static final String ONCHANGE = "onchange";

    // ##################### variaveis ########################

    private Form<OrdemFornecimentoPesquisaPage> form;
    private Programa programa;
    private Contrato contratoSelecionado;
    private Page backPage;
    private Integer itensPorPaginaContrato = Constants.ITEMS_PER_PAGE_PAGINATION;
    private Integer itensPorPaginaOf = Constants.ITEMS_PER_PAGE_PAGINATION;
    private String motivoCancelamento;

    private Label labelMensagem;
    private Model<String> mensagem = Model.of("");

    private List<Contrato> listaDeContratoComTodosBensFormatados = new ArrayList<Contrato>();
    private List<OrdemFornecimentoContrato> listaDeOrdemFornecimento = new ArrayList<OrdemFornecimentoContrato>();

    // ##################### paineis ##########################

    private PanelFasePrograma panelFasePrograma;
    private ContratoPanelBotoes execucaoPanelBotoes;
    private PanelListaDeContratos panelListaDeContratos;
    private PanelListaDeOfs panelListaDeOfs;
    private PanelPrincipalCancelarOf panelPrincipalCancelarOf;
    private PanelCancelarOf panelCancelarOf;
    private WebMarkupContainer containerBotaoNovaOf;

    // ##################### Componentes Wicket ###############

    private DataView<Contrato> dataViewListaContrato;
    private DataView<OrdemFornecimentoContrato> dataViewOrdemFornecimento;
    private TextArea<String> textAreaMotivoCancelamento;
    private RadioGroup<Contrato> radioContratoGrup;

    // ##################### Injeções de dependências #########

    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private ContratoService contratoService;
    @Inject
    private LicitacaoProgramaService licitacaoProgramaService;
    @Inject
    private FormatacaoItensContratoService formatacaoItensContratoService;
    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;
    @Inject
    private MailService mailService;

    public OrdemFornecimentoPesquisaPage(final PageParameters pageParameters, Programa programa, Page backPage) {
        super(pageParameters);
        this.programa = programa;
        this.backPage = backPage;

        setTitulo("Gerenciar Programa");
        initVariaveis();
        initComponents();

    }

    // Construtor usado somente no momento de voltar a página
    public OrdemFornecimentoPesquisaPage(final PageParameters pageParameters, Programa programa, Page backPage, Contrato contratoSelecionado) {
        super(pageParameters);
        this.programa = programa;
        this.backPage = backPage;
        this.contratoSelecionado = contratoSelecionado;

        setTitulo("Gerenciar Programa");
        initVariaveis();
        initComponents();

    }

    private void initVariaveis() {
        ContratoDto contratoDto = new ContratoDto();
        contratoDto.setNomePrograma(programa.getNomePrograma());
        contratoDto.setCodigoPrograma(programa.getCodigoIdentificadorProgramaPublicado());

        listaDeContratoComTodosBensFormatados = new ArrayList<Contrato>();
        List<Contrato> todosContratos = contratoService.buscarSemPaginacao(contratoDto);
        for (Contrato contrato : todosContratos) {
            List<Bem> bemRemanecentes = formatacaoItensContratoService.buscarListaBensRemanescentes(contrato);
            if (bemRemanecentes != null && bemRemanecentes.size() == 0) {
                listaDeContratoComTodosBensFormatados.add(contrato);
            }
        }
    }

    private void initComponents() {
        form = new Form<OrdemFornecimentoPesquisaPage>("form", new CompoundPropertyModel<OrdemFornecimentoPesquisaPage>(this));
        add(form);

        form.add(execucaoPanelBotoes = new ContratoPanelBotoes("execucaoPanelPotoes", programa, backPage, "ordemFornecimento"));
        form.add(panelFasePrograma = new PanelFasePrograma("panelFasePrograma", programa, backPage));
        form.add(panelListaDeContratos = new PanelListaDeContratos("panelListaDeContratos"));
        form.add(panelListaDeOfs = new PanelListaDeOfs("panelListaDeOfs"));
        form.add(containerBotaoNovaOf = newContainerBotaoGerarNovaOf("containerBotaoNovaOf"));

        panelPrincipalCancelarOf = new PanelPrincipalCancelarOf("panelPrincipalCancelarOf");
        panelPrincipalCancelarOf.setVisible(false);
        form.add(panelPrincipalCancelarOf);
        form.add(newButtonVoltar()); // btnVoltar
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

            add(newDataViewOrdensDeFornecimento()); // listaOrdemFornecimento
            add(new InfraAjaxPagingNavigator("paginationOf", dataViewOrdemFornecimento));
            add(newDropItensPorPaginaOf()); // itensPorPaginaOf
        }
    }

    private class PanelPrincipalCancelarOf extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelPrincipalCancelarOf(String id) {
            super(id);
            setOutputMarkupId(true);
            add(panelCancelarOf = new PanelCancelarOf("panelCancelarOf", null));
        }
    }

    private class PanelCancelarOf extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelCancelarOf(String id, Item<OrdemFornecimentoContrato> item) {
            super(id);
            setOutputMarkupId(true);

            String idOrdem = "";
            String frase = "";
            OrdemFornecimentoContrato ordemFornecimento = item == null || item.getModelObject() == null || item.getModelObject().getId() == null ? new OrdemFornecimentoContrato() : item.getModelObject();

            if (item != null) {
                idOrdem = item.getModelObject().getId().toString();
                frase = receberFraseDeCancelamento();
            }

            // Contem a frase informando sobre o cancelamento
            Label lblCancelarOf = new Label("lblCancelarOf", frase);
            lblCancelarOf.setEscapeModelStrings(false);
            add(lblCancelarOf);

            // Label de aviso de obrigatóriedade da informação do motivo do
            // cancelamento
            labelMensagem = new Label("mensagem", mensagem);
            labelMensagem.setEscapeModelStrings(false);
            labelMensagem.setOutputMarkupId(true);

            add(labelMensagem);

            add(newTextAreaMotivoCancelamento()); // txtMotivoCancelamento
            add(newButtonCancelarCancelamentoOf()); // btnCancelarCancelamentoOf
            add(newButtonConfirmarCancelamentoOf(ordemFornecimento)); // btnConfirmarCancelamentoOf
        }
    }

    private WebMarkupContainer newContainerBotaoGerarNovaOf(String id) {
        containerBotaoNovaOf = new WebMarkupContainer(id);
        containerBotaoNovaOf.add(newButtonNovo()); // btnAdicionarNovo
        containerBotaoNovaOf.setOutputMarkupId(true);
        return containerBotaoNovaOf;
    }

    // Componentes

    private Button newButtonVoltar() {
        Button buttonVoltar = componentFactory.newButton("btnVoltar", () -> voltar());
        buttonVoltar.setDefaultFormProcessing(false);
        return buttonVoltar;
    }

    public TextArea<String> newTextAreaMotivoCancelamento() {

        textAreaMotivoCancelamento = new TextArea<String>("txtMotivoCancelamento", new PropertyModel<String>(this, "motivoCancelamento"));
        textAreaMotivoCancelamento.setLabel(Model.of("Motivo Cancelamento"));
        textAreaMotivoCancelamento.setOutputMarkupId(true);
        textAreaMotivoCancelamento.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        actionTextArea(textAreaMotivoCancelamento);

        return textAreaMotivoCancelamento;
    }

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

    public DataView<Contrato> newDataViewListaContrato() {
        dataViewListaContrato = new DataView<Contrato>("listaContrato", new ProviderItensContrato()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<Contrato> item) {

                String grupoItem = verificarGrupoItem(item);

                BigDecimal valorTotal = contratoService.buscarValorDoContrato(item.getModelObject());
                BigDecimal saldoExecutado = contratoService.buscarSaldoExecutadoDoContrato(item.getModelObject(),false);
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
                        exibirListaItens(item, target, saldoAExecutar);
                    }
                });

                if (contratoSelecionado != null && contratoSelecionado.getId() != null && contratoSelecionado.getId().intValue() == item.getModelObject().getId().intValue()) {
                    radioContratoGrup.setConvertedInput(contratoSelecionado);
                    radioContratoGrup.setDefaultModelObject(contratoSelecionado);
                }

                List<String> regiaoEUf = verificarEstados(item);

                item.add(radioContrato);
                item.add(new Label("numeroContrato"));
                item.add(new Label("grupoItem", grupoItem));
                item.add(new Label("ufContrato", regiaoEUf.get(1)));
                item.add(new Label("regiaoContrato", regiaoEUf.get(0)));
                item.add(new Label("saldoContratado", MascaraUtils.formatarMascaraDinheiro(valorTotal)));
                item.add(new Label("saldoExecutar", MascaraUtils.formatarMascaraDinheiro(saldoAExecutar)));
            }
        };
        dataViewListaContrato.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataViewListaContrato;
    }

    public DataView<OrdemFornecimentoContrato> newDataViewOrdensDeFornecimento() {
        dataViewOrdemFornecimento = new DataView<OrdemFornecimentoContrato>("listaOrdemFornecimento", new ProviderOrdemFornecimento()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<OrdemFornecimentoContrato> item) {
                List<String> regiaoEUf = verificarString(item);

                String dataCadastro = DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataCadastro(), "dd/MM/yyyy HH:mm:ss");
                String datasComunicadas = " - ";
                String iconeInformativo = "";
                boolean ofComunicada = false;
                boolean mostrarBotaoExcluir = true;

                List<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> listaHistorico = buscarHistoricoComunicacao(item.getModelObject());

                try {
                    // Se a lista tiver o tamanho 1, então existe somente 1 data
                    if (listaHistorico.size() == 1) {
                        datasComunicadas = DataUtil.converteDataDeLocalDateParaString(listaHistorico.get(0).getDataComunicacao(), "dd/MM/yyyy HH:mm:ss");
                        ofComunicada = true;

                    } else {
                        datasComunicadas = DataUtil.converteDataDeLocalDateParaString(listaHistorico.get(0).getDataComunicacao(), "dd/MM/yyyy HH:mm:ss");

                        String datas = devolveDatasComunicadas(listaHistorico);
                        String cor = "green";

                        // Caso a última data de comunicação tiver sido
                        // cancelada.
                        if (listaHistorico.get(0).getPossuiCancelamento()) {
                            cor = "red";
                        } else {
                            cor = "green";
                        }

                        iconeInformativo = "<a type=\"button\" data-html=\"true\" data-toggle=\"popover\" title=\"Comunicações anteriores\" data-content=\"" + datas + "\" data-trigger=\"hover\"><i style=\"color:" + cor + ";\" class=\"fa fa-info-circle\"> </i></a>";
                        ofComunicada = true;
                    }

                    if (listaHistorico.size() > 0) {
                        // Caso a última data de comunicação tiver sido
                        // cancelada.
                        if (listaHistorico.get(0).getPossuiCancelamento()) {
                            datasComunicadas = datasComunicadas.concat("(Cancelada)");
                            mostrarBotaoExcluir = false;
                        }
                    }

                } catch (Exception ex) {
                    // Exception
                }

                Label lblDescricao = new Label("lblComunicadas", iconeInformativo);
                lblDescricao.setEscapeModelStrings(false);
                lblDescricao.setOutputMarkupId(true);
                lblDescricao.setVisible(listaHistorico.size() > 1);
                item.add(lblDescricao);

                item.add(new Label("numeroOf", item.getModelObject().getId()));
                item.add(new Label("itemOf", regiaoEUf.get(0)));
                item.add(new Label("ufOf", regiaoEUf.get(1)));
                item.add(new Label("municipioOf", regiaoEUf.get(2)));
                item.add(new Label("dataCadastro", dataCadastro));
                item.add(new Label("datasComunicadas", datasComunicadas));

                item.add(newButtonEditar(item).setVisible(mostrarBotaoExcluir)); // btnEditar
                item.add(newButtonVisualizar(item).setVisible(ofComunicada && !mostrarBotaoExcluir)); // btnEditar
                item.add(newButtonGerarMinuta(item)); // btnGerarMinuta
                item.add(newButtonComunicarFornecedor(item).setVisible(!ofComunicada)); // btnComunicar

                item.add(newButtonExcluir(item).setVisible(!ofComunicada)); // btnExcluir
                item.add(newButtonCancelarOf(item).setVisible(ofComunicada && mostrarBotaoExcluir)); // btnCancelarOf
            }
        };
        dataViewOrdemFornecimento.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataViewOrdemFornecimento;
    }

    private String devolveDatasComunicadas(List<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> lista) {

        String data = "";
        int flag = 0;
        for (HistoricoComunicacaoGeracaoOrdemFornecimentoContrato hist : lista) {
            if (flag == 0) {
                flag++;
                continue;
            }

            data = data.concat(DataUtil.converteDataDeLocalDateParaString(hist.getDataComunicacao(), "dd/MM/yyyy HH:mm:ss"));
            data = data.concat("<br />");
        }
        return data;
    }

    private InfraAjaxConfirmButton newButtonExcluir(Item<OrdemFornecimentoContrato> item) {
        InfraAjaxConfirmButton btnExcluir = componentFactory.newAJaxConfirmButton("btnExcluir", "MN053", form, (target, formz) -> excluir(target, item));
        return btnExcluir;
    }

    private InfraAjaxFallbackLink<Void> newButtonConfirmarCancelamentoOf(OrdemFornecimentoContrato ordemFornecimento) {
        return componentFactory.newAjaxFallbackLink("btnConfirmarCancelamentoOf", (target) -> actionConfirmarCancelamentoOf(target, ordemFornecimento));
    }

    private InfraAjaxFallbackLink<Void> newButtonCancelarCancelamentoOf() {
        return componentFactory.newAjaxFallbackLink("btnCancelarCancelamentoOf", (target) -> actionFecharAvisoCancelamentoOf(target));
    }

    private InfraAjaxFallbackLink<Void> newButtonCancelarOf(Item<OrdemFornecimentoContrato> item) {
        return componentFactory.newAjaxFallbackLink("btnCancelarOf", (target) -> actionCancelarOf(item, target));
    }

    private InfraAjaxFallbackLink<Void> newButtonGerarMinuta(Item<OrdemFornecimentoContrato> item) {
        return componentFactory.newAjaxFallbackLink("btnGerarMinuta", (target) -> actionGerarMinuta(item));
    }

    private InfraAjaxFallbackLink<Void> newButtonNovo() {
        return componentFactory.newAjaxFallbackLink("btnAdicionarNovo", (target) -> adicionarNovo());
    }

    private InfraAjaxFallbackLink<Void> newButtonEditar(Item<OrdemFornecimentoContrato> item) {
        return componentFactory.newAjaxFallbackLink("btnEditar", (target) -> editarOrdemFornecimento(item));
    }
    
    private InfraAjaxFallbackLink<Void> newButtonVisualizar(Item<OrdemFornecimentoContrato> item) {
        return componentFactory.newAjaxFallbackLink("btnVisualizar", (target) -> visualizarOf(item));
    }

    private InfraAjaxFallbackLink<Void> newButtonComunicarFornecedor(Item<OrdemFornecimentoContrato> item) {
        return componentFactory.newAjaxFallbackLink("btnComunicar", (target) -> comunicarFornecedor(item));
    }

    // PROVIDER
    private class ProviderItensContrato extends SortableDataProvider<Contrato, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<Contrato> iterator(long first, long size) {

            List<Contrato> listaRetorno = new ArrayList<Contrato>();
            if (!listaDeContratoComTodosBensFormatados.isEmpty()) {
                int inicio = (int) first;
                int fim = (int) (first + size);

                if (fim > listaDeContratoComTodosBensFormatados.size()) {
                    fim = listaDeContratoComTodosBensFormatados.size();
                }
                for (int i = inicio; i < fim; i++) {
                    listaRetorno.add(listaDeContratoComTodosBensFormatados.get(i));
                }
            }

            return listaDeContratoComTodosBensFormatados.iterator();
        }

        @Override
        public long size() {
            return listaDeContratoComTodosBensFormatados.size();
        }

        @Override
        public IModel<Contrato> model(Contrato object) {
            return new CompoundPropertyModel<Contrato>(object);
        }
    }

    private class ProviderOrdemFornecimento extends SortableDataProvider<OrdemFornecimentoContrato, String> {
        private static final long serialVersionUID = 1L;

        public ProviderOrdemFornecimento() {
            if (contratoSelecionado != null && contratoSelecionado.getId() != null) {
                listaDeOrdemFornecimento = ordemFornecimentoContratoService.buscarOrdemFornecimentoContrato(contratoSelecionado);
            }
        }

        @Override
        public Iterator<OrdemFornecimentoContrato> iterator(long first, long size) {
            Collections.sort(listaDeOrdemFornecimento, OrdemFornecimentoContrato.getComparator("id"));
            return listaDeOrdemFornecimento.iterator();
        }

        @Override
        public long size() {
            if (contratoSelecionado != null && contratoSelecionado.getId() != null) {
                listaDeOrdemFornecimento = ordemFornecimentoContratoService.buscarOrdemFornecimentoContrato(contratoSelecionado);
                return listaDeOrdemFornecimento.size();
            } else {
                return 0;
            }
        }

        @Override
        public IModel<OrdemFornecimentoContrato> model(OrdemFornecimentoContrato object) {
            return new CompoundPropertyModel<OrdemFornecimentoContrato>(object);
        }
    }

    // AÇÕES

    private void voltar() {
        setResponsePage(backPage);
    }

    private void actionTextArea(TextArea field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                motivoCancelamento = textAreaMotivoCancelamento.getConvertedInput();

            }
        };
        field.add(onChangeAjaxBehavior);
    }

    private String receberFraseDeCancelamento() {
        String frase = "Esta Ordem de Fornecimento já foi comunicada e somente poderá ser cancelada. Se deseja realmente executar esta ação informe o motivo na caixa de texto abaixo.";
        return frase;
    }

    private List<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> buscarHistoricoComunicacao(OrdemFornecimentoContrato of) {
        List<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> historico = ordemFornecimentoContratoService.buscarHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(of, true);
        return historico;
    }

    private void excluir(AjaxRequestTarget target, Item<OrdemFornecimentoContrato> item) {
        ordemFornecimentoContratoService.excluir(item.getModelObject().getId());

        atualizarDataViewOrdemFornecimento(target, null);
        atualizarDataViewContrato(target);
    }

    public String formatoDinheiro(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance().format(valor);
    }

    private List<String> verificarEstados(Item<Contrato> item) {

        String stringUf = new String("");
        String stringRegiao = new String("");
        BigDecimal valorEstimado = new BigDecimal("0");

        List<Uf> uf = new ArrayList<Uf>();
        List<Regiao> reg = new ArrayList<Regiao>();

        List<AgrupamentoLicitacao> listaLicitacao = contratoService.buscarAgrupamentoLicitacao(item.getModelObject());

        for (AgrupamentoLicitacao agrup : listaLicitacao) {

            List<SelecaoItem> listaItem = licitacaoProgramaService.buscarSelecaoItem(agrup);
            for (SelecaoItem si : listaItem) {

                List<BemUf> listUf = new ArrayList<BemUf>();
                listUf = licitacaoProgramaService.buscarBemUf(si);

                // Busca pela Uf, para ver se já foi adicionada a lista
                // temporaria de ufs.
                for (BemUf bemuf : listUf) {
                    if (!uf.contains(bemuf.getUf())) {
                        uf.add(bemuf.getUf());

                        if (!stringUf.equalsIgnoreCase("")) {
                            stringUf = stringUf.concat(" / ");
                        }
                        stringUf = stringUf.concat(" ").concat(bemuf.getUf().getSiglaUf()).concat(" ");
                    }
                }

                // Verifica se a região já foi adicionada a lista temporaria de
                // regiões.
                for (BemUf bemuf : listUf) {
                    if (!reg.contains(bemuf.getUf().getRegiao())) {
                        reg.add(bemuf.getUf().getRegiao());

                        if (!stringRegiao.equalsIgnoreCase("")) {
                            stringRegiao = stringRegiao.concat(" / ");
                        }
                        stringRegiao = stringRegiao.concat(" ").concat(bemuf.getUf().getRegiao().getSiglaRegiao()).concat(" ");
                    }
                }

                // Ira somar o valor total de todos os bens
                for (BemUf bemuf : listUf) {
                    valorEstimado = valorEstimado.add(bemuf.getBem().getValorEstimadoBem());
                }

            }
        }

        List<String> regiaoEUf = new ArrayList<String>();
        regiaoEUf.add(stringRegiao);
        regiaoEUf.add(stringUf);
        regiaoEUf.add(formatoDinheiro(valorEstimado));
        return regiaoEUf;
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

    // retorna o nome do BEM/GRUPO
    private String verificarGrupoItem(Item<Contrato> item) {
        String grupoItem = "";

        List<AgrupamentoLicitacao> listaLicitacao = contratoService.buscarAgrupamentoLicitacao(item.getModelObject());

        Collections.sort(listaLicitacao, AgrupamentoLicitacao.getComparator(1, "tipo"));
        for (AgrupamentoLicitacao agrup : listaLicitacao) {

            if (!grupoItem.equalsIgnoreCase("")) {
                grupoItem = grupoItem + "/ ";
            }
            grupoItem += " " + agrup.getNomeAgrupamento() + " ";
        }

        return grupoItem;
    }

    private void exibirListaItens(Item<Contrato> item, AjaxRequestTarget target, BigDecimal saldoAExecutar) {
        contratoSelecionado = item.getModelObject();
        atualizarDataViewOrdemFornecimento(target, saldoAExecutar);
    }

    private void atualizarDataViewContrato(AjaxRequestTarget target) {
        panelListaDeContratos.addOrReplace(newDataViewListaContrato());
        target.add(panelListaDeContratos);
    }

    private void atualizarDataViewOrdemFornecimento(AjaxRequestTarget target, BigDecimal saldoAExecutar) {

        if (saldoAExecutar != null && saldoAExecutar.doubleValue() == 0) {
            containerBotaoNovaOf.setVisible(false);
        } else {
            containerBotaoNovaOf.setVisible(true);
        }

        panelListaDeOfs.addOrReplace(newDataViewOrdensDeFornecimento());
        panelListaDeOfs.addOrReplace(new InfraAjaxPagingNavigator("paginationOf", dataViewOrdemFornecimento));
        panelListaDeOfs.setVisible(true);
        target.add(panelListaDeOfs);
        target.add(containerBotaoNovaOf);
    }

    private void actionGerarMinuta(Item<OrdemFornecimentoContrato> item) {
        setResponsePage(new MinutaOrdemFornecimentoPage(getPageParameters(), programa, item.getModelObject(), this, false));
    }

    private void actionCancelarOf(Item<OrdemFornecimentoContrato> item, AjaxRequestTarget target) {
        panelListaDeOfs.setVisible(false);
        panelPrincipalCancelarOf.setVisible(true);
        motivoCancelamento = "";

        panelPrincipalCancelarOf.addOrReplace(new PanelCancelarOf("panelCancelarOf", item));
        panelCancelarOf.setVisible(true);

        target.add(panelListaDeOfs);
        target.add(panelPrincipalCancelarOf);
    }

    private void actionConfirmarCancelamentoOf(AjaxRequestTarget target, OrdemFornecimentoContrato ordemFornecimento) {

        if (!validarCancelamento(target)) {
            return;
        }

        ordemFornecimentoContratoService.cancelarComunicacao(ordemFornecimento, motivoCancelamento,getIdentificador());
        List<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> list = ordemFornecimentoContratoService.buscarHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(ordemFornecimento, true);
        mailService.enviarEmailQuandoOrdemFornecimentoCancelada(ordemFornecimento, list.get(0));

        motivoCancelamento = "";
        panelPrincipalCancelarOf.setVisible(false);
        panelListaDeOfs.setVisible(true);
        target.add(panelPrincipalCancelarOf, panelListaDeOfs);
    }

    private boolean validarCancelamento(AjaxRequestTarget target) {

        String msg = "";
        boolean validar = true;

        if (motivoCancelamento == null || "".equalsIgnoreCase(motivoCancelamento)) {
            msg += "<p><li> O campo 'Motivo do Cancelamento' é obrigatório.</li><p />";
            validar = false;
        }

        if (!validar) {
            mensagem.setObject(msg);
        } else {
            mensagem.setObject("");
        }
        target.add(labelMensagem);
        return validar;
    }

    private void actionFecharAvisoCancelamentoOf(AjaxRequestTarget target) {
        panelPrincipalCancelarOf.setVisible(false);
        panelListaDeOfs.setVisible(true);

        target.add(panelPrincipalCancelarOf);
        target.add(panelListaDeOfs);
    }

    private void adicionarNovo() {
        if (contratoSelecionado == null) {
            addMsgError("Selecione um contrato.");
            return;
        }
        OrdemFornecimentoContrato ordem = new OrdemFornecimentoContrato();
        ordem.setContrato(contratoSelecionado);
        setResponsePage(new OrdemFornecimentoPage(getPageParameters(), ordem, contratoSelecionado, this));
    }

    private void editarOrdemFornecimento(Item<OrdemFornecimentoContrato> item) {
        setResponsePage(new OrdemFornecimentoPage(getPageParameters(), item.getModelObject(), contratoSelecionado, this));
    }
    
    private void visualizarOf(Item<OrdemFornecimentoContrato> item){
    	setResponsePage(new OrdemFornecimentoPage(getPageParameters(), item.getModelObject(), contratoSelecionado, this));
    }

    private void comunicarFornecedor(Item<OrdemFornecimentoContrato> item) {
        setResponsePage(new ComunicarFornecedorPage(new PageParameters(), programa, item.getModelObject(), this));
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
}
