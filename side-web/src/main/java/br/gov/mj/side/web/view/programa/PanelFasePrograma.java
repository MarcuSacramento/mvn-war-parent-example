package br.gov.mj.side.web.view.programa;

import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.side.entidades.enums.EnumAbaFaseAnalise;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.web.dto.AnaliseDto;
import br.gov.mj.side.web.dto.PermissaoProgramaDto;
import br.gov.mj.side.web.dto.ProgramaComboDto;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.util.SideSession;
import br.gov.mj.side.web.view.execucao.AcompanharOrdemFornecimentoPage;
import br.gov.mj.side.web.view.planejarLicitacao.PlanejamentoLicitacaoPage;
import br.gov.mj.side.web.view.programa.publicizacao.ProgramaPublicizacaoPage;
import br.gov.mj.side.web.view.propostas.PropostasEnviadasPage;

public class PanelFasePrograma extends Panel {
    private static final long serialVersionUID = 1L;
    
    private PanelPrincipal panelPrincipal;
    private PanelBreadcrump panelBreadcrump;
    
    private Page backPage;
    private Programa programa;
    private ProgramaComboDto idPrograma;
    
    private Label lblNomePrograma;
    
    private WebMarkupContainer containerDefinicao;
    private WebMarkupContainer containerPublicacao;
    private WebMarkupContainer containerAnalise;
    private WebMarkupContainer containerContrato;
    private WebMarkupContainer containerExecucao;
    private WebMarkupContainer containerAcompanhamento;
    
    private AjaxFallbackLink<Void> btnDefinicao;
    private AjaxFallbackLink<Void> btnPublicacao;
    private AjaxFallbackLink<Void> btnAnalise;
    private AjaxFallbackLink<Void> btnExecucao;
    private AjaxFallbackLink<Void> btnAcompanhamento;
    
    private InfraDropDownChoice<ProgramaComboDto> dropDownPrograma;
    
    private Integer numeroAbaFaseAtiva = 0;
    private Integer abaClicada = 0;
    
    private AttributeAppender classeAtivarStep = new AttributeAppender("class", "active", " ");
    private AttributeAppender classeDisabledStep = new AttributeAppender("class", "disabled", " ");
    private AttributeAppender classeDoneStep = new AttributeAppender("class", "done", " ");
    
    private List<ProgramaComboDto> listaProgramas;
    
    @Inject
    private ComponentFactory componentFactory;
    
    @Inject
    private ProgramaService programaService;

    public PanelFasePrograma(String id, Programa programa,Page backPage,Integer abaClicada) {
        super(id);
        this.backPage = backPage;
        this.programa = programa;
        this.abaClicada = abaClicada;
        
        verificarQualFaseEstaOPrograma();
        initVariaveis();
        initComponents();
        
    }
    
    public PanelFasePrograma(String id, Programa programa,Page backPage) {
        super(id);
        this.backPage = backPage;
        this.programa = programa;
        
        verificarQualFaseEstaOPrograma();
        initVariaveis();
        initComponents();
        
    }
    
    private void initVariaveis(){
        listaProgramas = programaService.buscarProgramas();
        if(programa == null || programa.getId() == null){
            return;
        }
        for(ProgramaComboDto combo:listaProgramas){
            if(combo.getId().intValue() == programa.getId().intValue()){
                idPrograma = combo;
                break;
            }
        }
    }
    
    private void initComponents(){
        
        panelPrincipal = new PanelPrincipal("panelPrincipal");  
        panelBreadcrump = new PanelBreadcrump("panelBreadcrump");
        
        add(panelPrincipal);
        add(panelBreadcrump);
    }
    
    private class PanelBreadcrump extends WebMarkupContainer{
        private static final long serialVersionUID = 1L;

        public PanelBreadcrump(String id){
            super(id);
            setOutputMarkupId(true);
            
            /*add(new Link("dashboard") {

                @Override
                public void onClick() {
                    setResponsePage(HomePage.class);
                }
            });*/

            /*add(new Link("programaPage") {

                @Override
                public void onClick() {
                    setResponsePage(backPage);
                }
            });*/
            
            add(newDropDownPrograma()); //dropDownPrograma
            add(newLabelNomePrograma()); //lblNomePrograma
            add(newButtonSuspenderPrograma()); //btnSuspenderPrograma
            add(newButtonCancelarPrograma()); //btnCancelarPrograma
        }
    }
    
    private class PanelPrincipal extends WebMarkupContainer{
        private static final long serialVersionUID = 1L;

