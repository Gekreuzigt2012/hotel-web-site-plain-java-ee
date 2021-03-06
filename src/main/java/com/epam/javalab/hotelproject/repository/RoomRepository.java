package com.epam.javalab.hotelproject.repository;

import com.epam.javalab.hotelproject.model.Request;
import com.epam.javalab.hotelproject.model.Room;
import com.epam.javalab.hotelproject.service.DatabaseService;
import com.epam.javalab.hotelproject.service.DatabaseServiceImpl;
import com.epam.javalab.hotelproject.utils.DateHelper;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides API for finding, inserting, deleting, updating etc. rooms in/from database.
 *
 * @author Iaichnikov Denis,
 * @version 1.0
 * @since 1.0
 */
public class RoomRepository implements RoomDAO {
    private final DatabaseService databaseService = DatabaseServiceImpl.getInstance();
    private final Room emptyRoom = new Room();
    private static final Logger LOGGER = Logger.getLogger(RoomRepository.class);

    @Override
    public List<Room> findAll() {
        List<Room> roomList = new ArrayList<>();
        try (Connection connection = databaseService.takeConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM sql11188080.rooms ")) {
            while (resultSet.next()) {
                roomList.add(new Room(resultSet.getInt("id"),
                        resultSet.getInt("number"),
                        resultSet.getInt("beds"),
                        resultSet.getInt("id_class")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roomList;
    }

    @Override
    public Room findByNumber(int number) {
        if (number == 0) {
            return emptyRoom;
        }
        ResultSet resultSet = null;
        try (Connection connection = databaseService.takeConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM sql11188080.rooms WHERE number = ?")) {
            preparedStatement.setInt(1, number);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Room(resultSet.getInt("id"),
                        resultSet.getInt("number"),
                        resultSet.getInt("beds"),
                        resultSet.getInt("id_class"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null && !resultSet.isClosed()) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return emptyRoom;
    }

    @Override
    public Room findByBeds(int beds) {
        if (beds == 0) {
            return emptyRoom;
        }
        ResultSet resultSet = null;
        try (Connection connection = databaseService.takeConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM sql11188080.rooms WHERE beds = ?")) {
            preparedStatement.setInt(1, beds);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Room(resultSet.getInt("id"),
                        resultSet.getInt("number"),
                        resultSet.getInt("beds"),
                        resultSet.getInt("id_class"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null || !resultSet.isClosed()) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return emptyRoom;
    }

    @Override
    public Room findByClass(int roomClass) {
        if (roomClass == 0) {
            return emptyRoom;
        }
        ResultSet resultSet = null;
        try (Connection connection = databaseService.takeConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM sql11188080.rooms WHERE id_class = ?")) {
            preparedStatement.setInt(1, roomClass);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Room(resultSet.getInt("id"),
                        resultSet.getInt("number"),
                        resultSet.getInt("beds"),
                        resultSet.getInt("id_class"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null || !resultSet.isClosed()) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return emptyRoom;
    }

    @Override
    public boolean insertRoom(Room room) {
        try (Connection connection = databaseService.takeConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO sql11188080.rooms (id, number, beds, id_class) VALUES (?, ?, ?, ?) ")) {
            preparedStatement.setInt(1, room.getId());
            preparedStatement.setInt(2, room.getNumber());
            preparedStatement.setInt(3, room.getBeds());
            preparedStatement.setInt(4, room.getRoomClass());
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateRoom(Room room) {
        try (Connection connection = databaseService.takeConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE `sql11188080`.`rooms` SET  `beds` = ?, `id_class` = ? WHERE number = ?")) {
            preparedStatement.setInt(1, room.getBeds());
            preparedStatement.setInt(2, room.getRoomClass());
            preparedStatement.setInt(3, room.getNumber());
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteRoom(Room room) {
        try (Connection connection = databaseService.takeConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM sql11188080.rooms WHERE number = ?")) {
            preparedStatement.setInt(1, room.getNumber());
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Room> findAvailableRooms(Request request) {
        ResultSet resultSet = null;
        List<Room> roomList = new ArrayList<>();
        try (Connection connection = databaseService.takeConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT rooms.* FROM sql11188080.rooms" +
                             " LEFT JOIN sql11188080.roomstatus ON rooms.id = roomstatus.id_room WHERE roomstatus.date_to < ?" +
                             " OR roomstatus.date_from IS null OR roomstatus.date_to IS null")) {
            preparedStatement.setDate(1, DateHelper.javaToSQLDdate(request.getDateFrom()));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roomList.add(new Room(resultSet.getInt("id"),
                        resultSet.getInt("number"),
                        resultSet.getInt("beds"),
                        resultSet.getInt("id_class")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null || !resultSet.isClosed()) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        for (Room room : roomList) {
            LOGGER.info(room.getNumber());
        }
        return roomList;
    }
}

