package br.gov.mj.side.web.view.execucao.painel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.DynamicImageResource;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraLocalDateTextField;
import br.gov.mj.infra.wicket.component.InfraRadioChoice;
import br.gov.mj.side.entidades.enums.EnumPerfilEntidade;
import br.gov.mj.side.entidades.enums.EnumResponsavelPreencherFormatacaoItem;
import br.gov.mj.side.entidades.enums.EnumSituacaoAvaliacaoPreliminarPreenchimentoItem;
import br.gov.mj.side.entidades.enums.EnumStatusFormatacao;
import br.gov.mj.side.entidades.enums.EnumTipoCampoFormatacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoItensContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoItensContratoResposta;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoObjetoFornecimento;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.web.dto.PanelCompararDTO;
import br.gov.mj.side.web.dto.formatacaoObjetoFornecimento.FormatacaoItensContratoRespostaDto;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.util.SideUtil;

public class PanelCompararItensAnalisar extends Panel {
    private static final long serialVersionUID = 1L;

    private Form<FormatacaoItensContratoResposta> form;

    private FormatacaoObjetoFornecimento formatacaoObjetoFornecimento = new FormatacaoObjetoFornecimento();
    private ObjetoFornecimentoContrato objetoFornecimentoContrato = new ObjetoFornecimentoContrato();
    private FormatacaoItensContratoResposta formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
    private Boolean informacaoBoleana = Boolean.FALSE;

    private List<FormatacaoObjetoFornecimento> listaFormatacaoFornecedor = new ArrayList<>();
    private List<FormatacaoObjetoFornecimento> listaFormatacaoBeneficiario = new ArrayList<>();
    private List<FormatacaoObjetoFornecimento> listaFormatacaoAmbos = new ArrayList<>();

    private List<FormatacaoItensContratoResposta> listaRespostaAlfanumericoBeneficiario = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaAnexoBeneficiario = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaBoleanoBeneficiario = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaDataBeneficiario = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaFotoBeneficiario = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaNumericoBeneficiario = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaTextoBeneficiario = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaVideoBeneficiario = new ArrayList<>();

    private List<FormatacaoItensContratoResposta> listaRespostaAlfanumericoFornecedor = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaAnexoFornecedor = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaBoleanoFornecedor = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaDataFornecedor = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaFotoFornecedor = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaNumericoFornecedor = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaTextoFornecedor = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaVideoFornecedor = new ArrayList<>();

    private List<FormatacaoItensContratoResposta> listaRespostaAlfanumericoAmbos = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaAnexoAmbos = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaBoleanoAmbos = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaDataAmbos = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaFotoAmbos = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaNumericoAmbos = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaTextoAmbos = new ArrayList<>();
    private List<FormatacaoItensContratoResposta> listaRespostaVideoAmbos = new ArrayList<>();

    private PanelDesktop panelDesktop;

    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;

    public PanelCompararItensAnalisar(String id, ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        super(id);
        this.objetoFornecimentoContrato = objetoFornecimentoContrato;

        initVariaveis();
        initComponents();
    }

    private void initVariaveis() {
        buscaListaFormatacoesRespostas();
        buscaFormatacoesRespostasFornecedor();
        buscaFormatacoesRespostasBeneficiario();
        buscaFormatacoesRespostasAmbosFornecedor();

    }

    private void initComponents() {
        form = componentFactory.newForm("form", new CompoundPropertyModel<FormatacaoItensContratoResposta>(formatacaoItensContratoResposta));

        form.add(componentFactory.newLabel("txtDescricaoItem", nomeDaDescricaoItem()));
        form.add(panelDesktop = new PanelDesktop("panelDesktop"));

        add(form);
    }

    // VERIFICAR - METODO PARA VALIDAR SE A DESCRIÇÃO DO ITEM ESTA NULA
    private String nomeDaDescricaoItem() {
        String descricaoItem = " - ";
        if (objetoFornecimentoContrato != null && objetoFornecimentoContrato.getId() != null) {
            descricaoItem = this.objetoFornecimentoContrato.getItem().getDescricaoBem();
        }
        return descricaoItem;
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

        String nome = " - ";
        if (formatacaoItensContrato != null && !"".equalsIgnoreCase(formatacaoItensContrato.getTituloQuesito())) {
            nome = formatacaoItensContrato.getPossuiInformacaoOpcional() ? formatacaoItensContrato.getTituloQuesito() : "* ".concat(formatacaoItensContrato.getTituloQuesito());
        }

        Label lblTituloQuesito = componentFactory.newLabel("txtLabelTituloQuesito", nome);
        lblTituloQuesito.setOutputMarkupId(Boolean.TRUE);
        return lblTituloQuesito;
    }

