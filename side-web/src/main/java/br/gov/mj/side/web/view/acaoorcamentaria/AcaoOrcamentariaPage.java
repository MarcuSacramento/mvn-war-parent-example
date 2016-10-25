package br.gov.mj.side.web.view.acaoorcamentaria;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.converter.MoneyBigDecimalConverter;
import br.gov.mj.infra.wicket.listener.FeedbackPanelListener;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.AcaoOrcamentaria;
import br.gov.mj.side.entidades.BeneficiarioEmendaParlamentar;
import br.gov.mj.side.entidades.EmendaParlamentar;
import br.gov.mj.side.web.service.AcaoOrcamentariaService;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.components.validators.AlphaNumericValidator;
import br.gov.mj.side.web.view.components.validators.CustomRangeValidator;
import br.gov.mj.side.web.view.components.validators.NumericValidator;
import br.gov.mj.side.web.view.template.TemplatePage;

@AuthorizeInstantiation({ AcaoOrcamentariaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_INCLUIR, AcaoOrcamentariaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_ALTERAR, AcaoOrcamentariaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_EXCLUIR, AcaoOrcamentariaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_VISUALIZAR })
public class AcaoOrcamentariaPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    public static final String ROLE_MANTER_ACAO_ORCAMENTARIA_INCLUIR = "manter_acao_orcamentaria:incluir";
    public static final String ROLE_MANTER_ACAO_ORCAMENTARIA_ALTERAR = "manter_acao_orcamentaria:alterar";
    public static final String ROLE_MANTER_ACAO_ORCAMENTARIA_EXCLUIR = "manter_acao_orcamentaria:excluir";
    public static final String ROLE_MANTER_ACAO_ORCAMENTARIA_VISUALIZAR = "manter_acao_orcamentaria:visualizar";

    private Form<AcaoOrcamentaria> form;
    private AcaoOrcamentaria entity;
    private DataView<EmendaParlamentar> dataView;
    private PanelGridEmendas panelGridEmendas;
    private Label labelTotal;
    private Page backPage;

    private boolean readOnly = false;
    private String ano = "";

    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private AcaoOrcamentariaService acaoOrcamentariaService;

    public AcaoOrcamentariaPage(PageParameters pageParameters, Page backPage, AcaoOrcamentaria entity, boolean readOnly) {
        super(pageParameters);
        this.backPage = backPage;
        this.readOnly = readOnly;

        if (entity != null && entity.getId() != null) {
            setTitulo("Alterar Ação Orçamentária");
            AcaoOrcamentaria a = acaoOrcamentariaService.buscarPeloId(entity.getId());

            List<EmendaParlamentar> listaEmendaParlamentar = new ArrayList<EmendaParlamentar>();
            for (EmendaParlamentar emendaParlamentar : acaoOrcamentariaService.buscarEmendaParlamentar(a)) {
                List<BeneficiarioEmendaParlamentar> listaBeneficiarios = acaoOrcamentariaService.buscarBeneficiarioEmendaParlamentar(emendaParlamentar);
                emendaParlamentar.setBeneficiariosEmendaParlamentar(listaBeneficiarios);
                listaEmendaParlamentar.add(emendaParlamentar);
            }
            a.setEmendasParlamentares(listaEmendaParlamentar);
            this.entity = a;
            setAno(entity.getAnoAcaoOrcamentaria().toString());
        } else {
            setTitulo("Cadastrar Ação Orçamentária");
            this.entity = new AcaoOrcamentaria();
        }
        this.readOnly = readOnly;
        if (readOnly) {
            setTitulo("Visualizar Ação Orçamentária");
        }
        initComponents();
    }

    private void initComponents() {
        form = componentFactory.newForm("form", entity);

        labelTotal = newLabelTotal();
        panelGridEmendas = new PanelGridEmendas("panelGridEmendas");

        // Breadcrump itens
        form.add(componentFactory.newLink("lnkDashboard", HomePage.class));
        form.add(componentFactory.newLink("lnkFonteRecursoPesquisaPage", AcaoOrcamentariaPesquisaPage.class));
        form.add(new Label("lblNomePage", getTitulo()));

        form.add(newTextFieldNumeroProgramaPPA());
        form.add(newTextFieldNomeProgramaPPA());
        form.add(newTextFieldNumeroAcaoOrcamentaria());
        form.add(newTextFieldNomeAcaoOrcamentaria());
        form.add(newTextFieldAno());
        form.add(newTextFieldValorPrevisto());
        form.add(labelTotal);
        form.add(newButtonAdicionarEmenda());
        form.add(panelGridEmendas);
        form.add(newButtonConfirmar());
        form.add(newButtonVoltar());

        add(form);
    }

    private TextField<String> newTextFieldAno() {
        TextField<String> field = componentFactory.newTextField("anoAcaoOrcamentaria", "Ano", true, new LambdaModel<String>(this::getAno, this::setAno), StringValidator.exactLength(4));
        field.add(new NumericValidator());
        field.setEnabled(!isReadOnly());
        return field;
    }

    private Label newLabelTotal() {
        BigDecimal total = BigDecimal.ZERO;
        if (entity != null && entity.getValorPrevisto() != null) {
            total = entity.getTotalValor();
        }
        Label label = new Label("totalValor", total) {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
        label.setEnabled(false);
        return label;
    }

    private class PanelGridEmendas extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelGridEmendas(String id) {
            super(id);
            dataView = newDataViewEmendaParlamentar();
            add(dataView);
        }
    }

    private TextField<String> newTextFieldNumeroProgramaPPA() {
        TextField<String> field = componentFactory.newTextField("numeroProgramaPPA", "Programa PPA Nº", true, null, StringValidator.exactLength(4));
        field.add(new AlphaNumericValidator());
        field.setEnabled(!isReadOnly());
        return field;
    }

    private TextField<String> newTextFieldNomeProgramaPPA() {
        TextField<String> field = componentFactory.newTextField("nomeProgramaPPA", "Programa PPA Nome", true, null, StringValidator.maximumLength(200));
        field.setEnabled(!isReadOnly());
        return field;
    }

    private TextField<String> newTextFieldNumeroAcaoOrcamentaria() {
        TextField<String> field = componentFactory.newTextField("numeroAcaoOrcamentaria", "Ação Orçamentária Nº", true, null, StringValidator.exactLength(4));
        field.add(new AlphaNumericValidator());
        field.setEnabled(!isReadOnly());
        return field;
    }

    private TextField<String> newTextFieldNomeAcaoOrcamentaria() {
        TextField<String> field = componentFactory.newTextField("nomeAcaoOrcamentaria", "Ação Orçamentária Nome", true, null, StringValidator.maximumLength(200));
        field.setEnabled(!isReadOnly());
        return field;
    }

    private TextField<BigDecimal> newTextFieldValorPrevisto() {
        TextField<BigDecimal> field = new TextField<BigDecimal>("valorPrevisto") {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
        field.setLabel(Model.of("Valor LOA"));
        field.setRequired(true);
        field.add(new CustomRangeValidator(new BigDecimal("0.00"), new BigDecimal("999999999999.99")));

        field.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                RequestCycle.get().setMetaData(FeedbackPanelListener.SKIP_FEEDBACK_LISTENER, Boolean.TRUE);
                atualizarLabelTotal();
                target.add(labelTotal);
            }
        });
        field.setEnabled(!isReadOnly());
        return field;
    }

    public void atualizarLabelTotal() {
        labelTotal.setDefaultModelObject(entity.getTotalValor());
    }

    private Button newButtonVoltar() {
        Button button = componentFactory.newButton("btnVoltar", () -> voltar());
        button.setDefaultFormProcessing(false);
        return button;
    }

    private void voltar() {
        setResponsePage(backPage);
    }

    private Button newButtonConfirmar() {
        Button button = componentFactory.newButton("btnConfirmar", () -> confirmar());
        button.setVisible(!isReadOnly());
        return button;

    }

    private Button newButtonAdicionarEmenda() {
        Button button = componentFactory.newButton("btnAdicionarEmenda", () -> adicionarEmenda());
        button.setDefaultFormProcessing(false);
        button.setVisible(!isReadOnly());
        return button;
    }

    private void confirmar() {

        if (!getSideSession().hasAnyRole(new String[] { AcaoOrcamentariaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_INCLUIR, AcaoOrcamentariaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_ALTERAR })) {
            throw new SecurityException();
        }

        AcaoOrcamentaria a = entity;
        boolean isInsert = a.getId() == null ? true : false;

        try {
            if (StringUtils.isNotEmpty(ano)) {
                a.setAnoAcaoOrcamentaria(Integer.parseInt(ano));
                if (validarAno(a.getAnoAcaoOrcamentaria())) {
                    AcaoOrcamentaria acaoSalva = acaoOrcamentariaService.incluirAlterar(a, getIdentificador());
                    if (isInsert) {
                        getSession().info("Cadastrado com sucesso");
                    } else {
                        getSession().info("Alterado com sucesso.");
                    }
                    setResponsePage(new AcaoOrcamentariaPage(new PageParameters(), backPage, acaoSalva, false));
                } else {
                    addMsgError("'Ano' não é um valor válido");
                }
            }
        } catch (NumberFormatException e) {
            addMsgError("'Ano' não é um número válido");
        }

    }

    private void adicionarEmenda() {
        setResponsePage(new EmendaParlamentarPage(null, this, new EmendaParlamentar(), false));
    }

    private DataView<EmendaParlamentar> newDataViewEmendaParlamentar() {

        return new DataView<EmendaParlamentar>("emendas", new ListDataProvider<EmendaParlamentar>(entity.getEmendasParlamentares())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<EmendaParlamentar> item) {
                item.add(new Label("numeroEmendaParlamantar", item.getModelObject().getNumeroEmendaParlamantar()));
                item.add(new Label("nomeParlamentar", item.getModelObject().getNomeParlamentar()));
                item.add(new Label("partidoPolitico.nomePartido", item.getModelObject().getPartidoPolitico().getSiglaNome()));
                item.add(new Label("valorPrevisto", item.getModelObject().getValorPrevisto()) {
                    private static final long serialVersionUID = 1L;

                    @SuppressWarnings("unchecked")
                    @Override
                    public <C> IConverter<C> getConverter(Class<C> type) {
                        return (IConverter<C>) new MoneyBigDecimalConverter();
                    }
                });

                Button btnVisualizar = componentFactory.newButton("btnVisualizar", () -> visualizarEmenda(item));
                btnVisualizar.setDefaultFormProcessing(false);
                item.add(btnVisualizar);

                Button btnAlterar = componentFactory.newButton("btnAlterarEmenda", () -> alterarEmenda(item));
                btnAlterar.setDefaultFormProcessing(false);
                btnAlterar.setVisible(!isReadOnly());
                item.add(btnAlterar);

                InfraAjaxConfirmButton btnExcluir = componentFactory.newAJaxConfirmButton("btnExcluirEmenda", "MSG001", form, (target, formz) -> excluirEmenda(target, item));
                btnExcluir.setVisible(!isReadOnly());
                item.add(btnExcluir);
            }
        };
    }

    private boolean validarAno(Integer ano) {
        return ano.toString().length() == 4;
    }

    private void visualizarEmenda(Item<EmendaParlamentar> item) {
        setResponsePage(new EmendaParlamentarPage(null, this, item.getModelObject(), true));
    }

    private void alterarEmenda(Item<EmendaParlamentar> item) {
        setResponsePage(new EmendaParlamentarPage(null, this, item.getModelObject(), false));
    }

    private void excluirEmenda(AjaxRequestTarget target, Item<EmendaParlamentar> item) {
        EmendaParlamentar e = item.getModelObject();
        entity.getEmendasParlamentares().remove(e);
        addMsgInfo("Removido com sucesso.");
        atualizarLabelTotal();
        target.add(labelTotal);
        target.add(panelGridEmendas);
    }

    public AcaoOrcamentaria getEntity() {
        return entity;
    }

    public void setEntity(AcaoOrcamentaria entity) {
        this.entity = entity;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }
}
