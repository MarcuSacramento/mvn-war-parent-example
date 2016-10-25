package br.gov.mj.side.web.view.programa.visualizacao;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaCriterioAcompanhamento;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.components.EntityDataProvider;

public class ProgramaCriterioAcompanhamentoPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private Programa programa;

    public ProgramaCriterioAcompanhamentoPanel(String id, Programa programa) {
        super(id);
        this.programa = programa;
        initComponents();
    }

    private void initComponents() {
        DataView<ProgramaCriterioAcompanhamento> dataView = newDataViewCriterioElegebilidade();
        add(dataView);
        add(newPaginator(dataView));

    }

    private DataView<ProgramaCriterioAcompanhamento> newDataViewCriterioElegebilidade() {
        DataView<ProgramaCriterioAcompanhamento> dataView = new DataView<ProgramaCriterioAcompanhamento>("criteriosAcompanhamento", new EntityDataProvider<ProgramaCriterioAcompanhamento>(programa.getCriteriosAcompanhamento())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ProgramaCriterioAcompanhamento> item) {
                item.add(new Label("nomeCriterioAcompanhamento"));
                item.add(new Label("descricaoCriterioAcompanhamento"));
                item.add(new Label("formaAcompanhamento"));
            }
        };
        dataView.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataView;
    }

    public InfraAjaxPagingNavigator newPaginator(DataView<ProgramaCriterioAcompanhamento> dataView) {
        return new InfraAjaxPagingNavigator("paginator", dataView);
    }

}
