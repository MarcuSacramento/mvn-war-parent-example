package br.gov.mj.side.web.view.programa.recursofinanceiro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.IConverter;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.converter.MoneyBigDecimalConverter;
import br.gov.mj.infra.wicket.listener.FeedbackPanelListener;
import br.gov.mj.side.entidades.AcaoOrcamentaria;
import br.gov.mj.side.entidades.EmendaParlamentar;
import br.gov.mj.side.entidades.programa.ProgramaRecursoFinanceiro;
import br.gov.mj.side.entidades.programa.RecursoFinanceiroEmenda;
import br.gov.mj.side.web.dto.AcaoEmendaComSaldoDto;
import br.gov.mj.side.web.dto.EmendaComSaldoDto;
import br.gov.mj.side.web.service.RecursoFinanceiroService;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.components.SelectItemEmendaComSaldoDtoCheckboxModel;
import br.gov.mj.side.web.view.components.validators.CustomRangeValidator;
import br.gov.mj.side.web.view.programa.ProgramaPage;
import br.gov.mj.side.web.view.programa.ProgramaPesquisaPage;
import br.gov.mj.side.web.view.template.TemplatePage;

public class ProgramaRecursoFinanceiroPage extends TemplatePage {

    private static final String VALOR_UTILIZAR_ITEM_PANEL = "valorUtilizarItemPanel";

    private static final long serialVersionUID = 1L;

    private Form<AcaoEmendaComSaldoDto> form;
    private ProgramaPage backPage;
    private AcaoEmendaComSaldoDto acaoEmendaComSaldoDto; // Model
    private Label lblValorTotal;
    private Label lblValorTotalEmendas;
    private DataView<EmendaComSaldoDto> dataView;
    private boolean editMode = false;
    private boolean readOnly = false;

    protected List<EmendaComSaldoDto> selectedRows = new ArrayList<EmendaComSaldoDto>(); // Model
                                                                                         // de
                                                                                         // objetos
                                                                                         // selecionados

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private RecursoFinanceiroService recursoFinanceiroService;

    public ProgramaRecursoFinanceiroPage(PageParameters pageParameters, AcaoOrcamentaria acaoOrcamentaria, ProgramaPage backPage) {
        super(pageParameters);
        this.backPage = backPage;
        setTitulo("Adicionar Recurso Financeiro");

        // Recupera a Ação Orçamentária selecionada
        this.acaoEmendaComSaldoDto = recursoFinanceiroService.buscarSaldoAcaoOrcamentaria(acaoOrcamentaria);
        this.acaoEmendaComSaldoDto.setAcaoOrcamentaria(acaoOrcamentaria);

        // Inicia componentes wicket
        initComponents();
    }

    public ProgramaRecursoFinanceiroPage(PageParameters pageParameters, ProgramaRecursoFinanceiro programaRecursoFinanceiro, ProgramaPage backPage, boolean readOnly) {
        super(pageParameters);
        editMode = true;
        this.readOnly = readOnly;

        this.backPage = backPage;
        setTitulo("Alterar Recurso Financeiro");
        if (readOnly) {
            setTitulo("Visualizar Recurso Financeiro");
        }

        // Recupera emendas selecionadas com saldo
        this.selectedRows = popularListaEmendaComSaldoDto(programaRecursoFinanceiro.getRecursoFinanceiroEmendas());

        // Recupera a ação orçamentaria com saldo.
        this.acaoEmendaComSaldoDto = popularAcaoEmendaComSaldoDto(programaRecursoFinanceiro);

        // Sincronizar lista de objetos
        sincronizarListaEmendaSelecionadas(acaoEmendaComSaldoDto, selectedRows);

        // Inicia componentes wicket
        initComponents();
    }

    private void initComponents() {

        form = componentFactory.newForm("form", acaoEmendaComSaldoDto);

        /*form.add(componentFactory.newLink("lnkDashboard", HomePage.class));
        form.add(componentFactory.newLink("lnkPesquisarPrograma", ProgramaPesquisaPage.class));
        form.add(new Link("lnkCadastrarPrograma") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(backPage);
            }
        });
        form.add(new Label("lblNomePage", getTitulo()));*/

        form.add(newLabelNumeroNomeAcaoOrcamentaria());
        form.add(newLabelSaldoLabel());
        form.add(newTextFieldValorAUtilizar());
        dataView = newDataViewEmendas();
        form.add(dataView);
        lblValorTotalEmendas = newLabelValorTotalDasEmendas();
        form.add(lblValorTotalEmendas);
        lblValorTotal = newLabelValorTotal();
        form.add(lblValorTotal);

        form.add(newButtonConfirmar());
        form.add(newButtonVoltar());

        add(form);

    }

