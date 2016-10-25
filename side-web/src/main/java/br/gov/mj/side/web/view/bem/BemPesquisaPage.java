package br.gov.mj.side.web.view.bem;

import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.jasperreports.JRConcreteResource;
import org.wicketstuff.jasperreports.handlers.CsvResourceHandler;

import br.gov.mj.apoio.entidades.Elemento;
import br.gov.mj.apoio.entidades.SubElemento;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.web.service.BemService;
import br.gov.mj.side.web.service.ElementoService;
import br.gov.mj.side.web.service.SubElementoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.RelatorioBemPesquisaDtoBuilder;
import br.gov.mj.side.web.util.SortableBemDataProvider;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.template.TemplatePage;

@AuthorizeInstantiation({ BemPesquisaPage.ROLE_MANTER_BEM_INCLUIR, BemPesquisaPage.ROLE_MANTER_BEM_ALTERAR, BemPesquisaPage.ROLE_MANTER_BEM_EXCLUIR, BemPesquisaPage.ROLE_MANTER_BEM_VISUALIZAR })
public class BemPesquisaPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    private static final String NOME_BEM = "nomeBem";

    public static final String ROLE_MANTER_BEM_INCLUIR = "manter_bem:incluir";
    public static final String ROLE_MANTER_BEM_ALTERAR = "manter_bem:alterar";
    public static final String ROLE_MANTER_BEM_EXCLUIR = "manter_bem:excluir";
    public static final String ROLE_MANTER_BEM_VISUALIZAR = "manter_bem:visualizar";

    private Bem entity; // Model
    private Elemento elemento = new Elemento();
    private Integer itensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;
    private boolean pesquisado = false;

    private Form<Bem> form;
    private InfraDropDownChoice<SubElemento> dropSubElemento;
    private PanelGridResultados panelGridResultados;
    private DataView<Bem> dataViewBem;
    private PanelSubelemento panelSubelemento;
    private SortableBemDataProvider dp;

    private InfraAjaxFallbackLink<Void> btnNovo;
    private AjaxSubmitLink btnPesquisar;

    @Inject
    private ElementoService elementoService;
    @Inject
    private SubElementoService subElementoService;
    @Inject
    private BemService bemService;
    @Inject
    private ComponentFactory componentFactory;

    public BemPesquisaPage(final PageParameters pageParameters) {
        super(pageParameters);
        setTitulo("Pesquisar Bem");
        setEntity(new Bem());
        initComponents();
    }

    protected void initComponents() {

        form = componentFactory.newForm("form", new CompoundPropertyModel<Bem>(entity));

        panelSubelemento = new PanelSubelemento("panelSubelemento");

        btnNovo = getButtonNovo();
        authorize(btnNovo, RENDER, BemPesquisaPage.ROLE_MANTER_BEM_INCLUIR);
        btnPesquisar = getButtonPesquisar();
        authorize(btnPesquisar, RENDER, BemPesquisaPage.ROLE_MANTER_BEM_VISUALIZAR);
        panelGridResultados = new PanelGridResultados("panelGridResultados");
        panelGridResultados.setOutputMarkupId(true);
        panelGridResultados.setVisible(isPesquisado());

        form.add(componentFactory.newLink("lnkDashboard", HomePage.class));
        form.add(componentFactory.newTextField(NOME_BEM, "Nome do Bem", false, null));
        form.add(panelSubelemento);
        form.add(btnNovo);
        form.add(btnPesquisar);
        form.add(panelGridResultados);

        add(form);
    }

    public AjaxSubmitLink getButtonPesquisar() {
        AjaxSubmitLink button = new AjaxSubmitLink("btnPesquisar", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                pesquisar();
                target.add(panelGridResultados);
                target.appendJavaScript("ancorarResultadoPesquisa('#ancoraPesquisa');");
            }
        };
        authorize(button, RENDER, BemPesquisaPage.ROLE_MANTER_BEM_VISUALIZAR);
        return button;
    }

    private void pesquisar() {
        if (getEntity().getSubElemento() == null) {
            getEntity().setSubElemento(new SubElemento());
        }
        getEntity().getSubElemento().setElemento(getElemento());
        setPesquisado(true);
        panelGridResultados.setVisible(isPesquisado());
        if(dp.size()==0){
        	addMsgInfo("Pesquisa não encontrou resultado.");
        }
    }

    private InfraAjaxFallbackLink<Void> getButtonNovo() {
        return componentFactory.newAjaxFallbackLink("btnAdicionarNovo", (target) -> adicionarNovo());
    }

    private void adicionarNovo() {
        setResponsePage(new BemPage(null, new Bem(), this, false,false));
    }

    private DropDownChoice<Elemento> getDropDownChoiceElemento() {
        InfraDropDownChoice<Elemento> dropDownChoice = componentFactory.newDropDownChoice("elemento", "Elemento", false, "id", "nomeECodigo", new LambdaModel<Elemento>(this::getElemento, this::setElemento), listaElementos(), (target) -> atualizarListaSubelementos(target));
        dropDownChoice.setNullValid(true);
        return dropDownChoice;
    }

    private List<Elemento> listaElementos() {
        return elementoService.buscarTodos();
    }

    private List<SubElemento> listaSubelementos() {

        if (elemento != null && elemento.getId() != null) {
            Long id = elemento.getId();
            return subElementoService.buscarPeloElementoId(id);
        } else {
            return subElementoService.buscarPeloElementoId(null);
        }

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

            dp = new SortableBemDataProvider(getEntity(), bemService);
            dataViewBem = getDataViewBem(dp);
            add(getDropItensPorPagina());
            add(dataViewBem);

            add(newOrderByBorder("orderByNomeBem", NOME_BEM, dp));
            add(newOrderByBorder("orderByDescricao", "descricaoBem", dp));
            add(newOrderByBorder("orderByNomeElemento", "subElemento.elemento.nomeElemento", dp));
            add(newOrderByBorder("orderByNomeSubElemento", "subElemento.nomeSubElemento", dp));

            add(getButtonExportarCsv());
            add(new InfraAjaxPagingNavigator("pagination", dataViewBem));
        }
    }

    private OrderByBorder<String> newOrderByBorder(String id, String property, SortableBemDataProvider dp) {
        return new OrderByBorder<String>(id, property, dp);
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

    private Button getButtonExportarCsv() {
        return componentFactory.newButton("btnExportarCsv", () -> exportarCsv());
    }

    private void exportarCsv() {
        List<Bem> bens = bemService.buscar(getEntity());
        if (!bens.isEmpty()) {
            RelatorioBemPesquisaDtoBuilder builder = new RelatorioBemPesquisaDtoBuilder();
            JRConcreteResource<CsvResourceHandler> relatorioResource = builder.exportarCsv(bens);
            ResourceRequestHandler handler = new ResourceRequestHandler(relatorioResource, getPageParameters());
            RequestCycle requestCycle = getRequestCycle();
            requestCycle.scheduleRequestHandlerAfterCurrent(handler);
        } else {
            addMsgError("MSG004");
        }
    }

    private DataView<Bem> getDataViewBem(SortableBemDataProvider dp) {
        DataView<Bem> dataView = new DataView<Bem>("bens", dp) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<Bem> item) {
                item.add(new Label(NOME_BEM));
                item.add(new Label("descricaoBem"));
                item.add(new Label("subElemento.elemento.nomeElemento"));
                item.add(new Label("subElemento.nomeSubElemento"));

                item.add(AttributeModifier.replace("class", new AbstractReadOnlyModel<String>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String getObject() {
                        return (item.getIndex() % 2 == 1) ? "even" : "odd";
                    }
                }));

                Button btnVisualizar = componentFactory.newButton("btnVisualizar", () -> visualizar(item));
                authorize(btnVisualizar, RENDER, BemPesquisaPage.ROLE_MANTER_BEM_VISUALIZAR);
                item.add(btnVisualizar);

                Button btnAlterar = componentFactory.newButton("btnAlterar", () -> alterar(item));
                authorize(btnAlterar, RENDER, BemPesquisaPage.ROLE_MANTER_BEM_ALTERAR);
                item.add(btnAlterar);

                InfraAjaxConfirmButton btnExcluir = componentFactory.newAJaxConfirmButton("btnExcluir", "MSG001", form, (target, formz) -> excluir(target, item));
                authorize(btnExcluir, RENDER, BemPesquisaPage.ROLE_MANTER_BEM_EXCLUIR);
                item.add(btnExcluir);
            }

        };
        dataView.setItemsPerPage(itensPorPagina);
        return dataView;
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

    // AÇÕES
    void atualizarListaSubelementos(AjaxRequestTarget target) {
        dropSubElemento.setChoices(listaSubelementos());
        target.add(panelSubelemento);
    }

    protected void visualizar(Item<Bem> item) {
        Bem b = item.getModelObject();
        setResponsePage(new BemPage(null, b, this, true,false));
    }

    private void alterar(Item<Bem> item) {
        Bem b = item.getModelObject();
        setResponsePage(new BemPage(null, b, this, false,false));
    }

    private void excluir(AjaxRequestTarget target, Item<Bem> item) {
        if (!getSideSession().hasRole(ROLE_MANTER_BEM_EXCLUIR)) {
            throw new SecurityException();
        }

        Bem b = item.getModelObject();
        bemService.excluir(b.getId());
        getSession().info("Excluído com sucesso");
        target.add(panelGridResultados);
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

}