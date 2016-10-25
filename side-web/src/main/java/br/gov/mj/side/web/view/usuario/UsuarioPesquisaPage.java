package br.gov.mj.side.web.view.usuario;

import java.util.Arrays;
import java.util.Iterator;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.negocio.exception.SecurityException;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.web.service.UsuarioService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.MascaraUtils;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.template.TemplatePage;

/**
 * Esta classe disponibiliza o front da página UsuarioPesquisaPage.
 * 
 * @author Thiago.Pereira
 * @since 2015
 */

@AuthorizeInstantiation({ UsuarioPesquisaPage.ROLE_MANTER_USUARIO_INTERNO_VINCULAR, UsuarioPesquisaPage.ROLE_MANTER_USUARIO_INTERNO_VISUALIZAR })
public class UsuarioPesquisaPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    public static final String ROLE_MANTER_USUARIO_INTERNO_VISUALIZAR = "manter_usuario_interno:pesquisar";
    public static final String ROLE_MANTER_USUARIO_INTERNO_VINCULAR = "manter_usuario_interno:alterar";

    private Integer itensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;
    private static final String ONCHANGE = "onchange";

    private Usuario usuario;
    private Form<Usuario> form;
    private WebMarkupContainer webMarkupContainer;

    private DataView<Usuario> dataViewUsuario;

    @Inject
    private UsuarioService usuarioService;
    @Inject
    private ComponentFactory componentFactory;

    public UsuarioPesquisaPage(PageParameters pageParameters) {
        super(pageParameters);
        setTitulo("Permissões de Acesso a Usuários");
        initComponents();
        validaComponenteDIV(pageParameters);
    }

    /**
     * Método que inicializa os componentes na tela
     */
    private void initComponents() {

        this.usuario = new Usuario();

        this.form = this.componentFactory.newForm("form", this.usuario);
        this.form.add(this.componentFactory.newLink("lnkDashboard", HomePage.class));
        this.form.add(this.componentFactory.newTextField("nomeCompleto", "Nome", false, null, StringValidator.maximumLength(200)));
        this.form.add(this.componentFactory.newTextField("numeroCpf", "CPF", false, null, StringValidator.maximumLength(14)));
        this.form.add(this.componentFactory.newTextField("login", "Usuário de Acesso", false, null));
        this.form.add(this.componentFactory.newTextField("email", "Email", false, null));
        this.form.add(newDropDownSituacao());
        this.form.add(newDropDownPrimeiroAcesso());
        this.form.add(newButtonPesquisar());
        this.form.add(this.componentFactory.newButton("limpar", () -> limpar()));
        this.form.add(criaComponenteDIV().setVisible(false));

        add(this.form);
    }

    /**
     * Este método modifica o componente de GRID para que ele fique visível na
     * tela.
     **/

    private void pesquisar() {
        if (!getSideSession().hasRole(ROLE_MANTER_USUARIO_INTERNO_VISUALIZAR)) {
            throw new SecurityException();
        }

        this.usuario = this.form.getModelObject();
        if (this.usuario.getNumeroCpf() != null) {
            this.usuario.setNumeroCpf(this.usuario.getNumeroCpf().replaceAll("[-.]", ""));
        }
        this.form.addOrReplace(criaComponenteDIV().setVisible(true));
    }

    public AjaxSubmitLink newButtonPesquisar() {
        AjaxSubmitLink button = new AjaxSubmitLink("pesquisar", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                pesquisar();

                target.add(form.addOrReplace(criaComponenteDIV().setVisible(true)));
                target.appendJavaScript("atualizaCssDropDown();");
                target.appendJavaScript("ancorarResultadoPesquisa('#ancoraPesquisa');");
            }
        };
        authorize(button, RENDER, UsuarioPesquisaPage.ROLE_MANTER_USUARIO_INTERNO_VISUALIZAR);
        return button;
    }

    /**
     * Este método limpa os valores preenchidos no textField e esconde o
     * componente de GRID.
     **/
    private void limpar() {
        this.form.setModelObject(new Usuario());
        this.form.clearInput();
        this.form.addOrReplace(criaComponenteDIV().setVisible(false));
    }

    /**
     * Este método recupera o item selecionado no grid e redireciona para página
     * que vincula o perfil com usuário
     * 
     * @param item
     **/
    private void alterar(Item<Usuario> item) {
        if (!getSideSession().hasRole(ROLE_MANTER_USUARIO_INTERNO_VINCULAR)) {
            throw new SecurityException();
        }
        Usuario usuario = item.getModelObject();
        setResponsePage(new UsuarioPage(getPageParameters(), usuario));
    }

    /**
     * Este método cria um componente de DropDown (SELECT) com as informações de
     * status do usuário.
     * 
     * @return dropDownChoice
     **/
    private InfraDropDownChoice<EnumStatusPessoa> newDropDownSituacao() {
        InfraDropDownChoice<EnumStatusPessoa> dropDownChoice = this.componentFactory.newDropDownChoice("situacaoUsuario", "", false, "", "descricao", null, Arrays.asList(EnumStatusPessoa.values()), null);
        dropDownChoice.setNullValid(true);
        return dropDownChoice;
    }

    /**
     * Este método cria um componente de DropDown (SELECT) com as informações de
     * acesso do usuário.
     * 
     * @return dropDownChoice
     **/
    private InfraDropDownChoice<Boolean> newDropDownPrimeiroAcesso() {
        InfraDropDownChoice<Boolean> dropDownChoice = this.componentFactory.newDropDownChoice("possuiPrimeiroAcesso", "", false, "", "", null, Arrays.asList(Boolean.TRUE, Boolean.FALSE), null);
        dropDownChoice.setNullValid(true);
        // Classe Anônima
        dropDownChoice.setChoiceRenderer(new IChoiceRenderer<Boolean>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Object getDisplayValue(Boolean object) {
                if (object) {
                    return "Sim";
                }
                return "Não";
            }

            @Override
            public String getIdValue(Boolean object, int index) {
                return object.toString();
            }
        });
        // Fim classe anônima
        return dropDownChoice;
    }

    /**
     * Classe interna, utilizada para criação do componente de DataView. Ela
     * quem busca os registros.
     **/
    private class UsuarioDataProvider implements IDataProvider<Usuario> {
        private static final long serialVersionUID = 1L;

        @Override
        public void detach() {
        }

        @Override
        // busca os registros
        public Iterator<? extends Usuario> iterator(long first, long count) {
            return usuarioService.buscarUsuariosInternosPaginado(usuario, (int) first, (int) count).iterator();
        }

        @Override
        // Conta a quantidade de resgistros
        public long size() {
            return usuarioService.contarUsuariosInternosPaginado(usuario);
        }

        @Override
        // Model do dataView
        public IModel<Usuario> model(Usuario object) {
            return new CompoundPropertyModel<Usuario>(object);
        }

    }

    /**
     * Este método apresenta as informações no GRID.
     * 
     * @return dataView
     */
    private DataView<Usuario> newDataViewUsuario() {
        dataViewUsuario = new DataView<Usuario>("usuarios", new UsuarioDataProvider()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<Usuario> item) {
                item.add(new Label("nomeCompleto"));
                item.add(new Label("numeroCpf", MascaraUtils.formatarMascaraCpfCnpj(item.getModelObject().getNumeroCpf())));
                item.add(new Label("email"));
                item.add(new Label("situacaoUsuario.descricao"));
                item.add(botaoAlterar(item));
            }
        };
        return dataViewUsuario;
    }

    private DropDownChoice<Integer> getDropItensPorPagina() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPagina", new LambdaModel<Integer>(this::getItensPorPagina, this::setItensPorPagina), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewUsuario.setItemsPerPage(itensPorPagina);
                webMarkupContainer.addOrReplace(dataViewUsuario);
                target.add(webMarkupContainer);
            };
        });
        return dropDownChoice;
    }

    /**
     * Este método cria um componente do botão alterar.
     * 
     * @return botaoAlterar
     **/
    private Button botaoAlterar(Item<Usuario> item) {
        Button botaoAlterar = this.componentFactory.newButton("alterar", () -> alterar(item));
        return botaoAlterar;
    }

    /**
     * Este método é utilizado para tornar a DIV (class="panel panel-default")
     * em um componente wicket
     * 
     * @return webMarkupContainer
     */
    private WebMarkupContainer criaComponenteDIV() {
        this.webMarkupContainer = new WebMarkupContainer("grid");
        this.webMarkupContainer.add(getDropItensPorPagina());
        this.webMarkupContainer.add(newDataViewUsuario());
        this.webMarkupContainer.add(new InfraAjaxPagingNavigator("paginacao", newDataViewUsuario()));
        return this.webMarkupContainer;
    }

    /**
     * Este método valida o botão voltar da página UsuarioPage, ou seja,
     * recupera o parâmetro passado em UsuarioPage e verifica se apresenta o
     * GRID da lista de usuários.
     **/
    private void validaComponenteDIV(final PageParameters pageParameters) {
        if (!pageParameters.get("voltar").isEmpty()) {
            pesquisar();
        }
    }

    public Integer getItensPorPagina() {
        return itensPorPagina;
    }

    public void setItensPorPagina(Integer itensPorPagina) {
        this.itensPorPagina = itensPorPagina;
    }
}