    private Button newButtonVoltar() {
        Button btnVoltar = componentFactory.newButton("btnVoltar", () -> voltar());
        btnVoltar.setDefaultFormProcessing(false);
        return btnVoltar;
    }

    private void voltar() {
        setResponsePage(backPage);
    }

    /**
     * Método converte {@link List} de objetos {@link RecursoFinanceiroEmenda}
     * em {@link List} de objetos {@link EmendaComSaldoDto} Utilizado para
     * recuperar as {@link EmendaParlamentar} selecionadas
     * 
     * @param listRecursoFinanceiroEmenda
     * @return listEmendaComSaldoDto
     */
    private List<EmendaComSaldoDto> popularListaEmendaComSaldoDto(List<RecursoFinanceiroEmenda> listRecursoFinanceiroEmenda) {
        List<EmendaComSaldoDto> listEmendaComSaldoDto = new ArrayList<EmendaComSaldoDto>();
        for (RecursoFinanceiroEmenda recursoFinanceiroEmenda : listRecursoFinanceiroEmenda) {
            EmendaComSaldoDto emendaSaldo = new EmendaComSaldoDto();
            emendaSaldo.setEmendaParlamentar(recursoFinanceiroEmenda.getEmendaParlamentar());
            emendaSaldo.setValorUtilizar(recursoFinanceiroEmenda.getValorUtilizar());
            listEmendaComSaldoDto.add(emendaSaldo);
        }
        return listEmendaComSaldoDto;
    }

    /**
     * Método converte um objeto {@link ProgramaRecursoFinanceiro} em objeto
     * {@link AcaoEmendaComSaldoDto}. Utilizado na edição do recurso financeiro
     * de um Programa.
     * 
     * @param programaRecursoFinanceiro
     * @return AcaoEmendaComSaldoDto
     */
    private AcaoEmendaComSaldoDto popularAcaoEmendaComSaldoDto(ProgramaRecursoFinanceiro programaRecursoFinanceiro) {
        AcaoEmendaComSaldoDto acaoSaldoDto;

        acaoSaldoDto = recursoFinanceiroService.buscarSaldoAcaoOrcamentaria(programaRecursoFinanceiro.getAcaoOrcamentaria());
        acaoSaldoDto.setAcaoOrcamentaria(programaRecursoFinanceiro.getAcaoOrcamentaria());
        acaoSaldoDto.setValorUtilizar(programaRecursoFinanceiro.getValorUtilizar());
        return acaoSaldoDto;

    }

    /**
     * Método sincroniza o valor a utilizar e o saldo da(s) emenda(s)
     * selecionada(s) com as emendas do Ação Orçamentária
     * 
     * @param acaoEmendaComSaldoDto
     * @param selectedRows
     */
    private void sincronizarListaEmendaSelecionadas(AcaoEmendaComSaldoDto acaoEmendaComSaldoDto, List<EmendaComSaldoDto> selectedRows) {
        for (EmendaComSaldoDto e : acaoEmendaComSaldoDto.getListaSaldoEmenda()) {
            for (EmendaComSaldoDto emendaSelecionada : selectedRows) {
                if (emendaSelecionada.getEmendaParlamentar().getId().equals(e.getEmendaParlamentar().getId())) {
                    e.setValorUtilizar(emendaSelecionada.getValorUtilizar());
                    emendaSelecionada.setSaldo(e.getSaldo());
                }
            }
        }
    }

    private Button newButtonConfirmar() {
        Button btnConfirmar = componentFactory.newButton("btnConfirmar", () -> confirmar());
        btnConfirmar.setVisible(!readOnly);
        return btnConfirmar;
    }

    private void confirmar() {

        if (validarEmendas()) {
            AcaoEmendaComSaldoDto acaoEmenda = form.getModelObject();
            ProgramaRecursoFinanceiro prf = popularProgramaRecursoFinanceiro(acaoEmenda);

            if (validarProgramaRecursoFinanceiro(prf)) {
                backPage.getForm().getModelObject().getRecursosFinanceiros().add(prf);
                backPage.getRecursoFinanceiroPanel().atualizarLabelTotal();
                if (isEditMode()) {
                    getSession().info(getString("MN026"));
                } else {
                    getSession().info(getString("MN027"));
                }
                setResponsePage(backPage);
            }
        }
    }

