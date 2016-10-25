package br.gov.mj.side.web.view.bem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.RangeValidator;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.apoio.entidades.Elemento;
import br.gov.mj.apoio.entidades.SubElemento;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.converter.MoneyBigDecimalConverter;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.AnexoBem;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.HistoricoBem;
import br.gov.mj.side.entidades.TagBem;
import br.gov.mj.side.entidades.enums.EnumMes;
import br.gov.mj.side.web.dto.AnexoDto;
import br.gov.mj.side.web.service.AnexoBemService;
import br.gov.mj.side.web.service.BemService;
import br.gov.mj.side.web.service.ElementoService;
import br.gov.mj.side.web.service.HistoricoBemService;
import br.gov.mj.side.web.service.SubElementoService;
import br.gov.mj.side.web.service.TagBemService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.components.EntityDataProvider;
import br.gov.mj.side.web.view.template.TemplatePage;

@AuthorizeInstantiation({ BemPage.ROLE_MANTER_BEM_INCLUIR, BemPage.ROLE_MANTER_BEM_ALTERAR, BemPage.ROLE_MANTER_BEM_EXCLUIR, BemPage.ROLE_MANTER_BEM_VISUALIZAR })
public class BemPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    public static final String ROLE_MANTER_BEM_INCLUIR = "manter_bem:incluir";
    public static final String ROLE_MANTER_BEM_ALTERAR = "manter_bem:alterar";
    public static final String ROLE_MANTER_BEM_EXCLUIR = "manter_bem:excluir";
    public static final String ROLE_MANTER_BEM_VISUALIZAR = "manter_bem:visualizar";

    private Form<Bem> form;
    private PanelBem panelBem;
    private PanelSubelemento panelSubelemento;
    private PanelAtributos panelAtributos;
    private PanelAnexos panelAnexos;
    private InfraAjaxFallbackLink<Void> btnNovo;

    private InfraDropDownChoice<SubElemento> dropSubElemento;
    private Bem entity; // model
    private AnexoBem anexo = new AnexoBem();
    private TagBem tagBem = new TagBem();
    private Elemento elemento = new Elemento();
    private List<FileUpload> uploads = new ArrayList<FileUpload>();
    private Page backPage;
    private boolean readOnly;
    private boolean atalhoNovoBem;
    private EnumMes mesSelecionado;
    private Integer anoSelecionado;

    private Label labelMensagem;
    private Model<String> mensagem = Model.of("");

    @Inject
    private BemService bemService;
    @Inject
    private ElementoService elementoService;
    @Inject
    private SubElementoService subElementoService;
    @Inject
    private AnexoBemService anexoService;
    @Inject
    private TagBemService tagBemService;
    @Inject
    private HistoricoBemService historicoBemService;
    @Inject
    private ComponentFactory componentFactory;

    public BemPage(PageParameters pageParameters, Page backPage) {
        super(pageParameters);
        this.backPage = backPage;
    }

    public BemPage(PageParameters pageParameters, Bem entidade, Page backPage, boolean readOnly, boolean atalhoNovoBem) {
        this(pageParameters, backPage);
        if (entidade != null && entidade.getId() != null) {
            setTitulo("Alterar Bem");
            Bem entityLocal = bemService.buscarPeloId(entidade.getId());
            setEntity(entityLocal);
            List<AnexoDto> list = anexoService.buscarPeloIdBem(entityLocal.getId());
            List<HistoricoBem> listHistorico = historicoBemService.buscarPeloIdBem(entityLocal.getId(), EnumOrder.DESC, "id");
            List<TagBem> listaTagBem = tagBemService.buscarPeloIdBem(entityLocal.getId(), EnumOrder.DESC, "id");
            getEntity().setAnexos(SideUtil.convertAnexoDtoToEntityAnexoBem(list));
            getEntity().setHistoricoBemValores(listHistorico);
            getEntity().setTags(listaTagBem);
            carregarAnoMes();
            elemento = getEntity().getSubElemento().getElemento();
        } else {
            setTitulo("Cadastrar Bem");
            setEntity(entidade);
            getEntity().setAnexos(new ArrayList<AnexoBem>());
            getEntity().setHistoricoBemValores(new ArrayList<HistoricoBem>());
        }
        setReadOnly(readOnly);
        setAtalhoNovoBem(atalhoNovoBem);
        if (readOnly) {
            setTitulo("Visualizar Bem");
        }
        initComponents();
    }

    private void initComponents() {

        form = componentFactory.newForm("form", new CompoundPropertyModel<Bem>(entity));

        panelBem = new PanelBem("panelBem");
        panelBem.setEnabled(!isReadOnly());
        panelAtributos = new PanelAtributos("panelAtributos");
        panelAtributos.setOutputMarkupId(true);
        panelAtributos.setEnabled(!isReadOnly());
        panelAnexos = new PanelAnexos("panelAnexos");

        btnNovo = getButtonNovo();
        authorize(btnNovo, RENDER, BemPesquisaPage.ROLE_MANTER_BEM_INCLUIR);
        btnNovo.setOutputMarkupId(true);
        btnNovo.setVisible(isAtalhoNovoBem());
        // Breadcrump itens
        form.add(componentFactory.newLink("lnkDashboard", HomePage.class));
        form.add(componentFactory.newLink("lnkBemPesquisaPage", BemPesquisaPage.class));
        form.add(new Label("lblNomePage", getTitulo()));
        form.add(btnNovo);
        form.add(panelBem);
        form.add(panelAtributos);
        form.add(panelAnexos);
        form.add(newDataViewHistóricoValorBem());
        form.add(getButtonConfirmar());
        form.add(getButtonVoltar());

        add(form);
    }

    private void carregarAnoMes() {
        LocalDate data = entity.getDataEstimativa();
        if (data != null) {
            setAnoSelecionado(data.getYear());
            setMesSelecionado(EnumMes.getBuscarPorCodigo(data.getMonthValue()));
        }
    }

    private TextField<BigDecimal> getTextFieldValorEstimado() {
        TextField<BigDecimal> field = new TextField<BigDecimal>("valorEstimadoBem") {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
        field.setLabel(Model.of("Valor estimado"));
        field.setRequired(true);
        field.add(RangeValidator.range(new BigDecimal("0.00"), new BigDecimal("99999999.00")));
        return field;
    }

    private TextArea<String> getTextAreaDescricao() {
        TextArea<String> textArea = new TextArea<String>("descricaoBem");
        textArea.setLabel(Model.of("Descrição"));
        textArea.setRequired(true);
        textArea.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        return textArea;
    }

    private TextField<String> getTextFieldNome() {
        TextField<String> text = componentFactory.newTextField("nomeBem", "Nome", true, null, StringValidator.maximumLength(200));
        actionTextNome(text);
        return text;
    }

    private TextField<String> getTextFieldCatMat() {
        TextField<String> text = componentFactory.newTextField("nomeCatmat", "CATMAT", false, null, StringValidator.maximumLength(10));
        text.setOutputMarkupId(true);
        return text;
    }

    private InfraDropDownChoice<SubElemento> getDropDownChoiceSubelemento() {
        InfraDropDownChoice<SubElemento> dropDownChoice = componentFactory.newDropDownChoice("subElemento", "Subelemento", true, "id", "nomeECodigo", null, listaSubelementos(), null);
        dropDownChoice.setNullValid(true);
        return dropDownChoice;
    }

    private DropDownChoice<Elemento> getDropDownChoiceElemento() {
        InfraDropDownChoice<Elemento> dropDownChoice = componentFactory.newDropDownChoice("elemento", "Elemento", true, "id", "nomeECodigo", new LambdaModel<Elemento>(this::getElemento, this::setElemento), listaElementos(), (target) -> atualizarListaSubelementos(target));
        dropDownChoice.setNullValid(true);
        return dropDownChoice;
    }

    void atualizarListaSubelementos(AjaxRequestTarget target) {
        dropSubElemento.setChoices(listaSubelementos());
        target.appendJavaScript("atualizaCssDropDown();");
        target.add(panelSubelemento);
    }

    private class PanelAtributos extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelAtributos(String id) {
            super(id);

            labelMensagem = new Label("mensagem", mensagem);
            labelMensagem.setEscapeModelStrings(false);
            labelMensagem.setOutputMarkupId(true);

            add(labelMensagem);
            add(getFormAtributos());
            add(getDataViewAtributos("atributos"));
        }

        private DataView<TagBem> getDataViewAtributos(String id) {

            return new DataView<TagBem>(id, new EntityDataProvider<TagBem>(form.getModelObject().getTags())) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void populateItem(Item<TagBem> item) {
                    item.add(new Label("nomeTag", item.getModelObject().getNomeTag()));
                    item.add(new Label("valorTag", item.getModelObject().getValorTag()));

                    @SuppressWarnings("unchecked")
                    InfraAjaxConfirmButton btnExcluir = componentFactory.newAJaxConfirmButton("btnExcluirAtributo", "MSG001", form, (target, formz) -> excluirAtributo(target, (Form<Bem>) formz, item));
                    item.add(btnExcluir);
                }
            };

        }

        private void excluirAtributo(AjaxRequestTarget target, Form<Bem> form, Item<TagBem> item) {
            TagBem t = item.getModelObject();
            form.getModelObject().getTags().remove(t);
            if (target != null) {
                target.add(form);
            }
            
            AttributeModifier atribute = new AttributeModifier("class", "col-sm-6 feedbackPanelINFO");
            String msg = "<p><li> Atributo removido.</li><p />";
            
            mensagem.setObject(msg);
            labelMensagem.add(atribute);
            target.add(labelMensagem);
        }
    }

    private Form<Void> getFormAtributos() {

        Form<Void> formAtributo = new Form<Void>("formAtributo");
        formAtributo.add(componentFactory.newLabel("msgInformativa", getString("MSG003")));
        formAtributo.add(getTextFieldNomeAtributo());
        formAtributo.add(getTextFieldValorAtributo());

        AjaxSubmitLink btnAdicionar = getButtonAdicionarAtributo();
        authorize(btnAdicionar, RENDER, BemPage.ROLE_MANTER_BEM_INCLUIR);
        formAtributo.add(btnAdicionar);

        formAtributo.setVisible(!isReadOnly());
        return formAtributo;
    }

    private TextField<TagBem> getTextFieldNomeAtributo() {
        TextField<TagBem> field = new TextField<TagBem>("tagBem.nomeTag", new PropertyModel<TagBem>(this, "tagBem.nomeTag"));
        field.add(StringValidator.maximumLength(50));
        return field;
    }

    private TextField<TagBem> getTextFieldValorAtributo() {
        TextField<TagBem> field = new TextField<TagBem>("tagBem.valorTag", new PropertyModel<TagBem>(this, "tagBem.valorTag"));
        field.add(StringValidator.maximumLength(50));
        return field;
    }

    private AjaxSubmitLink getButtonAdicionarAtributo() {
        return new AjaxSubmitLink("btnAdicionarAtributo") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                adicionarAtributo(target);
            }
        };
    }

    private void adicionarAtributo(AjaxRequestTarget target) {

        String msg = "";
        String atributoAModificar = "";
        
        if (tagBem.getNomeTag() == null) {            
            msg += "<p><li> Para adicionar é necessário informar o 'Nome' do atributo.</li><p />";
            atributoAModificar="col-sm-6 feedbackPanelERROR";
            
        } else if (tagBem.getValorTag() == null) {            
            msg += "<p><li> Para adicionar é necessário informar o 'Valor' do atributo.</li><p />";
            atributoAModificar="col-sm-6 feedbackPanelERROR";
            
        } else {
            boolean existe = false;
            List<TagBem> tags = form.getModelObject().getTags();

            for (TagBem t : tags) {
                if (t.getNomeTag().equalsIgnoreCase(tagBem.getNomeTag())) {
                    existe = true;
                }
            }
            if (!existe) {
                form.getModelObject().getTags().add(tagBem);
                msg += "<p><li> Atributo '" + tagBem.getNomeTag() + "' adicionado com sucesso.</li><p />";
                atributoAModificar="col-sm-6 feedbackPanelINFO";
                
                //addMsgInfo("Atributo : " + tagBem.getNomeTag() + " adicionado com sucesso.");
                tagBem = new TagBem();
                target.add(panelAtributos);
            } else {
                
                atributoAModificar="col-sm-6 feedbackPanelERROR";
                msg += "<p><li> Já existe Atributo com o nome '" + tagBem.getNomeTag() + "'.</li><p />";
            }
        }
        
        AttributeModifier atribute = new AttributeModifier("class", atributoAModificar);
        
        mensagem.setObject(msg);
        labelMensagem.add(atribute);
        target.add(labelMensagem);

    }

    private class PanelBem extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBem(String id) {
            super(id);

            panelSubelemento = new PanelSubelemento("panelSubelemento");

            add(getTextFieldNome());
            add(getTextFieldCatMat());
            add(panelSubelemento);
            add(getTextAreaDescricao());
            add(getTextFieldValorEstimado());
            add(newDropDownMes());
            add(newDropDownAno());
        }

    }

    private class PanelSubelemento extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelSubelemento(String id) {
            super(id);

            dropSubElemento = getDropDownChoiceSubelemento();

            add(dropSubElemento);
            add(getDropDownChoiceElemento());
        }
    }

    private class PanelAnexos extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelAnexos(String id) {
            super(id);
            FileUploadForm formUpload = getFormUpload();
            formUpload.add(getDataViewAnexos("anexos"));
            add(formUpload);
        }

        private DataView<AnexoBem> getDataViewAnexos(String id) {

            return new DataView<AnexoBem>(id, new EntityDataProvider<AnexoBem>(form.getModelObject().getAnexos())) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void populateItem(Item<AnexoBem> item) {
                    item.add(new Label("nomeAnexo"));
                    item.add(new Label("tamanhoArquivoEmMB"));

                    Button btnDownload = componentFactory.newButton("btnDonwload", () -> download(item));
                    item.add(btnDownload);

                    @SuppressWarnings("unchecked")
                    InfraAjaxConfirmButton btnExcluir = componentFactory.newAJaxConfirmButton("btnExcluirAnexo", "MSG002", form, (target, formz) -> excluirAnexo(target, (Form<Bem>) formz, item));
                    btnExcluir.setEnabled(!isReadOnly());
                    item.add(btnExcluir);

                }
            };
        }

        private void excluirAnexo(AjaxRequestTarget target, Form<Bem> form, Item<AnexoBem> item) {
            AnexoBem a = item.getModelObject();
            form.getModelObject().getAnexos().remove(a);
            addMsgInfo("Atributo removido.");
            if (target != null) {
                target.add(form);
            }
        }
    }

    private AjaxSubmitLink getButtonAdicionarAnexo() {

        return new AjaxSubmitLink("btnAdicionarAnexo") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                adicionarAnexo(target);
                target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
            }
        };

    }

    private void adicionarAnexo(AjaxRequestTarget target) {
        if (uploads != null) {
            if (!uploads.isEmpty()) {
                for (FileUpload component : uploads) {
                    AnexoBem anexoTemp = new AnexoBem();
                    anexoTemp.setNomeAnexo(component.getClientFileName());
                    anexoTemp.setConteudo(component.getBytes());
                    form.getModelObject().getAnexos().add(anexoTemp);
                }
                addMsgInfo("Arquivo adicionado com sucesso");
            }
        } else {
            addMsgError("Selecione um arquivo para anexar.");

        }
        target.add(panelAnexos);
    }

    private FileUploadForm getFormUpload() {
        return new FileUploadForm("formUpload", new PropertyModel<List<FileUpload>>(this, "uploads"));
    }

    private Button getButtonConfirmar() {
        Button buttonConsultar = componentFactory.newButton("btnConfirmar", () -> incluirAlterar());
        buttonConsultar.setVisible(!isReadOnly());
        return buttonConsultar;
    }

    private void incluirAlterar() {

        if (!getSideSession().hasAnyRole(new String[] { BemPage.ROLE_MANTER_BEM_INCLUIR, BemPage.ROLE_MANTER_BEM_ALTERAR })) {
            throw new SecurityException();
        }

        Bem bem = form.getModelObject();
        boolean insert = bem.getId() == null ? true : false;

        if (getAnoSelecionado() != null && getMesSelecionado() != null) {
            bem.setDataEstimativa(LocalDate.parse(getAnoSelecionado() + "-" + getMesSelecionado().getCodigoTexto() + "-01"));
        }

        bem = bemService.incluirAlterar(bem);
        if (insert) {
            getSession().info("Cadastrado com sucesso");
            setResponsePage(new BemPage(null, bem, backPage, false, true));
        } else {
            getSession().info("Alterado com sucesso.");
            setResponsePage(new BemPage(null, bem, backPage, false, false));
        }
    }

    private Button getButtonVoltar() {
        Button buttonVoltar = componentFactory.newButton("btnVoltar", () -> voltar());
        buttonVoltar.setDefaultFormProcessing(false);
        return buttonVoltar;
    }

    private void voltar() {
        setResponsePage(backPage);
    }

    private InfraAjaxFallbackLink<Void> getButtonNovo() {
        return componentFactory.newAjaxFallbackLink("btnAdicionarNovo", (target) -> adicionarNovo());
    }

    private void adicionarNovo() {
        setResponsePage(new BemPage(null, new Bem(), this, false, false));
    }

    public AnexoBem getAnexo() {
        return anexo;
    }

    public void setAnexo(AnexoBem anexo) {
        this.anexo = anexo;
    }

    public TagBem getTagBem() {
        return tagBem;
    }

    public void setTagBem(TagBem tagBem) {
        this.tagBem = tagBem;
    }

    private List<Elemento> listaElementos() {
        return elementoService.buscarTodos();
    }

    private List<SubElemento> listaSubelementos() {

        if (elemento.getId() != null) {
            Long id = elemento.getId();
            return subElementoService.buscarPeloElementoId(id);
        }
        return Collections.emptyList();
    }

    public Elemento getElemento() {
        return elemento;
    }

    public void setElemento(Elemento elemento) {
        this.elemento = elemento;
    }

    private class FileUploadForm extends Form<List<FileUpload>> {
        private static final long serialVersionUID = 1L;

        public FileUploadForm(String id, IModel<List<FileUpload>> model) {
            super(id, model);
            setMultiPart(true);

            FileUploadField fileUploadField = new FileUploadField("fileInput", model);
            fileUploadField.add(new UploadValidator());
            fileUploadField.setVisible(!isReadOnly());
            add(fileUploadField);

            AjaxSubmitLink btnAdicionarAnexo = getButtonAdicionarAnexo();
            authorize(btnAdicionarAnexo, RENDER, BemPage.ROLE_MANTER_BEM_INCLUIR);
            btnAdicionarAnexo.setVisible(!isReadOnly());
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

    public Bem getEntity() {
        return entity;
    }

    public void setEntity(Bem entity) {
        if (entity == null) {
            this.entity = new Bem();
        }
        this.entity = entity;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isAtalhoNovoBem() {
        return atalhoNovoBem;
    }

    public void setAtalhoNovoBem(boolean atalhoNovoBem) {
        this.atalhoNovoBem = atalhoNovoBem;
    }

    private void download(Item<AnexoBem> item) {
        AnexoBem a = item.getModelObject();

        if (a.getId() != null) {
            AnexoDto retorno = anexoService.buscarPeloId(a.getId());
            SideUtil.download(retorno.getConteudo(), retorno.getNomeAnexo());
        } else {
            SideUtil.download(a.getConteudo(), a.getNomeAnexo());
        }
    }

    private DataView<HistoricoBem> newDataViewHistóricoValorBem() {
        return new DataView<HistoricoBem>("historicoValorBem", new ListDataProvider<HistoricoBem>(getEntity().getHistoricoBemValores())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<HistoricoBem> item) {
                item.add(new Label("valorEstimadoHistorico", item.getModelObject().getValorEstimado()) {
                    private static final long serialVersionUID = 1L;

                    @SuppressWarnings("unchecked")
                    @Override
                    public <C> IConverter<C> getConverter(Class<C> type) {
                        return (IConverter<C>) new MoneyBigDecimalConverter();
                    }
                });
                item.add(new Label("dataEstimativaHistorico", item.getModelObject().getDataEstimativa().getMonthValue() + " / " + item.getModelObject().getDataEstimativa().getYear()));
            }
        };
    }

    private InfraDropDownChoice<EnumMes> newDropDownMes() {
        InfraDropDownChoice<EnumMes> dropDownChoice = componentFactory.newDropDownChoice("mes", "Data Estimada \"Mês\"", true, "codigo", "valor", new LambdaModel<EnumMes>(this::getMesSelecionado, this::setMesSelecionado), Arrays.asList(EnumMes.values()), null);
        dropDownChoice.setNullValid(true);
        return dropDownChoice;
    }

    private InfraDropDownChoice<Integer> newDropDownAno() {
        InfraDropDownChoice<Integer> dropDownChoice = componentFactory.newDropDownChoice("ano", "Data Estimada \"Ano\"", true, "", "", new LambdaModel<Integer>(this::getAnoSelecionado, this::setAnoSelecionado), getListaAno(), null);
        dropDownChoice.setNullValid(true);
        return dropDownChoice;
    }

    public EnumMes getMesSelecionado() {
        return mesSelecionado;
    }

    public void setMesSelecionado(EnumMes enumMesSelecionado) {
        this.mesSelecionado = enumMesSelecionado;
    }

    private List<Integer> getListaAno() {
        LocalDate dataHoje = LocalDate.now();
        List<Integer> anos = new ArrayList<Integer>();
        int anoAtual = dataHoje.getYear();

        // Adiciona 20 anos passados e 1 ano futuro a partir do ano atual.
        int anoInicial = anoAtual + 1;
        int anoFinal = anoAtual - 20;

        for (int i = anoInicial; i >= anoFinal; i--) {
            Integer ano = new Integer(i);
            anos.add(ano);
        }
        return anos;
    }

    public Integer getAnoSelecionado() {
        return anoSelecionado;
    }

    public void setAnoSelecionado(Integer anoSelecionado) {
        this.anoSelecionado = anoSelecionado;
    }

    private void actionTextNome(TextField<String> field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (field.getModelObject().length() > 200) {
                    String texto = field.getModelObject();
                    field.setModelObject(texto.substring(0, 199));
                }
            }
        };
        field.add(onChangeAjaxBehavior);
    }
}
