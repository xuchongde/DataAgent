/*
 * Copyright 2024-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.dataagent.mapper;

import com.alibaba.cloud.ai.dataagent.entity.AgentExtTool;
import com.alibaba.cloud.ai.dataagent.entity.AgentPresetQuestion;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AgentExtToolMapper {

	@Select("""
			SELECT * FROM agent_ext_tool
			         WHERE agent_id = #{agentId} AND is_deleted = 0
			ORDER BY tool_sort ASC, id ASC
			""")
	List<AgentExtTool> selectByAgentId(@Param("agentId") Long agentId);

	@Select("""
			SELECT * FROM agent_ext_tool
			         WHERE agent_id = #{agentId}
			ORDER BY tool_sort ASC, id ASC
			""")
	List<AgentExtTool> selectAllByAgentId(@Param("agentId") Long agentId);

	/**
	 * Query by id
	 */
	@Select("""
			SELECT * FROM agent_ext_tool WHERE id = #{id}
			""")
	AgentExtTool selectById(@Param("id") Long id);

	@Insert("""
			INSERT INTO agent_ext_tool (agent_id, tool_name, bean_id, tool_sort, tool_remark,create_time, update_time,is_deleted)
			VALUES (#{agentId},#{toolName}, #{beanId}, #{toolSort}, #{toolRemark},  NOW(), NOW(),0)
			""")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	int insert(AgentExtTool question);

	@Update("""
			<script>
			UPDATE agent_ext_tool
			<set>
				<if test="toolName != null">tool_name = #{toolName},</if>
				<if test="beanId != null">bean_id = #{beanId},</if>
				<if test="toolSort != null">tool_sort = #{toolSort},</if>
				<if test="toolRemark != null">tool_remark = #{toolRemark},</if>
				update_time = NOW()
			</set>
			WHERE id = #{id}
			</script>
			""")
	int update(AgentExtTool question);

	@Delete("""
			DELETE FROM agent_ext_tool WHERE id = #{id}
			""")
	int deleteById(@Param("id") Long id);

	@Delete("""
			DELETE FROM agent_ext_tool WHERE agent_id = #{agentId}
			""")
	int deleteByAgentId(@Param("agentId") Long agentId);

}
