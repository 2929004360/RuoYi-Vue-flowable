package com.ruoyi.flowable.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.flowable.domain.WfForm;
import com.ruoyi.flowable.domain.bo.WfFormBo;
import com.ruoyi.flowable.domain.vo.WfFormVo;
import com.ruoyi.flowable.mapper.WfFormMapper;
import com.ruoyi.flowable.service.IWfFormService;
import com.ruoyi.flowable.utils.StringUtils;
import com.ruoyi.system.api.service.ISysDeptServiceApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 流程表单Service业务层处理
 *
 * @author fengcheng
 * @createTime 2022/3/7 22:07
 */
@RequiredArgsConstructor
@Service
public class WfFormServiceImpl implements IWfFormService {

    private final WfFormMapper baseMapper;

    private final ISysDeptServiceApi deptServiceApi;

    /**
     * 查询流程表单
     *
     * @param formId 流程表单ID
     * @return 流程表单
     */
    @Override
    public WfFormVo queryById(String formId) {
        return baseMapper.selectVoById(formId);
    }

    /**
     * 查询流程表单列表
     *
     * @param bo 流程表单
     * @return 流程表单
     */
    @Override
    public List<WfFormVo> queryPageList(WfFormBo bo) {
        if (SecurityUtils.hasRole("admin")) {
            return baseMapper.selectVoList(buildQueryWrapper(bo));
        }

        Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();
        List<Long> deptIdList = deptServiceApi.selectBranchDeptId(deptId);
        deptIdList.add(deptId);
        bo.setDeptId(deptId);

        SysDept sysDept = deptServiceApi.selectDeptById(deptId);
        String[] ancestorsArr = sysDept.getAncestors().split(",");
        List<Long> ancestorsList = Convert.toList(Long.class, ancestorsArr);

        return baseMapper.selectWfFormList(bo, deptIdList, ancestorsList);
    }

    /**
     * 查询流程表单列表
     *
     * @param bo 流程表单
     * @return 流程表单
     */
    @Override
    public List<WfFormVo> queryList(WfFormBo bo) {
        LambdaQueryWrapper<WfForm> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 新增流程表单
     *
     * @param bo 流程表单
     * @return 结果
     */
    @Override
    public int insertForm(WfFormBo bo) {
        WfForm wfForm = new WfForm();
        wfForm.setFormName(bo.getFormName());
        wfForm.setContent(bo.getContent());
        wfForm.setRemark(bo.getRemark());
        wfForm.setCreateTime(DateUtils.getNowDate());
        wfForm.setCreateBy(String.valueOf(SecurityUtils.getUserId()));
        wfForm.setUserName(SecurityUtils.getLoginUser().getUser().getNickName());
        wfForm.setUserId(SecurityUtils.getUserId());
        wfForm.setDeptId(bo.getDeptId());
        wfForm.setDeptName(bo.getDeptName());
        wfForm.setType(bo.getType());
        return baseMapper.insert(wfForm);
    }

    /**
     * 修改流程表单
     *
     * @param bo 流程表单
     * @return 结果
     */
    @Override
    public int updateForm(WfFormBo bo) {
        return baseMapper.update(new WfForm(), new LambdaUpdateWrapper<WfForm>().set(ObjectUtil.isNotEmpty(bo.getDeptId()), WfForm::getDeptId, bo.getDeptId()).set(StrUtil.isNotBlank(bo.getDeptName()), WfForm::getDeptName, bo.getDeptName()).set(StrUtil.isNotBlank(bo.getFormName()), WfForm::getFormName, bo.getFormName()).set(StrUtil.isNotBlank(bo.getType()), WfForm::getType, bo.getType()).set(StrUtil.isNotBlank(bo.getContent()), WfForm::getContent, bo.getContent()).set(StrUtil.isNotBlank(bo.getRemark()), WfForm::getRemark, bo.getRemark()).set(WfForm::getUpdateTime, DateUtils.getNowDate()).set(WfForm::getUpdateBy, SecurityUtils.getUserId()).eq(WfForm::getFormId, bo.getFormId()));
    }

    /**
     * 批量删除流程表单
     *
     * @param ids 需要删除的流程表单ID
     * @return 结果
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids) {
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    private LambdaQueryWrapper<WfForm> buildQueryWrapper(WfFormBo bo) {
        LambdaQueryWrapper<WfForm> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getFormName()), WfForm::getFormName, bo.getFormName());
        return lqw;
    }
}
