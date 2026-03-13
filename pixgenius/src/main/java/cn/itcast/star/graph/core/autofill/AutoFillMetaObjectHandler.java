package cn.hx.pix.genius.core.autofill;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AutoFillMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入语句要自动填充的规则，insert
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        /**
         * 1、当前执行的插入sql，源数据
         * 2、要自动填充的字段名
         * 3、填充值的类型
         * 4、默认填充的值
         */
        this.strictInsertFill(metaObject,"createdTime", LocalDateTime.class,LocalDateTime.now());
        this.strictInsertFill(metaObject,"createTime", LocalDateTime.class,LocalDateTime.now());
    }

    /**
     * 更新语句要自动填充的规则，update
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject,"updatedTime", LocalDateTime.class,LocalDateTime.now());
    }
}
