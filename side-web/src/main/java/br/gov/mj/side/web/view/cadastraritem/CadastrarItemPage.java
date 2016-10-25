package br.gov.mj.side.web.view.cadastraritem;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.image.NonCachingImage;
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

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.InfraLocalDateTextField;
import br.gov.mj.infra.wicket.component.InfraRadioChoice;
import br.gov.mj.side.entidades.enums.EnumPerfilEntidade;
import br.gov.mj.side.entidades.enums.EnumTipoCampoFormatacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoItensContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoItensContratoResposta;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoObjetoFornecimento;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.web.dto.formatacaoObjetoFornecimento.FormatacaoItensContratoRespostaDto;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.template.TemplatePage;

@AuthorizeInstantiation({ CadastrarItemPage.ROLE_MANTER_CADASTRAR_ITEM_INCLUIR, CadastrarItemPage.ROLE_MANTER_CADASTRAR_ITEM_ALTERAR, CadastrarItemPage.ROLE_MANTER_CADASTRAR_ITEM_EXCLUIR, CadastrarItemPage.ROLE_MANTER_CADASTRAR_ITEM_VISUALIZAR })
public class CadastrarItemPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    public static final String ROLE_MANTER_CADASTRAR_ITEM_INCLUIR = "manter_cadastrar_item:incluir";
    public static final String ROLE_MANTER_CADASTRAR_ITEM_ALTERAR = "manter_cadastrar_item:alterar";
    public static final String ROLE_MANTER_CADASTRAR_ITEM_EXCLUIR = "manter_cadastrar_item:excluir";
    public static final String ROLE_MANTER_CADASTRAR_ITEM_VISUALIZAR = "manter_cadastrar_item:visualizar";

    private static final String ONCHANGE = "onchange";

    private Form<FormatacaoItensContratoResposta> form;
    private Page backPage;
    private FormatacaoObjetoFornecimento formatacaoObjetoFornecimento = new FormatacaoObjetoFornecimento();
    private ObjetoFornecimentoContrato objetoFornecimentoContrato = new ObjetoFornecimentoContrato();
    private FormatacaoItensContratoResposta formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
    private Boolean isMobile;
    private EnumPerfilEntidade perfilUsuarioLogado;
    private Boolean readOnly;
    private List<String> listaFormatacaoOrientacao = new ArrayList<>();
    private List<FormatacaoObjetoFornecimento> listaFormatacaoSALVAR = new ArrayList<>();
    private List<FileUpload> uploads = new ArrayList<FileUpload>();

    private List<FormatacaoItensContratoResposta> listaRespostaFormatacaoAlfanumerico = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaFormatacaoAnexo = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaFormatacaoBoleano = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaFormatacaoData = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaFormatacaoFoto = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaFormatacaoNumerico = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaFormatacaoTexto = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaFormatacaoVideo = new ArrayList<>();

    private AjaxSubmitLink buttonAnterior;
    private AjaxSubmitLink buttonProximo;

    private Boolean informacaoBoleana = Boolean.FALSE;
    private Boolean mostrarBotaoAnterior = Boolean.FALSE;
    private Boolean mostrarBotaoProximo = Boolean.TRUE;
    private Boolean mostrarBotaoAnexar = Boolean.FALSE;
    private Boolean mostrarPainelConformidade = Boolean.FALSE;
    private int indiceFormatacaoContrato;
    private int totalFormatacoes;

    private String latitude;
    private String longitude;
    private String dataFoto;
    private Boolean latitudeLongitudeObrigatorio = Boolean.FALSE;
    private Boolean fotoAnexada = Boolean.FALSE;

    private PanelFormatacaoMobile panelFormatacaoMobile;
    private PanelFormatacaoDesktop panelFormatacaoDesktop;

    private PanelFormatacaoAlfanumerica panelFormatacaoAlfanumerica;
    private PanelFormatacaoAnexo panelFormatacaoAnexo;
    private PanelFormatacaoBoleano panelFormatacaoBoleano;
    private PanelFormatacaoData panelFormatacaoData;
    private PanelFormatacaoIdentificadorUnico panelFormatacaoIdentificadorUnico;
    private PanelFormatacaoFoto panelFormatacaoFoto;
    private PanelFormatacaoNumerico panelFormatacaoNumerico;
    private PanelFormatacaoTexto panelFormatacaoTexto;
    private PanelFormatacaoVideo panelFormatacaoVideo;
    private PanelFormatacaoConformidadeItem panelFormatacaoConformidadeItem;
    private PanelBotoesAnteriorProximo panelBotoesAnteriorProximo;

    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;

    public CadastrarItemPage(PageParameters pageParameters, Page backPage, ObjetoFornecimentoContrato objetoFornecimentoContrato, int indiceFormatacaoContrato, EnumPerfilEntidade perfilUsuarioLogado, Boolean readOnly) {
        super(pageParameters);
        this.backPage = backPage;
        this.objetoFornecimentoContrato = objetoFornecimentoContrato;
        this.indiceFormatacaoContrato = indiceFormatacaoContrato;
        this.perfilUsuarioLogado = perfilUsuarioLogado;
        this.readOnly = readOnly;

        // recupera da sessao do usuario logado, a resolucao do dispositivo
        this.isMobile = (Boolean) getSessionAttribute("isMobile");

        initVariaveis();
        initComponents();
        setTitulo(this.objetoFornecimentoContrato.getItem().getNomeBem());
        if (isMobile) {
            exibiPainelTipoFormatacao();
        }
    }

    private void initVariaveis() {
        buscaListaFormatacoes();
        buscaToltalFormatacoes();
        setaListaOrientacaoFornecedor();
        buscaFormatacaoObjetoFornecimento();
        buscaFormatacoesRespostas();
    }

    private void initComponents() {
        form = componentFactory.newForm("form", new CompoundPropertyModel<FormatacaoItensContratoResposta>(formatacaoItensContratoResposta));
        form.setMultiPart(Boolean.TRUE);// botao PROXIMO no mobile ficar ativo

        form.add(componentFactory.newLabel("txtDescricaoItem", this.objetoFornecimentoContrato.getItem().getDescricaoBem()));
        form.add(panelFormatacaoMobile = new PanelFormatacaoMobile("panelFormatacaoMobile"));
        form.add(panelFormatacaoDesktop = new PanelFormatacaoDesktop("panelFormatacaoDesktop"));
        form.add(newButtonConferencia());

        add(form);
    }

    // componentes

    private ContextImage newImageFotoPadrao() {
        ContextImage image = new ContextImage("imagemFoto", "images/sem-imagem.png");
        image.setOutputMarkupId(Boolean.TRUE);
        return image;
    }

    private NonCachingImage newImageFoto(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        return new NonCachingImage("imagemFoto", new AbstractReadOnlyModel<DynamicImageResource>() {
            private static final long serialVersionUID = 1L;

            @Override
            public DynamicImageResource getObject() {
                DynamicImageResource dir = new DynamicImageResource() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected byte[] getImageData(Attributes attributes) {
                        return formatacaoItensContratoResposta.getConteudo();
                    }
                };
                String extensaoArquivo = FilenameUtils.getExtension(formatacaoItensContratoResposta.getNomeAnexo());
                dir.setFormat("image/".concat(extensaoArquivo));
                return dir;
            }
        });
    }

    private Label newLabelTituloQuesito(FormatacaoItensContrato formatacaoItensContrato) {
        Label lblTituloQuesito = componentFactory.newLabel("txtLabelTituloQuesito", formatacaoItensContrato.getPossuiInformacaoOpcional() ? formatacaoItensContrato.getTituloQuesito() : "* ".concat(formatacaoItensContrato.getTituloQuesito()));
        lblTituloQuesito.setOutputMarkupId(Boolean.TRUE);
        return lblTituloQuesito;
    }

    private Label newLabelIdentificadorUnico(FormatacaoItensContrato formatacaoItensContrato) {
        Label lblTituloQuesito = componentFactory.newLabel("txtLabelIdentificadorUnico", formatacaoItensContrato.getPossuiInformacaoOpcional() ? "Identificador único" : "* ".concat("Identificador único"));
        lblTituloQuesito.setOutputMarkupId(Boolean.TRUE);
        return lblTituloQuesito;
    }

    private Label newLabelNomeAnexo(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        Label lblNomeAnexo = componentFactory.newLabel("txtLabelNomeAnexo", formatacaoItensContratoResposta.getNomeAnexo());
        lblNomeAnexo.setOutputMarkupId(Boolean.TRUE);
        lblNomeAnexo.setVisible(formatacaoItensContratoResposta.getNomeAnexo() != null);
        return lblNomeAnexo;
    }

    private Label newLabelTamanhoAnexo(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        BigDecimal tamanhoAnexo = getTamanhoArquivoEmMB(formatacaoItensContratoResposta);
        Label lblTamanhoAnexo = new Label("txtLabelTamanhoAnexo", tamanhoAnexo + "MB");
        lblTamanhoAnexo.setOutputMarkupId(Boolean.TRUE);
        lblTamanhoAnexo.setVisible(tamanhoAnexo.compareTo(BigDecimal.ZERO) != 0);
        return lblTamanhoAnexo;
    }

    private Button newButtonDownload(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        Button btnDownload = componentFactory.newButton("btnDonwload", () -> download(formatacaoItensContratoResposta));
        btnDownload.setVisible(formatacaoItensContratoResposta.getNomeAnexo() != null);
        return btnDownload;
    }

    private void download(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        if (formatacaoItensContratoResposta.getId() != null) {
            FormatacaoItensContratoRespostaDto anexo = ordemFornecimentoContratoService.buscarFormatacaoItensContratoRespostaDownloadPeloId(formatacaoItensContratoResposta.getId());
            SideUtil.download(anexo.getConteudo(), anexo.getNomeAnexo());
        } else {
            SideUtil.download(formatacaoItensContratoResposta.getConteudo(), formatacaoItensContratoResposta.getNomeAnexo());
        }
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

    private Label newLabelPaginacao() {
        int cont = indiceFormatacaoContrato;
        return componentFactory.newLabel("txtLabelPaginacao", Integer.toString(++cont).concat(" / ").concat(Integer.toString(totalFormatacoes)));
    }

    private AjaxSubmitLink newButtonAnterior() {
        buttonAnterior = new AjaxSubmitLink("btnAnterior", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                actionButtonProximoAnterior(target, buttonAnterior);
            }
        };
        buttonAnterior.setOutputMarkupId(Boolean.TRUE);
        if (indiceFormatacaoContrato <= 0) {
            mostrarBotaoAnterior = Boolean.FALSE;
        } else {
            mostrarBotaoAnterior = Boolean.TRUE;
        }
        buttonAnterior.setVisible(mostrarBotaoAnterior);
        return buttonAnterior;
    }

    private AjaxSubmitLink newButtonProximo() {
        buttonProximo = new AjaxSubmitLink("btnProximo", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                actionButtonProximoAnterior(target, buttonProximo);
            }
        };
        buttonProximo.setOutputMarkupId(true);
        int cont = indiceFormatacaoContrato;
        if (++cont == totalFormatacoes) {
            mostrarBotaoProximo = Boolean.FALSE;
        } else {
            mostrarBotaoProximo = Boolean.TRUE;
        }
        buttonProximo.setOutputMarkupId(Boolean.TRUE);
        buttonProximo.setVisible(mostrarBotaoProximo);
        return buttonProximo;
    }

    private Button newButtonSalvarDesktop() {
        return new AjaxButton("btnSalvarDesktop") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                salvarDesktop(target);
            }
        };
    }

    private Button newButtonSalvarMobile() {
        Button btnSalvar = componentFactory.newButton("btnSalvarMobile", () -> salvarMobile());
        btnSalvar.setVisible(!mostrarBotaoAnexar);
        return btnSalvar;
    }

    private InfraAjaxFallbackLink<Void> newButtonConferencia() {
        InfraAjaxFallbackLink<Void> btnConferencia = componentFactory.newAjaxFallbackLink("btnConferencia", (target) -> voltar());

        if (isMobile) {
            btnConferencia.add(new Label("btnNomeConferencia", ""));
        } else {
            btnConferencia.add(new Label("btnNomeConferencia", "Conferência"));
        }
        btnConferencia.setOutputMarkupId(Boolean.TRUE);
        return btnConferencia;
    }

    private TextArea<String> newTextAreaTexto(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        TextArea<String> textArea = new TextArea<String>("textAreaTexto", new PropertyModel<String>(formatacaoItensContratoResposta, "respostaTexto"));
        textArea.setLabel(Model.of("Texto"));
        actionTextFieldArea(formatacaoItensContratoResposta, textArea);
        return textArea;
    }

    private TextArea<String> newTextAreaJustificativaNaoConfigurado() {
        TextArea<String> textArea = new TextArea<String>("textAreaJustificativaNaoConfigurado", new PropertyModel<String>(objetoFornecimentoContrato, "descricaoNaoConfiguradoDeAcordo"));
        textArea.setLabel(Model.of("Justificativa se o item está configurado de acordo."));
        textArea.setVisible(exibirTextJustificativaNaoConfigurado());
        actionTextFieldAreaJustificativaConfigurado(textArea);
        return textArea;
    }

    private TextArea<String> newTextAreaJustificativaNaoFunciona() {
        TextArea<String> textArea = new TextArea<String>("textAreaJustificativaNaoFunciona", new PropertyModel<String>(objetoFornecimentoContrato, "descricaoNaoFuncionandoDeAcordo"));
        textArea.setLabel(Model.of("Justificativa se o item está funcionando de acordo."));
        textArea.setVisible(exibirTextJustificativaNaoFunciona());
        actionTextFieldAreaJustificativaFunciona(textArea);
        return textArea;
    }

    private TextField<String> newTextFieldNumerico(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        TextField<String> textField = componentFactory.newTextField("textoNumerico", "Número", Boolean.FALSE, new PropertyModel<String>(formatacaoItensContratoResposta, "respostaAlfanumerico"));
        textField.setOutputMarkupId(Boolean.TRUE);
        actionTextFieldAlfanumerico(formatacaoItensContratoResposta, textField);
        return textField;
    }

    private TextField<String> newTextFieldTextoIdentificadorUinico(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        TextField<String> textField = componentFactory.newTextField("textoIdentificadorUnico", "Identificador Único", Boolean.FALSE, new PropertyModel<String>(formatacaoItensContratoResposta, "respostaAlfanumerico"));
        textField.setOutputMarkupId(Boolean.TRUE);
        actionTextFieldAlfanumerico(formatacaoItensContratoResposta, textField);
        return textField;
    }

    private InfraLocalDateTextField newDateTextFieldData(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        InfraLocalDateTextField dataTextFiel = componentFactory.newDateTextFieldWithDatePicker("data", "Data", Boolean.FALSE, new PropertyModel<LocalDate>(formatacaoItensContratoResposta, "respostaData"), "dd/MM/yyyy", "pt-BR");
        dataTextFiel.setOutputMarkupId(Boolean.TRUE);
        actionTextFieldData(formatacaoItensContratoResposta, dataTextFiel);
        return dataTextFiel;
    }

    private Label newLabelOrientacaoFornecedor(FormatacaoItensContrato formatacaoItensContrato) {
        Label lblOrientacaoFornecedor = componentFactory.newLabel("txtLabelOrientacaoFornecedor", formatacaoItensContrato.getOrientacaoFornecedores());
        lblOrientacaoFornecedor.setOutputMarkupId(Boolean.TRUE);
        return lblOrientacaoFornecedor;
    }

    private TextField<String> newTextFieldTextoAlfanumerico(FormatacaoItensContratoResposta formatacaoItensContratoResposta, FormatacaoItensContrato formatacaoItensContrato) {
        TextField<String> textField = componentFactory.newTextField("textoAlfanumerico", "Texto", Boolean.FALSE, new PropertyModel<String>(formatacaoItensContratoResposta, "respostaAlfanumerico"));
        textField.setOutputMarkupId(Boolean.TRUE);
        actionTextFieldAlfanumerico(formatacaoItensContratoResposta, textField);
        return textField;
    }

    private InfraRadioChoice<Boolean> newRadioBoleano(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        InfraRadioChoice<Boolean> radioChoiceLocal = componentFactory.newRadioChoice("radioBoleano", "Opção", Boolean.TRUE, Boolean.FALSE, "", "", new PropertyModel<Boolean>(formatacaoItensContratoResposta, "respostaBooleana"), Arrays.asList(Boolean.TRUE, Boolean.FALSE),
                (target) -> setarVariavel(formatacaoItensContratoResposta));
        radioChoiceLocal.setChoiceRenderer(new IChoiceRenderer<Boolean>() {
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
        radioChoiceLocal.setOutputMarkupId(Boolean.TRUE);
        return radioChoiceLocal;
    }

    private InfraRadioChoice<Boolean> newRadioItemNovo() {
        InfraRadioChoice<Boolean> radioChoiceLocal = componentFactory.newRadioChoice("radioItemNovo", "Conformidade: Item novo", Boolean.TRUE, Boolean.FALSE, "", "", new PropertyModel<Boolean>(objetoFornecimentoContrato, "estadoDeNovo"), Arrays.asList(Boolean.TRUE, Boolean.FALSE),
                (target) -> atualizaPanelConformidade(target));
        radioChoiceLocal.setChoiceRenderer(new IChoiceRenderer<Boolean>() {
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
        return radioChoiceLocal;
    }

    private InfraRadioChoice<Boolean> newRadioFunciona() {
        InfraRadioChoice<Boolean> radioChoiceLocal = componentFactory.newRadioChoice("radioFunciona", "Conformidade: Funciona de acordo", Boolean.TRUE, Boolean.FALSE, "", "", new PropertyModel<Boolean>(objetoFornecimentoContrato, "funcionandoDeAcordo"), Arrays.asList(Boolean.TRUE, Boolean.FALSE), (
                target) -> atualizaPanelConformidade(target));
        radioChoiceLocal.setChoiceRenderer(new IChoiceRenderer<Boolean>() {
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
        return radioChoiceLocal;
    }

    private InfraRadioChoice<Boolean> newRadioConfigurado() {
        InfraRadioChoice<Boolean> radioChoiceLocal = componentFactory.newRadioChoice("radioConfigurado", "Conformidade: Configurado de acordo", Boolean.TRUE, Boolean.FALSE, "", "", new PropertyModel<Boolean>(objetoFornecimentoContrato, "configuradoDeAcordo"),
                Arrays.asList(Boolean.TRUE, Boolean.FALSE), (target) -> atualizaPanelConformidade(target));
        radioChoiceLocal.setChoiceRenderer(new IChoiceRenderer<Boolean>() {
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
        return radioChoiceLocal;
    }

    private AjaxSubmitLink newButtonAdicionarAnexoMobile() {
        AjaxSubmitLink btnAdicionarAnexoMobile = new AjaxSubmitLink("btnAdicionarAnexoMobile") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                adicionarAnexoMobile(target);
            }
        };
        Label lblDescricao = new Label("icoBtnAdicionarAlterar", formatacaoItensContratoResposta.getNomeAnexo() == null ? "<i class=\"fa fa-plus\"></i>" : "<i class=\"fa fa-exchange\"></i>");
        lblDescricao.setEscapeModelStrings(false);
        lblDescricao.setOutputMarkupId(true);

        btnAdicionarAnexoMobile.add(lblDescricao);
        btnAdicionarAnexoMobile.setOutputMarkupId(true);
        return btnAdicionarAnexoMobile;
    }

    private AjaxSubmitLink newButtonAdicionarAnexoDesktop(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        AjaxSubmitLink btnAdicionarAnexoDesktop = new AjaxSubmitLink("btnAdicionarAnexoDesktop") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                adicionarAnexoDesktop(target, formatacaoItensContratoResposta);
            }
        };
        Label lblDescricao = new Label("icoBtnAdicionarAlterar", formatacaoItensContratoResposta.getNomeAnexo() == null ? "<i class=\"fa fa-plus\"></i>" : "<i class=\"fa fa-exchange\"></i>");
        lblDescricao.setEscapeModelStrings(false);
        lblDescricao.setOutputMarkupId(true);

        btnAdicionarAnexoDesktop.add(componentFactory.newLabel("txtBtnAdicionarAlterar", formatacaoItensContratoResposta.getNomeAnexo() == null ? "Adicionar" : "Substituir"));
        btnAdicionarAnexoDesktop.add(lblDescricao);
        btnAdicionarAnexoDesktop.setOutputMarkupId(true);
        return btnAdicionarAnexoDesktop;
    }

    private FileUploadForm getFormUpload(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        return new FileUploadForm("formUpload", new PropertyModel<List<FileUpload>>(this, "uploads"), formatacaoItensContratoResposta);
    }

    // acoes

    // mobile e desktop: atualiza o panel da conformidade quando seleciona as
    // opções.
    private void atualizaPanelConformidade(AjaxRequestTarget target) {

        panelFormatacaoMobile.addOrReplace(panelFormatacaoConformidadeItem = new PanelFormatacaoConformidadeItem("panelFormatacaoConformidadeItem"));
        target.add(panelFormatacaoMobile);

        panelFormatacaoDesktop.addOrReplace(panelFormatacaoConformidadeItem = new PanelFormatacaoConformidadeItem("panelFormatacaoConformidadeItem"));
        target.add(panelFormatacaoDesktop);
    }

    // mobile e desktop: redimenciona imagem anexada.
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

    // mobile e desktop: busca e insere na lista, todas os
    // FormatacaoObjetoFornecimento do item.
    private void buscaListaFormatacoes() {
        listaFormatacaoSALVAR.clear();
        listaFormatacaoSALVAR.addAll(SideUtil.convertDtoToEntityFormatacaoObjetoFornecimentoDto(this.ordemFornecimentoContratoService.buscarListaFormatacaoObjetoFornecimento(this.objetoFornecimentoContrato.getId(), perfilUsuarioLogado)));

        // ordenacao da lista.
        listaFormatacaoSALVAR.sort((FormatacaoObjetoFornecimento o1, FormatacaoObjetoFornecimento o2) -> o1.getId().compareTo(o2.getId()));
    }

    // mobile: busca o total de formatacoes encotradas na lista.
    private void buscaToltalFormatacoes() {
        int cont = listaFormatacaoSALVAR.size();
        if (perfilUsuarioLogado.equals(EnumPerfilEntidade.BENEFICIARIO)) {
            ++cont;
        }
        totalFormatacoes = cont;
    }

    // desktop: insere na lista as orientacoes ao fornecedor.
    private void setaListaOrientacaoFornecedor() {
        for (int i = 0; i < listaFormatacaoSALVAR.size(); i++) {
            listaFormatacaoOrientacao.add(listaFormatacaoSALVAR.get(i).getFormatacao().getOrientacaoFornecedores());
        }
    }

    // mobile: busca a FormatacaoObjetoFornecimento.
    private void buscaFormatacaoObjetoFornecimento() {
        int cont = indiceFormatacaoContrato;
        if (perfilUsuarioLogado.equals(EnumPerfilEntidade.BENEFICIARIO)) {
            ++cont;
        }
        if (cont != totalFormatacoes) {
            formatacaoObjetoFornecimento = listaFormatacaoSALVAR.get(indiceFormatacaoContrato);
        } else {
            int contQuesito = indiceFormatacaoContrato;
            formatacaoObjetoFornecimento = listaFormatacaoSALVAR.get(--contQuesito);
        }
    }

    // desktop: separa as formatacoes em listas para cada tipo de formatacao.
    private void buscaFormatacoesRespostas() {
        for (int i = 0; i < listaFormatacaoSALVAR.size(); i++) {
            if (listaFormatacaoSALVAR.get(i).getFormatacao().getTipoCampo().getValor().equals(EnumTipoCampoFormatacao.ALFANUMERICO.getValor())) {
                if (listaFormatacaoSALVAR.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoSALVAR.get(i).getFormatacao());
                    listaRespostaFormatacaoAlfanumerico.add(resposta);
                } else {
                    listaRespostaFormatacaoAlfanumerico.add(listaFormatacaoSALVAR.get(i).getFormatacaoResposta());
                }
            }
            if (listaFormatacaoSALVAR.get(i).getFormatacao().getTipoCampo().getValor().equals(EnumTipoCampoFormatacao.ANEXO.getValor())) {
                if (listaFormatacaoSALVAR.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoSALVAR.get(i).getFormatacao());
                    listaRespostaFormatacaoAnexo.add(resposta);
                } else {
                    listaRespostaFormatacaoAnexo.add(listaFormatacaoSALVAR.get(i).getFormatacaoResposta());
                }
            }
            if (listaFormatacaoSALVAR.get(i).getFormatacao().getTipoCampo().getValor().equals(EnumTipoCampoFormatacao.BOLEANO.getValor())) {
                if (listaFormatacaoSALVAR.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoSALVAR.get(i).getFormatacao());
                    listaRespostaFormatacaoBoleano.add(resposta);
                } else {
                    listaRespostaFormatacaoBoleano.add(listaFormatacaoSALVAR.get(i).getFormatacaoResposta());
                }
            }
            if (listaFormatacaoSALVAR.get(i).getFormatacao().getTipoCampo().getValor().equals(EnumTipoCampoFormatacao.DATA.getValor())) {
                if (listaFormatacaoSALVAR.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoSALVAR.get(i).getFormatacao());
                    listaRespostaFormatacaoData.add(resposta);
                } else {
                    listaRespostaFormatacaoData.add(listaFormatacaoSALVAR.get(i).getFormatacaoResposta());
                }
            }
            if (listaFormatacaoSALVAR.get(i).getFormatacao().getTipoCampo().getValor().equals(EnumTipoCampoFormatacao.FOTO.getValor())) {
                if (listaFormatacaoSALVAR.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoSALVAR.get(i).getFormatacao());
                    listaRespostaFormatacaoFoto.add(resposta);
                } else {
                    listaRespostaFormatacaoFoto.add(listaFormatacaoSALVAR.get(i).getFormatacaoResposta());
                }
            }
            if (listaFormatacaoSALVAR.get(i).getFormatacao().getTipoCampo().getValor().equals(EnumTipoCampoFormatacao.NUMERICO.getValor())) {
                if (listaFormatacaoSALVAR.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoSALVAR.get(i).getFormatacao());
                    listaRespostaFormatacaoNumerico.add(resposta);
                } else {
                    listaRespostaFormatacaoNumerico.add(listaFormatacaoSALVAR.get(i).getFormatacaoResposta());
                }
            }
            if (listaFormatacaoSALVAR.get(i).getFormatacao().getTipoCampo().getValor().equals(EnumTipoCampoFormatacao.TEXTO.getValor())) {
                if (listaFormatacaoSALVAR.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoSALVAR.get(i).getFormatacao());
                    listaRespostaFormatacaoTexto.add(resposta);
                } else {
                    listaRespostaFormatacaoTexto.add(listaFormatacaoSALVAR.get(i).getFormatacaoResposta());
                }
            }
            if (listaFormatacaoSALVAR.get(i).getFormatacao().getTipoCampo().getValor().equals(EnumTipoCampoFormatacao.VIDEO.getValor())) {
                if (listaFormatacaoSALVAR.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoSALVAR.get(i).getFormatacao());
                    listaRespostaFormatacaoVideo.add(resposta);
                } else {
                    listaRespostaFormatacaoVideo.add(listaFormatacaoSALVAR.get(i).getFormatacaoResposta());
                }
            }
        }
    }

    protected void voltar() {
        setResponsePage(new ConferenciaCadastrarItemPage(getPageParameters(), backPage, objetoFornecimentoContrato.getId()));
    }

    private void salvarMobile() {

        if (latitudeLongitudeObrigatorio && latitude == null && longitude == null) {
            getSession().error("É necessario habilitar a localização em seu navegador (GPS).");
        } else {

            formatacaoItensContratoResposta.setFormatacao(formatacaoObjetoFornecimento.getFormatacao());
            formatacaoObjetoFornecimento.setFormatacaoResposta(formatacaoItensContratoResposta);
            formatacaoObjetoFornecimento.setObjetoFornecimento(objetoFornecimentoContrato);

            String msg = new String();

            msg = validaCamposMobile(formatacaoObjetoFornecimento);

            if (formatacaoObjetoFornecimento.getFormatacao().getPossuiGPS() && latitude == null && longitude == null) {
                getSession().error("É necessario habilitar a localização em seu navegador (GPS).");
            } else if (msg.equals("")) {
                formatacaoObjetoFornecimento = this.ordemFornecimentoContratoService.incluirAlterarFormatacaoItensContratoResposta(formatacaoObjetoFornecimento);
                buscaListaFormatacoes();

                getSession().info("Resposta cadastrada com sucesso");
            } else {
                getSession().error(msg);
            }

        }

    }

    // mobile: valida o objeto antes de persistir.
    private String validaCamposMobile(FormatacaoObjetoFornecimento formatacaoObjetoFornecimento) {

        String msg = new String();

        EnumTipoCampoFormatacao tipo = formatacaoObjetoFornecimento.getFormatacao().getTipoCampo();

        if (tipo.getValor().equals(EnumTipoCampoFormatacao.ALFANUMERICO.getValor())) {
            if (formatacaoObjetoFornecimento.getFormatacaoResposta().getRespostaAlfanumerico() == null) {
                msg = "Informe o quesito '".concat(formatacaoObjetoFornecimento.getFormatacao().getTituloQuesito()).concat("'.");
            } else if (!formatacaoObjetoFornecimento.getFormatacao().getPossuiInformacaoOpcional() && formatacaoObjetoFornecimento.getFormatacaoResposta().getRespostaAlfanumerico() == null) {
                msg = "O quesito '".concat(formatacaoObjetoFornecimento.getFormatacao().getTituloQuesito()).concat("' é obrigatório.");
            }
        }
        if (tipo.getValor().equals(EnumTipoCampoFormatacao.ANEXO.getValor())) {
            if (formatacaoObjetoFornecimento.getFormatacaoResposta().getNomeAnexo() == null) {
                msg = "Informe o quesito '".concat(formatacaoObjetoFornecimento.getFormatacao().getTituloQuesito()).concat("'.");
            } else if (!formatacaoObjetoFornecimento.getFormatacao().getPossuiInformacaoOpcional() && formatacaoObjetoFornecimento.getFormatacaoResposta().getNomeAnexo() == null) {
                msg = "O quesito '".concat(formatacaoObjetoFornecimento.getFormatacao().getTituloQuesito()).concat("' é obrigatório.");
            }
        }
        if (tipo.getValor().equals(EnumTipoCampoFormatacao.BOLEANO.getValor())) {
            if (formatacaoObjetoFornecimento.getFormatacaoResposta().getRespostaBooleana() == null) {
                msg = "Informe o quesito '".concat(formatacaoObjetoFornecimento.getFormatacao().getTituloQuesito()).concat("'.");
            } else if (!formatacaoObjetoFornecimento.getFormatacao().getPossuiInformacaoOpcional() && formatacaoObjetoFornecimento.getFormatacaoResposta().getRespostaBooleana() == null) {
                msg = "O quesito '".concat(formatacaoObjetoFornecimento.getFormatacao().getTituloQuesito()).concat("' é obrigatório.");
            }
        }
        if (tipo.getValor().equals(EnumTipoCampoFormatacao.DATA.getValor())) {
            if (formatacaoObjetoFornecimento.getFormatacaoResposta().getRespostaData() == null) {
                msg = "Informe o quesito '".concat(formatacaoObjetoFornecimento.getFormatacao().getTituloQuesito()).concat("'.");
            } else if (!formatacaoObjetoFornecimento.getFormatacao().getPossuiInformacaoOpcional() && formatacaoObjetoFornecimento.getFormatacaoResposta().getRespostaData() == null) {
                msg = "O quesito '".concat(formatacaoObjetoFornecimento.getFormatacao().getTituloQuesito()).concat("' é obrigatório.");
            }
        }
        if (tipo.getValor().equals(EnumTipoCampoFormatacao.FOTO.getValor())) {
            if (formatacaoObjetoFornecimento.getFormatacaoResposta().getConteudo() == null) {
                msg = "Informe o quesito '".concat(formatacaoObjetoFornecimento.getFormatacao().getTituloQuesito()).concat("'.");
            } else if (!formatacaoObjetoFornecimento.getFormatacao().getPossuiInformacaoOpcional() && formatacaoObjetoFornecimento.getFormatacaoResposta().getNomeAnexo() == null) {
                msg = "O quesito '".concat(formatacaoObjetoFornecimento.getFormatacao().getTituloQuesito()).concat("' é obrigatório.");
            }
        }
        if (tipo.getValor().equals(EnumTipoCampoFormatacao.NUMERICO.getValor())) {
            if (formatacaoObjetoFornecimento.getFormatacaoResposta().getRespostaAlfanumerico() == null) {
                msg = "Informe o quesito '".concat(formatacaoObjetoFornecimento.getFormatacao().getTituloQuesito()).concat("'.");
            } else if (!formatacaoObjetoFornecimento.getFormatacao().getPossuiInformacaoOpcional() && formatacaoObjetoFornecimento.getFormatacaoResposta().getRespostaAlfanumerico() == null) {
                msg = "O quesito '".concat(formatacaoObjetoFornecimento.getFormatacao().getTituloQuesito()).concat("' é obrigatório.");
            }
        }
        if (tipo.getValor().equals(EnumTipoCampoFormatacao.TEXTO.getValor())) {
            if (formatacaoObjetoFornecimento.getFormatacaoResposta().getRespostaTexto() == null) {
                msg = "Informe o quesito '".concat(formatacaoObjetoFornecimento.getFormatacao().getTituloQuesito()).concat("'.");
            } else if (!formatacaoObjetoFornecimento.getFormatacao().getPossuiInformacaoOpcional() && formatacaoObjetoFornecimento.getFormatacaoResposta().getRespostaTexto() == null) {
                msg = "O quesito '".concat(formatacaoObjetoFornecimento.getFormatacao().getTituloQuesito()).concat("' é obrigatório.");
            }
        }
        if (tipo.getValor().equals(EnumTipoCampoFormatacao.VIDEO.getValor())) {
            if (formatacaoObjetoFornecimento.getFormatacaoResposta().getNomeAnexo() == null) {
                msg = "Informe o quesito '".concat(formatacaoObjetoFornecimento.getFormatacao().getTituloQuesito()).concat("'.");
            } else if (!formatacaoObjetoFornecimento.getFormatacao().getPossuiInformacaoOpcional() && formatacaoObjetoFornecimento.getFormatacaoResposta().getNomeAnexo() == null) {
                msg = "O quesito '".concat(formatacaoObjetoFornecimento.getFormatacao().getTituloQuesito()).concat("' é obrigatório.");
            }
        }
        if (perfilUsuarioLogado.equals(EnumPerfilEntidade.BENEFICIARIO) && totalFormatacoes == indiceFormatacaoContrato) {
            if (objetoFornecimentoContrato.getEstadoDeNovo() == null) {
                msg = "Por favor, preencha o quesito 'O item está em estado de novo?'";
            }
            if (objetoFornecimentoContrato.getFuncionandoDeAcordo() == null) {
                msg = "Por favor, preencha o quesito 'O item está funcionando de acordo com as especificações?'";
            } else if (!objetoFornecimentoContrato.getFuncionandoDeAcordo() && objetoFornecimentoContrato.getDescricaoNaoFuncionandoDeAcordo() == null) {
                msg = "Por favor, preencha a justificativa do quesito 'O item está funcionando de acordo com as especificações?'";
            }
            if (objetoFornecimentoContrato.getConfiguradoDeAcordo() == null) {
                msg = "Por favor, preencha o quesito 'O item está configurado de acordo com as especificações?'";
            } else if (!objetoFornecimentoContrato.getConfiguradoDeAcordo() && objetoFornecimentoContrato.getDescricaoNaoConfiguradoDeAcordo() == null) {
                msg = "Por favor, preencha a justificativa do quesito 'O item está configurado de acordo com as especificações?'";
            }
        }
        return msg;
    }

    private void salvarDesktop(AjaxRequestTarget target) {
        limpaRespostaNull();

        String msg = new String();

        msg = validaListaeConformidade();

        if (msg.equals("")) {
            objetoFornecimentoContrato.setListaFormatacaoObjetoFornecimento(listaFormatacaoSALVAR);
            objetoFornecimentoContrato = this.ordemFornecimentoContratoService.incluirAlterarFormatacaoItensContratoRespostaDesktop(objetoFornecimentoContrato);
            getSession().info("Resposta cadastrada com sucesso.");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        } else {
            getSession().error(msg);

            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        }
    }

    // desktop: valida a lista e conformidade dos itens
    private String validaListaeConformidade() {
        String msg = new String();

        // Lista
        for (FormatacaoObjetoFornecimento formatacao : listaFormatacaoSALVAR) {
            if (formatacao.getFormatacaoResposta() == null && !formatacao.getFormatacao().getPossuiInformacaoOpcional()) {
                msg = "Para salvar é necessário preencher os campos";
            } else if (formatacao.getFormatacaoResposta() != null) {
                msg = "";
                break;
            }
        }

        // Conformidade Itens
        if (perfilUsuarioLogado.equals(EnumPerfilEntidade.BENEFICIARIO)) {
            if (!msg.equalsIgnoreCase("")) {
                if (objetoFornecimentoContrato.getEstadoDeNovo() == null && objetoFornecimentoContrato.getFuncionandoDeAcordo() == null && objetoFornecimentoContrato.getConfiguradoDeAcordo() == null) {
                    msg = "Para salvar é necessário preencher os campos";
                } else {
                    msg = validaConformidade();
                }
            }
        }

        if (msg.equals("")) {
            for (FormatacaoObjetoFornecimento formatacao : listaFormatacaoSALVAR) {
                if (formatacao.getFormatacaoResposta() != null) {
                    if (formatacao.getFormatacao().getTipoCampo().equals(EnumTipoCampoFormatacao.FOTO) && formatacao.getFormatacao().getPossuiIdentificadorUnico()) {
                        if (formatacao.getFormatacaoResposta().getRespostaAlfanumerico() == null) {
                            msg = "Informe o identificador único da foto.";
                        }
                    }
                }
            }
        }

        return msg;
    }

    private String validaConformidade() {
        String retorno = new String();

        if (perfilUsuarioLogado.equals(EnumPerfilEntidade.BENEFICIARIO)) {
            if (objetoFornecimentoContrato.getFuncionandoDeAcordo() != null) {
                if (!objetoFornecimentoContrato.getFuncionandoDeAcordo()) {
                    if (objetoFornecimentoContrato.getDescricaoNaoFuncionandoDeAcordo() == null && !"".equalsIgnoreCase(objetoFornecimentoContrato.getDescricaoNaoFuncionandoDeAcordo())) {
                        retorno = "Por favor, preencha a justificativa do quesito 'O item está funcionando de acordo com as especificações?'";
                    }
                } else {
                    objetoFornecimentoContrato.setDescricaoNaoFuncionandoDeAcordo(new String());
                }
            }
            if (objetoFornecimentoContrato.getConfiguradoDeAcordo() != null) {
                if (!objetoFornecimentoContrato.getConfiguradoDeAcordo()) {
                    if (objetoFornecimentoContrato.getDescricaoNaoConfiguradoDeAcordo() == null && !"".equalsIgnoreCase(objetoFornecimentoContrato.getDescricaoNaoConfiguradoDeAcordo())) {
                        retorno = "Por favor, preencha a justificativa do quesito 'O item está configurado de acordo com as especificações?'";
                    }
                } else {
                    objetoFornecimentoContrato.setDescricaoNaoConfiguradoDeAcordo(new String());
                }
            }
        }

        return retorno;
    }

    private boolean verificaRespostaSalva() {
        EnumTipoCampoFormatacao tipo = this.formatacaoObjetoFornecimento.getFormatacao().getTipoCampo();
        String msg = "Seus dados não foram salvos, para saí, é necessario salvar.";
        Boolean retorno = Boolean.TRUE;

        if (tipo.getValor().equals(EnumTipoCampoFormatacao.ALFANUMERICO.getValor())) {
            if (formatacaoItensContratoResposta.getRespostaAlfanumerico() != null && formatacaoItensContratoResposta.getId() == null) {
                getSession().error(msg);
                retorno = Boolean.FALSE;
            }
        }
        if (tipo.getValor().equals(EnumTipoCampoFormatacao.ANEXO.getValor())) {
            if (formatacaoItensContratoResposta.getNomeAnexo() != null && formatacaoItensContratoResposta.getId() == null) {
                getSession().error(msg);
                retorno = Boolean.FALSE;
            }
        }
        if (tipo.getValor().equals(EnumTipoCampoFormatacao.BOLEANO.getValor())) {
            if (formatacaoItensContratoResposta.getRespostaBooleana() != null && formatacaoItensContratoResposta.getId() == null) {
                getSession().error(msg);
                retorno = Boolean.FALSE;
            }
        }
        if (tipo.getValor().equals(EnumTipoCampoFormatacao.DATA.getValor())) {
            if (formatacaoItensContratoResposta.getRespostaData() != null && formatacaoItensContratoResposta.getId() == null) {
                getSession().error(msg);
                retorno = Boolean.FALSE;
            }
        }
        if (tipo.getValor().equals(EnumTipoCampoFormatacao.FOTO.getValor())) {
            if (formatacaoItensContratoResposta.getNomeAnexo() != null && formatacaoItensContratoResposta.getId() == null) {
                getSession().error(msg);
                retorno = Boolean.FALSE;
            }
        }
        if (tipo.getValor().equals(EnumTipoCampoFormatacao.NUMERICO.getValor())) {
            if (formatacaoItensContratoResposta.getRespostaAlfanumerico() != null && formatacaoItensContratoResposta.getId() == null) {
                getSession().error(msg);
                retorno = Boolean.FALSE;
            }
        }
        if (tipo.getValor().equals(EnumTipoCampoFormatacao.TEXTO.getValor())) {
            if (formatacaoItensContratoResposta.getRespostaTexto() != null && formatacaoItensContratoResposta.getId() == null) {
                getSession().error(msg);
                retorno = Boolean.FALSE;
            }
        }
        if (tipo.getValor().equals(EnumTipoCampoFormatacao.VIDEO.getValor())) {
            if (formatacaoItensContratoResposta.getNomeAnexo() != null && formatacaoItensContratoResposta.getId() == null) {
                getSession().error(msg);
                retorno = Boolean.FALSE;
            }
        }
        return retorno;
    }

    // desktop: seta para null as resposta nao preenchidas.
    private void limpaRespostaNull() {
        for (FormatacaoObjetoFornecimento formatacao : listaFormatacaoSALVAR) {
            EnumTipoCampoFormatacao tipo = formatacao.getFormatacao().getTipoCampo();

            if (tipo.getValor().equals(EnumTipoCampoFormatacao.ALFANUMERICO.getValor())) {
                if (formatacao.getFormatacaoResposta() == null) {
                    formatacao.setFormatacaoResposta(null);
                } else if (formatacao.getFormatacaoResposta().getRespostaAlfanumerico() == null) {
                    formatacao.setFormatacaoResposta(null);
                }
            }
            if (tipo.getValor().equals(EnumTipoCampoFormatacao.ANEXO.getValor())) {
                if (formatacao.getFormatacaoResposta() == null) {
                    formatacao.setFormatacaoResposta(null);
                } else if (formatacao.getFormatacaoResposta().getNomeAnexo() == null) {
                    formatacao.setFormatacaoResposta(null);
                }
            }
            if (tipo.getValor().equals(EnumTipoCampoFormatacao.BOLEANO.getValor())) {
                if (formatacao.getFormatacaoResposta() == null) {
                    formatacao.setFormatacaoResposta(null);
                } else if (formatacao.getFormatacaoResposta().getRespostaBooleana() == null) {
                    formatacao.setFormatacaoResposta(null);
                }
            }
            if (tipo.getValor().equals(EnumTipoCampoFormatacao.DATA.getValor())) {
                if (formatacao.getFormatacaoResposta() == null) {
                    formatacao.setFormatacaoResposta(null);
                } else if (formatacao.getFormatacaoResposta().getRespostaData() == null) {
                    formatacao.setFormatacaoResposta(null);
                }
            }
            if (tipo.getValor().equals(EnumTipoCampoFormatacao.FOTO.getValor())) {
                if (formatacao.getFormatacaoResposta() == null) {
                    formatacao.setFormatacaoResposta(null);
                } else if (formatacao.getFormatacaoResposta().getConteudo() == null) {
                    formatacao.setFormatacaoResposta(null);
                }
            }
            if (tipo.getValor().equals(EnumTipoCampoFormatacao.NUMERICO.getValor())) {
                if (formatacao.getFormatacaoResposta() == null) {
                    formatacao.setFormatacaoResposta(null);
                } else if (formatacao.getFormatacaoResposta().getRespostaAlfanumerico() == null) {
                    formatacao.setFormatacaoResposta(null);
                }
            }
            if (tipo.getValor().equals(EnumTipoCampoFormatacao.TEXTO.getValor())) {
                if (formatacao.getFormatacaoResposta() == null) {
                    formatacao.setFormatacaoResposta(null);
                } else if (formatacao.getFormatacaoResposta().getRespostaTexto() == null) {
                    formatacao.setFormatacaoResposta(null);
                }
            }
            if (tipo.getValor().equals(EnumTipoCampoFormatacao.VIDEO.getValor())) {
                if (formatacao.getFormatacaoResposta() == null) {
                    formatacao.setFormatacaoResposta(null);
                } else if (formatacao.getFormatacaoResposta().getNomeAnexo() == null) {
                    formatacao.setFormatacaoResposta(null);
                }
            }
        }
    }

    // desktop: seta a latitude e longitude na resposta para o tipo FOTO.
    private void setaLatitudeLongitudeData(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        if (latitude != null && longitude != null) {
            if (!latitude.equalsIgnoreCase("undefined") && !longitude.equalsIgnoreCase("undefined")) {
                String latitudeTEMP = latitude.replace(".", "").replace(",", "");
                String longitudeTEMP = longitude.replace(".", "").replace(",", "");

                formatacaoItensContratoResposta.setLatitudeLongitudeFoto(latitudeTEMP.concat(" ").concat(longitudeTEMP));
            } else {
                formatacaoItensContratoResposta.setLatitudeLongitudeFoto(new String());
            }
        }

        if (dataFoto != null) {
            if (!dataFoto.equalsIgnoreCase("undefined")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
                LocalDateTime dataFotoFormatada = LocalDateTime.parse(dataFoto, formatter);
                formatacaoItensContratoResposta.setDataFoto(dataFotoFormatada);
            } else {
                formatacaoItensContratoResposta.setDataFoto(null);
            }
        }

    }

    // mobile: seta a latitude e longitude na resposta para o tipo FOTO.
    private void setaLatitudeLongitudeDataMobile() {
        if (latitude != null && longitude != null) {
            if (!latitude.equalsIgnoreCase("undefined") && !longitude.equalsIgnoreCase("undefined")) {
                String latitudeTEMP = latitude.replaceAll(".", "").replaceAll(",", "");
                String longitudeTEMP = longitude.replaceAll(".", "").replaceAll(",", "");

                formatacaoItensContratoResposta.setLatitudeLongitudeFoto(latitudeTEMP.concat(" ").concat(longitudeTEMP));
            } else {
                formatacaoItensContratoResposta.setLatitudeLongitudeFoto(new String());
            }
        }

        if (dataFoto != null) {
            if (!dataFoto.equalsIgnoreCase("undefined")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
                LocalDateTime dataFotoFormatada = LocalDateTime.parse(dataFoto, formatter);
                formatacaoItensContratoResposta.setDataFoto(dataFotoFormatada);
            } else {
                formatacaoItensContratoResposta.setDataFoto(null);
            }
        }

    }

    // desktop: pega o valor digitado no campo da data e seta a resposta na
    // lista para persistir.
    private void actionTextFieldData(FormatacaoItensContratoResposta formatacaoItensContratoResposta, InfraLocalDateTextField dataTextFiel) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                setaFormatacaoContratoResposta(formatacaoItensContratoResposta);
            }
        };
        dataTextFiel.add(onChangeAjaxBehavior);
    }

    // desktop: pega o valor digitado no text field e seta a resposta na
    // lista para persistir.
    private void actionTextFieldAlfanumerico(FormatacaoItensContratoResposta formatacaoItensContratoResposta, TextField<String> textField) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                setaFormatacaoContratoResposta(formatacaoItensContratoResposta);
            }
        };
        textField.add(onChangeAjaxBehavior);
    }

    // desktop: pega o valor digitado no text area e seta a resposta na
    // lista para persistir.
    private void actionTextFieldArea(FormatacaoItensContratoResposta formatacaoItensContratoResposta, TextArea<String> textField) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                setaFormatacaoContratoResposta(formatacaoItensContratoResposta);
            }
        };
        textField.add(onChangeAjaxBehavior);
    }

    // desktop e mobile: pega o valor digitado na justificativaConfigurado e
    // seta a resposta.
    private void actionTextFieldAreaJustificativaConfigurado(TextArea<String> textField) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                objetoFornecimentoContrato.setDescricaoNaoConfiguradoDeAcordo(textField.getConvertedInput());
            }
        };
        textField.add(onChangeAjaxBehavior);
    }

    // desktop e mobile: pega o valor digitado na justificativaFunciona e seta a
    // resposta.
    private void actionTextFieldAreaJustificativaFunciona(TextArea<String> textField) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                objetoFornecimentoContrato.setDescricaoNaoFuncionandoDeAcordo(textField.getConvertedInput());
            }
        };
        textField.add(onChangeAjaxBehavior);
    }

    // desktop: seta o objeto resposta na lista para ser persistido.
    private void setaFormatacaoContratoResposta(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        this.formatacaoItensContratoResposta = formatacaoItensContratoResposta;
        if (!isMobile) {
            for (int i = 0; i < listaFormatacaoSALVAR.size(); i++) {
                if (listaFormatacaoSALVAR.get(i).getFormatacao().equals(formatacaoItensContratoResposta.getFormatacao())) {
                    listaFormatacaoSALVAR.get(i).setFormatacaoResposta(formatacaoItensContratoResposta);
                }
            }
        }
    }

    // mobile e desktop: seta o valor selecionado no boolean no objeto resposta.
    private void setarVariavel(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        if (formatacaoItensContratoResposta == null) {
            formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
            // formatacaoItensContratoResposta.setRespostaBooleana(getInformacaoBoleana());
            setaFormatacaoContratoResposta(formatacaoItensContratoResposta);
        } else {
            // formatacaoItensContratoResposta.setRespostaBooleana(getInformacaoBoleana());
            setaFormatacaoContratoResposta(formatacaoItensContratoResposta);
        }
    }

    // mobile: soma ou subtrai o indice do objeto a ser apresentado (navegacao).
    private void actionButtonProximoAnterior(AjaxRequestTarget target, AjaxSubmitLink botaoClicado) {
        if (verificaRespostaSalva()) {
            if ("btnAnterior".equalsIgnoreCase(botaoClicado.getId())) {
                --indiceFormatacaoContrato;
            } else {
                ++indiceFormatacaoContrato;
            }
            buscaFormatacaoObjetoFornecimento();
            atualizaPaineis(target);
        }
    }

    // mobile: atualiza os paineis
    private void atualizaPaineis(AjaxRequestTarget target) {

        panelFormatacaoMobile.addOrReplace(panelFormatacaoAlfanumerica = new PanelFormatacaoAlfanumerica("panelFormatacaoAlfanumerica", formatacaoObjetoFornecimento.getFormatacaoResposta(), formatacaoObjetoFornecimento.getFormatacao()));
        panelFormatacaoMobile.addOrReplace(panelFormatacaoAnexo = new PanelFormatacaoAnexo("panelFormatacaoAnexo", formatacaoObjetoFornecimento.getFormatacaoResposta(), formatacaoObjetoFornecimento.getFormatacao()));
        panelFormatacaoMobile.addOrReplace(panelFormatacaoBoleano = new PanelFormatacaoBoleano("panelFormatacaoBoleano", formatacaoObjetoFornecimento.getFormatacaoResposta(), formatacaoObjetoFornecimento.getFormatacao()));
        panelFormatacaoMobile.addOrReplace(panelFormatacaoData = new PanelFormatacaoData("panelFormatacaoData", formatacaoObjetoFornecimento.getFormatacaoResposta(), formatacaoObjetoFornecimento.getFormatacao()));
        panelFormatacaoMobile.addOrReplace(panelFormatacaoFoto = new PanelFormatacaoFoto("panelFormatacaoFoto", formatacaoObjetoFornecimento.getFormatacaoResposta(), formatacaoObjetoFornecimento.getFormatacao()));
        panelFormatacaoMobile.addOrReplace(panelFormatacaoNumerico = new PanelFormatacaoNumerico("panelFormatacaoNumerico", formatacaoObjetoFornecimento.getFormatacaoResposta(), formatacaoObjetoFornecimento.getFormatacao()));
        panelFormatacaoMobile.addOrReplace(panelFormatacaoTexto = new PanelFormatacaoTexto("panelFormatacaoTexto", formatacaoObjetoFornecimento.getFormatacaoResposta(), formatacaoObjetoFornecimento.getFormatacao()));
        panelFormatacaoMobile.addOrReplace(panelFormatacaoVideo = new PanelFormatacaoVideo("panelFormatacaoVideo", formatacaoObjetoFornecimento.getFormatacaoResposta(), formatacaoObjetoFornecimento.getFormatacao()));
        panelFormatacaoMobile.addOrReplace(panelBotoesAnteriorProximo = new PanelBotoesAnteriorProximo("panelBotoesAnteriorProximo", formatacaoObjetoFornecimento.getFormatacaoResposta()));

        panelFormatacaoConformidadeItem = new PanelFormatacaoConformidadeItem("panelFormatacaoConformidadeItem");
        panelFormatacaoMobile.addOrReplace(panelFormatacaoConformidadeItem.setVisible(exibirPanelConformidadeItem()));

        form.addOrReplace(newButtonConferencia());

        exibiPainelTipoFormatacao();

        target.add(panelFormatacaoMobile, form);
    }

    // mobile: exibe ou enibe o painel, de acordo com o tipo do objeto.
    private void exibiPainelTipoFormatacao() {

        EnumTipoCampoFormatacao tipo = formatacaoObjetoFornecimento.getFormatacao().getTipoCampo();

        if (tipo.getValor() == EnumTipoCampoFormatacao.ALFANUMERICO.getValor()) {
            panelFormatacaoAlfanumerica.setVisible(Boolean.TRUE);
            panelFormatacaoAnexo.setVisible(Boolean.FALSE);
            panelFormatacaoBoleano.setVisible(Boolean.FALSE);
            panelFormatacaoData.setVisible(Boolean.FALSE);
            panelFormatacaoFoto.setVisible(Boolean.FALSE);
            panelFormatacaoNumerico.setVisible(Boolean.FALSE);
            panelFormatacaoTexto.setVisible(Boolean.FALSE);
            panelFormatacaoVideo.setVisible(Boolean.FALSE);
        }

        if (tipo.getValor() == EnumTipoCampoFormatacao.ANEXO.getValor()) {
            panelFormatacaoAlfanumerica.setVisible(Boolean.FALSE);
            panelFormatacaoAnexo.setVisible(Boolean.TRUE);
            panelFormatacaoBoleano.setVisible(Boolean.FALSE);
            panelFormatacaoData.setVisible(Boolean.FALSE);
            panelFormatacaoFoto.setVisible(Boolean.FALSE);
            panelFormatacaoNumerico.setVisible(Boolean.FALSE);
            panelFormatacaoTexto.setVisible(Boolean.FALSE);
            panelFormatacaoVideo.setVisible(Boolean.FALSE);
        }

        if (tipo.getValor() == EnumTipoCampoFormatacao.BOLEANO.getValor()) {
            panelFormatacaoAlfanumerica.setVisible(Boolean.FALSE);
            panelFormatacaoAnexo.setVisible(Boolean.FALSE);
            panelFormatacaoBoleano.setVisible(Boolean.TRUE);
            panelFormatacaoData.setVisible(Boolean.FALSE);
            panelFormatacaoFoto.setVisible(Boolean.FALSE);
            panelFormatacaoNumerico.setVisible(Boolean.FALSE);
            panelFormatacaoTexto.setVisible(Boolean.FALSE);
            panelFormatacaoVideo.setVisible(Boolean.FALSE);
        }

        if (tipo.getValor() == EnumTipoCampoFormatacao.DATA.getValor()) {
            panelFormatacaoAlfanumerica.setVisible(Boolean.FALSE);
            panelFormatacaoAnexo.setVisible(Boolean.FALSE);
            panelFormatacaoBoleano.setVisible(Boolean.FALSE);
            panelFormatacaoData.setVisible(Boolean.TRUE);
            panelFormatacaoFoto.setVisible(Boolean.FALSE);
            panelFormatacaoNumerico.setVisible(Boolean.FALSE);
            panelFormatacaoTexto.setVisible(Boolean.FALSE);
            panelFormatacaoVideo.setVisible(Boolean.FALSE);
        }

        if (tipo.getValor() == EnumTipoCampoFormatacao.FOTO.getValor()) {
            panelFormatacaoAlfanumerica.setVisible(Boolean.FALSE);
            panelFormatacaoAnexo.setVisible(Boolean.FALSE);
            panelFormatacaoBoleano.setVisible(Boolean.FALSE);
            panelFormatacaoData.setVisible(Boolean.FALSE);
            panelFormatacaoFoto.setVisible(Boolean.TRUE);
            panelFormatacaoNumerico.setVisible(Boolean.FALSE);
            panelFormatacaoTexto.setVisible(Boolean.FALSE);
            panelFormatacaoVideo.setVisible(Boolean.FALSE);
        }

        if (tipo.getValor() == EnumTipoCampoFormatacao.NUMERICO.getValor()) {
            panelFormatacaoAlfanumerica.setVisible(Boolean.FALSE);
            panelFormatacaoAnexo.setVisible(Boolean.FALSE);
            panelFormatacaoBoleano.setVisible(Boolean.FALSE);
            panelFormatacaoData.setVisible(Boolean.FALSE);
            panelFormatacaoFoto.setVisible(Boolean.FALSE);
            panelFormatacaoNumerico.setVisible(Boolean.TRUE);
            panelFormatacaoTexto.setVisible(Boolean.FALSE);
            panelFormatacaoVideo.setVisible(Boolean.FALSE);
        }

        if (tipo.getValor() == EnumTipoCampoFormatacao.TEXTO.getValor()) {
            panelFormatacaoAlfanumerica.setVisible(Boolean.FALSE);
            panelFormatacaoAnexo.setVisible(Boolean.FALSE);
            panelFormatacaoBoleano.setVisible(Boolean.FALSE);
            panelFormatacaoData.setVisible(Boolean.FALSE);
            panelFormatacaoFoto.setVisible(Boolean.FALSE);
            panelFormatacaoNumerico.setVisible(Boolean.FALSE);
            panelFormatacaoTexto.setVisible(Boolean.TRUE);
            panelFormatacaoVideo.setVisible(Boolean.FALSE);
        }

        if (tipo.getValor() == EnumTipoCampoFormatacao.VIDEO.getValor()) {
            panelFormatacaoAlfanumerica.setVisible(Boolean.FALSE);
            panelFormatacaoAnexo.setVisible(Boolean.FALSE);
            panelFormatacaoBoleano.setVisible(Boolean.FALSE);
            panelFormatacaoData.setVisible(Boolean.FALSE);
            panelFormatacaoFoto.setVisible(Boolean.FALSE);
            panelFormatacaoNumerico.setVisible(Boolean.FALSE);
            panelFormatacaoTexto.setVisible(Boolean.FALSE);
            panelFormatacaoVideo.setVisible(Boolean.TRUE);
        }

        int cont = indiceFormatacaoContrato;
        if (perfilUsuarioLogado.equals(EnumPerfilEntidade.BENEFICIARIO) && ++cont == totalFormatacoes) {
            panelFormatacaoAlfanumerica.setVisible(Boolean.FALSE);
            panelFormatacaoAnexo.setVisible(Boolean.FALSE);
            panelFormatacaoBoleano.setVisible(Boolean.FALSE);
            panelFormatacaoData.setVisible(Boolean.FALSE);
            panelFormatacaoFoto.setVisible(Boolean.FALSE);
            panelFormatacaoNumerico.setVisible(Boolean.FALSE);
            panelFormatacaoTexto.setVisible(Boolean.FALSE);
            panelFormatacaoVideo.setVisible(Boolean.FALSE);
        }

    }

    // mobile: adiciona o anexo para persistir.
    private void adicionarAnexoMobile(AjaxRequestTarget target) {
        EnumTipoCampoFormatacao tipoCampo = formatacaoObjetoFornecimento.getFormatacao().getTipoCampo();

        if (tipoCampo.equals(EnumTipoCampoFormatacao.FOTO) && formatacaoItensContratoResposta.getFormatacao().getPossuiGPS()) {
            if (latitude != null && longitude != null) {
                if (latitude.equalsIgnoreCase("undefined") && longitude.equalsIgnoreCase("undefined")) {
                    getSession().error("É necessario habilitar a localização da foto(GPS).");
                    target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
                    return;
                }
            } else {
                getSession().error("É necessario habilitar a localização da foto(GPS).");
                target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
                return;
            }
        }

        if (tipoCampo.equals(EnumTipoCampoFormatacao.FOTO) && formatacaoItensContratoResposta.getFormatacao().getPossuiData()) {
            if (dataFoto != null) {
                if (dataFoto.equalsIgnoreCase("undefined")) {
                    getSession().error("Não foi possivel recuperar a data da foto.");
                    target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
                    return;
                }
            } else {
                getSession().error("Não foi possivel recuperar a data da foto.");
                target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
                return;
            }
        }

        if (uploads != null) {
            if (!uploads.isEmpty()) {
                if (!validarTamanhoAnexos(target)) {
                    return;
                }
                for (FileUpload component : uploads) {

                    if (tipoCampo.equals(EnumTipoCampoFormatacao.FOTO)) {
                        if (validaFormato(component)) {
                            setaLatitudeLongitudeDataMobile();
                            formatacaoItensContratoResposta.setTamanho(null);

                            byte[] bytes = component.getMD5();
                            String hashFoto = SideUtil.retornarHashDoArquivoMD5(bytes);
                            
                            formatacaoItensContratoResposta.setCodigoUnicoFoto(hashFoto);
                            formatacaoItensContratoResposta.setConteudo(redimencionaImagem(component));
                            formatacaoItensContratoResposta.setNomeAnexo(component.getClientFileName());
                            setaFormatacaoContratoResposta(formatacaoItensContratoResposta);

                            panelFormatacaoMobile.addOrReplace(panelFormatacaoFoto = new PanelFormatacaoFoto("panelFormatacaoFoto", formatacaoItensContratoResposta, formatacaoObjetoFornecimento.getFormatacao()));

                            target.add(panelFormatacaoMobile);
                        } else {
                            addMsgError("Formato da imagem esta inválida.");
                            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
                        }
                    } else if (tipoCampo.equals(EnumTipoCampoFormatacao.VIDEO)) {
                        if (validaFormatoVideo(component)) {
                            formatacaoItensContratoResposta.setTamanho(null);
                            formatacaoItensContratoResposta.setConteudo(component.getBytes());
                            formatacaoItensContratoResposta.setNomeAnexo(component.getClientFileName());
                            setaFormatacaoContratoResposta(formatacaoItensContratoResposta);

                            panelFormatacaoMobile.addOrReplace(panelFormatacaoVideo = new PanelFormatacaoVideo("panelFormatacaoVideo", formatacaoItensContratoResposta, formatacaoObjetoFornecimento.getFormatacao()));

                            target.add(panelFormatacaoMobile);
                        } else {
                            addMsgError("Formato do video esta inválida.");
                            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
                        }

                    } else if (tipoCampo.equals(EnumTipoCampoFormatacao.ANEXO)) {
                        formatacaoItensContratoResposta.setTamanho(null);
                        formatacaoItensContratoResposta.setConteudo(component.getBytes());
                        formatacaoItensContratoResposta.setNomeAnexo(component.getClientFileName());
                        setaFormatacaoContratoResposta(formatacaoItensContratoResposta);

                        panelFormatacaoMobile.addOrReplace(panelFormatacaoAnexo = new PanelFormatacaoAnexo("panelFormatacaoAnexo", formatacaoItensContratoResposta, formatacaoObjetoFornecimento.getFormatacao()));

                        target.add(panelFormatacaoMobile);
                    }

                }

            }
        } else {
            addMsgError("Selecione um arquivo para anexar.");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        }

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

    private boolean validaFormatoVideo(FileUpload component) {

        Boolean retorno = Boolean.FALSE;
        String extensao = FilenameUtils.getExtension(component.getClientFileName());

        if (extensao.equalsIgnoreCase("mkv")) {
            retorno = Boolean.TRUE;
        } else if (extensao.equalsIgnoreCase("avi")) {
            retorno = Boolean.TRUE;
        } else if (extensao.equalsIgnoreCase("rmvb")) {
            retorno = Boolean.TRUE;
        } else if (extensao.equalsIgnoreCase("mp4")) {
            retorno = Boolean.TRUE;
        } else if (extensao.equalsIgnoreCase("mpeg")) {
            retorno = Boolean.TRUE;
        } else if (extensao.equalsIgnoreCase("rm")) {
            retorno = Boolean.TRUE;
        } else if (extensao.equalsIgnoreCase("mpg")) {
            retorno = Boolean.TRUE;
        } else if (extensao.equalsIgnoreCase("mov")) {
            retorno = Boolean.TRUE;
        } else if (extensao.equalsIgnoreCase("flv")) {
            retorno = Boolean.TRUE;
        } else if (extensao.equalsIgnoreCase("wmv")) {
            retorno = Boolean.TRUE;
        }

        return retorno;
    }

    // desktop: adiciona o anexo para persistir.
    private void adicionarAnexoDesktop(AjaxRequestTarget target, FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        EnumTipoCampoFormatacao tipoCampo = formatacaoItensContratoResposta.getFormatacao().getTipoCampo();

        if (tipoCampo.equals(EnumTipoCampoFormatacao.FOTO) && formatacaoItensContratoResposta.getFormatacao().getPossuiGPS()) {
            if (latitude != null && longitude != null) {
                if (latitude.equalsIgnoreCase("undefined") && longitude.equalsIgnoreCase("undefined")) {
                    getSession().error("É necessario habilitar a localização da foto(GPS).");
                    target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
                    return;
                }
            } else {
                getSession().error("Adicione uma foto.");
                target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
                return;
            }
        }

        if (tipoCampo.equals(EnumTipoCampoFormatacao.FOTO) && formatacaoItensContratoResposta.getFormatacao().getPossuiData()) {
            if (dataFoto != null) {
                if (dataFoto.equalsIgnoreCase("undefined")) {
                    getSession().error("Não foi possivel recuperar a data da foto.");
                    target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
                    return;
                }
            } else {
                getSession().error("Não foi possivel recuperar a data da foto.");
                target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
                return;
            }
        }

        if (uploads != null) {
            if (!uploads.isEmpty()) {
                if (!validarTamanhoAnexos(target)) {
                    return;
                }
                for (FileUpload component : uploads) {

                    if (tipoCampo.equals(EnumTipoCampoFormatacao.FOTO)) {
                        if (validaFormato(component)) {
                            setaLatitudeLongitudeData(formatacaoItensContratoResposta);
                            formatacaoItensContratoResposta.setTamanho(null);
                            
                            byte[] bytes = component.getMD5();
                            String hashFoto = SideUtil.retornarHashDoArquivoMD5(bytes);
                            
                            formatacaoItensContratoResposta.setCodigoUnicoFoto(hashFoto);
                            formatacaoItensContratoResposta.setConteudo(redimencionaImagem(component));
                            formatacaoItensContratoResposta.setNomeAnexo(component.getClientFileName());

                            fotoAnexada = Boolean.TRUE;

                            setaFormatacaoContratoResposta(formatacaoItensContratoResposta);

                            panelFormatacaoDesktop.addOrReplace(newDataViewListaDesktopFoto());
                            target.add(panelFormatacaoDesktop);
                        } else {
                            addMsgError("Formato da imagem esta inválida.");
                            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
                        }
                    } else if (tipoCampo.equals(EnumTipoCampoFormatacao.VIDEO)) {
                        if (validaFormatoVideo(component)) {
                            formatacaoItensContratoResposta.setTamanho(null);
                            formatacaoItensContratoResposta.setConteudo(component.getBytes());
                            formatacaoItensContratoResposta.setNomeAnexo(component.getClientFileName());
                            setaFormatacaoContratoResposta(formatacaoItensContratoResposta);

                            panelFormatacaoDesktop.addOrReplace(newDataViewListaDesktopVideo());

                            target.add(panelFormatacaoDesktop);
                        } else {
                            addMsgError("Formato do video esta inválida.");
                            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
                        }

                    } else if (tipoCampo.equals(EnumTipoCampoFormatacao.ANEXO)) {
                        formatacaoItensContratoResposta.setTamanho(null);
                        formatacaoItensContratoResposta.setConteudo(component.getBytes());
                        formatacaoItensContratoResposta.setNomeAnexo(component.getClientFileName());
                        setaFormatacaoContratoResposta(formatacaoItensContratoResposta);

                        panelFormatacaoDesktop.addOrReplace(newDataViewListaDesktopAnexo());

                        target.add(panelFormatacaoDesktop);
                    }

                }

            }
        } else {
            addMsgError("Selecione um arquivo para anexar.");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        }
    }

    private boolean validaLatitudeLongitude() {
        Boolean retorno = Boolean.FALSE;

        if (latitude != null && longitude != null) {
            if (!(latitude.equalsIgnoreCase("undefined") && longitude.equalsIgnoreCase("undefined"))) {
                retorno = Boolean.TRUE;
            }
        }

        return retorno;
    }

    // desktop: data views
    public DataView<FormatacaoItensContratoResposta> newDataViewListaDesktopAlfanumerico() {
        return new DataView<FormatacaoItensContratoResposta>("listaFormatacaoAlfanumerico", new ProviderFormatacoesAlfanumerico()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelFormatacaoAlfanumerica("panelFormatacaoAlfanumerica", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<FormatacaoItensContratoResposta> newDataViewListaDesktopAnexo() {
        return new DataView<FormatacaoItensContratoResposta>("listaFormatacaoAnexo", new ProviderFormatacoesAnexo()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelFormatacaoAnexo("panelFormatacaoAnexo", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<FormatacaoItensContratoResposta> newDataViewListaDesktopBoleano() {
        return new DataView<FormatacaoItensContratoResposta>("listaFormatacaoBoleano", new ProviderFormatacoesBoleano()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelFormatacaoBoleano("panelFormatacaoBoleano", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<FormatacaoItensContratoResposta> newDataViewListaDesktopData() {
        return new DataView<FormatacaoItensContratoResposta>("listaFormatacaoData", new ProviderFormatacoesData()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelFormatacaoData("panelFormatacaoData", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<FormatacaoItensContratoResposta> newDataViewListaDesktopFoto() {
        return new DataView<FormatacaoItensContratoResposta>("listaFormatacaoFoto", new ProviderFormatacoesFoto()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelFormatacaoFoto("panelFormatacaoFoto", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<FormatacaoItensContratoResposta> newDataViewListaDesktopNumerico() {
        return new DataView<FormatacaoItensContratoResposta>("listaFormatacaoNumerico", new ProviderFormatacoesNumerico()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelFormatacaoNumerico("panelFormatacaoNumerico", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<FormatacaoItensContratoResposta> newDataViewListaDesktopTexto() {
        return new DataView<FormatacaoItensContratoResposta>("listaFormatacaoTexto", new ProviderFormatacoesTexto()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelFormatacaoTexto("panelFormatacaoTexto", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<FormatacaoItensContratoResposta> newDataViewListaDesktopVideo() {
        DataView<FormatacaoItensContratoResposta> d = new DataView<FormatacaoItensContratoResposta>("listaFormatacaoVideo", new ProviderFormatacoesVideo()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelFormatacaoVideo("panelFormatacaoVideo", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
        d.setOutputMarkupId(Boolean.TRUE);
        return d;
    }

    // desktop: provaiders
    private class ProviderFormatacoesAlfanumerico extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaFormatacaoAlfanumerico.iterator();
        }

        @Override
        public long size() {
            return listaRespostaFormatacaoAlfanumerico.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderFormatacoesAnexo extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaFormatacaoAnexo.iterator();
        }

        @Override
        public long size() {
            return listaRespostaFormatacaoAnexo.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderFormatacoesBoleano extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaFormatacaoBoleano.iterator();
        }

        @Override
        public long size() {
            return listaRespostaFormatacaoBoleano.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderFormatacoesData extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaFormatacaoData.iterator();
        }

        @Override
        public long size() {
            return listaRespostaFormatacaoData.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderFormatacoesFoto extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaFormatacaoFoto.iterator();
        }

        @Override
        public long size() {
            return listaRespostaFormatacaoFoto.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderFormatacoesNumerico extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaFormatacaoNumerico.iterator();
        }

        @Override
        public long size() {
            return listaRespostaFormatacaoNumerico.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderFormatacoesTexto extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaFormatacaoTexto.iterator();
        }

        @Override
        public long size() {
            return listaRespostaFormatacaoTexto.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderFormatacoesVideo extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaFormatacaoVideo.iterator();
        }

        @Override
        public long size() {
            return listaRespostaFormatacaoVideo.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    // paineis
    private class PanelFormatacaoMobile extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelFormatacaoMobile(String id) {
            super(id);
            setOutputMarkupId(true);

            add(panelFormatacaoAlfanumerica = new PanelFormatacaoAlfanumerica("panelFormatacaoAlfanumerica", formatacaoObjetoFornecimento.getFormatacaoResposta(), formatacaoObjetoFornecimento.getFormatacao()));
            add(panelFormatacaoAnexo = new PanelFormatacaoAnexo("panelFormatacaoAnexo", formatacaoObjetoFornecimento.getFormatacaoResposta(), formatacaoObjetoFornecimento.getFormatacao()));
            add(panelFormatacaoBoleano = new PanelFormatacaoBoleano("panelFormatacaoBoleano", formatacaoObjetoFornecimento.getFormatacaoResposta(), formatacaoObjetoFornecimento.getFormatacao()));
            add(panelFormatacaoData = new PanelFormatacaoData("panelFormatacaoData", formatacaoObjetoFornecimento.getFormatacaoResposta(), formatacaoObjetoFornecimento.getFormatacao()));
            add(panelFormatacaoFoto = new PanelFormatacaoFoto("panelFormatacaoFoto", formatacaoObjetoFornecimento.getFormatacaoResposta(), formatacaoObjetoFornecimento.getFormatacao()));
            add(panelFormatacaoNumerico = new PanelFormatacaoNumerico("panelFormatacaoNumerico", formatacaoObjetoFornecimento.getFormatacaoResposta(), formatacaoObjetoFornecimento.getFormatacao()));
            add(panelFormatacaoTexto = new PanelFormatacaoTexto("panelFormatacaoTexto", formatacaoObjetoFornecimento.getFormatacaoResposta(), formatacaoObjetoFornecimento.getFormatacao()));
            add(panelFormatacaoVideo = new PanelFormatacaoVideo("panelFormatacaoVideo", formatacaoObjetoFornecimento.getFormatacaoResposta(), formatacaoObjetoFornecimento.getFormatacao()));
            add(panelBotoesAnteriorProximo = new PanelBotoesAnteriorProximo("panelBotoesAnteriorProximo", formatacaoObjetoFornecimento.getFormatacaoResposta()));

            panelFormatacaoConformidadeItem = new PanelFormatacaoConformidadeItem("panelFormatacaoConformidadeItem");
            add(panelFormatacaoConformidadeItem.setVisible(exibirPanelConformidadeItem()));

            setEnabled(readOnly);
            setVisible(isMobile);
        }

    }

    private class PanelFormatacaoDesktop extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelFormatacaoDesktop(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newDataViewListaDesktopAlfanumerico());
            add(newDataViewListaDesktopAnexo());
            add(newDataViewListaDesktopBoleano());
            add(newDataViewListaDesktopData());
            add(newDataViewListaDesktopFoto());
            add(newDataViewListaDesktopNumerico());
            add(newDataViewListaDesktopTexto());
            add(newDataViewListaDesktopVideo());

            panelFormatacaoConformidadeItem = new PanelFormatacaoConformidadeItem("panelFormatacaoConformidadeItem");

            add(panelFormatacaoConformidadeItem.setVisible(exibirPanelConformidadeItem()));
            add(newButtonSalvarDesktop());

            setEnabled(readOnly);
            setVisible(!isMobile);
        }

    }

    private class PanelFormatacaoAlfanumerica extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelFormatacaoAlfanumerica(String id, FormatacaoItensContratoResposta formatacaoItensContratoResposta, FormatacaoItensContrato formatacaoItensContrato) {
            super(id);
            setOutputMarkupId(true);

            if (formatacaoItensContratoResposta == null) {
                formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
            }

            add(newLabelTituloQuesito(formatacaoItensContrato));
            add(newLabelPaginacao().setVisible(isMobile));
            add(newLabelOrientacaoFornecedor(formatacaoItensContrato));
            add(newTextFieldTextoAlfanumerico(formatacaoItensContratoResposta, formatacaoItensContrato));
        }

    }

    private class PanelFormatacaoAnexo extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelFormatacaoAnexo(String id, FormatacaoItensContratoResposta formatacaoItensContratoResposta, FormatacaoItensContrato formatacaoItensContrato) {
            super(id);
            setOutputMarkupId(true);

            if (formatacaoItensContratoResposta == null) {
                formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
            }

            add(newLabelTituloQuesito(formatacaoItensContrato));
            add(newLabelPaginacao().setVisible(isMobile));
            add(newLabelOrientacaoFornecedor(formatacaoItensContrato));
            setaFormatacaoContratoResposta(formatacaoItensContratoResposta);
            add(getFormUpload(formatacaoItensContratoResposta));
            add(newLabelNomeAnexo(formatacaoItensContratoResposta));
            add(newLabelTamanhoAnexo(formatacaoItensContratoResposta));
            add(newButtonDownload(formatacaoItensContratoResposta));
        }

    }

    private class PanelFormatacaoBoleano extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelFormatacaoBoleano(String id, FormatacaoItensContratoResposta formatacaoItensContratoResposta, FormatacaoItensContrato formatacaoItensContrato) {
            super(id);
            setOutputMarkupId(true);

            if (formatacaoItensContratoResposta == null) {
                formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
            }

            add(newLabelTituloQuesito(formatacaoItensContrato));
            add(newLabelPaginacao().setVisible(isMobile));
            add(newLabelOrientacaoFornecedor(formatacaoItensContrato));
            add(newRadioBoleano(formatacaoItensContratoResposta));
        }

    }

    private class PanelFormatacaoData extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelFormatacaoData(String id, FormatacaoItensContratoResposta formatacaoItensContratoResposta, FormatacaoItensContrato formatacaoItensContrato) {
            super(id);
            setOutputMarkupId(true);

            if (formatacaoItensContratoResposta == null) {
                formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
            }

            add(newLabelTituloQuesito(formatacaoItensContrato));
            add(newLabelPaginacao().setVisible(isMobile));
            add(newLabelOrientacaoFornecedor(formatacaoItensContrato));
            add(newDateTextFieldData(formatacaoItensContratoResposta));
        }

    }

    private class PanelFormatacaoFoto extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelFormatacaoFoto(String id, FormatacaoItensContratoResposta formatacaoItensContratoResposta, FormatacaoItensContrato formatacaoItensContrato) {
            super(id);
            setOutputMarkupId(true);

            if (formatacaoItensContratoResposta == null) {
                formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
            }

            add(newLabelTituloQuesito(formatacaoItensContrato));
            add(newLabelPaginacao().setVisible(isMobile));
            add(newLabelOrientacaoFornecedor(formatacaoItensContrato));

            if (formatacaoItensContratoResposta.getConteudo() != null) {
                add(newImageFoto(formatacaoItensContratoResposta));
            } else {
                add(newImageFotoPadrao());
            }

            setaFormatacaoContratoResposta(formatacaoItensContratoResposta);
            add(getFormUpload(formatacaoItensContratoResposta));

            add(panelFormatacaoIdentificadorUnico = new PanelFormatacaoIdentificadorUnico("panelFormatacaoIdentificadorUnico", formatacaoItensContratoResposta, formatacaoItensContrato));
        }

    }

    private class PanelFormatacaoIdentificadorUnico extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelFormatacaoIdentificadorUnico(String id, FormatacaoItensContratoResposta formatacaoItensContratoResposta, FormatacaoItensContrato formatacaoItensContrato) {
            super(id);
            setOutputMarkupId(true);

            if (formatacaoItensContratoResposta == null) {
                formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
            }

            add(newLabelIdentificadorUnico(formatacaoItensContrato));
            add(newTextFieldTextoIdentificadorUinico(formatacaoItensContratoResposta));
            setVisible(formatacaoItensContrato.getPossuiIdentificadorUnico());
        }

    }

    private class PanelFormatacaoNumerico extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelFormatacaoNumerico(String id, FormatacaoItensContratoResposta formatacaoItensContratoResposta, FormatacaoItensContrato formatacaoItensContrato) {
            super(id);
            setOutputMarkupId(true);

            if (formatacaoItensContratoResposta == null) {
                formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
            }

            add(newLabelTituloQuesito(formatacaoItensContrato));
            add(newLabelPaginacao().setVisible(isMobile));
            add(newLabelOrientacaoFornecedor(formatacaoItensContrato));
            add(newTextFieldNumerico(formatacaoItensContratoResposta));
        }

    }

    private class PanelFormatacaoTexto extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelFormatacaoTexto(String id, FormatacaoItensContratoResposta formatacaoItensContratoResposta, FormatacaoItensContrato formatacaoItensContrato) {
            super(id);
            setOutputMarkupId(true);

            if (formatacaoItensContratoResposta == null) {
                formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
            }

            add(newLabelTituloQuesito(formatacaoItensContrato));
            add(newLabelPaginacao().setVisible(isMobile));
            add(newLabelOrientacaoFornecedor(formatacaoItensContrato));
            add(newTextAreaTexto(formatacaoItensContratoResposta));
        }

    }

    private class PanelFormatacaoVideo extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelFormatacaoVideo(String id, FormatacaoItensContratoResposta formatacaoItensContratoResposta, FormatacaoItensContrato formatacaoItensContrato) {
            super(id);
            setOutputMarkupId(true);

            if (formatacaoItensContratoResposta == null) {
                formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
            }

            add(newLabelTituloQuesito(formatacaoItensContrato));
            add(newLabelPaginacao().setVisible(isMobile));
            add(newLabelOrientacaoFornecedor(formatacaoItensContrato));
            setaFormatacaoContratoResposta(formatacaoItensContratoResposta);
            add(getFormUpload(formatacaoItensContratoResposta));
            add(newLabelNomeAnexo(formatacaoItensContratoResposta));
            add(newLabelTamanhoAnexo(formatacaoItensContratoResposta));
            add(newButtonDownload(formatacaoItensContratoResposta));
        }

    }

    private class PanelFormatacaoConformidadeItem extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelFormatacaoConformidadeItem(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newLabelPaginacao().setVisible(isMobile));
            add(newRadioItemNovo());
            add(newRadioFunciona());
            add(newTextAreaJustificativaNaoFunciona());
            add(newRadioConfigurado());
            add(newTextAreaJustificativaNaoConfigurado());
        }

    }

    private class PanelBotoesAnteriorProximo extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBotoesAnteriorProximo(String id, FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
            super(id);
            setOutputMarkupId(true);

            add(newButtonAnterior());
            add(newButtonSalvarMobile());
            add(newButtonProximo());
        }
    }

    // form para upload
    private class FileUploadForm extends Form<List<FileUpload>> {
        private static final long serialVersionUID = 1L;

        public FileUploadForm(String id, IModel<List<FileUpload>> model, FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
            super(id, model);
            setMultiPart(true);

            FileUploadField fileUploadField = new FileUploadField("fileInput", model);
            fileUploadField.add(new UploadValidator());
            add(fileUploadField);

            add(newButtonAdicionarAnexoDesktop(formatacaoItensContratoResposta).setVisible(!isMobile));
            add(newButtonAdicionarAnexoMobile().setVisible(isMobile));

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

    // mobile e desktop: avalia o perfil do usuario e exibe se for BENEFICIARIO.
    private Boolean exibirPanelConformidadeItem() {
        Boolean retorno = Boolean.FALSE;
        int cont = indiceFormatacaoContrato;
        if (perfilUsuarioLogado.equals(EnumPerfilEntidade.BENEFICIARIO)) {
            if (isMobile && ++cont == totalFormatacoes) {
                retorno = Boolean.TRUE;
            } else if (!isMobile) {
                retorno = Boolean.TRUE;
            }
        }
        return retorno;
    }

    // mobile e desktop: exibe ou enibe se selecionou SIM ou NAO.
    private Boolean exibirTextJustificativaNaoFunciona() {
        Boolean retorno = Boolean.FALSE;
        if (objetoFornecimentoContrato.getFuncionandoDeAcordo() != null) {
            if (!objetoFornecimentoContrato.getFuncionandoDeAcordo()) {
                retorno = Boolean.TRUE;
            }
        }
        return retorno;
    }

    // mobile e desktop: exibe ou enibe se selecionou SIM ou NAO.
    private Boolean exibirTextJustificativaNaoConfigurado() {
        Boolean retorno = Boolean.FALSE;
        if (objetoFornecimentoContrato.getConfiguradoDeAcordo() != null) {
            if (!objetoFornecimentoContrato.getConfiguradoDeAcordo()) {
                retorno = Boolean.TRUE;
            }
        }
        return retorno;
    }

    // Validar tamanho de anexos
    private boolean validarTamanhoAnexos(AjaxRequestTarget target) {
        boolean validar = true;

        if (uploads.get(0).getSize() > Bytes.megabytes(Constants.LIMITE_MEGABYTES).bytes()) {
            addMsgError("O tamanho do anexo excede o limite permitido (Anexo maior que " + Constants.LIMITE_MEGABYTES + "MB).");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
            validar = false;
        }
        return validar;
    }

    // desktop e mobile: retorna o tamanho em MB do arquivo anexado.
    private BigDecimal getTamanhoArquivoEmMB(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        Long tamanhoArquivo = new Long("0");
        if (formatacaoItensContratoResposta.getTamanho() != null) {
            tamanhoArquivo = formatacaoItensContratoResposta.getTamanho();
        } else if (formatacaoItensContratoResposta.getConteudo() != null) {
            tamanhoArquivo = new Long(formatacaoItensContratoResposta.getConteudo().length);
        }
        BigDecimal valor = new BigDecimal(String.valueOf(tamanhoArquivo));
        BigDecimal mega = new BigDecimal("1024");
        valor = valor.divide(mega).divide(mega);
        return valor.setScale(2, BigDecimal.ROUND_UP);
    }

    // getteres e setteres
    public Boolean getInformacaoBoleana() {
        return informacaoBoleana;
    }

    public void setInformacaoBoleana(Boolean informacaoBoleana) {
        this.informacaoBoleana = informacaoBoleana;
    }

}
