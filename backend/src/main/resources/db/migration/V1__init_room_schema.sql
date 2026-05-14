-- V1__init_room_schema.sql
-- 房态看板相关表结构

-- 楼栋表
CREATE TABLE IF NOT EXISTS building (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '楼栋名称',
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '楼栋';

-- 楼层表
CREATE TABLE IF NOT EXISTS floor (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    building_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL COMMENT '楼层名称',
    floor_no INT NOT NULL COMMENT '楼层序号',
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_building (building_id)
) COMMENT '楼层';

-- 房型表
CREATE TABLE IF NOT EXISTS room_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '房型名称',
    code VARCHAR(30) NOT NULL UNIQUE COMMENT '房型编码',
    icon VARCHAR(50) DEFAULT 'bed' COMMENT 'Material icon name',
    max_guests INT DEFAULT 2,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '房型';

-- 房间表
CREATE TABLE IF NOT EXISTS room (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(20) NOT NULL UNIQUE COMMENT '房间号',
    floor_id BIGINT NOT NULL,
    room_type_id BIGINT NOT NULL,
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0=空净 1=预留 2=预订 3=在住 4=脏房 5=停售',
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_floor (floor_id),
    INDEX idx_status (status)
) COMMENT '房间';

-- 房态变更日志
CREATE TABLE IF NOT EXISTS room_status_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    previous_status TINYINT NOT NULL,
    new_status TINYINT NOT NULL,
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    reason VARCHAR(255) COMMENT '变更原因',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_room_time (room_id, created_at)
) COMMENT '房态变更日志';

-- 房间分配记录
CREATE TABLE IF NOT EXISTS room_assignment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    order_id BIGINT COMMENT '关联订单ID',
    guest_name VARCHAR(100) COMMENT '入住人姓名',
    guest_phone VARCHAR(20) COMMENT '入住人手机号',
    check_in_time DATETIME,
    check_out_time DATETIME,
    status TINYINT DEFAULT 0 COMMENT '0=有效 1=已退房',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_room (room_id),
    INDEX idx_order (order_id)
) COMMENT '房间分配记录';
