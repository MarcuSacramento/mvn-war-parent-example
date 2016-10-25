package br.gov.mj.side.web.view.programa.informacoesGerais;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.apoio.entidades.Funcao;
import br.gov.mj.apoio.entidades.Orgao;
import br.gov.mj.apoio.entidades.SubFuncao;
import br.gov.mj.apoio.entidades.UnidadeExecutora;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.web.service.FuncaoService;
import br.gov.mj.side.web.service.OrgaoService;
import br.gov.mj.side.web.service.SubFuncaoService;
import br.gov.mj.side.web.service.UnidadeExecutoraService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.view.components.AjaxDialogButton;
import br.gov.mj.side.web.view.components.validators.NumericValidator;
import br.gov.mj.side.web.view.programa.ProgramaPage;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Backdrop;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.TextContentModal;

public class PanelInformacoesGeraisPage extends Panel {
    private static final String ONCHANGE = "onchange";

    private static final long serialVersionUID = 1L;
    
    public static final String BUSCA_PADRAO = "MJ";

    private Page backPage;
    private Programa programa;
    private ProgramaPage programaBackPage;

    private PanelPrincipal panelPrincipal;
    private PanelDropDown panelDropDown;
    private PanelOrgaoExecutor panelOrgaoExecutor;
    private TextArea<String> text;

    private String numeroProcessoSEI = "";
    private String descricaoPrograma = "";
    private String nomePrograma;
    private String nomeFantasiaPrograma;
    private String anoPrograma;
    private Orgao orgao;
    private UnidadeExecutora unidadeExecutora;
    
    private String anoProgramaTemp;

    private Funcao funcao;
    private SubFuncao subFuncao;

    private String modalMessage = new String();
    private Modal<String> modal;
    
    @Inject
    private FuncaoService funcaoService;

    @Inject
    private SubFuncaoService fubFuncaoService;
    
    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private UnidadeExecutoraService unidadeExecutoraService;

    @Inject
    private OrgaoService orgaoService;

    public PanelInformacoesGeraisPage(String id, Page backPage) {
        super(id);
        setOutputMarkupId(true);

        this.backPage = backPage;
        initVariaveis();

        panelPrincipal = new PanelPrincipal("panelPrincipal");
        panelDropDown = new PanelDropDown("panelDropDown");
        add(getTextFieldNumeroSei()); //numeroProcessoSEI
        panelOrgaoExecutor = new PanelOrgaoExecutor("panelOrgaoExecutor");

        add(panelPrincipal);
        add(panelDropDown);
        add(panelOrgaoExecutor);

        modal = getModal("modal");
        modal.show(false);
        add(modal);
    }

    private class PanelPrincipal extends WebMarkupContainer {
        public PanelPrincipal(String id) {
            super(id);
            setOutputMarkupId(true);

            add(getTextFieldNome()); // nomePrograma
            add(getTextFieldNomeFantasia()); // nomeFantasiaPrograma
            add(getTextAreaDescricao()); // descricaoGeral
            add(getTextFieldAno()); // anoPrograma
        }
    }

    private class PanelDropDown extends WebMarkupContainer {
        public PanelDropDown(String id) {
            super(id);
            setOutputMarkupId(true);

            add(getDropDownFuncao()); // dropFuncao
            add(getDropDownSubFuncao()); // subFuncao
        }
    }
    
    private class PanelOrgaoExecutor extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelOrgaoExecutor(String id) {
            super(id);

