package br.gov.mj.side.web.view;

import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.seg.entidades.UsuarioPerfil;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumPerfilUsuario;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.util.SideSession;
import br.gov.mj.side.web.view.beneficiario.cadastroExterno.BeneficiarioExternoPage;
import br.gov.mj.side.web.view.seguranca.RecuperarSenhaPage;
import br.gov.mj.side.web.view.template.TemplateExternalPage;

public class LoginPage extends TemplateExternalPage {
    private static final long serialVersionUID = 1L;

    private String login;
    private String senha;
    private int tamanhoTela;

    @Inject
    private GenericEntidadeService genericEntidadeService;

    @Inject
    private ComponentFactory factory;

    public LoginPage(final PageParameters parameters) {
        super(parameters);

        StatelessForm<LoginPage> form = new StatelessForm<LoginPage>("form") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                boolean result = AuthenticatedWebSession.get().signIn(login, senha);
                if (result) {
                    adicionarEntidadeRepresentanteNaSessao();
                    initOrigemRequisicao();
                    /*
                     * Se o usuário não tiver tentando acessar uma pagina
                     * anteriormente, simplesmente manda-lo para tela Home
                     */
                    Url url = RestartResponseAtInterceptPageException.getOriginalUrl();

                    /*
                     * Nova validação para perfil gestor, caso o mesmo entre na
                     * "Pesquisa Aberta" e após efetuar login seja redirecionada
                     * para "Home.classs" e não outro caminho.
                     */
                    url = retornaUrlPorPerfil(url);

                    if (url == null) {
                        setResponsePage(HomePage.class); // manda pra home
                    } else {
                        // envia o usuário pra onde ele tava tentando ir
                        // originalmente
                        continueToOriginalDestination();
                    }
                } else {
                    addMsgError("Usuário e/ou senha inválido(s).");
                }
            }
        };

        form.setDefaultModel(new CompoundPropertyModel<LoginPage>(this));
        form.add(factory.newTextField("login", "Login", true, null, StringValidator.maximumLength(50)));

        PasswordTextField txSenha = new PasswordTextField("senha");
        txSenha.setLabel(new Model<String>("Senha"));
        txSenha.add(StringValidator.maximumLength(50));
        form.add(txSenha);

        SubmitLink entrarLink = new SubmitLink("entrarLink");
        form.add(entrarLink);
        form.setDefaultButton(entrarLink);

        form.add(newButtonCadastro());

        BookmarkablePageLink<Void> linkAcesso = new BookmarkablePageLink<Void>("linkRecuperarSenha", RecuperarSenhaPage.class);
        form.add(linkAcesso);

        form.add(factory.newTextField("tamanhoTela", "Tamanho Tela", Boolean.FALSE, null));

        add(form);

    }

    protected boolean isMenuVisible() {
        return true;
    }

    private BookmarkablePageLink<Void> newButtonCadastro() {
        BookmarkablePageLink<Void> cadastro = new BookmarkablePageLink<Void>("btnCadastroExterno", BeneficiarioExternoPage.class);
        return cadastro;
    }

    private void adicionarEntidadeRepresentanteNaSessao() {

        if (getSideSession().getUsuarioLogado() != null) {
            List<PessoaEntidade> entidades = genericEntidadeService.buscarPessoaEntidadesDoUsuario(getSideSession().getUsuarioLogado());
            if (!entidades.isEmpty()) {
                Pessoa pessoa = entidades.get(0).getPessoa();
                if (pessoa.isRepresentante() || pessoa.isPreposto()) {
                    addSessionAttribute("entidade", entidades.get(0).getEntidade());
                }
            }

            PessoaEntidade pessoaEntidade = genericEntidadeService.buscarPessoaEntidadeDoUsuario(getSideSession().getUsuarioLogado());
            if (pessoaEntidade != null) {
                addSessionAttribute("pessoaEntidade", pessoaEntidade);
            }
        }

    }

    private void initOrigemRequisicao() {
        Boolean isMobile = tamanhoTela <= 800 ? Boolean.TRUE : Boolean.FALSE;
        addSessionAttribute("isMobile", isMobile);
    }

    protected SideSession getSideSession() {
        return SideSession.get();
    }

    private Url retornaUrlPorPerfil(Url url) {
        boolean manterUrl = false;
        boolean mostrarMensagemNaoPermissao = false;

        if (url != null) {
            if (url.getPath().contains("conferencia/item")) {
                // manter se for preposto ou representante
                if (usuarioLogadoPossuiPerfil(EnumPerfilUsuario.REPRESENTANTE) || usuarioLogadoPossuiPerfil(EnumPerfilUsuario.PREPOSTO)) {
                    manterUrl = true;
                } else {
                    mostrarMensagemNaoPermissao = true;
                }

            } else if (url.getPath().contains("inscricao")) {
                // manter se for representante

                if (usuarioLogadoPossuiPerfil(EnumPerfilUsuario.REPRESENTANTE)) {
                    manterUrl = true;
                } else {
                    mostrarMensagemNaoPermissao = true;
                }

            }

            if (!manterUrl) {

                // Será chamado somente se a pessoa logar tentando acessar
                // alguma
                // página que não tenha permissão
                if (mostrarMensagemNaoPermissao) {
                    getSession().error("Você não possui permissão para acessar a página requisitada.");
                }
                url = null;
            }
        }
        return url;

    }

    private boolean usuarioLogadoPossuiPerfil(EnumPerfilUsuario perfilUsuario) {
        for (UsuarioPerfil perfil : getSideSession().getUsuarioLogado().getPerfis()) {
            if (perfilUsuario.getDescricao().equals(perfil.getPerfil().getNomePerfil())) {
                return true;
            }
        }
        return false;
    }

}
