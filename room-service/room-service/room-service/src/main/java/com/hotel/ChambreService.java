package com.hotel.room;

import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;

import java.util.List;

@WebService
public interface ChambreService {

    @WebMethod
    List<Chambre> getAllChambres();

    @WebMethod
    Chambre getChambre(@WebParam(name = "num") int num);

    @WebMethod
    boolean addChambre(@WebParam(name = "chambre") Chambre chambre);

    @WebMethod
    boolean updateChambre(@WebParam(name = "chambre") Chambre chambre);

    @WebMethod
    boolean deleteChambre(@WebParam(name = "num") int num);

    @WebMethod
    List<Chambre> getChambresDisponibles();

    @WebMethod
    List<Chambre> getChambresByType(@WebParam(name = "type") String type);
}