        public PanelPrincipal(String id){
            super(id);
            setOutputMarkupId(true);
            
            add(newContainerAcompanhemanto()); //containerAcompanhamento
            add(newContainerExecucao()); //containerExecucao
            add(newContainerContrato()); //containerContrato
            add(newContainerAnalise()); //containerAnalise
            add(newContainerPublicacao()); //containerPublicacao
            add(newContainerDefinicao()); //containerDefinicao
            
            ativarAbaClicada();
        }
    }
    
    //Cada WebMarkupContainer deste é um <li> no Html
    private WebMarkupContainer newContainerDefinicao() {
        containerDefinicao = new WebMarkupContainer("containerDefinicao");
        
        //Aqui esta o link <a> no html que irá chamar a página
        containerDefinicao.add(newLinkDefinicao()); //btnDefinicao
        
        //Se estiver na primeira fase de execução
        //Do programa, ou seja, o programa ainda não foi publicado
        //Então irá ativar o primeiro 'Step' que mostra a fase
        if(numeroAbaFaseAtiva == 1){
            containerDefinicao.add(classeAtivarStep);
            
            containerPublicacao.add(classeDisabledStep);
            containerAnalise.add(classeDisabledStep);
            containerContrato.add(classeDisabledStep);
            containerExecucao.add(classeDisabledStep);
            containerAcompanhamento.add(classeDisabledStep);
        }
        
        return containerDefinicao;
    }
    
    private void ativarAbaClicada(){
        if(abaClicada == 1){
            containerDefinicao.add(classeDoneStep);
        }
        
        if(abaClicada == 2){
            containerPublicacao.add(classeDoneStep);
        }
        
        if(abaClicada == 3){
            containerAnalise.add(classeDoneStep);
        }
        
        if(abaClicada == 4){
            containerContrato.add(classeDoneStep);
        }
        if(abaClicada == 5){
            containerExecucao.add(classeDoneStep);
        }
    }
    
    private WebMarkupContainer newContainerPublicacao() {
        containerPublicacao = new WebMarkupContainer("containerPublicacao");
        containerPublicacao.add(newLinkPublicacao()); //btnPublicacao
        
        if(numeroAbaFaseAtiva == 2){
            containerPublicacao.add(classeAtivarStep);
            containerExecucao.add(classeDisabledStep);
            containerAcompanhamento.add(classeDisabledStep);
        }        
        return containerPublicacao;
    }
    
    private WebMarkupContainer newContainerAnalise() {
        containerAnalise = new WebMarkupContainer("containerAnalise");
        
        if(numeroAbaFaseAtiva == 3){
            containerAnalise.add(classeAtivarStep);  
            containerExecucao.add(classeDisabledStep);
            containerAcompanhamento.add(classeDisabledStep);
        }        
        containerAnalise.add(newLinkAnalise()); //btnAnalise
        return containerAnalise;
    }
    
    private WebMarkupContainer newContainerContrato() {
        containerContrato = new WebMarkupContainer("containerContrato");
        containerContrato.add(newLinkContrato()); //btnContrato
        
        if(numeroAbaFaseAtiva == 4){
            containerContrato.add(classeAtivarStep);
            containerExecucao.add(classeDisabledStep);  
            containerAcompanhamento.add(classeDisabledStep);
        }        
        return containerContrato;
    }
    
    private WebMarkupContainer newContainerExecucao() {
        containerExecucao = new WebMarkupContainer("containerExecucao");
        containerExecucao.add(newLinkExecucao()); //btnExecucao
        
        if(numeroAbaFaseAtiva == 5){            
            containerExecucao.add(classeAtivarStep);
            containerAcompanhamento.add(classeDisabledStep);
        }        
        return containerExecucao;
    }
    
    private WebMarkupContainer newContainerAcompanhemanto() {
        containerAcompanhamento = new WebMarkupContainer("containerAcompanhamento");
        containerAcompanhamento.add(newLinkAcompanhamento()); //btnAcompanhamento
        //containerProximo.setVisible(false);
        
        if(numeroAbaFaseAtiva == 6){
        	containerAcompanhamento.add(classeAtivarStep);
        }        
        return containerAcompanhamento;
    }
    
