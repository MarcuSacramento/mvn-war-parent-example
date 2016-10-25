package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.side.web.dao.AnexoBemDAO;
import br.gov.mj.side.web.dto.AnexoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AnexoBemService {

    @Inject
    private AnexoBemDAO anexoBemDAO;

    public AnexoDto buscarPeloId(Long id) {
        return anexoBemDAO.buscarPeloId(id);
    }

    public List<AnexoDto> buscarPeloIdBem(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return anexoBemDAO.buscarPeloIdBem(id);
    }
}
