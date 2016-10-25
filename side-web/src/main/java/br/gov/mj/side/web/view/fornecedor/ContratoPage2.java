package br.gov.mj.side.web.view.fornecedor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxFallbackLink;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.view.HomePage;
import br.gov.mj.side.web.view.components.AjaxDialogButton;
import br.gov.mj.side.web.view.fornecedor.paineis.PanelDadosBasicosVisualizarContrato;
import br.gov.mj.side.web.view.fornecedor.paineis.PanelHistoricoVisualizarContrato;
import br.gov.mj.side.web.view.fornecedor.paineis.PanelObjetosVisualizarContrato;
import br.gov.mj.side.web.view.fornecedor.paineis.PanelVisualizarContratoAnexos;
import br.gov.mj.side.web.view.fornecedor.paineis.PanelVisualizarDadosFornecedor;
import br.gov.mj.side.web.view.template.TemplatePage;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Backdrop;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.TextContentModal;

/**
 * 
 * @author joao.coutinho
 * @since 22/03/2016 - Tela "Visualizar Contrato",
 *                     do botão "Visualizar Registro",
 *                     na tela "Contrato".
 * 
 */
@AuthorizeInstantiation({ 
	ContratoPage2.ROLE_MANTER_CONTRATO_FORNECEDOR_VISUALIZAR
})
public class ContratoPage2 extends TemplatePage{
	
	public static final String ROLE_MANTER_CONTRATO_FORNECEDOR_VISUALIZAR	= "manter_contrato_fornecedor:visualizar";	

	private static final long serialVersionUID = 1L;
    private Form<Contrato> form;
    private Contrato contrato = new Contrato();
    private Programa programa;
    private Bem bem;
    private List<AgrupamentoLicitacao> listaGrupoItem = new ArrayList<AgrupamentoLicitacao>();
    private String numeroContrato;
    private String codigoPrograma;
    private Uf uf;
    private Entidade fornecedor;
    private PanelDadosBasicosVisualizarContrato panelDadosBasicosContrato;
    private PanelObjetosVisualizarContrato panelObjetosContrato;
    private PanelVisualizarDadosFornecedor panelDadosFornecedor;
    private PanelVisualizarContratoAnexos panelContratoAnexos;
    private PanelHistoricoVisualizarContrato panelHistoricoContrato;
    private InfraDropDownChoice<Programa> dropDownPrograma;
    private boolean cadastroNovo = false;
    private Page backPage;
    private boolean readOnly;
    private boolean habilitarTodosCampos = false;
    private PanelButton panelButton;
    private PanelBreadcrumb panelBreadcrumb;
    private AjaxSubmitLink buttonVoltar;

    // Modal
    private String msgConfirm;
    private Modal<String> modalConfirm;

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private ProgramaService programaService;

	public ContratoPage2(final PageParameters pageParameters, Page backPage, Contrato contrato, boolean readOnly){
		 super(pageParameters);

	        this.backPage = backPage;
	        this.contrato = contrato;
	        this.readOnly = readOnly;
	        this.habilitarTodosCampos = false;

	        initVariaveis();
	        initComponents();
	        chamadoNoPrimeiroAcesso();
	}

	// Este metodo irá desabilitar todos os paineis no primeiro acesso a página
    private void chamadoNoPrimeiroAcesso() {
        panelDadosBasicosContrato.setEnabled(!readOnly && habilitarTodosCampos);
        dropDownPrograma.setEnabled(!readOnly && habilitarTodosCampos);
        panelDadosFornecedor.setEnabled(!readOnly && habilitarTodosCampos);
        panelObjetosContrato.setEnabled(!readOnly && habilitarTodosCampos);
        panelDadosFornecedor.setEnabled(!readOnly && habilitarTodosCampos);
    }

