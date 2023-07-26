package application;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;


public class DatabaseConnector {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bienvenu_en_tunisie";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "wahchywahchy12";

    public boolean verifyClientCredentials(String address, String password) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean isValid = false;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            String query = "SELECT * FROM client WHERE email_client = ? AND mot_de_passe = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, address);
            statement.setString(2, password);

            resultSet = statement.executeQuery();

            isValid = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return isValid;
    }
    public boolean verifyAdminCredentials(String address, String password) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean isValid = false;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            String query = "SELECT * FROM admin WHERE email_admin = ? AND mot_de_passe = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, address);
            statement.setString(2, password);

            resultSet = statement.executeQuery();

            isValid = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return isValid;
    }
    public static List<Integer> getReservationIds() {
        List<Integer> reservationIds = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT id_reservation FROM reservation")) {
            while (resultSet.next()) {	
                reservationIds.add(resultSet.getInt("id_reservation"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting reservation IDs: " + e.getMessage());
        }
        return reservationIds;
    }
    public static boolean cancelReservation(int reservationId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM reservation WHERE id_reservation = ?")) {
            
            statement.setInt(1, reservationId);
            
            int rowsAffected = statement.executeUpdate();
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
        public static ObservableList<String> getReservationOptions() {
            ObservableList<String> options = FXCollections.observableArrayList();
            try {
                Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id_reservation, date_debut, date_fin, nb_lits FROM reservation");
                while (rs.next()) {
                    int reservationId = rs.getInt("id_reservation");
                    String startDate = rs.getString("date_debut");
                    String endDate = rs.getString("date_fin");
                    int numBeds = rs.getInt("nb_lits");
                    String option = String.format("%d: %s - %s (%d beds)", reservationId, startDate, endDate, numBeds);
                    options.add(option);
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return options;
        }



}

