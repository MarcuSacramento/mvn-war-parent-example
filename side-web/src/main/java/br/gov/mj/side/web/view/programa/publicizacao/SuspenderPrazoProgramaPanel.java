package br.gov.mj.side.web.view.programa.publicizacao;

import java.util.Arrays;

import javax.inject.Inject;

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.component.InfraLocalDateTextField;
import br.gov.mj.side.entidades.enums.EnumTipoPrograma;
import br.gov.mj.side.web.util.Constants;

public class SuspenderPrazoProgramaPanel extends Panel {

    private static final String PT_BR = "pt-BR";

    private static final String DD_MM_YYYY = "dd/MM/yyyy";

    @Inject
    private ComponentFactory componentFactory;

    private static final long serialVersionUID = 1L;

    public SuspenderPrazoProgramaPanel(String id) {
        super(id);
        initComponents();
    }

    private void initComponents() {
        add(newDateTextFieldDataPublicacaoDOU());
        add(newDateTextFieldDataInicialProposta());
        add(newDateTextFieldDataFinalProposta());
        add(newDropDownTipoPrograma());
        add(newDateTextFieldDataInicialAnalise());
        add(newDateTextFieldDataFinalAnalise());
        add(newTextAreaMotivo());
    }

    private InfraLocalDateTextField newDateTextFieldDataInicialProposta() {
        InfraLocalDateTextField field = componentFactory.newDateTextFieldWithDatePicker("dataInicialProposta", "Período para propostas Início", true, null, DD_MM_YYYY, PT_BR);
        field.setEnabled(false);
        return field;
    }

    private InfraLocalDateTextField newDateTextFieldDataFinalProposta() {
        InfraLocalDateTextField field = componentFactory.newDateTextFieldWithDatePicker("dataFinalProposta", "Período para propostas Início", true, null, DD_MM_YYYY, PT_BR);
        field.setEnabled(false);
        return field;
    }
    
    private InfraLocalDateTextField newDateTextFieldDataInicialAnalise() {
        InfraLocalDateTextField field = componentFactory.newDateTextFieldWithDatePicker("dataInicialAnalise", "Período para análise das propostas Início", true, null, DD_MM_YYYY, PT_BR);
        field.setEnabled(false);
        return field;
    }
    
    private InfraLocalDateTextField newDateTextFieldDataFinalAnalise() {
        InfraLocalDateTextField field = componentFactory.newDateTextFieldWithDatePicker("dataFinalAnalise", "Período para análise das propostas Final", true, null, DD_MM_YYYY, PT_BR);
        field.setEnabled(false);
        return field;
    }

    private InfraDropDownChoice<EnumTipoPrograma> newDropDownTipoPrograma() {
        InfraDropDownChoice<EnumTipoPrograma> dropDown = componentFactory.newDropDownChoice("tipoPrograma", "Tipo de Programa", true, "valor", "descricao", null, Arrays.asList(EnumTipoPrograma.values()), null);
        dropDown.setEnabled(false);
        return dropDown;
    }

    private InfraLocalDateTextField newDateTextFieldDataPublicacaoDOU() {
        return componentFactory.newDateTextFieldWithDatePicker("dataPublicacaoDOU", "Data Publicação DOU", true, null, DD_MM_YYYY, PT_BR);
    }

    private TextArea<String> newTextAreaMotivo() {
        TextArea<String> textArea = new TextArea<String>("motivo");
        textArea.setLabel(Model.of("Motivo"));
        textArea.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        textArea.setRequired(true);
        return textArea;
    }
}
