package br.gov.mj.side.web.view.fornecedor.paineis;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.side.entidades.enums.EnumTipoArquivoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ContratoAnexo;
import br.gov.mj.side.web.dto.AnexoDto;
import br.gov.mj.side.web.service.ContratoAnexoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.DataUtil;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.components.EntityDataProvider;

public class PanelVisualizarContratoAnexos extends Panel {
	private static final long serialVersionUID = 1L;

	private PanelPrincipalAnexo panelPrincipalAnexo;

	private Button btnDownload;
	private Integer itensPorPagina = Constants.ITEMS_PER_PAGE_PAGINATION;

	private DropDownChoice<EnumTipoArquivoContrato> drop;
	private EnumTipoArquivoContrato tipoArquivoContrato;
	private Contrato contrato;
	private boolean readOnly;

	private List<FileUpload> uploads;
	private List<ContratoAnexo> listAnexoTemp = new ArrayList<ContratoAnexo>();
	private Label labelMensagem;
	private Model<String> mensagem = Model.of("");

	@Inject
	private ComponentFactory componentFactory;

	@Inject
	private ContratoAnexoService contratoAnexoService;

	public PanelVisualizarContratoAnexos(String id, Contrato contrato, boolean readOnly) {
		super(id);
		setOutputMarkupId(true);
		this.contrato = contrato;
		this.readOnly = readOnly;

		initVariaveis();
		initComponents();
	}

	private class PanelPrincipalAnexo extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelPrincipalAnexo(String id) {
			super(id);
			DataView<ContratoAnexo> dataView = getDataViewAnexos("anexos");
			add(labelMensagem);
            add(dataView);
            add(new InfraAjaxPagingNavigator("pagination", dataView));
		}
	}

	public void initVariaveis() {
		listAnexoTemp = new ArrayList<ContratoAnexo>();

		if (contrato != null && contrato.getId() != null) {
			listAnexoTemp = SideUtil.convertAnexoDtoToEntityContratoAnexo(contratoAnexoService.buscarPeloIdContrato(contrato.getId()));
			
		}

		if (uploads == null) {
			uploads = new ArrayList<FileUpload>();
		}

		labelMensagem = new Label("mensagemAnexo", mensagem);
		labelMensagem.setEscapeModelStrings(false);
		labelMensagem.setOutputMarkupId(true);
	}

	private void initComponents() {
		add(panelPrincipalAnexo = new PanelPrincipalAnexo("panelInformacoesAnexo"));
	}

	// ==============================
	// CRIAÇÃO DOS COMPONENTES
	private DataView<ContratoAnexo> getDataViewAnexos(String id) {
		DataView<ContratoAnexo> dataView = new DataView<ContratoAnexo>(id, new EntityDataProvider<ContratoAnexo>(listAnexoTemp)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<ContratoAnexo> item) {
				item.add(new Label("descricao", item.getModelObject().getTipoArquivoContrato().getDescricao()));
				item.add(new Label("nomeAnexo", item.getModelObject().getNomeAnexo()));
				item.add(new Label("tamanhoArquivoEmMB", item.getModelObject().getTamanhoArquivoEmMB()));
				item.add(new Label("dataAnexoTable", DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataDocumento(), "dd/MM/yyyy")));
				item.add(new Label("dataCadastro", DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataCadastro(), "dd/MM/yyyy HH:mm:ss")));

				btnDownload = componentFactory.newButton("btnDonwload", () -> download(item));
				btnDownload.setEnabled(true);
				item.add(btnDownload);

				
			}
		};
		dataView.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
		return dataView;
	}

	// ====================================
	// AS AÇÕES SÃO IMPLEMENTADAS ABAIXO

	private void download(Item<ContratoAnexo> item) {
		ContratoAnexo a = item.getModelObject();
                if (a.getId() != null) {
                    AnexoDto retorno = contratoAnexoService.buscarPeloId(a.getId());
                    SideUtil.download(retorno.getConteudo(), retorno.getNomeAnexo());
                } else {
                    SideUtil.download(a.getConteudo(), a.getNomeAnexo());
                }
                
	}

	public void actionDropDownTipoArquivo(DropDownChoice<EnumTipoArquivoContrato> dropElemento) {
		dropElemento.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {

				tipoArquivoContrato = drop.getModelObject();
			}
		});
	}

	// GETTERS E SETTERS

	public List<ContratoAnexo> getListAnexoTemp() {
		return listAnexoTemp;
	}

	public void setListAnexoTemp(List<ContratoAnexo> listAnexoTemp) {
		this.listAnexoTemp = listAnexoTemp;
	}

	public EnumTipoArquivoContrato getTipoArquivoContrato() {
		return tipoArquivoContrato;
	}

	public void setTipoArquivoContrato(EnumTipoArquivoContrato tipoArquivoContrato) {
		this.tipoArquivoContrato = tipoArquivoContrato;
	}

	public PanelPrincipalAnexo getPanelPrincipalAnexo() {
		return panelPrincipalAnexo;
	}

	public void setPanelPrincipalAnexo(PanelPrincipalAnexo panelPrincipalAnexo) {
		this.panelPrincipalAnexo = panelPrincipalAnexo;
	}

	public Button getBtnDownload() {
		return btnDownload;
	}

	public void setBtnDownload(Button btnDownload) {
		this.btnDownload = btnDownload;
	}

	public DropDownChoice<EnumTipoArquivoContrato> getDrop() {
		return drop;
	}

	public void setDrop(DropDownChoice<EnumTipoArquivoContrato> drop) {
		this.drop = drop;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Integer getItensPorPagina() {
		return itensPorPagina;
	}

	public void setItensPorPagina(Integer itensPorPagina) {
		this.itensPorPagina = itensPorPagina;
	}

}
