package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.apoio.entidades.Funcao;
import br.gov.mj.side.web.dao.FuncaoDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class FuncaoService {

    @Inject
    private FuncaoDAO funcaoDAO;

    public List<Funcao> buscarTodos() {
        return funcaoDAO.buscarTodos();
    }

    public List<Funcao> buscar(Funcao funcao) {
        return funcaoDAO.buscar(funcao);
    }

}
