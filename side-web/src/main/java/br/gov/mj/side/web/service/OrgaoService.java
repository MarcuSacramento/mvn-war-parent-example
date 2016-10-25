package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.apoio.entidades.Orgao;
import br.gov.mj.side.web.dao.OrgaoDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class OrgaoService {

    @Inject
    private OrgaoDAO orgaoDAO;

    public List<Orgao> buscarTodos() {
        return orgaoDAO.buscarTodos();
    }

    public List<Orgao> buscar(Orgao orgao) {
        return orgaoDAO.buscar(orgao);
    }

}
