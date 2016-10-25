package br.gov.mj.side.web.view.programa.potenciaisBeneficiarios;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.infra.wicket.component.InfraRadioChoice;
import br.gov.mj.infra.wicket.model.LambdaModel;
import br.gov.mj.side.entidades.enums.EnumConfirme;
import br.gov.mj.side.entidades.enums.EnumPersonalidadeJuridica;
import br.gov.mj.side.entidades.programa.PotencialBeneficiarioMunicipio;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaPotencialBeneficiarioUf;
import br.gov.mj.side.web.service.MunicipioService;
import br.gov.mj.side.web.service.UfService;
import br.gov.mj.side.web.view.components.AjaxDialogButton;
import br.gov.mj.side.web.view.programa.ProgramaPage;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Backdrop;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.TextContentModal;

public class PanelPotenciaisBeneficiariosPage extends Panel {
    private static final long serialVersionUID = 1L;

    private Page backPage;
    private ProgramaPage page;
    private PanelCentral panelCentral;
    private PanelUf panelUf;
    private WebMarkupContainer panelMunicipio;

    private Label labelMensagem;
    private ListMultipleChoice<ProgramaPotencialBeneficiarioUf> listMultiplaUfs;
    private ListMultipleChoice<PotencialBeneficiarioMunicipio> listMultiplaMunicipios;

    private InfraRadioChoice<Boolean> radioChoice;
    private AjaxCheckBox checkPublicoComp;
    private AjaxCheckBox checkPrivateComp;

    private Model<String> mensagem = Model.of("");

    private ProgramaPotencialBeneficiarioUf ufApagar;

    private List<PotencialBeneficiarioMunicipio> selectedPotencialBeneficiarioMunicipios;
    private List<ProgramaPotencialBeneficiarioUf> selectedProgramaPotencialBeneficiarioUfs;
    private List<ProgramaPotencialBeneficiarioUf> listTempUf = new ArrayList<ProgramaPotencialBeneficiarioUf>();
    private List<ProgramaPotencialBeneficiarioUf> listTempUfNivelar = new ArrayList<ProgramaPotencialBeneficiarioUf>();
    private List<PotencialBeneficiarioMunicipio> listaMunicipiosBeneficiados;

    private Boolean limitacaoGeografica = Boolean.FALSE;
    private Boolean limitacaoMunicipioEspecifico = Boolean.FALSE;
    private Boolean checkPublico = false;
    private Boolean checkPrivado = false;

    private EnumPersonalidadeJuridica personalidadeJuridica;
    private int quantidadeUfs = 0; // Irá armazenar a quantidade ufs
                                   // selecionadas.

    private Modal<String> modalConfirmUf;
    private String msgConfirmUf = new String();

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private UfService ufService;

    @Inject
    private MunicipioService municipioService;

    public PanelPotenciaisBeneficiariosPage(String id, Page backPage) {
        super(id);

        setOutputMarkupId(true);

        this.backPage = backPage;
        initVariaveis();
        panelCentral = new PanelCentral("panelCentral");
        add(panelCentral);

        if (page.getForm().getModelObject().getPossuiLimitacaoGeografica() != null && page.getForm().getModelObject().getPossuiLimitacaoGeografica()) {
            panelUf.setVisible(true);
        } else {
            panelUf.setVisible(false);
        }

        if (page.getForm().getModelObject().getPossuiLimitacaoMunicipalEspecifica() != null && page.getForm().getModelObject().getPossuiLimitacaoMunicipalEspecifica()) {
            panelMunicipio.setVisible(true);
        } else {
            panelMunicipio.setVisible(false);
        }
    }

    private class PanelCentral extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelCentral(String id) {
            super(id);
            setOutputMarkupId(true);

            add(labelMensagem); // mensagemBeneficiarios

            add(getRadioLimitacao()); // radioLimitacaoGeografica

            add(getCheckPublico()); // checkPublico
            add(getCheckPrivado()); // checkPrivado
            verificarCheckBox();

            panelUf = new PanelUf("panelUf");

            panelMunicipio = new WebMarkupContainer("panelMunicipio");
            panelMunicipio.add(getMultipleChoiceMunicipios()); // selectedMunicipios
            panelMunicipio.setOutputMarkupId(true);

            modalConfirmUf = getModal("modalConfirmUf");
            modalConfirmUf.show(false);
            add(modalConfirmUf);

