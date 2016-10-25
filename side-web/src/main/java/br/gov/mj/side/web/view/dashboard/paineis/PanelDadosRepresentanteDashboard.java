package br.gov.mj.side.web.view.dashboard.paineis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraLocalDateTextField;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.seg.entidades.enums.EnumTipoUsuario;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumSiglaSistema;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.web.dto.EntidadeDto;
import br.gov.mj.side.web.dto.EntidadePesquisaDto;
import br.gov.mj.side.web.service.BeneficiarioService;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.service.SegurancaService;
import br.gov.mj.side.web.util.CPFUtils;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.EmailValidator;
import br.gov.mj.side.web.view.dashboard.DashboardInfoEntidadePage;

public class PanelDadosRepresentanteDashboard extends Panel {
    private static final long serialVersionUID = 1L;

    private static final String ONCHANGE = "onchange";

    private Page backPage;
    private DashboardInfoEntidadePage page;

    private PanelSomenteCpf panelSomenteCpf;
    private PanelPrincipalBeneficiario panelPrincipalBeneficiario;
    private PanelDataView panelDataView;

    private InfraLocalDateTextField field1;
    private InfraLocalDateTextField field2;

    private LocalDate dataInicio;
    private LocalDate dataFim;

    private List<PessoaEntidade> listaDePessoas = new ArrayList<PessoaEntidade>();

    private AjaxButton buttonAdicionar;
    private AjaxButton buttonSalvarEdicao;
    private AjaxSubmitLink buttonCancelarEdicao;
    private AjaxButton buttonVerificarCpf;
    private TextField<String> fieldCpf;
    private DropDownChoice<Boolean> dropDownChoice;

    private DataView<PessoaEntidade> dataView;
    private Label labelMensagem;
    private Model<String> mensagem = Model.of("");

    private EntidadeDto entidadeDto = new EntidadeDto();
    private Pessoa entidadeDtoTemp = new Pessoa();
    private Pessoa pessoaEncontradaPelaDigitacaoCpf = new Pessoa();
    private boolean modoEdicao = false;
    private boolean mostrarBotaoCancelarEdicao = false;
    private int posicaoPessoaLista;
    private String cpfTemporario = "";
    private String msg = "";

    private String nomePessoa;
    private String numeroCpf;
    private String descricaoCargo;
    private String numeroTelefone;
    private String email;
    private LocalDate dataInicioExercicio;
    private LocalDate dataFimExercicio;
    private String enderecoCorrespondencia;
    private Boolean ativo = null;
    private Boolean mostrarBotaoAdicionarRepresentante = true;
    private Boolean mostrarBotaoVerificarCpf = true;
    private Boolean mostrarTextFieldVerificarCpf = true;
    private Boolean mostrarTextFieldEmail = true;
    private Boolean dadoAindaNaoSalvoNoBanco = true;

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private BeneficiarioService beneficiarioService;

    @Inject
    private SegurancaService segurancaService;

    public PanelDadosRepresentanteDashboard(String id, Page backPage) {
        super(id);
        this.backPage = backPage;
        initVariaveis();
        setOutputMarkupId(true);

        add(panelPrincipalBeneficiario = new PanelPrincipalBeneficiario("panelPrincipalBeneficiario"));
        add(panelSomenteCpf = new PanelSomenteCpf("panelSomenteCpf"));
        add(panelDataView = new PanelDataView("panelDataView"));

        panelPrincipalBeneficiario.setEnabled(false);
        buttonAdicionar.setVisible(false);
    }

    private class PanelPrincipalBeneficiario extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelPrincipalBeneficiario(String id) {
            super(id);
            setOutputMarkupId(true);

