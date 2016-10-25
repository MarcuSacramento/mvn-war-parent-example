package br.gov.mj.side.web.view.consultaPublica;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.IConverter;
import org.wicketstuff.jasperreports.JRConcreteResource;
import org.wicketstuff.jasperreports.handlers.PdfResourceHandler;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.converter.MoneyBigDecimalConverter;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaHistoricoPublicizacao;
import br.gov.mj.side.web.dto.PermissaoProgramaDto;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.service.PublicizacaoService;
import br.gov.mj.side.web.util.RelatorioConsultaPublicaBuilder;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.components.converters.NumeroProcessoSeiConverter;
import br.gov.mj.side.web.view.programa.visualizacao.ProgramaAnexoVisualizarPanel;
import br.gov.mj.side.web.view.programa.visualizacao.ProgramaAnexosPublicadosPanel;
import br.gov.mj.side.web.view.programa.visualizacao.ProgramaBemKitPanel;
import br.gov.mj.side.web.view.programa.visualizacao.ProgramaCriterioAcompanhamentoPanel;
import br.gov.mj.side.web.view.programa.visualizacao.ProgramaCriterioAvaliacaoPanel;
import br.gov.mj.side.web.view.programa.visualizacao.ProgramaCriterioElegebilidadePanel;
import br.gov.mj.side.web.view.programa.visualizacao.ProgramaOrgaoExecutorPanel;
import br.gov.mj.side.web.view.programa.visualizacao.ProgramaPotenciaisBeneficiariosPanel;
import br.gov.mj.side.web.view.programa.visualizacao.ProgramaRecursoFinanceiroPanel;
import br.gov.mj.side.web.view.template.TemplatePage;

