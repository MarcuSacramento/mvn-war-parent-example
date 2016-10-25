package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.side.web.dao.MunicipioDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MunicipioService {

    @Inject
    private MunicipioDAO municipioDAO;

    public List<Municipio> buscarPelaUfId(Long id) {
        return municipioDAO.buscarPelaUfId(id);
    }

    public List<Municipio> buscarPelaUfSigla(String siglaUf) {
        return municipioDAO.buscarPelaUfSigla(siglaUf);
    }

    public List<Municipio> buscar(Municipio municipio) {
        return municipioDAO.buscar(municipio);
    }

}
