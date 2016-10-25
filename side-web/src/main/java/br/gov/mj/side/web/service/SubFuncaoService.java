package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.apoio.entidades.SubFuncao;
import br.gov.mj.side.web.dao.SubFuncaoDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SubFuncaoService {

    @Inject
    private SubFuncaoDAO subFuncaoDAO;

    public List<SubFuncao> buscarPelaFuncaoId(Long id) {
        return subFuncaoDAO.buscarPelaFuncaoId(id);
    }

    public List<SubFuncao> buscar(SubFuncao subFuncao) {
        return subFuncaoDAO.buscar(subFuncao);
    }

}
