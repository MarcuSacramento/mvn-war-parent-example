package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.side.web.dao.ComissaoAnexoDAO;
import br.gov.mj.side.web.dto.AnexoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ComissaoAnexoService {

    @Inject
    private ComissaoAnexoDAO comissaoAnexoDAO;

    public AnexoDto buscarPeloId(Long id) {
        return comissaoAnexoDAO.buscarPeloId(id);
    }

    public List<AnexoDto> buscarComissaoAnexo(Long idInscricaoPrograma) {
        if (idInscricaoPrograma == null) {
            throw new IllegalArgumentException("Parâmetro idInscricaoPrograma não pode ser null");
        }
        return comissaoAnexoDAO.buscarComissaoAnexo(idInscricaoPrograma);
    }

}
