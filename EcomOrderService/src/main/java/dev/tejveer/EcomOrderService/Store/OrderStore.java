package dev.tejveer.EcomOrderService.Store;

import com.google.gson.reflect.TypeToken;
import dev.tejveer.EcomOrderService.DTO.OrderListResponseDTO;
import dev.tejveer.EcomOrderService.Model.OrderItem;
import dev.tejveer.EcomOrderService.Model.OrderStatus;
import dev.tejveer.EcomOrderService.Impl.dao.OrderDAO;
import dev.tejveer.EcomOrderService.Impl.dao.PaymentDAO;
import dev.tejveer.EcomOrderService.Utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OrderStore {
    private DataSource dataSource;

    public String storeOrderSummary(OrderDAO orderDAO) throws Exception {
        String sqlQuery = "INSERT INTO order_summary (user_id, items_meta_data, order_status, transaction_id, total_amount)" +
                "VALUE (?,?,?,?,?)";
        PreparedStatement pstmt = null;
        Connection connection = null;
        try {
            String itemMetaData = Utils.gson.toJson(orderDAO.getOrderItems());
            connection = DataSourceUtils.getConnection(dataSource);
            pstmt = connection.prepareStatement(sqlQuery);
            pstmt.setString(1, orderDAO.getUserId());
            pstmt.setString(2, itemMetaData);
            pstmt.setString(3, orderDAO.getOrderStatus().name());
            pstmt.setString(4, orderDAO.getTransactionId());
            pstmt.setDouble(5, orderDAO.getTotalAmount());
            ResultSet rs = pstmt.executeQuery();
            String orderId = null;
            if (rs.next()) {
                orderId = rs.getString(1);
            }
            log.info("order summary has been successfully inserted into db table order_summary, orderId ::{}", orderId);
            return orderId;
        } catch (Exception e) {
            throw e;
        } finally {
            if (connection != null) {
                DataSourceUtils.releaseConnection(connection, dataSource);
            }
            if (pstmt != null) {
                pstmt.close();
            }
        }
    }

    public List<OrderListResponseDTO.OrderResponse> getOrderList(String userId) throws Exception {
        String sqlQuery = "SELECT * FROM order_summary WHERE user_id = ?";
        PreparedStatement pstmt = null;
        Connection connection = null;
        try {
            connection = DataSourceUtils.getConnection(dataSource);
            pstmt = connection.prepareStatement(sqlQuery);
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            List<OrderListResponseDTO.OrderResponse> orderResponseList = new ArrayList<>();
            while (rs.next()) {
                Type listType = new TypeToken<List<OrderItem>>() {
                }.getType();
                OrderListResponseDTO.OrderResponse orderResponse = OrderListResponseDTO.OrderResponse.builder()
                        .orderStatus(switch (rs.getString("order_status").toLowerCase()) {
                            case "confirmed" -> OrderStatus.CONFIRMED;
                            case "shipped" -> OrderStatus.SHIPPED;
                            case "cancelled" -> OrderStatus.CANCELLED;
                            default -> OrderStatus.IN_PROGRESS;
                        })
                        .transactionId(rs.getString("transaction_id"))
                        .orderItems(Utils.gson.fromJson(rs.getString("items_meta_data"), listType))
                        .totalAmount(rs.getDouble("total_amount"))
                        .createdAt(rs.getString("created_at"))
                        .build();
                orderResponseList.add(orderResponse);
            }
            log.info("order summary list has been successfully fetched from db table order_summary, totalOrderCount ::{}"
                    , orderResponseList.size());
            return orderResponseList;
        } catch (Exception e) {
            throw e;
        } finally {
            if (connection != null) {
                DataSourceUtils.releaseConnection(connection, dataSource);
            }
            if (pstmt != null) {
                pstmt.close();
            }
        }
    }

    public String updatePaymentStatus(PaymentDAO paymentDAO) throws Exception {
        String sqlQuery = "UPDATE order_summary SET transaction_id = ? , order_status = ? ,payment_status = ? , payment_time_stamp = ? " +
                "WHERE order_id = ? AND user_id = ?";
        PreparedStatement pstmt = null;
        Connection connection = null;
        try {
            connection = DataSourceUtils.getConnection(dataSource);
            pstmt = connection.prepareStatement(sqlQuery);
            pstmt.setString(1, paymentDAO.getTransactionId());
            pstmt.setString(2, paymentDAO.getOrderStatus());
            pstmt.setString(3, paymentDAO.getPaymentStatus());
            pstmt.setString(4, paymentDAO.getPaymentTimeStamp());
            pstmt.setString(5, paymentDAO.getOrderId());
            pstmt.setString(6, paymentDAO.getUserId());
            ResultSet rs = pstmt.executeQuery();
            String orderId = null;
            if (rs.next()) {
                orderId = rs.getString(1);
            }
            log.info("order summary has been successfully inserted into db table order_summary, orderId ::{}", orderId);
            return orderId;
        } catch (Exception e) {
            throw e;
        } finally {
            if (connection != null) {
                DataSourceUtils.releaseConnection(connection, dataSource);
            }
            if (pstmt != null) {
                pstmt.close();
            }
        }
    }


}