            add(getDropDownOrgao()); // nomeOrgao
            add(getDropDownUnidadeExecutora()); // unidadeExecutora
        }
    }

    public void initVariaveis() {
        programaBackPage = (ProgramaPage) backPage;
        programa = programaBackPage.getForm().getModelObject();

        if (programa.getId() != null) {
            nomePrograma = programa.getNomePrograma();
            nomeFantasiaPrograma = programa.getNomeFantasiaPrograma();
            anoPrograma = programa.getAnoPrograma().toString();
            anoProgramaTemp = programa.getAnoPrograma().toString();
            funcao = programa.getSubFuncao().getFuncao();
            subFuncao = programa.getSubFuncao();
            orgao = programa.getUnidadeExecutora().getOrgao();
            unidadeExecutora = programa.getUnidadeExecutora();
            numeroProcessoSEI = programa.getNumeroProcessoSEI();
        } else {
            funcao = new Funcao();
            subFuncao = new SubFuncao();
            orgao = new Orgao();
            unidadeExecutora = new UnidadeExecutora();
        }
    }

    /*
     * SERÃO DESENVOLVIDOS OS COMPONENTES
     */
    private TextField<String> getTextFieldNumeroSei() {
        TextField<String> field = new TextField<String>("numeroProcessoSEI", new PropertyModel<String>(this, "numeroProcessoSEI"));
        field.add(StringValidator.maximumLength(20));
        actionTextFieldNumeroSeii(field);
        return field;
    }

    private TextField<String> getTextFieldNome() {
        TextField<String> field = new TextField<String>("nomePrograma", new PropertyModel<String>(this, "nomePrograma"));
        field.add(StringValidator.maximumLength(200));
        actionTextFieldNome(field);
        return field;
    }

    private TextField<String> getTextFieldNomeFantasia() {
        TextField<String> field = new TextField<String>("nomeFantasiaPrograma", new PropertyModel<String>(this, "nomeFantasiaPrograma"));
        field.add(StringValidator.maximumLength(200));
        actionTextFieldNomeFantasia(field);
        return field;
    }

    public TextArea<String> getTextAreaDescricao() {

        text = new TextArea<String>("descricaoPrograma", Model.of(""));
        text.setLabel(Model.of("Descrição"));
        text.setOutputMarkupId(true);
        text.add(StringValidator.maximumLength(Constants.TAMANHO_CARACTERES_CAMPO_TEXT_AREA));
        actionTextArea(text);

        if (programa.getId() != null) {
            text.setModelObject(programa.getDescricaoPrograma());
        }

        return text;
    }

    private TextField<String> getTextFieldAno() {
        TextField<String> field = new TextField<String>("anoPrograma", new PropertyModel<String>(this, "anoPrograma"));
        field.add(StringValidator.exactLength(4));
        field.add(new NumericValidator());
        field.setLabel(Model.of("Ano"));
        actionTextFieldAno(field);
        return field;
    }

    public DropDownChoice<Funcao> getDropDownFuncao() {

        List<Funcao> listFuncao = funcaoService.buscarTodos();
        DropDownChoice<Funcao> drop = new DropDownChoice<Funcao>("dropFuncao", new PropertyModel<Funcao>(this, "funcao"), listFuncao, new ChoiceRenderer<Funcao>("codigoNome", "id"));
        drop.setNullValid(true);
        drop.setOutputMarkupId(true);
        actionDropDownFuncao(drop);
        return drop;
    }

    public DropDownChoice<SubFuncao> getDropDownSubFuncao() {

        List<SubFuncao> listSubFuncao = new ArrayList<SubFuncao>();
        if (funcao != null && funcao.getId() != null) {
            listSubFuncao = fubFuncaoService.buscarPelaFuncaoId(funcao.getId());
        }
        DropDownChoice<SubFuncao> drop = new DropDownChoice<SubFuncao>("subFuncao", new PropertyModel<SubFuncao>(this, "subFuncao"), listSubFuncao, new ChoiceRenderer<SubFuncao>("codigoNome", "id"));
        drop.setOutputMarkupId(true);
        actionDropDownSubFuncao(drop);
        drop.setNullValid(true);
        return drop;
    }
    
    public DropDownChoice<Orgao> getDropDownOrgao() {

        List<Orgao> listOrgao = orgaoService.buscarTodos();
        if(programa == null || programa.getId() == null){
            for (Orgao result : listOrgao) {
                String sigla = result.getSiglaOrgao();
                if (sigla.equals(BUSCA_PADRAO)) {
                    orgao = result;
                    break;
                }
            }   
        }

        DropDownChoice<Orgao> drop = new DropDownChoice<Orgao>("nomeOrgao", new PropertyModel<Orgao>(this, "orgao"), listOrgao, new ChoiceRenderer<Orgao>("codigoOrgaoSigla", "id"));
        drop.setNullValid(true);
        drop.setOutputMarkupId(true);
        actionDropDownOrgao(drop);
        return drop;
    }

    public DropDownChoice<UnidadeExecutora> getDropDownUnidadeExecutora() {

        List<UnidadeExecutora> listUnidadeExecutora = new ArrayList<UnidadeExecutora>();
        if (orgao != null && orgao.getId() != null) {
            listUnidadeExecutora = unidadeExecutoraService.buscarPeloOrgaoId(orgao.getId());
        }
        DropDownChoice<UnidadeExecutora> drop = new DropDownChoice<UnidadeExecutora>("unidadeExecutora", new PropertyModel<UnidadeExecutora>(this, "unidadeExecutora"), listUnidadeExecutora, new ChoiceRenderer<UnidadeExecutora>("codigoNome", "id"));
        actionDropDownUnidadeExecutora(drop);
        drop.setOutputMarkupId(true);
        drop.setNullValid(true);
        return drop;
    }

    /*
     * AS AÇÕES FICAM ABAIXO
     */

    public void actionDropDownOrgao(DropDownChoice drop) {
        drop.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                if (orgao == null || orgao.getId() == null) {
                    unidadeExecutora = new UnidadeExecutora();
                    programa.setUnidadeExecutora(unidadeExecutora);
                }

                panelOrgaoExecutor.addOrReplace(getDropDownUnidadeExecutora());
                target.appendJavaScript("atualizaCssDropDown();");
                target.add(panelOrgaoExecutor);
            }
        });
    }

    public void actionDropDownUnidadeExecutora(DropDownChoice drop) {
        drop.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                programa.setUnidadeExecutora(unidadeExecutora);
            }
        });
    }

    
    public void actionDropDownFuncao(DropDownChoice dropFuncao) {
        dropFuncao.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (funcao == null || funcao.getId() == null) {
                    subFuncao = new SubFuncao();
                    programa.setSubFuncao(subFuncao);
                }
                panelDropDown.addOrReplace(getDropDownSubFuncao());
                target.appendJavaScript("atualizaCssDropDown();");
                target.add(panelDropDown);
            }
        });
    }

    public void actionDropDownSubFuncao(DropDownChoice dropFuncao) {
        dropFuncao.add(new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                programa.setSubFuncao(subFuncao);
            }
        });
    }

    private void actionTextFieldNome(TextField field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                programa.setNomePrograma(nomePrograma);
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    private void actionTextFieldNomeFantasia(TextField field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                programa.setNomeFantasiaPrograma(nomeFantasiaPrograma);
            }
        };
        field.add(onChangeAjaxBehavior);
    }
    
    private void actionTextFieldNumeroSeii(TextField field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                programa.setNumeroProcessoSEI(numeroProcessoSEI);
            }
        };
        field.add(onChangeAjaxBehavior);
    }
    
    private void actionTextFieldAno(TextField field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                int ano = 0;
                if (anoPrograma == null) {
                    ano = 0;
                } else {
                    ano = Integer.parseInt(anoPrograma);
                }
                
                if(programa.getAnoPrograma() != null && !programa.getRecursosFinanceiros().isEmpty()){
                    setModalMessage(getString("MT002"));
                    modal.show(true);
                    target.add(modal);
                }
                
                programa.setAnoPrograma(ano);
                programaBackPage.atualizarDropDownRecursoFinanceiro(target);
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    private void actionTextArea(TextArea field) {
        AjaxFormComponentUpdatingBehavior onChangeAjaxBehavior = new AjaxFormComponentUpdatingBehavior(ONCHANGE) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                programa.setDescricaoPrograma(text.getConvertedInput());
            }
        };
        field.add(onChangeAjaxBehavior);
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public SubFuncao getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(SubFuncao subFuncao) {
        this.subFuncao = subFuncao;
    }

    public String getDescricaoPrograma() {
        return descricaoPrograma;
    }

    public void setDescricaoPrograma(String descricaoPrograma) {
        this.descricaoPrograma = descricaoPrograma;
    }
    
    private Modal<String> getModal(String id) {
        Modal<String> modal = new TextContentModal(id, new LambdaModel<String>(this::getModalMessage, this::setModalMessage));
        modal.addButton(newButtonRemoverRecursosFinanceiro(modal));
        modal.addButton(newButtonCancelarRemocaoRecursosFinanceiro(modal));
        modal.setBackdrop(Backdrop.STATIC);
        return modal;
    }

    private AjaxDialogButton newButtonCancelarRemocaoRecursosFinanceiro(Modal<String> modal) {
        return new AjaxDialogButton(Model.of("Não"), Buttons.Type.Danger){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                anoPrograma = anoProgramaTemp;
                panelPrincipal.addOrReplace(getTextFieldAno());
                modal.close(target);
                modal.show(false);
                target.add(panelPrincipal);
            }
        };
    }
    
    private AjaxDialogButton newButtonRemoverRecursosFinanceiro(Modal<String> modal) {
        return new AjaxDialogButton(Model.of("Sim"), Buttons.Type.Primary){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                anoProgramaTemp = anoPrograma;
                programaBackPage.removerRecursosFinanceiros(target);
                modal.close(target);
                modal.show(false);
            }
        };
    }
    
    public String getModalMessage() {
        return modalMessage;
    }

    public void setModalMessage(String modalMessage) {
        this.modalMessage = modalMessage;
    }

    public Orgao getOrgao() {
        return orgao;
    }

    public void setOrgao(Orgao orgao) {
        this.orgao = orgao;
    }

    public UnidadeExecutora getUnidadeExecutora() {
        return unidadeExecutora;
    }

    public void setUnidadeExecutora(UnidadeExecutora unidadeExecutora) {
        this.unidadeExecutora = unidadeExecutora;
    }

    public String getNumeroProcessoSEI() {
        return numeroProcessoSEI;
    }

    public void setNumeroProcessoSEI(String numeroProcessoSEI) {
        this.numeroProcessoSEI = numeroProcessoSEI;
    }
    
    
}
