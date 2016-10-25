package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.HistoricoBem;
import br.gov.mj.side.web.dao.HistoricoBemDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class HistoricoBemService {

    @Inject
    private HistoricoBemDAO historicoBemDAO;

    public HistoricoBem buscarPeloId(Long id) {
        return historicoBemDAO.buscarPeloId(id);
    }

    public List<HistoricoBem> buscarPeloIdBem(Long id) {
        return buscarPeloIdBem(id, EnumOrder.ASC, "id");
    }

    public List<HistoricoBem> buscarPeloIdBem(Long id, EnumOrder order, String propertyOrder) {
        return historicoBemDAO.buscarPeloIdBem(id, order, propertyOrder);
    }

    public Long countPeloIdBem(Long idBem) {
        return historicoBemDAO.countPeloIdBem(idBem);
    }

}
