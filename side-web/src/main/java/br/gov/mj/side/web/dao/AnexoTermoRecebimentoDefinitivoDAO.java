package br.gov.mj.side.web.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.gov.mj.side.web.dto.AnexoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AnexoTermoRecebimentoDefinitivoDAO {

    @Inject
    private EntityManager em;

    public List<AnexoDto> buscarPeloIdTermoRecebimentoDefinitivo(Long idNotaRemessa) {

        List<AnexoDto> listaRetorno = new ArrayList<AnexoDto>();
        List<?> listaAnexos = em.createQuery("select a.id, a.nomeAnexo, a.dataGeracao from TermoRecebimentoDefinitivo a where a.notaRecebimentoOrdemFornecimentoContrato.id=:idPai order by a.id asc").setParameter("idPai", idNotaRemessa).getResultList();

        for (Object object : listaAnexos) {
            Object[] o = (Object[]) object;
            listaRetorno.add(new AnexoDto((Long) o[0], (String) o[1], (LocalDateTime) o[2]));
        }
        return listaRetorno;
    }

    public AnexoDto buscarPeloId(Long id) {

        List<AnexoDto> listaRetorno = new ArrayList<AnexoDto>();
        List<?> listaAnexos = em.createQuery("select a.id, a.nomeAnexo, a.dataGeracao, a.termoRecebimentoDefinitivoGerado from TermoRecebimentoDefinitivo a where a.id=:id order by a.id asc").setParameter("id", id).getResultList();

        for (Object object : listaAnexos) {
            Object[] o = (Object[]) object;
            listaRetorno.add(new AnexoDto((Long) o[0], (String) o[1], (LocalDateTime) o[2], (byte[]) o[3]));
        }

        if (listaRetorno.size() > 0) {
            return listaRetorno.get(0);
        } else {
            return new AnexoDto();
        }
    }
    
    public AnexoDto buscarNotaFiscalPeloIdTermoRecebimento(Long idTermoRecebimentoDefinitivo) {

        List<AnexoDto> listaRetorno = new ArrayList<AnexoDto>();
        List<?> listaAnexos = em.createQuery("select a.id, a.numeroNotaFiscal, a.dataGeracao, a.notaFiscal from TermoRecebimentoDefinitivo a where a.id=:id order by a.id asc").setParameter("id", idTermoRecebimentoDefinitivo).getResultList();

        for (Object object : listaAnexos) {
            Object[] o = (Object[]) object;
            listaRetorno.add(new AnexoDto((Long) o[0], (String) o[1], (LocalDateTime) o[2], (byte[]) o[3]));
        }

        if (listaRetorno.size() > 0) {
            return listaRetorno.get(0);
        } else {
            return new AnexoDto();
        }
    }
}
