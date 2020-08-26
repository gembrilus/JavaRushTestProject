package com.space.service;

import com.space.controller.ShipOrder;
import com.space.exceptions.PageNotFoundException;
import com.space.exceptions.WrongRequestException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ShipServiceImpl implements ShipService {

    private final ShipRepository shipRepository;

    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    public Comparator<Ship> getComparator(ShipOrder order) {
        switch (order) {
            case ID: return Comparator.comparing(Ship::getId);
            case SPEED: return Comparator.comparing(Ship::getSpeed);
            case DATE: return Comparator.comparing(Ship::getProdDate);
            case RATING: return Comparator.comparing(Ship::getRating);
            default: return null;
        }
    }

    @Override
    public List<Ship> getAllShips(
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
            ShipOrder order,
            Integer pageNumber,
            Integer pageSize
    ) {
        List<Ship> allShips = shipRepository.findAll();

        if (name != null) {
            allShips = filter(s -> s.getName().contains(name), allShips);
        }

        if (planet != null) {
            allShips = filter(s -> s.getPlanet().contains(planet), allShips);
        }

        if (shipType != null) {
            allShips = filter(s -> s.getShipType().equals(shipType), allShips);
        }

        if (after != null) {
            allShips = filter(ship -> ship.getProdDate().getTime() >= after, allShips);
        }

        if (before != null) {
            allShips = filter(ship -> ship.getProdDate().getTime() <= before, allShips);
        }

        if (isUsed != null){
            allShips = filter(ship -> ship.getUsed() == isUsed, allShips);
        }

        if (minSpeed != null) {
            allShips = filter(ship -> Double.compare(ship.getSpeed(), minSpeed) >= 0, allShips);
        }

        if (maxSpeed != null) {
            allShips = filter(ship -> Double.compare(ship.getSpeed(),maxSpeed) <= 0, allShips);
        }

        if (minCrewSize != null) {
            allShips = filter(ship -> ship.getCrewSize() >= minCrewSize, allShips);
        }

        if (maxCrewSize != null) {
            allShips = filter(ship -> ship.getCrewSize() <= maxCrewSize, allShips);
        }

        if (minRating != null) {
            allShips = filter(ship -> Double.compare(ship.getRating(), minRating) >= 0, allShips);
        }

        if (maxRating != null) {
            allShips = filter(ship -> Double.compare(ship.getRating(), maxRating) <= 0, allShips);
        }
        if (order != null){
            allShips.sort(getComparator(order));
        }

        return allShips;
    }

    @Override
    public Ship createNewShip(Ship newShip) {
        if (       newShip.getName() == null
                || newShip.getPlanet() == null
                || newShip.getShipType() == null
                || newShip.getProdDate() == null
                || newShip.getSpeed() == null
                || newShip.getCrewSize() == null
                || newShip.getPlanet().length() > 50 || newShip.getPlanet().isEmpty()
                || newShip.getName().length() > 50 || newShip.getName().isEmpty()
                || newShip.getSpeed() < 0.01d || newShip.getSpeed() > 0.99d
                || newShip.getCrewSize() < 1 || newShip.getCrewSize() > 9999
        ) {
            throw new WrongRequestException();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(newShip.getProdDate());
        int year = cal.get(Calendar.YEAR);

        if (year < 2800 || year > 3019){
            throw new WrongRequestException();
        }

        newShip.setUsed(newShip.getUsed() != null && newShip.getUsed());
        newShip.setRating(evalRating(newShip));
        return shipRepository.save(newShip);
    }

    @Override
    public Ship updateShipById(Ship newShip, Long id) {

        if (isNotValidId(id)){
            throw new WrongRequestException();
        }
        if (!shipRepository.existsById(id)){
            throw new PageNotFoundException();
        }

        Ship editableShip = getShipById(id);
        String name = newShip.getName();

        if (name != null) {
            if (name.length() > 50 || name.isEmpty()){
                throw new WrongRequestException();
            }
            editableShip.setName(name);
        }

        String planet = newShip.getPlanet();

        if (planet != null) {
            if (planet.length() > 50 || planet.isEmpty()) {
                throw new WrongRequestException();
            }
            editableShip.setPlanet(planet);
        }

        if (newShip.getShipType() != null)
            editableShip.setShipType(newShip.getShipType());

        if (newShip.getUsed() != null) {
            editableShip.setUsed(newShip.getUsed());
        }

        if (newShip.getProdDate() != null) {

            Calendar cal = Calendar.getInstance();
            cal.setTime(newShip.getProdDate());
            int prodDate = cal.get(Calendar.YEAR);

            if (prodDate < 2800 || prodDate > 3019){
                throw new WrongRequestException();
            }

            editableShip.setProdDate(newShip.getProdDate());
        }

        Double speed = newShip.getSpeed();

        if (speed != null) {
            if (speed < 0.01d || speed > 0.99d){
                throw new WrongRequestException();
            }
            editableShip.setSpeed(speed);
        }

        Integer crewSize = newShip.getCrewSize();

        if (crewSize != null) {
            if (crewSize < 1 || crewSize > 9999) {
                throw new WrongRequestException();
            }

            editableShip.setCrewSize(crewSize);
        }
        editableShip.setRating(evalRating(editableShip));
        return editableShip;
    }

    @Override
    public void deleteShipById(Long id) {
        if (isNotValidId(id)){
            throw new WrongRequestException();
        }

        if (!shipRepository.existsById(id)){
            throw new PageNotFoundException();
        }

        shipRepository.deleteById(id);
    }

    @Override
    public Ship getShipById(Long id) {
        if (isNotValidId(id)) {
            throw new WrongRequestException();
        }

        if (!shipRepository.existsById(id)) {
            throw new PageNotFoundException();
        }

        return shipRepository.findById(id).orElse(null);
    }

    private Double evalRating(Ship ship) {
        double speed = ship.getSpeed();
        double coefficient = ship.getUsed() ? 0.5d : 1.0d;

        Calendar cal = Calendar.getInstance();
        cal.setTime(ship.getProdDate());
        double y1 = cal.get(Calendar.YEAR);

        double result = (80 * speed * coefficient) / (3019 - y1 + 1);
        return (double) Math.round(result * 100) / 100;
    }

    private boolean isNotValidId(Long id) {
        return id == null || id <= 0 || id != Math.floor(id);
    }

    private List<Ship> filter(Predicate<Ship> predicate, List<Ship> list){
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public List<Ship> getAllShipsByPage(Integer pageNumber, Integer pageSize, List<Ship> ships) {
        int skip = pageNumber * pageSize;
        List<Ship> result = new ArrayList<>();
        for (int i = skip; i < Math.min(skip + pageSize, ships.size()); i++) {
            result.add(ships.get(i));
        }
        return result;
    }

}
