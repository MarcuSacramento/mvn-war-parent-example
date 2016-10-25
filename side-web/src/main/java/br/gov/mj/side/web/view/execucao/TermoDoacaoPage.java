package br.gov.mj.side.web.view.execucao;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusGeracaoTermoDoacao;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumTipoArquivoTermoEntrega;
import br.gov.mj.side.entidades.enums.EnumTipoMinuta;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.TermoDoacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.AnexoNotaRemessa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.TermoRecebimentoDefinitivo;
import br.gov.mj.side.web.dto.AnexoDto;
import br.gov.mj.side.web.dto.TermoDefinitivoItensDto;
import br.gov.mj.side.web.dto.TermoDoacaoDto;
import br.gov.mj.side.web.dto.TermoRecebimentoDefinitivoDto;
import br.gov.mj.side.web.service.AnexoNotaRemessaService;
import br.gov.mj.side.web.service.AnexoTermoDoacaoService;
import br.gov.mj.side.web.service.AnexoTermoRecebimentoDefinitivoService;
import br.gov.mj.side.web.service.BeneficiarioService;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.service.TermoDoacaoService;
import br.gov.mj.side.web.service.TermoRecebimentoDefinitivoService;
import br.gov.mj.side.web.util.CnpjUtil;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.DataUtil;
import br.gov.mj.side.web.util.MascaraUtils;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.components.EntityDataProvider;
import br.gov.mj.side.web.view.programa.PanelFasePrograma;
import br.gov.mj.side.web.view.template.TemplatePage;

