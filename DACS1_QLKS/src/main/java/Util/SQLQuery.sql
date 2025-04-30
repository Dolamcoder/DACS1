--table account--

INSERT INTO account (userName, password, email, name, role)
VALUES
('QL001', 'dolamhotel123', 'dolam261106@gmail.com', 'Đỗ Lam', 2), -- Khách hàng
('NV002', 'tranthibhotel123', 'btthotel@gmail.com', 'Trần Thị B', 1);     -- Nhân viên

--table room
 CREATE TABLE room (
     roomID VARCHAR(10) PRIMARY KEY,                  -- Mã phòng
     number INT NOT NULL,                         -- Số phòng
     loaiPhong CHAR(2),                           -- Ký hiệu loại phòng (khóa ngoại)
     price DOUBLE NOT NULL,                       -- Giá tiền
     status INT NOT NULL CHECK (status IN (1, 2, 3, 4)),  -- Trạng thái
     FOREIGN KEY (loaiPhong) REFERENCES room_type(loaiPhong)
 );
 --booking
 CREATE TABLE Booking (
     bookingId VARCHAR(10) PRIMARY KEY,
     customerId VARCHAR(10) NOT NULL,
     roomId VARCHAR(10) NOT NULL,
     checkInDate DATE NOT NULL,
     checkOutDate DATE NOT NULL,
     status ENUM('Đã xác nhận', 'Đã hoàn thành') NOT NULL
 ) ;

 --table type room
 CREATE TABLE room_type (
     loaiPhong CHAR(2) PRIMARY KEY,               -- Ký hiệu loại phòng (VD: 'DB', 'TW', 'VIP')
     tenLoaiPhong VARCHAR(50) NOT NULL,           -- Tên loại phòng
     soGiuong INT NOT NULL,                       -- Số lượng giường
     sucChua INT NOT NULL,                        -- Sức chứa tối đa
     kichThuoc DOUBLE,                            -- Diện tích phòng (m²)
     moTa TEXT                                     -- Mô tả tiện nghi
 );
--table Customer
CREATE TABLE customers (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    cccd VARCHAR(20) UNIQUE,
    dia_chi VARCHAR(255),
    phone VARCHAR(20),
    gender VARCHAR(10),
   	birth varchar(10)
);
--table Service
CREATE TABLE service (
    serviceId VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL,
    description TEXT
)
--table Đạt dịch vụ
CREATE TABLE ServiceBooking (
    serviceBookingId VARCHAR(10) PRIMARY KEY,
    customers varchar(10) not NULL,
    serviceId VARCHAR(10) NOT NULL,

    FOREIGN KEY (customerID) REFERENCES customers(customerID),
    FOREIGN KEY (serviceId) REFERENCES Service(serviceId)
)

--table Employee
CREATE TABLE Employee (
    employeeId VARCHAR(10) PRIMARY KEY,
    email VARCHAR(100) UNIQUE,
    name VARCHAR(100) NOT NULL,
    diaChi VARCHAR(255),
    phone VARCHAR(50),
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    birth varchar(10) NOT NULL,
    hireDate DATE NOT NULL
)
--table Salary
CREATE TABLE Salary (
    stt INT AUTO_INCREMENT PRIMARY KEY,  -- Khóa chính của bảng, tự động tăng
    employeeId VARCHAR(10),                   -- ID nhân viên
    tichCuc INT,                              -- Thưởng tích cực
    tieuCuc INT,                              -- Phạt tiêu cực
    salary DOUBLE,                            -- Lương cơ bản
    FOREIGN KEY (employeeId) REFERENCES employee(employeeId)  -- Khóa ngoại tham chiếu đến bảng Employee
)

--table position
CREATE TABLE Position (
    stt INT AUTO_INCREMENT PRIMARY KEY,
    employeeId VARCHAR(10) ,
    position INT NOT NULL,
    name VARCHAR(100) NOT NULL,
 	FOREIGN KEY (employeeId) REFERENCES employee(employeeId)
)
--SQL tìm kiếm phòng để đặt
SELECT r.roomID, r.number, r.price, r.loaiPhong
FROM room r
WHERE r.loaiPhong = ?
AND (
    -- Trường hợp 1: Phòng hiện tại trống (status = 1)
    r.status = 1
    OR
    -- Trường hợp 2: Phòng không trống (status != 1), kiểm tra lịch đặt phòng
    r.roomID IN (
        SELECT b.roomId
        FROM Booking b
        WHERE b.roomId = r.roomID
        AND (
            -- Trường hợp 2a: Ngày nhận phòng của bạn lớn hơn ngày trả phòng trong Booking
            ? > b.checkOutDate
            OR
            -- Trường hợp 2b: Ngày nhận phòng ≤ ngày trả phòng, nhưng trạng thái là 'Đã hoàn thành'
            (? <= b.checkOutDate AND b.status = 'Đã hoàn thành')
        )
    )
);


