package br.gov.mj.side.web.view.cadastraritem;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.InfraRadioChoice;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.enums.EnumTipoPatrimonio;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.patrimoniamento.ArquivoUnico;
import br.gov.mj.side.entidades.programa.patrimoniamento.PatrimonioObjetoFornecimento;
import br.gov.mj.side.web.dto.PatrimonioObjetoFornecimentoDto;
import br.gov.mj.side.web.service.ArquivoUnicoService;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.service.PatrimonioService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.components.EntityDataProvider;
import br.gov.mj.side.web.view.template.TemplatePage;

public class RegistrarPatrimonioPage extends TemplatePage {

    /**
     * 
     */
    private static final long serialVersionUID = 7573841874089409377L;

    // ####################################_CONSTANTE_##############################################
    private Integer itensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;
    private static final String ONCHANGE = "onchange";

    // ####################################_VARIAVEIS_##############################################
    private Form<PatrimonioObjetoFornecimento> form;
    private PatrimonioObjetoFornecimento patrimonioObjetoFornecimento = new PatrimonioObjetoFornecimento();
    private ObjetoFornecimentoContrato objetoFornecimentoContrato = new ObjetoFornecimentoContrato();
    private PatrimonioObjetoFornecimentoDto patrimonioObjetoFornecimentoDto = new PatrimonioObjetoFornecimentoDto();
    private PatrimonioObjetoFornecimentoDto patrimonioObjetoFornecimentoDtoTemp = new PatrimonioObjetoFornecimentoDto();

    private Boolean botaoSim = Boolean.FALSE;
    private Boolean botaoNao = Boolean.FALSE;
    private Boolean botaoPergunta = Boolean.TRUE;
    private Boolean selcionarTipo = Boolean.TRUE;
    private Boolean selcionarTipoBloqueio = Boolean.FALSE;

    private String nomeItem = "";
    private String numeroPatrimonio = "";
    private String descricaoMotivo = "";

    private List<PatrimonioObjetoFornecimento> listaPatrimonioObjetoFornecimento = new ArrayList<PatrimonioObjetoFornecimento>();
    private List<PatrimonioObjetoFornecimento> listaPatrimonioObjetoFornecimentoAnterior = new ArrayList<PatrimonioObjetoFornecimento>();

    private String latitude;
    private String longitude;
    private String dataFoto;
    private List<FileUpload> uploads = new ArrayList<FileUpload>();

    // ####################################_COMPONETE_WICKET_##############################################
    private Page backPage;
    private PanelPergunta panelPergunta;
    private PanelRegistarSim panelRegistarSim;
    private PanelRegistarNao panelRegistarNao;
    private PanelDescricao panelDescricao;
    private PainelNomeItem painelNomeItem;
    private PainelAddItem painelAddItem;
    private PainelDataView painelDataView;
    private RadioGroup<Contrato> radioContratoGrup;
    private InfraAjaxFallbackLink<Void> btVoltar;
    private InfraAjaxFallbackLink<Void> btnRegistrar;
    private InfraAjaxFallbackLink<Void> btnRegistrarMultiplo;
    private InfraAjaxFallbackLink<Void> btnConfirmar;
    private TextField<String> fieldNomeItem;
    private TextField<String> fieldNumeroPatrimonio;
    private DataView<PatrimonioObjetoFornecimento> dataViewPatrimonioObjetoFornecimento;
    private InfraAjaxConfirmButton btnExcluirAnexo;
    private TextArea motivoTextArea;
    private Label labelMensagem;
    private Model<String> mensagem = Model.of("");
    private InfraRadioChoice<Boolean> radioChoiceLocal;

    // ####################################_INJECT_##############################################
    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private OrdemFornecimentoContratoService fornecimentoContratoService;
    @Inject
    private PatrimonioService patrimonioService;
    @Inject
    private ArquivoUnicoService arquivoUnicoService;

    public RegistrarPatrimonioPage(PageParameters pageParameters, ObjetoFornecimentoContrato objetoFornecimentoContrato, Page backPage, PatrimonioObjetoFornecimentoDto patrimonioObjetoFornecimentoDtoTemp) {
        super(pageParameters);
        this.backPage = backPage;
        this.objetoFornecimentoContrato = objetoFornecimentoContrato;
        this.patrimonioObjetoFornecimentoDtoTemp = patrimonioObjetoFornecimentoDtoTemp;
        setTitulo("Relatório Patrimonial");
        initVariaveis();
        initComponents();
    }

