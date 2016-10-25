package br.gov.mj.side.web.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.side.entidades.enums.EnumAnaliseFinalItem;
import br.gov.mj.side.entidades.enums.EnumFormaVerificacaoFormatacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.web.dao.ObjetoFornecimentoContratoDAO;
import br.gov.mj.side.web.dto.ObjetoFornecimentoContratoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ObjetoFornecimentoContratoService {

    private Map<String, BigDecimal> mapaBensDoContrato;

    @Inject
    private IGenericPersister genericPersister;

    @Inject
    private ObjetoFornecimentoContratoDAO objetoFornecimentoContratoDAO;
    @Inject
    private NotaRemessaService notaRemessaService;

    public void devolverParaCorrecao(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        objetoFornecimentoContratoDAO.devolverParaCorrecao(objetoFornecimentoContrato);
        notaRemessaService.atualizarAnaliseFinalObjetoFornecimentoContrato(objetoFornecimentoContrato, EnumAnaliseFinalItem.DEVOLVIDO);
    }

    public void setarNotaRemessaNoObjetoFornecimento(ObjetoFornecimentoContrato objetoFornecimentoContrato, NotaRemessaOrdemFornecimentoContrato notaRemessa) {

        if (objetoFornecimentoContrato == null || objetoFornecimentoContrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro objetoFornecimentoContrato não pode ser null");
        }

        if (notaRemessa == null || notaRemessa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        objetoFornecimentoContratoDAO.setarNotaRemessaNoObjetoFornecimento(objetoFornecimentoContrato, notaRemessa);
    }

    public void retirarNotaRemessaNoObjetoFornecimento(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        if (objetoFornecimentoContrato == null || objetoFornecimentoContrato.getId() == null) {
            throw new IllegalArgumentException("Parâmetro objetoFornecimentoContrato não pode ser null");
        }
        objetoFornecimentoContratoDAO.retirarNotaRemessaNoObjetoFornecimento(objetoFornecimentoContrato);
    }

    public List<ObjetoFornecimentoContrato> buscarSemPaginacao(ObjetoFornecimentoContratoDto objetoDto) {
        return objetoFornecimentoContratoDAO.buscarSemPaginacao(objetoDto);
    }

    public List<ObjetoFornecimentoContrato> verificarObjetosPassiveisDeDevolucao(List<ObjetoFornecimentoContrato> listaObjetoFornecimentoContratos) {

        String chave = "";
        Map<String, List<ObjetoFornecimentoContrato>> mapa = new HashMap<String, List<ObjetoFornecimentoContrato>>();

        for (ObjetoFornecimentoContrato ofo : listaObjetoFornecimentoContratos) {
            chave = ofo.getItem().getId().toString() + "-" + ofo.getOrdemFornecimento().getId().toString() + "-" + ofo.getLocalEntrega().getId().toString();

            if (mapa.containsKey(chave)) {
                mapa.get(chave).add(ofo);
            } else {

                List<ObjetoFornecimentoContrato> lista = new ArrayList<ObjetoFornecimentoContrato>();
                lista.add(ofo);
                mapa.put(chave, lista);
            }
        }
        List<ObjetoFornecimentoContrato> listaRetornar = new ArrayList<ObjetoFornecimentoContrato>();
        listaRetornar = montarListaObjeto(mapa);
        return listaRetornar;
    }

    private List<ObjetoFornecimentoContrato> montarListaObjeto(Map<String, List<ObjetoFornecimentoContrato>> mapa) {
        for (Map.Entry<String, List<ObjetoFornecimentoContrato>> pair : mapa.entrySet()) {

            if (pair.getValue().size() == 1) {
                pair.getValue().get(0).setMostrarItem(true);
            } else {
                for (ObjetoFornecimentoContrato ofo : pair.getValue()) {
                    if (ofo.getFormaVerificacao() == EnumFormaVerificacaoFormatacao.LOTE) {
                        ofo.setMostrarItem(false);
                    } else {
                        ofo.setMostrarItem(true);
                    }
                }
            }
        }

        List<ObjetoFornecimentoContrato> listaRetornar = new ArrayList<ObjetoFornecimentoContrato>();

        for (Map.Entry<String, List<ObjetoFornecimentoContrato>> pair : mapa.entrySet()) {
            for (ObjetoFornecimentoContrato ofo : pair.getValue()) {
                listaRetornar.add(ofo);
            }
        }
        return listaRetornar;
    }

}
