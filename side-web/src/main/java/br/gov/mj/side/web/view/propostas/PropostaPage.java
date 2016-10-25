package br.gov.mj.side.web.view.propostas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumAbaFaseAnalise;
import br.gov.mj.side.entidades.enums.EnumResultadoFinalAnaliseElegibilidade;
import br.gov.mj.side.entidades.enums.EnumStatusInscricao;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaCriterioAvaliacao;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaCriterioElegibilidade;
import br.gov.mj.side.entidades.programa.inscricao.analise.HistoricoAnaliseAvaliacao;
import br.gov.mj.side.entidades.programa.inscricao.analise.HistoricoAnaliseElegibilidade;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaAvaliacaoPublicado;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaElegibilidadePublicado;
import br.gov.mj.side.web.dto.AnaliseDto;
import br.gov.mj.side.web.service.InscricaoProgramaAvaliacaoService;
import br.gov.mj.side.web.service.InscricaoProgramaElegibilidadeService;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.ListaPublicadoService;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.programa.PanelFasePrograma;
import br.gov.mj.side.web.view.template.TemplatePage;

import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;

@AuthorizeInstantiation({ PropostaPage.ROLE_MANTER_ANALISE_VISUALIZAR, PropostaPage.ROLE_MANTER_ANALISE_ELEGIBILIDADE, PropostaPage.ROLE_MANTER_ANALISE_AVALIACAO })
public class PropostaPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    public static final String ROLE_MANTER_ANALISE_VISUALIZAR = "manter_analise:visualizar";
    public static final String ROLE_MANTER_ANALISE_ELEGIBILIDADE = "manter_analise:analisar_elegibilidade";
    public static final String ROLE_MANTER_ANALISE_AVALIACAO = "manter_analise:analisar_avaliacao";

    private Page backPage;

    private Form<InscricaoPrograma> form;
    private Button buttonSalvarElegibilidade;
    private Button buttonSalvarAvaliacao;
    private Button buttonConcluirElegibilidade;
    private Button buttonConcluirAvaliacao;
    private AjaxButton buttonAbrirCriteriosElebilidade;
    private AjaxButton buttonAbrirCriteriosAvaliacao;
    private AjaxButton buttonAbrirAnexos;

    private Programa programa = new Programa();
    private Entidade entidade = new Entidade();
    private InscricaoPrograma inscricaoPrograma = new InscricaoPrograma();

    private Boolean readOnly;
    private Boolean mostrarPanelElegibilidade = true;
    private Boolean mostrarPanelValidacao = false;
    private Boolean mostrarPanelAnexo = false;
    private Boolean mostrarGuiaAvaliacao = false;
    private Boolean analiseElegibilidadeJaEnviada = false;
    private String botaoClicado = "elegibilidade";
    private AnaliseDto analiseDto;

    private DadosProgramaPropostaPanel dadosProgramaPropostaPanel;
    private DadosEntidadePropostaPanel dadosEntidadePropostaPanel;
    private DadosInscricaoPropostaPanel dadosInscricaoPropostaPanel;
    private DadosPropostaElebigilidadePanel dadosPropostaElebigilidadePanel;
    private DadosPropostaValidacaoPanel dadosPropostaValidacaoPanel;
    private PanelAnexoProposta panelAnexoProposta;
    private PanelBotoesSalvarConcluir panelBotoesSalvarConcluir;
    private PanelBotoesGuia panelBotoesGuia;
    private PanelComAsGuias panelComAsGuias;
    private PanelFasePrograma panelFasePrograma;

    private AttributeModifier classClicado = new AttributeModifier("class", "btn btn-info btn-sm custom");
    private AttributeModifier classNaoclicado = new AttributeModifier("class", "btn btn-default btn-sm custom");

    private List<ListaElegibilidadePublicado> listaElegibilidade;
    private List<ListaAvaliacaoPublicado> listaAvaliacao = new ArrayList<ListaAvaliacaoPublicado>();

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private InscricaoProgramaService inscricaoService;

    @Inject
    private InscricaoProgramaElegibilidadeService elegibilidadeService;

    @Inject
    private InscricaoProgramaAvaliacaoService avaliacaoService;

    @Inject
    private ListaPublicadoService listaPublicadoService;

    public PropostaPage(final PageParameters pageParameters, InscricaoPrograma inscricaoPrograma, AnaliseDto analiseDto, Page backPage, Boolean readOnly) {
        super(pageParameters);
        this.backPage = backPage;
        this.inscricaoPrograma = inscricaoPrograma;
        this.readOnly = readOnly;
        this.analiseDto = analiseDto;

        String titulo = "";
        if (readOnly) {
            titulo = "Visualizar Proposta";
        } else {
            titulo = "Analisar Proposta";
        }
        setTitulo(titulo);

        initVariaveis();
        initComponents();
        mostrarGuiaDeCriteriosAvaliacao();
    }

    private class PanelBotoesGuia extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBotoesGuia(String id) {
            super(id);

            setOutputMarkupId(true);
            
            add(newButtonAbrirCriteriosElebilidade()); // btnAbrirCriteriosElebilidade
            add(newButtonAbrirCriteriosAvaliacao()); // btnAbrirCriteriosAvaliacao
            add(newButtonAbrirAnexos()); // btnAbrirAnexos

        }
    }

    private class PanelBotoesSalvarConcluir extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelBotoesSalvarConcluir(String id) {
            super(id);

            add(newButtonSalvarElegibilidade()); // btnSalvarElegibilidade
            add(newButtonConcluirElegibilidade()); // btnConcluirElebilidade
            add(newButtonSalvarAvalicao()); // btnSalvarAvaliacao
            add(newButtonConcluirAvaliacao()); // btnConcluirAvaliacao
            add(newButtonVoltar()); // btnVoltar
        }
    }

    private class PanelComAsGuias extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelComAsGuias(String id) {
            super(id);
            setOutputMarkupId(true);

            dadosPropostaElebigilidadePanel = new DadosPropostaElebigilidadePanel("dadosPropostaElebigilidadePanel", getInscricaoPrograma(), readOnly);
            dadosPropostaValidacaoPanel = new DadosPropostaValidacaoPanel("dadosPropostaValidacaoPanel", getInscricaoPrograma(), readOnly);
            panelAnexoProposta = new PanelAnexoProposta("panelAnexoProposta", getInscricaoPrograma(), readOnly);

            add(dadosPropostaElebigilidadePanel);
            add(dadosPropostaValidacaoPanel);
            add(panelAnexoProposta);
        }
    }

    private void initVariaveis() {
        entidade = inscricaoPrograma.getPessoaEntidade().getEntidade();
        listaElegibilidade = SideUtil.convertAnexoDtoToEntityListaElegibilidadePublicado(listaPublicadoService.buscarListaElegibilidadePublicadoPeloIdPrograma(inscricaoPrograma.getPrograma().getId()));
        listaAvaliacao = SideUtil.convertAnexoDtoToEntityListaAvaliacaoPublicado(listaPublicadoService.buscarListaAvaliacaoPublicadoPeloIdPrograma(inscricaoPrograma.getPrograma().getId()));
        
        if(analiseDto.getAbaClicada() == EnumAbaFaseAnalise.ELEGIBILIDADE){
            botaoClicado = "elegibilidade";
        }else{
            botaoClicado = "avaliacao";
        }
    }

    private void initComponents() {
        form = componentFactory.newForm("form", inscricaoPrograma);
        form.setMultiPart(true);
        add(form);

        panelFasePrograma = new PanelFasePrograma("panelFasePrograma", inscricaoPrograma.getPrograma(), backPage, 3);

        dadosProgramaPropostaPanel = new DadosProgramaPropostaPanel("dadosProgramaPropostaPanel", getInscricaoPrograma().getPrograma());
        dadosEntidadePropostaPanel = new DadosEntidadePropostaPanel("dadosEntidadePropostaPanel", getEntidade());
        dadosInscricaoPropostaPanel = new DadosInscricaoPropostaPanel("dadosInscricaoPropostaPanel", getInscricaoPrograma());
        panelBotoesSalvarConcluir = new PanelBotoesSalvarConcluir("panelBotoesSalvarConcluir");
        panelComAsGuias = new PanelComAsGuias("panelComAsGuias");
        panelBotoesGuia = new PanelBotoesGuia("panelBotoesGuias");

        /*
         * String titulo = ""; if (readOnly) { titulo = "Visualizar Proposta"; }
         * else { titulo = "Analisar Proposta"; } Label label = new
         * Label("lblTitulo", titulo); setTitulo(titulo);
         */

        form.add(panelFasePrograma);
        form.add(dadosEntidadePropostaPanel);
        form.add(dadosProgramaPropostaPanel);
        form.add(dadosInscricaoPropostaPanel);
        form.add(panelComAsGuias);
        form.add(panelBotoesGuia);
        form.add(panelBotoesSalvarConcluir);

        // form.add(label);

        isSomenteLeitura();
    }

    // Componentes

    private Button newButtonAbrirCriteriosElebilidade() {
        buttonAbrirCriteriosElebilidade = new AjaxButton("btnAbrirCriteriosElebilidade") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                acaoBotaoCriterioElegibilidade(target);
            }
        };

        buttonAbrirCriteriosElebilidade.setOutputMarkupId(true);
        buttonAbrirCriteriosElebilidade.add(corBotaoClicado("elegibilidade"));
        return buttonAbrirCriteriosElebilidade;
    }

    private Button newButtonAbrirCriteriosAvaliacao() {
        buttonAbrirCriteriosAvaliacao = new AjaxButton("btnAbrirCriteriosAvaliacao") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                acaoBotaoCriterioAvaliacao(target);
            }
        };
        buttonAbrirCriteriosAvaliacao.setOutputMarkupId(true);
        //buttonAbrirCriteriosAvaliacao.setVisible(mostrarGuiaDeCriteriosAvaliacao());
        buttonAbrirCriteriosAvaliacao.add(corBotaoClicado("avaliacao"));
        return buttonAbrirCriteriosAvaliacao;
    }

    private Button newButtonAbrirAnexos() {
        buttonAbrirAnexos = new AjaxButton("btnAbrirAnexos") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                acaoBotaoAnexo(target);
            }
        };
        buttonAbrirAnexos.add(corBotaoClicado("anexo"));
        buttonAbrirAnexos.setOutputMarkupId(true);
        return buttonAbrirAnexos;
    }

    private Button newButtonSalvarElegibilidade() {
        buttonSalvarElegibilidade = componentFactory.newButton("btnSalvarElegibilidade", () -> actionSalvarElebilidade());
        buttonSalvarElegibilidade.setOutputMarkupId(true);
        buttonSalvarElegibilidade.setVisible(mostrarSalvarConcluirElegibilidade());
        buttonSalvarElegibilidade.setEnabled(acionarBotaoSalvarElegibilidade());
        return buttonSalvarElegibilidade;
    }

    private Button newButtonConcluirElegibilidade() {
        buttonConcluirElegibilidade = componentFactory.newButton("btnConcluirElebilidade", () -> actionConcluirElebilidade());
        buttonConcluirElegibilidade.setOutputMarkupId(true);
        buttonConcluirElegibilidade.setVisible(mostrarSalvarConcluirElegibilidade());
        buttonConcluirElegibilidade.setEnabled(ativarBotaoConcluirElegibilidade());
        return buttonConcluirElegibilidade;
    }

    private Button newButtonSalvarAvalicao() {
        buttonSalvarAvaliacao = componentFactory.newButton("btnSalvarAvaliacao", () -> actionSalvarAvaliacao());
        buttonSalvarAvaliacao.setOutputMarkupId(true);
        buttonSalvarAvaliacao.setVisible(mostrarSalvarConcluirAvaliacao());
        buttonSalvarAvaliacao.setEnabled(acionarBotaoSalvarAvaliacao());
        return buttonSalvarAvaliacao;
    }

    private Button newButtonConcluirAvaliacao() {
        buttonConcluirAvaliacao = componentFactory.newButton("btnConcluirAvaliacao", () -> actionConcluirAvaliacao());
        buttonConcluirAvaliacao.setOutputMarkupId(true);
        buttonConcluirAvaliacao.setVisible(mostrarSalvarConcluirAvaliacao());
        buttonConcluirAvaliacao.setEnabled(ativarBotaoConcluirAvaliacao());
        return buttonConcluirAvaliacao;
    }

    private Button newButtonVoltar() {
        
        AjaxButton button = new AjaxButton("btnVoltar") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                actionVoltar(target);
            }
        };
        button.setOutputMarkupId(true);
        button.setDefaultFormProcessing(false);
        button.setVisible(mostrarBotaoVoltar());
        return button;
    }


    // AÇÕES
    
    private boolean mostrarBotaoVoltar(){
        if(botaoClicado.equalsIgnoreCase("anexo")){
            return false;
        }else{
            return true;
        }
    }

    private void acaoBotaoCriterioElegibilidade(AjaxRequestTarget target) {
        mostrarPanelElegibilidade = true;
        mostrarPanelValidacao = false;
        mostrarPanelAnexo = false;
        botaoClicado = "elegibilidade";

        dadosPropostaElebigilidadePanel.setVisible(mostrarPanelElegibilidade);
        dadosPropostaValidacaoPanel.setVisible(mostrarPanelValidacao);
        panelAnexoProposta.setVisible(mostrarPanelAnexo);

        atualizarBotoes(target);
        target.add(panelComAsGuias);
    }

    private void acaoBotaoCriterioAvaliacao(AjaxRequestTarget target) {
        mostrarPanelElegibilidade = false;
        mostrarPanelValidacao = true;
        mostrarPanelAnexo = false;
        botaoClicado = "avaliacao";

        dadosPropostaElebigilidadePanel.setVisible(mostrarPanelElegibilidade);
        dadosPropostaValidacaoPanel.setVisible(mostrarPanelValidacao);
        panelAnexoProposta.setVisible(mostrarPanelAnexo);

        atualizarBotoes(target);
        target.add(panelComAsGuias);
    }

    private void acaoBotaoAnexo(AjaxRequestTarget target) {
        mostrarPanelElegibilidade = false;
        mostrarPanelValidacao = false;
        mostrarPanelAnexo = true;
        botaoClicado = "anexo";

        dadosPropostaElebigilidadePanel.setVisible(mostrarPanelElegibilidade);
        dadosPropostaValidacaoPanel.setVisible(mostrarPanelValidacao);
        panelAnexoProposta.setVisible(mostrarPanelAnexo);

        atualizarBotoes(target);

        panelComAsGuias.addOrReplace(dadosPropostaElebigilidadePanel);
        panelComAsGuias.addOrReplace(dadosPropostaValidacaoPanel);
        panelComAsGuias.addOrReplace(panelAnexoProposta);
        target.add(panelComAsGuias);
    }

    private void atualizarBotoes(AjaxRequestTarget target) {
        panelBotoesSalvarConcluir.addOrReplace(newButtonSalvarElegibilidade());
        panelBotoesSalvarConcluir.addOrReplace(newButtonSalvarAvalicao());
        panelBotoesSalvarConcluir.addOrReplace(newButtonConcluirElegibilidade());
        panelBotoesSalvarConcluir.addOrReplace(newButtonConcluirAvaliacao());
        panelBotoesSalvarConcluir.addOrReplace(newButtonVoltar());

        panelBotoesGuia.addOrReplace(newButtonAbrirAnexos());
        panelBotoesGuia.addOrReplace(newButtonAbrirCriteriosAvaliacao());
        panelBotoesGuia.addOrReplace(newButtonAbrirCriteriosElebilidade());

        target.add(panelBotoesSalvarConcluir);
        target.add(panelBotoesGuia);
    }

    private boolean mostrarSalvarConcluirElegibilidade() {
        boolean mostrar = true;
        if (botaoClicado.equalsIgnoreCase("anexo")) {
            mostrar = false;
        } else {
            mostrar = botaoClicado.equalsIgnoreCase("elegibilidade");
        }
        return mostrar;
    }

    private boolean mostrarSalvarConcluirAvaliacao() {
        boolean mostrar = true;
        if (botaoClicado.equalsIgnoreCase("anexo")) {
            mostrar = false;
        } else {
            mostrar = botaoClicado.equalsIgnoreCase("avaliacao");
        }
        return mostrar;
    }

    private boolean acionarBotaoSalvarElegibilidade() {
        boolean mostrar = true;
        if (readOnly) {
            mostrar = false;
        } else {
            if (inscricaoPrograma.getStatusInscricao() != null) {
                EnumStatusInscricao status = inscricaoPrograma.getStatusInscricao();
                if (status == EnumStatusInscricao.ENVIADA_ANALISE || status == EnumStatusInscricao.ANALISE_ELEGIBILIDADE) {
                    mostrar = true;
                } else {
                    mostrar = false;
                }
            } else {
                mostrar = true;
            }
        }
        return mostrar;
    }

    private boolean acionarBotaoSalvarAvaliacao() {
        boolean mostrar = true;
        if (readOnly) {
            mostrar = false;
        } else {
            if (inscricaoPrograma.getStatusInscricao() != null) {
                EnumStatusInscricao status = inscricaoPrograma.getStatusInscricao();
                if (status == EnumStatusInscricao.CONCLUIDA_ANALISE_ELEGIBILIDADE || status == EnumStatusInscricao.ANALISE_AVALIACAO) {
                    mostrar = true;
                } else {
                    mostrar = false;
                }
            } else {
                mostrar = true;
            }
        }
        return mostrar;
    }

    private boolean ativarBotaoConcluirElegibilidade() {
        boolean mostrar = true;

        if (readOnly) {
            mostrar = false;
        } else {
            if (inscricaoPrograma.getStatusInscricao() != null) {
                EnumStatusInscricao status = inscricaoPrograma.getStatusInscricao();
                if (status == EnumStatusInscricao.ENVIADA_ANALISE || status == EnumStatusInscricao.ANALISE_ELEGIBILIDADE) {
                    mostrar = true;
                } else {
                    if (isGeradaSegundaListaDeElegibilidade()) {
                        mostrar = false;
                    } else {
                        mostrar = true;
                    }
                }
            } else {
                mostrar = true;
            }
        }
        return mostrar;
    }

    private boolean ativarBotaoConcluirAvaliacao() {
        boolean mostrar = true;

        if (readOnly) {
            mostrar = false;
        } else {
            mostrar = !isGeradaSegundaListaDeAvaliacao();
        }
        return mostrar;
    }

    private void isSomenteLeitura() {
        if (readOnly) {
            buttonConcluirElegibilidade.setEnabled(false);
            buttonConcluirAvaliacao.setEnabled(false);
            buttonSalvarElegibilidade.setEnabled(false);
            buttonSalvarAvaliacao.setEnabled(false);
        }
    }

    private void actionSalvarElebilidade() {
        if (!validarCriteriosElebilidade()) {
            return;
        }
        montarCriteriosElegibilidade();
        InscricaoPrograma inscricao = elegibilidadeService.salvar(inscricaoPrograma, getIdentificador());

        getSession().info("Salvo com sucesso.");
        setResponsePage(new PropostaPage(getPageParameters(), inscricao,analiseDto, backPage, false));
    }

    private void actionConcluirElebilidade() {
        if (!validarAnaliseFinalElegibilidade(validarCriteriosElebilidade())) {
            return;
        }
        montarCriteriosElegibilidade();
        InscricaoPrograma inscricao = elegibilidadeService.concluir(inscricaoPrograma, getIdentificador());

        getSession().info("Salvo com sucesso.");
        setResponsePage(new PropostaPage(getPageParameters(), inscricao, analiseDto, backPage, false));
    }

    private void actionSalvarAvaliacao() {
        if (!validarCriteriosValidacao()) {
            return;
        }

        montarCriteriosAvaliacao();
        InscricaoPrograma inscricao = avaliacaoService.salvar(inscricaoPrograma, getIdentificador());
        getSession().info("Salvo com sucesso.");
        setResponsePage(new PropostaPage(getPageParameters(), inscricao,analiseDto, backPage, false));
    }

    private void actionConcluirAvaliacao() {
        if (!validarAnaliseFinalClassificacao(validarCriteriosValidacao())) {
            return;
        }

        montarCriteriosAvaliacao();
        InscricaoPrograma inscricao = avaliacaoService.concluir(inscricaoPrograma, getIdentificador());

        getSession().info("Salvo com sucesso.");
        setResponsePage(new PropostaPage(getPageParameters(), inscricao,analiseDto, backPage, false));
    }

    private void actionVoltar(AjaxRequestTarget target) {
        
        setResponsePage(new PropostasEnviadasPage(new PageParameters(), inscricaoPrograma.getPrograma(), backPage, target,analiseDto, 3));
    }

    private boolean validarCriteriosElebilidade() {
        boolean validar = true;
        validar = validarCriteriosElegibilidade(validar);

        return validar;
    }

    private boolean validarCriteriosElegibilidade(boolean validar) {
        List<InscricaoProgramaCriterioElegibilidade> lista = new ArrayList<InscricaoProgramaCriterioElegibilidade>();
        lista = dadosPropostaElebigilidadePanel.getListaCriterios();

        for (InscricaoProgramaCriterioElegibilidade ice : lista) {
            if (ice.getAceitaCriterioElegibilidade() == null) {
                addMsgError("O campo 'Criterio Aceito ' do critério de Elegibilidade '" + ice.getProgramaCriterioElegibilidade().getNomeCriterioElegibilidade() + "' não foi especificado.");
                validar = false;
            } else {
                if (!ice.getAceitaCriterioElegibilidade() && (ice.getDescricaoMotivo() == null || "".equalsIgnoreCase(ice.getDescricaoMotivo()))) {
                    addMsgError("O campo 'Motivo' da Análise de Elegibilidade '" + ice.getProgramaCriterioElegibilidade().getNomeCriterioElegibilidade() + "' é obrigatório já que o critério não foi aceito.");
                    validar = false;
                }
            }
        }
        return validar;
    }

    private boolean validarAnaliseFinalElegibilidade(boolean validar) {
        HistoricoAnaliseElegibilidade historico = dadosPropostaElebigilidadePanel.getHistoricoElegibilidade();
        if (dadosPropostaElebigilidadePanel.getAlteradoResultadoAnalise()) {
            if (historico.getResultadoFinalAnalise() != null && historico.getDescricaoJustificativa() == null || "".equalsIgnoreCase(historico.getDescricaoJustificativa())) {
                addMsgError("O campo 'Justificativa' da Análise de Elegibilidade é obrigatório caso seja informado um Resultado Final.");
                validar = false;
            }

            if (historico.getMotivoAnalise() == null) {
                addMsgError("O campo 'Motivo' da Análise de Elegibilidade é obrigatório.");
                validar = false;
            }
        }

        return validar;
    }

    private boolean validarCriteriosValidacao() {
        boolean validar = true;
        validar = validarCriteriosValidacao(validar);
        return validar;
    }

    private boolean validarCriteriosValidacao(boolean validar) {
        List<InscricaoProgramaCriterioAvaliacao> lista = new ArrayList<InscricaoProgramaCriterioAvaliacao>();
        lista = dadosPropostaValidacaoPanel.getListaCriterios();

        for (InscricaoProgramaCriterioAvaliacao ipc : lista) {
            if (ipc.getAceitaCriterioAvaliacao() == null) {
                addMsgError("O campo 'Critério Aceito' da Análise de Avaliação '" + ipc.getProgramaCriterioAvaliacao().getNomeCriterioAvaliacao() + "' não foi especificado.");
                ipc.setNotaCriterio(0);
                validar = false;
            } else {
                if (!ipc.getAceitaCriterioAvaliacao() && (ipc.getDescricaoMotivo() == null || "".equalsIgnoreCase(ipc.getDescricaoMotivo()))) {
                    addMsgError("O campo 'Motivo' da Análise de Avaliação '" + ipc.getProgramaCriterioAvaliacao().getNomeCriterioAvaliacao() + "' é obrigatório já que o critério não foi aceito.");
                    validar = false;
                }
            }
        }
        return validar;
    }

    private boolean validarAnaliseFinalClassificacao(boolean validar) {

        HistoricoAnaliseAvaliacao historico = retornaHistoricoAvaliacao();
        if (dadosPropostaValidacaoPanel.getAlteradoResultadoAnalise()) {
            if (historico.getPontuacaoFinal() != null && historico.getDescricaoJustificativa() == null || "".equalsIgnoreCase(historico.getDescricaoJustificativa())) {
                addMsgError("O campo 'Justificativa' da Análise de Avaliação é obrigatório caso seja inserida uma Pontuação Final.");
                validar = false;
            }

            if (historico.getMotivoAnalise() == null) {
                addMsgError("O campo 'Motivo' da Análise de Avaliação é obrigatório.");
                validar = false;
            }
        }

        return validar;
    }

    private void montarCriteriosElegibilidade() {
        inscricaoPrograma.setProgramasCriterioElegibilidade(dadosPropostaElebigilidadePanel.getListaCriterios());
        inscricaoPrograma.setMotivoAnaliseElegibilidade(dadosPropostaElebigilidadePanel.getHistoricoElegibilidade().getMotivoAnalise());
        inscricaoPrograma.setResultadoFinalAnaliseElegibilidade(dadosPropostaElebigilidadePanel.getHistoricoElegibilidade().getResultadoFinalAnalise());
        inscricaoPrograma.setDescricaoJustificativaElegibilidade(dadosPropostaElebigilidadePanel.getHistoricoElegibilidade().getDescricaoJustificativa());

        if (inscricaoPrograma.getNumeroProcessoSEIRecursoElegibilidade() != null) {
            inscricaoPrograma.setNumeroProcessoSEIRecursoElegibilidade(limparCampos(inscricaoPrograma.getNumeroProcessoSEIRecursoElegibilidade()));
        }

        inscricaoPrograma.setAnexos(panelAnexoProposta.getListAnexoTemp());

        String resultadoAnalise = dadosPropostaElebigilidadePanel.getResultadoAnalise();
        if (inscricaoPrograma.getResultadoFinalAnaliseElegibilidade() == null) {
            List<EnumResultadoFinalAnaliseElegibilidade> result = Arrays.asList(EnumResultadoFinalAnaliseElegibilidade.values());
            for (EnumResultadoFinalAnaliseElegibilidade rf : result) {
                if (rf.getDescricao().equalsIgnoreCase(resultadoAnalise)) {
                    inscricaoPrograma.setResultadoFinalAnaliseElegibilidade(rf);
                    break;
                }
            }
        }
    }

    // Esta guia somente poderá ser mostrada caso tenha sido gerada a segunda
    // lista de elegiveis e a segunda lista
    // de avaliação ainda não tiver sido publicada.
    private boolean mostrarGuiaDeCriteriosAvaliacao() {
        mostrarGuiaAvaliacao = false;
        if (inscricaoPrograma.getStatusInscricao() != null) {
            if(analiseDto.getAbaClicada() == EnumAbaFaseAnalise.ELEGIBILIDADE){
                
                mostrarGuiaAvaliacao = false;
                dadosPropostaElebigilidadePanel.setVisible(true);
                dadosPropostaValidacaoPanel.setVisible(false);

                buttonSalvarAvaliacao.setVisible(false);
                buttonConcluirAvaliacao.setVisible(false);

                buttonSalvarElegibilidade.setVisible(true);
                buttonConcluirElegibilidade.setVisible(true);
                
            }else{
                
                mostrarGuiaAvaliacao = true;
                dadosPropostaElebigilidadePanel.setVisible(false);
                dadosPropostaValidacaoPanel.setVisible(true);

                buttonSalvarAvaliacao.setVisible(true);
                buttonConcluirAvaliacao.setVisible(true);

                buttonSalvarElegibilidade.setVisible(false);
                buttonConcluirElegibilidade.setVisible(false);
            }
        }           
        panelAnexoProposta.setVisible(false);
        return mostrarGuiaAvaliacao;
    }

    private boolean isGeradaSegundaListaDeElegibilidade() {
        if (listaElegibilidade == null || listaElegibilidade.size() == 0) {
            return false;
        } else {
            if (listaElegibilidade.size() > 1) {
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean isGeradaSegundaListaDeAvaliacao() {
        if (listaAvaliacao == null || listaAvaliacao.size() == 0) {
            return false;
        } else {
            if (listaAvaliacao.size() > 1) {
                return true;
            } else {
                return false;
            }
        }
    }

    private void montarCriteriosAvaliacao() {
        inscricaoPrograma.setProgramasCriterioAvaliacao(dadosPropostaValidacaoPanel.getListaCriterios());
        inscricaoPrograma.setAnexos(panelAnexoProposta.getListAnexoTemp());

        inscricaoPrograma.setPontuacaoFinal(dadosPropostaValidacaoPanel.getHistoricoAvaliacao().getPontuacaoFinal());
        inscricaoPrograma.setMotivoAnaliseAvaliacao(dadosPropostaValidacaoPanel.getHistoricoAvaliacao().getMotivoAnalise());
        inscricaoPrograma.setDescricaoJustificativaAvaliacao(dadosPropostaValidacaoPanel.getHistoricoAvaliacao().getDescricaoJustificativa());

        if (inscricaoPrograma.getNumeroProcessoSEIRecursoAvaliacao() != null) {
            inscricaoPrograma.setNumeroProcessoSEIRecursoAvaliacao(limparCampos(inscricaoPrograma.getNumeroProcessoSEIRecursoAvaliacao()));
        }

        if (!dadosPropostaValidacaoPanel.getAlteradoResultadoAnalise()) {
            inscricaoPrograma.setPontuacaoFinal(dadosPropostaValidacaoPanel.getNotaCriterio());
        } else {
            if (dadosPropostaValidacaoPanel.getHistoricoAvaliacao().getPontuacaoFinal() == null) {
                inscricaoPrograma.setPontuacaoFinal(0);
            } else {
                inscricaoPrograma.setPontuacaoFinal(dadosPropostaValidacaoPanel.getHistoricoAvaliacao().getPontuacaoFinal());
            }

        }
    }

    public String limparCampos(String valor) {
        String value = valor;
        value = value.replace(".", "");
        value = value.replace("/", "");
        value = value.replace("-", "");
        value = value.replace("(", "");
        value = value.replace(")", "");
        return value;
    }

    private AttributeModifier corBotaoClicado(String nomeBotao) {
        if (botaoClicado.equalsIgnoreCase(nomeBotao)) {
            return classClicado;
        } else {
            return classNaoclicado;
        }
    }

    private HistoricoAnaliseAvaliacao retornaHistoricoAvaliacao() {
        return dadosPropostaValidacaoPanel.getHistoricoAvaliacao();
    }

    public InscricaoPrograma getInscricaoPrograma() {
        return inscricaoPrograma;
    }

    public void setInscricaoPrograma(InscricaoPrograma inscricaoPrograma) {
        this.inscricaoPrograma = inscricaoPrograma;
    }

    public Form<InscricaoPrograma> getForm() {
        return form;
    }

    public void setForm(Form<InscricaoPrograma> form) {
        this.form = form;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }
}