            add(getTextFieldNome()); // entidadeRepresentante.nomePessoa
            add(getTextFieldCargo()); // entidadeRepresentante.descricaoCargo
            add(getTextFieldTelefone()); // entidadeRepresentante.numeroTelefone
            add(getTextFieldEmail()); // entidadeRepresentante.email
            add(getDateTextFieldPeriodo1()); // dataPeriodo1
            add(getDateTextFieldPeriodo2()); // dataPeriodo2
            add(getTextFieldEndereco()); // entidadeRepresentante.enderecoCorrespondencia
            add(getDropDownAtivarDesativarBeneficiario()); // dropAtivo
        }
    }

    private class PanelSomenteCpf extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelSomenteCpf(String id) {
            super(id);
            setOutputMarkupId(true);

            labelMensagem = new Label("mensagemAnexo", mensagem);
            labelMensagem.setEscapeModelStrings(false);
            add(labelMensagem);

            add(getTextFieldCpf()); // entidadeRepresentante.numeroCpf
            add(getButtonVerificarCpf()); // btnVerificarCpf
        }
    }

    private class PanelDataView extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDataView(String id) {
            super(id);
            add(getDataViewResultado()); // dataTitulares
            add(new InfraAjaxPagingNavigator("pagination", dataView));
            add(getButtonAdicionar()); // btnAdicionar
            add(getButtonCancelarEdicao()); // btnCancelarEdicao
            add(getButtonSalvarEdicao()); // btnSalvarEdicao
        }
    }

    private void initVariaveis() {
        page = (DashboardInfoEntidadePage) backPage;
        entidadeDto = page.getForm().getModelObject();

        // Se estiver editando o titular
        if (entidadeDto.getId() != null) {
            Entidade entidadeBuscar = new Entidade();
            entidadeBuscar.setId(entidadeDto.getId());
            listaDePessoas = beneficiarioService.buscarRepresentanteEntidade(entidadeBuscar,false);

            // é necessário inicializar esta variavel para que os campos de
            // input do titular venham todos em branco
            entidadeDto.setEntidadeTitular(new Pessoa());
        }

    }

    private TextField<String> getTextFieldNome() {

        TextField<String> field = componentFactory.newTextField("pessoa.nomePessoa", "Nome (Representante)", false, new PropertyModel(this, "nomePessoa"));
        field.add(StringValidator.maximumLength(200));
        actionTextFieldNome(field);
        return field;
    }

    private TextField<String> getTextFieldCpf() {
        fieldCpf = componentFactory.newTextField("entidadeRepresentante.numeroCpf", "CPF (Representante)", false, new PropertyModel(this, "numeroCpf"));
        fieldCpf.add(StringValidator.maximumLength(14));
        actionTextFieldNome(fieldCpf);
        fieldCpf.setOutputMarkupId(true);
        
        // Quando puder editar retirar o comentário abaixo.
        fieldCpf.setEnabled(false);
        //fieldCpf.setEnabled(mostrarTextFieldVerificarCpf);
        return fieldCpf;
    }

    private TextField<String> getTextFieldCargo() {
        TextField<String> field = componentFactory.newTextField("entidadeRepresentante.descricaoCargo", "Cargo (Representante)", false, new PropertyModel(this, "descricaoCargo"));
        field.add(StringValidator.maximumLength(200));
        actionTextFieldNome(field);
        return field;
    }

    private TextField<String> getTextFieldTelefone() {
        TextField<String> field = componentFactory.newTextField("entidadeRepresentante.numeroTelefone", "Telefone (Representante)", false, new PropertyModel(this, "numeroTelefone"));
        field.add(StringValidator.maximumLength(13));
        actionTextFieldNome(field);
        return field;
    }

    private TextField<String> getTextFieldEmail() {
        TextField<String> field = componentFactory.newTextField("entidadeRepresentante.email", "E-mail (Representante)", false, new PropertyModel(this, "email"));
        field.add(StringValidator.maximumLength(200));
        actionTextFieldNome(field);
        field.setEnabled(mostrarTextFieldEmail);
        return field;
    }

    private InfraLocalDateTextField getDateTextFieldPeriodo1() {

        field1 = componentFactory.newDateTextFieldWithDatePicker("entidadeRepresentante.dataInicioExercicio", "Período de Exercício (inicial)", false, new PropertyModel(this, "dataInicioExercicio"), "dd/MM/yyyy", "pt-BR");
        actionFimExercicio(field1);
        return field1;
    }

    private InfraLocalDateTextField getDateTextFieldPeriodo2() {
        field2 = componentFactory.newDateTextFieldWithDatePicker("entidadeRepresentante.dataFimExercicio", "Período de Exercício (final)", false, new PropertyModel(this, "dataFimExercicio"), "dd/MM/yyyy", "pt-BR");
        actionInicioExercicio(field2);
        return field2;
    }

    private TextField<String> getTextFieldEndereco() {
        TextField<String> field = componentFactory.newTextField("entidadeRepresentante.enderecoCorrespondencia", "Endereço de Correspondência", false, new PropertyModel(this, "enderecoCorrespondencia"));
        field.add(StringValidator.maximumLength(200));
        // actionTextFieldEndereco(field);
        return field;
    }

    
    
    private DataView<PessoaEntidade> getDataViewResultado() {
        dataView = new DataView<PessoaEntidade>("dataTitulares", new TitularProvider()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<PessoaEntidade> item) {

                String nomeDaPessoa = "";

                String cpf= CPFUtils.format(item.getModelObject().getPessoa().getNumeroCpf());
                String cpfMascarado=CPFUtils.mascararCpf(cpf,'*',3);        
                        
                item.add(new Label("pessoa.numeroCpf", cpfMascarado));

                if (mostrarPalavraTitularNoDataView(item)) {
                    nomeDaPessoa = item.getModelObject().getPessoa().getNomePessoa() + "<font color='red'> - TITULAR </font>";
                    Label label = new Label("pessoa.nomePessoa", nomeDaPessoa);
                    label.setEscapeModelStrings(false);
                    item.add(label);
                } else {
                    item.add(new Label("pessoa.nomePessoa"));
                }
                item.add(new Label("pessoa.statusPessoa", item.getModelObject().getPessoa().getStatusPessoa().getDescricao()));
                item.add(new Label("pessoa.descricaoCargo"));

                String dataInicial = dataDocumentoBR(item.getModelObject().getPessoa().getDataInicioExercicio());
                String dataFinal;
                if (item.getModelObject().getPessoa().getDataFimExercicio() != null) {
                    dataFinal = " a " + dataDocumentoBR(item.getModelObject().getPessoa().getDataFimExercicio());
                } else {
                    dataFinal = "";
                }
                item.add(new Label("exercicio", dataInicial + dataFinal));

                //Quando puder editar adicionar a linha comentada retirando a linha logo abaixo.
                item.add(getButtonEditar(item, false).setVisible(false));
                //item.add(getButtonEditar(item, false).setVisible(mostrarBotoesDeAcao(item)));
                item.add(getButtonVisualizar(item, true).setVisible(mostrarBotoesDeAcao(item)));
                dadoAindaNaoSalvoNoBanco = item.getModelObject().getId() == null;
                item.add(getButtonExcluir(item).setVisible(dadoAindaNaoSalvoNoBanco));
            }
        };
        dataView.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        dataView.setOutputMarkupId(true);
        return dataView;
    }

    public Button getButtonAdicionar() {
        buttonAdicionar = new AjaxButton("btnAdicionar") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                adicionar(target);
            }
        };
        buttonAdicionar.setVisible(mostrarBotaoAdicionarRepresentante);
        buttonAdicionar.setOutputMarkupId(true);
        return buttonAdicionar;
    }

    public AjaxSubmitLink getButtonEditar(Item<PessoaEntidade> item, boolean modoVisualizar) {
        AjaxSubmitLink button = new AjaxSubmitLink("btnEditar") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                acaoVisualizarEditar(target, item, modoVisualizar);
            }
        };
        button.setDefaultFormProcessing(false);
        return button;
    }

    public AjaxSubmitLink getButtonVisualizar(Item<PessoaEntidade> item, boolean modoVisualizar) {
        AjaxSubmitLink button = new AjaxSubmitLink("btnVisualizar") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                acaoVisualizarEditar(target, item, modoVisualizar);
            }
        };
        button.setDefaultFormProcessing(false);
        return button;
    }

    public AjaxButton getButtonSalvarEdicao() {
        buttonSalvarEdicao = new AjaxButton("btnSalvarEdicao") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                salvarEdicao(target);
            }
        };
        //Quando puder salvar as edições feitas remover a linha abaixo e trocar pela linha comentada.
        buttonSalvarEdicao.setVisible(false);
        //buttonSalvarEdicao.setVisible(modoEdicao);
        buttonSalvarEdicao.setOutputMarkupId(true);
        return buttonSalvarEdicao;
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

    public InfraAjaxConfirmButton getButtonExcluir(Item<PessoaEntidade> item) {
        return componentFactory.newAJaxConfirmButton("btnExcluir", "MSG001", null, (target, formz) -> excluirTitular(target, item));
    }

    public Button getButtonVerificarCpf() {
        buttonVerificarCpf = new AjaxButton("btnVerificarCpf") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                verificarCpf(target);
            }
        };
        
        //Quando puder verificar outros CPFs basta tirar o comentário da linha abaixo.
        buttonVerificarCpf.setVisible(false);
        //buttonVerificarCpf.setVisible(mostrarBotaoVerificarCpf);
        buttonVerificarCpf.setOutputMarkupId(true);
        return buttonVerificarCpf;
    }

    private DropDownChoice<Boolean> getDropDownAtivarDesativarBeneficiario() {
        dropDownChoice = new DropDownChoice<Boolean>("dropAtivo", new PropertyModel(this, "ativo"), Arrays.asList(Boolean.TRUE, Boolean.FALSE));
        dropDownChoice.setLabel(Model.of("Ativar Usuário"));
        dropDownChoice.setNullValid(true);
        dropDownChoice.setChoiceRenderer(new IChoiceRenderer<Boolean>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Object getDisplayValue(Boolean object) {
                if (object != null && object) {
                    return "Sim";
                } else {
                    return "Não";
                }
            }

            @Override
            public String getIdValue(Boolean object, int index) {
                return object.toString();
            }

        });

        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            protected void onUpdate(AjaxRequestTarget target) {
                acaoDrop(target);
            }

        });

        return dropDownChoice;
    }

    // AÇÕES

    private void actionTextFieldCpf(TextField<String> field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                boolean validar = true;
                msg = "";
                if (!validarCpfCompleto(validar)) {
                    mensagem.setObject(msg);
                    target.add(labelMensagem);
                    return;
                }
                mensagem.setObject("");
                target.add(labelMensagem);
                zerarCamposDadosBasicosSemCpf();

                Pessoa pessoa = new Pessoa();
                pessoa.setNumeroCpf(CPFUtils.clean(numeroCpf));

                EntidadePesquisaDto enti = new EntidadePesquisaDto();
                enti.setRepresentante(pessoa);
                enti.setUsuarioLogado(page.getUsuarioLogadoDaSessao());
                List<Entidade> lista = beneficiarioService.buscarSemPaginacao(enti);

                if (lista.size() > 0) {
                    for (PessoaEntidade result : lista.get(0).getPessoas()) {
                        if (CPFUtils.clean(result.getPessoa().getNumeroCpf()).equalsIgnoreCase(CPFUtils.clean(numeroCpf))) {
                            nomePessoa = result.getPessoa().getNomePessoa();
                            descricaoCargo = result.getPessoa().getDescricaoCargo();
                            numeroTelefone = result.getPessoa().getNumeroTelefone();
                            email = result.getPessoa().getEmail();
                            dataInicioExercicio = result.getPessoa().getDataInicioExercicio();
                            dataFimExercicio = result.getPessoa().getDataFimExercicio();
                            enderecoCorrespondencia = result.getPessoa().getEnderecoCorrespondencia();

                            pessoaEncontradaPelaDigitacaoCpf = result.getPessoa();
                            break;
                        }
                    }

                }
                panelPrincipalBeneficiario.setEnabled(true);
                atualizarInputs(target);
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    // O botão será mostrado se a pessoa for representante ou se for um cadastro
    // ainda novo.
    private boolean mostrarBotoesDeAcao(Item<PessoaEntidade> item) {
        return (!page.getReadOnly() && (item.getModelObject().getPessoa().getId() == null) || item.getModelObject().getPessoa().getTipoPessoa() == EnumTipoPessoa.REPRESENTANTE_ENTIDADE);
    }

    // Se esta pessoa for titular mas possuir função de representante será
    // mostrado com a palavara 'titular' na frente
    private boolean mostrarPalavraTitularNoDataView(Item<PessoaEntidade> item) {
        return item.getModelObject().getPessoa().isTitularComFuncaoDeRepesentante() && item.getModelObject().getPessoa().getStatusPessoa() == EnumStatusPessoa.ATIVO;
    }

    private void actionTextFieldNome(TextField<String> field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no Model
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    private void actionInicioExercicio(TextField<LocalDate> field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no Model
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    private void actionFimExercicio(TextField<LocalDate> field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no Model
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    private void actionTextFieldEndereco(TextField<String> field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no Model
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    public void adicionar(AjaxRequestTarget target) {
        if (!validarEditarPessoa(target)) {
            return;
        }

        modoEdicao = false;
        mostrarBotaoCancelarEdicao = false;

        Pessoa entidade = new Pessoa();
        if (pessoaEncontradaPelaDigitacaoCpf != null && pessoaEncontradaPelaDigitacaoCpf.getId() == null) {
            
            entidade=setarValoresNaEntidade(entidade);
            
        } else {
            entidade = pessoaEncontradaPelaDigitacaoCpf;
            entidade=setarValoresNaEntidade(entidade);
        }

        EnumStatusPessoa  statusPessoa = ativo ? EnumStatusPessoa.ATIVO : EnumStatusPessoa.INATIVO;

        entidade.setStatusPessoa(statusPessoa);
        PessoaEntidade pe = new PessoaEntidade();
        if (pessoaEncontradaPelaDigitacaoCpf.getId() != null) {
            pe.setPessoa(pessoaEncontradaPelaDigitacaoCpf);
        } else {
            pe.setPessoa(entidade);
        }
        listaDePessoas.add(pe);

        panelPrincipalBeneficiario.setEnabled(false);
        mostrarBotaoAdicionarRepresentante = false;
        mostrarBotaoVerificarCpf = true;
        mostrarTextFieldEmail = true;
        mostrarTextFieldVerificarCpf = true;
        zerarCamposDadosBasicos();
        atualizarInputs(target);
    }

    public void salvarEdicao(AjaxRequestTarget target) {

        numeroCpf = entidadeDtoTemp.getNumeroCpf();
        if (!validarEditarPessoa(target)) {
            return;
        }

        entidadeDtoTemp.setNomePessoa(nomePessoa);
        entidadeDtoTemp.setNumeroCpf(numeroCpf);
        entidadeDtoTemp.setDescricaoCargo(descricaoCargo);
        entidadeDtoTemp.setNumeroTelefone(numeroTelefone);
        entidadeDtoTemp.setEmail(email);
        entidadeDtoTemp.setDataInicioExercicio(dataInicioExercicio);
        entidadeDtoTemp.setDataFimExercicio(dataFimExercicio);
        entidadeDtoTemp.setEnderecoCorrespondencia(enderecoCorrespondencia);
        entidadeDtoTemp.setPossuiFuncaoDeRepresentante(true);

        EnumStatusPessoa statusPessoa = ativo ? EnumStatusPessoa.ATIVO : EnumStatusPessoa.INATIVO;
        entidadeDtoTemp.setStatusPessoa(statusPessoa);

        modoEdicao = false;
        mostrarBotaoCancelarEdicao = false;

        panelPrincipalBeneficiario.setEnabled(false);
        zerarCamposDadosBasicos();
        mostrarBotaoVerificarCpf = true;
        mostrarTextFieldEmail = true;
        mostrarTextFieldVerificarCpf = true;
        mostrarBotaoAdicionarRepresentante = false;
        atualizarInputs(target);
    }

    public void cancelarEdicao(AjaxRequestTarget target) {
        modoEdicao = false;
        mostrarBotaoCancelarEdicao = false;

        panelPrincipalBeneficiario.setEnabled(false);
        zerarCamposDadosBasicos();
        mostrarBotaoAdicionarRepresentante = false;
        mostrarBotaoVerificarCpf = true;
        mostrarTextFieldVerificarCpf = true;
        mostrarTextFieldEmail = true;
        atualizarInputs(target);
    }

    public void excluirTitular(AjaxRequestTarget target, Item<PessoaEntidade> item) {
        Pessoa itemSelecionadoExcluir = item.getModelObject().getPessoa();

        for (PessoaEntidade entidade : listaDePessoas) {
            if (entidade.getPessoa().getNumeroCpf().equalsIgnoreCase(itemSelecionadoExcluir.getNumeroCpf())) {
                listaDePessoas.remove(entidade);
                break;
            }
        }

        modoEdicao = false;
        mostrarBotaoCancelarEdicao = false;

        panelPrincipalBeneficiario.setEnabled(false);
        zerarCamposDadosBasicos();
        mostrarBotaoVerificarCpf = true;
        mostrarTextFieldVerificarCpf = true;
        mostrarTextFieldEmail = true;
        mostrarBotaoAdicionarRepresentante = false;
        atualizarInputs(target);
    }

    public void editarRepresentante(AjaxRequestTarget target, Item<PessoaEntidade> item) {

    }

    private boolean compararCpfsCadastradosDosDoisPaineis() {
        List<PessoaEntidade> outraListaPessoa = page.getPanelDadosTitular().getListaDePessoas();
        Boolean cpfUnico = true;
        for (PessoaEntidade entidadeExterna : outraListaPessoa) {
            if (CPFUtils.clean(entidadeExterna.getPessoa().getNumeroCpf()).equalsIgnoreCase(CPFUtils.clean(numeroCpf))) {
                msg += "<p><li> O CPF informado já esta cadastrado na lista de Titulares.</li><p />";
                cpfUnico = false;
                break;
            }
            if (!cpfUnico) {
                break;
            }
        }
        return cpfUnico;
    }

    public void zerarCamposDadosBasicos() {
        nomePessoa = "";
        numeroCpf = "";
        descricaoCargo = "";
        numeroTelefone = "";
        email = "";
        dataInicioExercicio = null;
        dataFimExercicio = null;
        enderecoCorrespondencia = "";
        ativo = null;
    }

    public void zerarCamposDadosBasicosSemCpf() {
        nomePessoa = "";
        descricaoCargo = "";
        numeroTelefone = "";
        email = "";
        dataInicioExercicio = null;
        dataFimExercicio = null;
        enderecoCorrespondencia = "";
        ativo = null;
    }

    private void atualizarInputs(AjaxRequestTarget target) {
        panelSomenteCpf.addOrReplace(getTextFieldCpf());
        panelSomenteCpf.addOrReplace(getButtonVerificarCpf());
        panelPrincipalBeneficiario.addOrReplace(getTextFieldNome());
        panelPrincipalBeneficiario.addOrReplace(getTextFieldCargo());
        panelPrincipalBeneficiario.addOrReplace(getTextFieldTelefone());
        panelPrincipalBeneficiario.addOrReplace(getTextFieldEmail());
        panelPrincipalBeneficiario.addOrReplace(getDateTextFieldPeriodo1());
        panelPrincipalBeneficiario.addOrReplace(getDateTextFieldPeriodo2());
        panelPrincipalBeneficiario.addOrReplace(getTextFieldEndereco());
        panelPrincipalBeneficiario.addOrReplace(getDropDownAtivarDesativarBeneficiario());
        panelDataView.addOrReplace(getButtonCancelarEdicao());
        panelDataView.addOrReplace(getButtonSalvarEdicao());
        panelDataView.addOrReplace(getButtonAdicionar());
        mensagem.setObject("");

        target.add(panelSomenteCpf);
        target.add(panelPrincipalBeneficiario);
        target.add(panelDataView);
        target.add(labelMensagem);
    }

    // O InputText do E-Mail somente será editavel se a propria pessoa logada
    // clicar nele.
    private boolean mostrarInputTextDeEmail(Pessoa usarioClicado) {
        boolean mostrar = true;
        Usuario usuarioLogado = page.receberUsuarioLogado();
        
        
        if (usarioClicado.getId() == null || verificarSeOUsuarioLogadoPossuiFuncaoDeUsuarioInterno(usuarioLogado)) {
            mostrar = true;
        } else {
            if (usarioClicado == null || usarioClicado.getUsuario() == null) {
                mostrar = false;
            } else {
                if (usuarioLogado.getId() == usarioClicado.getUsuario().getId()) {
                    mostrar = true;
                } else {
                    mostrar = false;
                }
            }
        }
        return mostrar;
    }

    public boolean validarEditarPessoa(AjaxRequestTarget target) {
        boolean validar = true;
        msg = "";

        validar = validarCpfCompleto(validar);
        validar = validarTitular(validar);
        validar = validarCamposObrigatorios(validar);
        validar = validarEmailUnico(validar);

        if (!validar) {
            mensagem.setObject(msg);
        } else {
            mensagem.setObject("");
        }
        target.add(labelMensagem);

        return validar;
    }

    public boolean validarCpfCompleto(boolean validar) {
        if (numeroCpf == null || "".equalsIgnoreCase(numeroCpf)) {
            msg += "<p><li> O Campo CPF é obrigatório.</li><p />";
            validar = false;
        } else {
            if (compararCpfsCadastradosDosDoisPaineis()) {
                if (validar) {
                    int i = 0;
                    int contador = 0;
                    for (PessoaEntidade b : listaDePessoas) {
                        String cpfLista = CPFUtils.clean(b.getPessoa().getNumeroCpf());

                        if (CPFUtils.clean(numeroCpf).equalsIgnoreCase(cpfLista)) {
                            if (posicaoPessoaLista != contador) {
                                msg += "<p><li> O campo 'CPF' informado já esta em uso.</li><p />";
                                validar = false;
                                i++;
                            }
                        }
                        contador++;
                    }

                    if (i > 0) {
                        validar = false;
                    } else {
                        validar = validarCPf(numeroCpf, validar);
                    }
                }
            } else {
                validar = false;
            }
        }

        return validar;
    }

    public boolean validarTitular(boolean validar) {
        if (numeroTelefone != null && numeroTelefone.length() < 10) {
            msg += "<p><li> O Telefone deverá conter ao menos 10 números.</li><p />";
            validar = false;
        }

        if (email != null && !EmailValidator.validate(email)) {
            msg += "<p><li> O Email está em um formato inválido.</li><p />";
            validar = false;
        }
        return validar;
    }

    private boolean validarCamposObrigatorios(boolean validar) {
        if (nomePessoa == null || "".equalsIgnoreCase(nomePessoa)) {
            msg += "<p><li> O campo 'Nome' é obrigatório.</li><p />";
            validar = false;
        }

        if (numeroCpf == null || "".equalsIgnoreCase(numeroCpf)) {
            msg += "<p><li> O campo 'CPF' é obrigatório.</li><p />";
            validar = false;
        }

        if (descricaoCargo == null || "".equalsIgnoreCase(descricaoCargo)) {
            msg += "<p><li> O campo 'Cargo' é obrigatório.</li><p />";
            validar = false;
        }

        if (numeroTelefone == null || "".equalsIgnoreCase(numeroTelefone)) {
            msg += "<p><li> O campo 'Telefone' é obrigatório.</li><p />";
            validar = false;
        }

        if (email == null || "".equalsIgnoreCase(email)) {
            msg += "<p><li> O campo 'E-Mail' é obrigatório.</li><p />";
            validar = false;
        }

        if (dataInicioExercicio == null) {
            msg += "<p><li> O campo 'Período de Exercício (Inicial)' é obrigatório.</li><p />";
            validar = false;
        }

        if (dataInicioExercicio != null && dataFimExercicio != null) {
            if (dataInicioExercicio.isAfter(dataFimExercicio)) {
                msg += "<p><li> O Período de Exercício Inicial não poderá ser superior ao Período de Exercício Final.</li><p />";
                validar = false;
            }
        }

        if (enderecoCorrespondencia == null || "".equalsIgnoreCase(enderecoCorrespondencia)) {
            msg += "<p><li> O campo 'Endereço de Correspondência' é obrigatório.</li><p />";
            validar = false;
        }

        if (ativo == null) {
            msg += "<p><li> O campo 'Ativar Representante' é obrigatório.</li><p />";
            validar = false;
        }

        return validar;
    }

    private boolean validarEmailUnico(boolean validar) {

        // Irá validar se este e-mail é unico entre os dataViews da tela
        validar = validarEmailUnicoNosDataViews(validar);
        if (!validar) {
            validar = false;
            return validar;
        }

        if(pessoaEncontradaPelaDigitacaoCpf == null || pessoaEncontradaPelaDigitacaoCpf.getId() == null)
        {
         // Irá verificar se este email já esta cadastrado na tabela de Pessoas
            if (!validarEmailUnicoNoSalvoNoBancoDeDados(validar)) {
                validar = false;
                return validar;
            }

            // Irá verificar se este email já esta cadastrado na tabela de Usuarios.
            if (!validarEmailUnicoSalvoNaTabelaDeUsuariosDoBancoDeDados(validar)) {
                validar = false;
                return validar;
            }
        }
        
        return validar;
    }

    private boolean validarEmailUnicoNosDataViews(boolean validar) {
        List<PessoaEntidade> outraListaPessoa = page.getPanelDadosTitular().getListaDePessoas();
        for (PessoaEntidade entidadeExterna : outraListaPessoa) {
            if (entidadeExterna.getPessoa().getEmail().equalsIgnoreCase(email)) {
                msg += "<p><li> O E-Mail informado já esta cadastrado na lista de Titulares.</li><p />";
                validar = false;
                break;
            }
        }
        return validar;
    }

    private boolean validarEmailUnicoNoSalvoNoBancoDeDados(boolean validar) {
        EntidadePesquisaDto pesquisaEmail = new EntidadePesquisaDto();
        Pessoa pessoa = new Pessoa();
        pessoa.setEmail(email);
        pesquisaEmail.setRepresentante(pessoa);
        pesquisaEmail.setUsuarioLogado(page.getUsuarioLogadoDaSessao());
        List<Entidade> lista = beneficiarioService.buscarSemPaginacao(pesquisaEmail);
        if (lista.size() > 0) {
            boolean encontrado=false;
            for (Entidade ent : lista) {
                for (PessoaEntidade pe : ent.getPessoas()) {
                    String emailEncontrado = pe.getPessoa().getEmail();

                    if (emailEncontrado.equalsIgnoreCase(email)) {
                        if (entidadeDtoTemp == null || pe.getPessoa().getId() != entidadeDtoTemp.getId()) {
                            msg += "<p><li> O E-Mail Informado já esta cadastrado no sistema.</li><p />";
                            validar = false;
                            encontrado=true;
                            break;
                        }
                    }
                }
                if(encontrado)
                {
                    break;
                }
            }
        }
        return validar;
    }

    private boolean validarEmailUnicoSalvoNaTabelaDeUsuariosDoBancoDeDados(boolean validar) {
        try {
            Usuario usuarioEncontrado = segurancaService.buscarUsuarioPeloEmail(email, EnumSiglaSistema.SIDE.getValor());
            if (usuarioEncontrado.getId() != entidadeDtoTemp.getUsuario().getId()) {
                msg += "<p><li> O E-Mail Informado já esta cadastrado no sistema.</li><p />";
                validar = false;
            }
        } catch (BusinessException be) {
            return validar;
        }
        return validar;
    }
    
    //porque esse método foi implementado se na classe DataUtil ja existe o metodo
    //converteDataDeLocalDateParaString que faz exatamente a mesma coisa.
    //Indicavel refatorar
    public String dataDocumentoBR(LocalDate dataDocumento) {
        DateTimeFormatter sdfPadraoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (dataDocumento != null) {
            dataDocumento.format(sdfPadraoBR);
            return sdfPadraoBR.format(dataDocumento);
        }
        return " - ";
    }

    // PROVIDER

    private class TitularProvider extends SortableDataProvider<PessoaEntidade, String> {
        private static final long serialVersionUID = 1L;

        public TitularProvider() {
            // setSort("nomeCriterio", SortOrder.ASCENDING);
        }

        @Override
        public Iterator<PessoaEntidade> iterator(long first, long size) {

            List<PessoaEntidade> listTemp = new ArrayList<PessoaEntidade>();
            int firstTemp = 0;
            int flagTemp = 0;
            for (PessoaEntidade k : listaDePessoas) {
                if (firstTemp >= first) {
                    if (flagTemp <= size) {
                        listTemp.add(k);
                        flagTemp++;
                    }
                }
                firstTemp++;
            }

            return listTemp.iterator();
        }

        @Override
        public long size() {
            return listaDePessoas.size();
        }

        @Override
        public IModel<PessoaEntidade> model(PessoaEntidade object) {
            return new CompoundPropertyModel<PessoaEntidade>(object);
        }
    }

    public boolean validarCPf(String cpf, boolean validar) {
        boolean valido = validar;
        if (CPFUtils.clean(cpf).length() < 11) {
            msg += "<p><li> O 'CPF' deverá conter 11 digitos.</li><p />";
            valido = false;
        } else {
            if (!CPFUtils.validate(CPFUtils.clean(cpf))) {
                msg += "<p><li> O 'CPF' informado esta em um formato inválido.</li><p />";
                valido = false;
            }
        }
        return valido;
    }

    private void verificarCpf(AjaxRequestTarget target) {
        boolean validar = true;
        msg = "";
        if (!validarCpfCompleto(validar)) {
            mensagem.setObject(msg);
            target.add(labelMensagem);
            return;
        }

        mensagem.setObject("");
        target.add(labelMensagem);
        zerarCamposDadosBasicosSemCpf();

        Pessoa pessoa = new Pessoa();
        pessoa.setNumeroCpf(CPFUtils.clean(numeroCpf));

        EntidadePesquisaDto enti = new EntidadePesquisaDto();
        enti.setRepresentante(pessoa);
        enti.setUsuarioLogado(page.getUsuarioLogadoDaSessao());
        List<Entidade> lista = beneficiarioService.buscarSemPaginacao(enti);

        EntidadePesquisaDto entiTitular = new EntidadePesquisaDto();
        entiTitular.setTitular(pessoa);
        entiTitular.setUsuarioLogado(page.getUsuarioLogadoDaSessao());
        List<Entidade> listaTitular = beneficiarioService.buscarSemPaginacao(entiTitular);

        if (listaTitular.size() > 0) {
            msg += "<p><li> Não é possível realizar este cadastro pois esta pessoa já esta cadastrada como Titular em outra Entidade.</li><p />";
            mensagem.setObject(msg);
            target.add(labelMensagem);
            return;
        }

        pessoaEncontradaPelaDigitacaoCpf = new Pessoa();
        if (lista.size() > 0) {
            for (PessoaEntidade result : lista.get(0).getPessoas()) {
                if (CPFUtils.clean(result.getPessoa().getNumeroCpf()).equalsIgnoreCase(CPFUtils.clean(numeroCpf))) {
                    nomePessoa = result.getPessoa().getNomePessoa();
                    descricaoCargo = result.getPessoa().getDescricaoCargo();
                    numeroTelefone = result.getPessoa().getNumeroTelefone();
                    email = result.getPessoa().getEmail();
                    dataInicioExercicio = result.getPessoa().getDataInicioExercicio();
                    dataFimExercicio = result.getPessoa().getDataFimExercicio();
                    enderecoCorrespondencia = result.getPessoa().getEnderecoCorrespondencia();
                    pessoaEncontradaPelaDigitacaoCpf = result.getPessoa();
                    break;
                }
            }
        }

        mostrarBotaoVerificarCpf = false;
        mostrarTextFieldEmail = mostrarInputTextDeEmail(entidadeDtoTemp);
        mostrarTextFieldVerificarCpf = false;
        mostrarBotaoAdicionarRepresentante = true;
        mostrarBotaoCancelarEdicao = true;
        panelPrincipalBeneficiario.setEnabled(true);

        atualizarInputs(target);
    }
    
    private boolean verificarSeOUsuarioLogadoPossuiFuncaoDeUsuarioInterno(Usuario usuario)
    {
            if(usuario==null)
            {
                return false;
            }
            else
            {
                if(usuario.getTipoUsuario()==EnumTipoUsuario.INTERNO)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
    }

    private void acaoDrop(AjaxRequestTarget target) {
        ativo = dropDownChoice.getModelObject();
    }

    private void acaoVisualizarEditar(AjaxRequestTarget target, Item<PessoaEntidade> item, Boolean modoVisualizar) {
        mostrarBotaoCancelarEdicao = true;
        entidadeDtoTemp = item.getModelObject().getPessoa();

        nomePessoa = entidadeDtoTemp.getNomePessoa();
        String numeroCpfPrimario= new String(CPFUtils.format(entidadeDtoTemp.getNumeroCpf()));
        numeroCpf=CPFUtils.mascararCpf(numeroCpfPrimario,'*',3); 
        descricaoCargo = entidadeDtoTemp.getDescricaoCargo();
        numeroTelefone = entidadeDtoTemp.getNumeroTelefone();
        email = entidadeDtoTemp.getEmail();
        dataInicioExercicio = entidadeDtoTemp.getDataInicioExercicio();
        dataFimExercicio = entidadeDtoTemp.getDataFimExercicio();
        enderecoCorrespondencia = entidadeDtoTemp.getEnderecoCorrespondencia();

        boolean isAtivo = entidadeDtoTemp.getStatusPessoa()==EnumStatusPessoa.ATIVO ? true : false;
        ativo = isAtivo;

        cpfTemporario = entidadeDtoTemp.getNumeroCpf();
        posicaoPessoaLista = item.getIndex();

        mostrarBotaoVerificarCpf = false;
        mostrarTextFieldVerificarCpf = false;
        mostrarTextFieldEmail = mostrarInputTextDeEmail(entidadeDtoTemp);
        mostrarBotaoAdicionarRepresentante = false;

        if (modoVisualizar) {
            panelPrincipalBeneficiario.setEnabled(false);
            modoEdicao = false;
        } else {
            panelPrincipalBeneficiario.setEnabled(true);
            modoEdicao = true;
        }

        fieldCpf.setEnabled(false);
        atualizarInputs(target);
    }
    
    private Pessoa setarValoresNaEntidade(Pessoa entidade)
    {
        entidade.setNomePessoa(nomePessoa);
        entidade.setNumeroCpf(numeroCpf);
        entidade.setDescricaoCargo(descricaoCargo);
        entidade.setNumeroTelefone(numeroTelefone);
        entidade.setEmail(email);
        entidade.setDataInicioExercicio(dataInicioExercicio);
        entidade.setDataFimExercicio(dataFimExercicio);
        entidade.setEnderecoCorrespondencia(enderecoCorrespondencia);
        entidade.setPossuiFuncaoDeRepresentante(true);
        entidade.setTipoPessoa(EnumTipoPessoa.REPRESENTANTE_ENTIDADE);
        
        return entidade;
    }

    public static String formatCpf(String cpf) {
        StringBuilder builder = new StringBuilder(cpf.replaceAll("[^\\d]", ""));
        builder.insert(3, '.');
        builder.insert(7, '.');
        builder.insert(11, '-');
        return builder.toString();
    }

    public List<PessoaEntidade> getListaDePessoas() {
        return listaDePessoas;
    }

    public void setListaDePessoas(List<PessoaEntidade> listaDePessoas) {
        this.listaDePessoas = listaDePessoas;
    }

    public InfraLocalDateTextField getField1() {
        return field1;
    }

    public void setField1(InfraLocalDateTextField field1) {
        this.field1 = field1;
    }

    public InfraLocalDateTextField getField2() {
        return field2;
    }

    public void setField2(InfraLocalDateTextField field2) {
        this.field2 = field2;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

}
