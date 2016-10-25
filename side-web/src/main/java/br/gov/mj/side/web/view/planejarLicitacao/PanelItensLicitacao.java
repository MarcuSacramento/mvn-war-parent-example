package br.gov.mj.side.web.view.planejarLicitacao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
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
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.apoio.entidades.Regiao;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.converter.MoneyBigDecimalConverter;
import br.gov.mj.infra.wicket.listener.FeedbackPanelListener;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.enums.EnumTipoAgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.BemUf;
import br.gov.mj.side.entidades.programa.licitacao.LicitacaoPrograma;
import br.gov.mj.side.entidades.programa.licitacao.SelecaoItem;
import br.gov.mj.side.web.dto.BemUfDto;
import br.gov.mj.side.web.dto.ItemBemDto;
import br.gov.mj.side.web.dto.LicitacaoNomeModelDto;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.LicitacaoProgramaService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.components.validators.CustomRangeValidator;

public class PanelItensLicitacao extends Panel {
    private static final long serialVersionUID = 1L;

    /*
     * Constants
     */
    private static final String CLASS = "class";
    private static final String ONCHANGE = "onchange";
    private static final String ONKEYUP = "onkeyup";

    private PanelPrimeiro panelPrimeiro;
    private PanelBotoes panelBotoes;
    private PanelBotoesGrupoSelecionados panelBotoesGrupoSelecionados;
    private PanelItensSelecionados panelItensSelecionados;
    private PanelGruposSelecionados panelGruposSelecionados;

    private DataView<BemUfDto> dataViewPrimeiraLista;
    private DataView<ItemBemDto> dataViewGruposSelecionados;
    private DataView<ItemBemDto> dataViewItensSelecionados;
    private InfraAjaxPagingNavigator paginatorGrupo;

    private AjaxCheckBox checkAdicionarBem;
    private AjaxCheckBox checkAdicionarTodosBem;
    private Label labelMensagem;
    private Label labelMensagemGrupo;
    private Label labelMensagemItem;
    private Model<String> mensagem = Model.of("");
    private Model<String> mensagemGrupo = Model.of("");
    private Model<String> mensagemItem = Model.of("");

    private Label msgItemGrupoExcluir;
    private Model<String> modalItemGrupoExcluir = Model.of("");

    private LicitacaoPrograma licitacaoPrograma;
    private Boolean readOnly;
    private Integer itensPorPaginaPrimeiro = 5;
    private Integer itensPorPaginaItensSelecionados = 5;
    private Integer itensPorPaginaGruposSelecionados = 5;
    private Integer contadorItensAdicionados = 1;
    private Integer contadorGruposAdicionados = 1;
    private Integer contadorDeGruposComMesmoNome = 2;
    private Boolean selecionarTodosBens;
    private boolean primeiroLoop = true;
    private Integer contadorDeItensCriados = 1;

    // Campos que ordenam
    private String coluna = "nome";
    private AttributeAppender classArrow = new AttributeAppender(CLASS, "mj_arrow_asc", " ");
    private AttributeAppender classArrowUnsorted = new AttributeAppender(CLASS, "mj_arrow_unsorted", " ");
    private int order = 1;

    private List<AgrupamentoLicitacao> listaAgrupamentosSalvas = new ArrayList<AgrupamentoLicitacao>();
    private List<BemUfDto> listaLocaisEntrega = new ArrayList<BemUfDto>();
    private List<BemUfDto> listaTemporariaDeBensSelecionados = new ArrayList<BemUfDto>();
    private List<BemUfDto> listaComDadosOriginaisBemUfDto = new ArrayList<BemUfDto>();
    private List<BemUfDto> listaBensJaSelecionados = new ArrayList<BemUfDto>();

    private List<ItemBemDto> listaDeItens = new ArrayList<ItemBemDto>();
    private List<ItemBemDto> listaDeGrupos = new ArrayList<ItemBemDto>();

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private InscricaoProgramaService inscricaoService;

    @Inject
    private ProgramaService programaService;

    @Inject
    LicitacaoProgramaService licitacaoService;

    public PanelItensLicitacao(String id, List<BemUfDto> listaLocaisEntrega, LicitacaoPrograma licitacaoPrograma, Boolean readOnly) {
        super(id);
        setOutputMarkupId(true);
        this.listaLocaisEntrega = listaLocaisEntrega;
        this.licitacaoPrograma = licitacaoPrograma;
        this.readOnly = readOnly;

        initVariaveis();

        add(panelPrimeiro = new PanelPrimeiro("panelPrimeiro"));
        add(panelGruposSelecionados = new PanelGruposSelecionados("panelGruposSelecionados"));
        add(panelItensSelecionados = new PanelItensSelecionados("panelItensSelecionados"));
        add(panelBotoes = new PanelBotoes("panelBotoes"));

        labelMensagem = new Label("mensagemVincular", mensagem);
        labelMensagem.setEscapeModelStrings(false);
        labelMensagem.setOutputMarkupId(true);
        add(labelMensagem);
    }

    private void initVariaveis() {
        if (licitacaoPrograma != null && licitacaoPrograma.getId() != null) {
            montarListasDeItensEGruposSalvas();
            atualizarBensDaPrimeiroTabelaAoAdicionar();
            listaLocaisEntrega.clear();

            contadorItensAdicionados = listaDeItens.size();
            contadorGruposAdicionados = listaDeGrupos.size();
        }

        copiarConteudoDaListaDeLocaisDeEntrega();
    }