public class ConsultaPublicaPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    @Inject
    private ProgramaService programaService;
    @Inject
    private PublicizacaoService publicizacaoService;
    @Inject
    private ComponentFactory componentFactory;

    private Programa programa;
    private PermissaoProgramaDto permissaoPrograma;
    private PanelInformacoesGerais panelInformacoesGerais;
    private ProgramaRecursoFinanceiroPanel recursoFinanceiroPanel;
    private ProgramaOrgaoExecutorPanel orgaoExecutorPanel;
    private ProgramaPotenciaisBeneficiariosPanel potenciaisBeneficiariosPanel;
    private ProgramaBemKitPanel bemKitPanel;
    private ProgramaCriterioElegebilidadePanel criterioElegibilidadePanel;
    private ProgramaCriterioAcompanhamentoPanel criterioAcompanhamentoPanel;
    private ProgramaAnexoVisualizarPanel panelVisualizarAnexos;
    private PanelBotoes panelBotoes;
    private ProgramaCriterioAvaliacaoPanel criterioAvaliacaoPanel;
    private ProgramaAnexosPublicadosPanel programaAnexosPublicadosPanel;

    private Page backPage;

    private Form<Programa> form;

    public ConsultaPublicaPage(final PageParameters pageParameters, Programa programa, Page backPage) {
        super(pageParameters);
        this.backPage = backPage;
        this.programa = programaService.buscarPeloId(programa.getId());
        permissaoPrograma = publicizacaoService.buscarPermissoesPrograma(programa);

        permissaoPrograma = publicizacaoService.buscarPermissoesPrograma(programa);
        this.programa.setHistoricoPublicizacao(programaService.buscarHistoricoPublicizacao(programa));

        setTitulo("Programa: ".concat(programa.getNomePrograma()));
        initEntity();
        initComponents();
        criarBreadcrumb();
    }

    private void initEntity() {
        programa.setCriteriosAcompanhamento(programaService.buscarProgramaCriterioAcompanhamento(programa));
        programa.setCriteriosElegibilidade(programaService.buscarProgramaCriterioElegibilidade(programa));
        programa.setProgramaBens(programaService.buscarProgramaBem(programa));
        programa.setProgramaKits(programaService.buscarProgramakit(programa));
        programa.setPotenciaisBeneficiariosUf(programaService.buscarProgramaPotencialBeneficiarioUf(programa));
        programa.setRecursosFinanceiros(programaService.buscarProgramaRecursoFinanceiroPeloIdPrograma(programa));
        programa.setCriteriosAvaliacao(programaService.buscarProgramaCriterioAvaliacao(programa));
    }

    private void initComponents() {

        form = componentFactory.newForm("form", new CompoundPropertyModel<Programa>(programa));

        panelInformacoesGerais = new PanelInformacoesGerais("panelInformacoesGerais");
        form.add(panelInformacoesGerais);

        recursoFinanceiroPanel = new ProgramaRecursoFinanceiroPanel("recursoFinanceiroPanel", programa);
        form.add(recursoFinanceiroPanel);

        orgaoExecutorPanel = new ProgramaOrgaoExecutorPanel("orgaoExecutorPanel");
        form.add(orgaoExecutorPanel);

        potenciaisBeneficiariosPanel = new ProgramaPotenciaisBeneficiariosPanel("potenciaisBeneficiariosPanel", programa);
        form.add(potenciaisBeneficiariosPanel);

        bemKitPanel = new ProgramaBemKitPanel("bemKitPanel", programa);
        form.add(bemKitPanel);

        criterioElegibilidadePanel = new ProgramaCriterioElegebilidadePanel("criterioElegibilidadePanel", programa);
        form.add(criterioElegibilidadePanel);

        criterioAvaliacaoPanel = new ProgramaCriterioAvaliacaoPanel("criterioAvaliacaoPanel", programa);
        form.add(criterioAvaliacaoPanel);

        criterioAcompanhamentoPanel = new ProgramaCriterioAcompanhamentoPanel("criterioAcompanhamentoPanel", programa);
        form.add(criterioAcompanhamentoPanel);

        panelVisualizarAnexos = new ProgramaAnexoVisualizarPanel("panelPrincipalAnexo", programa);
        form.add(panelVisualizarAnexos);

        if (panelVisualizarAnexos.getListAnexos().isEmpty()) {
            panelVisualizarAnexos.setVisible(false);
        } else {
            panelVisualizarAnexos.setVisible(true);
        }

        programaAnexosPublicadosPanel = new ProgramaAnexosPublicadosPanel("programaAnexosPublicadosPanel", programa);
        form.add(programaAnexosPublicadosPanel);

        panelBotoes = new PanelBotoes("panelBotoes");
        form.add(panelBotoes);

        add(form);
    }

    private void criarBreadcrumb() {
        form.add(new Link<Void>("consultaPublica") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(backPage);
            }
        });
    }

    public class PanelInformacoesGerais extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelInformacoesGerais(String id) {
            super(id);

            add(newLabelCodigoPrograma());
            add(newLabelNomePrograma());
            add(newLabelNomeFantasia());
            add(newLabelDescricao());
            add(newLabelAno());
            add(newLabelFuncao());
            add(newLabelSubfuncao());
            add(newLabelNumeroSEI());
            add(newLabelPeriodoRecebimentoPropostas());
            add(newLabelValorMaximoProposta());

        }
    }

    private class PanelBotoes extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBotoes(String id) {
            super(id);

            add(getButtonVoltar()); // btnVoltar
            add(getButtonRelatorio()); // btnPdf
        }
    }

    private Label newLabelCodigoPrograma() {
        return new Label("id", programa.getCodigoIdentificadorProgramaPublicado());
    }

    private Label newLabelNomePrograma() {
        return new Label("nomePrograma", programa.getNomePrograma());
    }

    private Label newLabelNomeFantasia() {
        return new Label("nomeFantasiaPrograma", programa.getNomeFantasiaPrograma());
    }

    private Label newLabelDescricao() {
        return new Label("descricaoPrograma", programa.getDescricaoPrograma());
    }

    private Label newLabelAno() {
        return new Label("anoPrograma", programa.getAnoPrograma());
    }

    private Label newLabelFuncao() {
        return new Label("subFuncao.funcao.nomeFuncao", programa.getSubFuncao().getFuncao().getNomeFuncao());
    }

    private Label newLabelSubfuncao() {
        return new Label("subFuncao.nomeSubFuncao", programa.getSubFuncao().getNomeSubFuncao());
    }

    private Label newLabelNumeroSEI() {
        return new Label("numeroProcessoSEI", programa.getNumeroProcessoSEI()) {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new NumeroProcessoSeiConverter();
            }
        };
    }

    private Label newLabelPeriodoRecebimentoPropostas() {

        String periodo = "";
        boolean visivel = false;
        List<ProgramaHistoricoPublicizacao> historico = programaService.buscarHistoricoPublicizacao(programa);

        if (!historico.isEmpty()) {
            if (historico.get(0).getDataInicialProposta() == null && historico.get(0).getDataFinalProposta() == null) {
                periodo += programa.getStatusPrograma().getDescricao();
            } else {
                periodo += dataBR(historico.get(0).getDataInicialProposta()) + " a " + dataBR(historico.get(0).getDataFinalProposta());
            }
            visivel = true;
        } else {
            periodo = " - ";
            visivel = false;
        }

        Label label = new Label("periodoRecebimento", periodo);
        label.setVisible(visivel);
        return label;
    }

    private Button getButtonVoltar() {
        return new Button("btnVoltar") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                voltar();
            }
        };
    }

    private Button getButtonRelatorio() {
        return componentFactory.newButton("btnPdf", () -> gerarPdf());
    }

    // AÇÕES

    private void gerarPdf() {

        List<Programa> lista = new ArrayList<Programa>();
        lista.add(programa);
        RelatorioConsultaPublicaBuilder builder = new RelatorioConsultaPublicaBuilder(programaService, SideUtil.getCaminhoCompletoRelatorios(getHttpServletRequest().getServletContext()));
        // Esta informação será utilizada para permitir que a tabela de
        // histórico seja mostrada
        // Esta tabela somente irá aparecer se não estiver sendo feita uma
        // consulta pública.

        builder.setMostrarHistorico(false);
        JRConcreteResource<PdfResourceHandler> relatorioResource = builder.export(lista);
        ResourceRequestHandler handler = new ResourceRequestHandler(relatorioResource, getPageParameters());
        RequestCycle requestCycle = getRequestCycle();
        requestCycle.scheduleRequestHandlerAfterCurrent(handler);

    }

    public void voltar() {
        setResponsePage(backPage);
    }

    public String dataBR(LocalDate dataDocumento) {
        DateTimeFormatter sdfPadraoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (dataDocumento != null) {
            dataDocumento.format(sdfPadraoBR);
            return sdfPadraoBR.format(dataDocumento);
        }
        return " - ";
    }

    // Método sobrescrito para remover autenticação na página.
    @Override
    protected void onConfigure() {

    }

    @Override
    protected boolean isMenuVisible() {
        Usuario usuarioLogado = getUsuarioLogadoDaSessao();
        if (usuarioLogado != null) {
            return true;
        } else {
            return false;
        }
    }

    private Label newLabelValorMaximoProposta() {
        return new Label("valorMaximoProposta") {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
    }
}
