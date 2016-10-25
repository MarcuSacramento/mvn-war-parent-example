package br.gov.mj.side.web.view.programa.inscricao;

import java.util.List;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;

import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaCriterioElegibilidade;

public class CriterioElegebilidadeInscricaoPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private List<InscricaoProgramaCriterioElegibilidade> criterios;
    private Form<InscricaoPrograma> form;
    private boolean readOnly;

    public CriterioElegebilidadeInscricaoPanel(String id, List<InscricaoProgramaCriterioElegibilidade> criterios, Form<InscricaoPrograma> form, boolean readOnly) {
        super(id);
        setReadOnly(readOnly);
        this.criterios = criterios;
        this.form = form;
        initComponents();
    }

    private void initComponents() {
        add(newListViewCriterios());
    }

    private PropertyListView<InscricaoProgramaCriterioElegibilidade> newListViewCriterios() {
        return new PropertyListView<InscricaoProgramaCriterioElegibilidade>("criterios", criterios) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<InscricaoProgramaCriterioElegibilidade> item) {
                InscricaoProgramaCriterioElegibilidade inscricaoProgramaCriterioElegibilidade = item.getModelObject();
                long index = item.getIndex() + 1;
                String titulo = "Critério " + index;
                item.add(new CriterioElegebilidadePanel("criterio", inscricaoProgramaCriterioElegibilidade, titulo, form,isReadOnly()));
            }
        };
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
