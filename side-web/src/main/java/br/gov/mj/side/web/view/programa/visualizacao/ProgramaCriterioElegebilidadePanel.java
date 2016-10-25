package br.gov.mj.side.web.view.programa.visualizacao;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaCriterioElegibilidade;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.components.EntityDataProvider;

public class ProgramaCriterioElegebilidadePanel extends Panel {
    private static final long serialVersionUID = 1L;

    private Programa programa;

    public ProgramaCriterioElegebilidadePanel(String id, Programa programa) {
        super(id);
        this.programa = programa;
        initComponents();
    }

    private void initComponents() {
        DataView<ProgramaCriterioElegibilidade> dataView = newDataViewCriterioElegebilidade();
        add(dataView);
        add(newPaginator(dataView));

    }

    private DataView<ProgramaCriterioElegibilidade> newDataViewCriterioElegebilidade() {
        DataView<ProgramaCriterioElegibilidade> dataView = new DataView<ProgramaCriterioElegibilidade>("criteriosElegibilidade", new EntityDataProvider<ProgramaCriterioElegibilidade>(programa.getCriteriosElegibilidade())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ProgramaCriterioElegibilidade> item) {
                item.add(new Label("nomeCriterioElegibilidade"));
                item.add(new Label("descricaoCriterioElegibilidade"));
                item.add(new Label("formaVerificacao"));
            }
        };
        dataView.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataView;
    }

    public InfraAjaxPagingNavigator newPaginator(DataView<ProgramaCriterioElegibilidade> dataView) {
        return new InfraAjaxPagingNavigator("paginator", dataView);
    }

}
