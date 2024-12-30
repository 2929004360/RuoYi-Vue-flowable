package com.ruoyi.flowable.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.flowable.domain.WfCategory;
import com.ruoyi.flowable.domain.bo.WfModelBo;
import com.ruoyi.flowable.domain.vo.WfCategoryVo;
import com.ruoyi.flowable.domain.vo.WfModelVo;
import com.ruoyi.flowable.mapper.WfCategoryMapper;
import com.ruoyi.flowable.page.PageQuery;
import com.ruoyi.flowable.page.TableDataInfo;
import com.ruoyi.flowable.service.IWfCategoryService;
import com.ruoyi.flowable.service.IWfModelService;
import com.ruoyi.flowable.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 流程分类Service业务层处理
 *
 * @author fengcheng
 * @date 2022-01-15
 */
@RequiredArgsConstructor
@Service
public class WfCategoryServiceImpl implements IWfCategoryService {
    private final WfCategoryMapper baseMapper;

    private final IWfModelService modelService;

    /**
     * 获取流程分类详细信息
     *
     * @param categoryId 分类主键
     * @return
     */
    @Override
    public WfCategoryVo queryById(String categoryId) {
        return baseMapper.selectVoById(categoryId);
    }

    /**
     * 查询流程分类列表
     *
     * @param category  流程分类对象
     * @param pageQuery 分页参数
     * @return
     */
    @Override
    public TableDataInfo<WfCategoryVo> queryPageList(WfCategory category, PageQuery pageQuery) {
        LambdaQueryWrapper<WfCategory> lqw = buildQueryWrapper(category);
        Page<WfCategoryVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询全部的流程分类列表
     *
     * @param category 流程分类对象
     * @return
     */
    @Override
    public List<WfCategoryVo> queryList(WfCategory category) {
        LambdaQueryWrapper<WfCategory> lqw = buildQueryWrapper(category);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<WfCategory> buildQueryWrapper(WfCategory category) {
        Map<String, Object> params = category.getParams();
        LambdaQueryWrapper<WfCategory> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(category.getCategoryName()), WfCategory::getCategoryName, category.getCategoryName());
        lqw.eq(StringUtils.isNotBlank(category.getCode()), WfCategory::getCode, category.getCode());
        return lqw;
    }

    /**
     * 新增流程分类
     *
     * @param category 流程分类信息
     * @return 结果
     */
    @Override
    public int insertCategory(WfCategory category) {
        WfCategory add = BeanUtil.toBean(category, WfCategory.class);
        add.setCreateTime(DateUtils.getNowDate());
        add.setCreateBy(SecurityUtils.getLoginUser().getUser().getNickName());
        return baseMapper.insert(add);
    }

    /**
     * 修改流程分类
     *
     * @param category 流程分类对象
     * @return
     */
    @Override
    public int updateCategory(WfCategory category) {
        WfCategory update = BeanUtil.toBean(category, WfCategory.class);
        update.setUpdateTime(DateUtils.getNowDate());
        update.setUpdateBy(SecurityUtils.getLoginUser().getUser().getNickName());
        return baseMapper.updateById(update);
    }

    /**
     * 校验并删除数据
     *
     * @param ids 主键集合
     * @param isValid 是否校验,true-删除前校验,false-不校验
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if (isValid) {
            WfModelBo modelBo = new WfModelBo();
            for (String id : ids) {
                modelBo.setCategory(baseMapper.selectVoById(id).getCode());
                List<WfModelVo> wfModelVos = modelService.selectList(modelBo);
                if (wfModelVos.size() > 0) {
                    throw new ServiceException("分类下存在流程模型，不允许删除");
                }
            }
        }
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 校验分类编码是否唯一
     *
     * @param category 流程分类
     * @return 结果
     */
    @Override
    public boolean checkCategoryCodeUnique(WfCategory category) {
        return baseMapper.exists(new LambdaQueryWrapper<WfCategory>()
                .eq(WfCategory::getCode, category.getCode())
                .ne(ObjectUtil.isNotNull(category.getCategoryId()), WfCategory::getCategoryId, category.getCategoryId()));
    }
}
