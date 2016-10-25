package br.gov.mj.side.web.view.programa.inscricao.membroComissaoRecebimento;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.programa.inscricao.ComissaoAnexo;
import br.gov.mj.side.entidades.programa.inscricao.ComissaoRecebimento;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.web.dto.AnexoDto;
import br.gov.mj.side.web.dto.PermissaoProgramaDto;
import br.gov.mj.side.web.service.ComissaoAnexoService;
import br.gov.mj.side.web.service.GenericEntidadeService;
import br.gov.mj.side.web.service.InscricaoProgramaService;
import br.gov.mj.side.web.service.MailService;
import br.gov.mj.side.web.service.PublicizacaoService;
import br.gov.mj.side.web.util.CPFUtils;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.components.EntityDataProvider;
import br.gov.mj.side.web.view.programa.inscricao.InscricaoNavPanel;
import br.gov.mj.side.web.view.programa.inscricao.locaisEntrega.EntregaInscricaoProgramaPage;
import br.gov.mj.side.web.view.template.TemplatePage;

/**
 * @author joao.coutinho
 * @since 26/02/2016 - Aba do menu INFORMAÇÕES DA ENTIDADE. Sprint 8
 */
@AuthorizeInstantiation({ ComissaoRecebimentoPage.ROLE_MANTER_COMISSAO_RECEBIMENTO_INCLUIR, ComissaoRecebimentoPage.ROLE_MANTER_COMISSAO_RECEBIMENTO_EXCLUIR })
public class ComissaoRecebimentoPage extends TemplatePage {
    private static final long serialVersionUID = 2781195383571024389L;

    // #######################################_VARIAVEIS_############################################
    Form<InscricaoPrograma> form;
    private FileUploadForm formUpload;
    private String nomeArquivo;
    private InscricaoPrograma inscricao;
    private ComissaoRecebimento membroSelecionado = new ComissaoRecebimento();
    private List<ComissaoRecebimento> listMembroTemp = new ArrayList<ComissaoRecebimento>();
    private Pessoa pessoaSelecionado;
    private MembroProvider membroProvider;
    private List<FileUpload> uploads = new ArrayList<FileUpload>();
    private List<ComissaoAnexo> listAnexoTemp = new ArrayList<ComissaoAnexo>();
    private boolean permissaoVincular = false;
    private List<Pessoa> listaPessoa;
    private Model<String> mensagem = Model.of("");
    private Model<String> mensagemMembro = Model.of("");

    // #######################################_CONSTANTE_############################################
    private static final boolean VERDADEIRO = true;
    private static final boolean FALSO = false;
    private static final int TAMANHO_PAGINADOR = 10;

    public static final String ROLE_MANTER_COMISSAO_RECEBIMENTO_INCLUIR = "manter_comissao_recebimento:incluir";
    public static final String ROLE_MANTER_COMISSAO_RECEBIMENTO_EXCLUIR = "manter_comissao_recebimento:excluir";

    // #######################################_ELEMENTOS_DO_WICKET_############################################
    private PanelMembroDropDownChoice panelMembroDropDownChoice;
    private PanelMembroDataView panelMembroDataView;
    private PanelButton panelButton;
    private DropDownChoice<Pessoa> dropMembroSelecionado;
    private DataView<ComissaoRecebimento> newListaMembros;
    private DataView<ComissaoAnexo> newListaComissaoAnexo;
    private PanelAnexos panelAnexos;
    private InfraAjaxConfirmButton btnExcluirMembro;
    private Button buttonSalvar;
    private Label labelMensagemAnexo;
    private Label labelMensagemMembro;
    private Link link;

    // #####################################_INJEÇÃO_DE_DEPENDENCIA_##############################################
    @Inject
    private ComponentFactory componentFactory;
    @Inject
    private InscricaoProgramaService inscricaoProgramaService;
    @Inject
    GenericEntidadeService genericEntidadeService;
    @Inject
    private MailService mailService;
    @Inject
    PublicizacaoService publicizacaoService;
    @Inject
    private ComissaoAnexoService comissaoAnexoService;