    private void initVariaveis() {
        List<PatrimonioObjetoFornecimento> lista = new ArrayList<PatrimonioObjetoFornecimento>();
        this.objetoFornecimentoContrato = fornecimentoContratoService.buscarObjetoFornecimentoContrato(this.objetoFornecimentoContrato.getId());
        if (this.objetoFornecimentoContrato.getTipoPatrimonio() != null && this.objetoFornecimentoContrato.getTipoPatrimonio().equals(EnumTipoPatrimonio.NAO_PATRIMONIAVEL)) {
            this.botaoSim = Boolean.FALSE;
            this.botaoNao = Boolean.TRUE;
            this.botaoPergunta = Boolean.FALSE;
            patrimonioObjetoFornecimentoDto.setObjetoFornecimentoContrato(this.objetoFornecimentoContrato);
            lista = patrimonioService.buscarSemPaginacao(patrimonioObjetoFornecimentoDto);
            if (lista.size() > 0) {
                for (PatrimonioObjetoFornecimento obj : lista) {
                    this.descricaoMotivo = obj.getMotivoItemNaoPatrimoniavel();
                    this.patrimonioObjetoFornecimento = obj;
                }
            }

        } else if (this.objetoFornecimentoContrato.getTipoPatrimonio() != null && this.objetoFornecimentoContrato.getTipoPatrimonio().equals(EnumTipoPatrimonio.UNICO)) {
            this.botaoSim = Boolean.TRUE;
            this.botaoNao = Boolean.FALSE;
            this.botaoPergunta = Boolean.FALSE;
            this.selcionarTipo = Boolean.TRUE;
            this.selcionarTipoBloqueio = Boolean.FALSE;
            patrimonioObjetoFornecimentoDto.setObjetoFornecimentoContrato(this.objetoFornecimentoContrato);
            lista = patrimonioService.buscarSemPaginacao(patrimonioObjetoFornecimentoDto);
            if (lista.size() > 0) {
                for (PatrimonioObjetoFornecimento obj : lista) {
                    this.numeroPatrimonio = obj.getNumeroPatrimonio();
                    this.patrimonioObjetoFornecimento = obj;
                }
            }

        } else if (this.objetoFornecimentoContrato.getTipoPatrimonio() != null && this.objetoFornecimentoContrato.getTipoPatrimonio().equals(EnumTipoPatrimonio.MULTIPLO)) {
            this.botaoSim = Boolean.TRUE;
            this.botaoNao = Boolean.FALSE;
            this.botaoPergunta = Boolean.FALSE;
            this.selcionarTipo = Boolean.FALSE;
            this.selcionarTipoBloqueio = Boolean.FALSE;
            patrimonioObjetoFornecimentoDto.setObjetoFornecimentoContrato(this.objetoFornecimentoContrato);
            this.listaPatrimonioObjetoFornecimento = patrimonioService.buscarSemPaginacao(patrimonioObjetoFornecimentoDto);
            if (this.listaPatrimonioObjetoFornecimentoAnterior.size() == 0) {
                this.listaPatrimonioObjetoFornecimentoAnterior.clear();
                this.listaPatrimonioObjetoFornecimentoAnterior.addAll(this.listaPatrimonioObjetoFornecimento);
            }
        } else {
            this.botaoSim = Boolean.FALSE;
            this.botaoNao = Boolean.FALSE;
            this.botaoPergunta = Boolean.TRUE;
            this.selcionarTipo = Boolean.TRUE;
            this.selcionarTipoBloqueio = Boolean.TRUE;
        }
    }

