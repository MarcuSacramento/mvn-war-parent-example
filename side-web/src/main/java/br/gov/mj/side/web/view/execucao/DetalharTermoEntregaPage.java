package br.gov.mj.side.web.view.execucao;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.enums.EnumAnaliseFinalItem;
import br.gov.mj.side.entidades.enums.EnumSituacaoAvaliacaoPreliminarPreenchimentoItem;
import br.gov.mj.side.entidades.enums.EnumSituacaoGeracaoTermos;
import br.gov.mj.side.entidades.enums.EnumSituacaoPreenchimentoItemFormatacaoBeneficiario;
import br.gov.mj.side.entidades.enums.EnumSituacaoPreenchimentoItemFormatacaoFornecedor;
import br.gov.mj.side.entidades.enums.EnumStatusExecucaoNotaRemessaContratante;
import br.gov.mj.side.entidades.enums.EnumStatusOrdemFornecimento;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumTipoMinuta;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.inscricao.ComissaoRecebimento;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.AnexoNotaRemessa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.ItensNotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.TermoRecebimentoDefinitivo;
import br.gov.mj.side.web.dto.AnexoDto;
import br.gov.mj.side.web.dto.ObjetoFornecimentoContratoDto;
import br.gov.mj.side.web.dto.TermoDefinitivoItensDto;
import br.gov.mj.side.web.dto.TermoRecebimentoDefinitivoDto;
import br.gov.mj.side.web.service.AnexoNotaRemessaService;
import br.gov.mj.side.web.service.AnexoTermoRecebimentoDefinitivoService;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.MailService;
import br.gov.mj.side.web.service.NotaRemessaService;
import br.gov.mj.side.web.service.ObjetoFornecimentoContratoService;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.service.TermoRecebimentoDefinitivoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.DataUtil;
import br.gov.mj.side.web.util.MascaraUtils;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.execucao.painel.PanelCompararItensAnalisar;
import br.gov.mj.side.web.view.programa.PanelFasePrograma;
import br.gov.mj.side.web.view.template.TemplatePage;

