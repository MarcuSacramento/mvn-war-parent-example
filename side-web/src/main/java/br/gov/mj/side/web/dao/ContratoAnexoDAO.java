package br.gov.mj.side.web.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.gov.mj.side.entidades.enums.EnumTipoArquivoContrato;
import br.gov.mj.side.web.dto.AnexoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ContratoAnexoDAO {

    @Inject
    private EntityManager em;

    public List<AnexoDto> buscarPeloIdContrato(Long idContrato) {

        List<AnexoDto> listaRetorno = new ArrayList<AnexoDto>();
        List<?> listaAnexos = em.createQuery("select a.id, a.nomeAnexo, a.tipoArquivoContrato, a.dataDocumento, a.dataCadastro, a.tamanho  from ContratoAnexo a where a.contrato.id=:idPai order by a.id asc").setParameter("idPai", idContrato).getResultList();

        for (Object object : listaAnexos) {
            Object[] o = (Object[]) object;
            listaRetorno.add(new AnexoDto((Long) o[0], (String) o[1], (EnumTipoArquivoContrato) o[2], (LocalDate) o[3], (LocalDateTime) o[4], (Long) o[5], null));
        }
        return listaRetorno;
    }

    public AnexoDto buscarPeloId(Long id) {

        List<AnexoDto> listaRetorno = new ArrayList<AnexoDto>();
        List<?> listaAnexos = em.createQuery("select a.id, a.nomeAnexo, a.tipoArquivoContrato, a.dataDocumento, a.dataCadastro, a.tamanho, a.conteudo  from ContratoAnexo a where a.id=:id order by a.id asc").setParameter("id", id).getResultList();

        for (Object object : listaAnexos) {
            Object[] o = (Object[]) object;
            listaRetorno.add(new AnexoDto((Long) o[0], (String) o[1], (EnumTipoArquivoContrato) o[2], (LocalDate) o[3], (LocalDateTime) o[4], (Long) o[5], (byte[]) o[6]));
        }

        if (listaRetorno.size() > 0) {
            return listaRetorno.get(0);
        } else {
            return new AnexoDto();
        }

    }

}