    private Label newLabelDataFoto(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {

        String data = " - ";
        if (formatacaoItensContratoResposta != null) {
            if (formatacaoItensContratoResposta.getDataFoto() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                data = formatacaoItensContratoResposta.getDataFoto().format(formatter);
            }
        }

        Label lblDataFoto = componentFactory.newLabel("txtLabelDataFoto", data);
        lblDataFoto.setOutputMarkupId(Boolean.TRUE);
        return lblDataFoto;
    }

    private Label newLabelIconeInfo(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {

        EnumStatusFormatacao statusFormatacao;
        String iconeInfMotivo = new String();

        if (formatacaoItensContratoResposta.getStatusFormatacao() != null) {
            statusFormatacao = formatacaoItensContratoResposta.getStatusFormatacao();
        } else {
            statusFormatacao = EnumStatusFormatacao.NAO_PREENCHIDO;
        }

        if (statusFormatacao.equals(EnumStatusFormatacao.CONFORMIDADE)) {
            iconeInfMotivo = "<a type=\"button\" data-placement=\"left\" data-toggle=\"popover\" data-content=\"" + statusFormatacao.getDescricao() + "\" data-trigger=\"hover\"><i style=\"color: #009947;\" class=\"fa fa-check fa-lg\"> </i></a>";
        } else if (statusFormatacao.equals(EnumStatusFormatacao.NAO_CONFORMIDADE)) {
            StringBuilder sb = new StringBuilder();
            sb.append("<table><tr><td>" + formatacaoItensContratoResposta.getMotivoNaoConformidade() == null ? "<table><tr><td> " : formatacaoItensContratoResposta.getMotivoNaoConformidade() + "</td></tr></table>");
            iconeInfMotivo = "<a type=\"button\" data-placement=\"left\" data-toggle=\"popover\" data-content=\"" + sb.toString() + "\" data-trigger=\"hover\"><i style=\"color: #E82C0C;\" class=\"fa fa-exclamation fa-lg\"> </i></a>";
        } else {
            iconeInfMotivo = "<a type=\"button\" data-placement=\"left\" data-toggle=\"popover\" data-content=\"" + statusFormatacao.getDescricao() + "\" data-trigger=\"hover\"><i style=\"color: #E82C0C;\" class=\"fa fa-exclamation fa-lg\"> </i></a>";
        }

        Label lbl = new Label("lblInfo", iconeInfMotivo);
        lbl.setOutputMarkupId(Boolean.TRUE);
        lbl.setEscapeModelStrings(Boolean.FALSE);
        return lbl;
    }

    private Label newLabelIconeInfoConformidade() {
        EnumSituacaoAvaliacaoPreliminarPreenchimentoItem statusConformidade;
        String iconeInfMotivo = new String();

        if (objetoFornecimentoContrato.getSituacaoAvaliacaoPreliminarPreenchimentoItem() != null) {
            statusConformidade = objetoFornecimentoContrato.getSituacaoAvaliacaoPreliminarPreenchimentoItem();
        } else {
            statusConformidade = EnumSituacaoAvaliacaoPreliminarPreenchimentoItem.SEM_AVALIACAO;
        }

        if (statusConformidade.equals(EnumSituacaoAvaliacaoPreliminarPreenchimentoItem.EM_CONFORMIDADE)) {
            iconeInfMotivo = "<a type=\"button\" data-placement=\"left\" data-toggle=\"popover\" data-content=\"" + statusConformidade.getDescricao() + "\" data-trigger=\"hover\"><i style=\"color: #009947;\" class=\"fa fa-check fa-lg\"> </i></a>";
        } else {
            iconeInfMotivo = "<a type=\"button\" data-placement=\"left\" data-toggle=\"popover\" data-content=\"" + statusConformidade.getDescricao() + "\" data-trigger=\"hover\"><i style=\"color: #E82C0C;\" class=\"fa fa-exclamation fa-lg\"> </i></a>";
        }

        Label lbl = new Label("lblInfoConformidade", iconeInfMotivo);
        lbl.setOutputMarkupId(Boolean.TRUE);
        lbl.setEscapeModelStrings(Boolean.FALSE);
        return lbl;
    }

    private Label newLabelLatitudeLongitude(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {

        String latitudeLongitude = " - ";
        if (formatacaoItensContratoResposta != null) {
            if (formatacaoItensContratoResposta.getLatitudeLongitudeFoto() != null) {
                latitudeLongitude = formatacaoItensContratoResposta.getLatitudeLongitudeFoto();
            }
        }

        Label lblLatitudeLongitude = componentFactory.newLabel("txtLabelLatitudeLongitude", latitudeLongitude);
        lblLatitudeLongitude.setOutputMarkupId(Boolean.TRUE);
        return lblLatitudeLongitude;
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

    private void download(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        if (formatacaoItensContratoResposta.getId() != null) {
            FormatacaoItensContratoRespostaDto anexo = ordemFornecimentoContratoService.buscarFormatacaoItensContratoRespostaDownloadPeloId(formatacaoItensContratoResposta.getId());
            SideUtil.download(anexo.getConteudo(), anexo.getNomeAnexo());
        } else {
            SideUtil.download(formatacaoItensContratoResposta.getConteudo(), formatacaoItensContratoResposta.getNomeAnexo());
        }
    }

    private Button newButtonDownload(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        Button btnDownload = componentFactory.newButton("btnDonwload", () -> download(formatacaoItensContratoResposta));
        btnDownload.setVisible(formatacaoItensContratoResposta.getNomeAnexo() != null);
        return btnDownload;
    }

    private TextField<String> newTextFieldDataFoto() {
        TextField<String> textField = componentFactory.newTextField("txtDataFoto", "Data Foto", Boolean.FALSE, new PropertyModel<String>(this, "dataFoto"));
        textField.setOutputMarkupId(Boolean.TRUE);
        return textField;
    }

    private TextArea<String> newTextAreaTexto(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        TextArea<String> textArea = new TextArea<String>("textAreaTexto", new PropertyModel<String>(formatacaoItensContratoResposta, "respostaTexto"));
        textArea.setLabel(Model.of("Texto"));
        return textArea;
    }

    private TextArea<String> newTextAreaJustificativaNaoConfigurado() {
        TextArea<String> textArea = new TextArea<String>("textAreaJustificativaNaoConfigurado", new PropertyModel<String>(objetoFornecimentoContrato, "descricaoNaoConfiguradoDeAcordo"));
        textArea.setLabel(Model.of("Justificativa se o item está configurado de acordo."));

        Boolean visivel = Boolean.FALSE;

        if (objetoFornecimentoContrato.getConfiguradoDeAcordo() != null) {
            if (!objetoFornecimentoContrato.getConfiguradoDeAcordo()) {
                visivel = Boolean.TRUE;
            }
        }

        textArea.setVisible(visivel);
        return textArea;
    }

    private TextArea<String> newTextAreaJustificativaNaoFunciona() {
        TextArea<String> textArea = new TextArea<String>("textAreaJustificativaNaoFunciona", new PropertyModel<String>(objetoFornecimentoContrato, "descricaoNaoFuncionandoDeAcordo"));
        textArea.setLabel(Model.of("Justificativa se o item está funcionando de acordo."));

        Boolean visivel = Boolean.FALSE;

        if (objetoFornecimentoContrato.getFuncionandoDeAcordo() != null) {
            if (!objetoFornecimentoContrato.getFuncionandoDeAcordo()) {
                visivel = Boolean.TRUE;
            }
        }

        textArea.setVisible(visivel);
        return textArea;
    }

    private TextField<String> newTextFieldNumerico(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        TextField<String> textField = componentFactory.newTextField("textoNumerico", "Número", Boolean.FALSE, new PropertyModel<String>(formatacaoItensContratoResposta, "respostaAlfanumerico"));
        textField.setOutputMarkupId(Boolean.TRUE);
        return textField;
    }

    private TextField<String> newTextFieldTextoIdentificadorUinico(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        TextField<String> textField = componentFactory.newTextField("textoIdentificadorUnico", "Identificador Único", Boolean.FALSE, new PropertyModel<String>(formatacaoItensContratoResposta, "respostaAlfanumerico"));
        textField.setOutputMarkupId(Boolean.TRUE);
        return textField;
    }

    private InfraLocalDateTextField newDateTextFieldData(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        InfraLocalDateTextField dataTextFiel = componentFactory.newDateTextFieldWithDatePicker("data", "Data", Boolean.FALSE, new PropertyModel<LocalDate>(formatacaoItensContratoResposta, "respostaData"), "dd/MM/yyyy", "pt-BR");
        dataTextFiel.setOutputMarkupId(Boolean.TRUE);
        return dataTextFiel;
    }

    private Label newLabelOrientacaoFornecedor(FormatacaoItensContrato formatacaoItensContrato) {

        String nome = " - ";
        if (formatacaoItensContrato != null && !"".equalsIgnoreCase(formatacaoItensContrato.getOrientacaoFornecedores())) {
            nome = formatacaoItensContrato.getOrientacaoFornecedores();
        }

        Label lblOrientacaoFornecedor = componentFactory.newLabel("txtLabelOrientacaoFornecedor", nome);
        lblOrientacaoFornecedor.setOutputMarkupId(Boolean.TRUE);
        return lblOrientacaoFornecedor;
    }

    private TextField<String> newTextFieldTextoAlfanumerico(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        TextField<String> textField = componentFactory.newTextField("textoAlfanumerico", "Texto", Boolean.FALSE, new PropertyModel<String>(formatacaoItensContratoResposta, "respostaAlfanumerico"));
        textField.setOutputMarkupId(Boolean.TRUE);
        return textField;
    }

    private InfraRadioChoice<Boolean> newRadioBoleano(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        InfraRadioChoice<Boolean> radioChoiceLocal = componentFactory.newRadioChoice("radioBoleano", "Opção", Boolean.TRUE, Boolean.FALSE, "", "", new PropertyModel<Boolean>(formatacaoItensContratoResposta, "respostaBooleana"), Arrays.asList(Boolean.TRUE, Boolean.FALSE),
                (target) -> setarVariavel());
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

    private InfraRadioChoice<Boolean> newRadioItemNovo() {
        InfraRadioChoice<Boolean> radioChoiceLocal = componentFactory.newRadioChoice("radioItemNovo", "Conformidade: Item novo", Boolean.TRUE, Boolean.FALSE, "", "", new PropertyModel<Boolean>(objetoFornecimentoContrato, "estadoDeNovo"), Arrays.asList(Boolean.TRUE, Boolean.FALSE),
                (target) -> setarVariavel());
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
        InfraRadioChoice<Boolean> radioChoiceLocal = componentFactory.newRadioChoice("radioFunciona", "Conformidade: Funciona de acordo", Boolean.TRUE, Boolean.FALSE, "", "", new PropertyModel<Boolean>(objetoFornecimentoContrato, "funcionandoDeAcordo"), Arrays.asList(Boolean.TRUE, Boolean.FALSE),
                (target) -> setarVariavel());
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
                Arrays.asList(Boolean.TRUE, Boolean.FALSE), (target) -> setarVariavel());
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

    // acoes

    // mobile e desktop: busca e insere na lista, todas os objetos formatacoes
    // do item.
    private void buscaListaFormatacoesRespostas() {
        listaFormatacaoFornecedor.clear();
        listaFormatacaoBeneficiario.clear();
        listaFormatacaoAmbos.clear();

        List<FormatacaoObjetoFornecimento> listaFornecedorTEMP = new ArrayList<>();
        List<FormatacaoObjetoFornecimento> listaBeneficiarioTEMP = new ArrayList<>();

        if (this.objetoFornecimentoContrato != null && this.objetoFornecimentoContrato.getId() != null) {
            listaFornecedorTEMP.addAll(SideUtil.convertDtoToEntityFormatacaoObjetoFornecimentoDto(this.ordemFornecimentoContratoService.buscarListaFormatacaoObjetoFornecimento(this.objetoFornecimentoContrato.getId(), EnumPerfilEntidade.FORNECEDOR)));
            listaBeneficiarioTEMP.addAll(SideUtil.convertDtoToEntityFormatacaoObjetoFornecimentoDto(this.ordemFornecimentoContratoService.buscarListaFormatacaoObjetoFornecimento(this.objetoFornecimentoContrato.getId(), EnumPerfilEntidade.BENEFICIARIO)));

            for (FormatacaoObjetoFornecimento formatacao : listaBeneficiarioTEMP) {
                if (formatacao.getFormatacao().getResponsavelFormatacao().getValor().equals(EnumResponsavelPreencherFormatacaoItem.BENEFICIARIO.getValor())) {
                    listaFormatacaoBeneficiario.add(formatacao);
                }
                if (formatacao.getFormatacao().getResponsavelFormatacao().getValor().equals(EnumResponsavelPreencherFormatacaoItem.AMBOS.getValor())) {
                    listaFormatacaoAmbos.add(formatacao);
                }
            }

            for (FormatacaoObjetoFornecimento formatacao : listaFornecedorTEMP) {
                if (formatacao.getFormatacao().getResponsavelFormatacao().getValor().equals(EnumResponsavelPreencherFormatacaoItem.FORNECEDOR.getValor())) {
                    listaFormatacaoFornecedor.add(formatacao);
                }
                if (formatacao.getFormatacao().getResponsavelFormatacao().getValor().equals(EnumResponsavelPreencherFormatacaoItem.AMBOS.getValor())) {
                    listaFormatacaoAmbos.add(formatacao);
                }
            }

        }

        // ordenacao da lista.
        listaFormatacaoFornecedor.sort((FormatacaoObjetoFornecimento o1, FormatacaoObjetoFornecimento o2) -> o1.getId().compareTo(o2.getId()));
        listaFormatacaoBeneficiario.sort((FormatacaoObjetoFornecimento o1, FormatacaoObjetoFornecimento o2) -> o1.getId().compareTo(o2.getId()));
        listaFormatacaoAmbos.sort((FormatacaoObjetoFornecimento o1, FormatacaoObjetoFornecimento o2) -> o1.getId().compareTo(o2.getId()));
    }

    // desktop: seta na lista de cada tipo de formatacao.
    private void buscaFormatacoesRespostasBeneficiario() {
        for (int i = 0; i < listaFormatacaoBeneficiario.size(); i++) {

            EnumTipoCampoFormatacao tipoFormatacao = listaFormatacaoBeneficiario.get(i).getFormatacao().getTipoCampo();

            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.ALFANUMERICO.getValor())) {
                if (listaFormatacaoBeneficiario.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoBeneficiario.get(i).getFormatacao());
                    listaRespostaAlfanumericoBeneficiario.add(resposta);
                } else {
                    listaRespostaAlfanumericoBeneficiario.add(listaFormatacaoBeneficiario.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.ANEXO.getValor())) {
                if (listaFormatacaoBeneficiario.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoBeneficiario.get(i).getFormatacao());
                    listaRespostaAnexoBeneficiario.add(resposta);
                } else {
                    listaRespostaAnexoBeneficiario.add(listaFormatacaoBeneficiario.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.BOLEANO.getValor())) {
                if (listaFormatacaoBeneficiario.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoBeneficiario.get(i).getFormatacao());
                    listaRespostaBoleanoBeneficiario.add(resposta);
                } else {
                    listaRespostaBoleanoBeneficiario.add(listaFormatacaoBeneficiario.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.DATA.getValor())) {
                if (listaFormatacaoBeneficiario.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoBeneficiario.get(i).getFormatacao());
                    listaRespostaDataBeneficiario.add(resposta);
                } else {
                    listaRespostaDataBeneficiario.add(listaFormatacaoBeneficiario.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.FOTO.getValor())) {
                if (listaFormatacaoBeneficiario.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoBeneficiario.get(i).getFormatacao());
                    listaRespostaFotoBeneficiario.add(resposta);
                } else {
                    listaRespostaFotoBeneficiario.add(listaFormatacaoBeneficiario.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.NUMERICO.getValor())) {
                if (listaFormatacaoBeneficiario.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoBeneficiario.get(i).getFormatacao());
                    listaRespostaNumericoBeneficiario.add(resposta);
                } else {
                    listaRespostaNumericoBeneficiario.add(listaFormatacaoBeneficiario.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.TEXTO.getValor())) {
                if (listaFormatacaoBeneficiario.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoBeneficiario.get(i).getFormatacao());
                    listaRespostaTextoBeneficiario.add(resposta);
                } else {
                    listaRespostaTextoBeneficiario.add(listaFormatacaoBeneficiario.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.VIDEO.getValor())) {
                if (listaFormatacaoBeneficiario.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoBeneficiario.get(i).getFormatacao());
                    listaRespostaVideoBeneficiario.add(resposta);
                } else {
                    listaRespostaVideoBeneficiario.add(listaFormatacaoBeneficiario.get(i).getFormatacaoResposta());
                }
            }
        }
    }

    // desktop: seta na lista de cada tipo de formatacao.
    private void buscaFormatacoesRespostasFornecedor() {
        for (int i = 0; i < listaFormatacaoFornecedor.size(); i++) {

            EnumTipoCampoFormatacao tipoFormatacao = listaFormatacaoFornecedor.get(i).getFormatacao().getTipoCampo();

            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.ALFANUMERICO.getValor())) {
                if (listaFormatacaoFornecedor.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoFornecedor.get(i).getFormatacao());
                    listaRespostaAlfanumericoFornecedor.add(resposta);
                } else {
                    listaRespostaAlfanumericoFornecedor.add(listaFormatacaoFornecedor.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.ANEXO.getValor())) {
                if (listaFormatacaoFornecedor.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoFornecedor.get(i).getFormatacao());
                    listaRespostaAnexoFornecedor.add(resposta);
                } else {
                    listaRespostaAnexoFornecedor.add(listaFormatacaoFornecedor.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.BOLEANO.getValor())) {
                if (listaFormatacaoFornecedor.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoFornecedor.get(i).getFormatacao());
                    listaRespostaBoleanoFornecedor.add(resposta);
                } else {
                    listaRespostaBoleanoFornecedor.add(listaFormatacaoFornecedor.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.DATA.getValor())) {
                if (listaFormatacaoFornecedor.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoFornecedor.get(i).getFormatacao());
                    listaRespostaDataFornecedor.add(resposta);
                } else {
                    listaRespostaDataFornecedor.add(listaFormatacaoFornecedor.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.FOTO.getValor())) {
                if (listaFormatacaoFornecedor.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setDataFoto(LocalDateTime.now());
                    resposta.setTamanho(0L);
                    resposta.setFormatacao(listaFormatacaoFornecedor.get(i).getFormatacao());
                    listaRespostaFotoFornecedor.add(resposta);
                } else {
                    listaRespostaFotoFornecedor.add(listaFormatacaoFornecedor.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.NUMERICO.getValor())) {
                if (listaFormatacaoFornecedor.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoFornecedor.get(i).getFormatacao());
                    listaRespostaNumericoFornecedor.add(resposta);
                } else {
                    listaRespostaNumericoFornecedor.add(listaFormatacaoFornecedor.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.TEXTO.getValor())) {
                if (listaFormatacaoFornecedor.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoFornecedor.get(i).getFormatacao());
                    listaRespostaTextoFornecedor.add(resposta);
                } else {
                    listaRespostaTextoFornecedor.add(listaFormatacaoFornecedor.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.VIDEO.getValor())) {
                if (listaFormatacaoFornecedor.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoFornecedor.get(i).getFormatacao());
                    listaRespostaVideoFornecedor.add(resposta);
                } else {
                    listaRespostaVideoFornecedor.add(listaFormatacaoFornecedor.get(i).getFormatacaoResposta());
                }
            }
        }
    }

