package br.gov.mj.side.web.view.fornecedor.paineis;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.web.util.MascaraUtils;

public class FornecedorInformacoesPanel extends Panel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// #######################################_VARIAVEIS_############################################
	private Entidade fornecedor;
	
	// #######################################_ELEMENTOS_DO_WICKET_##################################
	private PanelInformacoesFornecedor panelInformacoesFornecedor;
	
    public FornecedorInformacoesPanel(String id, Entidade fornecedor) {
		super(id);
		setOutputMarkupId(true);
		this.fornecedor = fornecedor;

		initComponents();
    }
    
    private void initComponents() {
    	add(panelInformacoesFornecedor = newPanelInformacoesFornecedor());       
    }
    
    // ####################################_PAINEL_###############################################
    public PanelInformacoesFornecedor newPanelInformacoesFornecedor(){
    	panelInformacoesFornecedor = new PanelInformacoesFornecedor();
    	return panelInformacoesFornecedor;
    }
    
    private class PanelInformacoesFornecedor extends WebMarkupContainer {

		private static final long serialVersionUID = 1L;

		public PanelInformacoesFornecedor() {
            super("panelInformacoesFornecedor");

            add(new Label("numeroCnpj", MascaraUtils.formatarMascaraCpfCnpj(fornecedor.getNumeroCnpj())));
            add(new Label("nomeEntidade", fornecedor.getNomeEntidade()));
            add(new Label("nomeUf", fornecedor.getMunicipio().getUf().getSiglaUf()));
            add(new Label("municipio", fornecedor.getMunicipio().getNomeMunicipio()));
            add(new Label("tipoEndereco", fornecedor.getTipoEndereco().getDescricaoTipoEndereco()));
            add(new Label("descricaoEndereco", fornecedor.getDescricaoEndereco()));
            add(new Label("numeroEndereco", fornecedor.getNumeroEndereco()));
            add(new Label("complementoEndereco", fornecedor.getComplementoEndereco()));
            add(new Label("bairro", fornecedor.getBairro()));
            add(new Label("numeroCep", MascaraUtils.formatarMascaraCep(fornecedor.getNumeroCep())));
            add(new Label("numeroTelefone", MascaraUtils.formatarMascaraTelefone(fornecedor.getNumeroTelefone())));
            add(new Label("nomeContato", fornecedor.getNomeContato()));
            add(new Label("email", fornecedor.getEmail()));
            add(new Label("observacoes", fornecedor.getObservacoes()));
        }
    }
}
