package com.hotel.room;

import jakarta.jws.WebService;

import java.util.List;

@WebService(
        endpointInterface = "com.hotel.room.ChambreService",
        serviceName = "ChambreService"
)
public class ChambreServiceImpl implements ChambreService {

    private final ChambreDao dao = new ChambreDao();

    @Override
    public List<Chambre> getAllChambres() {
        return dao.findAll();
    }

    @Override
    public Chambre getChambre(int num) {
        return dao.findByNum(num);
    }

    @Override
    public boolean addChambre(Chambre chambre) {
        return dao.insert(chambre);
    }

    @Override
    public boolean updateChambre(Chambre chambre) {
        return dao.update(chambre);
    }

    @Override
    public boolean deleteChambre(int num) {
        return dao.delete(num);
    }

    @Override
    public List<Chambre> getChambresDisponibles() {
        return dao.findAvailable();
    }

    @Override
    public List<Chambre> getChambresByType(String type) {
        return dao.findByType(type);
    }
}
