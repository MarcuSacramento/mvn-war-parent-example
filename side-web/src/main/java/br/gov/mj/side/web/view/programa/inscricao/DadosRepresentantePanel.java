package br.gov.mj.side.web.view.programa.inscricao;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.validator.StringValidator;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraDropDownChoice;
import br.gov.mj.infra.wicket.listener.FeedbackPanelListener;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.web.service.BeneficiarioService;
import br.gov.mj.side.web.view.components.converters.CpfMaskedHideConverter;

public class DadosRepresentantePanel extends Panel {

    private static final long serialVersionUID = 1L;

    private PanelPrincipalSolicitacao panelPrincipalSolicitacao;
    private Entidade entidade;
    private boolean readOnly; 

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private BeneficiarioService beneficiarioService;

    public DadosRepresentantePanel(String id, Entidade entidade,boolean readOnly) {
        super(id);
        setReadOnly(readOnly);
        this.entidade = entidade;

        add(panelPrincipalSolicitacao = new PanelPrincipalSolicitacao("panelPrincipalSolicitacao"));
    }

    private class PanelPrincipalSolicitacao extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public PanelPrincipalSolicitacao(String id) {
            super(id);
            setOutputMarkupId(true);

            add(getDropDownNome());
            add(getTextFieldCpf());
            add(getTextFieldCargo());
            add(getTextFieldTelefone());
            add(getTextFieldEmail());
        };
    }

    public DropDownChoice<PessoaEntidade> getDropDownNome() {
        List<PessoaEntidade> listaPessoaEntidade = new ArrayList<PessoaEntidade>();
        if(entidade!=null && entidade.getId()!=null){
            listaPessoaEntidade = beneficiarioService.buscarRepresentanteEntidade(entidade,true); 
        }
        InfraDropDownChoice<PessoaEntidade> drop = componentFactory.newDropDownChoice("pessoaEntidade", "Tipo", false, "pessoa.id", "pessoa.nomePessoa", null, listaPessoaEntidade, (target) -> atualizarPaineis(target));
        drop.setNullValid(true);
        drop.setOutputMarkupId(true);
        drop.setEnabled(!isReadOnly());
        return drop;
    }

    private  TextField<String>  getTextFieldCpf() {
        
        TextField<String> fieldCpf =  new TextField<String>("pessoaEntidade.pessoa.numeroCpf"){
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new CpfMaskedHideConverter();
            }
        };
        fieldCpf.add(StringValidator.maximumLength(14));
        fieldCpf.setOutputMarkupId(true);
        fieldCpf.setEnabled(false);
        
        return fieldCpf;
    }

    private TextField<String> getTextFieldCargo() {

        TextField<String> field = componentFactory.newTextField("pessoaEntidade.pessoa.descricaoCargo", "Cargo", false, null);
        field.add(StringValidator.maximumLength(200));
        field.setEnabled(false);
        return field;
    }

    private TextField<String> getTextFieldTelefone() {
        TextField<String> field = componentFactory.newTextField("pessoaEntidade.pessoa.numeroTelefone", "Telefone", false, null);
        field.add(StringValidator.maximumLength(13));
        field.setEnabled(false);
        return field;
    }

    private TextField<String> getTextFieldEmail() {

        TextField<String> field = componentFactory.newTextField("pessoaEntidade.pessoa.email", "E-Mail", false, null);
        field.add(StringValidator.maximumLength(200));
        field.setEnabled(false);
        return field;
    }

    private void atualizarPaineis(AjaxRequestTarget target) {
        //Desabilita feedback listener para não remover as mensagens de validação do FeedbackPanel
        RequestCycle.get().setMetaData(FeedbackPanelListener.SKIP_FEEDBACK_LISTENER, Boolean.TRUE);
        target.add(panelPrincipalSolicitacao);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

}
