package br.gov.mj.side.web.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import br.gov.mj.side.entidades.entidade.Pessoa;

public class SortablePessoaEntidadeDataProvider extends SortableDataProvider<Pessoa, String> {

    private static final long serialVersionUID = 1L;
    private List<Pessoa> listaEntidade = new ArrayList<Pessoa>();

    public SortablePessoaEntidadeDataProvider(List<Pessoa> listaEntidade) {
        this.listaEntidade = listaEntidade;
        setSort("nomePessoa", SortOrder.ASCENDING);
    }

    @Override
    public Iterator<? extends Pessoa> iterator(long first, long size) {

        // filtra paginado
        List<Pessoa> listaRetorno = new ArrayList<Pessoa>();

        if (listaEntidade.size() > 0) {
            int inicio = (int) first;
            long fim = first + size;

            if (fim > listaEntidade.size()) {
                fim = listaEntidade.size();
            }
            for (int i = inicio; i < fim; i++) {
                listaRetorno.add(listaEntidade.get(i));
            }
        }
        return listaRetorno.iterator();
    }

    @Override
    public long size() {
        return listaEntidade.size();
    }

    @Override
    public IModel<Pessoa> model(Pessoa object) {
        return new CompoundPropertyModel<Pessoa>(object);
    }
}
