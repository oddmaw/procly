package com.example.dao;

import com.example.database.DatabaseConnection;
import com.example.models.Client;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    private final DatabaseConnection dbConnection;

    public ClientDAO(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }
    public Client getClientById(int id) throws SQLException {
        String query = "SELECT * FROM clients WHERE idClient = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try(ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()) {
                    return mapClient(resultSet);
                }
                return null;
            }
        }
    }

    public List<Client> getAllClients() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM clients";
        try (Connection connection = dbConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                clients.add(mapClient(resultSet));
            }
        }
        return clients;
    }

    public int addClient(Client client) throws SQLException {
        String query = "INSERT INTO clients (nom, email, telephone) VALUES (?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, client.getNom());
            statement.setString(2, client.getEmail());
            statement.setString(3, client.getTelephone());
            return  statement.executeUpdate() ;
        }
    }

    public int updateClient(Client client) throws SQLException {
        String query = "UPDATE clients SET nom = ?, email = ?, telephone = ? WHERE idClient = ?";
        try(Connection connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, client.getNom());
            statement.setString(2, client.getEmail());
            statement.setString(3, client.getTelephone());
            statement.setInt(4,client.getIdClient());
            return statement.executeUpdate();
        }
    }

    public int deleteClient(int id) throws SQLException {
        String updateFacturesQuery = "UPDATE factures SET idClient = ? WHERE idClient = ?";
        String updateCommandesQuery = "UPDATE commandes SET idClient = ? WHERE idClient = ?";
        String deleteClientQuery = "DELETE FROM clients WHERE idClient = ?";

        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            connection.setAutoCommit(false);

            //update factures table
            try(PreparedStatement updateFacturesStatement = connection.prepareStatement(updateFacturesQuery)){
                updateFacturesStatement.setNull(1, Types.INTEGER);
                updateFacturesStatement.setInt(2, id);
                updateFacturesStatement.executeUpdate();
            }

            // update commandes table
            try(PreparedStatement updateCommandesStatement = connection.prepareStatement(updateCommandesQuery)){
                updateCommandesStatement.setNull(1, Types.INTEGER);
                updateCommandesStatement.setInt(2, id);
                updateCommandesStatement.executeUpdate();
            }

            //delete the client
            try(PreparedStatement deleteClientStatement = connection.prepareStatement(deleteClientQuery)){
                deleteClientStatement.setInt(1, id);
                deleteClientStatement.executeUpdate();
            }

            connection.commit();
            connection.setAutoCommit(true);

            return 1;
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackException) {
                System.out.println("Rollback error " + rollbackException.getMessage());
            }

            throw e;
        } finally {
            if(connection != null){
                try {
                    connection.close();
                } catch(SQLException e){
                    System.out.println("Error closing the connection " + e.getMessage());
                }
            }

        }
    }

    private Client mapClient(ResultSet resultSet) throws SQLException {
        Client client = new Client();
        client.setIdClient(resultSet.getInt("idClient"));
        client.setNom(resultSet.getString("nom"));
        client.setEmail(resultSet.getString("email"));
        client.setTelephone(resultSet.getString("telephone"));
        return client;
    }
}