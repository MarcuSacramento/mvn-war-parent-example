package br.gov.mj.side.web.view.programa.visualizacao;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaCriterioAvaliacao;
import br.gov.mj.side.entidades.programa.ProgramaCriterioAvaliacaoOpcaoResposta;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.components.EntityDataProvider;

public class ProgramaCriterioAvaliacaoPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private Programa programa;

    public ProgramaCriterioAvaliacaoPanel(String id, Programa programa) {
        super(id);
        this.programa = programa;
        initComponents();
    }

    private void initComponents() {
        DataView<ProgramaCriterioAvaliacao> dataView = newDataViewCriterioElegebilidade();
        add(dataView);
        add(newPaginator(dataView));

    }

    private DataView<ProgramaCriterioAvaliacao> newDataViewCriterioElegebilidade() {
        DataView<ProgramaCriterioAvaliacao> dataView = new DataView<ProgramaCriterioAvaliacao>("criteriosAvaliacao", new EntityDataProvider<ProgramaCriterioAvaliacao>(programa.getCriteriosAvaliacao())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ProgramaCriterioAvaliacao> item) {
                item.add(new Label("nomeCriterioAvaliacao"));
                item.add(new Label("descricaoCriterioAvaliacao"));
                item.add(new Label("formaVerificacao"));
                item.add(new Label("tipoResposta.descricao"));
                item.add(new Label("pesoResposta"));
                item.add(new Label("opcoesResposta", getOpcoesRespostaDescricao(item)).setEscapeModelStrings(false));
                item.add(new Label("possuiObrigatoriedadeDeAnexo", item.getModelObject().getPossuiObrigatoriedadeDeAnexo() ? "Sim" : "Não"));
                item.add(new Label("utilizadoParaCriterioDesempate", item.getModelObject().getUtilizadoParaCriterioDesempate() ? "Sim" : "Não"));
            }
        };
        dataView.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataView;
    }

    public InfraAjaxPagingNavigator newPaginator(DataView<ProgramaCriterioAvaliacao> dataView) {
        return new InfraAjaxPagingNavigator("paginator", dataView);
    }

    protected String getOpcoesRespostaDescricao(Item<ProgramaCriterioAvaliacao> item) {
        String retorno = "";
        List<ProgramaCriterioAvaliacaoOpcaoResposta> listOpcoesRespostas = item.getModelObject().getCriteriosAvaliacaoOpcaoResposta();
        if (!listOpcoesRespostas.isEmpty()) {
            for (ProgramaCriterioAvaliacaoOpcaoResposta programaCriterioAvaliacaoOpcaoResposta : listOpcoesRespostas) {
                retorno = retorno + ", " + programaCriterioAvaliacaoOpcaoResposta.getDescricaoOpcaoResposta() + "<span style='color:red;'> (" + programaCriterioAvaliacaoOpcaoResposta.getNotaOpcaoResposta() + ")</span>";
            }
            retorno = retorno.substring(2);
        } else {
            retorno = retorno + "-";
        }
        return retorno;
    }
}
