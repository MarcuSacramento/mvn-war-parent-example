package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.apoio.entidades.Elemento;
import br.gov.mj.side.web.dao.ElementoDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ElementoService {

    @Inject
    private ElementoDAO elementoDAO;

    public List<Elemento> buscarTodos() {
        return elementoDAO.buscarTodos();
    }

    public List<Elemento> buscar(Elemento elemento) {
        return elementoDAO.buscar(elemento);
    }

}
