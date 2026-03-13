/*
 Navicat Premium Dump SQL

 Source Server         : tengxun
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : 124.220.81.48:3306
 Source Schema         : pixgenius

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 16/12/2025 20:12:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sg_user
-- ----------------------------
DROP TABLE IF EXISTS `sg_user`;
CREATE TABLE `sg_user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录密码',
  `status` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '状态 0 正常  1超时锁定  2锁定  9无效 ',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `vip_level` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '会员等级',
  `deleted` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '逻辑删除',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `gender` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '性别',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1846118210353541123 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sg_user
-- ----------------------------
INSERT INTO `sg_user` VALUES (1846117794651930625, '2024-10-15 17:15:56', '2024-10-15 17:15:56', '1521030729', '$2a$10$ljMQlzvfmMkC0LCFils9DuBycP2xfXvoRodrhgJu1ywLmlqUS7B/W', NULL, 'admin', NULL, 0, 0, NULL, 'https://img1.baidu.com/it/u=1090452517,2487311686&fm=253&app=120&size=w931&n=0&f=JPEG&fmt=auto?sec=1729098000&t=bde85038085ca68a7e26aef57b0b6b2b', NULL);
INSERT INTO `sg_user` VALUES (1846118210353541122, '2024-10-15 17:17:35', '2024-10-15 17:17:35', '1521030727', '$2a$10$/9XitdLc5mfovmrQSMKkoOqeZERRPZNH1/xtN2Lb0ISoNrOV3GuIO', NULL, 'admin1', NULL, 0, 0, NULL, 'https://img1.baidu.com/it/u=1090452517,2487311686&fm=253&app=120&size=w931&n=0&f=JPEG&fmt=auto?sec=1729098000&t=bde85038085ca68a7e26aef57b0b6b2b', NULL);

-- ----------------------------
-- Table structure for sg_user_fund
-- ----------------------------
DROP TABLE IF EXISTS `sg_user_fund`;
CREATE TABLE `sg_user_fund`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `score` bigint UNSIGNED NULL DEFAULT NULL COMMENT '积分账户',
  `version` bigint UNSIGNED NULL DEFAULT NULL COMMENT '乐观锁',
  `freeze_score` bigint UNSIGNED NULL DEFAULT NULL COMMENT '积分冻结账户',
  `user_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1847221973004922883 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户账户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sg_user_fund
-- ----------------------------
INSERT INTO `sg_user_fund` VALUES (1, NULL, 103, 44, 100, 0);
INSERT INTO `sg_user_fund` VALUES (1847221973004922882, NULL, 100, 105, 97, 1846117794651930625);

-- ----------------------------
-- Table structure for sg_user_fund_record
-- ----------------------------
DROP TABLE IF EXISTS `sg_user_fund_record`;
CREATE TABLE `sg_user_fund_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `fund_type` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '账户类型0积分账户，1积分冻结账户',
  `money` int NULL DEFAULT NULL COMMENT '额度',
  `fund_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '账户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2000900778447339523 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '账户流水信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sg_user_fund_record
-- ----------------------------
INSERT INTO `sg_user_fund_record` VALUES (2000840036712972290, '2025-12-16 08:06:53', 1, -1, 1847221973004922882);
INSERT INTO `sg_user_fund_record` VALUES (2000840036729749505, '2025-12-16 08:06:53', 0, 1, 1);
INSERT INTO `sg_user_fund_record` VALUES (2000841414411816962, '2025-12-16 08:12:22', 1, -1, 1847221973004922882);
INSERT INTO `sg_user_fund_record` VALUES (2000841414525063169, '2025-12-16 08:12:22', 0, 1, 1);
INSERT INTO `sg_user_fund_record` VALUES (2000844528497987586, '2025-12-16 08:24:44', 1, -1, 1847221973004922882);
INSERT INTO `sg_user_fund_record` VALUES (2000844528535736321, '2025-12-16 08:24:44', 0, 1, 1);
INSERT INTO `sg_user_fund_record` VALUES (2000846558054580226, '2025-12-16 08:32:48', 1, -1, 1847221973004922882);
INSERT INTO `sg_user_fund_record` VALUES (2000846558134272001, '2025-12-16 08:32:48', 0, 1, 1);
INSERT INTO `sg_user_fund_record` VALUES (2000851247630385153, '2025-12-16 08:51:26', 1, -1, 1847221973004922882);
INSERT INTO `sg_user_fund_record` VALUES (2000851247659745281, '2025-12-16 08:51:26', 0, 1, 1);
INSERT INTO `sg_user_fund_record` VALUES (2000851770064502785, '2025-12-16 08:53:31', 1, -1, 1847221973004922882);
INSERT INTO `sg_user_fund_record` VALUES (2000851770093862914, '2025-12-16 08:53:31', 0, 1, 1);
INSERT INTO `sg_user_fund_record` VALUES (2000853055652233218, '2025-12-16 08:58:37', 1, -1, 1847221973004922882);
INSERT INTO `sg_user_fund_record` VALUES (2000853055698370562, '2025-12-16 08:58:37', 0, 1, 1);
INSERT INTO `sg_user_fund_record` VALUES (2000862716849815553, '2025-12-16 09:37:01', 1, -1, 1847221973004922882);
INSERT INTO `sg_user_fund_record` VALUES (2000862716921118721, '2025-12-16 09:37:01', 0, 1, 1);
INSERT INTO `sg_user_fund_record` VALUES (2000864034788212738, '2025-12-16 09:42:15', 1, -1, 1847221973004922882);
INSERT INTO `sg_user_fund_record` VALUES (2000864034813378561, '2025-12-16 09:42:15', 0, 1, 1);
INSERT INTO `sg_user_fund_record` VALUES (2000865977459802113, '2025-12-16 09:49:58', 1, -1, 1847221973004922882);
INSERT INTO `sg_user_fund_record` VALUES (2000865977476579330, '2025-12-16 09:49:58', 0, 1, 1);
INSERT INTO `sg_user_fund_record` VALUES (2000867414357049346, '2025-12-16 09:55:41', 1, -1, 1847221973004922882);
INSERT INTO `sg_user_fund_record` VALUES (2000867414411575297, '2025-12-16 09:55:41', 0, 1, 1);
INSERT INTO `sg_user_fund_record` VALUES (2000869814048395266, '2025-12-16 10:05:13', 1, -1, 1847221973004922882);
INSERT INTO `sg_user_fund_record` VALUES (2000869814102921217, '2025-12-16 10:05:13', 0, 1, 1);
INSERT INTO `sg_user_fund_record` VALUES (2000870539776233473, '2025-12-16 10:08:06', 1, -1, 1847221973004922882);
INSERT INTO `sg_user_fund_record` VALUES (2000870539793010689, '2025-12-16 10:08:06', 0, 1, 1);
INSERT INTO `sg_user_fund_record` VALUES (2000891981150556162, '2025-12-16 11:33:18', 1, -1, 1847221973004922882);
INSERT INTO `sg_user_fund_record` VALUES (2000891981217665025, '2025-12-16 11:33:18', 0, 1, 1);
INSERT INTO `sg_user_fund_record` VALUES (2000894091711746049, '2025-12-16 11:41:41', 1, -1, 1847221973004922882);
INSERT INTO `sg_user_fund_record` VALUES (2000894091757883394, '2025-12-16 11:41:41', 0, 1, 1);
INSERT INTO `sg_user_fund_record` VALUES (2000894742109884418, '2025-12-16 11:44:16', 1, -1, 1847221973004922882);
INSERT INTO `sg_user_fund_record` VALUES (2000894742135050241, '2025-12-16 11:44:16', 0, 1, 1);
INSERT INTO `sg_user_fund_record` VALUES (2000896217196269569, '2025-12-16 11:50:08', 1, -1, 1847221973004922882);
INSERT INTO `sg_user_fund_record` VALUES (2000896217225629698, '2025-12-16 11:50:08', 0, 1, 1);
INSERT INTO `sg_user_fund_record` VALUES (2000896734299426817, '2025-12-16 11:52:11', 1, -1, 1847221973004922882);
INSERT INTO `sg_user_fund_record` VALUES (2000896734320398337, '2025-12-16 11:52:11', 0, 1, 1);
INSERT INTO `sg_user_fund_record` VALUES (2000900778325704705, '2025-12-16 12:08:15', 1, -1, 1847221973004922882);
INSERT INTO `sg_user_fund_record` VALUES (2000900778447339522, '2025-12-16 12:08:15', 0, 1, 1);

-- ----------------------------
-- Table structure for sg_user_result
-- ----------------------------
DROP TABLE IF EXISTS `sg_user_result`;
CREATE TABLE `sg_user_result`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `user_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '用户',
  `collect` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '是否收藏',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2000900778178904066 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户图片生成表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sg_user_result
-- ----------------------------
INSERT INTO `sg_user_result` VALUES (1847191081372925954, NULL, 1846117794651930625, 0, 'http://localhost:8188/view?filename=ComfyUI_00353_.png&type=output&subfolder=');
INSERT INTO `sg_user_result` VALUES (1847202533613793281, NULL, 1846117794651930625, 0, 'http://localhost:8188/view?filename=ComfyUI_00355_.png&type=output&subfolder=');
INSERT INTO `sg_user_result` VALUES (1847202555428368386, NULL, 1846117794651930625, 0, 'http://localhost:8188/view?filename=ComfyUI_00356_.png&type=output&subfolder=');
INSERT INTO `sg_user_result` VALUES (1847202576966119425, NULL, 1846117794651930625, 0, 'http://localhost:8188/view?filename=ComfyUI_00357_.png&type=output&subfolder=');
INSERT INTO `sg_user_result` VALUES (1847204782389944321, NULL, 1846117794651930625, 0, 'http://localhost:8188/view?filename=ComfyUI_00358_.png&type=output&subfolder=');
INSERT INTO `sg_user_result` VALUES (1847204863704915970, NULL, 1846117794651930625, 0, 'http://localhost:8188/view?filename=ComfyUI_00359_.png&type=output&subfolder=');
INSERT INTO `sg_user_result` VALUES (1847204883824992257, NULL, 1846117794651930625, 0, 'http://localhost:8188/view?filename=ComfyUI_00360_.png&type=output&subfolder=');
INSERT INTO `sg_user_result` VALUES (1847204903458529282, NULL, 1846117794651930625, 0, 'http://localhost:8188/view?filename=ComfyUI_00361_.png&type=output&subfolder=');
INSERT INTO `sg_user_result` VALUES (1847222170141405185, NULL, 1846117794651930625, 0, 'http://localhost:8188/view?filename=ComfyUI_00362_.png&type=output&subfolder=');
INSERT INTO `sg_user_result` VALUES (1847222434978148353, NULL, 1846117794651930625, 0, 'http://localhost:8188/view?filename=ComfyUI_00363_.png&type=output&subfolder=');
INSERT INTO `sg_user_result` VALUES (1847222434978148354, NULL, 1846117794651930625, 0, 'http://localhost:8188/view?filename=ComfyUI_00364_.png&type=output&subfolder=');
INSERT INTO `sg_user_result` VALUES (1847476332351475713, NULL, 1846117794651930625, 0, 'http://localhost:8188/view?filename=ComfyUI_00380_.png&type=output&subfolder=');
INSERT INTO `sg_user_result` VALUES (1847476561712795649, NULL, 1846117794651930625, 0, 'http://localhost:8188/view?filename=ComfyUI_00381_.png&type=output&subfolder=');
INSERT INTO `sg_user_result` VALUES (1847480102720442369, '2024-10-19 11:29:16', 1846117794651930625, 0, 'http://localhost:8188/view?filename=ComfyUI_00382_.png&type=output&subfolder=');
INSERT INTO `sg_user_result` VALUES (1999429490050510849, '2025-12-12 18:41:53', 1846117794651930625, 0, 'http://localhost:8188/view?filename=ComfyUI_00021_.png&type=output&subfolder=');
INSERT INTO `sg_user_result` VALUES (1999437899470639106, '2025-12-12 19:15:18', 1846117794651930625, 0, 'http://localhost:8188/view?filename=ComfyUI_00022_.png&type=output&subfolder=');
INSERT INTO `sg_user_result` VALUES (1999440051228323842, '2025-12-12 19:23:51', 1846117794651930625, 0, 'http://localhost:8188/view?filename=ComfyUI_00023_.png&type=output&subfolder=');
INSERT INTO `sg_user_result` VALUES (1999443620778135553, '2025-12-12 19:38:02', 1846117794651930625, 0, 'http://192.168.100.129:8188/view?filename=ComfyUI_00024_.png&type=output&subfolder=');
INSERT INTO `sg_user_result` VALUES (2000501902858919937, '2025-12-15 17:43:16', 1846117794651930625, 0, 'http://192.168.100.129:8188/view?filename=ComfyUI_00028_.png&type=output&subfolder=');
INSERT INTO `sg_user_result` VALUES (2000603638995050497, '2025-12-16 00:27:32', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/2a/20251216/cfc32567/7ffd4e8c-eb58-42cc-b734-aa284d856af0-1.png?Expires=1766421813&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=YVusRK3toPzCAG3efbiTWGx%2BhKE%3D');
INSERT INTO `sg_user_result` VALUES (2000604312323448833, '2025-12-16 00:30:12', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/e7/20251216/cfc32567/5576a7ab-6b1b-4b75-b012-a88b747ece42-1.png?Expires=1766421974&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=rl7SGi%2BqswNu2bHoz8Ez79u%2BIJw%3D');
INSERT INTO `sg_user_result` VALUES (2000606069321527297, '2025-12-16 00:37:11', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/0e/20251216/cfc32567/052e7686-0c5b-4bfb-801f-34649ea846cd-1.png?Expires=1766422393&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=ygnP9mcZVvImM%2BnMTblX%2BA5Tki4%3D');
INSERT INTO `sg_user_result` VALUES (2000606144122744833, '2025-12-16 00:37:29', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/2b/20251216/cfc32567/6009687f-d0c7-45df-90b5-b7953ceb5adb-1.png?Expires=1766422411&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=7GHQp23Q7Ai09d9FE9eOwaI%2BnjU%3D');
INSERT INTO `sg_user_result` VALUES (2000606243473223681, '2025-12-16 00:37:53', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/a9/20251216/cfc32567/6afe50c8-1872-4647-ac19-a252424e8f8530.png?Expires=1766422434&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=NZaTzcKDbbA5wQ8b5OvSCDwoZfY%3D');
INSERT INTO `sg_user_result` VALUES (2000606300498980866, '2025-12-16 00:38:06', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/3f/20251216/cfc32567/d009d4c3-be19-440d-a4bd-82e955e2875030.png?Expires=1766422448&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=9FcF%2FtxHDOVPtqrhXWCGjUv31XM%3D');
INSERT INTO `sg_user_result` VALUES (2000607204958707713, '2025-12-16 00:41:42', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/fe/20251216/cfc32567/b6ab2ddb-8697-4d2b-aa7d-6e02c0ff53d3-1.png?Expires=1766422664&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=mA2poSSHxVdZVu%2BhcQJJW9ymS2Q%3D');
INSERT INTO `sg_user_result` VALUES (2000609567165538306, '2025-12-16 00:51:05', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/f5/20251216/cfc32567/1ae6df74-8476-459a-8056-5f258b0801e9-1.png?Expires=1766423218&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=3CKQbHftnihoXG7mFcoaDEheBxQ%3D');
INSERT INTO `sg_user_result` VALUES (2000609567228452865, '2025-12-16 00:51:05', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/0e/20251216/cfc32567/272cd76a-bebd-487a-a32c-5bc92cdcb718-1.png?Expires=1766423227&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=pzemQvewgRVfy5DdYtsPYNDcp30%3D');
INSERT INTO `sg_user_result` VALUES (2000611181280518146, '2025-12-16 00:57:30', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/db/20251216/cfc32567/2a057e03-4118-411d-b250-55d3d74ad2f8-1.png?Expires=1766423612&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=f1MUVNBU9iXf1hFavbVRnfMu3GU%3D');
INSERT INTO `sg_user_result` VALUES (2000616849685266433, '2025-12-16 01:20:01', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/ea/20251216/cfc32567/3eadad66-fdb3-4e35-89f0-e95da9039055-1.png?Expires=1766424963&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=4wuR1sfxlN46pOIzoV7y%2Fbx3f7s%3D');
INSERT INTO `sg_user_result` VALUES (2000619578990182402, '2025-12-16 01:30:52', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/75/20251216/cfc32567/1d8d4b28-e88e-4e98-819a-7bb3b2b7df7f-1.png?Expires=1766425614&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=Mp7%2BhCPw%2B%2B9NM21B8o7p0E%2Byfho%3D');
INSERT INTO `sg_user_result` VALUES (2000625944907661314, '2025-12-16 01:56:10', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/f2/20251216/cfc32567/6e33cff2-8d03-4b0a-8955-df105aa39c39-1.png?Expires=1766427132&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=PyqnWxGyn5TPBBRYwwlFSPlc3xk%3D');
INSERT INTO `sg_user_result` VALUES (2000631745386823681, '2025-12-16 02:19:13', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/59/20251216/cfc32567/83a00dd8-9736-482d-8a23-3ccdbf66c3c9-1.png?Expires=1766428514&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=CyFQEPLAHrGhObeCMIsuy0kFLos%3D');
INSERT INTO `sg_user_result` VALUES (2000643622099673090, '2025-12-16 03:06:24', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/9d/20251216/cfc32567/583e2525-6cb4-449e-b96c-d7bdb886134f-1.png?Expires=1766431346&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=ZuGV3a1SySD6sR3PQ06jgiTJjzQ%3D');
INSERT INTO `sg_user_result` VALUES (2000647196443549698, '2025-12-16 03:20:37', 1846117794651930625, 0, 'http://192.168.100.129:8188/view?filename=ComfyUI_00029_.png&type=output&subfolder=');
INSERT INTO `sg_user_result` VALUES (2000816710951235586, '2025-12-16 06:34:12', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/05/20251216/cfc32567/857ab391-f2ce-4d97-9472-d26750cd4758-1.png?Expires=1766472651&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=3SCwlC3sbbKggWnGP5TqSBoaJBw%3D');
INSERT INTO `sg_user_result` VALUES (2000817414600257538, '2025-12-16 06:37:00', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/5a/20251216/cfc32567/9fbe4694-7d1a-4389-b5db-7121c133804a-1.png?Expires=1766472819&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=oZ2%2FVzmSr4rPwSpAn%2BustmyxUsE%3D');
INSERT INTO `sg_user_result` VALUES (2000837955285417986, '2025-12-16 07:58:37', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/3d/20251216/cfc32567/6ee07120-679a-4910-a764-e508ed797f54-1.png?Expires=1766477716&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=GHmawo1wb%2B%2F2el8s1fZb7TsvJUg%3D');
INSERT INTO `sg_user_result` VALUES (2000839099290222593, '2025-12-16 08:03:10', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/e2/20251216/cfc32567/0b29f8a3-1cd4-41bc-982b-7edd17cd6e62-1.png?Expires=1766477989&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=ZO846KMyT9vq%2BNwQ%2FF94hbBBm%2Bs%3D');
INSERT INTO `sg_user_result` VALUES (2000840036671029250, '2025-12-16 08:06:53', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/93/20251216/cfc32567/43983760-3368-4af0-b428-a2553a3014ec-1.png?Expires=1766478212&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=JigLNIpEBt28teLJZfv%2BwRISX0Q%3D');
INSERT INTO `sg_user_result` VALUES (2000841414017552386, '2025-12-16 08:12:22', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/7a/20251216/cfc32567/63195c53-e23a-4fe1-8356-98826bf210d7-1.png?Expires=1766478541&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=57Nb6Nljj%2BcbpWXLtelLT1mOyk0%3D');
INSERT INTO `sg_user_result` VALUES (2000844528388935682, '2025-12-16 08:24:44', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/48/20251216/cfc32567/e8af4ab7-b924-4a66-9874-c96ad144816e-1.png?Expires=1766479283&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=%2Ft33Llkxd8tach4Aw%2BgImdfXQ9g%3D');
INSERT INTO `sg_user_result` VALUES (2000846557559652354, '2025-12-16 08:32:48', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/9a/20251216/cfc32567/e5d9e27d-c3dc-430b-9843-86e7fb0c6eaf-1.png?Expires=1766479767&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=QkqT5MscNZkvOBAtAw%2FgjEs6xjg%3D');
INSERT INTO `sg_user_result` VALUES (2000851247269675010, '2025-12-16 08:51:26', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/2e/20251216/cfc32567/94bc5340-9eec-4672-97d7-2a2b391c65e0-1.png?Expires=1766480885&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=OjavFkH4IyJHWKZF8NbpQxCYI9c%3D');
INSERT INTO `sg_user_result` VALUES (2000851769993199618, '2025-12-16 08:53:31', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/c0/20251216/cfc32567/5992352c-911a-43f2-aa00-8d64fa28fdb0-1.png?Expires=1766481010&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=Tick6UjiTYNtv7RGpUh5yeFYXhM%3D');
INSERT INTO `sg_user_result` VALUES (2000853055375409154, '2025-12-16 08:58:37', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/e7/20251216/cfc32567/13f623c3-15ac-4dee-8d29-b64b15ec7da8-1.png?Expires=1766481316&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=cOezJrJe0EILW7py6y%2BSVu%2FBp9g%3D');
INSERT INTO `sg_user_result` VALUES (2000862716497494018, '2025-12-16 09:37:01', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/9c/20251216/cfc32567/4c0ab8d1-9773-4905-9ea0-04562bb8d2b4-1.png?Expires=1766483620&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=JWNoQlPEdoO2Iz5OOzr89JfqJ%2Bk%3D');
INSERT INTO `sg_user_result` VALUES (2000864034666577922, '2025-12-16 09:42:15', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/5a/20251216/cfc32567/572ff4fc-454c-4757-b760-b2515f2f2f85-1.png?Expires=1766483934&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=bL%2BCV0fWKv3230XjMbU5FCPaaq0%3D');
INSERT INTO `sg_user_result` VALUES (2000865977388498945, '2025-12-16 09:49:58', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/c4/20251216/cfc32567/c2941f8c-2947-49ef-9ce7-fc1e9af8bf26-1.png?Expires=1766484397&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=c5DABGgrmS%2FIHvoPfc8PKWBo%2BVE%3D');
INSERT INTO `sg_user_result` VALUES (2000867414017310722, '2025-12-16 09:55:40', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/82/20251216/cfc32567/1ae045f9-3b33-4234-8f3c-6116b26bad4b-1.png?Expires=1766484740&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=moQ%2BtpzGFTzeRNJ5ZHKmyhQOQ3E%3D');
INSERT INTO `sg_user_result` VALUES (2000869813721239553, '2025-12-16 10:05:13', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/1b/20251216/cfc32567/7aff9cd2-f2f6-4bcf-8982-d18af41b5a2c-1.png?Expires=1766485312&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=wgSWxYZbX9s2Z4udo4mBcHT4U2Y%3D');
INSERT INTO `sg_user_result` VALUES (2000870539721707522, '2025-12-16 10:08:06', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/0a/20251216/cfc32567/8d5445ce-1ce1-400e-b95b-ad0e23541318-1.png?Expires=1766485485&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=Wfn1XbA%2FojWspv15jIr3m%2BqUl3I%3D');
INSERT INTO `sg_user_result` VALUES (2000891980815011842, '2025-12-16 11:33:18', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/0b/20251216/cfc32567/9f096cb7-cc15-42ff-a182-13d230790156-1.png?Expires=1766490597&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=IGwMWRcPWys%2FqzwGHJmDv%2BQSec4%3D');
INSERT INTO `sg_user_result` VALUES (2000894091267149825, '2025-12-16 11:41:41', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/94/20251216/cfc32567/a72b72e3-7bbc-4563-a98f-fcbe4480287b-1.png?Expires=1766491100&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=M2yjt4mwGJT6MEURjfdzj1hc6Oc%3D');
INSERT INTO `sg_user_result` VALUES (2000894742055358466, '2025-12-16 11:44:16', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/a7/20251216/cfc32567/f260415f-aae8-4ab0-ad19-08733689afb5-1.png?Expires=1766491255&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=ejXLoD%2FnkQvoX1iuP5IMC08dA%2Fo%3D');
INSERT INTO `sg_user_result` VALUES (2000896217154326530, '2025-12-16 11:50:08', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/a1/20251216/cfc32567/d11bf41f-88e3-477a-8c29-4b89a24d4c9e-1.png?Expires=1766491607&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=xVDqWM%2F57%2BAmEI%2BYTWkCI1%2FQePk%3D');
INSERT INTO `sg_user_result` VALUES (2000896734274260994, '2025-12-16 11:52:11', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/01/20251216/cfc32567/1378cc9d-1e92-44ed-9ce1-605b02c250e3-1.png?Expires=1766491730&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=W5CGKeoOw7nrsQYggMCCbC8euWg%3D');
INSERT INTO `sg_user_result` VALUES (2000900778178904065, '2025-12-16 12:08:15', 1846117794651930625, 0, 'https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/7d/b5/20251216/cfc32567/da4b2aac-67f0-4384-aab4-aa94293c763a-1.png?Expires=1766492694&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=UUw3MrSJZLlgvjhegwFLn0LtX5g%3D');

SET FOREIGN_KEY_CHECKS = 1;
