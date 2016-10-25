package br.gov.mj.side.web.view.contrato.painel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraLocalDateTextField;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.side.entidades.enums.EnumTipoArquivoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ContratoAnexo;
import br.gov.mj.side.web.dto.AnexoDto;
import br.gov.mj.side.web.service.ContratoAnexoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.DataUtil;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.components.EntityDataProvider;

@AuthorizeInstantiation({ PanelDadosBasicosContrato.ROLE_MANTER_CONTRATO_VISUALIZAR, PanelDadosBasicosContrato.ROLE_MANTER_CONTRATO_INCLUIR, PanelDadosBasicosContrato.ROLE_MANTER_CONTRATO_ALTERAR, PanelDadosBasicosContrato.ROLE_MANTER_CONTRATO_EXCLUIR })
public class PanelContratoAnexos extends Panel {
    private static final long serialVersionUID = 1L;

    public static final String ROLE_MANTER_CONTRATO_VISUALIZAR = "manter_contrato:visualizar";
    public static final String ROLE_MANTER_CONTRATO_INCLUIR = "manter_contrato:incluir";
    public static final String ROLE_MANTER_CONTRATO_ALTERAR = "manter_contrato:alterar";
    public static final String ROLE_MANTER_CONTRATO_EXCLUIR = "manter_contrato:excluir";

    private static final String ONCHANGE = "onchange";

    private Page backPage;

    private PanelPrincipalAnexo panelPrincipalAnexo;

    private Button btnDownload;
    private InfraAjaxConfirmButton btnExcluir;
    private AjaxSubmitLink btnAdicionar;
    private FileUploadField fileUploadField;
    private Integer itensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;

    private FileUploadForm formUpload;
    private DropDownChoice<EnumTipoArquivoContrato> drop;
    private EnumTipoArquivoContrato tipoArquivoContrato;
    private LocalDate dataAnexo;
    private Contrato contrato;
    private boolean readOnly;

    private List<FileUpload> uploads;
    private List<ContratoAnexo> listAnexoTemp = new ArrayList<ContratoAnexo>();
    private Label labelMensagem;
    private Model<String> mensagem = Model.of("");

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private ContratoAnexoService contratoAnexoService;

    public PanelContratoAnexos(String id, Contrato contrato, boolean readOnly) {
        super(id);
        setOutputMarkupId(true);
        this.contrato = contrato;
        this.readOnly = readOnly;

        initVariaveis();
        initComponents();
    }

    private class PanelPrincipalAnexo extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelPrincipalAnexo(String id) {
            super(id);

            formUpload = getFormUpload();
            formUpload.setMultiPart(true);
            add(formUpload);

            DataView<ContratoAnexo> dataView = getDataViewAnexos("anexos");

