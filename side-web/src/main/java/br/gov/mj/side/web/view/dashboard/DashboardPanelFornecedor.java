package br.gov.mj.side.web.view.dashboard;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.side.web.view.cadastraritem.CadastrarItemIdentificacaoPage;
import br.gov.mj.side.web.view.fornecedor.FornecedorContratoPage;
import br.gov.mj.side.web.view.fornecedor.FornecedorInformacoesPage;

public class DashboardPanelFornecedor extends Panel {

    private static final long serialVersionUID = 1L;

    // #######################################_ELEMENTOS_DO_WICKET_##################################
    private PanelBotoesDashboard panelBotoesDashboard;

    // #####################################_INJEÇÃO_DE_DEPENDENCIA_##############################################
    @Inject
    private ComponentFactory componentFactory;

    // #####################################_CONSTRUTOR_#############################################
    public DashboardPanelFornecedor(String id) {
        super(id);
        setOutputMarkupId(true);

        initComponents();
    }

    private void initComponents() {
        panelBotoesDashboard = new PanelBotoesDashboard("panelBotoesDashboard");
        add(panelBotoesDashboard);
    }

    // ####################################_PAINEL_###############################################
    private class PanelBotoesDashboard extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBotoesDashboard(String id) {
            super(id);

            InfraAjaxFallbackLink<Void> menuFornecedorInformacoesPage = componentFactory.newAjaxFallbackLink("btnInfomacoesCadastrais", (target) -> informacoesCadastrais());
            addAuthorization(menuFornecedorInformacoesPage, RENDER, FornecedorInformacoesPage.ROLE_MANTER_INFORMACAO_VISUALIZAR);
            add(menuFornecedorInformacoesPage);

            InfraAjaxFallbackLink<Void> menuFornecedorContratoPage = componentFactory.newAjaxFallbackLink("btnContratos", (target) -> contratos());
            addAuthorization(menuFornecedorInformacoesPage, RENDER, FornecedorContratoPage.ROLE_MANTER_CONTRATO_FORNECEDOR_VISUALIZAR, FornecedorContratoPage.ROLE_MANTER_CONTRATO_FORNECEDOR_EXECUTAR);
            add(menuFornecedorContratoPage);

            InfraAjaxFallbackLink<Void> menuCadastrarItemIdentificacaoPage = componentFactory.newAjaxFallbackLink("btnCadastrarItem", (target) -> cadastrarItem());
            addAuthorization(menuCadastrarItemIdentificacaoPage, RENDER, CadastrarItemIdentificacaoPage.ROLE_MANTER_CADASTRAR_ITEM_ALTERAR, CadastrarItemIdentificacaoPage.ROLE_MANTER_CADASTRAR_ITEM_INCLUIR, CadastrarItemIdentificacaoPage.ROLE_MANTER_CADASTRAR_ITEM_EXCLUIR,
                    CadastrarItemIdentificacaoPage.ROLE_MANTER_CADASTRAR_ITEM_VISUALIZAR);
            add(menuCadastrarItemIdentificacaoPage);
        }
    }

    // ####################################_AÇÕES_###############################################
    private void informacoesCadastrais() {
        setResponsePage(new FornecedorInformacoesPage(null));
    }

    private void contratos() {
        setResponsePage(new FornecedorContratoPage(null));
    }

    private void cadastrarItem() {
        setResponsePage(new CadastrarItemIdentificacaoPage(null));
    }

    private void addAuthorization(Component component, Action action, String... roles) {
        String s = StringUtils.join(roles, ",");
        MetaDataRoleAuthorizationStrategy.authorize(component, action, s);
    }

}
