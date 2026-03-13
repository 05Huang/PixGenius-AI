package cn.hx.pix.genius.core.service;

import cn.hx.pix.genius.core.pojo.SgUserFund;
import cn.hx.pix.genius.core.pojo.SgUserFundRecord;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserFundRecordService extends IService<SgUserFundRecord> {
    // 积分冻结
    void pointsFreeze(Long userId, Integer money);
    // 冻结归还
    void freezeReturn(Long userId, Integer money);
    // 积分扣除
    void pointsDeduction(Long userId, Integer money);
    // 直接划扣
    void directDeduction(Long userId, Integer money);

    // 获取用户账户信息
    SgUserFund getUserFund(Long userId);
}