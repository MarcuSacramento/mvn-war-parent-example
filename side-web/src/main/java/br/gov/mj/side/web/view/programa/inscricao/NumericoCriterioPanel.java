package br.gov.mj.side.web.view.programa.inscricao;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;

import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaCriterioAvaliacao;

public class NumericoCriterioPanel extends CriterioAvaliacaoPanel {

    private static final long serialVersionUID = 1L;

    public NumericoCriterioPanel(String id, InscricaoProgramaCriterioAvaliacao inscricaoProgramaCriterioAvaliacao, String titulo, Form<InscricaoPrograma> form, boolean readOnly) {
        super(id, inscricaoProgramaCriterioAvaliacao, titulo, form, readOnly);
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        add(newNumericoCriterioPanelResposta());
    }

    private TextField<String> newNumericoCriterioPanelResposta() {
        TextField<String> textField = new TextField<String>("descricaoResposta");
        textField.setEnabled(!isReadOnly());
        return textField;
    }
}
