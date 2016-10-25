package br.gov.mj.side.web;

import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;

import br.gov.mj.infra.wicket.application.BaseWicketApplication;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.SideSession;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.LoginPage;
import br.gov.mj.side.web.view.beneficiario.cadastroExterno.BeneficiarioExternoPage;
import br.gov.mj.side.web.view.cadastraritem.ConferenciaCadastrarItemPage;
import br.gov.mj.side.web.view.consultaPublica.ConsultaPublicaPesquisaPage;
import br.gov.mj.side.web.view.fornecedor.FornecedorPage;
import br.gov.mj.side.web.view.fornecedor.FornecedorPesquisaPage;
import br.gov.mj.side.web.view.parametro.ParametroPesquisaPage;
import br.gov.mj.side.web.view.programa.inscricao.InscricaoProgramaPage;
import br.gov.mj.side.web.view.propostas.PropostaPage;
import br.gov.mj.side.web.view.seguranca.AlterarSenhaPage;
import br.gov.mj.side.web.view.seguranca.RecuperarSenhaPage;
import de.agilecoders.wicket.core.Bootstrap;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 * @see br.gov.mj.exemplo.Start#main(String[])
 */
public class SideApplication extends BaseWicketApplication {

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<? extends WebPage> getHomePage() {
        return HomePage.class;
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return SideSession.class;

    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class;
    }

    @Override
    public void init() {
        Bootstrap.install(get());
        super.init();
        mountPage("pesquisaaberta", ConsultaPublicaPesquisaPage.class);
        mountPage("login", LoginPage.class);
        mountPage(Constants.PAGINA_ALTERACAO_SENHA, AlterarSenhaPage.class);
        mountPage("recuperarsenha", RecuperarSenhaPage.class);
        mountPage("proposta", PropostaPage.class);
        mountPage("inscricao", InscricaoProgramaPage.class);
        mountPage("fornecedor", FornecedorPage.class);
        mountPage("parametro", ParametroPesquisaPage.class);
        mountPage("conferencia", ConferenciaCadastrarItemPage.class);
        mountPage("pesquisafornecedor", FornecedorPesquisaPage.class);
        mountPage(Constants.PAGINA_ALTERACAO_CADASTRO_RECUSADO, BeneficiarioExternoPage.class);
        mountPage("conferencia/item", ConferenciaCadastrarItemPage.class);
        getApplicationSettings().setPageExpiredErrorPage(LoginPage.class);

    }
}