    //OS COMPONENTES VIRÃO ABAIXO
    private DropDownChoice<ProgramaComboDto> newDropDownPrograma() {
        
        DropDownChoice<ProgramaComboDto> dropDownPrograma = new DropDownChoice<ProgramaComboDto>("dropDownPrograma", new PropertyModel<ProgramaComboDto>(this, "idPrograma"), listaProgramas, new ChoiceRenderer<ProgramaComboDto>("nomePrograma"));
        actionDropDownPrograma(dropDownPrograma);
        dropDownPrograma.setNullValid(true);
        dropDownPrograma.setModelObject(idPrograma);
        dropDownPrograma.setConvertedInput(idPrograma);
        dropDownPrograma.setOutputMarkupId(true);
        return dropDownPrograma;
    }
    
    private AjaxFallbackLink<Void> newLinkDefinicao() {
        btnDefinicao = componentFactory.newAjaxFallbackLink("btnDefinicao", (target) -> actionAbrirPagina(target,"definicao"));
        btnDefinicao.setOutputMarkupId(true);
        return btnDefinicao;
    }
    
    private AjaxFallbackLink<Void> newLinkPublicacao() {
        btnPublicacao = componentFactory.newAjaxFallbackLink("btnPublicacao", (target) -> actionAbrirPagina(target,"publicacao"));
        btnPublicacao.setOutputMarkupId(true);
        return btnPublicacao;
    }
    
    private AjaxFallbackLink<Void> newLinkAnalise() {
        btnAnalise = componentFactory.newAjaxFallbackLink("btnAnalise", (target) -> actionAbrirPagina(target,"analise"));
        btnAnalise.setOutputMarkupId(true);
        return btnAnalise;
    }
    
    private AjaxFallbackLink<Void> newLinkContrato() {
        btnExecucao = componentFactory.newAjaxFallbackLink("btnContrato", (target) -> actionAbrirPagina(target,"contrato"));
        btnExecucao.setOutputMarkupId(true);
        return btnExecucao;
    }
    
    private AjaxFallbackLink<Void> newLinkExecucao() {
        btnExecucao = componentFactory.newAjaxFallbackLink("btnExecucao", (target) -> actionAbrirPagina(target,"execucao"));
        btnExecucao.setOutputMarkupId(true);
        return btnExecucao;
    }
    
    private AjaxFallbackLink<Void> newLinkAcompanhamento() {
        btnAcompanhamento = componentFactory.newAjaxFallbackLink("btnAcompanhamento", (target) -> actionAbrirPagina(target,"acompanhamento"));
        btnAcompanhamento.setOutputMarkupId(true);
        return btnAcompanhamento;
    }
    
    private Label newLabelNomePrograma(){
        EnumStatusPrograma status = programa.getStatusPrograma();
        String nomePrograma ="";
        
        if(status == EnumStatusPrograma.EM_ELABORACAO || status == EnumStatusPrograma.FORMULADO){
            nomePrograma = programa.getId().toString()+" - "+ programa.getNomePrograma();
        }else{
            nomePrograma = programa.getCodigoIdentificadorProgramaPublicadoENomePrograma();
        }
        
        lblNomePrograma = new Label("lblNomePrograma",nomePrograma);
        lblNomePrograma.setOutputMarkupId(true);
        return lblNomePrograma;
    }
    
    private Button newButtonCancelarPrograma() {
        Button btn = componentFactory.newButton("btnCancelarPrograma", () -> cancelarPrograma());
        btn.setDefaultFormProcessing(false);
        btn.setVisible(mostrarBotaoCancelarPrograma());
        return btn;
    }

    private void cancelarPrograma() {

        PermissaoProgramaDto permissaoPrograma = new PermissaoProgramaDto();
        permissaoPrograma.setCancelar(true);
        setResponsePage(new ProgramaPublicizacaoPage(new PageParameters(), programa, backPage, permissaoPrograma,2));
    }

    private Button newButtonSuspenderPrograma() {
        Button btn = componentFactory.newButton("btnSuspenderPrograma", () -> suspenderPrograma());
        btn.setDefaultFormProcessing(false);
        btn.setVisible(mostrarBotaoSuspenderPrograma());
        return btn;
    }
    
    private void suspenderPrograma() {

        PermissaoProgramaDto permissaoPrograma = new PermissaoProgramaDto();
        permissaoPrograma.setSuspenderPrograma(true);
        setResponsePage(new ProgramaPublicizacaoPage(new PageParameters(), programa, backPage, permissaoPrograma,2));
    }
    
    protected SideSession getSideSession() {
        return SideSession.get();
    }
    
    //AÇÕES
    
