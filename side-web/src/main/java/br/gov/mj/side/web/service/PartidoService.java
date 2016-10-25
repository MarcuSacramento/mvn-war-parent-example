package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.apoio.entidades.PartidoPolitico;
import br.gov.mj.side.web.dao.PartidoDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PartidoService {

    @Inject
    private PartidoDAO partidoDAO;

    public PartidoPolitico buscarPeloId(Long id) {
        return partidoDAO.buscarPeloId(id);
    }

    public List<PartidoPolitico> buscarTodos() {
        return partidoDAO.buscarTodos();
    }

}
