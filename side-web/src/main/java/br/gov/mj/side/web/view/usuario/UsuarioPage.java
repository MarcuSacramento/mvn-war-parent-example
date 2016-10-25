package br.gov.mj.side.web.view.usuario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraCheckBoxMultipleChoice;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.seg.entidades.Perfil;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.seg.entidades.UsuarioPerfil;
import br.gov.mj.seg.entidades.enums.EnumTipoUsuario;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.web.service.SegurancaService;
import br.gov.mj.side.web.service.UsuarioService;
import br.gov.mj.side.web.util.MascaraUtils;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.template.TemplatePage;

/**
 * Esta classe disponibiliza a visão da página UsuarioPage. Os componentes da
 * página e as ações de buscar e salvar Usuários, são também disponibilizadas
 * por ela.
 * 
 * @author Thiago Pereira
 * @since 2015
 * */
public class UsuarioPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    private List<Perfil> perfisSelecionados;
    private Form<Usuario> form;

    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private SegurancaService segurancaService;
    @Inject
    private UsuarioService usuarioService;

    public UsuarioPage(PageParameters pageParameters, Usuario usuario) {
        super(pageParameters);
        this.perfisSelecionados = new ArrayList<Perfil>();
        setTitulo("Vincular Usuários");
        getPerfisUsuario(usuario);
        initComponents(usuario);
    }

    /**
     * Este método inicializa os componentes na tela. Foi dividido para deixar o
     * construtor mais limpo.
     * 
     * @param usuario
     * */
    private void initComponents(Usuario usuario) {
        this.form = this.componentFactory.newForm("form", usuario);

        this.form.add(this.componentFactory.newLink("lnkDashboard", HomePage.class));
        this.form.add(this.componentFactory.newLink("usuarioPesquisaPage", UsuarioPesquisaPage.class));
        this.form.add(this.componentFactory.newLabel("primeiroNome", usuario.getPrimeiroNome()));
        this.form.add(this.componentFactory.newLabel("numeroCpf", MascaraUtils.formatarMascaraCpfCnpj(usuario.getNumeroCpf())));
        this.form.add(this.componentFactory.newLabel("usuarioCadastro", usuario.getUsuarioCadastro()));
        this.form.add(this.componentFactory.newLabel("email", usuario.getEmail()));
        this.form.add(newDropDownSituacao());
        this.form.add(this.componentFactory.newButton("salvar", () -> salvar(usuario)));
        this.form.add(botaoVoltar());
        this.form.add(newCheckBoxMultipleChoice(usuario));

        add(this.form);
    }

    /**
     * Este método vincula um ou mais perfis com o usuário, e persiste no banco
     * 
     * @param usuario
     * */
    private void salvar(Usuario usuario) {
        Set<UsuarioPerfil> usuarioPerfis = new HashSet<UsuarioPerfil>();

        for (int i = 0; i < this.perfisSelecionados.size(); i++) {
            UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
            usuarioPerfil.setUsuario(usuario);
            usuarioPerfil.setPerfil(this.perfisSelecionados.get(i));
            usuarioPerfis.add(usuarioPerfil);
        }

        usuario.setPerfis(usuarioPerfis);
        this.usuarioService.vincularPerfisUsuario(usuario, getIdentificador());
        getSession().info("Dados salvos com sucesso!");

        setResponsePage(new UsuarioPage(getPageParameters(), usuario));
    }

    /**
     * Este método recupera todos os perfis do banco, filtrando por tipo de
     * usuário.
     * 
     * @param usuario
     **/
    private List<Perfil> getPerfis(Usuario usuario) {
        usuario.getTipoUsuario();
        List<Perfil> perfis = this.segurancaService.buscarTodosPerfis(EnumTipoUsuario.INTERNO);
        return perfis;
    }

    /**
     * Este método recupera todos os perfis do usuário. É utilizado para compor
     * o model do checkbox
     **/
    private void getPerfisUsuario(Usuario usuario) {
        List<Perfil> perfis = this.usuarioService.buscarPerfisDoUsuario(usuario);
        this.perfisSelecionados = perfis;
    }

    /**
     * Este método cria um componente de checkbox com vários valores de perfis
     * recuperados do banco.
     * 
     * @param usuario
     * */
    private InfraCheckBoxMultipleChoice<Perfil> newCheckBoxMultipleChoice(Usuario usuario) {
        InfraCheckBoxMultipleChoice<Perfil> checkBox = this.componentFactory.newCheckBoxMultipleChoice("nomePerfil", "Perfis", false, false, "id", "nomePerfil", this.perfisSelecionados, getPerfis(usuario));
        return checkBox;
    }

    /**
     * Este método redireciona para página UsuarioPesquisa, passando um
     * parâmetro para identificação de que foi uma ação de voltar.
     **/
    private void voltar() {
        PageParameters pageParameters = new PageParameters();
        pageParameters.add("voltar", "voltar");
        setResponsePage(UsuarioPesquisaPage.class, pageParameters);
    }

    /**
     * Este método cria um componente de DropDown (SELECT) com as informações de
     * status do usuário.
     * 
     * @return dropDownChoice
     **/
    private InfraDropDownChoice<EnumStatusPessoa> newDropDownSituacao() {
        InfraDropDownChoice<EnumStatusPessoa> dropDownChoice = this.componentFactory.newDropDownChoice("situacaoUsuario", "Situação", false, "", "descricao", null, Arrays.asList(EnumStatusPessoa.values()), null);
        return dropDownChoice;
    }

    /**
     * Este método cria um componente de botão.
     * 
     * @return button
     **/
    private Button botaoVoltar() {
        Button button = this.componentFactory.newButton("voltar", () -> voltar());
        button.setDefaultFormProcessing(false);
        return button;
    }

}