    /*
     * Quando for pesquisado um contrato pelo número e encontrado será chamado
     * este construtor abaixo a variavel pesquisado informa se a chamada a este
     * construtor veio do resultado da pesquisa pelo código ou se veio da página
     * de pesquisa de contrato
     */
    public ContratoPage2(final PageParameters pageParameters, Page backPage, Contrato contrato, Programa programa,boolean readOnly, boolean pesquisado) {
        super(pageParameters);

        this.backPage = backPage;
        this.contrato = contrato;
        this.programa = programa;
        this.readOnly = readOnly;
        
        if(contrato != null && contrato.getId()!=null){
            this.habilitarTodosCampos = true;
        }

        initVariaveis();
        initComponents();

        if (pesquisado) {
            setMsgConfirm("Contrato já cadastrado, os dados foram carregados.");
            modalConfirm.show(true);
        }

        chamadoNoPrimeiroAcesso();
    }

    private void initVariaveis() {
        setTitulo("Visualizar Contrato");
        
        if (contrato != null && contrato.getId() != null) {
            cadastroNovo = false;
        } else {
            cadastroNovo = true;
        }        
        carregarListasQuandoForEditarContrato();
    }
    

    private void initComponents() {
        form = componentFactory.newForm("form", new CompoundPropertyModel<Contrato>(contrato));
        panelBreadcrumb = newPanelBreadcrumb();
        panelBreadcrumb.setVisible(readOnly && habilitarTodosCampos);
        form.add(panelBreadcrumb);
        form.add(newLabelCodigoPrograma());
	form.add(newLabelNumeroContrato());
        form.add(panelDadosBasicosContrato = new PanelDadosBasicosVisualizarContrato("panelDadosBasicosContrato", this, contrato, cadastroNovo, readOnly));
        form.add(newDropDownPrograma());
        form.add(panelObjetosContrato = new PanelObjetosVisualizarContrato("panelObjetosContrato", this, contrato, listaGrupoItem, cadastroNovo));
        form.add(panelDadosFornecedor = new PanelVisualizarDadosFornecedor("panelDadosFornecedor", this, contrato, cadastroNovo, getUsuarioLogadoDaSessao()));
        form.add(panelContratoAnexos = new PanelVisualizarContratoAnexos("panelAnexos", contrato, readOnly));
        form.add(panelHistoricoContrato = new PanelHistoricoVisualizarContrato("panelHistorico", contrato));

        // Modal
        modalConfirm = newModal("modalConfirm");
        modalConfirm.show(false);
        form.add(panelButton = newPanelButton());
        form.add(modalConfirm);

        add(form);
    }

    // CRIAÇÃO DOS COMPONENTES

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private InfraDropDownChoice<Programa> newDropDownPrograma() {
        Programa programa = new Programa();
        programa.setStatusPrograma(EnumStatusPrograma.ABERTO_GERACAO_CONTRATO);
        List<Programa> listaProgramas = programaService.buscar(programa);
        dropDownPrograma = componentFactory.newDropDownChoice("dropDownPrograma", "Programa", true, "id", "nomePrograma", new PropertyModel(this, "contrato.programa"), listaProgramas, null);
        atualizaDropDownGrupoItem(dropDownPrograma);
        dropDownPrograma.setNullValid(true);
        dropDownPrograma.setVisible(false);
        return dropDownPrograma;
    }
    private Label newLabelCodigoPrograma() {
        return new Label("codigoPrograma", codigoPrograma);
    }

    private Label newLabelNumeroContrato() {
        return new Label("numeroContrato", numeroContrato);
    }
    
    /**
     * Sprint 10 - painel do breadcrumb
     * @author joao.coutinho
     * @since 21/03/2016
     */
    public PanelBreadcrumb newPanelBreadcrumb() {
            panelBreadcrumb = new PanelBreadcrumb();
            return panelBreadcrumb;
    }

    /**
     * Sprint 10 - painel do botão
     * @author joao.coutinho
     * @since 21/03/2016
     * @see newPanelBreadcrumb()
     */
    @SuppressWarnings("serial")
    private class PanelBreadcrumb extends WebMarkupContainer {
            public PanelBreadcrumb() {
                    super("panelBreadcrumb");
                    setOutputMarkupId(Boolean.TRUE);
                    
                    add(componentFactory.newLink("lnkDashboard", HomePage.class));
                    add(componentFactory.newLink("lnkFornecedorContrato", FornecedorContratoPage.class));
                    add(new Label("lblNomePage", getTitulo()));
            }
    }
    
