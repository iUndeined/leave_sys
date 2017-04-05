SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for balance
-- ----------------------------
DROP TABLE IF EXISTS `balance`;
CREATE TABLE `balance` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `empl_id` int(11) DEFAULT NULL,
  `empl_no` varchar(36) DEFAULT NULL,
  `empl_name` varchar(12) DEFAULT NULL,
  `join_date` datetime DEFAULT NULL,
  `after_work` double DEFAULT NULL,
  `total_work` double DEFAULT NULL,
  `last_year_rest_al` double DEFAULT NULL,
  `last_year_rest_lil` double DEFAULT NULL,
  `curr_year_al_qua` double DEFAULT NULL,
  `curr_end_al` double DEFAULT NULL,
  `curr_end_lil` double DEFAULT NULL,
  `curr_year_apply_al` double DEFAULT NULL,
  `curr_year_apply_lil` double DEFAULT NULL,
  `curr_year_add_al` double DEFAULT NULL,
  `curr_year_add_lil` double DEFAULT NULL,
  `curr_rest_al` double DEFAULT NULL,
  `curr_rest_lil` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of balance
-- ----------------------------

-- ----------------------------
-- Table structure for email_task
-- ----------------------------
DROP TABLE IF EXISTS `email_task`;
CREATE TABLE `email_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `processes_node_id` int(11) DEFAULT NULL,
  `leave_id` int(11) DEFAULT NULL,
  `execute_date` datetime DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '{0: 执行中, 1: 已执行}',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of email_task
-- ----------------------------

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `manager_id` int(11) DEFAULT NULL,
  `employ_no` varchar(32) DEFAULT NULL,
  `name` varchar(12) DEFAULT NULL,
  `password` varchar(36) DEFAULT NULL,
  `dept` varchar(128) DEFAULT NULL,
  `desig` varchar(128) DEFAULT NULL,
  `email` varchar(64) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `gone` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of employee
-- ----------------------------

-- ----------------------------
-- Table structure for employee_wechat
-- ----------------------------
DROP TABLE IF EXISTS `employee_wechat`;
CREATE TABLE `employee_wechat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(128) DEFAULT NULL,
  `empl_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of employee_wechat
-- ----------------------------

-- ----------------------------
-- Table structure for leave
-- ----------------------------
DROP TABLE IF EXISTS `leave`;
CREATE TABLE `leave` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `employ_id` int(11) DEFAULT NULL,
  `employ_no` varchar(32) DEFAULT NULL,
  `processes_id` int(11) DEFAULT NULL,
  `apply_man_id` int(11) DEFAULT NULL,
  `apply_man` varchar(16) DEFAULT NULL,
  `apply_dept` varchar(16) DEFAULT NULL,
  `designation` varchar(16) DEFAULT NULL,
  `type` varchar(32) DEFAULT NULL,
  `reason` varchar(2048) DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `apply_days` double DEFAULT NULL,
  `ytd` double DEFAULT NULL,
  `mtd` double DEFAULT NULL,
  `scrip` varchar(1024) DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of leave
-- ----------------------------

-- ----------------------------
-- Table structure for logs
-- ----------------------------
DROP TABLE IF EXISTS `logs`;
CREATE TABLE `logs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `man` varchar(16) DEFAULT NULL,
  `man_id` int(11) DEFAULT NULL,
  `type` varchar(36) DEFAULT NULL,
  `from_id` int(11) DEFAULT NULL,
  `content` varbinary(1024) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of logs
-- ----------------------------

-- ----------------------------
-- Table structure for manager
-- ----------------------------
DROP TABLE IF EXISTS `manager`;
CREATE TABLE `manager` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account` varchar(24) DEFAULT NULL,
  `name` varchar(8) DEFAULT NULL,
  `password` varchar(36) DEFAULT NULL,
  `super` int(1) DEFAULT NULL COMMENT '{0: 否, 1: 是}',
  `hr` int(11) DEFAULT NULL COMMENT '{0: 否, 1:是}',
  `email` varchar(64) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of manager
-- ----------------------------
INSERT INTO `manager` VALUES ('1', 'admin', '系统超管', '1', '1', '1', null, '1');

-- ----------------------------
-- Table structure for processes
-- ----------------------------
DROP TABLE IF EXISTS `processes`;
CREATE TABLE `processes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of processes
-- ----------------------------

-- ----------------------------
-- Table structure for processes_binding
-- ----------------------------
DROP TABLE IF EXISTS `processes_binding`;
CREATE TABLE `processes_binding` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `man_id` int(11) DEFAULT NULL,
  `processes_id` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of processes_binding
-- ----------------------------

-- ----------------------------
-- Table structure for processes_node
-- ----------------------------
DROP TABLE IF EXISTS `processes_node`;
CREATE TABLE `processes_node` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `processes_id` int(11) DEFAULT NULL,
  `manager_id` int(11) DEFAULT NULL,
  `manager_name` varchar(256) DEFAULT NULL,
  `prev_node_id` int(11) DEFAULT NULL,
  `next_node_id` int(11) DEFAULT NULL,
  `first` int(11) DEFAULT NULL,
  `last` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of processes_node
-- ----------------------------

-- ----------------------------
-- Table structure for processes_result
-- ----------------------------
DROP TABLE IF EXISTS `processes_result`;
CREATE TABLE `processes_result` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `leave_id` int(11) DEFAULT NULL,
  `processes_node_id` int(11) DEFAULT NULL,
  `manager_id` int(11) DEFAULT NULL,
  `manager_name` varchar(256) DEFAULT NULL,
  `state` int(11) DEFAULT NULL COMMENT '{0:在途, 1:待处理, 2:同意, 3: 否决, 4: 终止}',
  `suggestion` varchar(2048) DEFAULT NULL,
  `reply_date` datetime DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of processes_result
-- ----------------------------
