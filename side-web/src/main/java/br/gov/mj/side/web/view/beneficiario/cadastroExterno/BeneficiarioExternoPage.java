package br.gov.mj.side.web.view.beneficiario.cadastroExterno;

import java.time.LocalDate;
import java.util.ArrayList;
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
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.EntidadeAnexo;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumOrigemCadastro;
import br.gov.mj.side.entidades.enums.EnumStatusEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.entidades.enums.EnumValidacaoCadastro;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.web.dto.EntidadeDto;
import br.gov.mj.side.web.dto.EntidadePesquisaDto;
import br.gov.mj.side.web.service.AnexoEntidadeService;
import br.gov.mj.side.web.service.BeneficiarioService;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.service.MailService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.service.SegurancaService;
import br.gov.mj.side.web.util.CPFUtils;
import br.gov.mj.side.web.util.CnpjUtil;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.EmailValidator;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.LoginPage;
import br.gov.mj.side.web.view.beneficiario.paineis.PanelAnexo;
import br.gov.mj.side.web.view.beneficiario.paineis.PanelDadosEntidade;
import br.gov.mj.side.web.view.beneficiario.paineis.PanelDadosRepresentante;
import br.gov.mj.side.web.view.beneficiario.paineis.PanelDadosTitular;
import br.gov.mj.side.web.view.components.AjaxDialogButton;
import br.gov.mj.side.web.view.template.TemplateExternalPage;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Backdrop;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.TextContentModal;

public class BeneficiarioExternoPage extends TemplateExternalPage {
    private static final long serialVersionUID = 1L;

    private PanelAtualizarRepresentante panelAtualizarRepresentante;
    private PanelDadosRepresentante panelDadosRepresentante;
    private PanelDadosEntidade panelDadosEntidade;
    private PanelDadosTitular panelDadosTitular;
    private PanelBotoes panelBotoes;
    private PanelAnexo panelAnexo;
    private TextField<String> fieldCnpj;
    private Modal<String> modalConfirmUf;
    private Modal<String> modalLimparFormulario;
    private String msgConfirmUf = new String();

    private Form<EntidadeDto> form;
    private Page backPage;
    private Button buttonSalvar;
    private AjaxButton buttonVerificarCnpj;
    private AjaxSubmitLink buttonCancelarEdicao;
    private Link link;
    private InfraDropDownChoice<Programa> dropDownPrograma;

    private Programa programaSelecionado;
    private EntidadeDto entidade = new EntidadeDto();
    private Entidade entidadePrincipal = new Entidade();
    private List<EntidadeAnexo> listaAnexos = new ArrayList<EntidadeAnexo>();
    private Boolean readOnly = false;
    private Boolean dataExpirada = false;
    private Boolean cadastroNovo = false;
    private Boolean existeEntidade = false;
    private Boolean mostrarBotaoVisualizarCnpj = true;
    private Boolean mostrarBotaoCancelarEdicao = false;
    boolean acionarCamposDeInput;
    private AjaxCheckBox checkRepresentante;
    private Label labelMensdfsagem;
    private Model<String> mensagem = Model.of("");
    private StringValue hash;
    private StringValue entidadePassadaParametro;
    
    private List<PessoaEntidade> listaDeRepresentantes = new ArrayList<PessoaEntidade>();
    private List<PessoaEntidade> listaDeTitulares = new ArrayList<PessoaEntidade>();
    private List<PessoaEntidade> listaDeMembrosComissao = new ArrayList<PessoaEntidade>();

    private String numeroCnpj;

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private BeneficiarioService beneficiarioService;
    
    @Inject
    private GenericEntidadeService genericEntidadeService;

    @Inject
    private MailService mailService;

    @Inject
    private AnexoEntidadeService anexoService;

