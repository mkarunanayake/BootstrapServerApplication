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
                    System.out.println("onlinehandler running");
                    if (!onlinePeers.isEmpty()) {
                        onlinePeersRWLock.writeLock().lock();
                        ArrayList<Peer> peers = new ArrayList<>(onlinePeers.values());
                        long currentTime = new Date(System.currentTimeMillis()).getTime();
                        for (Peer peer : peers) {
                            if ((currentTime - peer.getLastSeen()) > 120000) {
                                peerRepository.updatePeerInfo(peer);
                                onlinePeers.remove(peer.getUserID());
                                System.out.println("Heartbeat failure " + peer.getUserID());
                            }
                        }
                        onlinePeersRWLock.writeLock().unlock();
                    }
                    try {
                        Thread.sleep(150000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static ArrayList<Peer> getOnlinePeers(int userID) {
        onlinePeersRWLock.readLock().lock();
        ArrayList<Peer> peers = new ArrayList<>(onlinePeers.values());
        onlinePeersRWLock.readLock().unlock();
        if (onlinePeers.containsKey(userID)) {
            peers.remove(onlinePeers.get(userID));
        }
        int loggedInUsers = onlinePeers.size();
        if (loggedInUsers > 30) {
            peers = (ArrayList<Peer>) peers.subList(0, (loggedInUsers / 6) - 1);
        } else if (loggedInUsers > 10) {
            peers = (ArrayList<Peer>) peers.subList(0, (loggedInUsers / 3) - 1);
        } else if (loggedInUsers > 5) {
            peers = (ArrayList<Peer>) peers.subList(0, 2);
        }
        System.out.println("Total " + onlinePeers.size());
        System.out.println("Sent " + peers.size());
        return peers;
    }

    public static void heartbeatRecieved(Peer peer) {
        System.out.println("Heartbeat received " + peer.getUserID());
        onlinePeersRWLock.writeLock().lock();
        if (onlinePeers.containsKey(peer.getUserID())) {
            onlinePeers.replace(peer.getUserID(), peer);
        } else {
            onlinePeers.putIfAbsent(peer.getUserID(), peer);
        }
        onlinePeersRWLock.writeLock().unlock();
    }

    public static void userLogout(Peer peer) {
        System.out.println("User logout " + peer.getUserID());
        onlinePeersRWLock.writeLock().lock();
        if (onlinePeers.containsKey(peer.getUserID())) {
            onlinePeers.remove(peer.getUserID());
            System.out.println("Peer removed");
        }
        onlinePeersRWLock.writeLock().unlock();
        peerRepository.updatePeerInfo(peer);
        System.out.println("Repo updated with logout");
    }
}