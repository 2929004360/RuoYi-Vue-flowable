package com.ruoyi.web.controller.flowable;


import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.flowable.domain.WfCategory;
import com.ruoyi.flowable.domain.vo.WfCategoryVo;
import com.ruoyi.flowable.page.PageQuery;
import com.ruoyi.flowable.page.TableDataInfo;
import com.ruoyi.flowable.service.IWfCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 流程分类Controller
 *
 * @author fengcheng
 * @date 2022-01-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/flowable/workflow/category")
public class WfCategoryController extends BaseController {

    private final IWfCategoryService categoryService;

    /**
     * 查询流程分类列表
     *
     * @param category  流程分类对象
     * @param pageQuery 分页参数
     * @return
     */
    @PreAuthorize("@ss.hasPermi('workflow:category:list')")
    @GetMapping("/list")
    public TableDataInfo<WfCategoryVo> list(WfCategory category, PageQuery pageQuery) {
        return categoryService.queryPageList(category, pageQuery);
    }

    /**
     * 查询全部的流程分类列表
     *
     * @param category 流程分类对象
     * @return
     */
    @PreAuthorize("@ss.hasPermi('workflow:category:list')")
    @GetMapping("/listAll")
    public R<List<WfCategoryVo>> listAll(WfCategory category) {
        return R.ok(categoryService.queryList(category));
    }

    /**
     * 导出流程分类列表
     *
     * @param category 流程分类对象
     * @param response 响应
     */
    @PreAuthorize("@ss.hasPermi('workflow:category:export')")
    @Log(title = "流程分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@Validated WfCategory category, HttpServletResponse response) {
        List<WfCategoryVo> list = categoryService.queryList(category);
        ExcelUtil<WfCategoryVo> util = new ExcelUtil<WfCategoryVo>(WfCategoryVo.class);
        util.exportExcel(response, list, "流程分类");
    }

    /**
     * 获取流程分类详细信息
     *
     * @param categoryId 分类主键
     */
    @PreAuthorize("@ss.hasPermi('workflow:category:query')")
    @GetMapping("/{categoryId}")
    public R<WfCategoryVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable("categoryId") String categoryId) {
        return R.ok(categoryService.queryById(categoryId));
    }

    /**
     * 新增流程分类
     *
     * @param category 流程分类对象
     * @return
     */
    @PreAuthorize("@ss.hasPermi('workflow:category:add')")
    @Log(title = "流程分类", businessType = BusinessType.INSERT)
    @PostMapping()
    public AjaxResult add(@Validated @RequestBody WfCategory category) {
        if (categoryService.checkCategoryCodeUnique(category)) {
            return error("新增流程分类'" + category.getCategoryName() + "'失败，流程编码已存在");
        }
        return toAjax(categoryService.insertCategory(category));
    }

    /**
     * 修改流程分类
     *
     * @param category 流程分类对象
     * @return
     */
    @PreAuthorize("@ss.hasPermi('workflow:category:edit')")
    @Log(title = "流程分类", businessType = BusinessType.UPDATE)
    @PutMapping()
    public AjaxResult edit(@Validated @RequestBody WfCategory category) {
        if (categoryService.checkCategoryCodeUnique(category)) {
            return error("修改流程分类'" + category.getCategoryName() + "'失败，流程编码已存在");
        }
        return toAjax(categoryService.updateCategory(category));
    }

    /**
     * 校验并删除数据
     *
     * @param categoryIds 主键集合
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('workflow:category:remove')")
    @Log(title = "流程分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@NotEmpty(message = "主键不能为空") @PathVariable String[] categoryIds) {
        return toAjax(categoryService.deleteWithValidByIds(Arrays.asList(categoryIds), true));
    }
}
