package br.gov.mj.side.web.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.side.entidades.CodigoVerificacao;
import br.gov.mj.side.web.dao.CodigoVerificacaoDAO;
import br.gov.mj.side.web.util.SideUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class CodigoVerificacaoService {

    @Inject
    private IGenericPersister genericPersister;

    @Inject
    private CodigoVerificacaoDAO codigoVerificacaoDAO;

    public synchronized CodigoVerificacao geraCodigoAleatorioDeVerificacao() {
        CodigoVerificacao retorno = null;
        int i = 0;
        do {
            String codigoVerificacao = SideUtil.geraCodigoAleatorioDeVerificacao();
            CodigoVerificacao cv = genericPersister.findByUniqueProperty(CodigoVerificacao.class, "descricaoCodigoVerificacao", codigoVerificacao);
            if (cv == null) {
                retorno = codigoVerificacaoDAO.incluir(codigoVerificacao);
            }
            i++;
        } while (retorno == null && i < 100);
        return retorno;
    }
}
