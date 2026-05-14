-- 用户表（客户+管理员）
CREATE TABLE `user` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    phone VARCHAR(20),
    role TINYINT DEFAULT 0 COMMENT '0=客户 1=前台 2=管理员',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 订单表
CREATE TABLE booking_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单编号',
    user_id BIGINT NOT NULL,
    room_type_id BIGINT NOT NULL,
    room_id BIGINT COMMENT '分配的房间（入住时填入）',
    guest_name VARCHAR(100) NOT NULL,
    guest_phone VARCHAR(20) NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    nights INT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status TINYINT DEFAULT 0 COMMENT '0=待支付 1=已支付 2=已入住 3=已退房 4=已取消',
    pay_method VARCHAR(20) COMMENT 'wechat/alipay',
    paid_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_order_no (order_no),
    INDEX idx_check_in (check_in_date, check_out_date)
);

-- 房型增加价格、描述、图片字段
ALTER TABLE room_type ADD COLUMN price DECIMAL(10,2) DEFAULT 0 COMMENT '每晚价格';
ALTER TABLE room_type ADD COLUMN description VARCHAR(500) DEFAULT '' COMMENT '房型描述';
ALTER TABLE room_type ADD COLUMN image_url VARCHAR(255) DEFAULT '' COMMENT '房型图片';

-- 更新房型价格种子数据
UPDATE room_type SET price=499, description='豪华大床，尊享品质空间', image_url='/images/deluxe-king.jpg' WHERE code='DELUXE_KING';
UPDATE room_type SET price=329, description='温馨双床，适合朋友同行', image_url='/images/standard-twin.jpg' WHERE code='STANDARD_TWIN';
UPDATE room_type SET price=599, description='商务套房，独立办公区域', image_url='/images/business-suite.jpg' WHERE code='BUSINESS_SUITE';
UPDATE room_type SET price=799, description='温馨家庭房，宽敞舒适', image_url='/images/family-room.jpg' WHERE code='FAMILY_ROOM';

-- 管理员种子数据 (密码: admin123)
INSERT INTO `user` (username, password, nickname, phone, role) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKt6Z5EH', '系统管理员', '13800000000', 2);
