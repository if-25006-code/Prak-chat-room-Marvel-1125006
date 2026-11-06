
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {

        // Membuat Chat Room
        ChatRoom general = new ChatRoom("General");
        ChatRoom sports = new ChatRoom("Sports");

        // Membuat User
        User budi = new User("Budi");
        User andi = new User("Andi");
        User cici = new User("Cici");

        // Tahap 3: Bergabung ke Chat Room
        System.out.println("=== JOIN CHAT ROOM ===");

        budi.joinRoom(general);
        andi.joinRoom(general);
        cici.joinRoom(general);

        // Tahap 6: Multiple Chat Rooms
        budi.joinRoom(sports);
        andi.joinRoom(sports);

        // Tahap 5: Menambahkan Badwords
        general.addBadWord("jelek");
        general.addBadWord("bodoh");

        // Tahap 4: Group Chat
        System.out.println("\n=== GROUP CHAT ===");

        budi.sendToRoom("General", "Halo semuanya!");

        // Tahap 4: Private Message
        System.out.println("\n=== PRIVATE MESSAGE ===");

        budi.sendPrivate(
            "General",
            "Cici",
            "Rahasia ya ini"
        );

        // Tahap 5: Filter Badwords
        System.out.println("\n=== BADWORDS FILTER ===");

        andi.sendToRoom(
            "General",
            "Kamu sangat jelek!"
        );

        // Tahap 7: Mute User
        System.out.println("\n=== MUTE USER ===");

        general.muteUser(budi, andi);

        andi.sendToRoom(
            "General",
            "Pesan ini tidak akan terkirim."
        );

        budi.sendToRoom(
            "General",
            "Andi masih bisa membaca pesan."
        );

        // Tahap 7: Chat History
        general.showHistory();

        // Tahap 6: Kick User
        System.out.println("\n=== REMOVE USER ===");

        general.removeUser(budi, cici);

        // Tahap 6: Multiple Chat Rooms
        System.out.println("\n=== SPORTS CHAT ROOM ===");

        andi.sendToRoom(
            "Sports",
            "Ayo kita bahas pertandingan!"
        );

        // Menampilkan History Sports
        sports.showHistory();

        // Tahap 7: Unmute User
        System.out.println("\n=== UNMUTE USER ===");

        general.unmuteUser(budi, andi);

        andi.sendToRoom(
            "General",
            "Sekarang saya bisa mengirim pesan lagi."
        );

        general.showHistory();
    }

    static class User {
        private final String name;
        private final Map<String, ChatRoom> joinedRooms = new HashMap<>();

        public User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void joinRoom(ChatRoom room) {
            if (room == null) {
                return;
            }
            room.addUser(this);
            joinedRooms.put(room.getName(), room);
        }

        public void sendToRoom(String roomName, String message) {
            ChatRoom room = ChatRoom.getRoomByName(roomName);
            if (room == null) {
                System.out.println("Room '" + roomName + "' tidak ditemukan.");
                return;
            }
            room.sendMessage(this, message);
        }

        public void sendPrivate(String roomName, String targetName, String message) {
            ChatRoom room = ChatRoom.getRoomByName(roomName);
            if (room == null) {
                System.out.println("Room '" + roomName + "' tidak ditemukan.");
                return;
            }
            room.sendPrivate(this, targetName, message);
        }

        public void leaveRoom(ChatRoom room) {
            if (room != null) {
                joinedRooms.remove(room.getName());
            }
        }
    }

    static class ChatRoom {
        private static final Map<String, ChatRoom> rooms = new HashMap<>();

        private final String name;
        private final Set<User> members = new LinkedHashSet<>();
        private final Set<User> mutedUsers = new HashSet<>();
        private final Set<String> badWords = new HashSet<>();
        private final List<String> history = new ArrayList<>();

        public ChatRoom(String name) {
            this.name = name;
            rooms.put(name, this);
        }

        public static ChatRoom getRoomByName(String roomName) {
            return rooms.get(roomName);
        }

        public String getName() {
            return name;
        }

        public void addUser(User user) {
            if (user == null) {
                return;
            }
            members.add(user);
            user.joinedRooms.put(name, this);
        }

        public void addBadWord(String badWord) {
            if (badWord == null || badWord.trim().isEmpty()) {
                return;
            }
            badWords.add(badWord.toLowerCase());
        }

        public void muteUser(User source, User target) {
            if (!members.contains(source) || !members.contains(target)) {
                return;
            }
            mutedUsers.add(target);
            history.add(source.getName() + " mute " + target.getName() + " di " + name);
            System.out.println(source.getName() + " mematikan suara " + target.getName() + " di " + name);
        }

        public void unmuteUser(User source, User target) {
            if (!members.contains(source) || !members.contains(target)) {
                return;
            }
            mutedUsers.remove(target);
            history.add(source.getName() + " unmute " + target.getName() + " di " + name);
            System.out.println(source.getName() + " mengaktifkan kembali " + target.getName() + " di " + name);
        }

        public void removeUser(User source, User target) {
            if (!members.contains(source) || !members.contains(target)) {
                return;
            }
            members.remove(target);
            mutedUsers.remove(target);
            target.leaveRoom(this);
            history.add(source.getName() + " mengeluarkan " + target.getName() + " dari " + name);
            System.out.println(source.getName() + " mengeluarkan " + target.getName() + " dari " + name);
        }

        public void showHistory() {
            System.out.println("\n=== HISTORY " + name.toUpperCase() + " ===");
            if (history.isEmpty()) {
                System.out.println("Belum ada pesan.");
                return;
            }
            for (String entry : history) {
                System.out.println(entry);
            }
        }

        public void sendMessage(User sender, String message) {
            if (!members.contains(sender)) {
                System.out.println(sender.getName() + " belum bergabung di room " + name);
                return;
            }

            if (mutedUsers.contains(sender)) {
                System.out.println(sender.getName() + " sedang dimute di room " + name);
                return;
            }

            String text = message == null ? "" : message.trim();
            if (text.isEmpty()) {
                return;
            }

            String filtered = filterBadWords(text);
            String entry = sender.getName() + ": " + filtered;
            history.add(entry);
            System.out.println("[" + name + "] " + entry);
        }

        public void sendPrivate(User sender, String targetName, String message) {
            if (!members.contains(sender)) {
                System.out.println(sender.getName() + " belum bergabung di room " + name);
                return;
            }

            if (mutedUsers.contains(sender)) {
                System.out.println(sender.getName() + " sedang dimute di room " + name);
                return;
            }

            User target = findUser(targetName);
            if (target == null) {
                System.out.println("User '" + targetName + "' tidak ditemukan di room " + name);
                return;
            }

            String filtered = filterBadWords(message == null ? "" : message.trim());
            String entry = sender.getName() + " -> " + target.getName() + " (private): " + filtered;
            history.add(entry);
            System.out.println("[" + name + "] " + entry);
        }

        private User findUser(String targetName) {
            for (User member : members) {
                if (member.getName().equalsIgnoreCase(targetName)) {
                    return member;
                }
            }
            return null;
        }

        private String filterBadWords(String text) {
            String result = text;
            for (String badWord : badWords) {
                result = result.replaceAll("(?i)" + Pattern.quote(badWord), "***");
            }
            return result;
        }
    }
}
