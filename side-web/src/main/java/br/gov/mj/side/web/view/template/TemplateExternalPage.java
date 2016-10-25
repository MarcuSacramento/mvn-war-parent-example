package br.gov.mj.side.web.view.template;

import javax.inject.Inject;

import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.page.BasePage;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.web.util.SideSession;
import br.gov.mj.side.web.view.components.LanguagePanel;
import br.gov.mj.side.web.view.consultaPublica.ConsultaPublicaPesquisaPage;

public class TemplateExternalPage extends BasePage {
    private static final long serialVersionUID = 1L;

    @Inject
    private ComponentFactory factory;

    private FeedbackPanel feedbackPanel;
    private LanguagePanel languagePanel;
    private PanelMenu menu;
    protected Label lbTitulo;
    protected Label lbSubTitulo;

    public TemplateExternalPage(final PageParameters pageParameters) {
        super(pageParameters);

        feedbackPanel = new FeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupPlaceholderTag(true);
        add(feedbackPanel);

        this.languagePanel = new LanguagePanel("languagePanel");
        add(this.languagePanel.setVisible(false));

        lbTitulo = new Label("lbTitulo", "");
        add(lbTitulo);

        lbSubTitulo = new Label("lbSubTitulo", "");
        add(lbSubTitulo);

        initMenu();
        add(menu);

        add(factory.newLabel("labelVersion", getImplementationVersion()));

    }

    protected boolean isMenuVisible() {
        return true;
    }

    private void initMenu() {
        menu = new PanelMenu("panelMenu");
        menu.setVisible(isMenuVisible());
    }

    public class PanelMenu extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelMenu(String id) {
            super(id);
            add(factory.newLink("pesquisaAberta", ConsultaPublicaPesquisaPage.class));
        }
    }

    protected void setTitulo(String titulo) {
        lbTitulo.setDefaultModel(new Model<String>(titulo));
    }

    protected void setSubTitulo(String subTitulo) {
        lbSubTitulo.setDefaultModel(new Model<String>(subTitulo));
    }

    protected void setFilterFeedbackPanel(IFeedbackMessageFilter iFeedbackMessageFilter) {
        this.feedbackPanel.setFilter(iFeedbackMessageFilter);
    }

    public ComponentFactory getFactory() {
        return factory;
    }

    public LanguagePanel getLanguagePanel() {
        return languagePanel;
    }

    public PanelMenu getMenu() {
        return menu;
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
