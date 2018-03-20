package messenger;

import com.bootstrapserver.repository.PeerRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class OnlinePeerHandler {

    private static PeerRepository peerRepository = PeerRepository.getPeerRepository();
    private static HashMap<Integer, Peer> onlinePeers = new HashMap<>();
    private static ReadWriteLock onlinePeersRWLock = new ReentrantReadWriteLock();

    public static void startHandler() {
        ExecutorService availabilityExecutor = Executors.newSingleThreadExecutor();
        availabilityExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!onlinePeers.isEmpty()) {
                        onlinePeersRWLock.writeLock().lock();
                        ArrayList<Peer> peers = new ArrayList<>(onlinePeers.values());
                        long currentTime = new Date(System.currentTimeMillis()).getTime();
                        for (Peer peer : peers) {
                            if ((currentTime - peer.getLastSeen()) > 120000) {
                                peerRepository.updatePeerInfo(peer);
                                onlinePeers.remove(peer.getUserID());
                            }
                        }
                        onlinePeersRWLock.writeLock().unlock();
                    }
                    try {
                        Thread.sleep(130000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void userLogin(Peer peer) {
        if (!onlinePeers.containsKey(peer.getUserID())) {
            onlinePeers.put(peer.getUserID(), peer);
        }
    }

    public static ArrayList<Peer> getOnlinePeers(int userID) {
        onlinePeersRWLock.readLock().lock();
        ArrayList<Peer> peers = new ArrayList<>(onlinePeers.values());
        onlinePeersRWLock.readLock().unlock();
        if (onlinePeers.containsKey(userID)) {
            peers.remove(onlinePeers.get(userID));
        }
        return peers;
    }

    public static void heartbeatRecieved(Peer peer) {
        onlinePeersRWLock.writeLock().lock();
        if (onlinePeers.containsKey(peer.getUserID())) {
            onlinePeers.replace(peer.getUserID(), peer);
        } else {
            onlinePeers.putIfAbsent(peer.getUserID(), peer);
        }
        onlinePeersRWLock.writeLock().unlock();
    }

    public static void userLogout(Peer peer) {
        onlinePeersRWLock.writeLock().lock();
        if (onlinePeers.containsKey(peer.getUserID())) {
            onlinePeers.remove(peer.getUserID());
        }
        onlinePeersRWLock.writeLock().unlock();
        peerRepository.updatePeerInfo(peer);
    }
}