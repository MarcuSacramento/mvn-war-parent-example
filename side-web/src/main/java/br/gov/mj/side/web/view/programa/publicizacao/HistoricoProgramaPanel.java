package br.gov.mj.side.web.view.programa.publicizacao;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.util.convert.IConverter;

import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaHistoricoPublicizacao;
import br.gov.mj.side.web.view.components.EntityDataProvider;
import br.gov.mj.side.web.view.components.converters.LocalDateTimeConverter;

public class HistoricoProgramaPanel extends Panel {

    private static final long serialVersionUID = 1L;
    private Programa programa;

    public HistoricoProgramaPanel(String id, Programa programa) {
        super(id);
        this.programa = programa;
        initComponents();
    }

    private void initComponents() {
        add(newDataViewHistoricoPublicizacao());
    }

    private DataView<ProgramaHistoricoPublicizacao> newDataViewHistoricoPublicizacao() {
        return new DataView<ProgramaHistoricoPublicizacao>("historicoPublicizacao", new EntityDataProvider<ProgramaHistoricoPublicizacao>(programa.getHistoricoPublicizacao())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ProgramaHistoricoPublicizacao> item) {
                item.add(new Label("dataCadastro") {
                    @Override
                    public <C> IConverter<C> getConverter(Class<C> type) {
                        return (IConverter<C>) new LocalDateTimeConverter();
                    }
                });
                item.add(new Label("dataPublicacaoDOU"));
                item.add(new Label("dataInicialProposta"));
                item.add(new Label("dataFinalProposta"));
                item.add(new Label("dataInicialAnalise"));
                item.add(new Label("dataFinalAnalise"));
                item.add(new Label("tipoPrograma.descricao"));
                item.add(new Label("statusPrograma.descricao"));
                item.add(new Label("motivo"));
                item.add(new Label("usuarioCadastro"));
            }
        };
    }
}