    public void actionDropDownPrograma(DropDownChoice dropElemento) {
        dropElemento.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    
                    Programa programaClicado = programaService.buscarPeloId(idPrograma.getId());

                    EnumStatusPrograma status = programaClicado.getStatusPrograma();
                    
                    //Se estiver só na fase de elaboração do programa
                    if(status == EnumStatusPrograma.FORMULADO){
                        setResponsePage(new ProgramaPage(new PageParameters(), backPage, programaClicado, false,target,abaClicada));
                        return;
                    }
                    
                    
                    //Se já foi publicado o programa
                    if(status == EnumStatusPrograma.FORMULADO || status == EnumStatusPrograma.ABERTO_REC_PROPOSTAS ||
                            status == EnumStatusPrograma.SUSPENSO || status == EnumStatusPrograma.SUSPENSO_PRAZO_PROPOSTAS ||
                            status == EnumStatusPrograma.CANCELADO){
                        
                        PermissaoProgramaDto permissaoPrograma = new PermissaoProgramaDto();
                        
                        if(status == EnumStatusPrograma.FORMULADO){
                            permissaoPrograma.setPublicar(true);
                        }
                        
                        if(programaClicado.getStatusPrograma() == EnumStatusPrograma.ABERTO_REC_PROPOSTAS){
                            permissaoPrograma.setEscolherProrrogarSuspenderPrograma(true);
                        }
                        
                        if(programaClicado.getStatusPrograma() == EnumStatusPrograma.SUSPENSO_PRAZO_PROPOSTAS){
                            permissaoPrograma.setReabrirPrazo(true);
                        }
                        
                        if(programaClicado.getStatusPrograma() == EnumStatusPrograma.SUSPENSO){
                            permissaoPrograma.setSuspenderPrograma(true);
                        }
                        
                        if(programaClicado.getStatusPrograma() == EnumStatusPrograma.CANCELADO){
                            permissaoPrograma.setCancelar(true);
                        }
                        setResponsePage(new ProgramaPublicizacaoPage(new PageParameters(), programaClicado, backPage, permissaoPrograma,2));
                        
                        return;
                    }
                    
                    if(status == EnumStatusPrograma.EM_ANALISE || status == EnumStatusPrograma.ABERTO_REC_LOC_ENTREGA){
                        AnaliseDto analiseDto = new AnaliseDto();
                        analiseDto.setAbaClicada(EnumAbaFaseAnalise.ELEGIBILIDADE);
                        setResponsePage(new PropostasEnviadasPage(new PageParameters(), programaClicado, backPage,target,analiseDto,3));
                        return;
                    }
                    
                    if(status == EnumStatusPrograma.ABERTO_PLANEJAMENTO_LICITACAO ||
                            status == EnumStatusPrograma.ABERTO_GERACAO_CONTRATO){
                        setResponsePage(new PlanejamentoLicitacaoPage(new PageParameters(), backPage, programaClicado,target,4));
                        return;
                    }
                        
                }
        });
}
    
    private boolean mostrarBotaoSuspenderPrograma(){
        if(programa == null || programa.getId() == null || 
                programa.getStatusPrograma() == EnumStatusPrograma.SUSPENSO || programa.getStatusPrograma() == EnumStatusPrograma.CANCELADO){
            return false;
        }else{
            EnumStatusPrograma status = programa.getStatusPrograma();
            
            if(status == EnumStatusPrograma.EM_ELABORACAO || status == EnumStatusPrograma.FORMULADO){
                return false;
            }else{
                return true;
            }
        }
    }
    
    private boolean mostrarBotaoCancelarPrograma(){
        if(programa == null || programa.getId() == null || programa.getStatusPrograma() == EnumStatusPrograma.CANCELADO){
            return false;
        }else{            
            EnumStatusPrograma status = programa.getStatusPrograma();
            
            if(status == EnumStatusPrograma.EM_ELABORACAO || status == EnumStatusPrograma.FORMULADO){
                return false;
            }else{
                return true;
            }
        }
    }    
    
    private void verificarQualFaseEstaOPrograma(){
        
        EnumStatusPrograma status = programa.getStatusPrograma();
        
        if(status == EnumStatusPrograma.EM_ELABORACAO || status == null){
            numeroAbaFaseAtiva = 1;
            return;
        }
        
        if(status == EnumStatusPrograma.FORMULADO || status == EnumStatusPrograma.ABERTO_REC_PROPOSTAS ||
                status == EnumStatusPrograma.SUSPENSO || status == EnumStatusPrograma.SUSPENSO_PRAZO_PROPOSTAS ||
                status == EnumStatusPrograma.CANCELADO || status == EnumStatusPrograma.PUBLICADO){
            numeroAbaFaseAtiva = 2;
            return;
        }
        
        if(status == EnumStatusPrograma.EM_ANALISE || status == EnumStatusPrograma.ABERTO_REC_LOC_ENTREGA){
            numeroAbaFaseAtiva = 3;
            return;
        }
        
        if(status == EnumStatusPrograma.ABERTO_PLANEJAMENTO_LICITACAO ||
                status == EnumStatusPrograma.ABERTO_GERACAO_CONTRATO){
            numeroAbaFaseAtiva = 4;
            return;
        }
        
        if(status == EnumStatusPrograma.EM_EXECUCAO){
            numeroAbaFaseAtiva = 5;
            return;
        }
        
        if(status == EnumStatusPrograma.ACOMPANHAMENTO){
            numeroAbaFaseAtiva = 6;
            return;
        }
    }
    
    private void actionAbrirPagina(AjaxRequestTarget target,String pagina){
        EnumStatusPrograma statusAtual = programa.getStatusPrograma();
        
        if("definicao".equalsIgnoreCase(pagina)){
            
            /*
             * Cada aba indica a fase do programa, 1 fase definição, 2 fase publicação... se o programa estiver na aba 1 
             * então ele não poderá clicar na aba 2,3 ou 4...
             */
            if(numeroAbaFaseAtiva == 1)
            {
                return;
            }else{
                setResponsePage(new ProgramaPage(null, backPage, programa, false,target,1));
            }
        }
        
        if("publicacao".equalsIgnoreCase(pagina)){
            
            if(numeroAbaFaseAtiva < 2)
            {
                return;
            }else{
            
            PermissaoProgramaDto permissaoPrograma = new PermissaoProgramaDto(); 
            
            boolean passadaFaseAnalise = true;
            
            if(programa.getStatusPrograma() == EnumStatusPrograma.FORMULADO){
                permissaoPrograma.setPublicar(true);
                passadaFaseAnalise = false;
            }
            
            if(programa.getStatusPrograma() == EnumStatusPrograma.ABERTO_REC_PROPOSTAS || programa.getStatusPrograma() == EnumStatusPrograma.PUBLICADO){
                permissaoPrograma.setEscolherProrrogarSuspenderPrograma(true);
                passadaFaseAnalise = false;
            }
            
            if(programa.getStatusPrograma() == EnumStatusPrograma.SUSPENSO_PRAZO_PROPOSTAS){
                permissaoPrograma.setReabrirPrazo(true);
                passadaFaseAnalise = false;
            }
            
            if(programa.getStatusPrograma() == EnumStatusPrograma.EM_ANALISE){
                permissaoPrograma.setProrrogarAnalise(true);
                passadaFaseAnalise = false;
            }
            
            if(programa.getStatusPrograma() == EnumStatusPrograma.SUSPENSO){
                permissaoPrograma.setSuspenderPrograma(true);
                passadaFaseAnalise = true;
            }
            
            if(passadaFaseAnalise && !permissaoPrograma.getSuspenderPrograma()){
                permissaoPrograma.setVisualizarPanelAposAnalise(true);
            }
            
            setResponsePage(new ProgramaPublicizacaoPage(new PageParameters(), programa, backPage, permissaoPrograma,2));
            }
        }
        
        if("analise".equalsIgnoreCase(pagina)){
            
            if(programa == null || programa.getId() == null){
                return;
            }
            AnaliseDto analiseDto = new AnaliseDto();
            analiseDto.setAbaClicada(EnumAbaFaseAnalise.ELEGIBILIDADE);
            setResponsePage(new PropostasEnviadasPage(new PageParameters(), programa, backPage,target,analiseDto,3));
        }
        
        if("contrato".equalsIgnoreCase(pagina)){
            if(programa == null || programa.getId() == null){
                return;
            }else{
                setResponsePage(new PlanejamentoLicitacaoPage(new PageParameters(), backPage, programa,target,4));
            }
        }
        
        if("execucao".equalsIgnoreCase(pagina)){
            if(numeroAbaFaseAtiva < 5)
            {
                return;
            }else{
                setResponsePage(new AcompanharOrdemFornecimentoPage(new PageParameters(),programa,backPage,5));
            }
        }
        
        if("acompanhamento".equalsIgnoreCase(pagina)){
            if(numeroAbaFaseAtiva < 6)
            {
                return;
            }else{
                setResponsePage(new AcompanharOrdemFornecimentoPage(new PageParameters(),programa,backPage,6));
            }
        }
        
    }
}
