package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import com.space.service.ShipServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/ships")
public class ShipController {

    private final ShipService service;

    @Autowired
    public ShipController(ShipServiceImpl shipServiceImpl) {
        this.service = shipServiceImpl;
    }

    @GetMapping
    public List<Ship> getAllShips(    @RequestParam(required = false) String name,
                                      @RequestParam(required = false) String planet,
                                      @RequestParam(required = false) ShipType shipType,
                                      @RequestParam(required = false) Long after,
                                      @RequestParam(required = false) Long before,
                                      @RequestParam(required = false) Boolean isUsed,
                                      @RequestParam(required = false) Double minSpeed,
                                      @RequestParam(required = false) Double maxSpeed,
                                      @RequestParam(required = false) Integer minCrewSize,
                                      @RequestParam(required = false) Integer maxCrewSize,
                                      @RequestParam(required = false) Double minRating,
                                      @RequestParam(required = false) Double maxRating,
                                      @RequestParam(required = false) ShipOrder order,
                                      @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                      @RequestParam(required = false, defaultValue = "3") Integer pageSize
    ) {

        List<Ship> list = service.getAllShips(name, planet, shipType,
                after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize,
                maxCrewSize, minRating, maxRating, order);
        return service.getAllShipsByPage(pageNumber, pageSize, list);
    }

    @GetMapping("/count")
    public Integer getShipsCount(       @RequestParam(required = false) String name,
                                        @RequestParam(required = false) String planet,
                                        @RequestParam(required = false) ShipType shipType,
                                        @RequestParam(required = false) Long after,
                                        @RequestParam(required = false) Long before,
                                        @RequestParam(required = false) Boolean isUsed,
                                        @RequestParam(required = false) Double minSpeed,
                                        @RequestParam(required = false) Double maxSpeed,
                                        @RequestParam(required = false) Integer minCrewSize,
                                        @RequestParam(required = false) Integer maxCrewSize,
                                        @RequestParam(required = false) Double minRating,
                                        @RequestParam(required = false) Double maxRating
    ) {
        return service.getAllShips(name, planet, shipType,
                after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize,
                maxCrewSize, minRating, maxRating, null).size();
    }

    @GetMapping("/{id}")
    public Ship getShipById(@PathVariable Long id) {
        return service.getShipById(id);
    }

    @PostMapping
    public Ship createNewShip(@RequestBody Ship newShip) {
            return service.createNewShip(newShip);
    }

    @PostMapping("/{id}")
    public Ship updateShipById(@RequestBody Ship newShip, @PathVariable Long id) {
        return service.updateShipById(newShip, id);
    }

    @DeleteMapping("/{id}")
    public void deleteShipById(@PathVariable Long id) {
        service.deleteShipById(id);
    }
}
