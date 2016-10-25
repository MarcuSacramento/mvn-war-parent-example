package br.gov.mj.side.web.view.fornecedor.paineis;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraAjaxConfirmButton;
import br.gov.mj.infra.wicket.component.InfraLocalDateTextField;
import br.gov.mj.infra.wicket.component.pagination.InfraAjaxPagingNavigator;
import br.gov.mj.side.entidades.entidade.EntidadeAnexo;
import br.gov.mj.side.entidades.enums.EnumTipoArquivoEntidade;
import br.gov.mj.side.web.dto.AnexoDto;
import br.gov.mj.side.web.service.AnexoEntidadeService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.DataUtil;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.components.EntityDataProvider;
import br.gov.mj.side.web.view.components.validators.UploadValidator;
import br.gov.mj.side.web.view.planejarLicitacao.PlanejamentoLicitacaoPage;

/**
 * Painel contendo a funcionalidade de upload de arquivos. Adaptado para ser
 * utilizado de forma generica em qualquer pagina.
 * 
 * @author ronald.kalazans e adaptado para uso generico por diego.mota
 *
 */

@AuthorizeInstantiation({ PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_VISUALIZAR, PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_INCLUIR,
		PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_ALTERAR, PlanejamentoLicitacaoPage.ROLE_MANTER_PLANEJAR_LICITACAO_EXCLUIR })
public class PanelAnexo extends Panel {
	private static final long serialVersionUID = 1L;

	public static final String ROLE_MANTER_FORNECEDOR_VISUALIZAR = "manter_fornecedor:visualizar";
	public static final String ROLE_MANTER_FORNECEDOR_INCLUIR = "manter_fornecedor:incluir";
	public static final String ROLE_MANTER_FORNECEDOR_ALTERAR = "manter_fornecedor:alterar";
	public static final String ROLE_MANTER_FORNECEDOR_EXCLUIR = "manter_fornecedor:excluir";

	private static final String ONCHANGE = "onchange";

	private LocalDate dataAnexo;
	private List<FileUpload> uploads;
	private List<EntidadeAnexo> listAnexoTemp = new ArrayList<EntidadeAnexo>();
	private Model<String> mensagem = Model.of("");
	private EnumTipoArquivoEntidade tipoArquivo;

	// COMPOENTES WICKET
	private PanelPrincipalAnexo panelPrincipalAnexo;
	private Button btnDownload;
	private InfraAjaxConfirmButton btnExcluir;
	private AjaxSubmitLink buttonAdicionar;
	private FileUploadField fileUploadField;
	private Label labelMensagem;
	private FileUploadForm formUpload;
	private DropDownChoice<EnumTipoArquivoEntidade> drop;

	private Boolean readOnly;
	private Boolean deixarTodosPaineisHabilitados;

	// INJEÇÃO DE DEPENDENCIA
	@Inject
	private ComponentFactory componentFactory;

	 @Inject
	 private AnexoEntidadeService anexoService;
	
	// #############################################################################################
	// CONSTRUTORES, INITS & DESTROYERS

	/**
	 * 
	 * Cria uma instancia de PanelAnexo para adicioar a funcionalidade de upload
	 * de arquivos.
	 * 
	 * @param id
	 *            - nome que será referenciado na tela pelo wicket:id
	 * @param listAnexoTemp
	 *            - recebe a lista onde serão inseridos os arquivos em anexo.
	 *            Observe que o painel não irá pressistir esses dados, apenas
	 *            inseri-los na lista.
	 */
	public PanelAnexo(String id, List<EntidadeAnexo> listAnexoTemp, Boolean readOnly, Boolean deixarTodosPaineisHabilitados) {
		super(id);

		this.readOnly = readOnly;
		this.deixarTodosPaineisHabilitados = deixarTodosPaineisHabilitados;
		setOutputMarkupId(true);
		this.listAnexoTemp = listAnexoTemp;

		initVariaveis();
		add(panelPrincipalAnexo = new PanelPrincipalAnexo("panelPrincipalAnexo"));
	}

	public void initVariaveis() {
		if (uploads == null) {
			uploads = new ArrayList<FileUpload>();
		}

		labelMensagem = new Label("mensagemAnexo", mensagem);
		labelMensagem.setEscapeModelStrings(false);
		labelMensagem.setOutputMarkupId(true);
	}

	// PAINEIS
	private class PanelPrincipalAnexo extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;

