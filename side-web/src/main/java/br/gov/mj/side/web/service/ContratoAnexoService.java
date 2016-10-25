package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.side.web.dao.ContratoAnexoDAO;
import br.gov.mj.side.web.dto.AnexoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ContratoAnexoService {

    @Inject
    private ContratoAnexoDAO contratoAnexoDAO;

    public AnexoDto buscarPeloId(Long id) {
        return contratoAnexoDAO.buscarPeloId(id);
    }

    public List<AnexoDto> buscarPeloIdContrato(Long idContrato) {
        if (idContrato == null) {
            throw new IllegalArgumentException("Parâmetro idContrato não pode ser null");
        }
        return contratoAnexoDAO.buscarPeloIdContrato(idContrato);
    }

}
