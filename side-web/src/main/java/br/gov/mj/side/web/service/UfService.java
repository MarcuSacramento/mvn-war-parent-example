package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.apoio.entidades.Regiao;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.side.web.dao.UfDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UfService {

    @Inject
    private UfDAO ufDAO;

    public Uf buscarPeloId(Long id) {
        return ufDAO.buscarPeloId(id);
    }

    public List<Uf> buscarTodos() {
        return ufDAO.buscarTodos();
    }

    public List<Uf> buscarPorRegiao(Regiao regiao) {
        return ufDAO.buscarPorRegiao(regiao.getId());
    }

}