public class TermoDoacaoPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    // #######################################_VARIAVEIS_############################################
    private Form<TermoDoacaoPage> form;
    private Programa programa;
    private Page backPage;
    private Integer abaClicada;
    private Entidade beneficiarioSelecionado;
    private Entidade cnpjSelecionado;
    private Entidade estadoSelecionado;
    private Bem itemSelecionado;
    private Boolean trdSelecionarTodos;
    private List<TermoRecebimentoDefinitivo> listaTermoComNotaFiscal = new ArrayList<>();

    // #######################################_CONSTANTE_############################################
    private Integer itensPorPaginaTermoRecebimentoDefinitivo = Constants.ITEMS_PER_PAGE_PAGINATION;
    private Integer itensPorPaginaDoacoes = Constants.ITEMS_PER_PAGE_PAGINATION;
    private boolean btnGerarTD = Boolean.FALSE;
    private static final String ONCHANGE = "onchange";
    private static final String ONKEYUP = "onkeyup";
    private static final String ONBLUR = "onblur";

    // #######################################_ELEMENTOS_WICKET_############################################
    private DataView<TermoRecebimentoDefinitivo> dataViewObjetoTRD;
    private DataView<TermoDoacao> dataViewObjetoDoacoes;
    private List<TermoRecebimentoDefinitivo> listaTermoRecebimentoDefinitivo = new ArrayList<TermoRecebimentoDefinitivo>();
    private List<TermoDoacao> listaDoacoes = new ArrayList<TermoDoacao>();
    private List<TermoRecebimentoDefinitivo> listaTRDSelecionados = new ArrayList<TermoRecebimentoDefinitivo>();
    private List<TermoDoacaoDto> listatermoDoacaoDto = new ArrayList<TermoDoacaoDto>();
    private List<InscricaoPrograma> listaInscricaoPrograma = new ArrayList<InscricaoPrograma>();

    // #######################################_PAINEIS_############################################
    private PanelPesquisa panelPesquisa;
    private PanelTermoRecebimentoDefinitivo panelTermoRecebimentoDefinitivo;
    private PanelDoacoes panelDoacoes;

    // #####################################_INJEÇÃO_DE_DEPENDENCIA_##############################################
    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private TermoRecebimentoDefinitivoService termoRecebimentoDefinitivoService;
    @Inject
    private GenericEntidadeService genericEntidadeService;
    @Inject
    private ProgramaService programaService;
    @Inject
    private InscricaoProgramaService inscricaoProgramaService;
    @Inject
    private TermoDoacaoService termoDoacaoService;
    @Inject
    private AnexoTermoRecebimentoDefinitivoService anexoTermoRecebimentoDefinitivoService;
    @Inject
    private AnexoTermoDoacaoService anexoTermoDoacaoService;
    @Inject
    private BeneficiarioService beneficiarioService;
    @Inject
    private AnexoNotaRemessaService anexoNotaRemessaService;

    // #####################################_CONSTRUTOR_##############################################
    public TermoDoacaoPage(final PageParameters pageParameters, Programa programa, Page backPage, Integer abaClicada) {
        super(pageParameters);
        setTitulo("Gerenciar Programa");
        this.backPage = backPage;
        this.programa = programa;
        this.abaClicada = abaClicada;

        initVariaveis();
        initComponents();
    }

    private void initVariaveis() {
        TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto = new TermoRecebimentoDefinitivoDto();
        TermoDoacaoDto termoDoacaoDto = new TermoDoacaoDto();
        listaTermoRecebimentoDefinitivo = new ArrayList<TermoRecebimentoDefinitivo>();
        listaDoacoes = new ArrayList<TermoDoacao>();

        termoRecebimentoDefinitivoDto.setPrograma(programa);
        termoRecebimentoDefinitivoDto.setStatusGeracaoTermoDoacao(EnumStatusGeracaoTermoDoacao.TERMO_DOACAO_NAO_GERADO);
        termoDoacaoDto.setPrograma(programa);

        /*
         * List<TermoRecebimentoDefinitivo> listaTermoRD = new
         * ArrayList<TermoRecebimentoDefinitivo>(); listaTermoRD =
         * termoRecebimentoDefinitivoService
         * .buscarTermosRecebimentoDefinitivoPorPrograma(programa);
         */

        listaTermoRecebimentoDefinitivo = termoRecebimentoDefinitivoService.buscarSemPaginacao(termoRecebimentoDefinitivoDto);

        /*
         * for (TermoRecebimentoDefinitivo listaTRD : listaTermoRD) { if
         * (listaTRD
         * .getStatusTermoRecebimento().equals(EnumStatusGeracaoTermoDoacao
         * .TERMO_DOACAO_NAO_GERADO))
         * listaTermoRecebimentoDefinitivo.add(listaTRD); }
         */

        listaDoacoes = termoDoacaoService.buscarSemPaginacao(termoDoacaoDto);

        // ordenacao da lista.
        listaDoacoes.sort((TermoDoacao o1, TermoDoacao o2) -> o1.getNomeBeneficiario().compareTo(o2.getNomeBeneficiario()));
    }

    private void initComponents() {

        form = new Form<TermoDoacaoPage>("form", new CompoundPropertyModel<TermoDoacaoPage>(this));

        form.add(new PanelFasePrograma("panelFasePrograma", programa, backPage, abaClicada));
        form.add(new ExecucaoPanelBotoes("execucaoPanelPotoes", programa, backPage, "TermoDoacao"));

        form.add(panelPesquisa = newPanelPesquisa());

        form.add(panelTermoRecebimentoDefinitivo = newPanelTermoRecebimentoDefinitivo());
        form.add(panelDoacoes = newPanelDoacoes());
        form.add(new PanelButton());

        add(form);
    }

    // ####################################_PAINEIS_##############################################
    // PAINEL PESQUISA
    public PanelPesquisa newPanelPesquisa() {
        panelPesquisa = new PanelPesquisa();
        setOutputMarkupId(Boolean.TRUE);
        return panelPesquisa;
    }

    private class PanelPesquisa extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelPesquisa() {
            super("panelPesquisa");

            add(newDropDownBeneficiario());
            add(newDropDownCnpj());
            add(newDropDownEstado());
            add(newDropDownItem());
            add(newButtonPesquisar());
            add(newButtonLimpar());
        }
    }

    // PAINEL TERMO DE RECEBIMENTO DEFINITIVO
    public PanelTermoRecebimentoDefinitivo newPanelTermoRecebimentoDefinitivo() {
        panelTermoRecebimentoDefinitivo = new PanelTermoRecebimentoDefinitivo();
        setOutputMarkupId(Boolean.TRUE);
        return panelTermoRecebimentoDefinitivo;
    }

    private class PanelTermoRecebimentoDefinitivo extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelTermoRecebimentoDefinitivo() {
            super("panelTermoRecebimentoDefinitivo");

            add(newCheckBoxSelectedTodos());
            add(newDataViewTermoRecebimentoDefinitivo());
            add(newGerarTermoDoacao());

            add(newDropItensPorPaginaTermoRecebimentoDefinitivo());
            add(new InfraAjaxPagingNavigator("paginationTRD", dataViewObjetoTRD));
        }
    }

    // PAINEL DOAÇÕES
    public PanelDoacoes newPanelDoacoes() {
        panelDoacoes = new PanelDoacoes();
        setOutputMarkupId(Boolean.TRUE);
        return panelDoacoes;
    }

    private class PanelDoacoes extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDoacoes() {
            super("panelDoacoes");

            add(newDataViewDoacoes());

            add(newDropItensPorPaginaDoacoes());
            add(new InfraAjaxPagingNavigator("paginationDoacoes", dataViewObjetoDoacoes));
        }
    }

    // PAINEL BOTÃO
    private class PanelButton extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelButton() {
            super("panelButton");
            setOutputMarkupId(Boolean.TRUE);

            add(componentFactory.newAjaxFallbackLink("btnVoltar", (target) -> setResponsePage(backPage)));
        }
    }

    // ####################################_COMPONENTE_WICKET_###############################################
    private InfraDropDownChoice<Entidade> newDropDownBeneficiario() {
        InfraDropDownChoice<Entidade> dropDownChoice = componentFactory.newDropDownChoice("beneficiario", "Beneficiário", Boolean.FALSE, "id", "nomeEntidade", new LambdaModel<Entidade>(this::getBeneficiarioSelecionado, this::setBeneficiarioSelecionado), getListBeneficiario(), null);
        dropDownChoice.setNullValid(Boolean.TRUE);
        actionDownBeneficiario(dropDownChoice);
        return dropDownChoice;
    }

    private InfraDropDownChoice<Entidade> newDropDownCnpj() {
        InfraDropDownChoice<Entidade> dropDownChoice = componentFactory.newDropDownChoice("cnpj", "CNPJ", Boolean.FALSE, "id", "numeroCnpj", new LambdaModel<Entidade>(this::getCnpjSelecionado, this::setCnpjSelecionado), getListCnpj(), null);
        dropDownChoice.setNullValid(Boolean.TRUE);
        actionDownBeneficiario(dropDownChoice);
        return dropDownChoice;
    }

    private InfraDropDownChoice<Entidade> newDropDownEstado() {
        InfraDropDownChoice<Entidade> dropDownChoice = componentFactory.newDropDownChoice("estado", "Estado", Boolean.FALSE, "id", "municipio.uf.nomeUf", new LambdaModel<Entidade>(this::getEstadoSelecionado, this::setEstadoSelecionado), getListEstado(), null);
        dropDownChoice.setNullValid(Boolean.TRUE);
        actionDownBeneficiario(dropDownChoice);
        return dropDownChoice;
    }

    private InfraDropDownChoice<Bem> newDropDownItem() {
        InfraDropDownChoice<Bem> dropDownChoice = componentFactory.newDropDownChoice("item", "Item", Boolean.FALSE, "id", "nomeBem", new LambdaModel<Bem>(this::getItemSelecionado, this::setItemSelecionado), getListItem(), null);
        dropDownChoice.setNullValid(Boolean.TRUE);
        actionDownItem(dropDownChoice);
        return dropDownChoice;
    }

    private void actionDownBeneficiario(InfraDropDownChoice<Entidade> dropDownChoice) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no Model
            }
        };
        dropDownChoice.add(onChangeAjaxBehavior);
    }

    private void actionDownItem(InfraDropDownChoice<Bem> dropDownChoice) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // Setar no Model
            }
        };
        dropDownChoice.add(onChangeAjaxBehavior);
    }

    private InfraAjaxFallbackLink<Void> newButtonPesquisar() {
        return componentFactory.newAjaxFallbackLink("btnPesquisar", (target) -> pesquisar(target));
    }

    private InfraAjaxFallbackLink<Void> newButtonLimpar() {
        return componentFactory.newAjaxFallbackLink("btnLimpar", (target) -> limpar(target));
    }

    private AjaxCheckBox newCheckBoxSelectedTodos() {
        AjaxCheckBox checkItensSelectedTodosTRD = new AjaxCheckBox("checkTRDSelectedTodos", new PropertyModel<Boolean>(this, "trdSelecionarTodos")) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                actionCheckTRDTodos(target);
            }
        };
        checkItensSelectedTodosTRD.setOutputMarkupId(Boolean.TRUE);
        return checkItensSelectedTodosTRD;
    }

    // DataView Termo de Recebimento Definitivo
    private DataView<TermoRecebimentoDefinitivo> newDataViewTermoRecebimentoDefinitivo() {
        dataViewObjetoTRD = new DataView<TermoRecebimentoDefinitivo>("listaTRD", new EntityDataProvider<TermoRecebimentoDefinitivo>(listaTermoRecebimentoDefinitivo)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<TermoRecebimentoDefinitivo> item) {
                AttributeAppender classeAtivarPopover = new AttributeAppender("class", "pop", " ");

                setarListaTermoComNotaFiscal(item.getModelObject());

                item.add(newCheckBoxSelected(item));

                item.add(newLabelNomeBeneficiario(item.getModelObject()));
                item.add(new Label("nomeAnexo"));

                String[] listaItens = formatarLista(item);
                String iconeItem = "<a tabindex=\"0\" role=\"button\" data-toggle=\"popover\" data-trigger=\"focus\" data-content=\"" + listaItens[0] + "\"><i style=\"color:green;\" class=\"fa fa-info-circle\"> </i></a>";
                Label lblItem = new Label("itens", iconeItem);
                lblItem.setEscapeModelStrings(Boolean.FALSE);
                lblItem.setOutputMarkupId(Boolean.TRUE);
                if (item.getModelObject().getObjetosFornecimentoContrato().size() > 10)
                    lblItem.add(classeAtivarPopover);
                item.add(lblItem);

                String dataGeracao = DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataGeracao(), "dd/MM/yyyy");
                item.add(new Label("dataGeracao", dataGeracao));

                item.add(newTextFieldNumeroProcessoSei(item));
                item.add(newTextFieldNumeroDocumentoSei(item));

                Button btnDownloadTRD = componentFactory.newButton("btnDownloadTRD", () -> downloadTRD(item.getModelObject()));
                item.add(btnDownloadTRD);

                Button btnDownloadNF = componentFactory.newButton("btnDownloadNF", () -> downloadNF(item.getModelObject()));
                btnDownloadNF.setVisible(verificaTermoComNotaFiscal(item.getModelObject()));
                item.add(btnDownloadNF);
            }

        };
        dataViewObjetoTRD.setItemsPerPage(getItensPorPaginaTermoRecebimentoDefinitivo() == null ? Constants.ITEMS_PER_PAGE_PAGINATION : getItensPorPaginaTermoRecebimentoDefinitivo());
        return dataViewObjetoTRD;
    }

    private Label newLabelNomeBeneficiario(TermoRecebimentoDefinitivo termo) {
        String textoNomeBeneficiario = termo.getNomeBeneficiario();

        if (!verificaTermoComNotaFiscal(termo)) {
            textoNomeBeneficiario = textoNomeBeneficiario + "<p style=\"color: red;\">Nota Fiscal não anexada.</p>";
        }

        Label lbl = new Label("nomeBeneficiario", textoNomeBeneficiario);
        lbl.setEscapeModelStrings(Boolean.FALSE);
        return lbl;
    }

    private TextField<String> newTextFieldNumeroProcessoSei(Item<TermoRecebimentoDefinitivo> item) {
        TextField<String> field = componentFactory.newTextField("numeroProcessoSei", "Processo SEI", Boolean.FALSE, new PropertyModel<String>(item.getModelObject(), "numeroProcessoSEI"));
        acaoNumeroProcessoSei(field, item);
        field.setEnabled(item.getModelObject().getTrdSelecionado() == null || item.getModelObject().getTrdSelecionado() == Boolean.FALSE ? Boolean.FALSE : Boolean.TRUE);
        field.add(StringValidator.maximumLength(20));
        field.setOutputMarkupId(Boolean.TRUE);
        return field;
    }

    private TextField<String> newTextFieldNumeroDocumentoSei(Item<TermoRecebimentoDefinitivo> item) {
        TextField<String> field = componentFactory.newTextField("numeroDocumentoSei", "Nº Documento SEI", Boolean.FALSE, new PropertyModel<String>(item.getModelObject(), "numeroDocumentoSei"));
        acaoNumeroDocumentoSei(field, item);
        field.setEnabled(item.getModelObject().getTrdSelecionado() == null || item.getModelObject().getTrdSelecionado() == Boolean.FALSE ? Boolean.FALSE : Boolean.TRUE);
        field.add(StringValidator.maximumLength(8));
        field.setOutputMarkupId(Boolean.TRUE);
        return field;
    }

    private TextField<String> acaoNumeroDocumentoSei(TextField<String> textNumeroDocumentoSei, Item<TermoRecebimentoDefinitivo> item) {
        textNumeroDocumentoSei.add(new AjaxFormComponentUpdatingBehavior(ONKEYUP) {
            private static final long serialVersionUID = 1L;

            protected void onUpdate(AjaxRequestTarget target) {

            }
        });
        return textNumeroDocumentoSei;
    }

    private TextField<String> acaoNumeroProcessoSei(TextField<String> textNumeroProcessoSei, Item<TermoRecebimentoDefinitivo> item) {
        textNumeroProcessoSei.add(new AjaxFormComponentUpdatingBehavior(ONBLUR) {
            private static final long serialVersionUID = 1L;

            protected void onUpdate(AjaxRequestTarget target) {
                setaNumeroSeiParaMesmoBeneficiario(textNumeroProcessoSei, item);
                target.add(panelTermoRecebimentoDefinitivo);
            }
        });
        return textNumeroProcessoSei;
    }

    protected void setaNumeroSeiParaMesmoBeneficiario(TextField<String> textNumeroProcessoSei, Item<TermoRecebimentoDefinitivo> item) {
        for (int i = 0; i < listaTermoRecebimentoDefinitivo.size(); i++) {
            if (item.getModelObject().getEntidade().equals(listaTermoRecebimentoDefinitivo.get(i).getEntidade())) {
                listaTermoRecebimentoDefinitivo.get(i).setNumeroProcessoSEI(textNumeroProcessoSei.getConvertedInput());
            }
        }

    }

    private AjaxCheckBox newCheckBoxSelected(Item<TermoRecebimentoDefinitivo> item) {
        AjaxCheckBox checkItensSelectedTRD = new AjaxCheckBox("checkTRDSelected", new PropertyModel<Boolean>(item.getModelObject(), "trdSelecionado")) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                actionCheckTRD(target, item);
            }
        };
        checkItensSelectedTRD.setEnabled(verificaTermoComNotaFiscal(item.getModelObject()));
        checkItensSelectedTRD.setOutputMarkupId(Boolean.TRUE);
        return checkItensSelectedTRD;
    }

    private void setarListaTermoComNotaFiscal(TermoRecebimentoDefinitivo termo) {
        List<AnexoDto> listaAnexos = this.anexoNotaRemessaService.buscarPeloIdNotaRemessa(termo.getNotaRemessaOrdemFornecimentoContrato().getId());
        List<AnexoNotaRemessa> listaAnexoNotaRemessa = SideUtil.convertAnexoDtoToEntityAnexoNotaRemessa(listaAnexos);

        for (AnexoNotaRemessa anexo : listaAnexoNotaRemessa) {
            if (anexo.getTipoArquivoTermoEntrega() == EnumTipoArquivoTermoEntrega.NOTA_FISCAL) {
                this.listaTermoComNotaFiscal.add(termo);
            }
        }
    }

    private boolean verificaTermoComNotaFiscal(TermoRecebimentoDefinitivo termo) {
        return termo.getNomeAnexoNotaFiscal() != null && !termo.getNomeAnexoNotaFiscal().trim().equalsIgnoreCase(new String());
    }

    private InfraAjaxFallbackLink<Void> newGerarTermoDoacao() {
        InfraAjaxFallbackLink<Void> btnGerarTermoDoacao = componentFactory.newAjaxFallbackLink("btnGerarTermoDoacao", (target) -> actionGerarTermoDoacao(target));
        if (listaTermoRecebimentoDefinitivo.size() > 0) {
            btnGerarTD = Boolean.TRUE;
        } else {
            btnGerarTD = Boolean.FALSE;
        }
        btnGerarTermoDoacao.setVisible(btnGerarTD);
        btnGerarTermoDoacao.setOutputMarkupId(Boolean.TRUE);
        return btnGerarTermoDoacao;
    }

    private DropDownChoice<Integer> newDropItensPorPaginaTermoRecebimentoDefinitivo() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPaginaTermoRecebimentoDefinitivo", new LambdaModel<Integer>(this::getItensPorPaginaTermoRecebimentoDefinitivo, this::setItensPorPaginaTermoRecebimentoDefinitivo), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewObjetoTRD.setItemsPerPage(getItensPorPaginaTermoRecebimentoDefinitivo());
                target.add(panelTermoRecebimentoDefinitivo);
            };
        });
        return dropDownChoice;
    }

    // DataView Doações
    private DataView<TermoDoacao> newDataViewDoacoes() {
        dataViewObjetoDoacoes = new DataView<TermoDoacao>("listaDoacoes", new EntityDataProvider<TermoDoacao>(listaDoacoes)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<TermoDoacao> item) {
                AttributeAppender classeAtivarPopover = new AttributeAppender("class", "pop", " ");

                item.add(new Label("nomeBeneficiario"));
                item.add(new Label("numeroCnpj", CnpjUtil.imprimeCNPJ(item.getModelObject().getNumeroCnpj())));

                StringBuilder sb = new StringBuilder(1);
                sb.append("<table><tr><td><center>  ID  </center></td><td>&emsp;</td><td><center>  ITEM  </center></td></tr><tr><td colspan='3'><hr></hr></td></tr>");

                for (ObjetoFornecimentoContrato obj : item.getModelObject().getObjetosFornecimentoContrato()) {
                    sb.append("<tr><td><center>  " + obj.getId() + "  </center>  </td><td>&emsp;</td><td><center>  " + obj.getItem().getNomeBem() + "  </center></td></tr>");
                }
                sb.append("</table>");

                String iconeIdItem = "<a tabindex=\"0\" role=\"button\" data-toggle=\"popover\" data-trigger=\"focus\" data-content=\"" + sb.toString() + "\"><i style=\"color:green;\" class=\"fa fa-info-circle\"> </i></a>";
                Label lblItem = new Label("itens1", iconeIdItem);
                lblItem.setEscapeModelStrings(Boolean.FALSE);
                lblItem.setOutputMarkupId(Boolean.TRUE);
                if (item.getModelObject().getObjetosFornecimentoContrato().size() > 10)
                    lblItem.add(classeAtivarPopover);
                item.add(lblItem);

                String dataGeracao = DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataGeracao(), "dd/MM/yyyy");
                item.add(new Label("dataGeracao", dataGeracao));

                Button btnDownloadTD = componentFactory.newButton("btnDownloadTD", () -> downloadTD(item));
                item.add(btnDownloadTD);
            }

        };
        dataViewObjetoDoacoes.setItemsPerPage(getItensPorPaginaDoacoes() == null ? Constants.ITEMS_PER_PAGE_PAGINATION : getItensPorPaginaDoacoes());
        return dataViewObjetoDoacoes;
    }

    private DropDownChoice<Integer> newDropItensPorPaginaDoacoes() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensItensPorPaginaDoacoes", new LambdaModel<Integer>(this::getItensPorPaginaDoacoes, this::setItensPorPaginaDoacoes), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewObjetoDoacoes.setItemsPerPage(getItensPorPaginaDoacoes());
                target.add(panelDoacoes);

            };
        });
        return dropDownChoice;
    }

    private String[] formatarLista(Item<TermoRecebimentoDefinitivo> item) {
        String[] lista = new String[1];

        // List<ObjetoFornecimentoContrato> itensTRD = new
        // ArrayList<ObjetoFornecimentoContrato>();
        // itensTRD =
        // termoRecebimentoDefinitivoService.buscarTodosOsObjetosFornecimentoContratoPeloIdTermo(item.getModelObject().getId());
        // item.getModelObject().setObjetosFornecimentoContrato(itensTRD);

        StringBuilder sb = new StringBuilder(1);
        sb.append("<table><tr><td><center>  ID  </center></td><td>&emsp;</td><td><center>  ITEM  </center></td></tr><tr><td colspan='3'><hr></hr></td></tr>");

        for (ObjetoFornecimentoContrato obj : item.getModelObject().getObjetosFornecimentoContrato()) {
            sb.append("<tr><td><center>  " + obj.getId() + "  </center>  </td><td>&emsp;</td><td><center>  " + obj.getItem().getNomeBem() + "  </center></td></tr>");
        }
        sb.append("</table>");

        lista[0] = sb.toString();

        return lista;
    }

    // ####################################_AÇÕES_###############################################
    private List<Entidade> getListBeneficiario() {
        return genericEntidadeService.buscarTodosBeneficiariosPeloPrograma(programa);
    }

    private List<Entidade> getListCnpj() {
        return genericEntidadeService.buscarTodosBeneficiariosPeloPrograma(programa);
    }

    private List<Entidade> getListEstado() {
        return ordenarListaEstado(genericEntidadeService.buscarTodosBeneficiariosPeloPrograma(programa));
    }

    private List<Bem> getListItem() {
        return ordenarListaItem(programaService.buscarTodosOsBensDoPrograma(programa));
    }

    private List<Entidade> ordenarListaEstado(List<Entidade> listEstado) {
        List<String> listaNome = new ArrayList<String>();
        List<Entidade> novaLista = new ArrayList<Entidade>();

        for (Entidade item : listEstado) {
            listaNome.add(item.getMunicipio().getUf().getNomeUf());
        }

        Collections.sort(listaNome);

        for (String item1 : listaNome) {
            for (Entidade objetoBem : listEstado) {
                if (item1.equals(objetoBem.getMunicipio().getUf().getNomeUf())) {
                    novaLista.add(objetoBem);
                }
            }
        }
        return novaLista;
    }

    private List<Bem> ordenarListaItem(List<Bem> listItem) {
        List<String> listaNome = new ArrayList<String>();
        List<Bem> novaLista = new ArrayList<Bem>();

        for (Bem item : listItem) {
            listaNome.add(item.getNomeBem());
        }

        Collections.sort(listaNome);

        for (String item1 : listaNome) {
            for (Bem objetoBem : listItem) {
                if (item1.equals(objetoBem.getNomeBem())) {
                    novaLista.add(objetoBem);
                    break;
                }
            }
        }
        return novaLista;
    }

    private void pesquisar(AjaxRequestTarget target) {
        TermoRecebimentoDefinitivoDto pesquisaDto = new TermoRecebimentoDefinitivoDto();
        pesquisaDto.setPrograma(programa);
        pesquisaDto.setNomeBeneciario(beneficiarioSelecionado == null ? null : beneficiarioSelecionado.getNomeEntidade());
        pesquisaDto.setNumeroCnpj(cnpjSelecionado == null ? null : cnpjSelecionado.getNumeroCnpj());
        pesquisaDto.setEstado(estadoSelecionado == null ? null : estadoSelecionado.getMunicipio());
        pesquisaDto.setItem(itemSelecionado == null ? null : itemSelecionado);
        pesquisaDto.setStatusGeracaoTermoDoacao(EnumStatusGeracaoTermoDoacao.TERMO_DOACAO_NAO_GERADO);

        listaTermoRecebimentoDefinitivo = termoRecebimentoDefinitivoService.buscarSemPaginacao(pesquisaDto);

        if (listaTermoRecebimentoDefinitivo.size() == 0) {
            addMsgInfo("Pesquisa não encontrou resultado.");
            listaTermoRecebimentoDefinitivo = new ArrayList<TermoRecebimentoDefinitivo>();
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        } else {
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraPainelTRD');");
        }

        panelTermoRecebimentoDefinitivo.addOrReplace(newGerarTermoDoacao());
        panelTermoRecebimentoDefinitivo.addOrReplace(newDataViewTermoRecebimentoDefinitivo());
        panelTermoRecebimentoDefinitivo.addOrReplace(new InfraAjaxPagingNavigator("paginationTRD", dataViewObjetoTRD));
        target.appendJavaScript("atualizaCssDropDown();");
        target.add(panelTermoRecebimentoDefinitivo, panelPesquisa);
    }

    private void limpar(AjaxRequestTarget target) {
        setBeneficiarioSelecionado(null);
        setCnpjSelecionado(null);
        setEstadoSelecionado(null);
        setItemSelecionado(null);
        target.add(panelPesquisa);
        target.appendJavaScript("atualizaCssDropDown();");
    }

    private void actionCheckTRDTodos(AjaxRequestTarget target) {
        if (getTrdSelecionarTodos()) {
            listaTRDSelecionados.clear();
            for (TermoRecebimentoDefinitivo obj : listaTermoRecebimentoDefinitivo) {
                if (verificaTermoComNotaFiscal(obj)) {
                    obj.setTrdSelecionado(Boolean.TRUE);
                    listaTRDSelecionados.add(obj);
                }
            }

        } else {
            for (TermoRecebimentoDefinitivo obj : listaTermoRecebimentoDefinitivo) {
                obj.setTrdSelecionado(Boolean.FALSE);
                obj.setNumeroDocumentoSei(new String());
                listaTRDSelecionados.remove(obj);
            }
        }
        panelTermoRecebimentoDefinitivo.addOrReplace(newDataViewTermoRecebimentoDefinitivo());
        panelTermoRecebimentoDefinitivo.addOrReplace(new InfraAjaxPagingNavigator("paginationTRD", dataViewObjetoTRD));
        target.add(panelTermoRecebimentoDefinitivo);
    }

    private void actionCheckTRD(AjaxRequestTarget target, Item<TermoRecebimentoDefinitivo> item) {
        if (item.getModelObject().getTrdSelecionado()) {
            item.getModelObject().setTrdSelecionado(Boolean.TRUE);
            listaTRDSelecionados.add(item.getModelObject());
        } else {
            setTrdSelecionarTodos(null);
            item.getModelObject().setTrdSelecionado(Boolean.FALSE);
            item.getModelObject().setNumeroDocumentoSei(new String());
            listaTRDSelecionados.remove(item.getModelObject());
        }

        panelTermoRecebimentoDefinitivo.addOrReplace(newDataViewTermoRecebimentoDefinitivo());
        panelTermoRecebimentoDefinitivo.addOrReplace(new InfraAjaxPagingNavigator("paginationTRD", dataViewObjetoTRD));
        target.add(panelTermoRecebimentoDefinitivo);
    }

    private void downloadTRD(TermoRecebimentoDefinitivo termoRecebimentoDefinitivo) {
        TermoRecebimentoDefinitivo termo = new TermoRecebimentoDefinitivo();
        termo = termoRecebimentoDefinitivo;
        if (termo.getId() != null) {
            AnexoDto retorno = anexoTermoRecebimentoDefinitivoService.buscarPeloId(termo.getId());
            SideUtil.download(retorno.getConteudo(), "Termo de Recebimento Definitivo_" + retorno.getNomeAnexo() + ".pdf");
        }
    }

    private void downloadNF(TermoRecebimentoDefinitivo termoRecebimentoDefinitivo) {
        if (termoRecebimentoDefinitivo.getId() != null) {
            AnexoDto retorno = anexoTermoRecebimentoDefinitivoService.buscarNotaFiscalPeloIdTermoRecebimento(termoRecebimentoDefinitivo.getId());
            SideUtil.download(retorno.getConteudo(), termoRecebimentoDefinitivo.getNomeAnexoNotaFiscal());
        }
    }

    private void actionGerarTermoDoacao(AjaxRequestTarget target) {
        if (listaTRDSelecionados.size() > 0) {

            String msg = validaNumeroSei();

            if (msg.equalsIgnoreCase("")) {

                // limpa a mascara do processo SEI
                for (TermoRecebimentoDefinitivo termoSemCaracteres : listaTRDSelecionados) {
                    termoSemCaracteres.setNumeroProcessoSEI(MascaraUtils.limparFormatacaoMascara(termoSemCaracteres.getNumeroProcessoSEI()));
                }

                List<TermoDoacao> listaObjFornecimento = termoDoacaoService.montarTermoDeDoacaoComListaObjetoFornecimento(listaTRDSelecionados);
                List<TermoDoacaoDto> listaParaPersistirTermoDto = new ArrayList<>();

                for (TermoDoacao termoDoacao : listaObjFornecimento) {

                    List<ObjetoFornecimentoContrato> ListaTemp = new ArrayList<ObjetoFornecimentoContrato>();
                    for (ObjetoFornecimentoContrato obj : termoDoacao.getObjetosFornecimentoContrato()) {
                        ListaTemp.add(obj);
                    }
                    TermoDoacaoOfBuilder jasper = new TermoDoacaoOfBuilder(SideUtil.getCaminhoCompletoRelatorios(getHttpServletRequest().getServletContext()));
                    jasper.setNomeTermo("Termo de Doação");
                    jasper.setTipoTermo(EnumTipoMinuta.PDF);
                    jasper.setDataList(popularLista(ListaTemp, termoDoacao));
                    ByteArrayOutputStream exportar = jasper.exportToByteArray();
                    termoDoacao.setTermoDoacao(exportar.toByteArray());

                    TermoDoacaoDto termoDto = new TermoDoacaoDto();
                    termoDto.setTermoDoacao(termoDoacao);
                    termoDto.setUsuarioLogado(getIdentificador());
                    termoDto.setPrograma(programa);
                    // termoDoacaoService.incluir(termoDto);
                    listaParaPersistirTermoDto.add(termoDto);
                }
                termoDoacaoService.gerarTermoDoacao(listaParaPersistirTermoDto, listaTRDSelecionados);
                trdSelecionarTodos = Boolean.FALSE;
                listaTRDSelecionados.clear();
                initVariaveis();

                panelTermoRecebimentoDefinitivo.addOrReplace(newDataViewTermoRecebimentoDefinitivo());
                panelTermoRecebimentoDefinitivo.addOrReplace(new InfraAjaxPagingNavigator("paginationTRD", dataViewObjetoTRD));
                panelDoacoes.addOrReplace(newDataViewDoacoes());
                panelDoacoes.addOrReplace(new InfraAjaxPagingNavigator("paginationDoacoes", dataViewObjetoDoacoes));
                target.add(panelTermoRecebimentoDefinitivo);
                target.add(panelDoacoes);
                target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
                addMsgInfo("Termo de Doação definitivo gerado com sucesso!");
            } else {
                addMsgError(msg);
                target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
            }
        } else {
            addMsgError("Selecione o Item Analisado antes de gerar o Termo de Doação definitivo!");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        }
    }

    private String validaNumeroSei() {

        String msg = new String();

        for (TermoRecebimentoDefinitivo termo : listaTRDSelecionados) {
            for (int i = 0; i < listaTRDSelecionados.size(); i++) {
                if (termo.getNumeroProcessoSEI() == null || termo.getNumeroProcessoSEI().trim().equalsIgnoreCase("")) {
                    msg = "'Processo SEI' do TRD Nº ".concat(termo.getNomeAnexo()).concat(" não foi informado.");
                    break;
                }

                if (termo.getNumeroDocumentoSei() == null || termo.getNumeroDocumentoSei().trim().equalsIgnoreCase("")) {
                    msg = "'Nº Documento SEI' do TRD Nº ".concat(termo.getNomeAnexo()).concat(" não foi informado.");
                    break;
                }

                if (!termo.getEntidade().equals(listaTRDSelecionados.get(i).getEntidade())) {
                    if (termo.getNumeroProcessoSEI().equalsIgnoreCase(listaTRDSelecionados.get(i).getNumeroProcessoSEI())) {
                        msg = "'Processo SEI' do TRD Nº ".concat(termo.getNomeAnexo()).concat(" esta repetido no TRD Nº ").concat(listaTRDSelecionados.get(i).getNomeAnexo()).concat(" para outro Beneficário.");
                        break;
                    }
                }

                if (msg.equalsIgnoreCase("")) {
                    for (int j = 0; j < listaTRDSelecionados.size(); j++) {
                        if (!listaTRDSelecionados.get(i).equals(listaTRDSelecionados.get(j))) {
                            if (listaTRDSelecionados.get(j).getNumeroDocumentoSei().equalsIgnoreCase(listaTRDSelecionados.get(i).getNumeroDocumentoSei())) {
                                msg = "'Nº Documento SEI' do TRD Nº ".concat(listaTRDSelecionados.get(j).getNomeAnexo()).concat(" esta repetido no TRD Nº ").concat(listaTRDSelecionados.get(i).getNomeAnexo()).concat(".");
                                break;
                            }
                        }
                    }
                }

            }

            if (msg.trim().equalsIgnoreCase("")) {

                if (termo.getNumeroProcessoSEI().trim().length() < 20) {
                    msg = "'Processo SEI' do TRD Nº ".concat(termo.getNomeAnexo()).concat(" não foi informado corretamente.");
                    break;
                }

                if (termo.getNumeroDocumentoSei().trim().length() < 8) {
                    msg = "'Nº Documento SEI' do TRD Nº ".concat(termo.getNomeAnexo()).concat(" não foi informado corretamente.");
                    break;
                }
            }

        }
        return msg;
    }

    private void downloadTD(Item<TermoDoacao> item) {
        TermoDoacao termo = new TermoDoacao();
        termo = item.getModelObject();
        if (termo.getId() != null) {
            AnexoDto retorno = anexoTermoDoacaoService.buscarPeloId(termo.getId());
            SideUtil.download(retorno.getConteudo(), retorno.getNomeAnexo() + ".pdf");
        }
    }

    private List<TermoDoacaoDto> popularLista(List<ObjetoFornecimentoContrato> listaTemp, TermoDoacao termoDoacao) {
        listatermoDoacaoDto = new ArrayList<TermoDoacaoDto>();
        TermoDoacaoDto termoDoacaoDto = new TermoDoacaoDto();
        Entidade entidade = new Entidade();
        TermoDefinitivoItensDto termoDefinitivoItensDto = new TermoDefinitivoItensDto();
        termoDoacaoDto.setNomeBeneficiario(termoDoacao.getNomeBeneficiario());

        for (ObjetoFornecimentoContrato obj : ordernarLista(listaTemp)) {
            obj.getLocalEntrega().getEntidade();
            entidade = obj.getLocalEntrega().getEntidade();

            termoDefinitivoItensDto = new TermoDefinitivoItensDto();
            termoDefinitivoItensDto.setIdItem(String.valueOf(obj.getId()));
            termoDefinitivoItensDto.setNomeBem(obj.getItem().getNomeBem());
            termoDoacaoDto.getListaItens().add(termoDefinitivoItensDto);
        }

        termoDoacaoDto.setCodigoPrograma(programa.getCodigoIdentificadorProgramaPublicadoENomePrograma());
        termoDoacaoDto.setUnidadeExecutoraPrograma(programa.getUnidadeExecutora().getNomeUnidadeExecutora());

        listaInscricaoPrograma = inscricaoProgramaService.buscarInscricaoProgramaPeloProgramaEEntidade(programa, entidade);

        for (InscricaoPrograma inscricaoPrograma : listaInscricaoPrograma) {
            termoDoacaoDto.setNomeRepresentante(inscricaoPrograma.getPessoaEntidade().getPessoa().getNomePessoa());
            termoDoacaoDto.setTelefoneRepresentante(MascaraUtils.formatarMascaraTelefone(inscricaoPrograma.getPessoaEntidade().getPessoa().getNumeroTelefone()));
            termoDoacaoDto.setEmailRepresentante(inscricaoPrograma.getPessoaEntidade().getPessoa().getEmail());
        }

        for (PessoaEntidade pessoaEntidade : beneficiarioService.buscarTitularEntidade(entidade)) {
            termoDefinitivoItensDto = new TermoDefinitivoItensDto();
            if (pessoaEntidade.getPessoa().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
                termoDefinitivoItensDto.setNomeMembros(pessoaEntidade.getPessoa().getNomePessoa().toUpperCase());
                termoDoacaoDto.getListaMembros().add(termoDefinitivoItensDto);
            }
        }
        listatermoDoacaoDto.add(termoDoacaoDto);

        return listatermoDoacaoDto;
    }

    private List<ObjetoFornecimentoContrato> ordernarLista(List<ObjetoFornecimentoContrato> lista) {
        List<ObjetoFornecimentoContrato> novaLista = new ArrayList<ObjetoFornecimentoContrato>();
        List<String> listaId = new ArrayList<String>();

        for (ObjetoFornecimentoContrato idObjeto : lista) {
            listaId.add(String.valueOf(idObjeto.getId()));
        }
        Collections.sort(listaId);
        for (String id : listaId) {
            for (ObjetoFornecimentoContrato obj : lista) {
                if (id.equals(String.valueOf(obj.getId()))) {
                    novaLista.add(obj);
                }
            }
        }

        return novaLista;
    }

    // ####################################_GETTERS_E_SETTERS_###############################################
    public Entidade getBeneficiarioSelecionado() {
        return beneficiarioSelecionado;
    }

    public void setBeneficiarioSelecionado(Entidade beneficiarioSelecionado) {
        this.beneficiarioSelecionado = beneficiarioSelecionado;
    }

    public Entidade getCnpjSelecionado() {
        return cnpjSelecionado;
    }

    public void setCnpjSelecionado(Entidade cnpjSelecionado) {
        this.cnpjSelecionado = cnpjSelecionado;
    }

    public Entidade getEstadoSelecionado() {
        return estadoSelecionado;
    }

    public void setEstadoSelecionado(Entidade estadoSelecionado) {
        this.estadoSelecionado = estadoSelecionado;
    }

    public Bem getItemSelecionado() {
        return itemSelecionado;
    }

    public void setItemSelecionado(Bem itemSelecionado) {
        this.itemSelecionado = itemSelecionado;
    }

    public Boolean getTrdSelecionarTodos() {
        return trdSelecionarTodos;
    }

    public void setTrdSelecionarTodos(Boolean trdSelecionarTodos) {
        this.trdSelecionarTodos = trdSelecionarTodos;
    }

    public Integer getItensPorPaginaTermoRecebimentoDefinitivo() {
        return itensPorPaginaTermoRecebimentoDefinitivo;
    }

    public void setItensPorPaginaTermoRecebimentoDefinitivo(Integer itensPorPaginaTermoRecebimentoDefinitivo) {
        this.itensPorPaginaTermoRecebimentoDefinitivo = itensPorPaginaTermoRecebimentoDefinitivo;
    }

    public Integer getItensPorPaginaDoacoes() {
        return itensPorPaginaDoacoes;
    }

    public void setItensPorPaginaDoacoes(Integer itensPorPaginaDoacoes) {
        this.itensPorPaginaDoacoes = itensPorPaginaDoacoes;
    }

}
