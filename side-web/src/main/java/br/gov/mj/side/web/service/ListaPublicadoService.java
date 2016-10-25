package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.side.web.dao.ListaPublicadoDAO;
import br.gov.mj.side.web.dto.AnexoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ListaPublicadoService {

    @Inject
    private ListaPublicadoDAO listaPublicadoDAO;

    public AnexoDto buscarListaAvaliacaoPublicadoPeloId(Long id) {
        return listaPublicadoDAO.buscarListaAvaliacaoPublicadoPeloId(id);
    }

    public AnexoDto buscarListaElegibilidadePublicadoPeloId(Long id) {
        return listaPublicadoDAO.buscarListaElegibilidadePublicadoPeloId(id);
    }

    public List<AnexoDto> buscarListaAvaliacaoPublicadoPeloIdPrograma(Long idPrograma) {
        if (idPrograma == null) {
            throw new IllegalArgumentException("Parâmetro idPrograma não pode ser null");
        }
        return listaPublicadoDAO.buscarListaAvaliacaoPublicadoPeloIdPrograma(idPrograma);
    }

    public List<AnexoDto> buscarListaElegibilidadePublicadoPeloIdPrograma(Long idPrograma) {
        if (idPrograma == null) {
            throw new IllegalArgumentException("Parâmetro idPrograma não pode ser null");
        }
        return listaPublicadoDAO.buscarListaElegibilidadePublicadoPeloIdPrograma(idPrograma);
    }

}