    private boolean validarProgramaRecursoFinanceiro(ProgramaRecursoFinanceiro newRecursoFinanceiro) {
        for (ProgramaRecursoFinanceiro recursoFinanceiro : backPage.getForm().getModelObject().getRecursosFinanceiros()) {
            if (recursoFinanceiro.getAcaoOrcamentaria().getId().equals(newRecursoFinanceiro.getAcaoOrcamentaria().getId())) {
                if (isEditMode()) {
                    backPage.getForm().getModelObject().getRecursosFinanceiros().remove(recursoFinanceiro);
                    return true;
                } else {
                    addMsgError("MN025");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Método converte {@link List} de objetos {@link AcaoEmendaComSaldoDto} em
     * {@link List} de objetos {@link RecursoFinanceiroEmenda}
     * {@link ProgramaRecursoFinanceiro}
     * 
     * @param selectedRows
     * @return listRfe
     */
    private List<RecursoFinanceiroEmenda> popularRecursosFinanceiroEmenda(List<EmendaComSaldoDto> selectedRows) {
        List<RecursoFinanceiroEmenda> listRfe = new ArrayList<RecursoFinanceiroEmenda>();
        for (EmendaComSaldoDto emenda : selectedRows) {

            RecursoFinanceiroEmenda rfe = new RecursoFinanceiroEmenda();
            rfe.setEmendaParlamentar(emenda.getEmendaParlamentar());
            rfe.setValorUtilizar(emenda.getValorUtilizar());

            listRfe.add(rfe);
        }
        return listRfe;
    }

    /**
     * Método converte objeto {@link AcaoEmendaComSaldoDto} em objeto
     * {@link ProgramaRecursoFinanceiro}
     * 
     * @param acaoEmenda
     * @return
     */

    private ProgramaRecursoFinanceiro popularProgramaRecursoFinanceiro(AcaoEmendaComSaldoDto acaoEmenda) {
        ProgramaRecursoFinanceiro prf = new ProgramaRecursoFinanceiro();
        prf.setAcaoOrcamentaria(acaoEmenda.getAcaoOrcamentaria());
        prf.setValorUtilizar(acaoEmenda.getValorUtilizar());
        prf.setRecursoFinanceiroEmendas(popularRecursosFinanceiroEmenda(selectedRows));
        return prf;
    }

    private boolean validarEmendas() {
        List<EmendaComSaldoDto> listaEmendasSelecionadas = new ArrayList<EmendaComSaldoDto>(selectedRows);
        for (EmendaComSaldoDto emendaComSaldoDto : listaEmendasSelecionadas) {
            if (emendaComSaldoDto.getValorUtilizar() == null) {
                addMsgError("MN028", emendaComSaldoDto.getEmendaParlamentar().getNumeroEmendaParlamantar() + " - " + emendaComSaldoDto.getNomePartidoUF());
                return false;
            } else {
                int compareTo = emendaComSaldoDto.getSaldo().compareTo(emendaComSaldoDto.getValorUtilizar());
                if (compareTo < 0) {
                    addMsgError("MN029", emendaComSaldoDto.getEmendaParlamentar().getNumeroEmendaParlamantar() + " - " + emendaComSaldoDto.getNomePartidoUF());
                    return false;
                }
            }
        }
        return true;
    }

    private DataView<EmendaComSaldoDto> newDataViewEmendas() {

        return new DataView<EmendaComSaldoDto>("emendas", new ListDataProvider<EmendaComSaldoDto>(acaoEmendaComSaldoDto.getListaSaldoEmenda())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<EmendaComSaldoDto> item) {
                item.add(getCheckBoxSelected(item));
                item.add(new Label("numeroEmendaParlamentar", item.getModelObject().getEmendaParlamentar().getNumeroEmendaParlamantar()));
                item.add(new Label("nomeParlamentar", item.getModelObject().getNomePartidoUF()));
                item.add(new Label("tipoEmenda", item.getModelObject().getEmendaParlamentar().getTipoEmenda().getDescricao()));
                item.add(new Label("liberacao", item.getModelObject().getEmendaParlamentar().getPossuiLiberacao() ? "Sim" : "Não"));
                item.add(new Label("valorPrevisto", item.getModelObject().getEmendaParlamentar().getValorPrevisto()) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public <C> IConverter<C> getConverter(Class<C> type) {
                        return (IConverter<C>) new MoneyBigDecimalConverter();
                    }
                });
                item.add(new Label("saldo", item.getModelObject().getSaldo()) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public <C> IConverter<C> getConverter(Class<C> type) {
                        return (IConverter<C>) new MoneyBigDecimalConverter();
                    }
                });
                item.add(new ValorUtilizarItemPanel(VALOR_UTILIZAR_ITEM_PANEL, item));
            }

        };

    }

    private Label newLabelNumeroNomeAcaoOrcamentaria() {
        return componentFactory.newLabel("acaoOrcamentaria", acaoEmendaComSaldoDto.getAcaoOrcamentaria().getNumeroNomeAcaoOrcamentaria());
    }

    private Label newLabelValorTotalDasEmendas() {
        return new Label("valorTotalEmendas", acaoEmendaComSaldoDto.getValorTotalEmendas()) {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
    }

    private Label newLabelValorTotal() {
        return new Label("valorTotal", acaoEmendaComSaldoDto.getValorTotal()) {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
    }

    private Label newLabelSaldoLabel() {
        return new Label("saldoAcaoOrcamentaria", acaoEmendaComSaldoDto.getSaldoAcaoOrcamentaria()) {
            private static final long serialVersionUID = 1L;

            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };

    }

    private TextField<BigDecimal> newTextFieldValorAUtilizar() {
        TextField<BigDecimal> field = new TextField<BigDecimal>("valorUtilizar") {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
        field.setLabel(Model.of("Valor a utilizar"));
        field.setRequired(true);
        field.add(new CustomRangeValidator(new BigDecimal("0.00"), acaoEmendaComSaldoDto.getSaldoAcaoOrcamentaria()));

        field.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                RequestCycle.get().setMetaData(FeedbackPanelListener.SKIP_FEEDBACK_LISTENER, Boolean.TRUE);
                lblValorTotal.setDefaultModelObject(acaoEmendaComSaldoDto.getValorTotal());
                target.add(lblValorTotal);
            }
        });
        field.setEnabled(!readOnly);
        return field;
    }

    private TextField<BigDecimal> newTextFieldItemValorAUtilizar(IModel<BigDecimal> model) {
        return new TextField<BigDecimal>("valorUtilizarEmenda", model) {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
    }

    protected AjaxCheckBox getCheckBoxSelected(Item<EmendaComSaldoDto> item) {
        SelectItemEmendaComSaldoDtoCheckboxModel selectItemCheckboxModel = new SelectItemEmendaComSaldoDtoCheckboxModel(selectedRows, item.getModelObject());
        AjaxCheckBox ajaxCheckBox = new AjaxCheckBox("checkItem", selectItemCheckboxModel) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                RequestCycle.get().setMetaData(FeedbackPanelListener.SKIP_FEEDBACK_LISTENER, Boolean.TRUE);
                boolean selected = selectItemCheckboxModel.getObject();

                if (!selected) {
                    item.getModelObject().setValorUtilizar(null);
                }

                ValorUtilizarItemPanel p = (ValorUtilizarItemPanel) item.get(VALOR_UTILIZAR_ITEM_PANEL);
                p.setEnabled(isSelectRow(item.getModelObject()));
                lblValorTotalEmendas.setDefaultModelObject(acaoEmendaComSaldoDto.getValorTotalEmendas());
                lblValorTotal.setDefaultModelObject(acaoEmendaComSaldoDto.getValorTotal());
                target.add(item.get(VALOR_UTILIZAR_ITEM_PANEL), lblValorTotalEmendas, lblValorTotal);
            }
        };

        ajaxCheckBox.setEnabled(!readOnly);
        return ajaxCheckBox;

    }

