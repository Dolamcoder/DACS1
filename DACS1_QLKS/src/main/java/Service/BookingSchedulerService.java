package Service;

import Dao.Employee.RoomDao;
import javafx.application.Platform;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookingSchedulerService {
    private static final Logger logger = Logger.getLogger(BookingSchedulerService.class.getName());
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final RoomDao roomDao = new RoomDao();

    /**
     * Lên lịch cập nhật trạng thái phòng sau một khoảng thời gian cụ thể
     * @param roomId ID của phòng cần cập nhật
     * @param newStatus Trạng thái mới (1 = Trống)
     * @param minutes Số phút sau khi sẽ cập nhật
     */
    public static void scheduleRoomStatusUpdate(String roomId, int newStatus, long minutes) {
        logger.info("Đặt lịch cập nhật trạng thái phòng " + roomId + " sau " + minutes + " phút");

        scheduler.schedule(() -> {
            try {
                logger.info("Đang cập nhật trạng thái phòng " + roomId + " thành " + newStatus);
                boolean success = roomDao.updateStatus(roomId, newStatus);
                if (success) {
                    logger.info("Cập nhật trạng thái phòng " + roomId + " thành công");
                } else {
                    logger.warning("Không thể cập nhật trạng thái phòng " + roomId);
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Lỗi khi cập nhật trạng thái phòng " + roomId, e);
            }
        }, minutes, TimeUnit.MINUTES);
    }

    /**
     * Đóng scheduler service khi ứng dụng kết thúc
     */
    public static void shutdown() {
        logger.info("Đang đóng BookingSchedulerService...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}