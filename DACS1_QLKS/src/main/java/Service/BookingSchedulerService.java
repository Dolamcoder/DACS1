package Service;

import Dao.Employee.RoomDao;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BookingSchedulerService {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Schedules a room status update to available (1) after one hour
     * @param roomId The room ID to update
     */
    public static void scheduleRoomStatusReset(String roomId) {
        Runnable task = () -> {
            try {
                RoomDao roomDao = new RoomDao();
                boolean updated = roomDao.updateStatus(roomId, 1);
                if (updated) {
                    System.out.println("Room " + roomId + " status reset to available automatically");
                } else {
                    System.out.println("Failed to reset room " + roomId + " status");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        // Schedule the task to run after 1 hour
        scheduler.schedule(task, 1, TimeUnit.HOURS);
    }

    /**
     * Shuts down the scheduler service
     */
    public static void shutdown() {
        scheduler.shutdown();
    }
}