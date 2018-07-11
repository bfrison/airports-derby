package airports_derby;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;

public class Airport {

    String code, name, city, country;
    int elevation;
    double latitude, longitude;

    public Airport(String code, String name, String city, String country,
        int elevation, double latitude, double longitude) {
        if (!code.matches("[A-Za-z]{3}")) {
            throw new IllegalArgumentException(String.format("%s is not a "
                + "valid airport code", code));
        }
        this.code = code.toUpperCase();
        this.name = name;
        if (!city.matches("[A-Z]([a-z])+(\\.?)(\\x20[A-Z]([a-z])+){0,2}")) {
            throw new IllegalArgumentException(String.format("%s is not a "
                + "valid city name", city));
        }
        this.city = city;
        if (!country.matches("[A-Z]([a-z])+(\\.?)(\\x20[A-Z]([a-z])+){0,2}")) {
            throw new IllegalArgumentException(String.format("%s is not a "
                + "valid country name", country));
        }
        this.country = country;
        if (elevation < -10000 || elevation > 10000) {
            throw new IllegalArgumentException(String.format("%d is not a "
                + "valid elevation", elevation));
        }
        this.elevation = elevation;
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException(String.format("%f is not a "
                + "valid latitude", latitude));
        }
        this.latitude = latitude;
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException(String.format("%f is not a "
                + "valid longitude", longitude));
        }
        this.longitude = longitude;
    }

    Airport(ResultSet rs) throws SQLException {
        this(
                rs.getString("code"),
                rs.getString("name"),
                rs.getString("city"),
                rs.getString("country"),
                rs.getInt("elevation"),
                rs.getDouble("latitude"),
                rs.getDouble("longitude"));
    }

    PreparedStatement toInsertstmt(Connection conn) throws SQLException {
        String query = "INSERT INTO airports VALUES ( ? , ? , ? , ? , ? , ? , ? )";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, this.code);
        ps.setString(2, this.name);
        ps.setString(3, this.city);
        ps.setString(4, this.country);
        ps.setInt(5, this.elevation);
        ps.setDouble(6, this.latitude);
        ps.setDouble(7, this.longitude);
        return ps;
    }

    PreparedStatement toUpdatestmt(Connection conn) throws SQLException {
        String query = "UPDATE airports SET "
            + "name = ?,"
            + "city = ?,"
            + "country = ?,"
            + "elevation = ?,"
            + "latitude = ?,"
            + "longitude = ? "
            + "WHERE code = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, this.name);
        ps.setString(2, this.city);
        ps.setString(3, this.country);
        ps.setInt(4, this.elevation);
        ps.setDouble(5, this.latitude);
        ps.setDouble(6, this.longitude);
        ps.setString(7, this.code);
        return ps;
    }

    public double getDistance(Airport otherAirport) {
        return DistanceCalculator.distance(this.latitude, this.longitude,
            otherAirport.latitude, otherAirport.longitude,
            "K");
    }

    public Airport getNearestAirport(DefaultListModel<Airport> list) {
        double nearestDistance = Double.MAX_VALUE;
        Airport nearestAirport = null;
        Object items[] = list.toArray();
        for (Object item : items) {
            Airport newAirport = (Airport) item;
            double newDistance = DistanceCalculator.distance(this.latitude,
                this.longitude, newAirport.latitude, newAirport.longitude,
                "K");
            if (newDistance < nearestDistance && !newAirport.code.equals(this.code)) {
                nearestDistance = newDistance;
                nearestAirport = newAirport;
            }
        }
        return nearestAirport;
    }

    @Override
    public String toString() {
        return String.format("%s - %s in %s, %s", code, name, city, country);
    }
}
