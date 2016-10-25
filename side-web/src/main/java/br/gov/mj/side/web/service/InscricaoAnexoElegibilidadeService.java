package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.side.web.dao.InscricaoAnexoElegibilidadeDAO;
import br.gov.mj.side.web.dto.AnexoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class InscricaoAnexoElegibilidadeService {

    @Inject
    private InscricaoAnexoElegibilidadeDAO inscricaoAnexoElegibilidadeDAO;

    public AnexoDto buscarPeloId(Long id) {
        return inscricaoAnexoElegibilidadeDAO.buscarPeloId(id);
    }

    public List<AnexoDto> buscarInscricaoAnexoElegibilidade(Long idInscricaoProgramaCriterioElegibilidade) {
        if (idInscricaoProgramaCriterioElegibilidade == null) {
            throw new IllegalArgumentException("Parâmetro idInscricaoProgramaCriterioElegibilidade não pode ser null");
        }
        return inscricaoAnexoElegibilidadeDAO.buscarInscricaoAnexoElegibilidade(idInscricaoProgramaCriterioElegibilidade);
    }

}