    // desktop: seta na lista de cada tipo de formatacao.
    private void buscaFormatacoesRespostasAmbosFornecedor() {
        for (int i = 0; i < listaFormatacaoAmbos.size(); i++) {

            EnumTipoCampoFormatacao tipoFormatacao = listaFormatacaoAmbos.get(i).getFormatacao().getTipoCampo();

            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.ALFANUMERICO.getValor())) {
                if (listaFormatacaoAmbos.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoAmbos.get(i).getFormatacao());
                    listaRespostaAlfanumericoAmbos.add(resposta);
                } else {
                    listaRespostaAlfanumericoAmbos.add(listaFormatacaoAmbos.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.ANEXO.getValor())) {
                if (listaFormatacaoAmbos.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoAmbos.get(i).getFormatacao());
                    listaRespostaAnexoAmbos.add(resposta);
                } else {
                    listaRespostaAnexoAmbos.add(listaFormatacaoAmbos.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.BOLEANO.getValor())) {
                if (listaFormatacaoAmbos.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoAmbos.get(i).getFormatacao());
                    listaRespostaBoleanoAmbos.add(resposta);
                } else {
                    listaRespostaBoleanoAmbos.add(listaFormatacaoAmbos.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.DATA.getValor())) {
                if (listaFormatacaoAmbos.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoAmbos.get(i).getFormatacao());
                    listaRespostaDataAmbos.add(resposta);
                } else {
                    listaRespostaDataAmbos.add(listaFormatacaoAmbos.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.FOTO.getValor())) {
                if (listaFormatacaoAmbos.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoAmbos.get(i).getFormatacao());
                    listaRespostaFotoAmbos.add(resposta);
                } else {
                    listaRespostaFotoAmbos.add(listaFormatacaoAmbos.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.NUMERICO.getValor())) {
                if (listaFormatacaoAmbos.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoAmbos.get(i).getFormatacao());
                    listaRespostaNumericoAmbos.add(resposta);
                } else {
                    listaRespostaNumericoAmbos.add(listaFormatacaoAmbos.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.TEXTO.getValor())) {
                if (listaFormatacaoAmbos.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoAmbos.get(i).getFormatacao());
                    listaRespostaTextoAmbos.add(resposta);
                } else {
                    listaRespostaTextoAmbos.add(listaFormatacaoAmbos.get(i).getFormatacaoResposta());
                }
            }
            if (tipoFormatacao.getValor().equals(EnumTipoCampoFormatacao.VIDEO.getValor())) {
                if (listaFormatacaoAmbos.get(i).getFormatacaoResposta() == null) {
                    FormatacaoItensContratoResposta resposta = new FormatacaoItensContratoResposta();
                    resposta.setFormatacao(listaFormatacaoAmbos.get(i).getFormatacao());
                    listaRespostaVideoAmbos.add(resposta);
                } else {
                    listaRespostaVideoAmbos.add(listaFormatacaoAmbos.get(i).getFormatacaoResposta());
                }
            }
        }
    }

    // desktop: seta o objeto resposta na lista pare ser persistido.
    private void setaFormatacaoContratoResposta(FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
        this.formatacaoItensContratoResposta = formatacaoItensContratoResposta;
    }

    // mobile e desktop: seta o valor selecionado no boolean no objeto resposta.
    private void setarVariavel() {

    }

    // gride

    // alfanumerico
    public DataView<FormatacaoItensContratoResposta> newDataViewListaAlfanumericoFornecedor() {
        return new DataView<FormatacaoItensContratoResposta>("listaAlfanumericoFornecedor", new ProviderAlfanumericoFornecedor()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelAlfanumerica("panelFormatacaoAlfanumericaFornecedor", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<FormatacaoItensContratoResposta> newDataViewListaAlfanumericoBeneficiario() {
        return new DataView<FormatacaoItensContratoResposta>("listaAlfanumericoBeneficiario", new ProviderAlfanumericoBeneficiario()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelAlfanumerica("panelFormatacaoAlfanumericaBeneficiario", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<PanelCompararDTO> newDataViewListaAlfanumericoAmbosFornecedor() {
        return new DataView<PanelCompararDTO>("listaAlfanumericoAmbos", new ProviderAlfanumericoAmbosFornecedor()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<PanelCompararDTO> item) {

                item.add(new PanelAlfanumerica("panelFormatacaoAlfanumericaAmbosFornecedor", item.getModelObject().getRespostaFornecedor(), item.getModelObject().getRespostaFornecedor().getFormatacao()));
                item.add(new PanelAlfanumerica("panelFormatacaoAlfanumericaAmbosBeneficiario", item.getModelObject().getRespostaBeneficiario(), item.getModelObject().getRespostaBeneficiario().getFormatacao()));
            }
        };
    }

    // anexo
    public DataView<FormatacaoItensContratoResposta> newDataViewListaAnexoFornecedor() {
        return new DataView<FormatacaoItensContratoResposta>("listaRespostaAnexoFornecedor", new ProviderAnexoFornecedor()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelAnexo("panelFormatacaoAnexoFornecedor", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<FormatacaoItensContratoResposta> newDataViewListaAnexoBeneficiario() {
        return new DataView<FormatacaoItensContratoResposta>("listaRespostaAnexoBeneficiario", new ProviderAnexoBeneficiario()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelAnexo("panelFormatacaoAnexoBeneficiario", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<PanelCompararDTO> newDataViewListaAnexoAmbosFornecedor() {
        return new DataView<PanelCompararDTO>("listaRespostaAnexoAmbos", new ProviderAnexoAmbosFornecedor()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<PanelCompararDTO> item) {

                item.add(new PanelAnexo("panelFormatacaoAnexoAmbosFornecedor", item.getModelObject().getRespostaFornecedor(), item.getModelObject().getRespostaFornecedor().getFormatacao()));
                item.add(new PanelAnexo("panelFormatacaoAnexoAmbosBeneficiario", item.getModelObject().getRespostaBeneficiario(), item.getModelObject().getRespostaBeneficiario().getFormatacao()));
            }
        };
    }

    // boleano
    public DataView<FormatacaoItensContratoResposta> newDataViewListaBoleanoFornecedor() {
        return new DataView<FormatacaoItensContratoResposta>("listaRespostaBoleanoFonecedor", new ProviderBoleanoFornecedor()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelBoleano("panelFormatacaoBoleanoFornecedor", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<FormatacaoItensContratoResposta> newDataViewListaBoleanoBeneficiario() {
        return new DataView<FormatacaoItensContratoResposta>("listaRespostaBoleanoBeneficiario", new ProviderBoleanoBeneficiario()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelBoleano("panelFormatacaoBoleanoBeneficiario", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<PanelCompararDTO> newDataViewListaBoleanoAmbosFornecedor() {
        return new DataView<PanelCompararDTO>("listaRespostaBoleanoAmbos", new ProviderBoleanoAmbosFornecedor()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<PanelCompararDTO> item) {

                item.add(new PanelBoleano("panelFormatacaoBoleanoAmbosFornecedor", item.getModelObject().getRespostaFornecedor(), item.getModelObject().getRespostaFornecedor().getFormatacao()));
                item.add(new PanelBoleano("panelFormatacaoBoleanoAmbosBeneficiario", item.getModelObject().getRespostaBeneficiario(), item.getModelObject().getRespostaBeneficiario().getFormatacao()));
            }
        };
    }

    // Data
    public DataView<FormatacaoItensContratoResposta> newDataViewListaDataFornecedor() {
        return new DataView<FormatacaoItensContratoResposta>("listaRespostaDataFornecedor", new ProviderDataFornecedor()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelData("panelFormatacaoDataFornecedor", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<FormatacaoItensContratoResposta> newDataViewListaDataBeneficiario() {
        return new DataView<FormatacaoItensContratoResposta>("listaRespostaDataBeneficiario", new ProviderDataBeneficiario()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelData("panelFormatacaoDataBeneficiario", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<PanelCompararDTO> newDataViewListaDataAmbosFornecedor() {
        return new DataView<PanelCompararDTO>("listaRespostaDataAmbos", new ProviderDataAmbosFornecedor()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<PanelCompararDTO> item) {

                item.add(new PanelData("panelFormatacaoDataAmbosFornecedor", item.getModelObject().getRespostaFornecedor(), item.getModelObject().getRespostaFornecedor().getFormatacao()));
                item.add(new PanelData("panelFormatacaoDataAmbosBeneficiario", item.getModelObject().getRespostaBeneficiario(), item.getModelObject().getRespostaBeneficiario().getFormatacao()));
            }
        };
    }

    // foto
    public DataView<FormatacaoItensContratoResposta> newDataViewListaFotoFornecedor() {
        return new DataView<FormatacaoItensContratoResposta>("listaRespostaFotoFornecedor", new ProviderFotoFornecedor()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelFoto("panelFormatacaoFotoFornecedor", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<FormatacaoItensContratoResposta> newDataViewListaFotoBeneficiario() {
        return new DataView<FormatacaoItensContratoResposta>("listaRespostaFotoBeneficiario", new ProviderFotoBeneficiario()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelFoto("panelFormatacaoFotoBeneficiario", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<PanelCompararDTO> newDataViewListaFotoAmbosFornecedor() {
        return new DataView<PanelCompararDTO>("listaRespostaFotoAmbos", new ProviderFotoAmbosFornecedor()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<PanelCompararDTO> item) {

                item.add(new PanelFoto("panelFormatacaoFotoAmbosFornecedor", item.getModelObject().getRespostaFornecedor(), item.getModelObject().getRespostaFornecedor().getFormatacao()));
                item.add(new PanelFoto("panelFormatacaoFotoAmbosBeneficiario", item.getModelObject().getRespostaBeneficiario(), item.getModelObject().getRespostaBeneficiario().getFormatacao()));
            }
        };
    }

    // numerico
    public DataView<FormatacaoItensContratoResposta> newDataViewListaNumericoFornecedor() {
        return new DataView<FormatacaoItensContratoResposta>("listaRespostaNumericoFornecedor", new ProviderNumericoFornecedor()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelNumerico("panelFormatacaoNumericoFornecedor", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<FormatacaoItensContratoResposta> newDataViewListaNumericoBeneficiario() {
        return new DataView<FormatacaoItensContratoResposta>("listaRespostaNumericoBeneficiario", new ProviderNumericoBeneficiario()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelNumerico("panelFormatacaoNumericoBeneficiario", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<PanelCompararDTO> newDataViewListaNumericoAmbosFornecedor() {
        return new DataView<PanelCompararDTO>("listaRespostaNumericoAmbos", new ProviderNumericoAmbosFornecedor()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<PanelCompararDTO> item) {

                item.add(new PanelNumerico("panelFormatacaoNumericoAmbosFornecedor", item.getModelObject().getRespostaFornecedor(), item.getModelObject().getRespostaFornecedor().getFormatacao()));
                item.add(new PanelNumerico("panelFormatacaoNumericoAmbosBeneficiario", item.getModelObject().getRespostaBeneficiario(), item.getModelObject().getRespostaBeneficiario().getFormatacao()));
            }
        };
    }

    // texto
    public DataView<FormatacaoItensContratoResposta> newDataViewListaTextoFornecedor() {
        return new DataView<FormatacaoItensContratoResposta>("listaRespostaTextoFornecedor", new ProviderTextoFornecedor()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelTexto("panelFormatacaoTextoFornecedor", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<FormatacaoItensContratoResposta> newDataViewListaTextoBeneficiario() {
        return new DataView<FormatacaoItensContratoResposta>("listaRespostaTextoBeneficiario", new ProviderTextoBeneficiario()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelTexto("panelFormatacaoTextoBeneficiario", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
    }

    public DataView<PanelCompararDTO> newDataViewListaTextoAmbosFornecedor() {
        return new DataView<PanelCompararDTO>("listaRespostaTextoAmbos", new ProviderTextoAmbosFornecedor()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<PanelCompararDTO> item) {

                item.add(new PanelTexto("panelFormatacaoTextoAmbosFornecedor", item.getModelObject().getRespostaFornecedor(), item.getModelObject().getRespostaFornecedor().getFormatacao()));
                item.add(new PanelTexto("panelFormatacaoTextoAmbosBeneficiario", item.getModelObject().getRespostaBeneficiario(), item.getModelObject().getRespostaBeneficiario().getFormatacao()));
            }
        };
    }

    // video
    public DataView<FormatacaoItensContratoResposta> newDataViewListaVideoFornecedor() {
        DataView<FormatacaoItensContratoResposta> d = new DataView<FormatacaoItensContratoResposta>("listaRespostaVideoFornecedor", new ProviderVideoFornecedor()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelVideo("panelFormatacaoVideoFornecedor", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
        d.setOutputMarkupId(Boolean.TRUE);
        return d;
    }

    public DataView<FormatacaoItensContratoResposta> newDataViewListaVideoBeneficiario() {
        DataView<FormatacaoItensContratoResposta> d = new DataView<FormatacaoItensContratoResposta>("listaRespostaVideoBeneficiario", new ProviderVideoBeneficiario()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<FormatacaoItensContratoResposta> item) {

                item.add(new PanelVideo("panelFormatacaoVideoBeneficiario", item.getModelObject(), item.getModelObject().getFormatacao()));
            }
        };
        d.setOutputMarkupId(Boolean.TRUE);
        return d;
    }

    public DataView<PanelCompararDTO> newDataViewListaVideoAmbosFornecedor() {
        DataView<PanelCompararDTO> d = new DataView<PanelCompararDTO>("listaRespostaVideoAmbos", new ProviderVideoAmbosFornecedor()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<PanelCompararDTO> item) {

                item.add(new PanelVideo("panelFormatacaoVideoAmbosFornecedor", item.getModelObject().getRespostaFornecedor(), item.getModelObject().getRespostaFornecedor().getFormatacao()));
                item.add(new PanelVideo("panelFormatacaoVideoAmbosBeneficiario", item.getModelObject().getRespostaBeneficiario(), item.getModelObject().getRespostaBeneficiario().getFormatacao()));
            }
        };
        d.setOutputMarkupId(Boolean.TRUE);
        return d;
    }

    // provaiders

    // alfanumerico
    private class ProviderAlfanumericoFornecedor extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaAlfanumericoFornecedor.iterator();
        }

        @Override
        public long size() {
            return listaRespostaAlfanumericoFornecedor.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderAlfanumericoBeneficiario extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaAlfanumericoBeneficiario.iterator();
        }

        @Override
        public long size() {
            return listaRespostaAlfanumericoBeneficiario.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderAlfanumericoAmbosFornecedor extends SortableDataProvider<PanelCompararDTO, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<PanelCompararDTO> iterator(long first, long size) {

            List<PanelCompararDTO> listaAuxiliar = new ArrayList<>();
            PanelCompararDTO dto = new PanelCompararDTO();

            for (int i = 0; i < listaRespostaAlfanumericoAmbos.size(); i++) {

                if ((i % 2) == 0) {
                    dto.setRespostaFornecedor(listaRespostaAlfanumericoAmbos.get(i));
                } else {
                    dto.setRespostaBeneficiario(listaRespostaAlfanumericoAmbos.get(i));
                    listaAuxiliar.add(dto);
                    dto = new PanelCompararDTO();
                }

            }

            return listaAuxiliar.iterator();
        }

        @Override
        public long size() {
            return listaRespostaAlfanumericoAmbos.size();
        }

        @Override
        public IModel<PanelCompararDTO> model(PanelCompararDTO object) {
            return new CompoundPropertyModel<PanelCompararDTO>(object);
        }
    }

    // anexo
    private class ProviderAnexoFornecedor extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaAnexoFornecedor.iterator();
        }

        @Override
        public long size() {
            return listaRespostaAnexoFornecedor.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderAnexoBeneficiario extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaAnexoBeneficiario.iterator();
        }

        @Override
        public long size() {
            return listaRespostaAnexoBeneficiario.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderAnexoAmbosFornecedor extends SortableDataProvider<PanelCompararDTO, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<PanelCompararDTO> iterator(long first, long size) {
            List<PanelCompararDTO> listaAuxiliar = new ArrayList<>();
            PanelCompararDTO dto = new PanelCompararDTO();

            for (int i = 0; i < listaRespostaAnexoAmbos.size(); i++) {

                if ((i % 2) == 0) {
                    dto.setRespostaFornecedor(listaRespostaAnexoAmbos.get(i));
                } else {
                    dto.setRespostaBeneficiario(listaRespostaAnexoAmbos.get(i));
                    listaAuxiliar.add(dto);
                    dto = new PanelCompararDTO();
                }

            }

            return listaAuxiliar.iterator();
        }

        @Override
        public long size() {
            return listaRespostaAnexoAmbos.size();
        }

        @Override
        public IModel<PanelCompararDTO> model(PanelCompararDTO object) {
            return new CompoundPropertyModel<PanelCompararDTO>(object);
        }
    }

    // boleano
    private class ProviderBoleanoFornecedor extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaBoleanoFornecedor.iterator();
        }

        @Override
        public long size() {
            return listaRespostaBoleanoFornecedor.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderBoleanoBeneficiario extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaBoleanoBeneficiario.iterator();
        }

        @Override
        public long size() {
            return listaRespostaBoleanoBeneficiario.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderBoleanoAmbosFornecedor extends SortableDataProvider<PanelCompararDTO, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<PanelCompararDTO> iterator(long first, long size) {
            List<PanelCompararDTO> listaAuxiliar = new ArrayList<>();
            PanelCompararDTO dto = new PanelCompararDTO();

            for (int i = 0; i < listaRespostaBoleanoAmbos.size(); i++) {

                if ((i % 2) == 0) {
                    dto.setRespostaFornecedor(listaRespostaBoleanoAmbos.get(i));
                } else {
                    dto.setRespostaBeneficiario(listaRespostaBoleanoAmbos.get(i));
                    listaAuxiliar.add(dto);
                    dto = new PanelCompararDTO();
                }

            }

            return listaAuxiliar.iterator();
        }

        @Override
        public long size() {
            return listaRespostaBoleanoAmbos.size();
        }

        @Override
        public IModel<PanelCompararDTO> model(PanelCompararDTO object) {
            return new CompoundPropertyModel<PanelCompararDTO>(object);
        }
    }

    // data
    private class ProviderDataFornecedor extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaDataFornecedor.iterator();
        }

        @Override
        public long size() {
            return listaRespostaDataFornecedor.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderDataBeneficiario extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaDataBeneficiario.iterator();
        }

        @Override
        public long size() {
            return listaRespostaDataBeneficiario.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderDataAmbosFornecedor extends SortableDataProvider<PanelCompararDTO, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<PanelCompararDTO> iterator(long first, long size) {
            List<PanelCompararDTO> listaAuxiliar = new ArrayList<>();
            PanelCompararDTO dto = new PanelCompararDTO();

            for (int i = 0; i < listaRespostaDataAmbos.size(); i++) {

                if ((i % 2) == 0) {
                    dto.setRespostaFornecedor(listaRespostaDataAmbos.get(i));
                } else {
                    dto.setRespostaBeneficiario(listaRespostaDataAmbos.get(i));
                    listaAuxiliar.add(dto);
                    dto = new PanelCompararDTO();
                }

            }

            return listaAuxiliar.iterator();
        }

        @Override
        public long size() {
            return listaRespostaDataAmbos.size();
        }

        @Override
        public IModel<PanelCompararDTO> model(PanelCompararDTO object) {
            return new CompoundPropertyModel<PanelCompararDTO>(object);
        }
    }

    // foto
    private class ProviderFotoFornecedor extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaFotoFornecedor.iterator();
        }

        @Override
        public long size() {
            return listaRespostaFotoFornecedor.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderFotoBeneficiario extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaFotoBeneficiario.iterator();
        }

        @Override
        public long size() {
            return listaRespostaFotoBeneficiario.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderFotoAmbosFornecedor extends SortableDataProvider<PanelCompararDTO, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<PanelCompararDTO> iterator(long first, long size) {
            List<PanelCompararDTO> listaAuxiliar = new ArrayList<>();
            PanelCompararDTO dto = new PanelCompararDTO();

            for (int i = 0; i < listaRespostaFotoAmbos.size(); i++) {

                if ((i % 2) == 0) {
                    dto.setRespostaFornecedor(listaRespostaFotoAmbos.get(i));
                } else {
                    dto.setRespostaBeneficiario(listaRespostaFotoAmbos.get(i));
                    listaAuxiliar.add(dto);
                    dto = new PanelCompararDTO();
                }

            }

            return listaAuxiliar.iterator();
        }

        @Override
        public long size() {
            return listaRespostaFotoAmbos.size();
        }

        @Override
        public IModel<PanelCompararDTO> model(PanelCompararDTO object) {
            return new CompoundPropertyModel<PanelCompararDTO>(object);
        }
    }

    // numerico
    private class ProviderNumericoFornecedor extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaNumericoFornecedor.iterator();
        }

        @Override
        public long size() {
            return listaRespostaNumericoFornecedor.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderNumericoBeneficiario extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaNumericoBeneficiario.iterator();
        }

        @Override
        public long size() {
            return listaRespostaNumericoBeneficiario.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderNumericoAmbosFornecedor extends SortableDataProvider<PanelCompararDTO, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<PanelCompararDTO> iterator(long first, long size) {
            List<PanelCompararDTO> listaAuxiliar = new ArrayList<>();
            PanelCompararDTO dto = new PanelCompararDTO();

            for (int i = 0; i < listaRespostaNumericoAmbos.size(); i++) {

                if ((i % 2) == 0) {
                    dto.setRespostaFornecedor(listaRespostaNumericoAmbos.get(i));
                } else {
                    dto.setRespostaBeneficiario(listaRespostaNumericoAmbos.get(i));
                    listaAuxiliar.add(dto);
                    dto = new PanelCompararDTO();
                }

            }

            return listaAuxiliar.iterator();
        }

        @Override
        public long size() {
            return listaRespostaNumericoAmbos.size();
        }

        @Override
        public IModel<PanelCompararDTO> model(PanelCompararDTO object) {
            return new CompoundPropertyModel<PanelCompararDTO>(object);
        }
    }

    // texto
    private class ProviderTextoFornecedor extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaTextoFornecedor.iterator();
        }

        @Override
        public long size() {
            return listaRespostaTextoFornecedor.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderTextoBeneficiario extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaTextoBeneficiario.iterator();
        }

        @Override
        public long size() {
            return listaRespostaTextoBeneficiario.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderTextoAmbosFornecedor extends SortableDataProvider<PanelCompararDTO, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<PanelCompararDTO> iterator(long first, long size) {
            List<PanelCompararDTO> listaAuxiliar = new ArrayList<>();
            PanelCompararDTO dto = new PanelCompararDTO();

            for (int i = 0; i < listaRespostaTextoAmbos.size(); i++) {

                if ((i % 2) == 0) {
                    dto.setRespostaFornecedor(listaRespostaTextoAmbos.get(i));
                } else {
                    dto.setRespostaBeneficiario(listaRespostaTextoAmbos.get(i));
                    listaAuxiliar.add(dto);
                    dto = new PanelCompararDTO();
                }

            }

            return listaAuxiliar.iterator();
        }

        @Override
        public long size() {
            return listaRespostaTextoAmbos.size();
        }

        @Override
        public IModel<PanelCompararDTO> model(PanelCompararDTO object) {
            return new CompoundPropertyModel<PanelCompararDTO>(object);
        }
    }

    // video
    private class ProviderVideoFornecedor extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaVideoFornecedor.iterator();
        }

        @Override
        public long size() {
            return listaRespostaVideoFornecedor.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderVideoBeneficiario extends SortableDataProvider<FormatacaoItensContratoResposta, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<FormatacaoItensContratoResposta> iterator(long first, long size) {
            return listaRespostaVideoBeneficiario.iterator();
        }

        @Override
        public long size() {
            return listaRespostaVideoBeneficiario.size();
        }

        @Override
        public IModel<FormatacaoItensContratoResposta> model(FormatacaoItensContratoResposta object) {
            return new CompoundPropertyModel<FormatacaoItensContratoResposta>(object);
        }
    }

    private class ProviderVideoAmbosFornecedor extends SortableDataProvider<PanelCompararDTO, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<PanelCompararDTO> iterator(long first, long size) {
            List<PanelCompararDTO> listaAuxiliar = new ArrayList<>();
            PanelCompararDTO dto = new PanelCompararDTO();

            for (int i = 0; i < listaRespostaVideoAmbos.size(); i++) {

                if ((i % 2) == 0) {
                    dto.setRespostaFornecedor(listaRespostaVideoAmbos.get(i));
                } else {
                    dto.setRespostaBeneficiario(listaRespostaVideoAmbos.get(i));
                    listaAuxiliar.add(dto);
                    dto = new PanelCompararDTO();
                }

            }

            return listaAuxiliar.iterator();
        }

        @Override
        public long size() {
            return listaRespostaVideoAmbos.size();
        }

        @Override
        public IModel<PanelCompararDTO> model(PanelCompararDTO object) {
            return new CompoundPropertyModel<PanelCompararDTO>(object);
        }
    }

    // paineis

    private class PanelAlfanumerica extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelAlfanumerica(String id, FormatacaoItensContratoResposta formatacaoItensContratoResposta, FormatacaoItensContrato formatacaoItensContrato) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);

            if (formatacaoItensContratoResposta == null) {
                formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
            }

            add(newLabelTituloQuesito(formatacaoItensContrato));
            add(newLabelIconeInfo(formatacaoItensContratoResposta));
            add(newLabelOrientacaoFornecedor(formatacaoItensContrato));
            add(newTextFieldTextoAlfanumerico(formatacaoItensContratoResposta));
        }

    }

    private class PanelIdentificadorUnico extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelIdentificadorUnico(String id, FormatacaoItensContratoResposta formatacaoItensContratoResposta) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);

            if (formatacaoItensContratoResposta == null) {
                formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
            }

            add(newTextFieldTextoIdentificadorUinico(formatacaoItensContratoResposta));
            setVisible(formatacaoItensContratoResposta.getFormatacao().getPossuiIdentificadorUnico());
            setEnabled(Boolean.FALSE);
        }

    }

    private class PanelAnexo extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelAnexo(String id, FormatacaoItensContratoResposta formatacaoItensContratoResposta, FormatacaoItensContrato formatacaoItensContrato) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);

            if (formatacaoItensContratoResposta == null) {
                formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
            }

            add(newLabelTituloQuesito(formatacaoItensContrato));
            add(newLabelIconeInfo(formatacaoItensContratoResposta));
            add(newLabelOrientacaoFornecedor(formatacaoItensContrato));
            setaFormatacaoContratoResposta(formatacaoItensContratoResposta);
            add(newLabelNomeAnexo(formatacaoItensContratoResposta));
            add(newLabelTamanhoAnexo(formatacaoItensContratoResposta));
            add(newButtonDownload(formatacaoItensContratoResposta));
        }

    }

    private class PanelBoleano extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelBoleano(String id, FormatacaoItensContratoResposta formatacaoItensContratoResposta, FormatacaoItensContrato formatacaoItensContrato) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);

            if (formatacaoItensContratoResposta == null) {
                formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
            }

            add(newLabelTituloQuesito(formatacaoItensContrato));
            add(newLabelIconeInfo(formatacaoItensContratoResposta));
            add(newLabelOrientacaoFornecedor(formatacaoItensContrato));
            add(newRadioBoleano(formatacaoItensContratoResposta));
        }

    }

    private class PanelData extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelData(String id, FormatacaoItensContratoResposta formatacaoItensContratoResposta, FormatacaoItensContrato formatacaoItensContrato) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);

            if (formatacaoItensContratoResposta == null) {
                formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
            }

            add(newLabelTituloQuesito(formatacaoItensContrato));
            add(newLabelIconeInfo(formatacaoItensContratoResposta));
            add(newLabelOrientacaoFornecedor(formatacaoItensContrato));
            add(newDateTextFieldData(formatacaoItensContratoResposta));
        }

    }

    private class PanelFoto extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelFoto(String id, FormatacaoItensContratoResposta formatacaoItensContratoResposta, FormatacaoItensContrato formatacaoItensContrato) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);

            if (formatacaoItensContratoResposta == null) {
                formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
            }

            add(newLabelTituloQuesito(formatacaoItensContrato));
            add(newLabelIconeInfo(formatacaoItensContratoResposta));
            add(newLabelOrientacaoFornecedor(formatacaoItensContrato));

            if (formatacaoItensContratoResposta.getConteudo() != null) {
                add(newImageFoto(formatacaoItensContratoResposta));
            } else {
                add(newImageFotoPadrao());
            }

            setaFormatacaoContratoResposta(formatacaoItensContratoResposta);
            add(newLabelDataFoto(formatacaoItensContratoResposta));
            add(newLabelLatitudeLongitude(formatacaoItensContratoResposta));
            add(new PanelIdentificadorUnico("panelFormatacaoIdentificadorUnico", formatacaoItensContratoResposta));
        }

    }

    private class PanelNumerico extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelNumerico(String id, FormatacaoItensContratoResposta formatacaoItensContratoResposta, FormatacaoItensContrato formatacaoItensContrato) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);

            if (formatacaoItensContratoResposta == null) {
                formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
            }

            add(newLabelTituloQuesito(formatacaoItensContrato));
            add(newLabelIconeInfo(formatacaoItensContratoResposta));
            add(newLabelOrientacaoFornecedor(formatacaoItensContrato));
            add(newTextFieldNumerico(formatacaoItensContratoResposta));
        }

    }

    private class PanelTexto extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelTexto(String id, FormatacaoItensContratoResposta formatacaoItensContratoResposta, FormatacaoItensContrato formatacaoItensContrato) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);

            if (formatacaoItensContratoResposta == null) {
                formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
            }

            add(newLabelTituloQuesito(formatacaoItensContrato));
            add(newLabelIconeInfo(formatacaoItensContratoResposta));
            add(newLabelOrientacaoFornecedor(formatacaoItensContrato));
            add(newTextAreaTexto(formatacaoItensContratoResposta));
        }

    }

    private class PanelVideo extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelVideo(String id, FormatacaoItensContratoResposta formatacaoItensContratoResposta, FormatacaoItensContrato formatacaoItensContrato) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);

            if (formatacaoItensContratoResposta == null) {
                formatacaoItensContratoResposta = new FormatacaoItensContratoResposta();
            }

            add(newLabelTituloQuesito(formatacaoItensContrato));
            add(newLabelIconeInfo(formatacaoItensContratoResposta));
            add(newLabelOrientacaoFornecedor(formatacaoItensContrato));
            setaFormatacaoContratoResposta(formatacaoItensContratoResposta);
            add(newLabelNomeAnexo(formatacaoItensContratoResposta));
            add(newLabelTamanhoAnexo(formatacaoItensContratoResposta));
            add(newButtonDownload(formatacaoItensContratoResposta));

        }

    }

    private class PanelDesktop extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelDesktop(String id) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);

            add(newDataViewListaAlfanumericoFornecedor().setEnabled(Boolean.FALSE));
            add(newDataViewListaAlfanumericoBeneficiario().setEnabled(Boolean.FALSE));
            add(newDataViewListaAlfanumericoAmbosFornecedor().setEnabled(Boolean.FALSE));

            add(newDataViewListaAnexoFornecedor());
            add(newDataViewListaAnexoBeneficiario());
            add(newDataViewListaAnexoAmbosFornecedor());

            add(newDataViewListaBoleanoFornecedor().setEnabled(Boolean.FALSE));
            add(newDataViewListaBoleanoBeneficiario().setEnabled(Boolean.FALSE));
            add(newDataViewListaBoleanoAmbosFornecedor().setEnabled(Boolean.FALSE));

            add(newDataViewListaDataFornecedor().setEnabled(Boolean.FALSE));
            add(newDataViewListaDataBeneficiario().setEnabled(Boolean.FALSE));
            add(newDataViewListaDataAmbosFornecedor().setEnabled(Boolean.FALSE));

            add(newDataViewListaFotoFornecedor());
            add(newDataViewListaFotoBeneficiario());
            add(newDataViewListaFotoAmbosFornecedor());

            add(newDataViewListaNumericoFornecedor().setEnabled(Boolean.FALSE));
            add(newDataViewListaNumericoBeneficiario().setEnabled(Boolean.FALSE));
            add(newDataViewListaNumericoAmbosFornecedor().setEnabled(Boolean.FALSE));

            add(newDataViewListaTextoFornecedor().setEnabled(Boolean.FALSE));
            add(newDataViewListaTextoBeneficiario().setEnabled(Boolean.FALSE));
            add(newDataViewListaTextoAmbosFornecedor().setEnabled(Boolean.FALSE));

            add(newDataViewListaVideoFornecedor());
            add(newDataViewListaVideoBeneficiario());
            add(newDataViewListaVideoAmbosFornecedor());

            add(new PanelConformidadeItem("panelFormatacaoConformidadeItem"));
        }

    }

    private class PanelConformidadeItem extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelConformidadeItem(String id) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);

            add(newLabelIconeInfoConformidade());
            add(newRadioItemNovo().setEnabled(Boolean.FALSE));
            add(newRadioFunciona().setEnabled(Boolean.FALSE));
            add(newTextAreaJustificativaNaoFunciona().setEnabled(Boolean.FALSE));
            add(newRadioConfigurado().setEnabled(Boolean.FALSE));
            add(newTextAreaJustificativaNaoConfigurado().setEnabled(Boolean.FALSE));
        }

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
