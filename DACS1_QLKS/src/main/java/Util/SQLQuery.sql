-- Bảng loại phòng
CREATE TABLE room_type (
    loaiPhong CHAR(2) PRIMARY KEY,
    tenLoaiPhong VARCHAR(50) NOT NULL,
    soGiuong INT NOT NULL,
    sucChua INT NOT NULL,
    kichThuoc DOUBLE,
    moTa TEXT
);

-- Bảng phòng
CREATE TABLE room (
    roomID VARCHAR(10) PRIMARY KEY,
    number INT NOT NULL,
    loaiPhong CHAR(2),
    price DOUBLE NOT NULL,
    status INT NOT NULL CHECK (status IN (1, 2, 3, 4)),
    FOREIGN KEY (loaiPhong) REFERENCES room_type(loaiPhong)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Bảng khách hàng
CREATE TABLE customers (
    customerID VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100),
    gender VARCHAR(10),
    birthDate DATE,
    idNumber VARCHAR(20),
    address TEXT,
    email VARCHAR(100),
    phone VARCHAR(20),
    status VARCHAR(40)
);

-- Bảng cấp độ thẻ thành viên
CREATE TABLE card_levels (
    stt INT PRIMARY KEY AUTO_INCREMENT,
    customerID VARCHAR(10),
    levelName VARCHAR(50),
    totalAmount DOUBLE,
    description TEXT,
    FOREIGN KEY (customerID) REFERENCES customers(customerID)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Bảng lịch sử lưu trú
CREATE TABLE stay_history (
    stt INT PRIMARY KEY AUTO_INCREMENT,
    customerID VARCHAR(10),
    roomID VARCHAR(10),
    checkIn DATE,
    checkOut DATE,
    note TEXT,
    FOREIGN KEY (customerID) REFERENCES customers(customerID)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    FOREIGN KEY (roomID) REFERENCES room(roomID)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Bảng đặt phòng
CREATE TABLE booking (
    bookingID VARCHAR(10) PRIMARY KEY,
    roomID VARCHAR(10),
    customerID VARCHAR(10),
    checkInDate DATE,
    checkOutDate DATE,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) CHECK (status IN ('Đã xác nhận', 'Đã hủy', 'Đang ở', 'Hết hạn thanh toán', 'Đã thanh toán')),
    FOREIGN KEY (roomID) REFERENCES room(roomID)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    FOREIGN KEY (customerID) REFERENCES customers(customerID)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Bảng nhân viên
CREATE TABLE employee (
    employeeID VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(50),
    gender VARCHAR(10) CHECK (gender IN ('Male', 'Female')),
    birth DATE,
    hireDate DATE,
    position VARCHAR(200)
);

-- Bảng tài khoản
CREATE TABLE account (
    stt INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(255),
    name VARCHAR(100),
    email VARCHAR(50),
    role INT CHECK (role IN (1, 2)),
    EmployeeID VARCHAR(10),
    UNIQUE (EmployeeID),
    FOREIGN KEY (EmployeeID) REFERENCES employee(employeeID)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Bảng lương
CREATE TABLE salary (
    salaryID INT PRIMARY KEY AUTO_INCREMENT,
    employeeID VARCHAR(10),
    amount DOUBLE,
    transportAllowance DOUBLE,
    FOREIGN KEY (employeeID) REFERENCES employee(employeeID)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Bảng đánh giá nhân viên
CREATE TABLE employee_review (
    reviewID INT PRIMARY KEY AUTO_INCREMENT,
    employeeID VARCHAR(10),
    reviewDate DATE,
    rating_score int,
    comments TEXT,
    bonusAmount DOUBLE DEFAULT 0,
    FOREIGN KEY (employeeID) REFERENCES employee(employeeID)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);
-- Bảng dịch vụ
CREATE TABLE service (
    serviceID INT PRIMARY KEY,
    name VARCHAR(100),
    price DOUBLE,
    description TEXT
);

-- Bảng đặt dịch vụ
CREATE TABLE servicebooking (
    serviceBookingID VARCHAR(10) PRIMARY KEY,
    roomID VARCHAR(10),
    quantity INT,
    totalAmount DECIMAL(10, 2),
    FOREIGN KEY (roomID) REFERENCES room(roomID)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Bảng chi tiết đặt dịch vụ
CREATE TABLE ServiceBookingDetail (
    stt INT AUTO_INCREMENT PRIMARY KEY,
    ServiceBookingID VARCHAR(10),
    ServiceID INT,
    FOREIGN KEY (ServiceBookingID) REFERENCES servicebooking(ServiceBookingID)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    FOREIGN KEY (ServiceID) REFERENCES service(ServiceID)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Bảng hóa đơn
CREATE TABLE invoice (
    invoiceID VARCHAR(10) PRIMARY KEY,
    bookingID VARCHAR(10),
    ServiceBookingID VARCHAR(10),
    totalAmount DOUBLE,
    tax DOUBLE DEFAULT 0,
    discount DOUBLE DEFAULT 0,
    issueDate DATE,
    status VARCHAR(50),
    FOREIGN KEY (bookingID) REFERENCES booking(bookingID)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    FOREIGN KEY (ServiceBookingID) REFERENCES servicebooking(ServiceBookingID)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Bảng log hệ thống
CREATE TABLE audit_log (
    logID INT PRIMARY KEY AUTO_INCREMENT,
    tableName VARCHAR(50),
    recordID VARCHAR(10),
    action VARCHAR(50),
    actionBy VARCHAR(50),
    actionAt DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- bản ghi
INSERT INTO room_type (loaiPhong, tenLoaiPhong, soGiuong, sucChua, kichThuoc, moTa) VALUES
('DB', 'Double Bed', 1, 2, 25.0, 'Phòng có một giường đôi, thích hợp cho 2 người lớn.'),
('TW', 'Twin Bed', 2, 2, 26.0, 'Phòng có hai giường đơn, phù hợp cho bạn bè hoặc đồng nghiệp.'),
('VP', 'VIP Room', 1, 2, 40.0, 'Phòng cao cấp với trang thiết bị sang trọng, phòng tắm lớn.'),
('SU', 'Suite', 1, 3, 50.0, 'Phòng Suite rộng rãi với phòng khách riêng biệt.'),
('DL', 'Deluxe', 1, 2, 30.0, 'Phòng cao cấp với nội thất hiện đại và ban công.'),
('EX', 'Executive', 1, 2, 35.0, 'Phòng Executive dành cho doanh nhân với khu làm việc riêng.'),
('ST', 'Standard', 1, 2, 22.0, 'Phòng tiêu chuẩn đầy đủ tiện nghi, giá hợp lý.'),
('PR', 'Premium', 1, 2, 32.0, 'Phòng Premium với tiện nghi cao cấp, view đẹp.'),
('FM', 'Family Room', 2, 4, 45.0, 'Phòng gia đình rộng rãi với hai giường lớn.'),
('JR', 'Junior Suite', 1, 2, 38.0, 'Junior Suite với khu vực tiếp khách nhỏ và giường lớn.'),
('SG', 'Single Room', 1, 1, 20.0, 'Phòng đơn nhỏ gọn dành cho khách một mình.'),
('DS', 'Double Suite', 2, 4, 55.0, 'Suite 2 phòng ngủ cho gia đình hoặc nhóm bạn.'),
('RK', 'Royal King', 1, 2, 60.0, 'Phòng Royal King cực kỳ sang trọng với phòng tắm VIP.'),
('BS', 'Business Room', 1, 2, 28.0, 'Phòng Business thiết kế cho khách đi công tác.'),
('OC', 'Ocean View', 1, 2, 34.0, 'Phòng hướng biển với ban công lớn và view đẹp.'),
('GD', 'Garden View', 1, 2, 33.0, 'Phòng hướng vườn xanh mát, không gian yên tĩnh.'); 
INSERT INTO room (roomID, number, loaiPhong, price, status) VALUES
('P001', 101, 'DB', 600000, 1),
('P002', 102, 'TW', 650000, 1),
('P003', 103, 'ST', 500000, 1),
('P004', 104, 'PR', 850000, 1),
('P005', 105, 'FM', 1200000, 1),
('P006', 106, 'VP', 2000000, 1),
('P007', 107, 'SU', 1800000, 1),
('P008', 108, 'DL', 700000, 1),
('P009', 109, 'EX', 900000, 1),
('P010', 110, 'JR', 950000, 1),
('P011', 111, 'SG', 450000, 1),
('P012', 112, 'DS', 1400000, 1),
('P013', 113, 'RK', 2500000, 1),
('P014', 114, 'BS', 800000, 1),
('P015', 115, 'OC', 1000000, 1),
('P016', 116, 'GD', 750000, 1),
('P017', 117, 'DB', 620000, 1),
('P018', 118, 'TW', 670000, 1),
('P019', 119, 'PR', 870000, 1),
('P020', 120, 'FM', 1250000, 1);