package br.gov.mj.side.web.view.fornecedor.paineis;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.side.entidades.entidade.EntidadeAnexo;
import br.gov.mj.side.web.dto.AnexoDto;
import br.gov.mj.side.web.service.AnexoEntidadeService;
import br.gov.mj.side.web.util.DataUtil;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.components.EntityDataProvider;
import br.gov.mj.side.web.view.planejarLicitacao.PlanejamentoLicitacaoPage;

@AuthorizeInstantiation({
		PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_VISUALIZAR,
		PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_INCLUIR,
		PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_ALTERAR,
		PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_EXCLUIR })
public class PanelFornecedorAnexo extends Panel {
	private static final long serialVersionUID = 1L;

	public static final String ROLE_MANTER_FORNECEDOR_VISUALIZAR = "manter_fornecedor:visualizar";
	public static final String ROLE_MANTER_FORNECEDOR_INCLUIR = "manter_fornecedor:incluir";
	public static final String ROLE_MANTER_FORNECEDOR_ALTERAR = "manter_fornecedor:alterar";
	public static final String ROLE_MANTER_FORNECEDOR_EXCLUIR = "manter_fornecedor:excluir";

	// #######################################_VARIAVEIS_############################################
	private List<EntidadeAnexo> listAnexoTemp = new ArrayList<EntidadeAnexo>();

	// #######################################_ELEMENTOS_DO_WICKET_##################################
	private Button btnDownload;

	// #####################################_INJEÇÃO_DE_DEPENDENCIA_#################################
	@Inject
	private ComponentFactory componentFactory;
	
	@Inject
        private AnexoEntidadeService anexoService;

	// #####################################_CONSTRUTOR_#############################################
	public PanelFornecedorAnexo(String id, List<EntidadeAnexo> listAnexoTemp) {
		super(id);
		setOutputMarkupId(true);
		this.listAnexoTemp = listAnexoTemp;

		add(new PanelPrincipalAnexo("panelPrincipalAnexo"));
	}

	// ####################################_PAINEL_###############################################
	private class PanelPrincipalAnexo extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelPrincipalAnexo(String id) {
			super(id);
			DataView<EntidadeAnexo> dataView = getDataViewAnexos("anexos");
			add(dataView);
			add(new InfraAjaxPagingNavigator("paginator", dataView));
		}
	}

	// ####################################_COMPONENTE_WICKET_###############################################
	private DataView<EntidadeAnexo> getDataViewAnexos(String id) {
		return new DataView<EntidadeAnexo>(id, new EntityDataProvider<EntidadeAnexo>(listAnexoTemp)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<EntidadeAnexo> item) {
				item.add(new Label("descricao", item.getModelObject().getTipoArquivo().getDescricao()));
				item.add(new Label("nomeAnexo", item.getModelObject().getNomeAnexo()));
				item.add(new Label("tamanhoArquivoEmMB", item.getModelObject().getTamanhoArquivoEmMB()));
				item.add(new Label("dataAnexoTable", DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataDocumento(), "dd/MM/yyyy")));
				item.add(new Label("dataCadastro", DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataCadastro(), "dd/MM/yyyy HH:mm:ss")));

				btnDownload = componentFactory.newButton("btnDonwload", () -> download(item));
				item.add(btnDownload);
			}
		};
	}

	// ####################################_AÇÕES_###############################################
    private void download(Item<EntidadeAnexo> item) {
        EntidadeAnexo a = item.getModelObject();
        if (a.getId() != null) {
            AnexoDto retorno = anexoService.buscarPeloId(a.getId());
            SideUtil.download(retorno.getConteudo(), retorno.getNomeAnexo());
        } else {
            SideUtil.download(a.getConteudo(), a.getNomeAnexo());
        }

    }

}
