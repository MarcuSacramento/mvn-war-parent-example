package br.gov.mj.side.web.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.gov.mj.side.entidades.enums.EnumTipoLista;
import br.gov.mj.side.web.dto.AnexoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ListaPublicadoDAO {

    @Inject
    private EntityManager em;

    public List<AnexoDto> buscarListaAvaliacaoPublicadoPeloIdPrograma(Long idPrograma) {

        List<AnexoDto> listaRetorno = new ArrayList<AnexoDto>();
        List<?> listaAnexos = em.createQuery("select a.id, a.nomeArquivo, a.tipoLista,  a.dataCadastro, a.tamanho, a.usuarioCadastro  from ListaAvaliacaoPublicado a where a.programa.id=:idPai order by a.id asc").setParameter("idPai", idPrograma).getResultList();

        for (Object object : listaAnexos) {
            Object[] o = (Object[]) object;
            listaRetorno.add(new AnexoDto((Long) o[0], (String) o[1], (EnumTipoLista) o[2], (LocalDateTime) o[3], (Long) o[4], (String) o[5], null));
        }
        return listaRetorno;
    }

    public AnexoDto buscarListaAvaliacaoPublicadoPeloId(Long id) {

        List<AnexoDto> listaRetorno = new ArrayList<AnexoDto>();
        List<?> listaAnexos = em.createQuery("select a.id, a.nomeArquivo, a.tipoLista, a.dataCadastro, a.tamanho, a.usuarioCadastro,a.conteudo  from ListaAvaliacaoPublicado a where a.id=:id order by a.id asc").setParameter("id", id).getResultList();

        for (Object object : listaAnexos) {
            Object[] o = (Object[]) object;
            listaRetorno.add(new AnexoDto((Long) o[0], (String) o[1], (EnumTipoLista) o[2], (LocalDateTime) o[3], (Long) o[4], (String) o[5], (byte[]) o[6]));
        }

        if (listaRetorno.size() > 0) {
            return listaRetorno.get(0);
        } else {
            return new AnexoDto();
        }

    }

    public List<AnexoDto> buscarListaElegibilidadePublicadoPeloIdPrograma(Long idPrograma) {

        List<AnexoDto> listaRetorno = new ArrayList<AnexoDto>();
        List<?> listaAnexos = em.createQuery("select a.id, a.nomeArquivo, a.tipoLista,  a.dataCadastro, a.tamanho, a.usuarioCadastro  from ListaElegibilidadePublicado a where a.programa.id=:idPai order by a.id asc").setParameter("idPai", idPrograma).getResultList();

        for (Object object : listaAnexos) {
            Object[] o = (Object[]) object;
            listaRetorno.add(new AnexoDto((Long) o[0], (String) o[1], (EnumTipoLista) o[2], (LocalDateTime) o[3], (Long) o[4], (String) o[5], null));
        }
        return listaRetorno;
    }

    public AnexoDto buscarListaElegibilidadePublicadoPeloId(Long id) {

        List<AnexoDto> listaRetorno = new ArrayList<AnexoDto>();
        List<?> listaAnexos = em.createQuery("select a.id, a.nomeArquivo, a.tipoLista, a.dataCadastro, a.tamanho, a.usuarioCadastro,a.conteudo  from ListaElegibilidadePublicado a where a.id=:id order by a.id asc").setParameter("id", id).getResultList();

        for (Object object : listaAnexos) {
            Object[] o = (Object[]) object;
            listaRetorno.add(new AnexoDto((Long) o[0], (String) o[1], (EnumTipoLista) o[2], (LocalDateTime) o[3], (Long) o[4], (String) o[5], (byte[]) o[6]));
        }

        if (listaRetorno.size() > 0) {
            return listaRetorno.get(0);
        } else {
            return new AnexoDto();
        }

    }
}
