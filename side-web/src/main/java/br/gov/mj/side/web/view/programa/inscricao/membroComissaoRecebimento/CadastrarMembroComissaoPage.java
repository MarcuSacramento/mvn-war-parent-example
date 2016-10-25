package br.gov.mj.side.web.view.programa.inscricao.membroComissaoRecebimento;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.EntidadeAnexo;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.web.dto.EntidadeDto;
import br.gov.mj.side.web.dto.ListasPorTipoPessoaDto;
import br.gov.mj.side.web.service.AnexoEntidadeService;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.service.MailService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.DashboardPanel;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.beneficiario.paineis.PanelDadosMembroComissao;
import br.gov.mj.side.web.view.programa.inscricao.locaisEntrega.EntidadeNavPanel;
import br.gov.mj.side.web.view.template.TemplatePage;

@AuthorizeInstantiation({ CadastrarMembroComissaoPage.ROLE_MANTER_MEMBRO_COMISSAO_VISUALIZAR, CadastrarMembroComissaoPage.ROLE_MANTER_MEMBRO_COMISSAO_EXCLUIR, CadastrarMembroComissaoPage.ROLE_MANTER_MEMBRO_COMISSAO_EDITAR, CadastrarMembroComissaoPage.ROLE_MANTER_MEMBRO_COMISSAO_INCLUIR })
public class CadastrarMembroComissaoPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    public static final String ROLE_MANTER_MEMBRO_COMISSAO_VISUALIZAR = "manter_membro_comissao:visualizar";
    public static final String ROLE_MANTER_MEMBRO_COMISSAO_EXCLUIR = "manter_membro_comissao:excluir";
    public static final String ROLE_MANTER_MEMBRO_COMISSAO_EDITAR = "manter_membro_comissao:editar";
    public static final String ROLE_MANTER_MEMBRO_COMISSAO_INCLUIR = "manter_membro_comissao:incluir";

    private PanelAtualizarComissao panelAtualizarComissao;
    private PanelDadosMembroComissao panelDadosMembroComissao;
    private PanelBotoes panelBotoes;
    private DashboardPanel dashboardPessoasPanel;

    private Form<EntidadeDto> form;
    private Page backPage;
    private Button buttonSalvar;
    private Link link;

    private EntidadeDto entidade = new EntidadeDto();
    private Usuario usuarioLogado;
    private Entidade entidadePrincipal = new Entidade();
    private Boolean readOnly = false;

    private List<PessoaEntidade> listaDeRepresentantes = new ArrayList<PessoaEntidade>();
    private List<PessoaEntidade> listaDeTitulares = new ArrayList<PessoaEntidade>();
    private List<PessoaEntidade> listaDeMembrosComissao = new ArrayList<PessoaEntidade>();

    // Lista inicial sem qualquer alteração
    private List<PessoaEntidade> listaInicialDeMembros = new ArrayList<PessoaEntidade>();

    // Lista com os membros novos adicionados agora
    private List<PessoaEntidade> listaMembrosNovos = new ArrayList<PessoaEntidade>();

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private GenericEntidadeService genericEntidadeService;

    @Inject
    private MailService mailService;

    @Inject
    private AnexoEntidadeService anexoService;

    public CadastrarMembroComissaoPage(final PageParameters pageParameters) {
        super(pageParameters);

        PessoaEntidade pessoaEntidade = (PessoaEntidade) getSessionAttribute("pessoaEntidade");
        if (pessoaEntidade != null) {
            entidadePrincipal = pessoaEntidade.getEntidade();
            inicializarAsListasDePessoas();
        }
        initComponentes();

        setTitulo("Informações da Entidade");
    }

    private void initComponentes() {
        form = componentFactory.newForm("form", new CompoundPropertyModel<EntidadeDto>(entidade));
        entidade.setEntidade(entidadePrincipal);
        add(form);

        dashboardPessoasPanel = new DashboardPanel("dashboardPessoasPanel");
        authorize(dashboardPessoasPanel, RENDER, HomePage.ROLE_MANTER_INSCRICAO_VISUALIZAR);
        form.add(dashboardPessoasPanel);

        form.add(new EntidadeNavPanel("navPanel", this));

        panelAtualizarComissao = new PanelAtualizarComissao("panelAtualizarComissao");
        form.add(panelAtualizarComissao);

        form.add(panelBotoes = new PanelBotoes("panelBotoes"));

        add(form);
    }

    private class PanelAtualizarComissao extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelAtualizarComissao(String id) {
            super(id);
            setOutputMarkupId(true);
            add(panelDadosMembroComissao = new PanelDadosMembroComissao("panelComissao", form.getModelObject(), listaDeMembrosComissao, listaDeTitulares, listaDeRepresentantes, readOnly));
        }
    }

    private class PanelBotoes extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBotoes(String id) {
            super(id);
            setOutputMarkupId(true);
            add(getButtonSalvar()); // btnSalvar
        }
    }

    // COMPONENTES
    private Button getButtonSalvar() {
        buttonSalvar = new AjaxButton("btnSalvar") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                actionSalvarBeneficiario(target);
            }
        };
        buttonSalvar.setEnabled(!readOnly);
        return buttonSalvar;
    }

    // AÇÕES

    private void inicializarAsListasDePessoas() {
        ListasPorTipoPessoaDto listasPorTipoPessoaDto = genericEntidadeService.buscarPessoasPorTipo(entidadePrincipal);
        listaDeTitulares.addAll(listasPorTipoPessoaDto.getListaTitular());
        listaDeRepresentantes.addAll(listasPorTipoPessoaDto.getListaRepresentante());
        listaDeMembrosComissao.addAll(listasPorTipoPessoaDto.getListaMembroComissao());

        // Copia uma lista com os emails iniciais
        for (PessoaEntidade membro : listaDeMembrosComissao) {
            PessoaEntidade pe = new PessoaEntidade();
            Pessoa pessoa = new Pessoa();

            String email = membro.getPessoa().getEmail();
            Long id = membro.getPessoa().getId();

            pessoa.setEmail(email);
            pessoa.setId(id);
            pessoa.setNomePessoa(membro.getPessoa().getNomePessoa());
            pe.setPessoa(pessoa);
            pe.setId(membro.getId());

            listaInicialDeMembros.add(pe);
        }
    }

    private void verificarCadastrosNovos() {
        boolean cadastroNovo = true;
        listaMembrosNovos.clear();
        for (PessoaEntidade ent : listaDeMembrosComissao) {
            if (ent.getPessoa().getId() != null) {
                for (PessoaEntidade inicial : listaInicialDeMembros) {
                    if (ent.getPessoa().getId() == inicial.getPessoa().getId()) {
                        cadastroNovo = false;
                        break;
                    }
                }
            }

            if (cadastroNovo) {
                listaMembrosNovos.add(ent);
            }
            cadastroNovo = true;
        }
    }

    public void actionSalvarBeneficiario(AjaxRequestTarget target) {

        limparCadastrosNovos();
        verificarCadastrosNovos();
        String usuarioLogado = getIdentificador();
        entidadePrincipal.setUsuarioAlteracao(usuarioLogado);

        genericEntidadeService.incluirAlterarMembroComissao(entidadePrincipal, listaDeMembrosComissao, usuarioLogado);
        Entidade entidadePersistida = atualizarPessoasNovas(usuarioLogado);

        mailService.enviarEmailCadastroNovoMembroComissao(entidadePersistida, listaMembrosNovos, getUrlBase(Constants.PAGINA_ALTERACAO_SENHA));

        List<PessoaEntidade> listaEmailsAlterados = recuperarListaDeEmailsAlterados();
        if (listaEmailsAlterados.size() > 0) {
            mailService.enviarEmailAlteracaoEmail(entidadePersistida, listaEmailsAlterados);
        }
        getSession().info("Salvo com sucesso.");
        setResponsePage(new CadastrarMembroComissaoPage(new PageParameters()));
    }

    private Entidade atualizarPessoasNovas(String usuario) {

        // Seta os status das pessoas que foram alterados
        List<PessoaEntidade> pessoasRetorno = genericEntidadeService.buscarPessoa(entidadePrincipal);
        for (PessoaEntidade pe : pessoasRetorno) {
            boolean encontrado = false;

            for (PessoaEntidade titular : listaDeTitulares) {
                if (titular.getPessoa().getId() != null && titular.getPessoa().getId().intValue() == pe.getPessoa().getId().intValue()) {
                    pe.getPessoa().setStatusPessoa(titular.getPessoa().getStatusPessoa());
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                for (PessoaEntidade representante : listaDeRepresentantes) {
                    if (representante.getPessoa().getId() != null && representante.getPessoa().getId().intValue() == pe.getPessoa().getId().intValue()) {
                        pe.getPessoa().setStatusPessoa(representante.getPessoa().getStatusPessoa());
                        encontrado = true;
                        break;
                    }
                }
            }

            if (!encontrado) {
                for (PessoaEntidade membro : listaDeMembrosComissao) {
                    if (membro.getPessoa().getId() != null && membro.getPessoa().getId().intValue() == pe.getPessoa().getId().intValue()) {
                        pe.getPessoa().setStatusPessoa(membro.getPessoa().getStatusPessoa());
                        encontrado = true;
                        break;
                    }
                }
            }
        }

        // Persiste a entidade com as pessoas alteradas.
        List<EntidadeAnexo> listaAnexos = SideUtil.convertAnexoDtoToEntityEntidadeAnexo(anexoService.buscarPeloIdEntidade(entidadePrincipal.getId()));

        entidadePrincipal.setAnexos(listaAnexos);
        entidadePrincipal.setPessoas(pessoasRetorno);
        Entidade entidadePersistida = genericEntidadeService.incluirAlterar(entidadePrincipal, usuario, false);
        return entidadePersistida;
    }

    private void limparCadastrosNovos() {
        for (PessoaEntidade rep : listaDeMembrosComissao) {
            rep.getPessoa().setNumeroCpf(limparCampos(rep.getPessoa().getNumeroCpf()));
            rep.getPessoa().setNumeroTelefone(limparCampos(rep.getPessoa().getNumeroTelefone()));
        }
    }

    private List<PessoaEntidade> recuperarListaDeEmailsAlterados() {
        List<PessoaEntidade> listaEmailsAlterados = new ArrayList<PessoaEntidade>();
        for (PessoaEntidade pe : listaDeMembrosComissao) {
            String email = pe.getPessoa().getEmail();

            for (PessoaEntidade listaInicial : listaInicialDeMembros) {
                if (pe.getPessoa().getId().intValue() == listaInicial.getPessoa().getId().intValue()) {
                    String emailAlterado = listaInicial.getPessoa().getEmail();

                    if (!email.equalsIgnoreCase(emailAlterado)) {
                        listaEmailsAlterados.add(pe);
                        listaEmailsAlterados.add(listaInicial);
                    }
                    break;
                }
            }

        }
        return listaEmailsAlterados;
    }

    public List<PessoaEntidade> montarListaDeMembrosDeComissao() {
        List<PessoaEntidade> listaPessoa = new ArrayList<PessoaEntidade>();

        // Pega a lista de membros da comissão adicionados e limpa os campos
        for (PessoaEntidade rep : listaDeMembrosComissao) {

            rep.getPessoa().setNumeroCpf(limparCampos(rep.getPessoa().getNumeroCpf()));
            rep.getPessoa().setNumeroTelefone(limparCampos(rep.getPessoa().getNumeroTelefone()));
            listaPessoa.add(rep);
        }
        return listaPessoa;
    }

    private Long pegarMaiorIdPessoaAtual(List<PessoaEntidade> listaPessoasEntidade) {
        Long retorno = 0L;
        for (PessoaEntidade pessoaEntidade : listaPessoasEntidade) {
            if (pessoaEntidade.getPessoa().getId() != null) {
                if (pessoaEntidade.getPessoa().getId() > retorno) {
                    retorno = pessoaEntidade.getPessoa().getId();
                }
            }
        }
        return retorno;
    }

    public void actionSair() {
        setResponsePage(HomePage.class);
    }

    public Usuario receberUsuarioLogado() {
        return getUsuarioLogadoDaSessao();
    }

    private String limparCampos(String valor) {
        String value = valor;
        value = value.replace(".", "");
        value = value.replace("/", "");
        value = value.replace("-", "");
        value = value.replace("(", "");
        value = value.replace(")", "");
        return value;
    }

    public Form<EntidadeDto> getForm() {
        return form;
    }

    public void setForm(Form<EntidadeDto> form) {
        this.form = form;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }
}
