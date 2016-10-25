package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.side.web.dao.AnexoTermoRecebimentoDefinitivoDAO;
import br.gov.mj.side.web.dto.AnexoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AnexoTermoRecebimentoDefinitivoService {

    @Inject
    private AnexoTermoRecebimentoDefinitivoDAO termoRecebimentoDefinitivoDAO;

    public AnexoDto buscarPeloId(Long id) {
        return termoRecebimentoDefinitivoDAO.buscarPeloId(id);
    }

    public List<AnexoDto> buscarPeloIdTermoRecebimentoDefinitivo(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return termoRecebimentoDefinitivoDAO.buscarPeloIdTermoRecebimentoDefinitivo(id);
    }
    
    public AnexoDto buscarNotaFiscalPeloIdTermoRecebimento(Long idTermoRecebimentoDefinitivo) {
        if (idTermoRecebimentoDefinitivo == null) {
            throw new IllegalArgumentException("Parâmetro idNotaRemessa não pode ser null");
        }
        return termoRecebimentoDefinitivoDAO.buscarNotaFiscalPeloIdTermoRecebimento(idTermoRecebimentoDefinitivo);
    }
}
