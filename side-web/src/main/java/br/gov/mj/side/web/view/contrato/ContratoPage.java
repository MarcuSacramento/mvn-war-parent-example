package br.gov.mj.side.web.view.contrato;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;
import br.gov.mj.side.web.dto.ContratoDto;
import br.gov.mj.side.web.service.ContratoService;
import br.gov.mj.side.web.service.FormatacaoItensContratoService;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.util.MascaraUtils;
import br.gov.mj.side.web.view.components.AjaxDialogButton;
import br.gov.mj.side.web.view.contrato.painel.PanelContratoAnexos;
import br.gov.mj.side.web.view.contrato.painel.PanelDadosBasicosContrato;
import br.gov.mj.side.web.view.contrato.painel.PanelDadosFornecedor;
import br.gov.mj.side.web.view.contrato.painel.PanelHistoricoContrato;
import br.gov.mj.side.web.view.contrato.painel.PanelObjetosContrato;
import br.gov.mj.side.web.view.planejarLicitacao.ContratoPanelBotoes;
import br.gov.mj.side.web.view.programa.PanelFasePrograma;
import br.gov.mj.side.web.view.template.TemplatePage;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Backdrop;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.TextContentModal;

@AuthorizeInstantiation({ ContratoPage.ROLE_MANTER_CONTRATO_VISUALIZAR, ContratoPage.ROLE_MANTER_CONTRATO_INCLUIR, ContratoPage.ROLE_MANTER_CONTRATO_ALTERAR, ContratoPage.ROLE_MANTER_CONTRATO_EXCLUIR })
public class ContratoPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    public static final String ROLE_MANTER_CONTRATO_VISUALIZAR = "manter_contrato:visualizar";
    public static final String ROLE_MANTER_CONTRATO_INCLUIR = "manter_contrato:incluir";
    public static final String ROLE_MANTER_CONTRATO_ALTERAR = "manter_contrato:alterar";
    public static final String ROLE_MANTER_CONTRATO_EXCLUIR = "manter_contrato:excluir";

    private Form<Contrato> form;
    private Contrato contrato = new Contrato();
    private Programa programa;
    private PanelFasePrograma panelFasePrograma;
    private ContratoPanelBotoes contratoPanelBotoes;

    private Bem bem;
    private List<AgrupamentoLicitacao> listaGrupoItem = new ArrayList<AgrupamentoLicitacao>();
    private String numeroContrato;
    private Uf uf;
    private Entidade fornecedor;

    private PanelDadosBasicosContrato panelDadosBasicosContrato;
    private PanelObjetosContrato panelObjetosContrato;
    private PanelDadosFornecedor panelDadosFornecedor;
    private PanelContratoAnexos panelContratoAnexos;
    private PanelHistoricoContrato panelHistoricoContrato;

    private InfraDropDownChoice<Programa> dropDownPrograma;

    private boolean cadastroNovo = false;
    private Page backPage;
    private boolean readOnly;
    private boolean habilitarCampo;
    private boolean habilitarTodosCampos = false;
    private boolean possuiFormatacao = false;
    private boolean possuiOf = false;
    private String mensagemAviso = "";

    // Modal
    private String msgConfirm;
    private Modal<String> modalConfirm;

    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private ContratoService contratoService;
    @Inject
    private ProgramaService programaService;
    @Inject
    private FormatacaoItensContratoService formatacaoItensContratoService;
    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;

    // Chamado no primeiro acesso
    public ContratoPage(final PageParameters pageParameters, Page backPage, Contrato contrato, boolean readOnly) {
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
    public ContratoPage(final PageParameters pageParameters, Page backPage, Contrato contrato, Programa programa, boolean readOnly, boolean pesquisado) {
        super(pageParameters);

        this.backPage = backPage;
        this.contrato = contrato;
        this.programa = programa;
        this.readOnly = readOnly;

        if (contrato != null && contrato.getId() != null) {
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
        setTitulo("Gerenciar Programa");

        if (contrato != null && contrato.getId() != null) {
            cadastroNovo = false;
            verificarSePossuiFormatacoesEOfs();
        } else {
            cadastroNovo = true;
        }
        carregarListasQuandoForEditarContrato();
    }

    private void initComponents() {
        form = componentFactory.newForm("form", new CompoundPropertyModel<Contrato>(contrato));

        form.add(contratoPanelBotoes = new ContratoPanelBotoes("contratoPanelPotoes", contrato.getPrograma(), backPage, "contrato"));
        form.add(panelFasePrograma = new PanelFasePrograma("panelFasePrograma", contrato.getPrograma(), backPage));

        form.add(newContainerAviso("containerAviso"));
        form.add(newTextFieldNumeroContrato());
        form.add(panelDadosBasicosContrato = new PanelDadosBasicosContrato("panelDadosBasicosContrato", this, contrato, cadastroNovo, readOnly));
        form.add(newDropDownPrograma());
        form.add(newButtonPesquisaContrato());
        form.add(panelObjetosContrato = new PanelObjetosContrato("panelObjetosContrato", this, contrato, listaGrupoItem, cadastroNovo));
        form.add(panelDadosFornecedor = new PanelDadosFornecedor("panelDadosFornecedor", this, contrato, cadastroNovo, getUsuarioLogadoDaSessao()));
        form.add(panelContratoAnexos = new PanelContratoAnexos("panelAnexos", contrato, readOnly));
        form.add(panelHistoricoContrato = new PanelHistoricoContrato("panelHistorico", contrato));
        form.add(newButtonConfirmar());
        form.add(newButtonVoltar());

        // Modal
        modalConfirm = newModal("modalConfirm");
        modalConfirm.show(false);

        form.add(modalConfirm);

        add(form);
    }

    // Paineis e containers
    private WebMarkupContainer newContainerAviso(String id) {
        WebMarkupContainer containerAviso = new WebMarkupContainer(id);
        containerAviso.setVisible(possuiFormatacao);
        containerAviso.add(newLabelAviso()); // lblMensagemAviso
        return containerAviso;
    }

    // CRIAÇÃO DOS COMPONENTES

    private Label newLabelAviso() {
        Label lbl = new Label("lblMensagemAviso", mensagemAviso);
        lbl.setOutputMarkupId(true);
        return lbl;
    }

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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public TextField<String> newTextFieldNumeroContrato() {
        TextField<String> field = componentFactory.newTextField("numeroContrato", "Número do Contrato", false, new PropertyModel(this, "numeroContrato"));
        field.add(StringValidator.maximumLength(20));
        field.setEnabled(habilitarTextFildPesquisaContrato());
        actionTextFieldNumeroContrato(field);
        return field;
    }

    public AjaxButton newButtonPesquisaContrato() {
        AjaxButton buttonVerificarContrato = new AjaxButton("btnPesquisaContrato") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                pesquisarContratoExistente(target);
            }
        };

        buttonVerificarContrato.setOutputMarkupId(true);
        buttonVerificarContrato.setDefaultFormProcessing(false);
        buttonVerificarContrato.setVisible(habilitarTextFildPesquisaContrato());
        return buttonVerificarContrato;
    }

    private Button newButtonConfirmar() {
        Button buttonConfirmar = componentFactory.newButton("btnConfirmar", () -> guardar());
        buttonConfirmar.setEnabled(!readOnly);
        return buttonConfirmar;
    }

    private Button newButtonVoltar() {
        Button buttonVoltar = componentFactory.newButton("btnVoltar", () -> voltar());
        buttonVoltar.setDefaultFormProcessing(false);
        return buttonVoltar;
    }

    // AÇÕES DOS COMPONENTES

    private void verificarSePossuiFormatacoesEOfs() {
        if (contrato != null && !readOnly) {
            List<FormatacaoContrato> listaFormatacao = formatacaoItensContratoService.buscarFormatacaoContrato(contrato);
            List<OrdemFornecimentoContrato> listaOf = ordemFornecimentoContratoService.buscarOrdemFornecimentoContrato(contrato);

            if (listaFormatacao != null && !listaFormatacao.isEmpty()) {
                readOnly = true;
                possuiFormatacao = true;
            }

            if (listaOf != null && !listaOf.isEmpty()) {
                possuiOf = true;
            }

            if (possuiFormatacao) {
                montarMensagemAviso();
            }
        }
    }

    private void montarMensagemAviso() {
        mensagemAviso = "";
        mensagemAviso = "Não é possível alterar este contrato pois";
        if (possuiFormatacao) {
            mensagemAviso = mensagemAviso.concat(" existe ao menos 1 formatação");
        }
        if (possuiOf) {
            mensagemAviso = mensagemAviso.concat(" e uma ordem de fornecimento");
        }
        mensagemAviso = mensagemAviso.concat(" vinculada a seus itens.");
    }

    private void carregarListasQuandoForEditarContrato() {
        listaGrupoItem = programaService.buscarProgramaAgrupamentoLicitacao(contrato.getPrograma());
        fornecedor = contrato.getFornecedor();
        numeroContrato = contrato.getNumeroContrato();
    }

    private Boolean habilitarTextFildPesquisaContrato() {
        if (readOnly) {
            return false;
        } else {
            if (fornecedor == null || fornecedor.getId() == null) {
                return true;
            } else {
                return false;
            }
        }
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

    private void pesquisarContratoExistente(AjaxRequestTarget target) {

        if ("".equalsIgnoreCase(numeroContrato) || numeroContrato == null) {
            addMsgError("Informe um número para o contrato.");
            return;
        }

        List<Contrato> contratoLocalizado = new ArrayList<Contrato>();
        if (StringUtils.isNotBlank(numeroContrato)) {
            ContratoDto contratoDto = new ContratoDto();
            contratoDto.setNumeroContrato(numeroContrato);
            contratoLocalizado = contratoService.buscarSemPaginacao(contratoDto);
        }

        if (!contratoLocalizado.isEmpty() && contratoLocalizado.size() != 0) {
            if (contratoLocalizado.get(0).getPrograma().getId().intValue() == programa.getId().intValue()) {
                contrato = contratoLocalizado.get(0);
                setMsgConfirm("Contrato já cadastrado, os dados foram carregados.");
                modalConfirm.show(true);
            } else {
                setMsgConfirm("Contrato já cadastrado em outro programa, informe outro número.");
                modalConfirm.show(true);
                target.add(modalConfirm);
                return;
            }
        } else {
            contrato.setNumeroContrato(getNumeroContrato());
        }
        newButtonPesquisaContrato().setVisible(false);
        readOnly = false;
        form.setModelObject(contrato);
        atualizarPagina(target);
    }

    private void atualizarPagina(AjaxRequestTarget target) {

        if (contrato != null && contrato.getId() != null) {
            carregarListasQuandoForEditarContrato();
        }

        form.addOrReplace(newTextFieldNumeroContrato());
        form.addOrReplace(panelDadosBasicosContrato = new PanelDadosBasicosContrato("panelDadosBasicosContrato", this, contrato, cadastroNovo, readOnly));
        form.addOrReplace(newDropDownPrograma());
        form.addOrReplace(newButtonPesquisaContrato());
        form.addOrReplace(panelObjetosContrato = new PanelObjetosContrato("panelObjetosContrato", this, contrato, listaGrupoItem, cadastroNovo));
        form.addOrReplace(panelDadosFornecedor = new PanelDadosFornecedor("panelDadosFornecedor", this, contrato, cadastroNovo, getUsuarioLogadoDaSessao()));
        form.addOrReplace(panelContratoAnexos = new PanelContratoAnexos("panelAnexos", contrato, readOnly));
        form.addOrReplace(panelHistoricoContrato = new PanelHistoricoContrato("panelHistorico", contrato));
        form.addOrReplace(newButtonConfirmar());
        form.addOrReplace(newButtonVoltar());

        target.add(form);
    }

    private void actionTextFieldNumeroContrato(TextField<String> field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (contrato == null) {

                    ContratoDto contratoDto = new ContratoDto();
                    contratoDto.setNumeroContrato(numeroContrato);
                    contrato = contratoService.buscarSemPaginacao(contratoDto).get(0);
                }
                panelDadosBasicosContrato.atualizaPanel(target, contrato);
            }
        };
        field.add(onChangeAjaxBehavior);
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

    private void guardar() {
        if (!getSideSession().hasAnyRole(new String[] { ContratoPage.ROLE_MANTER_CONTRATO_INCLUIR, ContratoPage.ROLE_MANTER_CONTRATO_ALTERAR })) {
            throw new SecurityException();
        }

        if (!validarCampos()) {
            return;
        }

        Contrato contrato = form.getModelObject();
        boolean insert = contrato.getId() == null ? true : false;

        // SETA AS VARIAVEIS ANTES DE PERSISTIR
        contrato.setNumeroProcessoSEI(MascaraUtils.limparFormatacaoMascara(contrato.getNumeroProcessoSEI()));
        contrato.setListaAgrupamentosLicitacao(panelObjetosContrato.getListaDeItensSelecionados());
        // contrato.setFornecedor(panelDadosFornecedor.getFornecedor());
        contrato.setRepresentanteLegal(panelDadosFornecedor.getRepresentanteEscolhido().getPessoa());
        contrato.setPreposto(panelDadosFornecedor.getPrepostoEscolhido().getPessoa());
        contrato.setAnexos(panelContratoAnexos.getListAnexoTemp());

        contrato = contratoService.incluirAlterar(contrato, getIdentificador());
        if (insert) {
            getSession().info("Contrato cadastrado com sucesso");
        } else {
            getSession().info("Contrato alterado com sucesso.");
        }
        setResponsePage(new ContratoPage(null, backPage, contrato, programa, false, false));
    }

    private boolean validarCampos() {
        boolean validar = true;
        boolean dataInicialNula = false;
        boolean dataFinalNula = false;

        if (contrato.getDataVigenciaInicioContrato() == null) {
            addMsgError("O campo 'Período de Vigência (inicial)' é obrigatório.");
            validar = false;
            dataInicialNula = true;
        }

        if (contrato.getDataVigenciaFimContrato() == null) {
            addMsgError("O campo 'Período de Vigência (final)' é obrigatório.");
            dataFinalNula = true;
            validar = false;
        }

        if (contrato.getNumeroProcessoSEI() == null || "".equalsIgnoreCase(contrato.getNumeroProcessoSEI())) {
            addMsgError("O campo 'NUP-SEI' é obrigatório.");
            validar = false;
        }

        if ("".equalsIgnoreCase(numeroContrato) || numeroContrato == null) {
            addMsgError("Informe um número para o contrato.");
            validar = false;
        }

        if (!dataInicialNula && !dataFinalNula) {
            if (contrato.getDataVigenciaFimContrato().isBefore(contrato.getDataVigenciaInicioContrato())) {
                addMsgError("A data inicial do período de vigência não poderá ser maior do que a data final do período de vigência.");
                validar = false;
            }
        }

        if (panelObjetosContrato.getListaDeItensSelecionados().size() == 0) {
            addMsgError("Informe ao menos um Grupo / Item.");
            validar = false;
        }

        if (panelDadosFornecedor.getContrato().getFornecedor() == null) {
            addMsgError("O campo 'CNPJ Fornecedor' é obrigatório.");
            validar = false;
        }

        if (panelDadosFornecedor.getRepresentanteEscolhido() == null) {
            addMsgError("O campo 'Representante Legal Fornecedor' é obrigatório.");
            validar = false;
        }

        if (panelDadosFornecedor.getPrepostoEscolhido() == null) {
            addMsgError("O campo 'Preposto Fornecedor' é obrigatório.");
            validar = false;
        }
        return validar;
    }

    private void voltar() {
        setResponsePage(backPage);
    }

    // MODAL
    private Modal<String> newModal(String id) {
        Modal<String> modal = new TextContentModal(id, new LambdaModel<String>(this::getMsgConfirm, this::setMsgConfirm));
        modal.setBackdrop(Backdrop.STATIC);
        modal.addButton(newButtonFecharModal(modal));
        return modal;
    }

    // Ações

    // Este metodo irá desabilitar todos os paineis no primeiro acesso a página
    private void desabilitarPaineis() {
        panelDadosBasicosContrato.setEnabled(habilitarPaineis());
        dropDownPrograma.setEnabled(habilitarPaineis());
        panelDadosFornecedor.setEnabled(habilitarPaineis());
        panelObjetosContrato.setEnabled(habilitarPaineis());
        panelDadosFornecedor.setEnabled(habilitarPaineis());
        panelContratoAnexos.setEnabled(habilitarPaineis());
    }

    private boolean habilitarPaineis() {
        if (readOnly) {
            return false;
        } else {
            if (habilitarTodosCampos) {
                return true;
            } else {
                return false;
            }
        }
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

    public PanelDadosFornecedor getPanelDadosFornecedor() {
        return panelDadosFornecedor;
    }

    public void setPanelDadosFornecedor(PanelDadosFornecedor panelDadosFornecedor) {
        this.panelDadosFornecedor = panelDadosFornecedor;
    }

    public String getMsgConfirm() {
        return msgConfirm;
    }

    public void setMsgConfirm(String msgConfirm) {
        this.msgConfirm = msgConfirm;
    }
}
