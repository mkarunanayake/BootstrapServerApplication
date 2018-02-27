package com.bootstrapserver.repository;

import com.bootstrapserver.model.Peer;
import com.bootstrapserver.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;

public class PeerRepository {
    DBConnection dbConn;

    public PeerRepository() {
        this.dbConn = new DBConnection();
    }

    public Peer getPeer(int userID) {
        Connection conn = dbConn.getConnection();
        Peer peer = null;
        String selectStmt = "SELECT * FROM peer_details NATURAL JOIN user_details WHERE user_id = ?";
        try {
            PreparedStatement psmt = conn.prepareStatement(selectStmt);
            psmt.setInt(1, userID);
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                peer = new Peer();
                peer.setPeerAddress(rs.getString("peer_address"));
                peer.setPeerPort(rs.getInt("peer_port"));
                peer.setUserID(rs.getInt("user_id"));
            }
            psmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return peer;
    }

    public void updatePeerInfo(Peer peer) {
        Connection conn = dbConn.getConnection();
        String updateStmt = "UPDATE peer_details SET peer_address=?, peer_port=?, last_seen=? WHERE user_id = ?";
        String savePeerStmt = "INSERT INTO peer_details(peer_address, peer_port, last_seen, user_id) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement stmt = null;
            if (this.getPeer(peer.getUserID()) != null) {
                stmt = conn.prepareStatement(updateStmt);
            } else {
                stmt = conn.prepareStatement(savePeerStmt);
            }
            stmt.setString(1, peer.getPeerAddress());
            stmt.setInt(2, peer.getPeerPort());
            stmt.setLong(3, peer.getLastSeen());
            stmt.setInt(4, peer.getUserID());
            stmt.execute();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Peer> getPeerList(int size) {
        int count = 0;
        Connection conn = dbConn.getConnection();
        ArrayList<Peer> peersList = new ArrayList<>();
        String selectStmt = "SELECT * FROM peer_details WHERE last_seen>0 ORDER BY last_seen DESC";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(selectStmt);
            while (rs.next() && count < size) {
                Peer peer = new Peer();
                peer.setUserID(rs.getInt("user_id"));
                peer.setPeerPort(rs.getInt("peer_port"));
                peer.setPeerAddress(rs.getString("peer_address"));
                peersList.add(peer);
                count += 1;
            }
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return peersList;
    }

    public void setupPeerTable() {
        Connection conn = dbConn.getConnection();
        String createStmt = "CREATE TABLE peer_details(" +
                "user_id INT, " +
                "peer_address VARCHAR(15)," +
                "peer_port INT," +
                "last_seen BIGINT," +
                "PRIMARY KEY (user_id)," +
                "FOREIGN KEY (user_id) references user_details)";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(createStmt);
            stmt.close();
            conn.close();
            System.out.println("Peer Table Created");
        } catch (SQLException e) {
            if (e.getSQLState().equals("X0Y32")) {
                System.out.println("Peer Table Already created");
                return;
            }
            e.printStackTrace();
        }
    }
}
