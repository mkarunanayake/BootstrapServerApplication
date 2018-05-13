package com.bootstrapserver.repository;

import com.bootstrapserver.util.DBConnection;
import messenger.Peer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PeerRepository {
    DBConnection dbConn;
    private static PeerRepository peerRepository;

    private PeerRepository() {
        this.dbConn = DBConnection.getDbConnection();
    }

    public static PeerRepository getPeerRepository() {
        if (peerRepository == null) {
            synchronized (PeerRepository.class) {
                peerRepository = new PeerRepository();
            }
        }
        return peerRepository;
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
                peer.setLastSeen(rs.getLong("last_seen"));
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
                System.out.println("Updated");
            } else {
                stmt = conn.prepareStatement(savePeerStmt);
                System.out.println("Created");
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

    public ArrayList<Peer> getPeerList(int size, int userID) {
        int count = 0;
        Connection conn = dbConn.getConnection();
        ArrayList<Peer> peersList = new ArrayList<>();
        String selectStmt = "SELECT * FROM peer_details WHERE last_seen>0 AND (user_id NOT IN (?)) ORDER BY last_seen DESC";
        try {
            PreparedStatement stmt = conn.prepareStatement(selectStmt);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
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
}
