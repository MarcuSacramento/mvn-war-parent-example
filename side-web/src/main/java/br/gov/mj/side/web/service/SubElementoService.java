package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.apoio.entidades.SubElemento;
import br.gov.mj.side.web.dao.SubElementoDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SubElementoService {

    @Inject
    private SubElementoDAO subElementoDAO;

    public List<SubElemento> buscarPeloElementoId(Long id) {
        return subElementoDAO.buscarPeloElementoId(id);
    }

    public List<SubElemento> buscar(SubElemento subElemento) {
        return subElementoDAO.buscar(subElemento);
    }

}