		public PanelPrincipalAnexo(String id) {
			super(id);

			formUpload = getFormUpload();
			add(formUpload);

			DataView<EntidadeAnexo> dataView = getDataViewAnexos("anexos");

			formUpload.add(dataView); // anexos
			formUpload.add(new InfraAjaxPagingNavigator("paginator", dataView));
			formUpload.add(getDropDownTipo()); // tipoArquivo
			formUpload.add(newDateTextFieldDataAnexo());
			add(labelMensagem);
		}
	}

	public class FileUploadForm extends Form<List<FileUpload>> {
		private static final long serialVersionUID = 1L;

		public FileUploadForm(String id, IModel<List<FileUpload>> model) {
			super(id, model);
			setMultiPart(true);

			fileUploadField = new FileUploadField("fileInput", model);
			fileUploadField.setVisible(true);
			fileUploadField.add(new UploadValidator());
			add(fileUploadField);

			AjaxSubmitLink btnAdicionarAnexo = getButtonAdicionarAnexo();
			btnAdicionarAnexo.setVisible(true);
			add(btnAdicionarAnexo);
		}
		
	        /* Irá validar para não receber arquivos do tipo : .exe, .bat */
	        private class UploadValidator implements IValidator<List<FileUpload>> {
	            private static final long serialVersionUID = 1L;

	            @Override
	            public void validate(IValidatable<List<FileUpload>> validatable) {
	                List<FileUpload> list = validatable.getValue();
	                if (!list.isEmpty()) {
	                    FileUpload fileUpload = list.get(0);
	                    if (fileUpload.getSize()>Bytes.megabytes(Constants.LIMITE_MEGABYTES).bytes()) {
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

	// #############################################################################################
	// MONTAGEM DE COMPONENTES WICKET

	public DropDownChoice<EnumTipoArquivoEntidade> getDropDownTipo() {
		List<EnumTipoArquivoEntidade> lista = new ArrayList<EnumTipoArquivoEntidade>();
		lista.add(EnumTipoArquivoEntidade.INDICACAO_PREPOSTO);
		drop = new DropDownChoice<EnumTipoArquivoEntidade>("tipoArquivo", new PropertyModel<EnumTipoArquivoEntidade>(this, "tipoArquivo"), lista);
		drop.setChoiceRenderer(new ChoiceRenderer<EnumTipoArquivoEntidade>("descricao", "valor"));
		actionDropDownTipoArquivo(drop);
		drop.setNullValid(true);
		drop.setOutputMarkupId(true);
		drop.setEnabled(acionarComponente());
		return drop;
	}

	public FileUploadForm getFormUpload() {
		return new FileUploadForm("formUpload", new PropertyModel<List<FileUpload>>(this, "uploads"));
	}

	private InfraLocalDateTextField newDateTextFieldDataAnexo() {
		InfraLocalDateTextField field = componentFactory.newDateTextFieldWithDatePicker("data", "Data", false, new PropertyModel<LocalDate>(this, "dataAnexo"), "dd/MM/yyyy", "pt-BR");
		field.setEnabled(acionarComponente());
		return field;
	}

	private AjaxSubmitLink getButtonAdicionarAnexo() {
		buttonAdicionar = new AjaxSubmitLink("btnAdicionarAnexo") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				adicionarAnexo(target);
			}
		};
		buttonAdicionar.setOutputMarkupId(true);
		buttonAdicionar.setEnabled(acionarComponente());
		authorize(buttonAdicionar, RENDER, ROLE_MANTER_FORNECEDOR_INCLUIR);
		return buttonAdicionar;
	}

	private DataView<EntidadeAnexo> getDataViewAnexos(String id) {
		return new DataView<EntidadeAnexo>(id, new EntityDataProvider<EntidadeAnexo>(listAnexoTemp)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<EntidadeAnexo> item) {
				item.add(new Label("descricao", item.getModelObject().getTipoArquivo().getDescricao()));
				item.add(new Label("nomeAnexo", item.getModelObject().getNomeAnexo()));
				item.add(new Label("tamanhoArquivoEmMB", item.getModelObject().getTamanhoArquivoEmMB()));
				item.add(new Label("dataAnexoTable", DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataDocumento(), "dd/MM/yyyy")));
				item.add(new Label("dataCadastro", DataUtil.converteDataDeLocalDateParaString(item.getModelObject().getDataCadastro(), "dd/MM/yyyy HH:mm:ss")));

				btnDownload = componentFactory.newButton("btnDonwload", () -> download(item));
				item.add(btnDownload);

				btnExcluir = componentFactory.newAJaxConfirmButton("btnExcluirAnexo", "MSG002", formUpload, (target, formz) -> excluirAnexo(target, item));
				btnExcluir.setEnabled(acionarComponente());

				item.add(btnExcluir);
				authorize(btnExcluir, RENDER, ROLE_MANTER_FORNECEDOR_EXCLUIR);
			}
		};
	}

	// /#############################################################################################
	// METODOS PRIVADOS

	private void adicionarAnexo(AjaxRequestTarget target) {

		if (!validar(target)) {
			return;
		}

		if (uploads != null) {
			if (!uploads.isEmpty()) {
				for (FileUpload component : uploads) {
					EntidadeAnexo anexoTemp = new EntidadeAnexo();
					anexoTemp.setNomeAnexo(component.getClientFileName());
					anexoTemp.setConteudo(component.getBytes());
					anexoTemp.setTipoArquivo(tipoArquivo);
					anexoTemp.setDataDocumento(this.dataAnexo);
					anexoTemp.setDataCadastro(LocalDateTime.now());
					listAnexoTemp.add(anexoTemp);
				}
			}
		}
		this.dataAnexo = null;
		tipoArquivo = null;

		formUpload.addOrReplace(getDropDownTipo());
		formUpload.addOrReplace(newDateTextFieldDataAnexo());

		target.appendJavaScript("atualizaCssDropDown();");
		target.add(formUpload);
	}

	private void download(Item<EntidadeAnexo> item) {
		EntidadeAnexo a = item.getModelObject();
	        if (a.getId()!=null){
	            AnexoDto retorno = anexoService.buscarPeloId(a.getId());
	            SideUtil.download(retorno.getConteudo(), retorno.getNomeAnexo());
	        }else{
	            SideUtil.download(a.getConteudo(), a.getNomeAnexo());  
	        }

	}

	private void excluirAnexo(AjaxRequestTarget target, Item<EntidadeAnexo> item) {
		EntidadeAnexo a = item.getModelObject();
		listAnexoTemp.remove(a);
		if (target != null) {
			mensagem.setObject("");
			target.add(labelMensagem);

			target.add(panelPrincipalAnexo);
		}
	}

	// AÇÕES

	private boolean acionarComponente() {
		if (readOnly) {
			return false;
		} else {
			if (deixarTodosPaineisHabilitados) {
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean validar(AjaxRequestTarget target) {
		boolean validar = true;

		String msg = "";

		if (uploads == null) {
			msg += "<p><li> Adicione um arquivo a ser anexado.</li><p />";
			validar = false;
		}

		if (tipoArquivo == null) {
			msg += "<p><li> Informe o 'Tipo do Arquivo' a ser enviado.</li><p />";
			validar = false;
		}

		if (dataAnexo == null) {
			msg += "<p><li> O Campo 'Data' é obrigatório.</li><p />";
			validar = false;
		}

		if (uploads != null) {
			if (!uploads.isEmpty()) {
				for (FileUpload component : uploads) {

					String extension = FilenameUtils.getExtension(component.getClientFileName());
					if ("exe".equalsIgnoreCase(extension) || "bat".equalsIgnoreCase(extension)) {
						msg += "<p><li> Não são permitidos arquivos executáveis como .exe,.bat e etc. </li></p>";
						validar = false;
					}
				}
			}
		}

		if (!validar) {
			mensagem.setObject(msg);
		} else {
			mensagem.setObject("");
		}
		target.add(labelMensagem);

		return validar;
	}

	public void actionDropDownTipoArquivo(DropDownChoice<EnumTipoArquivoEntidade> dropElemento) {
		dropElemento.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				tipoArquivo = drop.getModelObject();
			}
		});
	}

	private void authorize(Component component, Action action, String... roles) {
		String s = StringUtils.join(roles, ",");
		MetaDataRoleAuthorizationStrategy.authorize(component, action, s);
	}

	// #############################################################################################
	// GETTERS & SETTERS
	public List<EntidadeAnexo> getListAnexoTemp() {
		return listAnexoTemp;
	}

	public void setListAnexoTemp(List<EntidadeAnexo> listAnexoTemp) {
		this.listAnexoTemp = listAnexoTemp;
	}

}
