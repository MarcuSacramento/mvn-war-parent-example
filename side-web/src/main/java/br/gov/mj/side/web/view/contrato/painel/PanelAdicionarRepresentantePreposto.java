package br.gov.mj.side.web.view.contrato.painel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.web.dto.EntidadePesquisaDto;
import br.gov.mj.side.web.service.FornecedorService;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.util.CPFUtils;
import br.gov.mj.side.web.util.EmailValidator;
import br.gov.mj.side.web.view.contrato.ContratoPage;

@AuthorizeInstantiation({ PanelAdicionarRepresentantePreposto.ROLE_MANTER_CONTRATO_VISUALIZAR, PanelAdicionarRepresentantePreposto.ROLE_MANTER_CONTRATO_INCLUIR, PanelAdicionarRepresentantePreposto.ROLE_MANTER_CONTRATO_ALTERAR, PanelAdicionarRepresentantePreposto.ROLE_MANTER_CONTRATO_EXCLUIR })
public class PanelAdicionarRepresentantePreposto extends Panel {

    private static final long serialVersionUID = 1L;

    public static final String ROLE_MANTER_CONTRATO_VISUALIZAR = "manter_contrato:visualizar";
    public static final String ROLE_MANTER_CONTRATO_INCLUIR = "manter_contrato:incluir";
    public static final String ROLE_MANTER_CONTRATO_ALTERAR = "manter_contrato:alterar";
    public static final String ROLE_MANTER_CONTRATO_EXCLUIR = "manter_contrato:excluir";

    private PanelDadosBasicos panelDadosBasicos;

    private TextField<String> fieldNome;
    private TextField<String> fieldCpf;
    private TextField<String> fieldTelefone;
    private TextField<String> fieldEmail;

    private AjaxSubmitLink buttonAdicionar;
    private ContratoPage pagina;

    private List<PessoaEntidade> segundaLista = new ArrayList<PessoaEntidade>();
    private List<PessoaEntidade> lista = new ArrayList<PessoaEntidade>();
    private PessoaEntidade pessoaEntidade = new PessoaEntidade();
    private EnumTipoPessoa tipoPessoa;

    private Label labelMensagem;
    private Model<String> mensagem = Model.of("");
    private String msg = "";

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private GenericEntidadeService entidadeService;

    public PanelAdicionarRepresentantePreposto(String id, List<PessoaEntidade> lista, List<PessoaEntidade> segundaLista, EnumTipoPessoa tipoPessoa, ContratoPage pagina) {
        super(id);
        setOutputMarkupId(true);
        this.lista = lista;
        this.segundaLista = segundaLista;
        this.tipoPessoa = tipoPessoa;
        this.pagina = pagina;

        initVariaveis();
        initComponents();
    }

    private void initVariaveis() {
        pessoaEntidade.setPessoa(new Pessoa());
    }

    private void initComponents() {
        add(panelDadosBasicos = new PanelDadosBasicos("panelDadosBasicos"));

        // Mensagens de erro
        labelMensagem = new Label("mensagemErro", mensagem);
        labelMensagem.setEscapeModelStrings(false);
        add(labelMensagem);
    }

    // PAINEIS
    private class PanelDadosBasicos extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDadosBasicos(String id) {
            super(id);

