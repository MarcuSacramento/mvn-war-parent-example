package br.gov.mj.side.web.view.dashboard;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

import br.gov.mj.side.web.view.acaoorcamentaria.AcaoOrcamentariaPesquisaPage;
import br.gov.mj.side.web.view.bem.BemPesquisaPage;
import br.gov.mj.side.web.view.beneficiario.BeneficiarioPesquisaPage;
import br.gov.mj.side.web.view.fornecedor.FornecedorPesquisaPage;
import br.gov.mj.side.web.view.kit.KitPagePesquisa;
import br.gov.mj.side.web.view.programa.ProgramaPesquisaPage;
import br.gov.mj.side.web.view.usuario.UsuarioPesquisaPage;

public class DashboardPanelGestor extends Panel {
	private static final long serialVersionUID = 1L;

	private PanelBotoesDashboard panelBotoesDashboard;

	public DashboardPanelGestor(String id) {
		super(id);
		setOutputMarkupId(true);

		initComponents();
	}

	private void initComponents() {
		panelBotoesDashboard = new PanelBotoesDashboard("panelBotoesDashboard");
		add(panelBotoesDashboard);
	}

	// PANEL
	
	private class PanelBotoesDashboard extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelBotoesDashboard(String id) {
			super(id);

			BookmarkablePageLink<Void> menuBemPage = new BookmarkablePageLink<Void>("btnBens", BemPesquisaPage.class);
			addAuthorization(menuBemPage, RENDER, BemPesquisaPage.ROLE_MANTER_BEM_ALTERAR, BemPesquisaPage.ROLE_MANTER_BEM_EXCLUIR, BemPesquisaPage.ROLE_MANTER_BEM_INCLUIR,
					BemPesquisaPage.ROLE_MANTER_BEM_VISUALIZAR);
			add(menuBemPage);

			BookmarkablePageLink<Void> menuKitPage = new BookmarkablePageLink<Void>("btnKits", KitPagePesquisa.class);
			addAuthorization(menuKitPage, RENDER, KitPagePesquisa.ROLE_MANTER_KIT_ALTERAR, KitPagePesquisa.ROLE_MANTER_KIT_EXCLUIR, KitPagePesquisa.ROLE_MANTER_KIT_INCLUIR,
					KitPagePesquisa.ROLE_MANTER_KIT_VISUALIZAR);
			add(menuKitPage);

			BookmarkablePageLink<Void> menuAcaoOrcamentariaPage = new BookmarkablePageLink<Void>("btnAcaoOrcamentaria", AcaoOrcamentariaPesquisaPage.class);
			addAuthorization(menuAcaoOrcamentariaPage, RENDER, AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_ALTERAR,
					AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_INCLUIR, AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_EXCLUIR,
					AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_VISUALIZAR);
			add(menuAcaoOrcamentariaPage);

			BookmarkablePageLink<Void> menuPrograma = new BookmarkablePageLink<Void>("btnProgramas", ProgramaPesquisaPage.class);
			addAuthorization(menuPrograma, RENDER, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_INCLUIR, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_ALTERAR,
					ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_EXCLUIR, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_VISUALIZAR);
			add(menuPrograma);

			BookmarkablePageLink<Void> beneficiarioPage = new BookmarkablePageLink<Void>("btnBeneficiarios", BeneficiarioPesquisaPage.class);
			addAuthorization(beneficiarioPage, RENDER, BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_INCLUIR, BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_ALTERAR,
					BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_CONSULTAR);
			add(beneficiarioPage);

			BookmarkablePageLink<Void> menuFornecedor = new BookmarkablePageLink<Void>("btnFornecedores", FornecedorPesquisaPage.class);
			addAuthorization(menuFornecedor, RENDER, FornecedorPesquisaPage.ROLE_MANTER_FORNECEDOR_INCLUIR, FornecedorPesquisaPage.ROLE_MANTER_FORNECEDOR_ALTERAR,
					FornecedorPesquisaPage.ROLE_MANTER_FORNECEDOR_EXCLUIR, FornecedorPesquisaPage.ROLE_MANTER_FORNECEDOR_VISUALIZAR);
			add(menuFornecedor);

			BookmarkablePageLink<Void> menuUsuario = new BookmarkablePageLink<Void>("btnPermissoes", UsuarioPesquisaPage.class);
			addAuthorization(menuUsuario, RENDER, UsuarioPesquisaPage.ROLE_MANTER_USUARIO_INTERNO_VINCULAR, UsuarioPesquisaPage.ROLE_MANTER_USUARIO_INTERNO_VISUALIZAR);
			add(menuUsuario);
		}
	}

	// AÇÕES

	private void addAuthorization(Component component, Action action, String... roles) {
		String s = StringUtils.join(roles, ",");
		MetaDataRoleAuthorizationStrategy.authorize(component, action, s);
	}
}
