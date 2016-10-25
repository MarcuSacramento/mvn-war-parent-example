package br.gov.mj.side.web.view.programa.publicizacao;

import java.time.LocalDate;
import java.util.Arrays;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.component.InfraLocalDateTextField;
import br.gov.mj.side.entidades.enums.EnumTipoPrograma;
import br.gov.mj.side.web.util.Constants;

public class ProrrogarPrazoAnaliseProgramaPanel extends Panel {

    private static final String PT_BR = "pt-BR";

    private static final String DD_MM_YYYY = "dd/MM/yyyy";

    private PanelAnalise panelAnalise;

    private InfraLocalDateTextField dataFimProposta;
    private InfraLocalDateTextField dataInicioAnalise;

    @Inject
    private ComponentFactory componentFactory;

    private static final long serialVersionUID = 1L;

    public ProrrogarPrazoAnaliseProgramaPanel(String id) {
        super(id);
        initComponents();
    }

    private class PanelAnalise extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelAnalise(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newDateTextFieldDataInicioAnalise());// dataInicialAnalise
            add(componentFactory.newDateTextFieldWithDatePicker("dataFinalAnalise", "Período para análise das propostas Final", true, null, DD_MM_YYYY, PT_BR));
        }
    }

    private void initComponents() {
        add(componentFactory.newLabel("msgAlerta", "MT001"));
        add(newDateTextFieldDataPublicacaoDOU());
        add(newDateTextFieldDataInicioProposta());// dataInicialProposta
        add(newDateTextFieldDataFimProposta());// dataFinalProposta
        add(newDropDownTipoPrograma());
        add(newTextAreaMotivo());

        add(panelAnalise = new PanelAnalise("panelAnalise"));
    }

    private InfraLocalDateTextField newDateTextFieldDataInicioProposta() {
        InfraLocalDateTextField dataInicioProposta = componentFactory.newDateTextFieldWithDatePicker("dataInicialProposta", "Período para propostas Início", true, null, DD_MM_YYYY, PT_BR);
        dataInicioProposta.setEnabled(false);
        return dataInicioProposta;
    }
    
    private InfraLocalDateTextField newDateTextFieldDataFimProposta() {
        dataFimProposta = componentFactory.newDateTextFieldWithDatePicker("dataFinalProposta", "Período para propostas Final", true, null, DD_MM_YYYY, PT_BR);
        dataFimProposta.setEnabled(false);
        return dataFimProposta;
    }

    private InfraLocalDateTextField newDateTextFieldDataInicioAnalise() {
        dataInicioAnalise = componentFactory.newDateTextFieldWithDatePicker("dataInicialAnalise", "Período para análise das propostas Início", true, null, DD_MM_YYYY, PT_BR);
        dataInicioAnalise.setEnabled(false);
        return dataInicioAnalise;
    }

    private TextArea<String> newTextAreaMotivo() {
        TextArea<String> textArea = new TextArea<String>("motivo");
        textArea.setLabel(Model.of("Motivo"));
        textArea.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        textArea.setRequired(true);
        return textArea;
    }

    private InfraDropDownChoice<EnumTipoPrograma> newDropDownTipoPrograma() {
        InfraDropDownChoice<EnumTipoPrograma> dropDown = componentFactory.newDropDownChoice("tipoPrograma", "Tipo de Programa", true, "valor", "descricao", null, Arrays.asList(EnumTipoPrograma.values()), null);
        dropDown.setEnabled(false);
        return dropDown;
    }

    private InfraLocalDateTextField newDateTextFieldDataPublicacaoDOU() {
        return componentFactory.newDateTextFieldWithDatePicker("dataPublicacaoDOU", "Data Publicação DOU", true, null, DD_MM_YYYY, PT_BR);
    }  
}
