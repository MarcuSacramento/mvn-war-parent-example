package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.side.web.dao.AnexoEntidadeDAO;
import br.gov.mj.side.web.dto.AnexoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AnexoEntidadeService {

    @Inject
    private AnexoEntidadeDAO anexoEntidadeDAO;

    public AnexoDto buscarPeloId(Long id) {
        return anexoEntidadeDAO.buscarPeloId(id);
    }

    public List<AnexoDto> buscarPeloIdEntidade(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return anexoEntidadeDAO.buscarPeloIdEntidade(id);
    }

}