            add(panelUf);
            add(panelMunicipio);
        }
    }

    private class PanelUf extends WebMarkupContainer {

        private static final long serialVersionUID = 1L;

        public PanelUf(String id) {
            super(id);
            setOutputMarkupId(true);

            add(getRadioLimitacaoMunicipio()); // radioLimitacaoMunicipio
            add(getMultipleChoice()); // multipleUfs
        }
    }

    public void initVariaveis() {
        page = (ProgramaPage) backPage;

        selectedPotencialBeneficiarioMunicipios = new ArrayList<PotencialBeneficiarioMunicipio>(); // Municipios
                                                                                                   // selecionados
        selectedProgramaPotencialBeneficiarioUfs = new ArrayList<ProgramaPotencialBeneficiarioUf>(); // Ufs
                                                                                                     // selecionadas

        listaMunicipiosBeneficiados = new ArrayList<PotencialBeneficiarioMunicipio>(); // Municipios
                                                                                       // todos

        labelMensagem = new Label("mensagemBeneficiarios", mensagem);
        labelMensagem.setEscapeModelStrings(false);
        labelMensagem.setOutputMarkupId(true);

        Programa programa = page.getForm().getModelObject();

        if (programa.getPossuiLimitacaoGeografica() != null && programa.getId() != null) {
            if (programa.getPossuiLimitacaoGeografica()) {
                setLimitacaoGeografica(programa.getPossuiLimitacaoGeografica());
                extrairProgramasSelecionados(programa);
                extrairMunicipiosSelecionados(selectedProgramaPotencialBeneficiarioUfs);
                preencherListaDeMunicipiosBaseadosNaUf();
                atualizarListaUfComparacao();
                nivelarUfSelecionadaeListTemp();
            }
        } else {
            programa.setPossuiLimitacaoGeografica(false);
        }

        if (programa.getPossuiLimitacaoMunicipalEspecifica() != null) {
            setLimitacaoMunicipioEspecifico(programa.getPossuiLimitacaoMunicipalEspecifica());
        }

        if (programa.getTipoPersonalidadeJuridica() != null) {
            personalidadeJuridica = page.getForm().getModelObject().getTipoPersonalidadeJuridica();
        }
    }

    private void extrairProgramasSelecionados(Programa programa) {
        if (!programa.getPotenciaisBeneficiariosUf().isEmpty()) {
            for (ProgramaPotencialBeneficiarioUf ppbuf : programa.getPotenciaisBeneficiariosUf()) {
                selectedProgramaPotencialBeneficiarioUfs.add(ppbuf);
            }
        }
    }

    private void extrairMunicipiosSelecionados(List<ProgramaPotencialBeneficiarioUf> programaPotencialBeneficiarioUfs) {
        if (!programaPotencialBeneficiarioUfs.isEmpty()) {
            for (ProgramaPotencialBeneficiarioUf uf : programaPotencialBeneficiarioUfs) {
                if (!uf.getPotencialBeneficiarioMunicipios().isEmpty())
                    for (PotencialBeneficiarioMunicipio municipio : uf.getPotencialBeneficiarioMunicipios()) {
                        selectedPotencialBeneficiarioMunicipios.add(municipio);
                    }
            }
        }
    }

    /*
     * ABAIXO VIRÃO OS COMPONENTES
     */

    private ListMultipleChoice<ProgramaPotencialBeneficiarioUf> getMultipleChoice() {
        List<ProgramaPotencialBeneficiarioUf> listaPotenciais = new ArrayList<ProgramaPotencialBeneficiarioUf>();
        List<Uf> listUfs = ufService.buscarTodos();
        for (Uf uf : listUfs) {
            ProgramaPotencialBeneficiarioUf ufPot = new ProgramaPotencialBeneficiarioUf();
            ufPot.setUf(uf);
            listaPotenciais.add(ufPot);
        }
        listMultiplaUfs = new ListMultipleChoice<ProgramaPotencialBeneficiarioUf>("multipleUfs", new PropertyModel<List<ProgramaPotencialBeneficiarioUf>>(this, "selectedProgramaPotencialBeneficiarioUfs"), listaPotenciais);
        listMultiplaUfs.setChoiceRenderer(new ChoiceRenderer<ProgramaPotencialBeneficiarioUf>("uf.nomeSigla", "uf.id"));
        acaoAdicionarUf(listMultiplaUfs);
        return listMultiplaUfs;
    }

    private ListMultipleChoice<PotencialBeneficiarioMunicipio> getMultipleChoiceMunicipios() {

        listMultiplaMunicipios = new ListMultipleChoice<PotencialBeneficiarioMunicipio>("selectedMunicipios", new PropertyModel<List<PotencialBeneficiarioMunicipio>>(this, "selectedPotencialBeneficiarioMunicipios"), listaMunicipiosBeneficiados);
        listMultiplaMunicipios.setChoiceRenderer(new ChoiceRenderer<PotencialBeneficiarioMunicipio>("municipio.nomeMunicipioUf", "municipio.id"));

        acaoAdicionarMunicipio(listMultiplaMunicipios);
        return listMultiplaMunicipios;
    }

    private AjaxCheckBox getCheckPublico() {
        checkPublicoComp = new AjaxCheckBox("checkPublico", new Model<Boolean>(false)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                acaoCheck();
            }
        };
        checkPublicoComp.setOutputMarkupId(true);
        return checkPublicoComp;
    }

    private AjaxCheckBox getCheckPrivado() {
        checkPrivateComp = new AjaxCheckBox("checkPrivado", new Model<Boolean>(false)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                acaoCheck();
            }
        };
        checkPrivateComp.setOutputMarkupId(true);

        return checkPrivateComp;
    }

    private void acaoCheck() {
        checkPrivado = checkPrivateComp.getModelObject();
        checkPublico = checkPublicoComp.getModelObject();

        if (checkPrivado && checkPublico) {
            page.getForm().getModelObject().setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.TODAS);
        } else {
            if (checkPrivado && !checkPublico) {
                page.getForm().getModelObject().setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.PRIVADA);
            } else {
                if (!checkPrivado && checkPublico) {
                    page.getForm().getModelObject().setTipoPersonalidadeJuridica(EnumPersonalidadeJuridica.PUBLICA);
                } else {
                    page.getForm().getModelObject().setTipoPersonalidadeJuridica(null);
                }
            }
        }
    }

    private InfraRadioChoice<Boolean> getRadioLimitacao() {
        InfraRadioChoice<Boolean> radioChoiceLocal = componentFactory.newRadioChoice("radioLimitacaoGeografica", "Limitação Geográfica", true, false, "", "", new LambdaModel<Boolean>(this::getLimitacaoGeografica, this::setLimitacaoGeografica), Arrays.asList(Boolean.TRUE, Boolean.FALSE),
                (target) -> selecionarUf(target));
        radioChoiceLocal.setChoiceRenderer(new IChoiceRenderer<Boolean>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Object getDisplayValue(Boolean object) {
                if (object != null && object) {
                    return "Sim";
                } else {
                    return "Não";
                }
            }

            @Override
            public String getIdValue(Boolean object, int index) {
                return object.toString();
            }

        });
        return radioChoiceLocal;
    }

    private InfraRadioChoice<Boolean> getRadioLimitacaoMunicipio() {
        radioChoice = componentFactory.newRadioChoice("radioLimitacaoMunicipio", "Limitação a municípios(s) específico(s)?", true, false, "", "", new LambdaModel<Boolean>(this::getLimitacaoMunicipioEspecifico, this::setLimitacaoMunicipioEspecifico), Arrays.asList(Boolean.TRUE, Boolean.FALSE), (
                target) -> selecionarMunicipio(target));
        radioChoice.setChoiceRenderer(new IChoiceRenderer<Boolean>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Object getDisplayValue(Boolean object) {
                if (object != null && object) {
                    return "Sim";
                } else {
                    return "Não";
                }
            }

            @Override
            public String getIdValue(Boolean object, int index) {
                return object.toString();
            }

        });
        return radioChoice;
    }

    private Modal<String> getModal(String id) {
        Modal<String> modal = new TextContentModal(id, new LambdaModel<String>(this::getMsgConfirmUf, this::setMsgConfirmUf));
        modal.addButton(newButtonCorfirmarExclusaoMunicipio(modal));
        modal.addButton(newButtonCancelarExclusaoMunicipio(modal));
        modal.setBackdrop(Backdrop.STATIC);
        return modal;
    }

    /*
     * ABAIXO VIRÃO AS AÇÕES
     */

    private AjaxDialogButton newButtonCorfirmarExclusaoMunicipio(Modal<String> modal) {
        return new AjaxDialogButton(Model.of("Confirmar"), Buttons.Type.Primary) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {

                for (int i = listTempUf.size() - 1; i >= 0; i--) {
                    if (ufApagar.getUf().getId().intValue() == listTempUf.get(i).getUf().getId().intValue()) {
                        listTempUf.remove(listTempUf.get(i));
                        break;
                    }
                }

                preencherListaDeMunicipiosBaseadosNaUf();

                for (int i = selectedPotencialBeneficiarioMunicipios.size() - 1; i >= 0; i--) {
                    if (selectedPotencialBeneficiarioMunicipios.get(i).getMunicipio().getUf().getId().intValue() == ufApagar.getUf().getId().intValue()) {
                        selectedPotencialBeneficiarioMunicipios.remove(i);
                    }
                }

                listMultiplaMunicipios.setModelObject(new ArrayList<PotencialBeneficiarioMunicipio>(selectedPotencialBeneficiarioMunicipios));
                listMultiplaMunicipios.setChoices(new ArrayList<PotencialBeneficiarioMunicipio>(listaMunicipiosBeneficiados));
                target.add(panelMunicipio);

                modal.close(target);
            }
        };
    }

    private AjaxDialogButton newButtonCancelarExclusaoMunicipio(Modal<String> modal) {
        return new AjaxDialogButton(Model.of("Cancelar"), Buttons.Type.Danger) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                reverterExclusaoUf();
                preencherListaDeMunicipiosBaseadosNaUf();

                page.getForm().getModelObject().setPotenciaisBeneficiariosUf(selectedProgramaPotencialBeneficiarioUfs);
                listMultiplaUfs.setModelObject(selectedProgramaPotencialBeneficiarioUfs);

                listMultiplaMunicipios.setModelObject(selectedPotencialBeneficiarioMunicipios);

                target.add(panelUf);
                target.add(panelMunicipio);

                modal.close(target);
            }
        };

    }

    private void selecionarUf(AjaxRequestTarget target) {
        panelUf.setVisible(getLimitacaoGeografica());
        boolean limitacaoGeograficaLocal = false;

        if (getLimitacaoGeografica()) {
            radioChoice.setVisible(true);
            limitacaoGeograficaLocal = true;
        } else {
            radioChoice.setVisible(false);
            panelMunicipio.setVisible(false);
            limitacaoGeograficaLocal = false;

            selectedPotencialBeneficiarioMunicipios.clear();
            selectedProgramaPotencialBeneficiarioUfs.clear();

            atualizarListaUfComparacao();

            // Baseado nas ufs selecionadas serão montadas as listas de
            // municipios
            preencherListaDeMunicipiosBaseadosNaUf();

            page.getForm().getModelObject().setPotenciaisBeneficiariosUf(selectedProgramaPotencialBeneficiarioUfs);

            listMultiplaMunicipios.setModelObject(selectedPotencialBeneficiarioMunicipios);
            listMultiplaMunicipios.setChoices(listaMunicipiosBeneficiados);

            target.add(panelMunicipio);
            quantidadeUfs++;

        }

        page.getForm().getModelObject().setPossuiLimitacaoMunicipalEspecifica(false);
        page.getForm().getModelObject().setPossuiLimitacaoGeografica(limitacaoGeograficaLocal);
        target.add(panelUf, panelMunicipio);
    }

    private void selecionarMunicipio(AjaxRequestTarget target) {
        panelMunicipio.setVisible(getLimitacaoMunicipioEspecifico());
        page.getForm().getModelObject().setPossuiLimitacaoMunicipalEspecifica(getLimitacaoMunicipioEspecifico());
        selectedPotencialBeneficiarioMunicipios.clear();

        atualizarListaUfComparacao();

        // Baseado nas ufs selecionadas serão montadas as listas de
        // municipios
        preencherListaDeMunicipiosBaseadosNaUf();

        page.getForm().getModelObject().setPotenciaisBeneficiariosUf(selectedProgramaPotencialBeneficiarioUfs);

        listMultiplaMunicipios.setModelObject(selectedPotencialBeneficiarioMunicipios);
        listMultiplaMunicipios.setChoices(listaMunicipiosBeneficiados);

        target.add(panelMunicipio);
        quantidadeUfs++;

        target.add(panelMunicipio);
    }

    public void acaoAdicionarUf(ListMultipleChoice<ProgramaPotencialBeneficiarioUf> multiple) {
        multiple.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {

                // Somente irá cair dentro do If se a pessoa tiver excluido
                // algum estado
                if (quantidadeUfs > selectedProgramaPotencialBeneficiarioUfs.size()) {
                    verificarUfExcluida(target);
                    return;
                }
                // Adiciona a uf adicionada a lista temporária de UF
                atualizarListaUfComparacao();

                // Baseado nas ufs selecionadas serão montadas as listas de
                // municipios
                preencherListaDeMunicipiosBaseadosNaUf();

                page.getForm().getModelObject().setPotenciaisBeneficiariosUf(selectedProgramaPotencialBeneficiarioUfs);

                listMultiplaMunicipios.setModelObject(selectedPotencialBeneficiarioMunicipios);
                listMultiplaMunicipios.setChoices(listaMunicipiosBeneficiados);

                target.add(panelMunicipio);
                quantidadeUfs++;
            }
        });
    }

    public void acaoAdicionarMunicipio(ListMultipleChoice<PotencialBeneficiarioMunicipio> multiple) {
        multiple.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                for (ProgramaPotencialBeneficiarioUf uf : selectedProgramaPotencialBeneficiarioUfs) {

                    List<PotencialBeneficiarioMunicipio> temp = new ArrayList<PotencialBeneficiarioMunicipio>();
                    for (PotencialBeneficiarioMunicipio municipio : selectedPotencialBeneficiarioMunicipios) {
                        if (municipio.getMunicipio().getUf().getSiglaUf().equalsIgnoreCase(uf.getUf().getSiglaUf())) {
                            temp.add(municipio);
                        }
                    }
                    uf.setPotencialBeneficiarioMunicipios(temp);
                }

                listMultiplaMunicipios.setModelObject(selectedPotencialBeneficiarioMunicipios);
                target.add(panelMunicipio);
            }
        });
    }

    public void acaoLimitacaoGeografica(RadioChoice<EnumConfirme> radio) {
        radio.add(new AjaxFormComponentUpdatingBehavior("onclick") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                Boolean valor = radio.getModelObject().getValor();
                if (valor) {
                    listMultiplaUfs.setVisible(true);
                } else {
                    listMultiplaUfs.setVisible(false);
                }
                target.add(listMultiplaUfs);
            }
        });
    }

    private void atualizarListaUfComparacao() {
        listTempUf.clear();
        if (!selectedProgramaPotencialBeneficiarioUfs.isEmpty()) {
            for (ProgramaPotencialBeneficiarioUf uf : selectedProgramaPotencialBeneficiarioUfs) {
                listTempUf.add(uf);
            }
        }
        quantidadeUfs = listTempUf.size();
    }

    private void reverterExclusaoUf() {
        if (selectedProgramaPotencialBeneficiarioUfs == null) {
            selectedProgramaPotencialBeneficiarioUfs = new ArrayList<ProgramaPotencialBeneficiarioUf>();
        }

        selectedProgramaPotencialBeneficiarioUfs.clear();
        if (!listTempUf.isEmpty()) {
            for (ProgramaPotencialBeneficiarioUf uf : listTempUf) {
                selectedProgramaPotencialBeneficiarioUfs.add(uf);
            }
        }
        quantidadeUfs = listTempUf.size();
    }

    private void preencherListaDeMunicipiosBaseadosNaUf() {
        if (listaMunicipiosBeneficiados == null) {
            listaMunicipiosBeneficiados = new ArrayList<PotencialBeneficiarioMunicipio>();
        }
        listaMunicipiosBeneficiados.clear();
        for (ProgramaPotencialBeneficiarioUf listaUf : selectedProgramaPotencialBeneficiarioUfs) {
            List<Municipio> listaMunicipio = municipioService.buscarPelaUfId(listaUf.getUf().getId());

            for (Municipio municipio : listaMunicipio) {
                PotencialBeneficiarioMunicipio m = new PotencialBeneficiarioMunicipio();
                municipio.setUf(listaUf.getUf());
                m.setMunicipio(municipio);
                m.setPotencialBeneficiarioUF(listaUf);
                listaMunicipiosBeneficiados.add(m);
            }
        }
    }

    public void setarMunicipiosNasUfs() {
        for (ProgramaPotencialBeneficiarioUf listaUf : selectedProgramaPotencialBeneficiarioUfs) {

            List<PotencialBeneficiarioMunicipio> listaMunicipios = new ArrayList<PotencialBeneficiarioMunicipio>();
            for (PotencialBeneficiarioMunicipio municipio : selectedPotencialBeneficiarioMunicipios) {
                if (municipio.getMunicipio().getUf().getId().intValue() == listaUf.getUf().getId().intValue()) {
                    listaMunicipios.add(municipio);
                }
            }
            listaUf.setPotencialBeneficiarioMunicipios(listaMunicipios);
            page.getForm().getModelObject().setPotenciaisBeneficiariosUf(selectedProgramaPotencialBeneficiarioUfs);
        }
    }

    private void verificarCheckBox() {
        EnumPersonalidadeJuridica person = page.getForm().getModelObject().getTipoPersonalidadeJuridica();
        if(page.getForm().getModelObject().getId() == null){
            person = EnumPersonalidadeJuridica.PUBLICA;
            page.getForm().getModelObject().setTipoPersonalidadeJuridica(person);
        }

        if (person == null) {
            checkPrivateComp.setModelObject(false);
            checkPublicoComp.setModelObject(false);
        } else {
            if (person == EnumPersonalidadeJuridica.PUBLICA) {
                checkPrivateComp.setModelObject(false);
                checkPublicoComp.setModelObject(true);
            } else {
                if (person == EnumPersonalidadeJuridica.TODAS) {
                    checkPrivateComp.setModelObject(true);
                    checkPublicoComp.setModelObject(true);
                } else {
                    checkPrivateComp.setModelObject(true);
                    checkPublicoComp.setModelObject(false);
                }
            }
        }
    }

    private void verificarUfExcluida(AjaxRequestTarget target) {
        for (int i = listTempUf.size() - 1; i >= 0; i--) {
            int flag = 0;
            for (ProgramaPotencialBeneficiarioUf listaAtual : selectedProgramaPotencialBeneficiarioUfs) {
                if (listTempUf.get(i).getUf().getId().intValue() == listaAtual.getUf().getId().intValue()) {
                    flag++;
                }
            }

            if (flag == 0) {
                if (listTempUf.get(i).getPotencialBeneficiarioMunicipios() != null && !listTempUf.get(i).getPotencialBeneficiarioMunicipios().isEmpty()) {
                    ufApagar = listTempUf.get(i);
                    setMsgConfirmUf(getString("MN030"));
                    modalConfirmUf.show(true);
                    target.add(modalConfirmUf);
                    break;
                } else {
                    listTempUf.remove(listTempUf.get(i));
                    preencherListaDeMunicipiosBaseadosNaUf();

                    listMultiplaMunicipios.setModelObject(new ArrayList<PotencialBeneficiarioMunicipio>(selectedPotencialBeneficiarioMunicipios));
                    listMultiplaMunicipios.setChoices(new ArrayList<PotencialBeneficiarioMunicipio>(listaMunicipiosBeneficiados));
                    target.add(panelMunicipio);
                    break;
                }
            }
            flag = 0;
        }
        return;
    }

    public void nivelarUfSelecionadaeListTemp() {
        if (!selectedProgramaPotencialBeneficiarioUfs.isEmpty()) {
            listTempUfNivelar.clear();
            for (ProgramaPotencialBeneficiarioUf uf : selectedProgramaPotencialBeneficiarioUfs) {
                listTempUfNivelar.add(uf);
            }
        }
    }

    public void nivelarListTempUfselected() {
        if (!listTempUfNivelar.isEmpty()) {
            for (int i = listTempUfNivelar.size() - 1; i >= 0; i--) {
                for (ProgramaPotencialBeneficiarioUf ufSelected : selectedProgramaPotencialBeneficiarioUfs) {
                    if (listTempUfNivelar.get(i).getUf().getId().intValue() == ufSelected.getUf().getId().intValue()) {
                        ufSelected.setId(listTempUfNivelar.get(i).getId());
                        break;
                    }
                }
            }
        }
        setarMunicipiosNasUfs();
    }

    public String getMsgConfirmUf() {
        return msgConfirmUf;
    }

    public void setMsgConfirmUf(String msgConfirmUf) {
        this.msgConfirmUf = msgConfirmUf;
    }

    public Boolean getLimitacaoMunicipioEspecifico() {
        return limitacaoMunicipioEspecifico;
    }

    public void setLimitacaoMunicipioEspecifico(Boolean limitacaoGeografica) {
        this.limitacaoMunicipioEspecifico = limitacaoGeografica;
    }

    public Boolean getLimitacaoGeografica() {
        return limitacaoGeografica;
    }

    public void setLimitacaoGeografica(Boolean limitacaoGeografica) {
        this.limitacaoGeografica = limitacaoGeografica;
    }

    public EnumPersonalidadeJuridica getPersonalidadeJuridica() {
        return personalidadeJuridica;
    }

    public void setPersonalidadeJuridica(EnumPersonalidadeJuridica personalidadeJuridica) {
        this.personalidadeJuridica = personalidadeJuridica;
    }
}
