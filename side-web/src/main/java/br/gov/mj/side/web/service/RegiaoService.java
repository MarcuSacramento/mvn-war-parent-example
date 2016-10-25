package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.apoio.entidades.Regiao;
import br.gov.mj.side.web.dao.RegiaoDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class RegiaoService {

    @Inject
    private RegiaoDAO regiaoDAO;

    public Regiao buscarPeloId(Long id) {
        return regiaoDAO.buscarPeloId(id);
    }

    public List<Regiao> buscarTodos() {
        return regiaoDAO.buscarTodos();
    }

}
