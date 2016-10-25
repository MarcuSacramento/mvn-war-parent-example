package br.gov.mj.side.web.view.dashboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.EntidadeAnexo;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumTipoArquivoEntidade;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.web.dto.EntidadeDto;
import br.gov.mj.side.web.dto.EntidadePesquisaDto;
import br.gov.mj.side.web.service.AnexoEntidadeService;
import br.gov.mj.side.web.service.BeneficiarioService;
import br.gov.mj.side.web.service.MailService;
import br.gov.mj.side.web.service.SegurancaService;
import br.gov.mj.side.web.util.CPFUtils;
import br.gov.mj.side.web.util.CnpjUtil;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.EmailValidator;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.DashboardPanel;
import br.gov.mj.side.web.view.components.AjaxDialogButton;
import br.gov.mj.side.web.view.dashboard.paineis.PanelAnexoDashboard;
import br.gov.mj.side.web.view.dashboard.paineis.PanelDadosEntidadeDashboard;
import br.gov.mj.side.web.view.dashboard.paineis.PanelDadosRepresentanteDashboard;
import br.gov.mj.side.web.view.dashboard.paineis.PanelDadosTitularDashboard;
import br.gov.mj.side.web.view.programa.inscricao.locaisEntrega.LocaisEntregaPage;
import br.gov.mj.side.web.view.template.TemplatePage;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;

