package br.gov.mj.side.web.view.programa.inscricao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoAnexoElegibilidade;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaCriterioElegibilidade;
import br.gov.mj.side.web.dto.AnexoDto;
import br.gov.mj.side.web.service.InscricaoAnexoElegibilidadeService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.components.EntityDataProvider;

public class CriterioElegebilidadePanel extends Panel {

    private static final long serialVersionUID = 1L;
    private List<FileUpload> uploads = new ArrayList<FileUpload>();
    private InscricaoProgramaCriterioElegibilidade inscricaoProgramaCriterioElegibilidade;
    private WebMarkupContainer container;
    private WebMarkupContainer containerMessage;
    private Form<InscricaoPrograma> form;
    private ListView<String> feedbackList;
    private boolean readOnly;

    private String titulo;
    private List<String> messages = new ArrayList<String>();

    private PanelAnexosCriteriosElegibilidade panelAnexosCriteriosElegibilidade;

    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private InscricaoAnexoElegibilidadeService inscricaoAnexoElegibilidadeService;

    public CriterioElegebilidadePanel(String id, InscricaoProgramaCriterioElegibilidade inscricaoProgramaCriterioElegibilidade, String titulo, Form<InscricaoPrograma> form, boolean readOnly) {
        super(id);
        this.form = form;

        setReadOnly(readOnly);
        setTitulo(titulo);
        setInscricaoProgramaCriterioElegibilidade(inscricaoProgramaCriterioElegibilidade);
        initComponents();
    }

    protected void initComponents() {
        feedbackList = newListViewMessage();
        feedbackList.setOutputMarkupId(true);
        containerMessage = new WebMarkupContainer("containerMessage");
        containerMessage.add(feedbackList);
        add(containerMessage);

        add(newLabelTitulo());
        add(newLabelNomeCriterio());
        add(newLabelDescricaoCriterio());
        add(newLabelFormaVerificacao());

        add(newDropDownAtendeCriterio());

        add(panelAnexosCriteriosElegibilidade = new PanelAnexosCriteriosElegibilidade("panelAnexosCriteriosElegibilidade"));

    }

    private Label newLabelAnexoObrigatorio() {
        Label lblAnexoObrigatorio = componentFactory.newLabel("possuiObrigatoriedadeDeAnexo", inscricaoProgramaCriterioElegibilidade.getProgramaCriterioElegibilidade().getPossuiObrigatoriedadeDeAnexo() ? " (Obrigatório)" : " (Opcional)");
        lblAnexoObrigatorio.setOutputMarkupId(Boolean.TRUE);
        return lblAnexoObrigatorio;
    }

    private DropDownChoice<Boolean> newDropDownAtendeCriterio() {
        DropDownChoice<Boolean> dropDownChoice = new DropDownChoice<Boolean>("atendeCriterioElegibilidade", Arrays.asList(Boolean.TRUE, Boolean.FALSE));
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
        dropDownChoice.setEnabled(!isReadOnly());
        return dropDownChoice;
    }

    private Label newLabelTitulo() {
        return new Label("titulo", getTitulo());
    }

