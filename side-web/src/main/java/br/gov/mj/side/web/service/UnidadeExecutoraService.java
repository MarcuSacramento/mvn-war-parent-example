package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.apoio.entidades.UnidadeExecutora;
import br.gov.mj.side.web.dao.UnidadeExecutoraDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UnidadeExecutoraService {

    @Inject
    private UnidadeExecutoraDAO unidadeExecutoraDAO;

    public List<UnidadeExecutora> buscarPeloOrgaoId(Long id) {
        return unidadeExecutoraDAO.buscarPeloOrgaoId(id);
    }

    public List<UnidadeExecutora> buscar(UnidadeExecutora unidadeExecutora) {
        return unidadeExecutoraDAO.buscar(unidadeExecutora);
    }

}
