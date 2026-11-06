
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ChatRoom {

    String roomName;
    List<User> members;
    List<String> badWords;
    Set<User> mutedUsers;
    List<String> chatHistory;
    User admin;

    public ChatRoom(String roomName) {
        this.roomName = roomName;
        members = new ArrayList<>();
        badWords = new ArrayList<>();
        mutedUsers = new LinkedHashSet<>();
        chatHistory = new ArrayList<>();
    }

    // Tahap 3: Register User
    public void registerUser(User user) {
        if (!members.contains(user)) {
            members.add(user);
            user.addRoom(this);

            if (admin == null) {
                admin = user;
                System.out.println(user.name + " menjadi admin " + roomName);
            } else {
                System.out.println(user.name + " bergabung ke " + roomName);
            }
        }
    }

    // Tahap 4: Broadcast Message
    public void sendBroadcast(User sender, String message) {

        if (!members.contains(sender)) {
            return;
        }

        if (mutedUsers.contains(sender)) {
            System.out.println(sender.name + " sedang di-mute.");
            return;
        }

        String filtered = filterMessage(message);

        String log = "[ChatRoom - " + roomName
                + "] (Broadcast dari " + sender.name + "): " + filtered;

        System.out.println(log);
        chatHistory.add(log);

        for (User user : members) {
            if (user != sender) {
                user.receiveMessage(sender, filtered);
            }
        }
    }

    // Private Message
    public void sendPrivateMessage(User sender, String recipientName, String message) {

        if (!members.contains(sender)) {
            return;
        }

        if (mutedUsers.contains(sender)) {
            System.out.println(sender.name + " sedang di-mute.");
            return;
        }

        for (User user : members) {
            if (user.name.equalsIgnoreCase(recipientName)) {

                String filtered = filterMessage(message);

                String log = "[ChatRoom - " + roomName
                        + "] (Private " + sender.name
                        + " -> " + user.name + "): " + filtered;

                System.out.println(log);
                chatHistory.add(log);

                user.receivePrivateMessage(sender, filtered);
                return;
            }
        }

        System.out.println("Penerima tidak ditemukan.");
    }

    // Tahap 5: Badwords Filter
    public void addBadWord(String word) {
        badWords.add(word.toLowerCase());
    }

    public String filterMessage(String message) {
        String result = message;

        for (String word : badWords) {
            result = result.replaceAll(
                "(?i)" + java.util.regex.Pattern.quote(word),
                "***"
            );
        }

        return result;
    }

    // Tahap 6: Remove User / Kick
    public void removeUser(User requester, User target) {

        if (requester != admin) {
            System.out.println("Hanya admin yang dapat kick user.");
            return;
        }

        if (target == admin) {
            System.out.println("Admin tidak dapat mengeluarkan dirinya sendiri.");
            return;
        }

        if (members.remove(target)) {
            target.removeRoom(this);
            mutedUsers.remove(target);

            System.out.println(target.name + " dikeluarkan dari " + roomName);
        }
    }

    // Tahap 7: Mute User
    public void muteUser(User requester, User target) {

        if (requester == admin && members.contains(target)) {
            mutedUsers.add(target);
            System.out.println(target.name + " berhasil di-mute.");
        } else {
            System.out.println("Tidak dapat melakukan mute.");
        }
    }

    public void unmuteUser(User requester, User target) {
        if (requester == admin && mutedUsers.remove(target)) {
            System.out.println(target.name + " berhasil di-unmute.");
        }
    }

    // Tahap 7: Chat History
    public void showHistory() {
        System.out.println("\n=== CHAT HISTORY " + roomName + " ===");

        if (chatHistory.isEmpty()) {
            System.out.println("Belum ada pesan.");
        }

        for (String message : chatHistory) {
            System.out.println(message);
        }
    }
}

class User {
    String name;
    List<ChatRoom> rooms;

    public User(String name) {
        this.name = name;
        this.rooms = new ArrayList<>();
    }

    public void addRoom(ChatRoom room) {
        if (!rooms.contains(room)) {
            rooms.add(room);
        }
    }

    public void removeRoom(ChatRoom room) {
        rooms.remove(room);
    }

    public void receiveMessage(User sender, String message) {
        System.out.println(name + " menerima pesan dari " + sender.name + ": " + message);
    }

    public void receivePrivateMessage(User sender, String message) {
        System.out.println(name + " menerima pesan privat dari " + sender.name + ": " + message);
    }

    public void sendMessage(ChatRoom room, String message) {
        room.sendBroadcast(this, message);
    }

    public void sendPrivateMessage(ChatRoom room, String recipientName, String message) {
        room.sendPrivateMessage(this, recipientName, message);
    }
}
