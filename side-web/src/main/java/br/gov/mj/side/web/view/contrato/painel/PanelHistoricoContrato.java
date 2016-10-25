package br.gov.mj.side.web.view.contrato.painel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.util.convert.IConverter;

import br.gov.mj.side.entidades.programa.ProgramaHistoricoPublicizacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.web.service.PublicizacaoService;
import br.gov.mj.side.web.view.components.converters.LocalDateConverter;
import br.gov.mj.side.web.view.components.converters.LocalDateTimeConverter;

public class PanelHistoricoContrato extends Panel {

	private static final long serialVersionUID = 1L;

	private Contrato contrato;
	private List<ProgramaHistoricoPublicizacao> listaHistorico = new ArrayList<ProgramaHistoricoPublicizacao>();

	@Inject
	private PublicizacaoService publicizacaoService;

	public PanelHistoricoContrato(String id, Contrato contrato) {
		super(id);
		this.contrato = contrato;

		initComponents();
	}

	private void initComponents() {
		add(new PanelInformacoesHistoricoContrato("panelInformacoesHistoricoContrato"));
	}

	private class PanelInformacoesHistoricoContrato extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelInformacoesHistoricoContrato(String id) {
			super(id);
			setOutputMarkupId(true);

			if (contrato != null && contrato.getId() != null) {
				listaHistorico = publicizacaoService.buscarHistoricoVigenciaContrato(contrato);
			}
			add(newDataViewHistorico(listaHistorico));
		}
	}

	private DataView<ProgramaHistoricoPublicizacao> newDataViewHistorico(List<ProgramaHistoricoPublicizacao> lista) {
		return new DataView<ProgramaHistoricoPublicizacao>("historicoContrato", new ListDataProvider<ProgramaHistoricoPublicizacao>(lista)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<ProgramaHistoricoPublicizacao> item) {

				item.add(new Label("dataInicialVigenciaContrato", item.getModelObject().getDataInicialVigenciaContrato()) {
					@Override
					public <C> IConverter<C> getConverter(Class<C> type) {
						return (IConverter<C>) new LocalDateConverter();
					}
				});
				item.add(new Label("dataFinalVigenciaContrato", item.getModelObject().getDataFinalVigenciaContrato()) {
					@Override
					public <C> IConverter<C> getConverter(Class<C> type) {
						return (IConverter<C>) new LocalDateConverter();
					}
				});
				item.add(new Label("usuarioCadastro", item.getModelObject().getUsuarioCadastro()));
				item.add(new Label("dataCadastro", item.getModelObject().getDataCadastro()) {
					@Override
					public <C> IConverter<C> getConverter(Class<C> type) {
						return (IConverter<C>) new LocalDateTimeConverter();
					}
				});
			}
		};
	}
}
