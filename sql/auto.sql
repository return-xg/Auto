/*
 Navicat Premium Dump SQL

 Source Server         : demo
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : auto

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 16/01/2026 16:33:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for appointment
-- ----------------------------
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `store_id` bigint NOT NULL COMMENT '门店ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `appointment_time` datetime NOT NULL COMMENT '预约安装时间',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态（0待确认，1已确认，2已完成，3已取消）',
  `note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_store_id`(`store_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_appointment_time`(`appointment_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '预约安装表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appointment
-- ----------------------------
INSERT INTO `appointment` VALUES (1, 11, 1, 1, '2026-01-14 08:00:00', 0, NULL, '2026-01-13 23:38:33', '2026-01-13 23:38:33');
INSERT INTO `appointment` VALUES (2, 14, 1, 1, '2026-01-15 10:00:00', 0, NULL, '2026-01-14 15:13:27', '2026-01-14 15:13:27');
INSERT INTO `appointment` VALUES (3, 20, 3, 106, '2026-01-23 14:00:00', 0, NULL, '2026-01-16 15:45:02', '2026-01-16 15:45:02');

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '数量',
  `selected` tinyint(1) NULL DEFAULT 1 COMMENT '是否选中（0否，1是）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `spec` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '规格参数',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '购物车表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES (27, 1, 8, 1, 1, '2026-01-14 17:33:16', '2026-01-14 17:33:16', '{\"颜色\":\"蓝黑\"}');
INSERT INTO `cart` VALUES (28, 1, 1, 1, 1, '2026-01-14 17:33:45', '2026-01-14 17:33:45', '{\"尺寸\":\"21寸\",\"厚度\":\"200\",\"颜色\":\"黑\"}');

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `is_show` tinyint(1) NULL DEFAULT 1 COMMENT '是否显示（0否，1是）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (2, '轮毂', 1, '2026-01-08 16:30:15', '2026-01-08 16:37:55');
INSERT INTO `category` VALUES (3, '车胎', 1, '2026-01-08 16:37:51', '2026-01-08 16:37:51');
INSERT INTO `category` VALUES (4, '尾翼', 1, '2026-01-08 16:38:23', '2026-01-08 16:38:22');
INSERT INTO `category` VALUES (8, '测试', 1, '2026-01-08 16:46:44', '2026-01-16 16:30:57');

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_product`(`user_id` ASC, `product_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of favorite
-- ----------------------------
INSERT INTO `favorite` VALUES (7, 1, 8, '2026-01-14 17:28:12');
INSERT INTO `favorite` VALUES (8, 1, 1, '2026-01-14 17:33:35');
INSERT INTO `favorite` VALUES (9, 106, 1, '2026-01-15 20:51:49');
INSERT INTO `favorite` VALUES (11, 106, 8, '2026-01-16 14:02:11');

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_price` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `pay_price` decimal(10, 2) NOT NULL COMMENT '实际支付金额',
  `freight_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '运费',
  `pay_type` tinyint(1) NULL DEFAULT NULL COMMENT '支付方式（1支付宝，2微信…）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态（0待支付，1待发货，2已发货，3已完成，4已取消）',
  `delivery_type` tinyint(1) NULL DEFAULT 1 COMMENT '配送方式（1送货上门，2门店安装）',
  `address_id` bigint NULL DEFAULT NULL COMMENT '收货地址ID',
  `store_id` bigint NULL DEFAULT NULL COMMENT '门店ID（如果选择门店安装）',
  `appointment_id` bigint NULL DEFAULT NULL COMMENT '预约安装ID',
  `transaction_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付交易号',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `delivery_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime NULL DEFAULT NULL COMMENT '确认收货时间',
  `close_time` datetime NULL DEFAULT NULL COMMENT '订单关闭时间（取消/完成）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (11, 'ORD202601132338326516717', 1, 56000.00, 56000.00, 0.00, 1, 3, 2, NULL, 1, 1, 'TXN202601132338330664045', '2026-01-13 23:38:33', '2026-01-13 23:38:38', '2026-01-14 15:59:19', NULL, '2026-01-13 23:38:33', '2026-01-14 15:59:19');
INSERT INTO `order` VALUES (12, 'ORD202601141415576019328', 1, 3000.00, 3010.00, 10.00, 2, 3, 1, 3, NULL, NULL, 'TXN202601141415578368027', '2026-01-14 14:15:58', '2026-01-14 14:16:03', '2026-01-14 20:18:13', NULL, '2026-01-14 14:15:58', '2026-01-14 20:18:13');
INSERT INTO `order` VALUES (14, 'ORD202601141513272115389', 1, 12000.00, 12000.00, 0.00, 1, 3, 2, NULL, 1, 2, 'TXN202601141513274400796', '2026-01-14 15:13:27', '2026-01-14 15:13:32', '2026-01-14 20:23:37', NULL, '2026-01-14 15:13:27', '2026-01-14 20:23:37');
INSERT INTO `order` VALUES (15, 'ORD202601141513413017075', 1, 3000.00, 3010.00, 10.00, 1, 3, 1, 1, NULL, NULL, 'TXN202601141513415399743', '2026-01-14 15:13:42', '2026-01-14 15:13:47', '2026-01-14 15:58:35', NULL, '2026-01-14 15:13:41', '2026-01-14 15:58:35');
INSERT INTO `order` VALUES (16, 'ORD202601152128436876496', 106, 9000.00, 9010.00, 10.00, 1, 2, 1, 4, NULL, NULL, 'TXN202601152128440709355', '2026-01-15 21:28:44', '2026-01-15 21:28:49', NULL, NULL, '2026-01-15 21:28:44', '2026-01-15 21:28:49');
INSERT INTO `order` VALUES (17, 'ORD202601161520286001908', 106, 50000.00, 50010.00, 10.00, 1, 2, 1, 4, NULL, NULL, 'TXN202601161520289464074', '2026-01-16 15:20:29', '2026-01-16 15:20:34', NULL, NULL, '2026-01-16 15:20:29', '2026-01-16 15:20:34');
INSERT INTO `order` VALUES (18, 'ORD202601161535227506997', 106, 300.00, 310.00, 10.00, 1, 2, 1, 4, NULL, NULL, 'TXN202601161535230068341', '2026-01-16 15:35:23', '2026-01-16 15:35:28', NULL, NULL, '2026-01-16 15:35:23', '2026-01-16 15:35:28');
INSERT INTO `order` VALUES (19, 'ORD202601161544130285545', 106, 300.00, 310.00, 10.00, 1, 2, 1, 4, NULL, NULL, 'TXN202601161544132899177', '2026-01-16 15:44:13', '2026-01-16 15:44:18', NULL, NULL, '2026-01-16 15:44:13', '2026-01-16 15:44:18');
INSERT INTO `order` VALUES (20, 'ORD202601161545015138969', 106, 300.00, 300.00, 0.00, 1, 2, 2, NULL, 3, 3, 'TXN202601161545017651812', '2026-01-16 15:45:02', '2026-01-16 15:45:07', NULL, NULL, '2026-01-16 15:45:02', '2026-01-16 15:45:07');
INSERT INTO `order` VALUES (21, 'ORD202601161631417065357', 106, 100.00, 110.00, 10.00, 2, 2, 1, 4, NULL, NULL, 'TXN202601161631420944593', '2026-01-16 16:31:42', '2026-01-16 16:31:47', NULL, NULL, '2026-01-16 16:31:42', '2026-01-16 16:31:47');

-- ----------------------------
-- Table structure for order_product
-- ----------------------------
DROP TABLE IF EXISTS `order_product`;
CREATE TABLE `order_product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称（快照）',
  `product_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '商品图片（快照）',
  `product_spec` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '商品规格（快照，JSON格式）',
  `price` decimal(10, 2) NOT NULL COMMENT '商品单价（快照）',
  `quantity` int NOT NULL COMMENT '数量',
  `total_price` decimal(10, 2) NOT NULL COMMENT '商品总价',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_product
-- ----------------------------
INSERT INTO `order_product` VALUES (14, 11, 1, '大轮毂', '/profile/upload/2026/01/12/967bebee1418499293180df3b67bd297.jpeg~tplv-a9rns2rl98-downsize_watermark_1_5_b_20260112191433A011.png', '{\"尺寸\":\"21寸\",\"厚度\":\"200\",\"颜色\":\"黑\"}', 3000.00, 2, 6000.00);
INSERT INTO `order_product` VALUES (15, 11, 8, '速系列尾翼', '/profile/upload/2026/01/12/e5542aa76302431b9d8bb8774f6b0ef5.jpeg~tplv-a9rns2rl98-downsize_watermark_1_5_b_20260112192334A001.png', '{\"颜色\":\"银\"}', 50000.00, 1, 50000.00);
INSERT INTO `order_product` VALUES (16, 12, 1, '大轮毂', '/profile/upload/2026/01/12/967bebee1418499293180df3b67bd297.jpeg~tplv-a9rns2rl98-downsize_watermark_1_5_b_20260112191433A011.png', '{\"尺寸\":\"20寸\",\"厚度\":\"100\",\"颜色\":\"黑\"}', 3000.00, 1, 3000.00);
INSERT INTO `order_product` VALUES (18, 14, 2, '迈巴赫', '/profile/upload/2026/01/12/4cc491fc405f44ac9cf2176ae6231b91_20260112142620A001.jpg', '{\"尺寸\":\"21\",\"颜色\":\"红\"}', 12000.00, 1, 12000.00);
INSERT INTO `order_product` VALUES (19, 15, 1, '大轮毂', '/profile/upload/2026/01/12/967bebee1418499293180df3b67bd297.jpeg~tplv-a9rns2rl98-downsize_watermark_1_5_b_20260112191433A011.png', '{\"尺寸\":\"21寸\"}', 3000.00, 1, 3000.00);
INSERT INTO `order_product` VALUES (20, 16, 1, '大轮毂', '/profile/upload/2026/01/12/967bebee1418499293180df3b67bd297.jpeg~tplv-a9rns2rl98-downsize_watermark_1_5_b_20260112191433A011.png', '{\"尺寸\":\"20寸\",\"厚度\":\"200\",\"颜色\":\"黑\"}', 3000.00, 3, 9000.00);
INSERT INTO `order_product` VALUES (21, 17, 8, '速系列尾翼', '/profile/upload/2026/01/12/e5542aa76302431b9d8bb8774f6b0ef5.jpeg~tplv-a9rns2rl98-downsize_watermark_1_5_b_20260112192334A001.png', NULL, 50000.00, 1, 50000.00);
INSERT INTO `order_product` VALUES (22, 18, 5, '米其林轮胎', '/profile/upload/2026/01/12/0738.jpg_wh860_20260112190730A001.jpg', '{\"尺寸\":\"18寸\"}', 300.00, 1, 300.00);
INSERT INTO `order_product` VALUES (23, 19, 5, '米其林轮胎', '/profile/upload/2026/01/12/0738.jpg_wh860_20260112190730A001.jpg', '{\"尺寸\":\"20寸\"}', 300.00, 1, 300.00);
INSERT INTO `order_product` VALUES (24, 20, 5, '米其林轮胎', '/profile/upload/2026/01/12/0738.jpg_wh860_20260112190730A001.jpg', '{\"尺寸\":\"20寸\"}', 300.00, 1, 300.00);
INSERT INTO `order_product` VALUES (25, 21, 7, '测试', '/profile/upload/2026/01/12/OIP-C_20260112190923A008.jpg', '{\"测试2\":\"测试222\"}', 100.00, 1, 100.00);

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `brand` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '品牌名称',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `main_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '主图',
  `sub_images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '附图（多个图片路径，JSON格式）',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '商品详情（HTML/富文本）',
  `spec` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '规格参数（JSON格式）',
  `fit_car_model` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '适配车型（JSON格式）',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `stock` int NULL DEFAULT 0 COMMENT '库存',
  `warn_stock` int NULL DEFAULT 10 COMMENT '库存预警值',
  `sales` int NULL DEFAULT 0 COMMENT '销量',
  `is_hot` tinyint(1) NULL DEFAULT 0 COMMENT '是否热销',
  `is_new` tinyint(1) NULL DEFAULT 0 COMMENT '是否新品',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态（0下架，1上架）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_brand_id`(`brand` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  FULLTEXT INDEX `ft_name_subtitle`(`name`) COMMENT '全文索引用于搜索'
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, 2, '奔驰', '大轮毂', '/profile/upload/2026/01/12/967bebee1418499293180df3b67bd297.jpeg~tplv-a9rns2rl98-downsize_watermark_1_5_b_20260112191433A011.png', '[\"/profile/upload/2026/01/12/生成奔驰轮毂照片_20260112191433A012.png\",\"/profile/upload/2026/01/12/c1e2f9a79cc144259b1b1b14d52d62b5.jpeg~tplv-a9rns2rl98-image_pre_watermark_1_5b_20260112191433A013.png\",\"/profile/upload/2026/01/12/cbfac2a3303d41baa0f0b7867f088a9d.jpeg~tplv-a9rns2rl98-downsize_watermark_1_5_b_20260112191433A014.png\",\"/profile/upload/2026/01/12/a8f497681cd5455ebae55afbe20e9ff9.jpeg~tplv-a9rns2rl98-downsize_watermark_1_5_b_20260112191433A015.png\"]', '奔驰轮毂', '[{\"name\":\"尺寸\",\"values\":[\"19寸\",\"20寸\",\"21寸\"]},{\"name\":\"厚度\",\"values\":[\"100\",\"200\"]},{\"name\":\"颜色\",\"values\":[\"黑\"]}]', '奔驰', 3000.00, 78, 15, 12, 0, 0, 1, '2026-01-08 17:15:41', '2026-01-13 19:23:18');
INSERT INTO `product` VALUES (2, 3, '迈巴赫', '迈巴赫', '/profile/upload/2026/01/12/4cc491fc405f44ac9cf2176ae6231b91_20260112142620A001.jpg', '[\"/profile/upload/2026/01/12/4cc491fc405f44ac9cf2176ae6231b91_20260112143426A002.jpg\",\"/profile/upload/2026/01/12/27267424_052818493083_2_20260112143426A003.jpg\",\"/profile/upload/2026/01/12/OIP-C_20260112143426A004.jpg\"]', '迈巴赫专用车胎', '[{\"name\":\"尺寸\",\"values\":[\"20\",\"21\"]},{\"name\":\"颜色\",\"values\":[\"红\",\"橙\"]}]', '迈巴赫', 12000.00, 96, 10, 4, 0, 0, 1, '2026-01-08 21:54:59', '2026-01-12 15:05:08');
INSERT INTO `product` VALUES (5, 3, '米其林', '米其林轮胎', '/profile/upload/2026/01/12/0738.jpg_wh860_20260112190730A001.jpg', '[\"/profile/upload/2026/01/12/1077.jpg_wh860_20260112190730A002.jpg\",\"/profile/upload/2026/01/12/2904.jpg_wh860_20260112190730A003.jpg\"]', '很好的轮胎', '[{\"name\":\"尺寸\",\"values\":[\"18寸\",\"19寸\",\"20寸\"]}]', NULL, 300.00, 147, 10, 3, 0, 0, 1, '2026-01-12 19:07:31', '2026-01-12 19:07:30');
INSERT INTO `product` VALUES (6, 8, '测试', '测试', '/profile/upload/2026/01/12/4cc491fc405f44ac9cf2176ae6231b91_20260112190837A004.jpg', '[\"/profile/upload/2026/01/12/0738.jpg_wh860_20260112190837A005.jpg\",\"/profile/upload/2026/01/12/1077.jpg_wh860_20260112190837A006.jpg\",\"/profile/upload/2026/01/12/2904.jpg_wh860_20260112190837A007.jpg\"]', '测试', '[{\"name\":\"测试\",\"values\":[\"测试1\",\"测试11\",\"测试111\"]}]', NULL, 100.00, 100, 10, 0, 0, 0, 1, '2026-01-12 19:08:38', '2026-01-12 19:18:15');
INSERT INTO `product` VALUES (7, 8, '测试', '测试', '/profile/upload/2026/01/12/OIP-C_20260112190923A008.jpg', '[\"/profile/upload/2026/01/12/27267424_052818493083_2_20260112190923A009.jpg\",\"/profile/upload/2026/01/12/6235.jpg_wh860_20260112190923A010.jpg\"]', '测试2222', '[{\"name\":\"测试2\",\"values\":[\"测试2\",\"测试22\",\"测试222\"]}]', '测试', 100.00, 99, 10, 1, 0, 0, 1, '2026-01-12 19:09:23', '2026-01-12 19:18:04');
INSERT INTO `product` VALUES (8, 4, '速', '速系列尾翼', '/profile/upload/2026/01/12/e5542aa76302431b9d8bb8774f6b0ef5.jpeg~tplv-a9rns2rl98-downsize_watermark_1_5_b_20260112192334A001.png', '[\"/profile/upload/2026/01/12/9040c5ee72d04bd1a8b76c0e89629a3e.jpeg~tplv-a9rns2rl98-downsize_watermark_1_5_b_20260112192334A002.png\",\"/profile/upload/2026/01/12/c40a923799a147f3b83b81622513fd01.jpeg~tplv-a9rns2rl98-downsize_watermark_1_5_b_20260112192334A003.png\",\"/profile/upload/2026/01/12/f75f343e43e8470786fbe0cfb829f080.jpeg~tplv-a9rns2rl98-downsize_watermark_1_5_b_20260112192334A004.png\",\"/profile/upload/2026/01/12/a0e3bb855790499db04fe309df21e922.jpeg~tplv-a9rns2rl98-downsize_watermark_1_5_b_20260112192334A005.png\"]', NULL, '[{\"name\":\"颜色\",\"values\":[\"银\",\"黑\",\"红\",\"红黑\",\"蓝黑\"]}]', NULL, 50000.00, 48, 5, 2, 0, 0, 1, '2026-01-12 19:23:34', '2026-01-12 19:23:34');

-- ----------------------------
-- Table structure for refund_return
-- ----------------------------
DROP TABLE IF EXISTS `refund_return`;
CREATE TABLE `refund_return`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` tinyint(1) NOT NULL COMMENT '类型（1退款，2退货）',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '退款/退货原因',
  `amount` decimal(10, 2) NOT NULL COMMENT '退款金额',
  `evidence` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '凭证图片（多个用逗号分隔）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0待审核，1审核通过，2审核拒绝，3退款中，4退款成功，5退货中，6退货完成）',
  `admin_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '管理员备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '退款/退货表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of refund_return
-- ----------------------------
INSERT INTO `refund_return` VALUES (9, 15, 1, 1, '1111111111111', 3000.00, NULL, 1, NULL, '2026-01-14 17:16:38', '2026-01-14 17:16:38');
INSERT INTO `refund_return` VALUES (10, 12, 1, 2, '222222222222222', 3000.00, 'http://localhost:8080/profile/upload/2026/01/14/4cc491fc405f44ac9cf2176ae6231b91_20260114201839A001.jpg', 2, '11111111111111111', '2026-01-14 20:18:40', '2026-01-14 20:18:40');
INSERT INTO `refund_return` VALUES (11, 12, 1, 2, '555555555555555', 3000.00, NULL, 1, NULL, '2026-01-15 19:24:21', '2026-01-15 19:24:21');

-- ----------------------------
-- Table structure for store
-- ----------------------------
DROP TABLE IF EXISTS `store`;
CREATE TABLE `store`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '门店名称',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区/县',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `business_hours` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '营业时间',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态（0停业，1营业）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_province_city_district`(`province` ASC, `city` ASC, `district` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '门店表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of store
-- ----------------------------
INSERT INTO `store` VALUES (1, '阳光店', '福建省', '福州市', '马尾区', '马尾镇登龙路99号阳光学院', '16888888888', '8:00-20:00', 1, '2026-01-10 16:15:16', '2026-01-13 23:21:42');
INSERT INTO `store` VALUES (2, '测试', '北京市', '市辖区', '东城区', '测试测试测试测试测试测试测试测试测试测试测试测试测试', '16999999999', '08:30-21:59', 0, '2026-01-12 18:54:00', '2026-01-13 23:22:05');
INSERT INTO `store` VALUES (3, '马尾店', '福建省', '福州市', '马尾区', '马尾镇', '16999999999', '12:20-21:59', 1, '2026-01-13 22:45:42', '2026-01-13 23:22:29');

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '参数配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2025-12-15 15:32:11', 'admin', '2026-01-16 16:07:54', '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config` VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2025-12-15 15:32:11', '', NULL, '初始化密码 123456');
INSERT INTO `sys_config` VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2025-12-15 15:32:11', 'admin', '2026-01-16 16:08:24', '深色主题theme-dark，浅色主题theme-light');
INSERT INTO `sys_config` VALUES (4, '账号自助-验证码开关', 'sys.account.captchaEnabled', 'true', 'Y', 'admin', '2025-12-15 15:32:11', '', NULL, '是否开启验证码功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES (5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'true', 'Y', 'admin', '2025-12-15 15:32:11', 'admin', '2025-12-26 20:27:00', '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES (6, '用户登录-黑名单列表', 'sys.login.blackIPList', '', 'Y', 'admin', '2025-12-15 15:32:11', '', NULL, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');
INSERT INTO `sys_config` VALUES (7, '用户管理-初始密码修改策略', 'sys.account.initPasswordModify', '1', 'Y', 'admin', '2025-12-15 15:32:11', '', NULL, '0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框');
INSERT INTO `sys_config` VALUES (8, '用户管理-账号密码更新周期', 'sys.account.passwordValidateDays', '0', 'Y', 'admin', '2025-12-15 15:32:11', '', NULL, '密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框');

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int NULL DEFAULT 0 COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '性别男');
INSERT INTO `sys_dict_data` VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '性别女');
INSERT INTO `sys_dict_data` VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '性别未知');
INSERT INTO `sys_dict_data` VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '显示菜单');
INSERT INTO `sys_dict_data` VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '隐藏菜单');
INSERT INTO `sys_dict_data` VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '默认分组');
INSERT INTO `sys_dict_data` VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '系统分组');
INSERT INTO `sys_dict_data` VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '系统默认是');
INSERT INTO `sys_dict_data` VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '系统默认否');
INSERT INTO `sys_dict_data` VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '通知');
INSERT INTO `sys_dict_data` VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '公告');
INSERT INTO `sys_dict_data` VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '关闭状态');
INSERT INTO `sys_dict_data` VALUES (18, 99, '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '其他操作');
INSERT INTO `sys_dict_data` VALUES (19, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '新增操作');
INSERT INTO `sys_dict_data` VALUES (20, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '修改操作');
INSERT INTO `sys_dict_data` VALUES (21, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '删除操作');
INSERT INTO `sys_dict_data` VALUES (22, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '授权操作');
INSERT INTO `sys_dict_data` VALUES (23, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '导出操作');
INSERT INTO `sys_dict_data` VALUES (24, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '导入操作');
INSERT INTO `sys_dict_data` VALUES (25, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '强退操作');
INSERT INTO `sys_dict_data` VALUES (26, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '生成操作');
INSERT INTO `sys_dict_data` VALUES (27, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '清空操作');
INSERT INTO `sys_dict_data` VALUES (28, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (29, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '停用状态');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE INDEX `dict_type`(`dict_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '用户性别列表');
INSERT INTO `sys_dict_type` VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '菜单状态列表');
INSERT INTO `sys_dict_type` VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '系统开关列表');
INSERT INTO `sys_dict_type` VALUES (4, '任务状态', 'sys_job_status', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '任务状态列表');
INSERT INTO `sys_dict_type` VALUES (5, '任务分组', 'sys_job_group', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '任务分组列表');
INSERT INTO `sys_dict_type` VALUES (6, '系统是否', 'sys_yes_no', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '系统是否列表');
INSERT INTO `sys_dict_type` VALUES (7, '通知类型', 'sys_notice_type', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '通知类型列表');
INSERT INTO `sys_dict_type` VALUES (8, '通知状态', 'sys_notice_status', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '通知状态列表');
INSERT INTO `sys_dict_type` VALUES (9, '操作类型', 'sys_oper_type', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '操作类型列表');
INSERT INTO `sys_dict_type` VALUES (10, '系统状态', 'sys_common_status', '0', 'admin', '2025-12-15 15:32:11', '', NULL, '登录状态列表');

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`  (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作系统',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '提示消息',
  `login_time` datetime NULL DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`) USING BTREE,
  INDEX `idx_sys_logininfor_s`(`status` ASC) USING BTREE,
  INDEX `idx_sys_logininfor_lt`(`login_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 239 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统访问记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
INSERT INTO `sys_logininfor` VALUES (100, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2025-12-15 16:12:38');
INSERT INTO `sys_logininfor` VALUES (101, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2025-12-26 17:52:45');
INSERT INTO `sys_logininfor` VALUES (102, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2025-12-26 17:53:02');
INSERT INTO `sys_logininfor` VALUES (103, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2025-12-26 17:54:58');
INSERT INTO `sys_logininfor` VALUES (104, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2025-12-26 19:47:55');
INSERT INTO `sys_logininfor` VALUES (105, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2025-12-26 19:54:25');
INSERT INTO `sys_logininfor` VALUES (106, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2025-12-26 20:01:40');
INSERT INTO `sys_logininfor` VALUES (107, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2025-12-26 20:03:39');
INSERT INTO `sys_logininfor` VALUES (108, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2025-12-26 20:18:47');
INSERT INTO `sys_logininfor` VALUES (109, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2025-12-26 20:34:00');
INSERT INTO `sys_logininfor` VALUES (110, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2025-12-26 20:43:59');
INSERT INTO `sys_logininfor` VALUES (111, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '1', '验证码已失效', '2025-12-26 20:48:02');
INSERT INTO `sys_logininfor` VALUES (112, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2025-12-26 20:48:02');
INSERT INTO `sys_logininfor` VALUES (113, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2025-12-26 20:49:36');
INSERT INTO `sys_logininfor` VALUES (114, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2025-12-26 20:49:36');
INSERT INTO `sys_logininfor` VALUES (115, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2025-12-26 20:55:21');
INSERT INTO `sys_logininfor` VALUES (116, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2025-12-26 20:58:51');
INSERT INTO `sys_logininfor` VALUES (117, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2025-12-26 20:58:53');
INSERT INTO `sys_logininfor` VALUES (118, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2025-12-26 21:00:47');
INSERT INTO `sys_logininfor` VALUES (119, 'test1', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '注册成功', '2025-12-26 21:01:19');
INSERT INTO `sys_logininfor` VALUES (120, 'test1', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2025-12-26 21:01:35');
INSERT INTO `sys_logininfor` VALUES (121, 'test1', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2025-12-26 21:01:48');
INSERT INTO `sys_logininfor` VALUES (122, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2025-12-26 21:02:49');
INSERT INTO `sys_logininfor` VALUES (123, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2025-12-26 21:02:54');
INSERT INTO `sys_logininfor` VALUES (124, 'test1', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2025-12-26 21:03:04');
INSERT INTO `sys_logininfor` VALUES (125, 'test1', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2025-12-26 21:09:39');
INSERT INTO `sys_logininfor` VALUES (126, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2025-12-26 23:50:48');
INSERT INTO `sys_logininfor` VALUES (127, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-06 22:30:26');
INSERT INTO `sys_logininfor` VALUES (128, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '1', '验证码错误', '2026-01-06 23:07:23');
INSERT INTO `sys_logininfor` VALUES (129, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-06 23:29:16');
INSERT INTO `sys_logininfor` VALUES (130, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-06 23:39:47');
INSERT INTO `sys_logininfor` VALUES (131, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-06 23:39:50');
INSERT INTO `sys_logininfor` VALUES (132, '123', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '注册成功', '2026-01-07 13:31:52');
INSERT INTO `sys_logininfor` VALUES (133, '123', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-07 13:31:58');
INSERT INTO `sys_logininfor` VALUES (134, '123', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-07 13:32:12');
INSERT INTO `sys_logininfor` VALUES (135, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-07 13:32:17');
INSERT INTO `sys_logininfor` VALUES (136, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-07 13:45:48');
INSERT INTO `sys_logininfor` VALUES (137, '111', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '注册成功', '2026-01-07 13:45:58');
INSERT INTO `sys_logininfor` VALUES (138, '333', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '注册成功', '2026-01-07 13:53:34');
INSERT INTO `sys_logininfor` VALUES (139, '333', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-07 13:53:40');
INSERT INTO `sys_logininfor` VALUES (140, '333', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-07 14:05:58');
INSERT INTO `sys_logininfor` VALUES (141, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-07 14:06:01');
INSERT INTO `sys_logininfor` VALUES (142, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-07 14:46:48');
INSERT INTO `sys_logininfor` VALUES (143, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-07 17:10:49');
INSERT INTO `sys_logininfor` VALUES (144, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-07 20:01:57');
INSERT INTO `sys_logininfor` VALUES (145, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-07 23:16:13');
INSERT INTO `sys_logininfor` VALUES (146, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-08 00:12:18');
INSERT INTO `sys_logininfor` VALUES (147, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-08 14:09:16');
INSERT INTO `sys_logininfor` VALUES (148, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-08 15:48:03');
INSERT INTO `sys_logininfor` VALUES (149, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-08 21:29:06');
INSERT INTO `sys_logininfor` VALUES (150, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-09 00:02:48');
INSERT INTO `sys_logininfor` VALUES (151, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-10 15:38:09');
INSERT INTO `sys_logininfor` VALUES (152, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '1', '验证码错误', '2026-01-10 16:09:52');
INSERT INTO `sys_logininfor` VALUES (153, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-10 16:09:56');
INSERT INTO `sys_logininfor` VALUES (154, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '1', '验证码已失效', '2026-01-10 16:58:37');
INSERT INTO `sys_logininfor` VALUES (155, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-10 16:58:37');
INSERT INTO `sys_logininfor` VALUES (156, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-10 17:27:27');
INSERT INTO `sys_logininfor` VALUES (157, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-10 19:50:47');
INSERT INTO `sys_logininfor` VALUES (158, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-10 20:35:26');
INSERT INTO `sys_logininfor` VALUES (159, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-10 20:46:28');
INSERT INTO `sys_logininfor` VALUES (160, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-10 20:51:08');
INSERT INTO `sys_logininfor` VALUES (161, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-10 20:51:11');
INSERT INTO `sys_logininfor` VALUES (162, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-12 13:22:56');
INSERT INTO `sys_logininfor` VALUES (163, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-12 14:24:42');
INSERT INTO `sys_logininfor` VALUES (164, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-12 18:42:03');
INSERT INTO `sys_logininfor` VALUES (165, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-13 15:57:02');
INSERT INTO `sys_logininfor` VALUES (166, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-13 17:30:43');
INSERT INTO `sys_logininfor` VALUES (167, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-13 18:56:58');
INSERT INTO `sys_logininfor` VALUES (168, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '1', '验证码已失效', '2026-01-13 22:12:43');
INSERT INTO `sys_logininfor` VALUES (169, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-13 22:12:43');
INSERT INTO `sys_logininfor` VALUES (170, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '1', '验证码错误', '2026-01-14 14:02:38');
INSERT INTO `sys_logininfor` VALUES (171, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-14 14:02:45');
INSERT INTO `sys_logininfor` VALUES (172, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-14 18:54:39');
INSERT INTO `sys_logininfor` VALUES (173, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-15 19:12:03');
INSERT INTO `sys_logininfor` VALUES (174, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-15 20:51:03');
INSERT INTO `sys_logininfor` VALUES (175, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '注册成功', '2026-01-15 20:51:16');
INSERT INTO `sys_logininfor` VALUES (176, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-15 20:51:25');
INSERT INTO `sys_logininfor` VALUES (177, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-15 21:23:42');
INSERT INTO `sys_logininfor` VALUES (178, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-15 21:23:43');
INSERT INTO `sys_logininfor` VALUES (179, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-15 21:23:52');
INSERT INTO `sys_logininfor` VALUES (180, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-15 21:24:01');
INSERT INTO `sys_logininfor` VALUES (181, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 10:49:52');
INSERT INTO `sys_logininfor` VALUES (182, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 11:19:32');
INSERT INTO `sys_logininfor` VALUES (183, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '1', '验证码错误', '2026-01-16 11:19:40');
INSERT INTO `sys_logininfor` VALUES (184, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 11:19:44');
INSERT INTO `sys_logininfor` VALUES (185, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 11:20:37');
INSERT INTO `sys_logininfor` VALUES (186, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 11:20:40');
INSERT INTO `sys_logininfor` VALUES (187, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 11:28:04');
INSERT INTO `sys_logininfor` VALUES (188, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 11:28:11');
INSERT INTO `sys_logininfor` VALUES (189, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 11:28:15');
INSERT INTO `sys_logininfor` VALUES (190, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 11:28:24');
INSERT INTO `sys_logininfor` VALUES (191, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 11:36:23');
INSERT INTO `sys_logininfor` VALUES (192, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 11:36:27');
INSERT INTO `sys_logininfor` VALUES (193, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 11:39:38');
INSERT INTO `sys_logininfor` VALUES (194, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 11:39:48');
INSERT INTO `sys_logininfor` VALUES (195, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 11:42:54');
INSERT INTO `sys_logininfor` VALUES (196, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 11:42:59');
INSERT INTO `sys_logininfor` VALUES (197, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 11:43:07');
INSERT INTO `sys_logininfor` VALUES (198, '测试', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '1', '用户不存在/密码错误', '2026-01-16 11:43:19');
INSERT INTO `sys_logininfor` VALUES (199, '测试', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '1', '验证码错误', '2026-01-16 11:43:25');
INSERT INTO `sys_logininfor` VALUES (200, '测试', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '1', '验证码错误', '2026-01-16 11:43:28');
INSERT INTO `sys_logininfor` VALUES (201, '测试', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '1', '用户不存在/密码错误', '2026-01-16 11:43:31');
INSERT INTO `sys_logininfor` VALUES (202, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '1', '验证码错误', '2026-01-16 11:43:42');
INSERT INTO `sys_logininfor` VALUES (203, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 11:43:47');
INSERT INTO `sys_logininfor` VALUES (204, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 11:53:19');
INSERT INTO `sys_logininfor` VALUES (205, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 11:53:28');
INSERT INTO `sys_logininfor` VALUES (206, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 11:53:33');
INSERT INTO `sys_logininfor` VALUES (207, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 11:53:43');
INSERT INTO `sys_logininfor` VALUES (208, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 13:03:58');
INSERT INTO `sys_logininfor` VALUES (209, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 13:04:46');
INSERT INTO `sys_logininfor` VALUES (210, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 13:04:57');
INSERT INTO `sys_logininfor` VALUES (211, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 13:11:49');
INSERT INTO `sys_logininfor` VALUES (212, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 13:12:32');
INSERT INTO `sys_logininfor` VALUES (213, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 13:18:52');
INSERT INTO `sys_logininfor` VALUES (214, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 13:18:58');
INSERT INTO `sys_logininfor` VALUES (215, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 13:19:11');
INSERT INTO `sys_logininfor` VALUES (216, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 14:11:43');
INSERT INTO `sys_logininfor` VALUES (217, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 14:11:48');
INSERT INTO `sys_logininfor` VALUES (218, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 14:11:54');
INSERT INTO `sys_logininfor` VALUES (219, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 14:12:04');
INSERT INTO `sys_logininfor` VALUES (220, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 15:45:13');
INSERT INTO `sys_logininfor` VALUES (221, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '1', '验证码错误', '2026-01-16 15:47:00');
INSERT INTO `sys_logininfor` VALUES (222, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 15:47:03');
INSERT INTO `sys_logininfor` VALUES (223, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 15:47:14');
INSERT INTO `sys_logininfor` VALUES (224, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 15:47:19');
INSERT INTO `sys_logininfor` VALUES (225, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 15:48:17');
INSERT INTO `sys_logininfor` VALUES (226, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 15:48:27');
INSERT INTO `sys_logininfor` VALUES (227, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 15:48:33');
INSERT INTO `sys_logininfor` VALUES (228, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 15:48:41');
INSERT INTO `sys_logininfor` VALUES (229, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 16:21:56');
INSERT INTO `sys_logininfor` VALUES (230, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 16:22:03');
INSERT INTO `sys_logininfor` VALUES (231, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 16:23:49');
INSERT INTO `sys_logininfor` VALUES (232, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 16:23:53');
INSERT INTO `sys_logininfor` VALUES (233, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 16:24:11');
INSERT INTO `sys_logininfor` VALUES (234, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 16:24:22');
INSERT INTO `sys_logininfor` VALUES (235, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 16:24:30');
INSERT INTO `sys_logininfor` VALUES (236, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 16:29:36');
INSERT INTO `sys_logininfor` VALUES (237, 'admin', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '退出成功', '2026-01-16 16:31:09');
INSERT INTO `sys_logininfor` VALUES (238, 'cs', '127.0.0.1', '内网IP', 'Edge 143', 'Windows >=10', '0', '登录成功', '2026-01-16 16:31:18');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由名称',
  `is_frame` int NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2025 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 1, 'system', NULL, '', '', 1, 0, 'M', '0', '0', '', 'system', 'admin', '2025-12-15 15:32:10', '', NULL, '系统管理目录');
INSERT INTO `sys_menu` VALUES (2, '系统监控', 0, 2, 'monitor', NULL, '', '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', '2025-12-15 15:32:10', '', NULL, '系统监控目录');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', '2025-12-15 15:32:10', '', NULL, '用户管理菜单');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2025-12-15 15:32:10', '', NULL, '角色管理菜单');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2025-12-15 15:32:10', '', NULL, '菜单管理菜单');
INSERT INTO `sys_menu` VALUES (108, '日志管理', 1, 9, 'log', '', '', '', 1, 0, 'M', '0', '0', '', 'log', 'admin', '2025-12-15 15:32:10', '', NULL, '日志管理菜单');
INSERT INTO `sys_menu` VALUES (109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2025-12-15 15:32:10', '', NULL, '在线用户菜单');
INSERT INTO `sys_menu` VALUES (500, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', '', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', '2025-12-15 15:32:10', '', NULL, '操作日志菜单');
INSERT INTO `sys_menu` VALUES (501, '登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', '', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', '2025-12-15 15:32:10', '', NULL, '登录日志菜单');
INSERT INTO `sys_menu` VALUES (1000, '用户查询', 100, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', '2025-12-15 15:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1001, '用户新增', 100, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', '2025-12-15 15:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1002, '用户修改', 100, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2025-12-15 15:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1003, '用户删除', 100, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', '2025-12-15 15:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1004, '用户导出', 100, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 'admin', '2025-12-15 15:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1005, '用户导入', 100, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 'admin', '2025-12-15 15:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1006, '重置密码', 100, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2025-12-15 15:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1007, '角色查询', 101, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', '2025-12-15 15:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1008, '角色新增', 101, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', '2025-12-15 15:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1009, '角色修改', 101, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2025-12-15 15:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1010, '角色删除', 101, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', '2025-12-15 15:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1011, '角色导出', 101, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 'admin', '2025-12-15 15:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1012, '菜单查询', 102, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 'admin', '2025-12-15 15:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1013, '菜单新增', 102, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 'admin', '2025-12-15 15:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1014, '菜单修改', 102, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2025-12-15 15:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1015, '菜单删除', 102, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2025-12-15 15:32:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1039, '操作查询', 500, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2025-12-15 15:32:11', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1040, '操作删除', 500, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2025-12-15 15:32:11', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1041, '日志导出', 500, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2025-12-15 15:32:11', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1042, '登录查询', 501, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2025-12-15 15:32:11', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1043, '登录删除', 501, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2025-12-15 15:32:11', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1044, '日志导出', 501, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2025-12-15 15:32:11', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1045, '账户解锁', 501, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock', '#', 'admin', '2025-12-15 15:32:11', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1046, '在线查询', 109, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2025-12-15 15:32:11', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1047, '批量强退', 109, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2025-12-15 15:32:11', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1048, '单条强退', 109, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2025-12-15 15:32:11', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2012, '商品管理', 0, 4, 'protucd', NULL, NULL, '', 1, 0, 'M', '0', '0', '', '#', 'admin', '2026-01-08 15:50:24', 'admin', '2026-01-16 15:55:02', '');
INSERT INTO `sys_menu` VALUES (2014, '商品分类管理', 2012, 3, 'category', 'category/category/index', NULL, '', 1, 0, 'C', '0', '0', '', '#', 'admin', '2026-01-08 16:15:54', 'admin', '2026-01-16 15:55:32', '');
INSERT INTO `sys_menu` VALUES (2015, '门店管理', 0, 5, 'store', NULL, NULL, '', 1, 0, 'M', '0', '0', NULL, '#', 'admin', '2026-01-10 15:40:27', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2016, '门店管理', 2015, 1, 'store', 'store/store/index', NULL, '', 1, 0, 'C', '0', '0', NULL, '#', 'admin', '2026-01-10 15:41:48', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2022, '订单管理', 0, 6, 'orderAdmin', 'order/orderAdmin/orderAdmin', NULL, '', 1, 0, 'C', '0', '0', '', '#', 'admin', '2026-01-14 17:37:12', 'admin', '2026-01-16 15:56:05', '');
INSERT INTO `sys_menu` VALUES (2023, '商品管理', 2012, 1, 'product', 'product/product/index', NULL, '', 1, 0, 'C', '0', '0', NULL, '#', 'admin', '2026-01-16 15:53:20', '', NULL, '');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告标题',
  `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob NULL COMMENT '公告内容',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '模块标题',
  `business_type` int NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求方式',
  `operator_type` int NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '返回参数',
  `status` int NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint NULL DEFAULT 0 COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`) USING BTREE,
  INDEX `idx_sys_oper_log_bt`(`business_type` ASC) USING BTREE,
  INDEX `idx_sys_oper_log_s`(`status` ASC) USING BTREE,
  INDEX `idx_sys_oper_log_ot`(`oper_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 266 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
INSERT INTO `sys_oper_log` VALUES (100, '用户头像', 2, 'com.auto.web.controller.system.SysProfileController.avatar()', 'POST', 1, 'admin', '研发部门', '/system/user/profile/avatar', '127.0.0.1', '内网IP', '', NULL, 1, 'Cannot invoke \"String.length()\" because the return value of \"com.auto.common.config.AutoConfig.getProfile()\" is null', '2025-12-26 17:56:38', 178);
INSERT INTO `sys_oper_log` VALUES (101, '用户头像', 2, 'com.auto.web.controller.system.SysProfileController.avatar()', 'POST', 1, 'admin', '研发部门', '/system/user/profile/avatar', '127.0.0.1', '内网IP', '', NULL, 1, 'D:\\ruoyi\\uploadPath\\avatar\\2025\\12\\26\\0af7a4e95e1c462880112d560c022599.jpg', '2025-12-26 19:48:07', 93);
INSERT INTO `sys_oper_log` VALUES (102, '用户头像', 2, 'com.auto.web.controller.system.SysProfileController.avatar()', 'POST', 1, 'admin', '研发部门', '/system/user/profile/avatar', '127.0.0.1', '内网IP', '', NULL, 1, 'D:\\ruoyi\\uploadPath\\avatar\\2025\\12\\26\\26507e8d533f4942a857bdf8ad1a98c5.jpg', '2025-12-26 19:48:11', 14);
INSERT INTO `sys_oper_log` VALUES (103, '用户头像', 2, 'com.auto.web.controller.system.SysProfileController.avatar()', 'POST', 1, 'admin', '研发部门', '/system/user/profile/avatar', '127.0.0.1', '内网IP', '', NULL, 1, 'Cannot invoke \"String.length()\" because the return value of \"com.auto.common.config.AutoConfig.getProfile()\" is null', '2025-12-26 19:51:16', 94);
INSERT INTO `sys_oper_log` VALUES (104, '用户头像', 2, 'com.auto.web.controller.system.SysProfileController.avatar()', 'POST', 1, 'admin', '研发部门', '/system/user/profile/avatar', '127.0.0.1', '内网IP', '', '{\"msg\":\"操作成功\",\"imgUrl\":\"/profile/avatar/2025/12/26/d7eb29e8511443d7bfd2e8de71ab7e60.jpg\",\"code\":200}', 0, NULL, '2025-12-26 19:54:08', 142);
INSERT INTO `sys_oper_log` VALUES (105, '用户头像', 2, 'com.auto.web.controller.system.SysProfileController.avatar()', 'POST', 1, 'admin', '研发部门', '/system/user/profile/avatar', '127.0.0.1', '内网IP', '', NULL, 1, '文件[OIP-C.webp]后缀[webp]不正确，请上传[bmp, gif, jpg, jpeg, png]格式', '2025-12-26 20:20:51', 17);
INSERT INTO `sys_oper_log` VALUES (106, '用户头像', 2, 'com.auto.web.controller.system.SysProfileController.avatar()', 'POST', 1, 'admin', '研发部门', '/system/user/profile/avatar', '127.0.0.1', '内网IP', '', '{\"msg\":\"操作成功\",\"imgUrl\":\"/profile/avatar/2025/12/26/589f3d8c9fd94340932851729455275f.jpg\",\"code\":200}', 0, NULL, '2025-12-26 20:22:58', 154);
INSERT INTO `sys_oper_log` VALUES (107, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/103', '127.0.0.1', '内网IP', '103 ', '{\"msg\":\"存在子菜单,不允许删除\",\"code\":601}', 0, NULL, '2025-12-26 20:24:11', 13);
INSERT INTO `sys_oper_log` VALUES (108, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/1019', '127.0.0.1', '内网IP', '1019 ', '{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}', 0, NULL, '2025-12-26 20:24:16', 25);
INSERT INTO `sys_oper_log` VALUES (109, '角色管理', 2, 'com.auto.web.controller.system.SysRoleController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2025-12-15 15:32:10\",\"dataScope\":\"2\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[1,2,100,1000,1001,1002,1003,1004,1005,1006,101,1007,1008,1009,1010,1011,102,1012,1013,1014,1015,105,1025,1026,1027,1028,1029,106,1030,1031,1032,1033,1034,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045,109,1046,1047,1048,110,1049,1050,1051,1052,1053,1054,3,115,116,1055,1056,1057,1058,1059,1060,117],\"params\":{},\"remark\":\"普通角色\",\"roleId\":2,\"roleKey\":\"common\",\"roleName\":\"普通角色\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 20:25:14', 73);
INSERT INTO `sys_oper_log` VALUES (110, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/1019', '127.0.0.1', '内网IP', '1019 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 20:25:20', 17);
INSERT INTO `sys_oper_log` VALUES (111, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/1018', '127.0.0.1', '内网IP', '1018 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 20:25:24', 14);
INSERT INTO `sys_oper_log` VALUES (112, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/1017', '127.0.0.1', '内网IP', '1017 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 20:25:27', 13);
INSERT INTO `sys_oper_log` VALUES (113, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/1016', '127.0.0.1', '内网IP', '1016 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 20:25:30', 14);
INSERT INTO `sys_oper_log` VALUES (114, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/103', '127.0.0.1', '内网IP', '103 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 20:26:01', 16);
INSERT INTO `sys_oper_log` VALUES (115, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/1020', '127.0.0.1', '内网IP', '1020 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 20:26:11', 24);
INSERT INTO `sys_oper_log` VALUES (116, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/1021', '127.0.0.1', '内网IP', '1021 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 20:26:14', 15);
INSERT INTO `sys_oper_log` VALUES (117, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/1022', '127.0.0.1', '内网IP', '1022 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 20:26:16', 12);
INSERT INTO `sys_oper_log` VALUES (118, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/1023', '127.0.0.1', '内网IP', '1023 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 20:26:18', 15);
INSERT INTO `sys_oper_log` VALUES (119, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/1024', '127.0.0.1', '内网IP', '1024 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 20:26:20', 11);
INSERT INTO `sys_oper_log` VALUES (120, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/104', '127.0.0.1', '内网IP', '104 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 20:26:23', 15);
INSERT INTO `sys_oper_log` VALUES (121, '参数管理', 2, 'com.auto.web.controller.system.SysConfigController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/config', '127.0.0.1', '内网IP', '{\"configId\":5,\"configKey\":\"sys.account.registerUser\",\"configName\":\"账号自助-是否开启用户注册功能\",\"configType\":\"Y\",\"configValue\":\"true\",\"createBy\":\"admin\",\"createTime\":\"2025-12-15 15:32:11\",\"params\":{},\"remark\":\"是否开启注册用户功能（true开启，false关闭）\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 20:27:00', 28);
INSERT INTO `sys_oper_log` VALUES (122, '通知公告', 3, 'com.auto.web.controller.system.SysNoticeController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/notice/1', '127.0.0.1', '内网IP', '[1] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 20:27:45', 8);
INSERT INTO `sys_oper_log` VALUES (123, '通知公告', 3, 'com.auto.web.controller.system.SysNoticeController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/notice/2', '127.0.0.1', '内网IP', '[2] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 20:27:47', 7);
INSERT INTO `sys_oper_log` VALUES (124, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/114', '127.0.0.1', '内网IP', '114 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 20:29:20', 24);
INSERT INTO `sys_oper_log` VALUES (125, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/113', '127.0.0.1', '内网IP', '113 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 20:29:24', 11);
INSERT INTO `sys_oper_log` VALUES (126, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/112', '127.0.0.1', '内网IP', '112 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 20:29:27', 13);
INSERT INTO `sys_oper_log` VALUES (127, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/111', '127.0.0.1', '内网IP', '111 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 20:29:29', 13);
INSERT INTO `sys_oper_log` VALUES (128, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/4', '127.0.0.1', '内网IP', '4 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 20:29:58', 13);
INSERT INTO `sys_oper_log` VALUES (129, '个人信息', 2, 'com.auto.web.controller.system.SysProfileController.updateProfile()', 'PUT', 1, 'admin', '研发部门', '/system/user/profile', '127.0.0.1', '内网IP', '{\"admin\":false,\"email\":\"auto@163.com\",\"nickName\":\"超级管理\",\"params\":{},\"phonenumber\":\"15888888888\",\"sex\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-26 23:51:17', 40);
INSERT INTO `sys_oper_log` VALUES (130, '用户管理', 1, 'com.auto.web.controller.system.SysUserController.add()', 'POST', 1, 'admin', NULL, '/system/user', '127.0.0.1', '内网IP', '{\"admin\":false,\"nickName\":\"admin\",\"params\":{},\"postIds\":[],\"roleIds\":[],\"status\":\"0\",\"userName\":\"admin\"} ', '{\"msg\":\"新增用户\'admin\'失败，登录账号已存在\",\"code\":500}', 0, NULL, '2026-01-06 23:56:29', 8);
INSERT INTO `sys_oper_log` VALUES (131, '用户管理', 1, 'com.auto.web.controller.system.SysUserController.add()', 'POST', 1, 'admin', NULL, '/system/user', '127.0.0.1', '内网IP', '{\"admin\":false,\"nickName\":\"admin1\",\"params\":{},\"postIds\":[],\"roleIds\":[],\"status\":\"0\",\"userName\":\"admin\"} ', '{\"msg\":\"新增用户\'admin\'失败，登录账号已存在\",\"code\":500}', 0, NULL, '2026-01-06 23:56:34', 14);
INSERT INTO `sys_oper_log` VALUES (132, '用户管理', 1, 'com.auto.web.controller.system.SysUserController.add()', 'POST', 1, 'admin', NULL, '/system/user', '127.0.0.1', '内网IP', '{\"admin\":false,\"createBy\":\"admin\",\"nickName\":\"admin1\",\"params\":{},\"postIds\":[],\"roleIds\":[],\"status\":\"0\",\"userId\":101,\"userName\":\"admin1\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-06 23:56:43', 100);
INSERT INTO `sys_oper_log` VALUES (133, '角色管理', 2, 'com.auto.web.controller.system.SysRoleController.edit()', 'PUT', 1, 'admin', NULL, '/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2025-12-15 15:32:10\",\"dataScope\":\"2\",\"delFlag\":\"0\",\"deptCheckStrictly\":false,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2,109,1046,1047,1048,110,1049,1050,1051,1052,1053,1054,3,115,116,1055,1056,1057,1058,1059,1060,117],\"params\":{},\"remark\":\"普通角色\",\"roleId\":2,\"roleKey\":\"common\",\"roleName\":\"普通角色\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-07 13:32:59', 72);
INSERT INTO `sys_oper_log` VALUES (134, '用户管理', 4, 'com.auto.web.controller.system.SysUserController.insertAuthRole()', 'PUT', 1, 'admin', NULL, '/system/user/authRole', '127.0.0.1', '内网IP', '{\"roleIds\":\"2\",\"userId\":\"102\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-07 13:37:20', 13);
INSERT INTO `sys_oper_log` VALUES (135, '代码生成', 6, 'com.auto.generator.controller.GenController.importTableSave()', 'POST', 1, '333', NULL, '/tool/gen/importTable', '127.0.0.1', '内网IP', '{\"tables\":\"user_address\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-07 14:05:40', 229);
INSERT INTO `sys_oper_log` VALUES (136, '代码生成', 2, 'com.auto.generator.controller.GenController.editSave()', 'PUT', 1, 'admin', NULL, '/tool/gen', '127.0.0.1', '内网IP', '{\"businessName\":\"address\",\"className\":\"UserAddress\",\"columns\":[{\"capJavaField\":\"Id\",\"columnComment\":\"主键ID\",\"columnId\":1,\"columnName\":\"id\",\"columnType\":\"bigint\",\"createBy\":\"333\",\"createTime\":\"2026-01-07 14:05:40\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":true,\"isIncrement\":\"1\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"id\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":1,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"UserId\",\"columnComment\":\"用户ID\",\"columnId\":2,\"columnName\":\"user_id\",\"columnType\":\"bigint\",\"createBy\":\"333\",\"createTime\":\"2026-01-07 14:05:40\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"0\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"0\",\"isPk\":\"0\",\"isQuery\":\"0\",\"isRequired\":\"0\",\"javaField\":\"userId\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":false,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":2,\"superColumn\":false,\"tableId\":1,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"Consignee\",\"columnComment\":\"收货人姓名\",\"columnId\":3,\"columnName\":\"consignee\",\"columnType\":\"varchar(50)\",\"createBy\":\"333\",\"createTime\":\"2026-01-07 14:05:40\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"0\",\"isRequired\":\"1\",\"javaField\":\"consignee\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":false,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":1,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"Phone\",\"columnComment\":\"手机号\",\"columnId\":4,\"columnName\":\"phone\",\"columnType\":\"varchar(20)\",\"createBy\":\"333\",\"createTime\":\"2026-01-07 14:05:40\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"0\",\"isRequired\":\"1\",\"javaField\":\"phone\",\"javaType\":', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-07 14:47:44', 85);
INSERT INTO `sys_oper_log` VALUES (137, '代码生成', 6, 'com.auto.generator.controller.GenController.importTableSave()', 'POST', 1, 'admin', NULL, '/tool/gen/importTable', '127.0.0.1', '内网IP', '{\"tables\":\"order\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-07 14:49:06', 89);
INSERT INTO `sys_oper_log` VALUES (138, '代码生成', 8, 'com.auto.generator.controller.GenController.batchGenCode()', 'GET', 1, 'admin', NULL, '/tool/gen/batchGenCode', '127.0.0.1', '内网IP', '{\"tables\":\"user_address\"}', NULL, 0, NULL, '2026-01-07 14:50:16', 57);
INSERT INTO `sys_oper_log` VALUES (139, '菜单管理', 2, 'com.auto.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"address/address/index\",\"createTime\":\"2026-01-07 14:56:43\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2000,\"menuName\":\"收货地址\",\"menuType\":\"M\",\"orderNum\":1,\"params\":{},\"parentId\":0,\"path\":\"address\",\"perms\":\"address:address:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-07 15:03:14', 45);
INSERT INTO `sys_oper_log` VALUES (140, '菜单管理', 2, 'com.auto.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"address/address/index\",\"createTime\":\"2026-01-07 15:08:20\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2006,\"menuName\":\"收货地址\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":3,\"path\":\"address\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-07 15:15:34', 65);
INSERT INTO `sys_oper_log` VALUES (141, '菜单管理', 2, 'com.auto.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"\",\"createTime\":\"2026-01-07 15:08:20\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2007,\"menuName\":\"收货地址查询\",\"menuType\":\"F\",\"orderNum\":1,\"params\":{},\"parentId\":2006,\"path\":\"#\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-07 15:15:43', 16);
INSERT INTO `sys_oper_log` VALUES (142, '菜单管理', 2, 'com.auto.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"\",\"createTime\":\"2026-01-07 15:08:20\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2008,\"menuName\":\"收货地址新增\",\"menuType\":\"F\",\"orderNum\":2,\"params\":{},\"parentId\":2006,\"path\":\"#\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-07 15:15:51', 16);
INSERT INTO `sys_oper_log` VALUES (143, '菜单管理', 2, 'com.auto.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"\",\"createTime\":\"2026-01-07 15:08:20\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2009,\"menuName\":\"收货地址修改\",\"menuType\":\"F\",\"orderNum\":3,\"params\":{},\"parentId\":2006,\"path\":\"#\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-07 15:15:55', 15);
INSERT INTO `sys_oper_log` VALUES (144, '菜单管理', 2, 'com.auto.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"\",\"createTime\":\"2026-01-07 15:08:20\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2010,\"menuName\":\"收货地址删除\",\"menuType\":\"F\",\"orderNum\":4,\"params\":{},\"parentId\":2006,\"path\":\"#\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-07 15:16:04', 17);
INSERT INTO `sys_oper_log` VALUES (145, '菜单管理', 2, 'com.auto.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"\",\"createTime\":\"2026-01-07 15:08:20\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2011,\"menuName\":\"收货地址导出\",\"menuType\":\"F\",\"orderNum\":5,\"params\":{},\"parentId\":2006,\"path\":\"#\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-07 15:16:12', 14);
INSERT INTO `sys_oper_log` VALUES (146, '菜单管理', 2, 'com.auto.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"address/address/index\",\"createTime\":\"2026-01-07 15:08:20\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2006,\"menuName\":\"收货地址\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":0,\"path\":\"address\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-07 15:32:51', 49);
INSERT INTO `sys_oper_log` VALUES (147, '收货地址', 1, 'com.auto.web.controller.client.UserAddressController.add()', 'POST', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"admin\",\"createTime\":\"2026-01-07 15:37:43\",\"detail\":\"登龙路\",\"district\":\"马尾区\",\"params\":{},\"phone\":\"1\",\"province\":\"福建省\"} ', NULL, 1, '\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'user_id\' doesn\'t have a default value\r\n### The error may exist in file [E:\\JavaObject\\Auto\\client\\target\\classes\\mapper\\address\\UserAddressMapper.xml]\r\n### The error may involve com.auto.mapper.UserAddressMapper.insertUserAddress-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into user_address          ( consignee,             phone,             province,             city,             district,             detail,                          create_time )           values ( ?,             ?,             ?,             ?,             ?,             ?,                          ? )\r\n### Cause: java.sql.SQLException: Field \'user_id\' doesn\'t have a default value\n; Field \'user_id\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'user_id\' doesn\'t have a default value', '2026-01-07 15:37:43', 66);
INSERT INTO `sys_oper_log` VALUES (148, '收货地址', 1, 'com.auto.web.controller.client.UserAddressController.add()', 'POST', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"admin\",\"createTime\":\"2026-01-07 15:38:35\",\"detail\":\"登龙路\",\"district\":\"马尾区\",\"id\":1,\"params\":{},\"phone\":\"1\",\"province\":\"福建省\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-07 15:38:35', 157);
INSERT INTO `sys_oper_log` VALUES (149, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"admin\",\"createTime\":\"2026-01-07 15:38:36\",\"detail\":\"登龙路99号\",\"district\":\"马尾区\",\"id\":1,\"isDefault\":0,\"params\":{},\"phone\":\"1\",\"province\":\"福建省\",\"updateTime\":\"2026-01-07 15:38:50\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-07 15:38:50', 11);
INSERT INTO `sys_oper_log` VALUES (150, '收货地址', 1, 'com.auto.web.controller.client.UserAddressController.add()', 'POST', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"123\",\"consignee\":\"123\",\"detail\":\"132\",\"district\":\"123\",\"params\":{},\"phone\":\"123\",\"province\":\"123\"} ', NULL, 1, '手机号格式不正确', '2026-01-07 15:47:44', 18);
INSERT INTO `sys_oper_log` VALUES (151, '收货地址', 1, 'com.auto.web.controller.client.UserAddressController.add()', 'POST', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"123\",\"consignee\":\"123\",\"detail\":\"132\",\"district\":\"123\",\"params\":{},\"phone\":\"1599999999\",\"province\":\"123\"} ', NULL, 1, '手机号格式不正确', '2026-01-07 15:47:52', 1);
INSERT INTO `sys_oper_log` VALUES (152, '收货地址', 1, 'com.auto.web.controller.client.UserAddressController.add()', 'POST', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"123\",\"consignee\":\"123\",\"createTime\":\"2026-01-07 15:47:54\",\"detail\":\"132\",\"district\":\"123\",\"id\":2,\"params\":{},\"phone\":\"15999999999\",\"province\":\"123\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-07 15:47:55', 23);
INSERT INTO `sys_oper_log` VALUES (153, '代码生成', 6, 'com.auto.generator.controller.GenController.importTableSave()', 'POST', 1, 'admin', NULL, '/tool/gen/importTable', '127.0.0.1', '内网IP', '{\"tables\":\"product,appointment,cart,category,favorite,order_product,refund_return,store\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-07 17:11:33', 420);
INSERT INTO `sys_oper_log` VALUES (154, '收货地址', 3, 'com.auto.web.controller.client.UserAddressController.remove()', 'DELETE', 1, 'admin', NULL, '/address/address/2', '127.0.0.1', '内网IP', '[2] ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-07 17:34:20', 18);
INSERT INTO `sys_oper_log` VALUES (155, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"admin\",\"createTime\":\"2026-01-07 15:38:36\",\"detail\":\"登龙路99号\",\"district\":\"马尾区\",\"id\":1,\"isDefault\":0,\"params\":{},\"phone\":\"1\",\"province\":\"福建省\",\"updateTime\":\"2026-01-07 15:38:51\",\"userId\":1} ', NULL, 1, '手机号格式不正确', '2026-01-07 17:34:24', 9);
INSERT INTO `sys_oper_log` VALUES (156, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"admin\",\"createTime\":\"2026-01-07 15:38:36\",\"detail\":\"登龙路99号\",\"district\":\"马尾区\",\"id\":1,\"isDefault\":0,\"params\":{},\"phone\":\"16999999999\",\"province\":\"福建省\",\"updateTime\":\"2026-01-07 17:34:31\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-07 17:34:31', 20);
INSERT INTO `sys_oper_log` VALUES (157, '菜单管理', 1, 'com.auto.web.controller.system.SysMenuController.add()', 'POST', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"商品管理\",\"menuType\":\"M\",\"orderNum\":2,\"params\":{},\"parentId\":0,\"path\":\"protucd\",\"status\":\"0\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-08 15:50:25', 36);
INSERT INTO `sys_oper_log` VALUES (158, '菜单管理', 1, 'com.auto.web.controller.system.SysMenuController.add()', 'POST', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"product/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"商品管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2012,\"path\":\"product\",\"status\":\"0\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-08 15:53:24', 21);
INSERT INTO `sys_oper_log` VALUES (159, '菜单管理', 2, 'com.auto.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"product/product/index\",\"createTime\":\"2026-01-08 15:53:24\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2013,\"menuName\":\"商品管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2012,\"path\":\"product\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-08 16:00:14', 18);
INSERT INTO `sys_oper_log` VALUES (160, '菜单管理', 1, 'com.auto.web.controller.system.SysMenuController.add()', 'POST', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"category/category/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"商品分类管理\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":0,\"path\":\"category\",\"status\":\"0\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-08 16:15:54', 21);
INSERT INTO `sys_oper_log` VALUES (161, '菜单管理', 1, 'com.auto.web.controller.system.SysMenuController.add()', 'POST', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"门店管理\",\"menuType\":\"M\",\"orderNum\":5,\"params\":{},\"parentId\":0,\"path\":\"store\",\"status\":\"0\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-10 15:40:27', 40);
INSERT INTO `sys_oper_log` VALUES (162, '菜单管理', 1, 'com.auto.web.controller.system.SysMenuController.add()', 'POST', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"store/store/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"门店管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2015,\"path\":\"store\",\"status\":\"0\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-10 15:41:48', 16);
INSERT INTO `sys_oper_log` VALUES (163, '菜单管理', 1, 'com.auto.web.controller.system.SysMenuController.add()', 'POST', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"cart/cartindex\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"购物车\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":0,\"path\":\"cart\",\"status\":\"0\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-10 17:18:28', 34);
INSERT INTO `sys_oper_log` VALUES (164, '菜单管理', 2, 'com.auto.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"cart/cart/index\",\"createTime\":\"2026-01-10 17:18:28\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2017,\"menuName\":\"购物车\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":0,\"path\":\"cart\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-10 17:18:57', 20);
INSERT INTO `sys_oper_log` VALUES (165, '菜单管理', 1, 'com.auto.web.controller.system.SysMenuController.add()', 'POST', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"product/product/userIndex\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"商品\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":0,\"path\":\"product\",\"status\":\"0\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-10 17:36:08', 41);
INSERT INTO `sys_oper_log` VALUES (166, '菜单管理', 1, 'com.auto.web.controller.system.SysMenuController.add()', 'POST', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"order/order/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"订单\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":0,\"path\":\"order\",\"status\":\"0\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-10 18:19:09', 37);
INSERT INTO `sys_oper_log` VALUES (167, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/2019', '127.0.0.1', '内网IP', '2019 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-10 20:36:37', 41);
INSERT INTO `sys_oper_log` VALUES (168, '菜单管理', 1, 'com.auto.web.controller.system.SysMenuController.add()', 'POST', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"order/order/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"订单\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":0,\"path\":\"order\",\"status\":\"0\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-10 20:37:23', 35);
INSERT INTO `sys_oper_log` VALUES (169, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/2020', '127.0.0.1', '内网IP', '2020 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-10 20:59:50', 16);
INSERT INTO `sys_oper_log` VALUES (170, '收货地址', 1, 'com.auto.web.controller.client.UserAddressController.add()', 'POST', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"1\",\"detail\":\"1\",\"district\":\"马尾区\",\"isDefault\":0,\"params\":{},\"phone\":\"11111111111\",\"province\":\"福建省\"} ', NULL, 1, '手机号格式不正确', '2026-01-10 21:39:00', 17);
INSERT INTO `sys_oper_log` VALUES (171, '收货地址', 1, 'com.auto.web.controller.client.UserAddressController.add()', 'POST', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"1\",\"detail\":\"1\",\"district\":\"马尾区\",\"isDefault\":0,\"params\":{},\"phone\":\"12111111111\",\"province\":\"福建省\"} ', NULL, 1, '手机号格式不正确', '2026-01-10 21:39:03', 1);
INSERT INTO `sys_oper_log` VALUES (172, '收货地址', 1, 'com.auto.web.controller.client.UserAddressController.add()', 'POST', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"1\",\"detail\":\"1\",\"district\":\"马尾区\",\"isDefault\":0,\"params\":{},\"phone\":\"121111111111\",\"province\":\"福建省\"} ', NULL, 1, '手机号格式不正确', '2026-01-10 21:39:06', 0);
INSERT INTO `sys_oper_log` VALUES (173, '收货地址', 1, 'com.auto.web.controller.client.UserAddressController.add()', 'POST', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"1\",\"detail\":\"1\",\"district\":\"马尾区\",\"isDefault\":0,\"params\":{},\"phone\":\"12111111111\",\"province\":\"福建省\"} ', NULL, 1, '手机号格式不正确', '2026-01-10 21:39:09', 1);
INSERT INTO `sys_oper_log` VALUES (174, '收货地址', 1, 'com.auto.web.controller.client.UserAddressController.add()', 'POST', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"1\",\"detail\":\"1\",\"district\":\"马尾区\",\"isDefault\":0,\"params\":{},\"phone\":\"1211111111\",\"province\":\"福建省\"} ', NULL, 1, '手机号格式不正确', '2026-01-10 21:39:12', 0);
INSERT INTO `sys_oper_log` VALUES (175, '收货地址', 1, 'com.auto.web.controller.client.UserAddressController.add()', 'POST', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"1\",\"createTime\":\"2026-01-10 21:39:24\",\"detail\":\"1\",\"district\":\"马尾区\",\"id\":3,\"isDefault\":0,\"params\":{},\"phone\":\"13388888888\",\"province\":\"福建省\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-10 21:39:25', 11);
INSERT INTO `sys_oper_log` VALUES (176, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"admin\",\"createTime\":\"2026-01-07 15:38:36\",\"detail\":\"登龙路99号\",\"district\":\"马尾区\",\"id\":1,\"isDefault\":0,\"params\":{},\"phone\":\"16999999999\",\"province\":\"福建省\",\"updateTime\":\"2026-01-10 21:44:06\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-10 21:44:06', 9);
INSERT INTO `sys_oper_log` VALUES (177, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"admin\",\"createTime\":\"2026-01-07 15:38:36\",\"detail\":\"登龙路99号\",\"district\":\"马尾区\",\"id\":1,\"isDefault\":1,\"params\":{},\"phone\":\"16999999999\",\"province\":\"福建省\",\"updateTime\":\"2026-01-10 21:45:34\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-10 21:45:34', 7);
INSERT INTO `sys_oper_log` VALUES (178, '菜单管理', 1, 'com.auto.web.controller.system.SysMenuController.add()', 'POST', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"favorite/favorite/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"收藏\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":0,\"path\":\"favorite\",\"status\":\"0\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-10 22:11:33', 150);
INSERT INTO `sys_oper_log` VALUES (179, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"admin\",\"createTime\":\"2026-01-07 15:38:36\",\"detail\":\"登龙路99号\",\"district\":\"马尾区\",\"id\":1,\"isDefault\":1,\"params\":{},\"phone\":\"16999999999\",\"province\":\"福建省\",\"updateTime\":\"2026-01-12 13:28:33\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-12 13:28:33', 19);
INSERT INTO `sys_oper_log` VALUES (180, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"1\",\"createTime\":\"2026-01-10 21:39:25\",\"detail\":\"1\",\"district\":\"马尾区\",\"id\":3,\"isDefault\":0,\"params\":{},\"phone\":\"13388888888\",\"province\":\"福建省\",\"updateTime\":\"2026-01-12 13:28:33\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-12 13:28:33', 19);
INSERT INTO `sys_oper_log` VALUES (181, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"admin\",\"createTime\":\"2026-01-07 15:38:36\",\"detail\":\"登龙路99号\",\"district\":\"马尾区\",\"id\":1,\"isDefault\":0,\"params\":{},\"phone\":\"16999999999\",\"province\":\"福建省\",\"updateTime\":\"2026-01-12 13:28:35\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-12 13:28:35', 9);
INSERT INTO `sys_oper_log` VALUES (182, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"1\",\"createTime\":\"2026-01-10 21:39:25\",\"detail\":\"1\",\"district\":\"马尾区\",\"id\":3,\"isDefault\":1,\"params\":{},\"phone\":\"13388888888\",\"province\":\"福建省\",\"updateTime\":\"2026-01-12 13:28:35\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-12 13:28:35', 7);
INSERT INTO `sys_oper_log` VALUES (183, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"1\",\"createTime\":\"2026-01-10 21:39:25\",\"detail\":\"1\",\"district\":\"马尾区\",\"id\":3,\"isDefault\":0,\"params\":{},\"phone\":\"13388888888\",\"province\":\"福建省\",\"updateTime\":\"2026-01-12 13:28:48\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-12 13:28:48', 8);
INSERT INTO `sys_oper_log` VALUES (184, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"admin\",\"createTime\":\"2026-01-07 15:38:36\",\"detail\":\"登龙路99号\",\"district\":\"马尾区\",\"id\":1,\"isDefault\":1,\"params\":{},\"phone\":\"16999999999\",\"province\":\"福建省\",\"updateTime\":\"2026-01-12 13:28:48\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-12 13:28:48', 6);
INSERT INTO `sys_oper_log` VALUES (185, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"admin\",\"createTime\":\"2026-01-07 15:38:36\",\"detail\":\"登龙路99号\",\"district\":\"马尾区\",\"id\":1,\"isDefault\":0,\"params\":{},\"phone\":\"16999999999\",\"province\":\"福建省\",\"updateTime\":\"2026-01-12 13:30:34\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-12 13:30:34', 7);
INSERT INTO `sys_oper_log` VALUES (186, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"admin\",\"createTime\":\"2026-01-07 15:38:36\",\"detail\":\"登龙路99号\",\"district\":\"马尾区\",\"id\":1,\"isDefault\":1,\"params\":{},\"phone\":\"16999999999\",\"province\":\"福建省\",\"updateTime\":\"2026-01-12 13:30:42\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-12 13:30:42', 8);
INSERT INTO `sys_oper_log` VALUES (187, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"1\",\"createTime\":\"2026-01-10 21:39:25\",\"detail\":\"1\",\"district\":\"马尾区\",\"id\":3,\"isDefault\":1,\"params\":{},\"phone\":\"13388888888\",\"province\":\"福建省\",\"updateTime\":\"2026-01-12 13:30:43\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-12 13:30:43', 7);
INSERT INTO `sys_oper_log` VALUES (188, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"admin\",\"createTime\":\"2026-01-07 15:38:36\",\"detail\":\"登龙路99号\",\"district\":\"马尾区\",\"id\":1,\"isDefault\":0,\"params\":{},\"phone\":\"16999999999\",\"province\":\"福建省\",\"updateTime\":\"2026-01-12 13:30:43\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-12 13:30:43', 18);
INSERT INTO `sys_oper_log` VALUES (189, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"admin\",\"createTime\":\"2026-01-07 15:38:36\",\"detail\":\"登龙路99号\",\"district\":\"马尾区\",\"id\":1,\"isDefault\":1,\"params\":{},\"phone\":\"16999999999\",\"province\":\"福建省\",\"updateTime\":\"2026-01-12 13:30:49\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-12 13:30:49', 8);
INSERT INTO `sys_oper_log` VALUES (190, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"1\",\"createTime\":\"2026-01-10 21:39:25\",\"detail\":\"1\",\"district\":\"马尾区\",\"id\":3,\"isDefault\":0,\"params\":{},\"phone\":\"13388888888\",\"province\":\"福建省\",\"updateTime\":\"2026-01-12 13:30:49\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-12 13:30:49', 5);
INSERT INTO `sys_oper_log` VALUES (191, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"1\",\"createTime\":\"2026-01-10 21:39:25\",\"detail\":\"1\",\"district\":\"马尾区\",\"id\":3,\"isDefault\":1,\"params\":{},\"phone\":\"13388888888\",\"province\":\"福建省\",\"updateTime\":\"2026-01-12 13:37:09\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-12 13:37:09', 35);
INSERT INTO `sys_oper_log` VALUES (192, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"1\",\"createTime\":\"2026-01-10 21:39:25\",\"detail\":\"1\",\"district\":\"马尾区\",\"id\":3,\"isDefault\":0,\"params\":{},\"phone\":\"13388888888\",\"province\":\"福建省\",\"updateTime\":\"2026-01-12 13:37:16\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-12 13:37:16', 11);
INSERT INTO `sys_oper_log` VALUES (193, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"admin\",\"createTime\":\"2026-01-07 15:38:36\",\"detail\":\"登龙路99号\",\"district\":\"马尾区\",\"id\":1,\"isDefault\":1,\"params\":{},\"phone\":\"16999999999\",\"province\":\"福建省\",\"updateTime\":\"2026-01-12 13:37:20\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-12 13:37:20', 23);
INSERT INTO `sys_oper_log` VALUES (194, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'admin', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"福州市\",\"consignee\":\"1\",\"createTime\":\"2026-01-10 21:39:25\",\"detail\":\"1\",\"district\":\"马尾区\",\"id\":3,\"isDefault\":1,\"params\":{},\"phone\":\"13388888888\",\"province\":\"福建省\",\"updateTime\":\"2026-01-12 13:37:24\",\"userId\":1} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-12 13:37:24', 23);
INSERT INTO `sys_oper_log` VALUES (195, '菜单管理', 1, 'com.auto.web.controller.system.SysMenuController.add()', 'POST', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"order/orderAdmin/orderAdmin\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"订单管理\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":0,\"path\":\"orderAdmin\",\"status\":\"0\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-14 17:37:12', 33);
INSERT INTO `sys_oper_log` VALUES (196, '角色管理', 2, 'com.auto.web.controller.system.SysRoleController.edit()', 'PUT', 1, 'admin', NULL, '/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2025-12-15 15:32:10\",\"dataScope\":\"2\",\"delFlag\":\"0\",\"deptCheckStrictly\":false,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2006,2007,2008,2009,2010,2011,2021,2017,2018],\"params\":{},\"remark\":\"普通角色\",\"roleId\":2,\"roleKey\":\"common\",\"roleName\":\"普通角色\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-14 20:21:17', 38);
INSERT INTO `sys_oper_log` VALUES (197, '收货地址', 1, 'com.auto.web.controller.client.UserAddressController.add()', 'POST', 1, 'cs', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"市辖区\",\"consignee\":\"测试\",\"createTime\":\"2026-01-15 21:27:45\",\"detail\":\"1号\",\"district\":\"东城区\",\"id\":4,\"isDefault\":0,\"params\":{},\"phone\":\"15666666666\",\"province\":\"北京市\",\"userId\":106} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-15 21:27:45', 33);
INSERT INTO `sys_oper_log` VALUES (198, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'cs', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"市辖区\",\"consignee\":\"测试\",\"createTime\":\"2026-01-15 21:27:45\",\"detail\":\"1号\",\"district\":\"东城区\",\"id\":4,\"isDefault\":1,\"params\":{},\"phone\":\"15666666666\",\"province\":\"北京市\",\"updateTime\":\"2026-01-15 21:27:46\",\"userId\":106} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-15 21:27:46', 19);
INSERT INTO `sys_oper_log` VALUES (199, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'cs', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"市辖区\",\"consignee\":\"测试\",\"createTime\":\"2026-01-15 21:27:45\",\"detail\":\"1号\",\"district\":\"东城区\",\"id\":4,\"isDefault\":0,\"params\":{},\"phone\":\"15666666666\",\"province\":\"北京市\",\"updateTime\":\"2026-01-15 21:27:49\",\"userId\":106} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-15 21:27:49', 13);
INSERT INTO `sys_oper_log` VALUES (200, '收货地址', 2, 'com.auto.web.controller.client.UserAddressController.edit()', 'PUT', 1, 'cs', NULL, '/address/address', '127.0.0.1', '内网IP', '{\"city\":\"市辖区\",\"consignee\":\"测试\",\"createTime\":\"2026-01-15 21:27:45\",\"detail\":\"1号\",\"district\":\"东城区\",\"id\":4,\"isDefault\":1,\"params\":{},\"phone\":\"15666666666\",\"province\":\"北京市\",\"updateTime\":\"2026-01-15 21:27:50\",\"userId\":106} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-15 21:27:50', 14);
INSERT INTO `sys_oper_log` VALUES (201, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/2018', '127.0.0.1', '内网IP', '2018 ', '{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}', 0, NULL, '2026-01-16 15:47:56', 19);
INSERT INTO `sys_oper_log` VALUES (202, '角色管理', 2, 'com.auto.web.controller.system.SysRoleController.edit()', 'PUT', 1, 'admin', NULL, '/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2025-12-15 15:32:10\",\"dataScope\":\"2\",\"delFlag\":\"0\",\"deptCheckStrictly\":false,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[],\"params\":{},\"remark\":\"普通角色\",\"roleId\":2,\"roleKey\":\"common\",\"roleName\":\"普通角色\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:48:12', 21);
INSERT INTO `sys_oper_log` VALUES (203, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/2018', '127.0.0.1', '内网IP', '2018 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:48:58', 13);
INSERT INTO `sys_oper_log` VALUES (204, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/2017', '127.0.0.1', '内网IP', '2017 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:49:01', 11);
INSERT INTO `sys_oper_log` VALUES (205, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/2021', '127.0.0.1', '内网IP', '2021 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:49:05', 11);
INSERT INTO `sys_oper_log` VALUES (206, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/2006', '127.0.0.1', '内网IP', '2006 ', '{\"msg\":\"存在子菜单,不允许删除\",\"code\":601}', 0, NULL, '2026-01-16 15:49:26', 3);
INSERT INTO `sys_oper_log` VALUES (207, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/2007', '127.0.0.1', '内网IP', '2007 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:49:33', 14);
INSERT INTO `sys_oper_log` VALUES (208, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/2008', '127.0.0.1', '内网IP', '2008 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:49:35', 14);
INSERT INTO `sys_oper_log` VALUES (209, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/2009', '127.0.0.1', '内网IP', '2009 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:49:37', 9);
INSERT INTO `sys_oper_log` VALUES (210, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/2010', '127.0.0.1', '内网IP', '2010 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:49:39', 11);
INSERT INTO `sys_oper_log` VALUES (211, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/2011', '127.0.0.1', '内网IP', '2011 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:49:42', 9);
INSERT INTO `sys_oper_log` VALUES (212, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/2006', '127.0.0.1', '内网IP', '2006 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:49:45', 10);
INSERT INTO `sys_oper_log` VALUES (213, '菜单管理', 2, 'com.auto.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"product/product/index\",\"createTime\":\"2026-01-08 15:53:24\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2013,\"menuName\":\"商品管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":0,\"path\":\"product\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ', '{\"msg\":\"修改菜单\'商品管理\'失败，菜单名称已存在\",\"code\":500}', 0, NULL, '2026-01-16 15:50:52', 29);
INSERT INTO `sys_oper_log` VALUES (214, '菜单管理', 2, 'com.auto.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2026-01-08 15:50:24\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2012,\"menuName\":\"商品管\",\"menuType\":\"M\",\"orderNum\":2,\"params\":{},\"parentId\":0,\"path\":\"protucd\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:51:05', 13);
INSERT INTO `sys_oper_log` VALUES (215, '菜单管理', 2, 'com.auto.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"product/product/index\",\"createTime\":\"2026-01-08 15:53:24\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2013,\"menuName\":\"商品管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":0,\"path\":\"product\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:51:14', 12);
INSERT INTO `sys_oper_log` VALUES (216, '菜单管理', 2, 'com.auto.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"product/product/index\",\"createTime\":\"2026-01-08 15:53:24\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2013,\"menuName\":\"商品管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":0,\"path\":\"product\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:51:39', 15);
INSERT INTO `sys_oper_log` VALUES (217, '菜单管理', 1, 'com.auto.web.controller.system.SysMenuController.add()', 'POST', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"product/product/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"商品管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2012,\"path\":\"product\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:53:20', 16);
INSERT INTO `sys_oper_log` VALUES (218, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/2013', '127.0.0.1', '内网IP', '2013 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:53:45', 15);
INSERT INTO `sys_oper_log` VALUES (219, '菜单管理', 1, 'com.auto.web.controller.system.SysMenuController.add()', 'POST', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"product/product/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"1\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":0,\"path\":\"product\",\"status\":\"0\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:54:05', 12);
INSERT INTO `sys_oper_log` VALUES (220, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/2024', '127.0.0.1', '内网IP', '2024 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:54:14', 11);
INSERT INTO `sys_oper_log` VALUES (221, '菜单管理', 2, 'com.auto.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2026-01-08 15:50:24\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2012,\"menuName\":\"商品管理\",\"menuType\":\"M\",\"orderNum\":2,\"params\":{},\"parentId\":0,\"path\":\"protucd\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:54:41', 12);
INSERT INTO `sys_oper_log` VALUES (222, '菜单管理', 2, 'com.auto.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2026-01-08 15:50:24\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2012,\"menuName\":\"商品管理\",\"menuType\":\"M\",\"orderNum\":4,\"params\":{},\"parentId\":0,\"path\":\"protucd\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:55:02', 11);
INSERT INTO `sys_oper_log` VALUES (223, '菜单管理', 2, 'com.auto.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"category/category/index\",\"createTime\":\"2026-01-08 16:15:54\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2014,\"menuName\":\"商品分类管理\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":2012,\"path\":\"category\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:55:32', 20);
INSERT INTO `sys_oper_log` VALUES (224, '菜单管理', 2, 'com.auto.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', NULL, '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"order/orderAdmin/orderAdmin\",\"createTime\":\"2026-01-14 17:37:12\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2022,\"menuName\":\"订单管理\",\"menuType\":\"C\",\"orderNum\":6,\"params\":{},\"parentId\":0,\"path\":\"orderAdmin\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 15:56:05', 18);
INSERT INTO `sys_oper_log` VALUES (225, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/106', '127.0.0.1', '内网IP', '106 ', '{\"msg\":\"存在子菜单,不允许删除\",\"code\":601}', 0, NULL, '2026-01-16 16:06:41', 4);
INSERT INTO `sys_oper_log` VALUES (226, '参数管理', 2, 'com.auto.web.controller.system.SysConfigController.edit()', 'PUT', 1, 'admin', NULL, '/system/config', '127.0.0.1', '内网IP', '{\"configId\":1,\"configKey\":\"sys.index.skinName\",\"configName\":\"主框架页-默认皮肤样式名称\",\"configType\":\"Y\",\"configValue\":\"skin-red\",\"createBy\":\"admin\",\"createTime\":\"2025-12-15 15:32:11\",\"params\":{},\"remark\":\"蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:07:33', 29);
INSERT INTO `sys_oper_log` VALUES (227, '参数管理', 2, 'com.auto.web.controller.system.SysConfigController.edit()', 'PUT', 1, 'admin', NULL, '/system/config', '127.0.0.1', '内网IP', '{\"configId\":1,\"configKey\":\"sys.index.skinName\",\"configName\":\"主框架页-默认皮肤样式名称\",\"configType\":\"Y\",\"configValue\":\"skin-blue\",\"createBy\":\"admin\",\"createTime\":\"2025-12-15 15:32:11\",\"params\":{},\"remark\":\"蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow\",\"updateBy\":\"admin\",\"updateTime\":\"2026-01-16 16:07:33\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:07:54', 13);
INSERT INTO `sys_oper_log` VALUES (228, '参数管理', 2, 'com.auto.web.controller.system.SysConfigController.edit()', 'PUT', 1, 'admin', NULL, '/system/config', '127.0.0.1', '内网IP', '{\"configId\":3,\"configKey\":\"sys.index.sideTheme\",\"configName\":\"主框架页-侧边栏主题\",\"configType\":\"Y\",\"configValue\":\"theme-light\",\"createBy\":\"admin\",\"createTime\":\"2025-12-15 15:32:11\",\"params\":{},\"remark\":\"深色主题theme-dark，浅色主题theme-light\",\"updateBy\":\"admin\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:08:12', 14);
INSERT INTO `sys_oper_log` VALUES (229, '参数管理', 2, 'com.auto.web.controller.system.SysConfigController.edit()', 'PUT', 1, 'admin', NULL, '/system/config', '127.0.0.1', '内网IP', '{\"configId\":3,\"configKey\":\"sys.index.sideTheme\",\"configName\":\"主框架页-侧边栏主题\",\"configType\":\"Y\",\"configValue\":\"theme-dark\",\"createBy\":\"admin\",\"createTime\":\"2025-12-15 15:32:11\",\"params\":{},\"remark\":\"深色主题theme-dark，浅色主题theme-light\",\"updateBy\":\"admin\",\"updateTime\":\"2026-01-16 16:08:12\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:08:24', 15);
INSERT INTO `sys_oper_log` VALUES (230, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1029', '127.0.0.1', '内网IP', '1029 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:09:36', 12);
INSERT INTO `sys_oper_log` VALUES (231, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1028', '127.0.0.1', '内网IP', '1028 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:09:38', 11);
INSERT INTO `sys_oper_log` VALUES (232, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1027', '127.0.0.1', '内网IP', '1027 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:09:41', 12);
INSERT INTO `sys_oper_log` VALUES (233, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1026', '127.0.0.1', '内网IP', '1026 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:09:45', 11);
INSERT INTO `sys_oper_log` VALUES (234, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1025', '127.0.0.1', '内网IP', '1025 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:09:48', 10);
INSERT INTO `sys_oper_log` VALUES (235, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/105', '127.0.0.1', '内网IP', '105 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:09:50', 12);
INSERT INTO `sys_oper_log` VALUES (236, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1030', '127.0.0.1', '内网IP', '1030 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:09:54', 11);
INSERT INTO `sys_oper_log` VALUES (237, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1031', '127.0.0.1', '内网IP', '1031 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:09:57', 10);
INSERT INTO `sys_oper_log` VALUES (238, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1033', '127.0.0.1', '内网IP', '1033 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:09:59', 15);
INSERT INTO `sys_oper_log` VALUES (239, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1032', '127.0.0.1', '内网IP', '1032 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:10:01', 15);
INSERT INTO `sys_oper_log` VALUES (240, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1034', '127.0.0.1', '内网IP', '1034 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:10:03', 10);
INSERT INTO `sys_oper_log` VALUES (241, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/106', '127.0.0.1', '内网IP', '106 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:10:05', 13);
INSERT INTO `sys_oper_log` VALUES (242, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1035', '127.0.0.1', '内网IP', '1035 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:10:12', 10);
INSERT INTO `sys_oper_log` VALUES (243, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1036', '127.0.0.1', '内网IP', '1036 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:10:14', 11);
INSERT INTO `sys_oper_log` VALUES (244, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1037', '127.0.0.1', '内网IP', '1037 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:10:16', 10);
INSERT INTO `sys_oper_log` VALUES (245, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1038', '127.0.0.1', '内网IP', '1038 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:10:18', 14);
INSERT INTO `sys_oper_log` VALUES (246, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/107', '127.0.0.1', '内网IP', '107 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:10:21', 14);
INSERT INTO `sys_oper_log` VALUES (247, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1049', '127.0.0.1', '内网IP', '1049 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:10:48', 11);
INSERT INTO `sys_oper_log` VALUES (248, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1050', '127.0.0.1', '内网IP', '1050 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:10:50', 12);
INSERT INTO `sys_oper_log` VALUES (249, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1052', '127.0.0.1', '内网IP', '1052 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:10:52', 10);
INSERT INTO `sys_oper_log` VALUES (250, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1053', '127.0.0.1', '内网IP', '1053 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:10:54', 11);
INSERT INTO `sys_oper_log` VALUES (251, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1051', '127.0.0.1', '内网IP', '1051 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:10:56', 11);
INSERT INTO `sys_oper_log` VALUES (252, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1054', '127.0.0.1', '内网IP', '1054 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:10:58', 11);
INSERT INTO `sys_oper_log` VALUES (253, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/110', '127.0.0.1', '内网IP', '110 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:11:01', 10);
INSERT INTO `sys_oper_log` VALUES (254, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/115', '127.0.0.1', '内网IP', '115 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:11:05', 12);
INSERT INTO `sys_oper_log` VALUES (255, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1055', '127.0.0.1', '内网IP', '1055 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:11:09', 11);
INSERT INTO `sys_oper_log` VALUES (256, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1056', '127.0.0.1', '内网IP', '1056 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:11:11', 11);
INSERT INTO `sys_oper_log` VALUES (257, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1057', '127.0.0.1', '内网IP', '1057 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:11:13', 14);
INSERT INTO `sys_oper_log` VALUES (258, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1058', '127.0.0.1', '内网IP', '1058 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:11:15', 11);
INSERT INTO `sys_oper_log` VALUES (259, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1059', '127.0.0.1', '内网IP', '1059 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:11:18', 14);
INSERT INTO `sys_oper_log` VALUES (260, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/1060', '127.0.0.1', '内网IP', '1060 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:11:21', 11);
INSERT INTO `sys_oper_log` VALUES (261, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/116', '127.0.0.1', '内网IP', '116 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:11:25', 11);
INSERT INTO `sys_oper_log` VALUES (262, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/117', '127.0.0.1', '内网IP', '117 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:11:27', 13);
INSERT INTO `sys_oper_log` VALUES (263, '菜单管理', 3, 'com.auto.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', NULL, '/system/menu/3', '127.0.0.1', '内网IP', '3 ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:11:29', 14);
INSERT INTO `sys_oper_log` VALUES (264, '个人信息', 2, 'com.auto.web.controller.system.SysProfileController.updateProfile()', 'PUT', 1, 'cs', NULL, '/system/user/profile', '127.0.0.1', '内网IP', '{\"admin\":false,\"email\":\"168@168.com\",\"nickName\":\"cs\",\"params\":{},\"phonenumber\":\"16888888888\",\"sex\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:23:04', 24);
INSERT INTO `sys_oper_log` VALUES (265, '个人信息', 2, 'com.auto.web.controller.system.SysProfileController.updateProfile()', 'PUT', 1, 'cs', NULL, '/system/user/profile', '127.0.0.1', '内网IP', '{\"admin\":false,\"email\":\"168@168.com\",\"nickName\":\"测试员\",\"params\":{},\"phonenumber\":\"16888888888\",\"sex\":\"0\"} ', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-16 16:23:32', 17);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '部门树选择项是否关联显示',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2025-12-15 15:32:10', '', NULL, '超级管理员');
INSERT INTO `sys_role` VALUES (2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', '2025-12-15 15:32:10', 'admin', '2026-01-16 15:48:12', '普通角色');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '密码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime NULL DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 107 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '超级管理', '00', 'auto@163.com', '15888888888', '0', '/profile/avatar/2025/12/26/589f3d8c9fd94340932851729455275f.jpg', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2026-01-16 16:29:34', '2025-12-15 15:32:10', 'admin', '2025-12-15 15:32:10', '', '2025-12-26 23:51:17', '管理员');
INSERT INTO `sys_user` VALUES (2, 'test', '测试', '00', 'auto@qq.com', '15666666666', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2025-12-15 15:32:10', '2025-12-15 15:32:10', 'admin', '2025-12-15 15:32:10', '', NULL, '测试员');
INSERT INTO `sys_user` VALUES (100, 'test1', 'test1', '00', '', '', '0', '', '$2a$10$nnKp4eakPiLOBID4.yUW3uLcAgxwfruoMNvFi8OOwReh2VJ2o7t0K', '0', '0', '127.0.0.1', '2025-12-26 21:03:04', '2025-12-26 21:01:19', '', '2025-12-26 21:01:19', '', NULL, NULL);
INSERT INTO `sys_user` VALUES (101, 'admin1', 'admin1', '00', '', '', '0', '', '$2a$10$R182qtdihzLKTy6MiACtP.7lV8TzEOF5Vw9FgKcQYiaZOR4KMwpWW', '0', '0', '', NULL, NULL, 'admin', '2026-01-06 23:56:43', '', NULL, NULL);
INSERT INTO `sys_user` VALUES (102, '123', '123', '00', '', '', '0', '', '$2a$10$iCWgte45CDi83qUCqs0pauSjbLwADFGRx5seKzLPXsHn7ehPsmLTa', '0', '0', '127.0.0.1', '2026-01-07 13:31:59', '2026-01-07 13:31:49', '', '2026-01-07 13:31:49', '', NULL, NULL);
INSERT INTO `sys_user` VALUES (103, '111', '111', '00', '', '', '0', '', '$2a$10$fYnfFwxbe9gzyGlF9oIinuU37W.09Su6uQ83t.UqAWqK0MKhyrrcG', '0', '0', '', NULL, '2026-01-07 13:45:58', '', '2026-01-07 13:45:58', '', NULL, NULL);
INSERT INTO `sys_user` VALUES (104, '222', '222', '00', '', '', '0', '', '$2a$10$IHZhcRZulevS4DYW7CpVUuJ2bHaOqiRzL.Mg0Hwf1Qy6j4tCC1npO', '0', '0', '', NULL, '2026-01-07 13:49:49', '', '2026-01-07 13:50:00', '', NULL, NULL);
INSERT INTO `sys_user` VALUES (105, '333', '333', '00', '', '', '0', '', '$2a$10$ihlV/2DuXcPoPGasB3kUl.Vm0Yh6b52ScJWXNX05b9Ie7zGrh88Pa', '0', '0', '127.0.0.1', '2026-01-07 13:53:41', '2026-01-07 13:53:31', '', '2026-01-07 13:53:30', '', NULL, NULL);
INSERT INTO `sys_user` VALUES (106, 'cs', '测试员', '00', '168@168.com', '16888888888', '0', '', '$2a$10$htrBMQjiqWj9gxObn5BfguUWKda/EvUYlYI.pvnLkEJ/Vi80.2jHq', '0', '0', '127.0.0.1', '2026-01-16 16:31:19', '2026-01-15 20:51:16', '', '2026-01-15 20:51:16', '', '2026-01-16 16:23:32', NULL);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户和角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);
INSERT INTO `sys_user_role` VALUES (102, 2);
INSERT INTO `sys_user_role` VALUES (105, 2);
INSERT INTO `sys_user_role` VALUES (106, 2);

-- ----------------------------
-- Table structure for user_address
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `consignee` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区/县',
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint(1) NULL DEFAULT 0 COMMENT '是否默认地址（0否，1是）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收货地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_address
-- ----------------------------
INSERT INTO `user_address` VALUES (1, 1, 'admin', '16999999999', '福建省', '福州市', '马尾区', '登龙路99号', 0, '2026-01-07 15:38:36', '2026-01-12 13:37:24');
INSERT INTO `user_address` VALUES (3, 1, '1', '13388888888', '福建省', '福州市', '马尾区', '1', 1, '2026-01-10 21:39:25', '2026-01-12 13:37:25');
INSERT INTO `user_address` VALUES (4, 106, '测试', '15666666666', '北京市', '市辖区', '东城区', '1号', 1, '2026-01-15 21:27:45', '2026-01-15 21:27:51');

SET FOREIGN_KEY_CHECKS = 1;
