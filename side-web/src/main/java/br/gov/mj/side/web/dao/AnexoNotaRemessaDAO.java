package br.gov.mj.side.web.dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.gov.mj.side.entidades.enums.EnumTipoArquivoTermoEntrega;
import br.gov.mj.side.web.dto.AnexoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AnexoNotaRemessaDAO {

    @Inject
    private EntityManager em;

    public List<AnexoDto> buscarPeloIdNotaRemessa(Long idNotaRemessa) {

        List<AnexoDto> listaRetorno = new ArrayList<AnexoDto>();
        List<?> listaAnexos = em.createQuery("select a.id, a.nomeAnexo, a.tipoArquivoTermoEntrega from AnexoNotaRemessa a where a.notaRemessaOrdemFornecimento.id=:idPai order by a.id asc").setParameter("idPai", idNotaRemessa).getResultList();

        for (Object object : listaAnexos) {
            Object[] o = (Object[]) object;
            listaRetorno.add(new AnexoDto((Long) o[0], (String) o[1], (EnumTipoArquivoTermoEntrega) o[2]));
        }
        return listaRetorno;
    }

    public AnexoDto buscarPeloId(Long id) {

        List<AnexoDto> listaRetorno = new ArrayList<AnexoDto>();
        List<?> listaAnexos = em.createQuery("select a.id, a.nomeAnexo, a.tipoArquivoTermoEntrega, a.conteudo from AnexoNotaRemessa a where a.id=:id order by a.id asc").setParameter("id", id).getResultList();

        for (Object object : listaAnexos) {
            Object[] o = (Object[]) object;
            listaRetorno.add(new AnexoDto((Long) o[0], (String) o[1], (EnumTipoArquivoTermoEntrega) o[2], (byte[]) o[3]));
        }

        if (listaRetorno.size() > 0) {
            return listaRetorno.get(0);
        } else {
            return new AnexoDto();
        }
    }
}
