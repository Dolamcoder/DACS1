package Model;

import java.util.Random;
import java.util.Set;

public class TaoID {
    private final Random random = new Random();

    public TaoID() {}

    public String randomIDKH(Set<String> usedIDs) {
        String id;
        do {
            int number = random.nextInt(10000); // 0 - 9999
            id = String.format("KH%04d", number);
        } while (usedIDs.contains(id));
        return id;
    }

    public String randomIDRoom(Set<String> usedIDs) {
        String id;
        do {
            int number = random.nextInt(10000);
            id = String.format("P%04d", number);
        } while (usedIDs.contains(id));
        return id;
    }

    public String randomIDRoomBooking(Set<String> usedIDs) {
        String id;
        do {
            int number = random.nextInt(10000);
            id = String.format("RB%04d", number);
        } while (usedIDs.contains(id));
        return id;
    }
    public String randomIDServiceBooking(Set<String> idServiceBooking) {
        String id;
        do {
            int number = random.nextInt(10000);
            id = String.format("SB%04d", number);
        } while (idServiceBooking.contains(id));
        return id;
    }
    public String randomIDinvoice(Set<String> idinvoice) {
        String id;
        do {
            int number = random.nextInt(10000);
            id = String.format("IV%04d", number);
        } while (idinvoice.contains(id));
        return id;
    }

}
