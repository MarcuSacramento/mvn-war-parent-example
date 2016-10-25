package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.side.web.dao.InscricaoAnexoAvaliacaoDAO;
import br.gov.mj.side.web.dto.AnexoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class InscricaoAnexoAvaliacaoService {

    @Inject
    private InscricaoAnexoAvaliacaoDAO inscricaoAnexoAvaliacaoDAO;

    public AnexoDto buscarPeloId(Long id) {
        return inscricaoAnexoAvaliacaoDAO.buscarPeloId(id);
    }

    public List<AnexoDto> buscarInscricaoAnexoAvaliacao(Long idInscricaoProgramaCriterioAvaliacao) {
        if (idInscricaoProgramaCriterioAvaliacao == null) {
            throw new IllegalArgumentException("Parâmetro idInscricaoProgramaCriterioAvaliacao não pode ser null");
        }
        return inscricaoAnexoAvaliacaoDAO.buscarInscricaoAnexoAvaliacao(idInscricaoProgramaCriterioAvaliacao);
    }

}
