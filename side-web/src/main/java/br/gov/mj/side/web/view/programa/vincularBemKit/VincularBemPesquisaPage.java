package br.gov.mj.side.web.view.programa.vincularBemKit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.apoio.entidades.Elemento;
import br.gov.mj.apoio.entidades.SubElemento;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.listener.FeedbackPanelListener;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.programa.ProgramaBem;
import br.gov.mj.side.entidades.programa.ProgramaKit;
import br.gov.mj.side.web.service.BemService;
import br.gov.mj.side.web.service.ElementoService;
import br.gov.mj.side.web.service.SubElementoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.bem.BemPage;
import br.gov.mj.side.web.view.programa.ProgramaPage;
import br.gov.mj.side.web.view.programa.ProgramaPesquisaPage;
import br.gov.mj.side.web.view.template.TemplatePage;

public class VincularBemPesquisaPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    private static final String NOME_BEM_ADICIONADO = "nomeBemAdicionado";
    private static final String NOME_BEM = "nomeBem";
    private static final String VALORS = "valorSelecionado";
    private static final String PAGINATOR1 = "pagination";

    private Page backPage;

    private Bem entity; // Model
    private Elemento elemento = new Elemento();
    private Integer itensPorPagina = 10;
    private boolean pesquisado = false;
    private List<Bem> listBensPesquisados;
    private List<ProgramaBem> listaBensSelecionadosDireito;
    private List<ProgramaBem> listaBensSelecionadosDireitoTemp;
    private String quantidade;
    private int paginasAdicionado = 10;
    private int valorSelecionado = 10;
    private boolean atualizandoLista;
    private BemSelecionadosProvider dp;
    private BemProvider bemProvider;

    private Form<Bem> form;
    private InfraDropDownChoice<SubElemento> dropSubElemento;
    private PanelGridResultados panelGridResultados;
    private PanelDataViewAdicionado panelDataViewAdicionado;
    private DataView<Bem> dataViewBem;
    private DataView<ProgramaBem> dataviewAdicionado;
    private PanelSubelemento panelSubelemento;
    private InfraAjaxPagingNavigator paginatorAdicionado;
    private InfraAjaxPagingNavigator paginator;

    private Button btnPesquisar;

    @Inject
    private ElementoService elementoService;
    @Inject
    private SubElementoService subElementoService;
    @Inject
    private BemService bemService;
    @Inject
    private ComponentFactory componentFactory;

    public VincularBemPesquisaPage(final PageParameters pageParameters) {
        super(pageParameters);
        setTitulo("Pesquisar Bem");
        setEntity(new Bem());
        initComponents();
        //criarBreadcrumb();
        initVariaveis();
        visualizarOcultarDataViews();
    }

    public VincularBemPesquisaPage(final PageParameters pageParameters, Page backPage) {
        super(pageParameters);

        this.backPage = backPage;

        setTitulo("Vincular Bem");
        setEntity(new Bem());
        initComponents();
        //criarBreadcrumb();
        initVariaveis();
        visualizarOcultarDataViews();
    }

    protected void initComponents() {

        form = componentFactory.newForm("form", new CompoundPropertyModel<Bem>(entity));
        form.add(componentFactory.newTextField(NOME_BEM, "Nome do Bem", false, null));

        panelSubelemento = new PanelSubelemento("panelSubelemento");
        form.add(panelSubelemento);

        btnPesquisar = getButtonPesquisar();
        form.add(btnPesquisar);

        panelGridResultados = new PanelGridResultados("panelGridResultados");
        panelGridResultados.setOutputMarkupId(true);
        panelGridResultados.setVisible(isPesquisado());
        form.add(panelGridResultados);

        panelDataViewAdicionado = new PanelDataViewAdicionado("panelDataViewAdicionado");
        form.add(panelDataViewAdicionado);

        form.add(getButtonConfirmar());
        form.add(getButtonVoltar());

        add(form);
    }

    public void initVariaveis() {
        if (listBensPesquisados == null) {
            listBensPesquisados = new ArrayList<Bem>();
        }

        ProgramaPage page = (ProgramaPage) backPage;
        listaBensSelecionadosDireito = page.getForm().getModelObject().getProgramaBens() != null ? page.getForm().getModelObject().getProgramaBens() : new ArrayList<ProgramaBem>();
        copiarParaListaTemp();
    }

    public void visualizarOcultarDataViews() {
        if (listaBensSelecionadosDireito.isEmpty()) {
            panelDataViewAdicionado.setVisible(false);
            panelGridResultados.setVisible(false);
            atualizandoLista = false;
        } else {
            panelDataViewAdicionado.setVisible(true);
            panelGridResultados.setVisible(true);
            atualizandoLista = true;
        }
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

    private Button getButtonPesquisar() {
        return componentFactory.newButton("btnPesquisar", () -> pesquisar());
    }

    private void pesquisar() {
        if (getEntity().getSubElemento() == null) {
            getEntity().setSubElemento(new SubElemento());
        }
        getEntity().getSubElemento().setElemento(getElemento());

        listBensPesquisados = bemService.buscar(getEntity());
        verificarCamposJaAdicionados();

        setPesquisado(true);
        panelGridResultados.setVisible(isPesquisado());
        panelDataViewAdicionado.setVisible(isPesquisado());
    }

    private void verificarCamposJaAdicionados() {
        for (int i = listaBensSelecionadosDireito.size() - 1; i > -1; i--) {
            int contain = 0;
            int posicao = 0;
            for (Bem selecionado : listBensPesquisados) {
                if (listaBensSelecionadosDireito.get(i).getBem().getId().intValue() == selecionado.getId().intValue()) {
                    contain++;
                    break;
                }
                posicao++;
            }

            if (contain != 0) {
                listBensPesquisados.remove(posicao);
            }
        }
    }

    private DropDownChoice<Elemento> getDropDownChoiceElemento() {
        InfraDropDownChoice<Elemento> dropDownChoice = componentFactory.newDropDownChoice("elemento", "Elemento", false, "id", "nomeECodigo", new LambdaModel<Elemento>(this::getElemento, this::setElemento), listaElementos(), (target) -> atualizarListaSubelementos(target));
        dropDownChoice.setNullValid(true);
        return dropDownChoice;
    }

    private List<Elemento> listaElementos() {
        return elementoService.buscarTodos();
    }

    void atualizarListaSubelementos(AjaxRequestTarget target) {
        dropSubElemento.setChoices(listaSubelementos());
        target.add(panelSubelemento);
    }

    private List<SubElemento> listaSubelementos() {

        if (elemento != null && elemento.getId() != null) {
            Long id = elemento.getId();
            return subElementoService.buscarPeloElementoId(id);
        }
        return Collections.emptyList();
    }

    private InfraDropDownChoice<SubElemento> getDropDownChoiceSubelemento() {
        InfraDropDownChoice<SubElemento> dropDownChoice = componentFactory.newDropDownChoice("subElemento", "Subelemento", false, "id", "nomeECodigo", null, listaSubelementos(), null);
        dropDownChoice.setOutputMarkupId(true);
        dropDownChoice.setNullValid(true);
        return dropDownChoice;
    }

    private class PanelGridResultados extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelGridResultados(String id) {
            super(id);

            bemProvider = new BemProvider();
            add(getDataViewBem());
            add(getDropItensPorPagina());

            // add(getDataViewBensAdicionados());//dataBensAdicionado
            add(getButtonPick1()); // btnPick1
            add(getPaginator());

            add(newOrderByBorderResult("orderByNomeBemResult", NOME_BEM, bemProvider));
            add(newOrderByBorderResult("orderByDescricaoBemResult", "descricaoBem", bemProvider));
        }
    }

    private OrderByBorder<String> newOrderByBorder(String id, String property, BemSelecionadosProvider dp) {
        return new OrderByBorder<String>(id, property, dp);
    }

    private OrderByBorder<String> newOrderByBorderResult(String id, String property, BemProvider dp) {
        return new OrderByBorder<String>(id, property, dp);
    }

    public class PanelDataViewAdicionado extends WebMarkupContainer {

        public PanelDataViewAdicionado(String id) {
            super(id);

            this.setOutputMarkupId(true);
            dp = new BemSelecionadosProvider();
            add(getDataViewBensAdicionados(dp));
            add(getPaginatorAdicionado());
            add(getButtonPick2()); // btnPick2
            add(getDropDownPaginatorAdicionado());

            add(newOrderByBorder("orderByNomeBem", NOME_BEM_ADICIONADO, dp));
            add(newOrderByBorder("orderByDescricao", "descricaoBemAdicionado", dp));
        }
    }

    private class BemProvider extends SortableDataProvider<Bem, String> {
        private static final long serialVersionUID = 1L;

        public BemProvider() {
            setSort("nomeBemAdicionadoResult", SortOrder.ASCENDING);
        }

        @Override
        public Iterator<Bem> iterator(long first, long size) {

            SortParam<String> sort = this.getSort();
            String property = sort.getProperty();

            if (NOME_BEM.equalsIgnoreCase(property)) {
                Collections.sort(listBensPesquisados, Bem.getComparator(sort.isAscending() ? 1 : -1, "nome"));
            } else {
                Collections.sort(listBensPesquisados, Bem.getComparator(sort.isAscending() ? 1 : -1, "descricao"));
            }

            List<Bem> bemTemp = new ArrayList<Bem>();
            int firstTemp = 0;
            int flagTemp = 0;
            for (Bem k : listBensPesquisados) {
                if (firstTemp >= first) {
                    if (flagTemp <= size) {
                        bemTemp.add(k);
                        flagTemp++;
                    }
                }
                firstTemp++;
            }

            return bemTemp.iterator();
        }

        @Override
        public long size() {
            return listBensPesquisados.size();
        }

        @Override
        public IModel<Bem> model(Bem object) {
            return new CompoundPropertyModel<Bem>(object);
        }
    }

    private class BemSelecionadosProvider extends SortableDataProvider<ProgramaBem, String> {
        private static final long serialVersionUID = 1L;

        public BemSelecionadosProvider() {
            setSort(NOME_BEM_ADICIONADO, SortOrder.ASCENDING);
        }

        @Override
        public Iterator<ProgramaBem> iterator(long first, long size) {

            List<ProgramaBem> bemTemp = new ArrayList<ProgramaBem>();

            SortParam<String> sort = this.getSort();
            String property = sort.getProperty();

            if (NOME_BEM_ADICIONADO.equalsIgnoreCase(property)) {
                Collections.sort(listaBensSelecionadosDireito, ProgramaBem.getComparator(sort.isAscending() ? 1 : -1, "nome"));
            } else {
                Collections.sort(listaBensSelecionadosDireito, ProgramaBem.getComparator(sort.isAscending() ? 1 : -1, "descricao"));
            }

            int firstTemp = 0;
            int flagTemp = 0;
            for (ProgramaBem k : listaBensSelecionadosDireito) {
                if (firstTemp >= first) {
                    if (flagTemp <= size) {
                        bemTemp.add(k);
                        flagTemp++;
                    }
                }
                firstTemp++;
            }
            return bemTemp.iterator();
        }

        @Override
        public long size() {
            return listaBensSelecionadosDireito.size();
        }

        @Override
        public IModel<ProgramaBem> model(ProgramaBem object) {
            return new CompoundPropertyModel<ProgramaBem>(object);
        }
    }

    public InfraAjaxPagingNavigator getPaginator() {
        if (paginator == null) {
            paginator = new InfraAjaxPagingNavigator(PAGINATOR1, dataViewBem);
        }
        return paginator;
    }

    public InfraAjaxPagingNavigator getPaginatorAdicionado() {
        if (paginatorAdicionado == null) {
            paginatorAdicionado = new InfraAjaxPagingNavigator("paginatorAdicionado", dataviewAdicionado);
        }
        return paginatorAdicionado;
    }

    public DropDownChoice<Integer> getDropDownPaginatorAdicionado() {
        DropDownChoice<Integer> dropPaginator = new DropDownChoice<Integer>("valoresPaginatorAdicionado", new PropertyModel<Integer>(this, VALORS), Constants.QUANTIDADE_ITENS_TABELA);
        dropPaginator.setOutputMarkupId(true);
        actionDropDownPaginatorAdicionado(dropPaginator);
        return dropPaginator;
    }

    private class PanelSubelemento extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelSubelemento(String id) {
            super(id);

            add(getDropDownChoiceElemento());
            dropSubElemento = getDropDownChoiceSubelemento();
            add(dropSubElemento);
        }

    }

    @SuppressWarnings("unchecked")
    private DataView<Bem> getDataViewBem() {
        dataViewBem = new DataView<Bem>("bens", bemProvider) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<Bem> item) {
                item.add(new Label(NOME_BEM));
                item.add(new Label("descricaoBem"));
                item.add(getButtonAdicionarBem(item)); // btnAdicionarBem

                item.add(AttributeModifier.replace("class", new AbstractReadOnlyModel<String>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String getObject() {
                        return (item.getIndex() % 2 == 1) ? "even" : "odd";
                    }
                }));
            }

        };
        dataViewBem.setItemsPerPage(itensPorPagina);
        return dataViewBem;
    }

    public DataView<ProgramaBem> getDataViewBensAdicionados(BemSelecionadosProvider dp) {
        if (listaBensSelecionadosDireito == null) {
            listaBensSelecionadosDireito = new ArrayList<ProgramaBem>();
        }

        dataviewAdicionado = new DataView<ProgramaBem>("dataBensAdicionado", dp) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ProgramaBem> item) {
                item.clearOriginalDestination();
                item.add(new Label(NOME_BEM_ADICIONADO, item.getModelObject().getBem().getNomeBem()));
                item.add(new Label("descricaoBemAdicionado", item.getModelObject().getBem().getDescricaoBem()));
                item.add(getButtonRemoverBem(item)); // btnRemoverBem
                item.add(new QuantidadeItemPanel("quantidadeItemPanel", item));
                item.add(new QuantidadeMaximaItemPanel("quantidadeMaximaItemPanel", item));
            }
        };
        dataviewAdicionado.setItemsPerPage(valorSelecionado);

        return dataviewAdicionado;
    }

    private class QuantidadeItemPanel extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public QuantidadeItemPanel(String id, Item<ProgramaBem> item) {
            super(id);
            TextField<Integer> tfValorUtilizar = new TextField<Integer>("quantidade", new PropertyModel<Integer>(item.getModel(), "quantidade"));
            tfValorUtilizar.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    // Desabilita o Feedback listener
                    RequestCycle.get().setMetaData(FeedbackPanelListener.SKIP_FEEDBACK_LISTENER, Boolean.TRUE);
                    item.getModelObject();

                }
            });
            add(tfValorUtilizar);
        }
    }
    
    private class QuantidadeMaximaItemPanel extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public QuantidadeMaximaItemPanel(String id, Item<ProgramaBem> item) {
            super(id);
            TextField<Integer> tfValorUtilizar = new TextField<Integer>("quantidadeMaxima", new PropertyModel<Integer>(item.getModel(), "quantidadePorProposta"));
            tfValorUtilizar.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    // Desabilita o Feedback listener
                    RequestCycle.get().setMetaData(FeedbackPanelListener.SKIP_FEEDBACK_LISTENER, Boolean.TRUE);
                    item.getModelObject();

                }
            });
            add(tfValorUtilizar);
        }
    }

    private DropDownChoice<Integer> getDropItensPorPagina() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPagina", new LambdaModel<Integer>(this::getItensPorPagina, this::setItensPorPagina), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewBem.setItemsPerPage(getItensPorPagina());
                target.add(panelGridResultados);
            };
        });
        return dropDownChoice;
    }

    private Button getButtonConfirmar() {
        return componentFactory.newButton("btnAvancar", () -> acaoConfirmar());
    }

    public InfraAjaxFallbackLink<Void> getButtonVoltar() {
        return componentFactory.newAjaxFallbackLink("btnVoltar", (target) -> acaoVoltar());
    }

    public InfraAjaxFallbackLink<Void> getButtonPick1() {
        return componentFactory.newAjaxFallbackLink("btnPick1", (target) -> actionPick1(target));
    }

    public InfraAjaxFallbackLink<Void> getButtonPick2() {
        return componentFactory.newAjaxFallbackLink("btnPick2", (target) -> actionPick2(target));
    }

    public AjaxSubmitLink getButtonAdicionarBem(Item<Bem> item) {
        return new AjaxSubmitLink("btnAdicionarBem", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                actionAdicionarKit(target, item);
            }
        };
    }

    public AjaxSubmitLink getButtonRemoverBem(Item<ProgramaBem> item) {
        return new AjaxSubmitLink("btnRemoverBem", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                actionRemoverKit(target, item);
            }
        };
    }

    /*
     * AS AÇÕES SERÃO IMPLEMENTAS ABAIXO
     */

    public void copiarParaListaTemp() {
        listaBensSelecionadosDireitoTemp = new ArrayList<ProgramaBem>();
        
        for (ProgramaBem bem : listaBensSelecionadosDireito) {
            ProgramaBem novoBem = new ProgramaBem();
            novoBem.setBem(bem.getBem());
            novoBem.setId(bem.getId());
            novoBem.setPrograma(bem.getPrograma());
            novoBem.setQuantidade(bem.getQuantidade());
            novoBem.setQuantidadePorProposta(bem.getQuantidadePorProposta());
            listaBensSelecionadosDireitoTemp.add(novoBem);
        }
    }

    public void cancelarAcoesLista() {
        listaBensSelecionadosDireito.clear();
        for (ProgramaBem bem : listaBensSelecionadosDireitoTemp) {
            ProgramaBem novoBem = new ProgramaBem();
            novoBem = bem;
            listaBensSelecionadosDireito.add(novoBem);
        }
    }

    private void actionPick1(AjaxRequestTarget target) {
        for (int i = listBensPesquisados.size() - 1; i > -1; i--) {
            int contain = 0;
            for (ProgramaBem select : listaBensSelecionadosDireito) {
                if (select.getBem().equals(listBensPesquisados.get(i))) {
                    contain++;
                    break;
                }
            }

            // Se este bem não estiver adicionado ainda a lista a direita e
            // removendo a esquerda
            if (contain == 0) {
                ProgramaBem bem = new ProgramaBem();
                bem.setBem(listBensPesquisados.get(i));

                listaBensSelecionadosDireito.add(bem);
            }
            contain = 0;
        }

        listBensPesquisados.clear();

        atualizarDataViews(target);
    }

    private void actionPick2(AjaxRequestTarget target) {
        for (ProgramaBem bem : listaBensSelecionadosDireito) {
            if (!listBensPesquisados.contains(bem.getBem())) {
                listBensPesquisados.add(bem.getBem());
            }
        }

        listaBensSelecionadosDireito.clear();

        atualizarDataViews(target);
    }

    private void actionAdicionarKit(AjaxRequestTarget target, Item<Bem> item) {

        ProgramaBem bem = new ProgramaBem();
        bem.setBem(item.getModelObject());
        listaBensSelecionadosDireito.add(0, bem);

        listBensPesquisados.remove(item.getModelObject());

        atualizarDataViews(target);
    }

    private void actionRemoverKit(AjaxRequestTarget target, Item<ProgramaBem> item) {

        ProgramaBem bemRemove = item.getModelObject();
        int cont = 0;
        for (ProgramaBem bem : listaBensSelecionadosDireito) {
            if (bemRemove.getBem().getId().intValue() == bem.getBem().getId().intValue()) {
                listBensPesquisados.add(0, bem.getBem());
                listaBensSelecionadosDireito.remove(cont);
                break;
            }
            cont++;
        }
        atualizarDataViews(target);
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

    private void atualizarDataViews(AjaxRequestTarget target) {
        panelGridResultados.addOrReplace(getDataViewBem());
        panelGridResultados.addOrReplace(new InfraAjaxPagingNavigator("pagination", dataViewBem));
        panelDataViewAdicionado.addOrReplace(getDataViewBensAdicionados(dp));
        panelDataViewAdicionado.addOrReplace(new InfraAjaxPagingNavigator("paginatorAdicionado", dataviewAdicionado));
        panelGridResultados.addOrReplace(new InfraAjaxPagingNavigator(PAGINATOR1, dataViewBem));

        target.add(panelGridResultados);
        target.add(panelDataViewAdicionado);
    }

    private void acaoConfirmar() {

        int cont = 0;
        int maximoMaiorQueLimite = 0;
        
        if (listaBensSelecionadosDireito != null && listaBensSelecionadosDireito.isEmpty()) {
            addMsgError("Não existem Bens selecionados na lista.");
            return;
        }

        for (ProgramaBem prog : listaBensSelecionadosDireito) {
            if (prog.getQuantidade() == null || prog.getQuantidade() < 1) {
                cont++;
            }
            
            if(prog.getQuantidadePorProposta() == null){
                prog.setQuantidadePorProposta(0);
            }
            
            if(prog.getQuantidadePorProposta() != null && prog.getQuantidade() != null && (prog.getQuantidadePorProposta() > prog.getQuantidade()))
            {
                maximoMaiorQueLimite ++;
            }
        }

        if (cont > 0) {
            addMsgError("Existe Bem adicionado a lista com a QUANTIDADE TOTAL '0', informe um valor para estes campos.");
            return;
        }
        
        if(maximoMaiorQueLimite > 0)
        {
            addMsgError("Existe Bem com a QUANTIDADE MÁXIMA POR PROPOSTA maior do que a QUANTIDADE TOTAL.");
            return;
        }
        
        

        if (atualizandoLista) {
            getSession().info("Lista de Bens atualizada com sucesso.");
        } else {
            getSession().info("Lista de Bens criada com sucesso.");
        }
        
        ProgramaPage page = (ProgramaPage) backPage;
        
        calcularValorMaximoProposta(page);
        page.getForm().getModelObject().setProgramaBens(listaBensSelecionadosDireito);
        setResponsePage(backPage);
    }
    
    private void calcularValorMaximoProposta(ProgramaPage page)
    {   
        BigDecimal valorTempBem = new BigDecimal(0);
        for(ProgramaBem pb:listaBensSelecionadosDireito)
        {
            BigDecimal quantidade;
            if(pb.getQuantidadePorProposta() == null)
            {
                quantidade = new BigDecimal("0");
            }
            else
            {
                quantidade = new BigDecimal(pb.getQuantidadePorProposta());
            } 
            valorTempBem = valorTempBem.add(quantidade.multiply(pb.getBem().getValorEstimadoBem()));
        }
        
        BigDecimal valorTempKit = new BigDecimal(0);
        for(ProgramaKit pb:page.getForm().getModelObject().getProgramaKits())
        {
            BigDecimal quantidade;
            if(pb.getQuantidadePorProposta() == null)
            {
                quantidade = new BigDecimal("0");
            }
            else
            {
                quantidade = new BigDecimal(pb.getQuantidadePorProposta());
            } 
            valorTempKit = valorTempKit.add(quantidade.multiply(pb.getKit().getValorEstimado()));
        }
        BigDecimal valorSomar = new BigDecimal("0");
        
        valorSomar = valorSomar.add(valorTempBem);
        valorSomar = valorSomar.add(valorTempKit);
        page.getForm().getModelObject().setValorMaximoProposta(valorSomar);
    }

    private void acaoVoltar() {
        cancelarAcoesLista();
        setResponsePage(backPage);
    }

    protected void visualizar(Item<Bem> item) {
        Bem b = item.getModelObject();
        setResponsePage(new BemPage(null, b, this, true,false));
    }

    public Elemento getElemento() {
        return elemento;
    }

    public void setElemento(Elemento elemento) {
        this.elemento = elemento;
    }

    public Bem getEntity() {
        return entity;
    }

    public void setEntity(Bem entity) {
        this.entity = entity;
    }

    public Integer getItensPorPagina() {
        return itensPorPagina;
    }

    public void setItensPorPagina(Integer itensPorPagina) {
        this.itensPorPagina = itensPorPagina;
    }

    public boolean isPesquisado() {
        return pesquisado;
    }

    public void setPesquisado(boolean pesquisado) {
        this.pesquisado = pesquisado;
    }

    public DataView<ProgramaBem> getDataviewAdicionado() {
        return dataviewAdicionado;
    }

    public void setDataviewAdicionado(DataView<ProgramaBem> dataviewAdicionado) {
        this.dataviewAdicionado = dataviewAdicionado;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }

}