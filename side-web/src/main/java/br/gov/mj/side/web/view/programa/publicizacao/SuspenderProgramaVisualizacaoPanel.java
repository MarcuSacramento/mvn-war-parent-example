package br.gov.mj.side.web.view.programa.publicizacao;

import java.time.LocalDate;

import javax.inject.Inject;

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraLocalDateTextField;
import br.gov.mj.side.entidades.programa.ProgramaHistoricoPublicizacao;
import br.gov.mj.side.web.util.Constants;

public class SuspenderProgramaVisualizacaoPanel extends Panel {

    @Inject
    private ComponentFactory componentFactory;
    
    private ProgramaHistoricoPublicizacao historico;

    private static final long serialVersionUID = 1L;
    
    public SuspenderProgramaVisualizacaoPanel(String id, ProgramaHistoricoPublicizacao ultimoHistorico) {
        super(id);
        this.historico = ultimoHistorico;
        initComponents();
    }

    private void initComponents() {
        add(newDate());
        add(newTextAreaMotivo());
    }
    
    private InfraLocalDateTextField newDate(){
        InfraLocalDateTextField date = componentFactory.newDateTextFieldWithDatePicker("dataPublicacaoDOU", "Data Publicação DOU", true, new PropertyModel<LocalDate>(this,"historico.dataPublicacaoDOU"), "dd/MM/yyyy", "pt-BR");
        date.setConvertedInput(historico.getDataFinalAnalise());
        date.setEnabled(false);
        return date;
    }

    private TextArea<String> newTextAreaMotivo() {
        TextArea<String> textArea = new TextArea<String>("motivo",new PropertyModel<String>(this, "historico.motivo"));
        textArea.setLabel(Model.of("Motivo"));
        textArea.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        textArea.setRequired(true);
        textArea.setEnabled(false);
        return textArea;
    }

    public ProgramaHistoricoPublicizacao getHistorico() {
        return historico;
    }

    public void setHistorico(ProgramaHistoricoPublicizacao historico) {
        this.historico = historico;
    }
}
