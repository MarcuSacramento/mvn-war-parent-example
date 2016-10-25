package br.gov.mj.side.web.view.programa.inscricao;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.IConverter;
import org.wicketstuff.jasperreports.JRConcreteResource;
import org.wicketstuff.jasperreports.handlers.PdfResourceHandler;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraLocalDateTextField;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.enums.EnumAbaFaseAnalise;
import br.gov.mj.side.entidades.enums.EnumStatusInscricao;
import br.gov.mj.side.entidades.enums.EnumTipoLista;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaHistoricoPublicizacao;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaAvaliacaoPublicado;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaElegibilidadePublicado;
import br.gov.mj.side.web.dto.AnaliseDto;
import br.gov.mj.side.web.dto.PermissaoProgramaDto;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.ListaPublicadoService;
import br.gov.mj.side.web.service.MailService;
import br.gov.mj.side.web.service.PublicizacaoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.InscricaoListClassificacaoReportBuilder;
import br.gov.mj.side.web.util.InscricaoListReportBuilder;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.util.SortableInscricaoAvaliacaoDataProvider;
import br.gov.mj.side.web.util.SortableInscricaoElegibilidadeDataProvider;
import br.gov.mj.side.web.view.components.converters.CpfCnpjConverter;
import br.gov.mj.side.web.view.enums.EnumSelecao;
import br.gov.mj.side.web.view.programa.PanelFasePrograma;
import br.gov.mj.side.web.view.propostas.PropostasEnviadasPage;
import br.gov.mj.side.web.view.template.TemplatePage;

