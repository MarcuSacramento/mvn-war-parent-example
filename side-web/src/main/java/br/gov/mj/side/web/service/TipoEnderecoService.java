package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.apoio.entidades.TipoEndereco;
import br.gov.mj.side.web.dao.TipoEnderecoDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TipoEnderecoService {

    @Inject
    private TipoEnderecoDAO tipoEnderecoDAO;

    public List<TipoEndereco> buscarTodos() {
        return tipoEnderecoDAO.buscarTodos();
    }

    public List<TipoEndereco> buscar(TipoEndereco tipoEndereco) {
        return tipoEnderecoDAO.buscar(tipoEndereco);
    }

}
