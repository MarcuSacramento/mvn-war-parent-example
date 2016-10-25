package br.gov.mj.side.web.view.beneficiario.cadastroExterno;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.web.view.LoginPage;
import br.gov.mj.side.web.view.template.TemplatePage;

public class MensagemCadastroBeneficiarioExternoPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    public MensagemCadastroBeneficiarioExternoPage(final PageParameters pageParameters) {
        super(pageParameters);
        setTitulo("");
        initComponents();
    }

    private void initComponents() {

        BookmarkablePageLink<Void> linkAcesso = new BookmarkablePageLink<Void>("linkAcesso", LoginPage.class);
        add(linkAcesso);

    }

    // Método sobrescrito para remover autenticação na página.
    @Override
    protected void onConfigure() {

    }

    @Override
    protected boolean isMenuVisible() {
        Usuario usuarioLogado = getUsuarioLogadoDaSessao();
        if (usuarioLogado != null) {
            return true;
        } else {
            return false;
        }
    }
}
