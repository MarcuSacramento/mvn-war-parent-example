package br.gov.mj.side.web.view.programa.visualizacao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.util.convert.IConverter;

import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.converter.MoneyBigDecimalConverter;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaRecursoFinanceiro;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.components.EntityDataProvider;

public class ProgramaRecursoFinanceiroPanel extends Panel {

    private static final long serialVersionUID = 1L;
    private Programa programa;

    public ProgramaRecursoFinanceiroPanel(String id, Programa programa) {
        super(id);
        this.programa = programa;
        initComponents();
    }

    private void initComponents() {

        DataView<ProgramaRecursoFinanceiro> dataView = newDataRecursoFinanceiro();
        add(dataView);
        add(newPaginator(dataView));
        add(newLabelValorTotal());
        add(newLabelValorMaximoProposta()); //valorMaximoProposta
    }

    private DataView<ProgramaRecursoFinanceiro> newDataRecursoFinanceiro() {
        return new DataView<ProgramaRecursoFinanceiro>("recursosFinanceiros", new EntityDataProvider<ProgramaRecursoFinanceiro>(getLista())) {
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
                setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
            }
        };
    }

    public InfraAjaxPagingNavigator newPaginator(DataView<ProgramaRecursoFinanceiro> dataView) {
        return new InfraAjaxPagingNavigator("paginator", dataView);
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
    
    private Label newLabelValorMaximoProposta() {
        return new Label("valorMaximoProposta", programa.getValorMaximoProposta()) {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
    }

    private BigDecimal getValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ProgramaRecursoFinanceiro recursoFinanceiro : getLista()) {
            total = total.add(recursoFinanceiro.getTotal());
        }
        return total;
    }

    private List<ProgramaRecursoFinanceiro> getLista() {
        Set<ProgramaRecursoFinanceiro> lista = new HashSet<ProgramaRecursoFinanceiro>(programa.getRecursosFinanceiros());
        return new ArrayList<ProgramaRecursoFinanceiro>(lista);
    }

}
