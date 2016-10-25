package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.TagBem;
import br.gov.mj.side.web.dao.TagBemDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TagBemService {

    @Inject
    private TagBemDAO tagBemDAO;

    public TagBem buscarPeloId(Long id) {
        return tagBemDAO.buscarPeloId(id);
    }

    public List<TagBem> buscarPeloIdBem(Long id) {
        return buscarPeloIdBem(id, EnumOrder.ASC, "id");
    }

    public List<TagBem> buscarPeloIdBem(Long id, EnumOrder order, String propertyOrder) {
        return tagBemDAO.buscarPeloIdBem(id, order, propertyOrder);
    }

    public Long countPeloIdBem(Long idBem) {
        return tagBemDAO.countPeloIdBem(idBem);
    }

}
