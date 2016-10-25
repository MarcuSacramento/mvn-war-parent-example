package br.gov.mj.side.web.view.programa.vincularBemKit;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.apoio.entidades.Elemento;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.Kit;
import br.gov.mj.side.entidades.programa.ProgramaBem;
import br.gov.mj.side.entidades.programa.ProgramaKit;
import br.gov.mj.side.web.service.BemService;
import br.gov.mj.side.web.service.ElementoService;
import br.gov.mj.side.web.service.KitService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.kit.KitPage;
import br.gov.mj.side.web.view.programa.ProgramaPage;
import br.gov.mj.side.web.view.programa.ProgramaPesquisaPage;
import br.gov.mj.side.web.view.template.TemplatePage;

public class VincularKitPesquisaPage extends TemplatePage {
    private static final String PAGINATOR_ADICIONADO = "paginatorAdicionado";
    private static final String MJ_ARROW_ASC = "mj_arrow_asc";
    private static final long serialVersionUID = 1L;
    private static final String DESCRICAO = "descricao";

    private static final String VALOR = "valor";
    private static final String VALORS = "valorSelecionado";

    private static final String PAGINATOR = "paginator";

    private static final String CLASS = "class";

    @Inject
    private ComponentFactory componentFactory;

    private Form<Void> form;
    private Page backPage;

    private PanelPesquisa panelPesquisa;
    private PanelDataView panelDataView;
    private PanelDataViewAdicionado panelDataViewAdicionado;
    private Kit objKit = new Kit();
    private Bem bem = new Bem();
    private Elemento elemento = new Elemento();
    private int paginas = 10;
    private int paginasAdicionado = 10;
    private int valor = 10;
    private int valorSelecionado = 10;
    private String coluna = "nome";
    private int order = 1;
    private int orderAdicionado = 1;
    private Integer quantidade;
    private boolean atualizandoLista;
    private AttributeAppender classArrow = new AttributeAppender(CLASS, MJ_ARROW_ASC, " ");
    private AttributeAppender classArrowAdicionado = new AttributeAppender(CLASS, MJ_ARROW_ASC, " ");
    private AttributeAppender classArrowUnsorted = new AttributeAppender(CLASS, "mj_arrow_unsorted", " ");

    private List<Kit> listaKits;
    private List<Kit> listaKitsSelecionados; // Kits do lado direito
    private List<Kit> listaKitsEsquerdaTemp;// Irá armazenar os kits ao clicar
                                            // sobre ele
    private List<ProgramaKit> listaKitsDireitaTemp;// Irá armazenar os kits ao
                                                   // clicar sobre ele do lado
                                                   // direito
    private List<ProgramaKit> listaKitsSelecionadosDireito;
    private List<ProgramaKit> listaKitsSelecionadosDireitoTemp;
    private List<Bem> listaBens;
    private List<Elemento> listaElementos;
    private List<Kit> listaKitsPesquisa;
    private DataView<Kit> dataview;
    private DataView<ProgramaKit> dataviewAdicionado;
    private InfraAjaxPagingNavigator paginator;
    private InfraAjaxPagingNavigator paginatorAdicionado;

    @Inject
    private ElementoService elementoService;

    @Inject
    private BemService bemService;

    @Inject
    private KitService kitService;

    public VincularKitPesquisaPage(final PageParameters pageParameters, Page backPage) {
        super(pageParameters);

        this.backPage = backPage;
        initComponents();
        // criarBreadcrumb();
    }

    public VincularKitPesquisaPage(final PageParameters pageParameters) {
        super(pageParameters);

        initComponents();
        // criarBreadcrumb();
    }

    private void criarBreadcrumb() {
        form.add(new Link("dashboard") {

            @Override
            public void onClick() {
                setResponsePage(HomePage.class);
            }
        });

        form.add(new Link("pesquisarPage") {

            @Override
            public void onClick() {
                setResponsePage(ProgramaPesquisaPage.class);
            }
        });

        form.add(new Link("cadastrarPage") {

            @Override
            public void onClick() {
                cancelarAcoesLista();
                setResponsePage(backPage);
            }
        });
    }

