package br.gov.mj.side.web.view.programa.contrato.ordemfornecimento;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.side.entidades.enums.EnumTipoMinuta;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.HistoricoComunicacaoGeracaoOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ItensOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;
import br.gov.mj.side.web.dto.EntregaPrevistaDto;
import br.gov.mj.side.web.dto.MinutaOrdemFornecedorDto;
import br.gov.mj.side.web.service.ContratoService;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.DataUtil;
import br.gov.mj.side.web.util.MascaraUtils;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.planejarLicitacao.ContratoPanelBotoes;
import br.gov.mj.side.web.view.programa.PanelFasePrograma;
import br.gov.mj.side.web.view.programa.contrato.comunicarFornecedor.ComunicarFornecedorPage;
import br.gov.mj.side.web.view.template.TemplatePage;

public class MinutaOrdemFornecimentoPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    // ##################### variaveis ########################

    private Form<MinutaOrdemFornecimentoPage> form;
    private Programa programa;
    private OrdemFornecimentoContrato ordemFornecimento;
    private Page backPage;
    private EnumTipoMinuta tipoMinuta;
    private boolean veioDaPaginaDeComunicar = false;

    private List<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> listaHistorico = new ArrayList<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato>();

    // ##################### paineis ##########################

    private PanelFasePrograma panelFasePrograma;
    private ContratoPanelBotoes execucaoPanelBotoes;
    private PanelGerarMinuta panelGerarMinuta;
    private PanelPrincipal panelPrincipal;
    private PanelDataView panelDataView;

    // ##################### Componentes Wicket ###############

    private DataView<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> dataViewHistorico;

    // ##################### Injeções de dependências #########

    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private ProgramaService programaService;
    @Inject
    private ContratoService contratoService;
    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;

    public MinutaOrdemFornecimentoPage(final PageParameters pageParameters, Programa programa, OrdemFornecimentoContrato ordemFornecimento, Page backPage, boolean veioDaPaginaDeComunicar) {
        super(pageParameters);
        this.programa = programa;
        this.ordemFornecimento = ordemFornecimento;
        this.backPage = backPage;
        this.veioDaPaginaDeComunicar = veioDaPaginaDeComunicar;

        setTitulo("Gerenciar Programa");
        initComponents();

    }

    private void initComponents() {
        form = new Form<MinutaOrdemFornecimentoPage>("form", new CompoundPropertyModel<MinutaOrdemFornecimentoPage>(this));
        add(form);

        form.add(execucaoPanelBotoes = new ContratoPanelBotoes("execucaoPanelPotoes", programa, backPage, "ordemFornecimento"));
        form.add(panelFasePrograma = new PanelFasePrograma("panelFasePrograma", programa, backPage));
        form.add(panelPrincipal = new PanelPrincipal("panelPrincipal"));

        form.add(newButtonVoltar()); // btnVoltar
        form.add(newButtonVoltarSemMinuta()); //btnVoltarSemMinuta 
    }

    // PAINEIS

    private class PanelPrincipal extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelPrincipal(String id) {
            super(id);
            setOutputMarkupId(true);

            add(panelGerarMinuta = new PanelGerarMinuta("panelGerarMinuta"));
            add(panelDataView = new PanelDataView("panelDataView"));

        }
    }

    private class PanelGerarMinuta extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelGerarMinuta(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newDropDownTipoMinuta()); // dropEscolhaMinuta
            add(newButtonVisualizarMinuta()); // btnVisualizarMinuta
            add(newButtonGerarMinuta()); // btnGerarMinuta
        }
    }

    private class PanelDataView extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelDataView(String id) {
            super(id);
            setOutputMarkupId(true);

            add(newDataViewHistorico()); // listaHistorico
            add(new InfraAjaxPagingNavigator("pagination", dataViewHistorico));

        }
    }

    // Componentes

    private InfraDropDownChoice<EnumTipoMinuta> newDropDownTipoMinuta() {
        List<EnumTipoMinuta> listaEnum = Arrays.asList(EnumTipoMinuta.values());
        InfraDropDownChoice<EnumTipoMinuta> dropDownChoice = componentFactory.newDropDownChoice("dropEscolhaMinuta", "Escolha Minuta", false, "valor", "descricao", new PropertyModel<EnumTipoMinuta>(this, "tipoMinuta"), listaEnum, null);
        dropDownChoice.setNullValid(true);
        return dropDownChoice;
    }

    private Button newButtonVisualizarMinuta() {
        Button btnDownload = componentFactory.newButton("btnVisualizarMinuta", () -> visualizarMinuta());
        btnDownload.setVisible(true);
        return btnDownload;
    }
    
    private Button newButtonDownloadMinuta(Item<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> item){
        Button btnDownload = componentFactory.newButton("btnVisualizarMinuta", () -> download(item));
        btnDownload.setVisible(true);
        return btnDownload;
    }
    
    private InfraAjaxFallbackLink newButtonGerarMinuta(){
        InfraAjaxFallbackLink btnMinuta = componentFactory.newAjaxFallbackLink("btnGerarMinuta", (target) -> gerarMinuta(target));
        btnMinuta.setOutputMarkupId(true);
        btnMinuta.setVisible(visualizarBotaoGerarMinuta());
        return btnMinuta;
    }
    
    private AjaxSubmitLink newButtonVoltar() {
        AjaxSubmitLink buttonVoltar = new AjaxSubmitLink("btnVoltar", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                actionVoltar(target);
            }
        };
        buttonVoltar.setVisible(!veioDaPaginaDeComunicar);
        return buttonVoltar;
    }

    private DataView<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> newDataViewHistorico() {
        dataViewHistorico = new DataView<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato>("listaHistorico", new HistoricoProvider()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> item) {

                String dataGeracao = DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataGeracao(), "dd/MM/yyyy HH:mm:ss");
                String dataComunicacao = DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataComunicacao(), "dd/MM/yyyy HH:mm:ss");
                String dataCancelamento = DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataCancelamento(), "dd/MM/yyyy HH:mm:ss");

                item.add(new Label("dataGeracao", dataGeracao));
                item.add(new Label("dataComunicacao", dataComunicacao));
                item.add(new Label("dataCancelamento",dataCancelamento));
                item.add(new Label("motivoCancelamento"));
                item.add(newButtonDownloadMinuta(item)); //btnVisualizarMinuta
            }
        };
        dataViewHistorico.setItemsPerPage(Constants.ITEMS_PER_PAGE_PAGINATION);
        return dataViewHistorico;
    }
    
    public AjaxSubmitLink newButtonVoltarSemMinuta() {
        AjaxSubmitLink buttonVoltar = new AjaxSubmitLink("btnVoltarSemMinuta", form) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                actionVoltarSemMinuta(target);
            }
        };
        buttonVoltar.setVisible(veioDaPaginaDeComunicar);
        
        return buttonVoltar;
    }
	

    // PROVIDER

    public class HistoricoProvider extends SortableDataProvider<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato, String> {
        private static final long serialVersionUID = 1L;

        public HistoricoProvider() {
            listaHistorico = ordemFornecimentoContratoService.buscarHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(ordemFornecimento,false);
        }

        @Override
        public Iterator<? extends HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> iterator(long first, long size) {
            List<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> listaRetorno = new ArrayList<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato>();
            
            int firstTemp = 0;
			int flagTemp = 0;
			for (HistoricoComunicacaoGeracaoOrdemFornecimentoContrato k : listaHistorico) {
				if (firstTemp >= first) {
					if (flagTemp <= size) {
						listaRetorno.add(k);
						flagTemp++;
					}
				}
				firstTemp++;
			}
        	
        	return listaRetorno.iterator();
        }

        @Override
        public long size() {
            return listaHistorico.size();
        }

        @Override
        public IModel<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> model(HistoricoComunicacaoGeracaoOrdemFornecimentoContrato object) {
            return new CompoundPropertyModel<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato>(object);
        }
    }

    // AÇÕES
    
    private void actionVoltarSemMinuta(AjaxRequestTarget target){
    	setResponsePage(new ComunicarFornecedorPage(new PageParameters(), programa, ordemFornecimento, backPage));
	}
    
    private boolean visualizarBotaoGerarMinuta(){
        HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historico = ordemFornecimentoContratoService.buscarUltimoHistoricoComunicacaoGeracaoOrdemFornecimentoContrato(ordemFornecimento, false);
        if(historico == null){
            return true;
        }else{
            if(historico.getPossuiCancelamento() == null){
            	return true;
            }else{
            	if(historico.getPossuiComunicado()){
            		if(historico.getPossuiCancelamento()){
            			return true;
            		}else{
            			return false;
            		}
            	}else{
            		return true;
            	}
            }
        }
    }

    private void download(Item<HistoricoComunicacaoGeracaoOrdemFornecimentoContrato> item) {
        SideUtil.download(item.getModelObject().getMinutaGerada(), (item.getModelObject().getNumeroDocumentoSei()==null?"-":item.getModelObject().getNumeroDocumentoSei().toString()) +".pdf");
    }

    private void visualizarMinuta() {
        if (tipoMinuta != null) {
            MinutaOfBuilder jasper = new MinutaOfBuilder(SideUtil.getCaminhoCompletoRelatorios(getHttpServletRequest().getServletContext()));
            jasper.setNomeMinuta("MinutaTR - " + "teste");
            jasper.setTipoMinuta(tipoMinuta);
            jasper.setMinutaOrdemFornecedorDto(popularMinuta());
            ByteArrayOutputStream exportar = jasper.exportToByteArray();

            SideUtil.download(exportar.toByteArray(), "OF_"+programa.getNomePrograma() + tipoDeMinutaAGerar());
           
            addMsgInfo("Download da minuta realizada com sucesso.");
        }else{
            addMsgError("Selecione o tipo de minuta a ser gerada.");
        }
    }

    private MinutaOrdemFornecedorDto popularMinuta(){
    	MinutaOrdemFornecedorDto minutaOrdemFornecedorDto = new MinutaOrdemFornecedorDto();
        minutaOrdemFornecedorDto.setNomeFornecedor(ordemFornecimento.getContrato().getFornecedor().getNomeEntidade());
        minutaOrdemFornecedorDto.setNumeroContrato(ordemFornecimento.getContrato().getNumeroContrato());
        minutaOrdemFornecedorDto.setPeriodoVigenciaContrato(formatarDataBr(ordemFornecimento.getContrato().getDataVigenciaFimContrato()));
        minutaOrdemFornecedorDto.setNomeProgramaContratante(ordemFornecimento.getContrato().getPrograma().getUnidadeExecutora().getNomeUnidadeExecutora());
        minutaOrdemFornecedorDto.setNomePrepostoContrato(ordemFornecimento.getContrato().getPreposto().getNomePessoa());
        minutaOrdemFornecedorDto.setTelefonePreposto(MascaraUtils.formatarMascaraTelefone(ordemFornecimento.getContrato().getPreposto().getNumeroTelefone()));
        minutaOrdemFornecedorDto.setEmailPreposto(ordemFornecimento.getContrato().getPreposto().getEmail());
        
        List<ItensOrdemFornecimentoContrato> listaOrdemFornecTemp = ordemFornecimentoContratoService.buscarItensOrdemFornecimentoContrato(ordemFornecimento);
        List<EntregaPrevistaDto> listaEntregaPrevistaDto = new ArrayList<EntregaPrevistaDto>();
        EntregaPrevistaDto entregaPrevistaDto;
        for(ItensOrdemFornecimentoContrato itens : listaOrdemFornecTemp){
        	entregaPrevistaDto = new EntregaPrevistaDto();
        	entregaPrevistaDto.setNomeBem(itens.getItem().getNomeBem());
        	entregaPrevistaDto.setQuantidade(itens.getQuantidade());
        	entregaPrevistaDto.setEnderecoCompleto(itens.getLocalEntrega().getEnderecoCompleto());
        	listaEntregaPrevistaDto.add(entregaPrevistaDto);
        }
        minutaOrdemFornecedorDto.setValorEstimadoBem(MascaraUtils.formatarMascaraDinheiro(contratoService.buscarValorTotalDaOrdemDeFornecimento(ordemFornecimento)));
        minutaOrdemFornecedorDto.setListaEntregaPrevistaDto(listaEntregaPrevistaDto);
		return minutaOrdemFornecedorDto;
	}

	private void gerarMinuta(AjaxRequestTarget target) {
        MinutaOfBuilder jasper = new MinutaOfBuilder(SideUtil.getCaminhoCompletoRelatorios(getHttpServletRequest().getServletContext()));
        jasper.setNomeMinuta("MinutaTR - " + "teste");
        jasper.setTipoMinuta(EnumTipoMinuta.PDF);
        jasper.setMinutaOrdemFornecedorDto(popularMinuta());
        ByteArrayOutputStream exportar = jasper.exportToByteArray();
        ordemFornecimentoContratoService.gerarMinuta(ordemFornecimento, exportar.toByteArray());

        panelDataView.addOrReplace(newDataViewHistorico());
        target.add(panelDataView);
    }

    private String tipoDeMinutaAGerar() {
        if (tipoMinuta == EnumTipoMinuta.DOC) {
            return ".doc";
        } else {
            if (tipoMinuta == EnumTipoMinuta.ODT) {
                return ".odt";
            } else {
                if (tipoMinuta == EnumTipoMinuta.PDF) {
                    return ".pdf";
                } else {
                    return ".html";
                }
            }
        }
    }

    private void actionVoltar(AjaxRequestTarget target) {
        setResponsePage(new OrdemFornecimentoPesquisaPage(new PageParameters(), programa, backPage,ordemFornecimento.getContrato()));
    }

    public String formatarDataBr(LocalDate dataDocumento) {
        DateTimeFormatter sdfPadraoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (dataDocumento != null) {
            dataDocumento.format(sdfPadraoBR);
            return sdfPadraoBR.format(dataDocumento);
        }
        return " - ";
    }
}
