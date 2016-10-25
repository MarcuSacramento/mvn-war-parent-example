package br.gov.mj.side.web.view.programa.recursofinanceiro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.IConverter;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.converter.MoneyBigDecimalConverter;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.AcaoOrcamentaria;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaRecursoFinanceiro;
import br.gov.mj.side.web.service.AcaoOrcamentariaService;
import br.gov.mj.side.web.view.programa.ProgramaPage;

public class RecursoFinanceiroPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private AcaoOrcamentaria acaoOrcamentaria;

    private DataView<ProgramaRecursoFinanceiro> dataView;
    private ProgramaPage programaPage;
    private Programa programa;
    private Label lblValorTotal;
    private PanelGridRecursoFinanceiro panelGridRecursoFinanceiro;
    private boolean readOnly;

    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private AcaoOrcamentariaService acaoOrcamentariaService;

    public RecursoFinanceiroPanel(String id, ProgramaPage programaPage, boolean readOnly) {
        super(id);
        this.readOnly = readOnly;
        this.programa = programaPage.getForm().getModelObject();
        this.programaPage = programaPage;

        initComponents();
    }

    private void initComponents() {

        add(newDropDownAcaoOrcamentaria());
        add(newButtonVicularRecurso());
        panelGridRecursoFinanceiro = new PanelGridRecursoFinanceiro("panelGridRecursoFinanceiro");
        add(panelGridRecursoFinanceiro);
        lblValorTotal = newLabelValorTotal();
        add(lblValorTotal);
        add(newLabelValorMaximoProposta()); //valorMaximoProposta

    }

    public Label newLabelValorMaximoProposta() {
        
        BigDecimal valor = programaPage.getEntity().getValorMaximoProposta(); 
        return new Label("valorMaximoProposta", valor) {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
    }
    
    private Label newLabelValorTotal() {
        return new Label("valorTotal", getValorTotal()) {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
    }

    public BigDecimal getValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ProgramaRecursoFinanceiro recursoFinanceiro : programaPage.getForm().getModelObject().getRecursosFinanceiros()) {
            total = total.add(recursoFinanceiro.getTotal());
        }
        return total;
    }

    public void atualizarLabelTotal() {
        lblValorTotal.setDefaultModelObject(getValorTotal());
    }

    private DataView<ProgramaRecursoFinanceiro> newDataViewFonteRecurso() {
        return new DataView<ProgramaRecursoFinanceiro>("recursosFinanceiros", new ListDataProvider<ProgramaRecursoFinanceiro>(programa.getRecursosFinanceiros())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ProgramaRecursoFinanceiro> item) {

                item.add(new Label("acaoOrcamentaria.numeroNomeAcaoOrcamentaria", item.getModelObject().getAcaoOrcamentaria().getNumeroNomeAcaoOrcamentaria()));
                item.add(new Label("total", item.getModelObject().getTotal()) {
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

                Button btnAlterar = componentFactory.newButton("btnAlterar", () -> alterarEmenda(item));
                btnAlterar.setDefaultFormProcessing(false);
                btnAlterar.setVisible(!readOnly);
                item.add(btnAlterar);

                InfraAjaxConfirmButton btnExcluir = componentFactory.newAJaxConfirmButton("btnExcluir", "MN022", programaPage.getForm(), (target, formz) -> excluirEmenda(target, item));
                btnExcluir.setVisible(!readOnly);
                item.add(btnExcluir);

            }
        };
    }

    private void excluirEmenda(AjaxRequestTarget target, Item<ProgramaRecursoFinanceiro> item) {
        programaPage.getForm().getModelObject().getRecursosFinanceiros().remove(item.getModelObject());
        atualizarLabelTotal();
        info(getString("MN023"));
        target.add(panelGridRecursoFinanceiro, lblValorTotal);
    }

    private void alterarEmenda(Item<ProgramaRecursoFinanceiro> item) {
        setResponsePage(new ProgramaRecursoFinanceiroPage(new PageParameters(), item.getModelObject(), programaPage, false));
    }

    private void visualizarEmenda(Item<ProgramaRecursoFinanceiro> item) {
        setResponsePage(new ProgramaRecursoFinanceiroPage(new PageParameters(), item.getModelObject(), programaPage, true));
    }

    private Button newButtonVicularRecurso() {
        
        AjaxButton btnVicularRecurso = new AjaxButton("btnVincularRecurso") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                vincularRecurso(target);
            }
        };
        btnVicularRecurso.setVisible(!readOnly);
        return btnVicularRecurso;
    }

    private void vincularRecurso(AjaxRequestTarget target) {
        if (isValido()) {
            setResponsePage(new ProgramaRecursoFinanceiroPage(new PageParameters(), acaoOrcamentaria, programaPage));
            acaoOrcamentaria = new AcaoOrcamentaria();
        }else{
            programaPage.actionAba(target, "recursoFinanceiro");
        }
        
    }

    private boolean isValido() {
        if (acaoOrcamentaria == null) {
            error(getString("MN024"));
            return false;
        } else {
            if (isCadastrado()) {
                error(getString("MN025"));
                return false;
            } else {
                return true;
            }
        }
    }

    private boolean isCadastrado() {
        for (ProgramaRecursoFinanceiro recursoFinanceiro : programaPage.getForm().getModelObject().getRecursosFinanceiros()) {
            if (recursoFinanceiro.getAcaoOrcamentaria().getId() == acaoOrcamentaria.getId()) {
                return true;
            }
        }
        return false;
    }

    public DropDownChoice<AcaoOrcamentaria> newDropDownAcaoOrcamentaria() {

        List<AcaoOrcamentaria> listAcao = new ArrayList<AcaoOrcamentaria>();

        Integer ano = programaPage.getForm().getModelObject().getAnoPrograma() == null ? 0 : programaPage.getForm().getModelObject().getAnoPrograma();
        AcaoOrcamentaria acao = new AcaoOrcamentaria();
        acao.setAnoAcaoOrcamentaria(ano);
        listAcao = acaoOrcamentariaService.buscar(acao);

        InfraDropDownChoice<AcaoOrcamentaria> dropDown = componentFactory.newDropDownChoice("acaoOrcamentaria", "Ação Orçamentária", false, "id", "numeroNomeAcaoOrcamentaria", new LambdaModel<AcaoOrcamentaria>(this::getAcaoOrcamentaria, this::setAcaoOrcamentaria), listAcao, null);
        dropDown.setOutputMarkupId(true);
        dropDown.setNullValid(true);
        dropDown.setVisible(!readOnly);
        return dropDown;
    }

    public AcaoOrcamentaria getAcaoOrcamentaria() {
        return acaoOrcamentaria;
    }

    public void setAcaoOrcamentaria(AcaoOrcamentaria acaoOrcamentaria) {
        this.acaoOrcamentaria = acaoOrcamentaria;
    }

    private class PanelGridRecursoFinanceiro extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelGridRecursoFinanceiro(String id) {
            super(id);
            dataView = newDataViewFonteRecurso();
            this.setOutputMarkupId(true);
            add(dataView);
        }
    }

    //Ações
    private void actionTextFieldNomeFantasia(TextField field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                //programa.setNomeFantasiaPrograma(nomeFantasiaPrograma);
            }
        };
        field.add(onChangeAjaxBehavior);
    }
    
    public void atualizarDropDownAcaoOrcamentaria(AjaxRequestTarget target) {
        panelGridRecursoFinanceiro.addOrReplace(newDropDownAcaoOrcamentaria());
        target.add(panelGridRecursoFinanceiro);
    }

    public PanelGridRecursoFinanceiro getPanelGridRecursoFinanceiro() {
        return panelGridRecursoFinanceiro;
    }

    public void setPanelGridRecursoFinanceiro(PanelGridRecursoFinanceiro panelGridRecursoFinanceiro) {
        this.panelGridRecursoFinanceiro = panelGridRecursoFinanceiro;
    }
}
