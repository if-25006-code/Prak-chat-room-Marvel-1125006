
import java.util.LinkedHashMap;
import java.util.Map;

public class User {
    String name;
    Map<String, ChatRoom> chatRooms;

    public User(String name) {
        this.name = name;
        chatRooms = new LinkedHashMap<>();
    }

    public String getName() {
        return name;
    }

    public void joinRoom(ChatRoom room) {
        room.registerUser(this);
    }

    public void addRoom(ChatRoom room) {
        chatRooms.put(room.getRoomName(), room);
    }

    public void removeRoom(ChatRoom room) {
        chatRooms.remove(room.getRoomName());
    }

    public void send(String message) {
        for (ChatRoom room : chatRooms.values()) {
            room.sendBroadcast(this, message);
        }
    }

    public void sendToRoom(String roomName, String message) {
        ChatRoom room = chatRooms.get(roomName);

        if (room != null) {
            room.sendBroadcast(this, message);
        } else {
            System.out.println("Tidak tergabung dalam room " + roomName);
        }
    }

    public void sendPrivate(String roomName, String recipientName, String message) {
        ChatRoom room = chatRooms.get(roomName);

        if (room != null) {
            room.sendPrivateMessage(this, recipientName, message);
        } else {
            System.out.println("Tidak tergabung dalam room " + roomName);
        }
    }

    public void receiveMessage(User sender, String message) {
        System.out.println("[" + name + "] Menerima pesan dari "
                + sender.name + ": " + message);
    }

    public void receivePrivateMessage(User sender, String message) {
        System.out.println("[" + name + "] Menerima pesan privat dari "
                + sender.name + ": " + message);
    }
}

interface ChatRoom {
    String getRoomName();

    void registerUser(User user);

    void sendBroadcast(User sender, String message);

    void sendPrivateMessage(User sender, String recipientName, String message);
}
