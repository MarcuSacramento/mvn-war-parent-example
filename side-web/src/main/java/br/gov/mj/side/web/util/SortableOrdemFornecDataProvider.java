package br.gov.mj.side.web.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;

public class SortableOrdemFornecDataProvider extends SortableDataProvider<OrdemFornecimentoContrato, String> {
    private static final long serialVersionUID = 1L;
    private List<OrdemFornecimentoContrato> listOrdemFornecTemp = new ArrayList<OrdemFornecimentoContrato>();

    public SortableOrdemFornecDataProvider(List<OrdemFornecimentoContrato> lista) {
        setSort("id", SortOrder.DESCENDING);
        this.listOrdemFornecTemp = lista;
    }

    @Override
    public Iterator<OrdemFornecimentoContrato> iterator(long first, long size) {
        List<OrdemFornecimentoContrato> contratoTemp = new ArrayList<OrdemFornecimentoContrato>();
        List<OrdemFornecimentoContrato> novaLista = new ArrayList<OrdemFornecimentoContrato>();
        List<String> listaNome = new ArrayList<String>();
        for (OrdemFornecimentoContrato nome : this.listOrdemFornecTemp) {
            listaNome.add(nome.getId().toString());
        }
        Collections.sort(listaNome);
        for (String nome : listaNome) {
            for (OrdemFornecimentoContrato objeto : this.listOrdemFornecTemp) {
                if (nome.equals(objeto.getId())) {
                    novaLista.add(objeto);
                    break;
                }
            }
        }
        int firstTemp = 0;
        int flagTemp = 0;
        for (OrdemFornecimentoContrato k : novaLista) {
            if (firstTemp >= first) {
                if (flagTemp <= size) {
                    contratoTemp.add(k);
                    flagTemp++;
                }
            }
            firstTemp++;
        }
        return contratoTemp.iterator();
    }

    @Override
    public long size() {
        return this.listOrdemFornecTemp.size();
    }

    @Override
    public IModel<OrdemFornecimentoContrato> model(OrdemFornecimentoContrato object) {
        return new CompoundPropertyModel<OrdemFornecimentoContrato>(object);
    }
}