public class DetalharTermoEntregaPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    // ##################### Constantes #######################
    private static final String ONCHANGE = "onchange";

    // ##################### Paineis #######################

    private Form<DetalharTermoEntregaPage> form;
    private PanelFasePrograma panelFasePrograma;
    private ExecucaoPanelBotoes execucaoPanelBotoes;
    private PanelItensParaAnalisar panelItensParaAnalisar;
    private PanelCompararItensAnalisar panelCompararItensParaAnalisar;
    private PanelCompararItensAnalisar panelCompararItensAnalisado;
    private PanelDetalhamentoCompararItens panelDetalhamentoCompararItens;
    private PanelDetalhamentoCompararItensAnalisado panelDetalhamentoCompararItensAnalisado;
    private PanelItensAnalisados panelItensAnalisados;
    private PanelDataViewAnexos panelDataViewAnexos;
    private PanelDataViewTermo panelDataViewTermo;
    private PanelBotoes panelBotoes;

    // ##################### Variáveis #######################
    private Programa programa;
    private Page backPage;
    private Integer abaClicada;
    private Integer itensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;
    private Integer itensPorPaginaAnalisad = Constants.ITEMS_PER_PAGE_PAGINATION;
    private Integer itensPorPaginaAnexo = Constants.ITEMS_PER_PAGE_PAGINATION;
    private Integer itensPorPaginaTermo = Constants.ITEMS_PER_PAGE_PAGINATION;
    private NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato;
    private ObjetoFornecimentoContrato objetoFornecimentoContrato = new ObjetoFornecimentoContrato();
    private List<ObjetoFornecimentoContrato> listaItensParaAnalisar = new ArrayList<ObjetoFornecimentoContrato>();
    private List<ObjetoFornecimentoContrato> listaItensAnalisarSelecionados = new ArrayList<ObjetoFornecimentoContrato>();
    private List<ObjetoFornecimentoContrato> listaItensAnalisadosSelecionados = new ArrayList<ObjetoFornecimentoContrato>();
    private List<ObjetoFornecimentoContrato> listaItensAnalisados = new ArrayList<ObjetoFornecimentoContrato>();
    private List<AnexoNotaRemessa> listaAnexosNotaRemessa = new ArrayList<AnexoNotaRemessa>();
    private List<TermoRecebimentoDefinitivo> listaTermoRecebimentoDefinitivo = new ArrayList<TermoRecebimentoDefinitivo>();
    private List<InscricaoPrograma> listaInscricaoPrograma = new ArrayList<InscricaoPrograma>();
    private String descricaoMotivo = "";

    private Boolean itemSelecionarTodos;
    private Boolean itemSelecionadosTodos;

    private Boolean habilitarAceitoRessalvaMotivo = Boolean.TRUE;
    private Boolean habilitarNaoAceitoMotivo = Boolean.TRUE;
    private Boolean habilitarAceitoRessalva = Boolean.FALSE;
    private Boolean habilitarNaoAceito = Boolean.FALSE;
    private Boolean habilitarCancelar = Boolean.FALSE;
    private Boolean habilitarMotivo = Boolean.FALSE;
    private Boolean habilitarAceito = Boolean.TRUE;

    private Boolean habilitarAnalisar = Boolean.FALSE;
    private Boolean habilitarAnalisado = Boolean.FALSE;

    // ###################### Componentes Wicket #######################
    private DataView<ObjetoFornecimentoContrato> dataViewObjetoFornecimentoContrato;
    private DataView<ObjetoFornecimentoContrato> dataViewObjetoFornecimentoContratoAnalisados;
    private DataView<AnexoNotaRemessa> newListaAnexoNotaRemessa;
    private DataView<TermoRecebimentoDefinitivo> newListaTermoRecebimentoDefinitivo;

    // ##################### Injeções de dependências #########

    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;
    @Inject
    private NotaRemessaService notaRemessaService;
    @Inject
    private AnexoNotaRemessaService anexoNotaRemessaService;
    @Inject
    private TermoRecebimentoDefinitivoService termoRecebimentoDefinitivoService;
    @Inject
    private AnexoTermoRecebimentoDefinitivoService anexoTermoRecebimentoDefinitivoService;
    @Inject
    private InscricaoProgramaService inscricaoProgramaService;
    @Inject
    private MailService mailService;
    @Inject
    private ObjetoFornecimentoContratoService objetoFornecimentoContratoService;

    // Esta sendo usado este
    public DetalharTermoEntregaPage(final PageParameters pageParameters, Programa programa, NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato, Page backPage, Integer abaClicada) {
        super(pageParameters);
        setTitulo("Gerenciar Programa");
        this.backPage = backPage;
        this.programa = programa;
        this.abaClicada = abaClicada;
        this.notaRemessaOrdemFornecimentoContrato = notaRemessaOrdemFornecimentoContrato;

        initVariaveis();
        initComponents();
    }

    private void initVariaveis() {
        polulaListas();
    }

    private void initComponents() {

        form = new Form<DetalharTermoEntregaPage>("form", new CompoundPropertyModel<DetalharTermoEntregaPage>(this));

        panelFasePrograma = new PanelFasePrograma("panelFasePrograma", programa, backPage, abaClicada);
        execucaoPanelBotoes = new ExecucaoPanelBotoes("execucaoPanelPotoes", programa, backPage, "AnaliseEntrega");

        panelBotoes = new PanelBotoes("panelBotoes");
        panelItensParaAnalisar = new PanelItensParaAnalisar("panelItensParaAnalisar");
        panelItensAnalisados = new PanelItensAnalisados("panelItensAnalisados");
        panelDataViewAnexos = new PanelDataViewAnexos("panelDataViewAnexos");
        panelDataViewTermo = new PanelDataViewTermo("panelDataViewTermo");

        form.add(panelFasePrograma);
        form.add(execucaoPanelBotoes);
        form.add(panelBotoes);
        form.add(panelItensParaAnalisar);
        form.add(panelItensAnalisados);
        form.add(panelDataViewAnexos);
        form.add(panelDataViewTermo);

        add(form);
    }

    private void polulaListas() {
        this.listaItensParaAnalisar.clear();
        this.listaItensAnalisados.clear();
        this.listaAnexosNotaRemessa.clear();
        this.listaTermoRecebimentoDefinitivo.clear();

        ObjetoFornecimentoContratoDto dto = new ObjetoFornecimentoContratoDto();
        ObjetoFornecimentoContrato ofc = new ObjetoFornecimentoContrato();
        ofc.setNotaRemessaOrdemFornecimentoContrato(notaRemessaOrdemFornecimentoContrato);
        dto.setObjetoFornecimentoContrato(ofc);

        List<ObjetoFornecimentoContrato> listaObjetoFornecimentoContrato = new ArrayList<ObjetoFornecimentoContrato>();
        listaObjetoFornecimentoContrato.addAll(objetoFornecimentoContratoService.verificarObjetosPassiveisDeDevolucao(objetoFornecimentoContratoService.buscarSemPaginacao(dto)));

        for (ObjetoFornecimentoContrato obj : listaObjetoFornecimentoContrato) {
            if (obj.getAnaliseFinalItem() == null || (obj.getAnaliseFinalItem() != null && obj.getAnaliseFinalItem().equals(EnumAnaliseFinalItem.NAO_ANALISADO))) {
                listaItensParaAnalisar.add(obj);
            } else {
                listaItensAnalisados.add(obj);
            }

        }

        if (listaItensParaAnalisar.size() > 0) {
            listaItensParaAnalisar = ordernarLista(listaItensParaAnalisar);
            habilitarAnalisar = Boolean.TRUE;
        }
        if (listaItensAnalisados.size() > 0) {
            listaItensAnalisados = ordernarLista(listaItensAnalisados);
            habilitarAnalisado = Boolean.TRUE;
        }

        this.listaTermoRecebimentoDefinitivo.addAll(termoRecebimentoDefinitivoService.buscarTodosOsTermosRecebimentoDefinitivoPorNotaRemessa(this.notaRemessaOrdemFornecimentoContrato.getId()));

        List<AnexoDto> lista = anexoNotaRemessaService.buscarPeloIdNotaRemessa(this.notaRemessaOrdemFornecimentoContrato.getId());
        this.listaAnexosNotaRemessa = SideUtil.convertAnexoDtoToEntityAnexoNotaRemessa(lista);
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

    // paineis
    private class PanelItensParaAnalisar extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelItensParaAnalisar(String id) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);
            setVisible(listaItensParaAnalisar.size() > 0);

            add(newCheckBoxSelectedTodos());
            add(newDataViewItens());
            add(new InfraAjaxPagingNavigator("pagination", dataViewObjetoFornecimentoContrato));
            add(newDropItensPorPagina());

            panelDetalhamentoCompararItens = new PanelDetalhamentoCompararItens("panelDetalhamentoCompararItens");
            panelDetalhamentoCompararItens.setVisible(Boolean.FALSE);
            add(panelDetalhamentoCompararItens);

            add(newTextAreaDescricao());
            add(newButtonAceitarRessalvaMotivo());
            add(newButtonNaoAceitarMotivo());

            add(newButtonAceitar());
            add(newButtonNaoAceitar());
            add(newButtonAceitarRessalva());
            add(newButtonCancelar());
        }
    }

    private class PanelDetalhamentoCompararItens extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDetalhamentoCompararItens(String id) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);

            add(newButtonFecharDetalhamentoItem());
            add(panelCompararItensParaAnalisar = new PanelCompararItensAnalisar("panelCompararItens", objetoFornecimentoContrato));
        }
    }

    private class PanelDetalhamentoCompararItensAnalisado extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDetalhamentoCompararItensAnalisado(String id) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);

            add(newButtonFecharDetalhamentoItemAnalisado());
            add(panelCompararItensAnalisado = new PanelCompararItensAnalisar("panelCompararItensAnalisado", objetoFornecimentoContrato));
        }
    }

    private class PanelItensAnalisados extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelItensAnalisados(String id) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);
            setVisible(listaItensAnalisados.size() > 0);

            panelDetalhamentoCompararItensAnalisado = new PanelDetalhamentoCompararItensAnalisado("panelDetalhamentoCompararItensAnalisado");
            panelDetalhamentoCompararItensAnalisado.setVisible(Boolean.FALSE);
            add(panelDetalhamentoCompararItensAnalisado);

            add(newCheckBoxSelectedAnalisadoTodos());
            add(newDataViewItensAnalisados());
            add(new InfraAjaxPagingNavigator("paginationAnalisados", dataViewObjetoFornecimentoContratoAnalisados));
            add(newDropItensPorPaginaAnalisado());
            add(newBtnDevolverCorrecao());
            add(newBtnDevolverAnalise());
            add(newButtonGerarTermoRecebimentoDefinitivo());

        }
    }

    private class PanelDataViewAnexos extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDataViewAnexos(String id) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);
            setVisible(listaAnexosNotaRemessa.size() > 0);

            add(newDataViewAnexo());
            add(new InfraAjaxPagingNavigator("paginationAnexos", newListaAnexoNotaRemessa));
            add(newDropItensPorPaginaAnexo());
        }
    }

    private class PanelDataViewTermo extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDataViewTermo(String id) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);
            setVisible(listaTermoRecebimentoDefinitivo.size() > 0);

            add(newDataViewTermoRecebimentoDefinitivoDto());
            add(new InfraAjaxPagingNavigator("paginationTermo", newListaTermoRecebimentoDefinitivo));
            add(newDropItensPorPaginaTermo());
        }
    }

    private class PanelBotoes extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBotoes(String id) {
            super(id);
            setOutputMarkupId(Boolean.TRUE);

            add(newButtonVoltar());
        }
    }

    // componentes
    private InfraAjaxFallbackLink<Void> newButtonDetalharItem(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnDetalharItem", (target) -> actionDetalharItem(objetoFornecimentoContrato, target));
        btn.setOutputMarkupId(Boolean.TRUE);
        return btn;
    }

    private InfraAjaxFallbackLink<Void> newButtonDetalharItemAnalisado(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnDetalharItemAnalisado", (target) -> actionDetalharItemAnalisado(objetoFornecimentoContrato, target));
        btn.setOutputMarkupId(Boolean.TRUE);
        return btn;
    }

    private DropDownChoice<Integer> newDropItensPorPagina() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPagina", new LambdaModel<Integer>(this::getItensPorPagina, this::setItensPorPagina), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewObjetoFornecimentoContrato.setItemsPerPage(getItensPorPagina());
                target.add(panelItensParaAnalisar);
            };
        });
        dropDownChoice.setOutputMarkupId(Boolean.TRUE);
        return dropDownChoice;
    }

    private DropDownChoice<Integer> newDropItensPorPaginaTermo() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPaginaTermo", new LambdaModel<Integer>(this::getItensPorPaginaTermo, this::setItensPorPaginaTermo), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                newListaTermoRecebimentoDefinitivo.setItemsPerPage(getItensPorPaginaTermo());
                target.add(panelDataViewTermo);
            };
        });
        dropDownChoice.setOutputMarkupId(Boolean.TRUE);
        return dropDownChoice;
    }

    private DropDownChoice<Integer> newDropItensPorPaginaAnalisado() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPaginaAnalisado", new LambdaModel<Integer>(this::getItensPorPaginaAnalisad, this::setItensPorPaginaAnalisad), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataViewObjetoFornecimentoContratoAnalisados.setItemsPerPage(getItensPorPaginaAnalisad());
                target.add(panelItensAnalisados);
            };
        });
        dropDownChoice.setOutputMarkupId(Boolean.TRUE);
        return dropDownChoice;
    }

    private DropDownChoice<Integer> newDropItensPorPaginaAnexo() {
        DropDownChoice<Integer> dropDownChoice = new DropDownChoice<Integer>("itensPorPaginaAnexo", new LambdaModel<Integer>(this::getItensPorPaginaAnexo, this::setItensPorPaginaAnexo), Constants.QUANTIDADE_ITENS_TABELA);
        dropDownChoice.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                newListaAnexoNotaRemessa.setItemsPerPage(getItensPorPaginaAnexo());
                target.add(panelDataViewAnexos);
            };
        });
        dropDownChoice.setOutputMarkupId(Boolean.TRUE);
        return dropDownChoice;
    }

    private DataView<ObjetoFornecimentoContrato> newDataViewItens() {
        dataViewObjetoFornecimentoContrato = new DataView<ObjetoFornecimentoContrato>("listaDetalhamentoItens", new SortableDataProviderItensParaAnalisar()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ObjetoFornecimentoContrato> item) {
                AttributeAppender classeAtivarPopover = new AttributeAppender("class", "pop", " ");

                item.add(newCheckBoxSelected(item.getModelObject()));

                Long numeroIdentificador;
                if (item.getModelObject().getObjetoFornecimentoContratoPai() == null) {
                    numeroIdentificador = item.getModelObject().getId();
                } else {
                    numeroIdentificador = item.getModelObject().getObjetoFornecimentoContratoPai();
                }

                item.add(new Label("numeroIdentificador", numeroIdentificador));
                item.add(new Label("nomeDoBem", item.getModelObject().getItem().getNomeBem()));
                item.add(new Label("formaVerificacao", item.getModelObject().getFormaVerificacao().getDescricao()));

                String cor = buscarCor(item.getModelObject(), 1);

                String iconeSituacaoFornecedor = "<a  title=\"" + (item.getModelObject().getSituacaoPreenchimentoFornecedor() == null ? "" : item.getModelObject().getSituacaoPreenchimentoFornecedor().getDescricao())
                        + "\"><i  style=\"border-radius: 50%;display: inline-block;height: 20px;width: 20px;border: 1px solid #000000;background-color: " + cor + "\"> </i></a>";
                Label situacaoFornecedor = new Label("situacaoFornecedor", iconeSituacaoFornecedor);
                situacaoFornecedor.setEscapeModelStrings(false);
                situacaoFornecedor.setOutputMarkupId(true);
                item.add(situacaoFornecedor);

                cor = buscarCor(item.getModelObject(), 2);
                String iconeSituacaoBeneficiario = "<a  title=\"" + (item.getModelObject().getSituacaoPreenchimentoBeneficiario() == null ? "" : item.getModelObject().getSituacaoPreenchimentoBeneficiario().getDescricao())
                        + "\"><i  style=\"border-radius: 50%;display: inline-block;height: 20px;width: 20px;border: 1px solid #000000;background-color: " + cor + "\"> </i></a>";
                Label situacaoBeneficiario = new Label("situacaoBeneficiario", iconeSituacaoBeneficiario);
                situacaoBeneficiario.setEscapeModelStrings(false);
                situacaoBeneficiario.setOutputMarkupId(true);
                item.add(situacaoBeneficiario);
                item.add(new Label("avaliacaoPreliminar", item.getModelObject().getSituacaoAvaliacaoPreliminarPreenchimentoItem() == null ? "" : item.getModelObject().getSituacaoAvaliacaoPreliminarPreenchimentoItem().getDescricao()));

                StringBuilder sb = new StringBuilder(1);
                sb.append("<table><tr><td>" + item.getModelObject().getMotivoNaoConformidade() == null ? "<table><tr><td> " : item.getModelObject().getMotivoNaoConformidade() + "</td></tr></table>");
                String iconeInfMotivo = "<a tabindex=\"0\" role=\"button\" data-toggle=\"popover\" data-trigger=\"focus\" data-content=\"" + sb.toString() + "\"><i style=\"color:green;\" class=\"fa fa-info-circle\"> </i></a>";
                Label lblDescricaoInfMotivo = new Label("infMotivo", iconeInfMotivo);
                lblDescricaoInfMotivo.setEscapeModelStrings(false);
                lblDescricaoInfMotivo.setOutputMarkupId(true);
                lblDescricaoInfMotivo.add(classeAtivarPopover);

                if (item.getModelObject().getSituacaoAvaliacaoPreliminarPreenchimentoItem() != null && item.getModelObject().getSituacaoAvaliacaoPreliminarPreenchimentoItem().equals(EnumSituacaoAvaliacaoPreliminarPreenchimentoItem.NAO_CONFORMIDADE)) {
                    lblDescricaoInfMotivo.setVisible(Boolean.TRUE);
                } else {
                    lblDescricaoInfMotivo.setVisible(Boolean.FALSE);
                }
                item.add(lblDescricaoInfMotivo);
                item.add(newButtonDetalharItem(item.getModelObject()));

            }

        };
        dataViewObjetoFornecimentoContrato.setItemsPerPage(getItensPorPagina() == null ? Constants.ITEMS_PER_PAGE_PAGINATION : getItensPorPagina());
        return dataViewObjetoFornecimentoContrato;
    }

    private class SortableDataProviderItensParaAnalisar extends SortableDataProvider<ObjetoFornecimentoContrato, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<ObjetoFornecimentoContrato> iterator(long first, long count) {
            return listaItensParaAnalisar.iterator();
        }

        @Override
        public long size() {
            return listaItensParaAnalisar.size();
        }

        @Override
        public IModel<ObjetoFornecimentoContrato> model(ObjetoFornecimentoContrato object) {
            return new CompoundPropertyModel<ObjetoFornecimentoContrato>(object);
        }

    }

    private String buscarCor(ObjetoFornecimentoContrato objetoFornecimentoContrato, int tipo) {
        String cor = "white";
        if (objetoFornecimentoContrato.getSituacaoPreenchimentoFornecedor() != null && tipo == 1) {

            if (objetoFornecimentoContrato.getSituacaoPreenchimentoFornecedor().equals(EnumSituacaoPreenchimentoItemFormatacaoFornecedor.PREENCHIDO)) {
                cor = "green";
            } else if (objetoFornecimentoContrato.getSituacaoPreenchimentoFornecedor().equals(EnumSituacaoPreenchimentoItemFormatacaoFornecedor.NAO_PREENCHIDO)) {
                cor = "red";
            } else if (objetoFornecimentoContrato.getSituacaoPreenchimentoFornecedor().equals(EnumSituacaoPreenchimentoItemFormatacaoFornecedor.PREENCHIDO_INCOMPLETO)) {
                cor = "yellow";
            } else if (objetoFornecimentoContrato.getSituacaoPreenchimentoFornecedor().equals(EnumSituacaoPreenchimentoItemFormatacaoFornecedor.PREENCHIDO_COM_PENDENCIA)) {
                cor = "red";
            }
        }

        if (objetoFornecimentoContrato.getSituacaoPreenchimentoBeneficiario() != null && tipo == 2) {

            if (objetoFornecimentoContrato.getSituacaoPreenchimentoBeneficiario().equals(EnumSituacaoPreenchimentoItemFormatacaoBeneficiario.PREENCHIDO)) {
                cor = "green";
            } else if (objetoFornecimentoContrato.getSituacaoPreenchimentoBeneficiario().equals(EnumSituacaoPreenchimentoItemFormatacaoBeneficiario.NAO_PREENCHIDO)) {
                cor = "red";
            } else if (objetoFornecimentoContrato.getSituacaoPreenchimentoBeneficiario().equals(EnumSituacaoPreenchimentoItemFormatacaoBeneficiario.PREENCHIDO_INCOMPLETO)) {
                cor = "yellow";
            } else if (objetoFornecimentoContrato.getSituacaoPreenchimentoBeneficiario().equals(EnumSituacaoPreenchimentoItemFormatacaoBeneficiario.PREENCHIDO_COM_PENDENCIA)) {
                cor = "red";
            }
        }
        return cor;
    }

    private DataView<ObjetoFornecimentoContrato> newDataViewItensAnalisados() {
        dataViewObjetoFornecimentoContratoAnalisados = new DataView<ObjetoFornecimentoContrato>("listaDetalhamentoItensAnalisados", new SortableDataProviderItensAnalisados()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ObjetoFornecimentoContrato> item) {
                item.add(newCheckBoxSelectedAnalisado(item.getModelObject()));

                Long numeroIdentificador;
                if (item.getModelObject().getObjetoFornecimentoContratoPai() == null) {
                    numeroIdentificador = item.getModelObject().getId();
                } else {
                    numeroIdentificador = item.getModelObject().getObjetoFornecimentoContratoPai();
                }

                item.add(new Label("numeroIdentificador", numeroIdentificador));
                item.add(new Label("nomeDoBem", item.getModelObject().getItem().getNomeBem()));
                item.add(new Label("formaVerificacao", item.getModelObject().getFormaVerificacao().getDescricao()));
                item.add(new Label("analiseFinal", item.getModelObject().getAnaliseFinalItem() == null ? "" : item.getModelObject().getAnaliseFinalItem().getDescricao()));
                item.add(newButtonDetalharItemAnalisado(item.getModelObject()));

            }

        };
        dataViewObjetoFornecimentoContratoAnalisados.setItemsPerPage(getItensPorPaginaAnalisad() == null ? Constants.ITEMS_PER_PAGE_PAGINATION : getItensPorPaginaAnalisad());
        return dataViewObjetoFornecimentoContratoAnalisados;
    }

    private class SortableDataProviderItensAnalisados extends SortableDataProvider<ObjetoFornecimentoContrato, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<ObjetoFornecimentoContrato> iterator(long first, long count) {
            return listaItensAnalisados.iterator();
        }

        @Override
        public long size() {
            return listaItensAnalisados.size();
        }

        @Override
        public IModel<ObjetoFornecimentoContrato> model(ObjetoFornecimentoContrato object) {
            return new CompoundPropertyModel<ObjetoFornecimentoContrato>(object);
        }

    }

    private AjaxCheckBox newCheckBoxSelectedTodos() {
        AjaxCheckBox checkItensAnalisar = new AjaxCheckBox("checkItensAnalisarTodos", new PropertyModel<Boolean>(this, "itemSelecionarTodos")) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                actionCheckAnalisarTodos(target);
            }
        };
        checkItensAnalisar.setOutputMarkupId(Boolean.TRUE);
        return checkItensAnalisar;
    }

    private AjaxCheckBox newCheckBoxSelected(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        AjaxCheckBox checkItensAnalisar = new AjaxCheckBox("checkItensAnalisar", new PropertyModel<Boolean>(objetoFornecimentoContrato, "itemSelecionado")) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                actionCheckAnalisar(target, objetoFornecimentoContrato);
            }
        };
        checkItensAnalisar.setOutputMarkupId(Boolean.TRUE);
        return checkItensAnalisar;
    }

    private AjaxCheckBox newCheckBoxSelectedAnalisado(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        AjaxCheckBox checkItensAnalisar = new AjaxCheckBox("checkItensAnalisado", new PropertyModel<Boolean>(objetoFornecimentoContrato, "itemSelecionado")) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                actionCheckAnalisados(target, objetoFornecimentoContrato);
            }
        };
        if (objetoFornecimentoContrato.getObjetoDevolvido() != null) {
            checkItensAnalisar.setEnabled(!objetoFornecimentoContrato.getObjetoDevolvido());
        }
        checkItensAnalisar.setOutputMarkupId(Boolean.TRUE);
        return checkItensAnalisar;
    }

    private AjaxCheckBox newCheckBoxSelectedAnalisadoTodos() {
        AjaxCheckBox checkItensAnalisar = new AjaxCheckBox("checkItensAnalisadoTodos", new PropertyModel<Boolean>(this, "itemSelecionadosTodos")) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                actionCheckAnalisadosTodos(target);
            }
        };
        checkItensAnalisar.setOutputMarkupId(Boolean.TRUE);
        return checkItensAnalisar;
    }

    private void actionCheckAnalisar(AjaxRequestTarget target, ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        if (objetoFornecimentoContrato.getItemSelecionado()) {
            listaItensAnalisarSelecionados.add(objetoFornecimentoContrato);
        } else {
            this.itemSelecionarTodos = Boolean.FALSE;
            listaItensAnalisarSelecionados.remove(objetoFornecimentoContrato);
        }
        panelItensParaAnalisar.addOrReplace(newDataViewItens());
        panelItensParaAnalisar.addOrReplace(new InfraAjaxPagingNavigator("pagination", dataViewObjetoFornecimentoContrato));
        target.add(panelItensParaAnalisar);
    }

    private void actionCheckAnalisarTodos(AjaxRequestTarget target) {
        listaItensAnalisarSelecionados.clear();
        if (getItemSelecionarTodos()) {
            for (ObjetoFornecimentoContrato obj : listaItensParaAnalisar) {
                obj.setItemSelecionado(Boolean.TRUE);
                listaItensAnalisarSelecionados.add(obj);
            }

        } else {
            for (ObjetoFornecimentoContrato obj : listaItensParaAnalisar) {
                obj.setItemSelecionado(Boolean.FALSE);
                listaItensAnalisarSelecionados.remove(obj);
            }
        }
        panelItensParaAnalisar.addOrReplace(newDataViewItens());
        panelItensParaAnalisar.addOrReplace(new InfraAjaxPagingNavigator("pagination", dataViewObjetoFornecimentoContrato));
        target.add(panelItensParaAnalisar);
    }

    private void actionCheckAnalisados(AjaxRequestTarget target, ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        if (objetoFornecimentoContrato.getItemSelecionado()) {
            listaItensAnalisadosSelecionados.add(objetoFornecimentoContrato);
        } else {
            this.itemSelecionadosTodos = Boolean.FALSE;
            listaItensAnalisadosSelecionados.remove(objetoFornecimentoContrato);
        }
        panelItensAnalisados.addOrReplace(newDataViewItensAnalisados());
        panelItensAnalisados.addOrReplace(new InfraAjaxPagingNavigator("paginationAnalisados", dataViewObjetoFornecimentoContratoAnalisados));
        target.add(panelItensAnalisados);
    }

    private void actionCheckAnalisadosTodos(AjaxRequestTarget target) {
        listaItensAnalisadosSelecionados.clear();
        if (getItemSelecionadosTodos()) {
            for (ObjetoFornecimentoContrato obj : listaItensAnalisados) {
                if (obj.getObjetoDevolvido() == null || !obj.getObjetoDevolvido()) {
                    obj.setItemSelecionado(Boolean.TRUE);
                    listaItensAnalisadosSelecionados.add(obj);
                }
            }

        } else {
            for (ObjetoFornecimentoContrato obj : listaItensAnalisados) {
                obj.setItemSelecionado(Boolean.FALSE);
                listaItensAnalisadosSelecionados.remove(obj);
            }
        }
        panelItensAnalisados.addOrReplace(newDataViewItensAnalisados());
        panelItensAnalisados.addOrReplace(new InfraAjaxPagingNavigator("paginationAnalisados", dataViewObjetoFornecimentoContratoAnalisados));
        target.add(panelItensAnalisados);
    }

    private DataView<AnexoNotaRemessa> newDataViewAnexo() {
        newListaAnexoNotaRemessa = new DataView<AnexoNotaRemessa>("anexos", new SortableDataProviderAnexos()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<AnexoNotaRemessa> item) {
                item.add(new Label("nomeAnexo"));
                item.add(new Label("tipoArquivoTermoEntrega", item.getModelObject().getTipoArquivoTermoEntrega().getDescricao()));
                Button btnDownload = componentFactory.newButton("btnDonwload", () -> downloadAnexo(item.getModelObject()));
                item.add(btnDownload);
            }
        };
        newListaAnexoNotaRemessa.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return newListaAnexoNotaRemessa;
    }

    private class SortableDataProviderAnexos extends SortableDataProvider<AnexoNotaRemessa, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<AnexoNotaRemessa> iterator(long first, long count) {
            return listaAnexosNotaRemessa.iterator();
        }

        @Override
        public long size() {
            return listaAnexosNotaRemessa.size();
        }

        @Override
        public IModel<AnexoNotaRemessa> model(AnexoNotaRemessa object) {
            return new CompoundPropertyModel<AnexoNotaRemessa>(object);
        }

    }

    /*
     * private void buscarTermoDefinitivo() { listaTermoRecebimentoDefinitivo =
     * new ArrayList<TermoRecebimentoDefinitivo>();
     * listaTermoRecebimentoDefinitivo.addAll(termoRecebimentoDefinitivoService.
     * buscarTodosOsTermosRecebimentoDefinitivoPorNotaRemessa(this.
     * notaRemessaOrdemFornecimentoContrato.getId())); }
     */

    private DataView<TermoRecebimentoDefinitivo> newDataViewTermoRecebimentoDefinitivoDto() {
        newListaTermoRecebimentoDefinitivo = new DataView<TermoRecebimentoDefinitivo>("termos", new SortableDataProviderTermoDefinitivoDto()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<TermoRecebimentoDefinitivo> item) {
                item.add(new Label("ntrd", item.getModelObject().getNomeAnexo()));
                item.add(new Label("dtGeracao", DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataGeracao(), "dd/MM/yyyy")));

                StringBuilder sb = new StringBuilder(1);
                sb.append("<table><tr><td><center>  ID  </center></td><td>&emsp;</td><td><center>  ITEM  </center></td></tr><tr><td colspan='3'><hr></hr></td></tr>");

                for (ObjetoFornecimentoContrato obj : termoRecebimentoDefinitivoService.buscarTodosOsObjetosFornecimentoContratoPeloIdTermo(item.getModelObject().getId())) {
                    sb.append("<tr><td><center>  " + obj.getId() + "  </center>  </td><td>&emsp;</td><td><center>  " + obj.getItem().getNomeBem() + "  </center></td></tr>");
                }
                sb.append("</table>");
                String iconeIdItem = "<a tabindex=\"0\" role=\"button\" data-toggle=\"popover\" data-trigger=\"focus\" data-content=\"" + sb.toString() + "\"><i style=\"color:green;\" class=\"fa fa-info-circle\"> </i></a>";
                Label lblDescricaoiconeIdItem = new Label("itemTermo", iconeIdItem);
                lblDescricaoiconeIdItem.setEscapeModelStrings(Boolean.FALSE);
                lblDescricaoiconeIdItem.setOutputMarkupId(Boolean.TRUE);
                item.add(lblDescricaoiconeIdItem);

                Button btnDownload = componentFactory.newButton("btnDonwloadTermo", () -> downloadAnexoTermo(item.getModelObject()));
                item.add(btnDownload);
            }

        };
        newListaTermoRecebimentoDefinitivo.setItemsPerPage(getItensPorPaginaTermo() == null ? Constants.ITEMS_PER_PAGE_PAGINATION : getItensPorPaginaTermo());
        return newListaTermoRecebimentoDefinitivo;
    }

    private class SortableDataProviderTermoDefinitivoDto extends SortableDataProvider<TermoRecebimentoDefinitivo, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<TermoRecebimentoDefinitivo> iterator(long first, long count) {
            return listaTermoRecebimentoDefinitivo.iterator();
        }

        @Override
        public long size() {
            return listaTermoRecebimentoDefinitivo.size();
        }

        @Override
        public IModel<TermoRecebimentoDefinitivo> model(TermoRecebimentoDefinitivo object) {
            return new CompoundPropertyModel<TermoRecebimentoDefinitivo>(object);
        }

    }

    private void downloadAnexoTermo(TermoRecebimentoDefinitivo termoRecebimentoDefinitivo) {
        if (termoRecebimentoDefinitivo.getId() != null) {
            AnexoDto retorno = anexoTermoRecebimentoDefinitivoService.buscarPeloId(termoRecebimentoDefinitivo.getId());
            SideUtil.download(retorno.getConteudo(), "Termo_Recebimento_Definitivo_" + retorno.getNomeAnexo() + ".pdf");
        }
    }

    private void downloadAnexo(AnexoNotaRemessa anexoNotaRemessa) {
        if (anexoNotaRemessa.getId() != null) {
            AnexoDto retorno = anexoNotaRemessaService.buscarPeloId(anexoNotaRemessa.getId());
            SideUtil.download(retorno.getConteudo(), retorno.getNomeAnexo());
        } else {
            SideUtil.download(anexoNotaRemessa.getConteudo(), anexoNotaRemessa.getNomeAnexo());
        }
    }

    private InfraAjaxFallbackLink<Void> newButtonFecharDetalhamentoItem() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnFecharDetalhamento", (target) -> actionFecharDetalhamentoItem(target));
        btn.setOutputMarkupId(Boolean.TRUE);
        return btn;
    }

    private InfraAjaxFallbackLink<Void> newButtonFecharDetalhamentoItemAnalisado() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnFecharDetalhamentoAnalisado", (target) -> actionFecharDetalhamentoItemAnalisado(target));
        btn.setOutputMarkupId(Boolean.TRUE);
        return btn;
    }

    private InfraAjaxFallbackLink<Void> newButtonVoltar() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnVoltar", (target) -> actionVoltar());
        btn.setOutputMarkupId(Boolean.TRUE);
        return btn;
    }

    private InfraAjaxFallbackLink<Void> newButtonAceitar() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnAceito", (target) -> actionAceitar(target));
        btn.setOutputMarkupId(Boolean.TRUE);
        btn.setVisible(this.habilitarAceito);
        return btn;
    }

    private void atualizarStatusDoContratanteDaNotaRemessa() {
        if (listaItensParaAnalisar != null && listaItensParaAnalisar.isEmpty()) {
            if (listaItensAnalisados != null && !listaItensAnalisados.isEmpty()) {

                int quantidadeItens = listaItensAnalisados.size();
                int itensAceitos = 0;
                int itensNaoAceitos = 0;
                int itensRessalva = 0;
                for (ObjetoFornecimentoContrato item : listaItensAnalisados) {

                    if (item.getAnaliseFinalItem() == EnumAnaliseFinalItem.ACEITO) {
                        itensAceitos++;
                    }

                    if (item.getAnaliseFinalItem() == EnumAnaliseFinalItem.NAO_ACEITO) {
                        itensNaoAceitos++;
                    }

                    if (item.getAnaliseFinalItem() == EnumAnaliseFinalItem.ACEITO_RESSALVA) {
                        itensRessalva++;
                    }
                }

                if (itensRessalva > 0) {
                    notaRemessaService.atualizarStatusExecucaoNotaRemessaContratantePeloIdNotaRemessa(EnumStatusExecucaoNotaRemessaContratante.ACEITO_COM_RESALVA, notaRemessaOrdemFornecimentoContrato.getId());
                    return;
                }

                if (quantidadeItens == itensAceitos) {
                    notaRemessaService.atualizarStatusExecucaoNotaRemessaContratantePeloIdNotaRemessa(EnumStatusExecucaoNotaRemessaContratante.ACEITO, notaRemessaOrdemFornecimentoContrato.getId());
                    return;
                }

                if (quantidadeItens == itensNaoAceitos) {
                    notaRemessaService.atualizarStatusExecucaoNotaRemessaContratantePeloIdNotaRemessa(EnumStatusExecucaoNotaRemessaContratante.NAO_ACEITO, notaRemessaOrdemFornecimentoContrato.getId());
                }

            }
        } else {
            notaRemessaService.atualizarStatusExecucaoNotaRemessaContratantePeloIdNotaRemessa(EnumStatusExecucaoNotaRemessaContratante.EM_ANALISE, notaRemessaOrdemFornecimentoContrato.getId());
        }
    }

    private InfraAjaxFallbackLink<Void> newButtonNaoAceitar() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnNaoAceito", (target) -> actionNaoAceitar(target));
        btn.setOutputMarkupId(Boolean.TRUE);
        btn.setVisible(this.habilitarNaoAceito);
        return btn;
    }

    private InfraAjaxFallbackLink<Void> newButtonAceitarRessalva() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnAceitoRessalva", (target) -> actionAceitarRessalva(target));
        btn.setOutputMarkupId(Boolean.TRUE);
        btn.setVisible(this.habilitarAceitoRessalva);
        return btn;
    }

    private InfraAjaxFallbackLink<Void> newButtonNaoAceitarMotivo() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnNaoAceitoMotivo", (target) -> actionNaoAceitarMotivo(target));
        btn.setOutputMarkupId(Boolean.TRUE);
        btn.setVisible(this.habilitarNaoAceitoMotivo);
        return btn;
    }

    private InfraAjaxFallbackLink<Void> newButtonCancelar() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnCancelar", (target) -> actionCancelar(target));
        btn.setOutputMarkupId(Boolean.TRUE);
        btn.setVisible(this.habilitarCancelar);
        return btn;
    }

    private void actionCancelar(AjaxRequestTarget target) {
        this.itemSelecionarTodos = Boolean.FALSE;
        for (ObjetoFornecimentoContrato obj : listaItensParaAnalisar) {
            obj.setItemSelecionado(Boolean.FALSE);
            listaItensAnalisarSelecionados.remove(obj);
        }
        this.descricaoMotivo = new String();
        panelDetalhamentoCompararItens.addOrReplace(panelCompararItensParaAnalisar = new PanelCompararItensAnalisar("panelCompararItens", new ObjetoFornecimentoContrato()));
        panelDetalhamentoCompararItens.setVisible(Boolean.FALSE);
        panelItensParaAnalisar.addOrReplace(panelDetalhamentoCompararItens);
        reverterBotoes();
        atualizarBotoes(target);
    }

    private void actionNaoAceitarMotivo(AjaxRequestTarget target) {
        if (this.listaItensAnalisarSelecionados.size() > 0) {
            atualizarNaoAceitarMotivo();
            atualizarBotoes(target);
        } else {
            addMsgError("É necessário selecionar um Item!");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        }
    }

    private boolean validarMotivo(AjaxRequestTarget target) {
        Boolean validar = Boolean.TRUE;
        if ("".trim().equals(this.descricaoMotivo)) {
            addMsgError("Campo 'Motivo' é obrigatório.");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
            validar = Boolean.FALSE;
        }

        if (!"".trim().equals(descricaoMotivo) && descricaoMotivo.length() > 50000) {
            addMsgError("'Motivo*' deve ter no máximo 50000 caracteres.");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
            validar = Boolean.FALSE;
            atualizarBotoes(target);
        }
        return validar;
    }

    private InfraAjaxFallbackLink<Void> newButtonAceitarRessalvaMotivo() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnAceitoRessalvaMotivo", (target) -> actionAceitarRessalvaMotivo(target));
        btn.setOutputMarkupId(Boolean.TRUE);
        btn.setVisible(this.habilitarAceitoRessalvaMotivo);
        return btn;
    }

    private void actionAceitarRessalvaMotivo(AjaxRequestTarget target) {
        if (this.listaItensAnalisarSelecionados.size() > 0) {
            botoesAceitarRessalvaMotivo();
            atualizarBotoes(target);
        } else {
            addMsgError("É necessário selecionar um Item!");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        }
    }

    public TextArea<String> newTextAreaDescricao() {
        TextArea<String> text = new TextArea<String>("descricaoMotivo", new PropertyModel<String>(this, "descricaoMotivo"));
        text.setLabel(Model.of("Motivo*"));
        text.setOutputMarkupId(Boolean.TRUE);
        text.setVisible(this.habilitarMotivo);
        actionAreaDescricao(text);
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

    // acoes
    private void actionDetalharItem(ObjetoFornecimentoContrato objetoFornecimentoContrato, AjaxRequestTarget target) {
        this.listaItensAnalisarSelecionados.add(objetoFornecimentoContrato);
        panelDetalhamentoCompararItens.addOrReplace(panelCompararItensParaAnalisar = new PanelCompararItensAnalisar("panelCompararItens", objetoFornecimentoContrato));
        panelDetalhamentoCompararItens.setVisible(Boolean.TRUE);
        target.add(panelDetalhamentoCompararItens);
        target.appendJavaScript("ancorarResultadoPesquisa('#ancorarDetalhamento');");
    }

    private void actionDetalharItemAnalisado(ObjetoFornecimentoContrato objetoFornecimentoContrato, AjaxRequestTarget target) {
        this.listaItensAnalisadosSelecionados.add(objetoFornecimentoContrato);
        panelDetalhamentoCompararItensAnalisado.addOrReplace(panelCompararItensAnalisado = new PanelCompararItensAnalisar("panelCompararItensAnalisado", objetoFornecimentoContrato));
        panelDetalhamentoCompararItensAnalisado.setVisible(Boolean.TRUE);
        target.add(panelDetalhamentoCompararItensAnalisado);
        target.appendJavaScript("ancorarResultadoPesquisa('#ancorarDetalhamentoAnalisado');");
    }

    private void actionFecharDetalhamentoItem(AjaxRequestTarget target) {
        this.listaItensAnalisarSelecionados.clear();
        panelDetalhamentoCompararItens.addOrReplace(panelCompararItensParaAnalisar = new PanelCompararItensAnalisar("panelCompararItens", new ObjetoFornecimentoContrato()));
        panelDetalhamentoCompararItens.setVisible(Boolean.FALSE);
        target.add(panelDetalhamentoCompararItens);
        target.appendJavaScript("ancorarResultadoPesquisa('#ancorarVoltarDetalhamento');");
    }

    private void actionFecharDetalhamentoItemAnalisado(AjaxRequestTarget target) {
        this.listaItensAnalisadosSelecionados.clear();
        panelDetalhamentoCompararItensAnalisado.addOrReplace(panelCompararItensAnalisado = new PanelCompararItensAnalisar("panelCompararItensAnalisado", new ObjetoFornecimentoContrato()));
        panelDetalhamentoCompararItensAnalisado.setVisible(Boolean.FALSE);
        target.add(panelDetalhamentoCompararItensAnalisado);
        target.appendJavaScript("ancorarResultadoPesquisa('#ancorarVoltarDetalhamentoAnalisado');");
    }

    private void actionVoltar() {
        setResponsePage(backPage);
    }

    private InfraAjaxConfirmButton newBtnDevolverCorrecao() {
        InfraAjaxConfirmButton btnDevolverCorrecao = componentFactory.newAJaxConfirmButton("btnDevolverCorrecao", "MT034", form, (target, formz) -> actionDevolverCorrecao(target));
        btnDevolverCorrecao.setOutputMarkupId(Boolean.TRUE);
        return btnDevolverCorrecao;
    }

    private InfraAjaxFallbackLink<Void> newBtnDevolverAnalise() {
        InfraAjaxFallbackLink<Void> btn = componentFactory.newAjaxFallbackLink("btnDevolverAnalise", (target) -> actionDevolverAnalise(target));
        btn.setOutputMarkupId(Boolean.TRUE);
        return btn;
    }

    private InfraAjaxConfirmButton newButtonGerarTermoRecebimentoDefinitivo() {
        InfraAjaxConfirmButton btnGerarTermoRecebimentoDefinitivo = componentFactory.newAJaxConfirmButton("btnGerarTermoRecebimentoDefinitivo", "MSG014", form, (target, formz) -> gerarGerarTermoRecebimentoDefinitivo(target));
        btnGerarTermoRecebimentoDefinitivo.setOutputMarkupId(Boolean.TRUE);
        return btnGerarTermoRecebimentoDefinitivo;
    }

    private List<ObjetoFornecimentoContrato> buscarNovaLista(List<ObjetoFornecimentoContrato> listaItensAnalisadosSelecionados) {
        List<ObjetoFornecimentoContrato> novaLista = new ArrayList<ObjetoFornecimentoContrato>();
        for (int i = 0; i < listaItensAnalisadosSelecionados.size(); i++) {
            if (listaItensAnalisadosSelecionados.get(i).getAnaliseFinalItem() != null && listaItensAnalisadosSelecionados.get(i).getAnaliseFinalItem().equals(EnumAnaliseFinalItem.NAO_ACEITO)) {
                for (ObjetoFornecimentoContrato obj : listaItensAnalisadosSelecionados) {
                    if (listaItensAnalisadosSelecionados.get(i).equals(obj)) {
                        obj.setItemSelecionado(Boolean.FALSE);
                    }
                }
            } else {
                novaLista.add(listaItensAnalisadosSelecionados.get(i));
            }
        }
        return novaLista;
    }

    private void gerarGerarTermoRecebimentoDefinitivo(AjaxRequestTarget target) {
        this.listaItensAnalisadosSelecionados = buscarNovaLista(listaItensAnalisadosSelecionados);

        if (this.listaItensAnalisadosSelecionados.size() > 0) {
            TermoRecebimentoDefinitivoOfBuilder jasper = new TermoRecebimentoDefinitivoOfBuilder(SideUtil.getCaminhoCompletoRelatorios(getHttpServletRequest().getServletContext()));
            jasper.setNomeTermo("Termo de Recebimento Definitivo - " + "teste");
            jasper.setTipoTermo(EnumTipoMinuta.PDF);
            jasper.setTermoRecebimentoDefinitivoDto(popular(this.listaItensAnalisadosSelecionados));
            ByteArrayOutputStream exportar = jasper.exportToByteArray();

            TermoRecebimentoDefinitivo termoRecebimentoDefinitivo = new TermoRecebimentoDefinitivo();
            termoRecebimentoDefinitivo.setNotaRemessaOrdemFornecimentoContrato(notaRemessaOrdemFornecimentoContrato);
            termoRecebimentoDefinitivo.setTermoRecebimentoDefinitivoGerado(exportar.toByteArray());
            termoRecebimentoDefinitivo.setNomeAnexo("Termo de Recebimento Definitivo");
            termoRecebimentoDefinitivoService.incluir(termoRecebimentoDefinitivo, getIdentificador(), this.listaItensAnalisadosSelecionados);

            /*
             * for (ObjetoFornecimentoContrato obj :
             * this.listaItensAnalisadosSelecionados) {
             * listaItensAnalisados.remove(obj); }
             */
            // buscarTermoDefinitivo();
            // this.listaItensAnalisadosSelecionados.clear();

            // email para inserir a nota fiscal
            mailService.enviarEmailFornecedorInserirNotaFiscal(termoRecebimentoDefinitivo.getNotaRemessaOrdemFornecimentoContrato().getOrdemFornecimento().getContrato().getPreposto());

            atualizarPainelAnalisarEAnalisados(target);
            atualizartermo(target);
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
            addMsgInfo("Termo de recebimento definitivo gerado com sucesso!");
        } else {
            // this.listaItensAnalisadosSelecionados.clear();
            atualizarPainelAnalisarEAnalisados(target);
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
            addMsgError("Selecione o Item Analisado antes de gerar Termo de Recebimento Definitivo!");
            addMsgError("OBS: Não é possível gerar Termo de Recebimento Definitivo de Itens com o Status de \"NÃO ACEITO\"!");
        }
    }

    private TermoRecebimentoDefinitivoDto popular(List<ObjetoFornecimentoContrato> novaLista) {
        TermoRecebimentoDefinitivoDto termoRecebimentoDefinitivoDto = new TermoRecebimentoDefinitivoDto();
        termoRecebimentoDefinitivoDto.setNomePrograma(notaRemessaOrdemFornecimentoContrato.getOrdemFornecimento().getContrato().getPrograma().getCodigoIdentificadorProgramaPublicadoENomePrograma());
        termoRecebimentoDefinitivoDto.setNomeUnidadeExecutora(notaRemessaOrdemFornecimentoContrato.getOrdemFornecimento().getContrato().getPrograma().getUnidadeExecutora().getNomeUnidadeExecutora());

        String[] lista = formatarLista(notaRemessaOrdemFornecimentoContrato, Boolean.TRUE);

        termoRecebimentoDefinitivoDto.setNomeBeneficiario(lista[5]);
        termoRecebimentoDefinitivoDto.setEnderecoBeneficiario(lista[2]);

        for (InscricaoPrograma inscricaoPrograma : listaInscricaoPrograma) {
            termoRecebimentoDefinitivoDto.setNomeRepresentante(inscricaoPrograma.getPessoaEntidade().getPessoa().getNomePessoa());
            termoRecebimentoDefinitivoDto.setTelefoneRepresentante(MascaraUtils.formatarMascaraTelefone(inscricaoPrograma.getPessoaEntidade().getPessoa().getNumeroTelefone()));
            termoRecebimentoDefinitivoDto.setEmailRepresentante(inscricaoPrograma.getPessoaEntidade().getPessoa().getEmail());
        }
        for (ObjetoFornecimentoContrato obj : ordernarLista(novaLista)) {
            TermoDefinitivoItensDto termoDefinitivoItensDto = new TermoDefinitivoItensDto();
            termoDefinitivoItensDto.setIdItem(String.valueOf(obj.getId()));
            termoDefinitivoItensDto.setNomeBem(obj.getItem().getNomeBem());

            if (obj.getEstadoDeNovo() != null && obj.getEstadoDeNovo()) {
                termoDefinitivoItensDto.setEstadoDeNovo("Sim");
            } else if (obj.getEstadoDeNovo() != null && !obj.getEstadoDeNovo()) {
                termoDefinitivoItensDto.setEstadoDeNovo("Não");
            } else {
                termoDefinitivoItensDto.setEstadoDeNovo("-");
            }

            if (obj.getFuncionandoDeAcordo() != null && obj.getFuncionandoDeAcordo()) {
                termoDefinitivoItensDto.setFuncionandoDeAcordo("Sim");
                termoDefinitivoItensDto.setDescricaoNaoFuncionandoDeAcordo("-");
            } else if (obj.getFuncionandoDeAcordo() != null && !obj.getFuncionandoDeAcordo()) {
                termoDefinitivoItensDto.setFuncionandoDeAcordo("Não");
                termoDefinitivoItensDto.setDescricaoNaoFuncionandoDeAcordo(obj.getDescricaoNaoFuncionandoDeAcordo());
            } else {
                termoDefinitivoItensDto.setFuncionandoDeAcordo("-");
                termoDefinitivoItensDto.setDescricaoNaoFuncionandoDeAcordo("-");
            }

            if (obj.getConfiguradoDeAcordo() != null && obj.getConfiguradoDeAcordo()) {
                termoDefinitivoItensDto.setConfiguradoDeAcordo("Sim");
                termoDefinitivoItensDto.setDescricaoNaoConfiguradoDeAcordo("-");
            } else if (obj.getConfiguradoDeAcordo() != null && !obj.getConfiguradoDeAcordo()) {
                termoDefinitivoItensDto.setConfiguradoDeAcordo("Não");
                termoDefinitivoItensDto.setDescricaoNaoConfiguradoDeAcordo(obj.getDescricaoNaoConfiguradoDeAcordo());
            } else {
                termoDefinitivoItensDto.setConfiguradoDeAcordo("-");
                termoDefinitivoItensDto.setDescricaoNaoConfiguradoDeAcordo("-");
            }
            termoRecebimentoDefinitivoDto.getListaItens().add(termoDefinitivoItensDto);
        }

        for (InscricaoPrograma inscricaoPrograma : listaInscricaoPrograma) {
            TermoDefinitivoItensDto termoEntregaitensDto = new TermoDefinitivoItensDto();
            termoEntregaitensDto.setNomeMembros(inscricaoPrograma.getPessoaEntidade().getPessoa().getNomePessoa().toUpperCase());
            termoRecebimentoDefinitivoDto.getListaMembros().add(termoEntregaitensDto);
            for (ComissaoRecebimento comissaoRecebimento : inscricaoProgramaService.buscarComissaoRecebimento(inscricaoPrograma)) {
                if (comissaoRecebimento.getMembroComissao().getStatusPessoa() == EnumStatusPessoa.ATIVO && !inscricaoPrograma.getPessoaEntidade().getPessoa().getNomePessoa().toUpperCase().equals(comissaoRecebimento.getMembroComissao().getNomePessoa().toUpperCase())) {
                    termoEntregaitensDto = new TermoDefinitivoItensDto();
                    termoEntregaitensDto.setNomeMembros(comissaoRecebimento.getMembroComissao().getNomePessoa().toUpperCase());
                    termoRecebimentoDefinitivoDto.getListaMembros().add(termoEntregaitensDto);
                }
            }
        }
        return termoRecebimentoDefinitivoDto;
    }

    private void actionDevolverCorrecao(AjaxRequestTarget target) {
        if (this.listaItensAnalisadosSelecionados.size() == 0) {
            addMsgError("É necessário selecionar um Item!");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
            return;
        }
        for (ObjetoFornecimentoContrato obj : listaItensAnalisadosSelecionados) {
            if (obj.getAnaliseFinalItem().equals(EnumAnaliseFinalItem.NAO_ACEITO) && obj.getMostrarItem()) {
                this.objetoFornecimentoContratoService.devolverParaCorrecao(obj);
            } else {
                addMsgError("Verificar se o item esta com a 'Análise Final' como não aceito e/ou se este item é uma formatação do lote.");
                target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
                return;
            }
        }
        populalistaInscricao(listaItensAnalisadosSelecionados.get(0).getLocalEntrega().getEntidade());
        enviaEmailParaFornecedorBeneficiario();
        target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        addMsgInfo("Devolução para Correção dos itens 'Não Aceito', efetuada com sucesso!");
        atualizarPainelAnalisarEAnalisados(target);
    }

    private void enviaEmailParaFornecedorBeneficiario() {
        Pessoa fornecedorPreposto = this.listaItensAnalisadosSelecionados.get(0).getOrdemFornecimento().getContrato().getPreposto();
        Pessoa entidadePessoaRepresentanteTitular = this.listaInscricaoPrograma.get(0).getPessoaEntidade().getPessoa();
        mailService.enviarEmailDevolverCorrecao(fornecedorPreposto, entidadePessoaRepresentanteTitular, listaItensAnalisadosSelecionados);
    }

    private void populalistaInscricao(Entidade entidade) {
        listaInscricaoPrograma = new ArrayList<InscricaoPrograma>();
        listaInscricaoPrograma = inscricaoProgramaService.buscarInscricaoProgramaPeloProgramaEEntidade(programa, entidade);
    }

    private void actionDevolverAnalise(AjaxRequestTarget target) {
        if (listaItensAnalisadosSelecionados.size() > 0) {
            for (ObjetoFornecimentoContrato obj : listaItensAnalisadosSelecionados) {
                obj.setMotivo("");
                obj.setItemSelecionado(Boolean.FALSE);
                notaRemessaService.devolverParaAnalise(obj);
                /*
                 * obj.setAnaliseFinalItem(EnumAnaliseFinalItem.NAO_ANALISADO);
                 * listaItensParaAnalisar.add(obj);
                 * listaItensAnalisados.remove(obj);
                 */
            }
            this.descricaoMotivo = "";
            // listaItensAnalisadosSelecionados.clear();
            atualizarPainelAnalisarEAnalisados(target);
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
            addMsgInfo("Item Devolvido para Análise com sucesso!");

        } else {
            addMsgError("É necessário selecionar um Item!");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        }
        atualizarStatusDoContratanteDaNotaRemessa();
    }

    private void actionAceitar(AjaxRequestTarget target) {
        // Ação para devolver para correção

        if (this.listaItensAnalisarSelecionados.size() > 0) {
            for (ObjetoFornecimentoContrato obj : listaItensAnalisarSelecionados) {
                obj.setItemSelecionado(Boolean.FALSE);
                notaRemessaService.aceitarAnaliseDoItem(obj);
                /*
                 * obj.setAnaliseFinalItem(EnumAnaliseFinalItem.ACEITO);
                 * listaItensAnalisados.add(obj);
                 * listaItensParaAnalisar.remove(obj);
                 */
            }
            atualizarPainelAnalisarEAnalisados(target);
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
            addMsgInfo("Item analisado com sucesso!");
        } else {
            addMsgError("É necessário selecionar um Item!");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        }
        atualizarStatusDoContratanteDaNotaRemessa();
    }

    private void actionNaoAceitar(AjaxRequestTarget target) {
        // Ação para devolver para correção
        if (!validarMotivo(target)) {
            return;
        }

        if (this.listaItensAnalisarSelecionados.size() > 0) {
            for (ObjetoFornecimentoContrato obj : listaItensAnalisarSelecionados) {
                obj.setItemSelecionado(Boolean.FALSE);
                obj.setMotivo(this.descricaoMotivo);
                notaRemessaService.naoAceitarAnaliseDoItem(obj);
                atualizarStatusDoContratanteDaNotaRemessa();
                /*
                 * obj.setAnaliseFinalItem(EnumAnaliseFinalItem.NAO_ACEITO);
                 * listaItensAnalisados.add(obj);
                 * listaItensParaAnalisar.remove(obj);
                 */
            }
            this.descricaoMotivo = "";
            reverterBotoes();
            atualizarBotoes(target);
            atualizarPainelAnalisarEAnalisados(target);
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
            addMsgInfo("Item analisado com sucesso!");
        } else {
            addMsgError("É necessário selecionar um Item!");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        }
        atualizarStatusDoContratanteDaNotaRemessa();
    }

    private void actionAceitarRessalva(AjaxRequestTarget target) {
        if (!validarMotivo(target)) {
            return;
        }

        // Ação para devolver para correção
        if (this.listaItensAnalisarSelecionados.size() > 0) {
            for (ObjetoFornecimentoContrato obj : listaItensAnalisarSelecionados) {
                obj.setItemSelecionado(Boolean.FALSE);
                obj.setMotivo(this.descricaoMotivo);
                notaRemessaService.aceitarComRessalvaAnaliseDoItem(obj);
                atualizarStatusDoContratanteDaNotaRemessa();
                /*
                 * obj.setAnaliseFinalItem(EnumAnaliseFinalItem.ACEITO_RESSALVA)
                 * ; listaItensAnalisados.add(obj);
                 * listaItensParaAnalisar.remove(obj);
                 */
            }
            this.descricaoMotivo = "";
            reverterBotoes();
            atualizarBotoes(target);
            atualizarPainelAnalisarEAnalisados(target);
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
            addMsgInfo("Item analisado com sucesso!");
        } else {
            addMsgError("É necessário selecionar um Item!");
            target.appendJavaScript("ancorarResultadoPesquisa('#ancoraMensagemFeedBackCadastrarItemDesktop');");
        }
        atualizarStatusDoContratanteDaNotaRemessa();
    }

    private void atualizarPainelAnalisarEAnalisados(AjaxRequestTarget target) {
        polulaListas();

        this.descricaoMotivo = "";
        this.itemSelecionarTodos = Boolean.FALSE;
        this.itemSelecionadosTodos = Boolean.FALSE;
        this.listaItensAnalisarSelecionados.clear();
        this.listaItensAnalisadosSelecionados.clear();

        panelItensParaAnalisar = new PanelItensParaAnalisar("panelItensParaAnalisar");
        panelItensAnalisados = new PanelItensAnalisados("panelItensAnalisados");
        panelDataViewAnexos = new PanelDataViewAnexos("panelDataViewAnexos");
        panelDataViewTermo = new PanelDataViewTermo("panelDataViewTermo");

        panelDetalhamentoCompararItens.addOrReplace(new PanelCompararItensAnalisar("panelCompararItens", new ObjetoFornecimentoContrato()));
        panelDetalhamentoCompararItens.setVisible(Boolean.FALSE);

        panelDetalhamentoCompararItensAnalisado.addOrReplace(new PanelCompararItensAnalisar("panelCompararItensAnalisado", new ObjetoFornecimentoContrato()));
        panelDetalhamentoCompararItensAnalisado.setVisible(Boolean.FALSE);

        form.addOrReplace(panelItensParaAnalisar);
        form.addOrReplace(panelItensAnalisados);
        form.addOrReplace(panelDataViewAnexos);
        form.addOrReplace(panelDataViewTermo);

        target.appendJavaScript("atualizaCssDropDown();");
        target.add(form);
    }

    private void botoesAceitarRessalvaMotivo() {
        this.habilitarAceitoRessalvaMotivo = Boolean.FALSE;
        this.habilitarCancelar = Boolean.TRUE;
        this.habilitarMotivo = Boolean.TRUE;
        this.habilitarNaoAceitoMotivo = Boolean.FALSE;
        this.habilitarAceitoRessalva = Boolean.TRUE;
        this.habilitarNaoAceito = Boolean.FALSE;
        this.habilitarAceito = Boolean.FALSE;
    }

    private void reverterBotoes() {
        this.habilitarAceitoRessalvaMotivo = Boolean.TRUE;
        this.habilitarNaoAceitoMotivo = Boolean.TRUE;
        this.habilitarAceitoRessalva = Boolean.FALSE;
        this.habilitarNaoAceito = Boolean.FALSE;
        this.habilitarCancelar = Boolean.FALSE;
        this.habilitarMotivo = Boolean.FALSE;
        this.habilitarAceito = Boolean.TRUE;
    }

    private void atualizarBotoes(AjaxRequestTarget target) {
        panelItensParaAnalisar.addOrReplace(newButtonAceitarRessalvaMotivo());
        panelItensParaAnalisar.addOrReplace(newButtonNaoAceitarMotivo());
        panelItensParaAnalisar.addOrReplace(newButtonAceitar());
        panelItensParaAnalisar.addOrReplace(newButtonNaoAceitar());
        panelItensParaAnalisar.addOrReplace(newButtonAceitarRessalva());
        panelItensParaAnalisar.addOrReplace(newButtonCancelar());
        panelItensParaAnalisar.addOrReplace(newTextAreaDescricao());
        target.add(panelItensParaAnalisar);
    }

    private void atualizartermo(AjaxRequestTarget target) {
        panelDataViewTermo.addOrReplace(newListaTermoRecebimentoDefinitivo = newDataViewTermoRecebimentoDefinitivoDto());
        panelDataViewTermo.addOrReplace(new InfraAjaxPagingNavigator("paginationTermo", newListaTermoRecebimentoDefinitivo));
        target.add(panelDataViewTermo);
    }

    private void atualizarNaoAceitarMotivo() {
        this.habilitarAceitoRessalvaMotivo = Boolean.FALSE;
        this.habilitarNaoAceitoMotivo = Boolean.FALSE;
        this.habilitarCancelar = Boolean.TRUE;
        this.habilitarMotivo = Boolean.TRUE;
        this.habilitarAceitoRessalva = Boolean.FALSE;
        this.habilitarNaoAceito = Boolean.TRUE;
        this.habilitarAceito = Boolean.FALSE;
    }

    private String[] formatarLista(NotaRemessaOrdemFornecimentoContrato notaRemessaLocaisEntrega, boolean buscarMenbros) {
        String[] lista = new String[8];

        if ((notaRemessaLocaisEntrega.getOrdemFornecimento().getStatusOrdemFornecimento() == null)
                || (notaRemessaLocaisEntrega.getOrdemFornecimento().getStatusOrdemFornecimento() != null && notaRemessaLocaisEntrega.getOrdemFornecimento().getStatusOrdemFornecimento() == EnumStatusOrdemFornecimento.NAO_COMUNICADA)
                || (notaRemessaLocaisEntrega.getOrdemFornecimento().getStatusOrdemFornecimento() != null && notaRemessaLocaisEntrega.getOrdemFornecimento().getStatusOrdemFornecimento() == EnumStatusOrdemFornecimento.EMITIDA)) {
            lista[6] = "false";
        } else {
            lista[6] = "true";
        }

        Programa programa = new Programa();
        programa = notaRemessaLocaisEntrega.getOrdemFornecimento().getContrato().getPrograma();
        StringBuilder listaItens = new StringBuilder(1);
        StringBuilder listaQuantidades = new StringBuilder(1);
        StringBuilder listaItemQuantidade = new StringBuilder(1);
        // Vamos pegar a UF, MUNICIPIO e ENDEREÇO.
        for (ItensNotaRemessaOrdemFornecimentoContrato INFOFC : notaRemessaLocaisEntrega.getListaItensNotaRemessaOrdemFornecimentoContratos()) {
            lista[0] = INFOFC.getItemOrdemFornecimentoContrato().getLocalEntrega().getMunicipio().getUf().getSiglaUf();
            lista[1] = INFOFC.getItemOrdemFornecimentoContrato().getLocalEntrega().getMunicipio().getNomeMunicipio();
            lista[2] = INFOFC.getItemOrdemFornecimentoContrato().getLocalEntrega().getEnderecoCompleto();
            lista[5] = INFOFC.getItemOrdemFornecimentoContrato().getLocalEntrega().getEntidade().getNomeEntidade();
            if (buscarMenbros) {
                Entidade entidade = new Entidade();
                entidade = INFOFC.getItemOrdemFornecimentoContrato().getLocalEntrega().getEntidade();
                populalistaInscricao(entidade);
                // listaInscricaoPrograma =
                // inscricaoProgramaService.buscarInscricaoProgramaPeloProgramaEEntidade(programa,
                // entidade);
            }
            break;
        }

        int count = 0;
        // Vamos montar os itens e quantidades
        listaItemQuantidade.append("<table ><tr><td><b>Item</b></td><td><b>Quantidade</b></td></tr><tr><td colspan='2'><hr></hr></td></tr>");
        for (ItensNotaRemessaOrdemFornecimentoContrato INFOFC : notaRemessaLocaisEntrega.getListaItensNotaRemessaOrdemFornecimentoContratos()) {
            if (count > 0) {
                listaItens.append("/ ");
                listaQuantidades.append("/ ");
                listaItemQuantidade.append("<tr><td>" + INFOFC.getItemOrdemFornecimentoContrato().getItem().getNomeBem() + "</td><td><center>" + INFOFC.getItemOrdemFornecimentoContrato().getQuantidade().toString() + "</center></td></tr>");
            }
            listaItens.append(INFOFC.getItemOrdemFornecimentoContrato().getItem().getNomeBem());
            listaQuantidades.append(INFOFC.getItemOrdemFornecimentoContrato().getQuantidade().toString());
            listaItemQuantidade.append("<tr><td>" + INFOFC.getItemOrdemFornecimentoContrato().getItem().getNomeBem() + "</td><td><center>" + INFOFC.getItemOrdemFornecimentoContrato().getQuantidade().toString() + "</center></td></tr>");
            count++;
        }
        listaItemQuantidade.append("</table>");

        lista[3] = listaItens.toString();
        lista[4] = listaQuantidades.toString();
        lista[7] = listaItemQuantidade.toString();

        return lista;
    }

    // getters e setters
    public Integer getItensPorPagina() {
        return itensPorPagina;
    }

    public void setItensPorPagina(Integer itensPorPagina) {
        this.itensPorPagina = itensPorPagina;
    }

    public Integer getItensPorPaginaAnalisad() {
        return itensPorPaginaAnalisad;
    }

    public void setItensPorPaginaAnalisad(Integer itensPorPaginaAnalisad) {
        this.itensPorPaginaAnalisad = itensPorPaginaAnalisad;
    }

    public Boolean getItemSelecionarTodos() {
        return itemSelecionarTodos;
    }

    public void setItemSelecionarTodos(Boolean itemSelecionarTodos) {
        this.itemSelecionarTodos = itemSelecionarTodos;
    }

    public Boolean getItemSelecionadosTodos() {
        return itemSelecionadosTodos;
    }

    public void setItemSelecionadosTodos(Boolean itemSelecionadosTodos) {
        this.itemSelecionadosTodos = itemSelecionadosTodos;
    }

    public Integer getItensPorPaginaAnexo() {
        return itensPorPaginaAnexo;
    }

    public void setItensPorPaginaAnexo(Integer itensPorPaginaAnexo) {
        this.itensPorPaginaAnexo = itensPorPaginaAnexo;
    }

    public Integer getItensPorPaginaTermo() {
        return itensPorPaginaTermo;
    }

    public void setItensPorPaginaTermo(Integer itensPorPaginaTermo) {
        this.itensPorPaginaTermo = itensPorPaginaTermo;
    }

}
