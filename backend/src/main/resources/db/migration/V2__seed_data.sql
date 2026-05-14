-- V2__seed_data.sql
-- 种子数据

-- 楼栋
INSERT INTO building (id, name, sort_order) VALUES (1, '主楼', 1);

-- 楼层
INSERT INTO floor (id, building_id, name, floor_no, sort_order) VALUES
(1, 1, '1楼', 1, 1),
(2, 1, '2楼', 2, 2),
(3, 1, '3楼', 3, 3);

-- 房型
INSERT INTO room_type (id, name, code, icon, max_guests) VALUES
(1, '豪华大床房', 'DELUXE_KING', 'king_bed', 2),
(2, '标准双床房', 'STANDARD_TWIN', 'bed', 2),
(3, '商务套房', 'BUSINESS_SUITE', 'meeting_room', 3),
(4, '家庭房', 'FAMILY_ROOM', 'family_restroom', 4);

-- 房间 (1楼: 101-104, 2楼: 201-204, 3楼: 301-304)
INSERT INTO room (id, room_number, floor_id, room_type_id, status, sort_order) VALUES
(1, '101', 1, 1, 0, 1),
(2, '102', 1, 2, 3, 2),
(3, '103', 1, 1, 2, 3),
(4, '104', 1, 3, 0, 4),
(5, '201', 2, 2, 3, 5),
(6, '202', 2, 1, 4, 6),
(7, '203', 2, 4, 0, 7),
(8, '204', 2, 2, 1, 8),
(9, '301', 3, 3, 3, 9),
(10, '302', 3, 1, 0, 10),
(11, '303', 3, 4, 5, 11),
(12, '304', 3, 2, 0, 12);

-- 房间分配记录 (在住房间)
INSERT INTO room_assignment (room_id, order_id, guest_name, guest_phone, check_in_time, check_out_time, status) VALUES
(2, 1001, '张三', '138****1234', '2026-05-13 14:00:00', '2026-05-15 12:00:00', 0),
(5, 1002, '李四', '139****5678', '2026-05-12 15:00:00', '2026-05-14 12:00:00', 0),
(9, 1003, '王五', '137****9012', '2026-05-13 16:00:00', '2026-05-16 12:00:00', 0);
