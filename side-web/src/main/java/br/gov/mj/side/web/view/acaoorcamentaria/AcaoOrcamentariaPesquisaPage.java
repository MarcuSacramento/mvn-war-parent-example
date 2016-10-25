package br.gov.mj.side.web.view.acaoorcamentaria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
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
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.StringValidator;
import org.wicketstuff.jasperreports.JRConcreteResource;
import org.wicketstuff.jasperreports.handlers.CsvResourceHandler;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.converter.MoneyBigDecimalConverter;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.AcaoOrcamentaria;
import br.gov.mj.side.entidades.EmendaParlamentar;
import br.gov.mj.side.web.service.AcaoOrcamentariaService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.RelatorioAcaoOrcamentariaBuilder;
import br.gov.mj.side.web.util.SortableAcaoOrcamentariaDataProvider;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.components.validators.NumericValidator;
import br.gov.mj.side.web.view.template.TemplatePage;

@AuthorizeInstantiation({ AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_INCLUIR, AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_ALTERAR, AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_EXCLUIR, AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_VISUALIZAR })
public class AcaoOrcamentariaPesquisaPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    public static final String ROLE_MANTER_ACAO_ORCAMENTARIA_INCLUIR = "manter_acao_orcamentaria:incluir";
    public static final String ROLE_MANTER_ACAO_ORCAMENTARIA_ALTERAR = "manter_acao_orcamentaria:alterar";
    public static final String ROLE_MANTER_ACAO_ORCAMENTARIA_EXCLUIR = "manter_acao_orcamentaria:excluir";
    public static final String ROLE_MANTER_ACAO_ORCAMENTARIA_VISUALIZAR = "manter_acao_orcamentaria:visualizar";

    private static final String ONCHANGE = "onchange";
    private static final String NOME_ACAO_ORCAMENTARIA = "nomeAcaoOrcamentaria";

    private Form<Void> form;

    // PAINEIS
    private PanelPesquisa panelPesquisa;
    private PanelDropEmendasVinculadas panelDropEmendasVinculadas;
    private PanelDropEmendaParlamentar panelDropEmendaParlamentar;
    private PanelGridResultados panelGridResultados;

    private SortableAcaoOrcamentariaDataProvider dp;
    private DataView<AcaoOrcamentaria> dataView;
    private DropDownChoice<AcaoOrcamentaria> dropDownAcaoOrcamentaria;
    private DropDownChoice<EmendaParlamentar> dropDownEmendaParlamentar;
    private InfraDropDownChoice<AcaoOrcamentaria> dropDownAcao;

    private Integer itensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;
    private AcaoOrcamentaria entity = new AcaoOrcamentaria();
    private EmendaParlamentar emendaParlamentar = new EmendaParlamentar();
    private List<EmendaParlamentar> listaEmendas = new ArrayList<EmendaParlamentar>();
    private String ano = "";
    private String emendasVinculadas = "";
    private boolean pesquisado = false;

    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private AcaoOrcamentariaService acaoOrcamentariaService;

    public AcaoOrcamentariaPesquisaPage(PageParameters pageParameters) {
        super(pageParameters);
        setTitulo("Pesquisar Ação Orçamentária");

        todasEmendas();
        initComponents();
    }

    private void initComponents() {
        form = new Form<Void>("form");

        AjaxSubmitLink btnPesquisar = newButtonPesquisar();
        authorize(btnPesquisar, RENDER, AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_VISUALIZAR);
        InfraAjaxFallbackLink<Void> btnNovo = newLinkNovo();
        authorize(btnNovo, RENDER, AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_INCLUIR);

        panelPesquisa = new PanelPesquisa("panelPesquisa");
        panelGridResultados = new PanelGridResultados("panelGridResultados");
        panelGridResultados.setVisible(isPesquisado());

        form.add(componentFactory.newLink("lnkDashboard", HomePage.class));
        form.add(componentFactory.newLabel("lblNomePage", getTitulo()));
        form.add(panelPesquisa);
        form.add(panelGridResultados);
        form.add(btnNovo);
        form.add(btnPesquisar);

        add(form);
    }

    private class PanelDropEmendaParlamentar extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDropEmendaParlamentar(String id) {
            super(id);
            setOutputMarkupId(true);
            dropDownEmendaParlamentar = newDropDownChoiceEmenda();
            add(dropDownEmendaParlamentar);
        }
    }

    private class PanelDropEmendasVinculadas extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDropEmendasVinculadas(String id) {
            super(id);
            setOutputMarkupId(true);
            add(newDropDownChoiceEmendasVinculadas()); // emendasVinculadas
        }
    }

    private boolean validarAno(Integer ano) {
        return ano.toString().length() == 4;
    }

    private Button newButtonExportarCsv() {
        return componentFactory.newButton("btnExportarCsv", () -> exportarCsv());
    }

    private TextField<String> newTextFieldAno() {
        TextField<String> field = componentFactory.newTextField("anoAcaoOrcamentaria", "Ano", false, new LambdaModel<String>(this::getAno, this::setAno), StringValidator.exactLength(4));
        field.add(new NumericValidator());
        return field;
    }

    private InfraDropDownChoice<AcaoOrcamentaria> newDropDownChoiceAcaoOrcamentaria() {
        dropDownAcao = componentFactory.newDropDownChoice("acaoOrcamentaria", "Ação Orçamentária", false, "nomeAcaoOrcamentaria", "numeroNomeAcaoOrcamentaria", new LambdaModel<AcaoOrcamentaria>(this::getEntity, this::setEntity), getAcoesOrcamentarias(), null);
        dropDownAcao.setNullValid(true);
        return dropDownAcao;
    }

    private InfraDropDownChoice<EmendaParlamentar> newDropDownChoiceEmenda() {
        InfraDropDownChoice<EmendaParlamentar> dropDown = componentFactory.newDropDownChoice("emendaParlamentar", "Emenda Parlamentar", false, "id", "numeroNome", new LambdaModel<EmendaParlamentar>(this::getEmendaParlamentar, this::setEmendaParlamentar), listaEmendas, null);
        actionDropDownEmendaParlamentar(dropDown);
        dropDown.setNullValid(true);
        return dropDown;
    }

    private List<AcaoOrcamentaria> getAcoesOrcamentarias() {
        return acaoOrcamentariaService.buscar(entity);
    }

    private DataView<AcaoOrcamentaria> newDataViewAcaoOrcamentaria(SortableAcaoOrcamentariaDataProvider dp) {
        DataView<AcaoOrcamentaria> newDataView = new DataView<AcaoOrcamentaria>("recursos", dp) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<AcaoOrcamentaria> item) {
                item.add(new Label(NOME_ACAO_ORCAMENTARIA));
                item.add(new Label("totalValor") {
                    private static final long serialVersionUID = 1L;

                    @SuppressWarnings("unchecked")
                    @Override
                    public <C> IConverter<C> getConverter(Class<C> type) {
                        return (IConverter<C>) new MoneyBigDecimalConverter();
                    }
                });

                Button btnVisualizar = componentFactory.newButton("btnVisualizar", () -> visualizar(item));
                authorize(btnVisualizar, RENDER, AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_VISUALIZAR);
                item.add(btnVisualizar);

                Button btnAlterar = componentFactory.newButton("btnAlterar", () -> alterar(item));
                authorize(btnAlterar, RENDER, AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_ALTERAR);
                item.add(btnAlterar);

                InfraAjaxConfirmButton btnExcluir = componentFactory.newAJaxConfirmButton("btnExcluir", "MSG001", form, (target, formz) -> excluir(target, item));
                authorize(btnExcluir, RENDER, AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_EXCLUIR);
                item.add(btnExcluir);
            }

        };
        newDataView.setItemsPerPage(itensPorPagina);
        return newDataView;
    }

    private void visualizar(Item<AcaoOrcamentaria> item) {

        if (!getSideSession().hasRole(ROLE_MANTER_ACAO_ORCAMENTARIA_VISUALIZAR)) {
            throw new SecurityException();
        }

        AcaoOrcamentaria a = item.getModelObject();
        setResponsePage(new AcaoOrcamentariaPage(new PageParameters(), this, a, true));
    }

    private void alterar(Item<AcaoOrcamentaria> item) {

        if (!getSideSession().hasRole(ROLE_MANTER_ACAO_ORCAMENTARIA_ALTERAR)) {
            throw new SecurityException();
        }

        AcaoOrcamentaria a = item.getModelObject();
        setResponsePage(new AcaoOrcamentariaPage(new PageParameters(), this, a, false));
    }

    private void excluir(AjaxRequestTarget target, Item<AcaoOrcamentaria> item) {
        if (!getSideSession().hasRole(ROLE_MANTER_ACAO_ORCAMENTARIA_EXCLUIR)) {
            throw new SecurityException();
        }
        entity = new AcaoOrcamentaria();
        AcaoOrcamentaria a = item.getModelObject();
        acaoOrcamentariaService.excluir(a.getId());
        addMsgInfo("Removido com sucesso.");
        dropDownAcaoOrcamentaria.setChoices(getAcoesOrcamentarias());
        target.add(panelGridResultados, panelPesquisa);
    }

    private class PanelGridResultados extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelGridResultados(String id) {
            super(id);
            dp = new SortableAcaoOrcamentariaDataProvider(acaoOrcamentariaService, entity, null, emendaParlamentar);
            dataView = newDataViewAcaoOrcamentaria(dp);

            add(newOrderByBorder("orderByNomeAcaoOrcamentaria", NOME_ACAO_ORCAMENTARIA, dataView, dp));
            add(newDropItensPorPagina());
            add(new InfraAjaxPagingNavigator("pagination", dataView));
            add(dataView);
            add(newButtonExportarCsv());
        }

        private OrderByBorder<String> newOrderByBorder(String id, String property, DataView<AcaoOrcamentaria> dataView, SortableAcaoOrcamentariaDataProvider dp) {
            return new OrderByBorder<String>(id, property, dp) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSortChanged() {
                    dataView.setCurrentPage(0);
                }
            };
        }

    }

    private class PanelPesquisa extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelPesquisa(String id) {
            super(id);
            dropDownAcaoOrcamentaria = newDropDownChoiceAcaoOrcamentaria();
            panelDropEmendasVinculadas = new PanelDropEmendasVinculadas("panelDropEmendasVinculadas");
            panelDropEmendaParlamentar = new PanelDropEmendaParlamentar("panelDropEmendaParlamentar");

            add(dropDownAcaoOrcamentaria);
            add(panelDropEmendasVinculadas);
            add(panelDropEmendaParlamentar);
            add(newTextFieldAno());
        }
    }

    private InfraDropDownChoice<String> newDropDownChoiceEmendasVinculadas() {
        InfraDropDownChoice<String> dropDownChoice = new InfraDropDownChoice<String>("emendasVinculadas", "", "", new LambdaModel<String>(this::getEmendasVinculadas, this::setEmendasVinculadas), Arrays.asList("Sim", "Não"));
        actionDropDownEmendasVinculadas(dropDownChoice);
        dropDownChoice.setNullValid(true);
        return dropDownChoice;
    }

    public AjaxSubmitLink newButtonPesquisar() {
        AjaxSubmitLink button = new AjaxSubmitLink("btnPesquisar", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                pesquisar();
                target.add(panelGridResultados);
                target.appendJavaScript("ancorarResultadoPesquisa('#ancoraPesquisa');");
            }
        };
        authorize(button, RENDER, AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_VISUALIZAR);
        return button;
    }

    private void pesquisar() {

        String msg = "'Ano' não é um valor válido";

        AcaoOrcamentaria acao = new AcaoOrcamentaria();
        acao.setId(entity == null ? null : entity.getId());

        boolean isValid = true;
        if (StringUtils.isNotEmpty(ano)) {
            try {
                acao.setAnoAcaoOrcamentaria(Integer.parseInt(ano));
                if (!validarAno(acao.getAnoAcaoOrcamentaria())) {
                    addMsgError(msg);
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                addMsgError(msg);
                isValid = false;
            }
        }
        if (isValid) {
            panelGridResultados.setVisible(true);

            Boolean b = false;
            if (emendaParlamentar != null && emendaParlamentar.getId() != null) {
                b = true;
            } else {
                b = StringUtils.isBlank(emendasVinculadas) ? null : ("Sim".equals(emendasVinculadas) ? new Boolean(true) : new Boolean(false));
            }

            dp.setEmendasVinculadas(b);
            dp.setEntity(acao);
            dp.setEmenda(emendaParlamentar);
            if( dp.size()==0){
            	addMsgInfo("Pesquisa não encontrou resultado.");
            }
        }
    }

    private InfraAjaxFallbackLink<Void> newLinkNovo() {
        return componentFactory.newAjaxFallbackLink("btnAdicionarNovo", target -> adicionarNovo());
    }

    private void adicionarNovo() {
        if (!getSideSession().hasRole(ROLE_MANTER_ACAO_ORCAMENTARIA_INCLUIR)) {
            throw new SecurityException();
        }
        setResponsePage(new AcaoOrcamentariaPage(new PageParameters(), this, null, false));
    }

    private DropDownChoice<Integer> newDropItensPorPagina() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPagina", new LambdaModel<Integer>(this::getItensPorPagina, this::setItensPorPagina), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataView.setItemsPerPage(getItensPorPagina());
                target.add(panelGridResultados);
            }
        });
        return dropDownChoice;
    }

    private void exportarCsv() {
        Boolean b = StringUtils.isBlank(emendasVinculadas) ? null : ("Sim".equals(emendasVinculadas) ? new Boolean(true) : new Boolean(false));
        List<AcaoOrcamentaria> acoes = acaoOrcamentariaService.buscar(getEntity(), b, emendaParlamentar);

        List<AcaoOrcamentaria> listaAcoes = new ArrayList<AcaoOrcamentaria>();
        for (AcaoOrcamentaria acaoOrcamentaria : acoes) {

            List<EmendaParlamentar> listaEmendasParlamentares = new ArrayList<EmendaParlamentar>();
            for (EmendaParlamentar emendaParlamentar : acaoOrcamentariaService.buscarEmendaParlamentar(acaoOrcamentaria)) {
                emendaParlamentar.setBeneficiariosEmendaParlamentar(acaoOrcamentariaService.buscarBeneficiarioEmendaParlamentar(emendaParlamentar));
                listaEmendasParlamentares.add(emendaParlamentar);
            }
            acaoOrcamentaria.setEmendasParlamentares(listaEmendasParlamentares);
            listaAcoes.add(acaoOrcamentaria);
        }

        if (!acoes.isEmpty()) {
            RelatorioAcaoOrcamentariaBuilder builder = new RelatorioAcaoOrcamentariaBuilder();
            JRConcreteResource<CsvResourceHandler> relatorioResource = builder.buildJrConcreteResourceCsv(listaAcoes);
            ResourceRequestHandler handler = new ResourceRequestHandler(relatorioResource, getPageParameters());
            RequestCycle requestCycle = getRequestCycle();
            requestCycle.scheduleRequestHandlerAfterCurrent(handler);
        } else {
            addMsgError("MSG004");
        }
    }

    @SuppressWarnings("rawtypes")
    public void actionDropDownEmendaParlamentar(DropDownChoice drop) {
        drop.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                if (emendaParlamentar != null) {
                    emendasVinculadas = "Sim";
                    panelDropEmendasVinculadas.addOrReplace(newDropDownChoiceEmendasVinculadas());
                    dropDownAcao.setModelObject(entity);
                    target.appendJavaScript("atualizaCssDropDown();");
                    target.add(panelDropEmendasVinculadas);
                }
            }
        });
    }

    @SuppressWarnings("rawtypes")
    public void actionDropDownEmendasVinculadas(DropDownChoice drop) {
        drop.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                if ("Não".equalsIgnoreCase(emendasVinculadas)) {
                    emendaParlamentar = null;
                    panelDropEmendaParlamentar.addOrReplace(newDropDownChoiceEmenda());
                    target.appendJavaScript("atualizaCssDropDown();");
                    target.add(panelDropEmendaParlamentar);
                }
            }
        });
    }

    public void todasEmendas() {
        List<EmendaParlamentar> todasEmendas = acaoOrcamentariaService.buscarEmendas();
        for (EmendaParlamentar emenda : todasEmendas) {
            if (listaEmendas.isEmpty()) {
                listaEmendas.add(emenda);
            } else {
                int flag = 0;
                for (EmendaParlamentar emenda2 : listaEmendas) {
                    if (emenda2.getNumeroEmendaParlamantar().equalsIgnoreCase(emenda.getNumeroEmendaParlamantar())) {
                        flag++;
                        break;
                    }
                }

                if (flag == 0) {
                    listaEmendas.add(emenda);
                }
                flag = 0;
            }
        }
    }

    public Integer getItensPorPagina() {
        return itensPorPagina;
    }

    public void setItensPorPagina(Integer itensPorPagina) {
        this.itensPorPagina = itensPorPagina;
    }

    public AcaoOrcamentaria getEntity() {
        return entity;
    }

    public void setEntity(AcaoOrcamentaria entity) {
        this.entity = entity;
    }

    public String getEmendasVinculadas() {
        return emendasVinculadas;
    }

    public void setEmendasVinculadas(String emendasVinculadas) {
        this.emendasVinculadas = emendasVinculadas;
    }

    public boolean isPesquisado() {
        return pesquisado;
    }

    public void setPesquisado(boolean pesquisado) {
        this.pesquisado = pesquisado;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public EmendaParlamentar getEmendaParlamentar() {
        return emendaParlamentar;
    }

    public void setEmendaParlamentar(EmendaParlamentar emendaParlamentar) {
        this.emendaParlamentar = emendaParlamentar;
    }
}