    @Inject
    private SegurancaService segurancaService;
    @Inject
    private ProgramaService programaService;

    
    public BeneficiarioExternoPage(final PageParameters pageParameters){
        super(pageParameters);
        
        initiVariaveis();
        initComponentes();
        
        
        
        String titulo = "";
        if (readOnly && !dataExpirada) {
            titulo = "Visualizar Beneficiário";
            setMsgConfirmUf("Esta entidade não pode ser editada neste momento pois a solicitação de cadastro já foi enviada.");
            modalConfirmUf.show(true);
        } else {
            titulo = "Cadastrar Beneficiário";
        }

        setTitulo(titulo);

        boolean acionarCamposDeInput = false;
        if (cadastroNovo) {
            acionarCamposDeInput = false;
        } else {
            if (readOnly) {
                acionarCamposDeInput = false;
                fieldCnpj.setEnabled(false);
                numeroCnpj = entidadePrincipal.getNumeroCnpj();
            } else {
                acionarCamposDeInput = true;
                fieldCnpj.setEnabled(false);
                numeroCnpj = entidadePrincipal.getNumeroCnpj();
                mostrarBotaoCancelarEdicao = true;
            }
            buttonVerificarCnpj.setVisibilityAllowed(false);
        }

        panelDadosEntidade.setEnabled(acionarCamposDeInput);
        panelDadosRepresentante.setEnabled(acionarCamposDeInput);
        panelDadosTitular.setEnabled(acionarCamposDeInput);
        panelAnexo.setEnabled(acionarCamposDeInput);
    }

    // Construtor chamado depois que for digitado o CNPJ da entidade, será
    // verificado se já existe entidade ou não
    public BeneficiarioExternoPage(final PageParameters pageParameters, Page backPage, Entidade entidadePrincipal, Boolean readOnly, Boolean existeEntidade, String numeroCnpj, Programa programaSelecionado) {
        super(pageParameters);

        this.backPage = backPage;
        this.entidadePrincipal = entidadePrincipal;
        this.readOnly = readOnly;
        this.existeEntidade = existeEntidade;
        this.programaSelecionado = programaSelecionado;
        mostrarBotaoVisualizarCnpj = false;
        mostrarBotaoCancelarEdicao = true;
        acionarCamposDeInput = true;

        initiVariaveis();
        initComponentes();

        if (existeEntidade) {
            setTitulo("Editar Beneficiário");
            setMsgConfirmUf("Entidade já Cadastrada.");
            modalConfirmUf.show(true);
            fieldCnpj.setEnabled(false);
        } else {
            setTitulo("Cadastrar Beneficiário");
            fieldCnpj.setEnabled(false);
        }
        this.numeroCnpj = numeroCnpj;
    }