    /**
	 * Sprint 10 - painel do botão
	 * @author joao.coutinho
	 * @since 21/03/2016
	 */
	public PanelButton newPanelButton() {
		panelButton = new PanelButton();
		return panelButton;
	}

	/**
	 * Sprint 10 - painel do botão
	 * @author joao.coutinho
	 * @since 21/03/2016
	 * @see newPanelButton()
	 */
	@SuppressWarnings("serial")
	private class PanelButton extends WebMarkupContainer {
		public PanelButton() {
			super("panelButton");
			setOutputMarkupId(Boolean.TRUE);
			add(newButtonVoltar());
		}
	}
	
	/**
	 * Sprint 10 - painel do botão
	 * @author joao.coutinho
	 * @since 21/03/2016
	 * @see PanelButton
	 */

	
    public InfraAjaxFallbackLink<Void> newButtonVoltar() {
           return componentFactory.newAjaxFallbackLink("btnVoltar", (target) -> setPage());
    }
    
    private void setPage() {
        if (backPage != null) {
            setResponsePage(backPage);
        } else {
            setResponsePage(FornecedorContratoPage.class);
        }
    }

    // AÇÕES DOS COMPONENTES

    private void carregarListasQuandoForEditarContrato() {
        listaGrupoItem = programaService.buscarProgramaAgrupamentoLicitacao(contrato.getPrograma());
        fornecedor = contrato.getFornecedor();
        numeroContrato = contrato.getNumeroContrato();
        codigoPrograma =  contrato.getPrograma().getCodigoIdentificadorProgramaPublicado();
    }

    private AjaxDialogButton newButtonFecharModal(Modal<String> modal) {
        return new AjaxDialogButton(Model.of("Fechar"), Buttons.Type.Primary) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                modal.show(false);
                modal.close(target);
            }
        };
    }

    private void atualizaDropDownGrupoItem(InfraDropDownChoice<Programa> drop) {
        drop.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                listaGrupoItem.clear();
                if (contrato.getPrograma() != null) {
                    listaGrupoItem = programaService.buscarProgramaAgrupamentoLicitacao(contrato.getPrograma());
                }
                panelObjetosContrato.atualizarPainelGrupoItem(target, listaGrupoItem);
            }
        });
    }
    // MODAL
    private Modal<String> newModal(String id) {
        Modal<String> modal = new TextContentModal(id, new LambdaModel<String>(this::getMsgConfirm, this::setMsgConfirm));
        modal.setBackdrop(Backdrop.STATIC);
        modal.addButton(newButtonFecharModal(modal));
        return modal;
    }

    // GETTERS E SETTERS

    public Form<Contrato> getForm() {
        return form;
    }

    public void setForm(Form<Contrato> form) {
        this.form = form;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Page getBackPage() {
        return backPage;
    }

    public void setBackPage(Page backPage) {
        this.backPage = backPage;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public Uf getUf() {
        return uf;
    }

    public void setUf(Uf uf) {
        this.uf = uf;
    }

    public List<AgrupamentoLicitacao> getListaGrupoItem() {
        return listaGrupoItem;
    }

    public void setListaGrupoItem(List<AgrupamentoLicitacao> listaGrupoItem) {
        this.listaGrupoItem = listaGrupoItem;
    }

    public PanelVisualizarDadosFornecedor getPanelDadosFornecedor() {
        return panelDadosFornecedor;
    }

    public void setPanelDadosFornecedor(PanelVisualizarDadosFornecedor panelDadosFornecedor) {
        this.panelDadosFornecedor = panelDadosFornecedor;
    }

    public String getMsgConfirm() {
        return msgConfirm;
    }

    public void setMsgConfirm(String msgConfirm) {
        this.msgConfirm = msgConfirm;
    }
}
