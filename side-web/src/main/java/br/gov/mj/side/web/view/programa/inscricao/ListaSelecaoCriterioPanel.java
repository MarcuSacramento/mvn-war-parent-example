package br.gov.mj.side.web.view.programa.inscricao;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;

import br.gov.mj.side.entidades.programa.ProgramaCriterioAvaliacaoOpcaoResposta;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaCriterioAvaliacao;

public class ListaSelecaoCriterioPanel extends CriterioAvaliacaoPanel {

    private static final long serialVersionUID = 1L;
    List<ProgramaCriterioAvaliacaoOpcaoResposta> list;

    public ListaSelecaoCriterioPanel(String id, InscricaoProgramaCriterioAvaliacao inscricaoProgramaCriterioAvaliacao, String titulo, Form<InscricaoPrograma> form, boolean readOnly) {
        super(id, inscricaoProgramaCriterioAvaliacao, titulo, form,readOnly);
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        add(newListaSelecaoPanelResposta());
    }

    private DropDownChoice<String> newListaSelecaoPanelResposta() {
        DropDownChoice<String> dropDown = new DropDownChoice<String>("descricaoResposta", getStrings());
        dropDown.setEnabled(!isReadOnly());
        return dropDown;
    }

    private List<String> getStrings() {
        List<String> listString = new ArrayList<String>();
        for (ProgramaCriterioAvaliacaoOpcaoResposta programaCriterioAvaliacaoOpcaoResposta : getInscricaoProgramaCriterioAvaliacao().getProgramaCriterioAvaliacao().getCriteriosAvaliacaoOpcaoResposta()) {
            listString.add(programaCriterioAvaliacaoOpcaoResposta.getDescricaoOpcaoResposta());
        }
        return listString;
    }
}
