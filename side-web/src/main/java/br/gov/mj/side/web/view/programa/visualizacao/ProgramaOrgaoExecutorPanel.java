package br.gov.mj.side.web.view.programa.visualizacao;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class ProgramaOrgaoExecutorPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public ProgramaOrgaoExecutorPanel(String id) {
        super(id);
        initComponents();
    }

    private void initComponents() {
        add(newLabelOrgao());
        add(newLabelUnidadeExecutora());
    }

    private Label newLabelOrgao() {
        return new Label("unidadeExecutora.orgao.nomeOrgao");
    }

    private Label newLabelUnidadeExecutora() {
        return new Label("unidadeExecutora.nomeUnidadeExecutora");
    }
}