public class DashboardInfoEntidadePage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    private DashboardPanel dashboardPanel;
    private PanelAtualizarRepresentante panelAtualizarRepresentante;
    private PanelDadosRepresentanteDashboard panelDadosRepresentante;
    private PanelDadosEntidadeDashboard panelDadosEntidade;
    private PanelDadosTitularDashboard panelDadosTitular;
    private PanelBotoes panelBotoes;
    private PanelAnexoDashboard panelAnexo;
    private Modal<String> modalConfirmUf;
    private Modal<String> modalLimparFormulario;

    private Form<EntidadeDto> form;
    private Page backPage;
    private Button buttonSalvar;
    private AjaxSubmitLink buttonCancelarEdicao;
    private Link link;

    private EntidadeDto entidade = new EntidadeDto();
    private Entidade entidadePrincipal = new Entidade();
    private List<EntidadeAnexo> listaAnexos = new ArrayList<EntidadeAnexo>();
    private Boolean readOnly;
    private Boolean existeEntidade = false;
    private Boolean mostrarBotaoVisualizarCnpj = true;
    private Boolean mostrarBotaoCancelarEdicao = false;
    private AjaxCheckBox checkRepresentante;
    private Label labelMensagem;
    private Model<String> mensagem = Model.of("");
    
    private List<Entidade> listaEntidadesUsuario = new ArrayList<Entidade>();

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private BeneficiarioService beneficiarioService;

    @Inject
    private MailService mailService;

    @Inject
    private AnexoEntidadeService anexoService;

    @Inject
    private SegurancaService segurancaService;

    public DashboardInfoEntidadePage(final PageParameters pageParameters, Boolean readOnly) {
        super(pageParameters);

        this.readOnly = readOnly;
        initiVariaveis();
        initComponentes();

        setTitulo(entidadePrincipal.getNomeEntidade());

        panelDadosEntidade.setEnabled(false);
        //Quando puder editar retirar os comentários abaixo
        
        //panelDadosTitular.setEnabled(acionarCamposDeInput);
        //panelDadosRepresentante.setEnabled(acionarCamposDeInput);
        //panelAnexo.setEnabled(acionarCamposDeInput);
    }

   private void initiVariaveis() {
       
       entidadePrincipal= (Entidade) getSessionAttribute("entidade");
       
        if (entidadePrincipal != null && entidadePrincipal.getId() != null) {
            
            List<PessoaEntidade> representante = new ArrayList<PessoaEntidade>();
            List<PessoaEntidade> titular = new ArrayList<PessoaEntidade>();

            titular = beneficiarioService.buscarTitularEntidade(entidadePrincipal);
            if (titular.get(0).getPessoa().getPossuiFuncaoDeRepresentante()) {
                representante = titular;
            } else {
                representante = beneficiarioService.buscarRepresentanteEntidade(entidadePrincipal,false);
            }

            listaAnexos = SideUtil.convertAnexoDtoToEntityEntidadeAnexo(anexoService.buscarPeloIdEntidade(entidadePrincipal.getId()));
            entidade.setEntidadeTitular(titular.get(0).getPessoa());
            entidade.setEntidadeRepresentante(representante.get(0).getPessoa());

            entidade.setId(entidadePrincipal.getId());
            entidade.setBairro(entidadePrincipal.getBairro());
            entidade.setComplementoEndereco(entidadePrincipal.getComplementoEndereco());
            entidade.setDataAlteracao(entidadePrincipal.getDataAlteracao());
            entidade.setDataCadastro(entidadePrincipal.getDataCadastro());
            entidade.setDescricaoEndereco(entidadePrincipal.getDescricaoEndereco());
            entidade.setEmail(entidadePrincipal.getEmail());
            entidade.setMunicipio(entidadePrincipal.getMunicipio());
            entidade.setNomeEntidade(entidadePrincipal.getNomeEntidade());
            entidade.setNumeroCep(entidadePrincipal.getNumeroCep());
            entidade.setNumeroCnpj(entidadePrincipal.getNumeroCnpj());
            entidade.setNumeroEndereco(entidadePrincipal.getNumeroEndereco());
            entidade.setNumeroTelefone(entidadePrincipal.getNumeroTelefone());
            entidade.setNumeroFoneFax(entidadePrincipal.getNumeroFoneFax());
            entidade.setNumeroProcessoSEI(entidadePrincipal.getNumeroProcessoSEI());
            entidade.setPersonalidadeJuridica(entidadePrincipal.getPersonalidadeJuridica());
            entidade.setStatusEntidade(entidadePrincipal.getStatusEntidade());
            entidade.setTipoEndereco(entidadePrincipal.getTipoEndereco());
            entidade.setTipoEntidade(entidadePrincipal.getTipoEntidade());
            entidade.setUsuarioAlteracao(entidadePrincipal.getUsuarioAlteracao());
            entidade.setUsuarioCadastro(entidadePrincipal.getUsuarioCadastro());
        }
    }

    private void initComponentes() {
        form = componentFactory.newForm("form", new CompoundPropertyModel<EntidadeDto>(entidade));
        add(form);

        labelMensagem = new Label("mensagemAnexo", mensagem);
        labelMensagem.setEscapeModelStrings(false);
        form.add(labelMensagem);
        
        form.add(new DashboardPanel("dashboardPessoasPanel"));
        

        panelDadosRepresentante = new PanelDadosRepresentanteDashboard("panelDadosRepresentante", this);
        
        form.add(new BookmarkablePageLink<Void>("linkLocalEntregaPage",LocaisEntregaPage.class));
        form.add(new BookmarkablePageLink<Void>("linkInformacoesEntidade",InformacoesEntidadePage.class));
        form.add(new BookmarkablePageLink<Void>("linkInformacoesRepresentantes",InformacoesRepresentantesPage.class));
        form.add(panelAtualizarRepresentante = new PanelAtualizarRepresentante("panelAtualizarRepresentante"));
        form.add(panelDadosEntidade = new PanelDadosEntidadeDashboard("panelDadosEntidade", entidadePrincipal));
        form.add(panelDadosTitular = new PanelDadosTitularDashboard("panelDadosTitular", this));
        form.add(panelBotoes = new PanelBotoes("panelBotoes"));
        form.add(panelAnexo = new PanelAnexoDashboard("panelAnexo", this, listaAnexos));
        add(form);
    }

    private class PanelAtualizarRepresentante extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelAtualizarRepresentante(String id) {
            super(id);
            setOutputMarkupId(true);

            add(panelDadosRepresentante);
        }
    }

    private class PanelBotoes extends WebMarkupContainer {
        public PanelBotoes(String id) {
            super(id);
            setOutputMarkupId(true);
            add(getButtonSalvar()); // btnSalvar
        }
    }

    private Button getButtonSalvar() {
        buttonSalvar = new AjaxButton("btnSalvar") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                actionSalvarBeneficiario(target);
            }
        };
        buttonSalvar.setVisible(false); //Quando puder editar tornar o Enabled true
        return buttonSalvar;
    }
    // AÇÕES

    private AjaxDialogButton newButtonFecharModal(Modal<String> modal) {
        return new AjaxDialogButton(Model.of("Fechar"), Buttons.Type.Primary) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                modal.show(false);
                modal.close(target);
            }
        };
    }

    private AjaxDialogButton newButtonLimparFormulario(Modal<String> modal) {
        return new AjaxDialogButton(Model.of("OK Limpar"), Buttons.Type.Primary) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(new DashboardInfoEntidadePage(null, false));
            }
        };
    }

    private void actionTextFieldCnpj(TextField<String> field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                // setar no model
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    public void actionSalvarBeneficiario(AjaxRequestTarget target) {

        List<PessoaEntidade> listaPessoa = montarListaTitularesERepresententesBeneficiario();
        EntidadeDto entidadeDto = form.getModelObject();

        if (!validarCamposEntidade(entidadeDto, listaPessoa, target)) {
            buttonCancelarEdicao.setMarkupId("btnCancelarInclusaoEdicao");
            target.focusComponent(link);
            return;
        }
        montarEntidadeParaSalvar(entidadeDto, listaPessoa);

        String usuarioLogado = getIdentificador();
        entidadePrincipal.setUsuarioAlteracao(usuarioLogado);

        Long maiorIdPessoaAtual = pegarMaiorIdPessoaAtual(entidadePrincipal.getPessoas());       
        Entidade entidadePersistida = beneficiarioService.incluirAlterar(entidadePrincipal, usuarioLogado);
        mailService.enviarEmailCadastroNovaEntidadePorUsuarioInterno(entidadePersistida,maiorIdPessoaAtual, getUrlBase(Constants.PAGINA_ALTERACAO_SENHA));

        
        getSession().info("Alterado com sucesso");
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
    
    public void montarEntidadeParaSalvar(EntidadeDto entidadeDto, List<PessoaEntidade> listaPessoa) {
        // adiciona a listaPessoa já com os titulares e representantes
        entidadePrincipal.setPessoas(listaPessoa);

        entidadePrincipal.setTipoEntidade(entidadeDto.getTipoEntidade());
        entidadePrincipal.setNomeEntidade(entidadeDto.getNomeEntidade());
        entidadePrincipal.setPersonalidadeJuridica(entidadeDto.getPersonalidadeJuridica());
        entidadePrincipal.setMunicipio(entidadeDto.getMunicipio());
        entidadePrincipal.setTipoEndereco(entidadeDto.getTipoEndereco());
        entidadePrincipal.setDescricaoEndereco(entidadeDto.getDescricaoEndereco());
        entidadePrincipal.setNumeroEndereco(entidadeDto.getNumeroEndereco());
        entidadePrincipal.setComplementoEndereco(entidadeDto.getComplementoEndereco());
        entidadePrincipal.setBairro(entidadeDto.getBairro());
        entidadePrincipal.setNumeroCep(limparCampos(entidadeDto.getNumeroCep()));
        entidadePrincipal.setNumeroTelefone(limparCampos(entidadeDto.getNumeroTelefone()));
        entidadePrincipal.setStatusEntidade(entidadeDto.getStatusEntidade());
        entidadePrincipal.setNumeroProcessoSEI(limparCampos(entidadeDto.getNumeroProcessoSEI()));

        if (entidadeDto.getNumeroFoneFax() != null) {
            entidadePrincipal.setNumeroFoneFax(limparCampos(entidadeDto.getNumeroFoneFax()));
        } else {
            entidadePrincipal.setNumeroFoneFax(null);
        }

        entidadePrincipal.setEmail(entidadeDto.getEmail());
        entidadePrincipal.setAnexos(panelAnexo.getListAnexoTemp());
    }

    public void actionSair() {
        setResponsePage(backPage);
    }

    public boolean validarCamposEntidade(EntidadeDto entityTemp, List<PessoaEntidade> listaPessoa, AjaxRequestTarget target) {
        boolean validar = true;
        String msg = "";

        if (!validarAnexos()) {
            addMsgError("É necessário anexar ao menos 1 arquivo de cada tipo informado no DropDown 'Tipo do Arquivo.");
            validar = false;
        }

        if (listaPessoa.size() == 0) {
            addMsgError("É necessário adicionar ao menos 1 Titular e 1 Representante ou  1 Titular com função de Representante.");
            validar = false;
        } else {
            boolean possuiRepresentante = false;
            for (PessoaEntidade ent : listaPessoa) {
                if (ent.getPessoa().getPossuiFuncaoDeRepresentante()) {
                    possuiRepresentante = true;
                    break;
                }
            }

            if (!possuiRepresentante) {
                addMsgError("Adicione alguém com função de representante.");
                validar = false;
            }
        }

        if (entityTemp.getTipoEntidade() == null) {
            addMsgError("O campo 'Tipo' é obrigatório.");
            validar = false;
        }

        if (entityTemp.getNomeEntidade() == null || entityTemp.getNomeEntidade().equalsIgnoreCase("")) {
            addMsgError("O campo 'Nome' é obrigatório.");
            validar = false;
        }

        if (entityTemp.getPersonalidadeJuridica() == null) {
            addMsgError("O campo 'Natureza Jurídica' é obrigatório.");
            validar = false;
        }

        if (panelDadosEntidade.getNomeUf() == null || panelDadosEntidade.getNomeUf().getId() == null) {
            addMsgError("O campo 'Estado' é obrigatório.");
            validar = false;
        }
        if (entityTemp.getMunicipio() == null) {
            addMsgError("O campo 'Município' é obrigatório.");
            validar = false;
        }

        if (entityTemp.getTipoEndereco() == null) {
            addMsgError("O campo 'Tipo de Endereço' é obrigatório.");
            validar = false;
        }

        if (entityTemp.getDescricaoEndereco() == null || entityTemp.getDescricaoEndereco().equalsIgnoreCase("")) {
            addMsgError("O campo 'Endereço' é obrigatório.");
            validar = false;
        }

        if (entityTemp.getNumeroEndereco() == null || entityTemp.getNumeroEndereco().equalsIgnoreCase("")) {
            addMsgError("O campo 'Número' é obrigatório.");
            validar = false;
        }

        if (entityTemp.getBairro() == null || entityTemp.getBairro().equalsIgnoreCase("")) {
            addMsgError("O campo 'Bairro' é obrigatório.");
            validar = false;
        }

        if (entityTemp.getNumeroCep() == null || entityTemp.getNumeroCep().equalsIgnoreCase("")) {
            addMsgError("O campo 'CEP' é obrigatório.");
            validar = false;
        } else {
            if (limparCampos(entityTemp.getNumeroCep()).length() < 8) {
                addMsgError("O CEP deverá contar 8 caracteres númericos.");
                validar = false;
            }
        }

        if (entityTemp.getNumeroTelefone() == null || entityTemp.getNumeroTelefone().equalsIgnoreCase("")) {
            addMsgError("O campo 'Telefone' é obrigatório.");
            validar = false;
        } else {
            if (entityTemp.getNumeroTelefone().length() < 10) {
                addMsgError("O Telefone da Entidade deverá conter ao menos 10 números.");
                validar = false;
            }
        }

        if (entityTemp.getNumeroFoneFax() != null && limparCampos(entityTemp.getNumeroFoneFax()).length() < 10) {
            addMsgError("O Telefone/FAX da Entidade deverá conter ao menos 10 números");
            validar = false;
        }

        if (entityTemp.getEmail() == null || entityTemp.getEmail().equalsIgnoreCase("")) {
            addMsgError(" O campo 'E-Mail' é obrigatório.");
            validar = false;
        } else {
            validar = validarEmailUnico(validar, entityTemp.getEmail());
        }

        if (entityTemp.getNumeroProcessoSEI() == null || "".equalsIgnoreCase(entityTemp.getNumeroProcessoSEI())) {
            addMsgError("O campo 'Número do Processo (NUP)' é obrigatório.");
            validar = false;
        } else {
            if (entityTemp.getNumeroProcessoSEI().length() < 17) {
                addMsgError("O campo 'Nº Processo (NUP)' deverá conter 17 caracteres númericos.");
                validar = false;
            } else {
                EntidadePesquisaDto dto = new EntidadePesquisaDto();
                Entidade buscarNUP = new Entidade();
                buscarNUP.setNumeroProcessoSEI(limparCampos(entityTemp.getNumeroProcessoSEI()));
                dto.setEntidade(buscarNUP);
                dto.setUsuarioLogado(getUsuarioLogadoDaSessao());
                List<Entidade> lista = beneficiarioService.buscarSemPaginacao(dto);

                if (lista.size() > 0) {
                    if (entidadePrincipal == null || entidadePrincipal.getId() == null || lista.get(0).getId() != entityTemp.getId()) {
                        addMsgError("O 'Nº Processo (NUP)' informado já esta cadastrado no sistema.");
                        validar = false;
                    }
                }
            }
        }

        if (entityTemp.getStatusEntidade() == null) {
            addMsgError("O campo 'Ativar Entidade' é obrigatório.");
            validar = false;
        }

        if (!validar) {
            mensagem.setObject(msg);
        } else {
            mensagem.setObject("");
        }
        // target.add(labelMensagem);

        return validar;
    }

    public boolean validarEntidade(boolean validar) {
        validar = validarCnpj(entidade.getNumeroCnpj(), validar);

        int tamanhoCepEntidade = limparCampos(entidade.getNumeroCep()).length();
        if (tamanhoCepEntidade < 7) {
            addMsgError("O CEP da Entidade deverá conter 7 números.");
            validar = false;
        }

        int tamanhoTelefoneEntidade = limparCampos(entidade.getNumeroTelefone()).length();
        if (tamanhoTelefoneEntidade < 10) {
            addMsgError("O Telefone da Entidade deverá conter ao menos 10 números.");
            validar = false;
        }

        if (entidade.getNumeroFoneFax() != null || !"".equalsIgnoreCase(entidade.getNumeroFoneFax())) {
            int tamanhoFaxEntidade = limparCampos(entidade.getNumeroFoneFax()).length();
            if (tamanhoFaxEntidade < 10) {
                addMsgError("O Telefone/FAX da Entidade deverá conter ao menos 10 números.");
                validar = false;
            }
        }

        return validar;
    }

    public boolean validarTitular(boolean validar, Pessoa titular) {
        String cpfTitular = limparCampos(titular.getNumeroCpf());
        if (!CPFUtils.validate(cpfTitular)) {
            addMsgError("O CPF do Titular está em um formato inválido.");
            validar = false;
        }

        int tamanhoTelefoneTitular = limparCampos(titular.getNumeroTelefone()).length();
        if (tamanhoTelefoneTitular < 10) {
            addMsgError("O Telefone do Titular deverá conter ao menos 10 números.");
            validar = false;
        }

        String emailTitular = titular.getEmail();
        if (!EmailValidator.validate(emailTitular)) {
            addMsgError("O Email do Titular está em um formato inválido");
            validar = false;
        }

        return validar;
    }

    public boolean validarRepresentante(boolean validar, Pessoa representante) {
        String cpfRepresentante = representante.getNumeroCpf();
        if (!CPFUtils.validate(cpfRepresentante)) {
            addMsgError("O CPF do Representante está em um formato inválido");
            validar = false;
        }

        int tamanhoTelefoneRepresentante = limparCampos(representante.getNumeroTelefone()).length();
        if (tamanhoTelefoneRepresentante < 10) {
            addMsgError("O Telefone do Representante deverá conter ao menos 10 números.");
            validar = false;
        }

        String emailRepresentante = representante.getEmail();
        if (!EmailValidator.validate(emailRepresentante)) {
            addMsgError("O Email do Representante está em um formato inválido");
            validar = false;
        }

        return validar;
    }

    public boolean validarAnexos() {
        boolean validar = true;
        List<EnumTipoArquivoEntidade> tipoArquivo = Arrays.asList(EnumTipoArquivoEntidade.values());
        List<EnumTipoArquivoEntidade> tiposCadastrados = new ArrayList<EnumTipoArquivoEntidade>();

        for (EntidadeAnexo anexo : panelAnexo.getListAnexoTemp()) {
            if (!tiposCadastrados.contains(anexo.getTipoArquivo())) {
                tiposCadastrados.add(anexo.getTipoArquivo());
            }
        }

        if (tiposCadastrados.size() < tipoArquivo.size()) {
            validar = false;
        }

        return validar;
    }

    private boolean validarEmailUnico(boolean validar, String emailTemp) {

        if (!EmailValidator.validate(emailTemp)) {
            addMsgError("O E-Mail da Entidade está em um formato inválido");
            validar = false;
            return validar;
        }

        // Irá validar se este e-mail é unico entre as Entidades
        if (!validarEmailUnicoSalvoNoBancoDeDados(validar, emailTemp)) {
            validar = false;
            return validar;
        }
        return validar;
    }

    private boolean validarEmailUnicoSalvoNoBancoDeDados(boolean validar, String emailTemp) {
        EntidadePesquisaDto dto = new EntidadePesquisaDto();
        Entidade buscar = new Entidade();
        buscar.setEmail(emailTemp);
        dto.setEntidade(buscar);
        dto.setUsuarioLogado(getUsuarioLogadoDaSessao());
        List<Entidade> lista = beneficiarioService.buscarSemPaginacao(dto);
        if (lista.size() > 0) {
            for (Entidade ent : lista) {
                if (ent.getEmail().equalsIgnoreCase(entidade.getEmail())) {
                    if (entidade.getId() == null || entidadePrincipal.getId() != ent.getId()) {
                        addMsgError("O E-Mail Informado na seção 'Dados da Entidade' já esta cadastrado no sistema para outra Entidade.");
                        validar = false;
                        break;
                    }
                }
            }
        }
        return validar;
    }

    public List<PessoaEntidade> montarListaTitularesERepresententesBeneficiario() {
        // Lista que conterá todos os titulares e representantes cadastrados
        List<PessoaEntidade> listaPessoa = new ArrayList<PessoaEntidade>();

        // Listas que serão usadas para limpar os cpf e telefones das pessoas e
        // depois adicionados a 'listaPessoa'
        List<PessoaEntidade> representante = new ArrayList<PessoaEntidade>();
        List<PessoaEntidade> titular = new ArrayList<PessoaEntidade>();

        // Pega a lista de titulares adicionados e limpa os campos
        titular = panelDadosTitular.getListaDePessoas();
        for (PessoaEntidade tit : titular) {
            tit.getPessoa().setNumeroCpf(limparCampos(tit.getPessoa().getNumeroCpf()));
            tit.getPessoa().setNumeroTelefone(limparCampos(tit.getPessoa().getNumeroTelefone()));
            listaPessoa.add(tit);
        }

        // Pega a lista de representantes adicionados e limpa os campos
        representante = panelDadosRepresentante.getListaDePessoas();
        for (PessoaEntidade rep : representante) {
            if(rep.getPessoa().getTipoPessoa()==EnumTipoPessoa.REPRESENTANTE_ENTIDADE)
            {
                rep.getPessoa().setNumeroCpf(limparCampos(rep.getPessoa().getNumeroCpf()));
                rep.getPessoa().setNumeroTelefone(limparCampos(rep.getPessoa().getNumeroTelefone()));
                listaPessoa.add(rep);
            }
        }

        return listaPessoa;
    }

    public boolean validarCnpj(String cnpj, boolean validar) {
        boolean valido = validar;
        if (cnpj.length() < 14) {
            addMsgError("O 'CNPJ' deverá conter 14 digitos.");
            valido = false;
        } else {
            CnpjUtil cnpjValido = new CnpjUtil(cnpj);
            if (!cnpjValido.isCnpjValido()) {
                addMsgError("O 'CNPJ' informado esta em um formato inválido.");
                valido = false;
            }
        }
        return valido;
    }

    private void acaoCheck(AjaxRequestTarget target) {
        boolean checkLocal = checkRepresentante.getModelObject();

        if (checkLocal) {
            panelAtualizarRepresentante.setVisible(false);
        } else {
            panelAtualizarRepresentante.setVisible(true);
        }
        target.add(panelAtualizarRepresentante);
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

    public PanelDadosRepresentanteDashboard getPanelDadosRepresentante() {
        return panelDadosRepresentante;
    }

    public void setPanelDadosRepresentante(PanelDadosRepresentanteDashboard panelDadosRepresentante) {
        this.panelDadosRepresentante = panelDadosRepresentante;
    }

    public PanelDadosEntidadeDashboard getPanelDadosEntidade() {
        return panelDadosEntidade;
    }

    public void setPanelDadosEntidade(PanelDadosEntidadeDashboard panelDadosEntidade) {
        this.panelDadosEntidade = panelDadosEntidade;
    }

    public PanelDadosTitularDashboard getPanelDadosTitular() {
        return panelDadosTitular;
    }

    public void setPanelDadosTitular(PanelDadosTitularDashboard panelDadosTitular) {
        this.panelDadosTitular = panelDadosTitular;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }
}