    private void initComponents() {
        form = new Form<Void>("form");
        add(form);

        listaKitsSelecionados = new ArrayList<Kit>();
        listaKitsEsquerdaTemp = new ArrayList<Kit>();
        listaKitsDireitaTemp = new ArrayList<ProgramaKit>();

        ProgramaPage page = (ProgramaPage) backPage;
        listaKitsSelecionadosDireito = page.getForm().getModelObject().getProgramaKits() != null ? page.getForm().getModelObject().getProgramaKits() : new ArrayList<ProgramaKit>();
        copiarParaListaTemp();

        panelPesquisa = new PanelPesquisa("panelPesquisa");
        panelDataView = new PanelDataView("panelDataView");
        panelDataViewAdicionado = new PanelDataViewAdicionado("panelDataViewAdicionado");

        form.add(panelPesquisa);
        form.add(panelDataView);
        form.add(panelDataViewAdicionado);
        form.add(getButtonConfirmar());
        form.add(getButtonVoltar());

        setTitulo("Vincular Kits");

        if (listaKitsSelecionadosDireito.isEmpty()) {
            panelDataView.setVisible(false);
            panelDataViewAdicionado.setVisible(false);
            atualizandoLista = false;
        } else {
            panelDataView.setVisible(true);
            panelDataViewAdicionado.setVisible(true);
            atualizandoLista = true;
        }
    }

    /*
     * ABAIXO SERÃO DESENVOLVIDOS OS PAINEIS
     */

    public class PanelPesquisa extends WebMarkupContainer {
        public PanelPesquisa(String id) {
            super(id);
            this.setOutputMarkupId(true);
            add(getDropDownNomeKit());// nomeKitAuto
            add(getDropDownNomeBem());// nomeBemAuto
            add(getDropDownNomeElemento());// nomeElementoAuto
            add(getButtonPesquisar());// btnPesquisar
        }
    }

    public class PanelDataView extends WebMarkupContainer {
        public PanelDataView(String id) {
            super(id);

            this.setOutputMarkupId(true);
            add(getDataViewKits());
            add(getPaginator());
            add(getDropDownPaginator());
            add(getButtonPick1()); // btnPick1
            add(getButtonOrdenarNome());// btnOrdenarNome
            add(getButtonOrdenarDescricao());// btnOrdenarDescricao
            add(getLabelSortNome());// lblOrderNome
            add(getLabelSortDescricao());// lblOrderDescricao
        }
    }

    public class PanelDataViewAdicionado extends WebMarkupContainer {
        public PanelDataViewAdicionado(String id) {
            super(id);

            this.setOutputMarkupId(true);
            add(getDataViewKitsAdicionados());
            add(getPaginatorAdicionado());
            add(getButtonPick2()); // btnPick2
            add(getDropDownPaginatorAdicionado());
            add(getButtonOrdenarNomeAdicionado());// btnOrdenarNomeAdicionado
            add(getButtonOrdenarDescricaoAdicionado());// btnOrdenarDescricaoAdicionado
            add(getLabelSortNomeAdicionado());// lblOrderNomeAdicionado
            add(getLabelSortDescricaoAdicionado());// lblOrderDescricaoAdicionado
        }
    }