    private DataView<InscricaoAnexoElegibilidade> newDataViewInscricaoAnexoElegebilidade() {
        return new DataView<InscricaoAnexoElegibilidade>("anexos", new EntityDataProvider<InscricaoAnexoElegibilidade>(getInscricaoProgramaCriterioElegibilidade().getAnexos())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<InscricaoAnexoElegibilidade> item) {
                item.add(new Label("nomeAnexo"));

                Button btnDownload = componentFactory.newButton("btnDonwload", () -> download(item));
                item.add(btnDownload);

                @SuppressWarnings("unchecked")
                InfraAjaxConfirmButton btnExcluir = componentFactory.newAJaxConfirmButton("btnExcluir", "MSG002", form, (target, formz) -> excluirAtributo(target, (Form<InscricaoPrograma>) formz, item));
                btnExcluir.setVisible(!isReadOnly());
                item.add(btnExcluir);
            }

        };
    }

    protected void excluirAtributo(AjaxRequestTarget target, Form<InscricaoPrograma> formz, Item<InscricaoAnexoElegibilidade> item) {
        InscricaoAnexoElegibilidade anexo = item.getModelObject();
        getInscricaoProgramaCriterioElegibilidade().getAnexos().remove(anexo);
        target.add(container);
    }

    private void download(Item<InscricaoAnexoElegibilidade> item) {
        InscricaoAnexoElegibilidade anexo = item.getModelObject();
        if (anexo.getId() != null) {
            AnexoDto retorno = inscricaoAnexoElegibilidadeService.buscarPeloId(anexo.getId());
            SideUtil.download(retorno.getConteudo(), retorno.getNomeAnexo());
        } else {
            SideUtil.download(anexo.getConteudo(), anexo.getNomeAnexo());
        }
    }

    private Label newLabelNomeCriterio() {
        return new Label("programaCriterioElegibilidade.nomeCriterioElegibilidade");
    }

    private Label newLabelDescricaoCriterio() {
        return new Label("programaCriterioElegibilidade.descricaoCriterioElegibilidade");
    }

    private Label newLabelFormaVerificacao() {
        return new Label("programaCriterioElegibilidade.formaVerificacao");
    }

    private FileUploadForm newFormAnexo() {
        return new FileUploadForm("anexoForm", new LambdaModel<List<FileUpload>>(this::getUploads, this::setUploads));
    }

    public List<FileUpload> getUploads() {
        return uploads;
    }

    public void setUploads(List<FileUpload> uploads) {
        this.uploads = uploads;
    }

    private class FileUploadForm extends Form<List<FileUpload>> {
        private static final long serialVersionUID = 1L;

        public FileUploadForm(String id, IModel<List<FileUpload>> model) {
            super(id, model);
            setMultiPart(true);

            FileUploadField fileUploadField = new FileUploadField("fileInput", model);
            fileUploadField.add(new UploadValidator());
            add(fileUploadField);

            AjaxSubmitLink btnAdicionarAnexo = getButtonAdicionarAnexo();
            add(btnAdicionarAnexo);

            setVisible(!isReadOnly());

        }

        /* Irá validar para não receber arquivos do tipo : .exe, .bat */
        private class UploadValidator implements IValidator<List<FileUpload>> {
            private static final long serialVersionUID = 1L;

            @Override
            public void validate(IValidatable<List<FileUpload>> validatable) {
                List<FileUpload> list = validatable.getValue();
                if (!list.isEmpty()) {
                    FileUpload fileUpload = list.get(0);
                    if (fileUpload.getSize() > Bytes.megabytes(Constants.LIMITE_MEGABYTES).bytes()) {
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

    private AjaxSubmitLink getButtonAdicionarAnexo() {

        return new AjaxSubmitLink("btnAdicionarAnexo") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                messages.clear();
                adicionarAnexo(target);
            }
        };

    }

    private void adicionarAnexo(AjaxRequestTarget target) {
        try {
            if (!uploads.isEmpty()) {
                for (FileUpload component : uploads) {
                    InscricaoAnexoElegibilidade anexo = new InscricaoAnexoElegibilidade();
                    anexo.setNomeAnexo(component.getClientFileName());
                    anexo.setConteudo(component.getBytes());
                    getInscricaoProgramaCriterioElegibilidade().getAnexos().add(anexo);
                }
            }
        } catch (NullPointerException e) {
            messages.add("Adicione um arquivo a ser anexado.");
        }
        target.add(container);
        target.add(containerMessage);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    private ListView<String> newListViewMessage() {
        return new ListView<String>("alerts", messages) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<String> item) {
                item.add(new Label("alert", item.getModelObject()));
            }
        };
    }

    public InscricaoProgramaCriterioElegibilidade getInscricaoProgramaCriterioElegibilidade() {
        return inscricaoProgramaCriterioElegibilidade;
    }

    public void setInscricaoProgramaCriterioElegibilidade(InscricaoProgramaCriterioElegibilidade inscricaoProgramaCriterioElegibilidade) {
        this.inscricaoProgramaCriterioElegibilidade = inscricaoProgramaCriterioElegibilidade;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    // panel

    private class PanelAnexosCriteriosElegibilidade extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelAnexosCriteriosElegibilidade(String id) {
            super(id);
            setOutputMarkupId(true);

            container = new WebMarkupContainer("container");
            container.add(newLabelAnexoObrigatorio());
            container.add(newFormAnexo());
            container.add(newDataViewInscricaoAnexoElegebilidade());
            add(container);

            // setVisible(!getInscricaoProgramaCriterioElegibilidade().getAnexos().isEmpty());
        }

    }

}
