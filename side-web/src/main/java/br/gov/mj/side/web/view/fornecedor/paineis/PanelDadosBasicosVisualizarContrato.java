package br.gov.mj.side.web.view.fornecedor.paineis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.convert.IConverter;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.web.view.components.converters.NumeroProcessoSeiConverter;
import br.gov.mj.side.web.view.fornecedor.ContratoPage2;

public class PanelDadosBasicosVisualizarContrato extends Panel {

	private static final long serialVersionUID = 1L;

	private PanelInformacoesContrato panelInformacoesContrato;
	private Contrato contrato;
	private Page backPage;
	private ContratoPage2 page;
	private boolean readOnly;


	public PanelDadosBasicosVisualizarContrato(String id, Page backPage, Contrato contrato, boolean cadastroNovo, boolean readOnly) {
		super(id);
		setOutputMarkupId(true);
		this.backPage = backPage;
		this.contrato = contrato;
		this.readOnly = readOnly;
		initVariaveis();
		initComponents();
	}

	private void initComponents() {
		panelInformacoesContrato = new PanelInformacoesContrato("panelInformacoesContrato");
		add(panelInformacoesContrato);
	}

	private void initVariaveis() {
		page = (ContratoPage2) backPage;
		contrato = page.getForm().getModelObject();
	}

	private class PanelInformacoesContrato extends WebMarkupContainer {

		private static final long serialVersionUID = 1L;

		public PanelInformacoesContrato(String id) {
			super(id);
			setOutputMarkupId(true);

			add(newLabelDataVigenciaInicioContrato());
			add(newLabelDataVigenciaFimContrato());
			add(newLabelNumeroProcessoSEI());
		}

	}

	// Criação dos componentes do panel

	private Label newLabelDataVigenciaInicioContrato() {
        return new Label("dataVigenciaInicioContrato", formatarDataBr(contrato.getDataVigenciaInicioContrato()));
    }
	
	private Label newLabelDataVigenciaFimContrato() {
        return new Label("dataVigenciaFimContrato", formatarDataBr(contrato.getDataVigenciaFimContrato()));
    }

	private Label newLabelNumeroProcessoSEI() {
		 return new Label("numeroProcessoSEI") {
	            private static final long serialVersionUID = 1L;

	            @SuppressWarnings("unchecked")
	            @Override
	            public <C> IConverter<C> getConverter(Class<C> type) {
	                return (IConverter<C>) new NumeroProcessoSeiConverter();
	            }
	        };
    }
	
	public void atualizaPanel(AjaxRequestTarget target, Contrato contrato) {
		panelInformacoesContrato.addOrReplace(newLabelNumeroProcessoSEI());
		target.add(panelInformacoesContrato);
	}
	
	public String formatarDataBr(LocalDate dataDocumento) {
        DateTimeFormatter sdfPadraoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (dataDocumento != null) {
            dataDocumento.format(sdfPadraoBR);
            return sdfPadraoBR.format(dataDocumento);
        }
        return " - ";
    }

	public boolean isReadOnly() {
		return readOnly;
	}

	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}

}