    // #####################################_CONSTRUTOR_##############################################
    /**
     * @author joao.coutinho
     * @since 26/02/2016 - Construtor da classe
     * @param pageParameters
     */
    public ComissaoRecebimentoPage(PageParameters pageParameters, InscricaoPrograma inscricao) {
        super(pageParameters);
        this.inscricao = inscricao;
        initObjects();
        initComponentes();
        setTitulo(getTituloNomePrograma());
    }

    /**
     * @author joao.coutinho
     * @since 01/03/2016 - Inícia todos objetos
     * @see ComissaoRecebimentoPage(PageParameters pageParameters)
     */
    private void initObjects() {
        PermissaoProgramaDto permissoesPrograma = publicizacaoService.buscarPermissoesPrograma(inscricao.getPrograma());
        if (permissoesPrograma != null) {
            permissaoVincular = permissoesPrograma.getVincularComissaoRecebimento();
        }
        this.pessoaSelecionado = new Pessoa();

        // Carregar Lista de membros e anexos.
        this.listMembroTemp = new ArrayList<ComissaoRecebimento>();
        this.listMembroTemp = inscricaoProgramaService.buscarComissaoRecebimento(this.inscricao);
        this.listAnexoTemp = new ArrayList<ComissaoAnexo>();
        this.listAnexoTemp = SideUtil.convertAnexoDtoToEntityComissaoAnexo(comissaoAnexoService.buscarComissaoAnexo(this.inscricao.getId()));
        this.listaPessoa = carregarSelecaoMembros();
    }

