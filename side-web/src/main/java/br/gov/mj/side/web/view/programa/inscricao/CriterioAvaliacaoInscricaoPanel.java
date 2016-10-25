package br.gov.mj.side.web.view.programa.inscricao;

import java.util.List;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;

import br.gov.mj.side.entidades.enums.EnumTipoResposta;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaCriterioAvaliacao;

public class CriterioAvaliacaoInscricaoPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private List<InscricaoProgramaCriterioAvaliacao> criterios;
    private Form<InscricaoPrograma> form;
    private boolean readOnly;

    public CriterioAvaliacaoInscricaoPanel(String id, List<InscricaoProgramaCriterioAvaliacao> criterios, Form<InscricaoPrograma> form, boolean readOnly) {
        super(id);
        setReadOnly(readOnly);
        this.criterios = criterios;
        this.form = form;
        initComponents();
    }

    private void initComponents() {
        add(newListViewCriterios());
    }

    private PropertyListView<InscricaoProgramaCriterioAvaliacao> newListViewCriterios() {
        return new PropertyListView<InscricaoProgramaCriterioAvaliacao>("criterios", criterios) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<InscricaoProgramaCriterioAvaliacao> item) {
                InscricaoProgramaCriterioAvaliacao inscricaoProgramaCriterioAvaliacao = item.getModelObject();
                long index = item.getIndex() + 1;
                String titulo = "Critério " + index;
                if (EnumTipoResposta.LISTA_SELECAO.equals(inscricaoProgramaCriterioAvaliacao.getProgramaCriterioAvaliacao().getTipoResposta())) {
                    item.add(new ListaSelecaoCriterioPanel("criterio", inscricaoProgramaCriterioAvaliacao, titulo, form, isReadOnly()));
                }       
                if (EnumTipoResposta.NUMERICO.equals(inscricaoProgramaCriterioAvaliacao.getProgramaCriterioAvaliacao().getTipoResposta())) {
                    item.add(new NumericoCriterioPanel("criterio", inscricaoProgramaCriterioAvaliacao, titulo, form, isReadOnly()));
                }
                if (EnumTipoResposta.TEXTO.equals(inscricaoProgramaCriterioAvaliacao.getProgramaCriterioAvaliacao().getTipoResposta())) {
                    item.add(new TextoCriterioPanel("criterio", inscricaoProgramaCriterioAvaliacao, titulo, form, isReadOnly()));
                }
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