    private void initComponents() {
        form = componentFactory.newForm("form", new CompoundPropertyModel<PatrimonioObjetoFornecimento>(this.patrimonioObjetoFornecimento));
        form.add(componentFactory.newLink("lnkPainelDashboard", HomePage.class));
        Link<Void> linkBackPage = new Link<Void>("lnkPainelPesquisarItemBackPage") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                if (patrimonioObjetoFornecimentoDtoTemp != null) {
                    setResponsePage(new PesquisarItemPage(getPageParameters(), patrimonioObjetoFornecimentoDtoTemp));
                } else {
                    setResponsePage(backPage);
                }
            }
        };
        form.add(linkBackPage);
        panelPergunta = newPanelPergunta();
        panelPergunta.setVisible(this.botaoPergunta);
        form.add(panelPergunta);

        panelDescricao = newPanelDescricao();
        panelDescricao.setVisible(this.botaoSim || this.botaoNao);
        form.add(panelDescricao);

        panelRegistarSim = newPanelRegistarSim();
        panelRegistarSim.setVisible(this.botaoSim);
        form.add(panelRegistarSim);

        panelRegistarNao = newPanelRegistarNao();
        panelRegistarNao.setVisible(this.botaoNao);
        form.add(panelRegistarNao);

        add(form);
    }

    // ####################################_PAINEIS_##############################################

    public PanelPergunta newPanelPergunta() {
        panelPergunta = new PanelPergunta();
        panelPergunta.setOutputMarkupId(Boolean.TRUE);
        return panelPergunta;
    }

    private class PanelPergunta extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelPergunta() {
            super("panelPergunta");
            add(newButtonSim());
            add(newButtonNao());
        }
    }

    public PanelDescricao newPanelDescricao() {
        panelDescricao = new PanelDescricao();
        panelDescricao.setOutputMarkupId(Boolean.TRUE);
        return panelDescricao;
    }

    private class PanelDescricao extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDescricao() {
            super("panelDescricao");
            add(componentFactory.newLabel("txtDescricaoItem", objetoFornecimentoContrato.getItem().getDescricaoBem()));
        }
    }

    public PanelRegistarSim newPanelRegistarSim() {
        panelRegistarSim = new PanelRegistarSim();
        panelRegistarSim.setOutputMarkupId(Boolean.TRUE);
        return panelRegistarSim;
    }

    private class PanelRegistarSim extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelRegistarSim() {
            super("panelRegistarSim");
            labelMensagem = new Label("mensagem", mensagem);
            labelMensagem.setEscapeModelStrings(Boolean.FALSE);
            add(labelMensagem);
            radioChoiceLocal = newRaioTipoPatrimonio();
            radioChoiceLocal.setEnabled(selcionarTipoBloqueio);
            add(radioChoiceLocal); //
            painelNomeItem = newPainelNomeItem();
            painelNomeItem.setVisible(!getSelcionarTipo());
            add(painelNomeItem);

            fieldNumeroPatrimonio = newTextFieldNumeroPatromonio();
            add(fieldNumeroPatrimonio);

            if (patrimonioObjetoFornecimento.getConteudo() != null) {
                add(newImageFoto());
            } else {
                add(newImageFotoPadrao());
            }
            add(getFormUpload());

            painelAddItem = newPainelAddItem();
            painelAddItem.setVisible(!getSelcionarTipo());
            add(painelAddItem);

            painelDataView = newPainelDataView();
            painelDataView.setVisible(!getSelcionarTipo() && (listaPatrimonioObjetoFornecimento.size() > 0));
            add(painelDataView);

            btnRegistrar = newButtonRegistrar();
            btnRegistrar.setVisible(getSelcionarTipo());
            add(btnRegistrar); // btnRegistrar

            btVoltar = newButtonVoltar();
            btVoltar.setVisible(getSelcionarTipo());
            add(btVoltar); // btnVoltar
        }
    }

    public PanelRegistarNao newPanelRegistarNao() {
        panelRegistarNao = new PanelRegistarNao();
        panelRegistarNao.setOutputMarkupId(Boolean.TRUE);
        return panelRegistarNao;
    }

    private class PanelRegistarNao extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelRegistarNao() {
            super("panelRegistarNao");
            labelMensagem = new Label("mensagem", mensagem);
            labelMensagem.setEscapeModelStrings(Boolean.FALSE);
            add(labelMensagem);
            motivoTextArea = newTextAreaDescricao();
            add(motivoTextArea);

            btnConfirmar = newButtonConfirmar();
            add(btnConfirmar); // btnRegistrar

            btVoltar = newButtonVoltar();
            add(btVoltar); // btnVoltar
        }
    }

    public PainelNomeItem newPainelNomeItem() {
        painelNomeItem = new PainelNomeItem();
        painelNomeItem.setOutputMarkupId(Boolean.TRUE);
        return painelNomeItem;
    }

    private class PainelNomeItem extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PainelNomeItem() {
            super("painelNomeItem");
            fieldNomeItem = newTextFieldNotaItem();
            add(fieldNomeItem);
        }
    }

    public PainelAddItem newPainelAddItem() {
        painelAddItem = new PainelAddItem();
        painelAddItem.setOutputMarkupId(Boolean.TRUE);
        return painelAddItem;
    }

    private class PainelAddItem extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PainelAddItem() {
            super("painelAddItem");
            add(newButtonAdicionarItem());
            add(newButtonCancelar());
        }
    }

    public PainelDataView newPainelDataView() {
        painelDataView = new PainelDataView();
        painelDataView.setOutputMarkupId(Boolean.TRUE);
        return painelDataView;
    }

    private class PainelDataView extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PainelDataView() {
            super("painelDataView");

            add(newDataViewItens());
            add(newDropItensPorPagina()); // itensPorPagina
            add(new InfraAjaxPagingNavigator("paginationItens", dataViewPatrimonioObjetoFornecimento));
            btnRegistrarMultiplo = newButtonRegistrarMultiplo();
            add(btnRegistrarMultiplo);
            add(newButtonVoltarMultiplo());
        }
    }

    // ####################################_COMPONENTE_WICKET_###############################################

    private ContextImage newImageFotoPadrao() {
        ContextImage image = new ContextImage("imagemFoto", "images/sem-imagem.png");
        image.setOutputMarkupId(Boolean.TRUE);
        return image;
    }

    private NonCachingImage newImageFoto() {
        return new NonCachingImage("imagemFoto", new AbstractReadOnlyModel<DynamicImageResource>() {
            private static final long serialVersionUID = 1L;

            @Override
            public DynamicImageResource getObject() {
                DynamicImageResource dir = new DynamicImageResource() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected byte[] getImageData(Attributes attributes) {
                        return patrimonioObjetoFornecimento.getConteudo();
                    }
                };
                String extensaoArquivo = FilenameUtils.getExtension(patrimonioObjetoFornecimento.getNomeAnexo());
                dir.setFormat("image/".concat(extensaoArquivo));
                return dir;
            }
        });
    }

    private FileUploadForm getFormUpload() {
        return new FileUploadForm("formUpload", new PropertyModel<List<FileUpload>>(this, "uploads"), patrimonioObjetoFornecimento);
    }

    // form para upload
    private class FileUploadForm extends Form<List<FileUpload>> {
        private static final long serialVersionUID = 1L;

        public FileUploadForm(String id, IModel<List<FileUpload>> model, PatrimonioObjetoFornecimento patrimonioObjetoFornecimento) {
            super(id, model);
            setMultiPart(Boolean.TRUE);
            FileUploadField fileUploadField = new FileUploadField("fileInput", model);
            fileUploadField.add(new UploadValidator());
            add(fileUploadField);
            add(newButtonAdicionarAnexoMobile());
            add(newTextFieldLatitude());
            add(newTextFieldLongitude());
            add(newTextFieldDataFoto());
        }

        /* Irá validar para não receber arquivos do tipo : .exe, .bat */
        private class UploadValidator implements IValidator<List<FileUpload>> {
            private static final long serialVersionUID = 1L;

            @Override
            public void validate(IValidatable<List<FileUpload>> validatable) {
                List<FileUpload> list = validatable.getValue();
                if (!list.isEmpty()) {
                    FileUpload fileUpload = list.get(0);
                    String extension = FilenameUtils.getExtension(fileUpload.getClientFileName());
                    if ("exe".equalsIgnoreCase(extension) || "bat".equalsIgnoreCase(extension)) {
                        ValidationError error = new ValidationError("Não são permitidos arquivos executáveis como .exe,.bat e etc.");
                        validatable.error(error);
                    }
                }
            }
        }
    }

    private AjaxSubmitLink newButtonAdicionarAnexoMobile() {
        AjaxSubmitLink btnAdicionarAnexoMobile = new AjaxSubmitLink("btnAdicionarAnexoMobile") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                adicionarAnexoMobile(target);
            }
        };
        Label lblDescricao = new Label("icoBtnAdicionarAlterar", this.patrimonioObjetoFornecimento.getNomeAnexo() == null ? "<i class=\"fa fa-plus\"></i>Adicionar" : "<i class=\"fa fa-exchange\"></i>Substituir");
        lblDescricao.setEscapeModelStrings(false);
        lblDescricao.setOutputMarkupId(true);

        btnAdicionarAnexoMobile.add(lblDescricao);
        btnAdicionarAnexoMobile.setOutputMarkupId(true);
        return btnAdicionarAnexoMobile;
    }

    // mobile: adiciona o anexo para persistir.
    private void adicionarAnexoMobile(AjaxRequestTarget target) {

        if (this.latitude != null && this.longitude != null) {
            if (this.latitude.equalsIgnoreCase("undefined") && this.longitude.equalsIgnoreCase("undefined")) {
                getSession().error("É necessario habilitar a localização da foto(GPS).");
                target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
                return;
            }
        } else {
            getSession().error("É necessario habilitar a localização da foto(GPS).");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
            return;
        }

        if (this.dataFoto != null) {
            if (this.dataFoto.equalsIgnoreCase("undefined")) {
                getSession().error("Não foi possivel recuperar a data da foto.");
                target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
                return;
            }
        } else {
            getSession().error("Não foi possivel recuperar a data da foto.");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
            return;
        }

        if (this.uploads != null) {
            if (!this.uploads.isEmpty()) {
                if (!validarTamanhoAnexos(target)) {
                    return;
                }
                for (FileUpload component : this.uploads) {

                    byte[] bytes = component.getMD5();
                    String hashFoto = SideUtil.retornarHashDoArquivoMD5(bytes);

                    if (verificaFotoUtilizadaLista(component.getBytes(), hashFoto)) {
                        getSession().error("A imagem já encontra-se em uso!");
                        target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
                        return;
                    }

                    if (validaFormato(component)) {
                        setaLatitudeLongitudeDataMobile();
                        this.patrimonioObjetoFornecimento.setTamanho(null);
                        if (this.patrimonioObjetoFornecimento.getArquivoUnico() != null) {
                            this.patrimonioObjetoFornecimento.getArquivoUnico().setCodigoUnico(hashFoto);
                        } else if (this.patrimonioObjetoFornecimento.getArquivoUnico() == null) {
                            ArquivoUnico au = new ArquivoUnico();
                            au.setCodigoUnico(hashFoto);
                            this.patrimonioObjetoFornecimento.setArquivoUnico(au);
                        }
                        this.patrimonioObjetoFornecimento.setConteudo(redimencionaImagem(component));
                        this.patrimonioObjetoFornecimento.setNomeAnexo(component.getClientFileName());
                        this.panelRegistarSim.addOrReplace(newImageFoto());
                        target.add(panelRegistarSim);
                    }

                }

            }
        } else {
            addMsgError("Selecione um arquivo para anexar.");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        }

    }

    // valida se a foto foi utitilizada
    private boolean verificaFotoUtilizadaLista(byte[] foto, String hashFoto) {
        Boolean retorno = Boolean.FALSE;

        // verifica se a foto foi utilizada no banco
        if (this.arquivoUnicoService.verificarSeAFotoJafoiUtilizadaNoSistema(hashFoto)) {
            retorno = Boolean.TRUE;
        }

        // verifica se a foto foi utilizada na lista
        for (PatrimonioObjetoFornecimento patrimonios : listaPatrimonioObjetoFornecimento) {
            if (patrimonios.getArquivoUnico().getCodigoUnico().equals(hashFoto)) {
                retorno = Boolean.TRUE;
            }
        }

        return retorno;
    }

    private boolean validarTamanhoAnexos(AjaxRequestTarget target) {
        boolean validar = true;

        if (this.uploads.get(0).getSize() > Bytes.megabytes(Constants.LIMITE_MEGABYTES).bytes()) {
            addMsgError("O tamanho do anexo excede o limite permitido (Anexo maior que " + Constants.LIMITE_MEGABYTES + "MB).");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
            validar = false;
        }
        return validar;
    }

    private boolean validaFormato(FileUpload component) {

        Boolean retorno = Boolean.FALSE;
        String extensao = FilenameUtils.getExtension(component.getClientFileName());

        if (extensao.equalsIgnoreCase("jpeg")) {
            retorno = Boolean.TRUE;
        } else if (extensao.equalsIgnoreCase("jpg")) {
            retorno = Boolean.TRUE;
        } else if (extensao.equalsIgnoreCase("gif")) {
            retorno = Boolean.TRUE;
        } else if (extensao.equalsIgnoreCase("png")) {
            retorno = Boolean.TRUE;
        } else if (extensao.equalsIgnoreCase("bmp")) {
            retorno = Boolean.TRUE;
        }

        return retorno;
    }

    private void setaLatitudeLongitudeDataMobile() {
        if (this.latitude != null && this.longitude != null) {
            if (!this.latitude.equalsIgnoreCase("undefined") && !this.longitude.equalsIgnoreCase("undefined")) {
                String latitudeTEMP = this.latitude.replaceAll(".", "").replaceAll(",", "");
                String longitudeTEMP = this.longitude.replaceAll(".", "").replaceAll(",", "");

                this.patrimonioObjetoFornecimento.setLatitudeLongitudeFoto(latitudeTEMP.concat(" ").concat(longitudeTEMP));
            } else {
                this.patrimonioObjetoFornecimento.setLatitudeLongitudeFoto(new String());
            }
        }

        if (this.dataFoto != null) {
            if (!this.dataFoto.equalsIgnoreCase("undefined")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
                LocalDateTime dataFotoFormatada = LocalDateTime.parse(this.dataFoto, formatter);
                this.patrimonioObjetoFornecimento.setDataFoto(dataFotoFormatada);
            } else {
                this.patrimonioObjetoFornecimento.setDataFoto(null);
            }
        }

    }

    private byte[] retornaBytesImagem(FileUpload componentArquivo) {

        byte[] bytesImagem = null;
        Graphics2D graphisImagem = null;
        ByteArrayOutputStream baos = null;
        int tipo = BufferedImage.TYPE_INT_ARGB;

        try {

            String extensaoArquivo = FilenameUtils.getExtension(componentArquivo.getClientFileName());

            // transforma InputStream em uma BufferedImage
            BufferedImage bufImagemLida = ImageIO.read(new ByteArrayInputStream(componentArquivo.getBytes()));

            int larguraFixa = 190;
            int larguraOriginal = bufImagemLida.getWidth();
            int alturaOriginal = bufImagemLida.getHeight();

            Float novaAltura = new Float(larguraOriginal) / new Float(alturaOriginal);
            Float novaLargura = novaAltura * larguraFixa;

            if (extensaoArquivo.equalsIgnoreCase("jpg")) {
                tipo = BufferedImage.TYPE_INT_RGB;
            } else if (extensaoArquivo.equalsIgnoreCase("jpeg")) {
                tipo = BufferedImage.TYPE_INT_RGB;
            }

            // cria imagem para trabalhar.
            BufferedImage imagemRedimensionada = new BufferedImage(novaLargura.intValue(), larguraFixa, tipo);

            // realiza o redimensionamento da imagem.
            graphisImagem = imagemRedimensionada.createGraphics();
            graphisImagem.drawImage(bufImagemLida, 0, 0, novaLargura.intValue(), larguraFixa, null);
            baos = new ByteArrayOutputStream();

            // escreve a imagem no OutputStream.
            ImageIO.write(imagemRedimensionada, extensaoArquivo, baos);

            // transforma o OutputStream em array de bytes e retorna.
            bytesImagem = baos.toByteArray();
        } catch (

        IOException e) {
            e.printStackTrace();
        } finally {
            // é necessario sempre libera recursos
            if (graphisImagem != null) {
                graphisImagem.dispose();
            }

            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesImagem;
    }

    private byte[] redimencionaImagem(FileUpload componentArquivo) {

        byte[] bytesImagem = null;
        Graphics2D graphisImagem = null;
        ByteArrayOutputStream baos = null;
        int tipo = BufferedImage.TYPE_INT_ARGB;

        try {

            String extensaoArquivo = FilenameUtils.getExtension(componentArquivo.getClientFileName());

            // transforma InputStream em uma BufferedImage
            BufferedImage bufImagemLida = ImageIO.read(new ByteArrayInputStream(componentArquivo.getBytes()));

            int larguraFixa = 190;
            int larguraOriginal = bufImagemLida.getWidth();
            int alturaOriginal = bufImagemLida.getHeight();

            Float novaAltura = new Float(larguraOriginal) / new Float(alturaOriginal);
            Float novaLargura = novaAltura * larguraFixa;

            if (extensaoArquivo.equalsIgnoreCase("jpg")) {
                tipo = BufferedImage.TYPE_INT_RGB;
            } else if (extensaoArquivo.equalsIgnoreCase("jpeg")) {
                tipo = BufferedImage.TYPE_INT_RGB;
            }

            // cria imagem para trabalhar.
            BufferedImage imagemRedimensionada = new BufferedImage(novaLargura.intValue(), larguraFixa, tipo);

            // realiza o redimensionamento da imagem.
            graphisImagem = imagemRedimensionada.createGraphics();
            graphisImagem.drawImage(bufImagemLida, 0, 0, novaLargura.intValue(), larguraFixa, null);
            baos = new ByteArrayOutputStream();

            // escreve a imagem no OutputStream.
            ImageIO.write(imagemRedimensionada, extensaoArquivo, baos);

            // transforma o OutputStream em array de bytes e retorna.
            bytesImagem = baos.toByteArray();
        } catch (

        IOException e) {
            e.printStackTrace();
        } finally {
            // é necessario sempre libera recursos
            if (graphisImagem != null) {
                graphisImagem.dispose();
            }

            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesImagem;
    }

    private TextField<String> newTextFieldLongitude() {
        TextField<String> textField = componentFactory.newTextField("txtLongitude", "Longitude", Boolean.FALSE, new PropertyModel<String>(this, "longitude"));
        textField.setOutputMarkupId(Boolean.TRUE);
        return textField;
    }

    private TextField<String> newTextFieldDataFoto() {
        TextField<String> textField = componentFactory.newTextField("txtDataFoto", "Data Foto", Boolean.FALSE, new PropertyModel<String>(this, "dataFoto"));
        textField.setOutputMarkupId(Boolean.TRUE);
        return textField;
    }

    private TextField<String> newTextFieldLatitude() {
        TextField<String> textField = componentFactory.newTextField("txtLatitude", "Latitude", Boolean.FALSE, new PropertyModel<String>(this, "latitude"));
        textField.setOutputMarkupId(Boolean.TRUE);
        return textField;
    }

    private InfraAjaxFallbackLink<Void> newButtonSim() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnSim", (target) -> actionButtonSim(target));
        btn.setOutputMarkupId(true);
        return btn;
    }

    private void actionButtonSim(AjaxRequestTarget target) {
        this.botaoSim = Boolean.TRUE;
        this.botaoNao = Boolean.FALSE;
        this.botaoPergunta = Boolean.FALSE;
        atualizarPaineis(target);
    }

    private InfraAjaxFallbackLink<Void> newButtonNao() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnNao", (target) -> actionButtonNao(target));
        btn.setOutputMarkupId(true);
        return btn;
    }

    private void actionButtonNao(AjaxRequestTarget target) {
        this.botaoSim = Boolean.FALSE;
        this.botaoNao = Boolean.TRUE;
        this.botaoPergunta = Boolean.FALSE;
        atualizarPaineis(target);
    }

    private InfraAjaxFallbackLink<Void> newButtonRegistrarMultiplo() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnRegistrarMultiplo", (target) -> actionbtnRegistrarMultiplo(target));
        btn.setOutputMarkupId(true);
        return btn;
    }

    private void actionbtnRegistrarMultiplo(AjaxRequestTarget target) {

        if (listaPatrimonioObjetoFornecimento.size() > 0) {
            this.listaPatrimonioObjetoFornecimento = patrimonioService.incluirAlterar(this.listaPatrimonioObjetoFornecimento, this.listaPatrimonioObjetoFornecimentoAnterior);
            addMsgInfo("Registrado com sucesso!");
            mensagem.setObject("");
            this.nomeItem = "";
            this.numeroPatrimonio = "";
            this.mensagem.setObject("");
            this.patrimonioObjetoFornecimento = new PatrimonioObjetoFornecimento();
            this.listaPatrimonioObjetoFornecimentoAnterior.clear();
            this.listaPatrimonioObjetoFornecimentoAnterior.addAll(this.listaPatrimonioObjetoFornecimento);
            radioChoiceLocal.setEnabled(Boolean.FALSE);
            target.add(radioChoiceLocal);
            atualizarTipoPatrimonio(target);
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        } else {
            addMsgError("É necessário adicionar um item para registrar!");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        }

    }

    private InfraAjaxFallbackLink<Void> newButtonRegistrar() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnRegistrar", (target) -> actionbtnRegistrar(target));
        btn.setOutputMarkupId(true);
        return btn;
    }

    private void actionbtnRegistrar(AjaxRequestTarget target) {
        List<PatrimonioObjetoFornecimento> listaPatrimonioObjetoFornecimento = new ArrayList<PatrimonioObjetoFornecimento>();
        if (this.patrimonioObjetoFornecimento != null) {
            if (!validarPatrimonio(target)) {
                return;
            }
            this.patrimonioObjetoFornecimento.setNumeroPatrimonio(this.numeroPatrimonio);
            this.objetoFornecimentoContrato.setItemPatrimoniavel(Boolean.TRUE);
            this.objetoFornecimentoContrato.setTipoPatrimonio(EnumTipoPatrimonio.UNICO);
            this.patrimonioObjetoFornecimento.setObjetoFornecimentoContrato(this.objetoFornecimentoContrato);
            listaPatrimonioObjetoFornecimento.add(this.patrimonioObjetoFornecimento);
            patrimonioService.incluirAlterar(listaPatrimonioObjetoFornecimento, new ArrayList<PatrimonioObjetoFornecimento>());
            addMsgInfo("Registrado com sucesso!");
            mensagem.setObject("");
            radioChoiceLocal.setEnabled(Boolean.FALSE);
            target.add(radioChoiceLocal);
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        }
    }

    private boolean validarPatrimonio(AjaxRequestTarget target) {
        boolean validar = Boolean.TRUE;
        String msg = "";

        if (("".equals(this.numeroPatrimonio) || this.numeroPatrimonio == null) && getSelcionarTipo() && this.botaoSim) {
            msg += "<p><li> É obrigatório o preenchimento do \"Número Patrimônio\".</li><p />";
            validar = Boolean.FALSE;
        }

        if ("".equals(this.nomeItem) && !getSelcionarTipo() && this.botaoSim) {
            msg += "<p><li> É obrigatório o preenchimento do \"Nome do Item\".</li><p />";
            validar = Boolean.FALSE;
        }

        if ("".equals(this.numeroPatrimonio) && !getSelcionarTipo() && this.botaoSim) {
            msg += "<p><li> É obrigatório o preenchimento do \"Número Patrimônio\".</li><p />";
            validar = Boolean.FALSE;
        }

        if (this.patrimonioObjetoFornecimento.getConteudo() == null && getSelcionarTipo() && this.botaoSim) {
            msg += "<p><li> É obrigatório anexar uma imagem.</li><p />";
            validar = Boolean.FALSE;
        }

        if (this.patrimonioObjetoFornecimento.getConteudo() == null && !getSelcionarTipo() && this.botaoSim) {
            msg += "<p><li> É obrigatório anexar uma imagem.</li><p />";
            validar = Boolean.FALSE;
        }

        if (("".equals(this.descricaoMotivo) || this.descricaoMotivo == null) && this.botaoNao) {
            msg += "<p><li> É obrigatório o preenchimento do \"Motivo\".</li><p />";
            validar = Boolean.FALSE;
        }

        if (!validar) {
            this.mensagem.setObject(msg);
        } else {
            this.mensagem.setObject("");
        }
        if (this.botaoSim) {
            this.panelRegistarSim.addOrReplace(this.labelMensagem);
        } else if (this.botaoNao) {
            this.panelRegistarNao.addOrReplace(this.labelMensagem);
        }

        target.add(this.labelMensagem);
        return validar;
    }

    private InfraAjaxFallbackLink<Void> newButtonConfirmar() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnConfirmar", (target) -> actionbtnConfirmar(target));
        btn.setOutputMarkupId(true);
        return btn;
    }

    private void actionbtnConfirmar(AjaxRequestTarget target) {
        if (!validarPatrimonio(target)) {
            return;
        } else {
            List<PatrimonioObjetoFornecimento> listaPatrimonioObjetoFornecimento = new ArrayList<PatrimonioObjetoFornecimento>();
            this.patrimonioObjetoFornecimento.setMotivoItemNaoPatrimoniavel(descricaoMotivo);
            this.objetoFornecimentoContrato.setItemPatrimoniavel(Boolean.FALSE);
            this.objetoFornecimentoContrato.setTipoPatrimonio(EnumTipoPatrimonio.NAO_PATRIMONIAVEL);
            this.patrimonioObjetoFornecimento.setObjetoFornecimentoContrato(this.objetoFornecimentoContrato);
            listaPatrimonioObjetoFornecimento.add(this.patrimonioObjetoFornecimento);
            patrimonioService.incluirAlterar(listaPatrimonioObjetoFornecimento, new ArrayList<PatrimonioObjetoFornecimento>());
            addMsgInfo("Motivo confirmado com sucesso!");
            mensagem.setObject("");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        }
    }

    private InfraAjaxFallbackLink<Void> newButtonAdicionarItem() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnAdicionarItem", (target) -> actionbtnAdicionarAlterarItem(target));
        btn.add(componentFactory.newLabel("txtBtnAdicionarAlterar", patrimonioObjetoFornecimento.getId() == null ? "Adicionar Item" : "Alterar Item"));
        btn.setOutputMarkupId(true);
        return btn;
    }

    private void actionbtnAdicionarAlterarItem(AjaxRequestTarget target) {
        if (!validarPatrimonio(target)) {
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
            return;
        }
        if ((this.nomeItem != null && !"".equals(this.nomeItem)) && (this.numeroPatrimonio != null && !"".equals(this.numeroPatrimonio))) {
            this.patrimonioObjetoFornecimento.setNomeItem(this.nomeItem);
            this.patrimonioObjetoFornecimento.setNumeroPatrimonio(this.numeroPatrimonio);
            this.objetoFornecimentoContrato.setItemPatrimoniavel(Boolean.TRUE);
            this.objetoFornecimentoContrato.setTipoPatrimonio(EnumTipoPatrimonio.MULTIPLO);
            this.patrimonioObjetoFornecimento.setObjetoFornecimentoContrato(this.objetoFornecimentoContrato);
            if (patrimonioObjetoFornecimento.getId() == null) {
                this.listaPatrimonioObjetoFornecimento.add(patrimonioObjetoFornecimento);
            }
            this.nomeItem = "";
            this.numeroPatrimonio = "";
            this.mensagem.setObject("");
            this.patrimonioObjetoFornecimento = new PatrimonioObjetoFornecimento();
            atualizarTipoPatrimonio(target);
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraItens');");
        }
    }

    private InfraAjaxFallbackLink<Void> newButtonCancelar() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnCancelar", (target) -> actionbtnCancelarItem(target));
        btn.setOutputMarkupId(true);
        return btn;
    }

    private void actionbtnCancelarItem(AjaxRequestTarget target) {
        this.nomeItem = "";
        this.numeroPatrimonio = "";
        this.mensagem.setObject("");
        this.patrimonioObjetoFornecimento = new PatrimonioObjetoFornecimento();
        atualizarTipoPatrimonio(target);
    }

    private InfraAjaxFallbackLink<Void> newButtonVoltarMultiplo() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnVoltarMultiplo", (target) -> actionVoltarMultiplo());
        btn.setOutputMarkupId(true);
        return btn;
    }

    private void actionVoltarMultiplo() {
        if (patrimonioObjetoFornecimentoDtoTemp != null) {
            setResponsePage(new PesquisarItemPage(getPageParameters(), patrimonioObjetoFornecimentoDtoTemp));
        } else {
            setResponsePage(backPage);
        }
    }

    private InfraAjaxFallbackLink<Void> newButtonVoltar() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnVoltar", (target) -> actionVoltar());
        btn.setOutputMarkupId(true);
        return btn;
    }

    private void actionVoltar() {
        if (patrimonioObjetoFornecimentoDtoTemp != null) {
            setResponsePage(new PesquisarItemPage(getPageParameters(), patrimonioObjetoFornecimentoDtoTemp));
        } else {
            setResponsePage(backPage);
        }
    }

    private void atualizarPaineis(AjaxRequestTarget target) {
        this.panelDescricao.setVisible(this.botaoSim || this.botaoNao);
        this.panelRegistarSim.setVisible(this.botaoSim);
        this.panelPergunta.setVisible(this.botaoPergunta);
        this.panelRegistarNao.setVisible(this.botaoNao);
        target.add(this.panelDescricao);
        target.add(this.panelRegistarSim);
        target.add(this.panelRegistarNao);
        target.add(this.panelPergunta);
    }

    private void atualizarTipoPatrimonio(AjaxRequestTarget target) {
        btVoltar = newButtonVoltar();
        btVoltar.setVisible(getSelcionarTipo());
        this.panelRegistarSim.addOrReplace(btVoltar);
        target.add(btVoltar);

        btnRegistrar = newButtonRegistrar();
        btnRegistrar.setVisible(getSelcionarTipo());
        this.panelRegistarSim.addOrReplace(btnRegistrar);
        target.add(btnRegistrar);

        painelNomeItem = newPainelNomeItem();
        painelNomeItem.setVisible(!getSelcionarTipo());
        this.panelRegistarSim.addOrReplace(painelNomeItem);
        target.add(painelNomeItem);

        fieldNumeroPatrimonio = newTextFieldNumeroPatromonio();
        this.panelRegistarSim.addOrReplace(fieldNumeroPatrimonio);
        target.add(fieldNumeroPatrimonio);

        painelAddItem = newPainelAddItem();
        painelAddItem.setVisible(!getSelcionarTipo());
        this.panelRegistarSim.addOrReplace(painelAddItem);
        target.add(painelAddItem);

        painelDataView = newPainelDataView();
        painelDataView.addOrReplace(newDataViewItens());
        painelDataView.setVisible(!getSelcionarTipo());
        this.panelRegistarSim.addOrReplace(painelDataView);
        target.add(painelDataView);

        panelRegistarSim.addOrReplace(newImageFotoPadrao(), getFormUpload());
        target.add(panelRegistarSim);
    }

    private InfraRadioChoice<Boolean> newRaioTipoPatrimonio() {
        InfraRadioChoice<Boolean> radioChoiceLocal = componentFactory.newRadioChoice("selecionarTipo", "Tipo Patrimônio", true, false, "", "", new PropertyModel(this, "selcionarTipo"), Arrays.asList(Boolean.TRUE, Boolean.FALSE), (target) -> limitar(target));
        radioChoiceLocal.setChoiceRenderer(new IChoiceRenderer<Boolean>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Object getDisplayValue(Boolean object) {
                if (object != null && object) {
                    return "Patrimônio Único";
                } else {
                    return "Patrimônio Múltiplo";
                }
            }

            @Override
            public String getIdValue(Boolean object, int index) {
                return object.toString();
            }

        });
        return radioChoiceLocal;
    }

    private void limitar(AjaxRequestTarget target) {
        if (getSelcionarTipo()) {
            this.listaPatrimonioObjetoFornecimento = new ArrayList<PatrimonioObjetoFornecimento>();
            this.nomeItem = "";
            this.numeroPatrimonio = "";
            this.patrimonioObjetoFornecimento = new PatrimonioObjetoFornecimento();
        }
        this.mensagem.setObject("");
        atualizarTipoPatrimonio(target);
    }

    public TextField<String> newTextFieldNotaItem() {
        TextField<String> field = componentFactory.newTextField("nomeItem", "Nome do Item", false, new PropertyModel<String>(this, "nomeItem"));
        action(field);
        field.add(StringValidator.maximumLength(60));
        return field;
    }

    public TextField<String> newTextFieldNumeroPatromonio() {
        TextField<String> field = componentFactory.newTextField("numeroPatrimonio", "Número Patrimônio", false, new PropertyModel<String>(this, "numeroPatrimonio"));
        action(field);
        field.add(StringValidator.maximumLength(60));
        return field;
    }

    private TextField<String> action(TextField<String> text) {
        text.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
            private static final long serialVersionUID = 1L;

            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no model
            }
        });
        return text;
    }

    public DataView<PatrimonioObjetoFornecimento> newDataViewItens() {
        dataViewPatrimonioObjetoFornecimento = new DataView<PatrimonioObjetoFornecimento>("listaItens", new EntityDataProvider<PatrimonioObjetoFornecimento>(this.listaPatrimonioObjetoFornecimento)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<PatrimonioObjetoFornecimento> item) {
                item.add(new Label("IdNomeItem", item.getModelObject().getNomeItem()));
                item.add(new Label("IdNumeroPatrimonio", item.getModelObject().getNumeroPatrimonio().toUpperCase()));

                InfraAjaxFallbackLink btnEditar = componentFactory.newAjaxFallbackLink("btnEditar", (target) -> editar(target, item.getModelObject()));
                item.add(btnEditar);

                btnExcluirAnexo = componentFactory.newAJaxConfirmButton("btnExcluirAnexo", "MSG015", form, (target, formz) -> excluir(target, item.getModelObject()));
                item.add(btnExcluirAnexo);
            }
        };
        dataViewPatrimonioObjetoFornecimento.setItemsPerPage(getItensPorPagina() == null ? Constants.ITEMS_PER_PAGE_PAGINATION : getItensPorPagina());
        return dataViewPatrimonioObjetoFornecimento;
    }

    private void editar(AjaxRequestTarget target, PatrimonioObjetoFornecimento editarItemPatrimonio) {
        this.nomeItem = editarItemPatrimonio.getNomeItem();
        this.numeroPatrimonio = editarItemPatrimonio.getNumeroPatrimonio();
        patrimonioObjetoFornecimento = editarItemPatrimonio;
        panelRegistarSim.addOrReplace(newImageFoto(), getFormUpload());
        painelAddItem.addOrReplace(newButtonAdicionarItem());
        target.add(painelNomeItem, panelRegistarSim, painelAddItem);
    }

    private void excluir(AjaxRequestTarget target, PatrimonioObjetoFornecimento excluirItemPatrimonio) {
        this.listaPatrimonioObjetoFornecimento.remove(excluirItemPatrimonio);
        painelDataView = newPainelDataView();
        painelDataView.addOrReplace(newDataViewItens());
        painelDataView.setVisible(!getSelcionarTipo());
        this.panelRegistarSim.addOrReplace(painelDataView);
        target.add(painelDataView);
    }

    public TextArea<String> newTextAreaDescricao() {
        TextArea text = new TextArea<String>("descricaoMotivo", new PropertyModel(this, "descricaoMotivo"));
        text.setLabel(Model.of("Motivo*"));
        text.setOutputMarkupId(true);
        actionAreaDescricao(text);
        text.add(StringValidator.maximumLength(4900));
        return text;
    }

    private void actionAreaDescricao(TextArea<String> text) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no Model
            }
        };
        text.add(onChangeAjaxBehavior);
    }

    // ####################################_GETS_AND_SETS###############################################
    public Boolean getSelcionarTipo() {
        return selcionarTipo;
    }

    public void setSelcionarTipo(Boolean selcionarTipo) {
        this.selcionarTipo = selcionarTipo;
    }

    public Integer getItensPorPagina() {
        return itensPorPagina;
    }

    public void setItensPorPagina(Integer itensPorPagina) {
        this.itensPorPagina = itensPorPagina;
    }

    private DropDownChoice<Integer> newDropItensPorPagina() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPagina", new LambdaModel<Integer>(this::getItensPorPagina, this::setItensPorPagina), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewPatrimonioObjetoFornecimento.setItemsPerPage(getItensPorPagina());
                target.add(painelDataView);
            };
        });
        return dropDownChoice;
    }

}
