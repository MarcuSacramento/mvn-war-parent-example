package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.side.web.dao.InscricaoAnexoAnaliseDAO;
import br.gov.mj.side.web.dto.AnexoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class InscricaoAnexoAnaliseService {

    @Inject
    private InscricaoAnexoAnaliseDAO inscricaoAnexoAnaliseDAO;

    public AnexoDto buscarPeloId(Long id) {
        return inscricaoAnexoAnaliseDAO.buscarPeloId(id);
    }

    public List<AnexoDto> buscarPeloIdEntidade(Long idInscricaoPrograma) {
        if (idInscricaoPrograma == null) {
            throw new IllegalArgumentException("Parâmetro idInscricaoPrograma não pode ser null");
        }
        return inscricaoAnexoAnaliseDAO.buscarInscricaoAnexoAnalise(idInscricaoPrograma);
    }

}
