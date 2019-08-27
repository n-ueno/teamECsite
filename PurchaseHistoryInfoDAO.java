package com.internousdev.oregon.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.internousdev.oregon.dto.PurchaseHistoryInfoDTO;
import com.internousdev.oregon.util.DBConnector;

public class PurchaseHistoryInfoDAO {

	public List<PurchaseHistoryInfoDTO> getPurchaseHistoryList(String userId) {
		DBConnector dbConnector = new DBConnector();
		Connection connection = dbConnector.getConnection();
		List<PurchaseHistoryInfoDTO> purchaseHistoryInfoDTOList = new ArrayList<PurchaseHistoryInfoDTO>();
		String sql = "SELECT"
				+ " phi.id as id,"
				+ " phi.user_id as user_id,"
				+ " phi.product_count as product_count,"
				+ " pi.product_id as product_id,"
				+ " pi.product_name as product_name,"
				+ " pi.product_name_kana as product_name_kana,"
				+ " pi.product_description as product_description,"
				+ " pi.category_id as category_id,"
				+ " pi.image_file_name as image_file_name,"
				+ " pi.image_file_path as image_file_path,"
				+ " pi.release_company,"
				+ " pi.release_date,"
				+ " phi.price as price,"
				+ " phi.price * phi.product_count as subtotal,"
				+ " phi.regist_date as regist_date,"
				+ " di.family_name as family_name,"
				+ " di.first_name as first_name,"
				+ " di.user_address as user_address"
				+ " FROM purchase_history_info as phi"
				+ " LEFT JOIN product_info as pi"
				+ " ON phi.product_id = pi.product_id"
				+ " LEFT JOIN destination_info as di"
				+ " ON phi.destination_id = di.id"
				+ " WHERE phi.user_id = ?"
				+ " ORDER BY regist_date DESC";

		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userId);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				PurchaseHistoryInfoDTO purchaseHistoryInfoDTO = new PurchaseHistoryInfoDTO();
				purchaseHistoryInfoDTO.setId(resultSet.getInt("id"));
				purchaseHistoryInfoDTO.setUserId(resultSet.getString("user_id"));
				purchaseHistoryInfoDTO.setProductId(resultSet.getInt("product_id"));
				purchaseHistoryInfoDTO.setProductName(resultSet.getString("product_name"));
				purchaseHistoryInfoDTO.setProductNameKana(resultSet.getString("product_name_kana"));
				purchaseHistoryInfoDTO.setImageFileName(resultSet.getString("image_file_name"));
				purchaseHistoryInfoDTO.setImageFilePath(resultSet.getString("image_file_path"));
				purchaseHistoryInfoDTO.setReleaseCompany(resultSet.getString("release_company"));
				purchaseHistoryInfoDTO.setReleaseDate(resultSet.getDate("release_date"));
				purchaseHistoryInfoDTO.setPrice(resultSet.getInt("price"));
				purchaseHistoryInfoDTO.setProductCount(resultSet.getInt("product_count"));
				purchaseHistoryInfoDTO.setSubtotal(resultSet.getInt("subtotal"));
				purchaseHistoryInfoDTO.setFamilyName(resultSet.getString("family_name"));
				purchaseHistoryInfoDTO.setFirstName(resultSet.getString("first_name"));
				purchaseHistoryInfoDTO.setUserAddress(resultSet.getString("user_address"));
				purchaseHistoryInfoDTOList.add(purchaseHistoryInfoDTO);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return purchaseHistoryInfoDTOList;
	}

	public int regist(String userId, int productId, int productCount, int destinationId, int price) {
		DBConnector dbConnector = new DBConnector();
		Connection connection = dbConnector.getConnection();
		String sql = "insert into purchase_history_info"
				+ "(user_id, product_id, product_count, price, destination_id, regist_date, update_date) "
				+ "values (?, ?, ?, ?, ?, now(), now())";
		int count = 0;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userId);
			preparedStatement.setInt(2, productId);
			preparedStatement.setInt(3, productCount);
			preparedStatement.setInt(4, price);
			preparedStatement.setInt(5, destinationId);
			count = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	public int deleteAll(String userId) {
		DBConnector dbConnector = new DBConnector();
		Connection connection = dbConnector.getConnection();
		String sql = "delete from purchase_history_info where user_id=?";
		int  count = 0;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userId);
			count = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

}
