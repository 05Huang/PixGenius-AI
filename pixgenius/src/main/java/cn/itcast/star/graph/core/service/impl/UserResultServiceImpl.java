package cn.hx.pix.genius.core.service.impl;

import cn.hx.pix.genius.core.mapper.UserResultMapper;
import cn.hx.pix.genius.core.pojo.UserResult;
import cn.hx.pix.genius.core.service.UserFundRecordService;
import cn.hx.pix.genius.core.service.UserResultService;
import cn.hx.pix.genius.core.utils.UserUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
* <p>
* sg_user_result Service 接口实现
* </p>
*
* @author luoxu
* @since 2024-10-18 16:07:29
*/
@Service
@Transactional
@Slf4j
public class UserResultServiceImpl extends ServiceImpl<UserResultMapper, UserResult> implements UserResultService {

    @Autowired
    UserFundRecordService userFundRecordService;

    @Override
    public void saveList(List<String> urls, Long userId) {
        List<UserResult> userResults = urls.stream().map((url) -> {
            UserResult userResult = new UserResult();
            userResult.setUserId(userId);
            userResult.setUrl(url);
            userResult.setCollect(0);
            return userResult;
        }).collect(Collectors.toList());
        this.saveBatch(userResults);
        // 积分扣除
        userFundRecordService.pointsDeduction(userId,userResults.size());
    }

    @Override
    public Page<UserResult> pageByUser(Long userId, int pageNum, int pageSize) {
        Page<UserResult> page = new Page<>(pageNum, pageSize);
        return this.lambdaQuery()
                .eq(UserResult::getUserId, userId)
                .orderByDesc(UserResult::getCreatedTime)
                .page(page);
    }
}