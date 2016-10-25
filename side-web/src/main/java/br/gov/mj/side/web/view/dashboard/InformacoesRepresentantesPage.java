package br.gov.mj.side.web.view.dashboard;

import javax.inject.Inject;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.web.service.BeneficiarioService;
import br.gov.mj.side.web.view.DashboardPanel;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.dashboard.paineis.PanelDadosEntidade;
import br.gov.mj.side.web.view.dashboard.paineis.PanelDadosPessoa;
import br.gov.mj.side.web.view.programa.inscricao.locaisEntrega.EntidadeNavPanel;
import br.gov.mj.side.web.view.template.TemplatePage;

public class InformacoesRepresentantesPage extends TemplatePage {

    private static final long serialVersionUID = 1L;
    // ###################################################################################
    // constantes

    // ###################################################################################
    // variaveis
    private Entidade entidade;

    // elementos do Wicket
    private Form<Entidade> form;
    private PanelDadosEntidade panelDadosEntidade;
    private PanelDadosPessoa panelDadosPessoa;
    private DashboardPanel dashboardPessoasPanel;

    // ###################################################################################
    // injeçãod e dependencia
    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private BeneficiarioService beneficiarioService;

    // ###################################################################################
    // conscructs, inits & destroiers
    public InformacoesRepresentantesPage(PageParameters pageParameters) {
        super(pageParameters);
        setTitulo("Informações Entidade");
        PessoaEntidade pessoaEntidade = (PessoaEntidade) getSessionAttribute("pessoaEntidade");

        if (pessoaEntidade != null) {
            entidade = pessoaEntidade.getEntidade();
            entidade.setLocaisEntregaEntidade(beneficiarioService.buscarLocaisEntrega(entidade, null));
            entidade.setPessoas(beneficiarioService.buscarRepresentanteEntidade(entidade, false));
        }
        form = componentFactory.newForm("form", new CompoundPropertyModel<Entidade>(entidade));
        initPage();
    }

    public void initPage() {

        dashboardPessoasPanel = new DashboardPanel("dashboardPessoasPanel");
        authorize(dashboardPessoasPanel, RENDER, HomePage.ROLE_MANTER_INSCRICAO_VISUALIZAR);
        form.add(dashboardPessoasPanel);

        form.add(new EntidadeNavPanel("navPanel", this));
        form.add(newPanelDadosPessoa());

        add(form);
    }

    // ###################################################################################
    // classes privadas

    // ###################################################################################
    // criação e configuração de componentes

    private PanelDadosPessoa newPanelDadosPessoa() {
        panelDadosPessoa = new PanelDadosPessoa("panelDadosTitular", form, entidade.getPessoas(), "Representantes", EnumTipoPessoa.REPRESENTANTE_ENTIDADE);
        panelDadosPessoa.enableHideMainPanelWhenEmpty();
        panelDadosPessoa.enableReadOnly();

        return panelDadosPessoa;
    }
}