    /**
     * @author joao.coutinho
     * @since 26/02/2016 - Inícia todos os componentes
     * @see ComissaoRecebimentoPage(PageParameters pageParameters)
     */
    private void initComponentes() {
        form = componentFactory.newForm("form", inscricao);
        form.add(new Label("lblMsgAlertaa", getString("MT030")).setVisible(!permissaoVincular));
        form.add(new InscricaoNavPanel("navPanel", inscricao, null, this));
        form.add(panelMembroDropDownChoice = newPanelMembroDropDownChoice());// painel
                                                                             // DropDown
                                                                             // Membros
        form.add(panelMembroDataView = newPanelMembroDataView());// Painel
                                                                 // tabela de
                                                                 // membros
        form.add(panelAnexos = newPanelAnexos());// Painel de Anexos
        form.add(panelButton = newPanelButton());// Painel de botões "salvar,
                                                 // voltar e anterior"
        // O link é uma ancora simples para subir a tela quando ocorrer algum
        // erro de validação
        link = new Link("ancora") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(ComissaoRecebimentoPage.class);
            }
        };
        form.add(link);
        add(form);
    }

    // ####################################_PAINES_###############################################
    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 26/02/2016 - Painel responsavel em adicionar seleção de membros.
     */
    public PanelMembroDropDownChoice newPanelMembroDropDownChoice() {
        panelMembroDropDownChoice = new PanelMembroDropDownChoice();
        authorize(panelMembroDropDownChoice, RENDER, ROLE_MANTER_COMISSAO_RECEBIMENTO_INCLUIR);
        panelMembroDropDownChoice.setEnabled(permissaoVincular);
        return panelMembroDropDownChoice;
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 26/02/2016
     * @see newPanelMembroComissaoRecebimento()
     */
    @SuppressWarnings("serial")
    private class PanelMembroDropDownChoice extends WebMarkupContainer {
        public PanelMembroDropDownChoice() {
            super("panelMembroDropDownChoice");
            setOutputMarkupId(VERDADEIRO);
            dropMembroSelecionado = newDownDownLocalEntrega();
            add(dropMembroSelecionado);

            labelMensagemMembro = new Label("mensagemMembro", mensagemMembro);
            labelMensagemMembro.setEscapeModelStrings(false);
            add(labelMensagemMembro);

            AjaxSubmitLink adicionar = newAdicionarButton();
            add(adicionar);
            adicionar.setVisible(permissaoVincular);
        }
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 26/02/2016 - painel de tabela de membros adicionados
     */
    public PanelMembroDataView newPanelMembroDataView() {
        panelMembroDataView = new PanelMembroDataView();
        return panelMembroDataView;
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 26/02/2016
     * @see newPanelMembroComissaoRecebimento()
     */
    @SuppressWarnings("serial")
    private class PanelMembroDataView extends WebMarkupContainer {
        public PanelMembroDataView() {
            super("panelMembroDataView");
            setOutputMarkupId(VERDADEIRO);
            membroProvider = new MembroProvider(listMembroTemp);
            add(newDataViewMembros(membroProvider));
            add(new InfraAjaxPagingNavigator("paginator", newListaMembros));
        }
    }

    /**
     * Sprint 8 - painel do botão
     * 
     * @author joao.coutinho
     * @since 26/02/2016
     */
    public PanelButton newPanelButton() {
        panelButton = new PanelButton();
        return panelButton;
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 26/02/2016
     * @see newPanelButton()
     */
    @SuppressWarnings("serial")
    private class PanelButton extends WebMarkupContainer {
        public PanelButton() {
            super("panelButton");
            setOutputMarkupId(VERDADEIRO);
            add(newButtonVoltar());
            add(newButtonSalvar());
            add(newButtonAnterior().setVisible(FALSO));
        }
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 26/02/2016 - painel de anexos
     */
    public PanelAnexos newPanelAnexos() {
        panelAnexos = new PanelAnexos();
        return panelAnexos;
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 29/02/2016
     * @see newPanelAnexos()()
     */
    private class PanelAnexos extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelAnexos() {
            super("panelAnexos");
            FileUploadForm fileUploadForm = newFormAnexo();
            fileUploadForm.add(newTextFieldNomeArquivo());
            authorize(fileUploadForm, RENDER, ROLE_MANTER_COMISSAO_RECEBIMENTO_INCLUIR);
            fileUploadForm.setEnabled(permissaoVincular);
            add(fileUploadForm);

            labelMensagemAnexo = new Label("mensagemAnexo", mensagem);
            labelMensagemAnexo.setEscapeModelStrings(false);
            add(labelMensagemAnexo);

            add(newDataViewAnexo());
            add(new InfraAjaxPagingNavigator("paginatorAnexo", newListaComissaoAnexo));
        }
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 29/02/2016
     * @see PanelAnexos
     * @return
     */
    private FileUploadForm newFormAnexo() {
        return new FileUploadForm("anexoForm", new LambdaModel<List<FileUpload>>(this::getUploads, this::setUploads));
    }

    public List<FileUpload> getUploads() {
        return uploads;
    }

    public void setUploads(List<FileUpload> uploads) {
        this.uploads = uploads;
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 29/02/2016
     * @see PanelAnexos
     * @return
     */
    private DataView<ComissaoAnexo> newDataViewAnexo() {
        newListaComissaoAnexo = new DataView<ComissaoAnexo>("anexos", new EntityDataProvider<ComissaoAnexo>(this.listAnexoTemp)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ComissaoAnexo> item) {
                item.add(new Label("descricaoAnexo"));
                item.add(new Label("nomeAnexo"));
                item.add(new Label("tamanhoArquivoEmMB"));
                item.add(new Label("dataCadastro", dataCadastroBR(item.getModelObject().getDataCadastro())));

                Button btnDownload = componentFactory.newButton("btnDonwload", () -> download(item));
                item.add(btnDownload);

                @SuppressWarnings("unchecked")
                InfraAjaxConfirmButton btnExcluir = componentFactory.newAJaxConfirmButton("btnExcluir", "MSG002", form, (target, formz) -> excluirAtributo(target, (Form<InscricaoPrograma>) formz, item));
                item.add(btnExcluir);
                authorize(btnExcluir, RENDER, ROLE_MANTER_COMISSAO_RECEBIMENTO_EXCLUIR);
                btnExcluir.setVisible(permissaoVincular);
            }
        };
        newListaComissaoAnexo.setItemsPerPage(TAMANHO_PAGINADOR);
        return newListaComissaoAnexo;
    }

    private void download(Item<ComissaoAnexo> item) {
        ComissaoAnexo a = item.getModelObject();
        if (a.getId() != null) {
            AnexoDto retorno = comissaoAnexoService.buscarPeloId(a.getId());
            SideUtil.download(retorno.getConteudo(), retorno.getNomeAnexo());
        } else {
            SideUtil.download(a.getConteudo(), a.getNomeAnexo());
        }
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 29/02/2016
     * @see newDataViewAnexos(String id)
     * @return
     */
    protected void excluirAtributo(AjaxRequestTarget target, Form<InscricaoPrograma> formz, Item<ComissaoAnexo> item) {
        ComissaoAnexo anexo = item.getModelObject();
        this.listAnexoTemp.remove(anexo);
        mensagem.setObject("");
        target.add(labelMensagemAnexo);
        target.add(this.panelAnexos);
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 29/02/2016
     * @see PanelAnexos
     * @return
     */
    private TextField<String> newTextFieldNomeArquivo() {
        TextField<String> field = new TextField<String>("nomeArquivo", new PropertyModel<String>(this, "nomeArquivo"));
        return field;
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 29/02/2016
     * @see PanelAnexos
     * @return
     */
    private class FileUploadForm extends Form<List<FileUpload>> {
        private static final long serialVersionUID = 1L;

        public FileUploadForm(String id, IModel<List<FileUpload>> model) {
            super(id, model);
            setMultiPart(true);
            FileUploadField fileUploadField = new FileUploadField("fileInput", model);
            fileUploadField.add(new UploadValidator());
            add(fileUploadField);
            AjaxSubmitLink btnAdicionarAnexo = newButtonAdicionarAnexo();
            add(btnAdicionarAnexo);
            btnAdicionarAnexo.setVisible(permissaoVincular);
        }

        /* Irá validar para não receber arquivos do tipo : .exe, .bat */
        private class UploadValidator implements IValidator<List<FileUpload>> {
            private static final long serialVersionUID = 1L;

            @Override
            public void validate(IValidatable<List<FileUpload>> validatable) {
                List<FileUpload> list = validatable.getValue();
                if (!list.isEmpty()) {
                    FileUpload fileUpload = list.get(0);
                    if (fileUpload.getSize() > Bytes.megabytes(Constants.LIMITE_MEGABYTES).bytes()) {
                        ValidationError error = new ValidationError("Arquivo para Download maior que " + Constants.LIMITE_MEGABYTES + "MB.");
                        validatable.error(error);
                    }
                    String extension = FilenameUtils.getExtension(fileUpload.getClientFileName());
                    if ("exe".equalsIgnoreCase(extension) || "bat".equalsIgnoreCase(extension)) {
                        ValidationError error = new ValidationError("Não são permitidos arquivos executáveis como .exe,.bat e etc.");
                        validatable.error(error);
                    }
                }
            }
        }
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 29/02/2016
     * @see FileUploadForm
     * @return
     */
    private AjaxSubmitLink newButtonAdicionarAnexo() {
        return new AjaxSubmitLink("btnAdicionarAnexo") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                adicionarAnexo(target);
            }
        };

    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 29/02/2016
     * @see newButtonAdicionarAnexo()
     * @return
     */
    private void adicionarAnexo(AjaxRequestTarget target) {
        if (!validarObrigatoriedadeAnexos(target)) {
            return;
        }
        try {
            if (!uploads.isEmpty()) {
                for (FileUpload component : uploads) {
                    ComissaoAnexo comissaoAnexo = new ComissaoAnexo();
                    comissaoAnexo.setNomeAnexo(component.getClientFileName());
                    comissaoAnexo.setConteudo(component.getBytes());
                    comissaoAnexo.setDescricaoAnexo(this.nomeArquivo);
                    comissaoAnexo.setDataCadastro(LocalDateTime.now());
                    this.listAnexoTemp.add(comissaoAnexo);
                }
                addMsgInfo("Arquivo adicionado com sucesso");
            }
            this.nomeArquivo = null;
            target.add(this.panelAnexos);
        } catch (NullPointerException e) {
            addMsgInfo("Adicione um arquivo a ser anexado.");
        }

    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @param target
     * @since 29/02/2016
     * @see adicionarAnexo(AjaxRequestTarget target)
     * @return
     */
    private boolean validarObrigatoriedadeAnexos(AjaxRequestTarget target) {
        boolean validar = VERDADEIRO;
        String msg = "";

        if ("".equals(this.nomeArquivo) || this.nomeArquivo == null) {
            msg += "<p><li> Campo 'Descrição' é obrigatório.</li><p />";
            validar = FALSO;
        }
        if (uploads == null) {
            msg += "<p><li> Selecione um arquivo para anexar.</li><p />";
            validar = FALSO;
        }
        if (!validar) {
            mensagem.setObject(msg);
        } else {
            mensagem.setObject("");
        }
        target.add(labelMensagemAnexo);
        return validar;
    }

    // metodo para retornar o titulo da pagina(nome do programa).
    private String getTituloNomePrograma() {
        String titulo = new String();
        if (this.inscricao == null) {
            titulo = "";
        } else {
            titulo = "Programa: ".concat(this.inscricao.getPrograma().getNomePrograma());
        }

        return titulo;
    }

    // ####################################_CRIACAO_DOS_COMPONENTES_###############################################
    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 26/02/2016 - metodo responsavel em adicionar membro da seleção em
     *        dataView.
     */
    private AjaxSubmitLink newAdicionarButton() {
        @SuppressWarnings("serial")
        AjaxSubmitLink button = new AjaxSubmitLink("btnAdicionar") {
            @SuppressWarnings("rawtypes")
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                super.onSubmit(target, form);
                adicionar(target);
            }
        };
        return button;
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 26/02/2016
     * @see newAdicionarButton()
     */
    private void adicionar(AjaxRequestTarget target) {
        if (!verificarObrigatoriedade(this.pessoaSelecionado, target)) {
            return;
        }

        this.membroSelecionado.setMembroComissao(this.pessoaSelecionado);
        this.membroSelecionado.setInscricaoPrograma(this.inscricao);
        boolean validarEntradaMembro = VERDADEIRO;
        try {
            for (ComissaoRecebimento objMembro : this.listMembroTemp) {
                if (objMembro.getMembroComissao().getNumeroCpf().equals(membroSelecionado.getMembroComissao().getNumeroCpf())) {
                    addMsgError("Membro já selecionado!");
                    validarEntradaMembro = FALSO;
                }
            }
            if (validarEntradaMembro) {
                this.listMembroTemp.add(membroSelecionado);
                this.listaPessoa.remove(membroSelecionado.getMembroComissao());
                this.membroSelecionado = new ComissaoRecebimento();
                this.pessoaSelecionado = new Pessoa();
                form.addOrReplace(panelMembroDropDownChoice);
                form.addOrReplace(panelMembroDataView);
                target.add(form);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @param target
     * @since 26/02/2016
     * @see adicionar(AjaxRequestTarget target)
     */
    private boolean verificarObrigatoriedade(Pessoa pessoa, AjaxRequestTarget target) {
        boolean validar = VERDADEIRO;
        String msg = "";
        if (pessoa == null) {
            msg += "<p><li> Campo 'Membro' é obrigatório.</li><p />";
            validar = FALSO;
        }
        if (!validar) {
            mensagemMembro.setObject(msg);
        } else {
            mensagemMembro.setObject("");
        }
        target.add(labelMensagemMembro);
        return validar;
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 26/02/2016 - metodo responsavel sem carregar lista de membros
     */
    private DropDownChoice<Pessoa> newDownDownLocalEntrega() {
        DropDownChoice<Pessoa> dropDown = componentFactory.newDropDownChoice("pessoaSelecionado", "Membro", false, "id", "nomePessoa", new LambdaModel<Pessoa>(this::getPessoaSelecionado, this::setPessoaSelecionado), this.listaPessoa, null);
        return dropDown;
    }

    /**
     * @author joao.coutinho
     * @since 02/03/2016 - Metodo responsavel em carregar lista para seleção de
     *        membros
     * @see newDownDownLocalEntrega()
     */
    public List<Pessoa> carregarSelecaoMembros() {
        PessoaEntidade pessoaEntidade = (PessoaEntidade) getSessionAttribute("pessoaEntidade");
        this.listaPessoa = new ArrayList<Pessoa>();
        List<Pessoa> listaTemp = new ArrayList<Pessoa>();
        if (pessoaEntidade != null) {
            for (PessoaEntidade objetoPessoaEntidade : genericEntidadeService.buscarPessoasPorTipo(pessoaEntidade.getEntidade()).getListaMembroComissao()) {
                if (objetoPessoaEntidade.getPessoa().getStatusPessoa().equals(EnumStatusPessoa.ATIVO)) {// Verifica
                                                                                                        // se
                                                                                                        // o
                                                                                                        // status
                                                                                                        // da
                                                                                                        // pessoa
                                                                                                        // encontra-se
                                                                                                        // ativo
                    this.listaPessoa.add(objetoPessoaEntidade.getPessoa());
                }
            }
            listaTemp.addAll(this.listaPessoa);
        }
        if (this.listMembroTemp.size() > 0 && listaTemp.size() > 0) {// Esse IF
                                                                     // é
                                                                     // responsavel
                                                                     // em tirar
                                                                     // os
                                                                     // membros
                                                                     // que
                                                                     // estiverem
                                                                     // dentro
                                                                     // da
                                                                     // tabela
                                                                     // de
                                                                     // membros.
            for (ComissaoRecebimento obj : this.listMembroTemp) {
                for (Pessoa objetoPessoa : listaTemp) {
                    if (obj.getMembroComissao().getNomePessoa().equals(objetoPessoa.getNomePessoa())) {
                        this.listaPessoa.remove(objetoPessoa);
                    }
                }
            }
            this.listaPessoa = ordenarListaPessoa(this.listaPessoa);
        }
        return this.listaPessoa;
    }

    /**
     * @author joao.coutinho
     * @see carregarSelecaoMembros()
     * @param listaPessoa
     * @return
     */
    private List<Pessoa> ordenarListaPessoa(List<Pessoa> listaPessoa) {
        List<String> listaNome = new ArrayList<String>();
        List<Pessoa> novaLista = new ArrayList<Pessoa>();
        for (Pessoa nome : listaPessoa) {
            listaNome.add(nome.getNomePessoa());
        }
        Collections.sort(listaNome);
        for (String nome : listaNome) {
            for (Pessoa objetoPessoa : listaPessoa) {
                if (nome.equals(objetoPessoa.getNomePessoa())) {
                    novaLista.add(objetoPessoa);
                }
            }
        }
        return novaLista;
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 26/02/2016
     */
    private Button newButtonVoltar() {
        return componentFactory.newButton("btnVoltar", () -> setResponsePage(CadastrarMembroComissaoPage.class));
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 01/03/2016
     */
    private Button newButtonSalvar() {
        buttonSalvar = new AjaxButton("btnSalvar") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                salvar(target);
            }
        };
        buttonSalvar.setVisible(permissaoVincular);
        authorize(buttonSalvar, RENDER, ROLE_MANTER_COMISSAO_RECEBIMENTO_INCLUIR, ROLE_MANTER_COMISSAO_RECEBIMENTO_EXCLUIR);
        return buttonSalvar;
    }

    /**
     * @author joao.coutinho
     * @param target
     * @since 01/03/2016 - Salva dados de Membros de Comissão Recebimentos
     * @see newButtonSalvar()
     * @param target
     */
    private void salvar(AjaxRequestTarget target) {

        form.getModelObject().setComissaoAnexos(this.listAnexoTemp);
        form.getModelObject().setComissaoRecebimento(this.listMembroTemp);
        long ultimoIdMembrosRecebimento = recuperarMaiorId(this.inscricao);
        if (this.inscricao.getComissaoRecebimento() == null || this.inscricao.getComissaoRecebimento().size() < 1) {
            getSession().error("Para formar uma comissão de recebimento de um programa é necessária a indicação de no mínimo 1 membro.");
            mensagem.setObject("");
            mensagemMembro.setObject("");
            target.add(labelMensagemAnexo);
            target.add(labelMensagemMembro);
            target.focusComponent(link);
            return;
        } else {
            InscricaoPrograma inscricaoPrograma = inscricaoProgramaService.submeterComissaoRecebimento(this.inscricao, getIdentificador());
            getSession().info("Salvo com sucesso.");
            if (inscricaoPrograma != null) {
                mailService.enviarEmailComissaoRecebimento(inscricaoPrograma, ultimoIdMembrosRecebimento);
            }
            setResponsePage(new ComissaoRecebimentoPage(getPageParameters(), this.inscricao));
        }
    }

    /**
     * @author joao.coutinho
     * @since 03/03/2016 - metodo responsável em recuperar o maior id dentro da
     *        lista de Comissão Recebimento
     * @see salvar(AjaxRequestTarget target)
     * @param inscricaoPrograma
     * @return
     */
    private long recuperarMaiorId(InscricaoPrograma inscricaoPrograma) {
        long novoId = 0;
        if (inscricaoPrograma != null) {
            for (ComissaoRecebimento id : inscricaoPrograma.getComissaoRecebimento()) {
                if (id.getId() != null) {
                    if (novoId == 0) {
                        novoId = id.getId();
                    }
                    if (id.getId() > novoId) {
                        novoId = id.getId();
                    }
                }
            }
        }
        return novoId;
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 26/02/2016
     */
    private Button newButtonAnterior() {
        return componentFactory.newButton("btnAnterior", () -> setResponsePage(new EntregaInscricaoProgramaPage(getPageParameters(), this.inscricao)));
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 26/02/2016
     */
    public DataView<ComissaoRecebimento> newDataViewMembros(MembroProvider membroProvider) {
        newListaMembros = new DataView<ComissaoRecebimento>("listaNewDataView", membroProvider) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ComissaoRecebimento> item) {
                item.add(new Label("nomePessoa", item.getModelObject().getMembroComissao().getNomePessoa()));
                item.add(new Label("numeroCpf", CPFUtils.format(item.getModelObject().getMembroComissao().getNumeroCpf())));
                item.add(new Label("numeroTelefone", item.getModelObject().getMembroComissao().getNumeroTelefone()));
                item.add(new Label("email", item.getModelObject().getMembroComissao().getEmail()));

                btnExcluirMembro = componentFactory.newAJaxConfirmButton("btnExcluirMembro", "MSG011", formUpload, (target, formz) -> excluirMembro(target, item));
                authorize(btnExcluirMembro, RENDER, ROLE_MANTER_COMISSAO_RECEBIMENTO_EXCLUIR);
                btnExcluirMembro.setVisible(permissaoVincular);
                item.add(btnExcluirMembro);
            }
        };
        newListaMembros.setItemsPerPage(TAMANHO_PAGINADOR);
        return newListaMembros;
    }

    /**
     * Sprint 8
     * 
     * @author joao.coutinho
     * @since 07/03/2016
     * @see newDataViewMembros( MembroProvider membroProvider)
     * @return
     */
    private void excluirMembro(AjaxRequestTarget target, Item<ComissaoRecebimento> item) {

        if (!validarQuantidademembro()) {
            getSession().error("A lista de membros não pode ser vazia!");
            return;
        }
        ComissaoRecebimento objetoComissaoRecebimento = item.getModelObject();
        this.listMembroTemp.remove(objetoComissaoRecebimento);
        this.listaPessoa.add(objetoComissaoRecebimento.getMembroComissao());
        this.listaPessoa = ordenarListaPessoa(this.listaPessoa);
        if (target != null) {
            mensagemMembro.setObject("");
            panelMembroDropDownChoice.addOrReplace(newDownDownLocalEntrega());
            target.add(panelMembroDropDownChoice);
            target.add(panelMembroDataView);
        }
    }

    private boolean validarQuantidademembro() {
        boolean retorno = false;
        if (this.listMembroTemp.size() > 1) {
            retorno = true;
        }
        return retorno;
    }

    // ####################################_PROVIDER_###############################################
    private class MembroProvider extends SortableDataProvider<ComissaoRecebimento, String> {
        private static final long serialVersionUID = 1L;
        List<ComissaoRecebimento> listMembroTemp = new ArrayList<ComissaoRecebimento>();

        public MembroProvider(List<ComissaoRecebimento> listMembroTemp) {
            setSort("nomeMembroComissao", SortOrder.ASCENDING);
            this.listMembroTemp = listMembroTemp;
        }

        @Override
        public Iterator<ComissaoRecebimento> iterator(long first, long size) {
            List<ComissaoRecebimento> membroTemp = new ArrayList<ComissaoRecebimento>();
            List<ComissaoRecebimento> novaLista = new ArrayList<ComissaoRecebimento>();
            List<String> listaNome = new ArrayList<String>();
            for (ComissaoRecebimento nome : this.listMembroTemp) {
                listaNome.add(nome.getMembroComissao().getNomePessoa());
            }
            Collections.sort(listaNome);
            for (String nome : listaNome) {
                for (ComissaoRecebimento objeto : this.listMembroTemp) {
                    if (nome.equals(objeto.getMembroComissao().getNomePessoa())) {
                        novaLista.add(objeto);
                        break;
                    }
                }
            }
            int firstTemp = 0;
            int flagTemp = 0;
            for (ComissaoRecebimento k : novaLista) {
                if (firstTemp >= first) {
                    if (flagTemp <= size) {
                        membroTemp.add(k);
                        flagTemp++;
                    }
                }
                firstTemp++;
            }
            return membroTemp.iterator();
        }

        @Override
        public long size() {
            return this.listMembroTemp.size();
        }

        @Override
        public IModel<ComissaoRecebimento> model(ComissaoRecebimento object) {
            return new CompoundPropertyModel<ComissaoRecebimento>(object);
        }
    }

    public String dataCadastroBR(LocalDateTime dataCadastro) {
        DateTimeFormatter sdfPadraoBR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        if (dataCadastro != null) {
            return sdfPadraoBR.format(dataCadastro);
        }
        return " - ";
    }

    // ####################################_GET_AND_SET_###############################################
    public Pessoa getPessoaSelecionado() {
        return pessoaSelecionado;
    }

    public void setPessoaSelecionado(Pessoa pessoaSelecionado) {
        this.pessoaSelecionado = pessoaSelecionado;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public boolean isPermissaoVincular() {
        return permissaoVincular;
    }

    public void setPermissaoVincular(boolean permissaoVincular) {
        this.permissaoVincular = permissaoVincular;
    }

}
