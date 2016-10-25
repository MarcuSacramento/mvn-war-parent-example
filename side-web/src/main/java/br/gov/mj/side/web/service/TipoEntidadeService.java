package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.apoio.entidades.TipoEntidade;
import br.gov.mj.side.web.dao.TipoEntidadeDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TipoEntidadeService {

    @Inject
    private TipoEntidadeDAO tipoEntidadeDAO;

    public List<TipoEntidade> buscarTodos() {
        return tipoEntidadeDAO.buscarTodos();
    }

    public List<TipoEntidade> buscar(TipoEntidade tipoEntidade) {
        return tipoEntidadeDAO.buscar(tipoEntidade);
    }

}
