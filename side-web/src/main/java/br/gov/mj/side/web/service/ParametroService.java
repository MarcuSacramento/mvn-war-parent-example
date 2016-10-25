package br.gov.mj.side.web.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.side.web.dao.ParametroDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ParametroService {

    @Inject
    private ParametroDAO parametroDAO;

    public String buscarValorPorChaveSigla(String chaveSigla) {
        return parametroDAO.buscarValorPorChaveSigla(chaveSigla);
    }

    public String getUrlBase(String urlComplemento) {
        if (urlComplemento == null) {
            urlComplemento = "";
        }
        return parametroDAO.buscarValorPorChaveSigla("LINK_APLICACAO") + "/" + urlComplemento;
    }

}
