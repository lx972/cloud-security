package cn.lx.security.dao;

import cn.lx.security.doamin.SysUser;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * cn.lx.security.dao
 *
 * @Author Administrator
 * @date 12:16
 */
public interface UserMapper extends Mapper<SysUser> {

    @Select("select * from sys_user where username=#{username}")
    @Results({@Result(id = true, property = "id", column = "id"),
            @Result(property = "authorities", column = "id", javaType = List.class,
                    many = @Many(select = "cn.lx.security.dao.RermissionMapper.findByUid"))})
    public SysUser findByUsername(String username);

}
