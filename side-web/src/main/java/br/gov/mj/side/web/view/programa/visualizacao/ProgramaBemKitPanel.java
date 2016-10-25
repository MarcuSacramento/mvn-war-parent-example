package br.gov.mj.side.web.view.programa.visualizacao;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaBem;
import br.gov.mj.side.entidades.programa.ProgramaKit;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.components.EntityDataProvider;

public class ProgramaBemKitPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private Programa programa;

    public ProgramaBemKitPanel(String id, Programa programa) {
        super(id);
        this.programa = programa;
        initComponents();
    }

    private void initComponents() {

        DataView<ProgramaBem> dataViewBem = newDataViewBens();
        add(dataViewBem);
        add(newPaginatorProgramaBem(dataViewBem));

        DataView<ProgramaKit> dataViewKit = newDataViewKits();
        add(dataViewKit);
        add(newPaginatorProgramaKit(dataViewKit));
    }

    private DataView<ProgramaBem> newDataViewBens() {
        DataView<ProgramaBem> dataView = new DataView<ProgramaBem>("programaBens", new EntityDataProvider<ProgramaBem>(programa.getProgramaBens())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ProgramaBem> item) {
                item.add(new Label("bem.nomeBem"));
                item.add(new Label("bem.descricaoBem"));
                item.add(new Label("quantidade"));
                item.add(new Label("quantidadePorProposta"));
            }
        };
        dataView.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataView;
    }

    public InfraAjaxPagingNavigator newPaginatorProgramaBem(DataView<ProgramaBem> dataView) {
        return new InfraAjaxPagingNavigator("paginatorBem", dataView);
    }

    public InfraAjaxPagingNavigator newPaginatorProgramaKit(DataView<ProgramaKit> dataView) {
        return new InfraAjaxPagingNavigator("paginatorKit", dataView);
    }

    private DataView<ProgramaKit> newDataViewKits() {
        DataView<ProgramaKit> dataView = new DataView<ProgramaKit>("programaKits", new EntityDataProvider<ProgramaKit>(programa.getProgramaKits())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ProgramaKit> item) {
                item.add(new Label("kit.nomeKit"));
                item.add(new Label("kit.descricaoKit"));
                item.add(new Label("quantidade"));
                item.add(new Label("quantidadePorProposta"));
            }

        };
        dataView.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataView;
    }

}
