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
public class InscricaoAnexoElegibilidadeDAO {

    @Inject
    private EntityManager em;

    public List<AnexoDto> buscarInscricaoAnexoElegibilidade(Long idInscricaoProgramaCriterioElegibilidade) {

        List<AnexoDto> listaRetorno = new ArrayList<AnexoDto>();
        List<?> listaAnexos = em.createQuery("select a.id, a.nomeAnexo, a.dataCadastro, a.tamanho  from InscricaoAnexoElegibilidade a where a.inscricaoProgramaCriterioElegibilidade.id=:idPai order by a.id asc").setParameter("idPai", idInscricaoProgramaCriterioElegibilidade).getResultList();

        for (Object object : listaAnexos) {
            Object[] o = (Object[]) object;
            listaRetorno.add(new AnexoDto((Long) o[0], (String) o[1], null, (LocalDateTime) o[2], (Long) o[3], null));
        }
        return listaRetorno;
    }

    public AnexoDto buscarPeloId(Long id) {

        List<AnexoDto> listaRetorno = new ArrayList<AnexoDto>();
        List<?> listaAnexos = em.createQuery("select a.id, a.nomeAnexo, a.dataCadastro, a.tamanho, a.conteudo  from InscricaoAnexoElegibilidade a where a.id=:id order by a.id asc").setParameter("id", id).getResultList();

        for (Object object : listaAnexos) {
            Object[] o = (Object[]) object;
            listaRetorno.add(new AnexoDto((Long) o[0], (String) o[1], null, (LocalDateTime) o[2], (Long) o[3], (byte[]) o[4]));
        }

        if (listaRetorno.size() > 0) {
            return listaRetorno.get(0);
        } else {
            return new AnexoDto();
        }

    }

}
