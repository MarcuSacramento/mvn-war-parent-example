package br.gov.mj.side.web.view.programa.inscricao;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.util.InscricaoReportBuilder;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.template.TemplatePage;

public class ConfirmarInscricaoProgramaPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    private DadosProgramaPanel dadosProgramaPanel;
    private DadosEntidadePanel dadosEntidadePanel;
    private DadosRepresentantePanel dadosRepresentantePanel;
    private DadosSolicitacaoPanel dadosSolicitacaoPanel;
    private CriterioAvaliacaoInscricaoPanel criterioAvaliacaoInscricaoPanel;
    private CriterioElegebilidadeInscricaoPanel criterioElegebilidadeInscricaoPanel;

    private InscricaoPrograma inscricaoPrograma;
    private Form<InscricaoPrograma> form;
    private Page backPage;
    private Boolean aceite;

    @Inject
    private ComponentFactory componentFactory;
    
    @Inject
    private ProgramaService programaService;
    
    @Inject
    private InscricaoProgramaService inscricaoProgramaService;

    public ConfirmarInscricaoProgramaPage(PageParameters pageParameters, InscricaoPrograma inscricaoPrograma, Page backPage) {
        super(pageParameters);
        setBackPage(backPage);
        setTitulo("Confirmar dados para envio da inscrição no programa");
        setInscricaoPrograma(inscricaoPrograma);
        initComponents();
    }

    private void initComponents() {

        form = componentFactory.newForm("form", getInscricaoPrograma());

        dadosProgramaPanel = new DadosProgramaPanel("dadosProgramaPanel", getInscricaoPrograma().getPrograma());
        form.add(dadosProgramaPanel);

        dadosEntidadePanel = new DadosEntidadePanel("dadosEntidadePanel", getInscricaoPrograma().getPessoaEntidade().getEntidade());
        form.add(dadosEntidadePanel);

        dadosRepresentantePanel = new DadosRepresentantePanel("dadosRepresentantePanel", getInscricaoPrograma().getPessoaEntidade().getEntidade(), true);
        form.add(dadosRepresentantePanel);

        dadosSolicitacaoPanel = new DadosSolicitacaoPanel("dadosSolicitacaoPanel", getInscricaoPrograma(), true);
        form.add(dadosSolicitacaoPanel);

        criterioAvaliacaoInscricaoPanel = new CriterioAvaliacaoInscricaoPanel("criterioAvaliacaoInscricaoPanel", getInscricaoPrograma().getProgramasCriterioAvaliacao(), form, true);
        form.add(criterioAvaliacaoInscricaoPanel);

        criterioElegebilidadeInscricaoPanel = new CriterioElegebilidadeInscricaoPanel("criterioElegebilidadeInscricaoPanel", getInscricaoPrograma().getProgramasCriterioElegibilidade(), form, true);
        form.add(criterioElegebilidadeInscricaoPanel);

        form.add(newLabelMsgAceite());
        form.add(newCheckBoxAceite());

        form.add(componentFactory.newButton("btnVoltar", () -> voltar()));
        form.add(componentFactory.newButton("btnConfirmar", () -> confirmar()));
        form.add(componentFactory.newButton("btnExportarPdf", () -> exportarPdf()));

        add(form);

    }

    private void exportarPdf() {
        //busca os recursos financeiros para evitar lazy no cálculo do valor total do programa
        getInscricaoPrograma().getPrograma().setRecursosFinanceiros(programaService.buscarProgramaRecursoFinanceiroPeloIdPrograma(getInscricaoPrograma().getPrograma()));
        
        InscricaoReportBuilder builder = new InscricaoReportBuilder(SideUtil.getCaminhoCompletoRelatorios(getHttpServletRequest().getServletContext()));
        ResourceRequestHandler handler = new ResourceRequestHandler(builder.exportToPdf(getInscricaoPrograma()), getPageParameters());
        getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
    }

    private CheckBox newCheckBoxAceite() {
        return new CheckBox("aceite", new LambdaModel<Boolean>(this::getAceite, this::setAceite));
    }

    private Label newLabelMsgAceite() {
        return componentFactory.newLabel("msgAceite", getString("MT011"));
    }

    private void confirmar() {
        if (getAceite()) {
            inscricaoProgramaService.submeter(inscricaoPrograma, getUsuarioLogadoDaSessao().getLogin());
            getSession().info("Inscrição enviada com sucesso");
            setResponsePage(getApplication().getHomePage());
        } else {
            addMsgError("Para realizar a inscrição é necessário estar de acordo com os termos estabelecidos");
        }
    }

    protected void voltar() {
        setResponsePage(backPage);
    }

    public InscricaoPrograma getInscricaoPrograma() {
        return inscricaoPrograma;
    }

    public void setInscricaoPrograma(InscricaoPrograma inscricaoPrograma) {
        this.inscricaoPrograma = inscricaoPrograma;
    }

    public Page getBackPage() {
        return backPage;
    }

    public void setBackPage(Page backPage) {
        this.backPage = backPage;
    }

    public Boolean getAceite() {
        return aceite;
    }

    public void setAceite(Boolean aceite) {
        this.aceite = aceite;
    }

}