    private class ValorUtilizarItemPanel extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public ValorUtilizarItemPanel(String id, Item<EmendaComSaldoDto> item) {
            super(id);
            TextField<BigDecimal> tfValorUtilizar = newTextFieldItemValorAUtilizar(new PropertyModel<BigDecimal>(item.getModelObject(), "valorUtilizar"));
            tfValorUtilizar.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    // Desabilita o Feedback listener
                    RequestCycle.get().setMetaData(FeedbackPanelListener.SKIP_FEEDBACK_LISTENER, Boolean.TRUE);

                    // Atualiza o valor das label a serem atualizadas
                    lblValorTotalEmendas.setDefaultModelObject(acaoEmendaComSaldoDto.getValorTotalEmendas());
                    lblValorTotal.setDefaultModelObject(acaoEmendaComSaldoDto.getValorTotal());

                    // Atualiza o valor a utilizar da emenda selecionada
                    atualizarItemSelectRows(item);

                    target.add(lblValorTotalEmendas, lblValorTotal);
                }
            });

            // Habilita campo na criação do componente caso esteja selecionado.
            if (readOnly) {
                setEnabled(false);
            } else {
                setEnabled(isSelectRow(item.getModelObject()));
            }
            add(tfValorUtilizar);
        }
    }

    private void atualizarItemSelectRows(Item<EmendaComSaldoDto> item) {
        if (!selectedRows.isEmpty()) {
            for (EmendaComSaldoDto emendaComSaldoDto : selectedRows) {
                if (item.getModelObject().getEmendaParlamentar().getId().equals(emendaComSaldoDto.getEmendaParlamentar().getId())) {
                    emendaComSaldoDto.setValorUtilizar(item.getModelObject().getValorUtilizar());
                }
            }
        }
    }

    private boolean isSelectRow(EmendaComSaldoDto emenda) {
        for (EmendaComSaldoDto emendaSelecionada : selectedRows) {
            if (emenda.getEmendaParlamentar().getId().equals(emendaSelecionada.getEmendaParlamentar().getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean isEditMode() {
        return editMode;
    }

}