public class PublicarListaPropostaPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    private Form<PublicarListaPropostaPage> form;
    private Programa programa;

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private ListaPublicadoService listaPublicadoService;

    @Inject
    private InscricaoProgramaService inscricaoProgramaService;

    @Inject
    private PublicizacaoService publicizacaoService;

    @Inject
    private MailService mailService;

    private PanelFasePrograma panelFasePrograma;
    
    private LocalDate dataInicio;
    private LocalDate dataFim;

    private DataView<InscricaoPrograma> dataView;
    private GridListaElegibilidadePanel gridListaElegibilidadePanel;
    private GridListaAvaliacaoPanel gridListaAvaliacaoPanel;
    private PeriodoPanel recursoPanel;
    private PeriodoPanel cadastroLocalEntregaPanel;

    private ButtonsPanel buttonsPanel;
    private List<ListaElegibilidadePublicado> listasPublicadasElegibilidade;
    private List<ListaAvaliacaoPublicado> listaAvaliacaoPublicados;
    private EnumSelecao tipoSelecao;
    private Page backPage;
    private Page paginaAnalise;
    private String subTitulo = "";
    private Label lblSubTitulo;

    public PublicarListaPropostaPage(PageParameters pageParameters, Programa programa, EnumSelecao tipoSelecao, Page backPage) {
        super(pageParameters);
        setTitulo("Publicar Lista de Propostas");
        setTipoSelecao(tipoSelecao);
        setPrograma(programa);
        this.backPage = backPage;
        this.paginaAnalise = backPage;
        listasPublicadasElegibilidade = SideUtil.convertAnexoDtoToEntityListaElegibilidadePublicado(listaPublicadoService.buscarListaElegibilidadePublicadoPeloIdPrograma(programa.getId()));
        listaAvaliacaoPublicados = SideUtil.convertAnexoDtoToEntityListaAvaliacaoPublicado(listaPublicadoService.buscarListaAvaliacaoPublicadoPeloIdPrograma(programa.getId()));

        initComponents();
    }

    private void initComponents() {

        form = componentFactory.newForm("form", this);
        
        form.add(panelFasePrograma = new PanelFasePrograma("panelFasePrograma",programa,backPage));

        lblSubTitulo = new Label("lblSubtitulo", new LambdaModel<String>(this::getSubTitulo, this::setSubTitulo));
        form.add(lblSubTitulo);

        gridListaElegibilidadePanel = newGridListaElegibilidadePanel();
        form.add(gridListaElegibilidadePanel);

        gridListaAvaliacaoPanel = newGridListaAvaliacaoPanel();
        form.add(gridListaAvaliacaoPanel);

        recursoPanel = newRecursoPeriodo();
        form.add(recursoPanel);

        cadastroLocalEntregaPanel = newCadastroLocaisEntregaPeriodo();
        form.add(cadastroLocalEntregaPanel);

        buttonsPanel = newButtonsPanel();
        form.add(buttonsPanel);

        setup();

        add(form);
    }

    private void setup() {

        esconderTodosPanels();
        definirSubTitulo();
        if (EnumSelecao.PROPOSTAS_ELEGIVEIS.equals(getTipoSelecao())) {
            gridListaElegibilidadePanel.setVisible(true);
            if (isPublicacaoListaPreliminar()) {
                recursoPanel.setVisible(true);
                buttonsPanel.setVisible(true);
            } else if (isPublicacaoListaDefinitiva()) {
                buttonsPanel.setVisible(true);
            }
        }

        if (EnumSelecao.CLASSIFICACAO_PROSPOSTA.equals(getTipoSelecao())) {
            gridListaAvaliacaoPanel.setVisible(true);
            buttonsPanel.setVisible(true);
            if (isPublicacaoListaPreliminar()) {
                recursoPanel.setVisible(true);
            }
            if (isPublicacaoListaDefinitiva()) {
                ProgramaHistoricoPublicizacao ultimoHistoricoPublicizacao = publicizacaoService.buscarUltimoProgramaHistoricoPublicizacao(programa);
                dataInicio = ultimoHistoricoPublicizacao.getDataFinalAnalise().plusDays(1);
                cadastroLocalEntregaPanel.setVisible(true);
            }
        }
    }

    private void definirSubTitulo() {
        if (isPublicacaoListaPreliminar()) {
            setSubTitulo("Lista de " + getTipoSelecao().getDescricao() + " - Preliminar");
        }

        if (isPublicacaoListaDefinitiva()) {
            setSubTitulo("Lista de " + getTipoSelecao().getDescricao() + " - Definitiva");
        }
    }

    private void esconderTodosPanels() {
        gridListaElegibilidadePanel.setVisible(false);
        gridListaAvaliacaoPanel.setVisible(false);
        recursoPanel.setVisible(false);
        buttonsPanel.setVisible(false);
        cadastroLocalEntregaPanel.setVisible(false);
    }

    private ButtonsPanel newButtonsPanel() {
        return new ButtonsPanel("buttonsPanel");
    }

    private GridListaAvaliacaoPanel newGridListaAvaliacaoPanel() {
        return new GridListaAvaliacaoPanel("gridListaAvaliacaoPanel");
    }

    private GridListaElegibilidadePanel newGridListaElegibilidadePanel() {
        return new GridListaElegibilidadePanel("gridListaPanel");
    }

    private PeriodoPanel newRecursoPeriodo() {
        return new PeriodoPanel("recursoPanel", true, true, "Recurso");
    }

    private PeriodoPanel newCadastroLocaisEntregaPeriodo() {
        return new PeriodoPanel("cadastroLocalEntregaPanel", false, true, "Cadastro locais de entrega");
    }

    private DataView<InscricaoPrograma> newDataViewPropostasElegiveis() {
        return new DataView<InscricaoPrograma>("inscricoes", new SortableInscricaoElegibilidadeDataProvider(getPrograma(), inscricaoProgramaService,EnumStatusInscricao.CONCLUIDA_ANALISE_ELEGIBILIDADE)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<InscricaoPrograma> item) {

                item.add(new Label("pessoaEntidade.entidade.nomeEntidade"));
                item.add(new Label("pessoaEntidade.entidade.numeroCnpj") {
                    private static final long serialVersionUID = 1L;

                    @SuppressWarnings("unchecked")
                    @Override
                    public <C> IConverter<C> getConverter(Class<C> type) {
                        return (IConverter<C>) new CpfCnpjConverter();
                    }
                });
                item.add(new Label("pessoaEntidade.entidade.tipoEntidade.descricaoTipoEntidade"));
                item.add(new Label("pessoaEntidade.entidade.municipio.uf.nomeUf"));
                setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
            }
        };
    }

    private DataView<InscricaoPrograma> newDataViewClassificacaoPropostas() {
        return new DataView<InscricaoPrograma>("inscricoes", new SortableInscricaoAvaliacaoDataProvider(getPrograma(), inscricaoProgramaService)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<InscricaoPrograma> item) {

                item.add(new Label("colocacao"));
                item.add(new Label("pessoaEntidade.entidade.nomeEntidade"));
                item.add(new Label("pessoaEntidade.entidade.numeroCnpj") {
                    private static final long serialVersionUID = 1L;

                    @SuppressWarnings("unchecked")
                    @Override
                    public <C> IConverter<C> getConverter(Class<C> type) {
                        return (IConverter<C>) new CpfCnpjConverter();
                    }
                });
                item.add(new Label("pessoaEntidade.entidade.tipoEntidade.descricaoTipoEntidade"));
                item.add(new Label("pessoaEntidade.entidade.municipio.uf.nomeUf"));

                if (item.getModelObject().isClassificado()) {
                    item.add(AttributeModifier.append("class", "success"));
                }

                setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
            }
        };
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    @SuppressWarnings("unused")
    private class PeriodoPanel extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        private String titulo;

        public PeriodoPanel(String id, boolean enabledDataInicio, boolean enabledDataFim, String titulo) {
            super(id);
            this.titulo = titulo;

            add(new Label("lblTituloPeriodo", new PropertyModel<String>(this, "titulo")));

            InfraLocalDateTextField dateTextFieldInicio = componentFactory.newDateTextFieldWithDatePicker("dataInicio", "Período para " + titulo + " Início", true, null);
            dateTextFieldInicio.setEnabled(enabledDataInicio);
            add(dateTextFieldInicio);

            InfraLocalDateTextField dateTextFieldFim = componentFactory.newDateTextFieldWithDatePicker("dataFim", "Período para " + titulo + " Fim", true, null);
            dateTextFieldFim.setEnabled(enabledDataFim);
            add(dateTextFieldFim);
        }
    }

    private class ButtonsPanel extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public ButtonsPanel(String id) {
            super(id);

            add(newButtonExportarPdf());
            add(newButtonPublicar());
            add(newButtonVoltar());
        }

        private Button newButtonVoltar() {
            Button btn = componentFactory.newButton("btVoltar", () -> voltar());
            btn.setDefaultFormProcessing(false);
            return btn;
        }

        private void voltar() {
            setResponsePage(paginaAnalise);
        }

        private String getLabelPublicar() {
            if (isPublicacaoListaDefinitiva()) {
                return "Publicar Lista DEFINITIVA";
            }
            if (isPublicacaoListaPreliminar()) {
                return "Publicar Lista PRELIMINAR";
            }
            return "";

        }

        private Button newButtonPublicar() {
            
            AjaxButton btn = new AjaxButton("btnPublicar") {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    publicar(target);
                }
            };
            btn.setOutputMarkupId(true);
            btn.add(new Label("lbl", getLabelPublicar()));
            btn.setVisible(!isVisualizacaoListaDefinitiva());
            return btn;
        }

        private void publicar(AjaxRequestTarget target) {
            byte[] conteudo = getByteArrayExportarPdf();
            if (conteudo == null) {
                addMsgError("Não foi possível publicar, pois o sistema não gerou a lista em PDF.");
            } else {
                if (getTipoSelecao() != null) {
                    if (hasPermissionPublicacao()) {
                        if (EnumSelecao.PROPOSTAS_ELEGIVEIS.equals(getTipoSelecao())) {
                            publicizacaoService.publicarListaElegibilidade(getByteArrayExportarPdf(), dataInicio, dataFim, programa, getUsuarioLogadoDaSessao().getLogin());

                        }

                        if (EnumSelecao.CLASSIFICACAO_PROSPOSTA.equals(getTipoSelecao())) {

                            LocalDate iniRecurso = null;
                            LocalDate fimRecurso = null;
                            LocalDate iniLocalEntrega = null;
                            LocalDate fimLocalEntrega = null;
                            if (getTipoLista().equals(EnumTipoLista.PRELIMINAR)) {
                                iniRecurso = dataInicio;
                                fimRecurso = dataFim;
                            } else if (getTipoLista().equals(EnumTipoLista.DEFINITIVA)) {
                                iniLocalEntrega = dataInicio;
                                fimLocalEntrega = dataFim;
                            }
                            publicizacaoService.publicarListaAvaliacao(getByteArrayExportarPdf(), iniRecurso, fimRecurso, iniLocalEntrega, fimLocalEntrega, programa, getUsuarioLogadoDaSessao().getLogin(), getTipoLista());
                        }
                        mailService.enviarEmailPublicacaoLista(getPrograma(), getTipoSelecao(), getTipoLista());
                        getSession().info("Lista de propostas publicada com sucesso");
                        
                        AnaliseDto analiseDto = new AnaliseDto();
                        analiseDto.setAbaClicada(EnumAbaFaseAnalise.ELEGIBILIDADE);
                        setResponsePage(new PropostasEnviadasPage(new PageParameters(), programa, backPage,target,analiseDto,3));
                    } else {
                        addMsgError("Não é possível realizar a publicação da lista gerada. Existe(em) proposta(s) pendente(s) de análise.");
                    }
                }
            }
        }

        private boolean hasPermissionPublicacao() {
            boolean publicar = true;
            PermissaoProgramaDto permissao = publicizacaoService.buscarPermissoesPrograma(programa);
            if (EnumSelecao.PROPOSTAS_ELEGIVEIS.equals(getTipoSelecao())) {
                if (permissao.getPublicarListaElegibilidade()) {
                    publicar = true;
                }
            } else if (EnumSelecao.CLASSIFICACAO_PROSPOSTA.equals(getTipoSelecao())) {
                if (permissao.getPublicarListaAvaliacao()) {
                    publicar = true;
                }
            }
            return publicar;
        }

        private EnumTipoLista getTipoLista() {
            EnumTipoLista tipoLista = null;
            if (isPublicacaoListaPreliminar()) {
                tipoLista = EnumTipoLista.PRELIMINAR;
            } else if (isPublicacaoListaDefinitiva()) {
                tipoLista = EnumTipoLista.DEFINITIVA;
            }
            return tipoLista;
        }

        private Button newButtonExportarPdf() {
            Button btn = componentFactory.newButton("btnExportarPdf", () -> exportarPdf());
            btn.setDefaultFormProcessing(false);
            return btn;
        }

        private void exportarPdf() {
            ResourceRequestHandler handler = null;
            if (isListaClassificacao()) {
                InscricaoListClassificacaoReportBuilder builder = new InscricaoListClassificacaoReportBuilder(getSubTitulo() + " (NÃO PUBLICADA)");
                handler = new ResourceRequestHandler(builder.exportToPdf(inscricaoProgramaService.gerarListaClassificacaoAvaliacaoSemPaginacao(programa)), getPageParameters());
            }
            if (isListaElegibilidade()) {
                InscricaoListReportBuilder builder = new InscricaoListReportBuilder(getSubTitulo() + " (NÃO PUBLICADA)");
                handler = new ResourceRequestHandler(builder.exportToPdf(inscricaoProgramaService.gerarListaClassificacaoElegibilidadeSemPaginacao(programa,EnumStatusInscricao.CONCLUIDA_ANALISE_ELEGIBILIDADE)), getPageParameters());
            }
            if (handler != null) {
                getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
            } else {
                addMsgError("Erro ao exportar lista para PDF");
            }
        }

    }

    private List<InscricaoPrograma> getListaInscricoesParaPublicar() {
        if (isListaElegibilidade()) {
            return inscricaoProgramaService.gerarListaClassificacaoElegibilidadeSemPaginacao(programa,EnumStatusInscricao.CONCLUIDA_ANALISE_ELEGIBILIDADE);
        } else {
            if (isListaClassificacao()) {
                return inscricaoProgramaService.gerarListaClassificacaoAvaliacaoSemPaginacao(programa);
            }
        }
        return Collections.emptyList();
    }

    private byte[] getByteArrayExportarPdf() {
        byte[] conteudo = null;

        if (isListaClassificacao()) {
            InscricaoListClassificacaoReportBuilder builder = new InscricaoListClassificacaoReportBuilder(getSubTitulo());
            JRConcreteResource<PdfResourceHandler> jr = builder.exportToPdf(getListaInscricoesParaPublicar());
            conteudo = builder.getByteArray(jr);
        }
        if (isListaElegibilidade()) {
            InscricaoListReportBuilder builder = new InscricaoListReportBuilder(getSubTitulo());
            JRConcreteResource<PdfResourceHandler> jr = builder.exportToPdf(getListaInscricoesParaPublicar());
            conteudo = builder.getByteArray(jr);
        }
        return conteudo;
    }

    private boolean isPublicacaoListaPreliminar() {
        if (isListaElegibilidade()) {
            return listasPublicadasElegibilidade.isEmpty();
        }
        if (isListaClassificacao()) {
            return listaAvaliacaoPublicados.isEmpty();
        }
        return false;
    }

    private boolean isPublicacaoListaDefinitiva() {
        if (isListaElegibilidade()) {
            return listasPublicadasElegibilidade.size() == 1;
        }
        if (isListaClassificacao()) {
            return listaAvaliacaoPublicados.size() == 1;
        }
        return false;
    }

    private boolean isVisualizacaoListaDefinitiva() {
        if (isListaElegibilidade()) {
            return listasPublicadasElegibilidade.size() == 2;
        }
        if (isListaClassificacao()) {
            return listaAvaliacaoPublicados.size() == 2;
        }
        return false;
    }

    public EnumSelecao getTipoSelecao() {
        return tipoSelecao;
    }

    public void setTipoSelecao(EnumSelecao tipoSelecao) {
        this.tipoSelecao = tipoSelecao;
    }

    private class GridListaElegibilidadePanel extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public GridListaElegibilidadePanel(String id) {
            super(id);

            dataView = newDataViewPropostasElegiveis();
            add(dataView);
            add(new InfraAjaxPagingNavigator("pagination", dataView));
        }
    }

    private class GridListaAvaliacaoPanel extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public GridListaAvaliacaoPanel(String id) {
            super(id);

            dataView = newDataViewClassificacaoPropostas();
            add(dataView);
            add(new InfraAjaxPagingNavigator("pagination", dataView));
        }
    }

    private boolean isListaClassificacao() {
        return EnumSelecao.CLASSIFICACAO_PROSPOSTA.equals(getTipoSelecao());
    }

    private boolean isListaElegibilidade() {
        return EnumSelecao.PROPOSTAS_ELEGIVEIS.equals(getTipoSelecao());
    }

    public String getSubTitulo() {
        return subTitulo;
    }

    public void setSubTitulo(String subTitulo) {
        this.subTitulo = subTitulo;
    }

    public Label getLblSubTitulo() {
        return lblSubTitulo;
    }

    public void setLblSubTitulo(Label lblSubTitulo) {
        this.lblSubTitulo = lblSubTitulo;
    }
}
