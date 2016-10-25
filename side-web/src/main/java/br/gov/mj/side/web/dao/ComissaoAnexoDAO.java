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
public class ComissaoAnexoDAO {

    @Inject
    private EntityManager em;

    public List<AnexoDto> buscarComissaoAnexo(Long idInscricaoPrograma) {

        List<AnexoDto> listaRetorno = new ArrayList<AnexoDto>();
        List<?> listaAnexos = em.createQuery("select a.id, a.nomeAnexo, a.dataCadastro, a.tamanho, a.descricaoAnexo  from ComissaoAnexo a where a.inscricaoPrograma.id=:idPai order by a.id asc").setParameter("idPai", idInscricaoPrograma).getResultList();

        for (Object object : listaAnexos) {
            Object[] o = (Object[]) object;
            listaRetorno.add(new AnexoDto((Long) o[0], (String) o[1], null, (LocalDateTime) o[2], (Long) o[3], null, (String) o[4]));
        }
        return listaRetorno;
    }

    public AnexoDto buscarPeloId(Long id) {

        List<AnexoDto> listaRetorno = new ArrayList<AnexoDto>();
        List<?> listaAnexos = em.createQuery("select a.id, a.nomeAnexo, a.dataCadastro, a.tamanho, a.conteudo, a.descricaoAnexo  from ComissaoAnexo a where a.id=:id order by a.id asc").setParameter("id", id).getResultList();

        for (Object object : listaAnexos) {
            Object[] o = (Object[]) object;
            listaRetorno.add(new AnexoDto((Long) o[0], (String) o[1], null, (LocalDateTime) o[2], (Long) o[3], (byte[]) o[4], (String) o[5]));
        }

        if (listaRetorno.size() > 0) {
            return listaRetorno.get(0);
        } else {
            return new AnexoDto();
        }

    }

}
