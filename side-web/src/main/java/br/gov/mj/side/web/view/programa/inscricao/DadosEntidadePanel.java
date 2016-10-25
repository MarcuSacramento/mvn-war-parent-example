package br.gov.mj.side.web.view.programa.inscricao;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.web.util.MascaraUtils;

public class DadosEntidadePanel extends Panel {

    private static final long serialVersionUID = 1L;

    private Entidade entidade;
    
    public DadosEntidadePanel(String id, Entidade entidade) {
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
        String numeroCnpj = entidade.getNumeroCnpj()!= null?entidade.getNumeroCnpj():"";
        Label lbl = new Label("numeroCnpj",MascaraUtils.formatarMascaraCpfCnpj(numeroCnpj));
        return lbl;
    }
    
    private Label newLabelNome() {
        return new Label("nomeEntidade",entidade.getNomeEntidade());
    }
    private Label newLabelTipo() {
        return new Label("tipoEntidade",getTipoEntidade());
    }
    
    private String getTipoEntidade() {
        if(entidade.getTipoEntidade() ==  null){
            return "";
        }
        return entidade.getTipoEntidade().getDescricaoTipoEntidade();
    }
    private Label newLabelEndereco() {
        return new Label("endereco",getEndereco());
    }

    private String getEndereco() {
        try {
            return entidade.getEnderecoCompleto();
        } catch (NullPointerException e) {
            return "";
        }
    }
    private Label newLabelTelefone() {
        return new Label("numeroTelefone",entidade.getNumeroTelefone());
    }
    private Label newLabelEmail() {
        return new Label("email",entidade.getEmail());
    }
    
    

}
