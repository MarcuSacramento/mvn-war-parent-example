package br.gov.mj.side.web.view.fornecedor.paineis;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;

import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.web.util.MascaraUtils;
import br.gov.mj.side.web.view.planejarLicitacao.PlanejamentoLicitacaoPage;

@AuthorizeInstantiation({
		PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_VISUALIZAR,
		PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_INCLUIR,
		PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_ALTERAR,
		PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_EXCLUIR })
public class PanelFornecedorDados extends Panel {
	private static final long serialVersionUID = 1L;

	public static final String ROLE_MANTER_FORNECEDOR_VISUALIZAR = "manter_fornecedor:visualizar";
	public static final String ROLE_MANTER_FORNECEDOR_INCLUIR = "manter_fornecedor:incluir";
	public static final String ROLE_MANTER_FORNECEDOR_ALTERAR = "manter_fornecedor:alterar";
	public static final String ROLE_MANTER_FORNECEDOR_EXCLUIR = "manter_fornecedor:excluir";

	// #######################################_VARIAVEIS_############################################
	private List<PessoaEntidade> pessoas;

	// #######################################_ELEMENTOS_DO_WICKET_##################################
	private DataView<PessoaEntidade> dataViewPessoa;

	// #####################################_CONSTRUTOR_#############################################
	public PanelFornecedorDados(String id, List<PessoaEntidade> pessoas) {
		super(id);
		this.pessoas = pessoas;

		initComponents();
	}
	
    private void initComponents() {
    	add(new PanelDataView("panelDataView"));       
    }

	// ####################################_PAINEIS_###############################################
	private class PanelDataView extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelDataView(String id) {
			super(id);
			add(newDataViewPessoa());
			add(new InfraAjaxPagingNavigator("pagination", dataViewPessoa));
		}
	}

	// ####################################_COMPONENTE_WICKET_###############################################
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private DataView newDataViewPessoa() {
		dataViewPessoa = new DataView<PessoaEntidade>("dataTitulares", new ListDataProvider(pessoas != null ? pessoas : new ArrayList<PessoaEntidade>())) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<PessoaEntidade> item) {
				item.clearOriginalDestination();
				item.add(new Label("pessoa.nomePessoa", item.getModelObject().getPessoa().getNomePessoa()));
				item.add(new Label("pessoa.numeroCpf", MascaraUtils.formatarMascaraCpfCnpj(item.getModelObject().getPessoa().getNumeroCpf())));
				item.add(new Label("pessoa.numeroTelefone", MascaraUtils.formatarMascaraTelefone(item.getModelObject().getPessoa().getNumeroTelefone())));
				item.add(new Label("pessoa.email", item.getModelObject().getPessoa().getEmail()));
				item.add(new Label("situacao", item.getModelObject().getPessoa().getStatusPessoa().getDescricao()));
			}
		};
		return dataViewPessoa;
	}
}
