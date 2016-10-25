package br.gov.mj.side.web.view;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.web.view.cadastraritem.CadastrarItemIdentificacaoPage;
import br.gov.mj.side.web.view.cadastraritem.PesquisarItemPage;
import br.gov.mj.side.web.view.consultaPublica.ConsultaProgramasBeneficiarioPage;
import br.gov.mj.side.web.view.dashboard.DashboardInscricoesPage;
import br.gov.mj.side.web.view.dashboard.InformacoesEntidadePage;
import br.gov.mj.side.web.view.dashboard.InformacoesGeraisRepresentantePage;

public class DashboardPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private Page backPage;
    private HomePage homePage;
    private Usuario usuarioLogado;
    private Entidade entidadeLogada;

    private PanelBotoesDashboard panelBotoesDashboard;

    private List<Entidade> listaRetorno = new ArrayList<Entidade>();

    @Inject
    private ComponentFactory componentFactory;

    public DashboardPanel(String id) {
        super(id);
        setOutputMarkupId(true);

        initVariaveis();
        initComponents();
    }

    private void initVariaveis() {
        // Inicializar as variáveis
    }

    private void initComponents() {
        panelBotoesDashboard = new PanelBotoesDashboard("panelBotoesDashboard");
        add(panelBotoesDashboard);
    }

    private class PanelBotoesDashboard extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBotoesDashboard(String id) {
            super(id);

            add(newButtonInformacoesGeraid()); // btnInformacoesGerais
            add(newButtonInformacoesEntidade()); // btnInformacoesEntidade
            add(newButtonPesquisaAberta()); // btnPesquisaAberta
            add(newButtonInscricoes()); // btnInscricoes
            add(newButtonCadastrarItem()); // btnCadastrarItem
            add(newButtonPesquisarItem()); // btnPesquisarItem
        }
    }

    // COMPONENTES

    public InfraAjaxFallbackLink<Void> newButtonInformacoesGeraid() {
        return componentFactory.newAjaxFallbackLink("btnInformacoesGerais", (target) -> informacoesGerais());
    }

    public InfraAjaxFallbackLink<Void> newButtonInformacoesEntidade() {
        InfraAjaxFallbackLink<Void> buttonInfoEntidade = componentFactory.newAjaxFallbackLink("btnInformacoesEntidade", (target) -> informacoesEntidade());
        return buttonInfoEntidade;
    }

    public InfraAjaxFallbackLink<Void> newButtonInscricoes() {
        InfraAjaxFallbackLink<Void> buttonInfoInscricoes = componentFactory.newAjaxFallbackLink("btnInscricoes", (target) -> inscricoes());
        return buttonInfoInscricoes;
    }

    public InfraAjaxFallbackLink<Void> newButtonPesquisaAberta() {
        InfraAjaxFallbackLink<Void> buttonPesquisa = componentFactory.newAjaxFallbackLink("btnPesquisaAberta", (target) -> pesquisaAberta());
        return buttonPesquisa;
    }

    public InfraAjaxFallbackLink<Void> newButtonCadastrarItem() {
        InfraAjaxFallbackLink<Void> buttonCadastrarItem = componentFactory.newAjaxFallbackLink("btnCadastrarItem", (target) -> cadastrarItem());
        return buttonCadastrarItem;
    }
    
    public InfraAjaxFallbackLink<Void> newButtonPesquisarItem() {
        InfraAjaxFallbackLink<Void> btnPesquisarItem = componentFactory.newAjaxFallbackLink("btnPesquisarItem", (target) -> pesquisarItem());
        return btnPesquisarItem;
    }

    // AÇÕES

    private void informacoesGerais() {
        // setResponsePage(new
        // InformacoesGeraisRepresentantePage(null,backPage));
        setResponsePage(new InformacoesGeraisRepresentantePage(null));
    }

    private void informacoesEntidade() {
        setResponsePage(new InformacoesEntidadePage(null));
    }

    private void inscricoes() {
        setResponsePage(new DashboardInscricoesPage(null));
    }

    private void pesquisaAberta() {
        setResponsePage(new ConsultaProgramasBeneficiarioPage(null));
    }

    private void cadastrarItem() {
        setResponsePage(new CadastrarItemIdentificacaoPage(null));
    }
    
    private void pesquisarItem() {
        setResponsePage(new PesquisarItemPage(null));
    }
}
