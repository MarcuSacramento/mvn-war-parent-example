package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.side.web.dao.AnexoTermoDoacaoDAO;
import br.gov.mj.side.web.dto.AnexoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AnexoTermoDoacaoService {

    @Inject
    private AnexoTermoDoacaoDAO termoDoacaoDAO;

    public AnexoDto buscarPeloId(Long id) {
        return termoDoacaoDAO.buscarPeloId(id);
    }

    public List<AnexoDto> buscarPeloIdTermoRecebimentoDefinitivo(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return termoDoacaoDAO.buscarPeloIdTermoDoacao(id);
    }
}
