package br.gov.mj.side.web.view.template;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.page.BasePage;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.web.util.SideSession;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.LoginPage;
import br.gov.mj.side.web.view.acaoorcamentaria.AcaoOrcamentariaPesquisaPage;
import br.gov.mj.side.web.view.bem.BemPesquisaPage;
import br.gov.mj.side.web.view.beneficiario.BeneficiarioPesquisaPage;
import br.gov.mj.side.web.view.fornecedor.FornecedorPesquisaPage;
import br.gov.mj.side.web.view.kit.KitPagePesquisa;
import br.gov.mj.side.web.view.programa.ProgramaPesquisaPage;
import br.gov.mj.side.web.view.usuario.NotificacaoPage;
import br.gov.mj.side.web.view.usuario.UsuarioPesquisaPage;

public class TemplatePage extends BasePage {
    private static final long serialVersionUID = 1L;

    private FeedbackPanel feedbackPanel;
    private Label lbTitulo;
    private String titulo;
    protected Label lbSubTitulo;

    @Inject
    private ComponentFactory factory;

    public TemplatePage(final PageParameters pageParameters) {
        super(pageParameters);

        feedbackPanel = new FeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupPlaceholderTag(true);
        add(feedbackPanel);

        lbTitulo = new Label("lbTitulo", "Titulo");
        add(lbTitulo);

        lbSubTitulo = new Label("lbSubTitulo", "");
        add(lbSubTitulo);

        Label label = factory.newLabel("labelNomeCompletoUsuario", () -> getNomeCompleto());
        add(label);

        adicionarMenu();
        add(factory.newLabel("labelVersion", getImplementationVersion()));

        add(new Link<Void>("linkLogoff") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                AuthenticatedWebSession.get().invalidate();
                setResponsePage(getApplication().getHomePage());
            }
        });

    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        AuthenticatedWebApplication app = (AuthenticatedWebApplication) Application.get();
        if (!AuthenticatedWebSession.get().isSignedIn()) {
            app.restartResponseAtSignInPage();
        }
    }

    protected boolean isMenuVisible() {
        return true;
    }

    protected boolean isMenuLoginVisible() {
        return getUsuarioLogadoDaSessao() == null;
    }

    private void adicionarMenu() {

        PanelMenu menu = new PanelMenu("panelMenu");
        menu.setVisible(isMenuVisible());
        add(menu);

        PanelLogin panelLogin = new PanelLogin("panelLogin");
        panelLogin.setVisible(isMenuLoginVisible());
        add(panelLogin);

/*        InfraAjaxFallbackLink btnViewAll = factory.newAjaxFallbackLink("btnViewAll", (target) -> setResponsePage(new NotificacaoPage(getPageParameters())));
        add(btnViewAll);*/
    }

    private class PanelLogin extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelLogin(String id) {
            super(id);
            BookmarkablePageLink<Void> logar = new BookmarkablePageLink<Void>("loginPage", LoginPage.class);
            add(logar);
        }
    }

    private class PanelMenu extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelMenu(String id) {
            super(id);

            /*
             * add(new Link<Void>("linkLogoff") { private static final long
             * serialVersionUID = 1L;
             * 
             * @Override public void onClick() {
             * AuthenticatedWebSession.get().invalidate();
             * setResponsePage(getApplication().getHomePage()); } });
             */

            BookmarkablePageLink<Void> homePage = new BookmarkablePageLink<Void>("homePage", HomePage.class);
            add(homePage);

            Label labelDefinicoes = new Label("labelDefinicoes", "Definições");
            addAuthorization(labelDefinicoes, RENDER, KitPagePesquisa.ROLE_MANTER_KIT_ALTERAR, KitPagePesquisa.ROLE_MANTER_KIT_EXCLUIR, KitPagePesquisa.ROLE_MANTER_KIT_INCLUIR, KitPagePesquisa.ROLE_MANTER_KIT_VISUALIZAR, BemPesquisaPage.ROLE_MANTER_BEM_ALTERAR,
                    BemPesquisaPage.ROLE_MANTER_BEM_EXCLUIR, BemPesquisaPage.ROLE_MANTER_BEM_INCLUIR, BemPesquisaPage.ROLE_MANTER_BEM_VISUALIZAR, AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_ALTERAR, AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_INCLUIR,
                    AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_EXCLUIR, AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_VISUALIZAR, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_INCLUIR, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_ALTERAR,
                    ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_EXCLUIR, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_VISUALIZAR);

            add(labelDefinicoes);

            BookmarkablePageLink<Void> menuBemPage = new BookmarkablePageLink<Void>("menuBemPage", BemPesquisaPage.class);
            addAuthorization(menuBemPage, RENDER, BemPesquisaPage.ROLE_MANTER_BEM_ALTERAR, BemPesquisaPage.ROLE_MANTER_BEM_EXCLUIR, BemPesquisaPage.ROLE_MANTER_BEM_INCLUIR, BemPesquisaPage.ROLE_MANTER_BEM_VISUALIZAR);
            add(menuBemPage);

            BookmarkablePageLink<Void> menuKitPage = new BookmarkablePageLink<Void>("menuKitPage", KitPagePesquisa.class);
            addAuthorization(menuKitPage, RENDER, KitPagePesquisa.ROLE_MANTER_KIT_ALTERAR, KitPagePesquisa.ROLE_MANTER_KIT_EXCLUIR, KitPagePesquisa.ROLE_MANTER_KIT_INCLUIR, KitPagePesquisa.ROLE_MANTER_KIT_VISUALIZAR);
            add(menuKitPage);

            BookmarkablePageLink<Void> menuFornecedor = new BookmarkablePageLink<Void>("menuFornecedorPage", FornecedorPesquisaPage.class);
            addAuthorization(menuFornecedor, RENDER, FornecedorPesquisaPage.ROLE_MANTER_FORNECEDOR_INCLUIR, FornecedorPesquisaPage.ROLE_MANTER_FORNECEDOR_ALTERAR, FornecedorPesquisaPage.ROLE_MANTER_FORNECEDOR_EXCLUIR, FornecedorPesquisaPage.ROLE_MANTER_FORNECEDOR_VISUALIZAR);
            add(menuFornecedor);

            BookmarkablePageLink<Void> menuAcaoOrcamentariaPage = new BookmarkablePageLink<Void>("menuRecursoPage", AcaoOrcamentariaPesquisaPage.class);
            addAuthorization(menuAcaoOrcamentariaPage, RENDER, AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_ALTERAR, AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_INCLUIR, AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_EXCLUIR,
                    AcaoOrcamentariaPesquisaPage.ROLE_MANTER_ACAO_ORCAMENTARIA_VISUALIZAR);
            add(menuAcaoOrcamentariaPage);

            BookmarkablePageLink<Void> menuPrograma = new BookmarkablePageLink<Void>("menuProgramaPage", ProgramaPesquisaPage.class);
            addAuthorization(menuPrograma, RENDER, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_INCLUIR, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_ALTERAR, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_EXCLUIR, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_VISUALIZAR);
            add(menuPrograma);

            BookmarkablePageLink<Void> beneficiarioPage = new BookmarkablePageLink<Void>("beneficiarioPesquisaPage", BeneficiarioPesquisaPage.class);
            addAuthorization(beneficiarioPage, RENDER, BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_INCLUIR, BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_ALTERAR, BeneficiarioPesquisaPage.ROLE_MANTER_BENEFICIARIO_CONSULTAR);
            add(beneficiarioPage);

            BookmarkablePageLink<Void> menuUsuario = new BookmarkablePageLink<Void>("menuUsuarioPage", UsuarioPesquisaPage.class);
            addAuthorization(menuUsuario, RENDER, UsuarioPesquisaPage.ROLE_MANTER_USUARIO_INTERNO_VINCULAR, UsuarioPesquisaPage.ROLE_MANTER_USUARIO_INTERNO_VISUALIZAR);
            add(menuUsuario);
        }
    }

    protected String getIdentificador() {
        if (getSideSession().isSignedIn()) {
            return getSideSession().getUsuarioLogado().getLogin();
        } else {
            return "Usuário";
        }
    }

    protected String getNomeCompleto() {
        if (getSideSession().isSignedIn()) {
            return getSideSession().getUsuarioLogado().getNomeCompleto();
        } else {
            return "Usuário";
        }
    }

    public Usuario getUsuarioLogadoDaSessao() {
        if (getSideSession().isSignedIn()) {
            return getSideSession().getUsuarioLogado();
        } else {
            return null;
        }
    }

    protected void setTitulo(String titulo) {
        this.titulo = titulo;
        lbTitulo.setDefaultModel(new Model<String>(titulo));
    }

    public String getTitulo() {
        return titulo;
    }

    private void addAuthorization(Component component, Action action, String... roles) {
        String s = StringUtils.join(roles, ",");
        MetaDataRoleAuthorizationStrategy.authorize(component, action, s);
    }

    protected SideSession getSideSession() {
        return SideSession.get();
    }

    protected String getUrlBase(String urlComplemento) {
        if (urlComplemento == null) {
            urlComplemento = "";
        }
        return RequestCycle.get().getRequest().getUrl().getProtocol() + "://" + RequestCycle.get().getRequest().getUrl().getHost() + ":" + RequestCycle.get().getRequest().getUrl().getPort() + "" + RequestCycle.get().getRequest().getContextPath() + "/" + urlComplemento;
    }

}
