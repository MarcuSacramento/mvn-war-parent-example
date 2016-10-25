package br.gov.mj.side.web.view.programa.visualizacao;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.web.view.programa.anexo.PanelAnexoPage;

/**
 * Panel para visualizar as informações de um Programa
 * 
 * @author william.barreto
 */

public class ProgramaPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private Programa programa;

    private ProgramaInformacoesGeraisPanel informacoesGeraisPanel;
    private ProgramaRecursoFinanceiroPanel recursoFinanceiroPanel;
    private ProgramaOrgaoExecutorPanel orgaoExecutorPanel;
    private ProgramaPotenciaisBeneficiariosPanel potenciaisBeneficiariosPanel;
    private ProgramaBemKitPanel bemKitPanel;
    private ProgramaCriterioElegebilidadePanel criterioElegibilidadePanel;
    private ProgramaCriterioAcompanhamentoPanel criterioAcompanhamentoPanel;
    private ProgramaCriterioAvaliacaoPanel criterioAvaliacaoPanel;
    private PanelAnexoPage anexoPanel;
    private ProgramaAnexosPublicadosPanel programaAnexosPublicadosPanel;

    public ProgramaPanel(String id, Programa entity) {
        super(id, new CompoundPropertyModel<Programa>(entity));
        this.programa = entity;
        initComponents();
    }

    private void initComponents() {
        
        anexoPanel = new PanelAnexoPage("anexoPanel", programa, panelAnexosIsReadOnly());
        add(anexoPanel);

        programaAnexosPublicadosPanel = new ProgramaAnexosPublicadosPanel("programaAnexosPublicadosPanel", programa);
        
        //Somente será visivel caso exista algum item na lista publicado.
        programaAnexosPublicadosPanel.setVisible(programaAnexosPublicadosPanel.getMostrarPanelAnexosPublicados());
        add(programaAnexosPublicadosPanel);
    }
    
    private boolean panelAnexosIsReadOnly(){
        EnumStatusPrograma status = programa.getStatusPrograma();
        if (status == EnumStatusPrograma.EM_ELABORACAO || status == EnumStatusPrograma.FORMULADO) {
            return false;
        } else {
            return true;
        }
    }

}
