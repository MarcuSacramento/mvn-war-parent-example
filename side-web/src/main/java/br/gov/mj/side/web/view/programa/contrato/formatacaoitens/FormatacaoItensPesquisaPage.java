package br.gov.mj.side.web.view.programa.contrato.formatacaoitens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoItensContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ItensFormatacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;
import br.gov.mj.side.web.dto.ContratoDto;
import br.gov.mj.side.web.dto.FormatacaoItensContratoDto;
import br.gov.mj.side.web.service.ContratoService;
import br.gov.mj.side.web.service.FormatacaoItensContratoService;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.SortableContratoDataProvider;
import br.gov.mj.side.web.view.planejarLicitacao.ContratoPanelBotoes;
import br.gov.mj.side.web.view.programa.PanelFasePrograma;
import br.gov.mj.side.web.view.template.TemplatePage;

public class FormatacaoItensPesquisaPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    private static final String TITULO_PAGINA = "Gerenciar Programa";
    private static final String ONCHANGE = "onchange";
    private static final String CLASS = "class";
    private static final String PAGINATOR = "paginationItens";

    private Integer itensPorPaginaContrato = Constants.ITEMS_PER_PAGE_PAGINATION;
    private Integer itensPorPaginaItensContrato = Constants.ITEMS_PER_PAGE_PAGINATION;
    private Integer itensPorPaginaFormatacao = Constants.ITEMS_PER_PAGE_PAGINATION;

    private AttributeAppender classArrow = new AttributeAppender(CLASS, "mj_arrow_asc", " ");
    private AttributeAppender classArrowUnsorted = new AttributeAppender(CLASS, "mj_arrow_unsorted", " ");

    private Form<FormatacaoItensContratoDto> form;
    private Programa programa;
    private Page backPage;
    private Contrato contrato;
    private FormatacaoItensContrato formatacaoItensContrato;
    private Boolean isListaSelecionada = Boolean.FALSE;
    private String coluna = "item";
    private int order = 1;
    private FormatacaoContrato formatacaoContrato;
    private int indiceFormatacaoContrato;
    private String nomeItemFormatado;
    private InfraAjaxPagingNavigator paginator;

    private DataView<Contrato> dataViewListaContrato;
    private DataView<Bem> dataViewListaItem;
    private DataView<FormatacaoContrato> dataViewListaFormatacaoContrato;

    private PanelFasePrograma panelFasePrograma;
    private ContratoPanelBotoes execucaoPanelBotoes;
    private PanelListaContrato panelListaContrato;
    private PanelListaItensContrato panelListaItensContrato;
    private PanelListaFormatacoes panelListaFormatacoes;
    private PanelBotoesAdicionar panelBotoesAdicionar;

    private List<Bem> listaDeItens = new ArrayList<Bem>();
    private List<ItensFormatacao> listaParaFormatacao = new ArrayList<ItensFormatacao>();
    private List<FormatacaoContrato> listaFormatacaoContrato = new ArrayList<FormatacaoContrato>();

    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private ContratoService contratoService;
    @Inject
    private FormatacaoItensContratoService formatacaoItensContratoService;
    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;

    public FormatacaoItensPesquisaPage(final PageParameters pageParameters) {
        super(pageParameters);

        setTitulo(TITULO_PAGINA);
        initComponents();
    }

    public FormatacaoItensPesquisaPage(final PageParameters pageParameters, Programa programa, Page backPage) {
        super(pageParameters);
        this.programa = programa;
        this.backPage = backPage;

        setTitulo(TITULO_PAGINA);
        initComponents();
    }

    private void initComponents() {
        form = componentFactory.newForm("form", new FormatacaoItensContratoDto());

        form.add(panelFasePrograma = new PanelFasePrograma("panelFasePrograma", programa, backPage));
        form.add(execucaoPanelBotoes = new ContratoPanelBotoes("execucaoPanelPotoes", programa, backPage, "formatacaoItens"));

        form.add(panelListaContrato = new PanelListaContrato("panelListaContrato"));
        form.add(panelListaItensContrato = new PanelListaItensContrato("panelListaItensContrato"));
        panelListaItensContrato.setVisible(Boolean.FALSE);

        panelBotoesAdicionar = new PanelBotoesAdicionar("panelBotoesAdicionar");
        panelBotoesAdicionar.setVisible(Boolean.FALSE);
        form.add(panelBotoesAdicionar);

        form.add(panelListaFormatacoes = new PanelListaFormatacoes("panelListaItensFormatados"));
        panelListaFormatacoes.setVisible(Boolean.FALSE);

        add(form);
    }

    // Paineis
    private class PanelListaContrato extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelListaContrato(String id) {
            super(id);
            setOutputMarkupId(true);

            ContratoDto contratoDto = new ContratoDto();
            contratoDto.setNomePrograma(programa.getNomePrograma());
            contratoDto.setCodigoPrograma(programa.getCodigoIdentificadorProgramaPublicado());

            SortableContratoDataProvider dataProvider = new SortableContratoDataProvider(contratoService, contratoDto);
            RadioGroup<Contrato> radioContratoGrup = new RadioGroup<Contrato>("radioGrupContrato", new Model<Contrato>());
            radioContratoGrup.add(dataViewListaContrato = newDataViewListaContrato(dataProvider));

            add(radioContratoGrup);

            add(newDropItensPorPaginaContrato());

            add(new OrderByBorder<String>("orderByNumeroContrato", "numeroContrato", dataProvider));
            add(new OrderByBorder<String>("orderByFornecedor", "fornecedor.nomeEntidade", dataProvider));
            add(new InfraAjaxPagingNavigator("pagination", dataViewListaContrato));
        }
    }

    private class PanelListaItensContrato extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelListaItensContrato(String id) {
            super(id);
            setOutputMarkupId(true);

            ProviderItensContrato dp = new ProviderItensContrato();
            add(newCheckBoxSelecionarTudo());
            add(newDataViewListaItens(dp));
            add(newDropItensPorPaginaItens());

            add(newLabelSortNome());
            add(newLabelSortDescricao());
            add(newButtonOrdenarNome());
            add(newButtonOrdenarDescricao());

            add(newPaginator());
        }
    }

    private class PanelBotoesAdicionar extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBotoesAdicionar(String id) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);

            add(newButtonAdicionar());
            add(newButtonVoltar());
        }

    }

    private class PanelListaFormatacoes extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelListaFormatacoes(String id) {
            super(id);
            setOutputMarkupId(true);

            SortableFormatacaoContratoDataProvider dp = new SortableFormatacaoContratoDataProvider();
            dataViewListaFormatacaoContrato = newDataViewListaFormatacaoContrato(dp);
            add(newDropItensPorPaginaFormatacao());
            add(dataViewListaFormatacaoContrato);

            add(new InfraAjaxPagingNavigator("paginationFormatacao", dataViewListaFormatacaoContrato));
        }

    }

    // Componentes
    private AjaxCheckBox newCheckBoxSelecionarTudo() {
        AjaxCheckBox check = new AjaxCheckBox("groupSelector", new PropertyModel<Boolean>(this, "isListaSelecionada")) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                selecionaLista(target);
            }

        };
        return check;
    }

    public Component newPaginator() {
        if (paginator == null) {
            paginator = new InfraAjaxPagingNavigator(PAGINATOR, dataViewListaItem);
        }
        return paginator;
    }

    // metodo do checkbox para marcar ou desmarcar a lista de objetos
    private void selecionaLista(AjaxRequestTarget target) {
        for (Bem bemNovo : listaDeItens) {
            if (isListaSelecionada) {
                bemNovo.setItemSelecionadoFormatacao(Boolean.TRUE);
            } else {
                bemNovo.setItemSelecionadoFormatacao(Boolean.FALSE);
            }
        }
        panelListaItensContrato.addOrReplace(newCheckBoxSelecionarTudo());
        target.add(panelListaItensContrato);
    }

    private InfraAjaxConfirmButton newButtonExcluir(Item<FormatacaoContrato> item) {
        List<OrdemFornecimentoContrato> listaOf = ordemFornecimentoContratoService.buscarOrdemFornecimentoContrato(contrato);
        InfraAjaxConfirmButton btnExcluir = componentFactory.newAJaxConfirmButton("btnExcluir", "MT029", form, (target, formz) -> acaoExcluir(target, item));
        btnExcluir.setVisible(listaOf.size() <= 0 && listaOf.isEmpty());
        return btnExcluir;
    }

    private Button newButtonEditar(Item<FormatacaoContrato> item) {
        List<OrdemFornecimentoContrato> listaOf = ordemFornecimentoContratoService.buscarOrdemFornecimentoContrato(contrato);
        Button btnAlterar = componentFactory.newButton("btnAlterar", () -> acaoEditar(item));
        btnAlterar.setOutputMarkupId(true);
        btnAlterar.setVisible(listaOf.size() <= 0 && listaOf.isEmpty());
        return btnAlterar;
    }

    private Button newButtonInformacao(Item<FormatacaoContrato> item) {
        List<OrdemFornecimentoContrato> listaOf = ordemFornecimentoContratoService.buscarOrdemFornecimentoContrato(contrato);
        Button btnInformacao = componentFactory.newButton("btnInformacao", null);
        btnInformacao.setVisible(listaOf.size() > 0 && !listaOf.isEmpty());
        return btnInformacao;
    }

    private AjaxSubmitLink newButtonAdicionar() {
        AjaxSubmitLink button = new AjaxSubmitLink("btnAdicionarNovo", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                adicionarNovo();
            }
        };
        return button;
    }

    private Button newButtonVoltar() {
        Button button = componentFactory.newButton("btnVoltar", () -> actionVoltar());
        button.setDefaultFormProcessing(false);
        return button;
    }

    private DropDownChoice<Integer> newDropItensPorPaginaContrato() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPaginaContrato", new LambdaModel<Integer>(this::getItensPorPaginaContrato, this::setItensPorPaginaContrato), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewListaContrato.setItemsPerPage(getItensPorPaginaContrato());
                target.add(panelListaContrato);
            };
        });
        return dropDownChoice;
    }

    private DropDownChoice<Integer> newDropItensPorPaginaItens() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPaginaItens", new LambdaModel<Integer>(this::getItensPorPaginaItensContrato, this::setItensPorPaginaItensContrato), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewListaItem.setItemsPerPage(getItensPorPaginaItensContrato());
                target.add(panelListaItensContrato);
            };
        });
        return dropDownChoice;
    }

    private DropDownChoice<Integer> newDropItensPorPaginaFormatacao() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPaginaFormatacao", new LambdaModel<Integer>(this::getItensPorPaginaFormatacao, this::setItensPorPaginaFormatacao), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewListaFormatacaoContrato.setItemsPerPage(getItensPorPaginaFormatacao());
                target.add(panelListaFormatacoes);
            };
        });
        return dropDownChoice;
    }

    // Ações
    private void acaoExcluir(AjaxRequestTarget target, Item<FormatacaoContrato> item) {
        formatacaoItensContratoService.excluir(item.getModelObject().getId());
        getSession().info("Removido com sucesso!");
        listaDeItens.clear();
        listaDeItens = formatacaoItensContratoService.buscarListaBensRemanescentes(item.getModelObject().getContrato());
        pesquisaListaFormatacaoContrato();
        target.add(panelListaItensContrato, panelListaFormatacoes);
    }

    private void acaoEditar(Item<FormatacaoContrato> item) {
        List<ItensFormatacao> listaItensParaEdicao = formatacaoItensContratoService.buscarItens(item.getModelObject());
        for (ItensFormatacao itensFormatacao : listaItensParaEdicao) {
            itensFormatacao.getItem().setItemSelecionadoFormatacao(Boolean.TRUE);
        }
        converteLista();
        listaItensParaEdicao.addAll(listaParaFormatacao);
        setResponsePage(new FormatacaoItensPage(getPageParameters(), this, programa, listaItensParaEdicao, item.getModelObject(), isListaSelecionada, contrato));
    }

    private void actionVoltar() {
        setResponsePage(backPage);
    }

    // pesquisa BENS do contrato selecionado
    private void pesquisaBensRemanescentes(Item<Contrato> item) {
        listaDeItens = formatacaoItensContratoService.buscarListaBensRemanescentes(contrato);
    }

    // atualiza a lista e paineis de BENS
    private void exibirListaItens(Item<Contrato> item, AjaxRequestTarget target) {
        contrato = item.getModelObject();
        pesquisaBensRemanescentes(item);
        pesquisaListaFormatacaoContrato();
        panelListaItensContrato.setVisible(Boolean.TRUE);
        panelListaFormatacoes.setVisible(Boolean.TRUE);
        panelBotoesAdicionar.setVisible(Boolean.TRUE);
        target.appendJavaScript("ancorarResultadoPesquisa('#ancoraPesquisa');");
        target.add(panelListaItensContrato, panelListaFormatacoes, panelBotoesAdicionar);
    }

    private void pesquisaListaFormatacaoContrato() {
        listaFormatacaoContrato = formatacaoItensContratoService.buscarFormatacaoContrato(contrato);
        List<FormatacaoContrato> lista = new ArrayList<FormatacaoContrato>();
        for (FormatacaoContrato formatacaoP : listaFormatacaoContrato) {
            formatacaoP.setItens(formatacaoItensContratoService.buscarItens(formatacaoP));
            lista.add(formatacaoP);
        }
        listaFormatacaoContrato.clear();
        listaFormatacaoContrato.addAll(lista);

    }

    private void adicionarNovo() {
        if (contrato != null) {
            if (!listaDeItens.isEmpty() && listaDeItens.size() > 0) {
                converteLista();
                setResponsePage(new FormatacaoItensPage(getPageParameters(), this, programa, listaParaFormatacao, new FormatacaoContrato(), isListaSelecionada, contrato));
            } else {
                addMsgError("Não existe item para o contrato selecionado.");
            }
        } else {
            addMsgError("Por favor, selecione um contrato.");
        }
    }

    // converte a lista de BENS para ITENSFORMATACAO
    private void converteLista() {
        int cont = 0;

        listaParaFormatacao.clear();

        for (int i = 0; i < listaDeItens.size(); i++) {
            ItensFormatacao item = new ItensFormatacao();
            item.setItem(listaDeItens.get(i));
            item.setFormatacao(formatacaoContrato);

            if (item.getItem().getItemSelecionadoFormatacao() == null) {
                item.getItem().setItemSelecionadoFormatacao(Boolean.FALSE);
            }

            // conta quantos objetos foram selecionados
            if (Boolean.TRUE.equals(item.getItem().getItemSelecionadoFormatacao())) {
                cont++;
            }

            listaParaFormatacao.add(item);
        }

        // altera o valor caso tenha selecionado todos os objetos da lista
        if (listaDeItens.size() == cont) {
            isListaSelecionada = Boolean.TRUE;
        } else {
            isListaSelecionada = Boolean.FALSE;
        }
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

    // marca o BEM selecionado
    private void setItemSelecionado(Item<Bem> item) {
        Boolean valorItem = item.getModelObject().getItemSelecionadoFormatacao();

        if (valorItem == null) {
            item.getModelObject().setItemSelecionadoFormatacao(Boolean.TRUE);
        } else if (valorItem == false) {
            item.getModelObject().setItemSelecionadoFormatacao(Boolean.TRUE);
        } else if (valorItem == true) {
            item.getModelObject().setItemSelecionadoFormatacao(Boolean.FALSE);
        }
    }

    // Data View
    public DataView<Contrato> newDataViewListaContrato(SortableContratoDataProvider dp) {
        dataViewListaContrato = new DataView<Contrato>("listaContrato", dp) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<Contrato> item) {

                String grupoItem = verificarGrupoItem(item);

                Radio<Contrato> radioContrato = new Radio<Contrato>("radioContrato", Model.of(item.getModelObject()));
                radioContrato.add(new AjaxEventBehavior(ONCHANGE) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        exibirListaItens(item, target);
                    }
                });

                item.add(radioContrato);
                item.add(new Label("numeroContrato"));
                item.add(new Label("grupoItem", grupoItem));
                item.add(new Label("fornecedor.nomeEntidade"));
            }
        };
        dataViewListaContrato.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataViewListaContrato;
    }

    // DataView com a lista de bens remanescentes para ser formatado
    private DataView<Bem> newDataViewListaItens(ProviderItensContrato dp) {
        dataViewListaItem = new DataView<Bem>("listaItensContrato", dp) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<Bem> item) {

                CheckBox check = new CheckBox("check1", new PropertyModel<Boolean>(item.getModelObject(), "itemSelecionadoFormatacao"));
                check.add(new AjaxEventBehavior(ONCHANGE) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        setItemSelecionado(item);
                    }
                });

                item.add(check);
                item.add(new Label("nomeBem", item.getModelObject().getNomeBem()));
                item.add(new Label("descricaoBem", item.getModelObject().getDescricaoBem()));
            }
        };
        dataViewListaItem.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataViewListaItem;
    }

    public DataView<FormatacaoContrato> newDataViewListaFormatacaoContrato(SortableFormatacaoContratoDataProvider dp) {
        dataViewListaFormatacaoContrato = new DataView<FormatacaoContrato>("listaFormatacaoItens", dp) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoContrato> item) {

                getIndiceLista(item);

                item.add(new Label("numeroFormatacao", ++indiceFormatacaoContrato));
                item.add(new Label("nomeItemFormatado", nomeItemFormatado));

                item.add(newButtonInformacao(item));
                item.add(newButtonEditar(item));
                item.add(newButtonExcluir(item));
            }
        };
        dataViewListaFormatacaoContrato.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataViewListaFormatacaoContrato;
    }

    private void getIndiceLista(Item<FormatacaoContrato> item) {
        nomeItemFormatado = "";
        for (int i = 0; i < listaFormatacaoContrato.size(); i++) {
            if (item.getModelObject().equals(listaFormatacaoContrato.get(i))) {

                for (ItensFormatacao itensFormatacao : listaFormatacaoContrato.get(i).getItens()) {
                    nomeItemFormatado += itensFormatacao.getItem().getNomeBem() + " / ";
                }

                indiceFormatacaoContrato = i;
            }

        }
    }

    // Provaider
    private class ProviderItensContrato extends SortableDataProvider<Bem, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<Bem> iterator(long first, long size) {

            List<Bem> listaRetorno = new ArrayList<Bem>();
            if (!listaDeItens.isEmpty()) {
                int inicio = (int) first;
                int fim = (int) (first + size);

                if (fim > listaDeItens.size()) {
                    fim = listaDeItens.size();
                }
                for (int i = inicio; i < fim; i++) {
                    listaRetorno.add(listaDeItens.get(i));
                }
            }

            return listaRetorno.iterator();
        }

        @Override
        public long size() {
            return listaDeItens.size();
        }

        @Override
        public IModel<Bem> model(Bem object) {
            return new CompoundPropertyModel<Bem>(object);
        }
    }

    private class SortableFormatacaoContratoDataProvider extends SortableDataProvider<FormatacaoContrato, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoContrato> iterator(long first, long count) {
            return listaFormatacaoContrato.iterator();
        }

        @Override
        public long size() {
            return listaFormatacaoContrato.size();
        }

        @Override
        public IModel<FormatacaoContrato> model(FormatacaoContrato object) {
            return new CompoundPropertyModel<FormatacaoContrato>(object);
        }

    }

    // getters e setters
    public Integer getItensPorPaginaContrato() {
        return itensPorPaginaContrato;
    }

    public void setItensPorPaginaContrato(Integer itensPorPaginaContrato) {
        this.itensPorPaginaContrato = itensPorPaginaContrato;
    }

    public Integer getItensPorPaginaItensContrato() {
        return itensPorPaginaItensContrato;
    }

    public void setItensPorPaginaItensContrato(Integer itensPorPaginaItensContrato) {
        this.itensPorPaginaItensContrato = itensPorPaginaItensContrato;
    }

    public Integer getItensPorPaginaFormatacao() {
        return itensPorPaginaFormatacao;
    }

    public void setItensPorPaginaFormatacao(Integer itensPorPaginaFormatacao) {
        this.itensPorPaginaFormatacao = itensPorPaginaFormatacao;
    }

    private Label newLabelSortNome() {
        Label label = null;
        label = new Label("lblOrderNome", "...");

        if ("item".equalsIgnoreCase(coluna)) {
            label.add(classArrow);
        } else {
            label.add(classArrowUnsorted);
        }
        return label;
    }

    private Label newLabelSortDescricao() {
        Label label = null;
        label = new Label("lblOrderDescricao", "...");

        if ("descricao".equalsIgnoreCase(coluna)) {
            label.add(classArrow);
        } else {
            label.add(classArrowUnsorted);
        }
        return label;
    }

    private AjaxSubmitLink newButtonOrdenarNome() {
        return new AjaxSubmitLink("btnOrdenarNome", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                coluna = "item";
                mudarOrdemTabela();
                atualizarSetasOrdenacao(target);
            }
        };
    }

    private AjaxSubmitLink newButtonOrdenarDescricao() {
        return new AjaxSubmitLink("btnOrdenarDescricao", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                coluna = "descricao";
                mudarOrdemTabelaDescricao();
                atualizarSetasOrdenacao(target);
            }
        };
    }

    public void atualizarSetasOrdenacao(AjaxRequestTarget target) {
        ProviderItensContrato dp = new ProviderItensContrato();
        panelListaItensContrato.addOrReplace(newCheckBoxSelecionarTudo());
        panelListaItensContrato.addOrReplace(newDataViewListaItens(dp));
        panelListaItensContrato.addOrReplace(newDropItensPorPaginaItens());
        panelListaItensContrato.addOrReplace(newLabelSortNome());
        panelListaItensContrato.addOrReplace(newButtonOrdenarNome());
        panelListaItensContrato.addOrReplace(newLabelSortDescricao());
        panelListaItensContrato.addOrReplace(newButtonOrdenarDescricao());
        panelListaItensContrato.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR, dataViewListaItem));
        target.add(panelListaItensContrato);
    }

    public void mudarOrdemTabela() {
        if (order == 1) {
            order = -1;
            classArrow = new AttributeAppender(CLASS, "mj_arrow_desc", " ");
            listaDeItens.sort(Collections.reverseOrder((Bem o1, Bem o2) -> o1.getNomeBem().compareToIgnoreCase(o2.getNomeBem())));
        } else {
            order = 1;
            classArrow = new AttributeAppender(CLASS, "mj_arrow_asc", " ");
            listaDeItens.sort((Bem o1, Bem o2) -> o1.getNomeBem().compareToIgnoreCase(o2.getNomeBem()));
        }
    }

    public void mudarOrdemTabelaDescricao() {
        if (order == 1) {
            order = -1;
            classArrow = new AttributeAppender(CLASS, "mj_arrow_desc", " ");
            listaDeItens.sort(Collections.reverseOrder((Bem o1, Bem o2) -> o1.getDescricaoBem().compareToIgnoreCase(o2.getDescricaoBem())));
        } else {
            order = 1;
            classArrow = new AttributeAppender(CLASS, "mj_arrow_asc", " ");
            listaDeItens.sort((Bem o1, Bem o2) -> o1.getDescricaoBem().compareToIgnoreCase(o2.getDescricaoBem()));
        }
    }

}
