package br.gov.mj.side.web.view.programa.publicizacao;

import java.time.LocalDate;
import java.util.Arrays;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraLocalDateTextField;
import br.gov.mj.side.entidades.enums.EnumTipoPrograma;

public class PublicarProgramaPanel extends Panel {

    private static final String PT_BR = "pt-BR";

    private static final String DD_MM_YYYY = "dd/MM/yyyy";
    
    private PanelAnalise panelAnalise;

    private InfraLocalDateTextField dataFimProposta;
    private InfraLocalDateTextField dataInicioAnalise;

    @Inject
    private ComponentFactory componentFactory;

    private static final long serialVersionUID = 1L;

    public PublicarProgramaPanel(String id) {
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
        add(componentFactory.newDateTextFieldWithDatePicker("dataPublicacaoDOU", "Data Publicação DOU", true, null, DD_MM_YYYY, PT_BR));
        add(componentFactory.newDateTextFieldWithDatePicker("dataInicialProposta", "Período para propostas Início", true, null, DD_MM_YYYY, PT_BR));
        add(newDateTextFieldDataFimProposta());// dataFinalProposta
        add(componentFactory.newDropDownChoice("tipoPrograma", "Tipo de Programa", true, "valor", "descricao", null, Arrays.asList(EnumTipoPrograma.values()), null));
        add(panelAnalise = new PanelAnalise("panelAnalise"));
    }
    
    
    private InfraLocalDateTextField newDateTextFieldDataFimProposta() {
        dataFimProposta = componentFactory.newDateTextFieldWithDatePicker("dataFinalProposta", "Período para propostas Final", true, null, DD_MM_YYYY, PT_BR);
        dataFimProposta.setEnabled(true);
        actionFinalPrazo(dataFimProposta);
        return dataFimProposta;
    }

    private InfraLocalDateTextField newDateTextFieldDataInicioAnalise() {
        dataInicioAnalise = componentFactory.newDateTextFieldWithDatePicker("dataInicialAnalise", "Período para análise das propostas Início", true, null, DD_MM_YYYY, PT_BR);
        dataInicioAnalise.setEnabled(false);
        return dataInicioAnalise;
    }

  //Ações
    private void actionFinalPrazo(InfraLocalDateTextField field)
    {
        field.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                LocalDate data=dataFimProposta.getModelObject();
                LocalDate dataSomada=data.plusDays(1);
                
                dataInicioAnalise.setModelObject(dataSomada);
                panelAnalise.addOrReplace(newDateTextFieldDataInicioAnalise());
                target.add(panelAnalise);
               
            }
        });
    }
}

