package com.example.dao;

import com.example.database.DatabaseConnection;
import com.example.models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private final DatabaseConnection dbConnection;

    public ProductDAO(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }
    public Product getProductById(int id) throws SQLException {
        String query = "SELECT * FROM products WHERE idProduit = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try(ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()) {
                    return mapProduct(resultSet);
                }
                return null;
            }
        }
    }

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";
        try (Connection connection = dbConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                products.add(mapProduct(resultSet));
            }
        }
        return products;
    }

    public int addProduct(Product product) throws SQLException {
        String query = "INSERT INTO products (nom, prix, quantiteEnStock) VALUES (?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, product.getNom());
            statement.setDouble(2, product.getPrix());
            statement.setInt(3, product.getQuantiteEnStock());
            return statement.executeUpdate();
        }
    }

    public int updateProduct(Product product) throws SQLException {
        String query = "UPDATE products SET nom = ?, prix = ?, quantiteEnStock = ? WHERE idProduit = ?";
        try(Connection connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, product.getNom());
            statement.setDouble(2, product.getPrix());
            statement.setInt(3, product.getQuantiteEnStock());
            statement.setInt(4,product.getIdProduit());
            return statement.executeUpdate();
        }

    }

    public int deleteProduct(int id) throws SQLException {
        String updateLignesFactureQuery = "UPDATE lignes_facture SET idProduit = ? WHERE idProduit = ?";
        String updateLignesCommandeQuery = "UPDATE lignes_commande SET idProduit = ? WHERE idProduit = ?";
        String deleteProductQuery = "DELETE FROM products WHERE idProduit = ?";

        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            connection.setAutoCommit(false);

            //update the lignes facture to null
            try(PreparedStatement updateLignesFactureStatement = connection.prepareStatement(updateLignesFactureQuery)){
                updateLignesFactureStatement.setNull(1, Types.INTEGER);
                updateLignesFactureStatement.setInt(2, id);
                updateLignesFactureStatement.executeUpdate();
            }

            //update the lignes commandes to null
            try(PreparedStatement updateLignesCommandeStatement = connection.prepareStatement(updateLignesCommandeQuery)){
                updateLignesCommandeStatement.setNull(1, Types.INTEGER);
                updateLignesCommandeStatement.setInt(2, id);
                updateLignesCommandeStatement.executeUpdate();
            }

            //delete the product
            try(PreparedStatement deleteProductStatement = connection.prepareStatement(deleteProductQuery)){
                deleteProductStatement.setInt(1, id);
                deleteProductStatement.executeUpdate();

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

    private Product mapProduct(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setIdProduit(resultSet.getInt("idProduit"));
        product.setNom(resultSet.getString("nom"));
        product.setPrix(resultSet.getDouble("prix"));
        product.setQuantiteEnStock(resultSet.getInt("quantiteEnStock"));
        return product;
    }
}