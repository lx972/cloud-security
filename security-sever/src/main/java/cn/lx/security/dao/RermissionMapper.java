package cn.lx.security.dao;

import cn.lx.security.doamin.SysPermission;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * cn.lx.security.dao
 *
 * @Author Administrator
 * @date 12:14
 */
public interface RermissionMapper extends Mapper<SysPermission> {

    @Select("SELECT * FROM sys_permission WHERE ID IN(" +
            "SELECT PID FROM sys_role_permission WHERE RID IN(" +
            "SELECT RID FROM sys_user_role WHERE uid=#{uid}" +
            "))")
    public List<SysPermission> findByUid(Integer uid);

}
