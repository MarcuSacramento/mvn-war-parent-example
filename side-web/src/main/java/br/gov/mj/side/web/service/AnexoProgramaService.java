package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.side.web.dao.AnexoProgramaDAO;
import br.gov.mj.side.web.dto.AnexoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AnexoProgramaService {

    @Inject
    private AnexoProgramaDAO anexoProgramaDAO;

    public AnexoDto buscarPeloId(Long id) {
        return anexoProgramaDAO.buscarPeloId(id);
    }

    public List<AnexoDto> buscarPeloIdPrograma(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        return anexoProgramaDAO.buscarPeloIdPrograma(id);
    }

}
