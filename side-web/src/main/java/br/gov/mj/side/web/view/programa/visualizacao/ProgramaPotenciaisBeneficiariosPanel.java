package br.gov.mj.side.web.view.programa.visualizacao;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import br.gov.mj.side.entidades.programa.PotencialBeneficiarioMunicipio;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaPotencialBeneficiarioUf;

public class ProgramaPotenciaisBeneficiariosPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private Programa programa;

    public ProgramaPotenciaisBeneficiariosPanel(String id, Programa programa) {
        super(id);
        this.programa = programa;
        initComponents();
    }

    private void initComponents() {
        add(newLabelNacional());
        add(newListViewUfMunicipio());
        add(newLabelRegimeJuridico());
    }

    private ListView<ProgramaPotencialBeneficiarioUf> newListViewUfMunicipio() {
        ListView<ProgramaPotencialBeneficiarioUf> listView = new ListView<ProgramaPotencialBeneficiarioUf>("potenciaisBeneficiariosUf", programa.getPotenciaisBeneficiariosUf()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<ProgramaPotencialBeneficiarioUf> item) {
                item.add(new Label("uf.nomeUf", item.getModelObject().getUf().getNomeSigla()));
                item.add(new Label("municipios", getMunicipios(item.getModelObject())));
            }
        };
        listView.setVisible(programa.getPossuiLimitacaoGeografica());
        return listView;
    }

    private String getMunicipios(ProgramaPotencialBeneficiarioUf uf) {
        String municipios = "";
        if (uf.getPotencialBeneficiarioMunicipios().isEmpty()) {
            municipios = "Todos";
        } else {
            for (PotencialBeneficiarioMunicipio potencialBeneficiarioMunicipio : uf.getPotencialBeneficiarioMunicipios()) {
                municipios = municipios + ", " + potencialBeneficiarioMunicipio.getMunicipio().getNomeMunicipio();
            }
            municipios = municipios.substring(2);
        }
        return municipios;

    }

    private Label newLabelRegimeJuridico() {
        String regimeJuridico = "";
        if (programa.getTipoPersonalidadeJuridica().getValor() == "T") {
            regimeJuridico = "Público e Privada sem fins lucrativos";
        } else {
            regimeJuridico = programa.getTipoPersonalidadeJuridica().getDescricao();
        }
        return new Label("tipoPersonalidadeJuridica", regimeJuridico);
    }

    private Label newLabelNacional() {
        Label label = new Label("nacional", "Nacional");
        label.setVisible(!programa.getPossuiLimitacaoGeografica());
        return label;
    }

}