            add(newTextFieldNome()); // txtNome
            add(newTextFieldCpf()); // txtCpf
            add(newTextFieldTelefone()); // txtTelefone
            add(newTextFieldEmail()); // txtEmail
            add(newButtonAdicionar());// btnAdicionar
            add(newButtonOcultarPanelAdicionar()); //btnOcultarAdicionar
        } 
    }

    // COMPONENTES
    public TextField<String> newTextFieldNome() {
        fieldNome = componentFactory.newTextField("txtNome", "Nome", false, new PropertyModel(this, "pessoaEntidade.pessoa.nomePessoa"));
        fieldNome.add(StringValidator.maximumLength(200));
        actionTextField(fieldNome);
        return fieldNome;
    }

    public TextField<String> newTextFieldCpf() {
        fieldCpf = componentFactory.newTextField("txtCpf", "CPF", false, new PropertyModel(this, "pessoaEntidade.pessoa.numeroCpf"));
        fieldCpf.add(StringValidator.maximumLength(14));
        actionTextField(fieldCpf);
        return fieldCpf;
    }

    public TextField<String> newTextFieldTelefone() {
        fieldTelefone = componentFactory.newTextField("txtTelefone", "Telefone", false, new PropertyModel(this, "pessoaEntidade.pessoa.numeroTelefone"));
        fieldTelefone.add(StringValidator.maximumLength(200));
        actionTextField(fieldTelefone);
        return fieldTelefone;
    }

    public TextField<String> newTextFieldEmail() {
        fieldEmail = componentFactory.newTextField("txtEmail", "Email", false, new PropertyModel(this, "pessoaEntidade.pessoa.email"));
        fieldEmail.add(StringValidator.maximumLength(200));
        actionTextField(fieldEmail);
        return fieldEmail;
    }

    private AjaxSubmitLink newButtonAdicionar() {
        buttonAdicionar = new AjaxSubmitLink("btnAdicionar") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                super.onSubmit(target, form);

                actionAdicionar(target);
            }
        };
        buttonAdicionar.setDefaultFormProcessing(false);
        buttonAdicionar.setOutputMarkupId(true);
        buttonAdicionar.setEnabled(true);
        return buttonAdicionar;
    }
    
    private AjaxSubmitLink newButtonOcultarPanelAdicionar() {
        AjaxSubmitLink buttonOcultarAdicionarPreposto = new AjaxSubmitLink("btnOcultarAdicionar") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                super.onSubmit(target, form);

                actionCancelar(target);
            }
        };
        buttonOcultarAdicionarPreposto.setDefaultFormProcessing(false);
        buttonOcultarAdicionarPreposto.setOutputMarkupId(true);
        buttonOcultarAdicionarPreposto.setEnabled(true);
        buttonOcultarAdicionarPreposto.setVisible(true);
        return buttonOcultarAdicionarPreposto;
    }

    // AÇÕES

    private void actionTextField(TextField field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no model
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    private void actionAdicionar(AjaxRequestTarget target) {

        setarValoresNaVariavelEntidadePessoa();
        msg = "";

        boolean todosCamposValidos = validarTodosOsCampos();
        mensagem.setObject(msg);
        target.add(labelMensagem);
        
        if(!todosCamposValidos){
            return;
        }

        lista.add(pessoaEntidade);

        zerarValoresNaVariavelEntidadePessoa();
        pagina.getPanelDadosFornecedor().actionAtualizarDrop(target, tipoPessoa);
    }
    
    private void actionCancelar(AjaxRequestTarget target){
        zerarValoresNaVariavelEntidadePessoa();
        pagina.getPanelDadosFornecedor().actionOcultarPanel(target, tipoPessoa);
    }

    private boolean validarTodosOsCampos() {
        boolean validarCamposObrigatorios = validarCamposObrigatorios();
        if (!validarCamposObrigatorios) {
            return false;
        }

        boolean validarCpf = validarCpfCompleto();
        boolean validarEmail = validarEmailCompleto();
        if (!validarCpf || !validarEmail) {
            return false;
        }

        return true;
    }

    private void setarValoresNaVariavelEntidadePessoa() {
        pessoaEntidade.getPessoa().setNomePessoa(fieldNome.getRawInput());
        pessoaEntidade.getPessoa().setNumeroCpf(fieldCpf.getRawInput());
        pessoaEntidade.getPessoa().setNumeroTelefone(fieldTelefone.getRawInput());
        pessoaEntidade.getPessoa().setEmail(fieldEmail.getRawInput());
        pessoaEntidade.getPessoa().setTipoPessoa(tipoPessoa);
    }

    private void zerarValoresNaVariavelEntidadePessoa() {

        pessoaEntidade = new PessoaEntidade();
        Pessoa pessoa = new Pessoa();
        pessoaEntidade.setPessoa(pessoa);
        
        fieldNome.clearInput();
        fieldCpf.clearInput();
        fieldTelefone.clearInput();
        fieldEmail.clearInput();
    }

    private boolean validarCamposObrigatorios() {
        boolean retorno = true;

        if (pessoaEntidade.getPessoa().getNomePessoa() == null || "".equalsIgnoreCase(pessoaEntidade.getPessoa().getNomePessoa())) {
            msg += "<p><li> O Campo 'CPF' é obrigatório.</li><p />";
            retorno = false;
        }
        if (pessoaEntidade.getPessoa().getNumeroCpf() == null || "".equalsIgnoreCase(pessoaEntidade.getPessoa().getNumeroCpf())) {
            msg += "<p><li> O Campo 'Nome' é obrigatório.</li><p />";
            retorno = false;
        }
        if (pessoaEntidade.getPessoa().getNumeroTelefone() == null || "".equalsIgnoreCase(pessoaEntidade.getPessoa().getNumeroTelefone())) {
            msg += "<p><li> O Campo 'Telefone' é obrigatório.</li><p />";
            retorno = false;
        }
        if (pessoaEntidade.getPessoa().getEmail() == null || "".equalsIgnoreCase(pessoaEntidade.getPessoa().getEmail())) {
            msg += "<p><li> O Campo 'E-Mail' é obrigatório.</li><p />";
            retorno = false;
        }

        return retorno;
    }

    public boolean validarCpfCompleto() {

        String cpf = pessoaEntidade.getPessoa().getNumeroCpf();
        
        if(!validarCPf(cpf)){
            return false;
        }
        
        if (!verificarSeCpfJaCadastrado(cpf)) {
            return false;
        }

        if (!compararCpfsCadastradosDosDoisPaineis()) {
            return false;
        }

        return true;
    }

    private boolean validarEmailCompleto() {
        String email = pessoaEntidade.getPessoa().getEmail();

        if (!EmailValidator.validate(email)) {
            msg += "<p><li> O 'E-Mail' informado esta em um formato inválido.</li><p />";
            return false;
        }

        if (!compararEmailsCadastradosDosDoisPaineis()) {
            return false;
        }

        if (!verificarSeEmailJaCadastradoEmBanco()) {
            return false;
        }
        return true;
    }

    private boolean compararEmailsCadastradosDosDoisPaineis() {

        String email = pessoaEntidade.getPessoa().getEmail();
        boolean validar = true;
        for (PessoaEntidade entidadeExterna : segundaLista) {
            if (entidadeExterna.getPessoa().getEmail().equalsIgnoreCase(email)) {

                String tipoPessoa = entidadeExterna.getPessoa().getTipoPessoa().getDescricao();

                msg += "<p><li> O E-Mail informado já esta cadastrado na lista de " + tipoPessoa + ".</li><p />";
                validar = false;
                break;
            }
            if (!validar) {
                break;
            }
        }
        return validar;
    }

    private boolean verificarSeEmailJaCadastradoEmBanco() {

        String email = pessoaEntidade.getPessoa().getEmail();
        boolean valido = true;
        Pessoa pessoa = new Pessoa();
        pessoa.setEmail(email);

        EntidadePesquisaDto entiRepresentante = new EntidadePesquisaDto();
        entiRepresentante.setTodos(pessoa);
        entiRepresentante.setUsuarioLogado(pagina.getUsuarioLogadoDaSessao());
        List<Entidade> listaDeRepresentantes = entidadeService.buscarSemPaginacao(entiRepresentante);
        if (listaDeRepresentantes.size() > 0) {
            valido = false;
            msg += "<p><li> O 'E-Mail' informado já esta em uso.</li><p />";
        }

        return valido;
    }

    public boolean validarCPf(String cpf) {
        boolean valido = true;
        
        if (CPFUtils.clean(cpf).length() < 11) {
            msg += "<p><li> O 'CPF' deverá conter 11 digitos.</li><p />";
            valido = false;
        } else {
            if (!CPFUtils.validate(cpf)) {
                msg += "<p><li> O 'CPF' informado esta em um formato inválido.</li><p />";
                valido = false;
            }
        }
        return valido;
    }

    private boolean compararCpfsCadastradosDosDoisPaineis() {

        String cpf = pessoaEntidade.getPessoa().getNumeroCpf();
        Boolean cpfUnico = true;
        for (PessoaEntidade entidadeExterna : segundaLista) {
            if (CPFUtils.clean(entidadeExterna.getPessoa().getNumeroCpf()).equalsIgnoreCase(CPFUtils.clean(cpf))) {

                String tipoPessoa = entidadeExterna.getPessoa().getTipoPessoa().getDescricao();

                msg += "<p><li> O CPF informado já esta cadastrado na lista de " + tipoPessoa + ".</li><p />";
                cpfUnico = false;
                break;
            }
            if (!cpfUnico) {
                break;
            }
        }
        return cpfUnico;
    }

    private boolean verificarSeCpfJaCadastrado(String cpf) {

        boolean valido = true;
        Pessoa pessoa = new Pessoa();
        pessoa.setNumeroCpf(CPFUtils.clean(cpf));
        EntidadePesquisaDto entiRepresentante = new EntidadePesquisaDto();
        entiRepresentante.setPesquisarTodos(true);
        entiRepresentante.setTodos(pessoa);

        entiRepresentante.setUsuarioLogado(pagina.getUsuarioLogadoDaSessao());
        List<Entidade> lista = entidadeService.buscarSemPaginacao(entiRepresentante);
        if (lista.size() > 0) {
            if (pessoaEntidade.getId() == null) {
                valido = false;
            } else {
                if (pessoaEntidade.getId().intValue() != lista.get(0).getId().intValue()) {
                    valido = false;
                }
            }
        }

        if (!valido) {
            msg += "<p><li> O 'CPF' informado já esta em uso.</li><p />";
        }

        return valido;
    }
}