            formUpload.add(dataView); // anexos
            formUpload.add(new InfraAjaxPagingNavigator("pagination", dataView));
            formUpload.add(getDropDownTipo()); // tipoArquivo
            formUpload.add(getDateTextFieldDataAnexo());
            add(labelMensagem);

        }
    }

    public void initVariaveis() {
        listAnexoTemp = new ArrayList<ContratoAnexo>();

        if (contrato != null && contrato.getId() != null) {
            listAnexoTemp = SideUtil.convertAnexoDtoToEntityContratoAnexo(contratoAnexoService.buscarPeloIdContrato(contrato.getId()));
        }

        if (uploads == null) {
            uploads = new ArrayList<FileUpload>();
        }

        labelMensagem = new Label("mensagemAnexo", mensagem);
        labelMensagem.setEscapeModelStrings(false);
        labelMensagem.setOutputMarkupId(true);
    }

    private void initComponents() {
        add(panelPrincipalAnexo = new PanelPrincipalAnexo("panelInformacoesAnexo"));
    }

    // ==============================
    // CRIAÇÃO DOS COMPONENTES

    public DropDownChoice<EnumTipoArquivoContrato> getDropDownTipo() {
        drop = new DropDownChoice<EnumTipoArquivoContrato>("tipoArquivoContrato", new PropertyModel<EnumTipoArquivoContrato>(this, "tipoArquivoContrato"), Arrays.asList(EnumTipoArquivoContrato.values()));
        drop.setChoiceRenderer(new ChoiceRenderer<EnumTipoArquivoContrato>("descricao", "valor"));
        actionDropDownTipoArquivo(drop);
        drop.setEnabled(ativarComponente());
        drop.setNullValid(true);
        drop.setOutputMarkupId(true);
        return drop;
    }

    private InfraLocalDateTextField getDateTextFieldDataAnexo() {
        InfraLocalDateTextField field = componentFactory.newDateTextFieldWithDatePicker("data", "Data", false, new PropertyModel<LocalDate>(this, "dataAnexo"), "dd/MM/yyyy", "pt-BR");
        field.setEnabled(ativarComponente());
        return field;
    }

    public FileUploadForm getFormUpload() {
        FileUploadForm formUpload = new FileUploadForm("formUpload", new PropertyModel<List<FileUpload>>(this, "uploads"));
        return formUpload;
    }

    private AjaxSubmitLink getButtonAdicionarAnexo() {

        btnAdicionar = new AjaxSubmitLink("btnAdicionarAnexo") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                adicionarAnexo(target);
            }
        };
        btnAdicionar.setOutputMarkupId(true);
        btnAdicionar.setEnabled(ativarComponente());
        return btnAdicionar;
    }

    private DataView<ContratoAnexo> getDataViewAnexos(String id) {
        DataView<ContratoAnexo> dataView = new DataView<ContratoAnexo>(id, new EntityDataProvider<ContratoAnexo>(listAnexoTemp)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ContratoAnexo> item) {
                item.add(new Label("descricao", item.getModelObject().getTipoArquivoContrato().getDescricao()));
                item.add(new Label("nomeAnexo", item.getModelObject().getNomeAnexo()));
                item.add(new Label("tamanhoArquivoEmMB", item.getModelObject().getTamanhoArquivoEmMB()));
                item.add(new Label("dataAnexoTable", DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataDocumento(), "dd/MM/yyyy")));
                item.add(new Label("dataCadastro", DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataCadastro(), "dd/MM/yyyy HH:mm:ss")));

                btnDownload = componentFactory.newButton("btnDonwload", () -> download(item));
                btnDownload.setEnabled(true);
                item.add(btnDownload);

                btnExcluir = componentFactory.newAJaxConfirmButton("btnExcluirAnexo", "MSG002", formUpload, (target, formz) -> excluirAnexo(target, item));
                btnExcluir.setEnabled(ativarComponente());

                item.add(btnExcluir);
            }
        };
        dataView.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataView;
    }

    // CRIAÇÃO DO FORMULÁRIO DE UPLOAD
    public class FileUploadForm extends Form<List<FileUpload>> {
        private static final long serialVersionUID = 1L;

        public FileUploadForm(String id, IModel<List<FileUpload>> model) {
            super(id, model);
            setMultiPart(true);

            fileUploadField = new FileUploadField("fileInput", model);
            fileUploadField.setVisible(ativarComponente());
            fileUploadField.add(new UploadValidator());
            add(fileUploadField);

            AjaxSubmitLink btnAdicionarAnexo = getButtonAdicionarAnexo();
            btnAdicionarAnexo.setVisible(true);
            add(btnAdicionarAnexo);

        }

        // validar para não receber arquivos do tipo : .exe, .bat */
        private class UploadValidator implements IValidator<List<FileUpload>> {
            private static final long serialVersionUID = 1L;

            @Override
            public void validate(IValidatable<List<FileUpload>> validatable) {
                List<FileUpload> list = validatable.getValue();
                if (!list.isEmpty()) {
                    FileUpload fileUpload = list.get(0);
                    String extension = FilenameUtils.getExtension(fileUpload.getClientFileName());
                    if (fileUpload.getSize()>Bytes.megabytes(Constants.LIMITE_MEGABYTES).bytes()) {
                        ValidationError error = new ValidationError("Arquivo para Download maior que " + Constants.LIMITE_MEGABYTES + "MB.");
                        validatable.error(error);
                    }
                    if ("exe".equalsIgnoreCase(extension) || "bat".equalsIgnoreCase(extension)) {
                        ValidationError error = new ValidationError("Não são permitidos arquivos executáveis como .exe,.bat e etc.");
                        validatable.error(error);
                    }
                }
            }
        }

    }

    // ====================================
    // AS AÇÕES SÃO IMPLEMENTADAS ABAIXO

    private boolean ativarComponente() {
        if (readOnly) {
            return false;
        } else {
            return true;
        }
    }

    private void adicionarAnexo(AjaxRequestTarget target) {

        if (!validar(target)) {
            return;
        }

        if (uploads != null) {
            if (!uploads.isEmpty()) {
                for (FileUpload component : uploads) {
                    ContratoAnexo anexoTemp = new ContratoAnexo();
                    anexoTemp.setTipoArquivoContrato(tipoArquivoContrato);
                    anexoTemp.setNomeAnexo(component.getClientFileName());
                    anexoTemp.setConteudo(component.getBytes());
                    anexoTemp.setDataDocumento(this.dataAnexo);
                    anexoTemp.setDataCadastro(LocalDateTime.now());
                    listAnexoTemp.add(anexoTemp);
                }
            }
        }
        this.dataAnexo = null;
        tipoArquivoContrato = null;

        formUpload.addOrReplace(getDropDownTipo());
        formUpload.addOrReplace(getDateTextFieldDataAnexo());

        target.appendJavaScript("atualizarDropDownTipoArquivo();");
        target.add(formUpload);
    }

    public boolean validar(AjaxRequestTarget target) {
        boolean validar = true;

        String msg = "";

        if (uploads == null) {
            msg += "<p><li> Selecione um arquivo para anexar.</li><p />";
            validar = false;
        }

        if (tipoArquivoContrato == null) {
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

    private void excluirAnexo(AjaxRequestTarget target, Item<ContratoAnexo> item) {
        ContratoAnexo a = item.getModelObject();
        listAnexoTemp.remove(a);
        if (target != null) {
            mensagem.setObject("");

            target.appendJavaScript("atualizarDropDownTipoArquivo();");
            target.add(labelMensagem);
            target.add(panelPrincipalAnexo);
        }
    }

    private void download(Item<ContratoAnexo> item) {
        ContratoAnexo a = item.getModelObject();
        if (a.getId() != null) {
            AnexoDto retorno = contratoAnexoService.buscarPeloId(a.getId());
            SideUtil.download(retorno.getConteudo(), retorno.getNomeAnexo());
        } else {
            SideUtil.download(a.getConteudo(), a.getNomeAnexo());
        }

    }

    public void actionDropDownTipoArquivo(DropDownChoice<EnumTipoArquivoContrato> dropElemento) {
        dropElemento.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                tipoArquivoContrato = drop.getModelObject();
            }
        });
    }

    // GETTERS E SETTERS

    public List<ContratoAnexo> getListAnexoTemp() {
        return listAnexoTemp;
    }

    public void setListAnexoTemp(List<ContratoAnexo> listAnexoTemp) {
        this.listAnexoTemp = listAnexoTemp;
    }

    public EnumTipoArquivoContrato getTipoArquivoContrato() {
        return tipoArquivoContrato;
    }

    public void setTipoArquivoContrato(EnumTipoArquivoContrato tipoArquivoContrato) {
        this.tipoArquivoContrato = tipoArquivoContrato;
    }

    public PanelPrincipalAnexo getPanelPrincipalAnexo() {
        return panelPrincipalAnexo;
    }

    public void setPanelPrincipalAnexo(PanelPrincipalAnexo panelPrincipalAnexo) {
        this.panelPrincipalAnexo = panelPrincipalAnexo;
    }

    public Button getBtnDownload() {
        return btnDownload;
    }

    public void setBtnDownload(Button btnDownload) {
        this.btnDownload = btnDownload;
    }

    public InfraAjaxConfirmButton getBtnExcluir() {
        return btnExcluir;
    }

    public void setBtnExcluir(InfraAjaxConfirmButton btnExcluir) {
        this.btnExcluir = btnExcluir;
    }

    public DropDownChoice<EnumTipoArquivoContrato> getDrop() {
        return drop;
    }

    public void setDrop(DropDownChoice<EnumTipoArquivoContrato> drop) {
        this.drop = drop;
    }

    public AjaxSubmitLink getBtnAdicionar() {
        return btnAdicionar;
    }

    public void setBtnAdicionar(AjaxSubmitLink btnAdicionar) {
        this.btnAdicionar = btnAdicionar;
    }

    public void setFormUpload(FileUploadForm formUpload) {
        this.formUpload = formUpload;
    }

    public FileUploadField getFileUploadField() {
        return fileUploadField;
    }

    public void setFileUploadField(FileUploadField fileUploadField) {
        this.fileUploadField = fileUploadField;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Integer getItensPorPagina() {
        return itensPorPagina;
    }

    public void setItensPorPagina(Integer itensPorPagina) {
        this.itensPorPagina = itensPorPagina;
    }

}