    private void initiVariaveis() {
        //Estas variáveis serão utilizadas somente quando o cadastro
        //For reprovado, serão utilizadas para recuperar a entidade novamente
        
        hash = getRequest().getRequestParameters().getParameterValue("hash");
        
        if(!hash.isNull()){
            
                Usuario usuarioEncontrado = segurancaService.buscarUsuarioPeloHashDeAlteracaoEntidade(hash.toString(), "SIDE");
                Entidade entidadeDoUsuario = genericEntidadeService.buscarEntidadeDoUsuario(usuarioEncontrado);
                entidadePrincipal = entidadeDoUsuario;
                
                if(usuarioEncontrado.getDataExpiracaoAlteraEntidade().isBefore(LocalDate.now())){
                    addMsgError("Data limite para ajustes da Entidade expirada. Contate o Administrador do Sistema");
                    readOnly = true;
                    dataExpirada = true;
                }
                
                if(entidadePrincipal.getValidacaoCadastro() == EnumValidacaoCadastro.NAO_ANALISADO ||
                        entidadePrincipal.getValidacaoCadastro() == EnumValidacaoCadastro.VALIDADO){
                    readOnly = true;
                }
            
            
        }
        
        if (entidadePrincipal != null && entidadePrincipal.getId() != null) {
            List<PessoaEntidade> representante = new ArrayList<PessoaEntidade>();
            List<PessoaEntidade> titular = new ArrayList<PessoaEntidade>();
            cadastroNovo = false;

            titular = beneficiarioService.buscarTitularEntidade(entidadePrincipal);
            if (titular != null && !titular.isEmpty() && titular.get(0).getPessoa().getPossuiFuncaoDeRepresentante()) {
                representante = titular;
            } else {
                representante = beneficiarioService.buscarRepresentanteEntidade(entidadePrincipal, false);
            }

            listaAnexos = SideUtil.convertAnexoDtoToEntityEntidadeAnexo(anexoService.buscarPeloIdEntidade(entidadePrincipal.getId()));
            entidade.setEntidadeTitular(titular == null || titular.isEmpty() ? new Pessoa():titular.get(0).getPessoa());
            
            if(representante.size()>0){
                entidade.setEntidadeRepresentante(representante.get(0).getPessoa());
            }

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
            entidade.setOrigemCadastro(entidadePrincipal.getOrigemCadastro());
            programaSelecionado = entidadePrincipal.getProgramaPreferencial();
            inicializarAsListasDePessoas();
        } else {
            cadastroNovo = true;
            entidade.setOrigemCadastro(EnumOrigemCadastro.CADASTRO_EXTERNO);
            entidadePrincipal.setValidacaoCadastro(EnumValidacaoCadastro.NAO_ANALISADO);
        }
        
        Long entidadeEscolhida = (Long) getSessionAttribute("programa");
        if(entidadeEscolhida != null && entidadeEscolhida != 0){
            programaSelecionado = programaService.buscarPeloId(entidadeEscolhida);
        }
    }

    private void initComponentes() {
        form = componentFactory.newForm("form", new CompoundPropertyModel<EntidadeDto>(entidade));
        add(form);

        form.add(newDropDownPrograma()); //dropDownPrograma
        form.add(getTextFieldCnpj()); // numeroCnpj
        form.add(getButtonVerificarCnpj()); // btnVerificarCnpj
        form.add(getButtonCancelarEdicao()); // btnCancelarEdicao

        form.add(panelAtualizarRepresentante = new PanelAtualizarRepresentante("panelAtualizarRepresentante"));
        form.add(panelDadosEntidade = new PanelDadosEntidade("panelDadosEntidade", this,form.getModelObject()));
        form.add(panelDadosTitular = new PanelDadosTitular("panelDadosTitular", this, cadastroNovo, form.getModelObject(), listaDeTitulares, listaDeRepresentantes,listaDeMembrosComissao, readOnly));
        
        form.add(panelAnexo = new PanelAnexo("panelAnexo", this, listaAnexos, form.getModelObject(), readOnly));
        
        form.add(panelBotoes = new PanelBotoes("panelBotoes"));

        modalConfirmUf = newModal("modalConfirmUf");
        modalConfirmUf.show(false);
        modalLimparFormulario = newModalLimparFormulario("modalLimparFormulario");
        modalLimparFormulario.show(false);

        form.add(modalLimparFormulario);
        form.add(modalConfirmUf);
        
        //O link é uma ancora simples para subir a tela quando ocorrer algum erro de validação
        link = new Link("ancora") {
            @Override
            public void onClick() {
                setResponsePage(LoginPage.class);
            }
        };
        form.add(link);
        add(form);
    }

    private class PanelAtualizarRepresentante extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelAtualizarRepresentante(String id) {
            super(id);
            setOutputMarkupId(true);

