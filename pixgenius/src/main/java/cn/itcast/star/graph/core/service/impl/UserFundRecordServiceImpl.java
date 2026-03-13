package cn.hx.pix.genius.core.service.impl;

import cn.hx.pix.genius.core.exception.CustomException;
import cn.hx.pix.genius.core.mapper.SgUserFundMapper;
import cn.hx.pix.genius.core.mapper.SgUserFundRecordMapper;
import cn.hx.pix.genius.core.pojo.SgUserFund;
import cn.hx.pix.genius.core.pojo.SgUserFundRecord;
import cn.hx.pix.genius.core.service.UserFundRecordService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class UserFundRecordServiceImpl extends ServiceImpl<SgUserFundRecordMapper, SgUserFundRecord> implements UserFundRecordService {

    @Autowired
    SgUserFundMapper sgUserFundMapper;
    @Autowired
    SgUserFundRecordMapper sgUserFundRecordMapper;
    /**
     * 积分冻结：指从用户积分账户拿出积分，增加到用户积分冻结账户上
     * @param userId
     * @param money
     */
    @Override
    public void pointsFreeze(Long userId, Integer money) {
        SgUserFund sgUserFund = getUserFund(userId);
        long temp = sgUserFund.getScore() - money;
        if(temp>=0){
            sgUserFund.setScore(temp);
            sgUserFund.setFreezeScore(sgUserFund.getFreezeScore()+money);
            sgUserFundMapper.updateById(sgUserFund);

            saveLog(0,-money,sgUserFund.getId());
            saveLog(1,money,sgUserFund.getId());
        }else{
            throw new CustomException("积分账户余额不足");
        }
    }

    /**
     * 冻结归还：指从积分冻结账户上拿出积分，增加到用户积分账户上
     * @param userId
     * @param money
     */
    @Override
    public void freezeReturn(Long userId, Integer money) {
        SgUserFund sgUserFund = getUserFund(userId);
        long temp = sgUserFund.getFreezeScore() - money;
        if(temp>=0){
            sgUserFund.setScore(sgUserFund.getScore()+money);
            sgUserFund.setFreezeScore(temp);
            sgUserFundMapper.updateById(sgUserFund);

            saveLog(0,money,sgUserFund.getId());
            saveLog(1,-money,sgUserFund.getId());
        }else{
            throw new CustomException("积分冻结账户余额不足");
        }
    }

    /**
     * 积分扣除：指从用户积分冻结账号上拿出积分，增加到总账户的积分账户上
     * @param userId
     * @param money
     */
    @Override
    public void pointsDeduction(Long userId, Integer money) {
        SgUserFund userFund = getUserFund(userId);
        SgUserFund allFund = getUserFund(0L);

        long temp = userFund.getFreezeScore() - money;
        if(temp>=0){
            // 用户账户修改
            userFund.setFreezeScore(temp);
            saveLog(1,-money,userFund.getId());
            sgUserFundMapper.updateById(userFund);

            // 修改总账户
            allFund.setScore(allFund.getScore()+money);
            saveLog(0,money,allFund.getId());
            sgUserFundMapper.updateById(allFund);
        }else{
            throw new CustomException("积分冻结账户余额不足");
        }

    }

    @Override
    public void directDeduction(Long userId, Integer money) {
        SgUserFund userFund = getUserFund(userId);
        SgUserFund allFund = getUserFund(0L);

        long temp = userFund.getScore() - money;
        if(temp>=0){
            // 用户账户修改
            userFund.setScore(temp);
            saveLog(0,-money,userFund.getId());
            sgUserFundMapper.updateById(userFund);

            // 修改总账户
            allFund.setScore(allFund.getScore()+money);
            saveLog(0,money,allFund.getId());
            sgUserFundMapper.updateById(allFund);
        }else{
            throw new CustomException("积分账户余额不足");
        }
    }

    private void saveLog(int fundType,int money,long fundId){
        SgUserFundRecord log = new SgUserFundRecord();
        log.setFundType(fundType);
        log.setMoney(money);
        log.setFundId(fundId);
        sgUserFundRecordMapper.insert(log);
    }

    @Override
    public SgUserFund getUserFund(Long userId){
        if(userId == null){
            throw new CustomException("用户ID不能为空");
        }
        // 使用 selectList 而不是 selectOne，防止因数据库存在重复记录导致报错
        List<SgUserFund> list = sgUserFundMapper.selectList(Wrappers.<SgUserFund>lambdaQuery()
                .eq(SgUserFund::getUserId, userId)
                .orderByDesc(SgUserFund::getId)); // 按 ID 倒序，取最新的一条
        
        SgUserFund sgUserFund = null;
        if(list != null && !list.isEmpty()){
            // 如果有多条，取第一条（由于倒序，这里取的是 ID 最大的）
            sgUserFund = list.get(0);
            if(list.size() > 1){
                log.warn("用户[{}]存在多条积分账户记录，系统自动选取最新一条(ID={})，当前积分={}", 
                        userId, sgUserFund.getId(), sgUserFund.getScore());
            } else {
                log.info("用户[{}]获取积分账户成功(ID={})，当前积分={}", userId, sgUserFund.getId(), sgUserFund.getScore());
            }
        }
        
        if(sgUserFund==null){
            // 双重检查，避免并发创建
            synchronized (this){
                list = sgUserFundMapper.selectList(Wrappers.<SgUserFund>lambdaQuery().eq(SgUserFund::getUserId, userId));
                if(list != null && !list.isEmpty()) {
                    sgUserFund = list.get(0);
                }
                
                if(sgUserFund==null) {
                    sgUserFund = new SgUserFund();
                    sgUserFund.setUserId(userId);
                    sgUserFund.setScore(0L);
                    sgUserFund.setFreezeScore(0L);
                    sgUserFund.setVersion(0L);
                    sgUserFundMapper.insert(sgUserFund);
                }
            }
        }
        return sgUserFund;
    }
}
