package br.gov.mj.side.web.view.propostas;

import javax.inject.Inject;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.util.MascaraUtils;

public class DadosEntidadePropostaPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private Entidade entidade;

    @Inject
    private ProgramaService programaService;

    public DadosEntidadePropostaPanel(String id, Entidade entidade) {
        super(id);
        this.entidade = entidade;
        initComponents();
    }

    private void initComponents() {

        add(newLabelCnpj());
        add(newLabelNome());
        add(newLabelTipo());
        add(newLabelEndereco());
        add(newLabelTelefone());
        add(newLabelEmail());
    }

    private Label newLabelCnpj() {
        return new Label("numeroCnpj", MascaraUtils.formatarMascaraCpfCnpj(entidade.getNumeroCnpj()));
    }

    private Label newLabelNome() {
        return new Label("nomeEntidade", entidade.getNomeEntidade());
    }

    private Label newLabelTipo() {
        return new Label("tipoEntidade", entidade.getTipoEntidade().getDescricaoTipoEntidade());
    }

    private Label newLabelEndereco() {
        return new Label("endereco", entidade.getEnderecoCompleto());
    }

    private Label newLabelTelefone() {
        return new Label("numeroTelefone", entidade.getNumeroTelefone());
    }

    private Label newLabelEmail() {
        return new Label("email", entidade.getEmail());
    }
}