    private void montarListasDeItensEGruposSalvas() {
        listaAgrupamentosSalvas = licitacaoService.buscarAgrupamentoLicitacao(licitacaoPrograma);

        LicitacaoNomeModelDto nome = new LicitacaoNomeModelDto();
        String nomeAnterior = "";
        String nomeGrupoAnterior = "";
        int flag = 0;

        for (AgrupamentoLicitacao agrup : listaAgrupamentosSalvas) {
            if (agrup.getTipoAgrupamentoLicitacao() == EnumTipoAgrupamentoLicitacao.GRUPO) {

                contadorDeItensCriados++;

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

                    itemBem.setNomeModelDto(nome);
                    itemBem.setQuantidadeImediata(si.getQuantidadeImediata());
                    itemBem.setTipoAgrupamentoLicitacao(agrup.getTipoAgrupamentoLicitacao());
                    itemBem.setUnidadeDeMedida(si.getUnidadeMedida());
                    itemBem.setValorTotalImediato(si.getValorTotalImediato());
                    itemBem.setValorTotalRegistrar(si.getValorTotalARegistrar());
                    itemBem.setValorUnitario(si.getValorUnitario());
                    itemBem.setAgrupamentoLicitacao(si.getAgrupamentoLicitacao());
                    itemBem.setContrato(agrup.getContrato());

                    List<BemUf> listBem = licitacaoService.buscarBemUf(si);
                    for (BemUf bemUf : listBem) {
                        BemUfDto buf = new BemUfDto();
                        buf.setBem(bemUf.getBem());
                        buf.setQuantidade(bemUf.getQuantidade());
                        buf.setUf(bemUf.getUf());
                        buf.setSelecaoItem(si);
                        buf.setId(bemUf.getId());

                        listaBensJaSelecionados.add(buf);
                        listaBem.add(buf);
                    }
                    itemBem.setListaDeBens(listaBem);
                    listaDeGrupos.add(itemBem);
                }
                flag = 0;
            } else {

                contadorDeItensCriados++;

                ItemBemDto itemBem = new ItemBemDto();
                LicitacaoNomeModelDto nomeItem = new LicitacaoNomeModelDto();

                itemBem.setContadorTemp(contadorDeItensCriados);
                nomeItem.setNomeGrupo(agrup.getNomeAgrupamento());
                itemBem.setContadorTemp(contadorDeItensCriados);

                List<BemUfDto> listaBem = new ArrayList<BemUfDto>();
                List<SelecaoItem> listaSelecaoItem = licitacaoService.buscarSelecaoItem(agrup);

                for (SelecaoItem si : listaSelecaoItem) {

                    itemBem.setNomeModelDto(nomeItem);
                    itemBem.setQuantidadeImediata(si.getQuantidadeImediata());
                    itemBem.setTipoAgrupamentoLicitacao(agrup.getTipoAgrupamentoLicitacao());
                    itemBem.setUnidadeDeMedida(si.getUnidadeMedida());
                    itemBem.setValorTotalImediato(si.getValorTotalImediato());
                    itemBem.setValorTotalRegistrar(si.getValorTotalARegistrar());
                    itemBem.setValorUnitario(si.getValorUnitario());
                    itemBem.setAgrupamentoLicitacao(si.getAgrupamentoLicitacao());
                    itemBem.setContrato(agrup.getContrato());

                    List<BemUf> listBem = licitacaoService.buscarBemUf(si);
                    for (BemUf bemUf : listBem) {
                        BemUfDto buf = new BemUfDto();
                        buf.setBem(bemUf.getBem());
                        buf.setQuantidade(bemUf.getQuantidade());
                        buf.setUf(bemUf.getUf());
                        buf.setSelecaoItem(si);
                        buf.setId(bemUf.getId());

                        listaBensJaSelecionados.add(buf);
                        listaBem.add(buf);
                    }
                    itemBem.setListaDeBens(listaBem);
                }
                listaDeItens.add(itemBem);
            }
        }
    }

    /*
     * ABAIXO VIRÃO OS PAINEIS
     */

    public class PanelPrimeiro extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelPrimeiro(String id) {
            super(id);
            setOutputMarkupId(true);
            add(newDataViewPrimeiraLista()); // dataLocaisEntrega
            add(newCheckAdicionarTodosBens()); // checkAdicionartodosBens
            add(newPaginatorPrimeiraLista()); // paginationPrimeiro
            add(newDropItensPorPaginaPrimeiro()); // itensPorPaginaPrimeiro

            add(newButtonOrdenarNome()); // btnOrdenarNome
            add(newButtonOrdenarRegiao()); // btnOrdenarRegiao
            add(newButtonOrdenarUf()); // btnOrdenarUf
            add(newButtonOrdenarQuantidade()); // btnOrdenarQuantidade

            add(newLabelSortNome()); // lblOrderNome
            add(newLabelSortRegiao()); // lblOrderRegiao
            add(newLabelSortUf()); // lblOrderUf
            add(newLabelSortQuantidade()); // lblOrderQuantidade
        }
    }

    private class PanelGruposSelecionados extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelGruposSelecionados(String id) {
            super(id);
            setOutputMarkupId(true);
            add(newDataViewGruposSelecionados()); // dataGruposSelecionados

            labelMensagemGrupo = new Label("mensagemVincularGrupo", mensagemGrupo);
            labelMensagemGrupo.setEscapeModelStrings(false);
            labelMensagemGrupo.setOutputMarkupId(true);
            add(labelMensagemGrupo);
        }
    }

    private class PanelItensSelecionados extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelItensSelecionados(String id) {
            super(id);
            setOutputMarkupId(true);
            add(newDataViewItensSelecionados()); // dataItensSelecionados

            labelMensagemItem = new Label("mensagemVincularItem", mensagemItem);
            labelMensagemItem.setEscapeModelStrings(false);
            labelMensagemItem.setOutputMarkupId(true);
            add(labelMensagemItem);
        }
    }

    private class PanelBotoes extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBotoes(String id) {
            super(id);
            setOutputMarkupId(true);
            add(newButtonAdicionar()); // btnAdicionar
        }
    }

    private class PanelBotoesGrupoSelecionados extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBotoesGrupoSelecionados(String id, Item<ItemBemDto> item) {
            super(id);
            setOutputMarkupId(true);
            add(newButtonRemoverGrupo(item)); // btnAdicionar
            add(newButtonInformacao(item)); // btnAdicionar
        }
    }

    /*
     * PAINEIS UTILIZADOS PARA PERMITIR QUE O USUÁRIO INFORME OS VALORES NOS
     * TEXTFIELDS DOS DATAVIEWS.
     */

    // Panel para informar o nome do item
    private class PanelNomeItem extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelNomeItem(String id, Item<ItemBemDto> item) {
            super(id);
            setOutputMarkupId(true);
            add(newTextFieldNomeItem(item)); // txtNomeBem
            setEnabled(botoesTabelaEnabled());
        }
    }

    private class PanelUnidadeMedida extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelUnidadeMedida(String id, Item<ItemBemDto> item) {
            super(id);
            setOutputMarkupId(true);
            add(newTextFieldUnidadeMedida(item)); // txtUnidadeMedida
            setEnabled(botoesTabelaEnabled());
        }
    }

    // Panel para informar a quantidade a
    private class PanelQuantidadeImediata extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelQuantidadeImediata(String id, Item<ItemBemDto> item, Integer quantidade) {
            super(id);
            setOutputMarkupId(true);
            add(newTextFieldQuantidadeImediata(item, quantidade)); // txtImediata
            setEnabled(botoesTabelaEnabled());
        }
    }

    // Panel para informar o nome do item
    private class PanelValorUnitario extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelValorUnitario(String id, Item<ItemBemDto> item, Integer quantidade) {
            super(id);
            setOutputMarkupId(true);
            add(newTextFieldNomeValorUnitario(item, quantidade)); // txtValorUnitario
            setEnabled(botoesTabelaEnabled());
        }
    }

    // Panel para informar a quantidade Total
    private class PanelValorTotalImediato extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelValorTotalImediato(String id, Item<ItemBemDto> item, Integer quantidade) {
            super(id);
            setOutputMarkupId(true);
            add(newTextFieldNomeValorTotalImediato(item, quantidade)); // txtValorTotalImediato
            setEnabled(botoesTabelaEnabled());
        }
    }

    // Panel para informar a quantidade Total
    private class PanelValorTotalRegistrar extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelValorTotalRegistrar(String id, Item<ItemBemDto> item, Integer quantidade) {
            super(id);
            setOutputMarkupId(true);
            add(newTextFieldNomeValorTotalRegistrar(item, quantidade)); // txtValorTotalRegistrar
            setEnabled(botoesTabelaEnabled());
        }
    }

    /*
     * ABAIXO VIRÃO OS COMPONENTES
     */

    private DropDownChoice<Integer> newDropItensPorPaginaiItensSelecionados() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPaginaItensSelecionados", new LambdaModel<Integer>(this::getItensPorPaginaItensSelecionados, this::setItensPorPaginaItensSelecionados), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewItensSelecionados.setItemsPerPage(getItensPorPaginaItensSelecionados());
                target.add(panelItensSelecionados);
            };
        });
        return dropDownChoice;
    }

    private DropDownChoice<Integer> newDropItensPorPaginaPrimeiro() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPaginaPrimeiro", new LambdaModel<Integer>(this::getItensPorPaginaPrimeiro, this::setItensPorPaginaPrimeiro), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewPrimeiraLista.setItemsPerPage(getItensPorPaginaPrimeiro());
                target.add(panelPrimeiro);
            };
        });
        return dropDownChoice;
    }

    private DropDownChoice<Integer> newDropItensPorPaginaGrupo() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPaginaGrupo", new LambdaModel<Integer>(this::getItensPorPaginaGruposSelecionados, this::setItensPorPaginaGruposSelecionados), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewGruposSelecionados.setItemsPerPage(getItensPorPaginaGruposSelecionados());
                target.add(panelGruposSelecionados);
            };
        });
        return dropDownChoice;
    }

    public DataView<BemUfDto> newDataViewPrimeiraLista() {
        dataViewPrimeiraLista = new DataView<BemUfDto>("dataLocaisEntrega", new ProviderPrimeiraLista()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<BemUfDto> item) {
                item.clearOriginalDestination();

                item.add(newCheckAdicionarBem(item));
                item.add(new Label("nomeDoBem", item.getModelObject().getBem().getNomeBem()));
                item.add(new Label("regiaoBem", item.getModelObject().getUf().getRegiao().getNomeRegiao()));
                item.add(new Label("ufBem", item.getModelObject().getUf().getNomeSigla()));
                item.add(new Label("quantidadeBem", item.getModelObject().getQuantidade().intValue()));
            }
        };
        dataViewPrimeiraLista.setOutputMarkupId(true);
        dataViewPrimeiraLista.setItemsPerPage(getItensPorPaginaPrimeiro());
        return dataViewPrimeiraLista;
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

                String descricao = item.getModelObject().getListaDeBens().get(0).getBem().getDescricaoBem();
                String iconeInformativo = "<a type=\"button\" data-toggle=\"popover\" title=\"Descrição\" data-content=\"" + descricao + "\" data-trigger=\"hover\"><i style=\"color:green;\" class=\"fa fa-info-circle\"> </i></a>";

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

                int tamanho = quantidadeItensMesclar(clicadoPaginator, idGrupoAtual, item);
                clicadoPaginator = false;

                if (primeiroLoop || item.getModelObject().getContadorTemp() != idGrupoAnterior) {
                    containerRowspan.add(new AttributeAppender("rowspan", new Model<Integer>(tamanho)));
                    containerBotao.add(new AttributeAppender("rowspan", new Model<Integer>(tamanho)));
                    primeiroLoop = false;
                } else {
                    containerRowspan.setVisible(false);
                    containerBotao.setVisible(false);
                }

                PanelNomeItem panel = new PanelNomeItem("panelNomeGrupo", item);
                containerRowspan.add(panel);
                item.add(containerRowspan);

                item.add(new Label("lblNomeBemGrupo", item.getModelObject().getListaDeBens().get(0).getBem().getNomeBem()));

                Label lblDescricao = new Label("lblDescricaoBemGrupo", iconeInformativo);
                lblDescricao.setEscapeModelStrings(false);
                lblDescricao.setOutputMarkupId(true);
                item.add(lblDescricao);

                item.add(new Label("lblRegiaoGrupo", regiao));
                item.add(new Label("lblUfGrupo", uf));
                item.add(new PanelUnidadeMedida("panelUnidadeMedidaItem", item));
                item.add(new Label("lblQuantidadeARegistrarGrupo", quantidade));
                item.add(new PanelQuantidadeImediata("panelQuantidadeImediataGrupo", item, quantidade));
                item.add(new PanelValorUnitario("panelValorUnitario", item, quantidade));
                item.add(new PanelValorTotalImediato("panelValorTotalImediato", item, quantidade));
                item.add(new PanelValorTotalRegistrar("panelValorTotalRegistrar", item, quantidade));

                PanelBotoesGrupoSelecionados panelBotoes = new PanelBotoesGrupoSelecionados("panelBotoes", item);
                containerBotao.add(panelBotoes);
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

    private DataView<ItemBemDto> newDataViewItensSelecionados() {
        dataViewItensSelecionados = new DataView<ItemBemDto>("dataItensSelecionados", new ProviderItensSelecionados(listaDeItens)) {
            private static final long serialVersionUID = 1L;
            private long idAnterior = 0;

            @Override
            protected void populateItem(Item<ItemBemDto> item) {

                String iconeInformativo = "<a type=\"button\" data-toggle=\"popover\" title=\"Descrição\" data-content=\"".concat(item.getModelObject().getListaDeBens().get(0).getBem().getDescricaoBem()) + "\" data-trigger=\"hover\"><i style=\"color:green;\" class=\"fa fa-info-circle\"> </i></a>";

                String regiao = verificarRegioes(item);
                String uf = verificarUfs(item);
                Integer quantidade = somarQuantidadeItens(item);

                item.add(new PanelNomeItem("panelNomeItem", item));
                item.add(new Label("lblNomeBemItem", item.getModelObject().getListaDeBens().get(0).getBem().getNomeBem()));
                item.add(new Label("lblDescricaoBemItem", new Model<String>(iconeInformativo)).setEscapeModelStrings(false));
                item.add(new Label("lblRegiaoItem", regiao));
                item.add(new Label("lblUfItem", uf));
                item.add(new PanelUnidadeMedida("panelUnidadeMedidaItem", item));
                item.add(new Label("lblQuantidadeARegistrarItem", quantidade));
                item.add(new PanelQuantidadeImediata("panelQuantidadeImediata", item, quantidade));
                item.add(new PanelValorUnitario("panelValorUnitario", item, quantidade));
                item.add(new PanelValorTotalImediato("panelValorTotalImediato", item, quantidade));
                item.add(new PanelValorTotalRegistrar("panelValorTotalRegistrar", item, quantidade));
                item.add(newButtonRemoverItem(item)); // btnRemoverItem
                item.add(newButtonInformacao(item)); // btnInformacao
            }
        };
        dataViewItensSelecionados.setOutputMarkupId(true);
        return dataViewItensSelecionados;
    }

    public InfraAjaxPagingNavigator newPaginatorPrimeiraLista() {
        return new InfraAjaxPagingNavigator("paginationPrimeiro", dataViewPrimeiraLista);
    }

    private AjaxCheckBox newCheckAdicionarTodosBens() {

        checkAdicionarTodosBem = new AjaxCheckBox("checkAdicionartodosBens", new PropertyModel<Boolean>(this, "selecionarTodosBens")) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                acaoCheckAdicionarTodos(target);

                String msg = "";
                mensagem.setObject(msg);
                target.add(labelMensagem);
            }
        };
        checkAdicionarTodosBem.setOutputMarkupId(true);
        checkAdicionarTodosBem.setEnabled(botoesTabelaEnabled());
        return checkAdicionarTodosBem;
    }

    private AjaxCheckBox newCheckAdicionarBem(Item<BemUfDto> item) {

        checkAdicionarBem = new AjaxCheckBox("checkAdicionarBem", new PropertyModel<Boolean>(item.getModelObject(), "selecionado")) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                acaoCheck(item, target);

                String msg = "";
                mensagem.setObject(msg);
                target.add(labelMensagem);
            }
        };
        checkAdicionarBem.setOutputMarkupId(true);
        checkAdicionarBem.setEnabled(botoesTabelaEnabled());
        return checkAdicionarBem;
    }

    private TextField<String> newTextFieldNomeItem(Item<ItemBemDto> item) {
        TextField<String> field = new TextField<String>("txtNomeBem", new PropertyModel<String>(item.getModelObject(), "nomeModelDto.nomeGrupo"));
        field.add(StringValidator.maximumLength(200));
        acaoTextFieldSetarModelNomeItemGrupo(field, item);
        field.setEnabled(item.getModelObject().getContrato() == null);
        return field;
    }

    private boolean verificarSeJaExisteEsteNomeDigitado(Item<ItemBemDto> item) {
        boolean validar = true;
        String nome = item.getModelObject().getNomeModelDto().getNomeGrupo();

        if (item.getModelObject().getTipoAgrupamentoLicitacao() == EnumTipoAgrupamentoLicitacao.GRUPO) {
            for (ItemBemDto grupo : listaDeGrupos) {
                if (nome.equalsIgnoreCase(grupo.getNomeModelDto().getNomeGrupo())) {

                    // Irá verificar se este nome que sendo iterado é o mesmo
                    // que esta querendo ser modificado, somente podera ser
                    // alterado se não for o mesmo
                    if (oItemDigitandoNaoEOMesmoASerIterado(item, grupo)) {
                        validar = false;
                        break;
                    }
                }
            }
        } else {
            for (ItemBemDto grupo : listaDeItens) {
                if (nome.equalsIgnoreCase(grupo.getNomeModelDto().getNomeGrupo())) {
                    if (oItemDigitandoNaoEOMesmoASerIterado(item, grupo)) {
                        validar = false;
                        break;
                    }
                }
            }
        }

        return validar;
    }

    // Ação setada como generica pois servirá de parametro para atributos do
    // tipo String, Long e BigDecimal
    private void acaoTextFieldSetarModelNomeItemGrupo(TextField<?> text, Item<ItemBemDto> item) {
        text.add(new AjaxFormComponentUpdatingBehavior(ONKEYUP) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no model
            }
        });
    }

    // Ação setada como generica pois servirá de parametro para atributos do
    // tipo String, Long e BigDecimal
    private void acaoTextFieldSetarModel(TextField<?> text) {
        text.add(new AjaxFormComponentUpdatingBehavior(ONKEYUP) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no model
            }
        });
    }

    private void acaoTextFieldQuantidadeImediata(TextField<?> text, Item<ItemBemDto> item, Integer quantidade) {
        text.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                RequestCycle.get().setMetaData(FeedbackPanelListener.SKIP_FEEDBACK_LISTENER, Boolean.TRUE);

                BigDecimal qtdImediata = new BigDecimal(item.getModelObject().getQuantidadeImediata() == null ? "0" : item.getModelObject().getQuantidadeImediata().toString());
                BigDecimal valorUnitario = new BigDecimal(item.getModelObject().getValorUnitario() == null ? "0" : item.getModelObject().getValorUnitario().toString());

                String msgGrupo = "";
                String msgItem = "";
                if (item.getModelObject().getTipoAgrupamentoLicitacao() == EnumTipoAgrupamentoLicitacao.GRUPO && qtdImediata.intValue() > quantidade) {
                    msgGrupo += "<p><li> A 'Quantidade Imediata' não pode ser maior do que a 'Quantidade a Registrar'.</li><p />";
                } else {
                    if (qtdImediata.intValue() > quantidade) {
                        msgItem += "<p><li> A 'Quantidade Imediata' não pode ser maior do que a 'Quantidade a Registrar'.</li><p />";
                    }
                }

                mensagemGrupo.setObject(msgGrupo);
                mensagemItem.setObject(msgItem);
                target.add(labelMensagemItem);
                target.add(labelMensagemGrupo);

                if (!"".equalsIgnoreCase(msgItem)) {
                    return;
                }

                BigDecimal valorTotalImediato = qtdImediata.multiply(valorUnitario);
                item.getModelObject().setValorTotalImediato(valorTotalImediato);

                if (item.getModelObject().getTipoAgrupamentoLicitacao() == EnumTipoAgrupamentoLicitacao.GRUPO) {
                    panelGruposSelecionados.addOrReplace(dataViewGruposSelecionados);
                    target.add(panelGruposSelecionados);
                } else {
                    panelItensSelecionados.addOrReplace(dataViewItensSelecionados);
                    target.add(panelItensSelecionados);
                }
            }
        });
    }

    private TextField<String> newTextFieldUnidadeMedida(Item<ItemBemDto> item) {
        TextField<String> field = new TextField<String>("txtUnidadeMedida", new PropertyModel<String>(item.getModelObject(), "unidadeDeMedida"));
        field.add(StringValidator.maximumLength(100));
        acaoTextFieldSetarModel(field);
        field.setEnabled(item.getModelObject().getContrato() == null);
        return field;
    }

    private TextField<Long> newTextFieldQuantidadeImediata(Item<ItemBemDto> item, Integer quantidade) {
        TextField<Long> field = new TextField<Long>("txtImediata", new PropertyModel<Long>(item.getModelObject(), "quantidadeImediata"));
        acaoTextFieldQuantidadeImediata(field, item, quantidade);
        field.setEnabled(item.getModelObject().getContrato() == null);
        return field;
    }

    private TextField<BigDecimal> newTextFieldNomeValorUnitario(Item<ItemBemDto> item, Integer quantidade) {
        TextField<BigDecimal> field = new TextField<BigDecimal>("valorUnitario") {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
        field.setLabel(Model.of("Valor Unitário"));
        field.setRequired(false);
        field.add(new CustomRangeValidator(new BigDecimal("0.00"), new BigDecimal("999999999999.99")));

        field.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                RequestCycle.get().setMetaData(FeedbackPanelListener.SKIP_FEEDBACK_LISTENER, Boolean.TRUE);

                BigDecimal qtdImediata = new BigDecimal(item.getModelObject().getQuantidadeImediata() == null ? "0" : item.getModelObject().getQuantidadeImediata().toString());
                BigDecimal qtdRegistrar = new BigDecimal(quantidade);
                BigDecimal valorUnitario = new BigDecimal(item.getModelObject().getValorUnitario() == null ? "0" : item.getModelObject().getValorUnitario().toString());

                BigDecimal valorTotalImediato = qtdImediata.multiply(valorUnitario);
                item.getModelObject().setValorTotalImediato(valorTotalImediato);

                BigDecimal valorTotalRegistrar = qtdRegistrar.multiply(valorUnitario);
                item.getModelObject().setValorTotalRegistrar(valorTotalRegistrar);

                if (item.getModelObject().getTipoAgrupamentoLicitacao() == EnumTipoAgrupamentoLicitacao.GRUPO) {
                    panelGruposSelecionados.addOrReplace(dataViewGruposSelecionados);
                    target.add(panelGruposSelecionados);
                } else {
                    panelItensSelecionados.addOrReplace(dataViewItensSelecionados);
                    target.add(panelItensSelecionados);
                }
            }
        });
        field.setEnabled(item.getModelObject().getContrato() == null);
        return field;
    }

    private TextField<BigDecimal> newTextFieldNomeValorTotalImediato(Item<ItemBemDto> item, Integer quantidade) {
        TextField<BigDecimal> field = new TextField<BigDecimal>("valorTotalImediato") {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
        field.setLabel(Model.of("Valor Total"));
        field.setRequired(false);
        field.setEnabled(false);
        field.add(new CustomRangeValidator(new BigDecimal("0.00"), new BigDecimal("999999999999.99")));

        field.add(new AjaxFormComponentUpdatingBehavior(ONKEYUP) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                RequestCycle.get().setMetaData(FeedbackPanelListener.SKIP_FEEDBACK_LISTENER, Boolean.TRUE);

                BigDecimal qtdImediata = new BigDecimal(item.getModelObject().getQuantidadeImediata() == null ? "0" : item.getModelObject().getQuantidadeImediata().toString());
                BigDecimal valorUnitario = new BigDecimal(item.getModelObject().getValorUnitario() == null ? "0" : item.getModelObject().getValorUnitario().toString());
                BigDecimal valorTotalImediato = qtdImediata.multiply(valorUnitario);
                item.getModelObject().setValorTotalRegistrar(valorTotalImediato);

                if (item.getModelObject().getTipoAgrupamentoLicitacao() == EnumTipoAgrupamentoLicitacao.GRUPO) {
                    panelGruposSelecionados.addOrReplace(dataViewGruposSelecionados);
                    target.add(panelGruposSelecionados);
                } else {
                    panelItensSelecionados.addOrReplace(dataViewItensSelecionados);
                    target.add(panelItensSelecionados);
                }
            }
        });
        return field;
    }

    private TextField<BigDecimal> newTextFieldNomeValorTotalRegistrar(Item<ItemBemDto> item, Integer quantidade) {
        BigDecimal qtdRegistrar = new BigDecimal(quantidade);
        BigDecimal valorUnitario = new BigDecimal(item.getModelObject().getValorUnitario() == null ? "0" : item.getModelObject().getValorUnitario().toString());
        BigDecimal valorTotalRegistrar = qtdRegistrar.multiply(valorUnitario);
        item.getModelObject().setValorTotalRegistrar(valorTotalRegistrar);

        TextField<BigDecimal> field = new TextField<BigDecimal>("valorTotalRegistrar", new PropertyModel<BigDecimal>(item.getModelObject(), "valorTotalRegistrar")) {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
        field.setLabel(Model.of("Valor Total Registrar"));
        field.setRequired(false);
        field.setEnabled(false);
        field.add(new CustomRangeValidator(new BigDecimal("0.00"), new BigDecimal("999999999999.99")));

        field.add(new AjaxFormComponentUpdatingBehavior(ONKEYUP) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                RequestCycle.get().setMetaData(FeedbackPanelListener.SKIP_FEEDBACK_LISTENER, Boolean.TRUE);

                BigDecimal qtdRegistrar = new BigDecimal(quantidade);
                BigDecimal valorUnitario = new BigDecimal(item.getModelObject().getValorUnitario() == null ? "0" : item.getModelObject().getValorUnitario().toString());
                BigDecimal valorTotalRegistrar = qtdRegistrar.multiply(valorUnitario);
                item.getModelObject().setValorTotalRegistrar(valorTotalRegistrar);

                if (item.getModelObject().getTipoAgrupamentoLicitacao() == EnumTipoAgrupamentoLicitacao.GRUPO) {
                    panelGruposSelecionados.addOrReplace(dataViewGruposSelecionados);
                    target.add(panelGruposSelecionados);
                } else {
                    panelItensSelecionados.addOrReplace(dataViewItensSelecionados);
                    target.add(panelItensSelecionados);
                }
            }
        });
        return field;
    }

    /*
     * Campos que ordenam
     */
    private Label newLabelSortNome() {
        Label label = null;
        label = new Label("lblOrderNome", "...");

        if ("nome".equalsIgnoreCase(coluna)) {
            label.add(classArrow);
        } else {
            label.add(classArrowUnsorted);
        }
        return label;
    }

    private Label newLabelSortRegiao() {
        Label label = null;
        label = new Label("lblOrderRegiao", "...");

        if ("regiao".equalsIgnoreCase(coluna)) {
            label.add(classArrow);
        } else {
            label.add(classArrowUnsorted);
        }
        return label;
    }

    private Label newLabelSortUf() {
        Label label = null;
        label = new Label("lblOrderUf", "...");

        if ("uf".equalsIgnoreCase(coluna)) {
            label.add(classArrow);
        } else {
            label.add(classArrowUnsorted);
        }
        return label;
    }

    private Label newLabelSortQuantidade() {
        Label label = null;
        label = new Label("lblOrderQuantidade", "...");

        if ("quantidade".equalsIgnoreCase(coluna)) {
            label.add(classArrow);
        } else {
            label.add(classArrowUnsorted);
        }
        return label;
    }

    public Button getButtonAdicionar() {
        Button buttonAdicionar = new AjaxButton("btnAdicionar") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                adicionarItem(target);
            }
        };
        buttonAdicionar.setEnabled(botoesTabelaEnabled());
        return buttonAdicionar;
    }

    private AjaxFallbackButton newButtonAdicionar() {
        AjaxFallbackButton buttonAdicionar = componentFactory.newAjaxFallbackButton("btnAdicionar", null, (target, form) -> adicionarItem(target));
        buttonAdicionar.setEnabled(botoesTabelaEnabled());
        return buttonAdicionar;
    }

    private AjaxFallbackButton newButtonRemoverItem(Item<ItemBemDto> item) {
        AjaxFallbackButton buttonRemover = componentFactory.newAjaxFallbackButton("btnRemoverItem", null, (target, form) -> removerItem(target, item));
        buttonRemover.setEnabled(botoesTabelaEnabled());
        buttonRemover.setVisible(item.getModelObject().getContrato() == null);
        return buttonRemover;
    }

    private Button newButtonInformacao(Item<ItemBemDto> item) {
        Button btnInformacao = componentFactory.newButton("btnInformacao", null);
        btnInformacao.setVisible(item.getModelObject().getContrato() != null);
        return btnInformacao;
    }

    private AjaxFallbackButton newButtonRemoverGrupo(Item<ItemBemDto> item) {
        AjaxFallbackButton buttonRemover = componentFactory.newAjaxFallbackButton("btnRemoverGrupo", null, (target, form) -> removerGrupo(target, item));
        buttonRemover.setEnabled(botoesTabelaEnabled());
        buttonRemover.setVisible(item.getModelObject().getContrato() == null);
        return buttonRemover;
    }

    private class ProviderPrimeiraLista extends SortableDataProvider<BemUfDto, String> {
        private static final long serialVersionUID = 1L;

        public ProviderPrimeiraLista() {
            // setSort("nomeCriterio", SortOrder.ASCENDING);
        }

        @Override
        public Iterator<BemUfDto> iterator(long first, long size) {

            Collections.sort(listaLocaisEntrega, BemUfDto.getComparator(order, coluna));

            List<BemUfDto> listTemp = new ArrayList<BemUfDto>();
            int firstTemp = 0;
            int flagTemp = 0;
            for (BemUfDto k : listaLocaisEntrega) {
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
            return listaLocaisEntrega.size();
        }

        @Override
        public IModel<BemUfDto> model(BemUfDto object) {
            return new CompoundPropertyModel<BemUfDto>(object);
        }
    }

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

    private class ProviderItensSelecionados extends SortableDataProvider<ItemBemDto, String> {
        private static final long serialVersionUID = 1L;

        private List<ItemBemDto> lista = new ArrayList<ItemBemDto>();

        public ProviderItensSelecionados(List<ItemBemDto> item) {
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

    /*
     * ABAIXO VIRÃO AS AÇÕES
     */

    private boolean oItemDigitandoNaoEOMesmoASerIterado(Item<ItemBemDto> item, ItemBemDto grupo) {
        ItemBemDto itemBemDto = item.getModelObject();
        if (!itemBemDto.getNomeModelDto().equals(grupo.getNomeModelDto())) {
            String alterado = item.getModelObject().getNomeModelDto().getNomeGrupo() + "(" + contadorDeGruposComMesmoNome + ")";
            item.getModelObject().getNomeModelDto().setNomeGrupo(alterado);
            contadorDeGruposComMesmoNome++;
            return true;
        }
        return false;
    }

    private void acaoCheckAdicionarTodos(AjaxRequestTarget target) {

        if (selecionarTodosBens) {
            for (BemUfDto bem : listaLocaisEntrega) {
                bem.setSelecionado(true);
                listaTemporariaDeBensSelecionados.add(bem);
            }
        } else {
            for (BemUfDto bem : listaLocaisEntrega) {
                bem.setSelecionado(false);
            }
            listaTemporariaDeBensSelecionados.clear();
        }
        atualizarCheckBox(target);
    }

    private void acaoCheck(Item<BemUfDto> item, AjaxRequestTarget target) {

        if (item.getModelObject().getSelecionado()) {
            listaTemporariaDeBensSelecionados.add(item.getModelObject());

        } else {
            listaTemporariaDeBensSelecionados.remove(item.getModelObject());
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

    private void adicionarItem(AjaxRequestTarget target) {

        if (listaTemporariaDeBensSelecionados.size() == 0) {
            String msg = "";
            msg += "<p><li> Selecione ao menos um 'Bem' na tabela de 'Locais de Entrega'.</li><p />";

            mensagem.setObject(msg);
            target.add(labelMensagem);
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraFeedBackLocaisEntrega');");
            return;
        }

        Boolean formarGrupos = dividirItensEmGrupos();

        listaTemporariaDeBensSelecionados.clear();
        selecionarTodosBens = false;

        atualizarBensDaPrimeiroTabelaAoAdicionar();
        primeiroLoop = true;

        if (formarGrupos) {
            atualizarTabelaLocaisEntregaEGrupo(target);
            contadorGruposAdicionados++;
        } else {
            atualizarTabelaLocaisEntregaEItens(target);
            contadorItensAdicionados++;
        }
    }

    private boolean dividirItensEmGrupos() {

        // Irá ordenar a lista
        Collections.sort(listaTemporariaDeBensSelecionados, BemUfDto.getComparator(1, "nome"));
        contadorDeItensCriados++;

        List<BemUfDto> listaPreenchidaTemp = new ArrayList<BemUfDto>();
        Bem bemAtual = listaTemporariaDeBensSelecionados.get(0).getBem();
        boolean isGrupo = false;

        LicitacaoNomeModelDto nome = new LicitacaoNomeModelDto();
        boolean loopSomenteUmaVez = true;
        Bem ultimoBemAdicionado = new Bem();
        for (BemUfDto temp : listaTemporariaDeBensSelecionados) {
            // Irei verificar item por item, se algum item for diferente do
            // 'bemAtual' então são itens diferentes, será um grupo.

            BemUfDto bem = new BemUfDto();
            bem.setBem(temp.getBem());
            bem.setQuantidade(temp.getQuantidade());
            bem.setUf(temp.getUf());

            if (bemAtual.getId() != null && bemAtual.getId().intValue() == temp.getBem().getId().intValue()) {
                listaPreenchidaTemp.add(bem);
                ultimoBemAdicionado = temp.getBem();
            } else {

                // Se cair aqui então significa que existem Itens diferentes...
                // então será um grupo.
                isGrupo = true;

                ItemBemDto itemBem = new ItemBemDto();

                nome.setNomeGrupo("Grupo");
                itemBem.setNomeModelDto(nome);
                itemBem.setTipoAgrupamentoLicitacao(EnumTipoAgrupamentoLicitacao.GRUPO);
                itemBem.setValorUnitario(ultimoBemAdicionado.getValorEstimadoBem());

                List<BemUfDto> listaComGrupo = copiarDaListaTemporariaParaDefinitiva(listaPreenchidaTemp);
                itemBem.setListaDeBens(listaComGrupo);
                itemBem.setContadorTemp(contadorDeItensCriados);
                listaDeGrupos.add(itemBem);
                ultimoBemAdicionado = temp.getBem();

                bemAtual = temp.getBem();
                listaPreenchidaTemp.clear();
                listaPreenchidaTemp.add(bem);
            }
            listaBensJaSelecionados.add(temp);
        }

        ItemBemDto itemBemGrupo = new ItemBemDto();
        if (isGrupo) {
            itemBemGrupo.setTipoAgrupamentoLicitacao(EnumTipoAgrupamentoLicitacao.GRUPO);
        } else {
            itemBemGrupo.setTipoAgrupamentoLicitacao(EnumTipoAgrupamentoLicitacao.ITEM);
        }
        itemBemGrupo.setListaDeBens(listaPreenchidaTemp);

        if (isGrupo) {
            itemBemGrupo.setNomeModelDto(nome);
            itemBemGrupo.setContadorTemp(contadorDeItensCriados);
            itemBemGrupo.setValorUnitario(ultimoBemAdicionado.getValorEstimadoBem());
            listaDeGrupos.add(itemBemGrupo);

        } else {
            nome.setNomeGrupo("Item ");
            itemBemGrupo.setNomeModelDto(nome);
            itemBemGrupo.setContadorTemp(contadorDeItensCriados);
            itemBemGrupo.setValorUnitario(ultimoBemAdicionado.getValorEstimadoBem());
            listaDeItens.add(itemBemGrupo);
        }
        return isGrupo;
    }

    private List<BemUfDto> copiarDaListaTemporariaParaDefinitiva(List<BemUfDto> temp) {

        List<BemUfDto> listaComGrupo = new ArrayList<BemUfDto>();
        for (BemUfDto copiar : temp) {
            BemUfDto bemCopiar = new BemUfDto();
            bemCopiar.setBem(copiar.getBem());
            bemCopiar.setQuantidade(copiar.getQuantidade());
            bemCopiar.setUf(copiar.getUf());
            listaComGrupo.add(bemCopiar);
        }

        return listaComGrupo;
    }

    private void removerGrupo(AjaxRequestTarget target, Item<ItemBemDto> item) {

        // Será preciso encontrar todos os itens que estão no grupo do botão
        // clicado...
        Integer nomeGrupoApagar = item.getModelObject().getContadorTemp();
        List<ItemBemDto> listaComTodosBens = buscarOsItensDoGrupoSelecionado(nomeGrupoApagar);

        // Com a lista dos itens que estão no grupo agora será necessário
        // retira-los da lista de Bens que já estão em uso
        removerOsItensDoGrupoDaListaDeBensJaEmUso(listaComTodosBens, target);

        // Retirar agora os grupos da lista que popula o DataView de grupo.
        for (ItemBemDto grupos : listaComTodosBens) {
            listaDeGrupos.remove(grupos);
        }

        atualizarAposRemoverItemPrimeiraTabel();
        atualizarTabelaLocaisEntregaEGrupo(target);
        target.appendJavaScript("ancorarResultadoPesquisa('#ancoraFeedBackGruposSelecionados');");
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

    private void removerOsItensDoGrupoDaListaDeBensJaEmUso(List<ItemBemDto> listaComTodosBens, AjaxRequestTarget target) {
        for (ItemBemDto lista : listaComTodosBens) {
            for (BemUfDto bem : lista.getListaDeBens()) {
                for (int i = listaBensJaSelecionados.size(); i >= 0; i--) {

                    Bem bemAdicionado = listaBensJaSelecionados.get(i - 1).getBem();
                    Uf ufAdicionado = listaBensJaSelecionados.get(i - 1).getUf();
                    Long quantidadeAdicionado = listaBensJaSelecionados.get(i - 1).getQuantidade();

                    if (bem.getBem().getId().intValue() == bemAdicionado.getId().intValue() && bem.getUf().getId().intValue() == ufAdicionado.getId().intValue() && bem.getQuantidade().intValue() == quantidadeAdicionado.intValue()) {
                        listaBensJaSelecionados.remove(i - 1);
                        break;
                    }
                }
            }
        }
    }

    private void removerItem(AjaxRequestTarget target, Item<ItemBemDto> item) {

        for (BemUfDto bem : item.getModelObject().getListaDeBens()) {
            if (listaBensJaSelecionados.size() > 0) {
                for (int i = listaBensJaSelecionados.size(); i >= 0; i--) {

                    Bem bemAdicionado = listaBensJaSelecionados.get(i - 1).getBem();
                    Uf ufAdicionado = listaBensJaSelecionados.get(i - 1).getUf();
                    Long quantidadeAdicionado = listaBensJaSelecionados.get(i - 1).getQuantidade();

                    if (bem.getBem().getId().intValue() == bemAdicionado.getId().intValue() && bem.getUf().getId().intValue() == ufAdicionado.getId().intValue() && bem.getQuantidade().intValue() == quantidadeAdicionado.intValue()) {
                        listaBensJaSelecionados.remove(i - 1);
                        break;
                    }
                }
            }
        }
        primeiroLoop = true;
        atualizarAposRemoverItemPrimeiraTabel();
        listaDeItens.remove(item.getModelObject());

        atualizarTabelaLocaisEntregaEItens(target);
        target.appendJavaScript("ancorarResultadoPesquisa('#ancoraFeedBackItensSelecionados');");
    }

    private void copiarConteudoDaListaDeLocaisDeEntrega() {

        listaLocaisEntrega.clear();
        for (BemUfDto bem : listaComDadosOriginaisBemUfDto) {

            BemUfDto novo = new BemUfDto();
            novo.setBem(bem.getBem());
            novo.setQuantidade(bem.getQuantidade());
            novo.setUf(bem.getUf());
            novo.setSelecionado(false);

            listaLocaisEntrega.add(novo);
        }
    }

    private void atualizarAposRemoverItemPrimeiraTabel() {
        copiarConteudoDaListaDeLocaisDeEntrega();
        atualizarBensDaPrimeiroTabela();
    }

    private void atualizarBensDaPrimeiroTabela() {
        for (BemUfDto bem : listaBensJaSelecionados) {
            for (int i = listaLocaisEntrega.size(); i > 0; i--) {

                Bem bemAdicionado = listaLocaisEntrega.get(i - 1).getBem();
                Uf ufAdicionado = listaLocaisEntrega.get(i - 1).getUf();
                Long quantidadeAdicionado = listaLocaisEntrega.get(i - 1).getQuantidade();

                if (bem.getBem().getId().intValue() == bemAdicionado.getId().intValue() && bem.getUf().getId().intValue() == ufAdicionado.getId().intValue() && bem.getQuantidade().intValue() == quantidadeAdicionado.intValue()) {

                    listaLocaisEntrega.remove(i - 1);
                    break;
                }
            }
        }
    }

    private void atualizarBensDaPrimeiroTabelaAoAdicionar() {
        for (BemUfDto bem : listaBensJaSelecionados) {
            if (listaLocaisEntrega.contains(bem)) {
                listaLocaisEntrega.remove(bem);
            }
        }
    }

    private void atualizarTabelaLocaisEntregaEItens(AjaxRequestTarget target) {
        panelPrimeiro.addOrReplace(newDataViewPrimeiraLista());
        panelPrimeiro.addOrReplace(newPaginatorPrimeiraLista());
        panelPrimeiro.addOrReplace(newCheckAdicionarTodosBens());

        this.addOrReplace(panelItensSelecionados = new PanelItensSelecionados("panelItensSelecionados"));
        target.add(panelPrimeiro);
        target.add(this);
    }

    private void atualizarTabelaLocaisEntregaEGrupo(AjaxRequestTarget target) {
        panelPrimeiro.addOrReplace(newDataViewPrimeiraLista());
        panelPrimeiro.addOrReplace(newPaginatorPrimeiraLista());
        panelPrimeiro.addOrReplace(newCheckAdicionarTodosBens());

        this.addOrReplace(panelGruposSelecionados = new PanelGruposSelecionados("panelGruposSelecionados"));
        target.add(panelPrimeiro);
        target.add(this);
    }

    private void atualizarCheckBox(AjaxRequestTarget target) {
        panelPrimeiro.addOrReplace(newDataViewPrimeiraLista());
        panelPrimeiro.addOrReplace(newPaginatorPrimeiraLista());
        panelPrimeiro.addOrReplace(newCheckAdicionarTodosBens());

        target.add(panelPrimeiro);
    }

    private boolean botoesTabelaEnabled() {
        if (readOnly) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * Ações que ordenam
     */

    public AjaxSubmitLink newButtonOrdenarNome() {
        return new AjaxSubmitLink("btnOrdenarNome") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                coluna = "nome";
                mudarOrdemTabela();
                atualizarSetasOrdenacao(target);
            }
        };
    }

    public AjaxSubmitLink newButtonOrdenarRegiao() {
        return new AjaxSubmitLink("btnOrdenarRegiao") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                coluna = "regiao";
                mudarOrdemTabela();
                atualizarSetasOrdenacao(target);
            }
        };
    }

    public AjaxSubmitLink newButtonOrdenarUf() {
        return new AjaxSubmitLink("btnOrdenarUf") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                coluna = "uf";
                mudarOrdemTabela();
                atualizarSetasOrdenacao(target);
            }
        };
    }

    public AjaxSubmitLink newButtonOrdenarQuantidade() {
        return new AjaxSubmitLink("btnOrdenarQuantidade") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                coluna = "quantidade";
                mudarOrdemTabela();
                atualizarSetasOrdenacao(target);
            }
        };
    }

    private void setarNomeGrupoEmTodosItens(Item<ItemBemDto> item) {
        if (item.getModelObject().getTipoAgrupamentoLicitacao() == EnumTipoAgrupamentoLicitacao.GRUPO) {
            String nomeGrupo = item.getModelObject().getNomeModelDto().getNomeGrupo();
            for (ItemBemDto grup : listaDeGrupos) {
                if (grup.getNomeModelDto().getNomeGrupo().equalsIgnoreCase(nomeGrupo)) {

                }
            }
        }

    }

    private void mudarOrdemTabela() {
        if (order == 1) {
            order = -1;
            classArrow = new AttributeAppender(CLASS, "mj_arrow_desc", " ");
        } else {
            order = 1;
            classArrow = new AttributeAppender(CLASS, "mj_arrow_asc", " ");
        }
    }

    private void atualizarSetasOrdenacao(AjaxRequestTarget target) {
        panelPrimeiro.addOrReplace(newDataViewPrimeiraLista());
        panelPrimeiro.addOrReplace(newLabelSortNome());
        panelPrimeiro.addOrReplace(newLabelSortRegiao());
        panelPrimeiro.addOrReplace(newLabelSortUf());
        panelPrimeiro.addOrReplace(newLabelSortQuantidade());
        panelPrimeiro.addOrReplace(newPaginatorPrimeiraLista());
        target.add(panelPrimeiro);
    }

    public Integer getItensPorPaginaPrimeiro() {
        return itensPorPaginaPrimeiro;
    }

    public void setItensPorPaginaPrimeiro(Integer itensPorPaginaPrimeiro) {
        this.itensPorPaginaPrimeiro = itensPorPaginaPrimeiro;
    }

    public PanelPrimeiro getPanelPrimeiro() {
        return panelPrimeiro;
    }

    public void setPanelPrimeiro(PanelPrimeiro panelPrimeiro) {
        this.panelPrimeiro = panelPrimeiro;
    }

    public List<BemUfDto> getListaLocaisEntrega() {
        return listaLocaisEntrega;
    }

    public void setListaLocaisEntrega(List<BemUfDto> listaLocaisEntrega) {
        this.listaLocaisEntrega = listaLocaisEntrega;
    }

    public Integer getItensPorPaginaItensSelecionados() {
        return itensPorPaginaItensSelecionados;
    }

    public void setItensPorPaginaItensSelecionados(Integer itensPorPaginaItensSelecionados) {
        this.itensPorPaginaItensSelecionados = itensPorPaginaItensSelecionados;
    }

    public List<BemUfDto> getListaComDadosOriginaisBemUfDto() {
        return listaComDadosOriginaisBemUfDto;
    }

    public void setListaComDadosOriginaisBemUfDto(List<BemUfDto> listaComDadosOriginaisBemUfDto) {
        this.listaComDadosOriginaisBemUfDto = listaComDadosOriginaisBemUfDto;
    }

    public Integer getItensPorPaginaGruposSelecionados() {
        return itensPorPaginaGruposSelecionados;
    }

    public void setItensPorPaginaGruposSelecionados(Integer itensPorPaginaGruposSelecionados) {
        this.itensPorPaginaGruposSelecionados = itensPorPaginaGruposSelecionados;
    }

    public Integer getContadorItensAdicionados() {
        return contadorItensAdicionados;
    }

    public void setContadorItensAdicionados(Integer contadorItensAdicionados) {
        this.contadorItensAdicionados = contadorItensAdicionados;
    }

    public Integer getContadorGruposAdicionados() {
        return contadorGruposAdicionados;
    }

    public void setContadorGruposAdicionados(Integer contadorGruposAdicionados) {
        this.contadorGruposAdicionados = contadorGruposAdicionados;
    }

    public List<ItemBemDto> getListaDeItens() {
        return listaDeItens;
    }

    public void setListaDeItens(List<ItemBemDto> listaDeItens) {
        this.listaDeItens = listaDeItens;
    }

    public List<ItemBemDto> getListaDeGrupos() {
        return listaDeGrupos;
    }

    public void setListaDeGrupos(List<ItemBemDto> listaDeGrupos) {
        this.listaDeGrupos = listaDeGrupos;
    }

    public boolean isPrimeiroLoop() {
        return primeiroLoop;
    }

    public void setPrimeiroLoop(boolean primeiroLoop) {
        this.primeiroLoop = primeiroLoop;
    }
}