            add(panelDadosRepresentante = new PanelDadosRepresentante("panelDadosRepresentante",form.getModelObject(), listaDeRepresentantes, listaDeTitulares, listaDeMembrosComissao, cadastroNovo, readOnly));
        }
    }
    
    private InfraDropDownChoice<Programa> newDropDownPrograma() {
        Programa programa = new Programa();
        List<Programa> listaTempProgramas = programaService.buscar(programa);
        List<Programa> listaProgramas = new ArrayList<Programa>();        
        
        for(Programa prg:listaTempProgramas){
            if(prg.getStatusPrograma() == EnumStatusPrograma.PUBLICADO || prg.getStatusPrograma() == EnumStatusPrograma.ABERTO_REC_PROPOSTAS){
                listaProgramas.add(prg);
            }
        }
        
        dropDownPrograma = componentFactory.newDropDownChoice("dropDownPrograma", "Selecione o programa de sua preferência", false, "id", "codigoIdentificadorProgramaPublicadoENomePrograma", new PropertyModel(this, "programaSelecionado"), listaProgramas, null);
        dropDownPrograma.setNullValid(true);
        dropDownPrograma.setEnabled(!readOnly);
        return dropDownPrograma;
    }

    public TextField<String> getTextFieldCnpj() {

        fieldCnpj = componentFactory.newTextField("numeroCnpj", "CNPJ", false, new PropertyModel(this, "numeroCnpj"));
        fieldCnpj.add(StringValidator.maximumLength(18));
        actionTextFieldCnpj(fieldCnpj);
        fieldCnpj.setOutputMarkupId(true);
        return fieldCnpj;
    }
    
    private class PanelBotoes extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBotoes(String id) {
            super(id);
            setOutputMarkupId(true);
            add(getButtonSalvar()); // btnSalvar
            add(getButtonVoltar()); // btnVoltar
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
        buttonSalvar.setEnabled(!readOnly);
        return buttonSalvar;
    }
    
    private Button getButtonVoltar() {
        Button button = componentFactory.newButton("btnVoltar", () -> actionSair());
        button.setDefaultFormProcessing(false);
        return button;
    }

    public Button getButtonVerificarCnpj() {
        buttonVerificarCnpj = new AjaxButton("btnVerificarCnpj") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                verificarCnpj(target);
            }
        };
        buttonVerificarCnpj.setOutputMarkupId(true);
        buttonVerificarCnpj.setVisible(mostrarBotaoVisualizarCnpj);
        return buttonVerificarCnpj;
    }

    public AjaxSubmitLink getButtonCancelarEdicao() {
        buttonCancelarEdicao = new AjaxSubmitLink("btnCancelarEdicao") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                cancelarEdicao(target);
            }
        };
        buttonCancelarEdicao.setDefaultFormProcessing(false);
        buttonCancelarEdicao.setVisible(mostrarBotaoCancelarEdicao);
        buttonCancelarEdicao.setOutputMarkupId(true);
        return buttonCancelarEdicao;
    }

    private Modal<String> newModal(String id) {
        Modal<String> modal = new TextContentModal(id, new LambdaModel<String>(this::getMsgConfirmUf, this::setMsgConfirmUf));
        modal.setBackdrop(Backdrop.STATIC);
        modal.addButton(newButtonFecharModal(modal));
        return modal;
    }

    private Modal<String> newModalLimparFormulario(String id) {
        Modal<String> modal = new TextContentModal(id, new LambdaModel<String>(this::getMsgConfirmUf, this::setMsgConfirmUf));
        modal.setBackdrop(Backdrop.STATIC);
        modal.addButton(newButtonLimparFormulario(modal));
        modal.addButton(newButtonFecharModal(modal));
        return modal;
    }

    // AÇÕES
    
    private boolean mostrarPainelDaComissao(){
        
        //Se o usuário não estiver logado (cadastro externo) não mostrar o painel
        if(entidade.getUsuario() == null && entidade.getOrigemCadastro() == EnumOrigemCadastro.CADASTRO_EXTERNO){
            return false;
        }else{
            
            //Se o cadastro ainda não foi analisado não mostrar o painel
            if(entidadePrincipal.getValidacaoCadastro() == EnumValidacaoCadastro.NAO_ANALISADO){
                return false;
            }else{
                return true;
            }
        }
    }
    
    private void inicializarAsListasDePessoas(){
        // Se estiver editando o titular
           if (entidadePrincipal.getId() != null) {
                   Entidade entidadeBuscar = new Entidade();
                   entidadeBuscar.setId(entidadePrincipal.getId());
                   listaDeRepresentantes = beneficiarioService.buscarRepresentanteEntidade(entidadeBuscar, false);
                   listaDeTitulares = beneficiarioService.buscarTitularEntidade(entidadeBuscar);
           }
       }

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
                setResponsePage(new BeneficiarioExternoPage(null));
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
        mailService.enviarEmailCadastroNovaEntidadePorUsuarioExterno(entidadePrincipal,maiorIdPessoaAtual, getUrlBase(Constants.PAGINA_ALTERACAO_SENHA));

        setResponsePage(new MensagemCadastroBeneficiarioExternoPage(null));
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
    
    public void cancelarEdicao(AjaxRequestTarget target) {
        setMsgConfirmUf("Esta ação irá limpar todo o Formulário, deseja continuar?.");
        modalLimparFormulario.show(true);
        target.add(modalLimparFormulario);
    }

    public void montarEntidadeParaSalvar(EntidadeDto entidadeDto, List<PessoaEntidade> listaPessoa) {
        // adiciona a listaPessoa já com os titulares e representantes
        entidadePrincipal.setPessoas(listaPessoa);

        entidadePrincipal.setProgramaPreferencial(programaSelecionado);
        entidadePrincipal.setNumeroCnpj(limparCampos(numeroCnpj));
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
        entidadePrincipal.setStatusEntidade(EnumStatusEntidade.INATIVA);
        entidadePrincipal.setOrigemCadastro(EnumOrigemCadastro.CADASTRO_EXTERNO);
        entidadePrincipal.setValidacaoCadastro(EnumValidacaoCadastro.NAO_ANALISADO);

        if (entidadeDto.getNumeroFoneFax() != null) {
            entidadePrincipal.setNumeroFoneFax(limparCampos(entidadeDto.getNumeroFoneFax()));
        } else {
            entidadePrincipal.setNumeroFoneFax(null);
        }

        entidadePrincipal.setEmail(entidadeDto.getEmail().toLowerCase());
        entidadePrincipal.setAnexos(panelAnexo.getListAnexoTemp());
    }

    public void actionSair() {
        setResponsePage(LoginPage.class);
    }

    public boolean validarCamposEntidade(EntidadeDto entityTemp, List<PessoaEntidade> listaPessoa, AjaxRequestTarget target) {
        boolean validar = true;
        String msg = "";

        if (listaPessoa.size() == 0) {
            addMsgError("É necessário adicionar ao menos 1 Titular e 1 Representante ou  1 Titular com função de Representante.");
            validar = false;
        } else {
            boolean possuiRepresentante = false;
            boolean possuiTitular = false;
            for (PessoaEntidade ent : listaPessoa) {
                if (ent.getPessoa().getPossuiFuncaoDeRepresentante() || ent.getPessoa().isTitularComFuncaoDeRepesentante()) {
                    possuiRepresentante = true;
                }
                
                if (ent.getPessoa().isTitular()) {
                    possuiTitular = true;
                }
            }

            if (!possuiRepresentante) {
                addMsgError("Adicione alguém com função de representante ou um titular com função de representante.");
                validar = false;
            }
            
            if (!possuiTitular) {
                addMsgError("Adicione alguém com função de titular.");
                validar = false;
            }
        }

        if (numeroCnpj == null || numeroCnpj.equalsIgnoreCase("")) {
            msg += "<p><li> O campo 'CNPJ' é obrigatório.</li><p />";
            validar = false;
        }
        
        if (programaSelecionado == null || programaSelecionado.getId() == null) {
            addMsgError("O campo 'Selecione o programa de sua preferência' é obrigatório.");
            validar = false;
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
        List<Entidade> lista = genericEntidadeService.buscarSemPaginacao(dto);
        if (lista.size() > 0) {
            for (Entidade ent : lista) {
                if (ent.getEmail().toLowerCase().equalsIgnoreCase(entidade.getEmail().toLowerCase())) {
                    if (entidade.getId() == null && entidadePrincipal.getId() != ent.getId()) {
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

        // Pega a lista de titulares adicionados e limpa os campos
        for (PessoaEntidade tit : listaDeTitulares) {
            tit.getPessoa().setNumeroCpf(limparCampos(tit.getPessoa().getNumeroCpf()));
            tit.getPessoa().setNumeroTelefone(limparCampos(tit.getPessoa().getNumeroTelefone()));
            listaPessoa.add(tit);
        }

        // Pega a lista de representantes adicionados e limpa os campos
        for (PessoaEntidade rep : listaDeRepresentantes) {
            if (rep.getPessoa().getTipoPessoa() == EnumTipoPessoa.REPRESENTANTE_ENTIDADE) {
                rep.getPessoa().setNumeroCpf(limparCampos(rep.getPessoa().getNumeroCpf()));
                rep.getPessoa().setNumeroTelefone(limparCampos(rep.getPessoa().getNumeroTelefone()));
                listaPessoa.add(rep);
            }
        }
        
     // Pega a lista de membros da comissão adicionados e limpa os campos
        for (PessoaEntidade rep : listaDeMembrosComissao) {
            if (rep.getPessoa().getTipoPessoa() == EnumTipoPessoa.MEMBRO_COMISSAO_RECEBIMENTO) {
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

    public void atualizarPaineisBaseadoNoCnpj(AjaxRequestTarget target, List<Entidade> entidade) {
        boolean possuiEntidade = false;
        if (entidade.size() > 0) {
            setMsgConfirmUf("A entidade informada já está cadastrada.");
            modalConfirmUf.show(true);
            target.add(modalConfirmUf);
            return;
        } else {
            entidadePrincipal = new Entidade();
        }
        
        setResponsePage(new BeneficiarioExternoPage(new PageParameters(), backPage, entidadePrincipal, false, possuiEntidade, numeroCnpj,programaSelecionado));
    }

    private void verificarCnpj(AjaxRequestTarget target) {
        boolean validar = true;
        if (numeroCnpj == null || "".equalsIgnoreCase(numeroCnpj)) {
            addMsgError("Informe um número de 'CNPJ' valido.");
            return;
        } else {
            String cnpj = limparCampos(numeroCnpj);
            validar = validarCnpj(cnpj, validar);
            if (!validar) {
                return;
            } else {
                
                EntidadePesquisaDto entidadePesquisaDto = new EntidadePesquisaDto();
                Entidade entidade = new Entidade();
                entidade.setNumeroCnpj(cnpj);
                
                entidadePesquisaDto.setEntidade(entidade);
                entidadePesquisaDto.setUsuarioLogado(getUsuarioLogadoDaSessao());
                
                List<Entidade> entidadeCnpj = new ArrayList<Entidade>();
                entidadeCnpj = genericEntidadeService.buscar(entidadePesquisaDto);

                atualizarPaineisBaseadoNoCnpj(target, entidadeCnpj);
            }
        }
    }

    public Usuario receberUsuarioLogado() {
        return getUsuarioLogadoDaSessao();
    }
    
 // Método sobrescrito para remover autenticação na página.
    @Override
    protected void onConfigure() {
        
    }

    @Override
    protected boolean isMenuVisible() {
        Usuario usuarioLogado=getUsuarioLogadoDaSessao();
        if(usuarioLogado!=null)
        {
            return true;
        }
        else
        {
            return false;
        }
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

    public String getMsgConfirmUf() {
        return msgConfirmUf;
    }

    public void setMsgConfirmUf(String msgConfirmUf) {
        this.msgConfirmUf = msgConfirmUf;
    }
}
