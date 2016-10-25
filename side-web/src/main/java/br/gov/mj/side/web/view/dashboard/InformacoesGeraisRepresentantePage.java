package br.gov.mj.side.web.view.dashboard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraLocalDateTextField;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.EntidadeAnexo;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumTipoArquivoEntidade;
import br.gov.mj.side.web.dto.AnexoDto;
import br.gov.mj.side.web.service.AnexoEntidadeService;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.DashboardPanel;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.components.EntityDataProvider;
import br.gov.mj.side.web.view.template.TemplatePage;

public class InformacoesGeraisRepresentantePage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    private PanelAnexoPessoa panelAnexoPessoa;
    private PanelPrincipalBeneficiario panelPrincipalBeneficiario;
    private DashboardPanel dashboardPessoasPanel;

    private Button btnDownload;
    private InfraAjaxConfirmButton btnExcluir;
    private AjaxSubmitLink buttonAdicionar;
    private FileUploadField fileUploadField;

    private Form<InformacoesGeraisRepresentantePage> form;
    private FileUploadForm formUpload;
    private DropDownChoice<EnumTipoArquivoEntidade> drop;
    private EnumTipoArquivoEntidade tipoArquivo;
    private LocalDate dataAnexo;

    private List<FileUpload> uploads;
    private List<EntidadeAnexo> listAnexoTemp = new ArrayList<EntidadeAnexo>();
    private Label labelMensagem;
    private Model<String> mensagem = Model.of("");
    private Page backPage;
    private Link link;

    private Usuario usuarioLogado;
    private Entidade entidadeEscolhida = new Entidade();

    private String nomePessoa;
    private String numeroCpf;
    private String descricaoCargo;
    private String numeroTelefone;
    private String email;
    private LocalDate dataInicioExercicio;
    private LocalDate dataFimExercicio;
    private String enderecoCorrespondencia;
    private Boolean ativo = null;

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private AnexoEntidadeService anexoService;

    @Inject
    private GenericEntidadeService genericEntidadeService;

    public InformacoesGeraisRepresentantePage(final PageParameters page) {
        super(page);
        
        initVariaveis();
        initComponents();
        setTitulo(entidadeEscolhida.getNomeEntidade());
    }

    private void initVariaveis() {
        verificarPessoaLogada();
        entidadeEscolhida = (Entidade) getSessionAttribute("entidade");

        labelMensagem = new Label("mensagemAnexo", mensagem);
        labelMensagem.setEscapeModelStrings(false);
        labelMensagem.setOutputMarkupId(true);
    }

    private void initComponents() {
        form = new Form<InformacoesGeraisRepresentantePage>("form", new CompoundPropertyModel<InformacoesGeraisRepresentantePage>(this));
        add(form);

        dashboardPessoasPanel = new DashboardPanel("dashboardPessoasPanel");
        authorize(dashboardPessoasPanel, RENDER, HomePage.ROLE_MANTER_INSCRICAO_VISUALIZAR);
        form.add(dashboardPessoasPanel);

        panelAnexoPessoa = new PanelAnexoPessoa("panelAnexoPessoa");
        panelPrincipalBeneficiario = new PanelPrincipalBeneficiario("panelPrincipalBeneficiario");
        panelPrincipalBeneficiario.setEnabled(false);
        panelAnexoPessoa.setVisible(false);

        form.add(panelAnexoPessoa);
        form.add(panelPrincipalBeneficiario);
    }

    private class PanelPrincipalBeneficiario extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelPrincipalBeneficiario(String id) {
            super(id);
            setOutputMarkupId(true);

            add(getTextFieldCpf()); // numeroCpf
            add(getTextFieldNome()); // nomePessoa
            add(getTextFieldCargo()); // descricaoCargo
            add(getTextFieldTelefone()); // numeroTelefone
            add(getTextFieldEmail()); // email
            add(getDateTextFieldPeriodo1()); // dataPeriodo1
            add(getDateTextFieldPeriodo2()); // dataPeriodo2
            add(getTextFieldEndereco()); // enderecoCorrespondencia
        }
    }

    private class PanelAnexoPessoa extends WebMarkupContainer {
        public PanelAnexoPessoa(String id) {
            super(id);

            formUpload = getFormUpload();
            add(formUpload);

            DataView<EntidadeAnexo> dataView = getDataViewAnexos("anexos");

            formUpload.add(dataView); // anexos
            formUpload.add(new InfraAjaxPagingNavigator("paginator", dataView));
            formUpload.add(getDropDownTipo()); // tipoArquivo
            formUpload.add(newDateTextFieldDataAnexo());
            add(labelMensagem);
        }
    }

    // COMPONENTES

    private TextField<String> getTextFieldNome() {

        TextField<String> field = componentFactory.newTextField("nomePessoa", "Nome (Representante)", false, new PropertyModel(this, "nomePessoa"));
        field.add(StringValidator.maximumLength(200));
        return field;
    }

    private TextField<String> getTextFieldCpf() {
        TextField<String> fieldCpf = componentFactory.newTextField("numeroCpf", "CPF (Representante)", false, new PropertyModel(this, "numeroCpf"));
        fieldCpf.add(StringValidator.maximumLength(14));
        fieldCpf.setOutputMarkupId(true);
        return fieldCpf;
    }

    private TextField<String> getTextFieldCargo() {
        TextField<String> field = componentFactory.newTextField("descricaoCargo", "Cargo (Representante)", false, new PropertyModel(this, "descricaoCargo"));
        field.add(StringValidator.maximumLength(200));
        return field;
    }

    private TextField<String> getTextFieldTelefone() {
        TextField<String> field = componentFactory.newTextField("numeroTelefone", "Telefone (Representante)", false, new PropertyModel(this, "numeroTelefone"));
        field.add(StringValidator.maximumLength(13));
        return field;
    }

    private TextField<String> getTextFieldEmail() {
        TextField<String> field = componentFactory.newTextField("email", "E-mail (Representante)", false, new PropertyModel(this, "email"));
        field.add(StringValidator.maximumLength(200));
        return field;
    }

    private InfraLocalDateTextField getDateTextFieldPeriodo1() {

        InfraLocalDateTextField field1 = componentFactory.newDateTextFieldWithDatePicker("dataPeriodo1", "Período de Exercício (inicial)", false, new PropertyModel(this, "dataInicioExercicio"), "dd/MM/yyyy", "pt-BR");
        return field1;
    }

    private InfraLocalDateTextField getDateTextFieldPeriodo2() {
        InfraLocalDateTextField field2 = componentFactory.newDateTextFieldWithDatePicker("dataPeriodo2", "Período de Exercício (final)", false, new PropertyModel(this, "dataFimExercicio"), "dd/MM/yyyy", "pt-BR");
        return field2;
    }

    private TextField<String> getTextFieldEndereco() {
        TextField<String> field = componentFactory.newTextField("enderecoCorrespondencia", "Endereço de Correspondência", false, new PropertyModel(this, "enderecoCorrespondencia"));
        field.add(StringValidator.maximumLength(200));
        return field;
    }

    private DropDownChoice<Boolean> getDropDownAtivarDesativarBeneficiario() {
        DropDownChoice<Boolean> dropDownChoice = new DropDownChoice<Boolean>("dropAtivo", new PropertyModel(this, "ativo"), Arrays.asList(Boolean.TRUE, Boolean.FALSE));
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
                // Setar no Model
            }

        });

        return dropDownChoice;
    }

    public DropDownChoice<EnumTipoArquivoEntidade> getDropDownTipo() {
        drop = new DropDownChoice<EnumTipoArquivoEntidade>("tipoArquivo", new PropertyModel<EnumTipoArquivoEntidade>(this, "tipoArquivo"), Arrays.asList(EnumTipoArquivoEntidade.values()));
        drop.setChoiceRenderer(new ChoiceRenderer<EnumTipoArquivoEntidade>("descricao", "valor"));
        drop.setNullValid(true);
        drop.setOutputMarkupId(true);

        return drop;
    }

    public FileUploadForm getFormUpload() {
        return new FileUploadForm("formUpload", new PropertyModel<List<FileUpload>>(this, "uploads"));
    }

    private InfraLocalDateTextField newDateTextFieldDataAnexo() {
        InfraLocalDateTextField field = componentFactory.newDateTextFieldWithDatePicker("data", "Data", false, new PropertyModel<LocalDate>(this, "dataAnexo"), "dd/MM/yyyy", "pt-BR");
        field.setEnabled(true);
        return field;
    }

    private AjaxSubmitLink getButtonAdicionarAnexo() {

        buttonAdicionar = new AjaxSubmitLink("btnAdicionarAnexo") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                adicionarAnexo(target);
            }
        };
        buttonAdicionar.setOutputMarkupId(true);
        return buttonAdicionar;
    }

    private DataView<EntidadeAnexo> getDataViewAnexos(String id) {

        return new DataView<EntidadeAnexo>(id, new EntityDataProvider<EntidadeAnexo>(listAnexoTemp)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<EntidadeAnexo> item) {
                item.add(new Label("descricao", item.getModelObject().getTipoArquivo().getDescricao()));
                item.add(new Label("nomeAnexo", item.getModelObject().getNomeAnexo()));
                item.add(new Label("tamanhoArquivoEmMB", item.getModelObject().getTamanhoArquivoEmMB()));
                item.add(new Label("dataAnexoTable", dataDocumentoBR(item.getModelObject().getDataDocumento())));
                item.add(new Label("dataCadastro", dataCadastroBR(item.getModelObject().getDataCadastro())));

                btnDownload = componentFactory.newButton("btnDonwload", () -> download(item));
                btnDownload.setEnabled(true);
                item.add(btnDownload);

                btnExcluir = componentFactory.newAJaxConfirmButton("btnExcluirAnexo", "MSG002", formUpload, (target, formz) -> excluirAnexo(target, item));

                item.add(btnExcluir);
            }
        };
    }

    // CRIAÇÃO DO FORMULÁRIO DE UPLOAD
    public class FileUploadForm extends Form<List<FileUpload>> {
        private static final long serialVersionUID = 1L;

        public FileUploadForm(String id, IModel<List<FileUpload>> model) {
            super(id, model);
            setMultiPart(true);

            fileUploadField = new FileUploadField("fileInput", model);
            fileUploadField.setVisible(true);
            fileUploadField.add(new UploadValidator());
            add(fileUploadField);

            AjaxSubmitLink btnAdicionarAnexo = getButtonAdicionarAnexo();
            btnAdicionarAnexo.setVisible(true);
            add(btnAdicionarAnexo);

        }

        /* Irá validar para não receber arquivos do tipo : .exe, .bat */
        private class UploadValidator implements IValidator<List<FileUpload>> {
            private static final long serialVersionUID = 1L;

            @Override
            public void validate(IValidatable<List<FileUpload>> validatable) {
                List<FileUpload> list = validatable.getValue();
                if (!list.isEmpty()) {
                    FileUpload fileUpload = list.get(0);
                    if (fileUpload.getSize()>Bytes.megabytes(Constants.LIMITE_MEGABYTES).bytes()) {
                        ValidationError error = new ValidationError("Arquivo para Download maior que " + Constants.LIMITE_MEGABYTES + "MB.");
                        validatable.error(error);
                    }
                    String extension = FilenameUtils.getExtension(fileUpload.getClientFileName());
                    if ("exe".equalsIgnoreCase(extension) || "bat".equalsIgnoreCase(extension)) {
                        ValidationError error = new ValidationError("Não são permitidos arquivos executáveis como .exe,.bat e etc.");
                        validatable.error(error);
                    }
                }
            }
        }

    }    
    /*
     * AS AÇÕES SERÃO IMPLEMENTADAS ABAIXO
     */

    private void adicionarAnexo(AjaxRequestTarget target) {

        if (!validar(target)) {
            return;
        }

        if (uploads != null) {
            if (!uploads.isEmpty()) {
                for (FileUpload component : uploads) {
                    EntidadeAnexo anexoTemp = new EntidadeAnexo();
                    anexoTemp.setNomeAnexo(component.getClientFileName());
                    anexoTemp.setConteudo(component.getBytes());
                    anexoTemp.setTipoArquivo(tipoArquivo);
                    anexoTemp.setDataDocumento(this.dataAnexo);
                    listAnexoTemp.add(anexoTemp);
                }
            }
        }
        this.dataAnexo = null;
        tipoArquivo = null;

        formUpload.addOrReplace(getDropDownTipo());
        formUpload.addOrReplace(newDateTextFieldDataAnexo());

        target.add(formUpload);
    }

    public boolean validar(AjaxRequestTarget target) {
        boolean validar = true;

        String msg = "";

        if (uploads == null) {
            msg += "<p><li> Selecione um arquivo para anexar.</li><p />";
            validar = false;
        }

        if (tipoArquivo == null) {
            msg += "<p><li> Informe o 'Tipo do Arquivo' a ser enviado.</li><p />";
            validar = false;
        }

        if (dataAnexo == null) {
            msg += "<p><li> O Campo 'Data' é obrigatório.</li><p />";
            validar = false;
        }

        if (uploads != null) {
            if (!uploads.isEmpty()) {
                for (FileUpload component : uploads) {

                    String extension = FilenameUtils.getExtension(component.getClientFileName());
                    if ("exe".equalsIgnoreCase(extension) || "bat".equalsIgnoreCase(extension)) {
                        msg += "<p><li> Não são permitidos arquivos executáveis como .exe,.bat e etc. </li></p>";
                        validar = false;
                    }
                }
            }
        }

        if (!validar) {
            mensagem.setObject(msg);
        } else {
            mensagem.setObject("");
        }
        target.add(labelMensagem);

        return validar;
    }

    public String dataDocumentoBR(LocalDate dataDocumento) {
        DateTimeFormatter sdfPadraoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (dataDocumento != null) {
            dataDocumento.format(sdfPadraoBR);
            return sdfPadraoBR.format(dataDocumento);
        }
        return " - ";
    }

    public String dataCadastroBR(LocalDateTime dataCadastro) {
        DateTimeFormatter sdfPadraoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        if (dataCadastro != null) {
            return sdfPadraoBR.format(dataCadastro);
        }
        return " - ";
    }

    private void excluirAnexo(AjaxRequestTarget target, Item<EntidadeAnexo> item) {
        EntidadeAnexo a = item.getModelObject();
        listAnexoTemp.remove(a);
        if (target != null) {
            mensagem.setObject("");
            target.add(labelMensagem);

            target.add(panelAnexoPessoa);
        }
    }

    private void download(Item<EntidadeAnexo> item) {
        EntidadeAnexo a = item.getModelObject();
        if (a.getId()!=null){
            AnexoDto retorno = anexoService.buscarPeloId(a.getId());
            SideUtil.download(retorno.getConteudo(), retorno.getNomeAnexo());
        }else{
            SideUtil.download(a.getConteudo(), a.getNomeAnexo());  
        }
    }

    public void actionDropDownTipoArquivo(DropDownChoice<EnumTipoArquivoEntidade> dropElemento) {
        dropElemento.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                tipoArquivo = drop.getModelObject();
            }
        });
    }

    public List<EntidadeAnexo> getListAnexoTemp() {
        return listAnexoTemp;
    }

    public void setListAnexoTemp(List<EntidadeAnexo> listAnexoTemp) {
        this.listAnexoTemp = listAnexoTemp;
    }

    private Pessoa setarValoresNaEntidade(Pessoa entidade) {
        nomePessoa = entidade.getNomePessoa();
        numeroCpf = entidade.getNumeroCpf();
        descricaoCargo = entidade.getDescricaoCargo();
        numeroTelefone = entidade.getNumeroTelefone();
        email = entidade.getEmail();
        dataInicioExercicio = entidade.getDataInicioExercicio();
        dataFimExercicio = entidade.getDataFimExercicio();
        enderecoCorrespondencia = entidade.getEnderecoCorrespondencia();
        ativo = entidade.getStatusPessoa() == EnumStatusPessoa.ATIVO ? true : false;

        return entidade;
    }

    private void verificarPessoaLogada() {
        usuarioLogado = getUsuarioLogadoDaSessao();
        List<PessoaEntidade> lista = new ArrayList<PessoaEntidade>();
        if (usuarioLogado != null) {
            lista = genericEntidadeService.buscarPessoaEntidadesDoUsuario(usuarioLogado);
            setarValoresNaEntidade(lista.get(0).getPessoa());
        }
    }

    public void actionVoltar() {
        setResponsePage(backPage);
    }

}
