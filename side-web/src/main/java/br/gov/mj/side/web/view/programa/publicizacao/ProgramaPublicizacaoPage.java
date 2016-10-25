package br.gov.mj.side.web.view.programa.publicizacao;

import java.time.LocalDate;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaHistoricoPublicizacao;
import br.gov.mj.side.web.dto.PermissaoProgramaDto;
import br.gov.mj.side.web.service.AnexoProgramaService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.service.PublicizacaoService;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.programa.PanelFasePrograma;
import br.gov.mj.side.web.view.programa.ProgramaPesquisaPage;
import br.gov.mj.side.web.view.programa.visualizacao.ProgramaPanel;
import br.gov.mj.side.web.view.template.TemplatePage;

@AuthorizeInstantiation({ ProgramaPublicizacaoPage.ROLE_MANTER_PROGRAMA_PUBLICAR, ProgramaPublicizacaoPage.ROLE_MANTER_PROGRAMA_SUSPENDER, ProgramaPublicizacaoPage.ROLE_MANTER_PROGRAMA_CANCELAR, ProgramaPesquisaPage.ROLE_MANTER_PROGRAMA_SUSPENDER_PRAZO,
        ProgramaPublicizacaoPage.ROLE_MANTER_PROGRAMA_PRORROGAR_PRAZO, ProgramaPublicizacaoPage.ROLE_MANTER_PROGRAMA_REABRIR_PRAZO })
public class ProgramaPublicizacaoPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    public static final String ROLE_MANTER_PROGRAMA_PUBLICAR = "manter_programa:publicar";
    public static final String ROLE_MANTER_PROGRAMA_SUSPENDER = "manter_programa:suspender";
    public static final String ROLE_MANTER_PROGRAMA_CANCELAR = "manter_programa:cancelar";
    public static final String ROLE_MANTER_PROGRAMA_SUSPENDER_PRAZO = "manter_programa:suspender_prazo";
    public static final String ROLE_MANTER_PROGRAMA_PRORROGAR_PRAZO = "manter_programa:prorrogar_prazo";
    public static final String ROLE_MANTER_PROGRAMA_REABRIR_PRAZO = "manter_programa:reabrir_prazo";
    
    private PanelFasePrograma panelFasePrograma;
    private PanelProrrogarSuspenderPrazo panelProrrogarSuspenderPrazo;

    @Inject
    private ProgramaService programaService;

    @Inject
    private AnexoProgramaService anexoProgramaService;

    @Inject
    private PublicizacaoService publicizacaoService;

    @Inject
    private ComponentFactory componentFactory;

    private ProgramaHistoricoPublicizacao programaHistoricoPublicizacao = new ProgramaHistoricoPublicizacao();
    private Programa programa;
    private PermissaoProgramaDto permissaoPrograma;
    private String mensagemAviso ="";
    private Integer abaClicada;
    private ProgramaHistoricoPublicizacao ultimoProgramaHistorico;

    private Page backPage;

    private Form<ProgramaHistoricoPublicizacao> form;

    public ProgramaPublicizacaoPage(final PageParameters pageParameters, Programa programa, Page backPage, PermissaoProgramaDto permissaoPrograma,Integer abaClicada) {
        super(pageParameters);
        this.backPage = backPage;
        this.programa = programaService.buscarPeloId(programa.getId());
        this.permissaoPrograma = permissaoPrograma;
        this.abaClicada = abaClicada;

        setTitulo("Gerenciar Programa");
        initEntity();
        initComponents();
    }
    
    private void initEntity() {
        programa.setCriteriosAcompanhamento(programaService.buscarProgramaCriterioAcompanhamento(programa));
        programa.setCriteriosElegibilidade(programaService.buscarProgramaCriterioElegibilidade(programa));
        programa.setProgramaBens(programaService.buscarProgramaBem(programa));
        programa.setProgramaKits(programaService.buscarProgramakit(programa));
        programa.setPotenciaisBeneficiariosUf(programaService.buscarProgramaPotencialBeneficiarioUf(programa));
        programa.setRecursosFinanceiros(programaService.buscarProgramaRecursoFinanceiroPeloIdPrograma(programa));
        programa.setAnexos(SideUtil.convertAnexoDtoToEntityProgramaAnexo(anexoProgramaService.buscarPeloIdPrograma(programa.getId())));
        programa.setHistoricoPublicizacao(programaService.buscarHistoricoPublicizacao(programa));
        programa.setCriteriosAvaliacao(programaService.buscarProgramaCriterioAvaliacao(programa));

        if (permissaoPrograma.getProrrogar() || permissaoPrograma.getProrrogarAnalise() || permissaoPrograma.getReabrirPrazo() || permissaoPrograma.getSuspenderPrazo() || permissaoPrograma.getVisualizarPanelAposAnalise()) {
            programaHistoricoPublicizacao = programaService.buscarHistoricoPublicizacao(programa).get(0);
            programaHistoricoPublicizacao.setDataPublicacaoDOU(null);
            programaHistoricoPublicizacao.setMotivo("");
        }
    }

    private void initComponents() {

        form = componentFactory.newForm("form", programaHistoricoPublicizacao);
        
      //Somente será carregado se tiver passado da fase de analise ou se clicado em suspender / prorrogar prazo
        ultimoProgramaHistorico = new ProgramaHistoricoPublicizacao();
        if(permissaoPrograma.getVisualizarPanelAposAnalise() || permissaoPrograma.getEscolherProrrogarSuspenderPrograma() || permissaoPrograma.getSuspenderPrograma()){
            ultimoProgramaHistorico = publicizacaoService.buscarUltimoProgramaHistoricoPublicizacao(programa);
        }
        
        PanelAviso panelAviso = new PanelAviso("panelAviso");
        form.add(panelAviso);
        
        
        panelFasePrograma = new PanelFasePrograma("panelFasePrograma",programa,backPage,2);
        form.add(panelFasePrograma);
        
        ProgramaPanel programaPanel = new ProgramaPanel("programaPanel", programa);
        programaPanel.setVisible(!permissaoPrograma.getEscolherProrrogarSuspenderPrograma());
        form.add(programaPanel);
        
        panelProrrogarSuspenderPrazo = new PanelProrrogarSuspenderPrazo("panelProrrogarSuspenderPrazo");
        panelProrrogarSuspenderPrazo.setVisible(permissaoPrograma.getEscolherProrrogarSuspenderPrograma() || permissaoPrograma.getProrrogar() ||
                permissaoPrograma.getSuspenderPrazo());
        form.add(panelProrrogarSuspenderPrazo);
        
        
        
        PublicarProgramaPanel publicarProgramaPanel = new PublicarProgramaPanel("publicarProgramaPanel");
        publicarProgramaPanel.setVisible(permissaoPrograma.getPublicar());
        authorize(publicarProgramaPanel, RENDER, ROLE_MANTER_PROGRAMA_PUBLICAR);
        form.add(publicarProgramaPanel);

        CancelarProgramaPanel cancelarProgramaPanel = new CancelarProgramaPanel("cancelarProgramaPanel");
        cancelarProgramaPanel.setVisible(permissaoPrograma.getCancelar());
        authorize(cancelarProgramaPanel, RENDER, ROLE_MANTER_PROGRAMA_CANCELAR);
        form.add(cancelarProgramaPanel);

        if(programa.getStatusPrograma() != EnumStatusPrograma.SUSPENSO){
            SuspenderProgramaPanel suspenderProgramaPanel = new SuspenderProgramaPanel("suspenderProgramaPanel");
            suspenderProgramaPanel.setVisible(permissaoPrograma.getSuspenderPrograma());
            authorize(suspenderProgramaPanel, RENDER, ROLE_MANTER_PROGRAMA_SUSPENDER);
            form.add(suspenderProgramaPanel);
        }else{
            SuspenderProgramaVisualizacaoPanel suspenderProgramaPanel = new SuspenderProgramaVisualizacaoPanel("suspenderProgramaPanel",ultimoProgramaHistorico);
            suspenderProgramaPanel.setVisible(permissaoPrograma.getSuspenderPrograma());
            authorize(suspenderProgramaPanel, RENDER, ROLE_MANTER_PROGRAMA_SUSPENDER);
            form.add(suspenderProgramaPanel);
        }
        

        ProrrogarPrazoProgramaPanel prorrogarPrazoProgramaPanel = new ProrrogarPrazoProgramaPanel("prorrogarPrazoProgramaPanel");
        prorrogarPrazoProgramaPanel.setVisible(permissaoPrograma.getProrrogar());
        authorize(prorrogarPrazoProgramaPanel, RENDER, ROLE_MANTER_PROGRAMA_PRORROGAR_PRAZO);
        form.add(prorrogarPrazoProgramaPanel);
        
        ProrrogarPrazoAnaliseProgramaPanel prorrogarPrazoAnaliseProgramaPanel = new ProrrogarPrazoAnaliseProgramaPanel("prorrogarPrazoAnaliseProgramaPanel");
        prorrogarPrazoAnaliseProgramaPanel.setVisible(permissaoPrograma.getProrrogarAnalise());
        authorize(prorrogarPrazoAnaliseProgramaPanel, RENDER, ROLE_MANTER_PROGRAMA_PRORROGAR_PRAZO);
        form.add(prorrogarPrazoAnaliseProgramaPanel);
        
        PrazoAposAnalisePanel prazoAposAnalisePanel = new PrazoAposAnalisePanel("prazoAposAnalisePanel",ultimoProgramaHistorico);
        prazoAposAnalisePanel.setVisible(permissaoPrograma.getVisualizarPanelAposAnalise());
        authorize(prazoAposAnalisePanel, RENDER, ROLE_MANTER_PROGRAMA_PRORROGAR_PRAZO);
        form.add(prazoAposAnalisePanel);

        ReabrirPrazoProgramaPanel reabrirPrazoProgramaPanel = new ReabrirPrazoProgramaPanel("reabrirPrazoProgramaPanel");
        reabrirPrazoProgramaPanel.setVisible(permissaoPrograma.getReabrirPrazo());
        authorize(reabrirPrazoProgramaPanel, RENDER, ROLE_MANTER_PROGRAMA_REABRIR_PRAZO);
        form.add(reabrirPrazoProgramaPanel);

        SuspenderPrazoProgramaPanel suspenderPrazoProgramaPanel = new SuspenderPrazoProgramaPanel("suspenderPrazoProgramaPanel");
        suspenderPrazoProgramaPanel.setVisible(permissaoPrograma.getSuspenderPrazo());
        authorize(suspenderPrazoProgramaPanel, RENDER, ROLE_MANTER_PROGRAMA_SUSPENDER_PRAZO);
        form.add(suspenderPrazoProgramaPanel);

        AcoesPanel acoes = new AcoesPanel("acoes");
        form.add(acoes);

        add(form);
    }
    
    private class PanelProrrogarSuspenderPrazo extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelProrrogarSuspenderPrazo(String id) {
            super(id);
            
            add(componentFactory.newLabel("msgAlerta", "MT001"));
            
            EscolherProrrogarSuspenderPanel escolherProrrogarSuspenderPanel = new EscolherProrrogarSuspenderPanel("panelProrrogarSuspenderPrazo",ultimoProgramaHistorico);
            add(escolherProrrogarSuspenderPanel);
            escolherProrrogarSuspenderPanel.setVisible(!permissaoPrograma.getProrrogar() && !permissaoPrograma.getSuspenderPrazo());
            
            add(newButtonOpcaoSuspenderPrazo()); //btnOpcaoSuspenderPrazo
            add(newButtonOpcaoProrrogarPrazo()); //btnOpcaoProrrogarPrazo
        }
    }
    
    private class PanelAviso extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelAviso(String id) {
            super(id);
            setOutputMarkupId(true);
            
            this.setVisible(mostrarMensagemAviso());
            add(newLabelAvisoCanceladoSuspenso()); //msgAlertaSuspensoCancelado
        }
    }

    private class AcoesPanel extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public AcoesPanel(String id) {
            super(id);
            Button btnPublicar = newButtonPublicarPrograma();
            authorize(btnPublicar, RENDER, ROLE_MANTER_PROGRAMA_PUBLICAR);
            add(btnPublicar);

            Button btnCancelar = newButtonCancelarPrograma();
            authorize(btnCancelar, RENDER, ROLE_MANTER_PROGRAMA_CANCELAR);
            add(btnCancelar);

            Button btnSuspender = newButtonSuspenderPrograma();
            authorize(btnSuspender, RENDER, ROLE_MANTER_PROGRAMA_SUSPENDER);
            add(btnSuspender);

            Button btnSuspenderPrazo = newButtonSuspenderPrazo();
            authorize(btnSuspenderPrazo, RENDER, ROLE_MANTER_PROGRAMA_SUSPENDER_PRAZO);
            add(btnSuspenderPrazo);

            Button btnProrrogarPrazo = newButtonProrrogarPrograma();
            authorize(btnProrrogarPrazo, RENDER, ROLE_MANTER_PROGRAMA_PRORROGAR_PRAZO);
            add(btnProrrogarPrazo);
            
            Button btnProrrogarPrazoAnalise = newButtonProrrogarAnalisePrograma();
            authorize(btnProrrogarPrazoAnalise, RENDER, ROLE_MANTER_PROGRAMA_PRORROGAR_PRAZO);
            add(btnProrrogarPrazoAnalise);

            Button btnReabrirPrazo = newButtonReabrirPrazoPrograma();
            authorize(btnReabrirPrazo, RENDER, ROLE_MANTER_PROGRAMA_REABRIR_PRAZO);
            add(btnReabrirPrazo);

            add(newButtonVoltar());
        }
    }
    
    
    private Label newLabelAvisoCanceladoSuspenso(){
        Label lbl = new Label("msgAlertaSuspensoCancelado",mensagemAviso);
        lbl.setOutputMarkupId(true);
        return lbl;
    }

    private Button newButtonReabrirPrazoPrograma() {
        Button btn = componentFactory.newButton("btnReabrirPrazoPrograma", () -> reabrirPrazoPrograma());
        btn.setVisible(permissaoPrograma.getReabrirPrazo());
        return btn;
    }

    private Button newButtonProrrogarPrograma() {
        Button btn = componentFactory.newButton("btnProrrogarPrograma", () -> prorrogarPrograma());
        btn.setVisible(permissaoPrograma.getProrrogar());
        return btn;
    }
    
    private Button newButtonProrrogarAnalisePrograma() {
        Button btn = componentFactory.newButton("btnProrrogarAnalisePrograma", () -> prorrogarAnalisePrograma());
        btn.setVisible(permissaoPrograma.getProrrogarAnalise());
        return btn;
    }

    private Button newButtonVoltar() {
        Button btn = componentFactory.newButton("btnVoltar", () -> voltar());
        btn.setDefaultFormProcessing(false);
        return btn;
    }

    private Button newButtonPublicarPrograma() {
        Button btn = componentFactory.newButton("btnPublicarPrograma", () -> publicarPrograma());
        btn.setVisible(permissaoPrograma.getPublicar());
        return btn;
    }

    private Button newButtonCancelarPrograma() {
        Button btn = componentFactory.newButton("btnCancelarPrograma", () -> cancelarPrograma());
        btn.setVisible(mostrarBotaoCancelarPrograma());
        return btn;
    }
    
    private Button newButtonSuspenderPrograma() {
        Button btn = componentFactory.newButton("btnSuspenderPrograma", () -> suspenderPrograma());
        btn.setVisible(mostrarBotaoSuspenderPrograma());
        return btn;
    }

    private Button newButtonSuspenderPrazo() {
        Button btn = componentFactory.newButton("btnSuspenderPrazo", () -> suspenderPrazo());
        btn.setVisible(permissaoPrograma.getSuspenderPrazo());
        return btn;
    }
    
    private Button newButtonOpcaoSuspenderPrazo() {
        Button btn = componentFactory.newButton("btnOpcaoSuspenderPrazo", () -> opcaoSuspenderPrazo());
        btn.setVisible(permissaoPrograma.getEscolherProrrogarSuspenderPrograma()|| permissaoPrograma.getSuspenderPrazo() || permissaoPrograma.getProrrogar());
        btn.setDefaultFormProcessing(false);
        return btn;
    }
    
    private Button newButtonOpcaoProrrogarPrazo() {
        Button btn = componentFactory.newButton("btnOpcaoProrrogarPrazo", () -> opcaoProrrogarPrazo());
        btn.setVisible(permissaoPrograma.getEscolherProrrogarSuspenderPrograma()|| permissaoPrograma.getSuspenderPrazo() || permissaoPrograma.getProrrogar());
        btn.setDefaultFormProcessing(false);
        return btn;
    }

    private void publicarPrograma() {
        if (!getSideSession().hasRole(ROLE_MANTER_PROGRAMA_PUBLICAR)) {
            throw new SecurityException();
        }

        if (validarPublicizacaoPrograma()) {

            publicizacaoService.publicar(programaHistoricoPublicizacao, programa, getIdentificador());
            getSession().info("Publicado com sucesso");
            
            PermissaoProgramaDto permissaoPrograma = new PermissaoProgramaDto();
            permissaoPrograma.setEscolherProrrogarSuspenderPrograma(true);
            setResponsePage(new ProgramaPublicizacaoPage(new PageParameters(), programa, backPage, permissaoPrograma,abaClicada));
        }
    }

    private void cancelarPrograma() {
        if (!getSideSession().hasRole(ROLE_MANTER_PROGRAMA_CANCELAR)) {
            throw new SecurityException();
        }
        publicizacaoService.cancelar(programaHistoricoPublicizacao, programa, getIdentificador());
        getSession().info("Programa cancelado com sucesso.");
        setResponsePage(new ProgramaPesquisaPage(new PageParameters()));
    }

    private void suspenderPrograma() {
        if (!getSideSession().hasRole(ROLE_MANTER_PROGRAMA_SUSPENDER)) {
            throw new SecurityException();
        }
        publicizacaoService.suspender(programaHistoricoPublicizacao, programa, getIdentificador());
        getSession().info("Programa suspenso com sucesso.");
        
        PermissaoProgramaDto permissaoPrograma = new PermissaoProgramaDto();
        permissaoPrograma.setSuspenderPrograma(true);
        setResponsePage(new ProgramaPublicizacaoPage(new PageParameters(), programa, backPage, permissaoPrograma,abaClicada));
    }

    private void suspenderPrazo() {
        if (!getSideSession().hasRole(ROLE_MANTER_PROGRAMA_SUSPENDER_PRAZO)) {
            throw new SecurityException();
        }
        publicizacaoService.suspenderPrazo(programaHistoricoPublicizacao, programa, getIdentificador());
        getSession().info("Prazo suspenso com sucesso.");
        
        PermissaoProgramaDto permissaoPrograma = new PermissaoProgramaDto();
        permissaoPrograma.setReabrirPrazo(true);
        setResponsePage(new ProgramaPublicizacaoPage(new PageParameters(), programa, backPage, permissaoPrograma,abaClicada));
    }
    
    private void opcaoSuspenderPrazo() {
        if (!getSideSession().hasRole(ROLE_MANTER_PROGRAMA_SUSPENDER_PRAZO)) {
            throw new SecurityException();
        }
            
        PermissaoProgramaDto permissaoPrograma = new PermissaoProgramaDto();
        permissaoPrograma.setSuspenderPrazo(true);
        setResponsePage(new ProgramaPublicizacaoPage(new PageParameters(), programa, backPage, permissaoPrograma,abaClicada));
    }
    
    private void opcaoProrrogarPrazo() {
        if (!getSideSession().hasRole(ROLE_MANTER_PROGRAMA_PRORROGAR_PRAZO)) {
            throw new SecurityException();
        }
        
        PermissaoProgramaDto permissaoPrograma = new PermissaoProgramaDto();
        permissaoPrograma.setProrrogar(true);
        setResponsePage(new ProgramaPublicizacaoPage(new PageParameters(), programa, backPage, permissaoPrograma,abaClicada));
    }

    private void prorrogarPrograma() {
        if (!getSideSession().hasRole(ROLE_MANTER_PROGRAMA_PRORROGAR_PRAZO)) {
            throw new SecurityException();
        }

        if (validarPublicizacaoPrograma()) {
            publicizacaoService.prorrogar(programaHistoricoPublicizacao, programa, getIdentificador());
            getSession().info("Programa prorrogado com sucesso.");
            
            PermissaoProgramaDto permissaoPrograma = new PermissaoProgramaDto();
            permissaoPrograma.setProrrogar(true);
            setResponsePage(new ProgramaPublicizacaoPage(new PageParameters(), programa, backPage, permissaoPrograma,abaClicada));
        }
    }
    
    private void prorrogarAnalisePrograma() {
        if (!getSideSession().hasRole(ROLE_MANTER_PROGRAMA_PRORROGAR_PRAZO)) {
            throw new SecurityException();
        }

        if (validarPublicizacaoPrograma()) {
            publicizacaoService.prorrogarAnalise(programaHistoricoPublicizacao, programa, getIdentificador());
            getSession().info("Análise do Programa prorrogado com sucesso.");
            
            PermissaoProgramaDto permissaoPrograma = new PermissaoProgramaDto();
            permissaoPrograma.setProrrogarAnalise(true);
            setResponsePage(new ProgramaPublicizacaoPage(new PageParameters(), programa, backPage, permissaoPrograma,abaClicada));
        }
    }

    private void reabrirPrazoPrograma() {
        if (!getSideSession().hasRole(ROLE_MANTER_PROGRAMA_REABRIR_PRAZO)) {
            throw new SecurityException();
        }
        if (validarPublicizacaoPrograma()) {
            publicizacaoService.reabrirPrazo(programaHistoricoPublicizacao, programa, getIdentificador());
            getSession().info("Prazo reaberto com sucesso.");
            
            PermissaoProgramaDto permissaoPrograma = new PermissaoProgramaDto();
            permissaoPrograma.setEscolherProrrogarSuspenderPrograma(true);
            setResponsePage(new ProgramaPublicizacaoPage(new PageParameters(), programa, backPage, permissaoPrograma,abaClicada));
            
        }
    }

    private void voltar() {
        setResponsePage(backPage);
    }

    // AÇÕES
    
    private boolean mostrarBotaoCancelarPrograma(){
        if(programa.getStatusPrograma() == EnumStatusPrograma.CANCELADO){
            return false;
        }else{
            return permissaoPrograma.getCancelar();
        }
    }
    
    private boolean mostrarBotaoSuspenderPrograma(){
        if(programa.getStatusPrograma() == EnumStatusPrograma.SUSPENSO){
            return false;
        }else{
            return permissaoPrograma.getSuspenderPrograma();
        }
    }
    
    private boolean mostrarMensagemAviso(){
        Boolean visivel = false;
        if(programa.getStatusPrograma() == EnumStatusPrograma.SUSPENSO){
            mensagemAviso = "Programa Suspenso";
            return true;
        }
        
        if(programa.getStatusPrograma() == EnumStatusPrograma.CANCELADO){
            mensagemAviso = "Programa Cancelado";
            return  true;
        }
        return false;
    }

    private boolean validarPublicizacaoPrograma() {
        boolean validar = true;

        LocalDate inicioPrazo = programaHistoricoPublicizacao.getDataInicialProposta();
        LocalDate fimPrazo = programaHistoricoPublicizacao.getDataFinalProposta();
        LocalDate inicioAnalise = programaHistoricoPublicizacao.getDataInicialAnalise();
        LocalDate fimAnalise = programaHistoricoPublicizacao.getDataFinalAnalise();

        // Validando o Inicio do Prazo
        if (inicioPrazo.isAfter(fimPrazo)) {
            addMsgError("O'Período para propostas' (Inicial) não pode ser superior ao 'Período para propostas' (Final).");
            validar = false;
        } else {
            if (inicioPrazo.isAfter(inicioAnalise)) {
                addMsgError("O 'Período para propostas' (Inicial) não pode ser superior ao 'Período para análise das Propostas' (Inicial).");
                validar = false;
            } else {
                if (inicioPrazo.isAfter(fimAnalise)) {
                    addMsgError("O 'Período para propostas' (Inicial) não pode ser superior ao 'Período para análise das Propostas' (Final).");
                    validar = false;
                }
            }
        }

        // Validando as datas
        if (fimPrazo.isAfter(inicioAnalise)) {
            addMsgError("O 'Período para propostas' (Final) não pode ser superior ao 'Período para análise das Propostas' (Inicial).");
            validar = false;
        } else {
            if (fimPrazo.isAfter(fimAnalise)) {
                addMsgError("O 'Período para propostas' (Final) não pode ser superior ao 'Período para análise das Propostas' (Final).");
                validar = false;
            }
        }

        // Validando as datas de analise
        if (inicioAnalise.isAfter(fimAnalise)) {
            addMsgError("O 'Período para análise das Propostas' (Inicial) não pode ser superior ao 'Período para análise das Propostas' (Final).");
            validar = false;
        }
        return validar;
    }

}