package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;

import java.util.List;

public interface ShipService {
    List<Ship> getAllShips(
            String name,
            String planet,
            ShipType shipType,
            Long after,
            Long before,
            Boolean isUsed,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating,
            ShipOrder order
    );

    List<Ship> getAllShipsByPage(Integer pageNumber, Integer pageSize, List<Ship> ships);

    Ship createNewShip(Ship newShip);

    Ship updateShipById(Ship newCharacteristics, Long id);

    void deleteShipById(Long id);

    Ship getShipById(Long id);

}
