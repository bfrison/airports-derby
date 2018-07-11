package airports_derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DataLayer {

    private final Connection conn;

    DataLayer() throws SQLException {
        //Type connection string below
        String url = "";
        conn = DriverManager.getConnection(url);
    }

    List<Airport> getAllAirports() throws SQLException {
        List<Airport> airportList = new ArrayList<>();
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM airports");
            while (rs.next()) {
                Airport item = new Airport(rs);
                airportList.add(item);
            }
        }
        return airportList;
    }

    void createAirport(Airport airport) throws SQLException {
        try (PreparedStatement ps = airport.toInsertstmt(conn)) {
            ps.execute();
        }
    }

    void updateAirport(Airport airport) throws SQLException {
        try (PreparedStatement ps = airport.toUpdatestmt(conn)) {
            ps.execute();
        }
    }

    void deleteAirport(String code) throws SQLException {
        String query = "DELETE FROM airports WHERE code= ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, code);
            ps.executeUpdate();
        }
    }
}