    private class PanelTextFieldQuantidade extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelTextFieldQuantidade(String id, Item<ProgramaKit> item) {
            super(id);
            TextField<Integer> text = new TextField<Integer>("quantidade", new PropertyModel<Integer>(item.getModel(), "quantidade"));
            text.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onUpdate(AjaxRequestTarget target) {

                }
            });

            add(text);
        }
    }

    private class PanelTextFieldQuantidadeMaxima extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelTextFieldQuantidadeMaxima(String id, Item<ProgramaKit> item) {
            super(id);
            TextField<Integer> text = new TextField<Integer>("quantidadeMaximaItemPanel", new PropertyModel<Integer>(item.getModel(), "quantidadePorProposta"));
            text.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onUpdate(AjaxRequestTarget target) {

                }
            });

            add(text);
        }
    }

    /*
     * ABAIXO SERÃO IMPLEMENTADOS OS COMPONENTES DOS PAINEIS
     */

    public DropDownChoice<Kit> getDropDownNomeKit() {
        if (listaKits == null) {
            listaKits = kitService.buscar(new Kit());
        }
        DropDownChoice<Kit> drop = new DropDownChoice<Kit>("nomeKitAuto", new PropertyModel<Kit>(this, "objKit"), listaKits, new ChoiceRenderer<Kit>("nomeKit"));
        drop.setNullValid(true);
        return drop;
    }

    public DropDownChoice<Bem> getDropDownNomeBem() {
        listaBens = bemService.buscar(new Bem());
        DropDownChoice<Bem> drop = new DropDownChoice<Bem>("nomeBemAuto", new PropertyModel<Bem>(this, "bem"), listaBens, new ChoiceRenderer<Bem>("nomeBem"));
        drop.setNullValid(true);
        return drop;
    }

    public DropDownChoice<Elemento> getDropDownNomeElemento() {
        if (listaElementos == null) {
            listaElementos = elementoService.buscar(new Elemento());
        }
        DropDownChoice<Elemento> drop = new DropDownChoice<Elemento>("nomeElementoAuto", new PropertyModel<Elemento>(this, "elemento"), listaElementos, new ChoiceRenderer<Elemento>("nomeECodigo"));
        drop.setNullValid(true);
        return drop;
    }

    public InfraAjaxFallbackLink<Void> getButtonNovo() {
        return componentFactory.newAjaxFallbackLink("btnNovo", (target) -> adicionarNovo());
    }

    private void adicionarNovo() {
        setResponsePage(new KitPage(null, this, new Kit(), "novo"));
    }

    private void acaoConfirmar() {

        int cont = 0;
        int maximoMaiorQueLimite = 0;

        if (listaKitsSelecionadosDireito != null && listaKitsSelecionadosDireito.isEmpty()) {
            addMsgError("Não existem Kits selecionados na lista.");
            return;
        }

        for (ProgramaKit kit : listaKitsSelecionadosDireito) {
            if (kit.getQuantidade() == null || kit.getQuantidade() < 1) {
                cont++;
            }

            if (kit.getQuantidadePorProposta() == null || kit.getQuantidadePorProposta() < 1) {
                kit.setQuantidadePorProposta(0);
            }

            if (kit.getQuantidadePorProposta() != null && kit.getQuantidade() != null && (kit.getQuantidadePorProposta() > kit.getQuantidade())) {
                maximoMaiorQueLimite++;
            }
        }

        if (cont > 0) {
            addMsgError("Existe Kit adicionado a lista com a QUANTIDADE '0', informe um valor para estes campos.");
            return;
        }

        if (maximoMaiorQueLimite > 0) {
            addMsgError("Existe Kit com a QUANTIDADE MÁXIMA POR PROPOSTA maior do que a QUANTIDADE TOTAL.");
            return;
        }

        if (atualizandoLista) {
            getSession().info("Lista de Kits atualizada com sucesso.");
        } else {
            getSession().info("Lista de Kits criada com sucesso.");
        }

        ProgramaPage page = (ProgramaPage) backPage;
        calcularValorMaximoProposta(page);

        page.getForm().getModelObject().setProgramaKits(listaKitsSelecionadosDireito);
        setResponsePage(backPage);
    }

    private void calcularValorMaximoProposta(ProgramaPage page) {
        BigDecimal valorTempBem = new BigDecimal(0);
        for (ProgramaBem pb : page.getForm().getModelObject().getProgramaBens()) {
            BigDecimal quantidade;
            if (pb.getQuantidadePorProposta() == null) {
                quantidade = new BigDecimal("0");
            } else {
                quantidade = new BigDecimal(pb.getQuantidadePorProposta());
            }
            valorTempBem = valorTempBem.add(quantidade.multiply(pb.getBem().getValorEstimadoBem()));
        }

        BigDecimal valorTempKit = new BigDecimal(0);
        for (ProgramaKit pb : listaKitsSelecionadosDireito) {
            BigDecimal quantidade;
            if (pb.getQuantidadePorProposta() == null) {
                quantidade = new BigDecimal("0");
            } else {
                quantidade = new BigDecimal(pb.getQuantidadePorProposta());
            }
            valorTempKit = valorTempKit.add(quantidade.multiply(pb.getKit().getValorEstimado()));
        }
        BigDecimal valorSomar = new BigDecimal("0");

        valorSomar = valorSomar.add(valorTempBem);
        valorSomar = valorSomar.add(valorTempKit);
        page.getForm().getModelObject().setValorMaximoProposta(valorSomar);
    }

    private void acaoVoltar(AjaxRequestTarget target) {
        cancelarAcoesLista();

        ProgramaPage page = (ProgramaPage) backPage;
        page.actionAba(target, "vincularBemKit");

        setResponsePage(backPage);
    }

    // Passa todos os resultados da esquerda para a direita do PickList
    private void actionPick1(AjaxRequestTarget target) {

        for (int i = listaKitsPesquisa.size() - 1; i > -1; i--) {
            int contain = 0;
            for (ProgramaKit select : listaKitsSelecionadosDireito) {
                if (select.getKit().equals(listaKitsPesquisa.get(i))) {
                    contain++;
                    break;
                }
            }

            // Se este kit não estiver adicionado ainda a lista a direita e
            // removendo a esquerda
            if (contain == 0) {
                ProgramaKit kit = new ProgramaKit();
                kit.setKit(listaKitsPesquisa.get(i));

                listaKitsSelecionadosDireito.add(kit);
            }
            contain = 0;
        }

        listaKitsPesquisa.clear();
        listaKitsEsquerdaTemp.clear();

        panelDataViewAdicionado.addOrReplace(getDataViewKitsAdicionados());
        panelDataViewAdicionado.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR_ADICIONADO, dataviewAdicionado));
        panelDataView.addOrReplace(getDataViewKits());
        panelDataView.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR, dataview));

        target.add(panelDataViewAdicionado);
        target.add(panelDataView);
    }

    private void actionPick2(AjaxRequestTarget target) {

        for (ProgramaKit kit : listaKitsSelecionadosDireito) {
            if (!listaKitsPesquisa.contains(kit.getKit())) {
                listaKitsPesquisa.add(kit.getKit());
            }
        }
        listaKitsSelecionadosDireito.clear();
        listaKitsDireitaTemp.clear();

        panelDataViewAdicionado.addOrReplace(getDataViewKitsAdicionados());
        panelDataView.addOrReplace(getDataViewKits());
        panelDataView.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR, dataview));
        panelDataViewAdicionado.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR_ADICIONADO, dataviewAdicionado));

        target.add(panelDataViewAdicionado);
        target.add(panelDataView);
    }

    private void actionAdicionarKit(AjaxRequestTarget target, Item<Kit> item) {

        ProgramaKit kit = new ProgramaKit();
        kit.setKit(item.getModelObject());
        listaKitsSelecionadosDireito.add(0, kit);

        listaKitsPesquisa.remove(item.getModelObject());

        panelDataViewAdicionado.addOrReplace(getDataViewKitsAdicionados());
        panelDataViewAdicionado.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR_ADICIONADO, dataviewAdicionado));
        panelDataView.addOrReplace(getDataViewKits());
        panelDataView.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR, dataview));

        target.add(panelDataViewAdicionado);
        target.add(panelDataView);
        return;
    }

    private void actionRemoverKit(AjaxRequestTarget target, Item<ProgramaKit> item) {

        ProgramaKit kitRemove = item.getModelObject();
        int cont = 0;
        for (ProgramaKit kit : listaKitsSelecionadosDireito) {
            if (kitRemove.getKit().getId().intValue() == kit.getKit().getId().intValue()) {
                listaKitsPesquisa.add(0, kit.getKit());
                listaKitsSelecionadosDireito.remove(cont);
                break;
            }
            cont++;
        }

        panelDataViewAdicionado.addOrReplace(getDataViewKitsAdicionados());
        panelDataViewAdicionado.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR_ADICIONADO, dataviewAdicionado));
        panelDataView.addOrReplace(getDataViewKits());
        panelDataView.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR, dataview));

        target.add(panelDataViewAdicionado);
        target.add(panelDataView);
        return;
    }

    private void editarKit(Item<Kit> item) {
        setResponsePage(new KitPage(null, this, item.getModelObject(), "editar"));
    }

    private void visualizarKit(Item<Kit> item) {
        setResponsePage(new KitPage(null, this, item.getModelObject(), "visualizar"));
    }

    public AjaxSubmitLink getButtonPesquisar() {
        return new AjaxSubmitLink("btnPesquisar", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                pesquisar();

                if (!listaKitsPesquisa.isEmpty()) {
                    panelDataView.setVisible(true);
                    panelDataViewAdicionado.setVisible(true);
                } else {
                    addMsgError("MSG004");
                }
                panelDataView.addOrReplace(getDataViewKits());
                panelDataView.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR, dataview));

                target.add(panelDataView);
                target.add(panelDataViewAdicionado);
            }
        };
    }

    public DropDownChoice<Integer> getDropDownPaginator() {
        DropDownChoice<Integer> dropPaginator = new DropDownChoice<Integer>("valoresPaginator", new PropertyModel<Integer>(this, VALOR), Constants.QUANTIDADE_ITENS_TABELA);
        dropPaginator.setOutputMarkupId(true);
        actionDropDownPaginator(dropPaginator);
        return dropPaginator;
    }

    public DropDownChoice<Integer> getDropDownPaginatorAdicionado() {
        DropDownChoice<Integer> dropPaginator = new DropDownChoice<Integer>("valoresPaginatorAdicionado", new PropertyModel<Integer>(this, VALORS), Constants.QUANTIDADE_ITENS_TABELA);
        dropPaginator.setOutputMarkupId(true);
        actionDropDownPaginatorAdicionado(dropPaginator);
        return dropPaginator;
    }

    public void actionDropDownPaginator(DropDownChoice dropElemento) {
        dropElemento.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                paginas = valor;
                dataview.setItemsPerPage(paginas);
                panelDataView.addOrReplace(dataview);
                target.add(panelDataView);
            }
        });
    }

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

    // DATAVIEW
    public DataView<Kit> getDataViewKits() {
        if (listaKitsPesquisa == null) {
            listaKitsPesquisa = new ArrayList<Kit>();
        }

        dataview = new DataView<Kit>("dataBens", new KitProvider()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<Kit> item) {

                item.clearOriginalDestination();
                item.add(new Label("nomeKit", item.getModelObject().getNomeKit()));
                item.add(new Label(DESCRICAO, item.getModelObject().getDescricaoKit()));
                item.add(getButtonAdicionarKit(item)); // btnAdicionarKit
            }
        };
        dataview.setItemsPerPage(paginas);

        return dataview;
    }

    public DataView<ProgramaKit> getDataViewKitsAdicionados() {
        if (listaKitsSelecionadosDireito == null) {
            listaKitsSelecionadosDireito = new ArrayList<ProgramaKit>();
        }

        dataviewAdicionado = new DataView<ProgramaKit>("dataBensAdicionado", new KitSelecionadosProvider()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ProgramaKit> item) {
                item.clearOriginalDestination();
                item.add(new Label("nomeKitAdicionado", item.getModelObject().getKit().getNomeKit()));
                item.add(new Label("descricaoKitAdicionado", item.getModelObject().getKit().getDescricaoKit()));
                item.add(getButtonRemoverKit(item)); // btnRemoverKit
                item.add(new PanelTextFieldQuantidade("panelText", item));
                item.add(new PanelTextFieldQuantidadeMaxima("quantidadeMaximaItemPanel", item));

            }
        };
        dataviewAdicionado.setItemsPerPage(paginasAdicionado);
        return dataviewAdicionado;
    }

    public InfraAjaxPagingNavigator getPaginator() {
        if (paginator == null) {
            paginator = new InfraAjaxPagingNavigator(PAGINATOR, dataview);
        }
        return paginator;
    }

    public InfraAjaxPagingNavigator getPaginatorAdicionado() {
        if (paginatorAdicionado == null) {
            paginatorAdicionado = new InfraAjaxPagingNavigator(PAGINATOR_ADICIONADO, dataviewAdicionado);
        }
        return paginatorAdicionado;
    }

    public InfraAjaxFallbackLink<Void> getButtonPick1() {
        return componentFactory.newAjaxFallbackLink("btnPick1", (target) -> actionPick1(target));
    }

    public InfraAjaxFallbackLink<Void> getButtonPick2() {
        return componentFactory.newAjaxFallbackLink("btnPick2", (target) -> actionPick2(target));
    }

    public AjaxSubmitLink getButtonAdicionarKit(Item<Kit> item) {
        return new AjaxSubmitLink("btnAdicionarKit", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                actionAdicionarKit(target, item);
            }
        };
    }

    public AjaxSubmitLink getButtonRemoverKit(Item<ProgramaKit> item) {
        return new AjaxSubmitLink("btnRemoverKit", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                actionRemoverKit(target, item);
            }
        };
    }

    private Button getButtonConfirmar() {
        return componentFactory.newButton("btnAvancar", () -> acaoConfirmar());
    }

    public InfraAjaxFallbackLink<Void> getButtonVoltar() {
        return componentFactory.newAjaxFallbackLink("btnVoltar", (target) -> acaoVoltar(target));
    }

    public void mudarOrdemTabela() {
        if (order == 1) {
            order = -1;
            classArrow = new AttributeAppender(CLASS, "mj_arrow_desc", " ");
        } else {
            order = 1;
            classArrow = new AttributeAppender(CLASS, MJ_ARROW_ASC, " ");
        }
    }

    public void mudarOrdemTabelaAdicionado() {
        if (orderAdicionado == 1) {
            orderAdicionado = -1;
            classArrowAdicionado = new AttributeAppender(CLASS, "mj_arrow_desc", " ");
        } else {
            orderAdicionado = 1;
            classArrowAdicionado = new AttributeAppender(CLASS, MJ_ARROW_ASC, " ");
        }
    }

    public AjaxSubmitLink getButtonOrdenarNome() {
        return new AjaxSubmitLink("btnOrdenarNome", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                coluna = "nome";

                mudarOrdemTabela();
                atualizarSetasOrdenacao(target);
            }
        };
    }

    public AjaxSubmitLink getButtonOrdenarNomeAdicionado() {
        return new AjaxSubmitLink("btnOrdenarNomeAdicionado", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                coluna = "nome";
                mudarOrdemTabelaAdicionado();
                atualizarSetasOrdenacaoAdicionado(target);
            }
        };
    }

    public AjaxSubmitLink getButtonOrdenarDescricao() {
        return new AjaxSubmitLink("btnOrdenarDescricao", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                coluna = DESCRICAO;
                mudarOrdemTabela();
                atualizarSetasOrdenacao(target);
            }
        };
    }

    public AjaxSubmitLink getButtonOrdenarDescricaoAdicionado() {
        return new AjaxSubmitLink("btnOrdenarDescricaoAdicionado", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                coluna = DESCRICAO;
                mudarOrdemTabelaAdicionado();
                atualizarSetasOrdenacaoAdicionado(target);
            }
        };
    }

    public Label getLabelSortNome() {
        Label label = null;
        label = new Label("lblOrderNome", "...");

        if ("nome".equalsIgnoreCase(coluna)) {
            label.add(classArrow);
        } else {
            label.add(classArrowUnsorted);
        }
        return label;
    }

    public Label getLabelSortNomeAdicionado() {
        Label label = null;
        label = new Label("lblOrderNomeAdicionado", "...");

        if ("nome".equalsIgnoreCase(coluna)) {
            label.add(classArrowAdicionado);
        } else {
            label.add(classArrowUnsorted);
        }
        return label;
    }

    public Label getLabelSortDescricao() {
        Label label = new Label("lblOrderDescricao", "...");

        if (DESCRICAO.equalsIgnoreCase(coluna)) {
            label.add(classArrow);
        } else {
            label.add(classArrowUnsorted);
        }
        return label;
    }

    public Label getLabelSortDescricaoAdicionado() {
        Label label = new Label("lblOrderDescricaoAdicionado", "...");

        if (DESCRICAO.equalsIgnoreCase(coluna)) {
            label.add(classArrowAdicionado);
        } else {
            label.add(classArrowUnsorted);
        }
        return label;
    }

    private class KitProvider implements IDataProvider<Kit> {
        private static final long serialVersionUID = 1L;

        public KitProvider() {
        }

        @Override
        public void detach() {
            // Verificar a usabilidade desse método
        }

        @Override
        public Iterator<Kit> iterator(long first, long size) {
            List<Kit> kitTemp = new ArrayList<Kit>();
            int firstTemp = 0;
            int flagTemp = 0;
            for (Kit k : listaKitsPesquisa) {
                if (firstTemp >= first) {
                    if (flagTemp <= size) {
                        kitTemp.add(k);
                        flagTemp++;
                    }
                }
                firstTemp++;
            }

            return kitTemp.iterator();
        }

        @Override
        public long size() {
            return listaKitsPesquisa.size();
        }

        @Override
        public IModel<Kit> model(Kit object) {
            return new CompoundPropertyModel<Kit>(object);
        }
    }

    private class KitSelecionadosProvider implements IDataProvider<ProgramaKit> {
        private static final long serialVersionUID = 1L;

        public KitSelecionadosProvider() {
        }

        @Override
        public void detach() {
            // Verificar a usabilidade desse método
        }

        @Override
        public Iterator<ProgramaKit> iterator(long first, long size) {

            List<ProgramaKit> kitTemp = new ArrayList<ProgramaKit>();
            int firstTemp = 0;
            int flagTemp = 0;
            for (ProgramaKit k : listaKitsSelecionadosDireito) {
                if (firstTemp >= first) {
                    if (flagTemp <= size) {
                        kitTemp.add(k);
                        flagTemp++;
                    }
                }
                firstTemp++;
            }

            return kitTemp.iterator();
        }

        @Override
        public long size() {
            return listaKitsSelecionadosDireito.size();
        }

        @Override
        public IModel<ProgramaKit> model(ProgramaKit object) {
            return new CompoundPropertyModel<ProgramaKit>(object);
        }
    }

    /*
     * AS AÇÕES SERÃO INSERIDAS ABAIXO
     */

    public void pesquisar() {
        listaKitsPesquisa = kitService.pesquisar(objKit, bem, elemento);

        for (int i = listaKitsSelecionadosDireito.size() - 1; i > -1; i--) {
            int contain = 0;
            int posicao = 0;
            for (Kit selecionado : listaKitsPesquisa) {
                if (listaKitsSelecionadosDireito.get(i).getKit().getId().intValue() == selecionado.getId().intValue()) {
                    contain++;
                    break;
                }
                posicao++;
            }

            if (contain != 0) {
                listaKitsPesquisa.remove(posicao);
            }
        }
    }

    public List<Kit> ordenarKit(List<Kit> lista, String coluna, int order) {
        Collections.sort(lista, Kit.getComparator(order, coluna));
        return lista;
    }

    public List<ProgramaKit> ordenarProgramaKit(List<ProgramaKit> lista, String coluna, int order) {
        Collections.sort(lista, ProgramaKit.getComparator(order, coluna));
        return lista;
    }

    public void apagarKit(Item<Kit> item) {
        Kit apagar = item.getModelObject();
        listaKitsPesquisa.remove(apagar);
        kitService.excluir(apagar.getId());
    }

    public void atualizarDataView(AjaxRequestTarget target) {
        panelDataView.addOrReplace(getDataViewKits());
        target.add(panelDataView);
    }

    private void excluirAtributo(AjaxRequestTarget target, Item<Kit> item) {

        Kit apagar = item.getModelObject();
        listaKitsPesquisa.remove(apagar);
        kitService.excluir(apagar.getId());

        if (target != null) {
            atualizarDataView(target);
        }

        addMsgInfo("Excluido com Sucesso.");
    }

    public InfraAjaxFallbackLink<Void> getButtonEditar(Item<Kit> item) {
        return componentFactory.newAjaxFallbackLink("btnEditar", (target) -> editarKit(item));
    }

    public InfraAjaxFallbackLink<Void> getButtonVisualizar(Item<Kit> item) {
        return componentFactory.newAjaxFallbackLink("btnVisualizar", (target) -> visualizarKit(item));
    }

    public InfraAjaxConfirmButton getButtonExcluir(Item<Kit> item) {
        return componentFactory.newAJaxConfirmButton("btnExcluir", "MSG001", form, (target, formz) -> excluirAtributo(target, item));
    }

    public void atualizarSetasOrdenacao(AjaxRequestTarget target) {

        listaKitsPesquisa = ordenarKit(listaKitsPesquisa, coluna, order);

        panelDataView.addOrReplace(getDataViewKits());
        panelDataView.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR, dataview));
        panelDataView.addOrReplace(getLabelSortNome());
        panelDataView.addOrReplace(getLabelSortDescricao());
        target.add(panelDataView);
    }

    public void atualizarSetasOrdenacaoAdicionado(AjaxRequestTarget target) {

        listaKitsSelecionadosDireito = ordenarProgramaKit(listaKitsSelecionadosDireito, coluna, orderAdicionado);

        panelDataViewAdicionado.addOrReplace(getDataViewKitsAdicionados());
        panelDataViewAdicionado.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR_ADICIONADO, dataviewAdicionado));
        panelDataViewAdicionado.addOrReplace(getLabelSortNomeAdicionado());
        panelDataViewAdicionado.addOrReplace(getLabelSortDescricaoAdicionado());
        target.add(panelDataViewAdicionado);
    }

    public void copiarParaListaTemp() {
        listaKitsSelecionadosDireitoTemp = new ArrayList<ProgramaKit>();
        for (ProgramaKit kit : listaKitsSelecionadosDireito) {
            ProgramaKit novoKit = new ProgramaKit();
            novoKit.setId(kit.getId());
            novoKit.setKit(kit.getKit());
            novoKit.setPrograma(kit.getPrograma());
            ;
            novoKit.setQuantidade(kit.getQuantidade());
            novoKit.setQuantidadePorProposta(kit.getQuantidadePorProposta());
            listaKitsSelecionadosDireitoTemp.add(novoKit);
        }
    }

    public void cancelarAcoesLista() {
        listaKitsSelecionadosDireito.clear();
        for (ProgramaKit kit : listaKitsSelecionadosDireitoTemp) {
            listaKitsSelecionadosDireito.add(kit);
        }
    }

    public String formatoDinheiro(double bigDecimal) {
        return NumberFormat.getCurrencyInstance().format(bigDecimal);
    }

    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }

    public List<Kit> getListaKitsSelecionados() {
        return listaKitsSelecionados;
    }

    public void setListaKitsSelecionados(List<Kit> listaKitsSelecionados) {
        this.listaKitsSelecionados = listaKitsSelecionados;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
