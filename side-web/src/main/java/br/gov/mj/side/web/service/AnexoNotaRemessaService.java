package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.side.web.dao.AnexoNotaRemessaDAO;
import br.gov.mj.side.web.dto.AnexoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AnexoNotaRemessaService {

    @Inject
    private AnexoNotaRemessaDAO anexoNotaRemessaDAO;

    public AnexoDto buscarPeloId(Long id) {
        return anexoNotaRemessaDAO.buscarPeloId(id);
    }

    public List<AnexoDto> buscarPeloIdNotaRemessa(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return anexoNotaRemessaDAO.buscarPeloIdNotaRemessa(id);
    }
}
