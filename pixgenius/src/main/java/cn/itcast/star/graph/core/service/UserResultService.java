package cn.hx.pix.genius.core.service;

import cn.hx.pix.genius.core.pojo.UserResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* <p>
* sg_user_result Service 接口
* </p>
*
* @author luoxu
* @since 2024-10-18 16:07:13
*/
public interface UserResultService extends IService<UserResult> {

    void saveList(List<String> urls,Long userId);

    Page<UserResult> pageByUser(Long userId, int pageNum, int pageSize);

}