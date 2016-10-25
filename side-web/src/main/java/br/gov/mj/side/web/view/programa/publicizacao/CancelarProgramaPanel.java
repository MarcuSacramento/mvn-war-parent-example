package br.gov.mj.side.web.view.programa.publicizacao;

import javax.inject.Inject;

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.side.web.util.Constants;

public class CancelarProgramaPanel extends Panel {

    @Inject
    private ComponentFactory componentFactory;

    private static final long serialVersionUID = 1L;

    public CancelarProgramaPanel(String id) {
        super(id);

        initComponents();
    }

    private void initComponents() {
        add(componentFactory.newDateTextFieldWithDatePicker("dataPublicacaoDOU", "Data Publicação DOU", true, null, "dd/MM/yyyy", "pt-BR"));
        add(newTextAreaMotivo());
    }

    private TextArea<String> newTextAreaMotivo() {
        TextArea<String> textArea = new TextArea<String>("motivo");
        textArea.setLabel(Model.of("Motivo"));
        textArea.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        textArea.setRequired(true);
        return textArea;
    }
}
