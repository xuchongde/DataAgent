CREATE TABLE `agent_ext_tool` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID, 用于内部关联',
  `agent_id` int(11) NOT NULL COMMENT '关联的智能体ID',
  `tool_name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '工具名称',
  `bean_id` varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT '实现类spring beanID',
  `tool_sort` int COLLATE utf8mb4_bin COMMENT '同一个agent工具排序,值小优先执行',
  `tool_remark` text COLLATE utf8mb4_bin COMMENT '工具描述',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` int(11) DEFAULT '0' COMMENT '逻辑删除字段，0=未删除, 1=已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='智能体扩展工具配置';