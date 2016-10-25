package br.gov.mj.side.web.view;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.web.view.dashboard.DashboardPanelFornecedor;
import br.gov.mj.side.web.view.dashboard.DashboardPanelGestor;
import br.gov.mj.side.web.view.template.TemplatePage;

public class HomePage extends TemplatePage {
    public static final String ROLE_MANTER_INSCRICAO_VISUALIZAR = "manter_inscricao_programa:visualizar";
    public static final String ROLE_MANTER_INSCRICAO_ALTERAR = "manter_inscricao_programa:alterar";
    public static final String ROLE_PANEL_PERFIL_GESTOR_VISUALIZAR = "panel_perfil_gestor:visualizar";
    public static final String ROLE_PANEL_PERFIL_FORNECEDOR_VISUALIZAR = "panel_perfil_fornecedor:visualizar";

    private static final long serialVersionUID = 1L;
    private Form<HomePage> form;
    private Usuario usuarioLogado;
    private Entidade entidadeEscolhida = new Entidade();

    private DashboardPanel dashboardPessoasPanel;
    private DashboardPanelGestor dashboardPanelGestor;
    private DashboardPanelFornecedor dashboardPanelFornecedor;

    private List<Entidade> listaRetorno = new ArrayList<Entidade>();

    @Inject
    private ComponentFactory componentFactory;

    public HomePage(PageParameters pageParameters) {
        super(pageParameters);

        initVariaveis();
        initComponents();

        if (entidadeEscolhida != null && entidadeEscolhida.getId() != null) {
            setTitulo(entidadeEscolhida.getNomeEntidade());
        } else {
            setTitulo("");
        }
    }

    private void initVariaveis() {
        usuarioLogado = getUsuarioLogadoDaSessao();
        entidadeEscolhida = (Entidade) getSessionAttribute("entidade");
    }

    private void initComponents() {
        form = new Form<HomePage>("form", new CompoundPropertyModel<HomePage>(this));
        add(form);

        dashboardPessoasPanel = new DashboardPanel("dashboardPessoasPanel");
        authorize(dashboardPessoasPanel, RENDER, HomePage.ROLE_MANTER_INSCRICAO_ALTERAR);
        form.add(dashboardPessoasPanel);

        dashboardPanelGestor = new DashboardPanelGestor("dashboardPanelGestor");
        authorize(dashboardPanelGestor, RENDER, HomePage.ROLE_PANEL_PERFIL_GESTOR_VISUALIZAR);
        form.add(dashboardPanelGestor);

        dashboardPanelFornecedor = new DashboardPanelFornecedor("dashboardPanelFornecedor");
        authorize(dashboardPanelFornecedor, RENDER, HomePage.ROLE_PANEL_PERFIL_FORNECEDOR_VISUALIZAR);
        form.add(dashboardPanelFornecedor);
    }

    // AÇÕES

    public Form<HomePage> getForm() {
        return form;
    }

    public void setForm(Form<HomePage> form) {
        this.form = form;
    }
}
